<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Thông tin cá nhân</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(to right, #e0f7fa, #fff);
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            margin: 0;
        }

        .container {
            background: #ffffff;
            padding: 30px 40px;
            border-radius: 15px;
            box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
            width: 100%;
            max-width: 500px;
        }

        h2 {
            text-align: center;
            color: #00796b;
            margin-bottom: 25px;
        }

        label {
            display: block;
            margin-bottom: 6px;
            color: #333;
            font-weight: bold;
        }

        input[type="text"], select {
            width: 100%;
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 8px;
            margin-bottom: 15px;
            transition: border 0.3s;
        }

        input[type="text"]:focus, select:focus {
            border-color: #00796b;
            outline: none;
        }

        .btn-container {
            display: flex;
            justify-content: space-between;
            margin-top: 10px;
        }

        button, a.button-link {
            padding: 10px 20px;
            border: none;
            border-radius: 8px;
            text-decoration: none;
            font-size: 15px;
            cursor: pointer;
            transition: background 0.3s;
        }

        button {
            background-color: #00796b;
            color: white;
        }

        button:hover {
            background-color: #004d40;
        }

        a.button-link {
            background-color: #b0bec5;
            color: #000;
        }

        a.button-link:hover {
            background-color: #90a4ae;
        }

        .message {
            text-align: center;
            margin-top: 20px;
            color: green;
            font-weight: bold;
        }
    </style>
</head>
<body>
    <div class="container">
        <h2>Thông tin tài khoản</h2>
        <form action="UpdateProfileServlet" method="post">
            <input type="hidden" name="accountId" value="${sessionScope.account.accountID}" />

            <label>Họ tên:</label>
            <input type="text" name="fullName" value="${sessionScope.account.fullName}" required />

            <label>SĐT:</label>
            <input type="text" name="phone" value="${sessionScope.account.phone}" />

            <label>Địa chỉ:</label>
            <input type="text" name="address" value="${sessionScope.account.address}" />

            <label>Giới tính:</label>
            <select name="gender">
                <option value="Nam" ${sessionScope.account.gender == 'Nam' ? 'selected' : ''}>Nam</option>
                <option value="Nữ" ${sessionScope.account.gender == 'Nữ' ? 'selected' : ''}>Nữ</option>
            </select>

            <div class="btn-container">
                <button type="submit">Lưu</button>
                <a href="home" class="button-link">Quay lại</a>
            </div>
        </form>

        <c:if test="${not empty message}">
            <p class="message">${message}</p>
        </c:if>
    </div>
</body>
</html>
