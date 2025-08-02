package controller;

import dao.CategoryDAO;
import dao.ProductDAO;
import dao.FeedBackDAO;
import dao.PromotionDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import model.Category;
import model.Product;
import model.Account;
import model.Promotion;
import model.Review;

@WebServlet(name = "ProductDetailServlet", urlPatterns = { "/ProductDetail" })
public class ProductDetailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        CategoryDAO categoryDAO = new CategoryDAO();
        List<Category> categories = categoryDAO.getAllCategoriesWithChildren();
        request.setAttribute("categories", categories);
        ProductDAO dao = new ProductDAO();
        String idRaw = request.getParameter("id");

        try {
            if (idRaw == null || idRaw.trim().isEmpty()) {
                request.setAttribute("errorMessage", "ID sản phẩm không hợp lệ");
                request.getRequestDispatcher("/WEB-INF/customer/productDetail.jsp").forward(request, response);
                return;
            }

            int id = Integer.parseInt(idRaw);
            Product mo = dao.getProductById(id);

            if (mo == null) {
                request.setAttribute("errorMessage", "Sản phẩm với ID " + id + " không tồn tại");
                request.getRequestDispatcher("/WEB-INF/customer/productDetail.jsp").forward(request, response);
                return;
            }

            // Chỉ thực hiện các thao tác này khi mo không null
            PromotionDAO promotionDAO = new PromotionDAO();
            Promotion appliedPromotion = promotionDAO.getValidPromotionForProduct(mo.getProductID());

            if (appliedPromotion != null) {
                request.setAttribute("appliedPromotion", appliedPromotion);
            }

            if (mo.getCategory() != null) {
                int parentId = mo.getCategory().getParentID();
                List<Product> relatedProducts = dao.getRelatedProductsByParentCategory(parentId, id);
                request.setAttribute("relatedProducts", relatedProducts);
            }

            request.setAttribute("mo", mo);
            request.setAttribute("dataCate", dao.getCategory());
            request.setAttribute("dataSup", dao.getAllSuppliers());

            // Lấy reviewList và orderId nếu có account đăng nhập
            FeedBackDAO fbDao = new FeedBackDAO();
            List<Review> allReviews = fbDao.getReviewsByProductId(mo.getProductID());
            int pageSize = 5;
            String pageStr = request.getParameter("page");
            int currentPage = 1;
            if (pageStr != null) {
                try {
                    currentPage = Integer.parseInt(pageStr);
                } catch (Exception e) {
                    currentPage = 1;
                }
            }
            int totalPages = (int) Math.ceil(allReviews.size() / (double) pageSize);
            if (currentPage < 1)
                currentPage = 1;
            if (currentPage > totalPages)
                currentPage = totalPages > 0 ? totalPages : 1;
            int fromIndex = (currentPage - 1) * pageSize;
            int toIndex = Math.min(fromIndex + pageSize, allReviews.size());
            List<Review> reviewList = allReviews.subList(fromIndex, toIndex);
            request.setAttribute("reviewList", reviewList);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);
            int reviewCount = fbDao.countReviewsByProductId(mo.getProductID());
            request.setAttribute("reviewCount", reviewCount);
            double avgRating = fbDao.getAverageRatingByProductId(mo.getProductID());
            int fullStars = (int) avgRating;
            boolean halfStar = (avgRating - fullStars) >= 0.25 && (avgRating - fullStars) < 0.75;
            int emptyStars = 5 - fullStars - (halfStar ? 1 : 0);
            request.setAttribute("avgRating", avgRating);
            request.setAttribute("fullStars", fullStars);
            request.setAttribute("halfStar", halfStar);
            request.setAttribute("emptyStars", emptyStars);

            Account acc = (Account) request.getSession().getAttribute("account");
            if (acc != null) {
                Integer orderId = fbDao.getOrderIdForReview(acc.getAccountID(), mo.getProductID());
                request.setAttribute("orderId", orderId);
            }

            // Lấy message từ session nếu có
            String message = (String) request.getSession().getAttribute("message");
            if (message != null) {
                request.setAttribute("message", message);
                request.getSession().removeAttribute("message");
            }

            request.getRequestDispatcher("/WEB-INF/customer/productDetail.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "ID sản phẩm phải là một số hợp lệ: " + idRaw);
            request.getRequestDispatcher("/WEB-INF/customer/productDetail.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Product detail page";
    }
}
