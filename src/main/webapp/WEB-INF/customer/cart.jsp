<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Giỏ hàng - EcoMarts</title>
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
            .cart-item {
                border-bottom: 1px solid #eee;
                padding: 15px 0;
                transition: all 0.3s ease;
            }
            .cart-item:hover {
                background-color: #fdf7ea;
            }
            .item-image {
                width: 100px;
                height: 100px;
                object-fit: contain;
                border-radius: 8px;
                border: 1px solid #eaeaea;
            }
            .quantity-input {
                width: 70px;
                border-radius: 20px;
                text-align: center;
                border: 1px solid #d1c9a6;
            }
            .cart-summary {
                background: linear-gradient(to bottom, #fff8e1, #f5f5dc);
                padding: 25px;
                border-radius: 12px;
                box-shadow: 0 4px 10px rgba(0,0,0,0.05);
            }
            .saved-item {
                opacity: 0.8;
            }
            .empty-cart {
                text-align: center;
                padding: 50px 0;
            }
            .btn-update-quantity {
                border: none;
                background: none;
                color: #6c757d;
                cursor: pointer;
                transition: color 0.2s;
            }
            .btn-update-quantity:hover {
                color: #4a3c1a;
            }
            .cart-action-btn {
                border-radius: 25px;
                padding: 8px 16px;
                transition: all 0.3s ease;
            }
            .cart-card {
                border-radius: 12px;
                overflow: hidden;
                box-shadow: 0 4px 10px rgba(0,0,0,0.05);
                border: none;
            }
            .cart-card-header {
                background: linear-gradient(to right, #f5f5dc, #eae2d0);
                border-bottom: 1px solid #e1d9c4;
                padding: 15px 20px;
            }
            .cart-title {
                color: #4a3c1a;
                font-weight: 600;
            }
            .main-content {
                margin-left: 250px;
                padding-top: 80px;
                min-height: calc(100vh - 60px);
                background-color: #fcfaf5;
                padding-bottom: 40px;
            }
            .checkout-btn {
                background: linear-gradient(to right, #4CAF50, #2e7d32);
                border: none;
                font-weight: 600;
                letter-spacing: 0.5px;
                box-shadow: 0 4px 8px rgba(76, 175, 80, 0.2);
            }
            .checkout-btn:hover {
                background: linear-gradient(to right, #2e7d32, #1b5e20);
                transform: translateY(-2px);
                box-shadow: 0 6px 12px rgba(76, 175, 80, 0.3);
            }
            .shop-more-btn {
                border: 2px solid #4CAF50;
                color: #4CAF50;
                font-weight: 600;
                transition: all 0.3s ease;
            }
            .shop-more-btn:hover {
                background-color: #4CAF50;
                color: white;
            }
        </style>
    </head>
    <body>
        <!-- Include Header -->
        <jsp:include page="header.jsp" />

        <div class="main-content">
            <div class="container py-5">
                <div class="row mb-4">
                    <div class="col">
                        <h2 class="mb-4 cart-title"><i class="fas fa-shopping-cart me-2"></i>Giỏ hàng của bạn</h2>
                    </div>
                </div>

                <!-- Show error/success messages -->
                <c:if test="${not empty sessionScope.cartError}">
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        ${sessionScope.cartError}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                    <% session.removeAttribute("cartError"); %>
                </c:if>

                <c:if test="${not empty sessionScope.cartMessage}">
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        ${sessionScope.cartMessage}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                    <% session.removeAttribute("cartMessage"); %>
                </c:if>

                <div class="row">
                    <!-- Cart items -->
                    <div class="col-lg-8">
                        <!-- Active cart items -->
                        <div class="card cart-card mb-4">
                            <div class="card-header cart-card-header d-flex justify-content-between align-items-center">
                                <h5 class="mb-0 cart-title">Sản phẩm trong giỏ hàng</h5>
                                <c:if test="${not empty activeItems}">
                                    <form action="cart" method="post">
                                        <input type="hidden" name="action" value="clear">
                                        <button type="submit" class="btn btn-sm btn-outline-danger cart-action-btn">
                                            <i class="fas fa-trash-alt me-1"></i> Xóa tất cả
                                        </button>
                                    </form>
                                </c:if>
                            </div>
                            <div class="card-body cart-container">
                                <c:choose>
                                    <c:when test="${empty activeItems}">
                                        <div class="empty-cart">
                                            <i class="fas fa-shopping-cart fa-4x mb-3 text-muted"></i>
                                            <h5>Giỏ hàng của bạn đang trống</h5>
                                            <p class="text-muted mb-4">Thêm sản phẩm vào giỏ hàng để tiến hành thanh toán</p>
                                            <a href="home" class="btn shop-more-btn">Tiếp tục mua sắm</a>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach items="${activeItems}" var="item">
                                            <div class="cart-item">
                                                <div class="row align-items-center">
                                                    <!-- Product Image -->
                                                    <div class="col-md-2 mb-2 mb-md-0">
                                                        <a href="ProductDetail?id=${item.productID}">
                                                            <img src="ImageServlet?name=${item.product.imageURL}" alt="${item.product.productName}" class="item-image">
                                                        </a>
                                                    </div>

                                                    <!-- Product Info -->
                                                    <div class="col-md-4 mb-2 mb-md-0">
                                                        <a href="ProductDetail?id=${item.productID}" class="text-decoration-none">
                                                            <h5 class="product-name">${item.product.productName}</h5>
                                                        </a>
                                                        <p class="text-muted small">${item.product.unit}</p>
                                                        <c:if test="${item.product.stockQuantity < item.quantity}">
                                                            <p class="text-danger small">Chỉ còn ${item.product.stockQuantity} sản phẩm</p>
                                                        </c:if>
                                                    </div>

                                                    <!-- Quantity -->
                                                    <div class="col-md-3 mb-2 mb-md-0">
                                                        <form action="cart" method="post" class="d-flex align-items-center">
                                                            <input type="hidden" name="action" value="update">
                                                            <input type="hidden" name="cartItemID" value="${item.cartItemID}">
                                                            <div class="input-group">
                                                                <button type="button" class="btn btn-outline-secondary btn-sm quantity-decrease">-</button>
                                                                <%-- Hiển thị số lượng không có .0 nếu không phải sầu riêng (kg) --%>
                                                                <c:choose>
                                                                    <c:when test="${item.product.unit eq 'kg'}">
                                                                        <input type="number" name="quantity" value="${item.quantity}" min="0.1" step="0.1" class="form-control form-control-sm quantity-input text-center" data-max-stock="${item.product.stockQuantity}">
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <input type="number" name="quantity"
                                                                               value="${fn:endsWith(item.quantity, '.0') ? fn:substringBefore(item.quantity, '.0') : item.quantity}"
                                                                               min="1" step="1" class="form-control form-control-sm quantity-input text-center" data-max-stock="${item.product.stockQuantity}" pattern="\d*">
                                                                    </c:otherwise>
                                                                </c:choose>
                                                                <button type="button" class="btn btn-outline-secondary btn-sm quantity-increase">+</button>
                                                            </div>
                                                            <div class="invalid-feedback stock-warning" style="display: none;">
                                                                Chỉ còn ${item.product.stockQuantity} sản phẩm trong kho
                                                            </div>
                                                        </form>
                                                    </div>

                                                    <!-- Price (thành tiền) -->
                                                    <c:set var="price" value="${item.product.price}" />
                                                    <c:set var="quantity" value="${item.quantity}" />
                                                    <div class="col-md-2 mb-2 mb-md-0 text-md-end">
                                                        <div class="fw-bold text-success item-total">
                                                            <% double price = (Double) pageContext.getAttribute("price");
                                                                int quantity = ((Number) pageContext.getAttribute("quantity")).intValue();
                                                                long roundedTotal = Math.round((price * quantity) / 1000.0) * 1000;
                                                                out.print(new java.text.DecimalFormat("#,###").format(roundedTotal));
                                                            %> ₫
                                                        </div>
                                                        <div class="text-muted small">
                                                            <% long roundedPrice = Math.round(price / 1000.0) * 1000;
                                                                out.print(new java.text.DecimalFormat("#,###").format(roundedPrice));
                                                            %> ₫ / ${item.product.unit}
                                                        </div>
                                                    </div>

                                                    <!-- Actions -->
                                                    <div class="col-md-1 text-end">
                                                        <div class="btn-group-vertical">
                                                            <form action="cart" method="post" class="mb-1">
                                                                <input type="hidden" name="action" value="saveForLater">
                                                                <input type="hidden" name="cartItemID" value="${item.cartItemID}">
                                                                <button type="submit" class="btn btn-sm btn-outline-secondary" title="Để dành sau">
                                                                    <i class="far fa-bookmark"></i>
                                                                </button>
                                                            </form>
                                                            <form action="cart" method="post">
                                                                <input type="hidden" name="action" value="remove">
                                                                <input type="hidden" name="cartItemID" value="${item.cartItemID}">
                                                                <button type="submit" class="btn btn-sm btn-outline-danger" title="Xóa">
                                                                    <i class="fas fa-trash-alt"></i>
                                                                </button>
                                                            </form>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>

                        <!-- Saved for later items -->
                        <c:if test="${not empty savedItems}">
                            <div class="card cart-card">
                                <div class="card-header cart-card-header">
                                    <h5 class="mb-0 cart-title"><i class="far fa-bookmark me-2"></i>Sản phẩm để dành sau (${savedItems.size()})</h5>
                                </div>
                                <div class="card-body">
                                    <c:forEach items="${savedItems}" var="item">
                                        <div class="cart-item saved-item">
                                            <div class="row align-items-center">
                                                <!-- Product Image -->
                                                <div class="col-md-2 mb-2 mb-md-0">
                                                    <a href="ProductDetail?id=${item.productID}">
                                                        <img src="ImageServlet?name=${item.product.imageURL}" alt="${item.product.productName}" class="item-image">
                                                    </a>
                                                </div>

                                                <!-- Product Info -->
                                                <div class="col-md-6 mb-2 mb-md-0">
                                                    <a href="ProductDetail?id=${item.productID}" class="text-decoration-none">
                                                        <h5 class="product-name">${item.product.productName}</h5>
                                                    </a>
                                                    <p class="text-muted small">${item.product.unit}</p>
                                                    <!-- Saved for later price -->
                                                    <c:set var="savedPrice" value="${item.product.price}" />
                                                    <div class="fw-bold text-success">
                                                        <% double savedPrice = (Double) pageContext.getAttribute("savedPrice");
                                                            long roundedSavedPrice = Math.round(savedPrice / 1000.0) * 1000;
                                                            out.print(new java.text.DecimalFormat("#,###").format(roundedSavedPrice));
                                                        %> ₫
                                                    </div>
                                                </div>

                                                <!-- Actions -->
                                                <div class="col-md-4 text-md-end">
                                                    <div class="btn-group">
                                                        <form action="cart" method="post" class="me-2">
                                                            <input type="hidden" name="action" value="moveToCart">
                                                            <input type="hidden" name="cartItemID" value="${item.cartItemID}">
                                                            <button type="submit" class="btn btn-sm btn-outline-primary cart-action-btn">
                                                                <i class="fas fa-cart-plus me-1"></i> Thêm vào giỏ hàng
                                                            </button>
                                                        </form>
                                                        <form action="cart" method="post">
                                                            <input type="hidden" name="action" value="remove">
                                                            <input type="hidden" name="cartItemID" value="${item.cartItemID}">
                                                            <button type="submit" class="btn btn-sm btn-outline-danger cart-action-btn">
                                                                <i class="fas fa-trash-alt"></i>
                                                            </button>
                                                        </form>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </div>
                        </c:if>
                    </div>

                    <!-- Cart summary -->
                    <div class="col-lg-4">
                        <div class="cart-summary">
                            <h4 class="mb-3 cart-title">Tóm tắt đơn hàng</h4>
                            <div class="d-flex justify-content-between mb-2" id="cart-subtotal-container">
                                <span id="cart-item-count">Tạm tính (${activeItems.size()} sản phẩm)</span>
                                <c:set var="cartTotalVal" value="${cartTotal}" />
                                <span id="cart-subtotal-amount">
                                    <% double cartTotalVal = (Double) pageContext.getAttribute("cartTotalVal");
                                        long roundedSubtotal = Math.round(cartTotalVal / 1000.0) * 1000;
                                        out.print(new java.text.DecimalFormat("#,###").format(roundedSubtotal));
                                    %> ₫
                                </span>
                            </div>
                            <div class="d-flex justify-content-between mb-2">
                                <span>Phí vận chuyển</span>
                                <span class="text-success">Miễn phí</span>
                            </div>
                            <hr>
                            <div class="d-flex justify-content-between mb-4">
                                <strong>Tổng cộng</strong>
                                <strong class="text-success fs-5 cart-total">
                                    <% long roundedTotal = Math.round(cartTotalVal / 1000.0) * 1000;
                                        out.print(new java.text.DecimalFormat("#,###").format(roundedTotal));
                                    %> ₫
                                </strong>
                            </div>

                            <c:if test="${not empty activeItems}">
                                <a href="checkout" class="btn btn-primary w-100 checkout-btn py-2 mb-2">
                                    <i class="fas fa-cash-register me-2"></i>Thanh toán ngay
                                </a>
                            </c:if>
                            <a href="home" class="btn btn-outline-secondary w-100 shop-more-btn py-2">
                                <i class="fas fa-shopping-basket me-2"></i>Tiếp tục mua sắm
                            </a>

                            <div class="mt-4">
                                <div class="d-flex align-items-center mb-2">
                                    <i class="fas fa-shield-alt text-success me-2"></i>
                                    <span>Cam kết sản phẩm chính hãng</span>
                                </div>
                                <div class="d-flex align-items-center">
                                    <i class="fas fa-exchange-alt text-success me-2"></i>
                                    <span>Đổi trả dễ dàng trong vòng 7 ngày</span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Include Footer -->
        <jsp:include page="footer.jsp" />

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script src="${pageContext.request.contextPath}/assets/js/cart.js?version=<%= System.currentTimeMillis()%>"></script>
        <script>
            // Initialize the cart manager when the page loads
            document.addEventListener('DOMContentLoaded', function () {
                // Create and initialize cart manager
                if (typeof EcomartsCart === 'function') {
                    if (!window.ecomartsCart) {
                        window.ecomartsCart = new EcomartsCart();
                    } else {
                        // Reinitialize if already exists
                        window.ecomartsCart.init();
                    }
                    console.log('Cart manager initialized on cart page');
                } else {
                    console.error('EcomartsCart class not found');
                }
            });
        </script>
        <c:if test="${not empty errorMessage}">
            <script>
                function showNotification(message, type = 'error') {
                    // Kiểm tra xem đã có container thông báo chưa
                    let notificationContainer = document.querySelector('.notification-container');
                    if (!notificationContainer) {
                        // Tạo container nếu chưa có
                        notificationContainer = document.createElement('div');
                        notificationContainer.className = 'notification-container';
                        notificationContainer.style.cssText = `
                    position: fixed;
                    top: 80px;
                    right: 20px;
                    z-index: 9999;
                `;
                        document.body.appendChild(notificationContainer);
                    }

                    // Tạo thông báo mới
                    const notification = document.createElement('div');
                    notification.className = `notification notification-\${type}`;

                    // Thêm icon phù hợp với loại thông báo
                    let icon = '';
                    switch (type) {
                        case 'success':
                            icon = '<i class="fas fa-check-circle" style="margin-right: 8px;"></i>';
                            break;
                        case 'error':
                            icon = '<i class="fas fa-exclamation-circle" style="margin-right: 8px;"></i>';
                            break;
                        case 'warning':
                            icon = '<i class="fas fa-exclamation-triangle" style="margin-right: 8px;"></i>';
                            break;
                        case 'info':
                            icon = '<i class="fas fa-info-circle" style="margin-right: 8px;"></i>';
                            break;
                    }

                    notification.style.cssText = `
                background-color: \${type === 'error' ? '#f44336' : type === 'warning' ? '#ff9800' : type === 'info' ? '#2196F3' : '#4CAF50'};
                color: white;
                padding: 15px;
                margin-bottom: 10px;
                border-radius: 4px;
                box-shadow: 0 2px 5px rgba(0,0,0,0.2);
                max-width: 300px;
                display: flex;
                align-items: center;
            `;

                    notification.innerHTML = icon + message;

                    // Thêm nút đóng
                    const closeButton = document.createElement('span');
                    closeButton.innerHTML = '&times;';
                    closeButton.style.cssText = `
                margin-left: 8px;
                cursor: pointer;
                font-size: 20px;
                font-weight: bold;
                margin-left: auto;
            `;
                    closeButton.addEventListener('click', () => {
                        notification.style.opacity = '0';
                        setTimeout(() => {
                            notification.remove();
                        }, 300);
                    });
                    notification.appendChild(closeButton);

                    // Thêm vào container
                    notificationContainer.appendChild(notification);

                    // Hiệu ứng hiển thị
                    notification.style.opacity = '0';
                    notification.style.transform = 'translateX(50px)';
                    notification.style.transition = 'all 0.3s ease';

                    setTimeout(() => {
                        notification.style.opacity = '1';
                        notification.style.transform = 'translateX(0)';
                    }, 10);

                    // Tự động xóa sau 5 giây
                    setTimeout(() => {
                        notification.style.opacity = '0';
                        notification.style.transform = 'translateX(50px)';
                        setTimeout(() => {
                            notification.remove();
                        }, 300);
                    }, 5000);
                }
                showNotification(`${errorMessage}`);
            </script>
            <c:remove var="errorMessage" scope="session"/>
        </c:if>
    </body>
</html>