package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import db.DBContext;
import model.Account;
import model.CartItem;
import model.Order;
import model.OrderDetail;
import model.Product;
import model.RevenueStats;

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
                
                // Create Account object and set it
                Account account = new Account();
                account.setAccountID(o.getAccountID());
                account.setFullName(rs.getString("FullName"));
                o.setAccount(account);
                
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
            SELECT od.*, p.ProductName, p.Unit
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
                od.setQuantity(rs.getDouble("Quantity"));
                od.setUnitPrice(rs.getDouble("UnitPrice"));
                od.setSubTotal(rs.getDouble("SubTotal"));
                od.setProductName(rs.getString("ProductName"));
                od.setUnit(rs.getString("Unit"));
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
                return (int)rs.getDouble(1);
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
                return (int)rs.getDouble(1);
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

    public int countCancelledOrders() {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM [Order] WHERE OrderStatus = N'Đã hủy'";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
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
                return (int)rs.getDouble(1);
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
                return (int)rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.out.println("Error in countOrdersByStatus: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public boolean updateOrderStatus(int orderId, String status) {
        String sql = "UPDATE [Order] SET OrderStatus = ? WHERE OrderID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Cập nhật trạng thái thanh toán của đơn hàng
     * @param orderId ID của đơn hàng
     * @param status Trạng thái thanh toán mới
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean updatePaymentStatus(int orderId, String status) {
        String sql = "UPDATE [Order] SET PaymentStatus = ? WHERE OrderID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, orderId);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Order> getOrdersByCustomerName(String name) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, a.FullName FROM [Order] o JOIN Account a ON o.AccountID = a.AccountID "
                + "WHERE a.FullName LIKE ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + name + "%");
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
                o.setAccountName(rs.getString("FullName")); // tên khách hàng
                list.add(o);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Order> getOrdersByAccountId(int accountId) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, a.FullName FROM [Order] o JOIN Account a ON o.AccountID = a.AccountID "
                + "WHERE o.AccountID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
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

    public List<RevenueStats> getMonthlyRevenueDetails(int month, int year) {
        List<RevenueStats> list = new ArrayList<>();
        String sql = """
        SELECT p.ProductName, SUM(od.Quantity) AS TotalQuantity, SUM(od.SubTotal) AS TotalRevenue
        FROM OrderDetail od
        JOIN Product p ON od.ProductID = p.ProductID
        JOIN [Order] o ON od.OrderID = o.OrderID
        WHERE MONTH(o.OrderDate) = ? AND YEAR(o.OrderDate) = ? AND o.OrderStatus = N'Đã giao'
        GROUP BY p.ProductName
        ORDER BY TotalQuantity DESC
    """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String name = rs.getString("ProductName");
                int quantity = rs.getInt("TotalQuantity");
                double revenue = rs.getDouble("TotalRevenue");
                list.add(new RevenueStats(name, quantity, revenue));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public double getMonthlyRevenue(int month, int year) {
        String sql = "SELECT SUM(TotalAmount) FROM [Order] WHERE MONTH(OrderDate) = ? AND YEAR(OrderDate) = ? AND OrderStatus = N'Đã giao'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countDeliveredOrdersInMonth(int month, int year) {
        String sql = "SELECT COUNT(*) FROM [Order] WHERE MONTH(OrderDate) = ? AND YEAR(OrderDate) = ? AND OrderStatus = N'Đã giao'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return (int)rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalProductSoldInMonth(int month, int year) {
        String sql = """
        SELECT SUM(od.Quantity) FROM [Order] o
        JOIN OrderDetail od ON o.OrderID = od.OrderID
        WHERE MONTH(o.OrderDate) = ? AND YEAR(o.OrderDate) = ? AND o.OrderStatus = N'Đã giao'
    """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return (int)rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
// Tổng doanh thu trong năm

    public double getYearlyRevenue(int year) {
        String sql = "SELECT SUM(TotalAmount) FROM [Order] "
                + "WHERE YEAR(OrderDate) = ? AND OrderStatus = N'Đã giao'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

// Tổng số đơn đã giao trong năm
    public int countDeliveredOrdersByYear(int year) {
        String sql = "SELECT COUNT(*) FROM [Order] "
                + "WHERE YEAR(OrderDate) = ? AND OrderStatus = N'Đã giao'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return (int)rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

// Tổng số lượng sản phẩm đã bán trong năm
    public int countProductsSoldByYear(int year) {
        String sql = """
        SELECT SUM(od.Quantity) 
        FROM OrderDetail od
        JOIN [Order] o ON od.OrderID = o.OrderID
        WHERE YEAR(o.OrderDate) = ? AND o.OrderStatus = N'Đã giao'
    """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return (int)rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

// Danh sách sản phẩm bán được trong năm
    public List<Map<String, Object>> getProductSalesByYear(int year) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = """
        SELECT p.ProductName, SUM(od.Quantity) AS totalQuantity, SUM(od.SubTotal) AS totalRevenue
        FROM OrderDetail od
        JOIN Product p ON od.ProductID = p.ProductID
        JOIN [Order] o ON od.OrderID = o.OrderID
        WHERE YEAR(o.OrderDate) = ? AND o.OrderStatus = N'Đã giao'
        GROUP BY p.ProductName
        ORDER BY totalRevenue DESC
    """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("productName", rs.getString("ProductName"));
                row.put("totalQuantity", rs.getInt("totalQuantity"));
                row.put("totalRevenue", rs.getDouble("totalRevenue"));
                list.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void deleteByOrderId(int orderId) {
        String sql = "DELETE FROM OrderDetail WHERE OrderID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("deleteByOrderId error: " + e.getMessage());
        }
    }

    public void deleteOrder(int orderId) {
        String sql = "DELETE FROM [Order] WHERE OrderID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.executeUpdate();
        } catch (Exception e) {
            System.out.println("deleteOrder error: " + e.getMessage());
        }
    }

    public void cancelOrder(int orderId) {
        String cancelOrderSQL = "UPDATE [Order] SET OrderStatus = N'Đã hủy' WHERE OrderID = ?";
        String getOrderDetailsSQL = "SELECT ProductID, Quantity FROM OrderDetail WHERE OrderID = ?";
        String updateStockSQL = "UPDATE Product SET StockQuantity = StockQuantity + ? WHERE ProductID = ?";

        try {
            conn.setAutoCommit(false); // Bắt đầu transaction

            // 1. Hủy đơn hàng
            try (PreparedStatement cancelStmt = conn.prepareStatement(cancelOrderSQL)) {
                cancelStmt.setInt(1, orderId);
                cancelStmt.executeUpdate();
            }

            // 2. Lấy chi tiết đơn hàng
            List<OrderDetail> orderDetails = new ArrayList<>();
            try (PreparedStatement detailStmt = conn.prepareStatement(getOrderDetailsSQL)) {
                detailStmt.setInt(1, orderId);
                ResultSet rs = detailStmt.executeQuery();
                while (rs.next()) {
                    int productId = rs.getInt("ProductID");
                    double quantity = rs.getDouble("Quantity");
                    orderDetails.add(new OrderDetail(productId, quantity)); // ✅ sửa chỗ này
                }
            }

            // 3. Cập nhật tồn kho
            try (PreparedStatement stockStmt = conn.prepareStatement(updateStockSQL)) {
                for (OrderDetail od : orderDetails) {
                    stockStmt.setDouble(1, od.getQuantity());
                    stockStmt.setInt(2, od.getProductID());
                    stockStmt.addBatch();
                }
                stockStmt.executeBatch();
            }

            conn.commit(); // Ghi nhận thay đổi
        } catch (SQLException e) {
            try {
                conn.rollback(); // Nếu lỗi, rollback toàn bộ
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                conn.setAutoCommit(true); // Trả lại trạng thái ban đầu
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public List<CartItem> getCartItemsFromOrder(int orderId) {
        List<CartItem> items = new ArrayList<>();
        String sql = "SELECT ProductID, Quantity FROM OrderDetail WHERE OrderID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                CartItem item = new CartItem();
                item.setProductID(rs.getInt("ProductID"));
                item.setQuantity(rs.getDouble("Quantity"));
                items.add(item);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }

public List<RevenueStats> getMonthlyRevenueInYear(int year) {
    List<RevenueStats> list = new ArrayList<>();

    // Khởi tạo map mặc định 12 tháng doanh thu = 0
    Map<Integer, Double> revenueMap = new HashMap<>();
    for (int i = 1; i <= 12; i++) {
        revenueMap.put(i, 0.0);
    }

    String sql = "SELECT MONTH(OrderDate) AS Month, SUM(TotalAmount) AS Revenue "
               + "FROM [Order] WHERE YEAR(OrderDate) = ? AND OrderStatus = N'Đã giao' "
               + "GROUP BY MONTH(OrderDate)";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, year);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            int month = rs.getInt("Month");
            double revenue = rs.getDouble("Revenue");
            revenueMap.put(month, revenue); // Ghi đè doanh thu thực tế
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    // Chuyển map thành danh sách RevenueStats
    for (int i = 1; i <= 12; i++) {
        RevenueStats stat = new RevenueStats();
        stat.setMonth(i);
        stat.setTotalRevenue(revenueMap.get(i));
        list.add(stat);
    }

    return list;
}
public Map<Integer, Double> getLast5YearsRevenue() {
    Map<Integer, Double> revenueMap = new LinkedHashMap<>();
    String sql = "SELECT YEAR(OrderDate) AS Year, SUM(TotalAmount) AS Revenue " +
                 "FROM [Order] " +
                 "WHERE OrderStatus = N'Đã giao' AND YEAR(OrderDate) BETWEEN ? AND ? " +
                 "GROUP BY YEAR(OrderDate) ORDER BY YEAR(OrderDate)";

    int currentYear = java.time.Year.now().getValue();
    int startYear = currentYear - 4;

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, startYear);
        ps.setInt(2, currentYear);
        ResultSet rs = ps.executeQuery();

        for (int year = startYear; year <= currentYear; year++) {
            revenueMap.put(year, 0.0); // mặc định = 0
        }

        while (rs.next()) {
            int year = rs.getInt("Year");
            double revenue = rs.getDouble("Revenue");
            revenueMap.put(year, revenue);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return revenueMap;
}







/**
 * Creates a new order from cart items
 * @param order The order to create
 * @param items The list of cart items to add to the order
 * @return The ID of the created order, or -1 if creation failed
 */
public int createOrderFromCart(Order order, List<CartItem> items) {
    int orderId = -1;
    
    try {
        // Set autocommit to false for transaction
        conn.setAutoCommit(false);
        
        // Insert order
        String insertOrderSql = "INSERT INTO [Order] (AccountID, OrderDate, TotalAmount, ShippingAddress, " +
                              "ShippingPhone, PaymentMethod, PaymentStatus, OrderStatus, Notes) " +
                              "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(insertOrderSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, order.getAccountID());
            ps.setTimestamp(2, new java.sql.Timestamp(order.getOrderDate().getTime()));
            ps.setDouble(3, order.getTotalAmount());
            ps.setString(4, order.getShippingAddress());
            ps.setString(5, order.getShippingPhone());
            ps.setString(6, order.getPaymentMethod());
            ps.setString(7, order.getPaymentStatus());
            ps.setString(8, order.getOrderStatus());
            ps.setString(9, order.getNotes());
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    orderId = rs.getInt(1);
                }
                rs.close();
            }
        }
        
        // If order creation succeeded, insert order details
        if (orderId > 0) {
            // Insert order details
            String insertDetailSql = "INSERT INTO OrderDetail (OrderID, ProductID, Quantity, UnitPrice) " +
                                   "VALUES (?, ?, ?, ?)";
            
            try (PreparedStatement ps = conn.prepareStatement(insertDetailSql)) {
                for (CartItem item : items) {
                    if (item.getProduct() != null) {
                        ps.setInt(1, orderId);
                        ps.setInt(2, item.getProductID());
                        ps.setDouble(3, item.getQuantity());
                        ps.setDouble(4, item.getProduct().getPrice());
                        ps.addBatch();
                    }
                }
                ps.executeBatch();
            }
        } else {
            // Order creation failed, rollback transaction
            conn.rollback();
            return orderId;
        }
        
        // Commit transaction if all operations succeeded
        conn.commit();
    } catch (SQLException e) {
        try {
            // Rollback transaction on error
            conn.rollback();
        } catch (SQLException rollbackEx) {
            rollbackEx.printStackTrace();
        }
        e.printStackTrace();
    } finally {
        try {
            // Reset auto-commit
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    return orderId;
}

/**
 * Creates a new order with a single item
 * @param order The order to create
 * @param item The cart item to add to the order
 * @return The ID of the created order, or -1 if creation failed
 */
public int createOrder(Order order, CartItem item) {
    int orderId = -1;
    
    try {
        // Set autocommit to false for transaction
        conn.setAutoCommit(false);
        
        // Insert order
        String insertOrderSql = "INSERT INTO [Order] (AccountID, OrderDate, TotalAmount, ShippingAddress, " +
                               "ShippingPhone, PaymentMethod, PaymentStatus, OrderStatus, Notes) " +
                               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(insertOrderSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, order.getAccountID());
            ps.setTimestamp(2, new java.sql.Timestamp(order.getOrderDate().getTime()));
            ps.setDouble(3, order.getTotalAmount());
            ps.setString(4, order.getShippingAddress());
            ps.setString(5, order.getShippingPhone());
            ps.setString(6, order.getPaymentMethod());
            ps.setString(7, order.getPaymentStatus());
            ps.setString(8, order.getOrderStatus());
            ps.setString(9, order.getNotes());
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    orderId = rs.getInt(1);
                }
                rs.close();
            }
        }
        
        // If order creation succeeded, insert order detail
        if (orderId > 0) {
            // Get product price
            ProductDAO productDAO = new ProductDAO();
            Product product = productDAO.getProductById(item.getProductID());
            
            if (product != null) {
                String insertDetailSql = "INSERT INTO OrderDetail (OrderID, ProductID, Quantity, UnitPrice) " +
                                        "VALUES (?, ?, ?, ?)";
                
                try (PreparedStatement ps = conn.prepareStatement(insertDetailSql)) {
                    ps.setInt(1, orderId);
                    ps.setInt(2, item.getProductID());
                    ps.setDouble(3, item.getQuantity());
                    ps.setDouble(4, item.getProduct() != null ? item.getProduct().getPrice() : product.getPrice());
                    
                    ps.executeUpdate();
                }
            } else {
                // Product not found, rollback transaction
                conn.rollback();
                orderId = -1;
                return orderId;
            }
        }
        
        // Commit transaction if all operations succeeded
        conn.commit();
    } catch (SQLException e) {
        try {
            // Rollback transaction on error
            conn.rollback();
        } catch (SQLException rollbackEx) {
            rollbackEx.printStackTrace();
        }
        e.printStackTrace();
    } finally {
        try {
            // Reset auto-commit
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    return orderId;
}

/**
 * Record voucher usage for an order
 * @param voucherId The voucher ID
 * @param accountId The account ID
 * @param orderId The order ID
 * @param discountAmount The discount amount applied
 */
public void recordVoucherUsage(int voucherId, int accountId, int orderId, double discountAmount) {
    String insertVoucherUsageSql = "INSERT INTO VoucherUsage (VoucherID, AccountID, OrderID, UsedDate, DiscountAmount) " +
                                 "VALUES (?, ?, ?, GETDATE(), ?)";
    
    String updateVoucherSql = "UPDATE Voucher SET UsageCount = UsageCount + 1 WHERE VoucherID = ?";
    
    try {
        // Insert voucher usage
        try (PreparedStatement ps = conn.prepareStatement(insertVoucherUsageSql)) {
            ps.setInt(1, voucherId);
            ps.setInt(2, accountId);
            ps.setInt(3, orderId);
            ps.setDouble(4, discountAmount);
            ps.executeUpdate();
        }
        
        // Update voucher usage count
        try (PreparedStatement ps = conn.prepareStatement(updateVoucherSql)) {
            ps.setInt(1, voucherId);
            ps.executeUpdate();
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


}
