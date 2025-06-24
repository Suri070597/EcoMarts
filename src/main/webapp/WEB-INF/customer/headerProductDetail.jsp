<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!-- Header -->
<div class="header1">
    <a href="home" class="logo">
        <img src="assets/img/eco.png" alt="Logo">
        <span>EcoMart</span>
    </a>

    <div class="search-bar">
        <i class="fas fa-search"></i>
        <input type="text" placeholder="Tìm kiếm sản phẩm...">
    </div>

    <div class="header-icons">
        <% String username = (String) session.getAttribute("username"); %>
        <% if (username != null) {%>
        <span>Chào, <%= username%></span>
        <a href="<%= request.getContextPath()%>/logout"><i class="fas fa-sign-out-alt"></i>Đăng Xuất</a>

        <% } else {%>
        <a href="<%= request.getContextPath()%>/cart"><i class="fas fa-shopping-cart"></i>Giỏ hàng</a>
        <a href="<%= request.getContextPath()%>/login"><i class="fas fa-sign-in-alt"></i> Đăng nhập</a>
        <a href="<%= request.getContextPath()%>/register"><i class="fas fa-user-plus"></i> Đăng ký</a>
        <% }%>
    </div>
</div>
<!-- JS -->
<script>
    function toggleCategory(button) {
        const content = button.nextElementSibling;
        content.style.display = content.style.display === "block" ? "none" : "block";
    }
</script>