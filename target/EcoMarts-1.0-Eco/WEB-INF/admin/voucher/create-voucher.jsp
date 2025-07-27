<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thêm Voucher</title>
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
    <%-- Include admin sidebar --%>
    <jsp:include page="../components/sidebar.jsp" />

    <div class="main-content">
        <div class="container">
            <h1>Tạo Voucher Mới</h1>
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger" role="alert">
                    ${errorMessage}
                </div>
            </c:if>
            <form method="POST" action="${pageContext.request.contextPath}/admin/voucher" class="needs-validation" novalidate>
                <input type="hidden" name="action" value="create">

                <div class="mb-3">
                    <label class="form-label" for="voucherCode">Mã voucher</label>
                    <input type="text" class="form-control" id="voucherCode" name="voucherCode" value="${param.voucherCode}" required>
                    <div class="invalid-feedback">Vui lòng nhập mã voucher</div>
                </div>

                <div class="mb-3">
                    <label class="form-label" for="description">Mô tả</label>
                    <input type="text" class="form-control" id="description" name="description" value="${param.description}" required>
                    <div class="invalid-feedback">Vui lòng nhập mô tả</div>
                </div>

                <div class="mb-3">
                    <label class="form-label" for="discountAmount">Số tiền giảm giá</label>
                    <input type="number" min='0' class="form-control" id="discountAmount" name="discountAmount" value="${param.discountAmount}" required>
                    <div class="invalid-feedback">Vui lòng nhập số tiền giảm giá</div>
                </div>

                <div class="mb-3">
                    <label class="form-label" for="minOrderValue">Giá trị đơn hàng tối thiểu</label>
                    <input type="number" min="0" class="form-control" id="minOrderValue" name="minOrderValue" value="${param.minOrderValue}" required>
                    <div class="invalid-feedback">Vui lòng nhập giá trị đơn hàng tối thiểu</div>
                </div>

                <div class="mb-3">
                    <label class="form-label" for="maxUsage">Số lần sử dụng tối đa</label>
                    <input type="number" min='0' class="form-control" id="maxUsage" name="maxUsage" value="${param.maxUsage}" required>
                    <div class="invalid-feedback">Vui lòng nhập số lần sử dụng tối đa</div>
                </div>

                <div class="mb-3">
                    <label class="form-label" for="usageCount">Số lần đã sử dụng</label>
                    <input type="number" min='0' class="form-control" id="usageCount" name="usageCount" value="${param.usageCount}" required>
                    <div class="invalid-feedback">Vui lòng nhập số lần đã sử dụng</div>
                </div>

                <div class="mb-3">
                    <label class="form-label" for="startDate">Ngày bắt đầu</label>
                    <input type="date" class="form-control" id="startDate" name="startDate" value="${param.startDate}" required>
                    <div class="invalid-feedback">Vui lòng chọn ngày bắt đầu</div>
                </div>

                <div class="mb-3">
                    <label class="form-label" for="endDate">Ngày kết thúc</label>
                    <input type="date" class="form-control" id="endDate" name="endDate" value="${param.endDate}" required>
                    <div class="invalid-feedback">Vui lòng chọn ngày kết thúc</div>
                </div>

                <div class="mb-3 form-check">
                    <input class="form-check-input" type="checkbox" id="isActive" name="isActive" ${param.isActive eq 'on' ? 'checked' : ''}>
                    <label class="form-check-label" for="isActive">Hoạt động</label>
                </div>

                <div class="mb-3">
                    <label class="form-label" for="categoryID">ID danh mục (tùy chọn)</label>
                    <input type="number" class="form-control" id="categoryID" min='1' name="categoryID" value="${param.categoryID}">
                </div>

                <div class="btn-group">
                    <a href="${pageContext.request.contextPath}/admin/voucher" class="btn btn-secondary">Quay lại</a>
                    <button type="submit" class="btn btn-primary">Tạo voucher</button>
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
