<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<link rel="stylesheet"
      href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis()%>">
<link rel="stylesheet"
      href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis()%>">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
<style>
        .main-content {
            margin-left: 250px;
            padding: 20px;
        }

        .order-info {
            background-color: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 20px;
        }

        .order-table {
            width: 100%;
            border-collapse: collapse;
            margin-bottom: 20px;
        }

        .order-table th,
        .order-table td {
            border: 1px solid #ddd;
            padding: 10px;
            text-align: left;
        }

        .order-table th {
            background-color: #f1f1f1;
        }

        .order-info-table {
            width: 100%;
            border-collapse: collapse;
            background-color: #fff;
        }

        .order-info-table th,
        .order-info-table td {
            border: 1px solid #ddd;
            padding: 10px;
            text-align: left;
            vertical-align: middle;
        }
        
        .order-table th,
        .order-table td {
            text-align: center;
        }
        
        .order-table td.number {
            text-align: right;
        }

        .order-info-table th {
            width: 260px;
            background-color: #f1f1f1;
            white-space: nowrap;
        }

        .status-form {
            display: inline-flex;
            align-items: center;
            gap: 10px;
            margin-top: 6px;
        }

        .status-message {
            color: green;
            font-weight: bold;
            margin-left: 12px;
        }

        .order-total {
            font-size: 18px;
            font-weight: bold;
            margin-top: 10px;
        }
        .btn-back {
            display: inline-block;
            padding: 8px 16px;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            font-weight: bold;
            transition: background-color 0.3s;
        }

        .btn-back:hover {
            background-color: #0056b3;
        }
        .order-summary-horizontal {
            width: 100%;
            max-width: 770px;
            margin-top: 20px;
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 15px;
            background-color: #fafafa;
        }

        .summary-row {
            display: flex;
            justify-content: space-between;
            padding: 8px 0;
            border-bottom: 1px solid #eee;
        }

        .summary-row:last-child {
            border-bottom: none;
        }

        .summary-row.total {
            font-weight: bold;
            color: #d9534f;
        }

        .summary-row .label {
            flex: 1;
        }

        .summary-row .value {
            flex: 1;
            text-align: right;
        }

    </style>
</head>
<body>
    <div class="container-fluid">
        <%-- Include admin sidebar --%>
        <jsp:include page="../components/sidebar.jsp" />

        <div class="main-content">
            <div class="container">
                <div style="display: flex; justify-content: space-between; align-items: center;">
                    <a href="<c:url value='/staff/order' />" class="btn-back">← Quay lại</a>

                    <h1>Chi tiết đơn hàng</h1>
                </div>


                <!-- Thông tin đơn hàng -->
                <div class="order-info">
                    <table class="order-info-table">
                        <tbody>
                            <tr>
                                <th>Mã đơn hàng</th>
                                <td>${order.orderID}</td>
                            </tr>
                            <tr>
                                <th>Khách hàng</th>
                                <td>${order.accountName}</td>
                            </tr>
                            <tr>
                                <th>Số điện thoại</th>
                                <td>${order.shippingPhone}</td>
                            </tr>
                            <tr>
                                <th>Địa chỉ giao hàng</th>
                                <td>${order.shippingAddress}</td>
                            </tr>
                            <tr>
                                <th>Ngày đặt hàng</th>
                                <td>${order.orderDate}</td>
                            </tr>
                            <tr>
                                <th>Trạng thái đơn hàng</th>
                                <td>${order.orderStatus}</td>
                            </tr>
                            <tr>
                                <th>Phương thức thanh toán</th>
                                <td>${order.paymentMethod}</td>
                            </tr>
                            <tr>
                                <th>Trạng thái thanh toán</th>
                                <td>${order.paymentStatus}</td>
                            </tr>
                        </tbody>
                    </table>
                </div>

                <!-- Danh sách sản phẩm -->
                <h3>Sản phẩm trong đơn hàng:</h3>
                <table class="order-table">
                    <thead>
                        <tr>
                            <th>Sản phẩm</th>
                            <th>Đơn giá</th>
                            <th>Số lượng</th>
                            <th>Thành tiền</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="d" items="${details}">
                            <tr>
                                <td>${d.productName}</td>
                                <td class="number"><fmt:formatNumber value="${d.unitPrice}" type="number" pattern="#,###"/> đ</td>
                                <td class="number">
                                    <c:choose>
                                        <c:when test="${d.quantity % 1 == 0}">
                                            <fmt:formatNumber value="${d.quantity}" pattern="#"/>
                                        </c:when>
                                        <c:otherwise>
                                            <fmt:formatNumber value="${d.quantity}" pattern="#.##"/>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="number"><fmt:formatNumber value="${d.subTotal}" type="number" pattern="#,###"/> đ</td>

                            </tr>
                        </c:forEach>
                    </tbody>
                </table>

                <!-- Tổng tiền -->
                <div class="order-summary-horizontal">
                    <div class="summary-row">
                        <div class="label">Giá gốc:</div>
                        <div class="value"><fmt:formatNumber value="${order.subtotal}" type="number" pattern="#,###"/> đ</div>
                    </div>
                    <div class="summary-row">
                        <div class="label">Giảm giá:</div>
                        <div class="value"><fmt:formatNumber value="${order.discountAmount}" type="number" pattern="#,###"/> đ</div>
                    </div>
                    <div class="summary-row">
                        <div class="label">VAT (8%):</div>
                        <div class="value"><fmt:formatNumber value="${order.vat}" type="number" pattern="#,###"/> đ</div>
                    </div>
                    <div class="summary-row total">
                        <div class="label">Tổng thanh toán:</div>
                        <div class="value"><fmt:formatNumber value="${order.grandTotal}" type="number" pattern="#,###"/> đ</div>
                    </div>
                </div>

            </div>
        </div>
    </div>
</body>