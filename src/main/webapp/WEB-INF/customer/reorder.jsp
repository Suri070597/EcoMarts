<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Đơn hàng đã mua</title>
        <style>
            body {
                font-family: Arial, sans-serif;
                margin: 40px;
                background-color: #f9f9f9;
            }

            .header {
                display: flex;
                justify-content: space-between;
                align-items: center;
                margin-bottom: 30px;
            }

            .header button {
                padding: 8px 16px;
                background-color: #4CAF50;
                color: white;
                border: none;
                border-radius: 6px;
                cursor: pointer;
            }

            .header h2 {
                margin: 0;
                color: #333;
            }

            table {
                width: 100%;
                border-collapse: collapse;
                background-color: white;
                box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            }

            th, td {
                padding: 12px;
                text-align: center;
                border-bottom: 1px solid #ddd;
            }

            th {
                background-color: #4CAF50;
                color: white;
            }

            tr:hover {
                background-color: #f1f1f1;
            }

            .reorder-btn {
                padding: 6px 12px;
                background-color: #2196F3;
                color: white;
                border: none;
                border-radius: 4px;
                cursor: pointer;
            }

            .reorder-btn:hover {
                background-color: #0b7dda;
            }
        </style>
    </head>
    <body>

        <div class="header">
            <a href="${pageContext.request.contextPath}/home">
                <button>← Trở về Trang chủ</button>
            </a>
            <h2>Đơn hàng đã mua</h2>
        </div>

        <table>
            <tr>
                <th>Ngày đặt</th>
                <th>Tổng tiền</th>
                <th>Trạng thái</th>
                <th>Số lượng sản phẩm</th>
                <th>Hành động</th>
            </tr>
            <c:forEach var="order" items="${orders}">
                <tr>
                    <td>${order.orderDate}</td>
                    <td>${order.totalAmount}</td>
                    <td>${order.orderStatus}</td>
                    <td>
                        <%
                            dao.OrderDetailDAO detailDAO = new dao.OrderDetailDAO();
                            int quantity = 0;
                            for (model.OrderDetail od : detailDAO.getOrderDetailsByOrderId(((model.Order) pageContext.getAttribute("order")).getOrderID())) {
                                quantity += od.getQuantity();
                            }
                        %>

                        <%= quantity%> sản phẩm
                    </td>
                    <td>
                        <form action="${pageContext.request.contextPath}/customer/orderDetail" method="get">
                            <input type="hidden" name="orderID" value="${order.orderID}" />
                            <button type="submit">Xem chi tiết</button>
                        </form>


                        <form action="${pageContext.request.contextPath}/reorder" method="post" style="display:inline;">
                            <input type="hidden" name="orderId" value="${order.orderID}">
                            <button class="reorder-btn" type="submit">Mua lại</button>
                        </form>
                    </td>

                </tr>
            </c:forEach>
        </table>

    </body>
</html>
