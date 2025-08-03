/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.CartItemDAO;
import dao.CategoryDAO;
import dao.OrderDAO;
import dao.OrderDetailDAO;
import dao.ProductDAO;
import dao.VoucherUsageDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Account;
import model.CartItem;
import model.Category;
import model.Order;
import model.OrderDetail;
import model.Product;
import model.VoucherUsage;

/**
 *
 * @author ADMIN
 */
@WebServlet(name = "OrderDetailServlet", urlPatterns = {"/orderDetail"})
public class OrderDetailServlet extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAO();
    private final OrderDetailDAO orderDetailDAO = new OrderDetailDAO();
    private final CartItemDAO cartItemDAO = new CartItemDAO();
    private final ProductDAO productDAO = new ProductDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final VoucherUsageDAO voucherUsageDAO = new VoucherUsageDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            HttpSession session = request.getSession();
            Account account = (Account) session.getAttribute("account");

            if (account == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            // Get order ID from request
            String orderIdStr = request.getParameter("orderID");

            if (orderIdStr == null || orderIdStr.isEmpty()) {
                request.setAttribute("errorMessage", "Thiếu thông tin đơn hàng");
                request.getRequestDispatcher("/WEB-INF/customer/reorder.jsp").forward(request, response);
                return;
            }

            int orderId = Integer.parseInt(orderIdStr);

            // Get order details
            Order order = orderDAO.getOrderById(orderId);
            if (order == null) {
                request.setAttribute("errorMessage", "Không tìm thấy đơn hàng với ID: " + orderId);
                request.getRequestDispatcher("/WEB-INF/customer/reorder.jsp").forward(request, response);
                return;
            }

            // Validate that order belongs to current user
            if (order.getAccountID() != account.getAccountID()) {
                request.setAttribute("errorMessage", "Bạn không có quyền xem đơn hàng này");
                request.getRequestDispatcher("/WEB-INF/customer/reorder.jsp").forward(request, response);
                return;
            }

            // Get order details and product information
            List<OrderDetail> orderDetails = orderDetailDAO.getOrderDetailsByOrderId(orderId);

            // Calculate total
            double total = 0;
            for (OrderDetail od : orderDetails) {
                // Load product information for display purposes only (image, description, etc)
                Product product = productDAO.getProductById(od.getProductID());
                if (product != null) {
                    // Lưu thông tin sản phẩm nhưng KHÔNG cập nhật giá và số lượng
                    // vì chúng ta muốn giữ giá trị gốc từ thời điểm đặt hàng
                    od.setProduct(product);
                }

                total += od.getSubTotal();
            }
// VAT = 8% của tổng phụ
            double vat = total * 0.08;

// Lấy thông tin voucher đã sử dụng (nếu có)
            VoucherUsage voucherUsage = voucherUsageDAO.getByOrderId(orderId);

// Nếu có voucher thì lấy số tiền giảm
            double discount = (voucherUsage != null) ? voucherUsage.getDiscountAmount() : 0.0;

// Tổng thanh toán cuối cùng = tổng phụ - giảm giá + VAT
            double finalTotal = total - discount + vat;

            // Get categories for the navigation menu
            List<Category> categories = categoryDAO.getAllCategoriesWithChildren();

            // Set attributes for JSP
            request.setAttribute("order", order);
            request.setAttribute("orderDetails", orderDetails);
            request.setAttribute("total", total);
            request.setAttribute("categories", categories);
            request.setAttribute("voucherUsage", voucherUsage);
            request.setAttribute("discount", discount);
            request.setAttribute("vat", vat);
            request.setAttribute("finalTotal", finalTotal);

            // Check for messages in session
            String successMessage = (String) session.getAttribute("successMessage");
            if (successMessage != null) {
                request.setAttribute("successMessage", successMessage);
                session.removeAttribute("successMessage");
            }

            String errorMessage = (String) session.getAttribute("errorMessage");
            if (errorMessage != null) {
                request.setAttribute("errorMessage", errorMessage);
                session.removeAttribute("errorMessage");
            }

            // Forward to JSP
            request.getRequestDispatcher("/WEB-INF/customer/order-detail.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Đã xảy ra lỗi khi xử lý chi tiết đơn hàng");
            request.getRequestDispatcher("/WEB-INF/customer/reorder.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String orderIdStr = request.getParameter("orderId");
        HttpSession session = request.getSession();
        Account account = (Account) session.getAttribute("account");

        if (orderIdStr == null || account == null) {
            session.setAttribute("errorMessage", "Thiếu thông tin hoặc chưa đăng nhập");
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        try {
            int orderId = Integer.parseInt(orderIdStr);

            // Verify order belongs to the current user
            Order order = orderDAO.getOrderById(orderId);
            if (order == null || order.getAccountID() != account.getAccountID()) {
                session.setAttribute("errorMessage", "Bạn không có quyền thực hiện thao tác với đơn hàng này");
                response.sendRedirect(request.getContextPath() + "/reorder");
                return;
            }

            switch (action) {
                case "cancel":
                    // Check if order can be cancelled (only in Processing status and not paid)
                    if (!"Đang xử lý".equals(order.getOrderStatus()) || "Đã thanh toán".equals(order.getPaymentStatus())) {
                        session.setAttribute("errorMessage", "Chỉ có thể hủy đơn hàng trong trạng thái 'Đang xử lý' và chưa thanh toán");
                        response.sendRedirect(request.getContextPath() + "/orderDetail?orderID=" + orderId);
                        return;
                    }

                    // Cancel the order and restore stock
                    try {
                        orderDAO.cancelOrder(orderId);

                        session.setAttribute("successMessage", "Đơn hàng đã được hủy thành công");
                    } catch (Exception ex) {
                        session.setAttribute("errorMessage", "Không thể hủy đơn hàng. Vui lòng thử lại sau");
                    }

                    response.sendRedirect(request.getContextPath() + "/orderDetail?orderID=" + orderId);
                    break;

                case "reorder":
                    // Add order items to cart
                    List<CartItem> items = orderDAO.getCartItemsFromOrder(orderId);

                    // Check stock availability before adding to cart
                    Map<Integer, Double> insufficientStock = new HashMap<>();
                    for (CartItem item : items) {
                        double stockQuantity = productDAO.getStockQuantityById(item.getProductID());
                        if (stockQuantity < item.getQuantity()) {
                            // Save the product with insufficient stock for message
                            Product product = productDAO.getProductById(item.getProductID());
                            if (product != null) {
                                insufficientStock.put(item.getProductID(), stockQuantity);
                                // Adjust quantity to available stock
                                if (stockQuantity > 0) {
                                    item.setQuantity(stockQuantity);
                                }
                            }
                        }

                        // Only add to cart if stock is available
                        if (stockQuantity > 0) {
                            cartItemDAO.upsertCartItem(account.getAccountID(), item.getProductID(),
                                    Math.min(item.getQuantity(), stockQuantity));
                        }
                    }

                    if (!insufficientStock.isEmpty()) {
                        session.setAttribute("cartError", "Một số sản phẩm không đủ số lượng trong kho");
                    } else {
                        session.setAttribute("cartMessage", "Đã thêm tất cả sản phẩm vào giỏ hàng");
                    }

                    response.sendRedirect(request.getContextPath() + "/cart");
                    break;

                default:
                    response.sendRedirect(request.getContextPath() + "/reorder");
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("errorMessage", "Đã xảy ra lỗi: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/reorder");
        }
    }
}
