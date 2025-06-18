<%@page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="model.Category" %>

<jsp:include page="../components/sidebar.jsp" />

<div class="main-content">
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
