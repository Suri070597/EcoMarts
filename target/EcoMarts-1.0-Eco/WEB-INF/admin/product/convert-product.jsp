<%@page import="java.util.List"%>
<%@page import="model.Product"%>
<%@page import="model.Category"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Product product = (Product) request.getAttribute("product");
    String error = (String) request.getAttribute("error");
    String success = (String) request.getAttribute("success");
%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Chuyển đổi đơn vị sản phẩm</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css">
    </head>
    <body>
        <div class="container-fluid">
            <jsp:include page="../components/sidebar.jsp" />
            
            <div class="main-content">
                <div class="container">
                    <h1>Chuyển đổi đơn vị sản phẩm</h1>
                    
                    <% if (error != null) { %>
                    <div class="alert alert-danger"><%= error %></div>
                    <% } %>
                    
                    <% if (success != null) { %>
                    <div class="alert alert-success"><%= success %></div>
                    <% } %>
                    
                    <% if (product == null) { %>
                    <div class="alert alert-danger">❌ Không tìm thấy sản phẩm.</div>
                    <a href="${pageContext.request.contextPath}/admin/product" class="btn btn-secondary">Quay lại</a>
                    <% } else { %>
                    
                    <!-- Thông tin sản phẩm -->
                    <div class="card mb-4">
                        <div class="card-header">
                            <h5>Thông tin sản phẩm</h5>
                        </div>
                        <div class="card-body">
                            <div class="row">
                                <div class="col-md-6">
                                    <p><strong>Mã sản phẩm:</strong> <%= product.getProductID() %></p>
                                    <p><strong>Tên sản phẩm:</strong> <%= product.getProductName() %></p>
                                    <p><strong>Thể loại:</strong> 
                                        <% if (product.getCategory() != null) { %>
                                            <%= product.getCategory().getCategoryName() %>
                                        <% } %>
                                    </p>
                                    <p><strong>Số lượng thùng hiện có:</strong> <%= product.getStockQuantity() %></p>
                                </div>
                                <div class="col-md-6">
                                    <p><strong>Số lượng sản phẩm trong 1 thùng:</strong> <%= product.getUnitPerBox() %></p>
                                    <p><strong>Đơn vị thùng:</strong> <%= product.getBoxUnitName() != null ? product.getBoxUnitName() : "N/A" %></p>
                                    <p><strong>Đơn vị nhỏ nhất:</strong> <%= product.getItemUnitName() != null ? product.getItemUnitName() : "N/A" %></p>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Form chuyển đổi -->
                    <div class="card">
                        <div class="card-header">
                            <h5>Thiết lập chuyển đổi</h5>
                        </div>
                        <div class="card-body">
                            <form method="post" action="${pageContext.request.contextPath}/admin/product">
                                <input type="hidden" name="action" value="convertUnits">
                                <input type="hidden" name="productId" value="<%= product.getProductID() %>">
                                
                                <div class="mb-3">
                                    <label class="form-label">Số lượng thùng chuyển đổi</label>
                                    <input type="number" min="1" max="<%= (int)product.getStockQuantity() %>" 
                                           class="form-control" name="boxesToConvert" required
                                           placeholder="Nhập số lượng thùng muốn chuyển đổi">
                                    <small class="form-text text-muted">
                                        Tối đa: <%= (int)product.getStockQuantity() %> thùng
                                    </small>
                                </div>
                                
                                <% 
                                    boolean isFruit = false;
                                    boolean isBeverage = false;
                                    if (product.getCategory() != null) {
                                        int catId = product.getCategory().getCategoryID();
                                        Integer parentId = product.getCategory().getParentID();
                                        isFruit = (catId == 3) || (parentId != null && parentId == 3);
                                        isBeverage = (parentId != null && (parentId == 1 || parentId == 2)); // Nước giải khát hoặc sữa (chỉ dựa theo parentID)
                                        
                                        // Debug info
                                        System.out.println("Product: " + product.getProductName());
                                        System.out.println("Category ID: " + catId);
                                        System.out.println("Parent ID: " + parentId);
                                        System.out.println("Is Fruit: " + isFruit);
                                        System.out.println("Is Beverage: " + isBeverage);
                                    }
                                %>
                                
                                <% if (isFruit) { %>
                                <!-- Trái cây - không cho chuyển đổi -->
                                <div class="alert alert-warning">
                                    <i class="fas fa-exclamation-triangle"></i>
                                    <strong>Lưu ý:</strong> Sản phẩm trái cây không thể chuyển đổi đơn vị.
                                </div>
                                <button type="submit" class="btn btn-secondary" disabled>Không thể chuyển đổi</button>
                                
                                <% } else if (isBeverage) { %>
                                <!-- Nước giải khát và sữa - chuyển sang lốc hoặc cả lốc hoặc unit -->
                                <div class="mb-3">
                                    <label class="form-label">Loại chuyển đổi</label>
                                    <select class="form-select" name="conversionType" required>
                                        <option value="">-- Chọn loại chuyển đổi --</option>
                                        <option value="pack">Chuyển sang lốc</option>
                                        <option value="unit">Chuyển sang <%= product.getItemUnitName() != null ? product.getItemUnitName() : "unit" %></option>
                                    </select>
                                </div>
                                
                                <div class="mb-3" id="packSizeField" style="display: none;">
                                    <label class="form-label">Số lon = 1 lốc</label>
                                    <input type="number" min="2" max="<%= product.getUnitPerBox() - 1 %>" 
                                           class="form-control" name="packSize" 
                                           placeholder="Nhập số lon trong 1 lốc">
                                    <small class="form-text text-muted">
                                        Phải từ 2 đến <%= product.getUnitPerBox() - 1 %> lon
                                    </small>
                                </div>
                                
                                <button type="submit" class="btn btn-primary">Thực hiện chuyển đổi</button>
                                
                                <% } else { %>
                                <!-- Các thể loại khác - chỉ chuyển sang unit -->
                                <div class="mb-3">
                                    <label class="form-label">Loại chuyển đổi</label>
                                    <select class="form-select" name="conversionType" required>
                                        <option value="unit" selected>Chuyển sang <%= product.getItemUnitName() != null ? product.getItemUnitName() : "unit" %></option>
                                    </select>
                                </div>
                                
                                <button type="submit" class="btn btn-primary">Thực hiện chuyển đổi</button>
                                <% } %>
                                
                                <a href="${pageContext.request.contextPath}/admin/product" class="btn btn-secondary ms-2">Quay lại</a>
                            </form>
                        </div>
                    </div>
                    
                    <!-- Kết quả tính toán -->
                    <div class="card mt-4" id="resultCard" style="display: none;">
                        <div class="card-header">
                            <h5>Kết quả tính toán</h5>
                        </div>
                        <div class="card-body">
                            <div class="row">
                                <div class="col-md-6">
                                    <p><strong>Số lượng thùng chuyển đổi:</strong> <span id="resultBoxes"></span></p>
                                    <p><strong>Số lượng unit:</strong> <span id="resultUnits"></span></p>
                                </div>
                                <div class="col-md-6">
                                    <p><strong>Số lượng lốc:</strong> <span id="resultPacks"></span></p>
                                    <p><strong>Đơn vị:</strong> <span id="resultUnitName"><%= product.getItemUnitName() != null ? product.getItemUnitName() : "unit" %></span></p>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <% } %>
                </div>
            </div>
        </div>
        
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            // Hiển thị/ẩn trường packSize dựa trên loại chuyển đổi
            document.querySelector('select[name="conversionType"]').addEventListener('change', function() {
                const packSizeField = document.getElementById('packSizeField');
                const packSizeInput = document.querySelector('input[name="packSize"]');
                
                if (this.value === 'pack') {
                    packSizeField.style.display = 'block';
                    packSizeInput.required = true;
                } else {
                    packSizeField.style.display = 'none';
                    packSizeInput.required = false;
                }
            });
        </script>
    </body>
</html>
