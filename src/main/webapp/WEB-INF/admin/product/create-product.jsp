<%@page import="dao.productDAO"%>
<%@page import="model.Supplier"%>
<%@page import="model.Category"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    List<Category> cate = (List<Category>) request.getAttribute("dataCate");
    List<Supplier> sup = (List<Supplier>) request.getAttribute("dataSup");
%>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Create Product</title>
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/css/bootstrap-icons.min.css">
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
                <div class="container">
                    <h1 style="margin-top: 45px;">Create product</h1>
                    <div id="imageError" class="alert alert-danger d-none" role="alert"></div>

                    <% String error = (String) request.getAttribute("error"); %>
                    <% if (error != null) {%>
                    <div class="alert alert-danger"><%= error%></div>
                    <% } %>
                    <form id="createForm" method="post" action="${pageContext.request.contextPath}/admin/product?action=create" enctype="multipart/form-data">
                        <div class="mb-3">
                            <label class="form-label">Product Name</label>
                            <input type="text" class="form-control" name="pName" required />
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Product Price</label>
                            <input type="number" min="0" step="any" class="form-control" name="pPrice" required />
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Product Quantity</label>
                            <input type="number" min="0" class="form-control" name="pQuanity" required />
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Product Unit</label>
                            <input type="text" class="form-control" name="pUnit" required />
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Product Description</label>
                            <textarea class="form-control" name="pDescription" rows="4" required></textarea>
                        </div>


                        <div class="mb-3">
                            <label class="form-label">Product Image</label>
                            <input type="file" class="form-control" name="pImage" id="pImage" accept=".jpg,.jpeg,.png" required>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Category</label>
                            <select name="categoryID" class="form-select" required>
                                <option value="">-- Select Category --</option>
                                <%  for (Category c : cate) {
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
                                <option value="<%= c.getCategoryID()%>"><%= display%></option>
                                <%
                                        }
                                    }
                                %>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Supplier</label>
                            <select name="supplierID" class="form-select" required>
                                <option value="">-- Select Supplier --</option>
                                <%
                                    for (Supplier s : sup) {
                                %>
                                <option value="<%= s.getSupplierId()%>"><%= s.getCompanyName()%></option>
                                <%
                                    }
                                %>
                            </select>
                        </div>
                        <a href="${pageContext.request.contextPath}/admin/product" class="btn btn-secondary" id="back"><i class="bi bi-arrow-return-left"></i> Back</a>
                        <button type="submit" class="btn btn-primary" id="submit"><i class="bi bi-file-earmark-plus"></i> Create</button>
                    </form>
                </div>
            </div>
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
            <script src="${pageContext.request.contextPath}/assets/js/handelImg.js"></script>
    </body>
</html>
