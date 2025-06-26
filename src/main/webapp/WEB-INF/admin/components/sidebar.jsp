<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<aside class="sidebar">
    <div class="logo">
        <a href="${pageContext.request.contextPath}/home"><img
                src="${pageContext.request.contextPath}/assets/img/eco.png" alt="Logo"></a>
    </div>
    <ul class="menu">
        <li><a href="${pageContext.request.contextPath}/admin/dashboard"><i class="fas fa-tachometer-alt"></i>
                Main dashboard</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/statistic/monthly"><i
                    class="fas fa-chart-line"></i> Revenue by month</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/statistic/yearly"><i class="fas fa-chart-bar"></i>
                Revenue by year</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/product"><i class="fas fa-box"></i> Product
                Management</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/account"><i class="fas fa-user"></i> Account
                Management</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/staff"><i class="fas fa-user-tie"></i>
                Staff Management</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/supplier"><i class="fas fa-handshake"></i>
                Supplier Management</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/category"><i class="fas fa-folder"></i>
                Category Management</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/voucher"><i class="fas fa-ticket-alt"></i>
                Voucher Management</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/promotion"><i class="fas fas fa-percent"></i>
                Promotion Management</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/dashboard?view=top-products"><i
                    class="fas fa-shopping-bag"></i> Top 10 product</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/dashboard?view=top-customers"><i
                    class="fas fa-users"></i> Top 5 customer</a></li>
        <li><a href="${pageContext.request.contextPath}/logout"><i
                    class="fas fa-sign-out-alt"></i> Logout</a></li>
    </ul>
</aside>