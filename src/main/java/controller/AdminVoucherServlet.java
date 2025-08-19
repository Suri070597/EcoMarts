package controller;

import dao.VoucherDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Account;
import model.Voucher;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminVoucherServlet", urlPatterns = {"/admin/voucher"})
public class AdminVoucherServlet extends HttpServlet {

    // =========================
    // ĐƯỜNG DẪN
    // =========================
    private static final String JSP_BASE              = "/WEB-INF/admin/voucher/";
    private static final String JSP_MANAGE            = JSP_BASE + "manage-voucher.jsp";
    private static final String JSP_CREATE            = JSP_BASE + "create-voucher.jsp";
    private static final String JSP_EDIT              = JSP_BASE + "edit-voucher.jsp";
    private static final String JSP_DETAIL            = JSP_BASE + "voucher-detail.jsp";
    private static final String JSP_OWNERS            = JSP_BASE + "voucher-owner.jsp";

    // =========================
    // PARAM & ACTION/VIEW KEYS
    // =========================
    private static final String PARAM_VIEW            = "view";
    private static final String PARAM_ACTION          = "action";
    private static final String PARAM_ID              = "id";
    private static final String PARAM_SEARCH          = "search";
    private static final String PARAM_STATUS          = "status";
    private static final String PARAM_ACCOUNT_ID      = "accountId";

    private static final String VIEW_CREATE           = "create";
    private static final String VIEW_EDIT             = "edit";
    private static final String VIEW_DETAIL           = "detail";
    private static final String VIEW_OWNERS           = "owners";

    private static final String ACTION_DELETE         = "delete";
    private static final String ACTION_STATUS         = "status";
    private static final String ACTION_CREATE         = "create";
    private static final String ACTION_EDIT           = "edit";
    private static final String ACTION_ASSIGN_BY_ID   = "assignOwnerById";

    // =========================
    // LIFECYCLE
    // =========================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        prepareEncoding(req, resp);
        VoucherDAO voucherDAO = new VoucherDAO();

        String action = str(req, PARAM_ACTION);
        if (ACTION_DELETE.equals(action)) {
            handleDelete(req, resp, voucherDAO);
            return;
        }
        if (ACTION_STATUS.equals(action)) {
            handleToggleStatus(req, resp, voucherDAO);
            return;
        }

        String view = str(req, PARAM_VIEW);
        if (view == null) {
            handleList(req, resp, voucherDAO);
            return;
        }

        switch (view) {
            case VIEW_CREATE -> forward(req, resp, JSP_CREATE);
            case VIEW_EDIT   -> handleEditView(req, resp, voucherDAO);
            case VIEW_DETAIL -> handleDetailView(req, resp, voucherDAO);
            case VIEW_OWNERS -> handleOwnersView(req, resp, voucherDAO);
            default          -> handleList(req, resp, voucherDAO);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        prepareEncoding(req, resp);
        VoucherDAO voucherDAO = new VoucherDAO();

        String action = str(req, PARAM_ACTION);
        if (ACTION_CREATE.equals(action)) {
            handleCreatePost(req, resp, voucherDAO);
            return;
        }
        if (ACTION_EDIT.equals(action)) {
            handleEditPost(req, resp, voucherDAO);
            return;
        }
        if (ACTION_ASSIGN_BY_ID.equals(action)) {
            handleAssignByIdPost(req, resp, voucherDAO);
            return;
        }

        // Nếu không khớp action nào, quay lại trang quản lý
        redirect(resp, req.getContextPath() + "/admin/voucher");
    }

    // =========================
    // HANDLERS – VIEW
    // =========================

    private void handleList(HttpServletRequest req, HttpServletResponse resp, VoucherDAO dao) throws ServletException, IOException {
        String keyword = str(req, PARAM_SEARCH);
        List<Voucher> vouchers = (notBlank(keyword))
                ? dao.searchVouchers(keyword.trim())
                : dao.getAllVouchers();

        int total = dao.countVouchers();
        req.setAttribute("vouchers", vouchers);
        req.setAttribute("totalVouchers", total);
        if (notBlank(keyword)) req.setAttribute("keyword", keyword);

        forward(req, resp, JSP_MANAGE);
    }

