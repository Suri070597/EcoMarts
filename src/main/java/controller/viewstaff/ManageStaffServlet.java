/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.viewstaff;

import dao.StaffDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Account;
import model.Staff;

/**
 *
 * @author HuuDuc
 */
@WebServlet(name = "ManageStaffServlet", urlPatterns = {"/ManageStaffServlet"})
public class ManageStaffServlet extends HttpServlet {

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
            out.println("<title>Servlet ManageStaffServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ManageStaffServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param req
     * @param resp
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession();
        Account account = (Account) session.getAttribute("account");
        if (account != null && account.getRole() == 2) {
            StaffDAO staffDAO = new StaffDAO();
            Staff staff = staffDAO.getStaffByAccountId(account.getAccountID());
            req.setAttribute("staff", staff);
            req.getRequestDispatcher("/WEB-INF/staff/staffs/manage-staff.jsp").forward(req, resp);
        } else {
            resp.sendRedirect("login.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            int staffId = Integer.parseInt(req.getParameter("staffId"));
            String fullName = req.getParameter("fullName");
            String phone = req.getParameter("phone");
            String address = req.getParameter("address");
            String gender = req.getParameter("gender");

            StaffDAO staffDAO = new StaffDAO();
            Staff staff = staffDAO.getStaffById(staffId);
            if (staff != null) {
                staff.setFullName(fullName);
                staff.setPhone(phone);
                staff.setAddress(address);
                staff.setGender(gender);

                boolean updated = staffDAO.updateStaff(staff);

                if (updated) {
                    req.setAttribute("message", "Cập nhật thành công!");
                } else {
                    req.setAttribute("message", "Cập nhật thất bại!");
                }
                req.setAttribute("staff", staffDAO.getStaffById(staffId));
            } else {
                req.setAttribute("message", "Không tìm thấy thông tin nhân viên.");
            }
            req.getRequestDispatcher("/WEB-INF/staff/staffs/manage-staff.jsp").forward(req, resp);
        } catch (ServletException | IOException | NumberFormatException e) {
            req.setAttribute("message", "Cập nhật thất bại: " + e.getMessage());
            req.getRequestDispatcher("/WEB-INF/staff/staffs/manage-staff.jsp").forward(req, resp);
        }
    }

    @Override
    public String getServletInfo() {
        return "Servlet for managing suppliers in the admin panel";
    }
}
