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
            try {
                int staffId = Integer.parseInt(idParam);
                boolean deleted = staffDAO.deleteStaffCompletely(staffId);
                if (!deleted) {
                    request.setAttribute("errorMessage", "Không thể xóa nhân viên này vì có dữ liệu liên quan!");
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
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "ID nhân viên không hợp lệ.");
                List<Staff> staffList = staffDAO.getAllStaff();
                int totalStaff = staffDAO.countStaff();
                int activeCount = staffDAO.countStaffByStatus("Active");
                int inactiveCount = staffDAO.countStaffByStatus("Inactive");

                request.setAttribute("staffList", staffList);
                request.setAttribute("totalStaff", totalStaff);
                request.setAttribute("activeStaffCount", activeCount);
                request.setAttribute("inactiveStaffCount", inactiveCount);
                request.getRequestDispatcher("/WEB-INF/admin/staff/manage-staff.jsp").forward(request, response);
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
            try {
                int id = Integer.parseInt(idParam);
                String status = request.getParameter("status");
                String newStatus = status.equals("Active") ? "Inactive" : "Active";
                staffDAO.updateStaffStatus(id, newStatus);
                Staff staff = staffDAO.getStaffById(id);
                accDAO.updateAccountStatus(staff.getAccountID(), newStatus);
                response.sendRedirect(request.getContextPath() + "/admin/staff");
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "ID nhân viên không hợp lệ.");
                response.sendRedirect(request.getContextPath() + "/admin/staff");
            } catch (Exception e) {
                request.setAttribute("errorMessage", "Lỗi khi cập nhật trạng thái: " + e.getMessage());
                response.sendRedirect(request.getContextPath() + "/admin/staff");
            }
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
                    try {
                        int editId = Integer.parseInt(editIdParam);
                        Staff staff = staffDAO.getStaffById(editId);
                        if (staff != null) {
                            request.setAttribute("staff", staff);
                            request.getRequestDispatcher("/WEB-INF/admin/staff/edit-staff.jsp").forward(request, response);
                        } else {
                            request.setAttribute("errorMessage", "Không tìm thấy nhân viên với ID này.");
                            response.sendRedirect(request.getContextPath() + "/admin/staff");
                        }
                    } catch (NumberFormatException e) {
                        request.setAttribute("errorMessage", "ID nhân viên không hợp lệ.");
                        response.sendRedirect(request.getContextPath() + "/admin/staff");
                    }
                    break;
                case "detail":
                    String detailIdParam = request.getParameter("id");
                    if (detailIdParam == null || detailIdParam.trim().isEmpty()) {
                        response.sendRedirect(request.getContextPath() + "/admin/staff");
                        return;
                    }
                    try {
                        int staffId = Integer.parseInt(detailIdParam);
                        Staff staffDetail = staffDAO.getStaffById(staffId);
                        if (staffDetail != null) {
                            request.setAttribute("staff", staffDetail);
                            request.getRequestDispatcher("/WEB-INF/admin/staff/staff-detail.jsp").forward(request, response);
                        } else {
                            request.setAttribute("errorMessage", "Không tìm thấy nhân viên với ID này.");
                            response.sendRedirect(request.getContextPath() + "/admin/staff");
                        }
                    } catch (NumberFormatException e) {
                        request.setAttribute("errorMessage", "ID nhân viên không hợp lệ.");
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
                String username = request.getParameter("username");
                String password = request.getParameter("password");
                String email = request.getParameter("email");
                String fullName = request.getParameter("fullName");
                String phone = request.getParameter("phone");
                String address = request.getParameter("address");
                String gender = request.getParameter("gender");
                String status = request.getParameter("status");
                int role = 2; // vai trò nhân viên

                // Validate individual fields
                if (username == null || username.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Vui lòng nhập tên đăng nhập.");
                    request.getRequestDispatcher("/WEB-INF/admin/staff/create-staff.jsp").forward(request, response);
                    return;
                }
                if (password == null || password.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Vui lòng nhập mật khẩu.");
                    request.getRequestDispatcher("/WEB-INF/admin/staff/create-staff.jsp").forward(request, response);
                    return;
                }
                if (email == null || email.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Vui lòng nhập email.");
                    request.getRequestDispatcher("/WEB-INF/admin/staff/create-staff.jsp").forward(request, response);
                    return;
                }
                if (fullName == null || fullName.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Vui lòng nhập họ tên.");
                    request.getRequestDispatcher("/WEB-INF/admin/staff/create-staff.jsp").forward(request, response);
                    return;
                }
                if (phone == null || phone.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Vui lòng nhập số điện thoại.");
                    request.getRequestDispatcher("/WEB-INF/admin/staff/create-staff.jsp").forward(request, response);
                    return;
                }
                if (address == null || address.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Vui lòng nhập địa chỉ.");
                    request.getRequestDispatcher("/WEB-INF/admin/staff/create-staff.jsp").forward(request, response);
                    return;
                }
                if (gender == null || gender.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Vui lòng chọn giới tính.");
                    request.getRequestDispatcher("/WEB-INF/admin/staff/create-staff.jsp").forward(request, response);
                    return;
                }
                if (status == null || status.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Vui lòng chọn trạng thái.");
                    request.getRequestDispatcher("/WEB-INF/admin/staff/create-staff.jsp").forward(request, response);
                    return;
                }

                // Trim all values
                username = username.trim();
                password = password.trim();
                email = email.trim();
                fullName = fullName.trim();
                phone = phone.trim();
                address = address.trim();
                gender = gender.trim();
                status = status.trim();

                // Check for duplicate username and email
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
                
                if (accDAO.isPhoneExists(phone)) {
                    request.setAttribute("errorMessage", "Phone đã tồn tại.");
                    request.getRequestDispatcher("/WEB-INF/admin/account/create-account.jsp").forward(request, response);
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
                    request.setAttribute("errorMessage", "Tạo tài khoản thất bại. Vui lòng thử lại.");
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

                String username = request.getParameter("username");
                String password = request.getParameter("password");
                String email = request.getParameter("email");
                String fullName = request.getParameter("fullName");
                String phone = request.getParameter("phone");
                String address = request.getParameter("address");
                String gender = request.getParameter("gender");
                String status = request.getParameter("status");

                // Validate individual fields
                if (username == null || username.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Vui lòng nhập tên đăng nhập.");
                    request.setAttribute("staff", staffDAO.getStaffById(staffID));
                    request.getRequestDispatcher("/WEB-INF/admin/staff/edit-staff.jsp").forward(request, response);
                    return;
                }
                if (email == null || email.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Vui lòng nhập email.");
                    request.setAttribute("staff", staffDAO.getStaffById(staffID));
                    request.getRequestDispatcher("/WEB-INF/admin/staff/edit-staff.jsp").forward(request, response);
                    return;
                }
                if (fullName == null || fullName.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Vui lòng nhập họ tên.");
                    request.setAttribute("staff", staffDAO.getStaffById(staffID));
                    request.getRequestDispatcher("/WEB-INF/admin/staff/edit-staff.jsp").forward(request, response);
                    return;
                }
                if (phone == null || phone.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Vui lòng nhập số điện thoại.");
                    request.setAttribute("staff", staffDAO.getStaffById(staffID));
                    request.getRequestDispatcher("/WEB-INF/admin/staff/edit-staff.jsp").forward(request, response);
                    return;
                }
                if (address == null || address.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Vui lòng nhập địa chỉ.");
                    request.setAttribute("staff", staffDAO.getStaffById(staffID));
                    request.getRequestDispatcher("/WEB-INF/admin/staff/edit-staff.jsp").forward(request, response);
                    return;
                }
                if (gender == null || gender.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Vui lòng chọn giới tính.");
                    request.setAttribute("staff", staffDAO.getStaffById(staffID));
                    request.getRequestDispatcher("/WEB-INF/admin/staff/edit-staff.jsp").forward(request, response);
                    return;
                }
                if (status == null || status.trim().isEmpty()) {
                    request.setAttribute("errorMessage", "Vui lòng chọn trạng thái.");
                    request.setAttribute("staff", staffDAO.getStaffById(staffID));
                    request.getRequestDispatcher("/WEB-INF/admin/staff/edit-staff.jsp").forward(request, response);
                    return;
                }

                // Trim all values
                username = username.trim();
                password = password != null ? password.trim() : "";
                email = email.trim();
                fullName = fullName.trim();
                phone = phone.trim();
                address = address.trim();
                gender = gender.trim();
                status = status.trim();

                // Check for duplicate username and email (excluding current account)
                Account checkUsername = accDAO.getAccountByUsername(username);
                if (checkUsername != null && checkUsername.getAccountID() != accountID) {
                    request.setAttribute("errorMessage", "Tên đăng nhập đã tồn tại.");
                    request.setAttribute("staff", staffDAO.getStaffById(staffID));
                    request.getRequestDispatcher("/WEB-INF/admin/staff/edit-staff.jsp").forward(request, response);
                    return;
                }

                Account checkEmail = accDAO.getAccountByEmail(email);
                if (checkEmail != null && checkEmail.getAccountID() != accountID) {
                    request.setAttribute("errorMessage", "Email đã tồn tại.");
                    request.setAttribute("staff", staffDAO.getStaffById(staffID));
                    request.getRequestDispatcher("/WEB-INF/admin/staff/edit-staff.jsp").forward(request, response);
                    return;
                }
                
                if (accDAO.isPhoneExists(phone)) {
                    request.setAttribute("errorMessage", "Phone đã tồn tại.");
                    request.getRequestDispatcher("/WEB-INF/admin/account/create-account.jsp").forward(request, response);
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
                    request.setAttribute("errorMessage", "Cập nhật tài khoản thất bại. Vui lòng thử lại.");
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
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "ID không hợp lệ.");
                request.getRequestDispatcher("/WEB-INF/admin/staff/edit-staff.jsp").forward(request, response);
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
