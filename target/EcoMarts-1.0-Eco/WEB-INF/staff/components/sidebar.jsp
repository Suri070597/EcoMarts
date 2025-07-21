<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<aside class="sidebar">
    <div class="logo">
        <a href="${pageContext.request.contextPath}/home"><img
                src="${pageContext.request.contextPath}/assets/img/eco.png" alt="Logo"></a>
    </div>
    <ul class="menu">
        <li><a href="${pageContext.request.contextPath}/staff/dashboard"><i class="fas fa-tachometer-alt"></i>
                Main dashboard</a></li>
        <li><a href="${pageContext.request.contextPath}/staff/order"><i class="fas fa-receipt"></i> Order Management</a>
        </li>
        <li><a href="${pageContext.request.contextPath}/staff/product"><i class="fas fa-box"></i> Product
                Management</a></li>
        <li><a href="${pageContext.request.contextPath}/staff/manage-review"><i class="fas fa-receipt"></i> Feedback Management</a>
        </li>
        <li><a href="${pageContext.request.contextPath}/logout"><i
                    class="fas fa-sign-out-alt"></i> Logout</a></li>
    </ul>
</aside>
