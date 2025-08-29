<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chỉnh sửa Khuyến mãi</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis()%>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis()%>">
        <style>
            .hint {
                font-size: 0.875rem;
                color: #6b7280;
            }
            .section-title {
                font-weight: 600;
                margin-bottom: .5rem;
            }
            .category-panel{
                border:1px solid #e5e7eb;
                border-radius:.5rem;
                padding:1rem;
                background:#fafafa;
            }
        </style>
    </head>

    <body>
        <div class="container-fluid">
            <jsp:include page="../components/sidebar.jsp" />

            <div class="main-content">
                <div class="card">
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <h1 class="card-title">Chỉnh sửa Khuyến mãi</h1>
                        <a href="${pageContext.request.contextPath}/admin/promotion" class="btn btn-secondary">
                            <i class="fas fa-arrow-left"></i> Quay lại danh sách
                        </a>
                    </div>

                    <div class="card-body">
                        <c:if test="${not empty errorMessage}">
                            <div class="alert alert-danger">${errorMessage}</div>
                        </c:if>

                        <form id="editForm" action="${pageContext.request.contextPath}/admin/promotion" method="post">
                            <input type="hidden" name="action" value="edit">
                            <input type="hidden" name="id" value="${promotion.promotionID}">

                            <!-- Tên / Mô tả -->
                            <div class="mb-3">
                                <label class="form-label">Tên khuyến mãi</label>
                                <input type="text" name="promotionName" class="form-control" required
                                       value="${promotion.promotionName}">
                            </div>

                            <div class="mb-3">
                                <label class="form-label">Mô tả</label>
                                <textarea name="description" class="form-control" rows="3">${promotion.description}</textarea>
                            </div>

                            <!-- Loại: Flash sale / Seasonal -->
                            <div class="mb-3">
                                <div class="section-title">Loại</div>
                                <div class="d-flex gap-4">
                                    <div class="form-check">
                                        <input class="form-check-input" type="radio" name="promoType" id="typeFlash"
                                               value="0" ${promotion.promoType == 0 ? 'checked' : ''}>
                                        <label class="form-check-label" for="typeFlash">Flash sale</label>
                                    </div>
                                    <div class="form-check">
                                        <input class="form-check-input" type="radio" name="promoType" id="typeSeasonal"
                                               value="1" ${promotion.promoType == 1 ? 'checked' : ''}>
                                        <label class="form-check-label" for="typeSeasonal">Seasonal</label>
                                    </div>
                                </div>
                            </div>

                            <!-- Giảm giá / Thời gian -->
                            <div class="row g-3">
                                <div class="col-md-4">
                                    <label class="form-label">Phần trăm giảm giá</label>
                                    <div class="input-group">
                                        <input type="number" name="discountPercent" step="0.01" min="0" max="100"
                                               class="form-control" required value="${promotion.discountPercent}">
                                        <span class="input-group-text">%</span>
                                    </div>
                                </div>
                                <div class="col-md-4">
                                    <label class="form-label">Ngày bắt đầu</label>
                                    <input type="date" name="startDate" class="form-control"
                                           value="<fmt:formatDate value='${promotion.startDate}' pattern='yyyy-MM-dd'/>">
                                </div>
                                <div class="col-md-4">
                                    <label class="form-label">Ngày kết thúc</label>
                                    <input type="date" name="endDate" class="form-control"
                                           value="<fmt:formatDate value='${promotion.endDate}' pattern='yyyy-MM-dd'/>">
                                </div>
                            </div>

                            <!-- Trạng thái -->
                            <div class="form-check my-3">
                                <input class="form-check-input" type="checkbox" name="isActive" id="isActive"
                                       ${promotion.active ? 'checked' : ''}>
                                <label class="form-check-label" for="isActive">Kích hoạt</label>
                            </div>

                            <!-- Áp dụng: all / category -->
                            <div class="mb-2 section-title">Áp dụng</div>
                            <div class="d-flex gap-4 mb-2">
                                <div class="a">
                                    <input style="display: none" class="form-check-input" type="radio" name="applyScope" id="scopeAll"
                                           value="0" ${promotion.applyScope == 0 ? 'checked' : ''}>
                                    <label class="form-check-label" for="scopeAll"></label>
                                </div>
                                <div class="form-check">
                                    <input class="form-check-input" type="radio" name="applyScope" id="scopeCategory"
                                           value="1" ${promotion.applyScope == 1 ? 'checked' : ''}>
                                    <label class="form-check-label" for="scopeCategory">Theo danh mục</label>
                                </div>
                            </div>
                            <div class="hint mb-3">
                                Nếu chọn “Theo danh mục”, hệ thống sẽ tự áp cho <b>mọi danh mục con</b> và tất cả sản phẩm thuộc chúng.
                            </div>

                            <!-- Chọn danh mục (single) -->
                            <div id="categoryBlock" class="category-panel mb-4" style="display:none;">
                                <div class="mb-2 fw-semibold">Chọn danh mục</div>

                                <c:if test="${empty categories}">
                                    <div class="text-muted">Không có danh mục nào.</div>
                                </c:if>

                                <select name="categoryID" class="form-select">
                                    <option value="">-- Chọn danh mục --</option>
                                    <c:forEach items="${categories}" var="c">
                                        <option value="${c.categoryID}"
                                                ${promotion.category != null && promotion.category.categoryID == c.categoryID ? 'selected' : ''}>
                                            ${c.categoryName}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>


                            <div class="d-flex gap-2">
                                <button type="submit" class="btn btn-primary">
                                    <i class="fas fa-save"></i> Lưu thay đổi
                                </button>
                                <a href="${pageContext.request.contextPath}/admin/promotion" class="btn btn-secondary">Hủy</a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <!-- libs -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

        <script>
            const scopeAll = document.getElementById('scopeAll');
            const scopeCategory = document.getElementById('scopeCategory');
            const categoryBlock = document.getElementById('categoryBlock');
            const form = document.getElementById('editForm');

            function toggleCategoryBlock() {
                categoryBlock.style.display = scopeCategory.checked ? 'block' : 'none';
            }
            scopeAll.addEventListener('change', toggleCategoryBlock);
            scopeCategory.addEventListener('change', toggleCategoryBlock);
            // init tại thời điểm load (theo giá trị applyScope hiện tại)
            toggleCategoryBlock();

            // Validate: nếu chọn "Theo danh mục" thì phải chọn ít nhất 1 danh mục
            form.addEventListener('submit', function (e) {
                if (scopeCategory.checked) {
                    const selectEl = document.querySelector('select[name="categoryID"]');
                    if (!selectEl.value) {  // chưa chọn gì
                        e.preventDefault();
                        alert('Vui lòng chọn 1 danh mục khi áp dụng theo danh mục.');
                    }
                }
            });

        </script>
    </body>
</html>
