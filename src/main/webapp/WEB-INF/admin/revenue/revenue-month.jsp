<%@page import="java.util.List"%>
<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<fmt:setLocale value="vi_VN"/>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Doanh thu theo tháng</title>
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/img/eco.png" type="image/x-icon">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis()%>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis()%>">
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
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
                margin-left: 50px;
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
            
            table td.number {
                text-align: right;
            }
            
            .btn-primary, .btn-success {
                border-radius: 10px;
            }
            .chart-container {
                margin-top: 40px;
            }
        </style>
    </head>
    <body>

        <jsp:include page="../components/sidebar.jsp" />
        <div class="row">
            <div class="col-md-2">
            </div>
            <div class="container-fuild col-md-10">

                <div class="dashboard-card">
                    <div class="dashboard-header">
                        <h2><i class="fas fa-chart-line"></i> Tổng quan doanh thu - <c:out value="${month}"/> / <c:out value="${year}"/></h2>

                        <form class="d-flex" method="get" action="${pageContext.request.contextPath}/admin/statistic/monthly">
                            <select class="form-select" name="month" id="month">
                                <c:forEach var="i" begin="1" end="12">
                                    <option value="${i}" ${i == month ? 'selected' : ''}>Tháng ${i}</option>
                                </c:forEach>
                            </select>

                            <input type="number" class="form-control" name="year" id="year" value="${year}" min="2000" max="2100"/>
                            <button type="submit" class="btn btn-primary"><i class="fas fa-filter"></i> Xem</button>

                        </form>
                        <div class="mb-4">
                            <form action="${pageContext.request.contextPath}/admin/export-monthly-revenue" method="post" class="text-end mt-3">
                                <input type="hidden" name="month" value="${month}"/>
                                <input type="hidden" name="year" value="${year}"/>
                                <button type="submit" class="btn btn-success">
                                    <i class="fas fa-file-excel"></i> Xuất ra Excel
                                </button>
                            </form>
                        </div>                  
                    </div>

                    <div class="row text-center mb-4">
                        <div class="col-md-4">
                            <div class="p-3 bg-light border rounded shadow-sm">
                                <h5><i class="fas fa-money-bill-wave text-success"></i> Tổng doanh thu</h5>
                                <p class="fw-bold text-success"><fmt:formatNumber value="${revenue}" type="currency"/></p>
                            </div>
                        </div>
                        <div class="col-md-4">
                            <div class="p-3 bg-light border rounded shadow-sm">
                                <h5><i class="fas fa-receipt text-primary"></i> Tổng số đơn hàng</h5>
                                <p class="fw-bold fs-5">${totalOrders}</p>
                            </div>
                        </div>
                        <div class="col-md-4">
                            <div class="p-3 bg-light border rounded shadow-sm">
                                <h5><i class="fas fa-boxes text-warning"></i> Tổng số sản phẩm đã bán</h5>
                                <p class="fw-bold fs-5">${totalProducts}</p>
                            </div>
                        </div>
                    </div>

                    <!-- Biểu đồ doanh thu 12 tháng -->
                    <div class="chart-container mt-5">
                        <h4 class="text-center mb-4">Doanh thu 12 tháng năm <c:out value="${year}"/></h4>

                        <canvas id="monthlyRevenueChart" height="100"></canvas>
                    </div>

                    <table class="table table-bordered text-center">
                        <thead>
                            <tr>
                                <th><i class="fas fa-box"></i> Sản phẩm</th>
                                <th><i class="fas fa-sort-amount-up-alt"></i> Số lượng đã bán</th>
                                <th><i class="fas fa-money-bill-wave text-success"></i> Doanh thu</th>
                            </tr>

                        </thead>
                        <tbody>
                            <c:forEach items="${productList}" var="item">
                                <tr>
                                    <td>${item.productName}</td>
                                    <td class="number">${item.totalQuantity}</td>
                                    <td class="number"><fmt:formatNumber value="${item.totalRevenue}" type="currency"/></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>


                </div>


            </div>
        </div>
        <%
            // Tạo mảng 12 tháng mặc định bằng 0
            double[] revenueByMonth = new double[12];
            for (int i = 0; i < revenueByMonth.length; i++) {
                revenueByMonth[i] = 0.0;
            }

            // Gán doanh thu cho từng tháng nếu có
            List<model.RevenueStats> stats = (List<model.RevenueStats>) request.getAttribute("revenuePerMonth");
            if (stats != null) {
                for (model.RevenueStats stat : stats) {
                    int month = stat.getMonth(); // từ 1–12
                    double revenue = stat.getTotalRevenue();
                    revenueByMonth[month - 1] = revenue; // tháng 1 sẽ nằm ở index 0
                }
            }
        %>

        <script>
            const ctx = document.getElementById('monthlyRevenueChart').getContext('2d');
            const revenueData = [
            <%= revenueByMonth[0]%>, <%= revenueByMonth[1]%>, <%= revenueByMonth[2]%>, <%= revenueByMonth[3]%>,
            <%= revenueByMonth[4]%>, <%= revenueByMonth[5]%>, <%= revenueByMonth[6]%>, <%= revenueByMonth[7]%>,
            <%= revenueByMonth[8]%>, <%= revenueByMonth[9]%>, <%= revenueByMonth[10]%>, <%= revenueByMonth[11]%>
            ];

            const monthlyChart = new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: ["Jan", "Feb", "Mar", "Apr", "May", "Jun",
                        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"],
                    datasets: [{
                            label: 'Revenue (đ)',
                            data: revenueData,
                            backgroundColor: 'rgba(54, 162, 235, 0.7)',
                            borderColor: 'rgba(54, 162, 235, 1)',
                            borderWidth: 1
                        }]
                },
                options: {
                    responsive: true,
                    scales: {
                        y: {
                            beginAtZero: true,
                            ticks: {
                                callback: function (value) {
                                    let price = new Intl.NumberFormat('vi-VN', {
                                        style: 'currency',
                                        currency: 'VND'
                                    }).format(value);

// Đổi ₫ thành đ
                                    price = price.replace('₫', 'đ');
                                    return price;

                                }
                            }
                        }
                    }
                }
            });
        </script>


    </body>
</html>
