<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="stylesheet"
      href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis()%>">
<link rel="stylesheet"
      href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis()%>">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <div class="container-fluid">
        <%-- Include admin sidebar --%>
        <jsp:include page="../components/sidebar.jsp" />

        <div class="main-content">
            <div class="container">
                <div style="display: flex; justify-content: space-between; align-items: center;">
                    <a href="<c:url value='/admin/order' />" class="btn-back">← Back</a>

                    <h1>Order Detail</h1>
                </div>


                <!-- Thông tin đơn hàng -->
                <div class="order-info">
                    <p><strong>Order ID:</strong> ${order.orderID}</p>
                    <p><strong>Customer:</strong> ${order.accountName}</p>
                    <p><strong>Phone:</strong> ${order.shippingPhone}</p>
                    <p><strong>Shipping Address:</strong> ${order.shippingAddress}</p>
                    <p><strong>Order Date:</strong> ${order.orderDate}</p>
                    <p><strong>Order Status:</strong> ${order.orderStatus}</p>
                    <p><strong>Payment Method:</strong> ${order.paymentMethod}</p>
                    <p><strong>Payment Status:</strong> ${order.paymentStatus}</p>
                </div>

                <!-- Danh sách sản phẩm -->
                <h3>Products in this order:</h3>
                <table class="order-table">
                    <thead>
                        <tr>
                            <th>Product</th>
                            <th>Unit Price</th>
                            <th>Quantity</th>
                            <th>Subtotal</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="d" items="${details}">
                            <tr>
                                <td>${d.productName}</td>
                                <td>$${d.unitPrice}</td>
                                <td>${d.quantity}</td>
                                <td>$${d.subTotal}</td>

                            </tr>
                        </c:forEach>
                    </tbody>
                </table>

                <!-- Tổng tiền -->
                <div class="order-total">
                    <p><strong>Total Amount:</strong> $${order.totalAmount}</p>
                </div>
            </div>

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

            </style>
