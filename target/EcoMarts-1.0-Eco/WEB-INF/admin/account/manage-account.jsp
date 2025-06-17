<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/img/eco.png"
              type="image/x-icon">
        <title>Account Manager</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis()%>">
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis()%>">
    </head>

    <body>
        <div class="container-fluid">
            <%-- Include admin sidebar --%>
            <jsp:include page="../components/sidebar.jsp" />

            <div class="main-content">
                <div class="card">
                    <div class="card-header">
                        <div class="header-actions">
                            <h1 class="card-title">Account Management</h1>
                            <div class="d-flex gap-3">
                                <form action="${pageContext.request.contextPath}/admin/account" method="get"
                                      class="search-box">
                                    <i class="fas fa-search"></i>
                                    <input type="text" name="search" placeholder="Search accounts..."
                                           value="${keyword != null ? keyword : ''}">
                                    <button type="submit" class="btn btn-sm btn-primary">Search</button>
                                </form>
                                <a href="${pageContext.request.contextPath}/admin/account?view=create"
                                   class="btn btn-success">
                                    <i class="fas fa-plus"></i>
                                    Create Account
                                </a>
                            </div>
                        </div>
                    </div>

                    <div class="dashboard-stats">
                        <div class="stat-card">
                            <div class="stat-icon bg-primary">
                                <i class="fas fa-users"></i>
                            </div>
                            <div class="stat-details">
                                <h3>${totalAccounts}</h3>
                                <p>Total Accounts</p>
                            </div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-icon bg-success">
                                <i class="fas fa-user"></i>
                            </div>
                            <div class="stat-details">
                                <h3>${customerCount}</h3>
                                <p>Customers</p>
                            </div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-icon bg-warning">
                                <i class="fas fa-user-shield"></i>
                            </div>
                            <div class="stat-details">
                                <h3>${adminCount}</h3>
                                <p>Admins</p>
                            </div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-icon bg-info">
                                <i class="fas fa-user-tie"></i>
                            </div>
                            <div class="stat-details">
                                <h3>${staffCount}</h3>
                                <p>Staff</p>
                            </div>
                        </div>
                    </div>

                    <div class="table-container">
                        <c:if test="${not empty errorMessage}">
                            <div class="alert alert-danger">${errorMessage}</div>
                        </c:if>
                        <table class="table table-striped table-hover">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Username</th>
                                    <th>Full Name</th>
                                    <th>Email</th>
                                    <th>Phone</th>
                                    <th>Role</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${accounts}" var="acc">
                                    <tr>
                                        <td>${acc.accountID}</td>
                                        <td>${acc.username}</td>
                                        <td>${acc.fullName}</td>
                                        <td>${acc.email}</td>
                                        <td>${acc.phone}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${acc.role == 0}">
                                                    <span class="badge bg-info">Customer</span>
                                                </c:when>
                                                <c:when test="${acc.role == 1}">
                                                    <span class="badge bg-warning">Admin</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-secondary">Staff</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <span
                                                class="status-badge ${acc.status eq 'Active' ? 'status-active' : 'status-inactive'}">
                                                ${acc.status}
                                            </span>
                                        </td>
                                        <td>
                                            <div class="d-flex gap-2">
                                                <a href='${pageContext.request.contextPath}/admin/account?view=detail&id=${acc.accountID}'
                                                   class='btn btn-sm btn-info'>
                                                    <i class="fas fa-eye"></i>
                                                </a>
                                                <a href='${pageContext.request.contextPath}/admin/account?view=edit&id=${acc.accountID}'
                                                   class='btn btn-sm btn-primary'>
                                                    <i class="fas fa-edit"></i>
                                                </a>
                                                <a href='javascript:void(0)'
                                                   onclick='confirmStatusChange("${pageContext.request.contextPath}/admin/account?action=status&id=${acc.accountID}&status=${acc.status}", "${acc.status}")'
                                                   class='btn btn-sm ${acc.status eq "Active" ? "btn-warning" : "btn-success"}'>
                                                    <i class="fas ${acc.status eq "Active" ? "fa-ban" : "fa-check"}"></i>
                                                </a>
                                                <a href='${pageContext.request.contextPath}/admin/account?action=delete&id=${acc.accountID}'
                                                   class='btn btn-sm btn-danger'
                                                   onclick="return confirmDelete(event, '${acc.accountID}')">
                                                    <i class="fas fa-trash"></i>
                                                </a>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
        <script>
                                                       function confirmStatusChange(url, status) {
                                                           // Chuẩn hóa trạng thái (loại bỏ khoảng trắng, chuyển về chữ thường)
                                                           const normalizedStatus = String(status).trim().toLowerCase();
                                                           const isActive = normalizedStatus === "active";

                                                           Swal.fire({
                                                               title: 'Xác nhận thay đổi trạng thái',
                                                               text: isActive
                                                                       ? 'Bạn có muốn khóa tài khoản này không?'
                                                                       : 'Bạn có muốn kích hoạt tài khoản này không?',
                                                               icon: 'question',
                                                               showCancelButton: true,
                                                               confirmButtonColor: '#3085d6',
                                                               cancelButtonColor: '#d33',
                                                               confirmButtonText: 'Đồng ý',
                                                               cancelButtonText: 'Hủy'
                                                           }).then((result) => {
                                                               if (result.isConfirmed) {
                                                                   window.location.href = url;
                                                               }
                                                           });
                                                       }

                                                       // Add search functionality for client-side filtering
                                                       document.querySelector('.search-box input').addEventListener('input', function (e) {
                                                           const searchText = e.target.value.toLowerCase();
                                                           const rows = document.querySelectorAll('tbody tr');

                                                           rows.forEach(row => {
                                                               const text = row.textContent.toLowerCase();
                                                               row.style.display = text.includes(searchText) ? '' : 'none';
                                                           });
                                                       });

                                                       function confirmDelete(event, accountId) {
                                                           event.preventDefault();
                                                           Swal.fire({
                                                               title: 'Xác nhận xóa tài khoản',
                                                               text: "Bạn muốn xóa tài khoản này không?",
                                                               icon: 'warning',
                                                               showCancelButton: true,
                                                               confirmButtonColor: '#d33',
                                                               cancelButtonColor: '#3085d6',
                                                               confirmButtonText: 'Đồng ý',
                                                               cancelButtonText: 'Hủy'
                                                           }).then((result) => {
                                                               if (result.isConfirmed) {
                                                                   window.location.href = '${pageContext.request.contextPath}/admin/account?action=delete&id=' + accountId;
                                                               }
                                                           });
                                                           return false;
                                                       }
        </script>
    </body>

</html>