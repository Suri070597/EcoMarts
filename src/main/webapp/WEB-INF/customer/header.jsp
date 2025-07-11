<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<style>
    .suggestions-box {
        position: absolute;
        top: 100%;
        left: 0;
        right: 0;
        background: #ffffff;
        border-radius: 10px;
        box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
        width: 100%;
        max-height: 250px;
        overflow-y: auto;
        z-index: 1000;
        margin-top: 5px;
        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    }

    .suggestion-item {
        padding: 12px 16px;
        font-size: 15px;
        color: #333;
        border-bottom: 1px solid #f1f1f1;
        transition: background-color 0.2s ease, color 0.2s ease;
        cursor: pointer; /* Trỏ chuột kiểu pointer khi hover */
    }

    .suggestion-item:last-child {
        border-bottom: none;
    }

    .suggestion-item:hover {
        background-color: #eaf4ff;
        color: #007bff;
    }

</style>

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
                                <a href="ViewAllProductServlet?categoryId=${child.categoryID}">${child.categoryName}</a>
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
        <a href="home"><img src="assets/img/eco.png" alt="Logo"></a>
        <span>EcoMart</span>
    </div>

    <form action="SearchProduct" method="get" class="search-bar" autocomplete="off">
        <i class="fas fa-search"></i>
        <input type="text" id="searchInput" name="keyword" placeholder="Tìm kiếm sản phẩm..." required>
        <div id="suggestions" class="suggestions-box"></div>
    </form>



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
<script>
    document.getElementById("searchInput").addEventListener("input", function () {
        let keyword = this.value.trim();
        let suggestionBox = document.getElementById("suggestions");

        if (keyword.length === 0) {
            suggestionBox.innerHTML = "";
            return;
        }

        fetch("Suggest?keyword=" + encodeURIComponent(keyword))
                .then(res => res.json())
                .then(data => {
                    suggestionBox.innerHTML = "";
                    data.forEach(item => {
                        let div = document.createElement("div");
                        div.classList.add("suggestion-item");
                        div.textContent = item.name;
                        div.onclick = () => {
                            window.location.href = "${pageContext.request.contextPath}/SearchProduct?keyword=" + encodeURIComponent(item.name);
                        };
                        suggestionBox.appendChild(div);
                    });
                });
    });
</script>
