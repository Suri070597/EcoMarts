<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/cart-badge.css?version=<%= System.currentTimeMillis()%>">
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
        <%
            String username = (String) session.getAttribute("username");
            model.Account account = (model.Account) session.getAttribute("account");
            Integer unreadCount = (Integer) request.getAttribute("unreadCount");
            if (unreadCount == null) {
                unreadCount = 0;
            }
            java.util.List<model.Review> unreadList = (java.util.List<model.Review>) request.getAttribute("unreadList");
        %>
        <% if (account != null) {
                // Only show cart for customers (role = 0)
                if (account.getRole() == 0) {
                    util.CartUtil cartUtil = new util.CartUtil();
                    int cartItemCount = cartUtil.getCartItemCount(account.getAccountID());
        %>
        <span>Chào, <%= account.getFullName()%></span>
        <a href="<%= request.getContextPath()%>/cart">
            <i class="fas fa-shopping-cart"></i>Giỏ hàng
            <% if (cartItemCount > 0) {%>
            <span class="badge bg-danger rounded-pill"><%= cartItemCount%></span>
            <% }%>
        </a>
<% } else { %>
    <span>Chào, <%= account.getFullName() %></span>

    <!-- Icon thông báo -->
    <a href="#" class="notification-link" data-toggle="modal" data-target="#notificationModal">
        <i class="fas fa-bell"></i>
        <% if (unreadCount > 0) { %>
        <span class="badge-notification"><%= unreadCount %></span>
        <% } %>
    </a>

    <!-- Dropdown Hồ sơ -->
    <div class="dropdown">
        <button class="btn btn-link dropdown-toggle" type="button" data-bs-toggle="dropdown">
            Hồ sơ <i class="fas fa-user-circle"></i>
        </button>
        <ul class="dropdown-menu">
            <li><a class="dropdown-item" href="UpdateProfileServlet">Xem thông tin</a></li>
            <li><a class="dropdown-item" href="VerifyPasswordServlet">Đổi mật khẩu</a></li>
            <li><a class="dropdown-item" href="MyVoucherServlet">Voucher của tôi</a></li>
        </ul>
    </div>
<% } %>

        <a href="<%= request.getContextPath()%>/logout"><i class="fas fa-sign-out-alt"></i>Đăng Xuất</a>
        <% } else {%>
        <a href="<%= request.getContextPath()%>/login"><i class="fas fa-sign-in-alt"></i> Đăng nhập</a>
        <a href="<%= request.getContextPath()%>/register"><i class="fas fa-user-plus"></i> Đăng ký</a>
        <% }%>


    </div>
</div>
</div>
<!-- Modal notification cho customer đặt ở cuối file, không ảnh hưởng logic khác -->
<div class="modal fade" id="notificationModal" tabindex="-1" aria-labelledby="notificationModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="notificationModalLabel">Thông báo phản hồi từ nhân viên</h5>
                <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
            </div>
            <div class="modal-body">
                <% if (account != null && account.getRole() == 0) { %>
                <% if (unreadCount == 0 || unreadList == null || unreadList.size() == 0) { %>
                <div class="text-center text-muted">Không có thông báo mới.</div>
                <% } else { %>
                <ul class="list-group">
                    <% for (model.Review reply : unreadList) {%>
                    <a href="<%= request.getContextPath()%>/read-notification?reviewId=<%= reply.getReviewID()%>" style="text-decoration:none;color:inherit;">
                        <li class="list-group-item" style="cursor:pointer;">
                            <div><b>Nhân viên:</b> <%= reply.getAccountName()%></div>
                            <div><b>Sản phẩm:</b> <%= reply.getProductName() != null ? reply.getProductName() : ""%></div>
                            <div><b>Phản hồi:</b> <%= reply.getComment()%></div>
                            <div class="text-muted small"><%= reply.getCreatedAt()%></div>
                        </li>
                    </a>
                    <% }%>
                </ul>
                <form method="post" action="<%= request.getContextPath()%>/mark-notifications-read" class="mt-3 text-end">
                    <button type="submit" class="btn btn-sm btn-primary">Đã đọc tất cả</button>
                </form>
                <% } %>
                <% } else { %>
                <div class="text-center text-muted">Vui lòng đăng nhập bằng tài khoản khách hàng để xem thông báo.</div>
                <% }%>
            </div>
        </div>
    </div>
</div>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>
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
<!-- Bootstrap JS (cần cho dropdown hoạt động) -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
