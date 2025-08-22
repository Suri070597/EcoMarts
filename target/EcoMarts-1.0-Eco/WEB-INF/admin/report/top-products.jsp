<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/img/eco.png"
              type="image/x-icon">
        <title>Top 10 sản phẩm</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis()%>">
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis()%>">
    </head>

    <body>
        <div class="container-fluid">
            <%-- Include admin sidebar --%>
            <jsp:include page="/WEB-INF/admin/components/sidebar.jsp" />

            <div class="main-content">
                <div class="card">
                    <div class="card-header">
                        <div class="header-actions">
                            <h1 class="card-title">Top 10 sản phẩm</h1>
                            <div class="d-flex gap-3">
                                <form action="${pageContext.request.contextPath}/admin/report/top-products" method="get">
                                </form>
                            </div>
                        </div>
                    </div>

                    <div class="table-container">
                        <table class="table table-striped table-hover">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Hình ảnh</th>
                                    <th>Tên sản phẩm</th>
                                    <th style="text-align: right;">Tổng số lượng bán</th>
                                    <th style="text-align: right;">Tổng doanh thu</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="product" items="${topProducts}" varStatus="status">
                                    <tr>
                                        <td>${product.productId}</td>
                                        <td>
                                            <img src="<%= request.getContextPath()%>/ImageServlet?name=${product.image}" alt="${product.productName}" width="60" height="60" style="object-fit: cover; border-radius: 6px;">
                                        </td>
                                        <td>${product.productName}</td>
                                        <td style="text-align: right;">${product.totalQuantity}</td>
                                        <td style="text-align: right;">
                                            <fmt:formatNumber value="${product.totalRevenue}" type="number"/>đ
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>

                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>

</html>