<%-- 
    Tài liệu   : supplier-detail
    Tạo ngày   : 17/06/2025, 21:53:18
    Tác giả    : ADMIN
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/img/eco.png" type="image/x-icon">
        <title>Chi Tiết Nhà Sản Xuất</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis()%>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis()%>">
    </head>
    <body>
        <div class="container-fluid">
            <%-- Bao gồm thanh bên quản trị --%>
            <jsp:include page="../components/sidebar.jsp" />
            <div class="main-content">
                <div class="card">
                    <div class="card-header">
                        <div class="header-actions">
                            <h1 class="card-title">Chi Tiết Nhà Sản Xuất</h1>
                            <div class="d-flex gap-3">
                                <a href="${pageContext.request.contextPath}/admin/supplier" class="btn btn-secondary">
                                    <i class="fas fa-arrow-left"></i> Quay lại danh sách
                                </a>
                                <a href="${pageContext.request.contextPath}/admin/supplier?view=edit&id=${supplier.supplierID}" class="btn btn-primary">
                                    <i class="fas fa-edit"></i> Chỉnh sửa
                                </a>
                            </div>
                        </div>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-4">
                                <div class="supplier-profile text-center">
                                    <div class="profile-image">
                                        <i class="fas fa-building fa-5x"></i>
                                    </div>
                                    <h3 class="mt-3">${supplier.companyName}</h3>
                                    <p class="text-muted">${supplier.brandName}</p>
                                    <div class="supplier-status">
                                        <span class="status-badge ${supplier.status == 1 ? 'status-active' : 'status-inactive'}">
                                            ${supplier.status == 1 ? 'Đang hợp tác' : 'Ngừng hợp tác'}
                                        </span>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-8">
                                <div class="supplier-details">
                                    <h4>Thông Tin Nhà Sản Xuất</h4>
                                    <table class="table table-striped">
                                        <tr>
                                            <th>ID nhà sản xuất:</th>
                                            <td>${supplier.supplierID}</td>
                                        </tr>
                                        <tr>
                                            <th>Tên thương hiệu:</th>
                                            <td>${supplier.brandName}</td>
                                        </tr>
                                        <tr>
                                            <th>Tên công ty:</th>
                                            <td>${supplier.companyName}</td>
                                        </tr>
                                        <tr>
                                            <th>Địa chỉ:</th>
                                            <td>${supplier.address}</td>
                                        </tr>
                                        <tr>
                                            <th>Email:</th>
                                            <td>${supplier.email}</td>
                                        </tr>
                                        <tr>
                                            <th>Số điện thoại:</th>
                                            <td>${supplier.phone}</td>
                                        </tr>
                                        <tr>
                                            <th>Trạng thái:</th>
                                            <td>${supplier.status == 1 ? 'Đang hợp tác' : 'Ngừng hợp tác'}</td>
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
