<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Nhập Kho</title>
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/img/eco.png" type="image/x-icon">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis()%>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis()%>">
    </head>
    <body>
        <div class="container-fluid">
            <jsp:include page="../components/sidebar.jsp" />

            <div class="main-content">
                <div class="container">
                    <h1>Phiếu Nhập Kho</h1>

                    <c:if test="${not empty error}">
                        <div class="alert alert-danger">${error}</div>
                    </c:if>

                    <div class="mb-3">
                        <button type="button" id="toggleForm" class="btn btn-primary">
                            <i class="fas fa-plus"></i> Tạo phiếu nhập
                        </button>
                    </div>

                    <div id="stockInForm" style="display:none;">
                        <form method="POST" action="${pageContext.request.contextPath}/staff/stockin" class="needs-validation" novalidate>
                            <input type="hidden" name="action" value="create">

                            <!-- Nhà cung cấp -->
                            <div class="mb-3">
                                <label class="form-label" for="supplierId">Nhà cung cấp</label>
                                <select class="form-control" id="supplierId" name="supplierId" required>
                                    <option value="">-- Chọn nhà cung cấp --</option>
                                    <c:forEach var="s" items="${suppliers}">
                                        <option value="${s.supplierID}" ${param.supplierId eq s.supplierID ? 'selected' : ''}>
                                            ${s.brandName} - ${s.companyName}
                                        </option>
                                    </c:forEach>
                                </select>
                                <div class="invalid-feedback">Vui lòng chọn nhà cung cấp</div>
                            </div>

                            <!-- Người nhận -->
                            <div class="mb-3">
                                <label class="form-label" for="receiverId">Người nhận</label>
                                <select class="form-control" id="receiverId" name="receiverId" required>
                                    <option value="">-- Chọn người nhận --</option>
                                    <c:forEach var="r" items="${receivers}">
                                        <option value="${r.id}" ${param.receiverId eq r.id ? 'selected' : ''}>
                                            ${r.fullName}
                                        </option>
                                    </c:forEach>
                                </select>
                                <div class="invalid-feedback">Vui lòng chọn người nhận</div>
                            </div>

                            <!-- Ngày nhập -->
                            <div class="mb-3">
                                <label class="form-label" for="date">Ngày nhập</label>
                                <input type="date" class="form-control" id="date" name="date" value="${param.date}" required>
                                <div class="invalid-feedback">Vui lòng chọn ngày nhập</div>
                            </div>

                            <!-- Ghi chú -->
                            <div class="mb-3">
                                <label class="form-label" for="note">Ghi chú</label>
                                <textarea class="form-control" id="note" name="note" rows="3">${param.note}</textarea>
                            </div>

                            <!-- Sản phẩm nhập -->
                            <div class="mb-3">
                                <label class="form-label">Sản phẩm</label>

                                <!-- Bộ lọc -->
                                <div class="row g-2 mb-2">
                                    <div class="col-md-6">
                                        <select id="categoryFilter" class="form-select">
                                            <option value="">-- Tất cả danh mục --</option>
                                            <c:forEach var="c" items="${categories}">
                                                <option value="${c.categoryID}">${c.categoryName}</option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="col-md-6">
                                        <input type="text" id="productSearch" class="form-control" placeholder="Tìm sản phẩm theo tên...">
                                    </div>
                                </div>

                                <table class="table table-bordered">
                                    <thead>
                                        <tr>
                                            <th>Danh mục</th>
                                            <th>Sản phẩm</th>
                                            <th>Đơn vị</th>
                                            <th>Số lượng</th>
                                            <th>Giá nhập</th>
                                            <th></th>
                                        </tr>
                                    </thead>
                                    <tbody id="productTableBody">
                                        <tr class="product-row">
                                            <td>
                                                <select class="form-control category-select">
                                                    <option value="">-- Tất cả danh mục --</option>
                                                    <c:forEach var="c" items="${categories}">
                                                        <option value="${c.categoryID}">${c.categoryName}</option>
                                                    </c:forEach>
                                                </select>
                                            </td>
                                            <td>
                                                <select name="productId" class="form-control product-select" required>
                                                    <option value="">-- Chọn sản phẩm --</option>
                                                    <c:forEach var="p" items="${products}">
                                                        <option value="${p.productID}"
                                                                data-unit="${p.itemUnitName}"
                                                                data-item-unit="${p.itemUnitName}"
                                                                data-box-unit="${p.boxUnitName}"
                                                                data-unit-per-box="${p.unitPerBox}"
                                                                data-category-id="${p.categoryID}">
                                                            ${p.productName} - SL: ${p.stockQuantity}
                                                        </option>
                                                    </c:forEach>
                                                </select>
                                            </td>
                                            <td>
                                                <select name="unit" class="form-control unit-select" required>
                                                    <option value="">-- Chọn đơn vị --</option>
                                                </select>
                                                <input type="hidden" name="unitSelectedName" class="unit-selected-name">
                                                <input type="hidden" name="convertedQuantity" class="converted-quantity">
                                            </td>
                                            <td><input type="number" name="quantity" class="form-control quantity-input" min="1" required></td>
                                            <td><input type="number" name="price" step="0.01" min="0" class="form-control" required></td>
                                            <td><button type="button" class="btn btn-danger btn-sm removeRow"><i class="fas fa-trash"></i></button></td>
                                        </tr>
                                    </tbody>
                                </table>

                                <button type="button" id="addRow" class="btn btn-success btn-sm">
                                    <i class="fas fa-plus"></i> Thêm sản phẩm
                                </button>
                            </div>

                            <div class="btn-group mt-3">
                                <a href="${pageContext.request.contextPath}/staff/stockin" class="btn btn-secondary">Quay lại</a>
                                <button type="submit" class="btn btn-primary">Lưu phiếu nhập</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            // Validation
            (function () {
                'use strict'
                var forms = document.querySelectorAll('.needs-validation')
                Array.prototype.slice.call(forms).forEach(function (form) {
                    form.addEventListener('submit', function (event) {
                        if (!form.checkValidity()) {
                            event.preventDefault()
                            event.stopPropagation()
                        }
                        form.classList.add('was-validated')
                    }, false)
                })
            })()

            const stockInForm = document.getElementById('stockInForm');
            const toggleFormBtn = document.getElementById('toggleForm');
            toggleFormBtn.addEventListener('click', () => {
                if (stockInForm.style.display === 'none') {
                    stockInForm.style.display = 'block';
                    toggleFormBtn.innerHTML = '<i class="fas fa-minus"></i> Ẩn phiếu nhập';
                } else {
                    stockInForm.style.display = 'none';
                    toggleFormBtn.innerHTML = '<i class="fas fa-plus"></i> Tạo phiếu nhập';
                }
            });

            const productTableBody = document.getElementById('productTableBody');

            function refreshUnitOptions(row) {
                const productSelect = row.querySelector('.product-select');
                const unitSelect = row.querySelector('.unit-select');
                const selectedOption = productSelect.selectedOptions[0];
                unitSelect.innerHTML = '';

                if (!selectedOption) return;

                const unit = selectedOption.dataset.unit || '';
                if (unit) {
                    const opt = document.createElement('option');
                    opt.value = unit;
                    opt.textContent = unit;
                    unitSelect.appendChild(opt);
                    row.querySelector('.unit-selected-name').value = unit;
                }

                const qty = parseFloat(row.querySelector('.quantity-input').value || '0');
                row.querySelector('.converted-quantity').value = isNaN(qty) ? '' : qty;
            }

            productTableBody.addEventListener('change', function (e) {
                if (e.target.classList.contains('product-select')) {
                    const row = e.target.closest('tr');
                    refreshUnitOptions(row);
                }
                if (e.target.classList.contains('quantity-input')) {
                    const row = e.target.closest('tr');
                    const qty = parseFloat(row.querySelector('.quantity-input').value || '0');
                    row.querySelector('.converted-quantity').value = isNaN(qty) ? '' : qty;
                }
            });

            productTableBody.addEventListener('click', function (e) {
                if (e.target.closest('.removeRow')) {
                    const row = e.target.closest('tr');
                    row.remove();
                }
            });

            document.getElementById('addRow').addEventListener('click', () => {
                const firstRow = productTableBody.querySelector('tr.product-row');
                const newRow = firstRow.cloneNode(true);
                newRow.querySelectorAll('input, select').forEach(i => {
                    if (i.tagName === 'SELECT')
                        i.selectedIndex = 0;
                    else
                        i.value = '';
                });
                productTableBody.appendChild(newRow);
                filterProducts(); // lọc cho dòng mới
            });

            // ====== Lọc sản phẩm theo danh mục + tìm kiếm ======
            const categoryFilter = document.getElementById('categoryFilter');
            const productSearch = document.getElementById('productSearch');

            function filterProducts() {
                const selectedCategory = categoryFilter.value.trim();
                const searchKeyword = productSearch.value.trim().toLowerCase();

                document.querySelectorAll('.product-select').forEach(select => {
                    Array.from(select.options).forEach(option => {
                        if (!option.value) return; // bỏ qua "-- Chọn sản phẩm --"

                        const categoryId = option.getAttribute('data-category-id') || '';
                        const productText = option.textContent.toLowerCase();

                        const matchCategory = (selectedCategory === '' || categoryId === selectedCategory);
                        const matchSearch = (searchKeyword === '' || productText.includes(searchKeyword));

                        option.style.display = (matchCategory && matchSearch) ? '' : 'none';
                    });

                    // Reset nếu option đang chọn bị ẩn
                    if (select.selectedIndex > 0 && select.options[select.selectedIndex].style.display === 'none') {
                        select.selectedIndex = 0;
                    }
                });
            }

            categoryFilter.addEventListener('change', filterProducts);
            productSearch.addEventListener('input', filterProducts);
        </script>
    </body>
</html>
