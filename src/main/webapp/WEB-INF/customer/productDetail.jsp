<%-- 
    Document   : productDetail
    Created on : Jun 23, 2025, 10:15:23 AM
    Author     : LNQB
--%>

<%@page import="dao.FeedBackDAO"%>
<%@page import="model.Review"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="model.Manufacturer"%>
<%@page import="model.Category"%>
<%@page import="java.util.List"%>
<%@page import="model.Product"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%
    Product mo = (Product) request.getAttribute("mo");
    List<Category> dataCate = (List<Category>) request.getAttribute("dataCate");
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

//    String nsx = "", hsd = "";
//    if (mo != null) {
//        nsx = sdf.format(mo.getManufactureDate());
//        hsd = sdf.format(mo.getExpirationDate());
//    }
%>
<c:set var="isDrinkOrMilk" value="false"/>
<c:if test="${mo ne null}">
    <c:choose>
        <c:when test="${mo.category ne null}">
            <c:if test="${mo.category.categoryID == 1 or mo.category.parentID == 1 or mo.category.categoryID == 2 or mo.category.parentID == 2}">
                <c:set var="isDrinkOrMilk" value="true"/>
            </c:if>
        </c:when>
        <c:otherwise>
            <c:if test="${mo.categoryID == 1 or mo.categoryID == 2}">
                <c:set var="isDrinkOrMilk" value="true"/>
            </c:if>
        </c:otherwise>
    </c:choose>
