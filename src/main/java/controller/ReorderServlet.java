/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.CategoryDAO;
import dao.OrderDAO;
import dao.OrderDetailDAO;
import java.io.IOException;
import java.io.PrintWriter;
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

        List<Order> orders = orderDAO.getOrdersByCustomerName(acc.getFullName());
        List<Category> categories = categoryDAO.getAllCategoriesWithChildren();

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
