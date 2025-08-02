package controller.google;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
import dao.AccountDAO1;

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
@WebServlet(urlPatterns = {"/updategoogle"})
public class UpdateGoogleServlet extends HttpServlet {

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
            out.println("<title>Servlet UpdateGoogleServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet UpdateGoogleServlet at " + request.getContextPath() + "</h1>");
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
        request.getRequestDispatcher("/WEB-INF/login/google/updategoogle.jsp").forward(request, response);
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

        String username = request.getParameter("username");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        // String city = request.getParameter("city");
        String address = request.getParameter("address");
        String gender = request.getParameter("gender");
        // String password = request.getParameter("password");
        // String confirmPassword = request.getParameter("confirmPassword");

        AccountDAO1 dao = AccountDAO1.getInstance();
        String error = null;

        try {
            // Check username trùng
            if (dao.checkUsernameExists(username)) {
                error = "Tên người dùng này đã tồn tại. Vui lòng chọn tên khác.";
                //        } else if (!password.equals(confirmPassword)) {
                //         error = "Mật khẩu và xác nhận mật khẩu không khớp.";
            }

            if (error != null) {
                request.setAttribute("error", error);
                request.getRequestDispatcher("/WEB-INF/login/google/updategoogle.jsp").forward(request, response);
                return;
            }

            // Tạo Account object
            Account acc = new Account();
            acc.setUsername(username);
            acc.setFullName(fullName);
            acc.setEmail(email);
            acc.setPhone(phone);
            acc.setAddress(address);
            acc.setGender(gender);
            acc.setRole(0);
            acc.setStatus("Active");
            // acc.setPassword(MD5Util.hash(password));

            acc.setPassword(db.MD5Util.hash("GOOGLE_LOGIN"));
            // Lưu mới vào DB
            dao.insertAccount(acc);

            // Lấy lại thông tin đầy đủ từ DB để set lên session (accountID v.v)
            Account accDb = dao.getAccountByEmail(email);
            request.getSession().setAttribute("account", accDb);

            response.sendRedirect(request.getContextPath() + "/home");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Đã xảy ra lỗi: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/login/google/updategoogle.jsp").forward(request, response);
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
