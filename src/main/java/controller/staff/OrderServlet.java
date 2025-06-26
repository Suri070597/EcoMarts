package controller.staff;

import dao.OrderDAO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Order;
import model.OrderDetail;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "OrderServlet", urlPatterns = {"/staff/order", "/staff/order/detail"})
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
        if (search != null && !search.isEmpty()) {
            try {
                int orderId = Integer.parseInt(search);
                Order o = dao.getOrderById(orderId);
                orders = o != null ? List.of(o) : List.of();
            } catch (NumberFormatException e) {
                orders = List.of(); // Không hợp lệ thì trả về danh sách rỗng
            }
        } else {
            orders = dao.getAllOrders();
        }

        int total = dao.countAllOrders();
        int delivered = dao.countDeliveredOrders();

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
}
