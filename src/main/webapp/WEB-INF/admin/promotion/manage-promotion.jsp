<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản Lý Khuyến Mãi</title>
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
              <h1 class="card-title">Quản Lý Khuyến Mãi</h1>
              <div class="d-flex gap-3">
                <form action="${pageContext.request.contextPath}/admin/promotion" method="get" class="d-flex gap-2">
                  <div class="search-box">
                    <i class="fas fa-search"></i>
                    <input type="text" name="search" placeholder="Tìm kiếm theo tên/mô tả..."
                           value="${keyword}">
                  </div>

                  <!-- Filter Loại -->
                  <select name="type" class="form-select form-select-sm" style="width:auto;">
                    <option value="">Loại: Tất cả</option>
                    <option value="0" <c:if test="${param.type=='0' || type==0}">selected</c:if>>Flash sale</option>
                    <option value="1" <c:if test="${param.type=='1' || type==1}">selected</c:if>>Seasonal</option>
                  </select>

                  <!-- Filter Trạng thái -->
                  <select name="status" class="form-select form-select-sm" style="width:auto;">
                    <option value="">Trạng thái: Tất cả</option>
                    <option value="1" <c:if test="${param.status=='1' || status==1}">selected</c:if>>Active</option>
                    <option value="0" <c:if test="${param.status=='0' || status==0}">selected</c:if>>Inactive</option>
                  </select>

                  <button type="submit" class="btn btn-outline-secondary btn-sm">Lọc</button>
                </form>

                <a href="${pageContext.request.contextPath}/admin/promotion?view=create" class="btn btn-success">
                  <i class="fas fa-plus"></i> Tạo khuyến mãi
                </a>
              </div>
            </div>
          </div>

          <div class="table-container">
            <c:if test="${not empty errorMessage}">
              <div class="alert alert-danger">${errorMessage}</div>
            </c:if>
<div style="display:none">count=${promotions != null ? promotions.size() : 0}</div>

            <table class="table table-hover align-middle">
              <thead>
                <tr>
                  <th style="width:70px;">ID</th>
                  <th>Tên</th>
                  <th style="width:160px;">Loại</th>
                  <th style="width:120px;">Giảm giá</th>
                  <th style="width:180px;">Thời gian</th>
                  <th style="width:140px;">Còn lại</th>
                  <th style="width:140px;">Trạng thái</th>
                  <th style="width:160px;">Áp dụng</th>
                  <th style="width:180px;">Thao tác</th>
                </tr>
              </thead>
              <tbody>
                <c:forEach items="${promotions}" var="p">
                  <tr>
                    <td>${p.promotionID}</td>

                    <td>
                      <a href="${pageContext.request.contextPath}/admin/promotion?view=detail&id=${p.promotionID}"
                         class="text-decoration-none">${p.promotionName}</a>
                    </td>

                    <!-- Loại: FLASH SALE / SEASONAL -->
                    <td>
                      <c:choose>
                        <c:when test="${p.promoType == 1}">
                          <span class="badge rounded-pill" style="background:#e6f6ef;color:#047857;font-weight:600;">SEASONAL</span>
                        </c:when>
                        <c:otherwise>
                          <span class="badge rounded-pill" style="background:#fff1e6;color:#b45309;font-weight:600;">FLASH SALE</span>
                        </c:otherwise>
                      </c:choose>
                    </td>

                    <td><fmt:formatNumber value="${p.discountPercent}" maxFractionDigits="2"/>%</td>

                    <!-- Thời gian: 2 dòng -->
                    <td class="small">
                      <div><fmt:formatDate value="${p.startDate}" pattern="dd/MM/yyyy"/></div>
                      <div class="text-muted">→ <fmt:formatDate value="${p.endDate}" pattern="dd/MM/yyyy"/></div>
                    </td>

                    <!-- Còn lại (countdown) -->
                    <td>
                      <span class="countdown" data-end="${p.endDate.time}">Calculating...</span>
                    </td>

                    <!-- Trạng thái -->
                    <td>
                      <span class="status-badge ${p.active ? 'status-active' : 'status-inactive'}">
                        ${p.active ? 'Active' : 'Inactive'}
                      </span>
                    </td>

                    <!-- Áp dụng: Tất cả / Theo danh mục -->
<td>
  <c:choose>
    <c:when test="${p.applyScope == SCOPE_ALL}">
      <span class="badge rounded-pill" style="background:#eef2ff;color:#3730a3;">Tất cả</span>
    </c:when>
    <c:otherwise>
      <span class="badge rounded-pill" style="background:#f3f4f6;color:#374151;">
        <c:out value="${p.category != null ? p.category.categoryName : 'Danh mục'}"/>
      </span>
    </c:otherwise>
  </c:choose>
</td>


                    <!-- Thao tác -->
                    <td>
                      <div class="d-flex gap-2">
                        <a href='${pageContext.request.contextPath}/admin/promotion?view=edit&id=${p.promotionID}'
                           class='btn btn-sm btn-primary' title="Sửa">
                          <i class="fas fa-pen"></i>
                        </a>
                        <a href='javascript:void(0)'
                           onclick='confirmStatusChange("${pageContext.request.contextPath}/admin/promotion?action=status&id=${p.promotionID}&status=${p.active}", "${p.active}")'
                           class='btn btn-sm ${p.active ? "btn-warning" : "btn-success"}' title="${p.active ? 'Vô hiệu hoá' : 'Kích hoạt'}">
                          <i class="fas ${p.active ? "fa-ban" : "fa-check"}"></i>
                        </a>
                        <a href='${pageContext.request.contextPath}/admin/promotion?action=delete&id=${p.promotionID}'
                           class='btn btn-sm btn-danger' onclick="return confirmDelete(event, '${p.promotionID}')" title="Xoá">
                          <i class="fas fa-trash"></i>
                        </a>
                      </div>
                    </td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>

    <!-- libs -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

    <!-- Countdown + confirm -->
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
          title: 'Xác nhận xóa promotion',
          text: 'Bạn có muốn xóa promotion này không?',
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
