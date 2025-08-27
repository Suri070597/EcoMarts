package controller.shortcuts;

import dao.CategoryDAO;
import dao.ProductDAO;
import dao.ViewProductDAO;
import dao.FeedBackDAO;
import dao.PromotionDAO;
import static dao.PromotionDAO.TYPE_FLASHSALE;
import static dao.PromotionDAO.TYPE_SEASONAL;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Category;
import model.Product;
import model.Promotion;

@WebServlet("/flashsale-shortcuts")
public class flashsale extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get categories from database
        CategoryDAO categoryDAO = new CategoryDAO();
        List<Category> categories = categoryDAO.getAllCategoriesWithChildren();
        request.setAttribute("categories", categories);

        // Get products
        ProductDAO dao = new ProductDAO();
        List<Product> list = dao.getAll();
        request.setAttribute("products", list);
        
        Promotion pro = new Promotion();
        PromotionDAO promoDao = new PromotionDAO();
        int flashInserted   = promoDao.rebuildMappingsForActiveType(TYPE_FLASHSALE);
        System.out.println("[Rebuild] FlashSale inserted rows = " + flashInserted);
        List<Product> flash = promoDao.listFlashSaleFromMapping();
        // Hydrate đầy đủ thông tin sản phẩm (stockQuantity, imageURL, unit names...)
        List<Product> hydrated = new java.util.ArrayList<>();
        if (flash != null) {
            for (Product sp : flash) {
                Product full = dao.getProductById(sp.getProductID());
                if (full != null) hydrated.add(full);
            }
        }
        request.setAttribute("flashSaleProducts", hydrated);
        
        // Xây map hiển thị giá tương tự homepage (ưu tiên giá đơn vị + tên đơn vị)
        try {
            java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols();
            symbols.setGroupingSeparator('.');
            java.text.DecimalFormat formatter = new java.text.DecimalFormat("#,###", symbols);
            java.util.Map<Integer, String> priceDisplayMap = new java.util.HashMap<>();

            if (hydrated != null) {
                for (Product p : hydrated) {
                    Double priceUnit = p.getPriceUnit();
                    String itemUnitName = p.getItemUnitName();
                    if (priceUnit != null && itemUnitName != null && !itemUnitName.trim().isEmpty()) {
                        priceDisplayMap.put(p.getProductID(), formatter.format(priceUnit) + " đ / " + itemUnitName);
                    } else if (p.getPrice() != null) {
                        priceDisplayMap.put(p.getProductID(), formatter.format(p.getPrice()) + " đ / thùng");
                    } else {
                        priceDisplayMap.put(p.getProductID(), "Chưa có giá");
                    }
                }
            }

            request.setAttribute("priceDisplayMap", priceDisplayMap);
        } catch (Exception ignore) {
        }

        // Lấy rating trung bình và số lượt đánh giá cho từng sản phẩm
        try {
            FeedBackDAO fbDao = new FeedBackDAO();
            java.util.Map<Integer, Double> avgRatingMap = new java.util.HashMap<>();
            java.util.Map<Integer, Integer> reviewCountMap = new java.util.HashMap<>();
            java.util.Map<Integer, Double> unitPriceMap = new java.util.HashMap<>();

            List<List<Product>> allProductLists = java.util.Arrays.asList(
                    (List<Product>) request.getAttribute("flashSaleProducts"));

            for (List<Product> plist : allProductLists) {
                if (plist != null) {
                    for (Product p : plist) {
                        int pid = p.getProductID();
                        if (!avgRatingMap.containsKey(pid)) {
                            double avg = fbDao.getAverageRatingByProductId(pid);
                            int count = fbDao.countReviewsByProductId(pid);
                            avgRatingMap.put(pid, avg);
                            reviewCountMap.put(pid, count);
                        }

                        // Lấy giá unit (lon) từ Inventory
                        if (!unitPriceMap.containsKey(pid)) {
                            Double unitPrice = dao.getUnitPrice(pid);
                            unitPriceMap.put(pid, unitPrice);
                        }
                    }
                }
            }
            request.setAttribute("avgRatingMap", avgRatingMap);
            request.setAttribute("reviewCountMap", reviewCountMap);
            request.setAttribute("unitPriceMap", unitPriceMap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        request.getRequestDispatcher("/WEB-INF/customer/partials/flashsale-shortcuts.jsp").forward(request, response);
    }
}
