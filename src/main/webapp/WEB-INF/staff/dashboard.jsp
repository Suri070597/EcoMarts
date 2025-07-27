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
        <title>EcoMart - Bảng điều khiển Nhân viên</title>
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
                <h1 class="page-title">Bảng điều khiển Nhân viên</h1>

                <div class="date-indicator">
                    <i class="fas fa-calendar-alt"></i>
                    <span>Hôm nay: <fmt:formatDate value="<%= new java.util.Date()%>" pattern="EEEE, dd MMMM yyyy" /></span>
                </div>

                <!-- Statistics Cards -->
                <div class="dashboard-stats">
                    <div class="stat-card">
                        <div class="stat-icon">
                            <i class="fas fa-shopping-cart"></i>
                        </div>
                        <div class="stat-title">Đơn hàng hôm nay</div>
                        <div class="stat-value">${todayOrders}</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon">
                            <i class="fas fa-box"></i>
                        </div>
                        <div class="stat-title">Đang xử lý</div>
                        <div class="stat-value">${processingOrders}</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon">
                            <i class="fas fa-truck"></i>
                        </div>
                        <div class="stat-title">Đang giao hàng</div>
                        <div class="stat-value">${shippingOrders}</div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon">
                            <i class="fas fa-exchange-alt"></i>
                        </div>
                        <div class="stat-title">Hoàn trả/Đổi trả</div>
                        <div class="stat-value">${returnOrders}</div>
                    </div>
                </div>

                <!-- Work Shift Information -->
                <div class="work-shift-card">
                    <div class="work-shift-title">
                        <i class="fas fa-clock"></i>Ca làm việc hôm nay
                    </div>
                    <div class="work-shift-info">
                        <div class="shift-time">
                            <div class="shift-time-label">Giờ vào</div>
                            <div class="shift-time-value">${workShift.checkIn}</div>
                        </div>
                        <div class="shift-time">
                            <div class="shift-time-label">Giờ ra</div>
                            <div class="shift-time-value">${workShift.checkOut}</div>
                        </div>
                    </div>
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
                    });
                </script>
            </main>
        </div>
    </body>
</html>
