<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*, model.Product" %>
<%
    List<Product> products = (List<Product>) request.getAttribute("products");
%>
<% for (Product p : products) {%>
<div class="product-card" data-product-id="<%= p.getProductID() %>" data-stock-quantity="<%= p.getStockQuantity() %>">
    <div class="product-badge">Hot</div>
    <div class="product-image-container">
        <img src="ImageServlet?name=<%= java.net.URLEncoder.encode(p.getImageURL(), "UTF-8")%>" alt="<%= p.getProductName()%>" class="product-image">
        <div class="product-actions">
            <button type="button" class="action-btn add-to-cart-action" data-product-id="<%= p.getProductID() %>" data-stock-quantity="<%= p.getStockQuantity() %>"><i class="fas fa-cart-plus"></i></button>
            <a href="ProductDetail?id=<%= p.getProductID()%>" class="action-btn"><i class="fas fa-eye"></i></a>
        </div>
    </div>
    <div class="product-info">
        <h3 class="product-name"><%= p.getProductName()%></h3>
        <div class="product-rating">
            <i class="fas fa-star"></i>
            <i class="fas fa-star"></i>
            <i class="fas fa-star"></i>
            <i class="fas fa-star"></i>
            <i class="fas fa-star-half-alt"></i>
            <span>(29)</span>
        </div>
        <div class="product-price"><%= new java.text.DecimalFormat("#,###").format(p.getPrice())%> VNĐ / <%= p.getUnit()%></div>
        <div class="text-muted small">Còn <span class="text-success"><%= p.getStockQuantity() %></span> sản phẩm</div>
        <div class="button-group">
            <button class="add-to-cart-btn" data-product-id="<%= p.getProductID() %>" data-stock-quantity="<%= p.getStockQuantity() %>"><i class="fas fa-shopping-cart"></i> Giỏ hàng</button>
            <a href="ProductDetail?id=<%= p.getProductID()%>" class="buy-now-btn">Mua ngay</a>
        </div>
    </div>
</div>
<% }%>
