<%@page import="model.Supplier"%>
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
                                    <button type="submit" class="btn btn-sm btn-primary">Tìm kiếm</button>
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
                                         <% double displayBoxPrice = 0;
                                            try { displayBoxPrice = pro.getBoxPrice(); } catch (Exception ignore) {}
                                            if (displayBoxPrice <= 0) {
                                                if (pro.getUnitPerBox() > 0) {
                                                    displayBoxPrice = pro.getPrice() * pro.getUnitPerBox();
                                                } else {
                                                    displayBoxPrice = pro.getPrice();
                                                }
                                            }
                                            out.print(new java.text.DecimalFormat("#,###").format(displayBoxPrice));
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
                                    <td><%= pro.getUnit()%></td>
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
                                    <td><%= pro.getCreatedAt()%></td>
                                    <td>
                                        <div class="d-flex gap-2 justify-content-center">
                                            <a href="${pageContext.request.contextPath}/admin/product?action=detail&id=<%= pro.getProductID()%>" class="btn btn-sm btn-info">
                                                <i class="fas fa-eye"></i>
                                            </a>
                                            <a href="${pageContext.request.contextPath}/admin/product?action=update&id=<%= pro.getProductID()%>" class="btn btn-sm btn-primary">
                                                <i class="fas fa-edit"></i>
                                            </a>

                                                                                         <!-- Nút chuyển đổi đơn vị -->
                                              <button type="button" class="btn btn-sm btn-warning" 
                                                      onclick="console.log('Button clicked for product:', <%= pro.getProductID()%>); showUnitConversion(<%= pro.getProductID()%>, '<%= pro.getProductName()%>', <%= pro.getUnitPerBox()%>, <%= pro.getUnitsPerPack()%>, '<%= pro.getBoxUnitName()%>', '<%= pro.getItemUnitName()%>', <%= pro.getStockQuantity()%>, <%= pro.getBoxPrice() > 0 ? pro.getBoxPrice() : (pro.getUnitPerBox() > 0 ? pro.getPrice() * pro.getUnitPerBox() : pro.getPrice())%>)">
                                                 <i class="fas fa-exchange-alt"></i> Chuyển đổi
                                             </button>

                                            <a href="${pageContext.request.contextPath}/admin/product?action=delete&id=<%= pro.getProductID()%>" class="btn btn-sm btn-danger">
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
                                <h6>Thông tin hiện tại</h6>
                                <p><strong>Tên sản phẩm:</strong> <span id="currentProductName"></span></p>
                                <p><strong>Số lượng thùng:</strong> <span id="currentStockQuantity"></span></p>
                                <p><strong>Giá 1 thùng:</strong> <span id="currentBoxPrice"></span> VNĐ</p>
                                <p><strong>Giá 1 <span id="currentItemUnitName"></span>:</strong> <span id="currentUnitPrice"></span> VNĐ</p>
                                <p><strong>1 thùng = </strong><span id="currentUnitPerBox"></span> <span id="currentItemUnitName"></span></p>
                                <div id="currentUnitsPerPackDiv" style="display: none;">
                                    <p><strong>1 thùng = </strong><span id="currentUnitsPerPack"></span> lốc</p>
                                    <p><strong>1 lốc = </strong><span id="currentLonToLoc"></span> <span id="currentItemUnitName"></span></p>
                                    <p><em>(Tự động tính toán: 1 thùng = <span id="currentUnitPerBox"></span> <span id="currentItemUnitName"></span> ÷ <span id="currentLonToLoc"></span> <span id="currentItemUnitName"></span>/lốc = <span id="currentUnitsPerPack"></span> lốc)</em></p>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <h6>Thiết lập chuyển đổi đơn vị</h6>
                                
                                <!-- Chuyển đổi 1 thùng = X lon -->
                                <div class="mb-3">
                                    <label class="form-label">1 thùng = ? lon:</label>
                                    <input type="number" min="1" max="1000" class="form-control" id="thungToLon" placeholder="Ví dụ: 24" />
                                    <div class="form-text">Số lượng lon trong 1 thùng</div>
                                </div>
                                
                                <!-- Chuyển đổi Y lon = 1 lốc -->
                                <div class="mb-3">
                                    <label class="form-label">? lon = 1 lốc:</label>
                                    <input type="number" min="1" max="1000" class="form-control" id="lonToLoc" placeholder="Ví dụ: 6" />
                                    <div class="form-text">Số lượng lon cần để tạo 1 lốc (để trống nếu không cần)</div>
                                </div>

                                
                                <button type="button" class="btn btn-primary" onclick="updateUnitConversion()">
                                    <i class="fas fa-save"></i> Cập nhật chuyển đổi
                                </button>
                                

                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Đóng</button>
                    </div>
                </div>
            </div>
        </div>
        
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            console.log('Script loaded');
            
            // Biến lưu thông tin sản phẩm hiện tại
            let currentProductInfo = {};
            
            // Hiển thị modal chuyển đổi đơn vị
            function showUnitConversion(productId, productName, unitPerBox, unitsPerPack, boxUnitName, itemUnitName, stockQuantity, boxPrice) {
                console.log('showUnitConversion called with:', {productId, productName, unitPerBox, unitsPerPack, boxUnitName, itemUnitName, stockQuantity, boxPrice});
                
                // Lưu thông tin sản phẩm
                currentProductInfo = {
                    productId: productId,
                    productName: productName,
                    unitPerBox: unitPerBox,
                    unitsPerPack: unitsPerPack,
                    boxUnitName: boxUnitName,
                    itemUnitName: itemUnitName,
                    stockQuantity: stockQuantity,
                    boxPrice: boxPrice, // Đây là giá 1 thùng
                    thungToLon: unitPerBox, // Mặc định từ database
                    lonToLoc: unitsPerPack > 1 ? unitsPerPack : 0 // Mặc định từ database
                };
                
                // Cập nhật thông tin hiển thị
                try {
                    document.getElementById('currentProductName').textContent = productName;
                    document.getElementById('currentStockQuantity').textContent = stockQuantity;
                    document.getElementById('currentBoxPrice').textContent = boxPrice.toLocaleString();
                    if (unitPerBox > 0) {
                        document.getElementById('currentUnitPerBox').textContent = unitPerBox;
                    } else {
                        document.getElementById('currentUnitPerBox').textContent = 'chưa thiết lập';
                    }
                    document.getElementById('currentUnitsPerPack').textContent = unitsPerPack;
                    document.getElementById('currentItemUnitName').textContent = itemUnitName;
                    
                    // Tính toán và hiển thị giá đơn vị nhỏ nhất
                    if (unitPerBox > 0) {
                        const unitPrice = Math.round(boxPrice / unitPerBox);
                        document.getElementById('currentUnitPrice').textContent = unitPrice.toLocaleString();
                    } else {
                        document.getElementById('currentUnitPrice').textContent = boxPrice.toLocaleString();
                    }
                    
                    // Tính toán và hiển thị thông tin về lốc
                    if (unitPerBox === 0) {
                        document.getElementById('currentUnitsPerPackDiv').style.display = 'none';
                    } else if (unitsPerPack > 1 && unitPerBox > 0) {
                        // Tính số lon trong 1 lốc (ngược lại từ unitsPerPack)
                        const lonToLoc = Math.floor(unitPerBox / unitsPerPack);
                        document.getElementById('currentLonToLoc').textContent = lonToLoc;
                        document.getElementById('currentUnitsPerPackDiv').style.display = 'block';
                    } else if (unitsPerPack > 0 && unitPerBox > 0) {
                        // Trường hợp unitsPerPack = 1 (1 thùng = 1 lốc)
                        const lonToLoc = unitPerBox;
                        document.getElementById('currentLonToLoc').textContent = lonToLoc;
                        document.getElementById('currentUnitsPerPackDiv').style.display = 'block';
                    } else {
                        document.getElementById('currentUnitsPerPackDiv').style.display = 'none';
                    }
                    
                    // Cập nhật giá trị mặc định cho form chuyển đổi
                    document.getElementById('thungToLon').value = unitPerBox;
                    
                    // Tính toán giá trị mặc định cho lonToLoc
                    let defaultLonToLoc = '';
                    if (unitsPerPack > 0 && unitPerBox > 0) {
                        defaultLonToLoc = Math.floor(unitPerBox / unitsPerPack);
                    }
                    document.getElementById('lonToLoc').value = defaultLonToLoc;
                    
                } catch (error) {
                    console.error('Error updating modal content:', error);
                }
                
                // Hiển thị modal
                try {
                    const modalElement = document.getElementById('unitConversionModal');
                    if (modalElement) {
                        const modal = new bootstrap.Modal(modalElement);
                        modal.show();
                    } else {
                        console.error('Modal element not found');
                    }
                } catch (error) {
                    console.error('Error showing modal:', error);
                }
            }
            


            // Function to update unit conversion settings
            function updateUnitConversion() {
                console.log('updateUnitConversion called');
                
                const thungToLon = parseInt(document.getElementById('thungToLon').value) || 0;
                const lonToLoc = parseInt(document.getElementById('lonToLoc').value) || 0;
                
                console.log('Values:', {thungToLon, lonToLoc});

                if (thungToLon <= 0) {
                    alert('Vui lòng nhập số lượng lon trong 1 thùng hợp lệ.');
                    return;
                }

                // Kiểm tra logic: nếu có lonToLoc thì phải nhỏ hơn hoặc bằng thungToLon
                if (lonToLoc > 0 && lonToLoc > thungToLon) {
                    alert('Số lượng lon trong 1 lốc không được lớn hơn số lượng lon trong 1 thùng.');
                    return;
                }
                
                // Kiểm tra xem có chia hết không
                if (lonToLoc > 0 && thungToLon % lonToLoc !== 0) {
                    alert('Số lượng lon trong 1 thùng (' + thungToLon + ') phải chia hết cho số lon trong 1 lốc (' + lonToLoc + '). Hiện tại sẽ bị dư ' + (thungToLon % lonToLoc) + ' lon.');
                    return;
                }

                // Prepare data for AJAX request
                const formData = new FormData();
                formData.append('action', 'updateUnitConversion');
                formData.append('productId', currentProductInfo.productId);
                formData.append('thungToLon', thungToLon);
                formData.append('lonToLoc', lonToLoc);

                // Make AJAX request to save conversion settings
                fetch('${pageContext.request.contextPath}/admin/product', {
                    method: 'POST',
                    body: formData
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        // Update the currentProductInfo object with the new conversion rates
                        currentProductInfo.thungToLon = thungToLon;
                        currentProductInfo.lonToLoc = lonToLoc;
                        
                        // Tính toán số lượng lốc trong 1 thùng để hiển thị
                        let soLocTrongThung = 0;
                        if (lonToLoc > 0) {
                            soLocTrongThung = thungToLon / lonToLoc; // Đã kiểm tra chia hết ở server
                        }
                        currentProductInfo.unitsPerPack = soLocTrongThung;
                        currentProductInfo.unitPerBox = thungToLon; // Cập nhật unitPerBox

                        // Cập nhật hiển thị thông tin hiện tại
                        try {
                            document.getElementById('currentUnitPerBox').textContent = thungToLon;
                            
                            // Cập nhật giá đơn vị
                            if (thungToLon > 0) {
                                const newUnitPrice = Math.round(currentProductInfo.boxPrice / thungToLon);
                                document.getElementById('currentUnitPrice').textContent = newUnitPrice.toLocaleString();
                            }
                            // Nếu server tự mở 1 thùng lần đầu, cập nhật tồn kho hiển thị
                            if (typeof data.autoOpenedBoxes === 'number' && data.autoOpenedBoxes > 0) {
                                currentProductInfo.stockQuantity = data.newStockQuantity ?? (currentProductInfo.stockQuantity - data.autoOpenedBoxes);
                                document.getElementById('currentStockQuantity').textContent = currentProductInfo.stockQuantity;
                            }
                            
                            if (lonToLoc > 0) {
                                document.getElementById('currentUnitsPerPack').textContent = soLocTrongThung;
                                document.getElementById('currentLonToLoc').textContent = lonToLoc;
                                document.getElementById('currentUnitsPerPackDiv').style.display = 'block';
                            } else {
                                document.getElementById('currentUnitsPerPack').textContent = '0';
                                document.getElementById('currentLonToLoc').textContent = '0';
                                document.getElementById('currentUnitsPerPackDiv').style.display = 'none';
                            }
                            
                            // Cập nhật form thiết lập chuyển đổi đơn vị
                            document.getElementById('thungToLon').value = thungToLon;
                            document.getElementById('lonToLoc').value = lonToLoc > 0 ? lonToLoc : '';
                            document.getElementById('openBoxes').value = 0;
                            
                            // Thêm hiệu ứng visual để người dùng biết form đã được cập nhật
                            const thungToLonInput = document.getElementById('thungToLon');
                            const lonToLocInput = document.getElementById('lonToLoc');
                            thungToLonInput.style.backgroundColor = '#e8f5e8';
                            lonToLocInput.style.backgroundColor = '#e8f5e8';
                            setTimeout(() => {
                                thungToLonInput.style.backgroundColor = '';
                                lonToLocInput.style.backgroundColor = '';
                            }, 1000);
                            
                        } catch (error) {
                            console.error('Error updating display:', error);
                        }

                        alert('Đã cập nhật chuyển đổi đơn vị thành công!\n\nThông tin hiện tại và form thiết lập đã được cập nhật.');
                    } else {
                        alert('Lỗi: ' + data.message);
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    alert('Đã xảy ra lỗi khi cập nhật chuyển đổi đơn vị');
                });
            }
        </script>
    </body>
</html>
