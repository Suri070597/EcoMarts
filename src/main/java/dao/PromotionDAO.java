package dao;

import db.DBContext;
import model.Category;
import model.Promotion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PromotionDAO - Phiên bản đồng bộ với schema:
 *  - Bảng Promotion có cột CategoryID (FK -> Category.CategoryID).
 *  - Cột scope trong Promotion là "applyScope" (chữ a thường).
 *  - Dùng alias ổn định để đọc ResultSet: PromoType, ApplyScope, CatID, CatName.
 */
public class PromotionDAO extends DBContext {

    // ======================
    // Row mapper
    // ======================
    private Promotion mapRow(ResultSet rs) throws SQLException {
        Promotion p = new Promotion();
        p.setPromotionID(rs.getInt("PromotionID"));
        p.setPromotionName(rs.getString("PromotionName"));
        p.setDescription(rs.getString("Description"));
        p.setDiscountPercent(rs.getDouble("DiscountPercent"));
        p.setStartDate(rs.getTimestamp("StartDate"));
        p.setEndDate(rs.getTimestamp("EndDate"));
        p.setActive(rs.getBoolean("IsActive"));

        // Đọc bằng nhãn alias ổn định
        p.setPromoType(rs.getInt("PromoType"));
        p.setApplyScope(rs.getInt("ApplyScope"));

        // Map Category (nếu có CatID)
        Category c = null;
        int catId;
        try {
            catId = rs.getInt("CatID");
            if (!rs.wasNull()) {
                c = new Category();
                c.setCategoryID(catId);
                try { c.setCategoryName(rs.getString("CatName")); } catch (SQLException ignore) {}
            }
        } catch (SQLException ignore) {
            // SELECT có thể không alias CatID/CatName (trường hợp không JOIN)
        }
        p.setCategory(c);

        return p;
    }

    // ======================
    // Queries
    // ======================

    /** Lấy toàn bộ promotions, order mới nhất */
    public List<Promotion> getAllPromotions() {
        String sql = """
            SELECT 
              p.PromotionID,
              p.PromotionName,
              p.Description,
              p.DiscountPercent,
              p.StartDate,
              p.EndDate,
              p.IsActive,
              p.PromoType       AS PromoType,
              p.applyScope      AS ApplyScope,
              c.CategoryID      AS CatID,
              c.CategoryName    AS CatName
            FROM Promotion p
            LEFT JOIN Category c ON p.CategoryID = c.CategoryID
            ORDER BY p.PromotionID DESC
        """;
        List<Promotion> out = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }

