package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import db.DBContext;
import model.Voucher;

public class VoucherDAO extends DBContext {

    public List<Voucher> getAllVouchers() {
        List<Voucher> list = new ArrayList<>();
        String sql = "SELECT * FROM Voucher ORDER BY VoucherID";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Voucher voucher = mapResultSetToVoucher(rs);
                list.add(voucher);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            System.err.println("Error in getAllVouchers: " + e.getMessage());
        }
        return list;
    }

    public Voucher getVoucherById(int voucherId) {
        String sql = "SELECT * FROM Voucher WHERE VoucherID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, voucherId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Voucher voucher = mapResultSetToVoucher(rs);
                rs.close();
                ps.close();
                return voucher;
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    public Voucher getVoucherByCode(String voucherCode) {
        String sql = "SELECT * FROM Voucher WHERE VoucherCode = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, voucherCode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Voucher voucher = mapResultSetToVoucher(rs);
                rs.close();
                ps.close();
                return voucher;
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    public boolean insertVoucher(Voucher voucher) {
        String sql = "INSERT INTO Voucher (VoucherCode, Description, DiscountAmount, MinOrderValue, MaxUsage, UsageCount, StartDate, EndDate, IsActive, CategoryID) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, voucher.getVoucherCode());
            ps.setString(2, voucher.getDescription());
            ps.setDouble(3, voucher.getDiscountAmount());
            ps.setDouble(4, voucher.getMinOrderValue());
            ps.setInt(5, voucher.getMaxUsage());
            ps.setInt(6, voucher.getUsageCount());
            ps.setTimestamp(7, voucher.getStartDate());
            ps.setTimestamp(8, voucher.getEndDate());
            ps.setBoolean(9, voucher.isActive());
            if (voucher.getCategoryID() != null) {
                ps.setInt(10, voucher.getCategoryID());
            } else {
                ps.setNull(10, Types.INTEGER);
            }

            int rows = ps.executeUpdate();
            ps.close();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Insert failed: " + e.getMessage());
            return false;
        }
    }

    public boolean updateVoucher(Voucher voucher) {
        String sql = "UPDATE Voucher SET VoucherCode = ?, Description = ?, DiscountAmount = ?, MinOrderValue = ?, MaxUsage = ?, UsageCount = ?, StartDate = ?, EndDate = ?, IsActive = ?, CategoryID = ? "
                   + "WHERE VoucherID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, voucher.getVoucherCode());
            ps.setString(2, voucher.getDescription());
            ps.setDouble(3, voucher.getDiscountAmount());
            ps.setDouble(4, voucher.getMinOrderValue());
            ps.setInt(5, voucher.getMaxUsage());
            ps.setInt(6, voucher.getUsageCount());
            ps.setTimestamp(7, voucher.getStartDate());
            ps.setTimestamp(8, voucher.getEndDate());
            ps.setBoolean(9, voucher.isActive());
            if (voucher.getCategoryID() != null) {
                ps.setInt(10, voucher.getCategoryID());
            } else {
                ps.setNull(10, Types.INTEGER);
            }
            ps.setInt(11, voucher.getVoucherID());

            int rows = ps.executeUpdate();
            ps.close();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Update failed: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteVoucher(int voucherId) {
        String sql = "DELETE FROM Voucher WHERE VoucherID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, voucherId);
            int result = ps.executeUpdate();
            ps.close();
            return result > 0;
        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
    }

    public List<Voucher> searchVouchers(String keyword) {
        List<Voucher> list = new ArrayList<>();
        String sql = "SELECT * FROM Voucher WHERE VoucherCode LIKE ? OR Description LIKE ? ORDER BY VoucherID";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Voucher voucher = mapResultSetToVoucher(rs);
                list.add(voucher);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            System.err.println("Error in searchVouchers: " + e.getMessage());
        }
        return list;
    }

    public int countVouchers() {
        String sql = "SELECT COUNT(*) FROM Voucher";
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
            System.out.println(e);
        }
        return 0;
    }

    private Voucher mapResultSetToVoucher(ResultSet rs) throws SQLException {
        Voucher voucher = new Voucher();
        voucher.setVoucherID(rs.getInt("VoucherID"));
        voucher.setVoucherCode(rs.getString("VoucherCode"));
        voucher.setDescription(rs.getString("Description"));
        voucher.setDiscountAmount(rs.getDouble("DiscountAmount"));
        voucher.setMinOrderValue(rs.getDouble("MinOrderValue"));
        voucher.setMaxUsage(rs.getInt("MaxUsage"));
        voucher.setUsageCount(rs.getInt("UsageCount"));
        voucher.setStartDate(rs.getTimestamp("StartDate"));
        voucher.setEndDate(rs.getTimestamp("EndDate"));
        voucher.setActive(rs.getBoolean("IsActive"));
        voucher.setCategoryID(rs.getObject("CategoryID") != null ? rs.getInt("CategoryID") : null);
        return voucher;
    }
    
    public boolean updateVoucherStatus(int voucherId, boolean isActive) {
    String sql = "UPDATE Voucher SET IsActive = ? WHERE VoucherID = ?";
    try {
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, isActive ? 1 : 0); // Vì cột BIT lưu 0/1
        ps.setInt(2, voucherId);

        int rows = ps.executeUpdate();
        ps.close();
        return rows > 0;
    } catch (SQLException e) {
        System.out.println("Update status failed: " + e.getMessage());
        return false;
    }
}

}
