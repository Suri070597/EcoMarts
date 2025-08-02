<%-- 
    Document   : reset_password
    Created on : Jun 30, 2025, 8:51:14 PM
    Author     : ADMIN
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Đặt lại mật khẩu</title>
        <style>
            body {
                font-family: Arial, sans-serif;
                background: #fff6ec;
                display: flex;
                justify-content: center;
                align-items: center;
                height: 100vh;
                margin: 0;
                color: #000; /* chữ đen */
            }
            .login-box {
                background: #fff;
                padding: 36px 30px 28px 30px;
                border-radius: 20px;
                box-shadow: 0 4px 18px rgba(60,52,34,0.09);
                text-align: center;
                width: 370px;
                min-width: 320px;
                color: #000;
            }
            h2 {
                margin-bottom: 22px;
                font-weight: bold;
                font-size: 2rem;
                color: #000; /* tiêu đề đen */
            }
            .form-control {
                width: 100%;
                padding: 13px 14px;
                margin: 12px 0 0 0;
                border: 1.5px solid #dbc09a; /* viền nâu nhạt */
                border-radius: 9px;
                font-size: 16px;
                box-sizing: border-box;
                outline: none;
                transition: border 0.2s;
                color: #000;
                background: #fff;
            }
            .form-control:focus {
                border-color: #b3936b; /* nâu đậm khi focus */
            }
            .btn {
                width: 100%;
                padding: 13px;
                background-color: #dbc09a; /* nền nâu nhạt */
                color: #000;               /* chữ đen */
                border: none;
                border-radius: 9px;
                font-size: 18px;
                cursor: pointer;
                margin-top: 18px;
                margin-bottom: 6px;
                font-weight: bold;
                transition: background 0.2s;
            }
            .btn:hover {
                background-color: #b3936b; /* hover nâu đậm */
            }
            .links {
                margin-top: 16px;
                font-size: 16px;
            }
            .links a {
                color: #0066cc; /* link vẫn xanh dương */
                text-decoration: none;
                margin: 0 5px;
            }
            .alert {
                margin: 13px 0 0 0;
                padding: 11px 19px;
                font-size: 16px;
                border-radius: 7px;
                color: #000;
            }
            .alert-success {
                background: #fff3e0;        /* nền nâu nhạt rể */
                border: 1px solid #dbc09a;
            }
            .alert-danger {
                background: #ffe9e9;        /* giữ đỏ cho error */
                border: 1px solid #b10e1e;
            }
        </style>

        <script>
            function validateForm() {
                var pw = document.getElementById("password").value;
                var cpw = document.getElementById("confirmPassword").value;
                if (pw !== cpw) {
                    alert("Mật khẩu xác nhận không trùng khớp!");
                    return false;
                }
                return true;
            }
        </script>
    </head>
    <body>
        <div class="login-box">
            <h2>Đặt lại mật khẩu</h2>
            <form action="reset-password" method="post" onsubmit="return validateForm();">
                <input type="hidden" name="token" value="${param.token != null ? param.token : token}">
                <input type="password" id="password" class="form-control"
                       name="password" placeholder="Mật khẩu mới" required
                       pattern="(?=.*[a-zA-Z])(?=.*\d)(?=.*[!@#$%^&*])[A-Za-z\d!@#$%^&*]{6,}"
                       title="Mật khẩu phải có ít nhất 6 ký tự, chứa chữ, số và ký tự đặc biệt">
                <input type="password" id="confirmPassword" class="form-control"
                       name="confirmPassword" placeholder="Xác nhận mật khẩu mới" required>
                <button class="btn" type="submit">Đặt lại mật khẩu</button>
            </form>
            <% String message = (String) request.getAttribute("message"); %>
            <% String error = (String) request.getAttribute("error"); %>
            <% if (message != null) {%>
            <div class="alert alert-success"><%= message%></div>
            <% } %>
            <% if (error != null) {%>
            <div class="alert alert-danger"><%= error%></div>
            <% }%>
            <div class="links">
                <a href="<%=request.getContextPath()%>/login">Quay lại đăng nhập</a>
            </div>
        </div>
    </body>
</html>
