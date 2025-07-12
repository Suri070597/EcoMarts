<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<c:forEach items="${products}" var="p">
    <div class="product-card">
        <div class="product-image-container">
            <img src="ImageServlet?name=${p.imageURL}" alt="${p.productName}" class="product-image">
            <div class="product-actions">
                <a href="${pageContext.request.contextPath}/ProductDetail?id=${p.productID}" class="action-btn"><i class="fas fa-eye"></i></a>
            </div>
        </div>
        <div class="product-info">
            <h3 class="product-name">${p.productName}</h3>
            <div class="product-rating">
                <!-- Ratings would be added here if available -->
                <i class="far fa-star"></i>
                <i class="far fa-star"></i>
                <i class="far fa-star"></i>
                <i class="far fa-star"></i>
                <i class="far fa-star"></i>
                <span>(0)</span>
            </div>
            <div class="product-price">${p.price} VNĐ / ${p.unit}</div>
            <div class="button-group">
                <button class="add-to-cart-btn" data-product-id="${p.productID}" data-stock-quantity="${p.stockQuantity}"><i class="fas fa-shopping-cart"></i> Giỏ hàng</button>
                <a href="${pageContext.request.contextPath}/ProductDetail?id=${p.productID}" class="buy-now-btn">Mua ngay</a>
            </div>
        </div>
    </div>
</c:forEach>
