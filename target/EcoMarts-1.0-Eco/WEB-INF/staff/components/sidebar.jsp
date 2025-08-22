<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<aside class="sidebar">
    <div class="logo">
        <a href="${pageContext.request.contextPath}/home"><img
                src="${pageContext.request.contextPath}/assets/img/eco.png" alt="Logo"></a>
    </div>
    <ul class="menu">
        <li><a href="${pageContext.request.contextPath}/staff/dashboard"><i class="fas fa-tachometer-alt"></i>
                Bảng điều khiển chính</a></li>
        <li><a href="${pageContext.request.contextPath}/staff/order"><i class="fas fa-receipt"></i> Quản lý đơn hàng</a>
        </li>
        <li><a href="${pageContext.request.contextPath}/staff/product"><i class="fas fa-box"></i> Quản lý sản phẩm</a></li>
        <li><a href="${pageContext.request.contextPath}/staff/manage-review"><i class="fas fa-receipt"></i> Quản lý phản hồi</a>
        </li>
        </li>
        <li><a href="${pageContext.request.contextPath}/ManageStaffServlet"><i class="fas fa-receipt"></i>Xem hồ sơ nhân viên</a>
        </li>
        </li>
        <li><a href="${pageContext.request.contextPath}/verifypasswordServlet"><i class="fas fa-receipt"></i>Đổi mật khẩu</a>
        </li>
        <li>
            <a href="${pageContext.request.contextPath}/staff/stockin">
                <i class="fas fa-box"></i> Quản lý nhập kho
            </a>
        </li>
        <li><a href="${pageContext.request.contextPath}/logout"><i
                    class="fas fa-sign-out-alt"></i> Đăng xuất</a></li>
    </ul>
</aside>
