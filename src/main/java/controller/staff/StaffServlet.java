package controller.staff;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;

import dao.OrderDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet handling staff dashboard functionality
 */
@WebServlet(name = "StaffServlet", urlPatterns = {"/staff", "/staff/dashboard"})
public class StaffServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getServletPath();
        
        // Provide dashboard data for both paths
        loadDashboardData(request);
        request.getRequestDispatcher("/WEB-INF/staff/dashboard.jsp").forward(request, response);
    }
    
    /**
     * Loads all necessary data for the staff dashboard
     * 
     * @param request The HTTP request object
     */
    private void loadDashboardData(HttpServletRequest request) {
        try {
            OrderDAO orderDAO = new OrderDAO();
            
            // Get today's date
            LocalDate today = LocalDate.now();
            Date sqlToday = Date.valueOf(today);
            
            // Get total orders today
            int todayOrders = orderDAO.countOrdersForDate(sqlToday);
            request.setAttribute("todayOrders", todayOrders);
            
            // Get orders being processed
            int processingOrders = orderDAO.countOrdersByStatus("Đang xử lý");
            request.setAttribute("processingOrders", processingOrders);
            
            // Get orders being delivered
            int shippingOrders = orderDAO.countOrdersByStatus("Đang giao hàng");
            request.setAttribute("shippingOrders", shippingOrders);
            
            // Get returns/refunds
            int returnOrders = orderDAO.countOrdersByStatus("Trả hàng/hoàn tiền");
            request.setAttribute("returnOrders", returnOrders);
        

        } catch (Exception e) {
            System.out.println("Error loading dashboard data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public String getServletInfo() {
        return "Handles staff dashboard and related functionality";
    }
} 