</c:if>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="robots" content="index, follow">
        <title>Chi Tiết Sản Phẩm</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="shortcut icon" href="assets/img/eco.png" type="image/x-icon">
        <!-- Google Font -->
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap"
              rel="stylesheet">
        <!-- Font Awesome -->
        <link rel="stylesheet"
              href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <!-- Main CSS -->
        <link rel="stylesheet" href="./assets/css/main.css?version=<%= System.currentTimeMillis()%>" />
        <!-- Home CSS -->
        <link rel="stylesheet" href="./assets/css/home.css?version=<%= System.currentTimeMillis()%>">
        <script defer src="./assets/js/homeJs.js"></script>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/assets/css/productDetail.css">

        <!-- Animate on scroll -->
        <link rel="stylesheet" href="https://unpkg.com/aos@next/dist/aos.css" />
        <!-- SweetAlert2 -->
        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    </head>

    <body>
        <jsp:include page="header.jsp" />

        <!-- Main content -->
        <div class="main-content1">
            <%
                String errorMessage = (String) request.getAttribute("errorMessage");
                if (errorMessage != null && !errorMessage.isEmpty()) {
            %>
            <div class="container mt-5">
                <div class="row justify-content-center">
                    <div class="col-md-8">
                        <div class="alert alert-danger text-center" role="alert">
                            <h4 class="alert-heading">Lỗi!</h4>
                            <p><%= errorMessage%></p>
                            <hr>
                            <p class="mb-0">
                                <a href="${pageContext.request.contextPath}/home" class="btn btn-primary">
                                    <i class="fas fa-home"></i> Quay về trang chủ
                                </a>
                            </p>
                        </div>
                    </div>
                </div>
            </div>
            <%
            } else if (mo != null) {
            %>
            <!-- SweetAlert2 sẽ hiển thị thông báo thay vì Bootstrap alert -->

            <div class="container mt-5">
                <div class="row">
                    <div class="col-md-6">
                        <img src="<%= request.getContextPath()%>/ImageServlet?name=<%= mo.getImageURL()%>" 
                             alt="<%= mo.getProductName()%>" class="img-fluid product-image">
                    </div>
                    <div class="col-md-6">
                        <h2 class="product-name"><%= mo.getProductName()%></h2>
                        <div class="promotion-section">
                            <%-- OLD PRICE BLOCK (kept for reference):
                            <c:choose>
                                <c:when test="${not empty appliedPromotion}">
                                    <div class="flash-sale-banner mt-3">
                                        <div class="flash-sale-header">
                                            <div class="flash-sale-label">${appliedPromotion.promotionName}</div>
                                            <div class="countdown-text">
                                                <i class="bi bi-clock"></i> KẾT THÚC TRONG 
                                                <span class="countdown-box" id="timer-dd">--</span> :
                                                <span class="countdown-box" id="timer-hh">--</span> :
                                                <span class="countdown-box" id="timer-mm">--</span> :
                                                <span class="countdown-box" id="timer-ss">--</span>
                                            </div>
                                        </div>
                                        <div class="mt-3">
                                            <span class="flash-sale-price">
                                                <c:choose>
                                                    <c:when test="${unitPrice != null}">
                                                        <fmt:formatNumber value="${unitPrice * (1 - appliedPromotion.discountPercent / 100)}" type="number" pattern="#,###"/> đ / ${mo.itemUnitName}
                                                    </c:when>
                                                    <c:otherwise>
                                                        <fmt:formatNumber value="${mo.price * (1 - appliedPromotion.discountPercent / 100)}" type="number" pattern="#,###"/> đ / ${mo.boxUnitName}
                                                    </c:otherwise>
                                                </c:choose>
                                            </span>
                                            <span class="original-price">
                                                <c:choose>
                                                    <c:when test="${unitPrice != null}">
                                                        <fmt:formatNumber value="${unitPrice}" type="number" pattern="#,###"/> đ
                                                    </c:when>
                                                    <c:otherwise>
                                                        <fmt:formatNumber value="${mo.price}" type="number" pattern="#,###"/> đ
                                                    </c:otherwise>
                                                </c:choose>
                                            </span>
                                            <span class="discount-percent">
                                                -<fmt:formatNumber value="${appliedPromotion.discountPercent}" type="number"/>%
                                            </span>
                                        </div>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="flash-sale-banner mt-3">
                                        <div class="flash-sale-header">
                                            <span class="flash-sale-price">
                                                <c:choose>
                                                    <c:when test="${unitPrice != null}">
                                                        <fmt:formatNumber value="${unitPrice}" type="number" pattern="#,###"/> đ / ${mo.itemUnitName}
                                                    </c:when>
                                                    <c:otherwise>
                                                        <fmt:formatNumber value="${mo.price}" type="number" pattern="#,###"/> đ / ${mo.boxUnitName}
                                                    </c:otherwise>
                                                </c:choose>
                                            </span>
                                        </div>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                            --%>

                            <c:choose>
                                <c:when test="${not empty appliedPromotion}">
                                    <div class="flash-sale-banner mt-3">
                                        <div class="flash-sale-header">
                                            <div class="flash-sale-label">${appliedPromotion.promotionName}</div>
                                            <div class="countdown-text">
                                                <i class="bi bi-clock"></i> KẾT THÚC TRONG 
                                                <span class="countdown-box" id="timer-dd">--</span> :
                                                <span class="countdown-box" id="timer-hh">--</span> :
                                                <span class="countdown-box" id="timer-mm">--</span> :
                                                <span class="countdown-box" id="timer-ss">--</span>
                                            </div>
                                        </div>
                                        <div class="mt-3">
                                            <c:set var="basePrice" value="${not empty mo.priceUnit ? mo.priceUnit : mo.price}"/>
                                            <c:set var="unitLabel" value="${not empty mo.itemUnitName ? mo.itemUnitName : mo.boxUnitName}"/>
                                            <c:set var="discounted" value="${basePrice * (1 - appliedPromotion.discountPercent/100.0)}"/>
                                            <span class="flash-sale-price">
                                                <fmt:formatNumber value="${discounted}" type="number" pattern="#,###"/> đ / ${unitLabel}
                                            </span>
                                            <span class="original-price">
                                                <fmt:formatNumber value="${basePrice}" type="number" pattern="#,###"/> đ / ${unitLabel}
                                            </span>
                                            <span class="discount-percent">
                                                -<fmt:formatNumber value="${appliedPromotion.discountPercent}" type="number"/>%
                                            </span>
                                        </div>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="flash-sale-banner mt-3">
                                        <div class="flash-sale-header">
                                            <span class="flash-sale-price">
                                                <c:set var="basePrice2" value="${not empty mo.priceUnit ? mo.priceUnit : mo.price}"/>
                                                <c:set var="unitLabel2" value="${not empty mo.itemUnitName ? mo.itemUnitName : mo.boxUnitName}"/>
                                                <fmt:formatNumber value="${basePrice2}" type="number" pattern="#,###"/> đ / ${unitLabel2}
                                            </span>
                                        </div>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <div class="product-detail">
                            <p><span class="sao">
                                    <fmt:formatNumber value="${avgRating}" maxFractionDigits="1" minFractionDigits="1"/>
                                </span>
                                <span class="text-warning">
                                    <c:forEach begin="1" end="${fullStars}"><i class="fas fa-star"></i></c:forEach><c:if test="${halfStar}"><i class="fas fa-star-half-alt"></i></c:if><c:forEach begin="1" end="${emptyStars}"><i class="far fa-star"></i></c:forEach>
                                </span> | <span class="sao">${reviewCount}</span> Đánh Giá</p>

                            <%
                                Category child = mo.getCategory();
                                String parentName = "N/A";
                                String childName = (child != null) ? child.getCategoryName() : "Unknown";
                                int parentId = -1;

                                if (child != null) {
                                    parentId = child.getParentID();
                                    for (Category c : dataCate) {
                                        if (c.getCategoryID() == parentId) {
                                            parentName = c.getCategoryName();
                                            break;
                                        }
                                    }
                                }
                            %>

                            <p><strong>Thể Loại:</strong>
                                <%
                                    if (parentId == 7) {
                                        out.print(parentName); // chỉ in cha
                                    } else if (!"N/A".equals(parentName)) {
                                        out.print(parentName + " > " + childName); // in cha > con
                                    } else {
                                        out.print(childName); // chỉ có con
                                    }
                                %>
                            </p>
                            <!-- Remove old stock quantity display per requirement -->
                            <%-- sửa ngay phần này --%>
                            <%-- <p><strong>Hạn Sử Dụng:</strong>  <%=nsx%> – <%=hsd%></p> --%>
                            <%-- <p><strong>Nhà Sản Xuất:</strong> <%=mo.getManufacturer().getCompanyName()%></p> --%>

                            <form action="cart" method="post" id="detail-cart-form">
                                <input type="hidden" name="action" value="add">
                                <input type="hidden" name="productID" value="<%= mo.getProductID()%>">

                                <%
                                    boolean isFruit = false;
                                    int categoryId = 0;
                                    String categoryName = "Unknown";
                                    int fruitParentId = 0;

                                    if (mo.getCategory() != null) {
                                        categoryId = mo.getCategory().getCategoryID();
                                        categoryName = mo.getCategory().getCategoryName();
                                        fruitParentId = mo.getCategory().getParentID() != null ? mo.getCategory().getParentID() : 0;
                                    } else {
                                        categoryId = mo.getCategoryID();
                                    }

                                    // Kiểm tra cả category cha (3) và category con (có ParentID = 3)
                                    isFruit = (categoryId == 3) || (fruitParentId == 3);
                                    String step = isFruit ? "0.1" : "1";
                                    String min = isFruit ? "0.1" : "1";
                                    String value = isFruit ? "0.1" : "1";
                                %>

                                <input type="hidden" name="packageType" id="selected-package-type" value="">
                                <input type="hidden" name="packSize" id="selected-pack-size" value="0">

                                <c:set var="discountPercent" value="${appliedPromotion != null ? appliedPromotion.discountPercent : 0}"/>

                                <div class="mb-3">
                                    <strong>Phân loại:</strong>
                                    <div class="d-flex gap-2 mt-2" id="unit-selector">
                                        <c:if test="${!isFruit}">
                                            <button type="button" class="btn btn-outline-secondary unit-btn" data-type="UNIT" data-available="${inventory['UNIT_Quantity']}" data-base="${basePrice * (1 - appliedPromotion.discountPercent/100.0)}">
                                                <span class="btn-label">${mo.itemUnitName}</span>
                                                <span class="btn-price-current text-danger fw-bold d-block"></span>
<span class="btn-price-original text-muted text-decoration-line-through d-block"></span>
<span class="btn-price-percent badge bg-danger ms-1" style="display:none;"></span>

                                            </button>
                                            <button type="button" class="btn btn-outline-secondary unit-btn" data-type="BOX" data-available="${inventory['BOX_Quantity']}" data-base="${mo.price * (1 - appliedPromotion.discountPercent/100.0)}">
                                                <span class="btn-label">${mo.boxUnitName}</span>
                                                <span class="btn-price-current text-danger fw-bold d-block"></span>
<span class="btn-price-original text-muted text-decoration-line-through d-block"></span>
<span class="btn-price-percent badge bg-danger ms-1" style="display:none;"></span>

                                            </button>
                                            <c:if test="${not empty inventory['PACK_LIST']}">
                                                <c:forEach var="p" items="${inventory['PACK_LIST']}">
                                                    <button type="button" class="btn btn-outline-secondary unit-btn" data-type="PACK" data-packsize="${p.packSize}" data-available="${p.quantity}" data-base="${(mo.pricePack != null 
                                                                                                                                                       ? mo.pricePack 
                                                                                                                                                       : (mo.priceUnit != null ? mo.priceUnit * p.packSize : 0)) 
                                                                                                                                                       * (1 - appliedPromotion.discountPercent/100.0)}">
                                                        <span class="btn-label">Lốc ${p.packSize} ${mo.itemUnitName}</span>
                                                        <span class="btn-price-current text-danger fw-bold d-block"></span>
