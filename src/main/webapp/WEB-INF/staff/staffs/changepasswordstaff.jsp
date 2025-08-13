<%-- 
    Document   : changepasswordstaff
    Created on : Jul 21, 2025, 4:33:00 PM
    Author     : ADMIN
--%>

<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    request.setAttribute("activeMenu", "profile");
%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Đổi mật khẩu</title>
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/img/eco.png" type="image/x-icon">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis()%>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis()%>">
        <style>
            .card-beige{
                border-radius:15px;
            }
            .btn-beige{
                background:#dbc09a;
                color:#111;
                border:none;
            }
            .btn-beige:hover{
                background:#bfa270;
                color:#111;
            }
            .btn-outline-beige{
                border:1px solid #dbc09a;
                color:#111;
                background:#fff;
            }
            .btn-outline-beige:hover{
                background:#f4e6d0;
                color:#111;
            }
            .main-content{
                padding:24px;
            }
            .card-header{
                border-bottom:1px solid #eee;
            }
            .card-body{
                padding-left:1.25rem;
                padding-right:1.25rem;
            }
            .row.mb-3{
                margin-bottom:.75rem!important;
            }
            .col-form-label{
                font-weight:600;
                color:#5a4634;
            }
            .message{
                margin-top:12px;
            }
            .form-control{
                border-radius:10px;
            }
        </style>
    </head>
    <body>
        <div class="container-fluid">
            <jsp:include page="../components/sidebar.jsp" />

            <div class="main-content">
                <div class="card card-beige shadow-sm">
                    <div class="card-header d-flex align-items-center justify-content-between">
                        <h1 class="card-title mb-0">
                            <i class="fa-solid fa-key me-2"></i> Đổi mật khẩu
                        </h1>
                        <a href="${pageContext.request.contextPath}/verifypasswordServlet" class="btn btn-sm btn-outline-beige">
                            <i class="fa-solid fa-arrow-left-long me-1"></i> Quay lại
                        </a>
                    </div>

                    <div class="card-body" style="max-width:560px;">
                        <form action="${pageContext.request.contextPath}/changepasswordstaff" method="post">
                            <div class="row mb-3 align-items-center">
                                <label class="col-sm-4 col-form-label">Mật khẩu mới:</label>
                                <div class="col-sm-8">
                                    <input type="password" name="newPassword" class="form-control" required>
                                </div>
                            </div>

                            <div class="row mb-3 align-items-center">
                                <label class="col-sm-4 col-form-label">Xác nhận mật khẩu:</label>
                                <div class="col-sm-8">
                                    <input type="password" name="confirmPassword" class="form-control" required>
                                </div>
                            </div>

                            <!-- Hai nút tách riêng -->
                            <div class="mt-2">
                                <button type="submit" class="btn btn-beige">
                                    <i class="fa-solid fa-floppy-disk me-1"></i> Xác nhận
                                </button>
                            </div>


                            <c:if test="${not empty message}">
                                <p class="message text-danger fw-semibold">${message}</p>
                            </c:if>
                        </form>
                    </div>
                </div>
            </div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>

