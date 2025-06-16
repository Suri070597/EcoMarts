<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="../components/sidebar.jsp" />


<div class="main-content">
    <h1>Order Management</h1>

    <!-- Tìm kiếm -->
    <form action="${pageContext.request.contextPath}/admin/order" method="get" class="search-form">
        <input type="text" name="search" placeholder="Search by OrderID" />
        <button type="submit">Search</button>
    </form>

    <!-- Thống kê -->
    <div class="order-stats">
        <p>Total Orders: <strong>${total}</strong></p>
        <p>Delivered Orders: <strong>${delivered}</strong></p>
    </div>

    <!-- Danh sách đơn hàng -->
    <div class="order-list">
        <c:forEach var="o" items="${orders}">
            <div class="order-card">
                <p><strong>Order ID:</strong> ${o.orderID}</p>
                <p><strong>Customer:</strong> ${o.accountName}</p>
                <p><strong>Order Date:</strong> ${o.orderDate}</p>
                <p><strong>Status:</strong> ${o.orderStatus}</p>
                <p><strong>Total:</strong> $${o.totalAmount}</p>
                <a href="${pageContext.request.contextPath}/admin/order/detail?id=${o.orderID}">View Details</a>
            </div>
        </c:forEach>
    </div>
</div>

<style>
    .main-content {
        margin-left: 250px;
        padding: 20px;
    }

    .search-form {
        margin: 20px 0;
    }

    .search-form input {
        padding: 5px 10px;
    }

    .order-stats {
        display: flex;
        gap: 50px;
        margin-bottom: 20px;
    }

    .order-list {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
        gap: 20px;
    }

    .order-card {
        background: #fff;
        border: 1px solid #ddd;
        padding: 15px;
        border-radius: 8px;
        box-shadow: 0 0 5px rgba(0, 0, 0, 0.1);
    }

    .order-card a {
        display: inline-block;
        margin-top: 10px;
        color: #007bff;
        text-decoration: none;
    }

    .order-card a:hover {
        text-decoration: underline;
    }
</style>
