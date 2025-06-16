<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

        <!-- Sidebar -->
        <div class="sidebar">
            <h2><i class="fas fa-list-ul"></i> Danh mục sản phẩm</h2>

            <c:choose>
                <c:when test="${empty categories}">
                    <!-- Debug message if categories is empty -->
                    <div style="color:red">No categories available!</div>
                </c:when>
                <c:otherwise>
                    <c:forEach items="${categories}" var="category">
                        <div class="category">
                            <button onclick="toggleCategory(this)">
                                <c:choose>
                                    <c:when
                                        test="${category.categoryName.contains('nước') || category.categoryName.contains('Nước')}">
                                        <i class="fa fa-glass-water" style="color: #4a90e2;"></i> </c:when>
                                    <c:when test="${category.categoryName.contains('Sữa')}" ><i class="fa fa-bottle-water" style="color: #FFFFFF;"></i></c:when>
                                    <c:when test="${category.categoryName.contains('Trái cây')}"><i class="fa fa-apple-whole" style="color: #43D214;"></i> </c:when>
                                    <c:when test="${category.categoryName.contains('Bánh')}"><i class="fa fa-cookie" style="color: #f1c40f;"></i> </c:when>
                                    <c:when test="${category.categoryName.contains('Mẹ và bé')}"><i class="fa fa-baby" style="color: #e84393;"></i> </c:when>
                                    <c:when test="${category.categoryName.contains('Mỹ phẩm')}"><i class="fa fa-pump-soap" style="color: #9b59b6;"></i> </c:when>
                                    <c:otherwise><i class="fa fa-fire" style="color: #D25014;"></i> </c:otherwise>
                                </c:choose>
                                ${category.categoryName} <i class="fa fa-chevron-down"></i>
                            </button>
                            <div class="category-content">
                                <c:if test="${not empty category.children}">
                                    <c:forEach items="${category.children}" var="child">
                                        <a href="category?id=${child.categoryID}">${child.categoryName}</a>
                                    </c:forEach>
                                </c:if>
                            </div>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- Header -->
        <div class="header">
            <div class="logo">
                <img src="assets/img/eco.png" alt="Logo">
                <span>EcoMart</span>
            </div>

            <div class="search-bar">
                <i class="fas fa-search"></i>
                <input type="text" placeholder="Tìm kiếm sản phẩm...">
            </div>

            <div class="header-icons">
                <a href="#"><i class="fas fa-shopping-cart"></i> Giỏ hàng</a>
                <!--        <a href="#"><i class="fa-solid fa-bell"></i></a>-->
                <a href="#"><i class="fas fa-user-plus"></i> Đăng ký</a>
                <a href="#"><i class="fas fa-sign-in-alt"></i> Đăng nhập</a>
            </div>
        </div>

        <!-- JS -->
        <script>
            function toggleCategory(button) {
                const content = button.nextElementSibling;
                content.style.display = content.style.display === "block" ? "none" : "block";
            }
        </script>