<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tạo khuyến mãi</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis()%>">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis()%>">
  </head>

  <body>
    <div class="container-fluid">
      <jsp:include page="../components/sidebar.jsp" />

      <div class="main-content">
        <div class="card">
          <div class="card-header">
            <div class="header-actions">
              <h1 class="card-title mb-0">Tạo khuyến mãi</h1>
              <div class="d-flex gap-2">
                <a href="${pageContext.request.contextPath}/admin/promotion" class="btn btn-outline-secondary">
                  <i class="fas fa-arrow-left"></i> Quay lại
                </a>
              </div>
            </div>
          </div>

          <div class="card-body">
<c:if test="${not empty errorMessage}">
  <ul>
    <c:forEach var="err" items="${errorMessage}">
      <li style="color:red">${err}</li>
    </c:forEach>
  </ul>
</c:if>


            <form id="createForm" method="post" action="${pageContext.request.contextPath}/admin/promotion">
              <input type="hidden" name="action" value="create"/>

              <div class="row g-3">
                <div class="col-md-6">
                  <label class="form-label">Tên khuyến mãi <span class="text-danger">*</span></label>
                  <input type="text" name="promotionName" class="form-control"
                         value="${promotion.promotionName}" required maxlength="100">
                </div>

                <div class="col-md-3">
                  <label class="form-label">Giảm giá (%) <span class="text-danger">*</span></label>
                  <input type="number" name="discountPercent" class="form-control"
                         value="${promotion.discountPercent}" min="0" max="100" step="0.01" required>
                </div>

                <div class="col-md-3">
                  <label class="form-label">Loại <span class="text-danger">*</span></label>
                  <select name="promoType" class="form-select" required>
                    <option value="0" <c:if test="${promotion != null && promotion.promoType == 0}">selected</c:if>>Flash Sale</option>
                    <option value="1" <c:if test="${promotion != null && promotion.promoType == 1}">selected</c:if>>Seasonal</option>
                  </select>
                </div>

                <div class="col-12">
                  <label class="form-label">Mô tả</label>
                  <textarea name="description" rows="3" class="form-control"
                            maxlength="255">${promotion.description}</textarea>
                </div>

                <div class="col-md-3">
                  <label class="form-label">Ngày bắt đầu <span class="text-danger">*</span></label>
                  <input type="date" name="startDate" class="form-control"
                         value="${promotion.startDate != null ? promotion.startDate.toLocalDateTime().toLocalDate() : ''}" required>
                </div>

                <div class="col-md-3">
                  <label class="form-label">Ngày kết thúc <span class="text-danger">*</span></label>
                  <input type="date" name="endDate" class="form-control"
                         value="${promotion.endDate != null ? promotion.endDate.toLocalDateTime().toLocalDate() : ''}" required>
                </div>

                <div class="col-md-3 d-flex align-items-end">
                  <div class="form-check">
                    <input class="form-check-input" type="checkbox" id="active" name="isActive"
                           <c:if test="${promotion == null || promotion.active}">checked</c:if>>
                    <label class="form-check-label" for="active">Kích hoạt ngay</label>
                  </div>
                </div>

                <!-- ÁP DỤNG -->
                <div class="col-12">
                  <label class="form-label d-block">Áp dụng <span class="text-danger">*</span></label>

                  <div class="form-check form-check-inline">
<!--                    <input class="form-check-input" type="radio" name="applyScope" id="scopeAll" value="0"
                           <c:if test="${promotion == null || promotion.applyScope == 0}">checked</c:if>>
                    <label class="form-check-label" for="scopeAll">Tất cả sản phẩm</label>-->
                  </div>

                  <div class="form-check form-check-inline">
                    <input class="form-check-input" type="radio" name="applyScope" id="scopeCategory" value="1"
                           <c:if test="${promotion != null && promotion.applyScope == 1}">checked</c:if>>
                    <label class="form-check-label" for="scopeCategory">Theo danh mục</label>
                  </div>
                </div>

                <!-- Chọn danh mục (ẩn/hiện theo radio) — single select, name=categoryID -->
                <div class="col-md-6" id="categoryPicker" style="display:none;">
                  <label class="form-label">Chọn danh mục</label>
                  <select name="categoryID" class="form-select">
                    <option value="">-- Chọn danh mục --</option>
                    <c:forEach var="c" items="${categories}">
                      <option value="${c.categoryID}"
                        <c:if test="${promotion != null && promotion.category != null && promotion.category.categoryID == c.categoryID}">selected</c:if>>
                        ${c.categoryName}
                      </option>
                    </c:forEach>
                  </select>
                  <!--<div class="form-text">Nếu chọn “Theo danh mục”, bắt buộc chọn 1 danh mục.</div>-->
                </div>

              </div>

              <hr class="my-4"/>

              <div class="d-flex gap-2">
                <button type="submit" class="btn btn-success">
                  <i class="fas fa-save"></i> Lưu khuyến mãi
                </button>
                <a href="${pageContext.request.contextPath}/admin/promotion" class="btn btn-outline-secondary">
                  Hủy
                </a>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
      // show/hide picker danh mục theo radio
      function toggleScope(){
        const cat = document.getElementById('scopeCategory').checked;
        document.getElementById('categoryPicker').style.display = cat ? '' : 'none';
      }
      document.querySelectorAll('input[name="applyScope"]').forEach(r => r.addEventListener('change', toggleScope));
      toggleScope(); // init

      // Validate: nếu theo danh mục -> bắt buộc chọn categoryID
      document.getElementById('createForm').addEventListener('submit', function(e){
        if (document.getElementById('scopeCategory').checked) {
          const sel = document.querySelector('select[name="categoryID"]');
          if (!sel || !sel.value) {
            e.preventDefault();
            alert('Vui lòng chọn 1 danh mục khi áp dụng theo danh mục.');
          }
        }
      });
    </script>
  </body>
</html>
