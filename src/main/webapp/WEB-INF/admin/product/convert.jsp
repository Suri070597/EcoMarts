<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Chuyển đổi đơn vị - ${productConvert.productName}</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">

        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/css/bootstrap-icons.min.css">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis()%>">
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis()%>">
        
        <style>
            .info-item {
                background: #f8f9fa;
                border-radius: 8px;
                padding: 15px;
                border-left: 4px solid #007bff;
            }
            
            .info-label {
                font-size: 0.875rem;
                color: #6c757d;
                margin-bottom: 8px;
                font-weight: 500;
            }
            
            .info-value {
                font-size: 1rem;
            }
            
            .lot-card {
                background: #fff;
                border: 1px solid #dee2e6;
                border-radius: 8px;
                padding: 15px;
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
                transition: all 0.3s ease;
            }
            
            .lot-card:hover {
                box-shadow: 0 4px 8px rgba(0,0,0,0.15);
                transform: translateY(-2px);
            }
            
            .lot-header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 12px;
                padding-bottom: 8px;
                border-bottom: 1px solid #e9ecef;
            }
            
            .lot-number {
                font-weight: bold;
                color: #007bff;
                font-size: 1.1rem;
            }
            
            .lot-quantity {
                background: #28a745;
                color: white;
                padding: 4px 8px;
                border-radius: 12px;
                font-size: 0.875rem;
                font-weight: 500;
            }
            
            .lot-details {
                display: flex;
                flex-direction: column;
                gap: 8px;
            }
            
            .lot-item {
                display: flex;
                align-items: center;
                color: #6c757d;
                font-size: 0.875rem;
            }
            
            .conversion-options {
                background: #f8f9fa;
                border-radius: 8px;
                padding: 15px;
                border: 1px solid #e9ecef;
            }
            
            .form-check-input:checked {
                background-color: #007bff;
                border-color: #007bff;
            }
            
            .btn-convert {
                background: linear-gradient(135deg, #007bff, #0056b3);
                border: none;
                color: white;
                padding: 12px 30px;
                border-radius: 8px;
                font-weight: 600;
                transition: all 0.3s ease;
            }
            
            .btn-convert:hover {
                background: linear-gradient(135deg, #0056b3, #004085);
                transform: translateY(-2px);
                box-shadow: 0 4px 12px rgba(0,123,255,0.3);
            }
            
            .card-header {
                background: linear-gradient(135deg, #f8f9fa, #e9ecef);
                border-bottom: 1px solid #dee2e6;
            }
            
            .card-title {
                color: #495057;
                font-weight: 600;
            }
        </style>
    </head>
    <body>
        <div class="container-fluid">
            <%-- Include admin sidebar --%>
            <jsp:include page="../components/sidebar.jsp" />

            <div class="main-content">
                <!-- Header -->
                <div class="card">
                    <div class="card-header">
                        <div class="header-actions d-flex justify-content-between align-items-center">
                            <h1 class="card-title">
                                <i class="fas fa-exchange-alt me-2"></i>Chuyển đổi đơn vị
                            </h1>
                            <a href="${pageContext.request.contextPath}/admin/product" class="btn btn-secondary">
                                <i class="fas fa-arrow-left me-2"></i>Quay lại
                            </a>
                        </div>
                    </div>
                </div>

                <!-- Thông tin sản phẩm -->
                <div class="card mb-4">
                    <div class="card-header">
                        <h5 class="card-title mb-0">
                            <i class="fas fa-info-circle me-2"></i>Thông tin sản phẩm
                        </h5>
                    </div>
                    <div class="card-body">

                    <c:if test="${productConvert.unitPerBox <= 0 or empty productConvert.boxUnitName or empty productConvert.itemUnitName}">
                        <div class="alert alert-warning">
                            <i class="fas fa-exclamation-triangle me-2"></i>
                            <strong>Cảnh báo:</strong> Sản phẩm chưa được thiết lập đầy đủ thông tin đơn vị. Vui lòng cập nhật thông tin sản phẩm trước khi chuyển đổi.
                        </div>
                    </c:if>
                        <div class="row g-3">
                            <div class="col-md-6 col-lg-3">
                                <div class="info-item">
                                    <div class="info-label">Đơn vị trong thùng</div>
                                    <div class="info-value">
                                        <c:choose>
                                            <c:when test="${productConvert.unitPerBox > 0}">
                                                <span class="badge bg-success">${productConvert.unitPerBox}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-danger">Chưa thiết lập</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6 col-lg-3">
                                <div class="info-item">
                                    <div class="info-label">Giá tham khảo</div>
                                    <div class="info-value">
                                        <c:choose>
                                            <c:when test="${productConvert.price != null}">
                                                <span class="text-success fw-bold">
                                                    <fmt:formatNumber value="${productConvert.price}" type="currency" currencySymbol="₫"/>
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">Chưa thiết lập</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6 col-lg-3">
                                <div class="info-item">
                                    <div class="info-label">Đơn vị thùng</div>
                                    <div class="info-value">
                                        <c:choose>
                                            <c:when test="${not empty productConvert.boxUnitName}">
                                                <span class="badge bg-primary">${productConvert.boxUnitName}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">Chưa thiết lập</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6 col-lg-3">
                                <div class="info-item">
                                    <div class="info-label">Đơn vị item</div>
                                    <div class="info-value">
                                        <c:choose>
                                            <c:when test="${not empty productConvert.itemUnitName}">
                                                <span class="badge bg-info">${productConvert.itemUnitName}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">Chưa thiết lập</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </div>
                            <div class="col-12">
                                <div class="info-item">
                                    <div class="info-label">Danh mục</div>
                                    <div class="info-value">
                                        <c:choose>
                                            <c:when test="${productConvert.category != null}">
                                                <span class="badge bg-secondary">${productConvert.category.categoryName}</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">Chưa phân loại</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Chọn lô hàng -->
                <div class="card mb-4">
                    <div class="card-header">
                        <h5 class="card-title mb-0">
                            <i class="fas fa-boxes me-2"></i>Chọn lô hàng để chuyển đổi
                        </h5>
                    </div>
                    <div class="card-body">
                        <c:if test="${empty availableLots}">
                            <div class="alert alert-warning">
                                <i class="fas fa-exclamation-triangle me-2"></i>
                                <strong>Cảnh báo:</strong> Không có lô hàng BOX nào có sẵn để chuyển đổi!
                            </div>
                        </c:if>

                        <c:if test="${not empty availableLots}">
                            <div class="row g-3 mb-4">
                                <div class="col-md-6">
                                    <div class="form-check">
                                        <input class="form-check-input" type="radio" name="lotSelection" id="allLots" value="all" checked>
                                        <label class="form-check-label" for="allLots">
                                            <strong>Chuyển đổi từ tất cả các lô BOX</strong>
                                        </label>
                                    </div>
                                </div>
                                <div class="col-md-6">
                                    <div class="form-check">
                                        <input class="form-check-input" type="radio" name="lotSelection" id="specificLot" value="specific">
                                        <label class="form-check-label" for="specificLot">
                                            <strong>Chọn lô cụ thể</strong>
                                        </label>
                                    </div>
                                </div>
                            </div>

                            <div class="mt-3" id="specificLotSelection" style="display: none;">
                                <label for="specificLotId" class="form-label">Chọn lô:</label>
                                <select class="form-select" id="specificLotId" name="specificLotId">
                                    <option value="">-- Chọn lô --</option>
                                    <c:forEach var="lot" items="${availableLots}">
                                        <option value="${lot.inventoryID}">
                                            Lô ${lot.lotNumber} - ${lot.quantity} thùng 
                                            (Ngày nhập: <fmt:formatDate value="${lot.lotDate}" pattern="dd/MM/yyyy"/>)
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>

                            <!-- Hiển thị thông tin các lô -->
                            <div class="mt-4">
                                <h6 class="mb-3">
                                    <i class="fas fa-list me-2"></i>Danh sách lô hàng có sẵn
                                </h6>
                                <div class="row g-3">
                                    <c:forEach var="lot" items="${availableLots}">
                                        <div class="col-md-6 col-lg-4">
                                            <div class="lot-card">
                                                <div class="lot-header">
                                                    <span class="lot-number">Lô ${lot.lotNumber}</span>
                                                    <span class="lot-quantity">${lot.quantity} thùng</span>
                                                </div>
                                                <div class="lot-details">
                                                    <div class="lot-item">
                                                        <i class="fas fa-calendar-plus me-2"></i>
                                                        <fmt:formatDate value="${lot.lotDate}" pattern="dd/MM/yyyy"/>
                                                    </div>
                                                    <div class="lot-item">
                                                        <i class="fas fa-calendar-times me-2"></i>
                                                        <fmt:formatDate value="${lot.expiryDate}" pattern="dd/MM/yyyy"/>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </c:forEach>
                                </div>
                            </div>
                        </c:if>
                    </div>
                </div>

                <!-- Form chuyển đổi -->
                <c:if test="${not empty availableLots and productConvert.unitPerBox > 0 and not empty productConvert.boxUnitName and not empty productConvert.itemUnitName}">
                    <div class="card mb-4">
                        <div class="card-header">
                            <h5 class="card-title mb-0">
                                <i class="fas fa-cogs me-2"></i>Thiết lập chuyển đổi
                            </h5>
                        </div>
                        <div class="card-body">
                            <form id="conversionForm">
                                <input type="hidden" name="productId" value="${productConvert.productID}">
                                <input type="hidden" name="action" value="convertUnits">

                                <div class="row g-3">
                                    <div class="col-md-6">
                                        <label for="boxesToConvert" class="form-label">
                                            <strong>Số lượng thùng cần chuyển đổi</strong>
                                        </label>
                                        <input type="number" class="form-control" id="boxesToConvert" name="boxesToConvert" 
                                               min="1" max="999" required>
                                        <div class="form-text">Nhập số lượng thùng muốn chuyển đổi</div>
                                        <div id="stockWarning" class="text-danger mt-1" style="display: none;"></div>
                                    </div>

                                    <div class="col-md-6">
                                        <label class="form-label"><strong>Loại chuyển đổi:</strong></label>
                                        <div class="conversion-options">
                                            <div class="form-check mb-2">
                                                <input class="form-check-input" type="radio" name="conversionType" id="unitType" value="unit" checked>
                                                <label class="form-check-label" for="unitType">
                                                    <strong>Chuyển sang lon</strong>
                                                </label>
                                            </div>
                                            <div class="form-check mb-2">
                                                <input class="form-check-input" type="radio" name="conversionType" id="packType" value="pack">
                                                <label class="form-check-label" for="packType">
                                                    <strong>Chuyển sang lốc</strong>
                                                </label>
                                            </div>
                                    <div class="col-md-4">
                                        <div class="form-check">
                                            <input class="form-check-input" type="radio" name="conversionType" id="bothType" value="both">
                                            <label class="form-check-label" for="bothType">
                                                <strong>Chuyển cả hai</strong>
                                            </label>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="mb-3" id="packSizeDiv" style="display: none;">
                                <label for="packSize" class="form-label">
                                    <strong>Số lon = 1 lốc</strong>
                                </label>
                                <input type="number" class="form-control" id="packSize" name="packSize" 
                                       min="2" max="${productConvert.unitPerBox - 1}">
                                <div class="form-text">
                                    Nhập số lon để tạo thành 1 lốc (từ 2 đến ${productConvert.unitPerBox - 1})
                                </div>
                                <div id="packSizeWarning" class="text-danger mt-1" style="display: none;"></div>
                            </div>

                            <!-- Kết quả tính toán -->
                            <div class="mb-3">
                                <div class="card">
                                    <div class="card-header bg-light">
                                        <h6 class="mb-0"><i class="fas fa-calculator me-2"></i>Kết quả tính toán</h6>
                                    </div>
                                    <div class="card-body">
                                        <div class="row">
                                            <div class="col-md-4">
                                                <strong>Tổng số lon:</strong> <span id="totalUnits">-</span>
                                            </div>
                                            <div class="col-md-4">
                                                <strong>Số lốc:</strong> <span id="packCount">-</span>
                                            </div>
                                            <div class="col-md-4">
                                                <strong>Giá lốc:</strong> <span id="packPrice">-</span>
                                            </div>
                                        </div>
                                        <div class="row mt-2">
                                            <div class="col-12">
                                                <small class="text-muted">
                                                    <i class="fas fa-info-circle me-1"></i>
                                                    Kết quả sẽ được tính toán tự động khi bạn nhập số liệu
                                                </small>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                                <a href="${pageContext.request.contextPath}/admin/product" class="btn btn-secondary me-md-2">
                                    <i class="fas fa-arrow-left me-2"></i>Quay lại
                                </a>
                                <button type="submit" class="btn btn-convert">
                                    <i class="fas fa-exchange-alt me-2"></i>Thực hiện chuyển đổi
                                </button>
                            </div>
                        </form>
                    </div>
                </c:if>

                <c:if test="${empty availableLots or productConvert.unitPerBox <= 0 or empty productConvert.boxUnitName or empty productConvert.itemUnitName}">
                    <div class="alert alert-info text-center">
                        <i class="fas fa-info-circle me-2"></i>
                        <strong>Không thể thực hiện chuyển đổi</strong><br>
                        <c:choose>
                            <c:when test="${empty availableLots}">
                                Không có lô hàng BOX nào có sẵn để chuyển đổi.
                            </c:when>
                            <c:otherwise>
                                Sản phẩm chưa được thiết lập đầy đủ thông tin đơn vị.
                            </c:otherwise>
                        </c:choose>
                    </div>
                </c:if>
            </div>

            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
            <script>
                // Xử lý hiển thị/ẩn dropdown chọn lô cụ thể
                document.querySelectorAll('input[name="lotSelection"]').forEach(radio => {
                    radio.addEventListener('change', function () {
                        const specificLotSelection = document.getElementById('specificLotSelection');
                        if (this.value === 'specific') {
                            specificLotSelection.style.display = 'block';
                        } else {
                            specificLotSelection.style.display = 'none';
                        }
                    });
                });

                // Xử lý hiển thị/ẩn form nhập số lon = lốc
                document.querySelectorAll('input[name="conversionType"]').forEach(radio => {
                    radio.addEventListener('change', function () {
                        const packSizeDiv = document.getElementById('packSizeDiv');
                        if (this.value === 'pack' || this.value === 'both') {
                            packSizeDiv.style.display = 'block';
                            document.getElementById('packSize').required = true;
                        } else {
                            packSizeDiv.style.display = 'none';
                            document.getElementById('packSize').required = false;
                            document.getElementById('packSize').value = '';
                        }
                        calculateConversion();
                        clearWarnings();
                    });
                });

                // Tính toán kết quả khi thay đổi input
                function calculateConversion() {
                    const boxesToConvert = parseInt(document.getElementById('boxesToConvert').value) || 0;
                    const packSize = parseInt(document.getElementById('packSize').value) || 0;
                    const conversionType = document.querySelector('input[name="conversionType"]:checked').value;
                    const unitPerBox = ${productConvert.unitPerBox};
                    const productPrice = ${productConvert.price != null ? productConvert.price : 0};

                    // Kiểm tra số lượng thùng
                    let totalStock = 0;
                    const lotSelection = document.querySelector('input[name="lotSelection"]:checked').value;
                    if (lotSelection === 'specific') {
                        const specificLotId = document.getElementById('specificLotId').value;
                        if (specificLotId) {
                            // Lấy số lượng từ lô cụ thể (cần implement API)
                            totalStock = 999; // Tạm thời để test
                        }
                    } else {
                        // Lấy tổng số lượng từ tất cả các lô BOX
                        totalStock = 0;
                <c:forEach var="lot" items="${availableLots}">
                        totalStock += ${lot.quantity};
                </c:forEach>
                    }

                    // Kiểm tra và hiển thị cảnh báo số lượng
                    const stockWarning = document.getElementById('stockWarning');
                    if (boxesToConvert > totalStock) {
                        stockWarning.textContent = `❌ Số lượng thùng chuyển đổi (${boxesToConvert}) vượt quá số lượng hiện có (${totalStock} thùng)!`;
                        stockWarning.style.display = 'block';
                        document.getElementById('totalUnits').textContent = 'Không thể chuyển đổi!';
                        document.getElementById('packCount').textContent = '-';
                        document.getElementById('packPrice').textContent = '-';
                        return;
                    } else {
                        stockWarning.style.display = 'none';
                    }

                    if (boxesToConvert > 0 && unitPerBox > 0) {
                        const totalUnits = boxesToConvert * unitPerBox;
                        document.getElementById('totalUnits').textContent = totalUnits + ' lon';

                        if ((conversionType === 'pack' || conversionType === 'both') && packSize > 0) {
                            // Kiểm tra tính chia hết
                            if (unitPerBox % packSize !== 0) {
                                document.getElementById('packCount').textContent = 'Lỗi: Số lon trong thùng không chia hết cho số lon/lốc!';
                                document.getElementById('packPrice').textContent = '-';
                                showPackSizeWarning('Số lon trong thùng (' + unitPerBox + ') không chia hết cho số lon/lốc (' + packSize + ')!');
                                return;
                            }

                            if (totalUnits % packSize !== 0) {
                                document.getElementById('packCount').textContent = 'Lỗi: Tổng số lon không chia hết cho số lon/lốc!';
                                document.getElementById('packPrice').textContent = '-';
                                showPackSizeWarning('Tổng số lon (' + totalUnits + ') không chia hết cho số lon/lốc (' + packSize + ')! Sẽ có dư ' + (totalUnits % packSize) + ' lon!');
                                return;
                            }

                            // Nếu chia hết hoàn toàn
                            const packCount = totalUnits / packSize;
                            const packPrice = (productPrice / unitPerBox) * packSize;
                            document.getElementById('packCount').textContent = packCount + ' lốc';
                            document.getElementById('packPrice').textContent = new Intl.NumberFormat('vi-VN').format(packPrice) + ' ₫';
                            hidePackSizeWarning();
                        } else {
                            document.getElementById('packCount').textContent = '-';
                            document.getElementById('packPrice').textContent = '-';
                        }
                    } else if (unitPerBox <= 0) {
                        document.getElementById('totalUnits').textContent = 'Lỗi: Đơn vị trong thùng = 0';
                        document.getElementById('packCount').textContent = '-';
                        document.getElementById('packPrice').textContent = '-';
                    } else {
                        document.getElementById('totalUnits').textContent = '-';
                        document.getElementById('packCount').textContent = '-';
                        document.getElementById('packPrice').textContent = '-';
                    }
                }

                // Hiển thị cảnh báo số lon/lốc
                function showPackSizeWarning(message) {
                    const packSizeWarning = document.getElementById('packSizeWarning');
                    packSizeWarning.textContent = '❌ ' + message;
                    packSizeWarning.style.display = 'block';
                }

                // Ẩn cảnh báo số lon/lốc
                function hidePackSizeWarning() {
                    const packSizeWarning = document.getElementById('packSizeWarning');
                    packSizeWarning.style.display = 'none';
                }

                // Xóa tất cả cảnh báo
                function clearWarnings() {
                    document.getElementById('stockWarning').style.display = 'none';
                    document.getElementById('packSizeWarning').style.display = 'none';
                }

                // Gắn event listeners
                document.getElementById('boxesToConvert').addEventListener('input', calculateConversion);
                document.getElementById('packSize').addEventListener('input', calculateConversion);

                // Xử lý form submit
                document.getElementById('conversionForm').addEventListener('submit', function (e) {
                    e.preventDefault();

                    const lotSelection = document.querySelector('input[name="lotSelection"]:checked').value;
                    const specificLotId = document.getElementById('specificLotId').value;
                    const boxesToConvert = parseInt(document.getElementById('boxesToConvert').value) || 0;
                    const conversionType = document.querySelector('input[name="conversionType"]:checked').value;
                    const packSize = parseInt(document.getElementById('packSize').value) || 0;

                    // Validation
                    if (lotSelection === 'specific' && !specificLotId) {
                        alert('❌ Vui lòng chọn lô cụ thể!');
                        return;
                    }

                    if (boxesToConvert <= 0) {
                        alert('❌ Vui lòng nhập số lượng thùng hợp lệ!');
                        document.getElementById('boxesToConvert').focus();
                        return;
                    }

                    // Kiểm tra số lượng thùng
                    let totalStock = 0;
                    if (lotSelection === 'specific') {
                        if (specificLotId) {
                            // Lấy số lượng từ lô cụ thể (cần implement API)
                            totalStock = 999; // Tạm thời để test
                        }
                    } else {
                        // Lấy tổng số lượng từ tất cả các lô BOX
                        totalStock = 0;
                <c:forEach var="lot" items="${availableLots}">
                        totalStock += ${lot.quantity};
                </c:forEach>
                    }

                    if (boxesToConvert > totalStock) {
                        alert(`❌ Số lượng thùng chuyển đổi (${boxesToConvert}) vượt quá số lượng hiện có (${totalStock} thùng)!`);
                        document.getElementById('boxesToConvert').focus();
                        return;
                    }

                    // Kiểm tra số lon/lốc nếu cần
                    if ((conversionType === 'pack' || conversionType === 'both') && packSize <= 0) {
                        alert('❌ Vui lòng nhập số lon = 1 lốc!');
                        document.getElementById('packSize').focus();
                        return;
                    }

                    if ((conversionType === 'pack' || conversionType === 'both') && packSize > 0) {
                        const unitPerBox = ${productConvert.unitPerBox};
                        const totalUnits = boxesToConvert * unitPerBox;

                        if (unitPerBox % packSize !== 0) {
                            alert(`❌ Số lon trong thùng (${unitPerBox}) không chia hết cho số lon/lốc (${packSize})!`);
                            document.getElementById('packSize').focus();
                            return;
                        }

                        if (totalUnits % packSize !== 0) {
                            alert(`❌ Tổng số lon (${totalUnits}) không chia hết cho số lon/lốc (${packSize})! Sẽ có dư ${totalUnits % packSize} lon!`);
                            document.getElementById('packSize').focus();
                            return;
                        }
                    }

                    // Tạo FormData và gửi request
                    const formData = new FormData(this);
                    formData.append('lotSelection', lotSelection);
                    if (lotSelection === 'specific') {
                        formData.append('specificLotId', specificLotId);
                    }

                    // Hiển thị loading
                    const submitBtn = this.querySelector('button[type="submit"]');
                    const originalText = submitBtn.innerHTML;
                    submitBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Đang xử lý...';
                    submitBtn.disabled = true;

                    fetch('${pageContext.request.contextPath}/admin/product', {
                        method: 'POST',
                        body: formData
                    })
                            .then(response => response.json())
                            .then(data => {
                                if (data.success) {
                                    alert('✅ ' + data.message);
                                    window.location.href = '${pageContext.request.contextPath}/admin/product';
                                } else {
                                    alert('❌ ' + data.message);
                                }
                            })
                            .catch(error => {
                                console.error('Error:', error);
                                alert('Đã xảy ra lỗi khi chuyển đổi!');
                            })
                            .finally(() => {
                                submitBtn.innerHTML = originalText;
                                submitBtn.disabled = false;
                            });
                });

                // Tính toán ban đầu
                calculateConversion();
            </script>
    </body>
</html>
