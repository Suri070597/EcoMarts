<%@page import="model.Product"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
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
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/homeProductDetail.css?version=<%= System.currentTimeMillis()%>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/mainProductDetail.css?version=<%= System.currentTimeMillis()%>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/productDetail.css?version=<%= System.currentTimeMillis()%>">
        <style>
            body {
                background-color: #f8f5ed;
                font-family: 'Poppins', sans-serif;
                padding: 20px;
            }
            .main-content1 h2 {
                margin-bottom: 25px;
            }
            .product-grid {
                display: flex;
                flex-wrap: wrap;
                gap: 20px;
            }
            .product-card {
                background: #fff;
                border-radius: 12px;
                box-shadow: 0 4px 8px rgba(0,0,0,0.05);
                padding: 15px;
                width: 240px;
                display: flex;
                flex-direction: column;
                transition: 0.3s ease;
            }
            .product-card:hover {
                transform: translateY(-4px);
                box-shadow: 0 8px 16px rgba(0,0,0,0.1);
            }
            .product-image {
                width: 100%;
                height: 240px;
                object-fit: cover;
                border-radius: 8px;
                margin-bottom: 10px;
            }
            .product-name {
                font-size: 16px;
                font-weight: 500;
                margin-bottom: 5px;
                color: #333;
            }
            .product-rating {
                font-size: 14px;
                color: #ffc107;
                margin-bottom: 5px;
            }
            .product-rating span {
                color: #666;
                margin-left: 5px;
            }
            .product-price {
                font-size: 15px;
                font-weight: 600;
                color: #333;
                margin-bottom: 10px;
            }
            .search-title {
                font-size: 24px;
                font-weight: 600;
                color: #333;
                margin-bottom: 10px;
                text-align: center;
                text-transform: uppercase;
                letter-spacing: 1px;
            }

            .no-result-message {
                font-size: 18px;
                color: #888;
                background-color: #fff3cd;
                border: 1px solid #ffeeba;
                padding: 15px 20px;
                border-radius: 8px;
                text-align: center;
                max-width: 600px;
                margin: 20px auto;
                box-shadow: 0 4px 10px rgba(0,0,0,0.05);
            }
        </style>
    </head>
    <body>
        <jsp:include page="headerProductDetail.jsp" />
        <div class="main-content1">
            <h2 class="search-title">Kết quả tìm kiếm cho: "<%= keyword%>"</h2>
            <div class="product-grid">
                <% if (products != null && !products.isEmpty()) {
                        for (Product p : products) {%>
                <div class="product-card">
                    <div class="product-image-container">
                        <img src="ImageServlet?name=<%= p.getImageURL()%>" alt="<%= p.getProductName()%>" class="product-image">
                        <div class="product-actions">
                            <button class="action-btn"><i class="fas fa-cart-plus"></i></button>
                            <a href="<%= request.getContextPath()%>/ProductDetail?id=<%= p.getProductID()%>" class="action-btn"><i class="fas fa-eye"></i></a>
                        </div>
                    </div>
                    <h3 class="product-name"><%= p.getProductName()%></h3>
                    <div class="product-rating">
                        <i class="fas fa-star"></i>
                        <i class="fas fa-star"></i>
                        <i class="fas fa-star"></i>
                        <i class="fas fa-star"></i>
                        <i class="fas fa-star-half-alt"></i>
                        <span>(29)</span>
                    </div>
                    <div class="product-price">
                        <%= new java.text.DecimalFormat("#,###").format(p.getPrice())%> VNĐ / <%= p.getUnit()%>
                    </div>
                    <div class="button-group">
                        <button class="add-to-cart-btn"><i class="fas fa-shopping-cart"></i> Giỏ hàng</button>
                        <button class="buy-now-btn">Mua ngay</button>
                    </div>
                </div>
                <% }
                } else { %>
                <p class="no-result-message"><i class="fas fa-exclamation-circle"></i> Không tìm thấy sản phẩm phù hợp.</p>
                <% }%>
            </div>
        </div>
        <jsp:include page="footerProductDetail.jsp" />
    </body>
</html>
