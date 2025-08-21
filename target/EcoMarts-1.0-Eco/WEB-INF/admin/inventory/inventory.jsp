<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Quản lý nhập kho</title>
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/img/eco.png" type="image/x-icon">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis()%>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis()%>">
    </head>
    <body>
        <div class="container-fluid">
            <%-- Sidebar --%>
            <jsp:include page="../components/sidebar.jsp" />

            <div class="main-content">
                <div class="card">
                    <div class="card-header">
                        <div class="header-actions d-flex justify-content-between align-items-center">
                            <h1 class="card-title">Danh sách nhập kho</h1>
                            <div class="d-flex gap-2">
                                <form action="${pageContext.request.contextPath}/admin/inventory" method="get" class="d-flex gap-2">
                                    <select name="supplierId" class="form-select form-select-sm" style="width:650px;" onchange="this.form.submit()">
                                        <option value="">-- Tất cả nhà cung cấp --</option>
                                        <c:forEach var="sup" items="${suppliers}">
                                            <option value="${sup.supplierID}" <c:if test="${sup.supplierID == supplierId}">selected</c:if>>
                                                ${sup.companyName}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </form>

                            </div>
                        </div>
                    </div>

                    <div class="table-container">
                        <table class="table table-striped table-hover text-center align-middle">
                            <thead>
                                <tr>
                                    <th style="text-align: center">Mã nhập</th>
                                    <th style="text-align: center">Nhà cung cấp</th>
                                    <th style="text-align: center">Người xử lý</th>
                                    <th style="text-align: center">Ngày</th>
                                    <th style="text-align: center">Trạng thái</th>
                                    <th style="text-align: center">Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="stock" items="${stockIns}">
                                    <!-- Xóa data-bs-toggle và data-bs-target -->
                                    <tr style="text-align:center; vertical-align:middle;">
                                        <td>${stock.stockInID}</td>
                                        <td>${stock.supplierName}</td>
                                        <td>${stock.receiverName}</td>
                                        <td>${stock.dateIn}</td>
                                        <td style="font-weight:bold;
                                            color:${stock.status == 'Pending' ? '#e6b800' : (stock.status == 'Completed' ? '#28a745' : '#dc3545')}">
                                            ${stock.status}
                                        </td>
                                        <td>
                                            <div class="d-flex gap-2 justify-content-center">
                                                <a href="${pageContext.request.contextPath}/admin/inventory?service=detail&id=${stock.stockInID}" class="btn btn-sm btn-info">
                                                    <i class="fas fa-eye"></i>
                                                </a>
                                                <!-- chỉ hiển thị 2 nút khi trạng thái Pending -->
                                                <c:if test="${stock.status == 'Pending'}">
                                                    <a href="${pageContext.request.contextPath}/admin/inventory?service=approve&id=${stock.stockInID}" 
                                                       class="btn btn-sm btn-success">
                                                        <i class="fas fa-check"></i>
                                                    </a>

                                                    <a href="${pageContext.request.contextPath}/admin/inventory?service=reject&id=${stock.stockInID}" 
                                                       class="btn btn-sm btn-danger">
                                                        <i class="fas fa-times"></i>
                                                    </a>
                                                </c:if>
                                            </div>
                                        </td>
                                    </tr>                                  
                                </c:forEach>
                            </tbody>
                        </table>                       
                    </div>
                </div>
            </div>
        </div>
        <!-- 1: SweetAlert Library -->
        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

        <!-- 2: Check message and show alert -->
        <c:if test="${param.message eq 'approved'}">
            <script>
        Swal.fire({
            icon: 'success',
            title: 'Approve thành công',
            showConfirmButton: false,
            timer: 1000
        });
            </script>
        </c:if>

        <c:if test="${param.message eq 'rejected'}">
            <script>
                Swal.fire({
                    icon: 'warning',
                    title: 'Phiếu nhập đã bị hủy!',
                    showConfirmButton: false,
                    timer: 1000
                });
            </script>
        </c:if>

        <!-- 3: Bootstrap JS -->
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
