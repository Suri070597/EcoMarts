<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="model.Category" %>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/img/eco.png" type="image/x-icon">
    <title>Thêm danh mục con</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis()%>">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis()%>">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/category.css?version=<%= System.currentTimeMillis()%>">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <div class="container-fluid">
        <%-- Include admin sidebar --%>
        <jsp:include page="../components/sidebar.jsp" />
        <div class="main-content">
            <div class="category-container">
                <div class="category-header">
                    <h1><i class="fas fa-plus-circle"></i> Thêm danh mục con</h1>
                </div>
                <form action="createCategory" method="post" onsubmit="return validateForm(event)">
                    <div class="mb-3">
                        <label for="parentID" class="form-label">Chọn danh mục cha:</label>
                        <select name="parentID" id="parentID" class="form-control" required>
                            <c:forEach var="cat" items="${parents}">
                                <option value="${cat.categoryID}" ${selectedParentID == cat.categoryID ? 'selected' : ''}>${cat.categoryName}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label for="categoryName" class="form-label">Tên danh mục con:</label>
                        <input type="text" name="categoryName" id="categoryName" class="form-control" value="${categoryName != null ? categoryName : ''}" required>
                        <c:if test="${not empty errorMessage}">
                            <div class="alert alert-danger" style="margin-top:8px;">${errorMessage}</div>
                        </c:if>
                    </div>
                    <div class="btn-group">
                        <a href="${pageContext.request.contextPath}/admin/category" class="btn btn-secondary"><i class="fas fa-arrow-left"></i> Quay lại</a>
                        <button type="submit" class="btn btn-primary"><i class="fas fa-save"></i> Thêm</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
    <script>
        function validateForm(e) {
            const name = document.getElementById('categoryName').value.trim();
            // Regex: không bắt đầu bằng số, chỉ cho chữ cái (mọi ngôn ngữ), số và khoảng trắng
            const re = /^(?!\d)[\p{L}\p{M}\d ]+$/u;
            if (!re.test(name)) {
                Swal.fire({
                    title: 'Dữ liệu không hợp lệ',
                    text: 'Tên danh mục không chứa kí tự đặc biệt và không bắt đầu bằng số.',
                    icon: 'error'
                });
                e.preventDefault();
                return false;
            }
            return true;
        }
    </script>
</body>
</html>
