package controller;

import dao.ViewProductDAO;
import dao.ProductDAO;
import dao.OrderDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Product;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "LoadMoreFeaturedServlet", urlPatterns = { "/loadMoreFeatured" })
public class LoadMoreFeaturedServlet extends HttpServlet {

        private ViewProductDAO dao = new ViewProductDAO();
        private ProductDAO productDao = new ProductDAO();
        private OrderDAO orderDao = new OrderDAO();

        @Override
        protected void doGet(HttpServletRequest request, HttpServletResponse response)
                        throws ServletException, IOException {

                response.setContentType("text/html;charset=UTF-8");
                PrintWriter out = response.getWriter();

                try {
                        // Lấy parameters
                        String offsetStr = request.getParameter("offset");
                        String limitStr = request.getParameter("limit");
                        String parentIdStr = request.getParameter("parentId");
                        String type = request.getParameter("type");

                        if (offsetStr == null || limitStr == null || parentIdStr == null) {
                                System.err.println("❌ Thiếu parameters");
                                out.print("error: missing parameters");
                                return;
                        }

                        int offset = Integer.parseInt(offsetStr);
                        int limit = Integer.parseInt(limitStr);
                        int parentId = Integer.parseInt(parentIdStr);

                        List<Product> products;
                        if ("featured".equalsIgnoreCase(type)) {
                                // Lấy top selling rồi phân trang theo offset/limit
                                java.util.List<java.util.Map<String, Object>> rows = orderDao
                                                .getTopSellingProducts(offset + limit);
                                java.util.List<Product> feats = new java.util.ArrayList<>();
                                for (int i = offset; i < rows.size() && i < offset + limit; i++) {
                                        java.util.Map<String, Object> row = rows.get(i);
                                        int pid = ((Number) row.get("productId")).intValue();
                                        Product p = productDao.getProductById(pid);
                                        if (p != null)
                                                feats.add(p);
                                }
                                products = feats;
                        } else {
                                // Lấy sản phẩm theo parent category (cũ)
                                products = dao.getFeaturedProductsByPage(parentId, offset, limit);
                        }

                        // Nếu không có sản phẩm, trả về chuỗi rỗng
                        if (products == null || products.isEmpty()) {
                                out.print("");
                                return;
                        }

                        // Tạo HTML cho các sản phẩm theo quy tắc hiển thị giá
                        StringBuilder html = new StringBuilder();
                        for (Product p : products) {
                                // Định dạng tiền tệ: 240.000 đ
                                java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols();
                                symbols.setGroupingSeparator('.');
                                java.text.DecimalFormat formatter = new java.text.DecimalFormat("#,###", symbols);

                                // Xác định chuỗi giá theo quy tắc
                                String priceDisplay = null;
                                Integer effectiveParentId = parentId;
                                if ("featured".equalsIgnoreCase(type)) {
                                        try {
                                                if (p.getCategory() != null) {
                                                        effectiveParentId = p.getCategory().getParentID();
                                                } else {
                                                        effectiveParentId = 0;
                                                }
                                        } catch (Exception ignore) {
                                        }
                                }

                                if (effectiveParentId == 1 || effectiveParentId == 2) {
                                        // Nước giải khát & Sữa: giá theo thùng (BOX)
                                        Double boxPrice = productDao.getBoxPrice(p.getProductID());
                                        if (boxPrice == null)
                                                boxPrice = p.getPrice();
                                        StringBuilder sb = new StringBuilder();
                                        sb.append(formatter.format(boxPrice)).append(" đ / thùng");
                                        try {
                                                Integer upb = p.getUnitPerBox();
                                                String iun = p.getItemUnitName();
                                                if (upb == null || upb <= 0 || iun == null || iun.trim().isEmpty()) {
                                                        Product full = productDao.getProductById(p.getProductID());
                                                        if (full != null) {
                                                                upb = full.getUnitPerBox();
                                                                iun = full.getItemUnitName();
                                                        }
                                                }
                                                if (upb != null && upb > 0 && iun != null && !iun.trim().isEmpty()) {
                                                        sb.append(" (").append(upb).append(" ").append(iun).append(")");
                                                }
                                        } catch (Exception ignore) {
                                        }
                                        priceDisplay = sb.toString();
                                } else if (effectiveParentId == 3) {
                                        // Trái cây: giữ nguyên
                                        priceDisplay = formatter.format(p.getPrice()) + " đ / " + p.getUnit();
                                } else {
                                        // Loại khác: phải có UNIT, nếu không có thì ẩn (skip)
                                        Double unitPrice = productDao.getUnitOnlyPrice(p.getProductID());
                                        if (unitPrice == null) {
                                                continue; // ẩn card
                                        }
                                        String unitLabel = productDao.getItemUnitName(p.getProductID());
                                        if (unitLabel == null || unitLabel.trim().isEmpty()) {
                                                continue; // an toàn: phải có unit label
                                        }
                                        priceDisplay = formatter.format(unitPrice) + " đ / " + unitLabel;
                                }

                                html.append("<div class=\"product-card\" data-product-id=\"").append(p.getProductID())
                                                .append("\" data-stock-quantity=\"").append(p.getStockQuantity())
                                                .append("\">");
                                if (p.getStockQuantity() <= 0) {
                                        html.append("<div class=\"product-badge out-of-stock\">Hết hàng</div>");
                                }
                                html.append("    <div class=\"product-image-container\">");
                                html.append("        <img src=\"ImageServlet?name=").append(p.getImageURL())
                                                .append("\" alt=\"")
                                                .append(p.getProductName())
                                                .append("\" class=\"product-image\">");
                                html.append("        <div class=\"product-actions\">");
                                html.append("            <button class=\"action-btn add-to-cart-action\" data-product-id=\"")
                                                .append(p.getProductID())
                                                .append("\" data-stock-quantity=\"").append(p.getStockQuantity())
                                                .append("\" ")
                                                .append(p.getStockQuantity() <= 0
                                                                ? "disabled style='opacity:0.5;cursor:not-allowed;'"
                                                                : "")
                                                .append("><i class=\"fas fa-cart-plus\"></i></button>");
                                html.append("            <a href=\"").append(request.getContextPath())
                                                .append("/ProductDetail?id=")
                                                .append(p.getProductID())
                                                .append("\" class=\"action-btn\"><i class=\"fas fa-eye\"></i></a>");
                                html.append("        </div>");
                                html.append("    </div>");
                                html.append("    <div class=\"product-info\">");
                                html.append("        <h3 class=\"product-name\">").append(p.getProductName())
                                                .append("</h3>");
                                html.append("        <div class=\"product-rating\">");
                                html.append("            <i class=\"far fa-star\"></i>");
                                html.append("            <i class=\"far fa-star\"></i>");
                                html.append("            <i class=\"far fa-star\"></i>");
                                html.append("            <i class=\"far fa-star\"></i>");
                                html.append("            <i class=\"far fa-star\"></i>");
                                html.append("            <span>(0)</span>");
                                html.append("        </div>");
                                html.append("        <div class=\"product-price\">");
                                html.append(priceDisplay);
                                html.append("</div>");
                                html.append("        <div class=\"button-group\">");
                                html.append("            <button class=\"add-to-cart-btn\" data-product-id=\"")
                                                .append(p.getProductID())
                                                .append("\" data-stock-quantity=\"").append(p.getStockQuantity())
                                                .append("\" ")
                                                .append(p.getStockQuantity() <= 0
                                                                ? "disabled style='opacity:0.5;cursor:not-allowed;'"
                                                                : "")
                                                .append("><i class=\"fas fa-shopping-cart\"></i> Giỏ hàng</button>");
                                html.append("            <a href=\"").append(request.getContextPath())
                                                .append("/ProductDetail?id=")
                                                .append(p.getProductID())
                                                .append("\" class=\"buy-now-btn\" ")
                                                .append(p.getStockQuantity() <= 0
                                                                ? "style='pointer-events:none;opacity:0.5;cursor:not-allowed;'"
                                                                : "")
                                                .append(">Mua ngay</a>");
                                html.append("        </div>");
                                html.append("    </div>");
                                html.append("</div>");
                        }

                        String result = html.toString();
                        out.print(result);

                } catch (NumberFormatException e) {
                        System.err.println("❌ Lỗi parse số: " + e.getMessage());
                        e.printStackTrace();
                        out.print("error: invalid number format");
                } catch (Exception e) {
                        System.err.println("❌ Lỗi chung: " + e.getMessage());
                        e.printStackTrace();
                        out.print("error: " + e.getMessage());
                }
        }
}
