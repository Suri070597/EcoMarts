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
import java.sql.Connection;
import javax.sound.midi.Receiver;

/**
 *
 * @author HuuDuc
 */
public class SupplierDAO extends DBContext {


    public boolean updateSupplierStatus(int supplierId, int status) {
        String sql = "UPDATE Supplier SET Status = ? WHERE SupplierID = ?";
        try (Connection conn = getConnection(); // Hàm getConnection() của bạn
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, status);
            stmt.setInt(2, supplierId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteSupplier(int supplierId) {
        String sql = "DELETE FROM Supplier WHERE SupplierID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, supplierId);
            int result = ps.executeUpdate();
            ps.close();
            return result > 0;
        } catch (SQLException e) {
            System.out.println("Delete failed: " + e.getMessage());
            return false;
        }
    }


    public int countSuppliers() {
        String sql = "SELECT COUNT(*) FROM Supplier";
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
            System.out.println("Count failed: " + e.getMessage());
        }
        return 0;
    }

    public int countSuppliersByStatus(int status) {
        String sql = "SELECT COUNT(*) FROM Supplier WHERE [Status] = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, status);
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
            System.out.println("Count by status failed: " + e.getMessage());
        }
        return 0;
    }
    
}
