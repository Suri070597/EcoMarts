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
import model.Supplier;
import db.DBContext;
import java.sql.Connection;

/**
 *
 * @author HuuDuc
 */
public class SupplierDAO extends DBContext {

    public List<Supplier> getAllSuppliers() {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT * FROM Supplier ORDER BY SupplierID";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Supplier supplier = mapResultSetToSupplier(rs);
                list.add(supplier);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error in getAllSuppliers: " + e.getMessage());
        }
        return list;
    }

    public Supplier getSupplierById(int supplierId) {
        String sql = "SELECT * FROM Supplier WHERE SupplierID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, supplierId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Supplier supplier = mapResultSetToSupplier(rs);
                rs.close();
                ps.close();
                return supplier;
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error in getSupplierById: " + e.getMessage());
        }
        return null;
    }

    public Supplier getSupplierByEmail(String email) {
        String sql = "SELECT * FROM Supplier WHERE Email = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Supplier supplier = mapResultSetToSupplier(rs);
                rs.close();
                ps.close();
                return supplier;
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error in getSupplierByEmail: " + e.getMessage());
        }
        return null;
    }

    public boolean insert(Supplier supplier) {
        String sql = "INSERT INTO Supplier (BrandName, CompanyName, [Address], Email, Phone, [Status]) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, supplier.getBrandName());
            ps.setString(2, supplier.getCompanyName());
            ps.setString(3, supplier.getAddress());
            ps.setString(4, supplier.getEmail());
            ps.setString(5, supplier.getPhone());
            ps.setInt(6, supplier.getStatus());

            int rows = ps.executeUpdate();
            ps.close();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Insert failed: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Supplier supplier) {
        String sql = "UPDATE Supplier SET BrandName = ?, CompanyName = ?, [Address] = ?, Email = ?, Phone = ?, [Status] = ? WHERE SupplierID = ?";
        try (Connection conn = getConnection(); // Hàm lấy kết nối database
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, supplier.getBrandName());
            stmt.setString(2, supplier.getCompanyName());
            stmt.setString(3, supplier.getAddress());
            stmt.setString(4, supplier.getEmail());
            stmt.setString(5, supplier.getPhone());
            stmt.setInt(6, supplier.getStatus());
            stmt.setInt(7, supplier.getSupplierID());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

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

    public List<Supplier> searchSuppliers(String keyword) {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT * FROM Supplier WHERE BrandName LIKE ? OR CompanyName LIKE ? OR Email LIKE ? OR Phone LIKE ? ORDER BY SupplierID";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ps.setString(4, searchPattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Supplier supplier = mapResultSetToSupplier(rs);
                list.add(supplier);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error in searchSuppliers: " + e.getMessage());
        }
        return list;
    }

    public List<Supplier> getSuppliersByStatus(int status) {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT * FROM Supplier WHERE [Status] = ? ORDER BY SupplierID";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Supplier supplier = mapResultSetToSupplier(rs);
                list.add(supplier);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error in getSuppliersByStatus: " + e.getMessage());
        }
        return list;
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

    private Supplier mapResultSetToSupplier(ResultSet rs) throws SQLException {
        Supplier supplier = new Supplier();
        supplier.setSupplierID(rs.getInt("SupplierID"));
        supplier.setBrandName(rs.getString("BrandName"));
        supplier.setCompanyName(rs.getString("CompanyName"));
        supplier.setAddress(rs.getString("Address"));
        supplier.setEmail(rs.getString("Email"));
        supplier.setPhone(rs.getString("Phone"));
        supplier.setStatus(rs.getInt("Status"));
        return supplier;
    }
}
