<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/img/eco.png"
              type="image/x-icon">
        <title>EcoMart Admin Dashboard</title>
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap"
              rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis()%>">
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis()%>">
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/dashboard.css?version=<%= System.currentTimeMillis()%>">
        <script src="https://cdn.jsdelivr.net/npm/chart.js@3.9.1/dist/chart.min.js"></script>
    </head>

    <body>
        <div class="dashboard-container">
            <button class="mobile-menu-toggle" id="mobile-menu-toggle">
                <i class="fas fa-bars"></i>
            </button>
            <jsp:include page="./components/sidebar.jsp" />

            <main class="main">
                <h1 class="page-title">Dashboard Overview</h1>

                <!-- Statistics Cards -->
                <div class="dashboard-stats">
                    <div class="stat-card">
                        <div class="stat-icon">
                            <i class="fas fa-chart-line"></i>
                        </div>
                        <div class="stat-title">Total Revenue</div>
                        <div class="stat-value">
                            <fmt:formatNumber value="${revenueSummary.total}" type="number"
                                              maxFractionDigits="0" />
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon">
                            <i class="fas fa-shopping-cart"></i>
                        </div>
                        <div class="stat-title">Total Orders</div>
                        <div class="stat-value">${totalOrders}</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon">
                            <i class="fas fa-box"></i>
                        </div>
                        <div class="stat-title">Products</div>
                        <div class="stat-value">${totalProducts}</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon">
                            <i class="fas fa-users"></i>
                        </div>
                        <div class="stat-title">Customers</div>
                        <div class="stat-value">${totalAccounts}</div>
                    </div>
                </div>

                <!-- Main Content -->
                <div class="dashboard-content">
                    <!-- Recent Orders -->
                    <div class="content-card">
                        <div class="content-card-title">
                            <i class="fas fa-receipt"></i> Recent Orders
                        </div>
                        <c:choose>
                            <c:when test="${not empty recentOrders}">
                                <table class="orders-table">
                                    <thead>
                                        <tr>
                                            <th>Order ID</th>
                                            <th>Customer</th>
                                            <th>Date</th>
                                            <th>Amount</th>
                                            <th>Status</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${recentOrders}" var="order">
                                            <tr>
                                                <td>#${order.orderId}</td>
                                                <td>${order.customerName}</td>
                                                <td>
                                                    <fmt:formatDate value="${order.orderDate}"
                                                                    pattern="dd/MM/yyyy" />
                                                </td>
                                                <td>
                                                    <fmt:formatNumber value="${order.totalAmount}"
                                                                      type="number" maxFractionDigits="0" />
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${order.orderStatus eq 'Đang xử lý'}">
                                                            <span
                                                                class="status status-processing">${order.orderStatus}</span>
                                                        </c:when>
                                                        <c:when test="${order.orderStatus eq 'Đang giao hàng'}">
                                                            <span
                                                                class="status status-shipping">${order.orderStatus}</span>
                                                        </c:when>
                                                        <c:when test="${order.orderStatus eq 'Đã giao'}">
                                                            <span
                                                                class="status status-delivered">${order.orderStatus}</span>
                                                        </c:when>
                                                        <c:when test="${order.orderStatus eq 'Đã hủy'}">
                                                            <span
                                                                class="status status-canceled">${order.orderStatus}</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="status">${order.orderStatus}</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                                <a href="${pageContext.request.contextPath}/admin/order" class="view-all">View all orders <i
                                        class="fas fa-arrow-right"></i></a>
                                </c:when>
                                <c:otherwise>
                                <div class="empty-state">
                                    <i class="fas fa-receipt"></i>
                                    <p>No recent orders found</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- Order Status Chart -->
                    <div class="content-card">
                        <div class="content-card-title">
                            <i class="fas fa-chart-pie"></i> Order Status Distribution
                        </div>
                        <div class="chart-container">
                            <c:choose>
                                <c:when test="${not empty orderStatusCounts}">
                                    <canvas id="orderStatusChart"></canvas>
                                    </c:when>
                                    <c:otherwise>
                                    <div class="empty-state">
                                        <i class="fas fa-chart-pie"></i>
                                        <p>No order status data available</p>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </main>
        </div>

        <script>
            // Wait for the DOM to be fully loaded
            document.addEventListener('DOMContentLoaded', function () {
                // Mobile menu toggle
                const mobileMenuToggle = document.getElementById('mobile-menu-toggle');
                const sidebar = document.querySelector('.sidebar');

                if (mobileMenuToggle) {
                    mobileMenuToggle.addEventListener('click', function () {
                        sidebar.classList.toggle('show');
                    });
                }

                // Order Status Chart
                const statusLabels = [];
                const statusData = [];

            <c:forEach items="${orderStatusCounts}" var="entry">
                statusLabels.push("${entry.key}");
                statusData.push(${entry.value});
            </c:forEach>

                const statusColors = [
                    'rgba(212, 183, 143, 0.8)', // primary-color
                    'rgba(139, 108, 66, 0.8)', // accent-color
                    'rgba(195, 95, 95, 0.8)', // accent-secondary
                    'rgba(106, 82, 50, 0.8)', // accent-dark
                    'rgba(184, 156, 112, 0.8)'   // primary-dark
                ];

                const chartElement = document.getElementById('orderStatusChart');
                if (chartElement) {
                    const statusCtx = chartElement.getContext('2d');
                    const statusChart = new Chart(statusCtx, {
                        type: 'doughnut',
                        data: {
                            labels: statusLabels,
                            datasets: [{
                                    data: statusData,
                                    backgroundColor: statusColors,
                                    borderColor: '#ffffff',
                                    borderWidth: 2,
                                    hoverOffset: 15
                                }]
                        },
                        options: {
                            responsive: true,
                            maintainAspectRatio: false,
                            plugins: {
                                legend: {
                                    position: 'bottom',
                                    labels: {
                                        boxWidth: 15,
                                        padding: 15,
                                        font: {
                                            size: 12,
                                            family: "'Roboto', sans-serif"
                                        }
                                    }
                                },
                                tooltip: {
                                    backgroundColor: 'rgba(255, 255, 255, 0.9)',
                                    titleColor: '#333',
                                    bodyColor: '#666',
                                    borderColor: '#ddd',
                                    borderWidth: 1,
                                    padding: 10,
                                    boxPadding: 5,
                                    cornerRadius: 4,
                                    displayColors: true,
                                    callbacks: {
                                        label: function (context) {
                                            const label = context.label || '';
                                            const value = context.raw;
                                            const total = context.chart.data.datasets[0].data.reduce((a, b) => a + b, 0);
                                            const percentage = Math.round((value / total) * 100);
                                            return `${label}: ${value} (${percentage}%)`;
                                        }
                                    }
                                }
                            },
                            layout: {
                                padding: 20
                            },
                            cutout: '60%',
                            animation: {
                                animateScale: true,
                                animateRotate: true,
                                duration: 800
                            }
                        }
                    });
                }
            });
        </script>
    </body>

</html>