    /**
     * Danh sách có filter + phân trang.
     * @param q         tìm theo tên/description (LIKE)
     * @param promoType null = bỏ lọc
     * @param status    null = bỏ lọc; 0/1 -> false/true
     * @param from      lọc EndDate >= from
     * @param to        lọc StartDate <= to
     * @param page      1-based; <=0 sẽ bỏ phân trang
     * @param size      >0; <=0 sẽ bỏ phân trang
     */
    public List<Promotion> list(String q, Integer promoType, Integer status,
                                Timestamp from, Timestamp to, int page, int size) {

        StringBuilder sb = new StringBuilder("""
            SELECT 
              p.PromotionID,
              p.PromotionName,
              p.Description,
              p.DiscountPercent,
              p.StartDate,
              p.EndDate,
              p.IsActive,
              p.PromoType       AS PromoType,
              p.applyScope      AS ApplyScope,
              c.CategoryID      AS CatID,
              c.CategoryName    AS CatName
            FROM Promotion p
            LEFT JOIN Category c ON p.CategoryID = c.CategoryID
            WHERE 1=1
        """);

        List<Object> params = new ArrayList<>();

        if (q != null && !q.isBlank()) {
            sb.append(" AND (p.PromotionName LIKE ? OR p.Description LIKE ?)");
            String k = "%" + q.trim() + "%";
            params.add(k); params.add(k);
        }
        if (promoType != null) {
            sb.append(" AND p.PromoType = ?");
            params.add(promoType);
        }
        if (status != null) {
            sb.append(" AND p.IsActive = ?");
            params.add(status == 1);
        }
        if (from != null) {
            sb.append(" AND p.EndDate >= ?");
            params.add(from);
        }
        if (to != null) {
            sb.append(" AND p.StartDate <= ?");
            params.add(to);
        }

        // ORDER BY bắt buộc nếu dùng OFFSET/FETCH
        sb.append(" ORDER BY p.EndDate ASC, p.PromotionID DESC ");

        boolean usePaging = page > 0 && size > 0;
        if (usePaging) {
            sb.append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY ");
        }

        List<Promotion> out = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sb.toString())) {
            int idx = 1;
            for (Object param : params) ps.setObject(idx++, param);
            if (usePaging) {
                int offset = Math.max(0, (page - 1) * size);
                ps.setInt(idx++, offset);
                ps.setInt(idx, size);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }

    /** Lấy 1 promotion theo ID */
    public Promotion getPromotionById(int id) {
        String sql = """
            SELECT 
              p.PromotionID,
              p.PromotionName,
              p.Description,
              p.DiscountPercent,
              p.StartDate,
              p.EndDate,
              p.IsActive,
              p.PromoType       AS PromoType,
              p.applyScope      AS ApplyScope,
              c.CategoryID      AS CatID,
              c.CategoryName    AS CatName
            FROM Promotion p
            LEFT JOIN Category c ON p.CategoryID = c.CategoryID
            WHERE p.PromotionID = ?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Đếm tổng số promotion (không filter) */
    public int countPromotions() {
        String sql = "SELECT COUNT(*) FROM Promotion";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ======================
    // Mutations
    // ======================

    /** Thêm mới, trả ID sinh ra; -1 nếu thất bại */
    public int insertPromotionReturningId(Promotion p) {
        String sql = """
            INSERT INTO Promotion
              (PromotionName, Description, DiscountPercent, StartDate, EndDate,
               IsActive, PromoType, ApplyScope, CategoryID)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getPromotionName());
            ps.setString(2, p.getDescription());
            ps.setDouble(3, p.getDiscountPercent());
            ps.setTimestamp(4, p.getStartDate());
            ps.setTimestamp(5, p.getEndDate());
            ps.setBoolean(6, p.isActive());
            ps.setInt(7, p.getPromoType());
            ps.setInt(8, p.getApplyScope());

            if (p.getApplyScope() == Promotion.SCOPE_CATEGORY && p.getCategory() != null) {
                ps.setInt(9, p.getCategory().getCategoryID());
            } else {
                ps.setNull(9, Types.INTEGER);
            }

            int affected = ps.executeUpdate();
            if (affected == 0) return -1;

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /** Cập nhật theo ID; true nếu thành công */
    public boolean updatePromotion(Promotion p) {
        String sql = """
            UPDATE Promotion
               SET PromotionName=?, Description=?, DiscountPercent=?, StartDate=?, EndDate=?,
                   IsActive=?, PromoType=?, ApplyScope=?, CategoryID=?
             WHERE PromotionID=?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getPromotionName());
            ps.setString(2, p.getDescription());
            ps.setDouble(3, p.getDiscountPercent());
            ps.setTimestamp(4, p.getStartDate());
            ps.setTimestamp(5, p.getEndDate());
            ps.setBoolean(6, p.isActive());
            ps.setInt(7, p.getPromoType());
            ps.setInt(8, p.getApplyScope());
            if (p.getApplyScope() == Promotion.SCOPE_CATEGORY && p.getCategory() != null) {
                ps.setInt(9, p.getCategory().getCategoryID());
            } else {
                ps.setNull(9, Types.INTEGER);
            }
            ps.setInt(10, p.getPromotionID());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Xoá theo ID; xoá mapping sản phẩm trước nếu có */
    public boolean deletePromotion(int id) {
        clearProductAssignments(id);
        String sql = "DELETE FROM Promotion WHERE PromotionID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Bật/tắt IsActive */
    public boolean updatePromotionStatus(int id, boolean active) {
        String sql = "UPDATE Promotion SET IsActive=? WHERE PromotionID=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, active);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ======================
    // Product assignments
    // ======================

    public void clearProductAssignments(int promotionID) {
        String sql = "DELETE FROM Product_Promotion WHERE PromotionID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, promotionID);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /** Gán danh sách sản phẩm cho promotion; trả list id thất bại (nếu có) */
    public List<Integer> updateProductAssignments(int promotionID, String[] productIDs) {
        clearProductAssignments(promotionID);
        List<Integer> failed = new ArrayList<>();
        if (productIDs == null || productIDs.length == 0) return failed;

        String insertSQL = "INSERT INTO Product_Promotion (ProductID, PromotionID) VALUES (?, ?)";
        try {
            conn.setAutoCommit(false);
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSQL)) {
                for (String pidStr : productIDs) {
                    try {
                        int productID = Integer.parseInt(pidStr);
                        insertStmt.setInt(1, productID);
                        insertStmt.setInt(2, promotionID);
                        insertStmt.addBatch();
                    } catch (Exception ex) {
                        failed.add(-1);
                    }
                }
                insertStmt.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return failed;
    }

    public int countAssignedProducts(int promotionId) {
        String sql = "SELECT COUNT(*) FROM Product_Promotion WHERE PromotionID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, promotionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Lấy promotion hợp lệ (đang active & trong thời gian) áp cho 1 sản phẩm.
     * JOIN bảng mapping Product_Promotion (không cần Category ở đây).
     */
    public Promotion getValidPromotionForProduct(int productID) {
        String sql = """
            SELECT TOP 1 
              p.PromotionID,
              p.PromotionName,
              p.Description,
              p.DiscountPercent,
              p.StartDate,
              p.EndDate,
              p.IsActive,
              p.PromoType       AS PromoType,
              p.applyScope      AS ApplyScope
            FROM Promotion p
            JOIN Product_Promotion pp ON p.PromotionID = pp.PromotionID
            WHERE pp.ProductID = ?
              AND p.IsActive = 1
              AND p.StartDate <= GETDATE()
              AND p.EndDate   >= GETDATE()
            ORDER BY p.EndDate DESC
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ======================
    // Main test nhanh
    // ======================
    public static void main(String[] args) {
        try {
            PromotionDAO dao = new PromotionDAO();

            System.out.println("===== TEST list() không filter =====");
            List<Promotion> list = dao.list(null, null, null, null, null, 1, 50);
            System.out.println("Tổng số khuyến mãi lấy được = " + list.size());
            for (Promotion p : list) System.out.println(p);

            System.out.println("\n===== TEST getPromotionById(1) =====");
            Promotion one = dao.getPromotionById(1);
            System.out.println(one != null ? one : "Không tìm thấy ID=1");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
