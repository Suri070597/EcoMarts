<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="java.util.*" %>
<%@ page import="model.Category" %>

<%
    List<Category> parentCategories = (List<Category>) request.getAttribute("parents");
    Map<Integer, List<Category>> childMap = (Map<Integer, List<Category>>) request.getAttribute("childMap");
%>
<html>
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/img/eco.png"
              type="image/x-icon">
        <title>Manage Categories</title>
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis()%>">
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis()%>">
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/category.css?version=<%= System.currentTimeMillis()%>">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <!-- SweetAlert2 CSS -->
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/sweetalert2@11/dist/sweetalert2.min.css">
    </head>
    <body>
        <div class="container-fluid">
            <%-- Include admin sidebar --%>
            <jsp:include page="../components/sidebar.jsp" />

            <div class="main-content">
                <div class="category-container">
                    <div class="category-header">
                        <h1><i class="fas fa-sitemap"></i> Quản lý danh mục</h1>
                        <a href="${pageContext.request.contextPath}/admin/createCategory" class="btn btn-primary">
                            + Thêm danh mục
                        </a>
                    </div>

                    <ul class="category-tree">
                        <c:forEach var="parent" items="${parents}">
                            <li class="parent" onclick="toggleChildren(this)">
                                <strong>${parent.categoryName}</strong>
                                <ul class="children hidden">
                                    <c:forEach var="child" items="${childMap[parent.categoryID]}">
                                        <li>
                                            <span>${child.categoryName}</span>
                                            <button class="btn-delete" onclick="confirmDelete('${child.categoryID}', '${child.categoryName}')">
                                                <i class="fas fa-trash"></i> Xóa
                                            </button>
                                        </li>
                                    </c:forEach>
                                </ul>
                            </li>
                        </c:forEach>
                    </ul>
                </div>
            </div>
        </div>

        <!-- SweetAlert2 JS -->
        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>

        <script>
                                                function toggleChildren(element) {
                                                    const children = element.querySelector(".children");
                                                    if (children) {
                                                        children.classList.toggle("hidden");
                                                        element.classList.toggle("expanded");
                                                    }
                                                }

                                                function confirmDelete(id, categoryName) {
                                                    event.stopPropagation(); // Prevent event bubbling

                                                    Swal.fire({
                                                        title: 'Confirm Delete Subfolder',
                                                        text: 'Are you sure you want to delete this account?',
                                                        icon: 'warning',
                                                        showCancelButton: true,
                                                        confirmButtonColor: '#c35f5f',
                                                        cancelButtonColor: '#8b6c42',
                                                        confirmButtonText: 'Yes',
                                                        cancelButtonText: 'Cancel',
                                                        reverseButtons: false,
                                                        customClass: {
                                                            popup: 'swal-custom-popup',
                                                            confirmButton: 'swal-confirm-btn',
                                                            cancelButton: 'swal-cancel-btn'
                                                        }
                                                    }).then((result) => {
                                                        if (result.isConfirmed) {
                                                            // Show loading state
                                                            Swal.fire({
                                                                title: 'Deleting...',
                                                                text: 'Please wait a moment',
                                                                allowOutsideClick: false,
                                                                didOpen: () => {
                                                                    Swal.showLoading();
                                                                }
                                                            });

                                                            // Redirect to delete action
                                                            window.location.href = "deleteCategory?id=" + id;
                                                        }
                                                    });
                                                }

                                                // Check for success/error messages from server
                                                window.addEventListener('load', function () {
                                                    const urlParams = new URLSearchParams(window.location.search);
                                                    const message = urlParams.get('message');
                                                    const type = urlParams.get('type');

                                                    if (message) {
                                                        if (type === 'success') {
                                                            Swal.fire({
                                                                title: 'Thành công!',
                                                                text: message,
                                                                icon: 'success',
                                                                confirmButtonColor: '#8b6c42'
                                                            });
                                                        } else if (type === 'error') {
                                                            Swal.fire({
                                                                title: 'Lỗi!',
                                                                text: message,
                                                                icon: 'error',
                                                                confirmButtonColor: '#c35f5f'
                                                            });
                                                        }
                                                    }
                                                });
        </script>
    </body>
</html>

