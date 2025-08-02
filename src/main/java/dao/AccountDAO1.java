package dao;

import model.Account;
import db.DBContext1;
import db.MD5Util;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author HuuDuc
 */
public class AccountDAO1 {

    private final DBContext1 dbContext = new DBContext1();
// Trong class AccountDAO1
    private static AccountDAO1 instance = null;

    public static AccountDAO1 getInstance() {
        if (instance == null) {
            instance = new AccountDAO1();
        }
        return instance;
    }

    public int insertAccount(Account account) throws SQLException {
        String sql = "INSERT INTO Account (Username, [Password], Email, FullName, Phone, [Address], Gender, [Role], [Status]) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, account.getUsername());
            // Sửa đoạn set password cho hợp lý với Google Login
            stmt.setString(2,
                    (account.getPassword() == null || account.getPassword().equals("") || account.getPassword().equals("GOOGLE_LOGIN"))
                    ? account.getPassword()
                    : (account.getRole() == 0 ? MD5Util.hash(account.getPassword()) : account.getPassword())
            );
            stmt.setString(3, account.getEmail());
            stmt.setString(4, account.getFullName());
            stmt.setString(5, account.getPhone());
            stmt.setString(6, account.getAddress());
            stmt.setString(7, account.getGender());
            stmt.setInt(8, account.getRole());
            stmt.setString(9, account.getStatus());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public Account checkLogin(String email, String hashedPassword) throws SQLException {
        String sql = "SELECT AccountID, Username, [Password], Email, FullName, Phone, [Address], Gender, [Role], [Status] FROM Account WHERE Email = ? AND [Status] = 'Active'";
        try (Connection conn = dbContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String dbPassword = rs.getString("Password");
                    if (hashedPassword.equals(dbPassword)) {
                        Account account = new Account();
                        account.setAccountID(rs.getInt("AccountID"));
                        account.setUsername(rs.getString("Username"));
                        account.setPassword(dbPassword);
                        account.setEmail(rs.getString("Email"));
                        account.setFullName(rs.getString("FullName"));
                        account.setPhone(rs.getString("Phone"));
                        account.setAddress(rs.getString("Address"));
                        account.setGender(rs.getString("Gender"));
                        account.setRole(rs.getInt("Role"));
                        account.setStatus(rs.getString("Status"));
                        return account;
                    } else {
                        System.out.println("Password mismatch: inputHash=" + hashedPassword + ", db=" + dbPassword);
                    }
                } else {
                    System.out.println("No active account found for email=" + email);
                }
            }
        } catch (SQLException e) {
            System.out.println("Database error during login: " + e.getMessage());
            throw e;
        }
        return null;
    }

    public boolean checkEmailExists(String email) throws SQLException {
        String sql = "SELECT 1 FROM Account WHERE Email = ?";
        try (Connection conn = dbContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean checkUsernameExists(String username) throws SQLException {
        String sql = "SELECT 1 FROM Account WHERE Username = ?";
        try (Connection conn = dbContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void updateAccountStatus(int accountId, String status) throws SQLException {
        String sql = "UPDATE Account SET [Status] = ? WHERE AccountID = ?";
        try (Connection conn = dbContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, accountId);
            stmt.executeUpdate();
            System.out.println("Updated account status to " + status + " for AccountID=" + accountId);
        }
    }

    // Lấy Account theo email
    public Account getAccountByEmail(Connection conn, String email) {
        String sql = "SELECT * FROM Account WHERE Email = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Account(
                        rs.getInt("AccountID"),
                        rs.getString("Username"),
                        rs.getString("Password"),
                        rs.getString("Email"),
                        rs.getString("FullName"),
                        rs.getString("Phone"),
                        rs.getString("Address"),
                        rs.getString("Gender"),
                        rs.getInt("Role"),
                        rs.getString("Status")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

// Cập nhật mật khẩu mới
    public void updatePassword(Connection conn, int accountId, String newPassword) {
        String sql = "UPDATE Account SET Password=? WHERE AccountID=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setInt(2, accountId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
// Cập nhật bổ sung thông tin cho tài khoản đã có bằng email (dùng cho Google Login)

    public boolean updateAccountByEmail(Account acc) throws SQLException {
        String sql = "UPDATE Account SET Username=?, FullName=?, Phone=?, [Address]=?, Gender=?, [Role]=?, [Status]=? WHERE Email=?";
        try (Connection conn = dbContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, acc.getUsername());
            ps.setString(2, acc.getFullName());
            ps.setString(3, acc.getPhone());
            ps.setString(4, acc.getAddress());
            ps.setString(5, acc.getGender());
            ps.setInt(6, acc.getRole());
            ps.setString(7, acc.getStatus());
            ps.setString(8, acc.getEmail());
            return ps.executeUpdate() > 0;
        }
    }
// Overload: Lấy account theo email, tự tạo connection, KHÔNG cần truyền connection vào

    public Account getAccountByEmail(String email) {
        String sql = "SELECT * FROM Account WHERE Email = ?";
        try (Connection conn = dbContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Account(
                        rs.getInt("AccountID"),
                        rs.getString("Username"),
                        rs.getString("Password"),
                        rs.getString("Email"),
                        rs.getString("FullName"),
                        rs.getString("Phone"),
                        rs.getString("Address"),
                        rs.getString("Gender"),
                        rs.getInt("Role"),
                        rs.getString("Status")
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
