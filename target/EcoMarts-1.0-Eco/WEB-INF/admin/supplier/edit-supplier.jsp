<%-- 
    Document   : edit-supplier
    Created on : Jun 17, 2025, 9:54:24 PM
    Author     : ADMIN
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Edit Supplier</title>
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/img/eco.png" type="image/x-icon">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis()%>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis()%>">
    </head>
    <body>
        <div class="container-fluid">
            <%-- Include admin sidebar --%>
            <jsp:include page="../components/sidebar.jsp" />
            <div class="main-content">
                <div class="container">
                    <h1>Update Supplier</h1>
                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger" role="alert">${errorMessage}</div>
                    </c:if>
                    <form method="POST" action="${pageContext.request.contextPath}/admin/supplier" class="needs-validation" novalidate>
                        <input type="hidden" name="action" value="edit">
                        <input type="hidden" name="id" value="${supplier.supplierID}">
                        <div class="mb-3">
                            <label class="form-label" for="brandName">Brand Name</label>
                            <input type="text" class="form-control" id="brandName" name="brandName" value="${supplier.brandName}" required>
                            <div class="invalid-feedback">Please enter a brand name</div>
                        </div>
                        <div class="mb-3">
                            <label class="form-label" for="companyName">Company Name</label>
                            <input type="text" class="form-control" id="companyName" name="companyName" value="${supplier.companyName}" required>
                            <div class="invalid-feedback">Please enter a company name</div>
                        </div>
                        <div class="mb-3">
                            <label class="form-label" for="address">Address</label>
                            <input type="text" class="form-control" id="address" name="address" value="${supplier.address}">
                        </div>
                        <div class="mb-3">
                            <label class="form-label" for="email">Email</label>
                            <input type="email" class="form-control" id="email" name="email" pattern="[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$" value="${supplier.email}" required>
                            <div class="invalid-feedback">Please enter a valid email address (e.g., example@domain.com)</div>
                        </div>
                        <div class="mb-3">
                            <label class="form-label" for="phone">Phone</label>
                            <input type="tel" class="form-control" id="phone" name="phone" pattern="[0-9]{10}" maxlength="10" value="${supplier.phone}" required>
                            <div class="invalid-feedback">Please enter a valid 10-digit phone number</div>
                        </div>
                        <div class="mb-3">
                            <label class="form-label" for="status">Status</label>
                            <select class="form-control" name="status" id="status" required>
                                <option value="1" ${supplier.status == 1 ? 'selected' : ''}>Cooperating</option>
                                <option value="0" ${supplier.status == 0 ? 'selected' : ''}>In Cooperating</option>
                            </select>
                            <div class="invalid-feedback">Please select a status</div>
                        </div>
                        <div class="btn-group">
                            <a href="${pageContext.request.contextPath}/admin/supplier" class="btn btn-secondary">Back</a>
                            <button type="submit" class="btn btn-primary">Update Supplier</button>
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
                    this.setCustomValidity('Please enter a valid email address (e.g., example@domain.com)');
                } else {
                    this.setCustomValidity('');
                }
            });
        </script>
    </body>
</html>