package controller;

import dao.VoucherDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Account;
import model.Voucher;

import java.io.IOException;
import java.time.Instant;

@WebServlet(name = "VoucherSaveServlet", urlPatterns = {"/voucher/save"})
public class VoucherSaveServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        Account account = (session != null) ? (Account) session.getAttribute("account") : null;

        // Require login
        if (account == null || account.getRole() != 0) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String code = req.getParameter("code");
        if (code == null || code.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/voucher-shortcuts?err=badcode");
            return;
        }
        code = code.trim();

        VoucherDAO dao = new VoucherDAO();
        Voucher voucher = dao.getVoucherByCode(code);
        if (voucher == null) {
            resp.sendRedirect(req.getContextPath() + "/voucher-shortcuts?err=notfound");
            return;
        }

        // Validate active and within time
        Instant now = Instant.now();
        boolean okActive = voucher.isActive();
        boolean okStart = voucher.getStartDate() == null || !voucher.getStartDate().toInstant().isAfter(now);
        boolean okEnd = voucher.getEndDate() == null || !voucher.getEndDate().toInstant().isBefore(now);
        if (!(okActive && okStart && okEnd)) {
            resp.sendRedirect(req.getContextPath() + "/voucher-shortcuts?err=expired");
            return;
        }

        // Already has it?
        if (dao.userHasVoucher(voucher.getVoucherID(), account.getAccountID())) {
            resp.sendRedirect(req.getContextPath() + "/voucher-shortcuts?msg=duplicate");
            return;
        }

        // Enforce claim limit if configured
        Integer claimLimit = voucher.getClaimLimit();
        if (claimLimit != null && claimLimit >= 0) {
            int assigned = dao.countAssigned(voucher.getVoucherID());
            if (assigned >= claimLimit) {
                resp.sendRedirect(req.getContextPath() + "/voucher-shortcuts?err=limit");
                return;
            }
        }

        VoucherDAO.AssignResult result = dao.assignVoucherToAccountId(voucher.getVoucherID(), account.getAccountID());
        switch (result) {
            case SUCCESS -> resp.sendRedirect(req.getContextPath() + "/voucher-shortcuts?msg=saved");
            case DUPLICATE -> resp.sendRedirect(req.getContextPath() + "/voucher-shortcuts?msg=duplicate");
            default -> resp.sendRedirect(req.getContextPath() + "/voucher-shortcuts?err=unknown");
        }
    }
}


