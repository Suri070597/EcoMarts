package dao;

import model.Account;
import db.DBContext1;
import db.MD5Util;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountDAO1 {
    private final DBContext1 dbContext = new DBContext1();

    public int insertAccount(Account account) throws SQLException {
        String sql = "INSERT INTO Account (Username, [Password], Email, FullName, Phone, [Address], Gender, [Role], [Status]) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, account.getUsername());
            stmt.setString(2, account.getRole() == 0 ? MD5Util.hash(account.getPassword()) : account.getPassword()); // MD5 cho khách hàng, plain text cho admin/staff
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

    public Account checkLogin(String email, String password) throws SQLException {
        String sql = "SELECT AccountID, Username, [Password], Email, FullName, Phone, [Address], Gender, [Role], [Status] FROM Account WHERE Email = ? AND [Status] = 'Active'";
        try (Connection conn = dbContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Account account = new Account();
                    account.setAccountID(rs.getInt("AccountID"));
                    account.setUsername(rs.getString("Username"));
                    account.setPassword(rs.getString("Password"));
                    account.setEmail(rs.getString("Email"));
                    account.setFullName(rs.getString("FullName"));
                    account.setPhone(rs.getString("Phone"));
                    account.setAddress(rs.getString("Address"));
                    account.setGender(rs.getString("Gender"));
                    account.setRole(rs.getInt("Role"));
                    account.setStatus(rs.getString("Status"));

                    System.out.println("Found account: email=" + email + ", role=" + account.getRole() + ", dbPassword=" + account.getPassword());

                    // Kiểm tra mật khẩu theo Role
                    if (account.getRole() == 1 || account.getRole() == 2) {
                        // Admin hoặc Staff: So sánh plain text
                        if (password.trim().equals(account.getPassword().trim())) {
                            System.out.println((account.getRole() == 1 ? "Admin" : "Staff") + " login successful: email=" + email);
                            return account;
                        } else {
                            System.out.println((account.getRole() == 1 ? "Admin" : "Staff") + " password mismatch: input=" + password + ", db=" + account.getPassword());
                        }
                    } else if (account.getRole() == 0) {
                        // Khách hàng: So sánh MD5
                        String hashedPassword = MD5Util.hash(password);
                        if (hashedPassword.equals(account.getPassword())) {
                            System.out.println("Customer login successful: email=" + email);
                            return account;
                        } else {
                            System.out.println("Customer password mismatch: inputHash=" + hashedPassword + ", db=" + account.getPassword());
                        }
                    }
                    System.out.println("Invalid password for email=" + email + ", role=" + account.getRole());
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
}