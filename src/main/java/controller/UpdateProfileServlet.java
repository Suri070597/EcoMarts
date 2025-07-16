/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.AccountDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Account;

/**
 *
 * @author nguye
 */
@WebServlet("/UpdateProfileServlet")
public class UpdateProfileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        Account account = (Account) session.getAttribute("account");
        if (account != null) {
            req.setAttribute("account", account);
            req.getRequestDispatcher("/WEB-INF/customer/viewProfile.jsp").forward(req, resp);
        } else {
            resp.sendRedirect("login.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            int accountId = Integer.parseInt(req.getParameter("accountId"));
            String fullName = req.getParameter("fullName");
            String phone = req.getParameter("phone");
            String address = req.getParameter("address");
            String gender = req.getParameter("gender");

            AccountDAO dao = new AccountDAO();
            boolean updated = dao.updateBasicInfo(accountId, fullName, phone, address, gender);

            if (updated) {
                Account acc = dao.getFullAccountById(accountId);
                req.getSession().setAttribute("account", acc);
                req.setAttribute("message", "Cập nhật thành công!");
            }

            req.getRequestDispatcher("/WEB-INF/customer/viewProfile.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("message", "Cập nhật thất bại: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/customer/viewProfile.jsp").forward(req, resp);
        }
    }
}
