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
            background: linear-gradient(to right, #f8f9fa, #e0f2f1);
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
            box-shadow: 0 6px 18px rgba(0, 0, 0, 0.15);
            max-width: 400px;
            width: 100%;
        }

        h2 {
            text-align: center;
            color: #00796b;
            margin-bottom: 25px;
        }

        label {
            font-weight: 500;
            display: block;
            margin-bottom: 8px;
        }

        input[type="password"] {
            width: 100%;
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 8px;
            margin-bottom: 15px;
            transition: border-color 0.3s;
        }

        input[type="password"]:focus {
            border-color: #00796b;
            outline: none;
        }

        .btn-container {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        button, .back-btn {
            padding: 10px 20px;
            border: none;
            border-radius: 8px;
            text-decoration: none;
            font-size: 14px;
            cursor: pointer;
            transition: background-color 0.3s;
        }

        button {
            background-color: #00796b;
            color: #fff;
        }

        button:hover {
            background-color: #004d40;
        }

        .back-btn {
            background-color: #b0bec5;
            color: #000;
        }

        .back-btn:hover {
            background-color: #90a4ae;
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
        <form action="VerifyPasswordServlet" method="post">
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
