package controller;

import dao.AccountDAO;
import dao.OrderDAO;
import dao.productDAO;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Product;

@WebServlet(name = "AdminDashboardServlet", urlPatterns = { "/admin/dashboard" })
public class AdminDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get data for dashboard
        OrderDAO orderDAO = new OrderDAO();
        productDAO productDAO = new productDAO();
        AccountDAO accountDAO = new AccountDAO();

        // 1. Revenue summary (total, monthly, daily)
        Map<String, Double> revenueSummary = orderDAO.getRevenueSummary();
        request.setAttribute("revenueSummary", revenueSummary);

        // 2. Revenue by month for chart
        List<Map<String, Object>> revenueByMonth = orderDAO.getRevenueByMonth();
        request.setAttribute("revenueByMonth", revenueByMonth);

        // 3. Order counts by status
        Map<String, Integer> orderStatusCounts = orderDAO.getOrderCountsByStatus();
        request.setAttribute("orderStatusCounts", orderStatusCounts);

        // 4. Recent orders (last 5)
        List<Map<String, Object>> recentOrders = orderDAO.getRecentOrders(5);
        request.setAttribute("recentOrders", recentOrders);

        // 5. Top selling products (top 10)
        List<Map<String, Object>> topProducts = orderDAO.getTopSellingProducts(10);
        request.setAttribute("topProducts", topProducts);

        // 6. Top customers (top 5)
        List<Map<String, Object>> topCustomers = orderDAO.getTopCustomers(5);
        request.setAttribute("topCustomers", topCustomers);

        // 7. Total products count
        List<Product> products = productDAO.getAll();
        request.setAttribute("totalProducts", products.size());

        // 8. Total accounts (customers) count
        request.setAttribute("totalAccounts", accountDAO.getAllAccounts().size());

        // 9. Total orders count
        request.setAttribute("totalOrders", orderDAO.countTotalOrders());

        // Forward to the dashboard JSP
        request.getRequestDispatcher("/WEB-INF/admin/dashboard.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}