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
        if (action != null && action.equals("delete")) {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/admin/staff");
                return;
            }
            int staffId = Integer.parseInt(idParam);

            try {
                // Use the comprehensive deletion method
                boolean deleted = staffDAO.deleteStaffCompletely(staffId);
                if (!deleted) {
                    request.setAttribute("errorMessage", "Không thể xóa nhân viên này!");
                    List<Staff> staffList = staffDAO.getAllStaff();

                    // Get statistics for dashboard
                    int totalStaff = staffDAO.countStaff();
                    int activeStaffCount = staffDAO.countStaffByStatus("Active");
                    int inactiveStaffCount = staffDAO.countStaffByStatus("Inactive");

                    request.setAttribute("staffList", staffList);
                    request.setAttribute("totalStaff", totalStaff);
                    request.setAttribute("activeStaffCount", activeStaffCount);
                    request.setAttribute("inactiveStaffCount", inactiveStaffCount);
                    request.getRequestDispatcher("/WEB-INF/admin/staff/manage-staff.jsp").forward(request, response);
                    return;
                }

                response.sendRedirect(request.getContextPath() + "/admin/staff");

            } catch (Exception e) {
                request.setAttribute("errorMessage", "Lỗi khi xóa nhân viên: " + e.getMessage());
                List<Staff> staffList = staffDAO.getAllStaff();

                // Get statistics for dashboard
                int totalStaff = staffDAO.countStaff();
                int activeStaffCount = staffDAO.countStaffByStatus("Active");
                int inactiveStaffCount = staffDAO.countStaffByStatus("Inactive");

                request.setAttribute("staffList", staffList);
                request.setAttribute("totalStaff", totalStaff);
                request.setAttribute("activeStaffCount", activeStaffCount);
                request.setAttribute("inactiveStaffCount", inactiveStaffCount);
                request.getRequestDispatcher("/WEB-INF/admin/staff/manage-staff.jsp").forward(request, response);
            }
            return;
        }

        if (action != null && action.equals("status")) {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.trim().isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/admin/staff");
                return;
            }
            int id = Integer.parseInt(idParam);
            String status = request.getParameter("status");
            String newStatus = status.equals("Active") ? "Inactive" : "Active";
            boolean result = staffDAO.updateStaffStatus(id, newStatus);
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

                    // Get statistics for dashboard
                    int totalStaff = staffDAO.countStaff();
                    int activeStaffCount = staffDAO.countStaffByStatus("Active");
                    int inactiveStaffCount = staffDAO.countStaffByStatus("Inactive");

                    request.setAttribute("staffList", staffList);
                    request.setAttribute("totalStaff", totalStaff);
                    request.setAttribute("activeStaffCount", activeStaffCount);
                    request.setAttribute("inactiveStaffCount", inactiveStaffCount);
                    request.getRequestDispatcher("/WEB-INF/admin/staff/manage-staff.jsp").forward(request, response);
                    break;
            }
        } else {
            List<Staff> staffList = staffDAO.getAllStaff();

            // Get statistics for dashboard
            int totalStaff = staffDAO.countStaff();
            int activeStaffCount = staffDAO.countStaffByStatus("Active");
            int inactiveStaffCount = staffDAO.countStaffByStatus("Inactive");

            request.setAttribute("staffList", staffList);
            request.setAttribute("totalStaff", totalStaff);
            request.setAttribute("activeStaffCount", activeStaffCount);
            request.setAttribute("inactiveStaffCount", inactiveStaffCount);
            request.getRequestDispatcher("/WEB-INF/admin/staff/manage-staff.jsp").forward(request, response);
        }
    }
