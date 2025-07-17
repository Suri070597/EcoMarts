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
                padding: 30px;
                box-shadow: 0 5px 20px rgba(0,0,0,0.1);
            }
            .register-title {
                text-align: center;
                color: #2e7d32;
                font-size: 24px;
                margin-bottom: 20px;
            }
            .error {
                color: red;
                text-align: center;
                margin-bottom: 10px;
            }
            label {
                margin-top: 10px;
                font-weight: 500;
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
                <label for="username">Tên người dùng:</label>
                <input type="text" name="username" id="username" required value="<%= acc != null && acc.getUsername() != null ? acc.getUsername() : ""%>">

                <label for="fullName">Họ và tên:</label>
                <input type="text" name="fullName" id="fullName"
                       value="<%= acc != null && acc.getFullName() != null ? acc.getFullName() : (acc != null && acc.getUsername() != null ? acc.getUsername() : "")%>"
                       required>


                <label for="email">Email:</label>
                <input type="email" name="email" id="email" readonly required value="<%= acc != null && acc.getEmail() != null ? acc.getEmail() : ""%>">
                <label for="phone">Số điện thoại:</label>
                <input type="tel" name="phone" id="phone" pattern="[0-9]{10}" required title="Số điện thoại phải có 10 chữ số">

                <label for="address">Địa chỉ chi tiết:</label>
                <input type="text" name="address" id="address" required>

                </select>



                <label for="gender">Giới tính:</label>
                <select name="gender" id="gender" required>
                    <option value="">--Chọn giới tính--</option>
                    <option value="Nam">Nam</option>
                    <option value="Nữ">Nữ</option>
                </select>

           <!--     <label for="password">Mật khẩu:</label>
                <input type="password" name="password" id="password" required
                       pattern="(?=.*[a-zA-Z])(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{6,}"
                       title="Mật khẩu phải có ít nhất 6 ký tự, chứa chữ, số và ký tự đặc biệt">

                <label for="confirmPassword">Xác nhận mật khẩu:</label>
                <input type="password" name="confirmPassword" id="confirmPassword" required> -->

                <input type="submit" value="Hoàn tất đăng ký" class="btn btn-success w-100 mt-3">
            </form>
        </div>
    </body>
</html>



