<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Revenue Dashboard</title>
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/img/eco.png" type="image/x-icon">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis()%>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis()%>">
        <style>
            body {
                background-color: #f8f9fa;
                padding: 20px;
                font-family: 'Segoe UI', sans-serif;
            }
            .dashboard-card {
                border-radius: 15px;
                box-shadow: 0 4px 20px rgba(0,0,0,0.1);
                padding: 30px;
                background: #ffffff;
                margin-bottom: 30px;
            }
            .dashboard-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 20px;
            }
            .form-select, .form-control {
                width: auto;
                display: inline-block;
                margin-right: 10px;
            }
            table {
                background-color: white;
                border-radius: 8px;
                overflow: hidden;
            }
            table th {
                background-color: #0d6efd;
                color: white;
            }
            .btn-primary, .btn-success {
                border-radius: 10px;
            }
        </style>
    </head>
    <body>

        <jsp:include page="../components/sidebar.jsp" />

        <div class="container">
            <div class="dashboard-card">
                <div class="dashboard-header">
                    <h2><i class="fas fa-chart-line"></i> Revenue Summary - <c:out value="${month}"/> / <c:out value="${year}"/></h2>
                    <form class="d-flex" method="get" action="${pageContext.request.contextPath}/admin/statistic/monthly">
                        <select class="form-select" name="month" id="month">
                            <c:forEach var="i" begin="1" end="12">
                                <option value="${i}" ${i == month ? 'selected' : ''}>Tháng ${i}</option>
                            </c:forEach>
                        </select>

                        <input type="number" class="form-control" name="year" id="year" value="${year}" min="2000" max="2100"/>

                        <button type="submit" class="btn btn-primary"><i class="fas fa-filter"></i> View</button>
                    </form>
                </div>

                <div class="row text-center mb-4">
                    <div class="col-md-4">
                        <div class="p-3 bg-light border rounded shadow-sm">
                            <h5><i class="fas fa-dollar-sign text-success"></i> Total Revenue</h5>
                            <p class="fw-bold text-success fs-5"><fmt:formatNumber value="${revenue}" type="currency"/></p>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="p-3 bg-light border rounded shadow-sm">
                            <h5><i class="fas fa-receipt text-primary"></i> Total Orders</h5>
                            <p class="fw-bold fs-5">${totalOrders}</p>
                        </div>
                    </div>
                    <div class="col-md-4">
                        <div class="p-3 bg-light border rounded shadow-sm">
                            <h5><i class="fas fa-boxes text-warning"></i> Total Products Sold</h5>
                            <p class="fw-bold fs-5">${totalProducts}</p>
                        </div>
                    </div>
                </div>

                <table class="table table-bordered text-center">
                    <thead>
                        <tr>
                            <th><i class="fas fa-box"></i> Product</th>
                            <th><i class="fas fa-sort-amount-up-alt"></i> Quantity Sold</th>
                            <th><i class="fas fa-sack-dollar"></i> Revenue</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${productList}" var="item">
                            <tr>
                                <td>${item.productName}</td>
                                <td>${item.totalQuantity}</td>
                                <td><fmt:formatNumber value="${item.totalRevenue}" type="currency"/></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>

                <form action="${pageContext.request.contextPath}/admin/export-monthly-revenue" method="post" class="text-end mt-3">
                    <input type="hidden" name="month" value="${month}"/>
                    <input type="hidden" name="year" value="${year}"/>
                    <button type="submit" class="btn btn-success">
                        <i class="fas fa-file-excel"></i> Export to Excel
                    </button>
                </form>
            </div>
        </div>

    </body>
</html>
