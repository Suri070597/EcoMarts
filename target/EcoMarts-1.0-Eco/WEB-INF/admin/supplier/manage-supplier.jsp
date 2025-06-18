<%-- 
    Document   : manage-supplier
    Created on : Jun 17, 2025, 9:54:48 PM
    Author     : ADMIN
--%>

<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/img/eco.png" type="image/x-icon">
        <title>Manage Suppliers</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis()%>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis()%>">
    </head>
    <body>
        <div class="container-fluid">
            <%-- Include admin sidebar --%>
            <jsp:include page="../components/sidebar.jsp" />
            <div class="main-content">
                <div class="card">
                    <div class="card-header">
                        <div class="header-actions">
                            <h1 class="card-title">Suppliers</h1>
                            <div class="d-flex gap-3">
                                <form action="${pageContext.request.contextPath}/admin/supplier" method="get" class="search-box">
                                    <i class="fas fa-search"></i>
                                    <input type="text" name="search" placeholder="Search suppliers..." value="${keyword != null ? keyword : ''}">
                                    <button type="submit" class="btn btn-sm btn-primary">Search</button>
                                </form>
                                <a href="${pageContext.request.contextPath}/admin/supplier?view=create" class="btn btn-success">
                                    <i class="fas fa-plus"></i> Create Supplier
                                </a>
                            </div>
                        </div>
                    </div>
                    <div class="dashboard-stats">
                        <div class="stat-card">
                            <div class="stat-icon bg-primary">
                                <i class="fas fa-building"></i>
                            </div>
                            <div class="stat-details">
                                <h3>${totalSuppliers}</h3>
                                <p>Total Suppliers</p>
                            </div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-icon bg-success">
                                <i class="fas fa-check-circle"></i>
                            </div>
                            <div class="stat-details">
                                <h3>${activeSuppliers}</h3>
                                <p>In Cooperation Suppliers</p>
                            </div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-icon bg-danger">
                                <i class="fas fa-times-circle"></i>
                            </div>
                            <div class="stat-details">
                                <h3>${inactiveSuppliers}</h3>
                                <p>Cooperation Suppliers</p>
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
                                    <th>Brand Name</th>
                                    <th>Company Name</th>
                                    <th>Address</th>
                                    <th>Email</th>
                                    <th>Phone</th>
                                    <th>Status</th>
                                    <th>Actions</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${suppliers}" var="sup">
                                    <tr>
                                        <td>${sup.supplierID}</td>
                                        <td>${sup.brandName}</td>
                                        <td>${sup.companyName}</td>
                                        <td>${sup.address}</td>
                                        <td>${sup.email}</td>
                                        <td>${sup.phone}</td>
                                        <td>
                                            <span class="status-badge ${sup.status == 1 ? 'status-active' : 'status-inactive'}">
                                                ${sup.status == 1 ? 'Cooperating' : 'In Cooperation'}
                                            </span>
                                        </td>
                                        <td>
                                            <div class="d-flex gap-2">
                                                <a href="${pageContext.request.contextPath}/admin/supplier?view=detail&id=${sup.supplierID}" class="btn btn-sm btn-info">
                                                    <i class="fas fa-eye"></i>
                                                </a>
                                                <a href="${pageContext.request.contextPath}/admin/supplier?view=edit&id=${sup.supplierID}" class="btn btn-sm btn-primary">
                                                    <i class="fas fa-edit"></i>
                                                </a>
                                                <a href="javascript:void(0)" onclick="confirmStatusChange('${pageContext.request.contextPath}/admin/supplier?action=status&id=${sup.supplierID}&status=${sup.status}', ${sup.status})" class="btn btn-sm ${sup.status == 1 ? 'btn-warning' : 'btn-success'}">
                                                    <i class="fas ${sup.status == 1 ? 'fa-ban' : 'fa-check'}"></i>
                                                </a>
                                                <a href="${pageContext.request.contextPath}/admin/supplier?action=delete&id=${sup.supplierID}" class="btn btn-sm btn-danger" onclick="return confirmDelete(event, '${sup.supplierID}')">
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
                                                    function confirmStatusChange(url, status) {
                                                        const isActive = status == 1;
                                                        Swal.fire({
                                                            title: 'Confirm Status Change',
                                                            text: isActive ? 'Do you want to deactivate this supplier?' : 'Do you want to activate this supplier?',
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
                                                    document.querySelector('.search-box input').addEventListener('input', function (e) {
                                                        const searchText = e.target.value.toLowerCase();
                                                        const rows = document.querySelectorAll('tbody tr');
                                                        rows.forEach(row => {
                                                            const text = row.textContent.toLowerCase();
                                                            row.style.display = text.includes(searchText) ? '' : 'none';
                                                        });
                                                    });
                                                    function confirmDelete(event, supplierId) {
                                                        event.preventDefault();
                                                        Swal.fire({
                                                            title: 'Confirm Delete',
                                                            text: 'Are you sure you want to delete this supplier?',
                                                            icon: 'warning',
                                                            showCancelButton: true,
                                                            confirmButtonColor: '#d33',
                                                            cancelButtonColor: '#3085d6',
                                                            confirmButtonText: 'Yes',
                                                            cancelButtonText: 'Cancel'
                                                        }).then((result) => {
                                                            if (result.isConfirmed) {
                                                                window.location.href = '${pageContext.request.contextPath}/admin/supplier?action=delete&id=' + supplierId;
                                                            }
                                                        });
                                                        return false;
                                                    }
        </script>
    </body>
</html>