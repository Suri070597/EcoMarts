<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thiết lập giá bán - EcoMart</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">

        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/css/bootstrap-icons.min.css">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
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
                <main class="p-4">
                     <h1>Thiết lập giá bán</h1>
                    <div class="row">
                        <!-- Product Information -->
                        <div>
                            <div class="card">
                                <div class="card-header">
                                    <h5 class="card-title mb-0">
                                        <i class="fas fa-info-circle me-2"></i>Thông tin sản phẩm
                                    </h5>
                                </div>
                                <div class="card-body">
                                    <div class="row">
                                        <div class="col-md-6">
                                            <p><strong>Tên sản phẩm:</strong> ${productPrice.productName}</p>
                                            <p><strong>Mô tả:</strong> ${productPrice.description}</p>
                                            <p><strong>Danh mục:</strong> ${productPrice.category.categoryName}</p>
                                            <p><strong>Nhà sản xuất:</strong> ${productPrice.manufacturer.companyName}</p>
                                        </div>
                                        <div class="col-md-6">
                                            <p><strong>Cấu trúc đóng gói:</strong></p>
                                            <ul class="list-unstyled">
                                                <li>• 1 ${productPrice.boxUnitName} = ${productPrice.unitPerBox} ${productPrice.itemUnitName}</li>
                                            </ul>
                                            <p><strong>Giá bán hiện tại:</strong> 
                                                <c:choose>
                                                    <c:when test="${productPrice.price != null && productPrice.price > 0}">
                                                        <span class="text-success">${productPrice.price}đ/${productPrice.boxUnitName}</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="text-warning">Chưa thiết lập</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </p>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <!-- Inventory Information -->
                            <div class="card mt-3">
                                <div class="card-header">
                                    <h5 class="card-title mb-0">
                                        <i class="fas fa-boxes me-2"></i>Thông tin kho hàng
                                    </h5>
                                </div>
                                <div class="card-body">
                                    <!-- Error Messages -->
                                    <c:if test="${not empty error}">
                                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                            <i class="fas fa-exclamation-triangle me-2"></i>
                                            ${error}
                                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                                        </div>
                                    </c:if>
                                    
                                    <!-- Error Messages from URL -->
                                    <c:if test="${not empty param.error}">
                                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                            <i class="fas fa-exclamation-triangle me-2"></i>
                                            <c:choose>
                                                <c:when test="${param.error == 'cannot_delete'}">
                                                    ❌ Không thể xóa sản phẩm này!
                                                </c:when>
                                                <c:when test="${param.error == 'reference_price_update_failed'}">
                                                    ❌ Thiết lập giá tham khảo thất bại!
                                                </c:when>
                                                <c:when test="${param.error == 'invalid_reference_price'}">
                                                    ❌ Giá tham khảo không hợp lệ!
                                                </c:when>
                                                <c:when test="${param.error == 'reference_price_error'}">
                                                    ❌ Đã xảy ra lỗi khi thiết lập giá tham khảo!
                                                </c:when>
                                                <c:otherwise>
                                                    ❌ ${param.error}
                                                </c:otherwise>
                                            </c:choose>
                                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                                        </div>
                                    </c:if>
                                    
                                    <!-- Success Messages -->
                                    <c:if test="${not empty success}">
                                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                                            <i class="fas fa-check-circle me-2"></i>
                                            ${success}
                                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                                        </div>
                                    </c:if>
                                    
                                    <!-- Success Messages from URL -->
                                    <c:if test="${not empty param.success}">
                                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                                            <i class="fas fa-check-circle me-2"></i>
                                            <c:choose>
                                                <c:when test="${param.success == 'price_updated'}">
                                                    ✅ Thiết lập giá bán thành công!
                                                </c:when>
                                                <c:when test="${param.success == 'reference_price_updated'}">
                                                    ✅ Thiết lập giá tham khảo thành công!
                                                </c:when>
                                                <c:otherwise>
                                                    ✅ ${param.success}
                                                </c:otherwise>
                                            </c:choose>
                                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                                        </div>
                                    </c:if>
                                    
                                    <!-- Pricing Analysis Section -->
                                    <div class="card mb-3">
                                        <div class="card-header bg-info text-white">
                                            <h6 class="card-title mb-0">
                                                <i class="fas fa-chart-line me-2"></i>Phân tích giá & Gợi ý
                                            </h6>
                                        </div>
                                        <div class="card-body">
                                            <!-- Cost Analysis -->
                                            <div class="row mb-3">
                                                <div class="col-md-6">
                                                    <h6><i class="fas fa-calculator me-2"></i>Phân tích chi phí:</h6>
                                                    <c:set var="totalCost" value="0"/>
                                                    <c:set var="totalQuantity" value="0"/>
                                                    <c:set var="minCost" value="999999999"/>
                                                    <c:set var="maxCost" value="0"/>
                                                    <c:set var="lotCount" value="0"/>
                                                    
                                                    <c:forEach var="lot" items="${lots}">
                                                        <c:if test="${lot.packageType == 'BOX' && lot.costPrice > 0}">
                                                            <c:set var="totalCost" value="${totalCost + (lot.costPrice * lot.quantity)}"/>
                                                            <c:set var="totalQuantity" value="${totalQuantity + lot.quantity}"/>
                                                            <c:if test="${lot.costPrice < minCost}">
                                                                <c:set var="minCost" value="${lot.costPrice}"/>
                                                            </c:if>
                                                            <c:if test="${lot.costPrice > maxCost}">
                                                                <c:set var="maxCost" value="${lot.costPrice}"/>
                                                            </c:if>
                                                            <c:set var="lotCount" value="${lotCount + 1}"/>
                                                        </c:if>
                                                    </c:forEach>
                                                    
                                                    <c:if test="${lotCount > 0}">
                                                        <c:set var="avgCost" value="${totalCost / totalQuantity}"/>
                                                        <ul class="list-unstyled">
                                                            <li><strong>Giá nhập thấp nhất:</strong> <span class="text-success">${minCost}đ</span></li>
                                                            <li><strong>Giá nhập cao nhất:</strong> <span class="text-warning">${maxCost}đ</span></li>
                                                            <li><strong>Giá nhập trung bình:</strong> <span class="text-info">${avgCost}đ</span></li>
                                                            <li><strong>Số lô có sẵn:</strong> <span class="text-primary">${lotCount}</span></li>
                                                        </ul>
                                                    </c:if>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    
                                    <!-- Reference Price Section -->
                                    <div class="card mb-3">
                                        <div class="card-header bg-primary text-white">
                                            <h6 class="card-title mb-0">
                                                <i class="fas fa-tag me-2"></i>Thiết lập giá tham khảo
                                            </h6>
                                        </div>
                                        <div class="card-body">
                                            <p class="text-muted mb-3">
                                                <i class="fas fa-info-circle me-2"></i>
                                                <strong>Giá tham khảo</strong> là giá chuẩn thị trường, được hiển thị cho khách hàng và dùng làm cơ sở tính toán.
                                            </p>
                                            
                                            <form action="${pageContext.request.contextPath}/admin/product" method="post" class="mb-3">
                                                <input type="hidden" name="action" value="set-reference-price">
                                                <input type="hidden" name="productId" value="${productPrice.productID}">
                                                
                                                <div class="row">
                                                    <div class="col-md-8">
                                                        <label for="referencePrice" class="form-label">
                                                            <strong>Giá tham khảo (đồng/${productPrice.boxUnitName})</strong>
                                                        </label>
                                                        <input type="number" 
                                                               class="form-control" 
                                                               id="referencePrice" 
                                                               name="referencePrice" 
                                                               min="0" 
                                                               step="1000"
                                                               value="${productPrice.price != null ? productPrice.price : ''}"
                                                               placeholder="Nhập giá tham khảo..."
                                                               required>
                                                        <div class="form-text">
                                                            Giá chuẩn thị trường cho 1 ${productPrice.boxUnitName}
                                                        </div>
                                                    </div>
                                                    <div class="col-md-4">
                                                        <label class="form-label">&nbsp;</label>
                                                        <div class="d-grid">
                                                            <button type="submit" class="btn btn-primary">
                                                                <i class="fas fa-save me-2"></i>Lưu giá tham khảo
                                                            </button>
                                                        </div>
                                                    </div>
                                                </div>
                                            </form>
                                        </div>
                                    </div>
                                    
                                    <!-- Lot-Specific Pricing Section -->
                                    <div class="card mb-3">
                                        <div class="card-header bg-warning text-dark">
                                            <h6 class="card-title mb-0">
                                                <i class="fas fa-box me-2"></i>Thiết lập giá bán theo lô
                                            </h6>
                                        </div>
                                        <div class="card-body">
                                            <p class="text-muted mb-3">
                                                <i class="fas fa-info-circle me-2"></i>
                                                <strong>Giá bán theo lô</strong> là giá thực tế khi bán hàng, có thể khác nhau giữa các lô.
                                            </p>
                                            
                                            <form action="${pageContext.request.contextPath}/admin/product" method="post">
                                                <input type="hidden" name="action" value="set-price">
                                                <input type="hidden" name="productId" value="${productPrice.productID}">
                                                <input type="hidden" name="inventoryId" value="${param.inventoryId}">
                                                
                                                <!-- Dropdown chọn lô hàng -->
                                                <div class="mb-3">
                                                    <label for="inventoryId" class="form-label">
                                                        <strong>Chọn lô hàng</strong>
                                                    </label>
                                                    <select class="form-select" id="inventoryId" name="inventoryId" onchange="window.location.href='${pageContext.request.contextPath}/admin/product?action=set-price&id=${productPrice.productID}&inventoryId=' + this.value">
                                                        <option value="">-- Thiết lập giá cho tất cả các lô --</option>
                                                        <c:forEach var="lot" items="${lots}">
                                                            <c:if test="${lot.packageType == 'BOX'}">
                                                                <option value="${lot.inventoryID}" ${lot.inventoryID eq param.inventoryId ? 'selected' : ''}>
                                                                    Lô ${lot.lotNumber} - ${lot.quantity} ${productPrice.boxUnitName} 
                                                                    (Giá nhập: ${lot.costPrice}đ, Giá bán: ${lot.unitPrice != null ? lot.unitPrice : 'Chưa thiết lập'})
                                                                </option>
                                                            </c:if>
                                                        </c:forEach>
                                                    </select>
                                                    <div class="form-text">
                                                        <strong>Chọn lô cụ thể</strong> để thiết lập giá cho lô đó, hoặc <strong>để trống</strong> để thiết lập giá cho tất cả các lô (chỉ ảnh hưởng Inventory.unitPrice, không thay đổi giá tham khảo)
                                                    </div>
                                                </div>
                                                
                                                <!-- Thông tin lô được chọn -->
                                                <c:if test="${not empty param.inventoryId}">
                                                    <c:forEach var="lot" items="${lots}">
                                                        <c:if test="${lot.inventoryID eq param.inventoryId}">
                                                            <div class="mb-3">
                                                                <div class="alert alert-info">
                                                                    <h6><i class="fas fa-info-circle me-2"></i>Thông tin lô được chọn:</h6>
                                                                    <div>
                                                                        <strong>Lô số:</strong> ${lot.lotNumber}<br>
                                                                        <strong>Số lượng:</strong> ${lot.quantity} ${productPrice.boxUnitName}<br>
                                                                        <strong>Giá nhập:</strong> ${lot.costPrice}đ<br>
                                                                        <strong>Giá bán hiện tại:</strong> 
                                                                        <c:choose>
                                                                            <c:when test="${lot.unitPrice != null && lot.unitPrice > 0}">
                                                                                ${lot.unitPrice}đ
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                <span class="text-warning">Chưa thiết lập</span>
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                    </div>
                                                                </div>
                                                            </div>
                                                        </c:if>
                                                    </c:forEach>
                                                </c:if>
                                                
                                                <div class="mb-3">
                                                    <label for="sellingPrice" class="form-label">
                                                        <strong>Giá bán cho lô (đồng/${productPrice.boxUnitName})</strong>
                                                    </label>
                                                    <input type="number" 
                                                           class="form-control" 
                                                           id="sellingPrice" 
                                                           name="sellingPrice" 
                                                           min="0" 
                                                           step="1000"
                                                           value=""
                                                           placeholder="Nhập giá bán cho lô..."
                                                           required>
                                                    <div class="form-text">
                                                        <strong>Nhập giá bán mới</strong> cho 1 ${productPrice.boxUnitName} của lô này (không lấy từ giá tham khảo)
                                                    </div>
                                                </div>
                                                
                                                <!-- Validation info -->
                                                <c:if test="${not empty param.inventoryId}">
                                                    <c:forEach var="lot" items="${lots}">
                                                        <c:if test="${lot.inventoryID eq param.inventoryId}">
                                                            <c:if test="${lot.costPrice > 0}">
                                                                <div class="alert alert-warning">
                                                                    <i class="fas fa-exclamation-triangle me-2"></i>
                                                                    <strong>Lưu ý:</strong> Giá nhập kho của lô này là ${lot.costPrice}đ. 
                                                                    Hãy đảm bảo giá bán hợp lý!
                                                                </div>
                                                            </c:if>
                                                        </c:if>
                                                    </c:forEach>
                                                </c:if>
                                                
                                                <div class="d-grid gap-2">
                                                    <button type="submit" class="btn btn-warning">
                                                        <i class="fas fa-save me-2"></i>Lưu giá bán cho lô
                                                    </button>
                                                </div>
                                            </form>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </main>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
