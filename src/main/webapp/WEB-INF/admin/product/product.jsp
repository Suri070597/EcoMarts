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
        </script>
    </body>
</html>
