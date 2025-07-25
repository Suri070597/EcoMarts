/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.OrderDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.List;
import model.RevenueStats;

/**
 *
 * @author nguye
 */
@WebServlet("/admin/statistic/monthly")
public class MonthlyRevenueServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Lấy month và year từ form
        int month = 0, year = 0;
        try {
            month = Integer.parseInt(request.getParameter("month"));
            year = Integer.parseInt(request.getParameter("year"));
        } catch (Exception e) {
            // Nếu không chọn thì mặc định tháng/năm hiện tại
            LocalDate now = LocalDate.now();
            month = now.getMonthValue();
            year = now.getYear();
        }

        // Gọi DAO để lấy dữ liệu
        OrderDAO dao = new OrderDAO();
        double revenue = dao.getMonthlyRevenue(month, year);
        int totalOrders = dao.countDeliveredOrdersInMonth(month, year);
        int totalProducts = dao.getTotalProductSoldInMonth(month, year);
        List<RevenueStats> productList = dao.getMonthlyRevenueDetails(month, year);
        List<RevenueStats> revenuePerMonth = dao.getMonthlyRevenueInYear(year);
        request.setAttribute("revenuePerMonth", revenuePerMonth);

        // Truyền dữ liệu sang JSP
        request.setAttribute("month", month);
        request.setAttribute("year", year);
        request.setAttribute("revenue", revenue);
        request.setAttribute("totalOrders", totalOrders);
        request.setAttribute("totalProducts", totalProducts);
        request.setAttribute("productList", productList);

        request.getRequestDispatcher("/WEB-INF/admin/revenue/revenue-month.jsp").forward(request, response);
    }
}
