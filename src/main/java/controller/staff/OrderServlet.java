package controller.staff;

import java.io.IOException;
import java.util.List;

import dao.OrderDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Order;
import model.OrderDetail;

@WebServlet(name = "OrderServlet", urlPatterns = {
    "/staff/order",
    "/staff/order/detail",
    "/staff/order/updateStatus" // 👈 thêm dòng này
})

public class OrderServlet extends HttpServlet {

    OrderDAO dao = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();
        if (path.equals("/staff/order")) {
            handleOrderList(request, response);
        } else if (path.equals("/staff/order/detail")) {
            handleOrderDetail(request, response);
        }
    }

    private void handleOrderList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String search = request.getParameter("search");

        List<Order> orders;
        if (search != null && !search.trim().isEmpty()) {
            try {
                // Nếu nhập số → tìm theo OrderID
                int orderId = Integer.parseInt(search.trim());
                Order o = dao.getOrderById(orderId);
                orders = o != null ? List.of(o) : List.of();
            } catch (NumberFormatException e) {
                // Nếu không phải số → tìm theo tên
                orders = dao.getOrdersByCustomerName(search.trim());
            }
        } else {
            orders = dao.getAllOrders();
            for (Order o : orders) {
                calculateOrderSummary(o);
            }

        }

        int total = dao.countAllOrders();
        int delivered = dao.countDeliveredOrders();
        int cancelled = dao.countCancelledOrders();
        request.setAttribute("cancelled", cancelled);
        request.setAttribute("orders", orders);
        request.setAttribute("total", total);
        request.setAttribute("delivered", delivered);

        request.getRequestDispatcher("/WEB-INF/staff/order/manage-order.jsp").forward(request, response);

    }

    private void handleOrderDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idRaw = request.getParameter("id");
        if (idRaw == null) {
            response.sendRedirect("order");
            return;
        }

        try {
            int id = Integer.parseInt(idRaw);
            Order order = dao.getOrderById(id);
            calculateOrderSummary(order);

            List<OrderDetail> details = dao.getOrderDetailsByOrderId(id);

            if (order == null) {
                response.sendRedirect("order");
                return;
            }

            request.setAttribute("order", order);
            request.setAttribute("details", details);

            request.getRequestDispatcher("/WEB-INF/staff/order/order-detail.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect("order");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();

        if (path.equals("/staff/order/updateStatus")) {
            updateOrderStatus(request, response);
        }
    }

    private void updateOrderStatus(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int orderId = Integer.parseInt(request.getParameter("orderId"));
            String newStatus = request.getParameter("status");

            boolean success = dao.updateOrderStatus(orderId, newStatus);

            if (success) {
                request.setAttribute("message", "Cập nhật trạng thái đơn hàng thành công!");
            } else {
                request.setAttribute("message", "Cập nhật thất bại. Vui lòng thử lại.");
            }

            // Lấy lại order & detail để hiển thị
            Order order = dao.getOrderById(orderId);
            calculateOrderSummary(order);
            List<OrderDetail> details = dao.getOrderDetailsByOrderId(orderId);
            request.setAttribute("order", order);
            request.setAttribute("details", details);

            request.getRequestDispatcher("/WEB-INF/staff/order/order-detail.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("order");
        }
    }

private void calculateOrderSummary(Order order) {
    double totalAfterDiscount = order.getTotalAmount(); // Đã trừ giảm giá
    double discount = dao.getDiscountAmountByOrderID(order.getOrderID()).doubleValue(); // Lấy số giảm

    double subtotal = totalAfterDiscount + discount; // Giá gốc
    double vat = subtotal * 0.08;
    double grandTotal = totalAfterDiscount + vat;

    order.setDiscountAmount(discount);
    order.setSubtotal(subtotal);
    order.setVat(vat);
    order.setGrandTotal(grandTotal);
}


}
