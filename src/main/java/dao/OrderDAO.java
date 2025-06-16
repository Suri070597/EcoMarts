package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Order;
import db.DBContext;

public class OrderDAO extends DBContext {

    // Get all orders with pagination
    public List<Order> getAllOrders(int page, int pageSize) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM [Order] ORDER BY OrderDate DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, (page - 1) * pageSize);
            ps.setInt(2, pageSize);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Order order = new Order();
                order.setOrderID(rs.getInt("OrderID"));
                order.setAccountID(rs.getInt("AccountID"));
                order.setOrderDate(rs.getTimestamp("OrderDate"));
                order.setTotalAmount(rs.getDouble("TotalAmount"));
                order.setShippingAddress(rs.getString("ShippingAddress"));
                order.setShippingPhone(rs.getString("ShippingPhone"));
                order.setPaymentMethod(rs.getString("PaymentMethod"));
                order.setPaymentStatus(rs.getString("PaymentStatus"));
                order.setOrderStatus(rs.getString("OrderStatus"));
                order.setNotes(rs.getString("Notes"));
                orders.add(order);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error in getAllOrders: " + e.getMessage());
        }
        return orders;
    }

    // Count total orders
    public int countTotalOrders() {
        String sql = "SELECT COUNT(*) FROM [Order]";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                rs.close();
                ps.close();
                return count;
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error in countTotalOrders: " + e.getMessage());
        }
        return 0;
    }

    // Get recent orders for dashboard (last 5 orders)
    public List<Map<String, Object>> getRecentOrders(int limit) {
        List<Map<String, Object>> recentOrders = new ArrayList<>();
        String sql = "SELECT o.OrderID, o.OrderDate, o.TotalAmount, o.OrderStatus, " +
                "a.Username FROM [Order] o JOIN Account a ON o.AccountID = a.AccountID " +
                "ORDER BY o.OrderDate DESC OFFSET 0 ROWS FETCH NEXT ? ROWS ONLY";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> order = new HashMap<>();
                order.put("orderId", rs.getInt("OrderID"));
                order.put("orderDate", rs.getTimestamp("OrderDate"));
                order.put("totalAmount", rs.getDouble("TotalAmount"));
                order.put("orderStatus", rs.getString("OrderStatus"));
                order.put("customerName", rs.getString("Username"));
                recentOrders.add(order);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error in getRecentOrders: " + e.getMessage());
        }
        return recentOrders;
    }

    // Get revenue by month for current year
    public List<Map<String, Object>> getRevenueByMonth() {
        List<Map<String, Object>> revenueData = new ArrayList<>();
        String sql = "SELECT MONTH(OrderDate) as Month, SUM(TotalAmount) as Revenue " +
                "FROM [Order] WHERE YEAR(OrderDate) = YEAR(GETDATE()) " +
                "AND OrderStatus = N'Đã giao' " +
                "GROUP BY MONTH(OrderDate) ORDER BY Month";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> monthData = new HashMap<>();
                monthData.put("month", rs.getInt("Month"));
                monthData.put("revenue", rs.getDouble("Revenue"));
                revenueData.add(monthData);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error in getRevenueByMonth: " + e.getMessage());
        }
        return revenueData;
    }

    // Get revenue summary - total, monthly, daily
    public Map<String, Double> getRevenueSummary() {
        Map<String, Double> summary = new HashMap<>();

        // Total revenue for all time
        String totalSql = "SELECT SUM(TotalAmount) FROM [Order] WHERE OrderStatus = N'Đã giao'";

        // This month's revenue
        String monthlySql = "SELECT SUM(TotalAmount) FROM [Order] WHERE " +
                "YEAR(OrderDate) = YEAR(GETDATE()) AND MONTH(OrderDate) = MONTH(GETDATE()) " +
                "AND OrderStatus = N'Đã giao'";

        // Today's revenue
        String dailySql = "SELECT SUM(TotalAmount) FROM [Order] WHERE " +
                "CAST(OrderDate AS DATE) = CAST(GETDATE() AS DATE) " +
                "AND OrderStatus = N'Đã giao'";

        try {
            // Get total revenue
            PreparedStatement ps1 = conn.prepareStatement(totalSql);
            ResultSet rs1 = ps1.executeQuery();
            if (rs1.next()) {
                summary.put("total", rs1.getDouble(1));
            } else {
                summary.put("total", 0.0);
            }
            rs1.close();
            ps1.close();

            // Get monthly revenue
            PreparedStatement ps2 = conn.prepareStatement(monthlySql);
            ResultSet rs2 = ps2.executeQuery();
            if (rs2.next()) {
                summary.put("monthly", rs2.getDouble(1));
            } else {
                summary.put("monthly", 0.0);
            }
            rs2.close();
            ps2.close();

            // Get daily revenue
            PreparedStatement ps3 = conn.prepareStatement(dailySql);
            ResultSet rs3 = ps3.executeQuery();
            if (rs3.next()) {
                summary.put("daily", rs3.getDouble(1));
            } else {
                summary.put("daily", 0.0);
            }
            rs3.close();
            ps3.close();
        } catch (SQLException e) {
            System.out.println("Error in getRevenueSummary: " + e.getMessage());
            summary.put("total", 0.0);
            summary.put("monthly", 0.0);
            summary.put("daily", 0.0);
        }

        return summary;
    }

    // Get order counts by status
    public Map<String, Integer> getOrderCountsByStatus() {
        Map<String, Integer> statusCounts = new HashMap<>();
        String sql = "SELECT OrderStatus, COUNT(*) as Count FROM [Order] GROUP BY OrderStatus";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String status = rs.getString("OrderStatus");
                int count = rs.getInt("Count");
                statusCounts.put(status, count);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error in getOrderCountsByStatus: " + e.getMessage());
        }

        return statusCounts;
    }

    // Get top selling products
    public List<Map<String, Object>> getTopSellingProducts(int limit) {
        List<Map<String, Object>> topProducts = new ArrayList<>();
        String sql = "SELECT p.ProductID, p.ProductName, p.ImageURL, SUM(od.Quantity) AS TotalQuantity, " +
                "SUM(od.SubTotal) AS TotalRevenue " +
                "FROM OrderDetail od " +
                "JOIN Product p ON od.ProductID = p.ProductID " +
                "JOIN [Order] o ON od.OrderID = o.OrderID " +
                "WHERE o.OrderStatus = N'Đã giao' " +
                "GROUP BY p.ProductID, p.ProductName, p.ImageURL " +
                "ORDER BY TotalQuantity DESC " +
                "OFFSET 0 ROWS FETCH NEXT ? ROWS ONLY";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> product = new HashMap<>();
                product.put("productId", rs.getInt("ProductID"));
                product.put("productName", rs.getString("ProductName"));
                product.put("image", rs.getString("ImageURL"));
                product.put("totalQuantity", rs.getInt("TotalQuantity"));
                product.put("totalRevenue", rs.getDouble("TotalRevenue"));
                topProducts.add(product);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error in getTopSellingProducts: " + e.getMessage());
        }

        return topProducts;
    }

    // Get top customers
    public List<Map<String, Object>> getTopCustomers(int limit) {
        List<Map<String, Object>> topCustomers = new ArrayList<>();
        String sql = "SELECT a.AccountID, a.Username, COUNT(o.OrderID) AS OrderCount, " +
                "SUM(o.TotalAmount) AS TotalSpent " +
                "FROM Account a " +
                "JOIN [Order] o ON a.AccountID = o.AccountID " +
                "WHERE o.OrderStatus = N'Đã giao' " +
                "GROUP BY a.AccountID, a.Username " +
                "ORDER BY TotalSpent DESC " +
                "OFFSET 0 ROWS FETCH NEXT ? ROWS ONLY";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> customer = new HashMap<>();
                customer.put("accountId", rs.getInt("AccountID"));
                customer.put("username", rs.getString("Username"));
                customer.put("orderCount", rs.getInt("OrderCount"));
                customer.put("totalSpent", rs.getDouble("TotalSpent"));
                topCustomers.add(customer);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error in getTopCustomers: " + e.getMessage());
        }

        return topCustomers;
    }
}
