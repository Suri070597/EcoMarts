package controller;

import dao.CategoryDAO;
import dao.ProductDAO;
import dao.ViewProductDAO;
import dao.FeedBackDAO;
import dao.OrderDAO;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.util.ArrayList;
import java.util.Map;
import model.Category;
import model.Product;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {

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
        OrderDAO order = new OrderDAO();
        request.setAttribute("products", list);

        // Lấy 7 danh sách sản phẩm theo từng ParentID
        ViewProductDAO viewDao = new ViewProductDAO();
        List<Product> list1 = viewDao.getFeaturedProductsByPage(1, 0, 6); // Nước giải khát
        List<Product> list2 = viewDao.getFeaturedProductsByPage(2, 0, 6); // Sữa các loại
        List<Product> list3 = viewDao.getFeaturedProductsByPage(3, 0, 6); // Trái cây
        List<Product> list4 = viewDao.getFeaturedProductsByPage(4, 0, 6); // Bánh kẹo
        List<Product> list5 = viewDao.getFeaturedProductsByPage(5, 0, 6); // Mẹ & Bé
        List<Product> list6 = viewDao.getFeaturedProductsByPage(6, 0, 6); // Mỹ phẩm
        List<Map<String, Object>> topRows = order.getTopSellingProducts(10);
        List<Product> topSellingProducts = new ArrayList<>();
        for (Map<String, Object> row : topRows) {
            int pid = ((Number) row.get("productId")).intValue();
            Product p = dao.getProductById(pid); // dùng ProductDAO đang có sẵn phía trên
            if (p != null) {
                topSellingProducts.add(p);
            }
        }
        request.setAttribute("featuredProducts7", topSellingProducts);

        // Tạo map hiển thị giá theo quy tắc
        java.util.Map<Integer, String> priceDisplayMap = new java.util.HashMap<>();
        java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        java.text.DecimalFormat formatter = new java.text.DecimalFormat("#,###", symbols);

        // Helper cho format
        java.util.function.BiConsumer<Integer, Double> putThung = (pid, priceVal) -> {
            if (priceVal != null)
                priceDisplayMap.put(pid, formatter.format(priceVal) + " đ / thùng");
        };
        java.util.function.Consumer<Product> putTraiCay = (p) -> {
            Double priceUnit = p.getPriceUnit();
            if (priceUnit != null) {
                priceDisplayMap.put(p.getProductID(), formatter.format(priceUnit) + " đ / " + p.getItemUnitName());
            } else {
                priceDisplayMap.put(p.getProductID(), "Chưa có giá");
            }
        };
        java.util.function.BiConsumer<Integer, String> putUnit = (pid, unitLabel) -> {
            Double up = dao.getUnitOnlyPrice(pid);
            if (up != null && unitLabel != null && !unitLabel.trim().isEmpty()) {
                priceDisplayMap.put(pid, formatter.format(up) + " đ / " + unitLabel);
            }
        };

        // Lọc danh sách theo yêu cầu và build price map
        // 1) Nước giải khát: hiển thị giá theo đơn vị
        java.util.List<Product> filtered1 = new java.util.ArrayList<>();
        if (list1 != null) {
            for (Product p : list1) {
                Double priceUnit = p.getPriceUnit();
                if (priceUnit != null) {
                    String display = formatter.format(priceUnit) + " đ / " + p.getItemUnitName();
                    priceDisplayMap.put(p.getProductID(), display);
                    filtered1.add(p);
                }
            }
        }
        // 2) Sữa: hiển thị giá theo đơn vị
        java.util.List<Product> filtered2 = new java.util.ArrayList<>();
        if (list2 != null) {
            for (Product p : list2) {
                Double priceUnit = p.getPriceUnit();
                if (priceUnit != null) {
                    String display = formatter.format(priceUnit) + " đ / " + p.getItemUnitName();
                    priceDisplayMap.put(p.getProductID(), display);
                    filtered2.add(p);
                }
            }
        }

        // 3) Trái cây: hiển thị giá theo đơn vị
        java.util.List<Product> filtered3 = new java.util.ArrayList<>();
        if (list3 != null) {
            for (Product p : list3) {
                Double priceUnit = p.getPriceUnit();
                if (priceUnit != null) {
                    String display = formatter.format(priceUnit) + " đ / " + p.getItemUnitName();
                    priceDisplayMap.put(p.getProductID(), display);
                    filtered3.add(p);
                }
            }
        }

        // 4/5/6) Khác: hiển thị giá theo đơn vị
        java.util.List<Product> filtered4 = new java.util.ArrayList<>();
        if (list4 != null) {
            for (Product p : list4) {
                Double priceUnit = p.getPriceUnit();
                if (priceUnit != null) {
                    String display = formatter.format(priceUnit) + " đ / " + p.getItemUnitName();
                    priceDisplayMap.put(p.getProductID(), display);
                    filtered4.add(p);
                }
            }
        }
        java.util.List<Product> filtered5 = new java.util.ArrayList<>();
        if (list5 != null) {
            for (Product p : list5) {
                Double priceUnit = p.getPriceUnit();
                if (priceUnit != null) {
                    String display = formatter.format(priceUnit) + " đ / " + p.getItemUnitName();
                    priceDisplayMap.put(p.getProductID(), display);
                    filtered5.add(p);
                }
            }
        }
        java.util.List<Product> filtered6 = new java.util.ArrayList<>();
        if (list6 != null) {
            for (Product p : list6) {
                Double priceUnit = p.getPriceUnit();
                if (priceUnit != null) {
                    String display = formatter.format(priceUnit) + " đ / " + p.getItemUnitName();
                    priceDisplayMap.put(p.getProductID(), display);
                    filtered6.add(p);
                }
            }
        }

        // 7) Sản phẩm nổi bật: áp dụng theo loại thật của sản phẩm
        java.util.List<Product> filtered7 = new java.util.ArrayList<>();
        if (topSellingProducts != null) {
            for (Product p : topSellingProducts) {
                int parentId = 0;
                try {
                    if (p.getCategory() != null)
                        parentId = p.getCategory().getParentID();
                } catch (Exception ignore) {
                }

                if (parentId == 1 || parentId == 2) {
                    Double priceUnit = p.getPriceUnit();
                    if (priceUnit != null) {
                        String display = formatter.format(priceUnit) + " đ / " + p.getItemUnitName();
                        priceDisplayMap.put(p.getProductID(), display);
                        filtered7.add(p);
                    }
                } else if (parentId == 3) {
                    putTraiCay.accept(p);
                    filtered7.add(p);
                } else {
                    Double up = dao.getUnitOnlyPrice(p.getProductID());
                    if (up != null) {
                        String unitLabel = dao.getItemUnitName(p.getProductID());
                        putUnit.accept(p.getProductID(), unitLabel);
                        filtered7.add(p);
                    }
                }
            }
        }

        // Đẩy danh sách đã lọc
        request.setAttribute("featuredProducts1", filtered1);
        request.setAttribute("featuredProducts2", filtered2);
        request.setAttribute("featuredProducts3", filtered3);
        request.setAttribute("featuredProducts4", filtered4);
        request.setAttribute("featuredProducts5", filtered5);
        request.setAttribute("featuredProducts6", filtered6);
        request.setAttribute("featuredProducts7", filtered7);
        request.setAttribute("priceDisplayMap", priceDisplayMap);

        // Lấy rating trung bình và số lượt đánh giá cho từng sản phẩm
        // Đồng thời lấy unit quantity từ bảng Inventory để xác định hết hàng
        try {
            FeedBackDAO fbDao = new FeedBackDAO();
            java.util.Map<Integer, Double> avgRatingMap = new java.util.HashMap<>();
            java.util.Map<Integer, Integer> reviewCountMap = new java.util.HashMap<>();
            java.util.Map<Integer, Double> unitPriceMap = new java.util.HashMap<>();
            java.util.Map<Integer, Double> unitQuantityMap = new java.util.HashMap<>();

            List<List<Product>> allProductLists = java.util.Arrays.asList(
                    (List<Product>) request.getAttribute("featuredProducts1"),
                    (List<Product>) request.getAttribute("featuredProducts2"),
                    (List<Product>) request.getAttribute("featuredProducts3"),
                    (List<Product>) request.getAttribute("featuredProducts4"),
                    (List<Product>) request.getAttribute("featuredProducts5"),
                    (List<Product>) request.getAttribute("featuredProducts6"),
                    (List<Product>) request.getAttribute("featuredProducts7"));

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

                        // Lấy unit quantity từ bảng Inventory với packType = 'UNIT'
                        if (!unitQuantityMap.containsKey(pid)) {
                            double unitQty = dao.getUnitQuantity(pid);
                            unitQuantityMap.put(pid, unitQty);
                        }

                        // Map cũ để tương thích (không còn dùng ở JSP sau khi cập nhật)
                        if (!unitPriceMap.containsKey(pid)) {
                            unitPriceMap.put(pid, null);
                        }
                    }
                }
            }
            request.setAttribute("avgRatingMap", avgRatingMap);
            request.setAttribute("reviewCountMap", reviewCountMap);
            request.setAttribute("unitPriceMap", unitPriceMap);
            request.setAttribute("unitQuantityMap", unitQuantityMap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        request.getRequestDispatcher("/WEB-INF/customer/homePage.jsp").forward(request, response);
    }
}
