package controller.shortcuts;

import dao.CategoryDAO;
import dao.VoucherDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Category;
import model.Account;
import model.Voucher;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@WebServlet("/voucher-shortcuts")
public class VoucherShortcuts extends HttpServlet {

    private static final String JSP_PATH = "/WEB-INF/customer/partials/voucher-shortcuts.jsp";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1) Categories cho header/sidebar
        CategoryDAO categoryDAO = new CategoryDAO();
        List<Category> categories = categoryDAO.getAllCategoriesWithChildren();
        request.setAttribute("categories", categories);

        // 2) Vouchers đang active + hiệu lực thời gian
        VoucherDAO voucherDAO = new VoucherDAO();
        List<Voucher> all = voucherDAO.getAllVouchers();
        Instant now = Instant.now();

        List<Voucher> vouchers = new ArrayList<>();

        // Lấy account hiện tại (nếu có)
        model.Account currentAccount = (model.Account) request.getSession().getAttribute("account");
        Integer accountId = currentAccount != null ? currentAccount.getAccountID() : null;

        for (Voucher v : all) {
            if (!v.isActive()) continue;
            if (!within(v.getStartDate(), v.getEndDate(), now)) continue;
            // Ẩn voucher mà user đã có
            if (accountId != null && voucherDAO.userHasVoucher(v.getVoucherID(), accountId)) continue;
            // Ẩn voucher đã hết lượt claim (ClaimLimit) nếu cấu hình
            Integer claimLimit = v.getClaimLimit();
            if (claimLimit != null && claimLimit >= 0) {
                int assigned = voucherDAO.countAssigned(v.getVoucherID());
                if (assigned >= claimLimit) continue;
                request.setAttribute("claimRemaining:" + v.getVoucherID(), Math.max(claimLimit - assigned, 0));
            }
            vouchers.add(v);
        }

        // Sắp xếp gợi ý: sắp hết hạn -> còn ít lượt
        vouchers.sort(Comparator
                .comparing((Voucher v) -> nullSafe(v.getEndDate()))
                .thenComparingDouble(VoucherShortcuts::percentRemaining));

        request.setAttribute("vouchers", vouchers);

        // 3) Forward tới JSP (đảm bảo path đúng như bạn đã đặt trong dự án)
        request.getRequestDispatcher(JSP_PATH).forward(request, response);
    }

    private static boolean within(Timestamp start, Timestamp end, Instant now) {
        boolean okStart = (start == null) || !start.toInstant().isAfter(now);
        boolean okEnd   = (end == null)   || !end.toInstant().isBefore(now);
        return okStart && okEnd;
    }

    private static Timestamp nullSafe(Timestamp t) {
        return t == null ? new Timestamp(Long.MAX_VALUE) : t;
    }

    private static double percentRemaining(Voucher v) {
        int total = v.getMaxUsage();
        if (total <= 0) return 100.0; // unlimited
        int used = Math.max(v.getUsageCount(), 0);
        int remaining = Math.max(total - used, 0);
        return (remaining * 100.0) / total;
    }
}
