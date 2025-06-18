<%-- 
    Document   : supplier-detail
    Created on : Jun 17, 2025, 9:53:18 PM
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
        <title>Supplier Details</title>
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
                            <h1 class="card-title">Supplier Details</h1>
                            <div class="d-flex gap-3">
                                <a href="${pageContext.request.contextPath}/admin/supplier" class="btn btn-secondary">
                                    <i class="fas fa-arrow-left"></i> Back to Suppliers
                                </a>
                                <a href="${pageContext.request.contextPath}/admin/supplier?view=edit&id=${supplier.supplierID}" class="btn btn-primary">
                                    <i class="fas fa-edit"></i> Edit Supplier
                                </a>
                            </div>
                        </div>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-4">
                                <div class="supplier-profile text-center">
                                    <div class="profile-image">
                                        <i class="fas fa-building fa-5x"></i>
                                    </div>
                                    <h3 class="mt-3">${supplier.companyName}</h3>
                                    <p class="text-muted">${supplier.brandName}</p>
                                    <div class="supplier-status">
                                        <span class="status-badge ${supplier.status == 1 ? 'status-active' : 'status-inactive'}">
                                            ${supplier.status == 1 ? 'Cooperating' : 'In Cooperating'}
                                        </span>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-8">
                                <div class="supplier-details">
                                    <h4>Supplier Information</h4>
                                    <table class="table table-striped">
                                        <tr>
                                            <th>Supplier ID:</th>
                                            <td>${supplier.supplierID}</td>
                                        </tr>
                                        <tr>
                                            <th>Brand Name:</th>
                                            <td>${supplier.brandName}</td>
                                        </tr>
                                        <tr>
                                            <th>Company Name:</th>
                                            <td>${supplier.companyName}</td>
                                        </tr>
                                        <tr>
                                            <th>Address:</th>
                                            <td>${supplier.address}</td>
                                        </tr>
                                        <tr>
                                            <th>Email:</th>
                                            <td>${supplier.email}</td>
                                        </tr>
                                        <tr>
                                            <th>Phone:</th>
                                            <td>${supplier.phone}</td>
                                        </tr>
                                        <tr>
                                            <th>Status:</th>
                                            <td>${supplier.status == 1 ? 'Cooperating' : 'In Cooperating'}</td>
                                        </tr>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>