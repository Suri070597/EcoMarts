package dao;

import db.DBContext;
import model.Category;
import model.Promotion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Product;

/**
 * PromotionDAO - Phiên bản đồng bộ với schema: - Bảng Promotion có cột ParentID
 * (FK -> Category.CategoryID). - Cột scope trong Promotion là "applyScope" (chữ
 * a thường). - Dùng alias ổn định để đọc ResultSet: PromoType, ApplyScope,
 * CatID, CatName.
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
                try {
                    c.setCategoryName(rs.getString("CatName"));
                } catch (SQLException ignore) {
                }
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
    /**
     * Lấy toàn bộ promotions, order mới nhất
     */
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
            LEFT JOIN Category c ON p.ParentID = c.CategoryID
            ORDER BY p.PromotionID DESC
        """;
        List<Promotion> out = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }

    /**
     * Danh sách có filter + phân trang.
     *
     * @param q tìm theo tên/description (LIKE)
     * @param promoType null = bỏ lọc
     * @param status null = bỏ lọc; 0/1 -> false/true
     * @param from lọc EndDate >= from
     * @param to lọc StartDate <= to
     * @param page 1-based; <=0 sẽ bỏ phân trang @param size
     * >0; <=0 sẽ bỏ phân trang
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
            LEFT JOIN Category c ON p.ParentID = c.CategoryID
            WHERE 1=1
        """);

        List<Object> params = new ArrayList<>();

        if (q != null && !q.isBlank()) {
            sb.append(" AND (p.PromotionName LIKE ? OR p.Description LIKE ?)");
            String k = "%" + q.trim() + "%";
            params.add(k);
            params.add(k);
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
            for (Object param : params) {
                ps.setObject(idx++, param);
            }
            if (usePaging) {
                int offset = Math.max(0, (page - 1) * size);
                ps.setInt(idx++, offset);
                ps.setInt(idx, size);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return out;
    }

    /**
     * Lấy 1 promotion theo ID
     */
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
            LEFT JOIN Category c ON p.ParentID = c.CategoryID
            WHERE p.PromotionID = ?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Đếm tổng số promotion (không filter)
     */
    public int countPromotions() {
        String sql = "SELECT COUNT(*) FROM Promotion";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ======================
    // Mutations
    // ======================
    /**
     * Thêm mới, trả ID sinh ra; -1 nếu thất bại
     */
    public int insertPromotionReturningId(Promotion p) {
        String sql = """
            INSERT INTO Promotion
              (PromotionName, Description, DiscountPercent, StartDate, EndDate,
               IsActive, PromoType, ApplyScope, ParentID)
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
            if (affected == 0) {
                return -1;
            }

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Cập nhật theo ID; true nếu thành công
     */
    public boolean updatePromotion(Promotion p) {
        String sql = """
            UPDATE Promotion
               SET PromotionName=?, Description=?, DiscountPercent=?, StartDate=?, EndDate=?,
                   IsActive=?, PromoType=?, ApplyScope=?, ParentID=?
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

    /**
     * Xoá theo ID; xoá mapping sản phẩm trước nếu có
     */
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

    /**
     * Bật/tắt IsActive
     */
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

    /**
     * Gán danh sách sản phẩm cho promotion; trả list ProductID bị từ chối (đã
     * có promotion khác)
     */
    public List<Integer> updateProductAssignments(int promotionID, String[] productIDs) {
        List<Integer> failed = new ArrayList<>();
        if (productIDs == null || productIDs.length == 0) {
            return failed;
        }

        final String insertSQL = "INSERT INTO Product_Promotion (ProductID, PromotionID) VALUES (?, ?)";
        final String checkSQL = "SELECT TOP 1 PromotionID FROM Product_Promotion WHERE ProductID = ?";

        boolean startedTx = false;
        boolean oldAuto = true;

        try {
            oldAuto = conn.getAutoCommit();
            if (oldAuto) {                 // chỉ mở TX nếu trước đó đang autoCommit
                conn.setAutoCommit(false);
                startedTx = true;
            }

            // Xoá mapping cũ của chính promotion này TRONG CÙNG TX
            clearProductAssignments(promotionID);

            try (PreparedStatement chk = conn.prepareStatement(checkSQL); PreparedStatement ins = conn.prepareStatement(insertSQL)) {

                for (String pidStr : productIDs) {
                    try {
                        int productID = Integer.parseInt(pidStr);

                        // Kiểm tra độc quyền promotion cho product
                        chk.setInt(1, productID);
                        try (ResultSet rs = chk.executeQuery()) {
                            if (rs.next()) {
                                int existed = rs.getInt(1);
                                if (existed != promotionID) {      // đã có promotion khác
                                    failed.add(productID);          // đánh dấu từ chối
                                    continue;                       // bỏ qua insert
                                }
                            }
                        }

                        // Hợp lệ -> insert
                        ins.setInt(1, productID);
                        ins.setInt(2, promotionID);
                        ins.addBatch();

                    } catch (Exception ex) {
                        failed.add(-1); // lỗi parse / lỗi khác
                    }
                }

                ins.executeBatch();
            }

            if (startedTx) {
                conn.commit();
            }
        } catch (SQLException e) {
            if (startedTx) {
                try {
                    conn.rollback();
                } catch (SQLException ignore) {
                }
            }
            e.printStackTrace();
        } finally {
            if (startedTx) {
                try {
                    conn.setAutoCommit(oldAuto);
                } catch (SQLException ignore) {
                }
            }
        }
        return failed;
    }

    public int countAssignedProducts(int promotionId) {
        String sql = "SELECT COUNT(*) FROM Product_Promotion WHERE PromotionID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, promotionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
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
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ======================
    // Main test nhanh
    // ======================
    // ====== HẰNG SỐ TYPE ======
    public static final int TYPE_SEASONAL = 1; // Seasonal
    public static final int TYPE_FLASHSALE = 0; // Flash Sale

    // ====== SCOPE (nội bộ) ======
    private static class Scope {

        int applyScope;     // 0 = global, 1 = by-category
        Integer parentId;   // null = root-all (nếu applyScope=1) hoặc global (nếu =0), >0 = category cụ thể
    }

    private Scope getScope(int promotionId) {
        final String sql = "SELECT applyScope, ParentID FROM Promotion WHERE PromotionID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, promotionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Scope s = new Scope();
                s.applyScope = rs.getInt("applyScope");
                s.parentId = (Integer) rs.getObject("ParentID");
                return s;
            }
        } catch (SQLException e) {
            throw new RuntimeException("getScope failed: " + promotionId, e);
        }
    }

    // ====== LẤY LIST PRODUCT THEO LOGIC SCOPE (để rebuild mapping) ======
    public List<Product> listAppliedProducts(int promotionId) {
        Scope s = getScope(promotionId);
        if (s == null) {
            return new java.util.ArrayList<>();
        }

        try {
            // CASE A: Global → tất cả sản phẩm
            if (s.applyScope == 0) {
                final String sqlAll =
                        "SELECT ProductID, ProductName, PriceBox AS Price, CategoryID " +
                        "FROM Product " +
                        "ORDER BY ProductID";
                try (PreparedStatement ps = conn.prepareStatement(sqlAll); ResultSet rs = ps.executeQuery()) {
                    return mapProducts(rs);
                }
            }

            // CASE B: Scope=category nhưng ParentID = NULL → tất cả root + con cháu
            if (s.parentId == null) {
                final String sqlRoots =
                        ";WITH tree AS (" +
                        "    SELECT CategoryID " +
                        "    FROM Category " +
                        "    WHERE ParentID IS NULL " +
                        "    UNION ALL " +
                        "    SELECT c.CategoryID " +
                        "    FROM Category c " +
                        "    JOIN tree t ON c.ParentID = t.CategoryID " +
                        ") " +
                        "SELECT p.ProductID, p.ProductName, p.PriceBox AS Price, p.CategoryID " +
                        "FROM Product p " +
                        "JOIN tree t ON p.CategoryID = t.CategoryID " +
                        "ORDER BY p.ProductID";
                try (PreparedStatement ps = conn.prepareStatement(sqlRoots); ResultSet rs = ps.executeQuery()) {
                    return mapProducts(rs);
                }
            }

            // CASE C: Scope=category với ParentID cụ thể
            final String sqlTree =
                    ";WITH tree AS (" +
                    "    SELECT CategoryID FROM Category WHERE CategoryID = ? " +
                    "    UNION ALL " +
                    "    SELECT c.CategoryID " +
                    "    FROM Category c " +
                    "    JOIN tree t ON c.ParentID = t.CategoryID " +
                    ") " +
                    "SELECT p.ProductID, p.ProductName, p.PriceBox AS Price, p.CategoryID " +
                    "FROM Product p " +
                    "JOIN tree t ON p.CategoryID = t.CategoryID " +
                    "ORDER BY p.ProductID";
            try (PreparedStatement ps = conn.prepareStatement(sqlTree)) {
                ps.setInt(1, s.parentId);
                try (ResultSet rs = ps.executeQuery()) {
                    return mapProducts(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("listAppliedProducts failed: " + promotionId, e);
        }
    }

    // ====== REBUILD MAPPING CHO 1 PROMOTION ======
    public int materializeMappingsForPromotion(int promotionId) {
        List<Product> products = listAppliedProducts(promotionId);
        final String deleteSQL = "DELETE FROM Product_Promotion WHERE PromotionID = ?";
        final String insertSQL = "INSERT INTO Product_Promotion (ProductID, PromotionID) VALUES (?, ?)";

        int inserted = 0;
        boolean startedTx = false;
        boolean oldAuto = true;

        try {
            oldAuto = conn.getAutoCommit();
            if (oldAuto) {                 // chỉ mở TX nếu trước đó đang autoCommit
                conn.setAutoCommit(false);
                startedTx = true;
            }

            try (PreparedStatement del = conn.prepareStatement(deleteSQL)) {
                del.setInt(1, promotionId);
                del.executeUpdate();
            }
            try (PreparedStatement ins = conn.prepareStatement(insertSQL)) {
                for (Product p : products) {
                    ins.setInt(1, p.getProductID());
                    ins.setInt(2, promotionId);
                    ins.addBatch();
                }
                int[] res = ins.executeBatch();
                for (int r : res) {
                    inserted += (r >= 0 ? r : 1);
                }
            }

            if (startedTx) {
                conn.commit();
            }
            return inserted;
        } catch (SQLException e) {
            if (startedTx) {
                try {
                    conn.rollback();
                } catch (SQLException ignore) {
                }
            }
            throw new RuntimeException("materializeMappingsForPromotion failed: " + promotionId, e);
        } finally {
            if (startedTx) {
                try {
                    conn.setAutoCommit(oldAuto);
                } catch (SQLException ignore) {
                }
            }
        }
    }

    // ====== LẤY DANH SÁCH ID PROMOTION ĐANG HIỆU LỰC THEO TYPE ======
    public List<Integer> listActivePromotionIdsByType(int promoType) {
        final String sql =
                "SELECT p.PromotionID " +
                "FROM Promotion p " +
                "WHERE p.IsActive = 1 " +
                "  AND p.StartDate <= GETDATE() " +
                "  AND p.EndDate   >= GETDATE() " +
                "  AND p.PromoType = ?";
        List<Integer> ids = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, promoType);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("listActivePromotionIdsByType failed: " + promoType, e);
        }
        return ids;
    }

    // ====== REBUILD MAPPING CHO TẤT CẢ PROMOTION ĐANG HIỆU LỰC CỦA 1 TYPE (ATOMIC) ======
    public int rebuildMappingsForActiveType(int promoType) {
        List<Integer> promoIds = listActivePromotionIdsByType(promoType);
        if (promoIds.isEmpty()) {
            return 0;
        }

        int total = 0;
        boolean startedTx = false;
        boolean oldAuto = true;

        try {
            oldAuto = conn.getAutoCommit();
            if (oldAuto) {
                conn.setAutoCommit(false);     // mở 1 TX lớn bao toàn bộ vòng lặp
                startedTx = true;
            }

            for (Integer id : promoIds) {
                // materialize... sẽ KHÔNG tự commit nếu đang ở trong TX ngoài
                total += materializeMappingsForPromotion(id);
            }

            if (startedTx) {
                conn.commit();      // commit 1 lần cho toàn bộ
            }
            return total;
        } catch (RuntimeException e) {
            if (startedTx) {
                try {
                    conn.rollback();
                } catch (SQLException ignore) {
                }
            }
            throw e;
        } catch (SQLException e) {
            if (startedTx) {
                try {
                    conn.rollback();
                } catch (SQLException ignore) {
                }
            }
            throw new RuntimeException("rebuildMappingsForActiveType failed: " + promoType, e);
        } finally {
            if (startedTx) {
                try {
                    conn.setAutoCommit(oldAuto);
                } catch (SQLException ignore) {
                }
            }
        }
    }

    // ====== LẤY SẢN PHẨM THEO TYPE TỪ BẢNG MAPPING (CÁCH B) ======
    public List<Product> listProductsByTypeFromMapping(int promoType) {
        final String sql =
                "SELECT DISTINCT p.ProductID, p.ProductName, p.PriceBox AS Price, p.CategoryID " +
                "FROM Product p " +
                "JOIN Product_Promotion pp ON p.ProductID = pp.ProductID " +
                "JOIN Promotion pr         ON pr.PromotionID = pp.PromotionID " +
                "WHERE pr.IsActive = 1 " +
                "  AND pr.StartDate <= GETDATE() " +
                "  AND pr.EndDate   >= GETDATE() " +
                "  AND pr.PromoType = ? " +
                "ORDER BY p.ProductID";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, promoType);
            try (ResultSet rs = ps.executeQuery()) {
                return mapProducts(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("listProductsByTypeFromMapping failed: " + promoType, e);
        }
    }

    // ====== WRAPPER NGẮN ======
    public List<Product> listFlashSaleFromMapping() {
        return listProductsByTypeFromMapping(TYPE_FLASHSALE);
    }

    public List<Product> listSeasonalFromMapping() {
        return listProductsByTypeFromMapping(TYPE_SEASONAL);
    }

    // ====== MAP PRODUCT ======
    private List<Product> mapProducts(ResultSet rs) throws SQLException {
        List<Product> out = new ArrayList<>();
        while (rs.next()) {
            Product p = new Product();
            try {
                p.setProductID(rs.getInt("ProductID"));
            } catch (Exception ignored) {
            }
            try {
                p.setProductName(rs.getString("ProductName"));
            } catch (Exception ignored) {
            }
            try {
                p.setPrice(rs.getDouble("Price"));
            } catch (Exception ignored) {
            }
            try {
                p.setCategoryID(rs.getInt("CategoryID"));
            } catch (Exception ignored) {
            }
            out.add(p);
        }
        return out;
    }

    // ====== MAIN TEST NHANH (CÁCH B) ======
    public static void main(String[] args) {
        try {
            PromotionDAO dao = new PromotionDAO();

            // 1) Rebuild mapping cho các promotion đang hiệu lực theo từng type
            int flashInserted = dao.rebuildMappingsForActiveType(TYPE_FLASHSALE);
            int seasonalInserted = dao.rebuildMappingsForActiveType(TYPE_SEASONAL);
            System.out.println("[Rebuild] FlashSale inserted rows = " + flashInserted);
            System.out.println("[Rebuild] Seasonal  inserted rows = " + seasonalInserted);

            // 2) Lấy sản phẩm theo type từ bảng mapping
            List<Product> flash = dao.listFlashSaleFromMapping();
            List<Product> seas = dao.listSeasonalFromMapping();

            System.out.println("\n=== FLASH SALE products (" + flash.size() + ") ===");
            for (int i = 0; i < Math.min(20, flash.size()); i++) {
                Product p = flash.get(i);
                System.out.println("  " + p.getProductID() + " - " + p.getProductName() + " - " + p.getPrice());
            }
            if (flash.size() > 20) {
                System.out.println("  ... (" + (flash.size() - 20) + " more)");
            }

            System.out.println("\n=== SEASONAL products (" + seas.size() + ") ===");
            for (int i = 0; i < Math.min(20, seas.size()); i++) {
                Product p = seas.get(i);
                System.out.println("  " + p.getProductID() + " - " + p.getProductName() + " - " + p.getPrice());
            }
            if (seas.size() > 20) {
                System.out.println("  ... (" + (seas.size() - 20) + " more)");
            }

            System.out.println("\nDONE.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Lấy PromotionID đang gán cho 1 sản phẩm; null nếu chưa có
     */
    public Integer getAssignedPromotionId(int productID) {
        final String sql = "SELECT TOP 1 PromotionID FROM Product_Promotion WHERE ProductID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("getAssignedPromotionId failed: " + productID, e);
        }
        return null;
    }

    /**
     * True nếu có thể gán (chưa có promotion nào, hoặc đã gán chính promotionID
     * này)
     */
    public boolean canAssignProductToPromotion(int productID, int promotionID) {
        Integer existed = getAssignedPromotionId(productID);
        return existed == null || existed == promotionID;
    }

    /**
     * Ném lỗi nếu product đang thuộc promotion khác
     */
    public void assertCanAssignProductToPromotion(int productID, int promotionID) {
        Integer existed = getAssignedPromotionId(productID);
        if (existed != null && existed != promotionID) {
            throw new IllegalStateException(
                    "Product " + productID + " đã thuộc Promotion " + existed
                    + " — không thể gán thêm Promotion " + promotionID);
        }
    }
}
