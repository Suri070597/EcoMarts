<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*, model.Product" %>
<%
    List<Product> products = (List<Product>) request.getAttribute("products");
%>
<% for (Product p : products) {%>
<div class="product-card">
    <div class="product-image-container">
        <img src="ImageServlet?name=<%= java.net.URLEncoder.encode(p.getImageURL(), "UTF-8")%>" alt="<%= p.getProductName()%>" class="product-image">
        <div class="product-actions">
            <button class="action-btn"><i class="fas fa-cart-plus"></i></button>
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
        <div class="button-group">
            <button class="add-to-cart-btn"><i class="fas fa-shopping-cart"></i> Giỏ hàng</button>
            <button class="buy-now-btn">Mua ngay</button>
        </div>
    </div>
</div>
<% }%>
