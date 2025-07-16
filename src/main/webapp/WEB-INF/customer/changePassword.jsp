<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Đổi mật khẩu</title>
        <style>
            body {
                font-family: 'Segoe UI', sans-serif;
                background: linear-gradient(to right, #e1f5fe, #ffffff);
                display: flex;
                justify-content: center;
                align-items: center;
                height: 100vh;
                margin: 0;
            }

            .form-container {
                background-color: #ffffff;
                padding: 30px 40px;
                border-radius: 12px;
                box-shadow: 0 6px 18px rgba(0, 0, 0, 0.1);
                width: 100%;
                max-width: 450px;
            }

            h2 {
                text-align: center;
                color: #0288d1;
                margin-bottom: 25px;
            }

            label {
                display: block;
                margin-bottom: 6px;
                font-weight: bold;
                color: #333;
            }

            input[type="password"] {
                width: 100%;
                padding: 10px;
                margin-bottom: 20px;
                border: 1px solid #ccc;
                border-radius: 8px;
                transition: border-color 0.3s;
            }

            input[type="password"]:focus {
                border-color: #0288d1;
                outline: none;
            }

            .btn-group {
                display: flex;
                justify-content: space-between;
                align-items: center;
            }

            button, .back-btn {
                padding: 10px 20px;
                border: none;
                border-radius: 8px;
                font-size: 14px;
                cursor: pointer;
                transition: background-color 0.3s;
            }

            button {
                background-color: #0288d1;
                color: #fff;
            }

            button:hover {
                background-color: #01579b;
            }

            .back-btn {
                background-color: #cfd8dc;
                color: #000;
                text-decoration: none;
                text-align: center;
            }

            .back-btn:hover {
                background-color: #b0bec5;
            }

            .message {
                color: red;
                font-weight: bold;
                text-align: center;
                margin-top: 15px;
            }
        </style>
    </head>
    <body>
        <div class="form-container">
            <h2>Đổi mật khẩu</h2>
            <form action="ChangePasswordServlet" method="post">
                <label>Mật khẩu mới:</label>
                <input type="password" name="newPassword" required />

                <label>Xác nhận mật khẩu mới:</label>
                <input type="password" name="confirmPassword" required />

                <div class="btn-group">
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