    private void handleEditView(HttpServletRequest req, HttpServletResponse resp, VoucherDAO dao) throws IOException, ServletException {
        Integer id = intOrNull(req, PARAM_ID);
        if (id == null) { redirect(resp, base(req)); return; }

        Voucher voucher = dao.getVoucherById(id);
        if (voucher == null) { redirect(resp, base(req)); return; }

        req.setAttribute("voucher", voucher);
        forward(req, resp, JSP_EDIT);
    }

    private void handleDetailView(HttpServletRequest req, HttpServletResponse resp, VoucherDAO dao) throws IOException, ServletException {
        Integer id = intOrNull(req, PARAM_ID);
        if (id == null) { redirect(resp, base(req)); return; }

        Voucher voucher = dao.getVoucherById(id);
        if (voucher == null) { redirect(resp, base(req)); return; }

        req.setAttribute("voucher", voucher);
        forward(req, resp, JSP_DETAIL);
    }

    private void handleOwnersView(HttpServletRequest req, HttpServletResponse resp, VoucherDAO dao) throws IOException, ServletException {
        Integer id = intOrNull(req, PARAM_ID);
        if (id == null) { redirect(resp, base(req)); return; }

        Voucher voucher = dao.getVoucherById(id);
        if (voucher == null) { redirect(resp, base(req)); return; }

        List<Account> owners = dao.getAcountByVoucherId(id);
        req.setAttribute("voucher", voucher);
        req.setAttribute("owners", owners);
        forward(req, resp, JSP_OWNERS);
    }

