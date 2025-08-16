<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Quản lý đơn hàng</title>
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/img/eco.png" type="image/x-icon">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis()%>">
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis()%>">
        <style>
            .table td.action {
                text-align: left;
            }
            
            .table td.price {
                text-align: right;
            }
        </style>
    </head>
    <body>
        <div class="container-fluid">
            <jsp:include page="../components/sidebar.jsp" />

            <div class="main-content">
                <div class="card">
                    <div class="card-header">
                        <div class="header-actions d-flex justify-content-between align-items-center">
                            <h1 class="card-title mb-0">Quản lý đơn hàng</h1>
                            <form action="${pageContext.request.contextPath}/staff/order" method="get" class="d-flex gap-2">
                                <input type="text" name="search" class="form-control form-control-sm"
                                       placeholder="Tìm kiếm đơn hàng..." value="${param.search}">
                                <button type="submit" class="btn btn-sm btn-primary">
                                    <i class="fas fa-search"></i>
                                </button>
                            </form>
                        </div>
                    </div>

                    <div class="dashboard-stats row m-2">
                        <div class="col-md-10">
                            <a href="${pageContext.request.contextPath}/staff/order" class="text-decoration-none text-reset">
                                <div class="stat-card d-flex align-items-center p-3 bg-light rounded">
                                    <div class="stat-icon bg-primary text-white rounded-circle p-3 me-3">
                                        <i class="fas fa-clipboard-list"></i>
                                    </div>
                                    <div>
                                        <h5>${total}</h5>
                                        <p class="mb-0">Tổng đơn hàng</p>
                                    </div>
                                </div>
                            </a>
                        </div>
                        <div class="col-md-10">
                            <a href="${pageContext.request.contextPath}/staff/order?status=Đã giao" class="text-decoration-none text-reset">
                                <div class="stat-card d-flex align-items-center p-3 bg-light rounded">
                                <div class="stat-icon bg-success text-white rounded-circle p-3 me-3">
                                    <i class="fas fa-truck"></i>
                                </div>
                                <div>
                                    <h5>${delivered}</h5>
                                    <p class="mb-0">Đơn hàng đã giao</p>
                                </div>
                                </div>
                            </a>
                        </div>
                        <div class="col-md-10">
                            <a href="${pageContext.request.contextPath}/staff/order?status=Đã hủy" class="text-decoration-none text-reset">
                                <div class="stat-card d-flex align-items-center p-3 bg-light rounded">
                                <div class="stat-icon bg-danger text-white rounded-circle p-3 me-3">
                                    <i class="fas fa-times-circle"></i>
                                </div>
                                <div>
                                    <h5>${cancelled}</h5>
                                    <p class="mb-0">Đơn hàng đã hủy</p>
                                </div>
                                </div>
                            </a>
                        </div>

                    </div>

                    <div class="table-responsive px-3 pb-3">
                        <table class="table table-striped table-hover align-middle">
                            <thead>
                                <tr>
                                    <th>Mã đơn hàng</th>
                                    <th>Khách hàng</th>
                                    <th>Ngày đặt hàng</th>
                                    <th>Trạng thái</th>
                                    <th>Tổng tiền</th>
                                    <th>Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="o" items="${orders}">
                                    <tr>
                                        <td>${o.orderID}</td>
                                        <td>${o.accountName}</td>
                                        <td><fmt:formatDate value="${o.orderDate}" pattern="yyyy-MM-dd HH:mm" /></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${o.orderStatus == 'Đang xử lý'}">
                                                    <span class="badge bg-warning text-dark">${o.orderStatus}</span>
                                                </c:when>
                                                <c:when test="${o.orderStatus == 'Đang giao hàng' || o.orderStatus == 'Đang giao'}">
                                                    <span class="badge bg-info text-dark">${o.orderStatus}</span>
                                                </c:when>
                                                <c:when test="${o.orderStatus == 'Đã giao'}">
                                                    <span class="badge bg-success">${o.orderStatus}</span>
                                                </c:when>
                                                <c:when test="${o.orderStatus == 'Đã hủy'}">
                                                    <span class="badge bg-danger">${o.orderStatus}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-secondary">${o.orderStatus}</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="price">
                                            <c:choose>
                                                <c:when test="${not empty o.grandTotal}">
                                                    <fmt:formatNumber value="${o.grandTotal}" type="number" groupingUsed="true" /> đ
                                                </c:when>
                                                <c:otherwise>
                                                    <%
                                                        dao.OrderDAO _orderDAO = new dao.OrderDAO();
                                                        model.Order _curr = (model.Order) pageContext.findAttribute("o");
                                                        double _totalAfterDiscount = _curr != null ? _curr.getTotalAmount() : 0.0;
                                                        double _discount = _orderDAO.getDiscountAmountByOrderID(_curr.getOrderID()).doubleValue();
                                                        double _subtotal = _totalAfterDiscount + _discount;
                                                        double _vat = _subtotal * 0.08;
                                                        double _grandTotal = _totalAfterDiscount + _vat;
                                                    %>
                                                    <fmt:formatNumber value="<%= _grandTotal %>" type="number" groupingUsed="true" /> đ
                                                </c:otherwise>
                                            </c:choose>
                                        </td>

                                        <td class="action">
                                            <a href="${pageContext.request.contextPath}/staff/order/detail?id=${o.orderID}"
                                               class="btn btn-sm btn-info" title="Xem chi tiết">
                                                <i class="fas fa-eye"></i>
                                            </a>
                                            <c:if test="${o.orderStatus ne 'Đã giao' and o.orderStatus ne 'Đã hủy'}">
                                                <form action="${pageContext.request.contextPath}/staff/order/nextStatus" method="post" style="display:inline-block;margin-left:6px;">
                                                    <input type="hidden" name="orderId" value="${o.orderID}" />
                                                    <button type="submit" class="btn btn-sm btn-warning" title="Chuyển trạng thái tiếp theo">
                                                        <i class="fas fa-forward"></i>
                                                    </button>
                                                </form>
                                            </c:if>
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
