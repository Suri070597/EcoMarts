/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.VoucherDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import model.Account;
import model.Voucher;

/**
 *
 * @author nguye
 */
@WebServlet("/MyVoucherServlet")
public class MyVoucherServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        Account account = (Account) req.getSession().getAttribute("account");
        if (account == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        VoucherDAO dao = new VoucherDAO();
        List<Voucher> vouchers = dao.getVouchersByAccountId(account.getAccountID());

        req.setAttribute("vouchers", vouchers);
        req.getRequestDispatcher("/WEB-INF/customer/myVouchers.jsp").forward(req, resp);
    }
}
