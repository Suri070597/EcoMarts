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
    <title>Chi Tiết Nhân Viên</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet"
        href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis()%>">
    <link rel="stylesheet"
        href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis()%>">
</head>

<body>
    <div class="container-fluid">
        <%-- Bao gồm sidebar quản trị viên --%>
        <jsp:include page="../components/sidebar.jsp" />

        <div class="main-content">
            <div class="card">
                <div class="card-header">
                    <div class="header-actions">
                        <h1 class="card-title">Chi Tiết Nhân Viên</h1>
                        <div class="d-flex gap-3">
                            <a href="${pageContext.request.contextPath}/admin/staff"
                                class="btn btn-secondary">
                                <i class="fas fa-arrow-left"></i>
                                Quay lại danh sách nhân viên
                            </a>
                            <a href="${pageContext.request.contextPath}/admin/staff?view=edit&id=${staff.staffID}"
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
                                    <i class="fas fa-user-tie fa-5x"></i>
                                </div>
                                <h3 class="mt-3">${staff.fullName}</h3>
                                <p class="text-muted">${staff.account.username}</p>
                                <div class="account-status">
                                    <span
                                        class="status-badge ${staff.status eq 'Active' ? 'status-active' : 'status-inactive'}">
                                        ${staff.status eq 'Active' ? 'Hoạt động' : 'Ngừng hoạt động'}
                                    </span>
                                </div>
                                <div class="account-role mt-2">
                                    <span class="badge bg-secondary">Nhân viên</span>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-8">
                            <div class="account-details">
                                <h4>Thông Tin Nhân Viên</h4>
                                <table class="table table-striped">
                                    <tr>
                                        <th>Mã nhân viên:</th>
                                        <td>${staff.staffID}</td>
                                    </tr>
                                    <tr>
                                        <th>Mã tài khoản:</th>
                                        <td>${staff.accountID}</td>
                                    </tr>
                                    <tr>
                                        <th>Tên đăng nhập:</th>
                                        <td>${staff.account.username}</td>
                                    </tr>
                                    <tr>
                                        <th>Email:</th>
                                        <td>${staff.email}</td>
                                    </tr>
                                    <tr>
                                        <th>Họ và tên:</th>
                                        <td>${staff.fullName}</td>
                                    </tr>
                                    <tr>
                                        <th>Số điện thoại:</th>
                                        <td>${staff.phone}</td>
                                    </tr>
                                    <tr>
                                        <th>Địa chỉ:</th>
                                        <td>${staff.address}</td>
                                    </tr>
                                    <tr>
                                        <th>Giới tính:</th>
                                        <td>${staff.gender}</td>
                                    </tr>
                                    <tr>
                                        <th>Vai trò:</th>
                                        <td>Nhân viên</td>
                                    </tr>
                                    <tr>
                                        <th>Trạng thái:</th>
                                        <td>${staff.status eq 'Active' ? 'Hoạt động' : 'Ngừng hoạt động'}</td>
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
