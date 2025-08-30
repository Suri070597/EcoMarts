/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.CategoryDAO;
import dao.OrderDAO;
import dao.OrderDetailDAO;
import dao.PromotionDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.math.RoundingMode;
import model.Account;
import model.Category;
import model.Order;
import model.OrderDetail;
import model.Promotion;

/**
 *
 * @author ADMIN
 */
@WebServlet(name = "ReorderServlet", urlPatterns = {"/reorder"})
public class ReorderServlet extends HttpServlet {

    OrderDAO orderDAO = new OrderDAO();
    OrderDetailDAO orderDetailDAO = new OrderDetailDAO();
    CategoryDAO categoryDAO = new CategoryDAO();
    PromotionDAO promotionDAO = new PromotionDAO();

    @Override
protected void doGet(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
    HttpSession session = req.getSession();
    Account acc = (Account) session.getAttribute("account");

    if (acc == null) {
        req.getRequestDispatcher("/WEB-INF/login/login.jsp").forward(req, resp);
        return;
    }

    List<Order> orders = orderDAO.getOrdersByAccountId(acc.getAccountID());
    List<Category> categories = categoryDAO.getAllCategoriesWithChildren();

for (Order order : orders) {
    // Lấy danh sách OrderDetail để tính toán chính xác
    List<OrderDetail> orderDetails = orderDetailDAO.getOrderDetailsByOrderId(order.getOrderID());
    
    // Tính tổng từ OrderDetail với promotion (giống như OrderDetailServlet)
    double total = 0;
    double totalOriginal = 0; // Tổng giá gốc
    double totalPromotion = 0; // Tổng giá sau promotion
    
    for (OrderDetail od : orderDetails) {
        // Tính giá gốc và giá sau promotion
        double originalPrice = od.getUnitPrice();
        double originalSubTotal = od.getSubTotal();
        
        // Kiểm tra promotion cho sản phẩm này
        Promotion productPromotion = promotionDAO.getValidPromotionForProduct(od.getProductID());
        double discountedPrice = originalPrice;
        double discountedSubTotal = originalSubTotal;
        
        if (productPromotion != null) {
            // Tính giá sau giảm giá
            double discountPercent = productPromotion.getDiscountPercent();
            discountedPrice = originalPrice * (1 - discountPercent / 100);
            discountedSubTotal = discountedPrice * od.getQuantity();
        }
        
        totalOriginal += originalSubTotal;
        totalPromotion += discountedSubTotal;
        total += discountedSubTotal; // Sử dụng giá sau promotion để tính tổng
    }
    
    // VAT = 8% của tổng phụ (sau promotion)
    double vat = total * 0.08;
    
    // Lấy thông tin voucher đã sử dụng (nếu có)
    double discount = orderDAO.getDiscountAmountByOrderID(order.getOrderID()).doubleValue();
    
    // Tổng thanh toán cuối cùng = tổng phụ (sau promotion) - giảm giá + VAT
    double finalTotal = total - discount + vat;
    
    // Đảm bảo tổng thanh toán không âm
    if (finalTotal < 0) {
        finalTotal = 0;
    }
    
    // Tính tổng tiền ưu đãi (tiết kiệm được từ promotion)
    double totalSavings = totalOriginal - totalPromotion;

    order.setDiscountAmount(discount);
    order.setSubtotal(total);
    order.setVat(vat);
    order.setGrandTotal(finalTotal);
    order.setTotalSavings(totalSavings); // Thêm field này vào model Order
    
    // Lấy danh sách tên sản phẩm cho order này
    String productNames = orderDetailDAO.getProductNamesByOrderId(order.getOrderID());
    order.setProductNames(productNames);
}


    req.setAttribute("orders", orders);
    req.setAttribute("categories", categories);
    req.getRequestDispatcher("/WEB-INF/customer/reorder.jsp").forward(req, resp);
}

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        Account acc = (Account) session.getAttribute("account");

        if (acc == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        int orderId = Integer.parseInt(req.getParameter("orderId"));
        List<OrderDetail> oldDetails = orderDetailDAO.getOrderDetailsByOrderId(orderId);

        // Tạo giỏ hàng tạm thời trong session (giả sử đang dùng Map<Integer, Integer> để lưu ProductID và Quantity)
        Map<Integer, Double> cart = (Map<Integer, Double>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
        }

        for (OrderDetail od : oldDetails) {
            int pid = od.getProductID();
            double qty = od.getQuantity();
            cart.put(pid, cart.getOrDefault(pid, 0.0) + qty);
        }

        session.setAttribute("cart", cart);
        resp.sendRedirect(req.getContextPath() + "/WEB-INF/customer/cart"); // chuyển hướng về trang giỏ hàng
    }
}
