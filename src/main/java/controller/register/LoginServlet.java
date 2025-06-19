/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.register;

import dao.AccountDAO1;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Account;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("Forwarding to /dangnhap/login.jsp");
        request.getRequestDispatcher("/WEB-INF/login/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        System.out.println("Login attempt: email=" + email + ", password=****");

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            System.out.println("Missing email or password");
            request.getSession().setAttribute("error", "Vui lòng nhập email và mật khẩu!");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        AccountDAO1 accountDAO = new AccountDAO1();
        try {
            Account account = accountDAO.checkLogin(email, password);
            if (account != null) {
                System.out.println("Login successful: email=" + email + ", role=" + account.getRole());
                request.getSession().setAttribute("email", account.getEmail());
                request.getSession().setAttribute("username", account.getUsername());
                request.getSession().setAttribute("fullName", account.getFullName());
                request.getSession().setAttribute("role", account.getRole());

                if (account.getRole() == 1) {
                    System.out.println("Redirecting to admin page for email=" + email);
                    response.sendRedirect(request.getContextPath() + "/admin");
                } else if (account.getRole() == 2) {
                    System.out.println("Redirecting to staff page for email=" + email);
                    response.sendRedirect(request.getContextPath() + "/staff");
                } else {
                    System.out.println("Redirecting to home page for email=" + email);
                    response.sendRedirect(request.getContextPath() + "/home");
                }
            } else {
                System.out.println("Login failed: invalid email or password for email=" + email);
                request.getSession().setAttribute("error", "Email hoặc mật khẩu không đúng!");
                response.sendRedirect(request.getContextPath() + "/login");
            }
        } catch (SQLException e) {
            System.out.println("Database error during login for email=" + email + ": " + e.getMessage());
            request.getSession().setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }

    @Override
    public String getServletInfo() {
        return "Handles user login";
    }
}