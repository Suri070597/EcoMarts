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
        <title>Chi Tiết Tài Khoản</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis()%>">
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis()%>">
    </head>

    <body>
        <div class="container-fluid">
            <%-- Gồm thanh bên quản trị --%>
            <jsp:include page="../components/sidebar.jsp" />

            <div class="main-content">
                <div class="card">
                    <div class="card-header">
                        <div class="header-actions">
                            <h1 class="card-title">Chi Tiết Tài Khoản</h1>
                            <div class="d-flex gap-3">
                                <a href="${pageContext.request.contextPath}/admin/account"
                                   class="btn btn-secondary">
                                    <i class="fas fa-arrow-left"></i>
                                    Quay lại danh sách
                                </a>
                                <a href="${pageContext.request.contextPath}/admin/account?view=edit&id=${account.accountID}"
                                   class="btn btn-primary">
                                    <i class="fas fa-edit"></i>
                                    Chỉnh sửa
                                </a>
                            </div>
                        </div>
                    </div>

                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-4">
                                <div class="account-profile text-center">
                                    <div class="profile-image">
                                        <c:choose>
                                            <c:when test="${account.role == 0}">
                                                <i class="fas fa-user fa-5x"></i>
                                            </c:when>
                                            <c:when test="${account.role == 1}">
                                                <i class="fas fa-user-shield fa-5x"></i>
                                            </c:when>
                                            <c:otherwise>
                                                <i class="fas fa-user-tie fa-5x"></i>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <h3 class="mt-3">${account.fullName}</h3>
                                    <p class="text-muted">${account.username}</p>
                                    <div class="account-status">
                                        <span
                                            class="status-badge ${account.status eq 'Active' ? 'status-active' : 'status-inactive'}">
                                            ${account.status eq 'Active' ? 'Hoạt động' : 'Ngừng hoạt động'}
                                        </span>
                                    </div>
                                    <div class="account-role mt-2">
                                        <c:choose>
                                            <c:when test="${account.role == 0}">
                                                <span class="badge bg-info">Khách hàng</span>
                                            </c:when>
                                            <c:when test="${account.role == 1}">
                                                <span class="badge bg-warning">Quản trị viên</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-secondary">Nhân viên</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-8">
                                <div class="account-details">
                                    <h4>Thông Tin Tài Khoản</h4>
                                    <table class="table table-striped">
                                        <tr>
                                            <th>Mã tài khoản:</th>
                                            <td>${account.accountID}</td>
                                        </tr>
                                        <tr>
                                            <th>Tên đăng nhập:</th>
                                            <td>${account.username}</td>
                                        </tr>
                                        <tr>
                                            <th>Email:</th>
                                            <td>${account.email}</td>
                                        </tr>
                                        <tr>
                                            <th>Họ và tên:</th>
                                            <td>${account.fullName}</td>
                                        </tr>
                                        <tr>
                                            <th>Số điện thoại:</th>
                                            <td>${account.phone}</td>
                                        </tr>
                                        <tr>
                                            <th>Địa chỉ:</th>
                                            <td>${account.address}</td>
                                        </tr>
                                        <tr>
                                            <th>Giới tính:</th>
                                            <td>${account.gender}</td>
                                        </tr>
                                        <tr>
                                            <th>Vai trò:</th>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${account.role == 0}">Khách hàng</c:when>
                                                    <c:when test="${account.role == 1}">Quản trị viên</c:when>
                                                    <c:otherwise>Nhân viên</c:otherwise>
                                                </c:choose>
                                            </td>
                                        </tr>
                                        <tr>
                                            <th>Trạng thái:</th>
                                            <td>${account.status eq 'Active' ? 'Hoạt động' : 'Ngưng hoạt động'}</td>
                                        </tr>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>

</html>
