<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cập Nhật Nhân Viên</title>
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/img/eco.png" type="image/x-icon">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet"
        href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis() %>">
    <link rel="stylesheet"
        href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis() %>">
</head>

<body>
    <div class="container-fluid">
        <%-- Bao gồm sidebar quản trị --%>
        <jsp:include page="../components/sidebar.jsp" />

        <div class="main-content">
            <div class="container">
                <h1>Chỉnh Sửa Thông Tin Nhân Viên</h1>
                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger" role="alert">
                        ${errorMessage}
                    </div>
                </c:if>
                <form method="POST" action="${pageContext.request.contextPath}/admin/staff"
                    class="needs-validation" novalidate id="staffForm">
                    <input type="hidden" name="action" value="edit">
                    <input type="hidden" name="staffID" value="${staff.staffID}">
                    <input type="hidden" name="accountID" value="${staff.accountID}">
                    <input type="hidden" name="role" value="2">

                    <div class="mb-3">
                        <label class="form-label" for="username">Tên đăng nhập</label>
                        <input type="text" class="form-control" id="username" name="username"
                            value="${staff.account.username}" required>
                        <div class="invalid-feedback">Vui lòng nhập tên đăng nhập</div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label" for="password">Mật khẩu</label>
                        <input type="password" class="form-control" id="password" name="password"
                            value="${staff.account.password}" required>
                        <div class="invalid-feedback" id="passwordFeedback">
                            Mật khẩu phải có ít nhất 6 ký tự bao gồm chữ cái, số và ký tự đặc biệt
                        </div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label" for="email">Email</label>
                        <input type="email" class="form-control" id="email" name="email"
                            pattern="[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$"
                            value="${staff.email}" required>
                        <div class="invalid-feedback">Vui lòng nhập địa chỉ email hợp lệ (vd: abc12@gmail.com)</div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label" for="fullName">Họ và tên</label>
                        <input type="text" class="form-control" id="fullName" name="fullName"
                            value="${staff.fullName}" required>
                        <div class="invalid-feedback">Vui lòng nhập họ và tên</div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label" for="phone">Số điện thoại</label>
                        <input type="tel" class="form-control" id="phone" name="phone"
                            pattern="[0-9]{10}" maxlength="10" value="${staff.phone}" required>
                        <div class="invalid-feedback">Vui lòng nhập số điện thoại hợp lệ (10 chữ số)</div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label" for="address">Địa chỉ</label>
                        <input type="text" class="form-control" id="address" name="address"
                            value="${staff.address}" required>
                        <div class="invalid-feedback">Vui lòng nhập địa chỉ</div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label" for="gender">Giới tính</label>
                        <select class="form-control" name="gender" id="gender" required>
                            <option value="Nam" ${staff.gender eq 'Nam' ? 'selected' : '' }>Nam</option>
                            <option value="Nữ" ${staff.gender eq 'Nữ' ? 'selected' : '' }>Nữ</option>
                        </select>
                        <div class="invalid-feedback">Vui lòng chọn giới tính</div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label" for="status">Trạng thái</label>
                        <select class="form-control" name="status" id="status" required>
                            <option value="Active" ${staff.status eq 'Active' ? 'selected' : '' }>Hoạt động</option>
                            <option value="Inactive" ${staff.status eq 'Inactive' ? 'selected' : '' }>Ngừng hoạt động</option>
                        </select>
                        <div class="invalid-feedback">Vui lòng chọn trạng thái</div>
                    </div>

                    <div class="btn-group">
                        <a href="${pageContext.request.contextPath}/admin/staff" class="btn btn-secondary">Quay lại</a>
                        <button type="submit" class="btn btn-primary">Cập nhật</button>
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

        // Kiểm tra mật khẩu
        document.getElementById('password').addEventListener('input', function () {
            const password = this.value;
            const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{6,}$/;

            if (!passwordRegex.test(password)) {
                this.setCustomValidity('Mật khẩu phải có ít nhất 6 ký tự bao gồm chữ cái, số và ký tự đặc biệt');
                document.getElementById('passwordFeedback').textContent =
                    'Mật khẩu phải có ít nhất 6 ký tự bao gồm chữ cái, số và ký tự đặc biệt';
            } else {
                this.setCustomValidity('');
            }
        });

        // Kiểm tra trước khi gửi
        document.getElementById('staffForm').addEventListener('submit', function (event) {
            const passwordInput = document.getElementById('password');
            const password = passwordInput.value;
            const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{6,}$/;
            
            const oldPassword = '${staff.account.password}';

            if (oldPassword !== password && !passwordRegex.test(password)) {
                event.preventDefault();
                passwordInput.setCustomValidity('Mật khẩu phải có ít nhất 6 ký tự bao gồm chữ cái, số và ký tự đặc biệt');
                document.getElementById('passwordFeedback').textContent =
                    'Mật khẩu phải có ít nhất 6 ký tự bao gồm chữ cái, số và ký tự đặc biệt';
                passwordInput.classList.add('is-invalid');
            }
        });

        // Kiểm tra số điện thoại
        document.getElementById('phone').addEventListener('input', function (e) {
            this.value = this.value.replace(/[^0-9]/g, '');
            if (this.value.length > 10) {
                this.value = this.value.slice(0, 10);
            }
        });

        // Kiểm tra email
        document.getElementById('email').addEventListener('input', function (e) {
            const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
            if (!emailPattern.test(this.value)) {
                this.setCustomValidity('Vui lòng nhập địa chỉ email hợp lệ (vd: abc12@gmail.com)');
            } else {
                this.setCustomValidity('');
            }
        });
    </script>
</body>

</html>
