<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%
    Map<Integer, Double> avgRatingMap = (Map<Integer, Double>) request.getAttribute("avgRatingMap");
    Map<Integer, Integer> reviewCountMap = (Map<Integer, Integer>) request.getAttribute("reviewCountMap");
%>

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
                <c:set var="productId" value="${p.productID}" />
                <c:set var="avgRating" value="${avgRatingMap[productId]}" />
                <c:set var="reviewCount" value="${reviewCountMap[productId]}" />
                
                <c:choose>
                    <c:when test="${empty avgRating}">
                        <c:set var="avgRating" value="0.0" />
                    </c:when>
                </c:choose>
                <c:choose>
                    <c:when test="${empty reviewCount}">
                        <c:set var="reviewCount" value="0" />
                    </c:when>
                </c:choose>
                
                <%
                    Object avgRatingObj = pageContext.getAttribute("avgRating");
                    double avgRatingValue = 0.0;
                    if (avgRatingObj != null) {
                        avgRatingValue = Double.parseDouble(avgRatingObj.toString());
                    }
                    int fullStars = (int) Math.floor(avgRatingValue);
                    boolean hasHalfStar = (avgRatingValue - fullStars) >= 0.5;
                    pageContext.setAttribute("fullStars", fullStars);
                    pageContext.setAttribute("hasHalfStar", hasHalfStar);
                %>
                
                <c:forEach begin="1" end="${fullStars}" var="i">
                    <i class="fas fa-star"></i>
                </c:forEach>
                <c:if test="${hasHalfStar}">
                    <i class="fas fa-star-half-alt"></i>
                </c:if>
                <c:forEach begin="1" end="${5 - fullStars - (hasHalfStar ? 1 : 0)}" var="i">
                    <i class="far fa-star"></i>
                </c:forEach>
                <span>(${reviewCount})</span>
            </div>
            <div class="product-price"><fmt:formatNumber value="${p.price}" type="number" pattern="#,###" /> đ / ${p.unit}</div>
            <div class="button-group">
                <button class="add-to-cart-btn" data-product-id="${p.productID}" data-stock-quantity="${p.stockQuantity}"><i class="fas fa-shopping-cart"></i> Giỏ hàng</button>
                <a href="${pageContext.request.contextPath}/ProductDetail?id=${p.productID}" class="buy-now-btn">Mua ngay</a>
            </div>
        </div>
    </div>
</c:forEach>
