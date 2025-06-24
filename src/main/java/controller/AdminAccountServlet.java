package controller;

import java.io.IOException;
import java.util.List;

import dao.AccountDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Account;

@WebServlet(name = "AdminAccountServlet", urlPatterns = {"/admin/account"})
public class AdminAccountServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String view = request.getParameter("view");
        String action = request.getParameter("action");
        AccountDAO accDAO = new AccountDAO();

        if (action != null && action.equals("delete")) {
            int id = Integer.parseInt(request.getParameter("id"));
            boolean result = accDAO.deleteAccount(id);
            if (!result) {
                request.setAttribute("errorMessage", "Không thể xóa tài khoản này do đã phát sinh dữ liệu liên quan!");
                List<Account> accounts = accDAO.getAllAccountsFull();
                request.setAttribute("accounts", accounts);
                request.getRequestDispatcher("/WEB-INF/admin/account/manage-account.jsp").forward(request, response);
                return;
            }
            response.sendRedirect(request.getContextPath() + "/admin/account");
            return;
        }

        if (action != null && action.equals("status")) {
            int id = Integer.parseInt(request.getParameter("id"));
            String status = request.getParameter("status");
            String newStatus = status.equals("Active") ? "Inactive" : "Active";
            boolean result = accDAO.updateAccountStatus(id, newStatus);
            response.sendRedirect(request.getContextPath() + "/admin/account");
            return;
        }

        if (view != null) {
            switch (view) {
                case "create":
                    request.getRequestDispatcher("/WEB-INF/admin/account/create-account.jsp").forward(request,
                            response);
                    break;
                case "edit":
                    int id = Integer.parseInt(request.getParameter("id"));
                    Account account = accDAO.getFullAccountById(id);
                    if (account != null) {
                        request.setAttribute("account", account);
                        request.getRequestDispatcher("/WEB-INF/admin/account/edit-account.jsp").forward(request,
                                response);
                    } else {
                        response.sendRedirect(request.getContextPath() + "/admin/account");
                    }
                    break;
                case "detail":
                    int accountId = Integer.parseInt(request.getParameter("id"));
                    Account accountDetail = accDAO.getFullAccountById(accountId);
                    if (accountDetail != null) {
                        request.setAttribute("account", accountDetail);
                        request.getRequestDispatcher("/WEB-INF/admin/account/account-detail.jsp").forward(request,
                                response);
                    } else {
                        response.sendRedirect(request.getContextPath() + "/admin/account");
                    }
                    break;
                default:
                    // Show the account list
                    String keyword = request.getParameter("search");
                    List<Account> accounts;
                    if (keyword != null && !keyword.trim().isEmpty()) {
                        accounts = accDAO.searchAccounts(keyword);
                        request.setAttribute("keyword", keyword);
                    } else {
                        accounts = accDAO.getAllAccountsFull();
                    }

                    // Get statistics for dashboard
                    int totalAccounts = accDAO.countAccounts();
                    int customerCount = accDAO.countAccountsByRole(0);
                    int adminCount = accDAO.countAccountsByRole(1);

                    request.setAttribute("accounts", accounts);
                    request.setAttribute("totalAccounts", totalAccounts);
                    request.setAttribute("customerCount", customerCount);
                    request.setAttribute("adminCount", adminCount);

                    request.getRequestDispatcher("/WEB-INF/admin/account/manage-account.jsp").forward(request,
                            response);
                    break;
            }
        } else {
            // If no view parameter, show the account list
            List<Account> accounts = accDAO.getAllAccountsFull();

            // Get statistics for dashboard
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
                String username = request.getParameter("username");
                String password = request.getParameter("password");
                String email = request.getParameter("email");
                String fullName = request.getParameter("fullName");
                String phone = request.getParameter("phone");
                String address = request.getParameter("address");
                String gender = request.getParameter("gender");
                int role = Integer.parseInt(request.getParameter("role"));
                String status = request.getParameter("status");

                Account account = new Account();
                account.setUsername(username);
                account.setPassword(password);
                account.setEmail(email);
                account.setFullName(fullName);
                account.setPhone(phone);
                account.setAddress(address);
                account.setGender(gender);
                account.setRole(role);
                account.setStatus(status);

                boolean res = accDAO.insertFullAccount(account);

                if (res) {
                    response.sendRedirect(request.getContextPath() + "/admin/account");
                } else {
                    request.setAttribute("errorMessage", "Failed to create account. Please try again.");
                    request.setAttribute("account", account); // Return the data back to the form
                    request.getRequestDispatcher("/WEB-INF/admin/account/create-account.jsp").forward(request,
                            response);
                }
            } catch (Exception e) {
                request.setAttribute("errorMessage", "Error: " + e.getMessage());
                request.getRequestDispatcher("/WEB-INF/admin/account/create-account.jsp").forward(request, response);
            }
        } else if ("edit".equals(action)) {
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                String username = request.getParameter("username");
                String password = request.getParameter("password");
                String email = request.getParameter("email");
                String fullName = request.getParameter("fullName");
                String phone = request.getParameter("phone");
                String address = request.getParameter("address");
                String gender = request.getParameter("gender");
                int role = Integer.parseInt(request.getParameter("role"));
                String status = request.getParameter("status");

                Account account = new Account();
                account.setAccountID(id);
                account.setUsername(username);
                account.setPassword(password);
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
                    request.setAttribute("errorMessage", "Failed to update account. Please try again.");
                    request.setAttribute("account", account); // Return the data back to the form
                    request.getRequestDispatcher("/WEB-INF/admin/account/edit-account.jsp").forward(request, response);
                }
            } catch (Exception e) {
                request.setAttribute("errorMessage", "Error: " + e.getMessage());
                request.getRequestDispatcher("/WEB-INF/admin/account/edit-account.jsp").forward(request, response);
            }
        }
    }
}
