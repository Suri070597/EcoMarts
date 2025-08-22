<%@page import="model.Manufacturer"%>
<%@page import="model.Category"%>
<%@page import="model.Product"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Quản lý sản phẩm</title>
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/img/eco.png" type="image/x-icon">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis()%>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis()%>">
    </head>
    <style>
        th {
            white-space: nowrap;
        }
        
        /* Validation styles */
        .form-control.is-valid {
            border-color: #198754;
            box-shadow: 0 0 0 0.25rem rgba(25, 135, 84, 0.25);
        }
        
        .form-control.is-invalid {
            border-color: #dc3545;
            box-shadow: 0 0 0 0.25rem rgba(220, 53, 69, 0.25);
        }
        
        .form-control.is-valid:focus {
            border-color: #198754;
            box-shadow: 0 0 0 0.25rem rgba(25, 135, 84, 0.25);
        }
        
                    .form-control.is-invalid:focus {
                border-color: #dc3545;
                box-shadow: 0 0 0 0.25rem rgba(220, 53, 69, 0.25);
            }

            /* Style cho nút disabled */
            .btn:disabled {
                opacity: 0.6;
                cursor: not-allowed;
            }

            .btn-warning:disabled {
                background-color: #6c757d;
                border-color: #6c757d;
                color: #fff;
            }

            .btn-warning:disabled:hover {
                background-color: #6c757d;
                border-color: #6c757d;
                color: #fff;
            }
    </style>
    <body>
        <div class="container-fluid">
            <%-- Sidebar --%>
            <jsp:include page="../components/sidebar.jsp" />

            <div class="main-content">
                <div class="card">
                    <div class="card-header">
                        <div class="header-actions d-flex justify-content-between align-items-center">
                            <h1 class="card-title">Danh sách sản phẩm</h1>
                            <div class="d-flex gap-2">
                                <form action="${pageContext.request.contextPath}/admin/product" method="get" class="search-box">
                                    <input type="hidden" name="action" value="search" />
                                    <i class="fas fa-search"></i>
                                    <input type="text" name="keyword" placeholder="Tìm kiếm sản phẩm..." value="${keyword != null ? keyword : ''}">
                                </form>

                                <a href="${pageContext.request.contextPath}/admin/product?action=create" class="btn btn-success">
                                    <i class="fas fa-plus"></i> Tạo mới
                                </a>
                            </div>
                        </div>
                    </div>

                    <%
                        List<Category> cate = (List<Category>) request.getAttribute("dataCate");
                        List<Product> product = (List<Product>) request.getAttribute("data");
                    %>

                    <!-- Stock status cards (same style as account dashboard) -->
                    <div class="dashboard-stats">
                        <div class="stat-card">
                            <div class="stat-icon bg-success">
                                <i class="fas fa-box-open"></i>
                            </div>
                            <div class="stat-details">
                                <h3>${inStockCount}</h3>
                                <p>Sản phẩm còn hàng</p>
                            </div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-icon bg-warning">
                                <i class="fas fa-exclamation-triangle"></i>
                            </div>
                            <div class="stat-details">
                                <h3>${lowStockCount}</h3>
                                <p>Sản phẩm gần hết hàng</p>
                            </div>
                        </div>
                        <div class="stat-card">
                            <div class="stat-icon bg-danger">
                                <i class="fas fa-times-circle"></i>
                            </div>
                            <div class="stat-details">
                                <h3>${outOfStockCount}</h3>
                                <p>Sản phẩm hết hàng</p>
                            </div>
                        </div>
                    </div>

                    <div class="table-container">
                        <% if (product != null && !product.isEmpty()) { %>
                        <table class="table table-striped table-hover text-center align-middle">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>Danh mục</th>
                                    <th>Tên sản phẩm</th>
                                    <th>Giá</th>
                                    <th>Số lượng</th>
                                    <th>Đơn vị</th>
                                    <th>Trạng thái</th>
                                    <!--<th>Mô tả</th>-->
                                    <th>Hình ảnh</th>
                                    <!--<th>Supplier</th>-->
                                    <th>Ngày tạo</th>
                                    <th>Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                    for (Product pro : product) {
                                        Category child = pro.getCategory();
                                        String parentName = "N/A";
                                        if (child != null) {
                                            int parentId = child.getParentID();
                                            for (Category c : cate) {
                                                if (c.getCategoryID() == parentId) {
                                                    parentName = c.getCategoryName();
                                                    break;
                                                }
                                            }
                                        }
                                %>
                                <tr>
                                    <td><%= pro.getProductID()%></td>
                                    <td><%= parentName%></td>
                                    <td><%= pro.getProductName()%></td>
                                    <td>
                                        <% double price = pro.getPrice();
                                            out.print(new java.text.DecimalFormat("#,###").format(price));
                                        %> đ
                                    </td>
                                    <td>
                                        <%
                                            double qty = pro.getStockQuantity();
                                            int categoryId = 0;
                                            if (pro.getCategory() != null) {
                                                categoryId = pro.getCategory().getCategoryID();
                                            }

                                            // Nếu là trái cây (categoryID = 3) thì giữ nguyên số thập phân
                                            if (categoryId == 3) {
                                                out.print(qty);
                                            } else {
                                                // Các loại khác thì loại bỏ .0
                                                if (qty == Math.floor(qty)) {
                                                    out.print((long) qty);
                                                } else {
                                                    out.print(qty);
                                                }
                                            }
                                        %>
                                    </td>
                                    <td><%= pro.getBoxUnitName()%></td>
                                    <td>
                                        <% if (pro.getStockQuantity() <= 0) { %>
                                        <span class="badge bg-danger">Hết hàng</span>
                                        <% } else if (pro.getStockQuantity() <= 10) { %>
                                        <span class="badge bg-warning">Sắp hết</span>
                                        <% } else { %>
                                        <span class="badge bg-success">Còn hàng</span>
                                        <% }%>
                                    </td>
                                    <td>
                                        <img src="<%= request.getContextPath()%>/ImageServlet?name=<%= pro.getImageURL()%>" alt="Product Image" style="width: 80px; height: auto;">
                                    </td>
                                    <td><fmt:formatDate value="<%= pro.getCreatedAt()%>" pattern="dd/MM/yyyy" /></td>
                                    <td>
                                        <div class="d-flex gap-2 justify-content-center">
                                            <a href="${pageContext.request.contextPath}/admin/product?action=detail&id=<%= pro.getProductID()%>" class="btn btn-sm btn-info">
                                                <i class="fas fa-eye"></i>
                                            </a>
                                            <a href="${pageContext.request.contextPath}/admin/product?action=update&id=<%= pro.getProductID()%>" class="btn btn-sm btn-primary">
                                                <i class="fas fa-edit"></i>
                                            </a>
                                            <%-- Convert button - disabled for fruits and out of stock --%>
                                            <%
                                                boolean isFruit = pro.getCategory().getParentID() == 3;
                                                boolean isOutOfStock = pro.getStockQuantity() <= 0;
                                                String buttonDisabled = (isFruit || isOutOfStock) ? "disabled" : "";
                                                String buttonTitle = isFruit ? "Fruit cannot be converted" : (isOutOfStock ? "Out of stock - Cannot convert" : "Convert product units");
                                            %>
                                            <button type="button" class="btn btn-sm btn-warning" 
                                                    onclick="showUnitConversion(<%= pro.getProductID()%>, '<%= pro.getProductName()%>', <%= pro.getStockQuantity()%>, <%= pro.getUnitPerBox()%>, '<%= pro.getBoxUnitName()%>', '<%= pro.getItemUnitName()%>', <%= pro.getPrice()%>, <%= pro.getCategory().getParentID()%>)"
                                                    <%= buttonDisabled %>
                                                    title="<%= buttonTitle %>">
                                                <i class="fas fa-exchange-alt"></i>
                                            </button>
                                            <a  href="${pageContext.request.contextPath}/admin/product?action=delete&id=<%= pro.getProductID()%>" class="btn btn-sm btn-danger">
                                                <i class="fas fa-trash"></i>
                                            </a>

                                        </div>
                                    </td>
                                </tr>
                                <% } %>
                            </tbody>
                        </table>
                        <% } else { %>
                        <div class="text-center">
                            <h1 class="text-danger my-4">Không có sản phẩm nào!</h1>
                        </div>
                        <% }%>
                    </div>
                </div>
            </div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        
        <!-- Modal chuyển đổi đơn vị -->
        <div class="modal fade" id="unitConversionModal" tabindex="-1" aria-labelledby="unitConversionModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="unitConversionModalLabel">Chuyển đổi đơn vị sản phẩm</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <div class="row">
                            <div class="col-md-6">
                                <h6>Thông tin sản phẩm</h6>
                                <p><strong>Tên sản phẩm:</strong> <span id="currentProductName"></span></p>
                                <p><strong>Số lượng thùng hiện có:</strong> <span id="currentStockQuantity"></span></p>
                                <p><strong>Giá 1 thùng:</strong> <span id="currentBoxPrice"></span> đ</p>
                                <p><strong>Số <span id="currentItemUnitName"></span> có trong 1 thùng:</strong> <span id="currentUnitPerBox"></span> <span id="currentItemUnitName2"></span></p>
                            </div>
                            <div class="col-md-6">
                                <h6>Thiết lập chuyển đổi</h6>
                                <form id="conversionForm">
                                    <input type="hidden" id="productId" name="productId">
                                    <div class="mb-3">
                                        <label class="form-label">Số lượng thùng cần chuyển đổi:</label>
                                        <input type="number" min="1" step="1" class="form-control" id="boxesToConvert" name="boxesToConvert" required 
                                               oninput="validatePositiveInteger(this)" 
                                               onkeypress="return event.charCode >= 48 && event.charCode <= 57">
                                        <div class="form-text">Số lượng thùng sẽ được chuyển đổi (chỉ nhập số nguyên dương)</div>
                                    </div>
                                    <div class="mb-3">
                                        <label class="form-label">Lựa chọn chuyển đổi:</label>
                                        <div class="form-check" id="convertToUnitDiv">
                                            <input class="form-check-input" type="radio" name="conversionType" id="convertToUnit" value="unit" checked>
                                            <label class="form-check-label" for="convertToUnit">
                                                Chỉ chuyển sang <span id="dynamicItemUnit">đơn vị</span>
                                            </label>
                                        </div>
                                        <div class="form-check" id="convertToPackDiv">
                                            <input class="form-check-input" type="radio" name="conversionType" id="convertToPack" value="pack">
                                            <label class="form-check-label" for="convertToPack">
                                                Chỉ chuyển sang lốc
                                            </label>
                                        </div>
                                        <div class="form-check" id="convertToBothDiv">
                                            <input class="form-check-input" type="radio" name="conversionType" id="convertToBoth" value="both">
                                            <label class="form-check-label" for="convertToBoth">
                                                Chuyển sang cả <span id="dynamicItemUnit2">đơn vị</span> và lốc
                                            </label>
                                        </div>
                                    </div>
                                    <div class="mb-3" id="packSizeDiv" style="display: none;">
                                        <label class="form-label">Số <span id="dynamicItemUnit3">đơn vị</span> = 1 lốc:</label>
                                        <input type="number" min="1" step="1" class="form-control" id="packSize" name="packSize" placeholder="Ví dụ: 6"
                                               oninput="validatePositiveInteger(this)" 
                                               onkeypress="return event.charCode >= 48 && event.charCode <= 57">
                                        <div class="form-text">Số <span id="dynamicItemUnit4">đơn vị</span> trong 1 lốc (chỉ nhập số nguyên dương)</div>
                                    </div>
                                    <div class="mb-3">
                                        <h6>Kết quả tính toán:</h6>
                                        <p><strong>Tổng số <span id="dynamicItemUnit5">đơn vị</span>:</strong> <span id="totalUnits">-</span></p>
                                        <p id="packCountRow" style="display: none;"><strong>Số lốc:</strong> <span id="packCount">-</span></p>
                                        <p><strong>Giá 1 <span id="dynamicItemUnit6">đơn vị</span>:</strong> <span id="unitPrice">-</span> đ</p>
                                        <p id="packPriceRow" style="display: none;"><strong>Giá 1 lốc:</strong> <span id="packPrice">-</span> đ</p>
                                    </div>
                                    <button type="button" class="btn btn-primary" onclick="performConversion()">
                                        <i class="fas fa-save"></i> Thực hiện chuyển đổi
                                    </button>
                                </form>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                    </div>
                </div>
            </div>
        </div>

        <script>
            // Add search functionality for client-side filtering
            document.querySelector('.search-box input').addEventListener('input', function (e) {
                const searchText = e.target.value.toLowerCase();
                const rows = document.querySelectorAll('tbody tr');

                rows.forEach(row => {
                    const text = row.textContent.toLowerCase();
                    row.style.display = text.includes(searchText) ? '' : 'none';
                });
            });

            // Biến lưu thông tin sản phẩm hiện tại
            let currentProduct = {};

            // Hàm validation cho số nguyên dương
            function validatePositiveInteger(input) {
                // Loại bỏ tất cả ký tự không phải số
                let value = input.value.replace(/[^0-9]/g, '');
                
                // Chuyển đổi thành số
                let numValue = parseInt(value) || 0;
                
                // Đảm bảo giá trị >= 1
                if (numValue < 1) {
                    numValue = 1;
                }
                
                // Cập nhật giá trị input
                input.value = numValue;
                
                // Thêm class để hiển thị trạng thái
                if (numValue > 0) {
                    input.classList.remove('is-invalid');
                    input.classList.add('is-valid');
                } else {
                    input.classList.remove('is-valid');
                    input.classList.add('is-invalid');
                }
                
                // Tính toán lại kết quả
                calculateConversion();
            }

            function showUnitConversion(productId, productName, stockQuantity, unitPerBox, boxUnitName, itemUnitName, boxPrice, categoryParentId) {
                // Check if out of stock
                if (stockQuantity <= 0) {
                    alert('❌ This product is out of stock! Cannot perform conversion.');
                    return;
                }
                
                // Check if it's a fruit product (ParentID = 3)
                // Get category info from button to check
                const button = event.target.closest('button');
                if (button && button.disabled) {
                    const title = button.getAttribute('title');
                    if (title && title.includes('Fruit cannot be converted')) {
                        alert('❌ Fruit products cannot be converted!');
                        return;
                    }
                }
                
                // Debug: log thông tin để kiểm tra
                console.log('Product Info:', {productId, productName, stockQuantity, unitPerBox, boxUnitName, itemUnitName, boxPrice});
                
                // Đảm bảo dữ liệu hợp lệ
                unitPerBox = unitPerBox || 0;
                stockQuantity = stockQuantity || 0;
                boxPrice = boxPrice || 0;
                
                currentProduct = {
                    productId: productId,
                    productName: productName,
                    stockQuantity: stockQuantity,
                    unitPerBox: unitPerBox,
                    boxUnitName: boxUnitName,
                    itemUnitName: itemUnitName,
                    boxPrice: boxPrice
                };

                // Hiển thị thông tin sản phẩm
                document.getElementById('currentProductName').textContent = productName || 'N/A';
                document.getElementById('currentStockQuantity').textContent = stockQuantity || 0;
                document.getElementById('currentBoxPrice').textContent = boxPrice > 0 ? new Intl.NumberFormat('vi-VN').format(boxPrice) : '0';
                document.getElementById('currentUnitPerBox').textContent = unitPerBox || 0;
                document.getElementById('currentItemUnitName').textContent = itemUnitName || 'đơn vị';
                document.getElementById('currentItemUnitName2').textContent = itemUnitName || 'đơn vị';
                document.getElementById('dynamicItemUnit').textContent = itemUnitName || 'đơn vị';
                document.getElementById('dynamicItemUnit2').textContent = itemUnitName || 'đơn vị';
                document.getElementById('dynamicItemUnit3').textContent = itemUnitName || 'đơn vị';
                document.getElementById('dynamicItemUnit4').textContent = itemUnitName || 'đơn vị';
                document.getElementById('dynamicItemUnit5').textContent = itemUnitName || 'đơn vị';
                document.getElementById('dynamicItemUnit6').textContent = itemUnitName || 'đơn vị';
                document.getElementById('productId').value = productId;

                // Reset form và validation state
                document.getElementById('conversionForm').reset();
                document.getElementById('totalUnits').textContent = '-';
                document.getElementById('packCount').textContent = '-';
                document.getElementById('unitPrice').textContent = '-';
                document.getElementById('packPrice').textContent = '-';
                
                // Reset validation classes
                document.getElementById('boxesToConvert').classList.remove('is-valid', 'is-invalid');
                document.getElementById('packSize').classList.remove('is-valid', 'is-invalid');
                
                // Set default conversion type
                document.getElementById('convertToUnit').checked = true;
                document.getElementById('packSizeDiv').style.display = 'none';
                document.getElementById('packCountRow').style.display = 'none';
                document.getElementById('packPriceRow').style.display = 'none';

                // Show/hide options by category
                const convertToUnitDiv = document.getElementById('convertToUnitDiv');
                const convertToPackDiv = document.getElementById('convertToPackDiv');
                const convertToBothDiv = document.getElementById('convertToBothDiv');

                // Drinks (1) and Milk (2): keep all options visible
                if (categoryParentId === 1 || categoryParentId === 2) {
                    convertToUnitDiv.style.display = 'block';
                    convertToPackDiv.style.display = 'block';
                    convertToBothDiv.style.display = 'block';
                // Fruits (3): button already disabled; nothing to do
                } else if (categoryParentId === 3) {
                    convertToUnitDiv.style.display = 'none';
                    convertToPackDiv.style.display = 'none';
                    convertToBothDiv.style.display = 'none';
                // Others: only show convert to smallest unit
                } else {
                    convertToUnitDiv.style.display = 'block';
                    convertToPackDiv.style.display = 'none';
                    convertToBothDiv.style.display = 'none';
                    document.getElementById('convertToUnit').checked = true;
                }

            // Hiển thị modal
            new bootstrap.Modal(document.getElementById('unitConversionModal')).show();
        }



            // Tính toán kết quả khi nhập số liệu
            document.getElementById('boxesToConvert').addEventListener('input', calculateConversion);
            document.getElementById('packSize').addEventListener('input', calculateConversion);
            
            // Xử lý thay đổi loại chuyển đổi
            document.querySelectorAll('input[name="conversionType"]').forEach(radio => {
                radio.addEventListener('change', function() {
                    const packSizeDiv = document.getElementById('packSizeDiv');
                    const packCountRow = document.getElementById('packCountRow');
                    const packPriceRow = document.getElementById('packPriceRow');
                    
                    if (this.value === 'pack' || this.value === 'both') {
                        packSizeDiv.style.display = 'block';
                        packCountRow.style.display = 'block';
                        packPriceRow.style.display = 'block';
                    } else {
                        packSizeDiv.style.display = 'none';
                        packCountRow.style.display = 'none';
                        packPriceRow.style.display = 'none';
                    }
                    calculateConversion();
                });
            });

            function calculateConversion() {
                const boxesToConvert = parseInt(document.getElementById('boxesToConvert').value) || 0;
                const packSize = parseInt(document.getElementById('packSize').value) || 0;
                const conversionType = document.querySelector('input[name="conversionType"]:checked').value;
                
                if (boxesToConvert > 0 && currentProduct.unitPerBox > 0) {
                    const totalUnits = boxesToConvert * currentProduct.unitPerBox;
                    const unitPrice = currentProduct.boxPrice / currentProduct.unitPerBox;
                    
                    document.getElementById('totalUnits').textContent = totalUnits;
                    document.getElementById('unitPrice').textContent = new Intl.NumberFormat('vi-VN').format(unitPrice);
                    
                    // Hiển thị thông tin lốc nếu có chọn lốc
                    if ((conversionType === 'pack' || conversionType === 'both') && packSize > 0) {
                        // Kiểm tra số lon trong thùng có chia hết cho số lon/lốc không
                        if (currentProduct.unitPerBox % packSize !== 0) {
                            document.getElementById('packCount').textContent = 'Không chia hết!';
                            document.getElementById('packPrice').textContent = '-';
                        } else if (totalUnits % packSize === 0) {
                            const packCount = totalUnits / packSize;
                            const packPrice = unitPrice * packSize;
                            document.getElementById('packCount').textContent = packCount;
                            document.getElementById('packPrice').textContent = new Intl.NumberFormat('vi-VN').format(packPrice);
                        } else {
                            document.getElementById('packCount').textContent = 'Không chia hết!';
                            document.getElementById('packPrice').textContent = '-';
                        }
                    } else {
                        document.getElementById('packCount').textContent = '-';
                        document.getElementById('packPrice').textContent = '-';
                    }
                }
            }

            function performConversion() {
                // Check again if product is out of stock
                if (currentProduct.stockQuantity <= 0) {
                    alert('❌ This product is out of stock! Cannot perform conversion.');
                    return;
                }
                
                const boxesToConvertInput = document.getElementById('boxesToConvert');
                const packSizeInput = document.getElementById('packSize');
                const conversionType = document.querySelector('input[name="conversionType"]:checked').value;
                
                const boxesToConvert = parseInt(boxesToConvertInput.value);
                const packSize = parseInt(packSizeInput.value) || 0;
                
                // Validation mạnh mẽ
                if (!boxesToConvert || boxesToConvert <= 0) {
                    alert('❌ Vui lòng nhập số lượng thùng hợp lệ (phải là số nguyên dương)!');
                    boxesToConvertInput.focus();
                    boxesToConvertInput.classList.add('is-invalid');
                    return;
                }
                
                if (boxesToConvert > currentProduct.stockQuantity) {
                    alert('❌ Số lượng thùng chuyển đổi vượt quá số lượng hiện có (' + currentProduct.stockQuantity + ' thùng)!');
                    boxesToConvertInput.focus();
                    boxesToConvertInput.classList.add('is-invalid');
                    return;
                }
                
                if ((conversionType === 'pack' || conversionType === 'both') && packSize > 0) {
                    // Kiểm tra số lon trong thùng có chia hết cho số lon/lốc không
                    if (currentProduct.unitPerBox % packSize !== 0) {
                        alert('❌ Số lon trong thùng (' + currentProduct.unitPerBox + ') không chia hết cho số lon/lốc (' + packSize + '). Vui lòng chọn số lon/lốc khác.');
                        packSizeInput.focus();
                        packSizeInput.classList.add('is-invalid');
                        return;
                    }
                    
                    const totalUnits = boxesToConvert * currentProduct.unitPerBox;
                    if (totalUnits % packSize !== 0) {
                        alert('❌ Tổng số lon (' + totalUnits + ') không chia hết cho số lon/lốc (' + packSize + '). Vui lòng chọn số lon/lốc khác.');
                        packSizeInput.focus();
                        packSizeInput.classList.add('is-invalid');
                        return;
                    }
                }
                
                // Xóa class invalid nếu validation thành công
                boxesToConvertInput.classList.remove('is-invalid');
                packSizeInput.classList.remove('is-invalid');

                // Gửi request chuyển đổi
                const formData = new FormData();
                formData.append('action', 'convertUnits');
                formData.append('productId', currentProduct.productId);
                formData.append('boxesToConvert', boxesToConvert);
                formData.append('conversionType', conversionType);
                formData.append('packSize', packSize);

                fetch('${pageContext.request.contextPath}/admin/product', {
                    method: 'POST',
                    body: formData
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        alert('Chuyển đổi thành công!');
                        location.reload(); // Reload trang để cập nhật dữ liệu
                    } else {
                        alert('Lỗi: ' + data.message);
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Đã xảy ra lỗi khi chuyển đổi!');
                });
            }
        </script>
    </body>
</html>
