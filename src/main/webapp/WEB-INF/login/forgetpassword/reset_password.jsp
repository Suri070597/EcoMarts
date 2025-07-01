<%-- 
    Document   : reset_password
    Created on : Jun 30, 2025, 8:51:14 PM
    Author     : ADMIN
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<html>
<head>
    <title>Đặt lại mật khẩu</title>
</head>
<body>
    <h2>Đặt lại mật khẩu</h2>
                <form action="<%=request.getContextPath()%>/reset-password" method="post">
                <input type="hidden" name="token" value="${param.token != null ? param.token : token}">
                <input 
                    type="password"name="password" placeholder="Mật khẩu mới" required
                    pattern="(?=.*[a-zA-Z])(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{6,}"
                    title="Mật khẩu phải có ít nhất 6 ký tự, chứa chữ, số và ký tự đặc biệt">

                <input type="password" name="confirmPassword" placeholder="Xác nhận mật khẩu mới" required>
                <button class="btn" type="submit">Cập nhật mật khẩu</button>
            </form>
    <p style="color: red;">${error}</p>
    <p style="color: green;">${message}</p>
    <a href="login">Quay lại đăng nhập</a>
</body>
</html>
