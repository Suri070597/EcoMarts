<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Voucher của tôi</title>
        <style>
            body {
                color: #000;
                background: #fff6ec;
                font-family: 'Segoe UI', Arial, sans-serif;
                margin: 0;
                padding: 0;
            }
            table {
                width: 90%;
                margin: auto;
                border-collapse: collapse;
                color: #000; /* chữ đen */
            }
            th, td {
                padding: 12px;
                border: 1px solid #dbc09a; /* border nâu nhạt */
                text-align: left;
                color: #000; /* chữ đen */
            }
            th {
                background-color: #dbc09a;  /* nâu nhạt */
                color: #000;                /* chữ đen */
            }
            tr:nth-child(even) {
                background-color: #f9f9f9;
            }
            h2 {
                text-align: center;
                margin-top: 30px;
                color: #000; /* tiêu đề đen */
            }
            .back-btn {
                display: block;
                width: 220px;
                margin: 30px auto;
                padding: 10px 0;
                text-align: center;
                background-color: #dbc09a;
                color: #000; /* chữ đen */
                text-decoration: none;
                border-radius: 8px;
                font-size: 17px;
                font-weight: bold;
                border: none;
                box-shadow: 0 2px 10px rgba(0,0,0,0.07);
                transition: background 0.25s, color 0.25s;
            }
            .back-btn:hover {
                background-color: #c3a476;
                color: #000;
            }
            .no-voucher {
                text-align: center;
                color: red;
                margin-top: 30px;
                font-size: 17px;
            }
        </style>
    </head>
    <body>

        <h2>Danh sách Voucher của bạn</h2>

        <c:if test="${empty vouchers}">
            <p class="no-voucher">Bạn chưa có voucher nào.</p>
        </c:if>

        <c:if test="${not empty vouchers}">
            <table>
                <thead>
                    <tr>
                        <th>Mã Voucher</th>
                        <th>Giảm giá (%)</th>
                        <th>Hạn sử dụng</th>
                        <th>Mô tả</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach items="${vouchers}" var="v">
                        <tr>
                            <td>${v.voucherCode}</td>
                            <td>${v.discountAmount}%</td>
                            <td>${v.endDate}</td>
                            <td>${v.description}</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>

        <!-- Nút quay lại -->
        <a href="${pageContext.request.contextPath}/home" class="back-btn">← Quay lại trang chủ</a>

    </body>
</html>
