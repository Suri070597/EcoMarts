package controller;

import dao.ProductDAO;
import dao.PromotionDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Promotion;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import model.Product;

@WebServlet(name = "AdminPromotionServlet", urlPatterns = {"/admin/promotion"})
public class AdminPromotionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String view = request.getParameter("view");
        String action = request.getParameter("action");
        PromotionDAO promotionDAO = new PromotionDAO();
        ProductDAO productDAO = new ProductDAO();

        if (action != null && action.equals("delete")) {
            int id = Integer.parseInt(request.getParameter("id"));
            boolean result = promotionDAO.deletePromotion(id);
            if (!result) {
                request.setAttribute("errorMessage", "Không thể xóa promotion này do có dữ liệu liên quan!");
                List<Promotion> promotions = promotionDAO.getAllPromotions();
                request.setAttribute("promotions", promotions);
                request.setAttribute("now", new Date()); // ✅ Thêm tại đây
                request.getRequestDispatcher("/WEB-INF/admin/promotion/manage-promotion.jsp").forward(request, response);
                return;
            }
            response.sendRedirect(request.getContextPath() + "/admin/promotion");
            return;
        }

        if (action != null && action.equals("status")) {
            int id = Integer.parseInt(request.getParameter("id"));
            boolean currentStatus = Boolean.parseBoolean(request.getParameter("status"));
            boolean newStatus = !currentStatus;
            boolean result = promotionDAO.updatePromotionStatus(id, newStatus);
            response.sendRedirect(request.getContextPath() + "/admin/promotion");
            return;
        }

        if (view != null) {
            switch (view) {
                case "create":
                    request.getRequestDispatcher("/WEB-INF/admin/promotion/create-promotion.jsp").forward(request, response);
                    break;
                case "edit": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    Promotion promotion = promotionDAO.getPromotionById(id);
                    if (promotion != null) {
                        request.setAttribute("promotion", promotion);
                        request.getRequestDispatcher("/WEB-INF/admin/promotion/edit-promotion.jsp").forward(request, response);
                    } else {
                        response.sendRedirect(request.getContextPath() + "/admin/promotion");
                    }
                    break;
                }
                case "detail": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    Promotion promotion = promotionDAO.getPromotionById(id);
                    if (promotion != null) {
                        request.setAttribute("promotion", promotion);
                        request.getRequestDispatcher("/WEB-INF/admin/promotion/promotion-detail.jsp").forward(request, response);
                    } else {
                        response.sendRedirect(request.getContextPath() + "/admin/promotion");
                    }
                    break;
                }
                case "assign-products": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    Promotion promotion = promotionDAO.getPromotionById(id);

                    List<Product> allProducts = productDAO.getAll(); // dùng getAll() của bạn đã có
                    List<Integer> assignedProductIDs = promotionDAO.getAssignedProductIDsByPromotion(id);

                    request.setAttribute("promotion", promotion);
                    request.setAttribute("allProducts", allProducts);
                    request.setAttribute("assignedIDs", assignedProductIDs);

                    request.getRequestDispatcher("/WEB-INF/admin/promotion/assign-products.jsp").forward(request, response);
                    break;
                }
                default: {
                    String keyword = request.getParameter("search");
                    List<Promotion> promotions;
                    if (keyword != null && !keyword.trim().isEmpty()) {
                        promotions = promotionDAO.searchPromotions(keyword);
                        request.setAttribute("keyword", keyword);
                    } else {
                        promotions = promotionDAO.getAllPromotions();
                    }

                    int totalPromotions = promotionDAO.countPromotions();

                    request.setAttribute("promotions", promotions);
                    request.setAttribute("totalPromotions", totalPromotions);
                    request.setAttribute("now", new Date()); // ✅ Thêm tại đây

                    request.getRequestDispatcher("/WEB-INF/admin/promotion/manage-promotion.jsp").forward(request, response);
                    break;
                }
            }
        } else {
            List<Promotion> promotions = promotionDAO.getAllPromotions();
            request.setAttribute("promotions", promotions);
            request.setAttribute("now", new Date()); // ✅ Thêm tại đây
            request.getRequestDispatcher("/WEB-INF/admin/promotion/manage-promotion.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        PromotionDAO promotionDAO = new PromotionDAO();

        if ("create".equals(action)) {
            try {
                Promotion promotion = extractPromotionFromRequest(request);
                boolean res = promotionDAO.insertPromotion(promotion);

                if (res) {
                    response.sendRedirect(request.getContextPath() + "/admin/promotion");
                } else {
                    request.setAttribute("errorMessage", "Failed to create promotion. Please try again.");
                    request.setAttribute("promotion", promotion);
                    request.getRequestDispatcher("/WEB-INF/admin/promotion/create-promotion.jsp").forward(request, response);
                }
            } catch (Exception e) {
                request.setAttribute("errorMessage", "Error: " + e.getMessage());
                request.getRequestDispatcher("/WEB-INF/admin/promotion/create-promotion.jsp").forward(request, response);
            }
        } else if ("edit".equals(action)) {
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                Promotion promotion = extractPromotionFromRequest(request);
                promotion.setPromotionID(id);
                boolean res = promotionDAO.updatePromotion(promotion);

                if (res) {
                    response.sendRedirect(request.getContextPath() + "/admin/promotion");
                } else {
                    request.setAttribute("errorMessage", "Failed to update promotion. Please try again.");
                    request.setAttribute("promotion", promotion);
                    request.getRequestDispatcher("/WEB-INF/admin/promotion/edit-promotion.jsp").forward(request, response);
                }
            } catch (Exception e) {
                request.setAttribute("errorMessage", "Error: " + e.getMessage());
                request.getRequestDispatcher("/WEB-INF/admin/promotion/edit-promotion.jsp").forward(request, response);
            }
        } else if ("assign-products".equals(action)) {
            int promotionID = Integer.parseInt(request.getParameter("promotionID"));
            String[] productIDs = request.getParameterValues("productIDs");

            // Cập nhật bảng Product_Promotion
            promotionDAO.updateProductAssignments(promotionID, productIDs);

            response.sendRedirect(request.getContextPath() + "/admin/promotion");
            return;
        }

    }

    private Promotion extractPromotionFromRequest(HttpServletRequest request) {
        Promotion p = new Promotion();
        p.setPromotionName(request.getParameter("promotionName"));
        p.setDescription(request.getParameter("description"));
        p.setDiscountPercent(Double.parseDouble(request.getParameter("discountPercent")));
        p.setStartDate(Timestamp.valueOf(request.getParameter("startDate") + " 00:00:00"));
        p.setEndDate(Timestamp.valueOf(request.getParameter("endDate") + " 23:59:59"));
        p.setActive("on".equals(request.getParameter("isActive")));
        return p;
    }
}
