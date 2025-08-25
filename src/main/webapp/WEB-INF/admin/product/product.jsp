<%@page import="model.Manufacturer"%>
<%@page import="model.Category"%>
<%@page import="model.Product"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map, java.util.HashMap" %>
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
                        List<Product> product = (List<Product>) request.getAttribute("products");
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
                                    // Create DAO instance and cache for inventory data
                                    dao.ProductDAO productDAO = new dao.ProductDAO();
                                    Map<Integer, Map<String, Object>> inventoryCache = new HashMap<>();
                                    
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
                                            // Get quantity from Inventory table using cache
                                            java.util.Map<String, Object> inventory = inventoryCache.get(pro.getProductID());
                                            if (inventory == null) {
                                                inventory = productDAO.getProductInventory(pro.getProductID());
                                                inventoryCache.put(pro.getProductID(), inventory);
                                            }
                                            
                                            double qty = 0;
                                            int categoryId = 0;
                                            if (pro.getCategory() != null) {
                                                categoryId = pro.getCategory().getCategoryID();
                                            }

                                            // Get quantity based on category
                                            if (categoryId == 3) {
                                                // Trái cây - lấy từ KG (hiển thị dưới dạng UNIT)
                                                if (inventory.containsKey("UNIT_Quantity")) {
                                                    qty = (Double) inventory.get("UNIT_Quantity");
                                                }
                                                out.print(qty);
                                            } else {
                                                // Các loại khác - lấy từ BOX
                                                if (inventory.containsKey("BOX_Quantity")) {
                                                    qty = (Double) inventory.get("BOX_Quantity");
                                                }
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
                                        <% if (qty <= 0) { %>
                                        <span class="badge bg-danger">Hết hàng</span>
                                        <% } else if (qty <= 10) { %>
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
                                            <a href="${pageContext.request.contextPath}/admin/product?action=set-price&id=<%= pro.getProductID()%>" class="btn btn-sm btn-success">
                                                <i class="fas fa-dollar-sign"></i>
                                            </a>
                                            <%-- Convert button - disabled for fruits and out of stock --%>
                                            <%
                                                boolean isFruit = pro.getCategory().getParentID() == 3;
                                                // Lấy số lượng từ Inventory thay vì từ Product
                                                Map<String, Object> productInventory = productDAO.getProductInventory(pro.getProductID());
                                                double boxQuantity = 0;
                                                if (productInventory != null && productInventory.containsKey("BOX_Quantity")) {
                                                    boxQuantity = (Double) productInventory.get("BOX_Quantity");
                                                }
                                                boolean isOutOfStock = boxQuantity <= 0;
                                                String buttonDisabled = (isFruit || isOutOfStock) ? "disabled" : "";
                                                String buttonTitle = isFruit ? "Fruit cannot be converted" : (isOutOfStock ? "Out of stock" : "Convert product units");
                                            %>
                                            <a href="${pageContext.request.contextPath}/admin/product?action=convert&id=<%= pro.getProductID()%>" 
                                               class="btn btn-sm btn-warning <%= buttonDisabled.isEmpty() ? "" : "disabled" %>"
                                                    title="<%= buttonTitle %>">
                                                <i class="fas fa-exchange-alt"></i>
                                            </a>
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
    </body>
</html>
