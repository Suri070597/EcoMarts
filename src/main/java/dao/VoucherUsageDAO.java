package dao;

import db.DBContext;
import model.Voucher;
import model.VoucherUsage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class VoucherUsageDAO {

    public VoucherUsage getByOrderId(int orderId) {
        String sql = "SELECT vu.*, v.VoucherCode, v.Description, v.DiscountAmount AS VoucherDiscount " +
                     "FROM VoucherUsage vu " +
                     "JOIN Voucher v ON vu.VoucherID = v.VoucherID " +
                     "WHERE vu.OrderID = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, orderId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                VoucherUsage vu = new VoucherUsage();
                vu.setVoucherUsageID(rs.getInt("VoucherUsageID"));
                vu.setVoucherID(rs.getInt("VoucherID"));
                vu.setAccountID(rs.getInt("AccountID"));
                vu.setOrderID(rs.getInt("OrderID"));
                vu.setUsedDate(rs.getTimestamp("UsedDate"));
                vu.setDiscountAmount(rs.getDouble("DiscountAmount"));

                // Join thông tin Voucher
                Voucher voucher = new Voucher();
                voucher.setVoucherID(rs.getInt("VoucherID"));
                voucher.setVoucherCode(rs.getString("VoucherCode"));
                voucher.setDescription(rs.getString("Description"));
                voucher.setDiscountAmount(rs.getDouble("VoucherDiscount"));

                vu.setVoucher(voucher);
                return vu;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