// Huuduc đã chỉnh sửa 
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
                int role = 2;

                // KIỂM TRA TRÙNG
                if (accDAO.isUsernameExists(username)) {
                    request.setAttribute("errorMessage", "Username already exists.");
                    request.getRequestDispatcher("/WEB-INF/admin/staff/create-staff.jsp").forward(request, response);
                    return;
                }
                if (accDAO.isEmailExists(email)) {
                    request.setAttribute("errorMessage", "Email already exists.");
                    request.getRequestDispatcher("/WEB-INF/admin/staff/create-staff.jsp").forward(request, response);
                    return;
                }
                if (username == null || username.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Username is required.");
                    request.getRequestDispatcher("/WEB-INF/admin/staff/create-staff.jsp").forward(request, response);
                    return;
                }
                if (password == null || password.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Password is required.");
                    request.getRequestDispatcher("/WEB-INF/admin/staff/create-staff.jsp").forward(request, response);
                    return;
                }
                if (email == null || email.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Email is required.");
                    request.getRequestDispatcher("/WEB-INF/admin/staff/create-staff.jsp").forward(request, response);
                    return;
                }
                if (fullName == null || fullName.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Full name is required.");
                    request.getRequestDispatcher("/WEB-INF/admin/staff/create-staff.jsp").forward(request, response);
                    return;
                }
                if (phone == null || phone.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Phone is required.");
                    request.getRequestDispatcher("/WEB-INF/admin/staff/create-staff.jsp").forward(request, response);
                    return;
                }
                if (address == null || address.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Address is required.");
                    request.getRequestDispatcher("/WEB-INF/admin/staff/create-staff.jsp").forward(request, response);
                    return;
                }
                if (gender == null || gender.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Gender is required.");
                    request.getRequestDispatcher("/WEB-INF/admin/staff/create-staff.jsp").forward(request, response);
                    return;
                }
                if (status == null || status.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Status is required.");
                    request.getRequestDispatcher("/WEB-INF/admin/staff/create-staff.jsp").forward(request, response);
                    return;
                }

                // Create account first
                // Tạo account
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
                    request.setAttribute("errorMessage", "Failed to create account. Unknown error.");
                    request.getRequestDispatcher("/WEB-INF/admin/staff/create-staff.jsp").forward(request, response);
                    return;
                }

                // KHÔNG insert staff nữa! Trigger đã tự tạo staff rồi
                response.sendRedirect(request.getContextPath() + "/admin/staff");
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("errorMessage", "Error: " + e.getMessage());
                request.getRequestDispatcher("/WEB-INF/admin/staff/create-staff.jsp").forward(request, response);
            }

