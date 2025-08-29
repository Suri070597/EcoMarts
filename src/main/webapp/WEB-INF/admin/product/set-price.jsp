<%@page import="model.Product"%>
<%@page import="model.Category"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Nhập tiền bán lẻ</title>
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/img/eco.png" type="image/x-icon">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis()%>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis()%>">
    </head>
    <style>
        .form-control:disabled {
            background-color: #e9ecef;
            opacity: 0.6;
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
                            <h1 class="card-title">Nhập tiền bán lẻ</h1>
                            <a href="${pageContext.request.contextPath}/admin/product" class="btn btn-secondary">
                                <i class="fas fa-arrow-left"></i> Quay lại
                            </a>
                        </div>
                    </div>

                    <div class="card-body">
                        <%
                            Product product = (Product) request.getAttribute("product");
                            String error = (String) request.getAttribute("error");
                            String success = (String) request.getAttribute("success");
                            
                            if (product != null) {
                                boolean isFruit = product.getCategory() != null && product.getCategory().getParentID() == 3;
                                boolean isBeverageOrMilk = product.getCategory() != null && 
                                    (product.getCategory().getParentID() == 1 || product.getCategory().getParentID() == 2);
                                
                                                                 // Lấy thông tin inventory để kiểm tra số lượng
                                 dao.ProductDAO productDAO = new dao.ProductDAO();
                                 double unitQuantity = productDAO.getQuantityByPackageType(product.getProductID(), "UNIT");
                                 double packQuantity = productDAO.getQuantityByPackageType(product.getProductID(), "PACK");
                                 double boxQuantity = productDAO.getQuantityByPackageType(product.getProductID(), "BOX");
                                 double kgQuantity = productDAO.getQuantityByPackageType(product.getProductID(), "KG");
                                 
                                 // Kiểm tra xem có thể nhập giá cho unit/pack/box/kg không
                                 boolean canSetUnitPrice = unitQuantity > 0;
                                 boolean canSetPackPrice = packQuantity > 0;
                                 boolean canSetBoxPrice = boxQuantity > 0;
                                 boolean canSetKgPrice = kgQuantity > 0;
                                 
                                 // Đối với các loại khác (không phải trái cây, nước giải khát, sữa)
                                 // cũng cần kiểm tra số lượng UNIT trước khi cho phép nhập giá
                                 boolean isOtherCategory = !isFruit && !isBeverageOrMilk;
                                 if (isOtherCategory) {
                                     canSetUnitPrice = unitQuantity > 0;
                                 }
                        %>
                        
                        <% if (error != null) { %>
                        <div class="alert alert-danger alert-dismissible fade show" role="alert">
                            <i class="fas fa-exclamation-triangle"></i>
                            <strong>Lỗi!</strong> 
                            <% if (error.contains("Lỗi logic giá")) { %>
                                <div class="mt-2">
                                    <strong>Quy tắc giá:</strong>
                                    <ul class="mb-0 mt-1">
                                        <li>Giá đơn vị ≤ Giá thùng</li>
                                        <% if (isBeverageOrMilk) { %>
                                        <li>Giá đơn vị ≤ Giá lốc</li>
                                        <li>Giá lốc ≤ Giá thùng</li>
                                        <% } %>
                                    </ul>
                                </div>
                            <% } else { %>
                                <%= error %>
                            <% } %>
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                        <% } %>
                        
                        <% if (success != null) { %>
                        <div class="alert alert-success alert-dismissible fade show" role="alert">
                            <i class="fas fa-check-circle"></i>
                            <strong>Thành công!</strong> <%= success %>
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                        <% } %>

                        <div class="row">
                            <div class="col-md-6">
                                <h5>Thông tin sản phẩm</h5>
                                <table class="table table-borderless">
                                    <tr>
                                        <td><strong>ID:</strong></td>
                                        <td><%= product.getProductID() %></td>
                                    </tr>
                                    <tr>
                                        <td><strong>Tên sản phẩm:</strong></td>
                                        <td><%= product.getProductName() %></td>
                                    </tr>
                                    <tr>
                                        <td><strong>Danh mục:</strong></td>
                                        <td><%= product.getCategory() != null ? product.getCategory().getCategoryName() : "N/A" %></td>
                                    </tr>
                                    <tr>
                                        <td><strong>Số lượng hiện có:</strong></td>
                                        <td>
                                            <% 
                                                double stockQty = product.getStockQuantity();
                                                if (isFruit) {
                                                    // Trái cây: hiển thị decimal (kg)
                                                    out.print(stockQty);
                                                } else {
                                                    // Các loại khác: hiển thị integer (thùng)
                                                    if (stockQty == Math.floor(stockQty)) {
                                                        out.print((long) stockQty);
                                                    } else {
                                                        out.print(stockQty);
                                                    }
                                                }
                                            %> 
                                            <%= product.getBoxUnitName() != null ? product.getBoxUnitName() : "N/A" %>
                                        </td>
                                    </tr>
                                </table>
                            </div>
                            
                            <div class="col-md-6">
                                <h5>Giá hiện tại</h5>
                                <table class="table table-borderless">
                                    <% if (isFruit) { %>
                                    <tr>
                                        <td><strong>Giá 1Kg:</strong></td>
                                        <td><%= product.getPriceUnit() != null ? String.format("%,.0f", product.getPriceUnit()) : "Chưa có" %> đ</td>
                                    </tr>
                                    <% } else { %>
                                    <tr>
                                        <td><strong>Giá thùng:</strong></td>
                                        <td><%= product.getPrice() != null ? String.format("%,.0f", product.getPrice()) : "Chưa có" %> đ</td>
                                    </tr>
                                    <tr>
                                        <td><strong>Giá đơn vị:</strong></td>
                                        <td><%= product.getPriceUnit() != null ? String.format("%,.0f", product.getPriceUnit()) : "Chưa có" %> đ</td>
                                    </tr>
                                    <% if (isBeverageOrMilk) { %>
                                    <tr>
                                        <td><strong>Giá lốc:</strong></td>
                                        <td><%= product.getPricePack() != null ? String.format("%,.0f", product.getPricePack()) : "Chưa có" %> đ</td>
                                    </tr>
                                    <% } %>
                                    <% } %>
                                </table>
                            </div>
                        </div>



                        <hr>



                        <form action="${pageContext.request.contextPath}/admin/product" method="post" id="priceForm">
                            <input type="hidden" name="action" value="updatePrice">
                            <input type="hidden" name="productId" value="<%= product.getProductID() %>">
                            
                            <div class="row">
                                <% if (isFruit) { %>
                                <!-- Form cho trái cây -->
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="priceUnit" class="form-label">Giá bán lẻ cho 1kg (đ)</label>
                                        <input type="number" 
                                               class="form-control" 
                                               id="priceUnit" 
                                               name="priceUnit" 
                                               min="0" 
                                               step="1000"
                                               value="<%= product.getPriceUnit() != null ? product.getPriceUnit().intValue() : "" %>"
                                               <%= !canSetKgPrice ? "disabled" : "" %>>
                                        <div class="form-text">
                                            <% if (canSetKgPrice) { %>
                                                Nhập giá bán lẻ cho 1kg trái cây
                                            <% } else { %>
                                                <span class="text-danger">Chưa có số lượng kg trong kho. Vui lòng nhập kho trước.</span>
                                            <% } %>
                                        </div>
                                    </div>
                                </div>
                                
                                <% } else if (isBeverageOrMilk) { %>
                                <!-- Form cho nước giải khát và sữa -->
                                <div class="col-md-4">
                                    <div class="mb-3">
                                        <label for="priceBox" class="form-label">Giá bán lẻ cho thùng (đ)</label>
                                        <input type="number" 
                                               class="form-control" 
                                               id="priceBox" 
                                               name="priceBox" 
                                               min="0" 
                                               step="1000"
                                               value="<%= product.getPrice() != null ? product.getPrice().intValue() : "" %>"
                                               <%= !canSetBoxPrice ? "disabled" : "" %>>
                                        <div class="form-text">
                                            <% if (canSetBoxPrice) { %>
                                                Giá bán lẻ cho 1 thùng
                                            <% } else { %>
                                                <span class="text-danger">Chưa có số lượng thùng trong kho. Vui lòng nhập kho trước.</span>
                                            <% } %>
                                        </div>
                                    </div>
                                </div>
                                
                                <div class="col-md-4">
                                    <div class="mb-3">
                                        <label for="priceUnit" class="form-label">
                                            Giá bán lẻ cho <%= product.getItemUnitName() != null ? product.getItemUnitName() : "đơn vị" %> (đ)
                                        </label>
                                        <input type="number" 
                                               class="form-control" 
                                               id="priceUnit" 
                                               name="priceUnit" 
                                               min="0" 
                                               step="1000"
                                               value="<%= product.getPriceUnit() != null ? product.getPriceUnit().intValue() : "" %>"
                                               <%= !canSetUnitPrice ? "disabled" : "" %>>
                                        <div class="form-text">
                                            Giá bán lẻ cho 1 <%= product.getItemUnitName() != null ? product.getItemUnitName() : "đơn vị" %>
                                        </div>
                                    </div>
                                </div>
                                
                                <div class="col-md-4">
                                    <div class="mb-3">
                                        <label for="pricePack" class="form-label">
                                            Giá bán lẻ cho lốc (đ)
                                        </label>
                                        <input type="number" 
                                               class="form-control" 
                                               id="pricePack" 
                                               name="pricePack" 
                                               min="0" 
                                               step="1000"
                                               value="<%= product.getPricePack() != null ? product.getPricePack().intValue() : "" %>"
                                               <%= !canSetPackPrice ? "disabled" : "" %>>
                                        <div class="form-text">
                                            Giá bán lẻ cho 1 lốc
                                        </div>
                                    </div>
                                </div>
                                
                                <% } else { %>
                                <!-- Form cho các loại khác -->
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="priceBox" class="form-label">Giá bán lẻ cho thùng (đ)</label>
                                        <input type="number" 
                                               class="form-control" 
                                               id="priceBox" 
                                               name="priceBox" 
                                               min="0" 
                                               step="1000"
                                               value="<%= product.getPrice() != null ? product.getPrice().intValue() : "" %>"
                                               <%= !canSetBoxPrice ? "disabled" : "" %>>
                                        <div class="form-text">
                                            <% if (canSetBoxPrice) { %>
                                                Giá bán lẻ cho 1 thùng
                                            <% } else { %>
                                                <span class="text-danger">Chưa có số lượng thùng trong kho. Vui lòng nhập kho trước.</span>
                                            <% } %>
                                        </div>
                                    </div>
                                </div>
                                
                                <div class="col-md-6">
                                    <div class="mb-3">
                                        <label for="priceUnit" class="form-label">Giá bán lẻ cho <%= product.getItemUnitName() != null ? product.getItemUnitName() : "đơn vị" %> (đ)</label>
                                        <input type="number" 
                                               class="form-control" 
                                               id="priceUnit" 
                                               name="priceUnit" 
                                               min="0" 
                                               step="1000"
                                               value="<%= product.getPriceUnit() != null ? product.getPriceUnit().intValue() : "" %>"
                                               <%= !canSetUnitPrice ? "disabled" : "" %>>
                                        <div class="form-text">
                                            <% if (canSetUnitPrice) { %>
                                                Giá bán lẻ cho 1 <%= product.getItemUnitName() != null ? product.getItemUnitName() : "đơn vị" %>

                                            <% } else { %>
                                                <span class="text-danger">Chưa có số lượng sau chuyển đổi. Vui lòng thực hiện chuyển đổi đơn vị trước.</span>
                                            <% } %>
                                        </div>
                                    </div>
                                </div>
                                <% } %>
                            </div>

                            <div class="d-flex gap-2">
                                <button type="submit" class="btn btn-primary" id="submitBtn">
                                    <i class="fas fa-save"></i> Lưu giá bán lẻ
                                </button>
                                <a href="${pageContext.request.contextPath}/admin/product" class="btn btn-secondary">
                                    <i class="fas fa-times"></i> Hủy
                                </a>
                            </div>
                        </form>
                        
                        <% } else { %>
                        <div class="alert alert-danger">
                            <i class="fas fa-exclamation-triangle"></i>
                            <strong>Lỗi!</strong> Không tìm thấy thông tin sản phẩm.
                        </div>
                        <% } %>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            // Auto-hide alerts
            document.addEventListener('DOMContentLoaded', function() {
                const alerts = document.querySelectorAll('.alert');
                alerts.forEach(alert => {
                    setTimeout(function() {
                        if (alert && alert.parentNode) {
                            alert.remove();
                        }
                    }, 5000);
                });
            });
        </script>
    </body>
</html>



