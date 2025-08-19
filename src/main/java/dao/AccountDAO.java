package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.DBContext;
import model.Account;
import model.AccountManager;

/**
 *
 * @author Nguyễn Thị Kim Soàn - CE180197
 */
public class AccountDAO extends DBContext {

    public List<AccountManager> getAllAccounts() {
        List<AccountManager> list = new ArrayList<>();
        String sql = "SELECT * FROM Account ORDER BY AccountID";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int AccountID = rs.getInt("AccountID");
                String username = rs.getString("Username");
                String password = rs.getString("Password");
                String email = rs.getString("Email");
                String phone = rs.getString("Phone");
                int role = rs.getInt("Role");
                String status = rs.getString("Status");
                list.add(new AccountManager(AccountID, username, password, email, phone, role, status));
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            System.err.println("Error in getAllAccounts: " + e.getMessage());
        }
        return list;
    }

    public List<Account> getAllAccountsFull() {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM Account ORDER BY AccountID";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Account account = mapResultSetToAccount(rs);
                list.add(account);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            System.err.println("Error in getAllAccountsFull: " + e.getMessage());
        }
        return list;
    }

    public AccountManager getAccountById(int accountId) {
        String sql = "SELECT * FROM Account WHERE AccountID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, accountId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                AccountManager account = new AccountManager(
                        rs.getInt("AccountID"),
                        rs.getString("Username"),
                        rs.getString("Password"),
                        rs.getString("Email"),
                        rs.getString("Phone"),
                        rs.getInt("Role"),
                        rs.getString("Status"));
                rs.close();
                ps.close();
                return account;
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    public Account getFullAccountById(int accountId) {
        String sql = "SELECT * FROM Account WHERE AccountID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, accountId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Account account = mapResultSetToAccount(rs);
                rs.close();
                ps.close();
                return account;
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    public Account getAccountByUsername(String username) {
        String sql = "SELECT * FROM Account WHERE Username = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Account account = mapResultSetToAccount(rs);
                rs.close();
                ps.close();
                return account;
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    public Account getAccountByEmail(String email) {
        String sql = "SELECT * FROM Account WHERE Email = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Account account = mapResultSetToAccount(rs);
                rs.close();
                ps.close();
                return account;
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }
    
    public Account getAccountByPhone(String phone) {
        String sql = "SELECT * FROM Account WHERE phone = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Account account = mapResultSetToAccount(rs);
                rs.close();
                ps.close();
                return account;
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        return null;
    }

    public boolean insert(String username, String password, String email, String fullName, String phone,
            String address, String gender, int role, String status) {
        String sql = "INSERT INTO Account (Username, Password, Email, FullName, Phone, Address, Gender, Role, Status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, email);
            ps.setString(4, fullName);
            ps.setString(5, phone);
            ps.setString(6, address);
            ps.setString(7, gender);
            ps.setInt(8, role);
            ps.setString(9, status);

            int rows = ps.executeUpdate();
            ps.close();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Insert failed: " + e.getMessage());
            return false;
        }
    }

    public boolean insertFullAccount(Account account) {
        String sql = "INSERT INTO Account (Username, Password, Email, FullName, Phone, Address, Gender, Role, Status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPassword());
            ps.setString(3, account.getEmail());
            ps.setString(4, account.getFullName());
            ps.setString(5, account.getPhone());
            ps.setString(6, account.getAddress());
            ps.setString(7, account.getGender());
            ps.setInt(8, account.getRole());
            ps.setString(9, account.getStatus());

            int rows = ps.executeUpdate();
            ps.close();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Insert failed: " + e.getMessage());
            return false;
        }
    }

    public boolean updateFullAccount(Account account) {
        String sql = "UPDATE Account SET Username = ?, Password = ?, Email = ?, FullName = ?, Phone = ?, "
                + "Address = ?, Gender = ?, Role = ?, Status = ? "
                + "WHERE AccountID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, account.getUsername());
            ps.setString(2, account.getPassword());
            ps.setString(3, account.getEmail());
            ps.setString(4, account.getFullName());
            ps.setString(5, account.getPhone());
            ps.setString(6, account.getAddress());
            ps.setString(7, account.getGender());
            ps.setInt(8, account.getRole());
            ps.setString(9, account.getStatus());
            ps.setInt(10, account.getAccountID());

            int rows = ps.executeUpdate();
            ps.close();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Update failed: " + e.getMessage());
            return false;
        }
    }

    public boolean updateAccountStatus(int accountId, String status) {
        String sql = "UPDATE Account SET Status = ? WHERE AccountID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, accountId);

            int rows = ps.executeUpdate();
            ps.close();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Update status failed: " + e.getMessage());
            return false;
        }
    }

    public boolean updatePassword(int accountId, String newPassword) {
        String sql = "UPDATE Account SET Password = ? WHERE AccountID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newPassword);
            ps.setInt(2, accountId);

            int rows = ps.executeUpdate();
            ps.close();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Update password failed: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteAccount(int accountId) {
        String sql = "DELETE FROM Account WHERE AccountID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, accountId);
            int result = ps.executeUpdate();
            ps.close();
            return result > 0;
        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
    }

    public List<Account> searchAccounts(String keyword) {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM Account WHERE Username LIKE ? OR Email LIKE ? OR FullName LIKE ? OR Phone LIKE ? ORDER BY AccountID";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ps.setString(4, searchPattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Account account = mapResultSetToAccount(rs);
                list.add(account);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            System.err.println("Error in searchAccounts: " + e.getMessage());
        }
        return list;
    }

    public List<Account> getAccountsByRole(int role) {
        List<Account> list = new ArrayList<>();
        String sql = "SELECT * FROM Account WHERE Role = ? ORDER BY AccountID";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, role);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Account account = mapResultSetToAccount(rs);
                list.add(account);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            System.err.println("Error in getAccountsByRole: " + e.getMessage());
        }
        return list;
    }

    public int countAccounts() {
        String sql = "SELECT COUNT(*) FROM Account WHERE Role IN (0, 1)";
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

    public int countAccountsByRole(int role) {
        String sql = "SELECT COUNT(*) FROM Account WHERE Role = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, role);
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

    private Account mapResultSetToAccount(ResultSet rs) throws SQLException {
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
        return account;
    }

    public boolean updateBasicInfo(int accountId, String fullName, String phone, String address, String gender) {
        String sql = "UPDATE Account SET FullName = ?, Phone = ?, Address = ?, Gender = ? WHERE AccountID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, fullName);
            ps.setString(2, phone);
            ps.setString(3, address);
            ps.setString(4, gender);
            ps.setInt(5, accountId);
            int rows = ps.executeUpdate();
            ps.close();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Update basic info failed: " + e.getMessage());
            return false;
        }
    }
// HuuDuc đã thêm 2 public này 

    public boolean isUsernameExists(String username) {
        String sql = "SELECT 1 FROM Account WHERE Username = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            boolean exists = rs.next();
            rs.close();
            return exists;
        } catch (SQLException e) {
            System.out.println("Check username exists failed: " + e.getMessage());
            return false; // Có thể đổi thành throw exception nếu cần
        }
    }

    public boolean isEmailExists(String email) {
        String sql = "SELECT 1 FROM Account WHERE Email = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            boolean exists = rs.next();
            rs.close();
            return exists;
        } catch (SQLException e) {
            System.out.println("Check email exists failed: " + e.getMessage());
            return false;
        }
    }
    
    public boolean isPhoneExists(String phone) {
        String sql = "SELECT 1 FROM Account WHERE Phone = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();
            boolean exists = rs.next();
            rs.close();
            return exists;
        } catch (SQLException e) {
            System.out.println("Check phone exists failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Get detailed account info by account ID
     *
     * @param accountId The account ID
     * @return The Account object or null if not found
     */
    public Account getUserDetail(int accountId) {
        return getFullAccountById(accountId);
    }
    
     public int getAccountIdByUsername(String username) {
        String sql = "SELECT AccountID FROM Account WHERE Username = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int accountId = rs.getInt("AccountID");
                rs.close();
                ps.close();
                return accountId;
            }
            rs.close();
            ps.close();
            return -1;
        } catch (SQLException e) {
            System.out.println("Get AccountID by Username failed: " + e.getMessage());
            return -1;
        }
    }
}
