package controller;

import dao.ProductDAO;
import dao.PromotionDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Promotion;
import model.Product;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

@WebServlet(name = "AdminPromotionServlet", urlPatterns = {"/admin/promotion"})
public class AdminPromotionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String view = request.getParameter("view");
        String action = request.getParameter("action");
        PromotionDAO promotionDAO = new PromotionDAO();
        ProductDAO productDAO = new ProductDAO();

        if ("delete".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            boolean result = promotionDAO.deletePromotion(id);
            if (!result) {
                request.setAttribute("errorMessage", "Không thể xóa promotion này do có dữ liệu liên quan!");
                request.setAttribute("promotions", promotionDAO.getAllPromotions());
                request.setAttribute("now", new Date());
                request.getRequestDispatcher("/WEB-INF/admin/promotion/manage-promotion.jsp").forward(request, response);
                return;
            }
            response.sendRedirect(request.getContextPath() + "/admin/promotion");
            return;
        }

        if ("status".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            boolean currentStatus = Boolean.parseBoolean(request.getParameter("status"));
            promotionDAO.updatePromotionStatus(id, !currentStatus);
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

                    List<Integer> disabledProductIDs = new ArrayList<>();
                    List<Product> allProducts = productDAO.getAll();
                    List<Integer> assignedProductIDs = promotionDAO.getAssignedProductIDsByPromotion(id);
                    Map<Integer, String> productPromotionMap = new HashMap<>();

                    for (Object[] row : promotionDAO.getProductPromotionInfoExcept(id)) {
                        productPromotionMap.put((int) row[0], (String) row[1]);
                        disabledProductIDs.add((int) row[0]);
                    }

                    request.setAttribute("promotion", promotion);
                    request.setAttribute("disabledProductIDs", disabledProductIDs);
                    request.setAttribute("allProducts", allProducts);
                    request.setAttribute("assignedIDs", assignedProductIDs);
                    request.setAttribute("productPromotionMap", productPromotionMap);

                    request.getRequestDispatcher("/WEB-INF/admin/promotion/assign-products.jsp").forward(request, response);
                    break;
                }
                default: {
                    String keyword = request.getParameter("search");
                    List<Promotion> promotions = (keyword != null && !keyword.trim().isEmpty())
                            ? promotionDAO.searchPromotions(keyword)
                            : promotionDAO.getAllPromotions();
                    request.setAttribute("promotions", promotions);
                    request.setAttribute("keyword", keyword);
                    request.setAttribute("totalPromotions", promotionDAO.countPromotions());
                    request.setAttribute("now", new Date());
                    request.getRequestDispatcher("/WEB-INF/admin/promotion/manage-promotion.jsp").forward(request, response);
                    break;
                }
            }
        } else {
            request.setAttribute("promotions", promotionDAO.getAllPromotions());
            request.setAttribute("now", new Date());
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
                if (promotionDAO.insertPromotion(promotion)) {
                    response.sendRedirect(request.getContextPath() + "/admin/promotion");
                } else {
                    request.setAttribute("errorMessage", "Tạo khuyến mãi thất bại. Vui lòng thử lại.");
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
                if (promotionDAO.updatePromotion(promotion)) {
                    response.sendRedirect(request.getContextPath() + "/admin/promotion");
                } else {
                    request.setAttribute("errorMessage", "Cập nhật khuyến mãi thất bại. Vui lòng thử lại.");
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

            promotionDAO.updateProductAssignments(promotionID, productIDs);

            response.sendRedirect(request.getContextPath() + "/admin/promotion");
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
