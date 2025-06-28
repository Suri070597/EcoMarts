<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Manage Orders</title>
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/img/eco.png" type="image/x-icon">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
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
                        <div class="header-actions d-flex justify-content-between align-items-center">
                            <h1 class="card-title mb-0">Order Management</h1>
                            <form action="${pageContext.request.contextPath}/staff/order" method="get" class="d-flex gap-2">
                                <input type="text" name="search" class="form-control form-control-sm"
                                       placeholder="Search Orders..." value="${param.search}">
                                <button type="submit" class="btn btn-sm btn-primary">
                                    <i class="fas fa-search"></i>
                                </button>
                            </form>
                        </div>
                    </div>

                    <div class="dashboard-stats row m-2">
                        <div class="col-md-10">
                            <div class="stat-card d-flex align-items-center p-3 bg-light rounded">
                                <div class="stat-icon bg-primary text-white rounded-circle p-3 me-3">
                                    <i class="fas fa-clipboard-list"></i>
                                </div>
                                <div>
                                    <h5>${total}</h5>
                                    <p class="mb-0">Total Orders</p>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-10">
                            <div class="stat-card d-flex align-items-center p-3 bg-light rounded">
                                <div class="stat-icon bg-success text-white rounded-circle p-3 me-3">
                                    <i class="fas fa-truck"></i>
                                </div>
                                <div>
                                    <h5>${delivered}</h5>
                                    <p class="mb-0">Delivered Orders</p>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-10">
                            <div class="stat-card d-flex align-items-center p-3 bg-light rounded">
                                <div class="stat-icon bg-danger text-white rounded-circle p-3 me-3">
                                    <i class="fas fa-times-circle"></i>
                                </div>
                                <div>
                                    <h5>${cancelled}</h5>
                                    <p class="mb-0">Cancelled Orders</p>
                                </div>
                            </div>
                        </div>

                    </div>

                    <div class="table-responsive px-3 pb-3">
                        <table class="table table-striped table-hover align-middle">
                            <thead>
                                <tr>
                                    <th>Order ID</th>
                                    <th>Customer</th>
                                    <th>Order Date</th>
                                    <th>Status</th>
                                    <th>Total</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="o" items="${orders}">
                                    <tr>
                                        <td>${o.orderID}</td>
                                        <td>${o.accountName}</td>
                                        <td><fmt:formatDate value="${o.orderDate}" pattern="yyyy-MM-dd HH:mm" /></td>
                                        <td>
                                            <span class="badge ${o.orderStatus eq 'Delivered' ? 'bg-success' : 'bg-secondary'}">
                                                ${o.orderStatus}
                                            </span>
                                        </td>
                                        <td><fmt:formatNumber value="${o.totalAmount}" type="number" groupingUsed="true" /> VND</td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/staff/order/detail?id=${o.orderID}"
                                               class="btn btn-sm btn-info" title="View Details">
                                                <i class="fas fa-eye"></i>
                                            </a>
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
    </body>
</html>
