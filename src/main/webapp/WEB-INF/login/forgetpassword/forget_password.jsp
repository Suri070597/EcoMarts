<%-- 
    Document   : forget_password
    Created on : Jun 30, 2025, 8:50:49 PM
    Author     : ADMIN
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Forget Password</title>
        <style>
            body {
                font-family: Arial, sans-serif;
                background: #fff6ec;
                display: flex;
                justify-content: center;
                align-items: center;
                height: 100vh;
                margin: 0;
            }
            .login-box {
                background: #fff;
                padding: 36px 30px 28px 30px;
                border-radius: 20px;
                box-shadow: 0 4px 18px 0 rgba(60,52,34,0.09);
                text-align: center;
                width: 370px;
                min-width: 320px;
            }
            h2 {
                margin-bottom: 22px;
                font-weight: bold;
                font-size: 2rem;
            }
            .form-control {
                width: 100%;
                padding: 13px 14px;
                margin: 12px 0 0 0;
                border: 1px solid #ddd;
                border-radius: 9px;
                font-size: 16px;
                box-sizing: border-box;
                outline: none;
                transition: border .2s;
            }
            .form-control:focus {
                border-color: #28a745;
            }
            .btn {
                width: 100%;
                padding: 13px;
                background-color: #28a745;
                color: white;
                border: none;
                border-radius: 9px;
                font-size: 18px;
                cursor: pointer;
                margin-top: 18px;
                margin-bottom: 6px;
                font-weight: bold;
                transition: background .2s;
            }
            .btn:hover {
                background-color: #00b07e;
            }
            .links {
                margin-top: 16px;
                font-size: 16px;
            }
            .links a {
                color: #0066cc;
                text-decoration: none;
                margin: 0 5px;
            }
            .alert {
                margin: 13px 0 0 0;
                padding: 11px 19px;
                font-size: 16px;
                border-radius: 7px;
            }
            .alert-success {
                background: #e9ffe5;
                color: #20892b;
            }
            .alert-danger {
                background: #ffe9e9;
                color: #b10e1e;
            }
        </style>

    </head>
    <body>
        <div class="login-box">
            <h2>Quên mật khẩu</h2>
            <form action="forget-password" method="post">
                <input type="email" class="form-control" name="email" placeholder="Nhập email của bạn" required>
                <button class="btn" type="submit">Gửi email xác nhận</button>
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

