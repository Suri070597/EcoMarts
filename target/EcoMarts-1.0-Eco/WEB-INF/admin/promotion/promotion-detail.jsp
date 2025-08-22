<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi Tiết Khuyến Mãi</title>
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
          <div class="card-header d-flex justify-content-between align-items-center">
            <h1 class="card-title mb-0">Chi tiết Khuyến Mãi</h1>
            <div class="d-flex gap-2">
              <a href="${pageContext.request.contextPath}/admin/promotion?view=edit&id=${promotion.promotionID}" class="btn btn-primary">
                <i class="fas fa-edit"></i> Sửa
              </a>
              <a href="${pageContext.request.contextPath}/admin/promotion" class="btn btn-outline-secondary">
                <i class="fas fa-arrow-left"></i> Quay lại
              </a>
            </div>
          </div>

          <div class="card-body">
            <c:if test="${not empty errorMessage}">
              <div class="alert alert-danger">${errorMessage}</div>
            </c:if>

            <table class="table table-bordered align-middle">
              <tbody>
                <tr>
                  <th style="width:240px;">Mã khuyến mãi</th>
                  <td>${promotion.promotionID}</td>
                </tr>

                <tr>
                  <th>Tên khuyến mãi</th>
                  <td>${promotion.promotionName}</td>
                </tr>

                <tr>
                  <th>Mô tả</th>
                  <td><c:out value="${promotion.description}" /></td>
                </tr>

                <tr>
                  <th>Loại</th>
                  <td>
                    <c:choose>
                      <c:when test="${promotion.promoType == 1}">
                        <span class="badge rounded-pill" style="background:#e6f6ef;color:#047857;font-weight:600;">SEASONAL</span>
                      </c:when>
                      <c:otherwise>
                        <span class="badge rounded-pill" style="background:#fff1e6;color:#b45309;font-weight:600;">FLASH SALE</span>
                      </c:otherwise>
                    </c:choose>
                  </td>
                </tr>

                <tr>
                  <th>Giảm giá</th>
                  <td><fmt:formatNumber value="${promotion.discountPercent}" maxFractionDigits="2"/>%</td>
                </tr>

                <tr>
                  <th>Thời gian</th>
                  <td>
                    <div><fmt:formatDate value="${promotion.startDate}" pattern="dd/MM/yyyy"/></div>
                    <div class="text-muted small">→ <fmt:formatDate value="${promotion.endDate}" pattern="dd/MM/yyyy"/></div>
                  </td>
                </tr>

                <tr>
                  <th>Còn lại</th>
                  <td>
                    <span class="countdown" data-end="${promotion.endDate.time}">Calculating...</span>
                  </td>
                </tr>

                <tr>
                  <th>Trạng thái</th>
                  <td>
                    <span class="status-badge ${promotion.active ? 'status-active' : 'status-inactive'}">
                      ${promotion.active ? 'Active' : 'Inactive'}
                    </span>
                  </td>
                </tr>

                <tr>
                  <th>Áp dụng</th>
                  <td>
                    <c:choose>
                      <c:when test="${promotion.applyScope == 0}">
                        <span class="badge rounded-pill" style="background:#eef2ff;color:#3730a3;">Tất cả sản phẩm</span>
                      </c:when>
                      <c:otherwise>
                        <span class="badge rounded-pill" style="background:#f3f4f6;color:#374151;">Theo danh mục</span>
                        <c:if test="${appliedProductCount >= 0}">
                          <span class="text-muted ms-2">(${appliedProductCount} sản phẩm đang áp dụng)</span>
                        </c:if>
                      </c:otherwise>
                    </c:choose>
                  </td>
                </tr>
              </tbody>
            </table>

            <div class="mt-3 d-flex gap-2">
              <a href="${pageContext.request.contextPath}/admin/promotion?view=edit&id=${promotion.promotionID}" class="btn btn-primary">
                <i class="fas fa-pen"></i> Chỉnh sửa
              </a>
              <a href="javascript:void(0)"
                 onclick="confirmStatusChange('${pageContext.request.contextPath}/admin/promotion?action=status&id=${promotion.promotionID}&status=${promotion.active}', '${promotion.active}')"
                 class="btn ${promotion.active ? 'btn-warning' : 'btn-success'}">
                <i class="fas ${promotion.active ? 'fa-ban' : 'fa-check'}"></i>
                ${promotion.active ? 'Vô hiệu hóa' : 'Kích hoạt'}
              </a>
              <a href="${pageContext.request.contextPath}/admin/promotion?action=delete&id=${promotion.promotionID}"
                 class="btn btn-danger"
                 onclick="return confirmDelete(event, '${promotion.promotionID}')">
                <i class="fas fa-trash"></i> Xóa
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- libs -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

    <!-- countdown + confirm -->
    <script>
      function fmt(ms){
        if (ms <= 0) return 'ĐÃ HẾT HẠN';
        const s = Math.floor(ms/1000), d = Math.floor(s/86400),
              h = Math.floor((s%86400)/3600), m = Math.floor((s%3600)/60), x = s%60;
        return (d>0? d+'ng ':'') + h+'h ' + m+'m ' + x+'s';
      }
      function tick(){
        const now = Date.now();
        document.querySelectorAll('.countdown').forEach(el=>{
          const end = Number(el.dataset.end);
          const left = end - now;
          el.textContent = fmt(left);
          el.classList.toggle('text-warning', left>0 && left <= 24*3600*1000);
          el.classList.toggle('text-danger',  left>0 && left <=  2*3600*1000);
        });
      }
      tick(); setInterval(tick, 1000);

      function confirmStatusChange(url, active) {
        const isActive = String(active).trim().toLowerCase() === "true";
        Swal.fire({
          title: 'Xác nhận thay đổi trạng thái',
          text: isActive ? 'Bạn có muốn vô hiệu hóa khuyến mãi này không?' : 'Bạn có muốn kích hoạt khuyến mãi này không?',
          icon: 'question',
          showCancelButton: true,
          confirmButtonColor: '#3085d6',
          cancelButtonColor: '#d33',
          confirmButtonText: 'Đồng ý',
          cancelButtonText: 'Hủy'
        }).then((r) => { if (r.isConfirmed) window.location.href = url; });
      }

      function confirmDelete(event, id) {
        event.preventDefault();
        Swal.fire({
          title: 'Xác nhận xóa',
          text: 'Bạn có muốn xóa khuyến mãi này không?',
          icon: 'warning',
          showCancelButton: true,
          confirmButtonColor: '#d33',
          cancelButtonColor: '#3085d6',
          confirmButtonText: 'Đồng ý',
          cancelButtonText: 'Hủy'
        }).then((r) => { if (r.isConfirmed) window.location.href = '${pageContext.request.contextPath}/admin/promotion?action=delete&id=' + id; });
        return false;
      }
    </script>
  </body>
</html>
