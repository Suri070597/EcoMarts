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
import model.Manufacturer;
import db.DBContext;
import java.sql.Connection;

/**
 *
 * @author HuuDuc
 */
public class ManufacturerDAO extends DBContext {

    public List<Manufacturer> getAllManufacturers() {
        List<Manufacturer> list = new ArrayList<>();
        String sql = "SELECT * FROM Manufacturer ORDER BY ManufacturerID";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Manufacturer manufacturer = mapResultSetToManufacturer(rs);
                list.add(manufacturer);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error in getAllManufacturers: " + e.getMessage());
        }
        return list;
    }

    public Manufacturer getManufacturerById(int manufacturerId) {
        String sql = "SELECT * FROM Manufacturer WHERE ManufacturerID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, manufacturerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Manufacturer manufacturer = mapResultSetToManufacturer(rs);
                rs.close();
                ps.close();
                return manufacturer;
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error in getManufacturerById: " + e.getMessage());
        }
        return null;
    }

    public Manufacturer getManufacturerByEmail(String email) {
        String sql = "SELECT * FROM Manufacturer WHERE Email = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Manufacturer manufacturer = mapResultSetToManufacturer(rs);
                rs.close();
                ps.close();
                return manufacturer;
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error in getManufacturerByEmail: " + e.getMessage());
        }
        return null;
    }

    public boolean insert(Manufacturer manufacturer) {
        String sql = "INSERT INTO Manufacturer (BrandName, CompanyName, [Address], Email, Phone, [Status]) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, manufacturer.getBrandName());
            ps.setString(2, manufacturer.getCompanyName());
            ps.setString(3, manufacturer.getAddress());
            ps.setString(4, manufacturer.getEmail());
            ps.setString(5, manufacturer.getPhone());
            ps.setInt(6, manufacturer.getStatus());

            int rows = ps.executeUpdate();
            ps.close();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Insert failed: " + e.getMessage());
            return false;
        }
    }

    /** Kiểm tra số điện thoại đã tồn tại (dùng cho create) */
    public boolean isPhoneExists(String phone) {
        String sql = "SELECT 1 FROM Manufacturer WHERE Phone = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error in isPhoneExists: " + e.getMessage());
            return false;
        }
    }

    /** Kiểm tra số điện thoại đã tồn tại cho bản ghi khác (dùng cho edit) */
    public boolean isPhoneExistsForOther(String phone, int excludeManufacturerId) {
        String sql = "SELECT 1 FROM Manufacturer WHERE Phone = ? AND ManufacturerID <> ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            ps.setInt(2, excludeManufacturerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.out.println("Error in isPhoneExistsForOther: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Manufacturer manufacturer) {
        String sql = "UPDATE Manufacturer SET BrandName = ?, CompanyName = ?, [Address] = ?, Email = ?, Phone = ?, [Status] = ? WHERE ManufacturerID = ?";
        try (Connection conn = getConnection(); // Hàm lấy kết nối database
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, manufacturer.getBrandName());
            stmt.setString(2, manufacturer.getCompanyName());
            stmt.setString(3, manufacturer.getAddress());
            stmt.setString(4, manufacturer.getEmail());
            stmt.setString(5, manufacturer.getPhone());
            stmt.setInt(6, manufacturer.getStatus());
            stmt.setInt(7, manufacturer.getManufacturerID());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateManufacturerStatus(int manufacturerId, int status) {
        String sql = "UPDATE Manufacturer SET Status = ? WHERE ManufacturerID = ?";
        try (Connection conn = getConnection(); // Hàm getConnection() của bạn
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, status);
            stmt.setInt(2, manufacturerId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteManufacturer(int manufacturerId) {
        String sql = "DELETE FROM Manufacturer WHERE ManufacturerID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, manufacturerId);
            int result = ps.executeUpdate();
            ps.close();
            return result > 0;
        } catch (SQLException e) {
            System.out.println("Delete failed: " + e.getMessage());
            return false;
        }
    }

    public List<Manufacturer> searchManufacturers(String keyword) {
        List<Manufacturer> list = new ArrayList<>();
        String sql = "SELECT * FROM Manufacturer WHERE BrandName LIKE ? OR CompanyName LIKE ? OR Email LIKE ? OR Phone LIKE ? ORDER BY ManufacturerID";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ps.setString(4, searchPattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Manufacturer manufacturer = mapResultSetToManufacturer(rs);
                list.add(manufacturer);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error in searchManufacturers: " + e.getMessage());
        }
        return list;
    }

    public List<Manufacturer> getManufacturersByStatus(int status) {
        List<Manufacturer> list = new ArrayList<>();
        String sql = "SELECT * FROM Manufacturer WHERE [Status] = ? ORDER BY ManufacturerID";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Manufacturer manufacturer = mapResultSetToManufacturer(rs);
                list.add(manufacturer);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error in getManufacturersByStatus: " + e.getMessage());
        }
        return list;
    }

    public int countManufacturers() {
        String sql = "SELECT COUNT(*) FROM Manufacturer";
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

    public int countManufacturersByStatus(int status) {
        String sql = "SELECT COUNT(*) FROM Manufacturer WHERE [Status] = ?";
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

    private Manufacturer mapResultSetToManufacturer(ResultSet rs) throws SQLException {
        Manufacturer manufacturer = new Manufacturer();
        manufacturer.setManufacturerID(rs.getInt("ManufacturerID"));
        manufacturer.setBrandName(rs.getString("BrandName"));
        manufacturer.setCompanyName(rs.getString("CompanyName"));
        manufacturer.setAddress(rs.getString("Address"));
        manufacturer.setEmail(rs.getString("Email"));
        manufacturer.setPhone(rs.getString("Phone"));
        manufacturer.setStatus(rs.getInt("Status"));
        return manufacturer;
    }
}
