package controller;

import dao.ViewProductDAO;
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

                        System.out.println(
                                        "🔍 Request parameters: offset=" + offsetStr + ", limit=" + limitStr
                                                        + ", parentId=" + parentIdStr);

                        if (offsetStr == null || limitStr == null || parentIdStr == null) {
                                System.out.println("❌ Thiếu parameters");
                                out.print("error: missing parameters");
                                return;
                        }

                        int offset = Integer.parseInt(offsetStr);
                        int limit = Integer.parseInt(limitStr);
                        int parentId = Integer.parseInt(parentIdStr);

                        System.out.println("📊 Parsed values: offset=" + offset + ", limit=" + limit + ", parentId="
                                        + parentId);

                        // Lấy sản phẩm từ database
                        List<Product> products = dao.getFeaturedProductsByPage(parentId, offset, limit);
                        System.out.println("📦 Số sản phẩm lấy được: " + (products != null ? products.size() : "null"));

                        // Nếu không có sản phẩm, trả về chuỗi rỗng
                        if (products == null || products.isEmpty()) {
                                System.out.println("📭 Không có sản phẩm để trả về");
                                out.print("");
                                return;
                        }

                        // Tạo HTML đơn giản cho các sản phẩm dgfg
                        StringBuilder html = new StringBuilder();
                        for (Product p : products) {
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
                                html.append("        <div class=\"product-price\">")
                                                .append(String.format("%,.0f", p.getPrice()))
                                                .append(" đ / ")
                                                .append(p.getUnit()).append("</div>");
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
                        System.out.println("✅ Trả về HTML với độ dài: " + result.length());
                        out.print(result);

                } catch (NumberFormatException e) {
                        System.out.println("❌ Lỗi parse số: " + e.getMessage());
                        e.printStackTrace();
                        out.print("error: invalid number format");
                } catch (Exception e) {
                        System.out.println("❌ Lỗi chung: " + e.getMessage());
                        e.printStackTrace();
                        out.print("error: " + e.getMessage());
                }
        }
}
