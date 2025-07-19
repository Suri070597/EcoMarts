<%-- 
    Document   : updategoogle
    Created on : Jul 14, 2025, 11:02:50 AM
    Author     : ADMIN
--%>

<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Account" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Bổ sung thông tin tài khoản Google</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <style>
            body {
                background: #fff6ec;
            }
            .container {
                margin: 60px auto;
                max-width: 500px;
                background: #fff;
                border-radius: 12px;
                padding: 30px 32px 28px 32px;
                box-shadow: 0 5px 20px rgba(0,0,0,0.1);
            }
            .register-title {
                text-align: center;
                color: #2e7d32;
                font-size: 24px;
                margin-bottom: 24px;
                font-weight: 600;
            }
            .error {
                color: red;
                text-align: center;
                margin-bottom: 10px;
            }
            .form-label {
                font-weight: 500;
                margin-bottom: 6px;
            }
            .form-control, .form-select {
                border-radius: 8px;
                padding: 8px 12px;
            }
            .mb-3 {
                margin-bottom: 18px !important;
            }
            .btn-success {
                background-color: #2e7d32 !important;
                border-color: #2e7d32 !important;
                font-weight: 500;
                font-size: 17px;
                padding: 10px 0;
                border-radius: 8px;
                box-shadow: 0 2px 6px rgba(46,125,50,0.09);
                transition: background 0.2s;
            }
            .btn-success:hover, .btn-success:focus {
                background-color: #256326 !important;
                border-color: #256326 !important;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <h2 class="register-title">Bổ sung thông tin tài khoản Google</h2>
            <% if (request.getAttribute("error") != null) {%>
            <div class="error"><%= request.getAttribute("error")%></div>
            <% } %>
            <%
                Account acc = (Account) session.getAttribute("account");
            %>
            <form action="<%=request.getContextPath()%>/updategoogle" method="post">
                <div class="mb-3">
                    <label for="username" class="form-label">Tên người dùng:</label>
                    <input type="text" name="username" id="username" class="form-control"
                           required value="<%= acc != null && acc.getUsername() != null ? acc.getUsername() : ""%>">
                </div>
                <div class="mb-3">
                    <label for="fullName" class="form-label">Họ và tên:</label>
                    <input type="text" name="fullName" id="fullName" class="form-control"
                           value="<%= acc != null && acc.getFullName() != null ? acc.getFullName() : (acc != null && acc.getUsername() != null ? acc.getUsername() : "")%>"
                           required>
                </div>
                <div class="mb-3">
                    <label for="email" class="form-label">Email:</label>
                    <input type="email" name="email" id="email" class="form-control"
                           readonly required value="<%= acc != null && acc.getEmail() != null ? acc.getEmail() : ""%>">
                </div>
                <div class="mb-3">
                    <label for="phone" class="form-label">Số điện thoại:</label>
                    <input type="tel" name="phone" id="phone" class="form-control"
                           pattern="[0-9]{10}" required title="Số điện thoại phải có 10 chữ số">
                </div>
                <div class="mb-3">
                    <label for="address" class="form-label">Địa chỉ chi tiết:</label>
                    <input type="text" name="address" id="address" class="form-control" required>
                </div>
                <div class="mb-3">
                    <label for="gender" class="form-label">Giới tính:</label>
                    <select name="gender" id="gender" class="form-select" required>
                        <option value="">--Chọn giới tính--</option>
                        <option value="Nam">Nam</option>
                        <option value="Nữ">Nữ</option>
                    </select>
                </div>
                <input type="submit" value="Hoàn tất đăng ký" class="btn btn-success w-100 mt-2">
            </form>
        </div>
    </body>
</html>
