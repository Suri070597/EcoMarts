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
<%-- Thêm biến xác định sản phẩm là trái cây --%>
<%
    boolean isFruit = false;
    if (mo.getCategory() != null) {
        int catId = mo.getCategory().getCategoryID();
        Integer parentId = mo.getCategory().getParentID();
        isFruit = (catId == 3) || (parentId != null && parentId == 3);
    }
%>
<% int fruitExpiryDays = 0;
   if (isFruit && mo.getManufactureDate() != null && mo.getExpirationDate() != null) {
       long diff = mo.getExpirationDate().getTime() - mo.getManufactureDate().getTime();
       fruitExpiryDays = (int) (diff / (1000 * 60 * 60 * 24));
   }
%>
<%
    // Xác định sản phẩm ban đầu có phải trái cây không
    boolean isOriginalFruit = false;
    if (mo.getCategory() != null) {
        int catId = mo.getCategory().getCategoryID();
        Integer parentId = mo.getCategory().getParentID();
        isOriginalFruit = (catId == 3) || (parentId != null && parentId == 3);
    }
%>
<div id="fruit-block-warning" class="alert alert-danger mt-2 ms-auto" style="display:none; max-width: 1190px;">Không thể chuyển sản phẩm thường sang trái cây. Vui lòng tạo mới sản phẩm trái cây nếu cần.</div>
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
                    <div id="fruit-block-warning" class="alert alert-danger mt-2 ms-auto" style="display:none; max-width: 1190px;">Không thể chuyển sản phẩm thường sang trái cây. Vui lòng tạo mới sản phẩm trái cây nếu cần.</div>
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
                        <% if (isFruit) { %>
                            <div class="mb-3">
                                <label class="form-label">Price (VND/kg)</label>
                                <input type="number" min="0" step="0.01" class="form-control" name="fruitPrice" value="<%= (int)Math.round(mo.getPrice()) %>" required />
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Stock Quantity (kg)</label>
                                <input type="number" min="1" step="1" class="form-control" name="fruitQuantity" value="<%= (int)Math.round(mo.getStockQuantity()) %>" required />
                            </div>
                        <% } else { %>
                            <div class="mb-3">
                                <label class="form-label">Giá 1 thùng/hộp/kiện</label>
                                <input type="number" min="0" step="1" class="form-control" name="boxPrice" required value="<%= mo.getUnitPerBox() > 0 ? (int)Math.round(mo.getPrice() * mo.getUnitPerBox()) : "" %>" />
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Số lượng thùng/hộp/kiện</label>
                                <input type="number" min="0" step="1" class="form-control" name="boxQuantity" required value="<%= mo.getUnitPerBox() > 0 ? (long)(mo.getStockQuantity() / mo.getUnitPerBox()) : (long)mo.getStockQuantity() %>" id="boxQuantity" />
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Số lượng sản phẩm trong 1 thùng/hộp/kiện</label>
                                <input type="number" min="1" step="1" class="form-control" name="unitPerBox" required value="<%= (long)mo.getUnitPerBox() %>" id="unitPerBox" />
                            </div>
                            <div class="mb-3">
                                <label class="form-label">Đơn vị thùng/hộp/kiện</label>
                                <select class="form-select" name="boxUnitName" required>
                                    <option value="">-- Chọn đơn vị thùng/hộp/kiện --</option>
                                    <option value="thùng" <%= "thùng".equals(mo.getBoxUnitName()) ? "selected" : ""%>>thùng</option>
                                    <option value="hộp" <%= "hộp".equals(mo.getBoxUnitName()) ? "selected" : ""%>>hộp</option>
                                    <option value="kiện" <%= "kiện".equals(mo.getBoxUnitName()) ? "selected" : ""%>>kiện</option>
                                    <option value="lốc" <%= "lốc".equals(mo.getBoxUnitName()) ? "selected" : ""%>>lốc</option>
                                </select>
                            </div>
                        <% } %>
                        <div class="mb-3">
                            <label class="form-label">Đơn vị nhỏ nhất</label>
                            <select class="form-select" name="itemUnitName" required>
                                <option value="">-- Chọn đơn vị nhỏ nhất --</option>
                                <option value="chai" <%= "chai".equals(mo.getItemUnitName()) ? "selected" : ""%>>chai</option>
                                <option value="lon" <%= "lon".equals(mo.getItemUnitName()) ? "selected" : ""%>>lon</option>
                                <option value="cái" <%= "cái".equals(mo.getItemUnitName()) ? "selected" : ""%>>cái</option>
                                <option value="hộp" <%= "hộp".equals(mo.getItemUnitName()) ? "selected" : ""%>>hộp</option>
                                <option value="kg" <%= "kg".equals(mo.getItemUnitName()) ? "selected" : ""%>>kg</option>
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
                            <label class="form-label" id="importOrManufactureLabel"><% if (isFruit) { %>Import Date<% } else { %>Manufacture Date<% } %></label>
                            <%
                                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
                                String manufactureDateStr = (mo.getManufactureDate() != null) ? sdf.format(mo.getManufactureDate()) : "";
                            %>
                            <input type="date" class="form-control" name="manufactureDate" value="<%= manufactureDateStr%>" required />
                        </div>
                        <% if (isFruit) { %>
                            <div class="mb-3">
                                <label class="form-label">Expiration (days)</label>
                                <input type="number" min="1" step="1" class="form-control" name="fruitExpiryDays" value="<%= fruitExpiryDays %>" placeholder="Enter shelf life in days (e.g., 3, 7, 14...)" />
                            </div>
                        <% } else { %>
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
                        <% } %>


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
        <!-- Xóa script updateBoxPreview và div box-preview -->
        <script>
function isFruitCategory(selectedId) {
    var fruitParentId = 3;
    var cateList = <%= cate != null ? new com.google.gson.Gson().toJson(cate) : "[]" %>;
    selectedId = parseInt(selectedId);
    if (selectedId === fruitParentId) return true;
    for (var i = 0; i < cateList.length; i++) {
        if (cateList[i].categoryID === selectedId && cateList[i].parentID === fruitParentId) return true;
    }
    return false;
}
document.addEventListener('DOMContentLoaded', function() {
    var cateSelect = document.querySelector('select[name="categoryID"]');
    var updateBtn = document.querySelector('button[type="submit"]');
    var fruitBlockWarning = document.getElementById('fruit-block-warning');
    var isOriginalFruit = <%= isOriginalFruit ? "true" : "false" %>;
    function checkBlockFruitChange() {
        var selected = cateSelect.value;
        var isFruit = isFruitCategory(selected);
        if ((!isOriginalFruit && isFruit) || (isOriginalFruit && !isFruit)) {
            fruitBlockWarning.style.display = '';
            if (updateBtn) updateBtn.disabled = true;
            if (!isOriginalFruit && isFruit) {
                fruitBlockWarning.textContent = 'Không thể chuyển sản phẩm thường sang trái cây. Vui lòng tạo mới sản phẩm trái cây nếu cần.';
            } else if (isOriginalFruit && !isFruit) {
                fruitBlockWarning.textContent = 'Không thể chuyển sản phẩm trái cây sang loại khác. Vui lòng tạo mới sản phẩm nếu cần.';
            }
        } else {
            fruitBlockWarning.style.display = 'none';
            if (updateBtn) updateBtn.disabled = false;
        }
    }
    cateSelect.addEventListener('change', checkBlockFruitChange);
    checkBlockFruitChange();
});
</script>
    </body>
</html>
