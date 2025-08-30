<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Đơn hàng đã mua - EcoMarts</title>
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/img/eco.png" type="image/x-icon">
        <!-- Google Font -->
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
        <!-- Font Awesome -->
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <!-- Bootstrap CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <!-- Main CSS -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/main.css?version=<%= System.currentTimeMillis()%>" />
        <style>
            .main-content {
                background-color: #fcfaf5;
                padding-bottom: 40px;
            }

            .reorder-container {
                max-width: 1200px;
                margin: 0 auto;
            }

            .order-card {
                border-radius: 12px;
                overflow: hidden;
                box-shadow: 0 4px 10px rgba(0,0,0,0.05);
                border: none;
                margin-bottom: 20px;
            }

            .order-card-header {
                background: linear-gradient(to right, #f5f5dc, #eae2d0);
                border-bottom: 1px solid #e1d9c4;
                padding: 15px 20px;
            }

            .order-title {
                color: #4a3c1a;
                font-weight: 600;
            }

            .order-table {
                width: 100%;
                border-collapse: collapse;
            }

            .order-table th {
                background-color: #f5f5f5;
                color: #4a3c1a;
                font-weight: 600;
                padding: 12px 15px;
                text-align: center;
                border-bottom: 1px solid #e0e0e0;
            }

            .order-table td {
                padding: 15px;
                text-align: center;
                border-bottom: 1px solid #e0e0e0;
                color: #333;
                vertical-align: middle;
            }
            
            .order-table td.price {
                text-align: right;
            }
            
            .order-table td:nth-child(2) {
                text-align: center;
                max-width: 300px;
                word-wrap: break-word;
            }

            .order-table tr:hover {
                background-color: #f9f9f9;
            }

            .status-badge {
                padding: 5px 12px;
                border-radius: 20px;
                font-size: 14px;
                font-weight: 600;
                display: inline-block;
            }

            .status-processing {
                background-color: #fff9c4;
                color: #ffa000;
            }

            .status-shipping {
                background-color: #e3f2fd;
                color: #1976d2;
            }

            .status-delivered {
                background-color: #e8f5e9;
                color: #388e3c;
            }

            .status-cancelled {
                background-color: #ffebee;
                color: #d32f2f;
            }

            .btn-view {
                background-color: #4a3c1a;
                color: white;
                border: none;
                border-radius: 20px;
                padding: 6px 15px;
                font-size: 14px;
                transition: all 0.3s;
                margin-bottom: 5px;
                width: 100%;
                max-width: 120px;
            }

            .btn-view:hover {
                background-color: #5e4d26;
                transform: translateY(-2px);
            }

            .btn-reorder {
                background-color: #4CAF50;
                color: white;
                border: none;
                border-radius: 20px;
                padding: 6px 15px;
                font-size: 14px;
                transition: all 0.3s;
                width: 100%;
                max-width: 120px;
            }

            .btn-reorder:hover {
                background-color: #3d8b40;
                transform: translateY(-2px);
            }

            .action-buttons {
                display: flex;
                flex-direction: column;
                align-items: center;
                gap: 5px;
            }
        </style>
    </head>
    <body>
        <!-- Include Header -->
        <jsp:include page="header.jsp" />

        <div class="main-content">
            <div class="container py-5 reorder-container">
                <div class="row mb-4">
                    <div class="col-12">
                        <nav aria-label="breadcrumb">
                            <ol class="breadcrumb">
                                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/home">Trang chủ</a></li>
                                <li class="breadcrumb-item active" aria-current="page">Đơn hàng của tôi</li>
                            </ol>
                        </nav>
                    </div>
                </div>

                <div class="card order-card">
                    <div class="card-header order-card-header">
                        <h5 class="mb-0 order-title">
                            <i class="fas fa-shopping-bag me-2"></i> Đơn hàng đã mua
                        </h5>
                    </div>
                    <div class="card-body p-0">
                        <div class="table-responsive">
                            <table class="order-table">
                                <thead>
                                    <tr>
                                        <th>Mã đơn hàng</th>
                                        <th>Sản phẩm</th>
                                        <th>Tổng tiền</th>
                                        <th>Trạng thái</th>
                                        <th>Số lượng sản phẩm</th>
                                        <th>Hành động</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="order" items="${orders}">
                                        <tr>
                                            <td>#${order.orderID}</td>
                                            <td>${order.productNames}</td>
                                            <td class="price"><fmt:formatNumber value="${order.totalAmount}" type="number"/> đ</td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${order.orderStatus == 'Đang xử lý'}">
                                                        <span class="status-badge status-processing">
                                                            <i class="fas fa-clock me-1"></i> ${order.orderStatus}
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${order.orderStatus == 'Đang giao hàng'}">
                                                        <span class="status-badge status-shipping">
                                                            <i class="fas fa-shipping-fast me-1"></i> ${order.orderStatus}
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${order.orderStatus == 'Đã giao'}">
                                                        <span class="status-badge status-delivered">
                                                            <i class="fas fa-check-circle me-1"></i> ${order.orderStatus}
                                                        </span>
                                                    </c:when>
                                                    <c:when test="${order.orderStatus == 'Đã hủy'}">
                                                        <span class="status-badge status-cancelled">
                                                            <i class="fas fa-times-circle me-1"></i> ${order.orderStatus}
                                                        </span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="status-badge">${order.orderStatus}</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>
                                            <td>
                                                <%
                                                    dao.OrderDetailDAO detailDAO = new dao.OrderDetailDAO();
                                                    int count = detailDAO.countDistinctProductsInOrder(((model.Order) pageContext.getAttribute("order")).getOrderID());
                                                %>
                                                <%= count%> sản phẩm
                                            </td>
                                            <td>
                                                <div class="action-buttons">
                                                    <form action="${pageContext.request.contextPath}/orderDetail" method="get">
                                                        <input type="hidden" name="orderID" value="${order.orderID}" />
                                                        <button type="submit" class="btn btn-view">
                                                            <i class="fas fa-eye me-1"></i> Chi tiết
                                                        </button>
                                                    </form>

                                                    <form method="post" action="${pageContext.request.contextPath}/orderDetail">
                                                        <input type="hidden" name="orderId" value="${order.orderID}">
                                                        <button type="submit" name="action" value="reorder" class="btn btn-reorder">
                                                            <i class="fas fa-shopping-cart me-1"></i> Mua lại
                                                        </button>
                                                    </form>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:forEach>

                                    <c:if test="${empty orders}">
                                        <tr>
                                            <td colspan="6" class="text-center py-5">
                                                <div class="d-flex flex-column align-items-center">
                                                    <i class="fas fa-shopping-bag fa-3x text-muted mb-3"></i>
                                                    <p class="mb-3">Bạn chưa có đơn hàng nào.</p>
                                                    <a href="${pageContext.request.contextPath}/home" class="btn btn-primary">
                                                        <i class="fas fa-shopping-cart me-2"></i> Mua sắm ngay
                                                    </a>
                                                </div>
                                            </td>
                                        </tr>
                                    </c:if>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Include Footer -->
        <jsp:include page="footer.jsp" />

        <!-- Bootstrap & jQuery -->
        <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

        <script>
            document.addEventListener('DOMContentLoaded', function () {
                // Display success/error messages if any
            <c:if test="${not empty successMessage}">
            alert("${successMessage}");
            </c:if>

            <c:if test="${not empty errorMessage}">
            alert("${errorMessage}");
            </c:if>
            });
        </script>
    </body>
</html>