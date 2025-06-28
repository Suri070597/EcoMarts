<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/img/eco.png" type="image/x-icon">
    <title>Voucher Details</title>
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
                    <h1 class="card-title">Voucher Details</h1>
                    <div class="d-flex gap-3">
                        <a href="${pageContext.request.contextPath}/admin/voucher" class="btn btn-secondary">
                            <i class="fas fa-arrow-left"></i>
                            Back to Vouchers
                        </a>
                        <a href="${pageContext.request.contextPath}/admin/voucher?view=edit&id=${voucher.voucherID}"
                           class="btn btn-primary">
                            <i class="fas fa-edit"></i>
                            Edit Voucher
                        </a>
                    </div>
                </div>
            </div>

            <div class="card-body">
                <div class="row">
                    <div class="col-md-4">
                        <div class="voucher-profile text-center">
                            <div class="profile-image">
                                <i class="fas fa-ticket-alt fa-5x"></i>
                            </div>
                            <h3 class="mt-3">${voucher.voucherCode}</h3>
                            <p class="text-muted">${voucher.description}</p>
                            <div class="voucher-status">
                                <span
                                        class="status-badge ${voucher.active ? 'status-active' : 'status-inactive'}">
                                    ${voucher.active ? 'Active' : 'Inactive'}
                                </span>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-8">
                        <div class="voucher-details">
                            <h4>Voucher Information</h4>
                            <table class="table table-striped">
                                <tr>
                                    <th>Voucher ID:</th>
                                    <td>${voucher.voucherID}</td>
                                </tr>
                                <tr>
                                    <th>Voucher Code:</th>
                                    <td>${voucher.voucherCode}</td>
                                </tr>
                                <tr>
                                    <th>Description:</th>
                                    <td>${voucher.description}</td>
                                </tr>
                                <tr>
                                    <th>Discount Amount:</th>
                                    <td><fmt:formatNumber value="${voucher.discountAmount}" type="number"/></td>
                                </tr>
                                <tr>
                                    <th>Min Order Value:</th>
                                    <td><fmt:formatNumber value="${voucher.minOrderValue}" type="number"/></td>
                                </tr>
                                <tr>
                                    <th>Max Usage:</th>
                                    <td>${voucher.maxUsage}</td>
                                </tr>
                                <tr>
                                    <th>Usage Count:</th>
                                    <td>${voucher.usageCount}</td>
                                </tr>
                                <tr>
                                    <th>Start Date:</th>
                                    <td><fmt:formatDate value="${voucher.startDate}" pattern="yyyy-MM-dd"/></td>
                                </tr>
                                <tr>
                                    <th>End Date:</th>
                                    <td><fmt:formatDate value="${voucher.endDate}" pattern="yyyy-MM-dd"/></td>
                                </tr>
                                <tr>
                                    <th>Category ID:</th>
                                    <td>${voucher.categoryID != null ? voucher.categoryID : 'All Categories'}</td>
                                </tr>
                                <tr>
                                    <th>Status:</th>
                                    <td>${voucher.active ? 'Active' : 'Inactive'}</td>
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
