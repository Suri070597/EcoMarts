<%@page import="java.util.List"%>
<%@page import="model.Supplier"%>
<%@page import="model.Category"%>
<%@page import="model.Product"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Product mo = (Product) request.getAttribute("mo");
    List<Category> cate = (List<Category>) request.getAttribute("dataCate");
    List<Supplier> sup = (List<Supplier>) request.getAttribute("dataSup");
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Edit Product</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">

        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/css/bootstrap-icons.min.css">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
        <link rel="stylesheet" href="assets/css/crudProduct.css"/>
    </head>
    <body>
        <div class="main-container">
            <%@ include file="../sidebar.jsp" %>
            <div class="content">
                <div class="content-container">
                    <h1>Edit Product</h1>
                    <div id="imageError" class="alert alert-danger d-none" role="alert"></div>
                    <% if (mo == null) { %>
                    <p>There is no product with that id</p>
                    <a href="Product" class="btn btn-secondary">Back</a>
                    <% } else {%>
                    <form id="createForm" method="post" action="Product?action=update&id=<%= mo.getProductID()%>" enctype="multipart/form-data">
                        <div class="mb-3">
                            <label class="form-label">Product ID</label>
                            <input type="text" class="form-control" value="<%= mo.getProductID()%>" disabled>
                            <input type="hidden" name="id" value="<%= mo.getProductID()%>"/>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Product Name</label>
                            <input type="text" class="form-control" name="pName" required value="<%= mo.getProductName()%>" />
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Product Price</label>
                            <input type="number" min="0" step="any" class="form-control" name="pPrice" required value="<%= mo.getPrice()%>" />
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Product Quantity</label>
                            <input type="number" min="0" class="form-control" name="pQuantity" required value="<%= mo.getQuantity()%>" />
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Product Unit</label>
                            <input type="text" class="form-control" name="pUnit" required value="<%= mo.getUnit()%>" />
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Product Description</label>
                            <textarea class="form-control" name="pDescription" rows="6" required><%= mo.getDescription()%></textarea>
                        </div>


                        <div class="mb-3">
                            <label class="form-label">Current Image</label><br>
                            <img src="ImageServlet?name=<%= mo.getImage()%>" alt="Current Image" width="100" />
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Change Image (optional)</label>
                            <input type="file" class="form-control" name="pImage" id="pImage" accept=".jpg,.jpeg,.png" >
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Category</label>
                            <select name="categoryID" class="form-select" required>
                                <option value="">-- Select Category --</option>
                                <% for (Category c : cate) {
                                        if (c.getParentID() != 0) {
                                            String parentName = "";
                                            for (Category p : cate) {
                                                if (c.getParentID() == p.getCategoryID()) {
                                                    parentName = p.getCategoryName() + " > ";
                                                    break;
                                                }
                                            }
                                            String display = parentName + c.getCategoryName();
                                %>
                                <option value="<%= c.getCategoryID()%>" <%= (mo.getCategory() != null && mo.getCategory().getCategoryID() == c.getCategoryID()) ? "selected" : ""%>>
                                    <%= display%>
                                </option>
                                <% }
                                    } %>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Supplier</label>
                            <select name="supplierID" class="form-select" required>
                                <option value="">-- Select Supplier --</option>
                                <% for (Supplier s : sup) {%>
                                <option value="<%= s.getSupplierId()%>" <%= (mo.getSupplier() != null && mo.getSupplier().getSupplierId() == s.getSupplierId()) ? "selected" : ""%>>
                                    <%= s.getSupplierName()%>
                                </option>
                                <% } %>
                            </select>
                        </div>

                        <a href="Product" class="btn btn-secondary">Back</a>
                        <button type="submit" class="btn btn-primary">Update</button>
                    </form>
                    <% }%>
                </div>
            </div>
        </div>
        <script src="assets/js/handelImg.js"></script>
    </body>
</html>
