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
        <script src="https://cdn.jsdelivr.net/npm/chart.js@3.9.1/dist/chart.min.js"></script>
        <style>
            body,
            html {
                margin: 0;
                padding: 0;
                height: 100%;
                font-family: 'Roboto', sans-serif;
                background-color: var(--bg-color);
            }

            .dashboard-container {
                display: flex;
                min-height: 100vh;
            }

            .main {
                flex: 1;
                padding: 30px;
                margin-left: 333px;
                /* Match sidebar width */
            }

            .mobile-menu-toggle {
                display: none;
                position: fixed;
                top: 15px;
                left: 15px;
                z-index: 1100;
                background-color: transparent;
                color: var(--primary-color);
                border: none;
                border-radius: 50%;
                width: 32px;
                height: 32px;
                font-size: 16px;
                cursor: pointer;
                transition: var(--transition);
            }

            .mobile-menu-toggle:hover {
                color: var(--primary-dark);
                transform: scale(1.1);
            }

            .page-title {
                color: var(--text-dark);
                margin-bottom: 30px;
                font-size: 28px;
                font-weight: 700;
                position: relative;
                padding-bottom: 15px;
            }

            .page-title:after {
                content: '';
                position: absolute;
                bottom: 0;
                left: 0;
                width: 50px;
                height: 4px;
                background: linear-gradient(to right, var(--primary-color), var(--primary-dark));
                border-radius: 2px;
            }

            .dashboard-stats {
                display: grid;
                grid-template-columns: repeat(4, 1fr);
                gap: 25px;
                margin-bottom: 40px;
            }

            .stat-card {
                background-color: var(--white);
                border-radius: var(--border-radius-lg);
                padding: 10px;
                box-shadow: var(--box-shadow);
                transition: var(--transition);
                position: relative;
                overflow: hidden;
                min-height: 50px;
            }

            .stat-card:hover {
                transform: translateY(-5px);
                box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
            }

            .stat-card .stat-title {
                font-size: 15px;
                color: var(--text-muted);
                margin-bottom: 4px;
                font-weight: 500;
                padding-right: 35px;
            }

            .stat-card .stat-value {
                font-size: 14px;
                font-weight: 500;
                color: var(--text-dark);
                margin-top: 0;
                padding-right: 35px;
            }

            .stat-card .stat-icon {
                position: absolute;
                top: 50%;
                right: 8px;
                transform: translateY(-50%);
                width: 28px;
                height: 28px;
                display: flex;
                align-items: center;
                justify-content: center;
                border-radius: 6px;
                background-color: var(--primary-color);
                color: white;
                font-size: 13px;
                transition: all 0.3s ease;
                box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
            }

            .stat-card:hover .stat-icon {
                transform: translateY(-50%) scale(1.1) rotate(5deg);
                background-color: var(--primary-dark);
                box-shadow: 0 6px 15px rgba(0, 0, 0, 0.15);
            }

            .stat-card:before {
                content: '';
                position: absolute;
                top: 0;
                left: 0;
                width: 5px;
                height: 100%;
                background: linear-gradient(to bottom, var(--primary-color), var(--primary-dark));
            }

            .dashboard-content {
                display: grid;
                grid-template-columns: 1fr 1fr;
                gap: 30px;
                margin-bottom: 30px;
                min-height: 400px;
            }

            .content-card {
                background-color: var(--white);
                border-radius: var(--border-radius-lg);
                padding: 25px;
                box-shadow: var(--box-shadow);
                display: flex;
                flex-direction: column;
            }

            .content-card-title {
                font-size: 20px;
                color: var(--text-dark);
                margin-bottom: 20px;
                font-weight: 600;
                display: flex;
                align-items: center;
            }

            .content-card-title i {
                margin-right: 10px;
                color: var(--primary-color);
            }

            .orders-table {
                width: 100%;
                border-collapse: collapse;
            }

            .orders-table th,
            .orders-table td {
                padding: 15px;
                text-align: left;
                border-bottom: 1px solid var(--gray-200);
            }

            .orders-table th {
                background-color: var(--gray-100);
                color: var(--text-dark);
                font-weight: 600;
                font-size: 14px;
                text-transform: uppercase;
            }

            .orders-table tr:hover {
                background-color: var(--gray-100);
            }

            .orders-table tr:last-child td {
                border-bottom: none;
            }

            .status {
                padding: 6px 12px;
                border-radius: 20px;
                font-size: 13px;
                font-weight: 500;
                display: inline-block;
                text-align: center;
                min-width: 100px;
            }

            .status-processing {
                background-color: #fff8e1;
                color: #ff9800;
            }

            .status-shipping {
                background-color: #e1f5fe;
                color: #03a9f4;
            }

            .status-delivered {
                background-color: #e8f5e9;
                color: #4caf50;
            }

            .status-canceled {
                background-color: #ffebee;
                color: #f44336;
            }

            .chart-container {
                flex: 1;
                display: flex;
                align-items: center;
                justify-content: center;
                position: relative;
                width: 100%;
                height: 350px;
            }

            #orderStatusChart {
                max-width: 100%;
                max-height: 350px;
            }

            .empty-state {
                display: flex;
                flex-direction: column;
                align-items: center;
                justify-content: center;
                height: 100%;
                color: var(--text-muted);
            }

            .empty-state i {
                font-size: 48px;
                margin-bottom: 15px;
            }

            .view-all {
                display: inline-block;
                margin-top: 20px;
                color: var(--primary-color);
                text-decoration: none;
                font-weight: 500;
                font-size: 14px;
                transition: color 0.3s ease;
            }

            .view-all:hover {
                color: var(--primary-dark);
            }

            @media (max-width: 1200px) {
                .dashboard-stats {
                    grid-template-columns: repeat(2, 1fr);
                }

                .dashboard-content {
                    grid-template-columns: 1fr;
                }
            }

            @media (max-width: 992px) {
                .main {
                    margin-left: 0;
                    padding: 20px;
                    padding-top: 40px;
                }

                .sidebar {
                    display: none;
                }

                .mobile-menu-toggle {
                    display: flex;
                    align-items: center;
                    justify-content: center;
                }

                .sidebar.show {
                    display: block;
                }
            }

            @media (max-width: 768px) {
                .dashboard-stats {
                    grid-template-columns: 1fr;
                }

                .main {
                    padding: 15px;
                }

                #orderStatusChart {
                    max-height: 300px;
                }
            }
        </style>
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