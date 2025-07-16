<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Voucher của tôi</title>
        <style>
            table {
                width: 90%;
                margin: auto;
                border-collapse: collapse;
            }

            th, td {
                padding: 12px;
                border: 1px solid #ccc;
                text-align: left;
            }

            th {
                background-color: #4CAF50;
                color: white;
            }

            tr:nth-child(even) {
                background-color: #f9f9f9;
            }

            h2 {
                text-align: center;
                margin-top: 30px;
            }

            .back-btn {
                display: block;
                width: 150px;
                margin: 30px auto;
                padding: 10px 20px;
                text-align: center;
                background-color: #007bff;
                color: white;
                text-decoration: none;
                border-radius: 5px;
                transition: background-color 0.3s ease;
            }

            .back-btn:hover {
                background-color: #0056b3;
            }

            .no-voucher {
                text-align: center;
                color: red;
                margin-top: 30px;
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
        <<a href="${pageContext.request.contextPath}/home" class="back-btn">← Quay lại trang chủ</a>

    </body>
</html>
