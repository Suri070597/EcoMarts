package controller;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

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
 * @author nguye
 */
@WebServlet("/ChangePasswordServlet")
public class ChangePasswordServlet extends HttpServlet {

@Override
protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String newPassword = req.getParameter("newPassword");
    String confirmPassword = req.getParameter("confirmPassword");
    Account account = (Account) req.getSession().getAttribute("account");

    if (account == null) {
        resp.sendRedirect("login.jsp");
        return;
    }

    if (!newPassword.equals(confirmPassword)) {
        req.setAttribute("message", "Mật khẩu xác nhận không khớp!");
        req.getRequestDispatcher("/WEB-INF/customer/changePassword.jsp").forward(req, resp);
        return;
    }

    String hashedNewPassword = db.MD5Util.hash(newPassword);

    // So sánh mật khẩu mới và mật khẩu hiện tại
    if (hashedNewPassword.equals(account.getPassword())) {
        req.setAttribute("message", "Mật khẩu mới không được trùng với mật khẩu hiện tại!");
        req.getRequestDispatcher("/WEB-INF/customer/changePassword.jsp").forward(req, resp);
        return;
    }

    AccountDAO dao = new AccountDAO();
    boolean updated = dao.updatePassword(account.getAccountID(), hashedNewPassword);

    if (updated) {
        req.setAttribute("message", "Đổi mật khẩu thành công!");
        req.getRequestDispatcher("/WEB-INF/login/login.jsp").forward(req, resp);
    } else {
        req.setAttribute("message", "Lỗi khi cập nhật mật khẩu.");
        req.getRequestDispatcher("/WEB-INF/customer/changePassword.jsp").forward(req, resp);
    }
}

}
