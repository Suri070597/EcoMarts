<%@ page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="java.util.Map" %>
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
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
        <style>
            body {
                background-color: #f8f9fa;
                font-family: 'Segoe UI', sans-serif;
                padding: 20px;
            }
            .dashboard-card {
                border-radius: 15px;
                box-shadow: 0 4px 20px rgba(0,0,0,0.1);
                padding: 30px;
                background: #ffffff;
                margin-bottom: 30px;
            }
            table th {
                background-color: #0d6efd;
                color: white;
            }
            .btn-export {
                float: right;
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

                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <h2><i class="fas fa-calendar-alt"></i> Revenue Summary - <c:out value="${year}"/></h2>
                        <form action="${pageContext.request.contextPath}/admin/export-yearly-revenue" method="post">
                            <input type="hidden" name="year" value="${year}"/>
                            <button type="submit" class="btn btn-success btn-export">
                                <i class="fas fa-file-excel"></i> Export to Excel
                            </button>
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

                    <!-- Biểu đồ doanh thu 5 năm -->
                    <div class="chart-container mt-5">
                        <h4 class="text-center mb-4">Revenue of Last 5 Years</h4>
                        <canvas id="yearlyRevenueChart" height="100"></canvas>
                    </div>
                </div>
            </div>
        </div>
        <%-- Xử lý dữ liệu doanh thu theo năm để render JS --%>
        <%
            Map<Integer, Double> revenueMap = (Map<Integer, Double>) request.getAttribute("last5YearsRevenue");
            int[] years = new int[5];
            double[] revenues = new double[5];
            int index = 0;
            if (revenueMap != null) {
                for (Integer y : revenueMap.keySet().stream().sorted().toList()) {
                    years[index] = y;
                    revenues[index] = revenueMap.get(y);
                    index++;
                }
            }
        %>

        <script>
            const ctx = document.getElementById('yearlyRevenueChart').getContext('2d');
            const yearLabels = [<%= years[0]%>, <%= years[1]%>, <%= years[2]%>, <%= years[3]%>, <%= years[4]%>];
            const yearData = [<%= revenues[0]%>, <%= revenues[1]%>, <%= revenues[2]%>, <%= revenues[3]%>, <%= revenues[4]%>];

            new Chart(ctx, {
                type: 'bar',
                data: {
                    labels: yearLabels,
                    datasets: [{
                            label: 'Revenue (VND)',
                            data: yearData,
                            backgroundColor: 'rgba(75, 192, 192, 0.7)',
                            borderColor: 'rgba(75, 192, 192, 1)',
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
                                    return new Intl.NumberFormat('vi-VN', {
                                        style: 'currency',
                                        currency: 'VND'
                                    }).format(value);
                                }
                            }
                        }
                    }
                }
            });
        </script>

    </body>
</html>
