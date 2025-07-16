<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Yearly Revenue</title>
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/img/eco.png" type="image/x-icon">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis()%>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis()%>">
        <style>
            body {
                background-color: #f4f6f9;
                font-family: 'Segoe UI', sans-serif;
                padding: 20px;
            }
            .revenue-card {
                background: white;
                padding: 30px;
                border-radius: 15px;
                box-shadow: 0 0 15px rgba(0,0,0,0.08);
                margin-top: 20px;
            }
            .revenue-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
            }
            h2 {
                color: #2c3e50;
                font-weight: 600;
            }
            .stat-box {
                background-color: #e9f0f7;
                border-radius: 10px;
                padding: 20px;
                text-align: center;
            }
            .stat-box h6 {
                font-size: 1rem;
                color: #555;
            }
            .stat-box p {
                font-size: 1.2rem;
                font-weight: bold;
                margin: 0;
            }
            table {
                margin-top: 20px;
            }
            th {
                background-color: #0d6efd;
                color: white;
            }
            td, th {
                text-align: center;
                vertical-align: middle;
            }
            .btn-export {
                margin-top: 20px;
                float: right;
            }
        </style>
    </head>
    <body>

        <jsp:include page="../components/sidebar.jsp" />

        <div class="container">
            <div class="revenue-card">
                <div class="revenue-header mb-4">
                    <h2><i class="fas fa-calendar-alt"></i> Revenue Summary for Year <c:out value="${year}"/></h2>
                </div>

                <div class="row text-center mb-4">
                    <div class="col-md-4">
                        <div class="stat-box">
                            <h6><i class="fas fa-sack-dollar text-success"></i> Total Revenue</h6>
                            <p class="text-success"><fmt:formatNumber value="${revenue}" type="currency"/></p>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="stat-box">
                            <h6><i class="fas fa-clipboard-list text-primary"></i> Total Orders</h6>
                            <p>${totalOrders}</p>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="stat-box">
                            <h6><i class="fas fa-boxes-stacked text-warning"></i> Total Products Sold</h6>
                            <p>${totalProducts}</p>
                        </div>
                    </div>
                </div>

                <table class="table table-bordered table-hover shadow-sm bg-white">
                    <thead>
                        <tr>
                            <th><i class="fas fa-box"></i> Product</th>
                            <th><i class="fas fa-sort-amount-up"></i> Quantity Sold</th>
                            <th><i class="fas fa-money-bill-wave"></i> Total Revenue</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${productList}" var="item">
                            <tr>
                                <td><c:out value="${item.productName}"/></td>
                                <td>${item.totalQuantity}</td>
                                <td><fmt:formatNumber value="${item.totalRevenue}" type="currency"/></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>

                <form action="${pageContext.request.contextPath}/admin/export-yearly-revenue" method="post">
                    <input type="hidden" name="year" value="${year}"/>
                    <button type="submit" class="btn btn-success btn-export">
                        <i class="fas fa-file-excel"></i> Export to Excel
                    </button>
                </form>
            </div>
        </div>

    </body>
</html>
