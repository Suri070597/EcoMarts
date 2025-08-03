<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Đăng Ký</title>
        <link rel="shortcut icon" href="assets/img/eco.png" type="image/x-icon">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">

        <!-- Bootstrap và FontAwesome -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">

        <!-- CSS tùy chỉnh -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis()%>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis()%>">

        <style>
            * {
                box-sizing: border-box;
            }
            body {
                font-family: 'Roboto', sans-serif;
                background-color: #fff6ec;
                display: flex;
                justify-content: center;
                align-items: center;
                min-height: 100vh;
                margin: 0;
            }
            .container {
                background-color: #fff;
                padding: 30px;
                border-radius: 12px;
                box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
                max-width: 500px;
                width: 100%;
            }
            .register-title {
                text-align: center;
                font-size: 24px;
                color: #dbc09a;
                margin-bottom: 20px;
                font-weight: bold;
            }
            .error {
                color: red;
                text-align: center;
                margin-bottom: 10px;
                font-size: 14px;
            }
            label {
                margin-top: 10px;
                font-weight: 500;
                color: #333;
            }
            input, select {
                width: 100%;
                padding: 10px;
                margin-top: 5px;
                border: 1px solid #ccc;
                border-radius: 6px;
                font-size: 15px;
            }
            input:focus, select:focus {
                border-color: #4CAF50;
                outline: none;
            }
            input[type="submit"] {
                margin-top: 20px;
                background-color: #dbc09a;
                color: white;
                border: none;
                font-weight: bold;
                transition: background-color 0.3s ease;
            }
            input[type="submit"]:hover {
                background-color: #dbc09a;
            }
        </style>
    </head>
    <body>
        <div class="container">
            <h2 class="register-title">Đăng Ký Tài Khoản</h2>

            <% if (request.getAttribute("error") != null) {%>
            <p class="error"><%= request.getAttribute("error")%></p>
            <% }%>

            <form action="<%= request.getContextPath()%>/RegistrationServlet" method="post">
                <label for="username">Tên người dùng:</label>
                <input type="text" id="username" name="username" required>

                <label for="fullName">Họ và tên:</label>
                <input type="text" id="fullName" name="fullName" required>

                <label for="email">Email:</label>
                <input type="email" id="email" name="email" required>

                <label for="phone">Số điện thoại:</label>
                <input type="tel" id="phone" name="phone" pattern="[0-9]{10}" required title="Số điện thoại phải có 10 chữ số">

                <label for="address">Địa chỉ:</label>
                <input type="text" id="address" name="address" required title="Thành Phố">

                <label for="gender">Giới tính:</label>
                <select name="gender" id="gender" required>
                    <option value="">-- Chọn giới tính --</option>
                    <option value="Nam">Nam</option>
                    <option value="Nữ">Nữ</option>

                </select>

                <label for="password">Mật khẩu:</label>
                <input type="password" id="password" name="password" required
                       pattern="(?=.*[a-zA-Z])(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{6,}"
                       title="Mật khẩu phải có ít nhất 6 ký tự, chứa chữ, số và ký tự đặc biệt">

                <label for="confirmPassword">Xác nhận mật khẩu:</label>
                <input type="password" id="confirmPassword" name="confirmPassword" required>

                <input type="submit" value="Đăng Ký">
            </form>
        </div>
    </body>
</html>
