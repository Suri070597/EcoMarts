package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.DBContext;
import model.Account;
import model.Staff;

/**
 * Staff DAO class for database operations
 */
public class StaffDAO extends DBContext {

    public List<Staff> getAllStaff() {
        List<Staff> list = new ArrayList<>();
        String sql = "SELECT s.*, a.Username, a.Password, a.Role FROM Staff s " +
                    "JOIN Account a ON s.AccountID = a.AccountID " +
                    "ORDER BY s.StaffID";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Staff staff = mapResultSetToStaff(rs);
                Account account = new Account();
                account.setAccountID(rs.getInt("AccountID"));
                account.setUsername(rs.getString("Username"));
                account.setPassword(rs.getString("Password"));
                account.setRole(rs.getInt("Role"));
                staff.setAccount(account);
                list.add(staff);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error in getAllStaff: " + e.getMessage());
        }
        return list;
    }

    public Staff getStaffById(int staffId) {
        String sql = "SELECT s.*, a.Username, a.Password, a.Role FROM Staff s " +
                    "JOIN Account a ON s.AccountID = a.AccountID " +
                    "WHERE s.StaffID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, staffId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Staff staff = mapResultSetToStaff(rs);
                Account account = new Account();
                account.setAccountID(rs.getInt("AccountID"));
                account.setUsername(rs.getString("Username"));
                account.setPassword(rs.getString("Password"));
                account.setRole(rs.getInt("Role"));
                staff.setAccount(account);
                rs.close();
                ps.close();
                return staff;
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error in getStaffById: " + e.getMessage());
        }
        return null;
    }

    public Staff getStaffByAccountId(int accountId) {
        String sql = "SELECT s.*, a.Username, a.Password, a.Role FROM Staff s " +
                    "JOIN Account a ON s.AccountID = a.AccountID " +
                    "WHERE s.AccountID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, accountId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Staff staff = mapResultSetToStaff(rs);
                Account account = new Account();
                account.setAccountID(rs.getInt("AccountID"));
                account.setUsername(rs.getString("Username"));
                account.setPassword(rs.getString("Password"));
                account.setRole(rs.getInt("Role"));
                staff.setAccount(account);
                rs.close();
                ps.close();
                return staff;
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error in getStaffByAccountId: " + e.getMessage());
        }
        return null;
    }

    public boolean insertStaff(Staff staff) {
        String sql = "INSERT INTO Staff (AccountID, FullName, Email, Phone, Gender, [Address], [Status]) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            System.out.println("=== INSERT STAFF DEBUG ===");
            System.out.println("AccountID: " + staff.getAccountID());
            System.out.println("FullName: " + staff.getFullName());
            System.out.println("Email: " + staff.getEmail());
            System.out.println("Phone: " + staff.getPhone());
            System.out.println("Gender: " + staff.getGender());
            System.out.println("Address: " + staff.getAddress());
            System.out.println("Status: " + staff.getStatus());
            
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, staff.getAccountID());
            ps.setString(2, staff.getFullName());
            ps.setString(3, staff.getEmail());
            ps.setString(4, staff.getPhone());
            ps.setString(5, staff.getGender());
            ps.setString(6, staff.getAddress());
            ps.setString(7, staff.getStatus());

            int rows = ps.executeUpdate();
            ps.close();
            System.out.println("Insert result: " + rows + " rows affected");
            System.out.println("=== END INSERT STAFF DEBUG ===");
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Insert staff failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateStaff(Staff staff) {
        String sql = "UPDATE Staff SET FullName = ?, Email = ?, Phone = ?, Gender = ?, " +
                    "[Address] = ?, [Status] = ? WHERE StaffID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, staff.getFullName());
            ps.setString(2, staff.getEmail());
            ps.setString(3, staff.getPhone());
            ps.setString(4, staff.getGender());
            ps.setString(5, staff.getAddress());
            ps.setString(6, staff.getStatus());
            ps.setInt(7, staff.getStaffID());

            int rows = ps.executeUpdate();
            ps.close();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Update staff failed: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteStaff(int staffId) {
        String sql = "DELETE FROM Staff WHERE StaffID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, staffId);

            int rows = ps.executeUpdate();
            ps.close();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Delete staff failed: " + e.getMessage());
            return false;
        }
    }

    public boolean updateStaffStatus(int staffId, String status) {
        String sql = "UPDATE Staff SET [Status] = ? WHERE StaffID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, staffId);

            int rows = ps.executeUpdate();
            ps.close();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Update staff status failed: " + e.getMessage());
            return false;
        }
    }

    public List<Staff> searchStaff(String keyword) {
        List<Staff> list = new ArrayList<>();
        String sql = "SELECT s.*, a.Username, a.Password, a.Role FROM Staff s " +
                    "JOIN Account a ON s.AccountID = a.AccountID " +
                    "WHERE s.FullName LIKE ? OR s.Email LIKE ? OR s.Phone LIKE ? " +
                    "OR a.Username LIKE ? ORDER BY s.StaffID";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ps.setString(4, searchPattern);
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Staff staff = mapResultSetToStaff(rs);
                Account account = new Account();
                account.setAccountID(rs.getInt("AccountID"));
                account.setUsername(rs.getString("Username"));
                account.setPassword(rs.getString("Password"));
                account.setRole(rs.getInt("Role"));
                staff.setAccount(account);
                list.add(staff);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error in searchStaff: " + e.getMessage());
        }
        return list;
    }

    public int countStaff() {
        String sql = "SELECT COUNT(*) FROM Staff";
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
            System.err.println("Error in countStaff: " + e.getMessage());
        }
        return 0;
    }

    public int countStaffByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM Staff WHERE [Status] = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, status);
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
            System.err.println("Error in countStaffByStatus: " + e.getMessage());
        }
        return 0;
    }

    private Staff mapResultSetToStaff(ResultSet rs) throws SQLException {
        Staff staff = new Staff();
        staff.setStaffID(rs.getInt("StaffID"));
        staff.setAccountID(rs.getInt("AccountID"));
        staff.setFullName(rs.getString("FullName"));
        staff.setEmail(rs.getString("Email"));
        staff.setPhone(rs.getString("Phone"));
        staff.setGender(rs.getString("Gender"));
        staff.setAddress(rs.getString("Address"));
        staff.setStatus(rs.getString("Status"));
        return staff;
    }

    /**
     * Delete a staff record and its associated account
     */
    public boolean deleteStaffCompletely(int staffId) {
        try {
            // First, get the staff record to find the associated AccountID
            Staff staff = getStaffById(staffId);
            if (staff == null) {
                System.err.println("Staff with ID " + staffId + " not found");
                return false;
            }
            
            int accountId = staff.getAccountID();
            
            // Delete the account first (which should cascade to delete the staff if ON DELETE CASCADE is set)
            AccountDAO accountDAO = new AccountDAO();
            boolean accountDeleted = accountDAO.deleteAccount(accountId);
            if (!accountDeleted) {
                System.err.println("Failed to delete account with ID " + accountId);
                return false;
            }
            
            return true;
        } catch (Exception e) {
            System.err.println("Error in deleteStaffCompletely for StaffID " + staffId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
} 