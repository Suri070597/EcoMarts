<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
    <head>
        <title>Gán sản phẩm vào khuyến mãi</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis()%>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis()%>">
        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    </head>
    <body>
        <div class="container-fluid">
            <jsp:include page="../components/sidebar.jsp"/>

            <div class="main-content">
                <div class="card mt-4">
                    <div class="card-header d-flex justify-content-between align-items-center flex-wrap gap-3">
                        <h4 class="mb-0">Gán sản phẩm vào khuyến mãi: <strong>${promotion.promotionName}</strong></h4>
                        <a href="${pageContext.request.contextPath}/admin/promotion" class="btn btn-secondary btn-sm">← Quay lại</a>
                    </div>

                    <div class="card-body">
                        <!-- Thanh tìm kiếm -->
                        <div class="mb-3 d-flex justify-content-between flex-wrap align-items-center">
                            <input type="text" id="searchInput" class="form-control w-100 w-md-50" placeholder="Tìm theo tên, ID, giá...">
                        </div>

                        <form id="assignForm" method="post" action="${pageContext.request.contextPath}/admin/promotion">
                            <input type="hidden" name="action" value="assign-products"/>
                            <input type="hidden" name="promotionID" value="${promotion.promotionID}"/>

                            <div class="table-responsive">
                                <table class="table table-bordered table-hover align-middle" id="productTable">
                                    <thead class="table-light">
                                        <tr>
                                            <th>Chọn</th>
                                            <th>ID Sản phẩm</th>
                                            <th>Tên sản phẩm</th>
                                            <th>Giá</th>
                                            <th>% Giảm giá</th>
                                            <th>Giá sau giảm</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="product" items="${allProducts}">
                                            <tr>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${productPromotionMap[product.productID] != null}">
                                                            <input type="checkbox" disabled />
                                                            <span class="text-danger">(Đã thuộc: ${productPromotionMap[product.productID]})</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <input type="checkbox" name="productIDs" value="${product.productID}"
                                                                   <c:if test="${assignedIDs.contains(product.productID)}">checked</c:if> />
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>${product.productID}</td>
                                                <td>${product.productName}</td>
                                                <td><fmt:formatNumber value="${product.price}" type="number" pattern="#,###"/> đ</td>
                                                <td><fmt:formatNumber value="${promotion.discountPercent}" type="number"/>%</td>
                                                <td><fmt:formatNumber value="${product.price * (1 - promotion.discountPercent / 100)}" type="number" pattern="#,###"/> đ</td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>

                            <div class="mt-3 d-flex gap-2">
                                <button type="button" class="btn btn-primary" onclick="confirmAssign()">Lưu thay đổi</button>
                                <a href="${pageContext.request.contextPath}/admin/promotion" class="btn btn-secondary">Hủy</a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <!-- Script: SweetAlert & Bootstrap -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

        <script>
                                    // Tìm kiếm theo nội dung dòng
                                    document.getElementById("searchInput").addEventListener("input", function () {
                                        const filter = this.value.toLowerCase();
                                        const rows = document.querySelectorAll("#productTable tbody tr");

                                        rows.forEach(row => {
                                            const text = row.textContent.toLowerCase();
                                            row.style.display = text.includes(filter) ? "" : "none";
                                        });
                                    });

                                    // Xác nhận lưu thay đổi
                                    function confirmAssign() {
                                        Swal.fire({
                                            title: 'Xác nhận gán sản phẩm',
                                            text: 'Bạn có chắc chắn muốn lưu các thay đổi không?',
                                            icon: 'question',
                                            showCancelButton: true,
                                            confirmButtonText: 'Đồng ý',
                                            cancelButtonText: 'Hủy',
                                            confirmButtonColor: '#3085d6',
                                            cancelButtonColor: '#d33'
                                        }).then((result) => {
                                            if (result.isConfirmed) {
                                                document.getElementById('assignForm').submit();
                                            }
                                        });
                                    }
        </script>
    </body>
</html>
