<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Thêm Khuyến Mãi</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis()%>">
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis()%>">
    </head>

    <body>
        <div class="container-fluid">
            <jsp:include page="../components/sidebar.jsp" />

            <div class="main-content">
                <div class="card">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <h1 class="card-title">Tạo Khuyến Mãi</h1>
                        <a href="${pageContext.request.contextPath}/admin/promotion" class="btn btn-secondary">
                            <i class="fas fa-arrow-left"></i> Quay lại danh sách
                        </a>
                    </div>

                    <div class="card-body">
                        <c:if test="${not empty errorMessage}">
                            <div class="alert alert-danger">${errorMessage}</div>
                        </c:if>

                        <form action="${pageContext.request.contextPath}/admin/promotion" method="post">
                            <input type="hidden" name="action" value="create">

                            <div class="mb-3">
                                <label class="form-label">Tên</label>
                                <input type="text" name="promotionName" class="form-control" required
                                       value="${promotion.promotionName}">
                            </div>

                            <div class="mb-3">
                                <label class="form-label">Mô tả</label>
                                <textarea name="description" class="form-control" rows="3">${promotion.description}</textarea>
                            </div>

                            <div class="mb-3">
                                <label class="form-label">Giảm giá %</label>
                                <input type="number" name="discountPercent" step="0.01" min="0" class="form-control" required
                                       value="${promotion.discountPercent}">
                            </div>

                            <div class="mb-3">
                                <label class="form-label">Ngày bắt đầu</label>
                                <input type="date" name="startDate" class="form-control"
                                       value="${promotion.startDate}">
                            </div>

                            <div class="mb-3">
                                <label class="form-label">Ngày kết thúc</label>
                                <input type="date" name="endDate" class="form-control"
                                       value="${promotion.endDate}">
                            </div>

                            <div class="form-check mb-3">
                                <input class="form-check-input" type="checkbox" name="isActive" id="isActive"
                                       ${promotion.active ? 'checked' : ''}>
                                <label class="form-check-label" for="isActive"> Hoạt động </label>
                            </div>

                            <button type="submit" class="btn btn-success">Tạo khuyến mãi</button>
                            <a href="${pageContext.request.contextPath}/admin/promotion" class="btn btn-secondary">Quay lại</a>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
