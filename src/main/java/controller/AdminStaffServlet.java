package controller;

import java.io.IOException;
import java.util.List;

import dao.AccountDAO;
import dao.StaffDAO;
import db.MD5Util;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Account;
import model.Staff;

@WebServlet(name = "AdminStaffServlet", urlPatterns = {"/admin/staff"})
public class AdminStaffServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String view = request.getParameter("view");
        String action = request.getParameter("action");
        StaffDAO staffDAO = new StaffDAO();
        AccountDAO accDAO = new AccountDAO();

        if ("delete".equals(action)) {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/admin/staff");
                return;
            }
            int staffId = Integer.parseInt(idParam);
            try {
                boolean deleted = staffDAO.deleteStaffCompletely(staffId);
                if (!deleted) {
                    request.setAttribute("errorMessage", "Không thể xóa nhân viên này!");
                    List<Staff> staffList = staffDAO.getAllStaff();
                    int totalStaff = staffDAO.countStaff();
                    int activeCount = staffDAO.countStaffByStatus("Active");
                    int inactiveCount = staffDAO.countStaffByStatus("Inactive");

                    request.setAttribute("staffList", staffList);
                    request.setAttribute("totalStaff", totalStaff);
                    request.setAttribute("activeStaffCount", activeCount);
                    request.setAttribute("inactiveStaffCount", inactiveCount);
                    request.getRequestDispatcher("/WEB-INF/admin/staff/manage-staff.jsp").forward(request, response);
                    return;
                }
                response.sendRedirect(request.getContextPath() + "/admin/staff");
            } catch (Exception e) {
                request.setAttribute("errorMessage", "Lỗi khi xóa nhân viên: " + e.getMessage());
                List<Staff> staffList = staffDAO.getAllStaff();
                int totalStaff = staffDAO.countStaff();
                int activeCount = staffDAO.countStaffByStatus("Active");
                int inactiveCount = staffDAO.countStaffByStatus("Inactive");

                request.setAttribute("staffList", staffList);
                request.setAttribute("totalStaff", totalStaff);
                request.setAttribute("activeStaffCount", activeCount);
                request.setAttribute("inactiveStaffCount", inactiveCount);
                request.getRequestDispatcher("/WEB-INF/admin/staff/manage-staff.jsp").forward(request, response);
            }
            return;
        }

        if ("status".equals(action)) {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/admin/staff");
                return;
            }
            int id = Integer.parseInt(idParam);
            String status = request.getParameter("status");
            String newStatus = status.equals("Active") ? "Inactive" : "Active";
            staffDAO.updateStaffStatus(id, newStatus);
            Staff staff = staffDAO.getStaffById(id);
            accDAO.updateAccountStatus(staff.getAccountID(), newStatus);
            response.sendRedirect(request.getContextPath() + "/admin/staff");
            return;
        }

        if (view != null) {
            switch (view) {
                case "create":
                    request.getRequestDispatcher("/WEB-INF/admin/staff/create-staff.jsp").forward(request, response);
                    break;
                case "edit":
                    String editIdParam = request.getParameter("id");
                    if (editIdParam == null || editIdParam.trim().isEmpty()) {
                        response.sendRedirect(request.getContextPath() + "/admin/staff");
                        return;
                    }
                    int editId = Integer.parseInt(editIdParam);
                    Staff staff = staffDAO.getStaffById(editId);
                    if (staff != null) {
                        request.setAttribute("staff", staff);
                        request.getRequestDispatcher("/WEB-INF/admin/staff/edit-staff.jsp").forward(request, response);
                    } else {
                        response.sendRedirect(request.getContextPath() + "/admin/staff");
                    }
                    break;
                case "detail":
                    String detailIdParam = request.getParameter("id");
                    if (detailIdParam == null || detailIdParam.trim().isEmpty()) {
                        response.sendRedirect(request.getContextPath() + "/admin/staff");
                        return;
                    }
                    int staffId = Integer.parseInt(detailIdParam);
                    Staff staffDetail = staffDAO.getStaffById(staffId);
                    if (staffDetail != null) {
                        request.setAttribute("staff", staffDetail);
                        request.getRequestDispatcher("/WEB-INF/admin/staff/staff-detail.jsp").forward(request, response);
                    } else {
                        response.sendRedirect(request.getContextPath() + "/admin/staff");
                    }
                    break;
                default:
                    String keyword = request.getParameter("search");
                    List<Staff> staffList;
                    if (keyword != null && !keyword.trim().isEmpty()) {
                        staffList = staffDAO.searchStaff(keyword);
                        request.setAttribute("keyword", keyword);
                    } else {
                        staffList = staffDAO.getAllStaff();
                    }
                    int totalStaff = staffDAO.countStaff();
                    int activeCount = staffDAO.countStaffByStatus("Active");
                    int inactiveCount = staffDAO.countStaffByStatus("Inactive");

                    request.setAttribute("staffList", staffList);
                    request.setAttribute("totalStaff", totalStaff);
                    request.setAttribute("activeStaffCount", activeCount);
                    request.setAttribute("inactiveStaffCount", inactiveCount);
                    request.getRequestDispatcher("/WEB-INF/admin/staff/manage-staff.jsp").forward(request, response);
            }
        } else {
            List<Staff> staffList = staffDAO.getAllStaff();
            int totalStaff = staffDAO.countStaff();
            int activeCount = staffDAO.countStaffByStatus("Active");
            int inactiveCount = staffDAO.countStaffByStatus("Inactive");
            request.setAttribute("staffList", staffList);
            request.setAttribute("totalStaff", totalStaff);
            request.setAttribute("activeStaffCount", activeCount);
            request.setAttribute("inactiveStaffCount", inactiveCount);
            request.getRequestDispatcher("/WEB-INF/admin/staff/manage-staff.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        StaffDAO staffDAO = new StaffDAO();
        AccountDAO accDAO = new AccountDAO();

        if ("create".equals(action)) {
            try {
                String username = request.getParameter("username").trim();
                String password = request.getParameter("password").trim();
                String email = request.getParameter("email").trim();
                String fullName = request.getParameter("fullName").trim();
                String phone = request.getParameter("phone").trim();
                String address = request.getParameter("address").trim();
                String gender = request.getParameter("gender").trim();
                String status = request.getParameter("status").trim();
                int role = 2; // vai trò nhân viên

                if (accDAO.isUsernameExists(username)) {
                    request.setAttribute("errorMessage", "Tên đăng nhập đã tồn tại.");
                    request.getRequestDispatcher("/WEB-INF/admin/staff/create-staff.jsp").forward(request, response);
                    return;
                }
                if (accDAO.isEmailExists(email)) {
                    request.setAttribute("errorMessage", "Email đã tồn tại.");
                    request.getRequestDispatcher("/WEB-INF/admin/staff/create-staff.jsp").forward(request, response);
                    return;
                }
                if (username.isEmpty() || password.isEmpty() || email.isEmpty() ||
                    fullName.isEmpty() || phone.isEmpty() || address.isEmpty() ||
                    gender.isEmpty() || status.isEmpty()) {
                    request.setAttribute("errorMessage", "Vui lòng điền đầy đủ các trường bắt buộc.");
                    request.getRequestDispatcher("/WEB-INF/admin/staff/create-staff.jsp").forward(request, response);
                    return;
                }

                Account account = new Account();
                account.setUsername(username);
                account.setPassword(MD5Util.hash(password));
                account.setEmail(email);
                account.setFullName(fullName);
                account.setPhone(phone);
                account.setAddress(address);
                account.setGender(gender);
                account.setRole(role);
                account.setStatus(status);

                boolean accountCreated = accDAO.insertFullAccount(account);
                if (!accountCreated) {
                    request.setAttribute("errorMessage", "Tạo tài khoản thất bại. Có thể tên đăng nhập hoặc email đã tồn tại.");
                    request.getRequestDispatcher("/WEB-INF/admin/staff/create-staff.jsp").forward(request, response);
                    return;
                }

                response.sendRedirect(request.getContextPath() + "/admin/staff");
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("errorMessage", "Lỗi: " + e.getMessage());
                request.getRequestDispatcher("/WEB-INF/admin/staff/create-staff.jsp").forward(request, response);
            }
        } else if ("edit".equals(action)) {
            try {
                String staffIDParam = request.getParameter("staffID");
                String accountIDParam = request.getParameter("accountID");

                if (staffIDParam == null || staffIDParam.trim().isEmpty() ||
                    accountIDParam == null || accountIDParam.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "ID không hợp lệ.");
                    request.getRequestDispatcher("/WEB-INF/admin/staff/edit-staff.jsp").forward(request, response);
                    return;
                }

                int staffID = Integer.parseInt(staffIDParam);
                int accountID = Integer.parseInt(accountIDParam);

                String username = request.getParameter("username").trim();
                String password = request.getParameter("password").trim();
                String email = request.getParameter("email").trim();
                String fullName = request.getParameter("fullName").trim();
                String phone = request.getParameter("phone").trim();
                String address = request.getParameter("address").trim();
                String gender = request.getParameter("gender").trim();
                String status = request.getParameter("status").trim();

                if (username.isEmpty() || email.isEmpty() || fullName.isEmpty() ||
                    phone.isEmpty() || address.isEmpty() || gender.isEmpty() || status.isEmpty()) {
                    request.setAttribute("errorMessage", "Vui lòng điền đầy đủ các trường bắt buộc.");
                    request.setAttribute("staff", staffDAO.getStaffById(staffID));
                    request.getRequestDispatcher("/WEB-INF/admin/staff/edit-staff.jsp").forward(request, response);
                    return;
                }

                Account account = new Account();
                account.setAccountID(accountID);
                account.setUsername(username);
                if (!password.isEmpty()) {
                    account.setPassword(MD5Util.hash(password));
                } else {
                    Account existing = accDAO.getFullAccountById(accountID);
                    if (existing != null) {
                        account.setPassword(existing.getPassword());
                    }
                }
                account.setEmail(email);
                account.setFullName(fullName);
                account.setPhone(phone);
                account.setAddress(address);
                account.setGender(gender);
                account.setRole(2);
                account.setStatus(status);

                boolean accountUpdated = accDAO.updateFullAccount(account);
                if (!accountUpdated) {
                    request.setAttribute("errorMessage", "Cập nhật tài khoản thất bại. Tài khoản hoặc email đã tồn tại.");
                    request.setAttribute("staff", staffDAO.getStaffById(staffID));
                    request.getRequestDispatcher("/WEB-INF/admin/staff/edit-staff.jsp").forward(request, response);
                    return;
                }

                Staff staff = new Staff();
                staff.setStaffID(staffID);
                staff.setAccountID(accountID);
                staff.setFullName(fullName);
                staff.setEmail(email);
                staff.setPhone(phone);
                staff.setGender(gender);
                staff.setAddress(address);
                staff.setStatus(status);

                boolean staffUpdated = staffDAO.updateStaff(staff);
                if (staffUpdated) {
                    response.sendRedirect(request.getContextPath() + "/admin/staff");
                } else {
                    request.setAttribute("errorMessage", "Cập nhật nhân viên thất bại. Vui lòng thử lại.");
                    request.setAttribute("staff", staffDAO.getStaffById(staffID));
                    request.getRequestDispatcher("/WEB-INF/admin/staff/edit-staff.jsp").forward(request, response);
                }
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("errorMessage", "Lỗi: " + e.getMessage());
                try {
                    String staffIDParam = request.getParameter("staffID");
                    if (staffIDParam != null && !staffIDParam.trim().isEmpty()) {
                        int staffID = Integer.parseInt(staffIDParam);
                        request.setAttribute("staff", staffDAO.getStaffById(staffID));
                    }
                } catch (Exception ex) {
                    // ignore
                }
                request.getRequestDispatcher("/WEB-INF/admin/staff/edit-staff.jsp").forward(request, response);
            }
        }
    }
}
