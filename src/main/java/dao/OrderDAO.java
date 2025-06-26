package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DBContext;
import model.Order;
import model.OrderDetail;

public class OrderDAO extends DBContext {

    // Lấy danh sách tất cả đơn hàng
    public List<Order> getAllOrders() {
        List<Order> list = new ArrayList<>();
        String sql = """
            SELECT o.*, a.FullName
            FROM [Order] o
            JOIN Account a ON o.AccountID = a.AccountID
            ORDER BY o.OrderDate DESC
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Order o = new Order();
                o.setOrderID(rs.getInt("OrderID"));
                o.setAccountID(rs.getInt("AccountID"));
                o.setOrderDate(rs.getTimestamp("OrderDate"));
                o.setTotalAmount(rs.getDouble("TotalAmount"));
                o.setShippingAddress(rs.getString("ShippingAddress"));
                o.setShippingPhone(rs.getString("ShippingPhone"));
                o.setPaymentMethod(rs.getString("PaymentMethod"));
                o.setPaymentStatus(rs.getString("PaymentStatus"));
                o.setOrderStatus(rs.getString("OrderStatus"));
                o.setNotes(rs.getString("Notes"));
                o.setAccountName(rs.getString("FullName"));
                list.add(o);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Tìm đơn hàng theo ID
    public Order getOrderById(int id) {
        String sql = """
            SELECT o.*, a.FullName
            FROM [Order] o
            JOIN Account a ON o.AccountID = a.AccountID
            WHERE o.OrderID = ?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Order o = new Order();
                o.setOrderID(rs.getInt("OrderID"));
                o.setAccountID(rs.getInt("AccountID"));
                o.setOrderDate(rs.getTimestamp("OrderDate"));
                o.setTotalAmount(rs.getDouble("TotalAmount"));
                o.setShippingAddress(rs.getString("ShippingAddress"));
                o.setShippingPhone(rs.getString("ShippingPhone"));
                o.setPaymentMethod(rs.getString("PaymentMethod"));
                o.setPaymentStatus(rs.getString("PaymentStatus"));
                o.setOrderStatus(rs.getString("OrderStatus"));
                o.setNotes(rs.getString("Notes"));
                o.setAccountName(rs.getString("FullName"));
                return o;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Chi tiết sản phẩm trong đơn hàng
    public List<OrderDetail> getOrderDetailsByOrderId(int orderId) {
        List<OrderDetail> list = new ArrayList<>();
        String sql = """
            SELECT od.*, p.ProductName
            FROM OrderDetail od
            JOIN Product p ON od.ProductID = p.ProductID
            WHERE od.OrderID = ?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                OrderDetail od = new OrderDetail();
                od.setOrderDetailID(rs.getInt("OrderDetailID"));
                od.setOrderID(rs.getInt("OrderID"));
                od.setProductID(rs.getInt("ProductID"));
                od.setQuantity(rs.getInt("Quantity"));
                od.setUnitPrice(rs.getDouble("UnitPrice"));
                od.setSubTotal(rs.getDouble("SubTotal"));
                od.setProductName(rs.getString("ProductName"));
                list.add(od);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Thống kê số đơn hàng
    public int countAllOrders() {
        String sql = "SELECT COUNT(*) FROM [Order]";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countDeliveredOrders() {
        String sql = "SELECT COUNT(*) FROM [Order] WHERE OrderStatus = N'Đã giao'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
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
        String sql = "SELECT o.OrderID, o.OrderDate, o.TotalAmount, o.OrderStatus, "
                + "a.Username FROM [Order] o JOIN Account a ON o.AccountID = a.AccountID "
                + "ORDER BY o.OrderDate DESC OFFSET 0 ROWS FETCH NEXT ? ROWS ONLY";
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
        String sql = "SELECT MONTH(OrderDate) as Month, SUM(TotalAmount) as Revenue "
                + "FROM [Order] WHERE YEAR(OrderDate) = YEAR(GETDATE()) "
                + "AND OrderStatus = N'Đã giao' "
                + "GROUP BY MONTH(OrderDate) ORDER BY Month";
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
        String monthlySql = "SELECT SUM(TotalAmount) FROM [Order] WHERE "
                + "YEAR(OrderDate) = YEAR(GETDATE()) AND MONTH(OrderDate) = MONTH(GETDATE()) "
                + "AND OrderStatus = N'Đã giao'";

        // Today's revenue
        String dailySql = "SELECT SUM(TotalAmount) FROM [Order] WHERE "
                + "CAST(OrderDate AS DATE) = CAST(GETDATE() AS DATE) "
                + "AND OrderStatus = N'Đã giao'";

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
        String sql = "SELECT p.ProductID, p.ProductName, p.ImageURL, SUM(od.Quantity) AS TotalQuantity, "
                + "SUM(od.SubTotal) AS TotalRevenue "
                + "FROM OrderDetail od "
                + "JOIN Product p ON od.ProductID = p.ProductID "
                + "JOIN [Order] o ON od.OrderID = o.OrderID "
                + "WHERE o.OrderStatus = N'Đã giao' "
                + "GROUP BY p.ProductID, p.ProductName, p.ImageURL "
                + "ORDER BY TotalQuantity DESC "
                + "OFFSET 0 ROWS FETCH NEXT ? ROWS ONLY";

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
        String sql = "SELECT a.AccountID, a.Username, COUNT(o.OrderID) AS OrderCount, "
                + "SUM(o.TotalAmount) AS TotalSpent "
                + "FROM Account a "
                + "JOIN [Order] o ON a.AccountID = o.AccountID "
                + "WHERE o.OrderStatus = N'Đã giao' "
                + "GROUP BY a.AccountID, a.Username "
                + "ORDER BY TotalSpent DESC "
                + "OFFSET 0 ROWS FETCH NEXT ? ROWS ONLY";

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

    // Count orders for a specific date
    public int countOrdersForDate(java.sql.Date date) {
        String sql = "SELECT COUNT(*) FROM [Order] WHERE CAST(OrderDate AS DATE) = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, date);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error in countOrdersForDate: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    // Count orders by status
    public int countOrdersByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM [Order] WHERE OrderStatus = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error in countOrdersByStatus: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
}
