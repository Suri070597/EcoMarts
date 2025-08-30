<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/img/eco.png" type="image/x-icon">
        <title>EcoMart - Bảng điều khiển Admin</title>
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;500;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis()%>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis()%>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/dashboard.css?version=<%= System.currentTimeMillis()%>">
        <script src="https://cdn.jsdelivr.net/npm/chart.js@3.9.1/dist/chart.min.js"></script>
    </head>

    <body>
        <div class="dashboard-container">
            <button class="mobile-menu-toggle" id="mobile-menu-toggle">
                <i class="fas fa-bars"></i>
            </button>
            <jsp:include page="./components/sidebar.jsp" />

            <main class="main">
                <h1 class="page-title">Tổng Quan Bảng Điều Khiển</h1>

                <!-- Thống kê -->
                <div class="dashboard-stats">
                    <div class="stat-card">
                        <div class="stat-icon">
                            <i class="fas fa-chart-line"></i>
                        </div>
                        <div class="stat-title">Tổng Doanh Thu</div>
                        <div class="stat-value">
                            <fmt:formatNumber value="${revenueSummary.total}" type="number" maxFractionDigits="0" /> ₫
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon">
                            <i class="fas fa-shopping-cart"></i>
                        </div>
                        <div class="stat-title">Tổng Đơn Hàng</div>
                        <div class="stat-value">${totalOrders}</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon">
                            <i class="fas fa-box"></i>
                        </div>
                        <div class="stat-title">Sản Phẩm</div>
                        <div class="stat-value">${totalProducts}</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon">
                            <i class="fas fa-users"></i>
                        </div>
                        <div class="stat-title">Khách Hàng</div>
                        <div class="stat-value">${totalAccounts}</div>
                    </div>
                </div>

                <!-- Thông tin chi tiết doanh thu -->
                <div class="dashboard-stats mt-4">
                    <div class="stat-card">
                        <div class="stat-icon">
                            <i class="fas fa-receipt"></i>
                        </div>
                        <div class="stat-title">Tạm tính (trước thuế)</div>
                        <div class="stat-value">
                            <fmt:formatNumber value="${revenueSummary.subtotal}" type="number" maxFractionDigits="0" /> ₫
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon">
                            <i class="fas fa-percentage"></i>
                        </div>
                        <div class="stat-title">Thuế VAT (8%)</div>
                        <div class="stat-value">
                            <fmt:formatNumber value="${revenueSummary.totalTax}" type="number" maxFractionDigits="0" /> ₫
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon">
                            <i class="fas fa-tag"></i>
                        </div>
                        <div class="stat-title">Giảm giá Voucher</div>
                        <div class="stat-value">
                            <fmt:formatNumber value="${revenueSummary.totalDiscount}" type="number" maxFractionDigits="0" /> ₫
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon">
                            <i class="fas fa-chart-bar"></i>
                        </div>
                        <div class="stat-title">Doanh thu tháng này</div>
                        <div class="stat-value">
                            <fmt:formatNumber value="${revenueSummary.monthly}" type="number" maxFractionDigits="0" /> ₫
                        </div>
                    </div>
                </div>

                <!-- Nội dung chính -->
                <div class="dashboard-content">
                    <!-- Đơn hàng gần đây -->
                    <div class="content-card">
                        <div class="content-card-title">
                            <i class="fas fa-receipt"></i> Đơn Hàng Gần Đây
                        </div>
                        <c:choose>
                            <c:when test="${not empty recentOrders}">
                                <table class="orders-table">
                                    <thead>
                                        <tr>
                                            <th>Mã Đơn Hàng</th>
                                            <th>Khách Hàng</th>
                                            <th>Ngày</th>
                                            <th>Số Tiền</th>
                                            <th>Trạng Thái</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${recentOrders}" var="order">
                                            <tr>
                                                <td>#${order.orderId}</td>
                                                <td>${order.customerName}</td>
                                                <td>
                                                    <fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy" />
                                                </td>
                                                <td class="price">
                                                    <fmt:formatNumber value="${order.totalAmount}" type="number" maxFractionDigits="0" /> ₫
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${order.orderStatus eq 'Đang xử lý'}">
                                                            <span class="status status-processing">${order.orderStatus}</span>
                                                        </c:when>
                                                        <c:when test="${order.orderStatus eq 'Đang giao hàng'}">
                                                            <span class="status status-shipping">${order.orderStatus}</span>
                                                        </c:when>
                                                        <c:when test="${order.orderStatus eq 'Đã giao'}">
                                                            <span class="status status-delivered">${order.orderStatus}</span>
                                                        </c:when>
                                                        <c:when test="${order.orderStatus eq 'Đã hủy'}">
                                                            <span class="status status-canceled">${order.orderStatus}</span>
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
<!--                                <a href="${pageContext.request.contextPath}/admin/order" class="view-all">
                                    Xem tất cả đơn hàng <i class="fas fa-arrow-right"></i>
                                </a>-->
                            </c:when>
                            <c:otherwise>
                                <div class="empty-state">
                                    <i class="fas fa-receipt"></i>
                                    <p>Không có đơn hàng gần đây</p>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <!-- Biểu đồ trạng thái đơn hàng -->
                    <div class="content-card">
                        <div class="content-card-title">
                            <i class="fas fa-chart-pie"></i> Phân Bổ Trạng Thái Đơn Hàng
                        </div>
                        <div class="chart-container">
                            <c:choose>
                                <c:when test="${not empty orderStatusCounts}">
                                    <canvas id="orderStatusChart"></canvas>
                                    </c:when>
                                    <c:otherwise>
                                    <div class="empty-state">
                                        <i class="fas fa-chart-pie"></i>
                                        <p>Không có dữ liệu trạng thái đơn hàng</p>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </main>
        </div>

        <script>
            document.addEventListener('DOMContentLoaded', function () {
                // Toggle menu trên thiết bị di động
                const mobileMenuToggle = document.getElementById('mobile-menu-toggle');
                const sidebar = document.querySelector('.sidebar');

                if (mobileMenuToggle) {
                    mobileMenuToggle.addEventListener('click', function () {
                        sidebar.classList.toggle('show');
                    });
                }

                // Biểu đồ trạng thái đơn hàng
                const statusLabels = [];
                const statusData = [];

            <c:forEach items="${orderStatusCounts}" var="entry">
            statusLabels.push('<c:out value="${entry.key}" escapeXml="true" />');
            statusData.push(<c:out value="${entry.value}" />);
            </c:forEach>



                const statusColors = [
                    'rgba(212, 183, 143, 0.8)',
                    'rgba(139, 108, 66, 0.8)',
                    'rgba(195, 95, 95, 0.8)',
                    'rgba(106, 82, 50, 0.8)',
                    'rgba(184, 156, 112, 0.8)'
                ];

                const chartElement = document.getElementById('orderStatusChart');
                if (chartElement && statusData.length > 0) {
                    const statusCtx = chartElement.getContext('2d');

                    // Tính tổng để tính phần trăm
                    const total = statusData.reduce((a, b) => a + b, 0);

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
                                        },
                                        generateLabels: function (chart) {
                                            const data = chart.data;
                                            if (data.labels.length && data.datasets.length) {
                                                return data.labels.map((label, i) => {
                                                    const value = data.datasets[0].data[i];
                                                    const percentage = Math.round((value / total) * 100);
                                                    return {
                                                        text: label + ': ' + value + ' (' + percentage + '%)',
                                                        fillStyle: data.datasets[0].backgroundColor[i],
                                                        strokeStyle: data.datasets[0].backgroundColor[i],
                                                        lineWidth: 0,
                                                        hidden: false,
                                                        index: i
                                                    };
                                                });
                                            }
                                            return [];
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
                                            const percentage = Math.round((value / total) * 100);
                                            return label + ': ' + value + ' (' + percentage + '%)';
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
