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

@WebServlet(name = "AdminDashboardServlet", urlPatterns = {"/admin/dashboard"})
public class AdminDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get data for dashboard
        OrderDAO orderDAO = new OrderDAO();
        ProductDAO productDAO = new ProductDAO();
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
        String view = request.getParameter("view");
        if ("top-products".equals(view)) {
            List<Map<String, Object>> topProducts = orderDAO.getTopSellingProducts(10);
            request.setAttribute("topProducts", topProducts);
            request.getRequestDispatcher("/WEB-INF/admin/report/top-products.jsp").forward(request, response);
            return;

            // 6. Top customers (top 5)
        } else if ("top-customers".equals(view)) {
            List<Map<String, Object>> topCustomers = orderDAO.getTopCustomers(5);
            request.setAttribute("topCustomers", topCustomers);
            request.getRequestDispatcher("/WEB-INF/admin/report/top-customers.jsp").forward(request, response);
            return;
        }

        // 7. Total products count
        List<Product> products = productDAO.getAll();
        request.setAttribute("totalProducts", products.size());

        // 8. Total accounts (customers) count
        request.setAttribute("totalAccounts", accountDAO.countAccountsByRole(0));

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