<span class="btn-price-original text-muted text-decoration-line-through d-block"></span>
<span class="btn-price-percent badge bg-danger ms-1" style="display:none;"></span>

                                                    </button>
                                                </c:forEach>
                                            </c:if>
                                        </c:if>
                                        <c:if test="${isFruit}">
                                            <button type="button" class="btn btn-outline-secondary unit-btn active" data-type="KG" data-available="${inventory['UNIT_Quantity']}" data-base="${mo.priceUnit * (1 - appliedPromotion.discountPercent/100.0)}">
                                                <span class="btn-label">kg</span>
                                                <span class="btn-price-current text-danger fw-bold d-block"></span>
<span class="btn-price-original text-muted text-decoration-line-through d-block"></span>
<span class="btn-price-percent badge bg-danger ms-1" style="display:none;"></span>

                                            </button>
                                        </c:if>
                                    </div>
                                </div>

                                <div class="mb-3">
                                    <div id="price-display" style="font-size: 0px;">
                                        <span id="current-price" class="text-danger fw-bold"></span>
                                        <span id="original-price" class="text-muted text-decoration-line-through ms-2" style="display:none;"></span>
                                        <span id="discount-badge" class="badge bg-danger ms-2" style="display:none;">-0%</span>
                                    </div>
                                </div>

                                <div class="d-flex align-items-center gap-3 mb-2">
                                    <strong>Số Lượng</strong>
                                    <div class="input-group" style="width:150px;">
                                        <button class="btn btn-outline-secondary" type="button" id="qty-dec">-</button>
                                        <input type="number" id="product-quantity" name="quantity" class="form-control text-center" value="<%= value%>" min="<%= min%>" step="<%= step%>">
                                        <button class="btn btn-outline-secondary" type="button" id="qty-inc">+</button>
                                    </div>
                                    <span class="text-muted" id="available-text"></span>
                                </div>
                                <div class="form-text text-danger" id="quantity-warning" style="display:none;"></div>

                                <div>
                                    <button type="submit" id="add-to-cart-btn" class="btn btn-outline-danger">
                                        <i class="fa-solid fa-cart-shopping"></i> Thêm Vào Giỏ Hàng
                                    </button>
                                    <button type="button" id="buy-now-btn" class="btn btn-danger">Mua Ngay</button>
                                </div>

                                <script>
                                    document.addEventListener('DOMContentLoaded', function () {
                                        const quantityInput = document.getElementById('product-quantity');
                                        const quantityWarning = document.getElementById('quantity-warning');
                                        const addToCartBtn = document.getElementById('add-to-cart-btn');
                                        const buyNowBtn = document.getElementById('buy-now-btn');
                                        const availableText = document.getElementById('available-text');
                                        const form = document.getElementById('detail-cart-form');
                                        const selType = document.getElementById('selected-package-type');
                                        const selPack = document.getElementById('selected-pack-size');

                                        const isFruitPage = ${isFruit};

                                        function getAvailableFor(type, packSize) {
                                            if (isFruitPage)
                                                return ${inventory['UNIT_Quantity'] != null ? inventory['UNIT_Quantity'] : 0};

                                            // Lấy số lượng từ button đang active
                                            const active = document.querySelector('.unit-btn.active');
                                            if (active) {
                                                const available = parseFloat(active.getAttribute('data-available') || '0');
                                                return available;
                                            }

                                            // Fallback: tìm button theo type và packSize
                                            const targetBtn = document.querySelector(`.unit-btn[data-type="${type}"]\${packSize > 0 ? `[data - packsize = "${packSize}"]` : ''}`);
                                            if (targetBtn) {
                                                return parseFloat(targetBtn.getAttribute('data-available') || '0');
                                            }

                                            return 0;
                                        }

                                        function updateAvailableLabel() {
                                            const active = document.querySelector('.unit-btn.active');
                                            if (active) {
                                                const avail = parseFloat(active.getAttribute('data-available') || '0');
                                                availableText.textContent = avail + ' sản phẩm có sẵn';
                                                return avail;
                                            }
                                            availableText.textContent = '0 sản phẩm có sẵn';
                                            return 0;
                                        }

                                        function formatVND(n) {
                                            try {
                                                return new Intl.NumberFormat('vi-VN').format(n);
                                            } catch (e) {
                                                return n;
                                            }
                                        }

                                        function updateButtonPrices() {
                                            const dp = parseFloat(`${discountPercentJS}`);
                                            document.querySelectorAll('.unit-btn').forEach(btn => {
                                                const base = parseFloat(btn.getAttribute('data-base') || '0');
                                                const cur = btn.querySelector('.btn-price-current');
                                                const orig = btn.querySelector('.btn-price-original');
                                                const badge = btn.querySelector('.btn-price-percent');
                                                if (!cur)
                                                    return;
                                                if (!isNaN(dp) && dp > 0 && base > 0) {
                                                    const discounted = base * (1 - dp / 100);
                                                    cur.textContent = '(' + formatVND(discounted) + ' đ)';
                                                    if (orig) {
                                                        orig.textContent = formatVND(base) + ' đ';
                                                        orig.style.display = '';
                                                    }
                                                    if (badge) {
                                                        badge.textContent = `-${dp}%`;
                                                        badge.style.display = '';
                                                    }
                                                } else if (base > 0) {
                                                    cur.textContent = '(' + formatVND(base) + ' đ)';
                                                    if (orig)
                                                        orig.style.display = 'none';
                                                    if (badge)
                                                        badge.style.display = 'none';
                                                } else {
                                                    cur.textContent = '';
                                                    if (orig)
                                                        orig.style.display = 'none';
                                                    if (badge)
                                                        badge.style.display = 'none';
                                                }
                                            });
                                        }

                                        function updatePriceDisplay() {
                                            const active = document.querySelector('.unit-btn.active');
                                            const current = document.getElementById('current-price');
                                            const original = document.getElementById('original-price');
                                            const badge = document.getElementById('discount-badge');
                                            if (!active) {
                                                current.textContent = '';
                                                original.style.display = 'none';
                                                badge.style.display = 'none';
                                                return;
                                            }
                                            const basePrice = parseFloat(active.getAttribute('data-base') || '0');
                                            const dp = parseFloat(`${discountPercentJS}`);
                                            if (!isNaN(dp) && dp > 0 && basePrice > 0) {
                                                const discounted = basePrice * (1 - dp / 100);
                                                current.textContent = formatVND(discounted) + ' đ';
                                                original.textContent = formatVND(basePrice) + ' đ';
                                                original.style.display = '';
                                                badge.textContent = `-${dp}%`;
                                                badge.style.display = '';
                                            } else {
                                                current.textContent = formatVND(basePrice) + ' đ';
                                                original.style.display = 'none';
                                                badge.style.display = 'none';
                                            }
                                        }

                                        function selectDefaultUnit() {
                                            if (isFruitPage) {
                                                selType.value = 'KG';
                                                return;
                                            }

                                            // Tìm button đầu tiên có sẵn và active nó
                                            const firstBtn = document.querySelector('.unit-btn');
                                            if (firstBtn) {
                                                firstBtn.classList.add('active');
                                                selType.value = firstBtn.getAttribute('data-type');
                                                selPack.value = firstBtn.getAttribute('data-packsize') || '0';
                                            }
                                        }

                                        // Unit selector
                                        document.querySelectorAll('.unit-btn').forEach(btn => {
                                            btn.addEventListener('click', () => {
                                                document.querySelectorAll('.unit-btn').forEach(b => b.classList.remove('active'));
                                                btn.classList.add('active');
                                                selType.value = btn.getAttribute('data-type');
                                                selPack.value = btn.getAttribute('data-packsize') || '0';
                                                updateAvailableLabel();
                                                updatePriceDisplay();
                                                updateButtonPrices();
                                            });
                                        });

                                        // Initialize defaults and labels
                                        selectDefaultUnit();
                                        updateAvailableLabel();
                                        const discountPercentJS = ${discountPercent != null ? discountPercent : 0};
                                        updatePriceDisplay();
                                        updateButtonPrices();

                                        const decBtn = document.getElementById('qty-dec');
                                        const incBtn = document.getElementById('qty-inc');

                                        decBtn.addEventListener('click', () => {
                                            const step = parseFloat(quantityInput.step || '1');
                                            const min = parseFloat(quantityInput.min || step);
                                            let val = parseFloat(quantityInput.value || min);
                                            val = Math.max(min, val - step);
                                            quantityInput.value = step < 1 ? val.toFixed(1) : val.toFixed(0);

                                            // Clear warning when decreasing
                                            quantityWarning.style.display = 'none';
                                            addToCartBtn.disabled = false;
                                            buyNowBtn.disabled = false;
                                        });

                                        incBtn.addEventListener('click', () => {
                                            const step = parseFloat(quantityInput.step || '1');
                                            let val = parseFloat(quantityInput.value || step);
                                            const maxAvail = updateAvailableLabel();

                                            if (val + step > maxAvail) {
                                                quantityWarning.style.display = 'block';
                                                quantityWarning.textContent = 'Chỉ còn ' + maxAvail + ' sản phẩm trong kho';
                                                return;
                                            }

                                            val = val + step;
                                            quantityInput.value = step < 1 ? val.toFixed(1) : val.toFixed(0);

                                            // Clear warning when increasing within limit
                                            quantityWarning.style.display = 'none';
                                            addToCartBtn.disabled = false;
                                            buyNowBtn.disabled = false;
                                        });

                                        // Validate quantity when changed
                                        quantityInput.addEventListener('input', function () {
                                            const quantity = isFruitPage ? parseFloat(this.value) : parseInt(this.value);
                                            const minValue = isFruitPage ? 0.1 : 1;

                                            if (isNaN(quantity) || quantity < minValue) {
                                                this.value = minValue;
                                                quantityWarning.style.display = 'none';
                                                addToCartBtn.disabled = false;
                                                buyNowBtn.disabled = false;
                                            } else {
                                                const maxAvail = updateAvailableLabel();
                                                if (quantity > maxAvail) {
                                                    quantityWarning.style.display = 'block';
                                                    quantityWarning.textContent = 'Số lượng tối đa có thể mua: ' + maxAvail;
                                                    addToCartBtn.disabled = true;
                                                    buyNowBtn.disabled = true;
                                                } else {
                                                    quantityWarning.style.display = 'none';
                                                    addToCartBtn.disabled = false;
                                                    buyNowBtn.disabled = false;
                                                }
                                            }
                                        });

                                        // Prevent form submission if quantity is invalid
                                        form.addEventListener('submit', function (event) {
                                            const quantity = isFruitPage ? parseFloat(quantityInput.value) : parseInt(quantityInput.value);
                                            const minValue = isFruitPage ? 0.1 : 1;
                                            const maxAvail = updateAvailableLabel();

                                            if (isNaN(quantity) || quantity < minValue || quantity > maxAvail) {
                                                event.preventDefault();
                                                quantityWarning.style.display = 'block';
                                                quantityWarning.textContent = quantity > maxAvail
                                                        ? 'Số lượng tối đa có thể mua: ' + maxAvail
                                                        : 'Vui lòng nhập số lượng hợp lệ (tối thiểu: ' + minValue + ')';
                                            }
                                        });

                                        // Add Buy Now functionality
                                        buyNowBtn.addEventListener('click', function () {
                                            const quantity = isFruitPage ? parseFloat(quantityInput.value) : parseInt(quantityInput.value);
                                            const minValue = isFruitPage ? 0.1 : 1;
                                            const maxAvail = updateAvailableLabel();

                                            // Validate quantity
                                            if (isNaN(quantity) || quantity < minValue || quantity > maxAvail) {
                                                quantityWarning.style.display = 'block';
                                                quantityWarning.textContent = quantity > maxAvail
                                                        ? 'Số lượng tối đa có thể mua: ' + maxAvail
                                                        : 'Vui lòng nhập số lượng hợp lệ (tối thiểu: ' + minValue + ')';
                                                return;
                                            }

                                            // Use the built-in form instead of creating a new one
                                            const buyNowForm = document.createElement('form');
                                            buyNowForm.method = 'post';
                                            buyNowForm.action = 'buy-now';

                                            // Add action parameter
                                            const actionInput = document.createElement('input');
                                            actionInput.type = 'hidden';
                                            actionInput.name = 'action';
                                            actionInput.value = 'initiate';
                                            buyNowForm.appendChild(actionInput);

                                            // Add product ID parameter
                                            const productIdInput = document.createElement('input');
                                            productIdInput.type = 'hidden';
                                            productIdInput.name = 'productID';
                                            productIdInput.value = '<%= mo.getProductID()%>';
                                            buyNowForm.appendChild(productIdInput);

                                            // Add quantity parameter
                                            const quantityInputHidden = document.createElement('input');
                                            quantityInputHidden.type = 'hidden';
                                            quantityInputHidden.name = 'quantity';
                                            quantityInputHidden.value = quantity;
                                            buyNowForm.appendChild(quantityInputHidden);

                                            // Add selected package info
                                            const packageTypeHidden = document.createElement('input');
                                            packageTypeHidden.type = 'hidden';
                                            packageTypeHidden.name = 'packageType';
                                            packageTypeHidden.value = selType.value || 'UNIT';
                                            buyNowForm.appendChild(packageTypeHidden);

                                            const packSizeHidden = document.createElement('input');
                                            packSizeHidden.type = 'hidden';
                                            packSizeHidden.name = 'packSize';
                                            packSizeHidden.value = selPack.value || '0';
                                            buyNowForm.appendChild(packSizeHidden);

                                            // Append form to body and submit
                                            document.body.appendChild(buyNowForm);
                                            buyNowForm.submit();
                                        });
                                    });
                                </script>
                                

                            </form>


                        </div>
                    </div>

                    <div class="row mt-5">
                        <div class="col-12">
                            <div class="card">
                                <div class="card-body">
                                    <h4 class="product-text" class="card-title">Thông Tin Sản Phẩm</h4>
                                    <p class="card-text"><%=mo.getDescription().replaceAll("\n", "<br/>")%></p>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div class="row mt-5">
                        <div class="col-12">
                            <div class="card">
                                <div class="card-body">
                                    <h4 class="product-text" class="card-title">Đánh Giá (${reviewCount})</h4>                                
                                    <div class="review-comments">
                                        <c:if test="${empty reviewList}">
                                            <p class="text-muted">Không có đánh giá nào.</p>
                                        </c:if>
                                        <c:forEach var="review" items="${reviewList}">
                                            <div class="review-comment mb-3">
                                                <div class="d-flex justify-content-between align-items-start">
                                                    <div>
                                                        <h5 class="mb-1">
                                                            <strong>${review.userName}<c:if test="${review.accountRole == 2}"> (Staff)</c:if></strong>
                                                            <c:if test="${review.accountRole != 2}">
                                                                <span class="text-warning">
                                                                    <c:forEach begin="1" end="${review.rating}" var="i">★</c:forEach><c:forEach begin="1" end="${5 - review.rating}" var="i">☆</c:forEach>
                                                                    </span>
                                                            </c:if>
                                                            <span class="text-muted small ms-2">
                                                                <fmt:formatDate value="${review.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                                            </span>
                                                        </h5>
                                                    </div>
                                                    <div class="action-buttons">
                                                        <!-- Nút sửa review chỉ cho customer -->
                                                        <c:if test="${sessionScope.account != null && sessionScope.account.role == 0 && review.accountID == sessionScope.account.accountID}">
                                                            <a href="Review?action=edit&reviewId=${review.reviewID}" class="btn btn-link btn-sm">Sửa</a>
                                                            <button type="button" class="btn btn-link btn-sm text-danger" onclick="deleteReview(${review.reviewID})">Xóa</button>
                                                        </c:if>
                                                    </div>
                                                </div>
                                                <div class="review-comment-text">${review.comment}</div>
                                                <c:if test="${not empty review.imageURL}">
                                                    <div>
                                                        <img src="${pageContext.request.contextPath}/ImageServlet_2?name=${review.imageURL}" alt="Ảnh review" style="max-width:150px;max-height:150px;">
                                                    </div>
                                                </c:if>
                                                <c:if test="${not empty flatRepliesMap[review.reviewID]}">
                                                    <div class="toggle-reply-box my-2">
                                                        <button type="button" class="btn btn-outline-info btn-sm" style="border-radius: 20px; font-weight: 500;"
                                                                onclick="toggleReply('${review.reviewID}')">
                                                            <span id="arrow${review.reviewID}" style="font-size: 1.1em;">&#9654;</span>
                                                            <span class="ms-1">Xem phản hồi</span>
                                                        </button>
                                                    </div>
                                                </c:if>
                                                <div id="replySection${review.reviewID}" style="display:none;" class="reply-thread compact reply-group">
                                                    <c:forEach var="reply" items="${flatRepliesMap[review.reviewID]}">
                                                        <div class="reply-item" style="margin-left: ${(reply.depth > 0 ? reply.depth * 24 : 0)}px; margin-right: 0;">
                                                            <div class="bubble ${reply.accountRole == 2 ? 'bubble-staff' : ''}" style="position: relative; z-index: 2; margin-left: 0; display: block; width: 100%;">
                                                                <div class="d-flex justify-content-between align-items-center">
                                                                    <div class="d-flex align-items-center gap-2">
                                                                        <strong>${reply.userName}</strong>
                                                                        <c:choose>
                                                                            <c:when test="${reply.accountRole == 2}">
                                                                                <span class="badge badge-staff">Nhân viên</span>
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                <span class="badge badge-customer">Khách hàng</span>
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                        <span class="text-muted small">
                                                                            <fmt:formatDate value="${reply.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                                                        </span>
                                                                    </div>
                                                                    <div class="action-buttons">
                                                                        <!-- Chỉ customer mới được reply vào staff comment -->
                                                                        <c:if test="${sessionScope.account != null && sessionScope.account.role == 0 && reply.accountRole == 2}">
                                                                            <button type="button" class="btn btn-link btn-sm" onclick="setReply('${reply.reviewID}', '${reply.orderID}', '${reply.productID}')">Trả lời</button>
                                                                        </c:if>
                                                                        <!-- Nút sửa/xóa chỉ cho customer sở hữu -->
                                                                        <c:if test="${sessionScope.account != null && sessionScope.account.role == 0 && reply.accountID == sessionScope.account.accountID}">
                                                                            <a href="Review?action=edit&reviewId=${reply.reviewID}" class="btn btn-link btn-sm">Sửa</a>
                                                                            <button type="button" class="btn btn-link btn-sm text-danger" onclick="deleteReview(${reply.reviewID})">Xóa</button>
                                                                        </c:if>
                                                                    </div>
                                                                </div>
                                                                <div class="mt-2">${reply.comment}</div>
                                                                <c:if test="${not empty reply.imageURL}">
                                                                    <div class="mt-2">
                                                                        <img class="reply-image" src="${pageContext.request.contextPath}/ImageServlet_2?name=${reply.imageURL}" alt="Ảnh reply">
                                                                    </div>
                                                                </c:if>
                                                            </div>
                                                        </div>
                                                    </c:forEach>
                                                </div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                    <nav aria-label="Page navigation">
                                        <ul class="pagination justify-content-center">
                                            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                                <a class="page-link" href="ProductDetail?id=${mo.productID}&page=${currentPage - 1}">Trước</a>
                                            </li>
                                            <c:forEach var="i" begin="1" end="${totalPages}">
                                                <li class="page-item ${i == currentPage ? 'active' : ''}">
                                                    <a class="page-link" href="ProductDetail?id=${mo.productID}&page=${i}">${i}</a>
                                                </li>
                                            </c:forEach>
                                            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                                <a class="page-link" href="ProductDetail?id=${mo.productID}&page=${currentPage + 1}">Sau</a>
                                            </li>
                                        </ul>
                                    </nav>
                                    <c:if test="${not empty message}">
                                        <div class="alert alert-info text-center" style="margin-top: 10px">${message}</div>
                                    </c:if>
                                    <!-- Thông báo cho guest hoặc admin -->
                                    <c:if test="${sessionScope.account == null}">
                                        <div class="alert alert-info mt-3">Bạn cần <a href="login.jsp">đăng nhập</a> để gửi đánh giá hoặc phản hồi.</div>
                                    </c:if>
                                    <c:if test="${sessionScope.account != null && sessionScope.account.role == 1}">
                                        <div class="alert alert-warning mt-3">Tài khoản admin không được phép gửi đánh giá hoặc phản hồi.</div>
                                    </c:if>
                                    <!-- Chỉ hiển thị form review nếu là customer -->
                                    <c:if test="${sessionScope.account != null && sessionScope.account.role == 0}">
                                        <form method="post" action="Review" enctype="multipart/form-data" id="reviewForm">
                                            <input type="hidden" name="parentReviewId" id="parentReviewId" value="" />
                                            <input type="hidden" name="reviewId" id="reviewId" value="${editingReview.reviewID}" />
                                            <input type="hidden" name="action" id="action" value="${not empty editingReview ? 'edit' : 'add'}" />
                                            <input type="hidden" name="orderId" value="${orderId}">
                                            <input type="hidden" name="productId" value="<%=mo.getProductID()%>">
                                            <div class="mb-3" id="ratingSection" style="${empty editingReview ? 'display:block' : (editingReview.parentReviewID == null or editingReview.parentReviewID == 0 ? 'display:block' : 'display:none')}">
                                                <label for="rating" class="form-label">Xếp Hạng Của Bạn</label>
                                                <div class="star-rating">
                                                    <input type="radio" id="star5" name="rating" value="5" ${not empty editingReview and editingReview.rating == 5 ? 'checked' : ''} ${empty editingReview or (editingReview.parentReviewID == null or editingReview.parentReviewID == 0) ? 'required' : ''}><label for="star5" class="fas fa-star"></label>
                                                    <input type="radio" id="star4" name="rating" value="4" ${not empty editingReview and editingReview.rating == 4 ? 'checked' : ''}><label for="star4" class="fas fa-star"></label>
                                                    <input type="radio" id="star3" name="rating" value="3" ${not empty editingReview and editingReview.rating == 3 ? 'checked' : ''}><label for="star3" class="fas fa-star"></label>
                                                    <input type="radio" id="star2" name="rating" value="2" ${not empty editingReview and editingReview.rating == 2 ? 'checked' : ''}><label for="star2" class="fas fa-star"></label>
                                                    <input type="radio" id="star1" name="rating" value="1" ${not empty editingReview and editingReview.rating == 1 ? 'checked' : ''}><label for="star1" class="fas fa-star"></label>
                                                </div>
                                            </div>
                                            <div class="mb-3">
                                                <label for="comment" class="form-label">Bình Luận/Trả Lời</label>
                                                <textarea class="form-control" id="comment" name="comment" rows="6" required>${editingReview.comment}</textarea>
                                            </div>
                                            <div class="mb-3">
                                                <label for="image" class="form-label">Tải Ảnh Lên</label>
                                                <input type="file" class="form-control" id="image" name="image" accept="image/*">
                                                <c:if test="${not empty editingReview.imageURL}">
                                                    <div class="mt-2">
                                                        <small class="text-muted">Ảnh hiện tại:</small><br>
                                                        <img src="${pageContext.request.contextPath}/ImageServlet_2?name=${editingReview.imageURL}" alt="Ảnh hiện tại" style="max-width:150px;max-height:150px;border-radius:5px;margin-top:5px;">
                                                    </div>
                                                </c:if>
                                            </div>
                                            <button type="submit" class="btn btn-success">${not empty editingReview ? 'Cập nhật' : 'Gửi'}</button>
                                            <c:if test="${not empty editingReview}">
                                                <a href="ProductDetail?id=<%=mo.getProductID()%>" class="btn btn-secondary ms-2">Hủy</a>
                                            </c:if>
                                        </form>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </div>

                    <h4 class="mb-4 product-text">Sản Phẩm Liên Quan</h4>
                    <div id="relatedProductCarousel" class="carousel slide" data-bs-ride="false">
                        <div class="carousel-inner">
                            <%
                                List<Product> related = (List<Product>) request.getAttribute("relatedProducts");
                                if (related != null && !related.isEmpty()) {
                                    for (int i = 0; i < related.size(); i += 4) {
                            %>
                            <div class="carousel-item <%= (i == 0 ? "active" : "")%>">
                                <div class="row">
                                    <%
                                        for (int j = i; j < i + 4 && j < related.size(); j++) {
                                            Product p = related.get(j);
                                    %>
                                    <div class="col-md-3">
                                        <div class="card h-100 text-center san-pham-lq">
                                            <a href="<%= request.getContextPath()%>/ProductDetail?id=<%= p.getProductID()%>">
                                                <img src="<%= request.getContextPath()%>/ImageServlet?name=<%= p.getImageURL()%>"
                                                     class="card-img-top img-fluid product-image1" style="height: 350px; object-fit: cover;" alt="<%= p.getProductName()%>">
                                                <div class="card-body">
                                                    <p class="card-title mb-1"><%= p.getProductName()%></p>
                                                    <p class="text-danger fw-bold">
                                                        <%
                                                            // Sử dụng PriceUnit và ItemUnitName trực tiếp từ Product
                                                            Double priceUnit1 = p.getPriceUnit();
                                                            String itemUnitName = p.getItemUnitName();

                                                            if (priceUnit1 != null && itemUnitName != null) {
                                                                java.text.DecimalFormat df = new java.text.DecimalFormat("#,###");
                                                                out.print(df.format(priceUnit1) + " đ / " + itemUnitName);
                                                            } else {
                                                                out.print("Chưa có giá");
                                                            }
                                                        %>
                                                    </p>
                                                </div>
                                            </a>
                                        </div>
                                    </div>
                                    <% } %>
                                </div>
                            </div>
                            <% } %>
                            <% } else { %>
                            <p class="text-muted1">Không có sản phẩm liên quan.</p>
                            <% }%>
                        </div>

                        <button class="carousel-control-prev custom-carousel-btn" type="button" data-bs-target="#relatedProductCarousel" data-bs-slide="prev">
                            <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                            <span class="visually-hidden">Previous</span>
                        </button>

                        <button class="carousel-control-next custom-carousel-btn" type="button" data-bs-target="#relatedProductCarousel" data-bs-slide="next">
                            <span class="carousel-control-next-icon" aria-hidden="true"></span>
                            <span class="visually-hidden">Next</span>
                        </button>
                    </div>
                </div>
            </div>
            <%
                }
            %>
        </div>

        <jsp:include page="footer.jsp" />

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script src="https://kit.fontawesome.com/a076d05399.js" crossorigin="anonymous"></script>
        <script src="${pageContext.request.contextPath}/assets/js/cart.js?version=<%= System.currentTimeMillis()%>"></script>

        <script>
                                                                                function setReply(parentId, orderId, productId) {
                                                                                    document.getElementById('parentReviewId').value = parentId;
                                                                                    document.getElementsByName('orderId')[0].value = orderId;
                                                                                    document.getElementsByName('productId')[0].value = productId;
                                                                                    document.getElementById('action').value = 'add';
                                                                                    document.getElementById('reviewId').value = '';
                                                                                    document.getElementById('comment').focus();
                                                                                    document.getElementById('comment').value = '';

                                                                                    // Reset rating
                                                                                    var stars = document.getElementsByName('rating');
                                                                                    for (var i = 0; i < stars.length; i++) {
                                                                                        stars[i].checked = false;
                                                                                    }

                                                                                    // Ẩn/hiện rating
                                                                                    var ratingSection = document.getElementById('ratingSection');
                                                                                    if (ratingSection) {
                                                                                        if (parentId) {
                                                                                            ratingSection.style.display = 'none';
                                                                                            // Bỏ required cho rating khi reply
                                                                                            for (var i = 0; i < stars.length; i++) {
                                                                                                stars[i].required = false;
                                                                                            }
                                                                                        } else {
                                                                                            ratingSection.style.display = 'block';
                                                                                            // Bắt buộc required khi review gốc
                                                                                            for (var i = 0; i < stars.length; i++) {
                                                                                                stars[i].required = true;
                                                                                            }
                                                                                        }
                                                                                    }

                                                                                    // Reset file input
                                                                                    document.getElementById('image').value = '';

                                                                                    // Đổi text button
                                                                                    document.querySelector('#reviewForm button[type="submit"]').textContent = 'Gửi';
                                                                                }

                                                                                function deleteReview(reviewId) {
                                                                                    if (confirm('Bạn có chắc chắn muốn xóa đánh giá này?')) {
                                                                                        window.location.href = 'Review?action=delete&reviewId=' + reviewId;
                                                                                    }
                                                                                }
        </script>

        <script>
            function toggleReply(reviewId) {
                var section = document.getElementById('replySection' + reviewId);
                var arrow = document.getElementById('arrow' + reviewId);
                if (section.style.display === 'none') {
                    section.style.display = 'block';
                    arrow.innerHTML = '&#9660;'; // ▼
                } else {
                    section.style.display = 'none';
                    arrow.innerHTML = '&#9654;'; // ▶
                }
            }
        </script>

        <style>
            .toggle-reply-box {
                display: flex;
                align-items: center;
                margin-bottom: 0.5rem;
            }
            .review-comment-text {
                white-space: pre-wrap;
                word-wrap: break-word;
                overflow-wrap: anywhere;
            }
            .reply-thread {
                position: relative;
                margin-top: 8px;
                background: transparent;
                border: 0;
                border-radius: 0;
                padding: 0;
            }
            .reply-group {
                background: #fdfaf3;
                border: 1px solid #eadfca;
                border-radius: 10px;
                padding: 10px 8px;
                margin-top: 6px;
            }
            .reply-item {
                position: relative;
                margin: 8px 0;
            }
            .reply-item:before {
                content: "";
                position: absolute;
                left: -16px;
                top: 0;
                bottom: 0;
                border-left: 2px dashed #e0cba4;
            }
            .bubble {
                background: #fffdf7;
                border: 1px solid #e8d7bc;
                border-radius: 8px;
                padding: 10px 12px;
                box-shadow: 0 1px 3px rgba(139,108,66,0.06);
                width: 100%;
                box-sizing: border-box;
            }
            .bubble:before {
                content: "";
                position: absolute;
                left: -8px;
                top: 12px;
                width:0;
                height:0;
                border-top:8px solid transparent;
                border-bottom:8px solid transparent;
                border-right:8px solid #e8d7bc;
            }
            .bubble:after {
                content: "";
                position: absolute;
                left: -7px;
                top: 12px;
                width:0;
                height:0;
                border-top:8px solid transparent;
                border-bottom:8px solid transparent;
                border-right:8px solid #fffdf7;
            }
            .bubble-staff {
                background:#f7fbff;
                border-color:#c8def2;
            }
            .bubble-staff:after {
                border-right-color:#f7fbff;
            }
            .badge-staff {
                background: #0d6efd;
                color: #fff;
                font-size: .75rem;
            }
            .badge-customer {
                background: #8b6c42;
                color: #fff;
                font-size: .75rem;
            }
            .reply-image {
                max-width: 140px;
                max-height: 140px;
                border-radius: 6px;
                border: 1px solid #e1e1e1;
            }
            .toggle-reply-box button {
                transition: all 0.3s ease;
                background: linear-gradient(135deg, #f5f2ea 0%, #e8d7bc 100%);
                border: 1px solid #d4b78f;
                color: #8b6c42;
                font-weight: 500;
                box-shadow: 0 1px 3px rgba(139, 108, 66, 0.1);
            }
            .toggle-reply-box button:hover {
                background: linear-gradient(135deg, #e8d7bc 0%, #d4b78f 100%);
                color: #6a5232;
                border-color: #b89c70;
                transform: translateY(-1px);
                box-shadow: 0 3px 8px rgba(139, 108, 66, 0.2);
            }
            .toggle-reply-box button:active {
                transform: translateY(0);
                box-shadow: 0 1px 4px rgba(139, 108, 66, 0.15);
            }
            .toggle-reply-box button:focus {
                outline: 2px solid #8b6c42;
                outline-offset: 2px;
            }
            .action-buttons {
                display: flex;
                gap: 8px;
                flex-wrap: wrap;
            }
            .action-buttons .btn-link {
                padding: 6px 12px;
                font-size: 0.875rem;
                color: #8b6c42;
                text-decoration: none;
                border-radius: 6px;
                transition: all 0.3s ease;
                background: linear-gradient(135deg, #f5f2ea 0%, #e8d7bc 100%);
                border: 1px solid #d4b78f;
                font-weight: 500;
                box-shadow: 0 1px 3px rgba(139, 108, 66, 0.1);
                display: inline-block;
                min-width: 80px;
                text-align: center;
            }
            .action-buttons .btn-link:hover {
                background: linear-gradient(135deg, #e8d7bc 0%, #d4b78f 100%);
                color: #6a5232;
                text-decoration: none;
                border-color: #b89c70;
                transform: translateY(-1px);
                box-shadow: 0 3px 8px rgba(139, 108, 66, 0.2);
            }
            .action-buttons .btn-link:active {
                transform: translateY(0);
                box-shadow: 0 1px 4px rgba(139, 108, 66, 0.15);
            }
            .action-buttons .btn-link:focus {
                outline: 2px solid #8b6c42;
                outline-offset: 2px;
            }
            .review-comment {
                border-left: 3px solid #e9ecef;
                padding-left: 15px;
                margin-bottom: 15px;
            }
            .flash-sale-banner {
                background: linear-gradient(to right, #ff512f, #dd2476);
                border-radius: 8px;
                padding: 15px;
                color: #fff;
                font-family: 'Arial', sans-serif;
            }

            .flash-sale-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                font-size: 1rem;
                font-weight: bold;
            }

            .flash-sale-label {
                font-size: 1.2rem;
                display: flex;
                align-items: center;
            }

            .flash-sale-label::before {
                content: "⚡";
                margin-right: 5px;
                font-size: 1.4rem;
            }

            .countdown-text {
                display: flex;
                align-items: center;
                gap: 6px;
                font-size: 0.75rem;
            }

            .countdown-text i {
                margin-right: 4px;
            }

            .countdown-box {
                display: inline-block;
                background-color: #000;
                color: #fff;
                font-weight: bold;
                padding: 2px 8px;
                border-radius: 4px;
                font-family: 'Courier New', monospace;
                min-width: 30px;
                text-align: center;
                box-shadow: 0 2px 4px rgba(0,0,0,0.2);
            }

            .flash-sale-price {
                font-size: 2rem;
                color: #fefefe;
                font-weight: bold;
            }

            .original-price {
                color: #ccc;
                text-decoration: line-through;
                margin-left: 10px;
            }

            .discount-percent {
                color: #ffcccb;
                font-weight: bold;
                margin-left: 10px;
            }
        </style>

        <script>
            (function () {
                const endTime = new Date("${appliedPromotion.endDate}").getTime();

                const dd = document.getElementById("timer-dd");
                const hh = document.getElementById("timer-hh");
                const mm = document.getElementById("timer-mm");
                const ss = document.getElementById("timer-ss");

                const timer = setInterval(function () {
                    const now = new Date().getTime();
                    const distance = endTime - now;

                    if (distance < 0) {
                        clearInterval(timer);
                        dd.innerHTML = hh.innerHTML = mm.innerHTML = ss.innerHTML = "00";
                        return;
                    }

                    const days = String(Math.floor(distance / (1000 * 60 * 60 * 24))).padStart(2, "0");
                    const hours = String(Math.floor((distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))).padStart(2, "0");
                    const minutes = String(Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60))).padStart(2, "0");
                    const seconds = String(Math.floor((distance % (1000 * 60)) / 1000)).padStart(2, "0");

                    dd.innerHTML = days;
                    hh.innerHTML = hours;
                    mm.innerHTML = minutes;
                    ss.innerHTML = seconds;
                }, 1000);
            })();
        </script>

        <!-- Script để xử lý thông báo từ session -->
        <script>
            // Kiểm tra và hiển thị thông báo từ session
            document.addEventListener('DOMContentLoaded', function() {
                // Kiểm tra cartError
                <% if (session.getAttribute("cartError") != null) { %>
                    const errorMessage = '<%= session.getAttribute("cartError") %>';
                    Swal.fire({
                        icon: 'error',
                        title: 'Lỗi!',
                        text: errorMessage,
                        confirmButtonText: 'Đóng',
                        confirmButtonColor: '#d33'
                    });
                    <% session.removeAttribute("cartError"); %>
                <% } %>
                
                // Kiểm tra cartMessage
                <% if (session.getAttribute("cartMessage") != null) { %>
                    const successMessage = '<%= session.getAttribute("cartMessage") %>';
                    Swal.fire({
                        icon: 'success',
                        title: 'Thành công!',
                        text: successMessage,
                        confirmButtonText: 'Đóng',
                        confirmButtonColor: '#28a745'
                    });
                    <% session.removeAttribute("cartMessage"); %>
                <% } %>
            });
        </script>
<script>
    function formatVND(n) {
    try {
        return new Intl.NumberFormat('vi-VN').format(n);
    } catch (e) {
        return n;
    }
}

function updateButtonPrices(discountPercent) {
    document.querySelectorAll('.unit-btn').forEach(btn => {
        const base = parseFloat(btn.getAttribute('data-base') || '0');
        
        // Giá gốc chưa giảm
        let original = 0;
        if (discountPercent > 0) {
            original = base / (1 - discountPercent / 100);
        } else {
            original = base;
        }

        const currentEl = btn.querySelector('.btn-price-current');
        const originalEl = btn.querySelector('.btn-price-original');
        const percentEl = btn.querySelector('.btn-price-percent');

        // Gán giá khuyến mãi
        currentEl.textContent = formatVND(base) + "₫";

        if (discountPercent > 0) {
            // Gán giá gốc + hiển thị
            originalEl.textContent = formatVND(original) + "₫";
            originalEl.style.display = "block";

            // Gán phần trăm giảm
            percentEl.textContent = "-" + discountPercent + "%";
            percentEl.style.display = "inline-block";
        } else {
            originalEl.style.display = "none";
            percentEl.style.display = "none";
        }
    });
}

// gọi khi load trang (truyền từ server vào)
updateButtonPrices(${appliedPromotion.discountPercent});

    </script>
    </body>
</html>