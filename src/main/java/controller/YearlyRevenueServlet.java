package controller;

import dao.OrderDAO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/statistic/yearly")
public class YearlyRevenueServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        OrderDAO orderDAO = new OrderDAO();

        int year;
        try {
            year = Integer.parseInt(request.getParameter("year"));
        } catch (Exception e) {
            year = java.time.Year.now().getValue(); // mặc định là năm hiện tại
        }

        double revenue = orderDAO.getYearlyRevenue(year);
        int totalOrders = orderDAO.countDeliveredOrdersByYear(year);
        int totalProducts = orderDAO.countProductsSoldByYear(year);
        List<Map<String, Object>> productList = orderDAO.getProductSalesByYear(year);
        Map<Integer, Double> last5YearsRevenue = orderDAO.getLast5YearsRevenue();
        request.setAttribute("last5YearsRevenue", last5YearsRevenue);

        request.setAttribute("year", year);
        request.setAttribute("revenue", revenue);
        request.setAttribute("totalOrders", totalOrders);
        request.setAttribute("totalProducts", totalProducts);
        request.setAttribute("productList", productList);

        request.getRequestDispatcher("/WEB-INF/admin/revenue/revenue-year.jsp").forward(request, response);
    }
}
