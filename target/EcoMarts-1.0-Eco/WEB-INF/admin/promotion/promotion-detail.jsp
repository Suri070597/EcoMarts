<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Promotion Detail</title>
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
                    <div class="card-header d-flex justify-content-between align-items-center">
                        <h1 class="card-title">Promotion Detail</h1>
                        <a href="${pageContext.request.contextPath}/admin/promotion" class="btn btn-secondary">
                            <i class="fas fa-arrow-left"></i> Back to List
                        </a>
                    </div>

                    <div class="card-body">
                        <table class="table table-bordered">
                            <tr>
                                <th>ID</th>
                                <td>${promotion.promotionID}</td>
                            </tr>
                            <tr>
                                <th>Name</th>
                                <td>${promotion.promotionName}</td>
                            </tr>
                            <tr>
                                <th>Description</th>
                                <td>${promotion.description}</td>
                            </tr>
                            <tr>
                                <th>Discount</th>
                                <td><fmt:formatNumber value="${promotion.discountPercent}" type="number"/>%</td>
                            </tr>
                            <tr>
                                <th>Start Date</th>
                                <td><fmt:formatDate value="${promotion.startDate}" pattern="yyyy-MM-dd"/></td>
                            </tr>
                            <tr>
                                <th>End Date</th>
                                <td><fmt:formatDate value="${promotion.endDate}" pattern="yyyy-MM-dd"/></td>
                            </tr>
                            <tr>
                                <th>Status</th>
                                <td>
                                    <span class="status-badge ${promotion.active ? 'status-active' : 'status-inactive'}">
                                        ${promotion.active ? 'Active' : 'Inactive'}
                                    </span>
                                </td>
                            </tr>
                        </table>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>