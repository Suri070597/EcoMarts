<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/img/eco.png" type="image/x-icon">
        <title>Manage Vouchers</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis()%>">
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis()%>">
    </head>

    <body>
        <div class="container-fluid">
            <%-- Include admin sidebar --%>
            <jsp:include page="../components/sidebar.jsp" />

            <div class="main-content">
                <div class="card">
                    <div class="card-header">
                        <div class="header-actions">
                            <h1 class="card-title">Voucher Management</h1>
                            <div class="d-flex gap-3">
                                <form action="${pageContext.request.contextPath}/admin/voucher" method="get"
                                      class="search-box">
                                    <i class="fas fa-search"></i>
                                    <input type="text" name="search" placeholder="Search vouchers..."
                                           value="${keyword != null ? keyword : ''}">
                                    <button type="submit" class="btn btn-sm btn-primary">Search</button>
                                </form>
                                <a href="${pageContext.request.contextPath}/admin/voucher?view=create"
                                   class="btn btn-success">
                                    <i class="fas fa-plus"></i>
                                    Create Voucher
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
                                    <th>Code</th>
                                    <th>Description</th>
                                    <th>Discount</th>
                                    <th>Min Order</th>
                                    <th>Usage</th>
                                    <th>Start Date</th>
                                    <th>End Date</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${vouchers}" var="v">
                                    <tr>
                                        <td>${v.voucherID}</td>
                                        <td>${v.voucherCode}</td>
                                        <td>${v.description}</td>
                                        <td><fmt:formatNumber value="${v.discountAmount}" type="number"/></td>
                                        <td><fmt:formatNumber value="${v.minOrderValue}" type="number"/></td>
                                        <td>${v.usageCount}/${v.maxUsage}</td>
                                        <td><fmt:formatDate value="${v.startDate}" pattern="yyyy-MM-dd"/></td>
                                        <td><fmt:formatDate value="${v.endDate}" pattern="yyyy-MM-dd"/></td>
                                        <td>
                                            <span class="status-badge ${v.active ? 'status-active' : 'status-inactive'}">
                                                ${v.active ? 'Active' : 'Inactive'}
                                            </span>
                                        </td>
                                        <td>
                                            <div class="d-flex gap-2">
                                                <a href='${pageContext.request.contextPath}/admin/voucher?view=detail&id=${v.voucherID}'
                                                   class='btn btn-sm btn-info'>
                                                    <i class="fas fa-eye"></i>
                                                </a>
                                                <a href='${pageContext.request.contextPath}/admin/voucher?view=edit&id=${v.voucherID}'
                                                   class='btn btn-sm btn-primary'>
                                                    <i class="fas fa-edit"></i>
                                                </a>
                                                <a href='javascript:void(0)'
                                                   onclick='confirmStatusChange("${pageContext.request.contextPath}/admin/voucher?action=status&id=${v.voucherID}&status=${v.active}", "${v.active}")'
                                                   class='btn btn-sm ${v.active ? "btn-warning" : "btn-success"}'>
                                                    <i class="fas ${v.active ? "fa-ban" : "fa-check"}"></i>
                                                </a>
                                                <a href='${pageContext.request.contextPath}/admin/voucher?action=delete&id=${v.voucherID}'
                                                   class='btn btn-sm btn-danger'
                                                   onclick="return confirmDelete(event, '${v.voucherID}')">
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

                                                       function confirmDelete(event, voucherId) {
                                                           event.preventDefault();
                                                           Swal.fire({
                                                               title: 'Confirm Delete Voucher',
                                                               text: 'Are you sure you want to delete this voucher?',
                                                               icon: 'warning',
                                                               showCancelButton: true,
                                                               confirmButtonColor: '#d33',
                                                               cancelButtonColor: '#3085d6',
                                                               confirmButtonText: 'Yes',
                                                               cancelButtonText: 'Cancel'
                                                           }).then((result) => {
                                                               if (result.isConfirmed) {
                                                                   window.location.href = '${pageContext.request.contextPath}/admin/voucher?action=delete&id=' + voucherId;
                                                               }
                                                           });
                                                           return false;
                                                       }
        </script>
        <script>
            function confirmStatusChange(url, active) {
                const isActive = String(active).trim().toLowerCase() === "true";

                Swal.fire({
                    title: 'Confirm Status Change',
                    text: isActive ? 'Do you want to deactivate this voucher?' : 'Do you want to activate this voucher?',
                    icon: 'question',
                    showCancelButton: true,
                    confirmButtonColor: '#3085d6',
                    cancelButtonColor: '#d33',
                    confirmButtonText: 'Yes',
                    cancelButtonText: 'Cancel'
                }).then((result) => {
                    if (result.isConfirmed) {
                        window.location.href = url;
                    }
                });
            }
        </script>

    </body>

</html>