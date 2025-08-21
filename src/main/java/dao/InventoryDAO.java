/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import db.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Inventory;
import model.Product;
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
}
