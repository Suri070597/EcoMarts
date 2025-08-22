<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<aside class="sidebar">
    <div class="logo">
        <a href="${pageContext.request.contextPath}/home"><img
                src="${pageContext.request.contextPath}/assets/img/eco.png" alt="Logo"></a>
    </div>
    <ul class="menu">
        <li><a href="${pageContext.request.contextPath}/admin/dashboard"><i class="fas fa-tachometer-alt"></i>
                Bảng điều khiển chính</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/statistic/monthly"><i
                    class="fas fa-chart-line"></i> Doanh thu theo tháng</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/statistic/yearly"><i class="fas fa-chart-bar"></i>
                Doanh thu theo năm</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/product"><i class="fas fa-box"></i> Quản lý sản phẩm</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/inventory"><i class="fas fa-box"></i> Quản lý nhập kho</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/account"><i class="fas fa-user"></i> Quản lý tài khoản</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/staff"><i class="fas fa-user-tie"></i>
                Quản lý nhân viên</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/manufacturer"><i class="fas fa-handshake"></i>
                Quản lý nhà sản xuất</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/category"><i class="fas fa-folder"></i>
                Quản lý danh mục</a></li>
        


        <li><a href="${pageContext.request.contextPath}/admin/voucher"><i class="fas fa-ticket-alt"></i>

                Quản lý voucher</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/promotion"><i class="fas fas fa-percent"></i>
                Quản lý khuyến mãi</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/dashboard?view=top-products"><i
                    class="fas fa-shopping-bag"></i> Top 10 sản phẩm</a></li>
        <li><a href="${pageContext.request.contextPath}/admin/dashboard?view=top-customers"><i
                    class="fas fa-users"></i> Top 5 khách hàng</a></li>
        <li><a href="${pageContext.request.contextPath}/logout"><i
                    class="fas fa-sign-out-alt"></i> Đăng xuất</a></li>
    </ul>
</aside>