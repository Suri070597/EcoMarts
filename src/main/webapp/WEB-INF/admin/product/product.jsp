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
        
        /* Style cho link disabled */
        .btn.disabled-link {
            opacity: 0.6;
            cursor: not-allowed;
            pointer-events: none;
            background-color: #6c757d !important;
            border-color: #6c757d !important;
            color: #fff !important;
            text-decoration: none;
        }
        
        .btn.disabled-link:hover {
            background-color: #6c757d !important;
            border-color: #6c757d !important;
            color: #fff !important;
            text-decoration: none;
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

                    <!-- Thông báo thành công/lỗi từ URL parameters -->
                    <% 
                        String success = request.getParameter("success");
                        String errorParam = request.getParameter("error");
                        String productName = request.getParameter("product_name");
                    %>
                    
                    <% if (errorParam != null) { %>
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        <i class="fas fa-exclamation-triangle"></i>
                        <strong>Lỗi!</strong> 
                        <% if (errorParam.equals("invalid_id")) { %>
                            ID sản phẩm không hợp lệ.
                        <% } else if (errorParam.equals("product_not_found")) { %>
                            Không tìm thấy sản phẩm.
                        <% } else if (errorParam.equals("delete_failed")) { %>
                            Xóa sản phẩm thất bại.
                        <% } else if (errorParam.equals("product_processing")) { %>
                            Sản phẩm đang được xử lý trong đơn hàng. Không thể xóa.
                        <% } else if (errorParam.equals("invalid_id_format")) { %>
                            Định dạng ID không hợp lệ.
                        <% } else if (errorParam.equals("delete_exception")) { %>
                            Đã xảy ra lỗi khi xóa sản phẩm.
                        <% } else { %>
                            <%= errorParam %>
                        <% } %>
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                    <% } %>

                    <% if (success != null && !success.equals("price_updated")) { %>
                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                        <i class="fas fa-check-circle"></i>
                        <strong>Thành công!</strong> 
                        <%= success %>
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                    <% } %>

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
                                    <th>Hình ảnh</th>
                                    <th>Ngày tạo</th>
                                    <th>Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                    for (Product pro : product) {
                                        Category child = pro.getCategory();
                                        String categoryDisplay = "N/A";
                                        if (child != null) {
                                            // Hiển thị tên danh mục con
                                            categoryDisplay = child.getCategoryName();
                                        }
                                %>
                                <tr>
                                    <td><%= pro.getProductID()%></td>
                                    <td><%= categoryDisplay%></td>
                                    <td><%= pro.getProductName()%></td>
                                    <td>
                                        <% 
                                            // Kiểm tra xem có phải trái cây không (parentID = 3)
                                            boolean isFruit = pro.getCategory() != null && pro.getCategory().getParentID() == 3;
                                            Double displayPrice = null;
                                            
                                            if (isFruit) {
                                                // Nếu là trái cây, hiển thị giá từ PriceUnit
                                                displayPrice = pro.getPriceUnit();
                                            } else {
                                                // Nếu không phải trái cây, hiển thị giá từ PriceBox
                                                displayPrice = pro.getPrice();
                                            }
                                            
                                            if (displayPrice != null && displayPrice > 0) {
                                                out.print(new java.text.DecimalFormat("#,###").format(displayPrice));
                                            } else {
                                                out.print("Chưa có giá");
                                            }
                                        %> đ
                                    </td>
                                    <td>
                                        <%
                                            double qty = pro.getStockQuantity();
                                            // Hiển thị số lượng thùng (luôn là số nguyên)
                                                if (qty == Math.floor(qty)) {
                                                    out.print((long) qty);
                                                } else {
                                                    out.print(qty);
                                            }
                                        %>
                                    </td>
                                    <td><%= pro.getBoxUnitName() != null ? pro.getBoxUnitName() : "N/A"%></td>
                                    <td>
                                        <% 
                                            double stockQty = pro.getStockQuantity();
                                            if (stockQty <= 0) { 
                                        %>
                                        <span class="badge bg-danger">Hết hàng</span>
                                        <% } else if (stockQty <= 5) { %>
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
                                                boolean isFruitForConvert = pro.getCategory() != null && pro.getCategory().getParentID() == 3;
                                                boolean isOutOfStock = pro.getStockQuantity() <= 0;
                                                boolean shouldDisable = isFruitForConvert || isOutOfStock;
                                                String disabledClass = shouldDisable ? "disabled-link" : "";
                                                String buttonTitle = isFruitForConvert ? "Fruit cannot be converted" : (isOutOfStock ? "Out of stock - Cannot convert" : "Convert product units");
                                                String href = shouldDisable ? "javascript:void(0)" : request.getContextPath() + "/admin/product?action=convert&id=" + pro.getProductID();
                                            %>
                                            <a href="<%= href %>" 
                                               class="btn btn-sm btn-warning <%= disabledClass %>" 
                                                    title="<%= buttonTitle %>">
                                                <i class="fas fa-exchange-alt"></i>
                                            </a>

                                            <a href="${pageContext.request.contextPath}/admin/product?action=setPrice&id=<%= pro.getProductID()%>"
                                               class="btn btn-sm btn-success"
                                               title="Nhập tiền bán lẻ">
                                                <i class="fas fa-dollar-sign"></i>
                                            </a>

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

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

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

            // Auto-hide alerts after 5 seconds
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






