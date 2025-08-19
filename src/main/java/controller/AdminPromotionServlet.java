package controller;

import dao.CategoryDAO;
import dao.ProductDAO;
import dao.PromotionDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Category;
import model.Promotion;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@WebServlet(name = "AdminPromotionServlet", urlPatterns = {"/admin/promotion"})
public class AdminPromotionServlet extends HttpServlet {

    // =========================
    // JSP paths
    // =========================
    private static final String JSP_BASE   = "/WEB-INF/admin/promotion/";
    private static final String JSP_MANAGE = JSP_BASE + "manage-promotion.jsp";
    private static final String JSP_CREATE = JSP_BASE + "create-promotion.jsp";
    private static final String JSP_EDIT   = JSP_BASE + "edit-promotion.jsp";
    private static final String JSP_DETAIL = JSP_BASE + "promotion-detail.jsp";
    private static final String JSP_ASSIGN = JSP_BASE + "assign-products.jsp"; // giữ để dùng sau nếu cần

    // =========================
    // Query params
    // =========================
    private static final String P_VIEW    = "view";
    private static final String P_ACTION  = "action";
    private static final String P_ID      = "id";
    private static final String P_SEARCH  = "search";
    private static final String P_STATUS  = "status";
    private static final String P_TYPE    = "type";
    private static final String P_FROM    = "from";
    private static final String P_TO      = "to";

    // =========================
    // Views / Actions
    // =========================
    private static final String V_CREATE = "create";
    private static final String V_EDIT   = "edit";
    private static final String V_DETAIL = "detail";

    private static final String A_CREATE = "create";
    private static final String A_EDIT   = "edit";
    private static final String A_DELETE = "delete";
    private static final String A_STATUS = "status";

    // =========================
    // Form fields
    // =========================
    private static final String F_NAME        = "promotionName";
    private static final String F_DESC        = "description";
    private static final String F_DISCOUNT    = "discountPercent";
    private static final String F_START       = "startDate";     // yyyy-MM-dd
    private static final String F_END         = "endDate";       // yyyy-MM-dd
    private static final String F_ACTIVE      = "isActive";      // on|off
    private static final String F_PROMO_TYPE  = "promoType";     // 0=FLASH,1=SEASONAL
    private static final String F_APPLY_SCOPE = "applyScope";    // 0=ALL,1=CATEGORY
    private static final String F_CATEGORY_ID = "categoryID";    // single category id

    // =========================
    // GET
    // =========================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        prepare(req, resp);
        PromotionDAO dao = new PromotionDAO();

        String action = req.getParameter(P_ACTION);
        if (A_DELETE.equals(action)) { handleDelete(req, resp, dao); return; }
        if (A_STATUS.equals(action)) { handleToggleStatus(req, resp, dao); return; }

        String view = req.getParameter(P_VIEW);
        if (view == null) { handleList(req, resp, dao); return; }

