package dao;

import db.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.OrderDetail;

public class OrderDetailDAO extends DBContext {

    public List<OrderDetail> getOrderDetailsByOrderId(int orderId) {
        List<OrderDetail> orderDetails = new ArrayList<>();
        String sql = "SELECT OrderDetailID, OrderID, ProductID, Quantity, UnitPrice " +
                "FROM OrderDetail WHERE OrderID = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                OrderDetail orderDetail = new OrderDetail(
                        rs.getInt("OrderDetailID"),
                        rs.getInt("OrderID"),
                        rs.getInt("ProductID"),
                        rs.getInt("Quantity"),
                        rs.getDouble("UnitPrice"));
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
        String sql = "INSERT INTO OrderDetail (OrderID, ProductID, Quantity, UnitPrice) " +
                "VALUES (?, ?, ?, ?)";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, orderDetail.getOrderID());
            ps.setInt(2, orderDetail.getProductID());
            ps.setInt(3, orderDetail.getQuantity());
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
        String sql = "UPDATE OrderDetail SET Quantity = ?, UnitPrice = ? " +
                "WHERE OrderDetailID = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, orderDetail.getQuantity());
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
}