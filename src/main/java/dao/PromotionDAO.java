package dao;

import db.DBContext;
import model.Promotion;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class PromotionDAO extends DBContext {

    public List<Promotion> getAllPromotions() {
        List<Promotion> list = new ArrayList<>();
        String sql = "SELECT * FROM Promotion ORDER BY PromotionID";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Promotion p = mapResultSetToPromotion(rs);
                list.add(p);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            System.err.println("Error in getAllPromotions: " + e.getMessage());
        }
        return list;
    }

    public Promotion getPromotionById(int promotionId) {
        String sql = "SELECT * FROM Promotion WHERE PromotionID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, promotionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Promotion p = mapResultSetToPromotion(rs);
                rs.close();
                ps.close();
                return p;
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.out.println("Error in getPromotionById: " + e.getMessage());
        }
        return null;
    }

    public boolean insertPromotion(Promotion p) {
        String sql = "INSERT INTO Promotion (PromotionName, Description, DiscountPercent, StartDate, EndDate, IsActive) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, p.getPromotionName());
            ps.setString(2, p.getDescription());
            ps.setDouble(3, p.getDiscountPercent());
            ps.setTimestamp(4, p.getStartDate());
            ps.setTimestamp(5, p.getEndDate());
            ps.setInt(6, p.isActive() ? 1 : 0);

            int rows = ps.executeUpdate();
            ps.close();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Insert failed: " + e.getMessage());
            return false;
        }
    }

    public boolean updatePromotion(Promotion p) {
        String sql = "UPDATE Promotion SET PromotionName = ?, Description = ?, DiscountPercent = ?, StartDate = ?, EndDate = ?, IsActive = ? "
                + "WHERE PromotionID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, p.getPromotionName());
            ps.setString(2, p.getDescription());
            ps.setDouble(3, p.getDiscountPercent());
            ps.setTimestamp(4, p.getStartDate());
            ps.setTimestamp(5, p.getEndDate());
            ps.setInt(6, p.isActive() ? 1 : 0);
            ps.setInt(7, p.getPromotionID());

            int rows = ps.executeUpdate();
            ps.close();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Update failed: " + e.getMessage());
            return false;
        }
    }

    public boolean deletePromotion(int promotionId) {
        String sql = "DELETE FROM Promotion WHERE PromotionID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, promotionId);
            int result = ps.executeUpdate();
            ps.close();
            return result > 0;
        } catch (SQLException e) {
            System.out.println("Delete failed: " + e.getMessage());
            return false;
        }
    }

    public List<Promotion> searchPromotions(String keyword) {
        List<Promotion> list = new ArrayList<>();
        String sql = "SELECT * FROM Promotion WHERE PromotionName LIKE ? OR Description LIKE ? ORDER BY PromotionID";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Promotion p = mapResultSetToPromotion(rs);
                list.add(p);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            System.err.println("Error in searchPromotions: " + e.getMessage());
        }
        return list;
    }

    public int countPromotions() {
        String sql = "SELECT COUNT(*) FROM Promotion";
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
            System.out.println("Error in countPromotions: " + e.getMessage());
        }
        return 0;
    }

    public boolean updatePromotionStatus(int promotionId, boolean isActive) {
        String sql = "UPDATE Promotion SET IsActive = ? WHERE PromotionID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, isActive ? 1 : 0);
            ps.setInt(2, promotionId);

            int rows = ps.executeUpdate();
            ps.close();
            return rows > 0;
        } catch (SQLException e) {
            System.out.println("Update status failed: " + e.getMessage());
            return false;
        }
    }

    public void updateProductAssignments(int promotionID, String[] productIDs) {
        String deleteSQL = "DELETE FROM Product_Promotion WHERE PromotionID = ?";
        String insertSQL = "INSERT INTO Product_Promotion (ProductID, PromotionID) VALUES (?, ?)";

        try {
            conn.setAutoCommit(false);

            // Xóa hết các gán cũ của promotion này
            try (PreparedStatement delStmt = conn.prepareStatement(deleteSQL)) {
                delStmt.setInt(1, promotionID);
                delStmt.executeUpdate();
            }

            // Thêm lại các sản phẩm được chọn
            if (productIDs != null && productIDs.length > 0) {
                try (PreparedStatement insStmt = conn.prepareStatement(insertSQL)) {
                    for (String pidStr : productIDs) {
                        int productID = Integer.parseInt(pidStr);
                        insStmt.setInt(1, productID);
                        insStmt.setInt(2, promotionID);
                        insStmt.addBatch();
                    }
                    insStmt.executeBatch();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            System.err.println("Error in updateProductAssignments: " + e.getMessage());
            try {
                conn.rollback();
            } catch (SQLException ex) {
                System.err.println("Rollback failed: " + ex.getMessage());
            }
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Could not reset autocommit: " + e.getMessage());
            }
        }
    }

    public List<Integer> getAssignedProductIDsByPromotion(int promotionID) {
        List<Integer> assignedIds = new ArrayList<>();
        String sql = "SELECT ProductID FROM Product_Promotion WHERE PromotionID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, promotionID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                assignedIds.add(rs.getInt("ProductID"));
            }
        } catch (Exception e) {
            System.err.println("Error in getAssignedProductIDsByPromotion: " + e.getMessage());
        }

        return assignedIds;
    }

    public List<Promotion> getPromotionsByProductID(int productID) {
        List<Promotion> list = new ArrayList<>();
        String sql = """
        SELECT pr.*
        FROM Promotion pr
        JOIN Product_Promotion pp ON pr.PromotionID = pp.PromotionID
        WHERE pp.ProductID = ?
        ORDER BY pr.EndDate DESC
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Promotion p = new Promotion();
                p.setPromotionID(rs.getInt("PromotionID"));
                p.setPromotionName(rs.getString("PromotionName"));
                p.setDescription(rs.getString("Description"));
                p.setDiscountPercent(rs.getDouble("DiscountPercent"));
                p.setStartDate(rs.getTimestamp("StartDate"));
                p.setEndDate(rs.getTimestamp("EndDate"));
                p.setActive(rs.getBoolean("IsActive"));
                list.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error in getPromotionsByProductID: " + e.getMessage());
        }
        return list;
    }

    
public Promotion getActivePromotionForProduct(int productId) {
    String sql = """
        SELECT p.*
        FROM Promotion p
        JOIN Product_Promotion pp ON p.PromotionID = pp.PromotionID
        WHERE pp.ProductID = ? 
          AND p.IsActive = 1
          AND p.StartDate <= GETDATE()
          AND p.EndDate >= GETDATE()
        ORDER BY p.EndDate ASC
    """;

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, productId);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return mapResultSetToPromotion(rs);
            }
        }
    } catch (SQLException e) {
        System.err.println("Error in getActivePromotionForProduct: " + e.getMessage());
    }

    return null;
}



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
}
