package controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import dao.AccountDAO;
import dao.OrderDAO;
import dao.ProductDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Product;

@WebServlet(name = "AdminDashboardServlet", urlPatterns = { "/admin/dashboard" })
public class AdminDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get data for dashboard
        OrderDAO orderDAO = new OrderDAO();
        ProductDAO productDAO = new ProductDAO();
        AccountDAO accountDAO = new AccountDAO();

        // 1. Revenue summary with detailed breakdown (total, monthly, daily, tax,
        // voucher)
        Map<String, Object> revenueSummary = orderDAO.getRevenueSummaryWithDetails();
        request.setAttribute("revenueSummary", revenueSummary);

        // 2. Revenue by month for chart
        List<Map<String, Object>> revenueByMonth = orderDAO.getRevenueByMonth();
        request.setAttribute("revenueByMonth", revenueByMonth);

        // 3. Order counts by status
        Map<String, Integer> orderStatusCounts = orderDAO.getOrderCountsByStatus();
        request.setAttribute("orderStatusCounts", orderStatusCounts);

        // 4. Recent orders (last 5) with calculated grandTotal
        List<Map<String, Object>> recentOrders = orderDAO.getRecentOrders(5);
        // Calculate grandTotal for each recent order (same logic as staff)
        for (Map<String, Object> order : recentOrders) {
            int orderId = (Integer) order.get("orderId");
            double totalAfterDiscount = (Double) order.get("totalAmount"); // Đã trừ giảm giá
            double discount = orderDAO.getDiscountAmountByOrderID(orderId).doubleValue(); // Lấy số giảm

            double subtotal = totalAfterDiscount + discount; // Giá gốc
            double vat = subtotal * 0.08; // Thuế tính trên giá gốc
            double grandTotal = totalAfterDiscount + vat; // Tổng cuối = giá sau voucher + thuế

            order.put("grandTotal", grandTotal);
            order.put("discountAmount", discount);
            order.put("subtotal", subtotal);
            order.put("vat", vat);
        }
        request.setAttribute("recentOrders", recentOrders);

        // 5. Top selling products (top 10)
        String view = request.getParameter("view");
        if ("top-products".equals(view)) {
            List<Map<String, Object>> topProducts = orderDAO.getTopSellingProducts(10);
            request.setAttribute("topProducts", topProducts);
            request.getRequestDispatcher("/WEB-INF/admin/report/top-products.jsp").forward(request, response);
            return;
        }

        // 6. Top customers (top 10)
        if ("top-customers".equals(view)) {
            List<Map<String, Object>> topCustomers = orderDAO.getTopCustomers(10);
            request.setAttribute("topCustomers", topCustomers);
            request.getRequestDispatcher("/WEB-INF/admin/report/top-customers.jsp").forward(request, response);
            return;
        }

        // 7. Additional statistics
        int totalOrders = orderDAO.countTotalOrders();
        List<Product> products = productDAO.getAll();
        int totalProducts = products.size();
        int totalAccounts = accountDAO.countAccountsByRole(0);

        request.setAttribute("totalOrders", totalOrders);
        request.setAttribute("totalProducts", totalProducts);
        request.setAttribute("totalAccounts", totalAccounts);

        request.getRequestDispatcher("/WEB-INF/admin/dashboard.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