    // =========================
    // HANDLERS – ACTION (GET)
    // =========================

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp, VoucherDAO dao) throws IOException, ServletException {
        Integer id = intOrNull(req, PARAM_ID);
        if (id == null) { redirect(resp, base(req)); return; }

        boolean ok = dao.deleteVoucher(id);
        if (!ok) {
            req.setAttribute("errorMessage", "Không thể xóa voucher này do đã phát sinh dữ liệu liên quan!");
            handleList(req, resp, dao);
            return;
        }
        redirect(resp, base(req));
    }

    private void handleToggleStatus(HttpServletRequest req, HttpServletResponse resp, VoucherDAO dao) throws IOException {
        Integer id = intOrNull(req, PARAM_ID);
        if (id == null) { redirect(resp, base(req)); return; }

        boolean current = Boolean.parseBoolean(str(req, PARAM_STATUS));
        dao.updateVoucherStatus(id, !current);
        redirect(resp, base(req));
    }

    // =========================
    // HANDLERS – ACTION (POST)
    // =========================

    private void handleCreatePost(HttpServletRequest req, HttpServletResponse resp, VoucherDAO dao) throws IOException, ServletException {
        try {
            Voucher v = extractVoucherFromRequest(req);
            boolean ok = dao.insertVoucher(v);
            if (ok) { redirect(resp, base(req)); return; }

            req.setAttribute("errorMessage", "Tạo mã giảm giá thất bại. Vui lòng thử lại.");
            req.setAttribute("voucher", v);
            forward(req, resp, JSP_CREATE);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Error: " + e.getMessage());
            forward(req, resp, JSP_CREATE);
        }
    }

    private void handleEditPost(HttpServletRequest req, HttpServletResponse resp, VoucherDAO dao) throws IOException, ServletException {
        Integer id = intOrNull(req, PARAM_ID);
        if (id == null) { redirect(resp, base(req)); return; }

        try {
            Voucher v = extractVoucherFromRequest(req);
            v.setVoucherID(id);
            boolean ok = dao.updateVoucher(v);
            if (ok) { redirect(resp, base(req)); return; }

            req.setAttribute("errorMessage", "Cập nhật mã giảm giá thất bại. Vui lòng thử lại.");
            req.setAttribute("voucher", v);
            forward(req, resp, JSP_EDIT);
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Error: " + e.getMessage());
            forward(req, resp, JSP_EDIT);
        }
    }

    private void handleAssignByIdPost(HttpServletRequest req, HttpServletResponse resp, VoucherDAO dao) throws IOException {
        Integer voucherId = intOrNull(req, PARAM_ID);
        String accIdRaw = str(req, PARAM_ACCOUNT_ID);
        String redirectBase = ownersUrl(req, voucherId);

        if (voucherId == null) { redirect(resp, base(req)); return; }
        if (!notBlank(accIdRaw)) { redirect(resp, redirectBase + "&err=empty"); return; }

        Integer accountId = parseIntOrNull(accIdRaw.trim());
        if (accountId == null) { redirect(resp, redirectBase + "&err=badid"); return; }

        VoucherDAO.AssignResult res = dao.assignVoucherToAccountId(voucherId, accountId);
        switch (res) {
            case SUCCESS     -> redirect(resp, redirectBase + "&msg=assigned");
            case USER_NOT_FOUND -> redirect(resp, redirectBase + "&err=notfound");
            case DUPLICATE   -> redirect(resp, redirectBase + "&err=duplicate");
            default          -> redirect(resp, redirectBase + "&err=unknown");
        }
    }

    // =========================
    // UTILITIES
    // =========================

    private static void prepareEncoding(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
    }

    private static void forward(HttpServletRequest req, HttpServletResponse resp, String jsp) throws ServletException, IOException {
        req.getRequestDispatcher(jsp).forward(req, resp);
    }

    private static void redirect(HttpServletResponse resp, String url) throws IOException {
        resp.sendRedirect(url);
    }

    private static String base(HttpServletRequest req) {
        return req.getContextPath() + "/admin/voucher";
    }

    private static String ownersUrl(HttpServletRequest req, Integer voucherId) {
        if (voucherId == null) return base(req);
        return base(req) + "?view=owners&id=" + voucherId;
    }

    private static String str(HttpServletRequest req, String name) {
        return req.getParameter(name);
    }

    private static boolean notBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private static Integer intOrNull(HttpServletRequest req, String name) {
        return parseIntOrNull(req.getParameter(name));
    }

    private static Integer parseIntOrNull(String raw) {
        if (!notBlank(raw)) return null;
        try { return Integer.parseInt(raw.trim()); } catch (NumberFormatException e) { return null; }
    }

    // Map form -> entity (dùng thống nhất cho create & edit)
    private static Voucher extractVoucherFromRequest(HttpServletRequest request) {
        Voucher v = new Voucher();
        v.setVoucherCode(request.getParameter("voucherCode"));
        v.setDescription(request.getParameter("description"));
        v.setDiscountAmount(Double.parseDouble(request.getParameter("discountAmount")));
        v.setMinOrderValue(Double.parseDouble(request.getParameter("minOrderValue")));
        v.setMaxUsage(Integer.parseInt(request.getParameter("maxUsage")));
        v.setUsageCount(0); // create: 0; edit: nếu muốn giữ giá trị cũ thì load trước rồi set
        v.setStartDate(java.sql.Timestamp.valueOf(request.getParameter("startDate") + " 00:00:00"));
        v.setEndDate(java.sql.Timestamp.valueOf(request.getParameter("endDate") + " 23:59:59"));
        v.setActive("on".equals(request.getParameter("isActive")));

        String categoryIdParam = request.getParameter("categoryID");
        if (notBlank(categoryIdParam)) {
            v.setCategoryID(Integer.parseInt(categoryIdParam.trim()));
        } else {
            v.setCategoryID(null);
        }
        String claimLimitParam = request.getParameter("claimLimit");
        if (notBlank(claimLimitParam)) {
            v.setClaimLimit(Integer.parseInt(claimLimitParam.trim()));
        } else {
            v.setClaimLimit(null);
        }
        return v;
    }
}
