<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Chi Tiết Phiếu Nhập #${stock.stockInID}</title>
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/img/eco.png" type="image/x-icon">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css">

    </head>
    <body>
        <div class="container-fluid">
            <%-- Sidebar --%>
            <jsp:include page="../components/sidebar.jsp" />

            <div class="main-content">
                <div class="container mt-4">
                    <div class="card">
                        <div class="card-header d-flex justify-content-between align-items-center">
                            <h2>Chi Tiết Phiếu Nhập #${stock.stockInID}</h2>
                            <a href="${pageContext.request.contextPath}/admin/inventory" class="btn btn-secondary">Quay lại</a>
                        </div>
                        <div class="card-body">
                            <!-- Thông tin phiếu nhập -->
                            <div class="row">
                                <div class="col-md-12">
                                    <table class="table table-borderless">
                                        <tr><th>Nhà cung cấp:</th><td>${stock.manufacturerName}</td></tr>
                                        <tr><th>Người xử lý:</th><td>${stock.receiverName}</td></tr>
                                        <tr><th>Ngày nhập:</th><td><fmt:formatDate value="${stock.dateIn}" pattern="dd/MM/yyyy"/></td></tr>
                                        <tr>
                                            <th>Trạng thái:</th>
                                            <td style="font-weight:bold; color:
                                                <c:choose>
                                                    <c:when test="${stock.status == 'Pending'}">#e6b800</c:when>
                                                    <c:when test="${stock.status == 'Completed'}">#28a745</c:when>
                                                    <c:otherwise>#dc3545</c:otherwise>
                                                </c:choose>">
                                                ${stock.status}
                                            </td>
                                        </tr>
                                    </table>
                                </div>
                            </div>

                            <!-- Bảng chi tiết sản phẩm -->
                            <div class="row mt-4">
                                <div class="col-12">
                                    <table class="table table-bordered table-striped">
                                        <thead class="table-light text-center">
                                            <tr>
                                                <th>Mã</th>
                                                <th>Sản phẩm</th>
                                                <th>Số lượng</th>
                                                <th>Giá nhập</th>
                                                <th>Ngày hết hạn</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="detail" items="${stock.details}">
                                                <tr class="text-center align-middle">
                                                    <td>${detail.inventoryID}</td>
                                                    <td>${detail.productName}</td>
                                                    <td><fmt:formatNumber value="${detail.quantity}" type="number" maxFractionDigits="0"/></td>
                                                    <td><fmt:formatNumber value="${detail.unitPrice}" type="number" groupingUsed="true"/></td>
                                                    <td style="padding:0.35rem;">
                                                        ${detail.expirationDate} <!-- hiển thị ngày hết hạn -->
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>

                                    <!-- Tổng số lượng và tổng tiền -->
                                    <table class="table table-borderless mt-3">
                                        <tr>
                                            <th>Tổng số lượng:</th>
                                            <td><fmt:formatNumber value="${totalQuantity}" type="number" maxFractionDigits="0"/></td>
                                        </tr>
                                        <tr>
                                            <th>Tổng tiền:</th>
                                            <td><fmt:formatNumber value="${totalPrice}" type="number" groupingUsed="true"/></td>
                                        </tr>
                                    </table>
                                    <!-- Sau bảng thông tin phiếu nhập -->
                                    <c:if test="${stock.status == 'Pending'}">
                                        <div class="text-center mt-3">
                                            <a href="${pageContext.request.contextPath}/admin/inventory?service=approve&id=${stock.stockInID}" class="btn btn-success mx-2">
                                                <i class="fas fa-check"></i> Duyệt
                                            </a>
                                            <a href="${pageContext.request.contextPath}/admin/inventory?service=reject&id=${stock.stockInID}" class="btn btn-danger mx-2">
                                                <i class="fas fa-times"></i> Hủy
                                            </a>
                                        </div>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
