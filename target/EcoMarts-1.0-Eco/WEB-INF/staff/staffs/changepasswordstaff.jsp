<%-- 
    Document   : changepasswordstaff
    Created on : Jul 21, 2025, 4:33:00 PM
    Author     : ADMIN
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Đổi mật khẩu</title>
         <link rel="shortcut icon" href="assets/img/eco.png" type="image/x-icon">
        <style>
            body {
                font-family: 'Segoe UI', sans-serif;
                background: linear-gradient(to right, #fff6ec, #fff);
                display: flex;
                justify-content: center;
                align-items: center;
                height: 100vh;
                margin: 0;
                color: #000; /* chữ đen */
            }
            .form-container {
                background-color: #fff;
                padding: 32px 24px;
                border-radius: 15px;
                box-shadow: 0 8px 28px rgba(0, 0, 0, 0.12);
                max-width: 420px;
                width: 100%;
                margin: 0 18px;
                box-sizing: border-box;
                color: #000;
            }
            h2 {
                text-align: center;
                color: #000; /* tiêu đề đen */
                margin-bottom: 26px;
                letter-spacing: 0.5px;
            }
            label {
                display: block;
                margin-bottom: 7px;
                color: #000;
                font-weight: 500;
            }
            input[type="password"] {
                width: 100%;
                padding: 11px 12px;
                border: 1.5px solid #dbc09a; /* viền nâu nhạt */
                border-radius: 12px;
                margin-bottom: 17px;
                font-size: 16px;
                background: #fff;
                color: #000;
                transition: border-color 0.3s;
                box-sizing: border-box;
            }
            input[type="password"]:focus {
                border-color: #b3936b; /* nâu đậm hơn khi focus */
                outline: none;
            }
            .btn-group {
                display: flex;
                justify-content: space-between;
                gap: 18px;
                margin-top: 8px;
            }
            button, .back-btn {
                flex: 1;
                padding: 10px 0;
                border: none;
                border-radius: 8px;
                font-size: 16px;
                font-weight: 600;
                cursor: pointer;
                transition: background 0.3s, color 0.3s;
                text-align: center;
                background-color: #dbc09a; /* nền nâu nhạt */
                color: #000; /* chữ đen */
            }
            button:hover, .back-btn:hover {
                background-color: #b3936b; /* hover nâu đậm */
                color: #000;
            }
            .back-btn {
                text-decoration: none;
                display: inline-block;
            }
            .message {
                color: red;
                font-weight: bold;
                text-align: center;
                margin-top: 15px;
                font-size: 17px;
            }
        </style>
    </head>
    <body>
        <div class="form-container">
            <h2>Đổi mật khẩu</h2>
            <form action="changepasswordstaff" method="post">
                <label>Mật khẩu mới:</label>
                <input type="password" name="newPassword" required />

                <label>Xác nhận mật khẩu mới:</label>
                <input type="password" name="confirmPassword" required />

                <div class="btn-group">
                    <button type="submit">Xác nhận</button>
                    <a href="staff" class="back-btn">Quay lại</a>
                </div>

                <c:if test="${not empty message}">
                    <p class="message">${message}</p>
                </c:if>
            </form>
        </div>
    </body>
</html>
