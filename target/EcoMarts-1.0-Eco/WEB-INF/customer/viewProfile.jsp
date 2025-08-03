<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
    <head>
        <title>Thông tin cá nhân</title>
        <style>
            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                background: linear-gradient(to right, #fff6ec, #fff);
                display: flex;
                justify-content: center;
                align-items: center;
                min-height: 100vh;
                margin: 0;
            }
            .container {
                background: #fff;
                padding: 32px 38px;
                border-radius: 15px;
                box-shadow: 0 8px 28px rgba(0, 0, 0, 0.12);
                width: 100%;
                max-width: 480px;
            }
            h2 {
                text-align: center;
                color: #111111;
                margin-bottom: 26px;
                letter-spacing: 0.5px;
            }
            label {
                display: block;
                margin-bottom: 7px;
                color: #333;
                font-weight: 500;
                letter-spacing: 0.2px;
            }
            input[type="text"], select {
                width: 100%;
                padding: 11px 12px;
                border: 1px solid #ddd;
                border-radius: 8px;
                margin-bottom: 17px;
                font-size: 16px;
                background: #fff;
                transition: border 0.3s;
            }
            input[type="text"]:focus, select:focus {
                border-color: #4CAF50;
                outline: none;
                background: #fff;
            }
            .btn-container {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-top: 16px;
                gap: 18px;
            }
            button, a.button-link {
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
                color: #111; /* chữ màu đen */
                background-color: #dbc09a; /* nền nút màu vàng */
            }
            button:hover, a.button-link:hover {
                background-color: #bfa270; /* nền nút đậm hơn khi hover */
                color: #111; /* chữ vẫn màu đen */
            }
            a.button-link {
                border: 1px solid #dbc09a; /* viền cũng vàng */
            }
            .message {
                text-align: center;
                margin-top: 20px;
                color: #4CAF50;
                font-weight: bold;
                font-size: 17px;
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
                <input type="text" name="phone" pattern="[0-9]{10}" required title="Số điện thoại phải có 10 chữ số" value="${sessionScope.account.phone}" />

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
