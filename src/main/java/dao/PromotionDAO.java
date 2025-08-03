package dao;

import db.DBContext;
import model.Promotion;

import java.sql.*;
import java.util.*;

public class PromotionDAO extends DBContext {

    // --- Utility mapping ---
    private Promotion mapResultSetToPromotion(ResultSet rs) throws SQLException {
        Promotion p = new Promotion();
        p.setPromotionID(rs.getInt("PromotionID"));
        p.setPromotionName(rs.getString("PromotionName"));
        p.setDescription(rs.getString("Description"));
        p.setDiscountPercent(rs.getDouble("DiscountPercent"));
        p.setStartDate(rs.getTimestamp("StartDate"));
        p.setEndDate(rs.getTimestamp("EndDate"));
        p.setActive(rs.getBoolean("IsActive"));
        return p;
    }

    private List<Promotion> getPromotionsFromQuery(String sql, Object... params) {
        List<Promotion> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToPromotion(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // --- CRUD ---
    public List<Promotion> getAllPromotions() {
        return getPromotionsFromQuery("SELECT * FROM Promotion ORDER BY PromotionID");
    }

    public Promotion getPromotionById(int id) {
        List<Promotion> result = getPromotionsFromQuery("SELECT * FROM Promotion WHERE PromotionID = ?", id);
        return result.isEmpty() ? null : result.get(0);
    }

    public boolean insertPromotion(Promotion p) {
        String sql = "INSERT INTO Promotion (PromotionName, Description, DiscountPercent, StartDate, EndDate, IsActive) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getPromotionName());
            ps.setString(2, p.getDescription());
            ps.setDouble(3, p.getDiscountPercent());
            ps.setTimestamp(4, p.getStartDate());
            ps.setTimestamp(5, p.getEndDate());
            ps.setBoolean(6, p.isActive());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePromotion(Promotion p) {
        String sql = "UPDATE Promotion SET PromotionName = ?, Description = ?, DiscountPercent = ?, StartDate = ?, EndDate = ?, IsActive = ? WHERE PromotionID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getPromotionName());
            ps.setString(2, p.getDescription());
            ps.setDouble(3, p.getDiscountPercent());
            ps.setTimestamp(4, p.getStartDate());
            ps.setTimestamp(5, p.getEndDate());
            ps.setBoolean(6, p.isActive());
            ps.setInt(7, p.getPromotionID());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePromotion(int id) {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Promotion WHERE PromotionID = ?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Promotion> searchPromotions(String keyword) {
        return getPromotionsFromQuery("SELECT * FROM Promotion WHERE PromotionName LIKE ? OR Description LIKE ? ORDER BY PromotionID", "%" + keyword + "%", "%" + keyword + "%");
    }

    public int countPromotions() {
        try (PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM Promotion")) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean updatePromotionStatus(int id, boolean active) {
        try (PreparedStatement ps = conn.prepareStatement("UPDATE Promotion SET IsActive = ? WHERE PromotionID = ?")) {
            ps.setBoolean(1, active);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- Product-Promotion Assignments ---
    public List<Integer> updateProductAssignments(int promotionID, String[] productIDs) {
        List<Integer> failed = new ArrayList<>();
        String deleteOldSQL = "DELETE FROM Product_Promotion WHERE ProductID = ?";
        String insertSQL = "INSERT INTO Product_Promotion (ProductID, PromotionID) VALUES (?, ?)";
        if (productIDs == null || productIDs.length == 0) {
            return failed;
        }

        try {
            conn.setAutoCommit(false);
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteOldSQL); PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {

                for (String pidStr : productIDs) {
                    int productID = Integer.parseInt(pidStr);

                    deleteStmt.setInt(1, productID);
                    deleteStmt.executeUpdate();

                    insertStmt.setInt(1, productID);
                    insertStmt.setInt(2, promotionID);
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
                conn.commit();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return failed;
    }

    public List<Integer> getAssignedProductIDsByPromotion(int promotionID) {
        List<Integer> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement("SELECT ProductID FROM Product_Promotion WHERE PromotionID = ?")) {
            ps.setInt(1, promotionID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(rs.getInt("ProductID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Promotion getValidPromotionForProduct(int productID) {
        String sql = """
            SELECT TOP 1 pr.* FROM Promotion pr
            JOIN Product_Promotion pp ON pr.PromotionID = pp.PromotionID
            WHERE pp.ProductID = ? AND pr.IsActive = 1
              AND pr.StartDate <= GETDATE() AND pr.EndDate >= GETDATE()
            ORDER BY pr.EndDate DESC
        """;
        List<Promotion> result = getPromotionsFromQuery(sql, productID);
        return result.isEmpty() ? null : result.get(0);
    }

    public List<Object[]> getProductPromotionInfoExcept(int currentPromotionID) {
        List<Object[]> result = new ArrayList<>();
        String sql = """
            SELECT pp.ProductID, pr.PromotionName
            FROM Product_Promotion pp
            JOIN Promotion pr ON pp.PromotionID = pr.PromotionID
            WHERE pr.IsActive = 1 AND pr.EndDate > GETDATE()
              AND pr.PromotionID != ?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, currentPromotionID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(new Object[]{rs.getInt("ProductID"), rs.getString("PromotionName")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public void clearProductAssignments(int promotionID) {
        String sql = "DELETE FROM Product_Promotion WHERE PromotionID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, promotionID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
