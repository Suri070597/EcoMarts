/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import model.Token;
import db.DBContext1;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class TokenDAO {
    private final DBContext1 dbContext = new DBContext1();

    public void insertToken(Token token) throws SQLException {
        String sql = "INSERT INTO Token_Table (AccountID, Token, [Status], Time_Add, Time_Exp) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dbContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, token.getAccountId());
            stmt.setString(2, token.getToken());
            stmt.setString(3, token.getStatus());
            stmt.setTimestamp(4, token.getTimeAdd());
            stmt.setTimestamp(5, token.getTimeExp());
            stmt.executeUpdate();
            System.out.println("Inserted token for AccountID=" + token.getAccountId() + ", Token=" + token.getToken());
        }
    }

    public Token getValidToken(String token, int accountId) throws SQLException {
        String sql = "SELECT TokenID, AccountID, Token, [Status], Time_Add, Time_Exp FROM Token_Table WHERE Token = ? AND AccountID = ? AND [Status] = 'unused' AND Time_Exp > GETDATE()";
        try (Connection conn = dbContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token.trim());
            stmt.setInt(2, accountId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Token t = new Token();
                    t.setTokenId(rs.getInt("TokenID"));
                    t.setAccountId(rs.getInt("AccountID"));
                    t.setToken(rs.getString("Token"));
                    t.setStatus(rs.getString("Status"));
                    t.setTimeAdd(rs.getTimestamp("Time_Add"));
                    t.setTimeExp(rs.getTimestamp("Time_Exp"));
                    System.out.println("Found valid token for AccountID=" + accountId + ", Token=" + token);
                    return t;
                } else {
                    System.out.println("No valid token found for AccountID=" + accountId + ", Token=" + token);
                }
            }
        }
        return null;
    }

    public void updateTokenStatus(int tokenId, String status) throws SQLException {
        String sql = "UPDATE Token_Table SET [Status] = ? WHERE TokenID = ?";
        try (Connection conn = dbContext.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, tokenId);
            stmt.executeUpdate();
            System.out.println("Updated token status to " + status + " for TokenID=" + tokenId);
        }
    }
}