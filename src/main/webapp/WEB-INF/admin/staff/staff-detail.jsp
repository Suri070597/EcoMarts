<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/img/eco.png"
        type="image/x-icon">
    <title>Staff Details</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link rel="stylesheet"
        href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis()%>">
    <link rel="stylesheet"
        href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis()%>">
</head>

<body>
    <div class="container-fluid">
        <%-- Include admin sidebar --%>
        <jsp:include page="../components/sidebar.jsp" />

        <div class="main-content">
            <div class="card">
                <div class="card-header">
                    <div class="header-actions">
                        <h1 class="card-title">Staff Details</h1>
                        <div class="d-flex gap-3">
                            <a href="${pageContext.request.contextPath}/admin/staff"
                                class="btn btn-secondary">
                                <i class="fas fa-arrow-left"></i>
                                Back to Staff
                            </a>
                            <a href="${pageContext.request.contextPath}/admin/staff?view=edit&id=${staff.staffID}"
                                class="btn btn-primary">
                                <i class="fas fa-edit"></i>
                                Edit Staff
                            </a>
                        </div>
                    </div>
                </div>

                <div class="card-body">
                    <div class="row">
                        <div class="col-md-4">
                            <div class="account-profile text-center">
                                <div class="profile-image">
                                    <i class="fas fa-user-tie fa-5x"></i>
                                </div>
                                <h3 class="mt-3">${staff.fullName}</h3>
                                <p class="text-muted">${staff.account.username}</p>
                                <div class="account-status">
                                    <span
                                        class="status-badge ${staff.status eq 'Active' ? 'status-active' : 'status-inactive'}">
                                        ${staff.status}
                                    </span>
                                </div>
                                <div class="account-role mt-2">
                                    <span class="badge bg-secondary">Staff</span>
                                </div>
                            </div>
                        </div>
                        <div class="col-md-8">
                            <div class="account-details">
                                <h4>Staff Information</h4>
                                <table class="table table-striped">
                                    <tr>
                                        <th>Staff ID:</th>
                                        <td>${staff.staffID}</td>
                                    </tr>
                                    <tr>
                                        <th>Account ID:</th>
                                        <td>${staff.accountID}</td>
                                    </tr>
                                    <tr>
                                        <th>Username:</th>
                                        <td>${staff.account.username}</td>
                                    </tr>
                                    <tr>
                                        <th>Email:</th>
                                        <td>${staff.email}</td>
                                    </tr>
                                    <tr>
                                        <th>Full Name:</th>
                                        <td>${staff.fullName}</td>
                                    </tr>
                                    <tr>
                                        <th>Phone:</th>
                                        <td>${staff.phone}</td>
                                    </tr>
                                    <tr>
                                        <th>Address:</th>
                                        <td>${staff.address}</td>
                                    </tr>
                                    <tr>
                                        <th>Gender:</th>
                                        <td>${staff.gender}</td>
                                    </tr>
                                    <tr>
                                        <th>Role:</th>
                                        <td>Staff</td>
                                    </tr>
                                    <tr>
                                        <th>Status:</th>
                                        <td>${staff.status}</td>
                                    </tr>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>

</html> 