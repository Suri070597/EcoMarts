package controller.register;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author HuuDuc
 */
@WebServlet(name = "AdminServlet", urlPatterns = {"/admin"})
public class AdminServlet extends HttpServlet {

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
            out.println("<title>Servlet OtpconfirmServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet OtpconfirmServlet at " + request.getContextPath() + "</h1>");
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
        System.out.println("Đang truy cập trang quản trị");
        Integer role = (Integer) request.getSession().getAttribute("role");
        String email = (String) request.getSession().getAttribute("email");
        System.out.println("Admin access check: role=" + role + ", email=" + email);

        if (role != null && role == 1 && email != null) {
            System.out.println("Quyền truy cập quản trị được cấp: email=" + email);
            request.getRequestDispatcher("/WEB-INF/admin/dashboard.jsp").forward(request, response);
        } else {
            System.out.println("Truy cập trái phép vào trang quản trị: email=" + (email != null ? email : "null") + ", vai trò=" + (role != null ? role : "null"));
            request.getSession().setAttribute("error", "Vui lòng đăng nhập với tài khoản admin!");
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }

    @Override
    public String getServletInfo() {
        return "Handles admin page access";
    }
}
