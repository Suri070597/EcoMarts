/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Account;

/**
 *
 * @author nguye
 */
@WebServlet("/VerifyPasswordServlet")
public class VerifyPasswordServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Hiển thị trang xác minh mật khẩu hiện tại
        req.getRequestDispatcher("/WEB-INF/customer/verifyCurrentPassword.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String currentPassword = req.getParameter("currentPassword");
        String hashedPassword = db.MD5Util.hash(currentPassword);

        Account account = (Account) req.getSession().getAttribute("account");

        if (account != null && account.getPassword().equals(hashedPassword)) {
            req.getRequestDispatcher("/WEB-INF/customer/changePassword.jsp").forward(req, resp);
        } else {
            req.setAttribute("message", "Mật khẩu không chính xác!");
            req.getRequestDispatcher("/WEB-INF/customer/verifyCurrentPassword.jsp").forward(req, resp);
        }

    }
}
