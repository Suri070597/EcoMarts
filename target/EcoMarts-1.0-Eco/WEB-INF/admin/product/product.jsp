<%@page import="model.Supplier"%>
<%@page import="model.Category"%>
<%@page import="model.Product"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Product Management</title>
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
                            <h1 class="card-title">Product List</h1>
                            <div class="d-flex gap-2">
                                <form action="${pageContext.request.contextPath}/admin/account" method="get"
                                      class="search-box">
                                    <i class="fas fa-search"></i>
                                    <input type="text" name="search" placeholder="Search accounts..."
                                           value="${keyword != null ? keyword : ''}">
                                    <button type="submit" class="btn btn-sm btn-primary">Search</button>
                                </form>
                                <a href="${pageContext.request.contextPath}/admin/product?action=create" class="btn btn-success">
                                    <i class="fas fa-plus"></i> Create
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
                                    <th>Category</th>
                                    <th>Product Name</th>
                                    <th>Price</th>
                                    <th>Quantity</th>
                                    <th>Unit</th>
                                    <th>Description</th>
                                    <th>Image</th>
                                    <th>Supplier</th>
                                    <th>Create Date</th>
                                    <th>Actions</th>
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
                                    <td><%= new java.text.DecimalFormat("#,###").format(pro.getPrice())%></td>
                                    <td><%= pro.getStockQuantity()%></td>
                                    <td><%= pro.getUnit()%></td>
                                    <td><%= pro.getDescription().replaceAll("\n", "<br/>")%></td>
                                    <td>
                                        <img src="<%= request.getContextPath()%>/ImageServlet?name=<%= pro.getImageURL()%>" alt="Product Image" style="width: 80px; height: auto;">
                                    </td>
                                    <td><%= pro.getSupplier().getCompanyName()%></td>
                                    <td><%= pro.getCreatedAt()%></td>
                                    <td>
                                        <div class="d-flex gap-2 justify-content-center">
                                            <a href="${pageContext.request.contextPath}/admin/product?action=update&id=<%= pro.getProductID()%>" class="btn btn-sm btn-primary">
                                                <i class="fas fa-edit"></i>
                                            </a>
                                            <a href="<%= request.getContextPath()%>/admin/product?action=delete&id=<%= pro.getProductID()%>" class="btn btn-sm btn-danger">
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
                            <h1 class="text-danger my-4">There is no data!</h1>
                        </div>
                        <% }%>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
