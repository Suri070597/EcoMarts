<%-- 
    Document   : verifypassword
    Created on : Jul 21, 2025, 4:33:32 PM
    Author     : ADMIN
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Xác nhận mật khẩu</title>
        <style>
            body {
                font-family: 'Segoe UI', sans-serif;
                background: linear-gradient(to right, #fff6ec, #fff);
                display: flex;
                justify-content: center;
                align-items: center;
                height: 100vh;
                margin: 0;
            }
            .form-container {
                background-color: #fff;
                padding: 32px 24px;      /* đều 2 bên */
                border-radius: 15px;
                box-shadow: 0 8px 28px rgba(0, 0, 0, 0.12);
                max-width: 400px;
                width: 100%;
                margin: 0 18px;
                box-sizing: border-box;
            }

            h2 {
                text-align: center;
                color: #4CAF50;
                margin-bottom: 26px;
                letter-spacing: 0.5px;
            }
            label {
                font-weight: 500;
                display: block;
                margin-bottom: 8px;
                margin-left: 0;      /* Đảm bảo sát lề trái */
                padding-left: 0;
                color: #333;
            }
            input[type="password"] {
                width: 100%;
                padding: 11px 12px;
                border: 1.5px solid #4CAF50; /* để border xanh lá rõ nét */
                border-radius: 12px;
                margin-bottom: 17px;
                margin-left: 0;
                margin-right: 0;
                font-size: 16px;
                background: #fff;
                transition: border-color 0.3s;
                box-sizing: border-box;
            }
            input[type="password"]:focus {
                border-color: #4CAF50;
                outline: none;
            }
            .btn-container {
                display: flex;
                justify-content: space-between;
                align-items: center;
                gap: 18px;
                margin-top: 10px;
            }
            button, .back-btn {
                flex: 1;
                padding: 10px 0;
                border: none;
                border-radius: 8px;
                text-decoration: none;
                font-size: 16px;
                font-weight: 600;
                cursor: pointer;
                transition: background 0.3s, color 0.3s;
                text-align: center;
            }
            button {
                background-color: #4CAF50;
                color: #fff;
            }
            button:hover {
                background-color: #388E3C;
            }
            .back-btn {
                background-color: #f5f5f5;
                color: #4CAF50;
                border: 1px solid #4CAF50;
                display: inline-block;
            }
            .back-btn:hover {
                background-color: #e8f5e9;
                color: #388E3C;
                border-color: #388E3C;
            }
            .message {
                margin-top: 15px;
                text-align: center;
                color: red;
                font-weight: bold;
            }
        </style>
    </head>
    <body>
        <div class="form-container">
            <h2>Xác nhận mật khẩu</h2>
            <form action="verifypasswordServlet" method="post">
                <label>Nhập mật khẩu hiện tại:</label>
                <input type="password" name="currentPassword" required />

                <div class="btn-container">
                    <button type="submit">Xác nhận</button>
                    <a href="home" class="back-btn">Quay lại</a>
                </div>

                <c:if test="${not empty message}">
                    <p class="message">${message}</p>
                </c:if>
            </form>
        </div>
    </body>
</html>