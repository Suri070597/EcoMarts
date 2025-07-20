package controller;

import dao.CartItemDAO;
import dao.OrderDAO;
import dao.OrderDetailDAO;
import model.Order;
import model.OrderDetail;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import model.Account;
import model.CartItem;

@WebServlet("/customer/orderDetail")

public class OrderDetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
//        try {
        String orderIdStr = request.getParameter("orderID");
        System.out.println("Received orderID: " + orderIdStr);

        if (orderIdStr == null || orderIdStr.isEmpty()) {
            throw new IllegalArgumentException("Thiếu orderID trên request.");
        }

        int orderId = Integer.parseInt(orderIdStr);

        OrderDAO orderDAO = new OrderDAO();
        OrderDetailDAO detailDAO = new OrderDetailDAO();

        Order order = orderDAO.getOrderById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Không tìm thấy đơn hàng với ID: " + orderId);
        }

        List<OrderDetail> orderDetails = detailDAO.getOrderDetailsByOrderId(orderId);
        if (orderDetails == null) {
            throw new IllegalStateException("Không có chi tiết đơn hàng nào.");
        }

        double total = 0;
        for (OrderDetail od : orderDetails) {
            total += od.getSubTotal();
        }

        request.setAttribute("order", order);
        request.setAttribute("orderDetails", orderDetails);
        request.setAttribute("total", total);

        request.getRequestDispatcher("/WEB-INF/customer/order-detail.jsp").forward(request, response);
    }
//        } catch (Exception e) {
//            e.printStackTrace(); // In stack trace để biết lỗi thật sự
//            response.sendError(500, "Có lỗi xảy ra khi xử lý chi tiết đơn hàng.");
//        }
//    }
    private final OrderDAO orderDAO = new OrderDAO();
    private final OrderDetailDAO orderDetailDAO = new OrderDetailDAO();
    private final CartItemDAO cartItemDAO = new CartItemDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String orderIdStr = request.getParameter("orderId");
        HttpSession session = request.getSession();
        Account acc = (Account) session.getAttribute("account");

        if (orderIdStr == null || acc == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu orderId hoặc chưa đăng nhập");
            return;
        }

        int orderId = Integer.parseInt(orderIdStr);

        switch (action) {
            case "cancel":
                orderDAO.cancelOrder(orderId);
                response.sendRedirect(request.getContextPath() + "/customer/reorder");
                break;

            case "reorder":
                List<CartItem> items = orderDetailDAO.getCartItemsFromOrder(orderId);
                for (CartItem item : items) {
                    cartItemDAO.upsertCartItem(acc.getAccountID(), item.getProductID(), item.getQuantity());
                }
                response.sendRedirect(request.getContextPath() + "/cart");
                break;

            case "back":
                response.sendRedirect(request.getContextPath() + "/customer/reorder");
                break;

            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Hành động không hợp lệ");
        }
    }
}
