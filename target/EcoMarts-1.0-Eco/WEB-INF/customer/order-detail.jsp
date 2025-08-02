<%@page contentType="text/html;charset=UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chi tiết đơn hàng #${order.orderID} - EcoMarts</title>
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
                margin-left: 250px;
                padding-top: 80px;
                min-height: calc(100vh - 60px);
                background-color: #fcfaf5;
                padding-bottom: 40px;
            }

            .order-detail-container {
                max-width: 1000px;
                margin: 0 auto;
            }

            .order-card {
                border-radius: 12px;
                overflow: hidden;
                box-shadow: 0 4px 10px rgba(0,0,0,0.05);
                border: none;
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

            .status-badge {
                padding: 5px 12px;
                border-radius: 20px;
                font-size: 14px;
                font-weight: 600;
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

            .product-img {
                width: 60px;
                height: 60px;
                object-fit: contain;
                border-radius: 6px;
                border: 1px solid #eaeaea;
            }

            .table-products th {
                background-color: #f5f5f5;
                border-top: none;
            }

            .order-action-btn {
                border-radius: 25px;
                padding: 8px 20px;
                font-weight: 500;
                transition: all 0.3s ease;
            }

            .history-item {
                border-left: 3px solid #4CAF50;
                padding: 8px 16px;
                margin-bottom: 10px;
                background-color: #f9f9f9;
            }

            .history-timestamp {
                color: #757575;
                font-size: 0.85rem;
            }

            .history-user {
                font-weight: 500;
                color: #4a3c1a;
            }

            .history-action {
                color: #4CAF50;
            }
        </style>
    </head>
    <body>
        <!-- Include Header -->
        <jsp:include page="header.jsp" />

        <div class="main-content">
            <div class="container py-5 order-detail-container">
                <div class="row">
                    <div class="col-12">
                        <nav aria-label="breadcrumb">
                            <ol class="breadcrumb">
                                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/home">Trang chủ</a></li>
                                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/customer/reorder">Đơn hàng của tôi</a></li>
                                <li class="breadcrumb-item active" aria-current="page">Chi tiết đơn hàng #${order.orderID}</li>
                            </ol>
                        </nav>
                    </div>
                </div>

                <!-- Order Summary -->
                <div class="card order-card mb-4">
                    <div class="card-header order-card-header d-flex justify-content-between align-items-center">
                        <h5 class="mb-0 order-title">Chi tiết đơn hàng #${order.orderID}</h5>
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
                                <span class="badge bg-secondary">${order.orderStatus}</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-6">
                                <h6 class="fw-bold">Thông tin đơn hàng</h6>
                                <p><strong>Ngày đặt:</strong> <fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy HH:mm" /></p>
                                <p><strong>Phương thức thanh toán:</strong> ${order.paymentMethod}</p>
                                <p><strong>Trạng thái thanh toán:</strong> ${order.paymentStatus}</p>
                                <c:if test="${not empty order.notes}">
                                    <p><strong>Ghi chú:</strong> ${order.notes}</p>
                                </c:if>
                            </div>
                            <div class="col-md-6">
                                <h6 class="fw-bold">Thông tin giao hàng</h6>
                                <p><strong>Người nhận:</strong> ${order.account.fullName}</p>
                                <p><strong>Địa chỉ:</strong> ${order.shippingAddress}</p>
                                <p><strong>Số điện thoại:</strong> ${order.shippingPhone}</p>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Products -->
                <div class="card order-card mb-4">
                    <div class="card-header order-card-header">
                        <h5 class="mb-0 order-title">Sản phẩm</h5>
                    </div>
                    <div class="card-body p-0">
                        <div class="table-responsive">
                            <table class="table table-products mb-0">
                                <thead>
                                    <tr>
                                        <th class="text-center">Ảnh</th>
                                        <th>Sản phẩm</th>
                                        <th class="text-center">Đơn giá</th>
                                        <th class="text-center">Số lượng</th>
                                        <th class="text-end">Thành tiền</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="od" items="${orderDetails}">
                                        <tr>
                                            <td class="text-center">
                                                <c:if test="${not empty od.product && not empty od.product.imageURL}">
                                                    <img src="${pageContext.request.contextPath}/ImageServlet?name=${od.product.imageURL}" 
                                                         class="product-img" alt="${od.productName}">
                                                </c:if>
                                                <c:if test="${empty od.product || empty od.product.imageURL}">
                                                    <div class="product-img bg-light d-flex align-items-center justify-content-center">
                                                        <i class="fas fa-image text-muted"></i>
                                                    </div>
                                                </c:if>
                                            </td>
                                            <td>
                                                <a href="${pageContext.request.contextPath}/ProductDetail?id=${od.productID}" class="text-decoration-none">
                                                    ${od.productName}
                                                </a>
                                            </td>
                                            <td class="text-center"><fmt:formatNumber value="${od.unitPrice}" type="number" pattern="#,###"/> đ</td>
                                            <td class="text-center">
                                                <c:choose>
                                                    <c:when test="${od.unit eq 'kg'}">
                                                        <c:choose>
                                                            <c:when test="${od.quantity % 1 == 0}">
                                                                <fmt:formatNumber value="${od.quantity}" pattern="#"/>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <fmt:formatNumber value="${od.quantity}" pattern="#.##"/>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <fmt:formatNumber value="${od.quantity}" pattern="#" />
                                                    </c:otherwise>
                                                </c:choose>
                                                ${od.unit}
                                            </td>
                                            <td class="text-end fw-bold"><fmt:formatNumber value="${od.subTotal}" type="number"/> đ</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>

                <!-- Order Total -->
                <div class="card order-card mb-4">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-center mb-2">
                            <span>Tổng tiền sản phẩm:</span>
                            <span class="text-end"><fmt:formatNumber value="${total}" type="number"/> đ</span>
                        </div>
                        <div class="d-flex justify-content-between align-items-center mb-2">
                            <span>Phí vận chuyển:</span>
                            <span class="text-success">Miễn phí</span>
                        </div>
                        <c:if test="${not empty discount}">
                            <div class="d-flex justify-content-between align-items-center mb-2">
                                <span>Giảm giá:</span>
                                <span class="text-danger">-<fmt:formatNumber value="${discount}" type="number"/> đ</span>
                            </div>
                        </c:if>
                        <hr>
                        <div class="d-flex justify-content-between align-items-center">
                            <span class="fw-bold fs-5">Tổng thanh toán:</span>
                            <span class="fw-bold fs-5 text-success"><fmt:formatNumber value="${order.totalAmount}" type="number"/> đ</span>
                        </div>
                    </div>
                </div>

                <!-- Order Actions -->
                <div class="d-flex justify-content-between">
                    <a href="${pageContext.request.contextPath}/reorder" class="btn btn-outline-secondary order-action-btn">
                        <i class="fas fa-arrow-left me-2"></i>Quay lại
                    </a>

                    <div>
                        <c:if test="${order.orderStatus == 'Đang xử lý'}">
                            <button id="cancelOrderBtn" class="btn btn-outline-danger order-action-btn me-2">
                                <i class="fas fa-times-circle me-2"></i>Hủy đơn hàng
                            </button>
                        </c:if>

                        <button id="reorderBtn" class="btn btn-primary order-action-btn">
                            <i class="fas fa-shopping-cart me-2"></i>Mua lại
                        </button>
                    </div>
                </div>


            </div>
        </div>

        <!-- Include Footer -->
        <jsp:include page="footer.jsp" />

        <!-- Bootstrap & SweetAlert2 -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
        <script>
            document.addEventListener('DOMContentLoaded', function () {
                // Cancel Order Button
                const cancelOrderBtn = document.getElementById('cancelOrderBtn');
                if (cancelOrderBtn) {
                    cancelOrderBtn.addEventListener('click', function () {
                        Swal.fire({
                            title: 'Xác nhận hủy đơn hàng',
                            text: 'Bạn có chắc chắn muốn hủy đơn hàng này? Hành động này không thể hoàn tác.',
                            icon: 'warning',
                            showCancelButton: true,
                            confirmButtonColor: '#d33',
                            cancelButtonColor: '#3085d6',
                            confirmButtonText: 'Có, hủy đơn hàng',
                            cancelButtonText: 'Không, giữ nguyên'
                        }).then((result) => {
                            if (result.isConfirmed) {
                                // Submit form for order cancellation
                                const form = document.createElement('form');
                                form.method = 'post';
                                form.action = '${pageContext.request.contextPath}/customer/orderDetail';

                                const orderId = document.createElement('input');
                                orderId.type = 'hidden';
                                orderId.name = 'orderId';
                                orderId.value = '${order.orderID}';
                                form.appendChild(orderId);

                                const action = document.createElement('input');
                                action.type = 'hidden';
                                action.name = 'action';
                                action.value = 'cancel';
                                form.appendChild(action);

                                document.body.appendChild(form);
                                form.submit();
                            }
                        });
                    });
                }

                // Reorder Button
                const reorderBtn = document.getElementById('reorderBtn');
                if (reorderBtn) {
                    reorderBtn.addEventListener('click', function () {
                        // Submit form for reordering
                        const form = document.createElement('form');
                        form.method = 'post';
                        form.action = '${pageContext.request.contextPath}/customer/orderDetail';

                        const orderId = document.createElement('input');
                        orderId.type = 'hidden';
                        orderId.name = 'orderId';
                        orderId.value = '${order.orderID}';
                        form.appendChild(orderId);

                        const action = document.createElement('input');
                        action.type = 'hidden';
                        action.name = 'action';
                        action.value = 'reorder';
                        form.appendChild(action);

                        document.body.appendChild(form);
                        form.submit();
                    });
                }

                // Display success message if exists
            <c:if test="${not empty successMessage}">
                Swal.fire({
                    title: 'Thành công',
                    text: '${successMessage}',
                    icon: 'success',
                    confirmButtonColor: '#4CAF50'
                });
            </c:if>

                // Display error message if exists
            <c:if test="${not empty errorMessage}">
                Swal.fire({
                    title: 'Lỗi',
                    text: '${errorMessage}',
                    icon: 'error',
                    confirmButtonColor: '#d33'
                });
            </c:if>
            });
        </script>
    </body>
</html>