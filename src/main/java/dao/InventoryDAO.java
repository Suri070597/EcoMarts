/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.DBContext;
import model.Inventory;
/**
 *
 * @author ADMIN
 */
public class InventoryDAO extends DBContext {
    public List<Inventory> getAllInventory() throws SQLException {
        List<Inventory> list = new ArrayList<>();
        String sql = "SELECT i.InventoryID, i.ProductID, i.Quantity, i.LastUpdated, p.ProductName " +
                     "FROM Inventory i " +
                     "JOIN Product p ON i.ProductID = p.ProductID " +
                     "ORDER BY i.LastUpdated DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Inventory inv = new Inventory();
                inv.setInventoryID(rs.getInt("InventoryID"));
                inv.setProductID(rs.getInt("ProductID"));
                inv.setProductName(rs.getString("ProductName"));
                inv.setQuantity(rs.getDouble("Quantity"));

                list.add(inv);
            }
        }
        return list;
    }

    /**
     * Get current inventory quantity by product and package selection
     */
    public double getQuantityByPackage(int productId, String packageType, Integer packSize) {
        String sql = "SELECT Quantity FROM Inventory WHERE ProductID = ? AND PackageType = ? AND PackSize = ?";
        int effectivePack = packSize != null ? packSize : 0;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.setString(2, packageType);
            ps.setInt(3, effectivePack);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("Quantity");
            }
        } catch (SQLException e) {
            // log
        }
        return 0.0;
    }

    /**
     * Adjust inventory by delta (negative to deduct, positive to add) for given product/package
     */
    public void adjustInventoryQuantity(int productId, String packageType, Integer packSize, double delta) {
        String updateSql = "UPDATE Inventory SET Quantity = CASE WHEN Quantity + ? < 0 THEN 0 ELSE Quantity + ? END, LastUpdated = GETDATE() WHERE ProductID = ? AND PackageType = ? AND PackSize = ?";
        String insertSql = "INSERT INTO Inventory (ProductID, PackageType, Quantity, PackSize) VALUES (?, ?, ?, ?)";
        int effectivePack = packSize != null ? packSize : 0;

        try (PreparedStatement ups = conn.prepareStatement(updateSql)) {
            ups.setDouble(1, delta);
            ups.setDouble(2, delta);
            ups.setInt(3, productId);
            ups.setString(4, packageType);
            ups.setInt(5, effectivePack);
            int affected = ups.executeUpdate();
            if (affected == 0 && delta > 0) {
                try (PreparedStatement ins = conn.prepareStatement(insertSql)) {
                    ins.setInt(1, productId);
                    ins.setString(2, packageType);
                    ins.setDouble(3, delta);
                    ins.setInt(4, effectivePack);
                    ins.executeUpdate();
                }
            }
        } catch (SQLException e) {
            // log
        }
    }
}
