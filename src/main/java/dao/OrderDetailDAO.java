package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DBContext;
import model.CartItem;
import model.OrderDetail;
import model.Product;

public class OrderDetailDAO extends DBContext {

    public List<OrderDetail> getOrderDetailsByOrderId(int orderId) {
        List<OrderDetail> orderDetails = new ArrayList<>();
        String sql = "SELECT \n"
                + "    od.OrderDetailID,\n"
                + "    od.OrderID,\n"
                + "    od.ProductID,\n"
                + "    p.ProductName,\n"
                + "    p.Unit,\n"
                + "    od.Quantity,\n"
                + "    od.UnitPrice,\n"
                + "    od.SubTotal,\n"
                + "    od.DisplayUnitName, od.PackageType, od.PackSize,\n"
                + "    o.OrderStatus\n"
                + "FROM OrderDetail od\n"
                + "JOIN Product p ON od.ProductID = p.ProductID\n"
                + "JOIN [Order] o ON od.OrderID = o.OrderID\n"
                + "WHERE od.OrderID = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                OrderDetail orderDetail = new OrderDetail(
                        rs.getInt("OrderDetailID"),
                        rs.getInt("OrderID"),
                        rs.getInt("ProductID"),
                        rs.getDouble("Quantity"),  // Lấy là double để hỗ trợ số lượng thập phân
                        rs.getDouble("UnitPrice")
                );

                // Set thêm thông tin phụ
                orderDetail.setProductName(rs.getString("ProductName"));
                String displayUnit = null;
                try { displayUnit = rs.getString("DisplayUnitName"); } catch (Exception ignore) {}
                if (displayUnit != null && !displayUnit.trim().isEmpty()) {
                    orderDetail.setUnit(displayUnit);
                } else {
                    orderDetail.setUnit(rs.getString("Unit"));
                }
                orderDetail.setOrderStatus(rs.getString("OrderStatus"));
                orderDetail.setSubTotal(rs.getDouble("SubTotal")); // nếu không tính trong constructor

                orderDetails.add(orderDetail);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error in getOrderDetailsByOrderId: " + e.getMessage());
        }

        return orderDetails;
    }

    public boolean addOrderDetail(OrderDetail orderDetail) {
        String sql = "INSERT INTO OrderDetail (OrderID, ProductID, Quantity, UnitPrice) "
                + "VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, orderDetail.getOrderID());
            ps.setInt(2, orderDetail.getProductID());
            ps.setDouble(3, orderDetail.getQuantity());
            ps.setDouble(4, orderDetail.getUnitPrice());

            int rowsAffected = ps.executeUpdate();
            ps.close();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error in addOrderDetail: " + e.getMessage());
            return false;
        }
    }

    public boolean updateOrderDetail(OrderDetail orderDetail) {
        String sql = "UPDATE OrderDetail SET Quantity = ?, UnitPrice = ? "
                + "WHERE OrderDetailID = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setDouble(1, orderDetail.getQuantity());
            ps.setDouble(2, orderDetail.getUnitPrice());
            ps.setInt(3, orderDetail.getOrderDetailID());

            int rowsAffected = ps.executeUpdate();
            ps.close();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error in updateOrderDetail: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteOrderDetail(int orderDetailId) {
        String sql = "DELETE FROM OrderDetail WHERE OrderDetailID = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, orderDetailId);

            int rowsAffected = ps.executeUpdate();
            ps.close();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error in deleteOrderDetail: " + e.getMessage());
            return false;
        }
    }

    public Map<Integer, Integer> getProductCountByOrderIDs(List<Integer> orderIDs) {
        Map<Integer, Integer> result = new HashMap<>();
        if (orderIDs == null || orderIDs.isEmpty()) {
            return result;
        }

        StringBuilder queryBuilder = new StringBuilder(
                "SELECT OrderID, SUM(Quantity) as TotalQty FROM OrderDetail WHERE OrderID IN ("
        );
        for (int i = 0; i < orderIDs.size(); i++) {
            queryBuilder.append("?");
            if (i < orderIDs.size() - 1) {
                queryBuilder.append(",");
            }
        }
        queryBuilder.append(") GROUP BY OrderID");

        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(queryBuilder.toString())) {

            for (int i = 0; i < orderIDs.size(); i++) {
                ps.setInt(i + 1, orderIDs.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.put(rs.getInt("OrderID"), rs.getInt("TotalQty"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<CartItem> getCartItemsFromOrder(int orderId) {
        List<CartItem> items = new ArrayList<>();
        String sql = "SELECT od.ProductID, od.Quantity, p.ProductName, p.Price, p.ImageURL "
                + "FROM OrderDetail od JOIN Product p ON od.ProductID = p.ProductID "
                + "WHERE od.OrderID = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                CartItem item = new CartItem();
                item.setProductID(rs.getInt("ProductID"));
                item.setQuantity(rs.getInt("Quantity"));

                Product p = new Product();
                p.setProductID(rs.getInt("ProductID"));
                p.setProductName(rs.getString("ProductName"));
                p.setPrice(rs.getDouble("Price"));
                p.setImageURL(rs.getString("ImageURL"));

                item.setProduct(p);
                items.add(item);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return items;
    }
public int countDistinctProductsInOrder(int orderId) {
    String sql = "SELECT COUNT(DISTINCT ProductID) FROM OrderDetail WHERE OrderID = ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, orderId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return 0;
}

public String getProductNamesByOrderId(int orderId) {
    String sql = "SELECT p.ProductName FROM OrderDetail od " +
                 "JOIN Product p ON od.ProductID = p.ProductID " +
                 "WHERE od.OrderID = ?";
    
    List<String> productNames = new ArrayList<>();
    
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, orderId);
        ResultSet rs = ps.executeQuery();
        
        while (rs.next()) {
            productNames.add(rs.getString("ProductName"));
        }
        
        rs.close();
    } catch (SQLException e) {
        e.printStackTrace();
    }
    
    // Chỉ hiển thị 3 tên sản phẩm đầu tiên, nếu có nhiều hơn thì thêm "..."
    if (productNames.size() <= 3) {
        return String.join(", ", productNames);
    } else {
        return String.join(", ", productNames.subList(0, 3)) + "...";
    }
}

}
