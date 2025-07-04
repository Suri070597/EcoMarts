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
                    <h1>Edit Product</h1>
                    <div id="imageError" class="alert alert-danger d-none" role="alert"></div>
                    <% String error = (String) request.getAttribute("error"); %>
                    <% if (error != null) {%>
                    <div class="alert alert-danger"><%= error%></div>
                    <% }%>
                    <% if (mo == null) { %>
                    <div class="alert alert-danger">❌ Không tìm thấy sản phẩm với ID yêu cầu.</div>
                    <a href="${pageContext.request.contextPath}/admin/product" class="btn btn-secondary">Trở về</a>
                    <% } else {%>

                    <!-- Hiển thị form update như bình thường -->
                    <form id="createForm" method="post" action="${pageContext.request.contextPath}/admin/product?action=update&id=<%= mo.getProductID()%>" enctype="multipart/form-data">
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
                            <input type="number" min="0" step="any" class="form-control" name="pPrice" required
                                   value="<%= new java.text.DecimalFormat("0.##").format(mo.getPrice())%>" />
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Product Quantity</label>
                            <input type="number" min="0" class="form-control" name="pQuantity" required value="<%= mo.getStockQuantity()%>" />
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Product Unit</label>
                            <select class="form-select" name="pUnit" required>
                                <option value="">-- Chọn đơn vị --</option>
                                <option value="kg" <%= "kg".equals(mo.getUnit()) ? "selected" : ""%>>kg</option>
                                <option value="gói" <%= "gói".equals(mo.getUnit()) ? "selected" : ""%>>gói</option>
                                <option value="chai" <%= "chai".equals(mo.getUnit()) ? "selected" : ""%>>chai</option>
                                <option value="lon" <%= "lon".equals(mo.getUnit()) ? "selected" : ""%>>lon</option>
                                <option value="lốc" <%= "lốc".equals(mo.getUnit()) ? "selected" : ""%>>lốc</option>
                                <option value="thùng" <%= "thùng".equals(mo.getUnit()) ? "selected" : ""%>>thùng</option>
                                <option value="hộp" <%= "hộp".equals(mo.getUnit()) ? "selected" : ""%>>hộp</option>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Product Description</label>
                            <textarea class="form-control" name="pDescription" rows="6" required><%= mo.getDescription()%></textarea>
                        </div>


                        <div class="mb-3">
                            <label class="form-label">Current Image</label><br>
                            <img src="<%= request.getContextPath()%>/ImageServlet?name=<%= mo.getImageURL()%>" alt="Current Image" width="100" />
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Change Image (optional)</label>
                            <input type="file" class="form-control" name="pImage" id="pImage" accept=".jpg,.jpeg,.png" >
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Manufacture Date</label>
                            <%
                                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                                String manufactureDateStr = (mo.getManufactureDate() != null) ? sdf.format(mo.getManufactureDate()) : "";
                            %>
                            <input type="date" class="form-control" name="manufactureDate" value="<%= manufactureDateStr%>" required />
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Expiration Period</label>
                            <%
                                int expiryMonths = 0;
                                if (mo.getManufactureDate() != null && mo.getExpirationDate() != null) {
                                    java.util.Calendar manu = java.util.Calendar.getInstance();
                                    java.util.Calendar exp = java.util.Calendar.getInstance();
                                    manu.setTime(mo.getManufactureDate());
                                    exp.setTime(mo.getExpirationDate());

                                    expiryMonths = (exp.get(java.util.Calendar.YEAR) - manu.get(java.util.Calendar.YEAR)) * 12
                                            + (exp.get(java.util.Calendar.MONTH) - manu.get(java.util.Calendar.MONTH));
                                }
                            %>
                            <select class="form-select" name="expirySelect" required>
                                <option value="">-- Select Expiration Period --</option>
                                <option value="3" <%= (expiryMonths == 3) ? "selected" : ""%>>3 months</option>
                                <option value="6" <%= (expiryMonths == 6) ? "selected" : ""%>>6 months</option>
                                <option value="12" <%= (expiryMonths == 12) ? "selected" : ""%>>1 year</option>
                                <option value="24" <%= (expiryMonths == 24) ? "selected" : ""%>>2 years</option>
                            </select>
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
                                    <%= s.getCompanyName()%>
                                </option>
                                <% } %>
                            </select>
                        </div>

                        <a href="${pageContext.request.contextPath}/admin/product" class="btn btn-secondary">Back</a>
                        <button type="submit" class="btn btn-primary">Update</button>
                    </form>
                    <% }%>
                </div>
            </div>
        </div>
        <script src="${pageContext.request.contextPath}/assets/js/handelImg.js"></script>
    </body>
</html>
