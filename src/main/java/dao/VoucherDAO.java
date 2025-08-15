package dao;

import db.DBContext;
import model.Account;
import model.Voucher;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VoucherDAO extends DBContext {

    // =========================
    // ENUM
    // =========================
    public enum AssignResult {
        SUCCESS, USER_NOT_FOUND, DUPLICATE, ERROR
    }

    // =========================
    // SQL 
    // =========================
    private static final String SQL_VOUCHER_SELECT_ALL
            = "SELECT * FROM Voucher ORDER BY VoucherID";

    private static final String SQL_VOUCHER_SELECT_BY_ID
            = "SELECT * FROM Voucher WHERE VoucherID = ?";

    private static final String SQL_VOUCHER_SELECT_BY_CODE
            = "SELECT * FROM Voucher WHERE VoucherCode = ?";

    private static final String SQL_VOUCHER_INSERT
            = "INSERT INTO Voucher (VoucherCode, Description, DiscountAmount, MinOrderValue, MaxUsage, UsageCount, StartDate, EndDate, IsActive, CategoryID) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_VOUCHER_UPDATE
            = "UPDATE Voucher SET VoucherCode = ?, Description = ?, DiscountAmount = ?, MinOrderValue = ?, MaxUsage = ?, UsageCount = ?, StartDate = ?, EndDate = ?, IsActive = ?, CategoryID = ? "
            + "WHERE VoucherID = ?";

    private static final String SQL_VOUCHER_DELETE
            = "DELETE FROM Voucher WHERE VoucherID = ?";

    private static final String SQL_VOUCHER_SEARCH
            = "SELECT * FROM Voucher WHERE VoucherCode LIKE ? OR Description LIKE ? ORDER BY VoucherID";

    private static final String SQL_VOUCHER_COUNT
            = "SELECT COUNT(*) FROM Voucher";

    private static final String SQL_VOUCHER_UPDATE_STATUS
            = "UPDATE Voucher SET IsActive = ? WHERE VoucherID = ?";

    private static final String SQL_VOUCHER_BY_ACCOUNT
            = "SELECT v.* FROM Voucher v "
            + "JOIN AccountVoucher av ON v.VoucherID = av.VoucherID "
            + "WHERE av.AccountID = ?";

    private static final String SQL_ACCOUNT_BY_VOUCHER
            = "SELECT a.AccountID, a.FullName, a.Email, a.Phone, av.DateAssigned "
            + "FROM AccountVoucher av "
            + "JOIN Account a ON a.AccountID = av.AccountID "
            + "WHERE av.VoucherID = ? "
            + "ORDER BY av.DateAssigned DESC";

    private static final String SQL_CHECK_ACCOUNT_EXISTS
            = "SELECT 1 FROM Account WHERE AccountID = ?";

    private static final String SQL_CHECK_AV_DUPLICATE
            = "SELECT 1 FROM AccountVoucher WHERE VoucherID = ? AND AccountID = ?";

    // Nếu AccountVoucherID là IDENTITY & DateAssigned có DEFAULT GETDATE():
    private static final String SQL_INSERT_ACCOUNT_VOUCHER
            = "INSERT INTO AccountVoucher (AccountID, VoucherID) VALUES (?, ?)";

    // LOGIC VOUCHER
    // =========================
    private static final String SQL_CAN_USE_VOUCHER
            = """
    SELECT 1
    FROM Voucher v
    JOIN AccountVoucher av
      ON av.VoucherID = v.VoucherID
     AND av.AccountID = ?
   WHERE v.VoucherID = ?
     AND v.IsActive = 1
     AND v.StartDate <= GETDATE() AND v.EndDate >= GETDATE()
     AND v.UsageCount < v.MaxUsage
     AND av.IsUsed = 0
              """;

    private static final String SQL_MARK_USED
            = """
    UPDATE AccountVoucher
       SET IsUsed = 1, UsedAt = GETDATE()
     WHERE AccountID = ? AND VoucherID = ? AND IsUsed = 0
""";

    private static final String SQL_INCR_USAGE
            = """
    UPDATE Voucher
       SET UsageCount = UsageCount + 1
     WHERE VoucherID = ?
       AND IsActive = 1
       AND StartDate <= GETDATE() AND EndDate >= GETDATE()
       AND UsageCount < MaxUsage
""";

    // =========================
    // CRUD VOUCHER
    // =========================
    public List<Voucher> getAllVouchers() {
        List<Voucher> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SQL_VOUCHER_SELECT_ALL); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapVoucher(rs));
            }
        } catch (SQLException e) {
            logErr("getAllVouchers", e);
        }
        return list;
    }

    public Voucher getVoucherById(int voucherId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_VOUCHER_SELECT_BY_ID)) {
            ps.setInt(1, voucherId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapVoucher(rs);
                }
            }
        } catch (SQLException e) {
            logErr("getVoucherById", e);
        }
        return null;
    }

    public Voucher getVoucherByCode(String voucherCode) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_VOUCHER_SELECT_BY_CODE)) {
            ps.setString(1, voucherCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapVoucher(rs);
                }
            }
        } catch (SQLException e) {
            logErr("getVoucherByCode", e);
        }
        return null;
    }

    public boolean insertVoucher(Voucher v) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_VOUCHER_INSERT)) {
            ps.setString(1, v.getVoucherCode());
            ps.setString(2, v.getDescription());
            ps.setDouble(3, v.getDiscountAmount());
            ps.setDouble(4, v.getMinOrderValue());
            ps.setInt(5, v.getMaxUsage());
            ps.setInt(6, v.getUsageCount());
            ps.setTimestamp(7, v.getStartDate());
            ps.setTimestamp(8, v.getEndDate());
            ps.setBoolean(9, v.isActive());
            if (v.getCategoryID() != null) {
                ps.setInt(10, v.getCategoryID());
            } else {
                ps.setNull(10, Types.INTEGER);
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logErr("insertVoucher", e);
            return false;
        }
    }

    public boolean updateVoucher(Voucher v) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_VOUCHER_UPDATE)) {
            ps.setString(1, v.getVoucherCode());
            ps.setString(2, v.getDescription());
            ps.setDouble(3, v.getDiscountAmount());
            ps.setDouble(4, v.getMinOrderValue());
            ps.setInt(5, v.getMaxUsage());
            ps.setInt(6, v.getUsageCount());
            ps.setTimestamp(7, v.getStartDate());
            ps.setTimestamp(8, v.getEndDate());
            ps.setBoolean(9, v.isActive());
            if (v.getCategoryID() != null) {
                ps.setInt(10, v.getCategoryID());
            } else {
                ps.setNull(10, Types.INTEGER);
            }
            ps.setInt(11, v.getVoucherID());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logErr("updateVoucher", e);
            return false;
        }
    }

    public boolean deleteVoucher(int voucherId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_VOUCHER_DELETE)) {
            ps.setInt(1, voucherId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logErr("deleteVoucher", e);
            return false;
        }
    }

    public List<Voucher> searchVouchers(String keyword) {
        List<Voucher> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SQL_VOUCHER_SEARCH)) {
            String like = "%" + keyword + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapVoucher(rs));
                }
            }
        } catch (SQLException e) {
            logErr("searchVouchers", e);
        }
        return list;
    }

    public int countVouchers() {
        try (PreparedStatement ps = conn.prepareStatement(SQL_VOUCHER_COUNT); ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            logErr("countVouchers", e);
            return 0;
        }
    }

    public boolean updateVoucherStatus(int voucherId, boolean isActive) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_VOUCHER_UPDATE_STATUS)) {
            // BIT(0/1) nên setInt cho chắc chắn
            ps.setInt(1, isActive ? 1 : 0);
            ps.setInt(2, voucherId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logErr("updateVoucherStatus", e);
            return false;
        }
    }

    // =========================
    // LIÊN QUAN ACCOUNT-VOUCHER
    // =========================
    public List<Voucher> getVouchersByAccountId(int accountId) {
        List<Voucher> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SQL_VOUCHER_BY_ACCOUNT)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapVoucher(rs));
                }
            }
        } catch (SQLException e) {
            logErr("getVouchersByAccountId", e);
        }
        return list;
    }

    public List<Account> getAcountByVoucherId(int voucherId) {
        List<Account> owners = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SQL_ACCOUNT_BY_VOUCHER)) {
            ps.setInt(1, voucherId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    owners.add(mapAccountBasic(rs));
                }
            }
        } catch (SQLException e) {
            logErr("getOwnersByVoucherId", e);
        }
        return owners;
    }

    /**
     * Gán voucher cho user theo AccountID, tránh trùng, yêu cầu
     * AccountVoucherID là IDENTITY và DateAssigned có DEFAULT(GETDATE())
     */
    public AssignResult assignVoucherToAccountId(int voucherId, int accountId) {
        // 1) Kiểm tra account tồn tại
        try (PreparedStatement ps = conn.prepareStatement(SQL_CHECK_ACCOUNT_EXISTS)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return AssignResult.USER_NOT_FOUND;
                }
            }
        } catch (SQLException e) {
            logErr("assignVoucherToAccountId:checkUser", e);
            return AssignResult.ERROR;
        }

        // 2) Chống trùng
        try (PreparedStatement ps = conn.prepareStatement(SQL_CHECK_AV_DUPLICATE)) {
            ps.setInt(1, voucherId);
            ps.setInt(2, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return AssignResult.DUPLICATE;
                }
            }
        } catch (SQLException e) {
            logErr("assignVoucherToAccountId:checkDup", e);
            return AssignResult.ERROR;
        }

        // 3) Insert
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT_ACCOUNT_VOUCHER)) {
            ps.setInt(1, accountId);
            ps.setInt(2, voucherId);
            int n = ps.executeUpdate();
            return n > 0 ? AssignResult.SUCCESS : AssignResult.ERROR;
        } catch (SQLException e) {
            logErr("assignVoucherToAccountId:insert", e);
            return AssignResult.ERROR;
        }
    }

    /**
     * Có thể dùng không? (voucher còn hạn, còn lượt, và user chưa dùng)
     *
     * @param accountId
     * @param voucherId
     * @return
     */
    public boolean canUseVoucher(int accountId, int voucherId) {
        try (PreparedStatement ps = conn.prepareStatement(SQL_CAN_USE_VOUCHER)) {
            ps.setInt(1, accountId);
            ps.setInt(2, voucherId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logErr("canUseVoucher", e);
            return false;
        }
    }

    /**
     * Đánh dấu đã dùng đúng 1 lần + tăng UsageCount (atomic)
     *
     * @param accountId
     * @param voucherId
     * @return
     */
    public boolean redeemOnce(int accountId, int voucherId) {
        boolean ok = false;
        try {
            conn.setAutoCommit(false);

            // 1) Đánh dấu đã dùng (chỉ khi hiện tại IsUsed = 0)
            int a;
            try (PreparedStatement ps = conn.prepareStatement(SQL_MARK_USED)) {
                ps.setInt(1, accountId);
                ps.setInt(2, voucherId);
                a = ps.executeUpdate();
            }
            if (a == 0) { // đã dùng rồi hoặc chưa được cấp phát
                conn.rollback();
                return false;
            }

            // 2) Tăng UsageCount của voucher (còn hạn & còn lượt)
            int b;
            try (PreparedStatement ps = conn.prepareStatement(SQL_INCR_USAGE)) {
                ps.setInt(1, voucherId);
                b = ps.executeUpdate();
            }
            if (b == 0) { // hết hạn/hết lượt/không tồn tại
                conn.rollback();
                return false;
            }

            conn.commit();
            ok = true;
        } catch (SQLException e) {
            logErr("redeemOnce", e);
            try {
                conn.rollback();
            } catch (SQLException ignore) {
            }
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ignore) {
            }
        }
        return ok;
    }

    // =========================
    // MAPPERS
    // =========================
    private Voucher mapVoucher(ResultSet rs) throws SQLException {
        Voucher v = new Voucher();
        v.setVoucherID(rs.getInt("VoucherID"));
        v.setVoucherCode(rs.getString("VoucherCode"));
        v.setDescription(rs.getString("Description"));
        v.setDiscountAmount(rs.getDouble("DiscountAmount"));
        v.setMinOrderValue(rs.getDouble("MinOrderValue"));
        v.setMaxUsage(rs.getInt("MaxUsage"));
        v.setUsageCount(rs.getInt("UsageCount"));
        v.setStartDate(rs.getTimestamp("StartDate"));
        v.setEndDate(rs.getTimestamp("EndDate"));
        v.setActive(rs.getBoolean("IsActive"));
        v.setCategoryID(rs.getObject("CategoryID") != null ? rs.getInt("CategoryID") : null);
        return v;
    }

    private Account mapAccountBasic(ResultSet rs) throws SQLException {
        Account a = new Account();
        a.setAccountID(rs.getInt("AccountID"));
        a.setFullName(rs.getString("FullName"));
        a.setEmail(rs.getString("Email"));
        a.setPhone(rs.getString("Phone"));
        return a;
    }

    // =========================
    // LOG HELPER
    // =========================
    private void logErr(String where, Exception e) {
        System.err.println("[VoucherDAO][" + where + "] " + e.getMessage());
    }
}
