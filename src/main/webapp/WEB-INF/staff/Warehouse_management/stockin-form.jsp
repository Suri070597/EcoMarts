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

                    <!-- Nút hiển thị form -->
                    <div class="mb-3">
                        <button type="button" id="toggleForm" class="btn btn-primary">
                            <i class="fas fa-plus"></i> Tạo phiếu nhập
                        </button>
                    </div>

                    <!-- Form ẩn mặc định -->
                    <div id="stockInForm" style="display:none;">
                        <form method="POST" action="${pageContext.request.contextPath}/staff/stockin" class="needs-validation" novalidate>
                            <input type="hidden" name="action" value="create">

                            <!-- Nhà cung cấp -->
                            <div class="mb-3">
                                <label class="form-label" for="supplierId">Nhà cung cấp</label>
                                <select class="form-control" id="supplierId" name="supplierId" required>
                                    <option value="">-- Chọn nhà cung cấp --</option>
                                    <c:forEach var="s" items="${suppliers}">
                                        <option value="${s.manufacturerID}" ${param.manufacturerID eq s.manufacturerID ? 'selected' : ''}>
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
                                        <option value="${r.accountID}" ${param.receiverId eq r.accountID ? 'selected' : ''}>
                                            ${r.fullName}
                                        </option>
                                    </c:forEach>
                                </select>
                                <div class="invalid-feedback">Vui lòng chọn người nhận</div>
                            </div>

                            <!-- Ngày nhập -->
                            <div class="mb-3">
                                <label class="form-label" for="date">Ngày nhập</label>
                                <input type="date" class="form-control" id="date" name="date" 
                                       value="${param.date}" 
                                       required
                                       min="<%= java.time.LocalDate.now()%>">
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

                                <!-- Filter: Danh mục và tìm kiếm -->
                                <div class="row g-2 mb-2">
                                    <div class="col-md-12" style="margin-bottom: 8px;">
                                        <input type="search" id="productSearch" 
                                               class="form-control" 
                                               placeholder="Tìm sản phẩm theo tên..." 
                                               autocomplete="off">
                                        <div id="searchResults" class="list-group position-absolute" style="z-index:1000; display:none;"></div>
                                    </div>
                                </div>

                                <table class="table table-bordered" style="table-layout: auto; width: 100%;">
                                    <thead>
                                        <tr>
                                            <th style="white-space: nowrap;">Danh mục</th>
                                            <th>Sản phẩm</th>
                                            <th>Đơn vị</th>
                                            <th style="white-space: nowrap;">Số lượng</th>
                                            <th style="white-space: nowrap;">Giá nhập</th>
                                            <th></th>
                                        </tr>
                                    </thead>
                                    <tbody id="productTableBody">
                                        <!-- Row mẫu -->
                                    </tbody>
                                </table>

                                <!--                                <button type="button" id="addRow" class="btn btn-success btn-sm">
                                                                    <i class="fas fa-plus"></i> Thêm sản phẩm
                                                                </button>-->
                            </div>

                            <!-- Nút điều khiển -->
                            <div class="btn-group mt-3">
                                <a href="${pageContext.request.contextPath}/staff/stockin" class="btn btn-secondary">Quay lại</a>
                                <button type="submit" class="btn btn-primary">Lưu phiếu nhập</button>
                                <button type="button" class="btn btn-success" id="exportExcelBtn">
                                    <i class="fas fa-file-excel"></i> Xuất Excel
                                </button>
                            </div>
                        </form>
                        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

                        <c:if test="${param.success == '1'}">
                            <script>
                                Swal.fire({
                                    icon: 'success',
                                    title: 'Thành công!',
                                    text: 'Nhập kho thành công!',
                                    timer: 1000, // tự tắt sau 2 giây
                                    showConfirmButton: false
                                });
                            </script>
                        </c:if>

                    </div>
                </div>
                <div class="container d-flex justify-content-center mb-5">
                    <div class="card w-100" style="max-width: 900px; background-color: #ffffff; padding: 20px;">
                        <h1>Danh sách Phiếu Nhập Kho</h1>
                        <table class="table table-bordered table-striped" style="width:100%; border-collapse:collapse;">
                            <thead>
                                <tr style="text-align:center; vertical-align:middle; background-color:#e9ecef;">
                                    <th style="white-space: nowrap; padding:0.5rem;">Mã nhập</th>
                                    <th style="padding:0.5rem; text-align: center;">Nhà cung cấp</th>
                                    <th style="white-space: nowrap; padding:0.5rem;">Người xử lý</th>
                                    <th style="padding:0.5rem; white-space: nowrap;">Ngày</th>
                                    <th style="white-space: nowrap; padding:0.5rem;">Trạng thái</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="stock" items="${stockIns}">
                                    <tr data-bs-toggle="collapse" data-bs-target="#details${stock.stockInID}" 
                                        style="cursor:pointer; text-align:center; vertical-align:middle; transition: background-color 0.3s;"
                                        onmouseover="this.style.backgroundColor = '#f2f7ff';" 
                                        onmouseout="this.style.backgroundColor = '';">
                                        <td style="padding:0.5rem;">${stock.stockInID}</td>
                                        <td style="padding:0.5rem;">${stock.manufacturerName}</td>
                                        <td style="padding:0.5rem;">${stock.receiverName}</td>
                                        <td style="white-space: nowrap; padding:0.5rem;">${stock.dateIn}</td>
                                        <td style="padding:0.5rem; font-weight:bold;
                                            color:${stock.status == 'Pending' ? '#e6b800' : (stock.status == 'Completed' ? '#28a745' : '#dc3545')}">
                                            ${stock.status}
                                        </td>
                                    </tr>
                                    <tr class="collapse" id="details${stock.stockInID}">
                                        <td colspan="5" style="padding:0.5rem; background-color:#f9f9f9;">
                                            <table class="table table-sm table-bordered mb-0" style="width:100%; border-collapse:collapse; font-size:0.9rem;">
                                                <thead>
                                                    <tr style="text-align:center; vertical-align:middle; background-color:#dee2e6;">
                                                        <th style="padding:0.35rem; text-align: center;">Mã</th>
                                                        <th style="padding:0.35rem; text-align: center;">Sản phẩm</th>
                                                        <th style="padding:0.35rem; text-align: center;">Số lượng</th>
                                                        <th style="padding:0.35rem; text-align: center;">Giá nhập</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <c:forEach var="detail" items="${stock.details}">
                                                        <tr style="text-align:center; vertical-align:middle;">
                                                            <td style="padding:0.35rem;">${detail.inventoryID}</td>
                                                            <td style="padding:0.35rem;">${detail.productName}</td>
                                                            <!-- Quantity: chỉ hiển thị số nguyên -->
                                                            <td style="padding:0.35rem;">
                                                                <fmt:formatNumber value="${detail.quantity}" type="number" maxFractionDigits="0"/>
                                                            </td>
                                                            <!-- Unit Price: có dấu phẩy ngăn cách nghìn -->
                                                            <td style="padding:0.35rem;">
                                                                <fmt:formatNumber value="${detail.unitPrice}" type="number" groupingUsed="true"/>
                                                            </td>
                                                        </tr>
                                                    </c:forEach>
                                                </tbody>
                                            </table>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <!-- JS -->
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
            <script>
                                            // Validation Bootstrap
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

                                            // Hàm tạo unit cho row
                                            function refreshUnitOptions(row) {
                                                const productSelect = row.querySelector('.product-select');
                                                const unitSelect = row.querySelector('.unit-select');
                                                const selectedOption = productSelect.selectedOptions[0];
                                                unitSelect.innerHTML = '';
                                                if (!selectedOption)
                                                    return;
                                                const unit = selectedOption.dataset.unit || '';
                                                const opt = document.createElement('option');
                                                opt.value = unit;
                                                opt.textContent = unit;
                                                unitSelect.appendChild(opt);
                                                row.querySelector('.unit-selected-name').value = unit;
                                                const qty = parseFloat(row.querySelector('.quantity-input').value || '0');
                                                row.querySelector('.converted-quantity').value = isNaN(qty) ? '' : qty;
                                            }

                                            // Event: change sản phẩm => refresh unit
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

                                            // Xóa dòng
                                            productTableBody.addEventListener('click', function (e) {
                                                if (e.target.closest('.removeRow')) {
                                                    const row = e.target.closest('tr');
                                                    row.remove();
                                                }
                                            });

                                            // Thêm dòng
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
                                            });
            </script>
            <!--        search-->
            <script>
                document.addEventListener("DOMContentLoaded", function () {
                    const searchInput = document.getElementById("productSearch");
                    const searchResults = document.getElementById("searchResults");
                    const tableBody = document.getElementById("productTableBody");

                    searchInput.addEventListener("keypress", function (e) {
                        if (e.key === "Enter") {
                            e.preventDefault(); // chặn submit form
                            // nếu muốn, bạn có thể trigger search tự động
                            const keyword = searchInput.value.trim();
                            if (keyword) {
                                searchInput.dispatchEvent(new Event("input")); // dùng lại logic input
                            }
                        }
                    });

                    // Khi gõ hoặc nhấn Enter
                    searchInput.addEventListener("input", function () {
                        const keyword = searchInput.value.trim();
                        if (!keyword) {
                            searchResults.style.display = "none";
                            return;
                        }

                        fetch(`stockin?action=search&keyword=\${encodeURIComponent(keyword)}`)
                                .then(res => res.json())
                                .then(data => {
                                    searchResults.innerHTML = "";
                                    if (data.length === 0) {
                                        searchResults.style.display = "none";
                                        return;
                                    }
                                    data.forEach(item => {
                                        const option = document.createElement("button");
                                        option.type = "button";
                                        option.className = "list-group-item list-group-item-action";
                                        option.textContent = `\${item.productName} - Tồn kho: \${item.stockQuantity}`;
                                        option.dataset.product = JSON.stringify(item); // lưu info
                                        searchResults.appendChild(option);
                                    });
                                    searchResults.style.display = "block";
                                })
                                .catch(err => console.error(err));
                    });

                    // Khi click chọn sản phẩm
                    searchResults.addEventListener("click", function (e) {
                        if (e.target.tagName === "BUTTON") {
                            const item = JSON.parse(e.target.dataset.product);
                            addProductToTable(item);
                            searchInput.value = "";
                            searchResults.style.display = "none";
                        }
                    });

                    function addProductToTable(item) {
                        // Kiểm tra xem sản phẩm đã có trong bảng chưa
                        const existingRow = Array.from(tableBody.querySelectorAll('tr.product-row')).find(row => {
                            const productSelect = row.querySelector('.product-select');
                            return productSelect && productSelect.value == item.productID;
                        });

                        if (existingRow) {
                            // Nếu đã có, có thể cộng thêm số lượng hoặc chỉ báo alert
                            const qtyInput = existingRow.querySelector('.quantity-input');
                            qtyInput.value = parseInt(qtyInput.value || '0') + 1; // tăng 1 làm ví dụ
                            qtyInput.focus();
                            return; // không thêm row mới
                        }

                        // Nếu chưa có, thêm row mới
                        const row = document.createElement("tr");
                        row.classList.add("product-row");
                        row.innerHTML = `
                            <td>
                                <select class="form-control category-select">
                                    <option value="\${item.category.categoryID}" selected>\${item.category.categoryName}</option>
                                </select>
                            </td>
                            <td>
                                <select name="productId" class="form-control product-select" required>
                                    <option value="\${item.productID}" selected>\${item.productName}</option>
                                </select>
                            </td>
                            <td>
                                <select name="unit" class="form-control unit-select" required
                                        style="white-space: nowrap; width: auto; min-width: 80px;">
                                    <option value="\${item.boxUnitName}" selected>\${item.boxUnitName}</option>
                                </select>
                                <input type="hidden" name="unitSelectedName" value="\${item.boxUnitName}" class="unit-selected-name">
                                <input type="hidden" name="packageType" value="\${item.boxUnitName}">
                                <input type="hidden" name="packSize" value="\${item.unitPerBox}">
                            </td>
                            <td><input type="number" name="quantity" class="form-control quantity-input" min="1" required 
                                style="white-space: nowrap; width:80px;"></td>
                            <td><input type="number" name="price" step="0.01" min="1" class="form-control" required style="white-space: nowrap; width:120px;"></td>
                            <td><button type="button" class="btn btn-danger btn-sm removeRow"><i class="fas fa-trash"></i></button></td>
                        `;
                        tableBody.appendChild(row);
                    }

                    // Ẩn list khi click ra ngoài
                    document.addEventListener("click", function (e) {
                        if (!searchResults.contains(e.target) && e.target !== searchInput) {
                            searchResults.style.display = "none";
                        }
                    });
                });
            </script>
            <!--        export excel-->
            <script src="https://cdnjs.cloudflare.com/ajax/libs/exceljs/4.3.0/exceljs.min.js"></script>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/FileSaver.js/2.0.5/FileSaver.min.js"></script>
            <script>
                document.getElementById("exportExcelBtn").addEventListener("click", async function () {
                    const form = document.querySelector("#stockInForm form");
                    const supplierSelect = form.querySelector("#supplierId");
                    const receiverSelect = form.querySelector("#receiverId");
                    const dateInput = form.querySelector("#date");
                    const supplier = form.querySelector("#supplierId").selectedOptions[0]?.text || '';
                    const receiver = form.querySelector("#receiverId").selectedOptions[0]?.text || '';
                    const date = form.querySelector("#date").value || '';
                    // Check supplier
                    if (!supplierSelect.value) {
                        alert("Vui lòng chọn Nhà cung cấp!");
                        supplierSelect.focus();
                        return;
                    }

// Check receiver
                    if (!receiverSelect.value) {
                        alert("Vui lòng chọn Người nhận!");
                        receiverSelect.focus();
                        return;
                    }

                    if (!date) {
                        alert("Vui lòng nhập Ngày nhập!");
                        dateInput.focus();
                        return;
                    }

                    const rows = Array.from(document.querySelectorAll("#productTableBody tr.product-row"));
                    if (rows.length === 0) {
                        alert("Vui lòng thêm ít nhất 1 sản phẩm!");
                        return;
                    }
                    const note = form.querySelector("#note").value || '';

                    // Validate sản phẩm
                    const products = [];
                    for (let i = 0; i < rows.length; i++) {
                        const row = rows[i];
                        const category = row.querySelector(".category-select").value;
                        const product = row.querySelector(".product-select").value;
                        const unit = row.querySelector(".unit-select").value;
                        const quantity = parseInt(row.querySelector(".quantity-input").value || '0');
                        const price = parseFloat(row.querySelector("input[name='price']").value || '0');

                        if (!category || !product || !unit) {
                            alert(`Dòng ${i + 1}: Vui lòng chọn đầy đủ thông tin sản phẩm!`);
                            return;
                        }
                        if (quantity <= 0) {
                            alert(`Dòng ${i + 1}: Số lượng phải lớn hơn 0!`);
                            return;
                        }
                        if (price <= 0) {
                            alert(`Dòng ${i + 1}: Giá nhập phải lớn hơn 0!`);
                            return;
                        }

                        products.push({Category: category, Product: product, Unit: unit, Quantity: quantity, Price: price});
                    }

                    const wb = new ExcelJS.Workbook();
                    const ws = wb.addWorksheet('Phiếu Nhập');

                    // Style info
                    const infoStyle = {
                        font: {bold: true},
                        fill: {type: 'pattern', pattern: 'solid', fgColor: {argb: 'FFDDEBF7'}},
                        border: {top: {style: 'thin'}, left: {style: 'thin'}, bottom: {style: 'thin'}, right: {style: 'thin'}},
                        alignment: {horizontal: 'left', vertical: 'middle'}
                    };

                    // Thông tin cơ bản (2 cột)
                    const infoRows = [
                        ['Nhà cung cấp', supplier],
                        ['Người nhận', receiver],
                        ['Ngày nhập', date],
                        ['Ghi chú', note]
                    ];
                    infoRows.forEach(r => {
                        const row = ws.addRow(r);
                        row.height = 20;
                        row.eachCell((cell, colNumber) => {
                            Object.assign(cell, infoStyle);
                            cell.alignment = {horizontal: colNumber === 1 ? 'right' : 'left', vertical: 'middle'};
                        });
                    });

                    // Khoảng trống
                    ws.addRow([]);

                    // Header sản phẩm
                    const headerRow = ws.addRow(['Danh mục', 'Sản phẩm', 'Đơn vị', 'Số lượng', 'Giá nhập']);
                    headerRow.height = 25;
                    headerRow.eachCell(cell => {
                        cell.font = {bold: true, color: {argb: 'FFFFFFFF'}};
                        cell.fill = {type: 'pattern', pattern: 'solid', fgColor: {argb: 'FF4472C4'}};
                        cell.alignment = {horizontal: 'center', vertical: 'middle'};
                        cell.border = {top: {style: 'thin'}, left: {style: 'thin'}, bottom: {style: 'thin'}, right: {style: 'thin'}};
                    });

                    // Dữ liệu sản phẩm
                    products.forEach((p, i) => {
                        const row = ws.addRow([p.Category, p.Product, p.Unit, p.Quantity, p.Price]);
                        row.height = 20;
                        row.eachCell((cell, colNumber) => {
                            cell.border = {top: {style: 'thin'}, left: {style: 'thin'}, bottom: {style: 'thin'}, right: {style: 'thin'}};
                            // màu xen kẽ
                            if (i % 2 === 0)
                                cell.fill = {type: 'pattern', pattern: 'solid', fgColor: {argb: 'FFF2F2F2'}};
                            cell.alignment = {horizontal: (colNumber >= 4 ? 'center' : 'left'), vertical: 'middle'};
                            if (colNumber === 4)
                                cell.numFmt = '0'; // Số lượng
                            if (colNumber === 5)
                                cell.numFmt = '#,##0'; // Giá nhập
                        });
                    });

                    // Tự động điều chỉnh độ rộng cột
                    ws.columns.forEach(c => c.width = 20);

                    // Xuất file
                    const buffer = await wb.xlsx.writeBuffer();
                    const blob = new Blob([buffer], {type: 'application/octet-stream'});
                    saveAs(blob, `PhieuNhap_${Date.now()}.xlsx`);
                });
            </script>

            <script>
                // Nếu URL có ?success=1 thì xóa nó
                if (window.location.search.includes('success=1')) {
                    const url = window.location.href.split('?')[0]; // chỉ lấy phần URL trước ?
                    window.history.replaceState({}, document.title, url);
                }

                // Tương tự nếu dùng ?error=1
                if (window.location.search.includes('error=1')) {
                    const url = window.location.href.split('?')[0];
                    window.history.replaceState({}, document.title, url);
                }
            </script>
    </body>
</html>
