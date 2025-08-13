<%@page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
    request.setAttribute("activeMenu", "profile");
    String flash = (String) session.getAttribute("flashMessage");
    if (flash != null) {
        request.setAttribute("message", flash);
        session.removeAttribute("flashMessage");
    }
%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Thông tin nhân viên</title>
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/img/eco.png" type="image/x-icon">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis()%>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis()%>">
        <style>
            .card-beige{
                border-radius:15px
            }
            .btn-beige{
                background:#dbc09a;
                color:#111;
                border:none
            }
            .btn-beige:hover{
                background:#bfa270;
                color:#111
            }
            .btn-outline-beige{
                border:1px solid #dbc09a;
                color:#111;
                background:#fff
            }
            .btn-outline-beige:hover{
                background:#f4e6d0;
                color:#111
            }
            .main-content{
                padding:24px
            }
            .card-header{
                border-bottom:1px solid #eee
            }
            .card-body{
                padding-left:1.25rem;
                padding-right:1.25rem
            }
            .row.mb-3,.row.mb-4{
                margin-bottom:.75rem!important
            }
            .col-form-label{
                font-weight:600;
                color:#5a4634
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
                            <i class="fa-solid fa-id-card me-2"></i> Thông tin nhân viên
                        </h1>
                        <div class="d-flex gap-2">
                            <a href="${pageContext.request.contextPath}/ManageStaffServlet?action=edit" class="btn btn-beige">
                                <i class="fa-solid fa-pen-to-square me-1"></i> Edit
                            </a>
                            <a href="${pageContext.request.contextPath}/staff" class="btn btn-outline-beige">
                                <i class="fa-solid fa-arrow-left-long me-1"></i> Quay lại
                            </a>
                        </div>
                    </div>

                    <div class="card-body">
                        <form>
                            <input type="hidden" name="staffId" value="${staff.staffID}"/>

                            <div class="row mb-3 align-items-center">
                                <label class="col-sm-2 col-form-label">Họ tên:</label>
                                <div class="col-sm-10">
                                    <input type="text" class="form-control" value="${staff.fullName}" readonly>
                                </div>
                            </div>

                            <div class="row mb-3 align-items-center">
                                <label class="col-sm-2 col-form-label">SĐT:</label>
                                <div class="col-sm-10">
                                    <input type="text" class="form-control" value="${staff.phone}" readonly>
                                </div>
                            </div>

                            <div class="row mb-3 align-items-center">
                                <label class="col-sm-2 col-form-label">Địa chỉ:</label>
                                <div class="col-sm-10">
                                    <input type="text" class="form-control" value="${staff.address}" readonly>
                                </div>
                            </div>

                            <div class="row mb-3 align-items-center">
                                <label class="col-sm-2 col-form-label">Giới tính:</label>
                                <div class="col-sm-10">
                                    <input type="text" class="form-control" value="${staff.gender}" readonly>
                                </div>
                            </div>

                            <div class="row mb-4 align-items-center">
                                <label class="col-sm-2 col-form-label">Email:</label>
                                <div class="col-sm-10">
                                    <input type="email" class="form-control" value="${staff.email}" readonly>
                                </div>
                            </div>
                        </form>

                        <c:if test="${not empty message}">
                            <p class="text-success fw-semibold mt-3">${message}</p>
                        </c:if>
                    </div>
                </div>
            </div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
