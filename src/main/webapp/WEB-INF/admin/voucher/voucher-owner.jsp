<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Danh Sách Khách Hàng Sở Hữu Voucher</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/img/eco.png" type="image/x-icon">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis()%>">
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis()%>">
    </head>
    <body>
        <div class="container-fluid">
            <%-- Sidebar --%>
            <jsp:include page="../components/sidebar.jsp" />

            <div class="main-content">
                <div class="card">
                    <div class="card-header">
                        <div class="header-actions">
                            <h1 class="card-title mb-0">
                                Chủ sở hữu voucher:
                                <span class="text-primary">
                                    <strong><c:out value="${voucher.voucherCode}" /></strong>
                                </span>
                            </h1>

                            <div class="d-flex gap-3">
                                <%-- Search client-side --%>
                                <div class="search-box">
                                    <i class="fas fa-search"></i>
                                    <input id="searchInput" type="text" placeholder="Tìm theo tên, email, số điện thoại...">
                                </div>

                                <%-- Form thêm voucher theo id user --%>
                                <form action="${pageContext.request.contextPath}/admin/voucher" method="post" class="d-flex gap-2">
                                    <input type="hidden" name="action" value="assignOwnerById"/>
                                    <input type="hidden" name="id" value="${voucher.voucherID}"/>
                                    <input type="number" name="accountId" class="form-control" min="1"
                                           placeholder="Nhập ID khách hàng để thêm" required/>
                                    <button type="submit" class="btn btn-primary">
                                        <i class="fas fa-user-plus"></i> Add
                                    </button>
                                </form>

                                <a href="${pageContext.request.contextPath}/admin/voucher" class="btn btn-secondary">
                                    <i class="fas fa-arrow-left"></i> Quay lại
                                </a>
                            </div>
                        </div>

                        <div class="mt-2">
                            <span class="badge bg-secondary">
                                Tổng: <strong><c:out value="${fn:length(owners)}"/></strong> khách hàng
                            </span>
                        </div>
                    </div>

                    <div class="table-container">
                        <%-- Thông báo kết quả thao tác add --%>
                        <c:if test="${param.msg == 'assigned'}">
                            <div class="alert alert-success">Đã thêm voucher cho user theo email.</div>
                        </c:if>
                        <c:if test="${param.err == 'notfound'}">
                            <div class="alert alert-warning">Không tìm thấy user với email đã nhập.</div>
                        </c:if>
                        <c:if test="${param.err == 'duplicate'}">
                            <div class="alert alert-info">User này đã sở hữu voucher này.</div>
                        </c:if>
                        <c:if test="${param.err == 'empty'}">
                            <div class="alert alert-warning">Vui lòng nhập email.</div>
                        </c:if>
                        <c:if test="${param.err == 'unknown'}">
                            <div class="alert alert-danger">Có lỗi xảy ra. Vui lòng thử lại.</div>
                        </c:if>

                        <c:choose>
                            <c:when test="${empty owners}">
                                <div class="alert alert-info mb-0">Chưa có khách hàng nào sở hữu voucher này.</div>
                            </c:when>
                            <c:otherwise>
                                <div class="table-responsive">
                                    <table class="table table-striped table-hover align-middle">
                                        <thead>
                                            <tr>
                                                <th style="width:120px;">ID</th>
                                                <th>Tên khách hàng</th>
                                                <th>Email</th>
                                                <th style="width:180px;">Số điện thoại</th>
                                            </tr>
                                        </thead>
                                        <tbody id="ownerTbody">
                                            <c:forEach var="o" items="${owners}">
                                                <tr>
                                                    <td><c:out value="${o.accountID}"/></td>
                                                    <td><c:out value="${o.fullName}"/></td>
                                                    <td><c:out value="${o.email}"/></td>
                                                    <td><c:out value="${o.phone}"/></td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <script>
            // Lọc client-side
            (function () {
                const input = document.getElementById('searchInput');
                const rows = document.querySelectorAll('#ownerTbody tr');
                input?.addEventListener('input', function (e) {
                    const q = e.target.value.toLowerCase().trim();
                    rows.forEach(row => {
                        const text = row.textContent.toLowerCase();
                        row.style.display = q ? (text.includes(q) ? '' : 'none') : '';
                    });
                });
            })();
        </script>
    </body>
</html>
