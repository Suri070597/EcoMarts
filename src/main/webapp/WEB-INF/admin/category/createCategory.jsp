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
                    <h1><i class="fas fa-plus-circle"></i>Add Subcategory</h1>
                </div>
                <form action="createCategory" method="post">
                    <div class="mb-3">
                        <label for="parentID" class="form-label">Select parent category:</label>
                        <select name="parentID" id="parentID" class="form-control" required>
                            <c:forEach var="cat" items="${parents}">
                                <option value="${cat.categoryID}">${cat.categoryName}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label for="categoryName" class="form-label">Subcategory name:</label>
                        <input type="text" name="categoryName" id="categoryName" class="form-control" required>
                    </div>
                    <div class="btn-group">
                        <a href="${pageContext.request.contextPath}/admin/category" class="btn btn-secondary"><i class="fas fa-arrow-left"></i> Back</a>
                        <button type="submit" class="btn btn-primary"><i class="fas fa-save"></i> Add</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</body>
</html>
