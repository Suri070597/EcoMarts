/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import db.DBContext1;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.Customer;

/**
 *
 * @author ADMIN
 */
public class CustomerDAO {

    private final DBContext1 dbContext = new DBContext1();

    public int insert(Customer customer) throws SQLException {
        String sql = "INSERT INTO Customer (AccountID, FullName, Email, Phone, Gender, [Address]) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dbContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, customer.getAccountID());
            ps.setString(2, customer.getFullName());
            ps.setString(3, customer.getEmail());
            ps.setString(4, customer.getPhone());
            ps.setString(5, customer.getGender());
            ps.setString(6, customer.getAddress());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public Customer getByAccountId(int accountId) throws SQLException {
        String sql = "SELECT * FROM Customer WHERE AccountID = ?";
        try (Connection conn = dbContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Customer c = new Customer();
                    c.setCustomerID(rs.getInt("CustomerID"));
                    c.setAccountID(rs.getInt("AccountID"));
                    c.setFullName(rs.getString("FullName"));
                    c.setEmail(rs.getString("Email"));
                    c.setPhone(rs.getString("Phone"));
                    c.setGender(rs.getString("Gender"));
                    c.setAddress(rs.getString("Address"));
                    return c;
                }
            }
        }
        return null;
    }

    // ĐÃ CÓ: update theo Email (giữ lại nếu nơi khác đang dùng)
    public boolean updateAccountByEmail(Customer acc) throws SQLException {
        String sql = "UPDATE Customer SET FullName=?, Phone=?, [Address]=?, Gender=? WHERE Email=?";
        try (Connection conn = dbContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, acc.getFullName());
            ps.setString(2, acc.getPhone());
            ps.setString(3, acc.getAddress());
            ps.setString(4, acc.getGender());
            ps.setString(5, acc.getEmail());
            return ps.executeUpdate() > 0;
        }
    }

    // MỚI: update theo AccountID (an toàn hơn khi Email có thể đổi)
    public boolean updateByAccountId(Customer acc) throws SQLException {
        String sql = "UPDATE Customer SET FullName=?, Email=?, Phone=?, Gender=?, [Address]=? WHERE AccountID=?";
        try (Connection conn = dbContext.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, acc.getFullName());
            ps.setString(2, acc.getEmail());
            ps.setString(3, acc.getPhone());
            ps.setString(4, acc.getGender());
            ps.setString(5, acc.getAddress());
            ps.setInt(6, acc.getAccountID());
            return ps.executeUpdate() > 0;
        }
    }

    // MỚI: upsert theo AccountID
    public boolean upsertByAccountId(Customer acc) throws SQLException {
        Customer existed = getByAccountId(acc.getAccountID());
        if (existed == null) {
            return insert(acc) > 0;
        } else {
            return updateByAccountId(acc);
        }
    }
}
