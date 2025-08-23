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
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Account;
import model.Category;
import model.Order;
import model.OrderDetail;

/**
 *
 * @author ADMIN
 */
@WebServlet(name = "ReorderServlet", urlPatterns = {"/reorder"})
public class ReorderServlet extends HttpServlet {

    OrderDAO orderDAO = new OrderDAO();
    OrderDetailDAO orderDetailDAO = new OrderDetailDAO();
    CategoryDAO categoryDAO = new CategoryDAO();

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

        // Sửa trong ReorderServlet.java
        for (Order order : orders) {
            // Lấy chi tiết đơn hàng để tính tổng phụ
            List<OrderDetail> orderDetails = orderDetailDAO.getOrderDetailsByOrderId(order.getOrderID());

            double subtotal = 0;
            for (OrderDetail od : orderDetails) {
                subtotal += od.getSubTotal();
            }

            double discount = orderDAO.getDiscountAmountByOrderID(order.getOrderID()).doubleValue();
            double vat = subtotal * 0.08;
            double grandTotal = subtotal - discount + vat;

            order.setDiscountAmount(discount);
            order.setSubtotal(subtotal);
            order.setVat(vat);
            order.setGrandTotal(grandTotal);

            // Lấy danh sách tên sản phẩm
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
