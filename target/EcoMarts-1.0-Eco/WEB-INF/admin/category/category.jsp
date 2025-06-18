<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.*" %>
<%@ page import="model.Category" %>

<jsp:include page="../components/sidebar.jsp" />

<%
    List<Category> parentCategories = (List<Category>) request.getAttribute("parents");
    Map<Integer, List<Category>> childMap = (Map<Integer, List<Category>>) request.getAttribute("childMap");
%>

<link rel="stylesheet"
      href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis()%>">
<link rel="stylesheet"
      href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis()%>">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<style>
    .category-tree {
        list-style: none;
        padding-left: 0;
        font-family: 'Segoe UI', sans-serif;
    }

    .category-tree li.parent {
        font-weight: bold;
        position: relative;
        padding: 10px 12px;
        background-color: #f9f9f9;
        border: 1px solid #ddd;
        border-radius: 8px;
        margin-bottom: 8px;
        transition: background-color 0.2s;
    }

    .category-tree li.parent:hover {
        background-color: #eef6ff;
    }

    .category-tree li.parent::before {
        content: "+";
        font-weight: bold;
        color: #3498db;
        position: absolute;
        left: -20px;
        top: 50%;
        transform: translateY(-50%);
    }

    .category-tree li.parent.expanded::before {
        content: "−";
    }

    .category-tree ul.children {
        margin-top: 10px;
        padding-left: 20px;
        border-left: 2px dashed #ccc;
    }

    .category-tree ul.children li {
        margin: 6px 0;
        padding: 6px 10px;
        background-color: #fff;
        border-radius: 6px;
        border: 1px solid #eee;
        display: flex;
        justify-content: space-between;
        align-items: center;
    }

    .category-tree ul.children li:hover {
        background-color: #f2f9ff;
    }

    .category-tree button {
        background-color: #e74c3c;
        color: white;
        border: none;
        padding: 4px 10px;
        border-radius: 6px;
        font-size: 13px;
        cursor: pointer;
        transition: background-color 0.3s;
    }

    .category-tree button:hover {
        background-color: #c0392b;
    }

</style>
<body>
    <div class="container-fluid">
        <%-- Include admin sidebar --%>
        <jsp:include page="../components/sidebar.jsp" />

        <div class="main-content">
            <div class="container">
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
                .hidden {
                    display: none;
                }
                .parent {
                    cursor: pointer;
                    margin-bottom: 10px;
                }
                button {
                    margin-left: 10px;
                }
            </style>

            <script>
                function toggleChildren(element) {
                    const children = element.querySelector(".children");
                    if (children) {
                        children.classList.toggle("hidden");
                        element.classList.toggle("expanded");
                    }
                }


                function confirmDelete(id) {
                    event.stopPropagation(); // Không làm sập cây
                    if (confirm("Bạn có chắc muốn xóa danh mục này?")) {
                        window.location.href = "deleteCategory?id=" + id;
                    }
                }
            </script>
