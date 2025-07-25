<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/img/eco.png"
          type="image/x-icon">
    <title>Manage Staff</title>
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
                        <h1 class="card-title">Staff Management</h1>
                        <div class="d-flex gap-3">
                            <form action="${pageContext.request.contextPath}/admin/staff" method="get"
                                  class="search-box">
                                <i class="fas fa-search"></i>
                                <input type="text" name="search" placeholder="Search staff..."
                                       value="${keyword != null ? keyword : ''}">
                            </form>
                            <a href="${pageContext.request.contextPath}/admin/staff?view=create"
                               class="btn btn-success">
                                <i class="fas fa-plus"></i>
                                Create Staff
                            </a>
                        </div>
                    </div>
                </div>

                <div class="dashboard-stats">
                    <div class="stat-card">
                        <div class="stat-icon bg-primary">
                            <i class="fas fa-users"></i>
                        </div>
                        <div class="stat-details">
                            <h3>${totalStaff}</h3>
                            <p>Total Staff</p>
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon bg-success">
                            <i class="fas fa-user-check"></i>
                        </div>
                        <div class="stat-details">
                            <h3>${activeStaffCount}</h3>
                            <p>Active Staff</p>
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon bg-warning">
                            <i class="fas fa-user-times"></i>
                        </div>
                        <div class="stat-details">
                            <h3>${inactiveStaffCount}</h3>
                            <p>Inactive Staff</p>
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
                                <th>Username</th>
                                <th>Full Name</th>
                                <th>Email</th>
                                <th>Phone</th>
                                <th>Gender</th>
                                <th>Status</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach items="${staffList}" var="staff">
                                <tr>
                                    <td>${staff.staffID}</td>
                                    <td>${staff.account.username}</td>
                                    <td>${staff.fullName}</td>
                                    <td>${staff.email}</td>
                                    <td>${staff.phone}</td>
                                    <td>${staff.gender}</td>
                                    <td>
                                        <span
                                            class="status-badge ${staff.status eq 'Active' ? 'status-active' : 'status-inactive'}">
                                            ${staff.status}
                                        </span>
                                    </td>
                                    <td>
                                        <div class="d-flex gap-2">
                                            <a href='${pageContext.request.contextPath}/admin/staff?view=detail&id=${staff.staffID}'
                                               class='btn btn-sm btn-info'>
                                                <i class="fas fa-eye"></i>
                                            </a>
                                            <a href='${pageContext.request.contextPath}/admin/staff?view=edit&id=${staff.staffID}'
                                               class='btn btn-sm btn-primary'>
                                                <i class="fas fa-edit"></i>
                                            </a>
                                            <a href='javascript:void(0)'
                                               data-staff-id="${staff.staffID}"
                                               data-status="${staff.status}"
                                               onclick='confirmStatusChange(this)'
                                               class='btn btn-sm ${staff.status eq "Active" ? "btn-warning" : "btn-success"}'>
                                                <i class="fas ${staff.status eq "Active" ? "fa-ban" : "fa-check"}"></i>
                                            </a>
                                            <a href='${pageContext.request.contextPath}/admin/staff?action=delete&id=${staff.staffID}'
                                               class='btn btn-sm btn-danger'
                                               onclick="return confirmDelete(event, '${staff.staffID}')">
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
        function confirmStatusChange(element) {
            const staffId = element.getAttribute('data-staff-id');
            const status = element.getAttribute('data-status');
            
            console.log('confirmStatusChange called with:', { staffId, status });
            
            // Check if staff ID is valid
            if (!staffId || staffId === 'undefined' || staffId.includes('undefined')) {
                console.error('Invalid staff ID:', staffId);
                Swal.fire({
                    title: 'Error',
                    text: 'Invalid staff ID. Please try again.',
                    icon: 'error',
                    confirmButtonColor: '#3085d6'
                });
                return;
            }
            
            // Chuáº©n hÃ³a tráº¡ng thÃ¡i (loáº¡i bá» khoáº£ng tráº¯ng, chuyá»n vá» chá»¯ thÆ°á»ng)
            const normalizedStatus = String(status).trim().toLowerCase();
            const isActive = normalizedStatus === "active";
            const newStatus = isActive ? "Inactive" : "Active";

            Swal.fire({
                title: 'Confirm Status Change',
                text: isActive
                        ? 'Do you want to deactivate this staff?'
                        : 'Do you want to activate this staff?',
                icon: 'question',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: 'Yes',
                cancelButtonText: 'Cancel'
            }).then((result) => {
                if (result.isConfirmed) {
                    const url = '${pageContext.request.contextPath}/admin/staff?action=status&id=' + staffId + '&status=' + status;
                    console.log('Redirecting to:', url);
                    window.location.href = url;
                }
            });
        }

        // Add search functionality for client-side filtering
        document.querySelector('.search-box input').addEventListener('input', function (e) {
            const searchText = e.target.value.toLowerCase();
            const rows = document.querySelectorAll('tbody tr');

            rows.forEach(row => {
                const text = row.textContent.toLowerCase();
                row.style.display = text.includes(searchText) ? '' : 'none';
            });
        });

        function confirmDelete(event, staffId) {
            event.preventDefault();
            // Get the correct URL from the anchor tag
            const deleteUrl = event.currentTarget.href;
            
            console.log('confirmDelete called with:', { staffId, deleteUrl });
            
            // Check if URL is valid
            if (!deleteUrl || deleteUrl === 'undefined' || deleteUrl.includes('undefined')) {
                console.error('Invalid delete URL:', deleteUrl);
                Swal.fire({
                    title: 'Error',
                    text: 'Invalid delete URL. Please try again.',
                    icon: 'error',
                    confirmButtonColor: '#3085d6'
                });
                return;
            }
            
            Swal.fire({
                title: 'Confirm Delete Staff',
                text: 'Are you sure you want to delete this staff?',
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#d33',
                cancelButtonColor: '#3085d6',
                confirmButtonText: 'Yes',
                cancelButtonText: 'Cancel'
            }).then((result) => {
                if (result.isConfirmed) {
                    console.log('Redirecting to delete URL:', deleteUrl);
                    window.location.href = deleteUrl;
                }
            });
        }
    </script>
</body>

</html> 