<%-- 
    Tài liệu   : create-manufacturer
    Tạo ngày   : 17/06/2025, 21:53:39
    Tác giả    : ADMIN
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Thêm Nhà Sản Xuất</title>
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/img/eco.png" type="image/x-icon">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis()%>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis()%>">
    </head>
    <body>
        <div class="container-fluid">
            <%-- Bao gồm thanh bên quản trị --%>
            <jsp:include page="../components/sidebar.jsp" />
            <div class="main-content">
                <div class="container">
                    <h1>Thêm Nhà Sản Xuất Mới</h1>
                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger" role="alert">${errorMessage}</div>
                    </c:if>
                    <form method="POST" action="${pageContext.request.contextPath}/admin/manufacturer" class="needs-validation" novalidate>
                        <input type="hidden" name="action" value="create">
                        <div class="mb-3">
                            <label class="form-label" for="brandName">Tên thương hiệu</label>
                            <input type="text" class="form-control" id="brandName" name="brandName" value="${param.brandName}" required>
                            <div class="invalid-feedback">Vui lòng nhập tên thương hiệu</div>
                        </div>
                        <div class="mb-3">
                            <label class="form-label" for="companyName">Tên công ty</label>
                            <input type="text" class="form-control" id="companyName" name="companyName" value="${param.companyName}" required>
                            <div class="invalid-feedback">Vui lòng nhập tên công ty</div>
                        </div>
                        <div class="mb-3">
                            <label class="form-label" for="address">Địa chỉ</label>
                            <input type="text" class="form-control" id="address" name="address" value="${param.address}">
                        </div>
                        <div class="mb-3">
                            <label class="form-label" for="email">Email</label>
                            <input type="email" class="form-control" id="email" name="email" pattern="[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$" value="${param.email}" required>
                            <div class="invalid-feedback">Vui lòng nhập địa chỉ email hợp lệ (ví dụ: example@domain.com)</div>
                        </div>
                        <div class="mb-3">
                            <label class="form-label" for="phone">Số điện thoại</label>
                            <input type="tel" class="form-control" id="phone" name="phone" pattern="[0-9]{10}" maxlength="10" value="${param.phone}" required>
                            <div class="invalid-feedback">Vui lòng nhập số điện thoại hợp lệ (10 chữ số)</div>
                        </div>
                        <div class="mb-3">
                            <label class="form-label" for="status">Trạng thái</label>
                            <select class="form-control" name="status" id="status" required>
                                <option value="1" ${param.status eq '1' ? 'selected' : ''}>Đang hợp tác</option>
                                <option value="0" ${param.status eq '0' ? 'selected' : ''}>Ngừng hợp tác</option>
                            </select>
                            <div class="invalid-feedback">Vui lòng chọn trạng thái</div>
                        </div>
                        <div class="btn-group">
                            <a href="${pageContext.request.contextPath}/admin/manufacturer" class="btn btn-secondary">Quay lại</a>
                            <button type="submit" class="btn btn-primary">Thêm nhà sản xuất</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            (function () {
                'use strict';
                var forms = document.querySelectorAll('.needs-validation');
                Array.prototype.slice.call(forms).forEach(function (form) {
                    form.addEventListener('submit', function (event) {
                        if (!form.checkValidity()) {
                            event.preventDefault();
                            event.stopPropagation();
                        }
                        form.classList.add('was-validated');
                    }, false);
                });
            })();
            document.getElementById('phone').addEventListener('input', function (e) {
                this.value = this.value.replace(/[^0-9]/g, '');
                if (this.value.length > 10) {
                    this.value = this.value.slice(0, 10);
                }
            });
            document.getElementById('email').addEventListener('input', function (e) {
                const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
                if (!emailPattern.test(this.value)) {
                    this.setCustomValidity('Vui lòng nhập địa chỉ email hợp lệ (ví dụ: example@domain.com)');
                } else {
                    this.setCustomValidity('');
                }
            });
        </script>
    </body>
</html>
