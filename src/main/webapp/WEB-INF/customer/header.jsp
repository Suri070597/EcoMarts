<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/cart-badge.css?version=<%= System.currentTimeMillis()%>">
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">

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

    .btn-admin {
        background-color: gainsboro;
        color: white;
        padding: 8px 15px;
        border-radius: 5px;
        margin: 0 10px;
        text-decoration: none;
        transition: background-color 0.2s;
    }

    .btn-admin:hover {
        background-color: #f1f1f1;
        color: white;
    }

    .btn-staff {
        background-color: gainsboro;
        color: white;
        padding: 8px 15px;
        border-radius: 5px;
        margin: 0 10px;
        text-decoration: none;
        transition: background-color 0.2s;
    }

    .btn-staff:hover {
        background-color: #f1f1f1;
        color: white;
    }
</style>

<!-- Sidebar -->
<div class="sidebar">
    <h2><i class="fas fa-list-ul"></i> Danh mục sản phẩm</h2>

    <c:choose>
        <c:when test="${empty categories}">
            <!-- Debug message if categories is empty -->
            <div style="color:red">Không có danh mục nào!</div>
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
                                <a href="${pageContext.request.contextPath}/ViewAllProductServlet?categoryId=${child.categoryID}">${child.categoryName}</a>
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
    <div class="logo" style="position: relative;">
        <a href="${pageContext.request.contextPath}/home">
            <img src="${pageContext.request.contextPath}/assets/img/eco.png" alt="Logo">
        </a>
        <span>EcoMart</span>

        <a class="logo-hit" href="${pageContext.request.contextPath}/home" aria-label="EcoMart"></a>
    </div>

    <style>
        .logo {
            position: relative;
        }
        .logo .logo-hit {
            position: absolute;
            inset: 0;
            display: block;
            text-indent: -9999px;
        }
        .logo {
            cursor: pointer;
        }
    </style>



    <form action="${pageContext.request.contextPath}/SearchProduct" method="get" class="search-bar" autocomplete="off">
        <i class="fas fa-search"></i>
        <input type="text" id="searchInput" name="keyword" placeholder="Tìm kiếm sản phẩm..." required>
        <div id="suggestions" class="suggestions-box"></div>
    </form>



    <div class="header-icons">
        <%
            model.Account account = (model.Account) session.getAttribute("account");
            Integer unreadCount = (Integer) request.getAttribute("unreadCount");
            if (unreadCount == null) {
                unreadCount = 0;
            }

            java.util.List<model.Review> unreadList
                    = (java.util.List<model.Review>) request.getAttribute("unreadList");

            int cartItemCount = 0;
            if (account != null && account.getRole() == 0) {
                dao.CartItemDAO cartItemDAO = new dao.CartItemDAO();
                cartItemCount = cartItemDAO.countCartItems(account.getAccountID(), "Active");
            }
        %>

        <% if (account != null && account.getRole() == 0) {%>




        <!-- Icon thông báo -->
        <div class="dropdown dropdown-notify">
            <a style="margin-right: 20px" href="#" class="notification-link dropdown-toggle"
               data-bs-toggle="dropdown" data-bs-auto-close="outside" aria-expanded="false">
                <i class="fas fa-bell"></i>
                <% if (unreadCount > 0) {%>
                <span class="badge-notification"><%= unreadCount%></span>
                <% }%>
            </a>

            <div class="dropdown-menu dropdown-menu-end dropdown-notify-menu p-0">
                <div class="notify-header d-flex justify-content-between align-items-center px-3 py-2">
                    <strong>Thông báo</strong>
                    <button type="button" class="btn btn-sm btn-link p-0 text-muted"
                            onclick="this.closest('.dropdown').classList.remove('show'); this.closest('.dropdown-menu').classList.remove('show');"
                            aria-label="Close">&times;</button>
                </div>

                <div class="notify-body">
                    <% if (account != null && account.getRole() == 0) { %>
                    <% if (unreadCount == 0 || unreadList == null || unreadList.size() == 0) { %>
                    <div class="text-center text-muted py-3">Không có thông báo mới.</div>
                    <% } else { %>
                    <ul class="list-group list-group-flush">
                        <% for (model.Review reply : unreadList) {%>
                        <li class="list-group-item notify-item">
                            <a class="stretched-link text-decoration-none text-reset"
                               href="<%= request.getContextPath()%>/read-notification?reviewId=<%= reply.getReviewID()%>"></a>
                            <div class="fw-semibold">Nhân viên: <%= reply.getAccountName()%></div>
                            <div>Sản phẩm: <%= reply.getProductName() != null ? reply.getProductName() : ""%></div>
                            <div>Phản hồi: <%= reply.getComment()%></div>
                            <div class="text-muted small"><%= reply.getCreatedAt()%></div>
                        </li>
                        <% }%>
                    </ul>
                    <form method="post" action="<%= request.getContextPath()%>/mark-notifications-read"
                          class="px-3 py-2 text-end">
                        <button type="submit" class="btn btn-sm btn-primary">Đã đọc tất cả</button>
                    </form>
                    <% } %>
                    <% } else { %>
                    <div class="text-center text-muted py-3">Vui lòng đăng nhập để xem thông báo.</div>
                    <% }%>
                </div>
            </div>
        </div>

        <!-- Icon giỏ hàng -->
        <div>
            <a style="margin-right: -23px;" href="<%= request.getContextPath()%>/cart">
                <i  class="fas fa-shopping-cart"></i>
                <% if (cartItemCount > 0) {%>
                <span class="badge bg-danger rounded-pill"><%= cartItemCount%></span>
                <% }%>
            </a>
        </div>

        <!-- Dropdown hồ sơ -->
        <div class="dropdown profile-dropdown">
            <button class="btn btn-link dropdown-toggle" type="button">
                <i class="fas fa-user-circle"></i><span><%= account.getFullName()%></span>
            </button>
            <ul class="dropdown-menu">
                <li><a class="dropdown-item" href="UpdateProfileServlet">Xem thông tin</a></li>
                <li><a class="dropdown-item" href="VerifyPasswordServlet">Đổi mật khẩu</a></li>
                <li><a class="dropdown-item" href="MyVoucherServlet">Voucher của tôi</a></li>
            </ul>
        </div>

        <!-- Logout -->
        <a href="<%= request.getContextPath()%>/logout"><i class="fas fa-sign-out-alt"></i> Đăng Xuất</a>

        <% } else if (account != null && account.getRole() == 1) {%>
        <!-- Admin user -->
        <span>Chào, <%= account.getFullName()%> (Admin)</span>

        <!-- Return to Admin -->
        <a href="<%= request.getContextPath()%>/admin" class="btn-admin">
            <i class="fas fa-user-shield"></i> Quay lại trang Admin
        </a>

        <!-- Logout -->
        <a href="<%= request.getContextPath()%>/logout"><i class="fas fa-sign-out-alt"></i> Đăng Xuất</a>

        <% } else if (account != null && account.getRole() == 2) {%>
        <!-- Staff user -->
        <span>Chào, <%= account.getFullName()%> (Nhân viên)</span>

        <!-- Return to Staff -->
        <a href="<%= request.getContextPath()%>/staff" class="btn-staff">
            <i class="fas fa-user-tie"></i> Quay lại trang Nhân viên
        </a>

        <!-- Logout -->
        <a href="<%= request.getContextPath()%>/logout"><i class="fas fa-sign-out-alt"></i> Đăng Xuất</a>

        <% } else if (account == null) {%>
        <!-- Chưa đăng nhập -->
        <a href="<%= request.getContextPath()%>/login"><i class="fas fa-sign-in-alt"></i> Đăng nhập</a>
        <a href="<%= request.getContextPath()%>/register"><i class="fas fa-user-plus"></i> Đăng ký</a>
        <% }%>
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

        fetch("${pageContext.request.contextPath}/Suggest?keyword=" + encodeURIComponent(keyword))
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