        switch (view) {
            case V_CREATE -> {
                CategoryDAO cdao = new CategoryDAO();
                req.setAttribute("categories", cdao.getParentCategories());
                forward(req, resp, JSP_CREATE);
            }
            case V_EDIT   -> handleEditView(req, resp, dao);
            case V_DETAIL -> handleDetailView(req, resp, dao);
            default       -> handleList(req, resp, dao);
        }
    }

    // =========================
    // POST
    // =========================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        prepare(req, resp);
        PromotionDAO dao = new PromotionDAO();

        String action = req.getParameter(P_ACTION);
        if (A_CREATE.equals(action)) { handleCreatePost(req, resp, dao); return; }
        if (A_EDIT.equals(action))   { handleEditPost(req, resp, dao);   return; }

        resp.sendRedirect(base(req));
    }

    // =========================
    // Handlers
    // =========================
    private void handleList(HttpServletRequest req, HttpServletResponse resp, PromotionDAO dao)
        throws ServletException, IOException {
    String q = req.getParameter(P_SEARCH);
    Integer type = parseIntOrNull(req.getParameter(P_TYPE));
    Integer status = parseIntOrNull(req.getParameter(P_STATUS));
    Timestamp from = tsOrNull(req.getParameter(P_FROM));
    Timestamp to   = tsOrNull(req.getParameter(P_TO));

    List<Promotion> promotions = dao.list(q, type, status, from, to, 1, 10);
    System.out.println("[Manage] promotions size = " + (promotions != null ? promotions.size() : -1));

    req.setAttribute("promotions", promotions);
    req.setAttribute("keyword", q);
    req.setAttribute("type", type);
    req.setAttribute("status", status);
    req.setAttribute("from", from);
    req.setAttribute("to", to);
    req.setAttribute("totalPromotions", dao.countPromotions());
    req.setAttribute("now", new Date());

    // Cho JSP dùng (đỡ phải nhớ số)
    req.setAttribute("SCOPE_ALL", Promotion.SCOPE_ALL);
    req.setAttribute("SCOPE_CATEGORY", Promotion.SCOPE_CATEGORY);
    req.setAttribute("TYPE_FLASH", Promotion.TYPE_FLASH);
    req.setAttribute("TYPE_SEASONAL", Promotion.TYPE_SEASONAL);

    forward(req, resp, JSP_MANAGE);
}


    private void handleEditView(HttpServletRequest req, HttpServletResponse resp, PromotionDAO dao)
            throws IOException, ServletException {
        Integer id = parseIntOrNull(req.getParameter(P_ID));
        if (id == null) { resp.sendRedirect(base(req)); return; }

        Promotion p = dao.getPromotionById(id);
        if (p == null) { resp.sendRedirect(base(req)); return; }

        CategoryDAO cdao = new CategoryDAO();
        req.setAttribute("categories", cdao.getParentCategories());
        req.setAttribute("promotion", p);
        forward(req, resp, JSP_EDIT);
    }

    private void handleDetailView(HttpServletRequest req, HttpServletResponse resp, PromotionDAO dao)
            throws IOException, ServletException {
        Integer id = parseIntOrNull(req.getParameter(P_ID));
        if (id == null) { resp.sendRedirect(base(req)); return; }

        Promotion p = dao.getPromotionById(id);
        if (p == null) { resp.sendRedirect(base(req)); return; }

        req.setAttribute("promotion", p);
        req.setAttribute("appliedProductCount", dao.countAssignedProducts(id));
        req.setAttribute("now", new Date());
        forward(req, resp, JSP_DETAIL);
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp, PromotionDAO dao)
            throws IOException, ServletException {
        Integer id = parseIntOrNull(req.getParameter(P_ID));
        if (id == null) { resp.sendRedirect(base(req)); return; }

        boolean ok = dao.deletePromotion(id);
        if (!ok) {
            req.setAttribute("errorMessage", "Không thể xóa khuyến mãi này do đã phát sinh dữ liệu liên quan!");
            handleList(req, resp, dao);
            return;
        }
        resp.sendRedirect(base(req));
    }

    private void handleToggleStatus(HttpServletRequest req, HttpServletResponse resp, PromotionDAO dao)
            throws IOException {
        Integer id = parseIntOrNull(req.getParameter(P_ID));
        if (id == null) { resp.sendRedirect(base(req)); return; }
        Promotion p = dao.getPromotionById(id);
        if (p != null) dao.updatePromotionStatus(id, !p.isActive());
        resp.sendRedirect(base(req));
    }

    private void handleCreatePost(HttpServletRequest req, HttpServletResponse resp, PromotionDAO dao)
            throws IOException, ServletException {
        try {
            Promotion p = extractPromotion(req);
            if (!valid(p)) {
                req.setAttribute("errorMessage", "Dữ liệu không hợp lệ.");
                req.setAttribute("promotion", p);
                CategoryDAO cdao = new CategoryDAO();
                req.setAttribute("categories", cdao.getParentCategories());
                forward(req, resp, JSP_CREATE);
                return;
            }

            int newId = dao.insertPromotionReturningId(p);
            if (newId <= 0) {
                req.setAttribute("errorMessage", "Tạo khuyến mãi thất bại. Vui lòng thử lại.");
                req.setAttribute("promotion", p);
                CategoryDAO cdao = new CategoryDAO();
                req.setAttribute("categories", cdao.getParentCategories());
                forward(req, resp, JSP_CREATE);
                return;
            }

            // Mapping sản phẩm theo scope
            if (p.getApplyScope() == Promotion.SCOPE_CATEGORY) {
                Integer rootCatId = parseIntOrNull(req.getParameter(F_CATEGORY_ID));
                if (rootCatId != null) {
                    CategoryDAO cdao = new CategoryDAO();
                    List<Integer> allCatIds = cdao.getDescendantCategoryIds(List.of(rootCatId));

                    ProductDAO productDAO = new ProductDAO();
                    List<Integer> prodIds = productDAO.getProductIdsByCategoryIdsExpanded(allCatIds);

                    String[] pidStr = prodIds.stream().map(String::valueOf).toArray(String[]::new);
                    dao.updateProductAssignments(newId, pidStr);
                } else {
                    dao.clearProductAssignments(newId);
                }
            } else {
                dao.clearProductAssignments(newId); // ALL
            }

            resp.sendRedirect(base(req));
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Error: " + e.getMessage());
            CategoryDAO cdao = new CategoryDAO();
            req.setAttribute("categories", cdao.getParentCategories());
            forward(req, resp, JSP_CREATE);
        }
    }

    private void handleEditPost(HttpServletRequest req, HttpServletResponse resp, PromotionDAO dao)
            throws IOException, ServletException {
        Integer id = parseIntOrNull(req.getParameter(P_ID));
        if (id == null) { resp.sendRedirect(base(req)); return; }

        try {
            Promotion p = extractPromotion(req);
            p.setPromotionID(id);

            if (!valid(p)) {
                req.setAttribute("errorMessage", "Dữ liệu không hợp lệ.");
                req.setAttribute("promotion", p);
                CategoryDAO cdao = new CategoryDAO();
                req.setAttribute("categories", cdao.getParentCategories());
                forward(req, resp, JSP_EDIT);
                return;
            }

            boolean ok = dao.updatePromotion(p);
            if (!ok) {
                req.setAttribute("errorMessage", "Cập nhật khuyến mãi thất bại. Vui lòng thử lại.");
                req.setAttribute("promotion", p);
                CategoryDAO cdao = new CategoryDAO();
                req.setAttribute("categories", cdao.getParentCategories());
                forward(req, resp, JSP_EDIT);
                return;
            }

            // remap products theo scope
            if (p.getApplyScope() == Promotion.SCOPE_CATEGORY) {
                Integer rootCatId = parseIntOrNull(req.getParameter(F_CATEGORY_ID));
                if (rootCatId != null) {
                    CategoryDAO cdao = new CategoryDAO();
                    List<Integer> allCatIds = cdao.getDescendantCategoryIds(List.of(rootCatId));

                    ProductDAO productDAO = new ProductDAO();
                    List<Integer> prodIds = productDAO.getProductIdsByCategoryIdsExpanded(allCatIds);

                    String[] pidStr = prodIds.stream().map(String::valueOf).toArray(String[]::new);
                    dao.updateProductAssignments(id, pidStr);
                } else {
                    dao.clearProductAssignments(id);
                }
            } else {
                dao.clearProductAssignments(id);
            }

            resp.sendRedirect(base(req));
        } catch (Exception e) {
            req.setAttribute("errorMessage", "Error: " + e.getMessage());
            CategoryDAO cdao = new CategoryDAO();
            req.setAttribute("categories", cdao.getParentCategories());
            forward(req, resp, JSP_EDIT);
        }
    }

    // =========================
    // Utils
    // =========================
    private static void prepare(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
    }

    private static void forward(HttpServletRequest req, HttpServletResponse resp, String jsp)
            throws ServletException, IOException {
        req.getRequestDispatcher(jsp).forward(req, resp);
    }

    private static String base(HttpServletRequest req) {
        return req.getContextPath() + "/admin/promotion";
    }

    private static boolean notBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private static Integer parseIntOrNull(String raw) {
        if (!notBlank(raw)) return null;
        try { return Integer.parseInt(raw.trim()); } catch (NumberFormatException e) { return null; }
    }

    private static int parseIntOrDefault(String raw, int def) {
        if (!notBlank(raw)) return def;
        try { return Integer.parseInt(raw.trim()); } catch (NumberFormatException e) { return def; }
    }

    private static Timestamp tsOrNull(String raw) {
        if (!notBlank(raw)) return null;
        try { return Timestamp.valueOf(raw.trim()); } catch (IllegalArgumentException e) { return null; }
    }

    private static Promotion extractPromotion(HttpServletRequest req) {
        Promotion p = new Promotion();
        p.setPromotionName(req.getParameter(F_NAME));
        p.setDescription(req.getParameter(F_DESC));
        p.setDiscountPercent(Double.parseDouble(req.getParameter(F_DISCOUNT)));
        p.setStartDate(Timestamp.valueOf(req.getParameter(F_START) + " 00:00:00"));
        p.setEndDate(Timestamp.valueOf(req.getParameter(F_END) + " 23:59:59"));
        p.setActive("on".equals(req.getParameter(F_ACTIVE)));
p.setPromoType(typeFrom(req.getParameter(F_PROMO_TYPE)));
    p.setApplyScope(scopeFrom(req.getParameter(F_APPLY_SCOPE)));

        if (p.getApplyScope() == Promotion.SCOPE_CATEGORY) {
            Integer catId = parseIntOrNull(req.getParameter(F_CATEGORY_ID));
            if (catId != null) {
                Category c = new Category();
                c.setCategoryID(catId);
                p.setCategory(c);
            }
        }
        return p;
    }

    private static boolean valid(Promotion p) {
        if (!notBlank(p.getPromotionName())) return false;
        if (p.getDiscountPercent() < 0 || p.getDiscountPercent() > 100) return false;
        if (p.getStartDate() == null || p.getEndDate() == null) return false;
        if (!p.getStartDate().before(p.getEndDate())) return false;
        if (p.getApplyScope() == Promotion.SCOPE_CATEGORY &&
            (p.getCategory() == null || p.getCategory().getCategoryID() == 0)) return false;
        return true;
    }
    private static Integer intOrNull(String s) {
    if (s == null || s.isBlank()) return null;
    try { return Integer.valueOf(s.trim()); } catch (NumberFormatException e) { return null; }
}

private static int scopeFrom(String raw) {
    if (raw == null) return Promotion.SCOPE_ALL;
    raw = raw.trim().toLowerCase();
    return switch (raw) {
        case "1", "category", "cate" -> Promotion.SCOPE_CATEGORY;
        case "0", "all" -> Promotion.SCOPE_ALL;
        default -> {
            Integer n = intOrNull(raw);
            yield (n != null && n == 1) ? Promotion.SCOPE_CATEGORY : Promotion.SCOPE_ALL;
        }
    };
}

private static int typeFrom(String raw) {
    if (raw == null) return Promotion.TYPE_FLASH;
    raw = raw.trim().toLowerCase();
    return switch (raw) {
        case "1", "seasonal", "season" -> Promotion.TYPE_SEASONAL;
        case "0", "flash" -> Promotion.TYPE_FLASH;
        default -> {
            Integer n = intOrNull(raw);
            yield (n != null && n == 1) ? Promotion.TYPE_SEASONAL : Promotion.TYPE_FLASH;
        }
    };
}

}