//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        String action = request.getParameter("action");
//        StaffDAO staffDAO = new StaffDAO();
//        AccountDAO accDAO = new AccountDAO();
//
//        if ("create".equals(action)) {
//            try {
//                String username = request.getParameter("username");
//                String password = request.getParameter("password");
//                String email = request.getParameter("email");
//                String fullName = request.getParameter("fullName");
//                String phone = request.getParameter("phone");
//                String address = request.getParameter("address");
//                String gender = request.getParameter("gender");
//
//                // Check for null role parameter and set default to 2 (Staff)
//                String roleParam = request.getParameter("role");
//                int role = 2; // Default to Staff role
//                if (roleParam != null && !roleParam.trim().isEmpty()) {
//                    role = Integer.parseInt(roleParam);
//                }
//
//                String status = request.getParameter("status");
//
//                // Validate required fields
//                if (username == null || username.trim().isEmpty()) {
//                    request.setAttribute("errorMessage", "Username is required.");
//                    request.getRequestDispatcher("/WEB-INF/admin/staff/create-staff.jsp").forward(request, response);
//                    return;
//                }
//                if (password == null || password.trim().isEmpty()) {
//                    request.setAttribute("errorMessage", "Password is required.");
//                    request.getRequestDispatcher("/WEB-INF/admin/staff/create-staff.jsp").forward(request, response);
//                    return;
//                }
//                if (email == null || email.trim().isEmpty()) {
//                    request.setAttribute("errorMessage", "Email is required.");
//                    request.getRequestDispatcher("/WEB-INF/admin/staff/create-staff.jsp").forward(request, response);
//                    return;
//                }
//                if (fullName == null || fullName.trim().isEmpty()) {
//                    request.setAttribute("errorMessage", "Full name is required.");
//                    request.getRequestDispatcher("/WEB-INF/admin/staff/create-staff.jsp").forward(request, response);
//                    return;
//                }
//                if (phone == null || phone.trim().isEmpty()) {
//                    request.setAttribute("errorMessage", "Phone is required.");
//                    request.getRequestDispatcher("/WEB-INF/admin/staff/create-staff.jsp").forward(request, response);
//                    return;
//                }
//                if (address == null || address.trim().isEmpty()) {
//                    request.setAttribute("errorMessage", "Address is required.");
//                    request.getRequestDispatcher("/WEB-INF/admin/staff/create-staff.jsp").forward(request, response);
//                    return;
//                }
//                if (gender == null || gender.trim().isEmpty()) {
//                    request.setAttribute("errorMessage", "Gender is required.");
//                    request.getRequestDispatcher("/WEB-INF/admin/staff/create-staff.jsp").forward(request, response);
//                    return;
//                }
//                if (status == null || status.trim().isEmpty()) {
//                    request.setAttribute("errorMessage", "Status is required.");
//                    request.getRequestDispatcher("/WEB-INF/admin/staff/create-staff.jsp").forward(request, response);
//                    return;
//                }
//
//                // Create account first
//                Account account = new Account();
//                account.setUsername(username);
//                account.setPassword(MD5Util.hash(password));
//                account.setEmail(email);
//                account.setFullName(fullName);
//                account.setPhone(phone);
//                account.setAddress(address);
//                account.setGender(gender);
//                account.setRole(role);
//                account.setStatus(status);
//
//                boolean accountCreated = accDAO.insertFullAccount(account);
//                if (!accountCreated) {
//                    request.setAttribute("errorMessage", "Failed to create account. Username or email may already exist.");
//                    request.getRequestDispatcher("/WEB-INF/admin/staff/create-staff.jsp").forward(request, response);
//                    return;
//                }
//
//                // Get the created account ID
//                Account createdAccount = accDAO.getAccountByUsername(username);
//                if (createdAccount == null) {
//                    request.setAttribute("errorMessage", "Failed to retrieve created account.");
//                    request.getRequestDispatcher("/WEB-INF/admin/staff/create-staff.jsp").forward(request, response);
//                    return;
//                }
//
//                // Create staff record
//                Staff staff = new Staff();
//                staff.setAccountID(createdAccount.getAccountID());
//                staff.setFullName(fullName);
//                staff.setEmail(email);
//                staff.setPhone(phone);
//                staff.setGender(gender);
//                staff.setAddress(address);
//                staff.setStatus(status);
//
//                boolean staffCreated = staffDAO.insertStaff(staff);
//                if (staffCreated) {
//                    response.sendRedirect(request.getContextPath() + "/admin/staff");
//                } else {
//                    // If staff creation fails, delete the account
//                    accDAO.deleteAccount(createdAccount.getAccountID());
//                    request.setAttribute("errorMessage", "Failed to create staff record. Please try again.");
//                    request.getRequestDispatcher("/WEB-INF/admin/staff/create-staff.jsp").forward(request, response);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                request.setAttribute("errorMessage", "Error: " + e.getMessage());
//                request.getRequestDispatcher("/WEB-INF/admin/staff/create-staff.jsp").forward(request, response);
//            }
        } else if ("edit".equals(action)) {
            try {
                String staffIDParam = request.getParameter("staffID");
                String accountIDParam = request.getParameter("accountID");

                if (staffIDParam == null || staffIDParam.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Invalid staff ID.");
                    request.getRequestDispatcher("/WEB-INF/admin/staff/edit-staff.jsp").forward(request, response);
                    return;
                }

                if (accountIDParam == null || accountIDParam.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Invalid account ID.");
                    request.getRequestDispatcher("/WEB-INF/admin/staff/edit-staff.jsp").forward(request, response);
                    return;
                }

                int staffID = Integer.parseInt(staffIDParam);
                int accountID = Integer.parseInt(accountIDParam);

                // Get form data
                String username = request.getParameter("username");
                String password = request.getParameter("password");
                String email = request.getParameter("email");
                String fullName = request.getParameter("fullName");
                String phone = request.getParameter("phone");
                String address = request.getParameter("address");
                String gender = request.getParameter("gender");
                String status = request.getParameter("status");

                // Validate required fields
                if (username == null || username.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Username is required.");
                    Staff currentStaff = staffDAO.getStaffById(staffID);
                    request.setAttribute("staff", currentStaff);
                    request.getRequestDispatcher("/WEB-INF/admin/staff/edit-staff.jsp").forward(request, response);
                    return;
                }
                if (email == null || email.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Email is required.");
                    Staff currentStaff = staffDAO.getStaffById(staffID);
                    request.setAttribute("staff", currentStaff);
                    request.getRequestDispatcher("/WEB-INF/admin/staff/edit-staff.jsp").forward(request, response);
                    return;
                }
                if (fullName == null || fullName.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Full name is required.");
                    Staff currentStaff = staffDAO.getStaffById(staffID);
                    request.setAttribute("staff", currentStaff);
                    request.getRequestDispatcher("/WEB-INF/admin/staff/edit-staff.jsp").forward(request, response);
                    return;
                }
                if (phone == null || phone.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Phone is required.");
                    Staff currentStaff = staffDAO.getStaffById(staffID);
                    request.setAttribute("staff", currentStaff);
                    request.getRequestDispatcher("/WEB-INF/admin/staff/edit-staff.jsp").forward(request, response);
                    return;
                }
                if (address == null || address.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Address is required.");
                    Staff currentStaff = staffDAO.getStaffById(staffID);
                    request.setAttribute("staff", currentStaff);
                    request.getRequestDispatcher("/WEB-INF/admin/staff/edit-staff.jsp").forward(request, response);
                    return;
                }
                if (gender == null || gender.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Gender is required.");
                    Staff currentStaff = staffDAO.getStaffById(staffID);
                    request.setAttribute("staff", currentStaff);
                    request.getRequestDispatcher("/WEB-INF/admin/staff/edit-staff.jsp").forward(request, response);
                    return;
                }
                if (status == null || status.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Status is required.");
                    Staff currentStaff = staffDAO.getStaffById(staffID);
                    request.setAttribute("staff", currentStaff);
                    request.getRequestDispatcher("/WEB-INF/admin/staff/edit-staff.jsp").forward(request, response);
                    return;
                }

                // Update Account first
                Account account = new Account();
                account.setAccountID(accountID);
                account.setUsername(username);

                // If password field is not empty, update with new MD5 hashed password
                if (password != null && !password.trim().isEmpty()) {
                    account.setPassword(MD5Util.hash(password));
                } else {
                    // Retrieve current password from database if not changed
                    Account existingAccount = accDAO.getFullAccountById(accountID);
                    if (existingAccount != null) {
                        account.setPassword(existingAccount.getPassword());
                    }
                }

                account.setEmail(email);
                account.setFullName(fullName);
                account.setPhone(phone);
                account.setAddress(address);
                account.setGender(gender);
                account.setRole(2); // Staff role
                account.setStatus(status);

                boolean accountUpdated = accDAO.updateFullAccount(account);
                if (!accountUpdated) {
                    request.setAttribute("errorMessage", "Failed to update account. Username or email may already exist.");
                    Staff currentStaff = staffDAO.getStaffById(staffID);
                    request.setAttribute("staff", currentStaff);
                    request.getRequestDispatcher("/WEB-INF/admin/staff/edit-staff.jsp").forward(request, response);
                    return;
                }

                // Update Staff record
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
                    request.setAttribute("errorMessage", "Failed to update staff record. Please try again.");
                    Staff currentStaff = staffDAO.getStaffById(staffID);
                    request.setAttribute("staff", currentStaff);
                    request.getRequestDispatcher("/WEB-INF/admin/staff/edit-staff.jsp").forward(request, response);
                }
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("errorMessage", "Error: " + e.getMessage());
                try {
                    String staffIDParam = request.getParameter("staffID");
                    if (staffIDParam != null && !staffIDParam.trim().isEmpty()) {
                        int staffID = Integer.parseInt(staffIDParam);
                        Staff currentStaff = staffDAO.getStaffById(staffID);
                        request.setAttribute("staff", currentStaff);
                    }
                } catch (Exception ex) {
                    // Ignore error getting current staff
                }
                request.getRequestDispatcher("/WEB-INF/admin/staff/edit-staff.jsp").forward(request, response);
            }
        }
    }

}
