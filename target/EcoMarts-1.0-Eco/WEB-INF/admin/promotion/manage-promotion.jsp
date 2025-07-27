<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Promotion Manager</title>
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
                    <div class="card-header">
                        <div class="header-actions">
                            <h1 class="card-title">Promotion Management</h1>
                            <div class="d-flex gap-3">
                                <form action="${pageContext.request.contextPath}/admin/promotion" method="get"
                                      class="search-box">
                                    <i class="fas fa-search"></i>
                                    <input type="text" name="search" placeholder="Search promotions..."
                                           value="${keyword}">
                                    <button type="submit" class="btn btn-sm btn-primary">Search</button>
                                </form>
                                <a href="${pageContext.request.contextPath}/admin/promotion?view=create"
                                   class="btn btn-success">
                                    <i class="fas fa-plus"></i> Create Promotion
                                </a>
                            </div>
                        </div>
                    </div>

                    <div class="table-container">
                        <c:if test="${not empty errorMessage}">
                            <div class="alert alert-danger">${errorMessage}</div>
                        </c:if>

                        <table class="table table-striped table-hover">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Name</th>
                                    <th>Description</th>
                                    <th>Discount</th>
                                    <th>Start Date</th>
                                    <th>End Date</th>
                                    <th>Time left</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${promotions}" var="p">
                                    <tr>
                                        <td>${p.promotionID}</td>
                                        <td>${p.promotionName}</td>
                                        <td>${p.description}</td>
                                        <td><fmt:formatNumber value="${p.discountPercent}" type="number"/>%</td>
                                        <td><fmt:formatDate value="${p.startDate}" pattern="yyyy-MM-dd"/></td>
                                        <td><fmt:formatDate value="${p.endDate}" pattern="yyyy-MM-dd"/></td>
                                        <td>
                                            <p id="timer-${p.promotionID}">Calculating...</p>
                                            <script>
                                                (function () {
                                                    const countDownDate = new Date("${p.endDate}").getTime();
                                                    const timerId = "timer-${p.promotionID}";

                                                    const x = setInterval(function () {
                                                        const now = new Date().getTime();
                                                        const distance = countDownDate - now;

                                                        if (distance < 0) {
                                                            clearInterval(x);
                                                            document.getElementById(timerId).innerHTML = "EXPIRED";
                                                            return;
                                                        }

                                                        const days = Math.floor(distance / (1000 * 60 * 60 * 24));
                                                        const hours = Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
                                                        const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
                                                        const seconds = Math.floor((distance % (1000 * 60)) / 1000);

                                                        document.getElementById(timerId).innerHTML =
                                                                days + "d " + hours + "h " + minutes + "m " + seconds + "s ";
                                                    }, 1000);
                                                })();
                                            </script>
                                        </td>
                                        <td>
                                            <span class="status-badge ${p.active ? 'status-active' : 'status-inactive'}">
                                                ${p.active ? 'Active' : 'Inactive'}
                                            </span>
                                        </td>
                                        <td>
                                            <div class="d-flex gap-2">
                                                <a href='${pageContext.request.contextPath}/admin/promotion?view=detail&id=${p.promotionID}'
                                                   class='btn btn-sm btn-info'>
                                                    <i class="fas fa-eye"></i>
                                                </a>
                                                <a href='${pageContext.request.contextPath}/admin/promotion?view=edit&id=${p.promotionID}'
                                                   class='btn btn-sm btn-primary'>
                                                    <i class="fas fa-edit"></i>
                                                </a>
                                                <a href='javascript:void(0)'
                                                   onclick='confirmStatusChange("${pageContext.request.contextPath}/admin/promotion?action=status&id=${p.promotionID}&status=${p.active}", "${p.active}")'
                                                   class='btn btn-sm ${p.active ? "btn-warning" : "btn-success"}'>
                                                    <i class="fas ${p.active ? "fa-ban" : "fa-check"}"></i>
                                                </a>
                                                <a href='${pageContext.request.contextPath}/admin/promotion?action=delete&id=${p.promotionID}'
                                                   class='btn btn-sm btn-danger'
                                                   onclick="return confirmDelete(event, '${p.promotionID}')">
                                                    <i class="fas fa-trash"></i>
                                                </a>
                                                <a href='${pageContext.request.contextPath}/admin/promotion?view=assign-products&id=${p.promotionID}'
                                                   class='btn btn-sm btn-secondary'
                                                   title="Gán sản phẩm">
                                                    <i class="fas fa-box-open"></i>
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

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

        <script>
                                                       // Add search functionality for client-side filtering
                                                       document.querySelector('.search-box input').addEventListener('input', function (e) {
                                                           const searchText = e.target.value.toLowerCase();
                                                           const rows = document.querySelectorAll('tbody tr');

                                                           rows.forEach(row => {
                                                               const text = row.textContent.toLowerCase();
                                                               row.style.display = text.includes(searchText) ? '' : 'none';
                                                           });
                                                       });
                                                       function confirmStatusChange(url, active) {
                                                           const isActive = String(active).trim().toLowerCase() === "true";

                                                           Swal.fire({
                                                               title: 'Xác nhận thay đổi trạng thái',
                                                               text: isActive ? 'Bạn có muốn vô hiệu hóa promotion này không?' : 'Bạn có muốn kích hoạt promotion này không?',
                                                               icon: 'question',
                                                               showCancelButton: true,
                                                               confirmButtonColor: '#3085d6',
                                                               cancelButtonColor: '#d33',
                                                               confirmButtonText: 'Đồng ý',
                                                               cancelButtonText: 'Hủy'
                                                           }).then((result) => {
                                                               if (result.isConfirmed) {
                                                                   window.location.href = url;
                                                               }
                                                           });
                                                       }

                                                       function confirmDelete(event, promotionId) {
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
                                                           }).then((result) => {
                                                               if (result.isConfirmed) {
                                                                   window.location.href = '${pageContext.request.contextPath}/admin/promotion?action=delete&id=' + promotionId;
                                                               }
                                                           });
                                                           return false;
                                                       }
        </script>
    </body>
</html>