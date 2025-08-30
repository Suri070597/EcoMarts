<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Mua ngay - EcoMarts</title>
        <link rel="shortcut icon" href="assets/img/eco.png" type="image/x-icon">
        <!-- Google Font -->
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
        <!-- Font Awesome -->
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <!-- Bootstrap CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <!-- Main CSS -->
        <link rel="stylesheet" href="./assets/css/main.css?version=<%= System.currentTimeMillis()%>" />
        <style>
            .main-content {
                margin-left: 250px;
                padding-top: 80px;
                min-height: calc(100vh - 60px);
                background-color: #fcfaf5;
                padding-bottom: 40px;
            }

            .checkout-container {
                max-width: 1000px;
                margin: 0 auto;
            }

            .product-image {
                width: 100px;
                height: 100px;
                object-fit: contain;
                border-radius: 8px;
                border: 1px solid #eaeaea;
            }

            .checkout-card {
                border-radius: 12px;
                overflow: hidden;
                box-shadow: 0 4px 10px rgba(0,0,0,0.05);
                border: none;
                margin-bottom: 20px;
            }

            .checkout-card-header {
                background: linear-gradient(to right, #f5f5dc, #eae2d0);
                border-bottom: 1px solid #e1d9c4;
                padding: 15px 20px;
            }

            .checkout-title {
                color: #4a3c1a;
                font-weight: 600;
            }

            .form-control:focus {
                border-color: #4CAF50;
                box-shadow: 0 0 0 0.2rem rgba(76, 175, 80, 0.25);
            }

            .payment-method {
                border: 1px solid #ced4da;
                border-radius: 4px;
                padding: 10px 15px;
                display: flex;
                align-items: center;
                margin-bottom: 10px;
                cursor: pointer;
                transition: all 0.2s;
            }

            .payment-method:hover {
                background-color: #f8f9fa;
            }

            .payment-method.selected {
                border-color: #4CAF50;
                background-color: #f1f8e9;
            }

            .payment-logo {
                width: 40px;
                height: 40px;
                margin-right: 15px;
                object-fit: contain;
            }

            .checkout-summary {
                background: linear-gradient(to bottom, #fff8e1, #f5f5dc);
                padding: 25px;
                border-radius: 12px;
                box-shadow: 0 4px 10px rgba(0,0,0,0.05);
            }

            .place-order-btn {
                background: linear-gradient(to right, #4CAF50, #2e7d32);
                border: none;
                font-weight: 600;
                letter-spacing: 0.5px;
                box-shadow: 0 4px 8px rgba(76, 175, 80, 0.2);
            }

            .place-order-btn:hover {
                background: linear-gradient(to right, #2e7d32, #1b5e20);
                transform: translateY(-2px);
                box-shadow: 0 6px 12px rgba(76, 175, 80, 0.3);
            }

            .edit-button {
                color: #4CAF50;
                cursor: pointer;
                margin-left: 10px;
                font-size: 14px;
                text-decoration: none;
            }

            .edit-button:hover {
                text-decoration: underline;
            }

            .voucher-input {
                position: relative;
            }

            .apply-voucher-btn {
                position: absolute;
                right: 0;
                top: 0;
                height: 100%;
                border-top-left-radius: 0;
                border-bottom-left-radius: 0;
            }

            .discount-badge {
                background-color: #ffecb3;
                color: #ff8f00;
                font-weight: 500;
                padding: 4px 8px;
                border-radius: 16px;
                margin-left: 10px;
            }
        </style>
    </head>
    <body>
        <!-- Include Header -->
        <jsp:include page="header.jsp" />

        <div class="main-content">
            <div class="container py-5 checkout-container">
                <div class="row mb-4">
                    <div class="col">
                        <h2 class="mb-4 checkout-title">
                            <i class="fas fa-shopping-bag me-2"></i>Mua ngay
                        </h2>
                    </div>
                </div>

                <!-- Show error messages -->
                <c:if test="${not empty error}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        ${error}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                </c:if>

                <!-- Order Form -->
                <form action="buy-now" method="post" id="checkout-form">
                    <c:choose>
                        <c:when test="${not empty sessionScope.checkoutFromCart}">
                            <input type="hidden" name="action" value="processCart">
                        </c:when>
                        <c:otherwise>
                            <input type="hidden" name="action" value="processSingle">
                        </c:otherwise>
                    </c:choose>

                    <div class="row">
                        <!-- Left Column: Shipping info & Payment -->
                        <div class="col-lg-8">
                            <!-- Product Information -->
                            <div class="card checkout-card mb-4">
                                <div class="card-header checkout-card-header">
                                    <h5 class="mb-0">Thông tin sản phẩm</h5>
                                </div>
                                <div class="card-body">
                                    <c:choose>
                                        <c:when test="${not empty sessionScope.checkoutFromCart}">
                                            <!-- Cart items display -->
                                            <c:forEach items="${cartItems}" var="item" varStatus="status">
                                                <div class="d-flex align-items-center mb-3 ${!status.last ? 'border-bottom pb-3' : ''}">
                                                    <img src="ImageServlet?name=${item.product.imageURL}" alt="${item.product.productName}" class="product-image me-3">
                                                    <div class="flex-grow-1">
                                                        <h5>${item.product.productName}</h5>
                                                        <p class="text-muted mb-0">Đơn vị: ${item.product.unit}</p>
                                                        <div class="d-flex justify-content-between align-items-center mb-2">
                                                            <div class="quantity-controls">
                                                                <div class="input-group" style="width: 150px;">
                                                                    <button type="button" class="btn btn-outline-secondary btn-sm cart-quantity-decrease" data-id="${item.cartItemID}">
                                                                        <i class="fas fa-minus"></i>
                                                                    </button>
                                                                    <c:choose>
                                                                        <c:when test="${item.product.unit eq 'kg'}">
                                                                            <input type="number" class="form-control form-control-sm text-center cart-quantity-input"
                                                                                   name="cartQuantity_${item.cartItemID}" 
                                                                                   value="${item.quantity}" min="0.1" step="0.1" max="${item.product.stockQuantity}"
                                                                                   data-price="${item.product.price}" data-id="${item.cartItemID}" 
                                                                                   data-max="${item.product.stockQuantity}" data-unit="${item.product.unit}">
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <input type="number" class="form-control form-control-sm text-center cart-quantity-input"
                                                                                   name="cartQuantity_${item.cartItemID}" 
                                                                                   value="${fn:endsWith(item.quantity, '.0') ? fn:substringBefore(item.quantity, '.0') : item.quantity}"
                                                                                   min="1" step="1" max="${item.product.stockQuantity}" 
                                                                                   data-price="${item.product.price}" data-id="${item.cartItemID}" 
                                                                                   data-max="${item.product.stockQuantity}" data-unit="${item.product.unit}" pattern="\d*">
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                    <button type="button" class="btn btn-outline-secondary btn-sm cart-quantity-increase" data-id="${item.cartItemID}">
                                                                        <i class="fas fa-plus"></i>
                                                                    </button>
                                                                </div>
                                                                <small class="text-muted">Còn lại: ${item.product.stockQuantity} ${item.product.unit}</small>
                                                            </div>
                                                        </div>
                                                        <div class="price-section">
                                                            <c:set var="productPromotion" value="${requestScope['appliedPromotion_'.concat(item.product.productID)]}" />
                                                            <c:if test="${not empty productPromotion}">
                                                                <!-- Giá gốc (gạch ngang) -->
                                                                <p class="text-muted small mb-1" style="text-decoration: line-through;">
                                                                    <fmt:formatNumber value="${requestScope['originalPrice_'.concat(item.product.productID)]}" type="number" pattern="#,###"/> đ / ${item.product.unit}
                                                                </p>
                                                                <!-- Giá đã giảm và badge khuyến mãi -->
                                                                <p class="text-danger small fw-bold mb-0 d-flex align-items-center">
                                                                    <fmt:formatNumber value="${item.product.price}" type="number" pattern="#,###"/> đ / ${item.product.unit}
                                                                    <span class="badge bg-danger ms-2" style="font-size: 11px;">
                                                                        -<fmt:formatNumber value="${productPromotion.discountPercent}" pattern="#,##0"/>%
                                                                    </span>
                                                                </p>
                                                            </c:if>
                                                            <c:if test="${empty productPromotion}">
                                                                <!-- Chỉ hiển thị giá gốc nếu không có khuyến mãi -->
                                                                <p class="text-muted small mb-0">
                                                                    <fmt:formatNumber value="${item.product.price}" type="number" pattern="#,###"/> đ / ${item.product.unit}
                                                                </p>
                                                            </c:if>
                                                        </div>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <!-- Single product buy now display -->
                                            <div class="d-flex align-items-center">
                                                <img src="ImageServlet?name=${buyNowItem.product.imageURL}" alt="${buyNowItem.product.productName}" class="product-image me-3">
                                                <div class="flex-grow-1">
                                                    <h5>${buyNowItem.product.productName}</h5>
                                                    <p class="text-muted mb-0">Đơn vị: ${buyNowItem.product.unit}</p>
                                                    <div class="quantity-controls mb-2">
                                                        <div class="input-group" style="width: 150px;">
                                                            <button type="button" class="btn btn-outline-secondary btn-sm quantity-decrease">
                                                                <i class="fas fa-minus"></i>
                                                            </button>
                                                            <c:choose>
                                                                <c:when test="${buyNowItem.product.unit eq 'kg'}">
                                                                    <input type="number" id="product-quantity" name="quantity" class="form-control form-control-sm text-center" 
                                                                           value="${buyNowItem.quantity}" min="0.1" step="0.1" max="${buyNowItem.product.stockQuantity}" 
                                                                           data-price="${buyNowItem.product.price}" data-max="${buyNowItem.product.stockQuantity}">
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <input type="number" id="product-quantity" name="quantity" class="form-control form-control-sm text-center" 
                                                                           value="${fn:endsWith(buyNowItem.quantity, '.0') ? fn:substringBefore(buyNowItem.quantity, '.0') : buyNowItem.quantity}" 
                                                                           min="1" step="1" max="${buyNowItem.product.stockQuantity}" 
                                                                           data-price="${buyNowItem.product.price}" data-max="${buyNowItem.product.stockQuantity}" pattern="\d*">
                                                                </c:otherwise>
                                                            </c:choose>
                                                            <button type="button" class="btn btn-outline-secondary btn-sm quantity-increase">
                                                                <i class="fas fa-plus"></i>
                                                            </button>
                                                        </div>
                                                        <small class="text-muted">Còn lại: ${buyNowItem.product.stockQuantity} ${buyNowItem.product.unit}</small>
                                                    </div>
                                                    <div class="price-section">
                                                        <c:if test="${not empty appliedPromotion}">
                                                            <!-- Giá gốc (gạch ngang) -->
                                                            <p class="text-muted mb-1" style="text-decoration: line-through;">
                                                                <fmt:formatNumber value="${originalPrice}" type="number" pattern="#,###"/> đ/${buyNowItem.product.unit}
                                                            </p>
                                                            <!-- Giá đã giảm và badge khuyến mãi -->
                                                            <p class="text-danger fw-bold mb-0 d-flex align-items-center">
                                                                <fmt:formatNumber value="${buyNowItem.product.price}" type="number" pattern="#,###"/> đ/${buyNowItem.product.unit}
                                                                <span class="badge bg-danger ms-2">
                                                                    -<fmt:formatNumber value="${appliedPromotion.discountPercent}" pattern="#,##0"/>%
                                                                </span>
                                                            </p>
                                                        </c:if>
                                                        <c:if test="${empty appliedPromotion}">
                                                            <!-- Chỉ hiển thị giá gốc nếu không có khuyến mãi -->
                                                            <p class="text-success fw-bold mb-0">
                                                                <fmt:formatNumber value="${buyNowItem.product.price}" type="number" pattern="#,###"/> đ/${buyNowItem.product.unit}
                                                            </p>
                                                        </c:if>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>

                            <!-- Shipping Information -->
                            <div class="card checkout-card mb-4">
                                <div class="card-header checkout-card-header d-flex justify-content-between align-items-center">
                                    <h5 class="mb-0">Thông tin giao hàng</h5>
                                </div>
                                <div class="card-body">
                                    <div class="mb-3">
                                        <label for="recipientName" class="form-label">Tên người nhận <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control" id="recipientName" name="recipientName" 
                                               value="${empty recipientName ? userInfo.fullName : recipientName}" required>
                                    </div>
                                    <div class="mb-3">
                                        <label for="shippingPhone" class="form-label">Số điện thoại <span class="text-danger">*</span></label>
                                        <input type="tel" class="form-control" id="shippingPhone" name="shippingPhone" 
                                               value="${empty shippingPhone ? userInfo.phone : shippingPhone}" required>
                                    </div>
                                    <div class="mb-3">
                                        <label for="shippingAddress" class="form-label">Địa chỉ giao hàng <span class="text-danger">*</span></label>
                                        <textarea class="form-control" id="shippingAddress" name="shippingAddress" rows="3" required>${empty shippingAddress ? userInfo.address : shippingAddress}</textarea>
                                    </div>
                                    <div class="mb-3">
                                        <label for="notes" class="form-label">Ghi chú</label>
                                        <textarea class="form-control" id="notes" name="notes" rows="2">${notes}</textarea>
                                    </div>
                                </div>
                            </div>

                            <!-- Payment Method -->
                            <div class="card checkout-card mb-4">
                                <div class="card-header checkout-card-header">
                                    <h5 class="mb-0">Phương thức thanh toán</h5>
                                </div>
                                <div class="card-body">
                                    <div class="payment-method selected" onclick="selectPayment('COD', this)">
                                        <input type="radio" name="paymentMethod" id="payment-cod" value="COD" checked>
                                        <label for="payment-cod" class="ms-2 flex-grow-1 cursor-pointer">
                                            <div class="d-flex align-items-center">
                                                <i class="fas fa-money-bill-wave fa-2x text-success me-3"></i>
                                                <div>
                                                    <div class="fw-bold">Thanh toán khi nhận hàng (COD)</div>
                                                    <small class="text-muted">Thanh toán bằng tiền mặt khi nhận hàng</small>
                                                </div>
                                            </div>
                                        </label>
                                    </div>

                                    <div class="payment-method mt-3" onclick="selectPayment('VNPay', this)">
                                        <input type="radio" name="paymentMethod" id="payment-vnpay" value="VNPay">
                                        <label for="payment-vnpay" class="ms-2 flex-grow-1 cursor-pointer">
                                            <div class="d-flex align-items-center">
                                                <img src="assets/img/vnpay-logo.png" alt="VNPay" class="payment-logo">
                                                <div>
                                                    <div class="fw-bold">Thanh toán qua VNPay</div>
                                                    <small class="text-muted">Thanh toán an toàn với VNPay</small>
                                                </div>
                                            </div>
                                        </label>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <!-- Right Column: Order Summary -->
                        <div class="col-lg-4">
                            <div class="checkout-summary">
                                <h4 class="mb-3 checkout-title">Tóm tắt đơn hàng</h4>

                                <!-- Product subtotal -->
                                <div class="d-flex justify-content-between mb-2">
                                    <span>Tạm tính:</span>
                                    <span id="subtotal">
                                        <fmt:formatNumber value="${itemTotal}" type="number"/> đ
                                    </span>
                                </div>

                                <!-- Promotion info -->
                                <c:if test="${not empty appliedPromotion}">
                                    <div class="d-flex justify-content-between mb-2">
                                        <span>Giảm giá khuyến mãi:</span>
                                        <span class="text-danger">
                                            -<fmt:formatNumber value="${appliedPromotion.discountPercent}" pattern="#,##0"/>%
                                        </span>
                                    </div>
                                    <div class="d-flex justify-content-between mb-2">
                                        <span>Giá gốc:</span>
                                        <span class="text-muted" style="text-decoration: line-through;">
                                            <fmt:formatNumber value="${originalTotal}" type="number"/> đ
                                        </span>
                                    </div>
                                </c:if>

                                <!-- Shipping fee -->
                                <div class="d-flex justify-content-between mb-2">
                                    <span>Phí vận chuyển:</span>
                                    <span class="text-success">Miễn phí</span>
                                </div>

                                <!-- Voucher -->
                                <div class="mb-3">
                                    <label for="voucherCode" class="form-label">Mã giảm giá</label>
                                    <div class="d-flex voucher-input">
                                        <select class="form-select" id="voucherCode" name="voucherCode">
                                            <option value="">-- Chọn mã giảm giá --</option>
                                            <c:forEach items="${validVouchers}" var="voucher">
                                                <option value="${voucher.voucherCode}" 
                                                        data-discount="${voucher.discountAmount}" 
                                                        data-min-order="${voucher.minOrderValue}">
                                                    ${voucher.voucherCode} - Giảm <fmt:formatNumber value="${voucher.discountAmount}" type="number"/>đ 
                                                    (Đơn tối thiểu <fmt:formatNumber value="${voucher.minOrderValue}" type="number"/>đ)
                                                </option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                </div>

                                <!-- Discount amount -->
                                <div id="discount-row" class="d-flex justify-content-between mb-2" style="display: none">
                                    <span>Giảm giá voucher:</span>
                                    <span id="discount-amount" class="text-danger">-0 đ</span>
                                </div>
                                <!-- VAT -->
                                <div class="d-flex justify-content-between mb-2">
                                    <span>VAT (8%):</span>
                                    <span id="vat-amount">0 đ</span>
                                </div>

                                <hr>

                                <!-- Total amount -->
                                <div class="d-flex justify-content-between mb-4">
                                    <strong>Tổng cộng:</strong>
                                    <strong class="text-success fs-5" id="total-amount">
                                        <fmt:formatNumber value="${totalAmount}" type="number"/> đ
                                    </strong>
                                </div>

                                <button type="submit" class="btn btn-primary w-100 place-order-btn py-2 mb-2">
                                    <i class="fas fa-check-circle me-2"></i>Đặt hàng
                                </button>

                                <c:choose>
                                    <c:when test="${not empty sessionScope.checkoutFromCart}">
                                        <a href="cart" class="btn btn-outline-secondary w-100 py-2">
                                            <i class="fas fa-arrow-left me-2"></i>Quay lại giỏ hàng
                                        </a>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="home" class="btn btn-outline-secondary w-100 py-2">
                                            <i class="fas fa-arrow-left me-2"></i>Quay lại
                                        </a>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <!-- Include Footer -->
        <jsp:include page="footer.jsp" />

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
        <script>
                                        // Function to select payment method
                                        function selectPayment(method, element) {
                                            // Remove selected class from all payment methods
                                            document.querySelectorAll('.payment-method').forEach(el => {
                                                el.classList.remove('selected');
                                            });

                                            // Add selected class to clicked element
                                            element.classList.add('selected');

                                            // Set radio button checked
                                            document.querySelector('input[value="' + method + '"]').checked = true;
                                        }

                                        // Function to show alerts using SweetAlert2
                                        function showAlert(title, text, icon) {
                                            Swal.fire({
                                                title: title,
                                                text: text,
                                                icon: icon,
                                                confirmButtonColor: '#4CAF50',
                                                confirmButtonText: 'Đồng ý'
                                            });
                                        }

                                        // Handle quantity updates and voucher selection
                                        document.addEventListener('DOMContentLoaded', function () {
                                            const vatAmount = document.getElementById('vat-amount');

                                            // Check if we're processing cart or single product
                                            const isCartCheckout = document.querySelector('input[name="action"][value="processCart"]') !== null;

                                            // For single product checkout only
                                            const quantityInput = isCartCheckout ? null : document.getElementById('product-quantity');
                                            const decreaseBtn = isCartCheckout ? null : document.querySelector('.quantity-decrease');
                                            const increaseBtn = isCartCheckout ? null : document.querySelector('.quantity-increase');

                                            // Common elements for both checkout types
                                            const voucherSelect = document.getElementById('voucherCode');
                                            const discountRow = document.getElementById('discount-row');
                                            const discountAmount = document.getElementById('discount-amount');
                                            const totalAmount = document.getElementById('total-amount');
                                            const subtotalElement = document.getElementById('subtotal');

                                            // For single product checkout only
                                            const maxStock = isCartCheckout ? 0 : parseInt(quantityInput ? quantityInput.getAttribute('data-max') : '0');
                                            const unitPrice = isCartCheckout ? 0 : parseFloat(quantityInput ? quantityInput.getAttribute('data-price') : '0');

                                            // Update all price displays based on quantity (for single product only)
                                            function updatePrices() {
                                                if (isCartCheckout) {
                                                    // Cart checkout has fixed prices from server
                                                    return;
                                                }

                                                const quantity = parseFloat(quantityInput.value);
                                                const isKgUnit = quantityInput.getAttribute('step') === '0.1';
                                                const minQuantity = isKgUnit ? 0.1 : 1;

                                                if (isNaN(quantity) || quantity < minQuantity) {
                                                    quantityInput.value = minQuantity;
                                                    return updatePrices();
                                                }

                                                if (quantity > maxStock) {
                                                    quantityInput.value = maxStock;
                                                    showAlert('Số lượng không hợp lệ', 'Chỉ còn ' + maxStock + ' sản phẩm trong kho', 'warning');
                                                    return updatePrices();
                                                }

                                                // Calculate and update subtotal
                                                const newSubtotal = quantity * unitPrice;
                                                const formattedSubtotal = new Intl.NumberFormat('vi-VN').format(newSubtotal);
                                                subtotalElement.textContent = formattedSubtotal + ' đ';

                                                // Check if a voucher is selected
                                                const selectedOption = voucherSelect.options[voucherSelect.selectedIndex];
                                                if (selectedOption.value) {
                                                    const discount = parseFloat(selectedOption.getAttribute('data-discount'));
                                                    const minOrder = parseFloat(selectedOption.getAttribute('data-min-order'));

                                                    if (newSubtotal >= minOrder) {
                                                        // Apply discount
                                                        const formattedDiscount = new Intl.NumberFormat('vi-VN').format(discount);
                                                        discountAmount.textContent = '-' + formattedDiscount + ' đ';

                                                        // Recalculate total
                                                        let vat = newSubtotal * 0.08;
                                                        let newTotal = newSubtotal - discount + vat;
                                                        if (newTotal < 0)
                                                            newTotal = 0;

                                                        vatAmount.textContent = new Intl.NumberFormat('vi-VN').format(vat) + ' đ';
                                                        totalAmount.textContent = new Intl.NumberFormat('vi-VN').format(newTotal) + ' đ';

                                                        discountRow.style.display = 'flex';
                                                    } else {
                                                        // Không đủ điều kiện, xóa voucher
                                                        voucherSelect.value = "";
                                                        discountAmount.textContent = '-0 đ';
                                                        discountRow.style.display = 'none';

                                                        let vat = newSubtotal * 0.08;
                                                        vatAmount.textContent = new Intl.NumberFormat('vi-VN').format(vat) + ' đ';
                                                        let newTotal = newSubtotal + vat;
                                                        totalAmount.textContent = new Intl.NumberFormat('vi-VN').format(newTotal) + ' đ';

                                                        showAlert('Mã giảm giá không áp dụng được',
                                                                'Giá trị đơn hàng phải từ ' + new Intl.NumberFormat('vi-VN').format(minOrder) + ' đ để sử dụng mã này',
                                                                'warning');
                                                    }
                                                } else {
                                                    // Không có voucher
                                                    discountAmount.textContent = '-0 đ';
                                                    discountRow.style.display = 'none';

                                                    let vat = newSubtotal * 0.08;
                                                    vatAmount.textContent = new Intl.NumberFormat('vi-VN').format(vat) + ' đ';
                                                    let newTotal = newSubtotal + vat;
                                                    totalAmount.textContent = new Intl.NumberFormat('vi-VN').format(newTotal) + ' đ';
                                                }

                                            }

                                            // For single product checkout only
                                            if (!isCartCheckout) {
                                                // Quantity decrease button
                                                decreaseBtn.addEventListener('click', function () {
                                                    const currentValue = parseFloat(quantityInput.value);
                                                    const isKgUnit = quantityInput.getAttribute('step') === '0.1';
                                                    const step = isKgUnit ? 0.1 : 1;
                                                    const minQuantity = isKgUnit ? 0.1 : 1;

                                                    if (currentValue > minQuantity) {
                                                        // For kg, decrement by 0.1, for others by 1
                                                        quantityInput.value = (Math.round((currentValue - step) * 10) / 10).toString();
                                                        updatePrices();
                                                    }
                                                });

                                                // Quantity increase button
                                                increaseBtn.addEventListener('click', function () {
                                                    const currentValue = parseFloat(quantityInput.value);
                                                    const isKgUnit = quantityInput.getAttribute('step') === '0.1';
                                                    const step = isKgUnit ? 0.1 : 1;

                                                    if (currentValue < maxStock) {
                                                        // For kg, increment by 0.1, for others by 1
                                                        quantityInput.value = (Math.round((currentValue + step) * 10) / 10).toString();
                                                        updatePrices();
                                                    } else {
                                                        showAlert('Số lượng tối đa', 'Chỉ còn ' + maxStock + ' sản phẩm trong kho', 'info');
                                                    }
                                                });

                                                // Direct quantity input change
                                                quantityInput.addEventListener('change', updatePrices);
                                                quantityInput.addEventListener('input', function () {
                                                    const isKgUnit = this.getAttribute('step') === '0.1';
                                                    const minQuantity = isKgUnit ? 0.1 : 1;

                                                    if (this.value === '' || isNaN(this.value)) {
                                                        this.value = minQuantity;
                                                    }
                                                });
                                            }

                                            // For cart checkout only - Setup quantity change handlers
                                            if (isCartCheckout) {
                                                // Get all cart quantity inputs
                                                const cartQuantityInputs = document.querySelectorAll('.cart-quantity-input');

                                                // Calculate subtotal for cart items
                                                function calculateCartSubtotal() {
                                                    let total = 0;
                                                    cartQuantityInputs.forEach(input => {
                                                        const price = parseFloat(input.getAttribute('data-price'));
                                                        const quantity = parseFloat(input.value);
                                                        if (!isNaN(price) && !isNaN(quantity)) {
                                                            total += price * quantity;
                                                        }
                                                    });
                                                    return total;
                                                }

                                                // Update individual item price
                                                function updateCartItemPrice(input) {
                                                    // Chỉ cần tính giá, không cập nhật hiển thị vì phần tử hiển thị đã bị xóa
                                                    const price = parseFloat(input.getAttribute('data-price'));
                                                    const quantity = parseFloat(input.value);
                                                    return !isNaN(price) && !isNaN(quantity) ? price * quantity : 0;
                                                }

                                                // Update all cart prices
                                                function updateCartPrices() {
                                                    // Calculate and update subtotal directly without updating individual prices
                                                    const newSubtotal = calculateCartSubtotal();
                                                    const formattedSubtotal = new Intl.NumberFormat('vi-VN').format(newSubtotal);
                                                    subtotalElement.textContent = formattedSubtotal + ' đ';

                                                    // Check if a voucher is selected
                                                    const selectedOption = voucherSelect.options[voucherSelect.selectedIndex];
                                                    if (selectedOption.value) {
                                                        const discount = parseFloat(selectedOption.getAttribute('data-discount'));
                                                        const minOrder = parseFloat(selectedOption.getAttribute('data-min-order'));

                                                        if (newSubtotal >= minOrder) {
                                                            const formattedDiscount = new Intl.NumberFormat('vi-VN').format(discount);
                                                            discountAmount.textContent = '-' + formattedDiscount + ' đ';

                                                            let vat = newSubtotal * 0.08;
                                                            let newTotal = newSubtotal - discount + vat;
                                                            if (newTotal < 0)
                                                                newTotal = 0;

                                                            vatAmount.textContent = new Intl.NumberFormat('vi-VN').format(vat) + ' đ';
                                                            totalAmount.textContent = new Intl.NumberFormat('vi-VN').format(newTotal) + ' đ';

                                                            discountRow.style.display = 'flex';
                                                        } else {
                                                            voucherSelect.value = "";
                                                            discountAmount.textContent = '-0 đ';
                                                            discountRow.style.display = 'none';

                                                            let vat = newSubtotal * 0.08;
                                                            vatAmount.textContent = new Intl.NumberFormat('vi-VN').format(vat) + ' đ';
                                                            let newTotal = newSubtotal + vat;
                                                            totalAmount.textContent = new Intl.NumberFormat('vi-VN').format(newTotal) + ' đ';

                                                            showAlert('Mã giảm giá không áp dụng được',
                                                                    'Giá trị đơn hàng phải từ ' + new Intl.NumberFormat('vi-VN').format(minOrder) + ' đ để sử dụng mã này',
                                                                    'warning');
                                                        }
                                                    } else {
                                                        discountAmount.textContent = '-0 đ';
                                                        discountRow.style.display = 'none';

                                                        let vat = newSubtotal * 0.08;
                                                        vatAmount.textContent = new Intl.NumberFormat('vi-VN').format(vat) + ' đ';
                                                        let newTotal = newSubtotal + vat;
                                                        totalAmount.textContent = new Intl.NumberFormat('vi-VN').format(newTotal) + ' đ';
                                                    }

                                                }

                                                // Initialize cart prices on page load
                                                updateCartPrices();

                                                // Set up event listeners for each cart item
                                                cartQuantityInputs.forEach(input => {
                                                    const itemId = input.getAttribute('data-id');
                                                    const isKgUnit = input.getAttribute('step') === '0.1';
                                                    const maxStock = parseFloat(input.getAttribute('data-max'));

                                                    // Handle input change
                                                    input.addEventListener('change', function () {
                                                        updateCartPrices();
                                                    });

                                                    // Handle invalid input
                                                    input.addEventListener('input', function () {
                                                        const minQuantity = isKgUnit ? 0.1 : 1;

                                                        if (this.value === '' || isNaN(this.value)) {
                                                            this.value = minQuantity;
                                                        }
                                                    });

                                                    // Tìm nút giảm dựa trên input thay vì data-id
                                                    // Thay vì tìm trên toàn tài liệu, tìm nút trước input
                                                    const decreaseBtn = input.previousElementSibling;
                                                    if (decreaseBtn && decreaseBtn.classList.contains('cart-quantity-decrease')) {
                                                        decreaseBtn.addEventListener('click', function () {
                                                            const currentValue = parseFloat(input.value);
                                                            const step = isKgUnit ? 0.1 : 1;
                                                            const minQuantity = isKgUnit ? 0.1 : 1;

                                                            if (currentValue > minQuantity) {
                                                                input.value = (Math.round((currentValue - step) * 10) / 10).toString();
                                                                updateCartPrices();
                                                            }
                                                        });
                                                    }

                                                    // Tìm nút tăng dựa trên input thay vì data-id
                                                    // Thay vì tìm trên toàn tài liệu, tìm nút sau input
                                                    const increaseBtn = input.nextElementSibling;
                                                    if (increaseBtn && increaseBtn.classList.contains('cart-quantity-increase')) {
                                                        increaseBtn.addEventListener('click', function () {
                                                            const currentValue = parseFloat(input.value);
                                                            const step = isKgUnit ? 0.1 : 1;

                                                            if (currentValue < maxStock) {
                                                                input.value = (Math.round((currentValue + step) * 10) / 10).toString();
                                                                updateCartPrices();
                                                            } else {
                                                                showAlert('Số lượng tối đa', 'Bạn đã chọn số lượng tối đa có sẵn trong kho', 'warning');
                                                            }
                                                        });
                                                    }
                                                });

                                                // Override updatePrices function for cart checkout
                                                updatePrices = updateCartPrices;
                                            }

                                            // Voucher selection handler
                                            voucherSelect.addEventListener('change', function () {
                                                const selectedOption = voucherSelect.options[voucherSelect.selectedIndex];
                                                let currentTotal;

                                                if (isCartCheckout) {
                                                    // Use the subtotal from the server for cart checkout
                                                    const subtotalText = subtotalElement.textContent.trim().replace(' đ', '').replace(/\./g, '');
                                                    currentTotal = parseFloat(subtotalText.replace(',', '.'));
                                                } else {
                                                    // Calculate for single product
                                                    const quantity = parseFloat(quantityInput.value);
                                                    currentTotal = quantity * unitPrice;
                                                }

                                                if (selectedOption.value) {
                                                    const discount = parseFloat(selectedOption.getAttribute('data-discount'));
                                                    const minOrder = parseFloat(selectedOption.getAttribute('data-min-order'));

                                                    if (currentTotal >= minOrder) {
                                                        // Apply discount
                                                        const formattedDiscount = new Intl.NumberFormat('vi-VN').format(discount);
                                                        discountAmount.textContent = '-' + formattedDiscount + ' đ';

                                                        // Calculate new total
                                                        let vat = currentTotal * 0.08;
                                                        let newTotal = currentTotal - discount + vat;
                                                        if (newTotal < 0)
                                                            newTotal = 0;

                                                        vatAmount.textContent = new Intl.NumberFormat('vi-VN').format(vat) + ' đ';
                                                        totalAmount.textContent = new Intl.NumberFormat('vi-VN').format(newTotal) + ' đ';

                                                        // Show discount row
                                                        discountRow.style.display = 'flex';

                                                        // Store voucher info for form submission
                                                        document.getElementById('voucherCode').value = selectedOption.value;
                                                    } else {
                                                        // Reset if minimum order not met
                                                        showAlert('Mã giảm giá không áp dụng được',
                                                                'Giá trị đơn hàng phải từ ' + new Intl.NumberFormat('vi-VN').format(minOrder) + ' đ để sử dụng mã này',
                                                                'warning');
                                                        voucherSelect.value = "";
                                                        discountRow.style.display = 'none';
                                                        let vat = currentTotal * 0.08;
                                                        vatAmount.textContent = new Intl.NumberFormat('vi-VN').format(vat) + ' đ';
                                                        let total = currentTotal + vat;
                                                        totalAmount.textContent = new Intl.NumberFormat('vi-VN').format(total) + ' đ';

                                                    }
                                                } else {
                                                    // No voucher selected, reset to original total
                                                    discountAmount.textContent = '-0 đ'; // <-- THÊM DÒNG NÀY
                                                    discountRow.style.display = 'none';
                                                    let vat = currentTotal * 0.08;
                                                    vatAmount.textContent = new Intl.NumberFormat('vi-VN').format(vat) + ' đ';
                                                    let total = currentTotal + vat;
                                                    totalAmount.textContent = new Intl.NumberFormat('vi-VN').format(total) + ' đ';
                                                }

                                            });

                                            // Initialize prices on page load
                                            // Make sure the total amount is displayed correctly even before any user interaction
                                            if (!isCartCheckout && quantityInput) {
                                                const initialQuantity = parseFloat(quantityInput.value);
                                                const initialTotal = initialQuantity * unitPrice;
                                                if (totalAmount.textContent.trim() === 'đ') {
                                                    totalAmount.textContent = new Intl.NumberFormat('vi-VN').format(initialTotal) + ' đ';
                                                }
                                                updatePrices();
                                            }

                                            // Validate form before submission
                                            const checkoutForm = document.getElementById('checkout-form');
                                            checkoutForm.addEventListener('submit', function (event) {
                                                const recipientName = document.getElementById('recipientName').value.trim();
                                                const shippingPhone = document.getElementById('shippingPhone').value.trim();
                                                const shippingAddress = document.getElementById('shippingAddress').value.trim();

                                                if (!recipientName || !shippingPhone || !shippingAddress) {
                                                    event.preventDefault();
                                                    showAlert('Thông tin thiếu', 'Vui lòng điền đầy đủ thông tin người nhận, số điện thoại và địa chỉ giao hàng', 'error');
                                                    return false;
                                                }

                                                // Validate phone number format
                                                const phoneRegex = /^(0|\+84)[3|5|7|8|9][0-9]{8}$/;
                                                if (!phoneRegex.test(shippingPhone)) {
                                                    event.preventDefault();
                                                    showAlert('Số điện thoại không hợp lệ', 'Vui lòng nhập số điện thoại Việt Nam hợp lệ (10 số, bắt đầu bằng 03, 05, 07, 08, 09)', 'error');
                                                    return false;
                                                }

                                                return true;
                                            });

                                            // Display errors from server if any
            <c:if test="${not empty error}">
                                            showAlert('Lỗi', '${error}', 'error');
            </c:if>
                                            if (!isCartCheckout && quantityInput) {
                                                const initialQuantity = parseFloat(quantityInput.value);
                                                const initialTotal = initialQuantity * unitPrice;
                                                let vat = initialTotal * 0.08;
                                                vatAmount.textContent = new Intl.NumberFormat('vi-VN').format(vat) + ' đ';
                                                totalAmount.textContent = new Intl.NumberFormat('vi-VN').format(initialTotal + vat) + ' đ';
                                                updatePrices();
                                            }

                                        });
        </script>
    </body>
</html>