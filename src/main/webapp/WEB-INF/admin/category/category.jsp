<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.*" %>
<%@ page import="model.Category" %>

<jsp:include page="../components/sidebar.jsp" />

<%
    List<Category> parentCategories = (List<Category>) request.getAttribute("parents");
    Map<Integer, List<Category>> childMap = (Map<Integer, List<Category>>) request.getAttribute("childMap");
%>

<div class="main-content">
    <h1>Quản lý danh mục</h1>
   <a href="${pageContext.request.contextPath}/admin/createCategory" class="btn btn-primary">+ Thêm category</a>

    <ul class="category-tree">
        <c:forEach var="parent" items="${parents}">
            <li class="parent" onclick="toggleChildren(this)">
                <strong>${parent.categoryName}</strong>
                <ul class="children hidden">
                    <c:forEach var="child" items="${childMap[parent.categoryID]}">
                        <li>
                            ${child.categoryName}
                            <button onclick="confirmDelete(${child.categoryID})">Xóa</button>
                        </li>
                    </c:forEach>
                </ul>
            </li>
        </c:forEach>
    </ul>
</div>

<style>
    .category-tree ul.children {
        margin-left: 20px;
        padding-left: 10px;
        border-left: 2px solid #ccc;
    }
    .hidden { display: none; }
    .parent { cursor: pointer; margin-bottom: 10px; }
    button { margin-left: 10px; }
</style>

<script>
    function toggleChildren(element) {
        const children = element.querySelector(".children");
        if (children) {
            children.classList.toggle("hidden");
        }
    }

    function confirmDelete(id) {
        event.stopPropagation(); // Không làm sập cây
        if (confirm("Bạn có chắc muốn xóa danh mục này?")) {
            window.location.href = "deleteCategory?id=" + id;
        }
    }
</script>
