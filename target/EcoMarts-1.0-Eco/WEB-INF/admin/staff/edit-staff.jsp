<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Staff Manager</title>
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/img/eco.png"
        type="image/x-icon">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet"
        href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis() %>">
    <link rel="stylesheet"
        href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis() %>">
</head>

<body>
    <div class="container-fluid">
        <%-- Include admin sidebar --%>
        <jsp:include page="../components/sidebar.jsp" />

        <div class="main-content">
            <div class="container">
                <h1>Edit Staff</h1>
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
                        <label class="form-label" for="username">Username</label>
                        <input type="text" class="form-control" id="username" name="username"
                            value="${staff.account.username}" required>
                        <div class="invalid-feedback">Please enter a username</div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label" for="password">Password</label>
                        <input type="password" class="form-control" id="password" name="password"
                            value="${staff.account.password}" required>
                        <div class="invalid-feedback" id="passwordFeedback">Password must be at least 8 characters and include letters, numbers, and special characters</div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label" for="email">Email</label>
                        <input type="email" class="form-control" id="email" name="email"
                            pattern="[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$"
                            value="${staff.email}" required>
                        <div class="invalid-feedback">Please enter a valid email address (e.g.,
                            abc12@gmail.com)</div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label" for="fullName">Full Name</label>
                        <input type="text" class="form-control" id="fullName" name="fullName"
                            value="${staff.fullName}" required>
                        <div class="invalid-feedback">Please enter a full name</div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label" for="phone">Phone</label>
                        <input type="tel" class="form-control" id="phone" name="phone"
                            pattern="[0-9]{10}" maxlength="10" value="${staff.phone}" required>
                        <div class="invalid-feedback">Please enter a valid 10-digit phone number</div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label" for="address">Address</label>
                        <input type="text" class="form-control" id="address" name="address"
                            value="${staff.address}" required>
                        <div class="invalid-feedback">Please enter an address</div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label" for="gender">Gender</label>
                        <select class="form-control" name="gender" id="gender" required>
                            <option value="">-- Select Gender --</option>
                            <option value="Nam" ${staff.gender eq 'Nam' ? 'selected' : '' }>Nam</option>
                            <option value="Nữ" ${staff.gender eq 'Nữ' ? 'selected' : '' }>Nữ</option>
                        </select>
                        <div class="invalid-feedback">Please select a gender</div>
                    </div>
                    <div class="mb-3">
                        <label class="form-label" for="status">Status</label>
                        <select class="form-control" name="status" id="status" required>
                            <option value="Active" ${staff.status eq 'Active' ? 'selected' : '' }>Active</option>
                            <option value="Inactive" ${staff.status eq 'Inactive' ? 'selected' : '' }>Inactive</option>
                        </select>
                        <div class="invalid-feedback">Please select a status</div>
                    </div>
                    <div class="btn-group">
                        <a href="${pageContext.request.contextPath}/admin/staff"
                            class="btn btn-secondary">Back</a>
                        <button type="submit" class="btn btn-primary">Update Staff</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // Form validation
        (function () {
            'use strict'
            var forms = document.querySelectorAll('.needs-validation')
            Array.prototype.slice.call(forms).forEach(function (form) {
                form.addEventListener('submit', function (event) {
                    if (!form.checkValidity()) {
                        event.preventDefault()
                        event.stopPropagation()
                    }
                    form.classList.add('was-validated')
                }, false)
            })
        })()

        // Password validation
        document.getElementById('password').addEventListener('input', function() {
            const password = this.value;
            const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/;
            
            if (!passwordRegex.test(password)) {
                this.setCustomValidity('Password must contain at least 8 characters, including letters, numbers, and special characters');
                document.getElementById('passwordFeedback').textContent = 
                    'Password must contain at least 8 characters, including letters, numbers, and special characters';
            } else {
                this.setCustomValidity('');
            }
        });

        // Custom form validation
        document.getElementById('staffForm').addEventListener('submit', function(event) {
            const passwordInput = document.getElementById('password');
            const password = passwordInput.value;
            const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/;
            
            if (!passwordRegex.test(password)) {
                event.preventDefault();
                passwordInput.setCustomValidity('Password must contain at least 8 characters, including letters, numbers, and special characters');
                document.getElementById('passwordFeedback').textContent = 
                    'Password must contain at least 8 characters, including letters, numbers, and special characters';
                passwordInput.classList.add('is-invalid');
            }
        });

        // Phone number validation
        document.getElementById('phone').addEventListener('input', function (e) {
            // Remove any non-digit characters
            this.value = this.value.replace(/[^0-9]/g, '');

            // Limit to 10 digits
            if (this.value.length > 10) {
                this.value = this.value.slice(0, 10);
            }
        });

        // Email validation
        document.getElementById('email').addEventListener('input', function (e) {
            const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
            if (!emailPattern.test(this.value)) {
                this.setCustomValidity('Please enter a valid email address (e.g., abc12@gmail.com)');
            } else {
                this.setCustomValidity('');
            }
        });
    </script>
</body>

</html> 