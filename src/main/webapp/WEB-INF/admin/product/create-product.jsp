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

                        <div class="mb-3" id="fruit-price-group" style="display:none">
                            <label class="form-label">Price (VND/kg)</label>
                            <input type="number" min="0" step="0.01" class="form-control" name="fruitPrice" id="fruitPrice" placeholder="Enter price per kg" />
                        </div>
                        <div class="mb-3" id="fruit-qty-group" style="display:none">
                            <label class="form-label">Stock Quantity (kg)</label>
                            <input type="number" min="1" step="1" class="form-control" name="fruitQuantity" id="fruitQuantity" placeholder="Enter quantity in kg (integer only, e.g., 10, 20, 50...)" />
                        </div>
                        <div class="mb-3 box-related">
                            <label class="form-label">Price 1 box/pack/case</label>
                            <input type="number" min="0" step="any" class="form-control" name="boxPrice" id="boxPrice" />
                        </div>
                        <div class="mb-3 box-related">
                            <label class="form-label">Quantity 1 box/pack/case</label>
                            <input type="number" min="0" class="form-control" name="boxQuantity" id="boxQuantity" oninput="updateBoxPreview()" />
                            <div id="box-preview" class="form-text text-primary"></div>
                        </div>
                        <div class="mb-3 box-related">
                            <label class="form-label">Quantity of products in 1 box/pack/case</label>
                            <input type="number" min="1" class="form-control" name="unitPerBox" id="unitPerBox" oninput="updateBoxPreview()" />
                        </div>
                        <div class="mb-3 box-related">
                            <label class="form-label">Unit 1 box/pack/case</label>
                            <select class="form-select" name="boxUnitName" id="boxUnitName">
                                <option value="">-- Chọn đơn vị thùng/hộp/kiện --</option>
                                <option value="thùng">thùng</option>
                                <option value="hộp">hộp</option>
                                <option value="kiện">kiện</option>
                                <option value="lốc">lốc</option>
                            </select>
                        </div>
                        <div class="mb-3" id="item-unit-group">
                            <label class="form-label">Smallest unit</label>
                            <select class="form-select" name="itemUnitName" id="itemUnitName" required>
                                <option value="">-- Chọn đơn vị nhỏ nhất --</option>
                                <option value="chai">chai</option>
                                <option value="lon">lon</option>
                                <option value="cái">cái</option>
                                <option value="hộp">hộp</option>
                            </select>
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
                            <label class="form-label" id="importOrManufactureLabel">Manufacture Date</label>
                            <input type="date" class="form-control" name="manufactureDate" id="manufactureDate" required />
                        </div>

                        <div class="mb-3" id="fruit-expiry-group" style="display:none">
                            <label class="form-label">Expiration (days)</label>
                            <input type="number" min="1" step="1" class="form-control" name="fruitExpiryDays" id="fruitExpiryDays" placeholder="Enter shelf life in days (e.g., 3, 7, 14...)" />
                        </div>
                        <div class="mb-3" id="expiry-select-group">
                            <label class="form-label">Expiration Period</label>
                            <select class="form-select" id="expirySelect" name="expirySelect" required>
                                <option value="">-- Select Expiration Period --</option>
                                <option value="3">3 months</option>
                                <option value="6">6 months</option>
                                <option value="12">1 year</option>
                                <option value="24">2 years</option>
                            </select>
                        </div>


                        <div class="mb-3">
                            <label class="form-label">Category</label>
                            <select name="categoryID" class="form-select" required>
                                <option value="">-- Select Category --</option>
                                <% if (cate != null) {
                                        for (Category c : cate) {
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
                                    }
                                %>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Supplier</label>
                            <select name="supplierID" class="form-select" required>
                                <option value="">-- Select Supplier --</option>
                                <% if (sup != null) {

                                        for (Supplier s : sup) {
                                %>
                                <option value="<%= s.getSupplierId()%>"><%= s.getCompanyName()%></option>
                                <%
                                        }
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
            <script>
                // Danh sách ID trái cây (cha = 3 hoặc chính nó = 3)
                function isFruitCategory(selectedId) {
                    // ID 3 là trái cây, các danh mục con cũng có parentID = 3
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
                    var boxFields = document.querySelectorAll('.box-related');
                    var fruitPriceGroup = document.getElementById('fruit-price-group');
                    var fruitQtyGroup = document.getElementById('fruit-qty-group');
                    var itemUnitGroup = document.getElementById('item-unit-group');
                    var itemUnitSelect = document.getElementById('itemUnitName');
                    var fruitPriceInput = document.getElementById('fruitPrice');
                    var fruitQtyInput = document.getElementById('fruitQuantity');
                    var boxPriceInput = document.getElementById('boxPrice');
                    var boxQtyInput = document.getElementById('boxQuantity');
                    var unitPerBoxInput = document.getElementById('unitPerBox');
                    var boxUnitNameInput = document.getElementById('boxUnitName');
                    var fruitExpiryGroup = document.getElementById('fruit-expiry-group');
                    var expirySelectGroup = document.getElementById('expiry-select-group');
                    var expirySelect = document.getElementById('expirySelect');
                    var fruitExpiryDaysInput = document.getElementById('fruitExpiryDays');
                    function updateForm() {
                        var selected = cateSelect.value;
                        if (isFruitCategory(selected)) {
                            boxFields.forEach(f => {
                                f.style.display = 'none';
                                var input = f.querySelector('input, select');
                                if (input) {
                                    input.required = false;
                                    input.disabled = true;
                                }
                            });
                            fruitPriceGroup.style.display = '';
                            fruitQtyGroup.style.display = '';
                            fruitExpiryGroup.style.display = '';
                            fruitPriceInput.required = true;
                            fruitPriceInput.disabled = false;
                            fruitQtyInput.required = true;
                            fruitQtyInput.disabled = false;
                            fruitExpiryDaysInput.required = true;
                            fruitExpiryDaysInput.disabled = false;
                            expirySelectGroup.style.display = 'none';
                            expirySelect.required = false;
                            expirySelect.disabled = true;
                            // Đơn vị nhỏ nhất chỉ là kg
                            itemUnitSelect.innerHTML = '<option value="kg">kg</option>';
                            var label = document.getElementById('importOrManufactureLabel');
                            label.textContent = 'Import Date';
                        } else {
                            boxFields.forEach(f => {
                                f.style.display = '';
                                var input = f.querySelector('input, select');
                                if (input) {
                                    input.required = true;
                                    input.disabled = false;
                                }
                            });
                            fruitPriceGroup.style.display = 'none';
                            fruitQtyGroup.style.display = 'none';
                            fruitExpiryGroup.style.display = 'none';
                            fruitPriceInput.required = false;
                            fruitPriceInput.disabled = true;
                            fruitQtyInput.required = false;
                            fruitQtyInput.disabled = true;
                            fruitExpiryDaysInput.required = false;
                            fruitExpiryDaysInput.disabled = true;
                            expirySelectGroup.style.display = '';
                            expirySelect.required = true;
                            expirySelect.disabled = false;
                            // Khôi phục các đơn vị nhỏ nhất khác
                            itemUnitSelect.innerHTML = '<option value="">-- Chọn đơn vị nhỏ nhất --</option>' +
                                '<option value="chai">chai</option>' +
                                '<option value="lon">lon</option>' +
                                '<option value="cái">cái</option>' +
                                '<option value="hộp">hộp</option>';
                            var label = document.getElementById('importOrManufactureLabel');
                            label.textContent = 'Manufacture Date';
                        }
                    }
                    cateSelect.addEventListener('change', updateForm);
                    updateForm();
                });
            </script>
            <script>
                function updateBoxPreview() {
                    var boxQty = parseInt(document.getElementById('boxQuantity').value) || 0;
                    var unitPerBox = parseInt(document.getElementById('unitPerBox').value) || 0;
                    var itemUnit = document.getElementById('itemUnitName') ? document.getElementById('itemUnitName').value : '';
                    if (boxQty > 0 && unitPerBox > 0) {
                        var total = boxQty * unitPerBox;
                        document.getElementById('box-preview').textContent = boxQty + ' x ' + unitPerBox + ' = ' + total + (itemUnit ? ' ' + itemUnit : '');
                    } else {
                        document.getElementById('box-preview').textContent = '';
                    }
                }
            </script>
    </body>
</html>
