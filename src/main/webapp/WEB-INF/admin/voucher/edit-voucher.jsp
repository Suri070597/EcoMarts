<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Voucher Manager</title>
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/img/eco.png" type="image/x-icon">
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
            <h1>Cập Nhật Voucher</h1>
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger" role="alert">
                    ${errorMessage}
                </div>
            </c:if>
            <form method="POST" action="${pageContext.request.contextPath}/admin/voucher" class="needs-validation" novalidate>
                <input type="hidden" name="action" value="edit">
                <input type="hidden" name="id" value="${voucher.voucherID}">

                <div class="mb-3">
                    <label class="form-label" for="voucherCode">Voucher Code</label>
                    <input type="text" class="form-control" id="voucherCode" name="voucherCode" value="${voucher.voucherCode}" required>
                    <div class="invalid-feedback">Please enter a voucher code</div>
                </div>

                <div class="mb-3">
                    <label class="form-label" for="description">Description</label>
                    <input type="text" class="form-control" id="description" name="description" value="${voucher.description}" required>
                    <div class="invalid-feedback">Please enter a description</div>
                </div>

                <div class="mb-3">
                    <label class="form-label" for="discountAmount">Discount Amount</label>
                    <input type="number" step="0.01" class="form-control" id="discountAmount" name="discountAmount" value="${voucher.discountAmount}" required>
                    <div class="invalid-feedback">Please enter a discount amount</div>
                </div>

                <div class="mb-3">
                    <label class="form-label" for="minOrderValue">Min Order Value</label>
                    <input type="number" step="0.01" class="form-control" id="minOrderValue" name="minOrderValue" value="${voucher.minOrderValue}" required>
                    <div class="invalid-feedback">Please enter a minimum order value</div>
                </div>

                <div class="mb-3">
                    <label class="form-label" for="maxUsage">Max Usage</label>
                    <input type="number" class="form-control" id="maxUsage" name="maxUsage" value="${voucher.maxUsage}" required>
                    <div class="invalid-feedback">Please enter max usage count</div>
                </div>

                <div class="mb-3">
                    <label class="form-label" for="usageCount">Usage Count</label>
                    <input type="number" class="form-control" id="usageCount" name="usageCount" value="${voucher.usageCount}" required>
                    <div class="invalid-feedback">Please enter current usage count</div>
                </div>

                <div class="mb-3">
                    <label class="form-label" for="startDate">Start Date</label>
                    <input type="date" class="form-control" id="startDate" name="startDate" value="<fmt:formatDate value='${voucher.startDate}' pattern='yyyy-MM-dd'/>" required>
                    <div class="invalid-feedback">Please select a start date</div>
                </div>

                <div class="mb-3">
                    <label class="form-label" for="endDate">End Date</label>
                    <input type="date" class="form-control" id="endDate" name="endDate" value="<fmt:formatDate value='${voucher.endDate}' pattern='yyyy-MM-dd'/>" required>
                    <div class="invalid-feedback">Please select an end date</div>
                </div>

                <div class="mb-3 form-check">
                    <input class="form-check-input" type="checkbox" id="isActive" name="isActive" ${voucher.active ? 'checked' : ''}>
                    <label class="form-check-label" for="isActive">Active</label>
                </div>

                <div class="mb-3">
                    <label class="form-label" for="categoryID">Category ID (optional)</label>
                    <input type="number" class="form-control" id="categoryID" name="categoryID" value="${voucher.categoryID}">
                </div>

                <div class="btn-group">
                    <a href="${pageContext.request.contextPath}/admin/voucher" class="btn btn-secondary">Back</a>
                    <button type="submit" class="btn btn-primary">Update Voucher</button>
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
</script>

</body>

</html>
