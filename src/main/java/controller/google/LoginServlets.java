/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.google;

import dao.AccountDAO1;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.SQLException;

import model.Account;

/**
 *
 * @author HuuDuc
 */
@WebServlet(name = "LoginServlets", urlPatterns = {"/logins"})
public class LoginServlets extends HttpServlet {

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
            out.println("<title>Servlet LoginServlets</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet LoginServlets at " + request.getContextPath() + "</h1>");
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

        String code = request.getParameter("code");
        String error = request.getParameter("error");

        if (error != null) {
            response.sendRedirect("login");
            return;
        }

        if (code != null) {
            // Lấy access token và thông tin từ Google
            GoogleLogin gg = new GoogleLogin();
            String accessToken = gg.getToken(code);
            Account accGoogle = gg.getUserInfo(accessToken);

            // Xử lý username nếu Google không trả về
            if (accGoogle.getUsername() == null || accGoogle.getUsername().isEmpty()) {
                String email = accGoogle.getEmail();
                String username = email != null && email.contains("@") ? email.substring(0, email.indexOf("@")) : "";
                accGoogle.setUsername(username);
            }

            AccountDAO1 dao = AccountDAO1.getInstance();

            try {
                if (dao.checkEmailExists(accGoogle.getEmail())) {
                    // Đã có tài khoản => Đăng nhập luôn
                    Account accDB = dao.getAccountByEmail(accGoogle.getEmail());
                    request.getSession().setAttribute("account", accDB); // <--- đồng nhất key!
                    response.sendRedirect(request.getContextPath() + "/home");
                } else {
                    // Chưa có tài khoản => Bổ sung thông tin
                    request.getSession().setAttribute("account", accGoogle); // <--- đồng nhất key!
                    response.sendRedirect(request.getContextPath() + "/updategoogle");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                response.sendRedirect("login");
            }
        } else {
            response.sendRedirect("login");
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
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
