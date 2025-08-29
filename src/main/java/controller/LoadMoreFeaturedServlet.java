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
import db.DBContext;

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

                        // Tạo HTML cho các sản phẩm theo logic ViewProductDAO:
                        // - Trái cây (ParentID=3): kiểm tra tồn kho KG > 0
                        // - Danh mục khác: kiểm tra tồn kho UNIT > 0
                        // - Giá: UnitPrice / ItemUnitName
                        StringBuilder html = new StringBuilder();
                        for (Product p : products) {
                                java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols();
                                symbols.setGroupingSeparator('.');
                                java.text.DecimalFormat formatter = new java.text.DecimalFormat("#,###", symbols);

                                // Xác định parentId thực tế cho từng sản phẩm
                                Integer effectiveParentId = null;
                                try {
                                        if (p.getCategory() != null) {
                                                effectiveParentId = p.getCategory().getParentID();
                                        }
                                } catch (Exception ignore) {
                                }
                                if (effectiveParentId == null) {
                                        try {
                                                Product fullTmp = productDao.getProductById(p.getProductID());
                                                if (fullTmp != null && fullTmp.getCategory() != null) {
                                                        effectiveParentId = fullTmp.getCategory().getParentID();
                                                }
                                        } catch (Exception ignore) {
                                        }
                                }
                                if (effectiveParentId == null) {
                                        effectiveParentId = parentId; // fallback theo tham số
                                }

                                String packageType = (effectiveParentId != null && effectiveParentId == 3) ? "KG"
                                                : "UNIT";
                                double pkgQty = productDao.getQuantityByPackageType(p.getProductID(), packageType);
//                                if (pkgQty <= 0) {
////                                        continue; // không hiển thị nếu không có tồn đúng loại
//                                }

                                // Lấy giá từ Product.PriceUnit thay vì Inventory.UnitPrice
                                Double unitPrice = p.getPriceUnit();
                                if (unitPrice == null) {
                                        // Fallback lấy từ DB Product
                                        Product full = productDao.getProductById(p.getProductID());
                                        if (full != null)
                                                unitPrice = full.getPriceUnit();
                                }
                                if (unitPrice == null) {
                                        continue;
                                }
                                String unitLabel = productDao.getItemUnitName(p.getProductID());
                                if (unitLabel == null || unitLabel.trim().isEmpty()) {
                                        continue;
                                }
                                String priceDisplay = formatter.format(unitPrice) + " đ / " + unitLabel;

                                html.append("<div class=\"product-card\" data-product-id=\"").append(p.getProductID())
                                                .append("\" data-stock-quantity=\"").append(pkgQty)
                                                .append("\">");
                                if (pkgQty <= 0) {
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
                                                .append("\" data-stock-quantity=\"").append(pkgQty)
                                                .append("\" ")
                                                .append(pkgQty <= 0
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
                                                .append("\" data-stock-quantity=\"").append(pkgQty)
                                                .append("\" ")
                                                .append(pkgQty <= 0
                                                                ? "disabled style='opacity:0.5;cursor:not-allowed;'"
                                                                : "")
                                                .append("><i class=\"fas fa-shopping-cart\"></i> Giỏ hàng</button>");
                                html.append("            <a href=\"").append(request.getContextPath())
                                                .append("/ProductDetail?id=")
                                                .append(p.getProductID())
                                                .append("\" class=\"buy-now-btn\" ")
                                                .append(pkgQty <= 0
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

        // Chỉ dùng trong servlet này để lấy số lượng UNIT, tránh sửa DAO khác
        private double getUnitQuantity(int productId) {
                double qty = 0.0;
                String sql = "SELECT COALESCE(Quantity, 0) AS Q FROM Inventory WHERE ProductID = ? AND PackageType = 'UNIT'";
                DBContext db = new DBContext();
                java.sql.ResultSet rs = null;
                try {
                        rs = db.execSelectQuery(sql, new Object[] { productId });
                        if (rs.next()) {
                                qty = rs.getDouble("Q");
                        }
                } catch (Exception e) {
                        // log và trả 0 để ẩn sản phẩm nếu lỗi
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
                return qty;
        }
}
