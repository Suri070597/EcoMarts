<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="model.Category" %>

<jsp:include page="../components/sidebar.jsp" />
<link rel="stylesheet"
      href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis()%>">
<link rel="stylesheet"
      href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis()%>">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<style>
    /* Container chính */
    .container {
        max-width: 600px;
        margin: 40px auto;
        background: #fff;
        padding: 30px 40px;
        border-radius: 16px;
        box-shadow: 0 0 15px rgba(0, 0, 0, 0.1);
        font-family: 'Segoe UI', sans-serif;
    }

    /* Tiêu đề */
    h1 {
        text-align: center;
        margin-bottom: 30px;
        color: #2c3e50;
    }

    /* Label */
    label {
        display: block;
        margin-bottom: 8px;
        font-weight: 600;
        color: #34495e;
    }

    /* Input & Select */
    input[type="text"],
    select {
        width: 100%;
        padding: 10px 12px;
        margin-bottom: 20px;
        border: 1px solid #ccc;
        border-radius: 8px;
        transition: border-color 0.3s;
        font-size: 16px;
    }

    input[type="text"]:focus,
    select:focus {
        border-color: #3498db;
        outline: none;
    }

    /* Button */
    button[type="submit"] {
        background-color: #3498db;
        color: white;
        padding: 10px 20px;
        border: none;
        border-radius: 8px;
        cursor: pointer;
        font-size: 16px;
        transition: background-color 0.3s;
        margin-right: 10px;
    }

    button[type="submit"]:hover {
        background-color: #2980b9;
    }

    /* Link quay lại */
    a {
        text-decoration: none;
        color: #7f8c8d;
        font-size: 16px;
        transition: color 0.3s;
    }

    a:hover {
        color: #2c3e50;
    }

</style>
<body>
    <div class="container-fluid">
        <%-- Include admin sidebar --%>
        <jsp:include page="../components/sidebar.jsp" />

        <div class="main-content">
            <div class="container">
                <h1>Thêm danh mục con</h1>

                <form action="createCategory" method="post">
                    <label for="parentID">Chọn mục cha:</label>
                    <select name="parentID" id="parentID" required>
                        <c:forEach var="cat" items="${parents}">
                            <option value="${cat.categoryID}">${cat.categoryName}</option>
                        </c:forEach>
                    </select>

                    <br><br>

                    <label for="categoryName">Tên mục con:</label>
                    <input type="text" name="categoryName" id="categoryName" required>

                    <br><br>

                    <button type="submit">Thêm</button>
                    <a href="${pageContext.request.contextPath}/admin/category">Quay lại</a>

                </form>
            </div>
