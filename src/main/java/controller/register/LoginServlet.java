/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.register;

import dao.AccountDAO1;
import db.MD5Util;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Account;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

/**
 *
 * @author HuuDuc
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet NewServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet NewServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
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

        System.out.println("Đang thử đăng nhập: email=" + email + ", mật khẩu=****");

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            System.out.println("Thiếu email hoặc mật khẩu");
            request.getSession().setAttribute("error", "Vui lòng nhập email và mật khẩu!");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        System.out.println("================ GỠ LỖI ĐĂNG NHẬP ================");
        System.out.println("GIÁ TRỊ TRƯỜNG EMAIL: [" + email + "]");
        System.out.println("GIÁ TRỊ TRƯỜNG MẬT KHẨU: [" + password + "]");
        System.out.println("ĐỘ DÀI MẬT KHẨU: " + (password == null ? "null" : password.length()));
        String hashedPassword = MD5Util.hash(password);
        System.out.println("MẬT KHẨU SAU KHI MÃ HÓA: [" + hashedPassword + "]");

        // *** Bổ sung hash password ***
        // String hashedPassword = MD5Util.hash(password);
        AccountDAO1 accountDAO = new AccountDAO1();
        try {
            // Truyền hashedPassword vào checkLogin!
            Account account = accountDAO.checkLogin(email, hashedPassword);
            if (account != null) {
                System.out.println("Đăng nhập thành công: email=" + email + ", vai trò=" + account.getRole());
                request.getSession().setAttribute("account", account);
                request.getSession().setAttribute("email", account.getEmail());
                request.getSession().setAttribute("username", account.getUsername());
                request.getSession().setAttribute("fullName", account.getFullName());
                request.getSession().setAttribute("role", account.getRole());

                switch (account.getRole()) {
                    case 1 ->
                        response.sendRedirect(request.getContextPath() + "/admin");
                    case 2 ->
                        response.sendRedirect(request.getContextPath() + "/staff");
                    default ->
                        response.sendRedirect(request.getContextPath() + "/home");
                }
            } else {
                System.out.println("Đăng nhập thất bại: email hoặc mật khẩu không hợp lệ cho email=" + email);
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
