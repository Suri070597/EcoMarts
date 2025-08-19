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

@WebServlet(name = "AdminAccountServlet", urlPatterns = {"/admin/account"})
public class AdminAccountServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String view = request.getParameter("view");
        String action = request.getParameter("action");
        AccountDAO accDAO = new AccountDAO();

        if (action != null && action.equals("delete")) {
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                String base = request.getContextPath() + "/admin/account";
                Account target = accDAO.getFullAccountById(id);
                if (target == null) {
                    response.sendRedirect(base + "?type=error&message=" + java.net.URLEncoder.encode("Tài khoản không tồn tại", java.nio.charset.StandardCharsets.UTF_8));
                    return;
                }
                if (target.getRole() == 1) {
                    response.sendRedirect(base + "?type=error&message=" + java.net.URLEncoder.encode("Không thể xóa tài khoản quản trị viên", java.nio.charset.StandardCharsets.UTF_8));
                    return;
                }
                boolean result = accDAO.deleteAccount(id);
                if (result) {
                    response.sendRedirect(base + "?type=success&message=" + java.net.URLEncoder.encode("Xóa tài khoản thành công", java.nio.charset.StandardCharsets.UTF_8));
                } else {
                    response.sendRedirect(base + "?type=error&message=" + java.net.URLEncoder.encode("Không thể xóa vì có liên kết dữ liệu liên quan", java.nio.charset.StandardCharsets.UTF_8));
                }
            } catch (Exception e) {
                String base = request.getContextPath() + "/admin/account";
                response.sendRedirect(base + "?type=error&message=" + java.net.URLEncoder.encode("Không thể xóa vì có liên kết dữ liệu liên quan", java.nio.charset.StandardCharsets.UTF_8));
            }
            return;
        }

        if (action != null && action.equals("status")) {
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                Account target = accDAO.getFullAccountById(id);
                if (target == null) {
                    response.sendRedirect(request.getContextPath() + "/admin/account?type=error&message=" + java.net.URLEncoder.encode("Tài khoản không tồn tại", java.nio.charset.StandardCharsets.UTF_8));
                    return;
                }
                if (target.getRole() == 1) {
                    response.sendRedirect(request.getContextPath() + "/admin/account?type=error&message=" + java.net.URLEncoder.encode("Không thể cập nhật trạng thái tài khoản quản trị viên", java.nio.charset.StandardCharsets.UTF_8));
                    return;
                }
                String status = request.getParameter("status");
                String newStatus = status.equals("Active") ? "Inactive" : "Active";
                accDAO.updateAccountStatus(id, newStatus);
                response.sendRedirect(request.getContextPath() + "/admin/account?type=success&message=" + java.net.URLEncoder.encode("Cập nhật trạng thái thành công", java.nio.charset.StandardCharsets.UTF_8));
            } catch (Exception e) {
                response.sendRedirect(request.getContextPath() + "/admin/account?type=error&message=" + java.net.URLEncoder.encode("Lỗi khi cập nhật trạng thái", java.nio.charset.StandardCharsets.UTF_8));
            }
            return;
        }

        if (view != null) {
            switch (view) {
                case "create":
                    request.getRequestDispatcher("/WEB-INF/admin/account/create-account.jsp").forward(request, response);
                    break;
                case "edit":
                    int id = Integer.parseInt(request.getParameter("id"));
                    Account account = accDAO.getFullAccountById(id);
                    if (account != null) {
                        if (account.getRole() == 1) {
                            response.sendRedirect(request.getContextPath() + "/admin/account?type=error&message=" + java.net.URLEncoder.encode("Không thể chỉnh sửa tài khoản quản trị viên", java.nio.charset.StandardCharsets.UTF_8));
                            return;
                        }
                        request.setAttribute("account", account);
                        request.getRequestDispatcher("/WEB-INF/admin/account/edit-account.jsp").forward(request, response);
                    } else {
                        response.sendRedirect(request.getContextPath() + "/admin/account");
                    }
                    break;
                case "detail":
                    int accountId = Integer.parseInt(request.getParameter("id"));
                    Account accountDetail = accDAO.getFullAccountById(accountId);
                    if (accountDetail != null) {
                        request.setAttribute("account", accountDetail);
                        request.getRequestDispatcher("/WEB-INF/admin/account/account-detail.jsp").forward(request, response);
                    } else {
                        response.sendRedirect(request.getContextPath() + "/admin/account");
                    }
                    break;
                default:
                    String keyword = request.getParameter("search");
                    List<Account> accounts;
                    if (keyword != null && !keyword.trim().isEmpty()) {
                        accounts = accDAO.searchAccounts(keyword);
                        request.setAttribute("keyword", keyword);
                    } else {
                        accounts = accDAO.getAllAccountsFull();
                    }

                    int totalAccounts = accDAO.countAccounts();
                    int customerCount = accDAO.countAccountsByRole(0);
                    int adminCount = accDAO.countAccountsByRole(1);

                    request.setAttribute("accounts", accounts);
                    request.setAttribute("totalAccounts", totalAccounts);
                    request.setAttribute("customerCount", customerCount);
                    request.setAttribute("adminCount", adminCount);

                    request.getRequestDispatcher("/WEB-INF/admin/account/manage-account.jsp").forward(request, response);
                    break;
            }
        } else {
            List<Account> accounts = accDAO.getAllAccountsFull();

            int totalAccounts = accDAO.countAccounts();
            int customerCount = accDAO.countAccountsByRole(0);
            int adminCount = accDAO.countAccountsByRole(1);

            request.setAttribute("accounts", accounts);
            request.setAttribute("totalAccounts", totalAccounts);
            request.setAttribute("customerCount", customerCount);
            request.setAttribute("adminCount", adminCount);

            request.getRequestDispatcher("/WEB-INF/admin/account/manage-account.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
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
                int role = Integer.parseInt(request.getParameter("role"));
                String status = request.getParameter("status").trim();

                role = 0;
                status = "Active";

                // Block creating admin accounts
                if (role == 1) {
                    request.setAttribute("errorMessage", "Không thể tạo tài khoản quản trị viên.");
                    request.getRequestDispatcher("/WEB-INF/admin/account/create-account.jsp").forward(request, response);
                    return;
                }

                if (accDAO.isUsernameExists(username)) {
                    request.setAttribute("errorMessage", "Tên đăng nhập đã tồn tại.");
                    request.getRequestDispatcher("/WEB-INF/admin/account/create-account.jsp").forward(request, response);
                    return;
                }
                if (accDAO.isEmailExists(email)) {
                    request.setAttribute("errorMessage", "Email đã tồn tại.");
                    request.getRequestDispatcher("/WEB-INF/admin/account/create-account.jsp").forward(request, response);
                    return;
                }

                if (accDAO.isPhoneExists(phone)) {
                    request.setAttribute("errorMessage", "Phone đã tồn tại.");
                    request.getRequestDispatcher("/WEB-INF/admin/account/create-account.jsp").forward(request, response);
                    return;
                }

                if (username == null || username.isEmpty()) {
                    request.setAttribute("errorMessage", "Vui lòng nhập tên đăng nhập.");
                    request.getRequestDispatcher("/WEB-INF/admin/account/create-account.jsp").forward(request, response);
                    return;
                }
                if (password == null || password.isEmpty()) {
                    request.setAttribute("errorMessage", "Vui lòng nhập mật khẩu.");
                    request.getRequestDispatcher("/WEB-INF/admin/account/create-account.jsp").forward(request, response);
                    return;
                }
                if (email == null || email.isEmpty()) {
                    request.setAttribute("errorMessage", "Vui lòng nhập email.");
                    request.getRequestDispatcher("/WEB-INF/admin/account/create-account.jsp").forward(request, response);
                    return;
                }
                if (fullName == null || fullName.isEmpty()) {
                    request.setAttribute("errorMessage", "Vui lòng nhập họ tên.");
                    request.getRequestDispatcher("/WEB-INF/admin/account/create-account.jsp").forward(request, response);
                    return;
                }
                if (phone == null || phone.isEmpty()) {
                    request.setAttribute("errorMessage", "Vui lòng nhập số điện thoại.");
                    request.getRequestDispatcher("/WEB-INF/admin/account/create-account.jsp").forward(request, response);
                    return;
                }
                if (address == null || address.isEmpty()) {
                    request.setAttribute("errorMessage", "Vui lòng nhập địa chỉ.");
                    request.getRequestDispatcher("/WEB-INF/admin/account/create-account.jsp").forward(request, response);
                    return;
                }
                if (gender == null || gender.isEmpty()) {
                    request.setAttribute("errorMessage", "Vui lòng chọn giới tính.");
                    request.getRequestDispatcher("/WEB-INF/admin/account/create-account.jsp").forward(request, response);
                    return;
                }
                if (status == null || status.isEmpty()) {
                    request.setAttribute("errorMessage", "Vui lòng chọn trạng thái.");
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

                boolean res = accDAO.insertFullAccount(account);

                if (res) {
                    // Nếu tạo tài khoản staff (role = 2), cần insert vào bảng Staff
                    if (role == 2) {
                        try {
                            // Lấy AccountID vừa tạo
                            int accountId = accDAO.getAccountIdByUsername(username);
                            if (accountId != -1) {
                                // Tạo Staff object và insert vào bảng Staff
                                Staff staff = new Staff();
                                staff.setAccountID(accountId);
                                staff.setFullName(fullName);
                                staff.setEmail(email);
                                staff.setPhone(phone);
                                staff.setGender(gender);
                                staff.setAddress(address);
                                staff.setStatus(status);

                                StaffDAO staffDAO = new StaffDAO();
                                boolean staffCreated = staffDAO.insertStaff(staff);
                                if (!staffCreated) {
                                    // Nếu tạo staff thất bại, xóa tài khoản vừa tạo
                                    accDAO.deleteAccount(accountId);
                                    request.setAttribute("errorMessage", "Tạo thông tin staff thất bại. Vui lòng thử lại.");
                                    request.setAttribute("account", account);
                                    request.getRequestDispatcher("/WEB-INF/admin/account/create-account.jsp").forward(request, response);
                                    return;
                                }
                            }
                        } catch (Exception e) {
                            // Nếu có lỗi, xóa tài khoản vừa tạo
                            int accountId = accDAO.getAccountIdByUsername(username);
                            if (accountId != -1) {
                                accDAO.deleteAccount(accountId);
                            }
                            request.setAttribute("errorMessage", "Lỗi khi tạo thông tin staff: " + e.getMessage());
                            request.setAttribute("account", account);
                            request.getRequestDispatcher("/WEB-INF/admin/account/create-account.jsp").forward(request, response);
                            return;
                        }
                    }

                    response.sendRedirect(request.getContextPath() + "/admin/account");
                } else {
                    request.setAttribute("errorMessage", "Tạo tài khoản thất bại. Vui lòng thử lại.");
                    request.setAttribute("account", account);
                    request.getRequestDispatcher("/WEB-INF/admin/account/create-account.jsp").forward(request, response);
                }
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("errorMessage", "Lỗi: " + e.getMessage());
                request.getRequestDispatcher("/WEB-INF/admin/account/create-account.jsp").forward(request, response);
            }
        } else if ("edit".equals(action)) {
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                String username = request.getParameter("username").trim();
                String password = request.getParameter("password").trim();
                String email = request.getParameter("email").trim();
                String fullName = request.getParameter("fullName").trim();
                String phone = request.getParameter("phone").trim();
                String address = request.getParameter("address").trim();
                String gender = request.getParameter("gender").trim();
                int role = Integer.parseInt(request.getParameter("role"));
                String status = request.getParameter("status").trim();

                // Keep role fixed to customer; do not override status so it uses existing data from form
                role = 0;

                Account existingAccount = accDAO.getFullAccountById(id);

                // Block editing admin accounts
                if (existingAccount != null && existingAccount.getRole() == 1) {
                    response.sendRedirect(request.getContextPath() + "/admin/account?type=error&message=" + java.net.URLEncoder.encode("Không thể chỉnh sửa tài khoản quản trị viên", java.nio.charset.StandardCharsets.UTF_8));
                    return;
                }

                // Block turning any account into admin
                if (role == 1) {
                    request.setAttribute("errorMessage", "Không thể cập nhật vai trò thành quản trị viên.");
                    request.setAttribute("account", existingAccount);
                    request.getRequestDispatcher("/WEB-INF/admin/account/edit-account.jsp").forward(request, response);
                    return;
                }

                if (username.isEmpty() || email.isEmpty() || fullName.isEmpty()
                        || phone.isEmpty() || address.isEmpty() || gender.isEmpty() || status.isEmpty()) {
                    request.setAttribute("errorMessage", "Vui lòng điền đầy đủ thông tin.");
                    request.setAttribute("account", existingAccount);
                    request.getRequestDispatcher("/WEB-INF/admin/account/edit-account.jsp").forward(request, response);
                    return;
                }

                Account checkUsername = accDAO.getAccountByUsername(username);
                if (checkUsername != null && checkUsername.getAccountID() != id) {
                    request.setAttribute("errorMessage", "Tên đăng nhập đã tồn tại.");
                    request.setAttribute("account", existingAccount);
                    request.getRequestDispatcher("/WEB-INF/admin/account/edit-account.jsp").forward(request, response);
                    return;
                }

                Account checkEmail = accDAO.getAccountByEmail(email);
                if (checkEmail != null && checkEmail.getAccountID() != id) {
                    request.setAttribute("errorMessage", "Email đã tồn tại.");
                    request.setAttribute("account", existingAccount);
                    request.getRequestDispatcher("/WEB-INF/admin/account/edit-account.jsp").forward(request, response);
                    return;
                }

                Account checkPhone = accDAO.getAccountByPhone(phone);
                if (checkPhone != null && checkPhone.getAccountID() != id) {
                    request.setAttribute("errorMessage", "Email đã tồn tại.");
                    request.setAttribute("account", existingAccount);
                    request.getRequestDispatcher("/WEB-INF/admin/account/edit-account.jsp").forward(request, response);
                    return;
                }

                Account account = new Account();
                account.setAccountID(id);
                account.setUsername(username);
                if (!password.isEmpty()) {
                    account.setPassword(MD5Util.hash(password));
                } else {
                    account.setPassword(existingAccount.getPassword());
                }
                account.setEmail(email);
                account.setFullName(fullName);
                account.setPhone(phone);
                account.setAddress(address);
                account.setGender(gender);
                account.setRole(role);
                account.setStatus(status);

                boolean res = accDAO.updateFullAccount(account);

                if (res) {
                    response.sendRedirect(request.getContextPath() + "/admin/account");
                } else {
                    request.setAttribute("errorMessage", "Cập nhật tài khoản thất bại. Vui lòng thử lại.");
                    request.setAttribute("account", account);
                    request.getRequestDispatcher("/WEB-INF/admin/account/edit-account.jsp").forward(request, response);
                }

            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("errorMessage", "Lỗi: " + e.getMessage());
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    Account acc = accDAO.getFullAccountById(id);
                    request.setAttribute("account", acc);
                } catch (Exception ex) {
                    // Bỏ qua
                }
                request.getRequestDispatcher("/WEB-INF/admin/account/edit-account.jsp").forward(request, response);
            }
        }
    }
}
