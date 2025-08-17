<%-- 
    Tài liệu   : manage-manufacturer
    Tạo ngày  : Jun 17, 2025, 9:54:48 PM
    Tác giả   : ADMIN
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
        <title>Quản Lý Nhà Sản Xuất</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis()%>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis()%>">
    </head>
    <body>
        <div class="container-fluid">
            <%-- Bao gồm sidebar quản trị --%>
            <jsp:include page="../components/sidebar.jsp" />
            <div class="main-content">
                <div class="card">
                    <div class="card-header">
                        <div class="header-actions">
                            <h1 class="card-title">Danh Sách Nhà Sản Xuất</h1>
                            <div class="d-flex gap-3">
                                <form action="${pageContext.request.contextPath}/admin/manufacturer" method="get" class="search-box">
                                    <i class="fas fa-search"></i>
                                    <input type="text" name="search" placeholder="Tìm kiếm nhà sản xuất..." value="${keyword != null ? keyword : ''}">
                                </form>
                                <a href="${pageContext.request.contextPath}/admin/manufacturer?view=create" class="btn btn-success">
                                    <i class="fas fa-plus"></i> Thêm nhà sản xuất
                                </a>
                            </div>
                        </div>
                    </div>
                    <div class="dashboard-stats">
                        <div class="stat-card">
                            <div class="stat-icon bg-primary">
                                <i class="fas fa-building"></i>
                            </div>
                            <div class="stat-details">
                                <h3>${totalManufacturers}</h3>
                                <p>Tổng số nhà sản xuất</p>
                            </div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-icon bg-success">
                                <i class="fas fa-check-circle"></i>
                            </div>
                            <div class="stat-details">
                                <h3>${activeManufacturers}</h3>
                                <p>Đang hợp tác</p>
                            </div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-icon bg-danger">
                                <i class="fas fa-times-circle"></i>
                            </div>
                            <div class="stat-details">
                                <h3>${inactiveManufacturers}</h3>
                                <p>Ngừng hợp tác</p>
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
                                    <th>Tên thương hiệu</th>
                                    <th>Tên công ty</th>
                                    <th>Địa chỉ</th>
                                    <th>Email</th>
                                    <th>Số điện thoại</th>
                                    <th>Trạng thái</th>
                                    <th>Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${manufacturers}" var="sup">
                                    <tr>
                                        <td>${sup.manufacturerID}</td>
                                        <td>${sup.brandName}</td>
                                        <td>${sup.companyName}</td>
                                        <td>${sup.address}</td>
                                        <td>${sup.email}</td>
                                        <td>${sup.phone}</td>
                                        <td>
                                            <span class="status-badge ${sup.status == 1 ? 'status-active' : 'status-inactive'}">
                                                ${sup.status == 1 ? 'Cooperation' : 'In Cooperation'}
                                            </span>
                                        </td>
                                        <td>
                                            <div class="d-flex gap-2">
                                                <a href="${pageContext.request.contextPath}/admin/manufacturer?view=detail&id=${sup.manufacturerID}" class="btn btn-sm btn-info" title="Xem chi tiết">
                                                    <i class="fas fa-eye"></i>
                                                </a>
                                                <a href="${pageContext.request.contextPath}/admin/manufacturer?view=edit&id=${sup.manufacturerID}" class="btn btn-sm btn-primary" title="Chỉnh sửa">
                                                    <i class="fas fa-edit"></i>
                                                </a>
                                                <a href="javascript:void(0)" onclick="confirmStatusChange('${pageContext.request.contextPath}/admin/manufacturer?action=status&id=${sup.manufacturerID}&status=${sup.status}', ${sup.status})" class="btn btn-sm ${sup.status == 1 ? 'btn-warning' : 'btn-success'}" title="Thay đổi trạng thái">
                                                    <i class="fas ${sup.status == 1 ? 'fa-ban' : 'fa-check'}"></i>
                                                </a>
                                                <a href="${pageContext.request.contextPath}/admin/manufacturer?action=delete&id=${sup.manufacturerID}" class="btn btn-sm btn-danger" onclick="return confirmDelete(event, '${sup.manufacturerID}')" title="Xóa">
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
                const isActive = status == 1;
                Swal.fire({
                    title: 'Xác nhận thay đổi trạng thái',
                    text: isActive ? 'Bạn có muốn ngừng hợp tác với nhà sản xuất này không?' : 'Bạn có muốn bắt đầu hợp tác lại với nhà sản xuất này không?',
                    icon: 'question',
                    showCancelButton: true,
                    confirmButtonColor: '#3085d6',
                    cancelButtonColor: '#d33',
                    confirmButtonText: 'Có',
                    cancelButtonText: 'Hủy'
                }).then((result) => {
                    if (result.isConfirmed) {
                        window.location.href = url;
                    }
                });
            }

            document.querySelector('.search-box input').addEventListener('input', function (e) {
                const searchText = e.target.value.toLowerCase();
                const rows = document.querySelectorAll('tbody tr');
                rows.forEach(row => {
                    const text = row.textContent.toLowerCase();
                    row.style.display = text.includes(searchText) ? '' : 'none';
                });
            });

            function confirmDelete(event, manufacturerId) {
                event.preventDefault();
                Swal.fire({
                    title: 'Xác nhận xóa nhà sản xuất',
                    text: 'Bạn có chắc chắn muốn xóa nhà sản xuất này không?',
                    icon: 'warning',
                    showCancelButton: true,
                    confirmButtonColor: '#d33',
                    cancelButtonColor: '#3085d6',
                    confirmButtonText: 'Có',
                    cancelButtonText: 'Hủy'
                }).then((result) => {
                    if (result.isConfirmed) {
                        window.location.href = '${pageContext.request.contextPath}/admin/manufacturer?action=delete&id=' + manufacturerId;
                    }
                });
                return false;
            }
        </script>
    </body>
</html>
