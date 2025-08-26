package controller;

import dao.CategoryDAO;
import dao.ProductDAO;
import dao.FeedBackDAO;
import dao.PromotionDAO;
import db.DBContext;
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

                // Tự xây danh sách liên quan theo ParentID, không phụ thuộc StockQuantity cũ
                java.util.List<Product> filteredRelated = new java.util.ArrayList<>();
                java.util.Map<Integer, String> relatedPriceDisplayMap = new java.util.HashMap<>();
                java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols();
                symbols.setGroupingSeparator('.');
                java.text.DecimalFormat formatter = new java.text.DecimalFormat("#,###", symbols);

                String sql = "SELECT p.ProductID FROM Product p JOIN Category c ON p.CategoryID = c.CategoryID "
                        + "WHERE c.ParentID = ? AND p.ProductID <> ?";
                DBContext db = new DBContext();
                java.sql.ResultSet rs = null;
                try {
                    rs = db.execSelectQuery(sql, new Object[] { parentId, id });
                    while (rs.next()) {
                        int pid = rs.getInt(1);
                        Product rp = dao.getProductById(pid);
                        if (rp == null)
                            continue;

                        // Lấy tồn kho qua DAO (getProductById đã tính theo KG nếu trái cây, BOX nếu
                        // khác)
                        double qty = rp.getStockQuantity();
                        if (qty <= 0)
                            continue;

                        // Yêu cầu có PriceUnit và ItemUnitName
                        Double priceUnit = rp.getPriceUnit();
                        if (priceUnit == null) {
                            Product full = dao.getProductById(pid);
                            if (full != null)
                                priceUnit = full.getPriceUnit();
                        }
                        String itemUnit = rp.getItemUnitName();
                        if (itemUnit == null || itemUnit.trim().isEmpty()) {
                            itemUnit = dao.getItemUnitName(pid);
                        }
                        if (priceUnit == null || itemUnit == null || itemUnit.trim().isEmpty())
                            continue;

                        // Cập nhật tồn kho để hiển thị, chuỗi giá theo Unit
                        rp.setStockQuantity(qty);
                        filteredRelated.add(rp);
                        relatedPriceDisplayMap.put(pid, formatter.format(priceUnit) + " đ / " + itemUnit);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (rs != null)
                            rs.getStatement().close();
                    } catch (Exception ignore) {
                    }
                    try {
                        db.closeConnection();
                    } catch (Exception ignore) {
                    }
                }

                request.setAttribute("relatedProducts", filteredRelated);
                request.setAttribute("relatedPriceDisplayMap", relatedPriceDisplayMap);
            }

            request.setAttribute("mo", mo);
            request.setAttribute("dataCate", dao.getCategory());
            request.setAttribute("dataSup", dao.getAllManufacturers());

            // Ưu tiên giá lẻ (UNIT) nếu tồn tại trong Inventory, nếu không thì dùng giá
            // thùng (giữ cho phần chính nếu JSP đang dùng)
            Double unitPrice = dao.getUnitPrice(mo.getProductID());
            request.setAttribute("unitPrice", unitPrice);

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

            // Flatten replies per root review for unlimited depth rendering in JSP
            java.util.Map<Integer, java.util.List<Review>> flatRepliesMap = new java.util.HashMap<>();
            try {
                for (Review root : reviewList) {
                    java.util.List<Review> flat = new java.util.ArrayList<>();
                    flattenReplies(root.getReplies(), flat, 0);
                    flatRepliesMap.put(root.getReviewID(), flat);
                }
            } catch (Exception ignore) {
            }
            request.setAttribute("flatRepliesMap", flatRepliesMap);

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

    private void flattenReplies(java.util.List<Review> replies, java.util.List<Review> out, int depth) {
        if (replies == null)
            return;
        for (Review child : replies) {
            child.setDepth(depth);
            out.add(child);
            flattenReplies(child.getReplies(), out, depth + 1);
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

    // (Không cần hàm lấy tồn kho riêng; dùng DAO.getProductById để đọc tồn kho phù
    // hợp)
}
