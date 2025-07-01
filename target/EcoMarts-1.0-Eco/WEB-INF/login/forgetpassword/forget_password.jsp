<%-- 
    Document   : forget_password
    Created on : Jun 30, 2025, 8:50:49 PM
    Author     : ADMIN
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<html>
<head>
    <title>Quên mật khẩu</title>
</head>
<body>
    <h2>Quên mật khẩu</h2>
    <form action="forget-password" method="post">
        Nhập email: <input type="email" name="email" required />
        <button type="submit">Gửi liên kết xác nhận</button>
    </form>
    <p style="color: red;">${error}</p>
    <p style="color: green;">${message}</p>
    <a href="login">Quay lại đăng nhập</a>
</body>
</html>

