<%@page import="model.Category"%>
<%@page import="dao.ViewProductDAO"%>
<%@ page import="java.util.*, model.Product" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%@ page import="model.*" %>
<%@ page import="dao.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%
    List<Product> products = (List<Product>) request.getAttribute("searchResult");
    String keyword = (String) request.getAttribute("searchKeyword");
%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Kết Quả Tìm Kiếm</title>
        <link rel="shortcut icon" href="assets/img/eco.png" type="image/x-icon">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <!-- Font Awesome -->
        <link rel="stylesheet"
              href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <!-- Main CSS -->
        <link rel="stylesheet" href="./assets/css/main.css?version=<%= System.currentTimeMillis()%>" />
        <!-- Home CSS -->
        <link rel="stylesheet" href="./assets/css/home.css?version=<%= System.currentTimeMillis()%>">
        <script defer src="./assets/js/homeJs.js"></script>
        <!-- Animate on scroll -->
        <link rel="stylesheet" href="https://unpkg.com/aos@next/dist/aos.css" />
    </head>

    <body>
        <jsp:include page="header.jsp" />

        <!-- Main content -->
        <div class="main-content1">
            <h2 class="search-title">Kết quả tìm kiếm cho: "<%= keyword%>"</h2>
            <div class="product-grid">
                <c:choose>
                    <c:when test="${not empty searchResult}">
                        <c:forEach var="p" items="${searchResult}">
                            <div class="product-card">
                                <div class="product-image-container">
                                    <img src="ImageServlet?name=${p.imageURL}" alt="${p.productName}" class="product-image">
                                    <div class="product-actions">
                                        <button class="action-btn"><i class="fas fa-cart-plus"></i></button>
                                        <a href="ProductDetail?id=${p.productID}" class="action-btn"><i class="fas fa-eye"></i></a>
                                    </div>
                                </div>
                                <div class="product-info">
                                    <h3 class="product-name">${p.productName}</h3>
                                    <div class="product-rating">
                                        <i class="fas fa-star"></i>
                                        <i class="fas fa-star"></i>
                                        <i class="fas fa-star"></i>
                                        <i class="fas fa-star"></i>
                                        <i class="fas fa-star-half-alt"></i>
                                        <span>(29)</span>
                                    </div>
                                    <div class="product-price">
                                        <fmt:formatNumber value="${p.price}" type="number" pattern=",###" /> VNĐ / ${p.unit}
                                    </div>
                                    <div class="button-group">
                                        <button class="add-to-cart-btn" data-product-id="${p.productID}" data-stock-quantity="${p.stockQuantity}"><i class="fas fa-shopping-cart"></i> Giỏ hàng</button>
                                        <a href="${pageContext.request.contextPath}/ProductDetail?id=${p.productID}" class="buy-now-btn">Mua ngay</a>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <p class="no-result-message"><i class="fas fa-exclamation-circle"></i> Không tìm thấy sản phẩm phù hợp.</p>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
        <jsp:include page="footer.jsp" />

    </body>
</html>
