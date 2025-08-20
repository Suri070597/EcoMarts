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
}
