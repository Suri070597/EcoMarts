/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.forgetpassword;

import dao.AccountDAO1;
import dao.TokenDAO;
import db.DBContext1;
import db.EmailUtil;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.Connection;
import java.util.UUID;
import model.Account;

/**
 *
 * @author HuuDuc
 */
@WebServlet(name = "ForgetPasswordServlet", urlPatterns = {"/forget-password"})
public class ForgetPasswordServlet extends HttpServlet {

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
            out.println("<title>Servlet ForgetPasswordServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ForgetPasswordServlet at " + request.getContextPath() + "</h1>");
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        try (Connection conn = new DBContext1().getConnection()) {
            AccountDAO1 accountDAO = new AccountDAO1();
            Account acc = accountDAO.getAccountByEmail(conn, email);
            if (acc == null) {
                request.setAttribute("error", "Email không tồn tại!");
                request.getRequestDispatcher("/WEB-INF/login/forgetpassword/forget_password.jsp").forward(request, response);
                return;
            }
            String token = UUID.randomUUID().toString();
            TokenDAO tokenDAO = new TokenDAO();
            tokenDAO.createToken(conn, acc.getAccountID(), token);

            // Gửi email
            String resetLink = "http://localhost:8080/EcoMart/reset-password?token=" + token;
            String subject = "Đặt lại mật khẩu EcoMart";
            String body = "Bạn vừa yêu cầu đặt lại mật khẩu. Click vào link này để đặt lại mật khẩu (có hiệu lực 1 phút):\n" + resetLink;
            EmailUtil.send(email, subject, body);

            request.setAttribute("message", "Vui lòng kiểm tra email để đặt lại mật khẩu.");
            request.getRequestDispatcher("/WEB-INF/login/forgetpassword/forget_password.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Có lỗi xảy ra. Vui lòng thử lại sau!");
            request.getRequestDispatcher("/WEB-INF/login/forgetpassword/forget_password.jsp").forward(request, response);
        }
    }

 @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/login/forgetpassword/forget_password.jsp").forward(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}