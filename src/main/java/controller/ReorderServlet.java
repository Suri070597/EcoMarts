/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

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
import model.Order;
import model.OrderDetail;

/**
 *
 * @author nguye
 */
@WebServlet("/customer/reorder")

public class ReorderServlet extends HttpServlet {
    OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Account acc = (Account) session.getAttribute("account");

        if (acc == null) {
            req.getRequestDispatcher("/WEB-INF/login/login.jsp").forward(req, resp);
            return;
        }

        List<Order> orders = orderDAO.getOrdersByCustomerName(acc.getFullName());
        req.setAttribute("orders", orders);
        req.getRequestDispatcher("/WEB-INF/customer/reorder.jsp").forward(req, resp);

    }
}


