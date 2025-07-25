<%@page contentType="text/html;charset=UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Chi tiết đơn hàng</title>
    </head>
    <body>
        <h2>Chi tiết đơn hàng #${order.orderID}</h2>
        <p>Trạng thái: ${order.orderStatus}</p>
        <p>Ngày đặt: <fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy" /></p>
        <p>Tổng tiền: <fmt:formatNumber value="${total}" type="currency" /></p>

        <table border="1">
            <tr>
                <th>Sản phẩm</th>
                <th>Số lượng</th>
                <th>Giá</th>
                <th>Thành tiền</th>
            </tr>
            <c:forEach var="od" items="${orderDetails}">
                <tr>
                    <td>${od.productName}</td>
                    <td>${od.quantity}</td>
                    <td><fmt:formatNumber value="${od.unitPrice}" type="currency" /></td>
                    <td><fmt:formatNumber value="${od.subTotal}" type="currency" /></td>
                </tr>
            </c:forEach>
        </table>

        <form method="post" action="${pageContext.request.contextPath}/customer/orderDetail">
            <input type="hidden" name="orderId" value="${order.orderID}" />
            <button type="submit" name="action" value="back">Quay lại</button>

            <c:if test="${order.orderStatus != 'Đã giao' and order.orderStatus != 'Đã hủy'}">
                <button type="submit" name="action" value="cancel">Hủy đơn</button>
            </c:if>

            <button type="submit" name="action" value="reorder">Mua lại</button>
        </form>
    </body>
</html>
