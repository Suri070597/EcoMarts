/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.viewstaff;

import dao.AccountDAO;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Account;

/**
 *
 * @author HuuDuc
 */
@WebServlet(name = "Changepasswordstaff", urlPatterns = {"/changepasswordstaff"})
public class Changepasswordstaff extends HttpServlet {

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
            out.println("<title>Servlet Changepasswordstaff</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet Changepasswordstaff at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the
    // + sign on the left to edit the code.">
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
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param req
     * @param resp
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        Account account = (Account) req.getSession().getAttribute("account");
        if (account == null || account.getRole() != 2) { // role 2 là staff
            resp.sendRedirect("login");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            req.setAttribute("message", "Mật khẩu xác nhận không khớp!");
            req.getRequestDispatcher("/WEB-INF/staff/staffs/changepasswordstaff.jsp").forward(req, resp);
            return;
        }

        // Hash password mới
        String newPasswordHash = db.MD5Util.hash(newPassword);

        // So sánh password mới (hash) với password cũ (hash)
        if (newPasswordHash.equals(account.getPassword())) {
            req.setAttribute("message", "Mật khẩu mới không được trùng với mật khẩu hiện tại!");
            req.getRequestDispatcher("/WEB-INF/staff/staffs/changepasswordstaff.jsp").forward(req, resp);
            return;
        }

        AccountDAO dao = new AccountDAO();
        boolean updated = dao.updatePassword(account.getAccountID(), newPasswordHash);

        if (updated) {
            req.setAttribute("message", "Đổi mật khẩu thành công! Đăng nhập lại.");
            req.getSession().invalidate();
            req.getRequestDispatcher("/WEB-INF/login/login.jsp").forward(req, resp);
        } else {
            req.setAttribute("message", "Lỗi khi cập nhật mật khẩu.");
            req.getRequestDispatcher("/WEB-INF/staff/staffs/changepasswordstaff.jsp").forward(req, resp);
        }
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
