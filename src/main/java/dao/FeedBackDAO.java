package dao;

import db.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.io.File;

public class FeedBackDAO extends DBContext {

    // Kiểm tra role của account (0: customer, 1: admin, 2: staff, ...)
    public int getAccountRole(int accountId) throws SQLException {
        String sql = "SELECT Role FROM Account WHERE AccountID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return -1; // Không tìm thấy
    }

    // Kiểm tra đã tồn tại review gốc (không phải reply) cho cùng OrderID +
    // ProductID + AccountID
    public boolean isReviewExists(int orderId, int productId, int accountId) throws SQLException {
        String sql = "SELECT 1 FROM Review WHERE OrderID = ? AND ProductID = ? AND AccountID = ? AND ParentReviewID IS NULL";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, productId);
            ps.setInt(3, accountId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    // Kiểm tra đơn hàng đã thanh toán
    public boolean isOrderPaid(int orderId, int accountId) throws SQLException {
        String sql = "SELECT 1 FROM [Order] WHERE OrderID = ? AND AccountID = ? AND PaymentStatus = N'Đã thanh toán'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, accountId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

    // Kiểm tra trạng thái tài khoản
    public boolean isAccountActive(int accountId) throws SQLException {
        String sql = "SELECT Status FROM Account WHERE AccountID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String status = rs.getString(1);
                return status != null && status.equalsIgnoreCase("Active");
            }
        }
        return false;
    }

    // Thêm review hoặc reply
    public boolean addReview(Integer parentReviewId, Integer orderId, int productId, int accountId, Integer rating,
            String comment, String imageUrl) throws SQLException {
        int role = getAccountRole(accountId);
        // Với customer, bắt buộc tài khoản active; staff bỏ qua kiểm tra này
        if (role != 2 && !isAccountActive(accountId)) {
            return false;
        }
        // Chỉ customer mới được review gốc
        if (parentReviewId == null) {
            if (role != 0) {
                return false;
            }
            // orderId bắt buộc với review gốc
            if (orderId == null || orderId <= 0) {
                return false;
            }
            if (!isOrderPaid(orderId, accountId)) {
                return false;
            }
            if (isReviewExists(orderId, productId, accountId)) {
                return false;
            }
        } else {
            if (role == 2) {
                // Staff reply vào bất kỳ review của customer (gốc hoặc con)
                String sqlCheckParentRole = "SELECT a.Role FROM Review r JOIN Account a ON r.AccountID = a.AccountID WHERE r.ReviewID = ?";
                try (PreparedStatement ps = conn.prepareStatement(sqlCheckParentRole)) {
                    ps.setInt(1, parentReviewId);
                    ResultSet rs = ps.executeQuery();
                    if (!rs.next() || rs.getInt(1) != 0) {
                        return false; // parent không phải customer
                    }
                }
            } else if (role == 0) {
                // Customer reply: cho phép trả lời BẤT KỲ reply của STAFF trong thread
                // Điều kiện:
                // 1) parentReviewId thuộc về STAFF
                // 2) Review GỐC của thread thuộc về customer hiện tại
                // Kiểm tra (1)
                String sqlParentRole = "SELECT a.Role FROM Review r JOIN Account a ON r.AccountID = a.AccountID WHERE r.ReviewID = ?";
                try (PreparedStatement ps = conn.prepareStatement(sqlParentRole)) {
                    ps.setInt(1, parentReviewId);
                    ResultSet rs = ps.executeQuery();
                    if (!rs.next() || rs.getInt(1) != 2) {
                        return false; // parent không phải staff
                    }
                }
                // Kiểm tra (2): chủ thread là customer hiện tại
                int rootId = findRootReviewId(parentReviewId);
                String sqlRoot = "SELECT AccountID FROM Review WHERE ReviewID = ? AND ParentReviewID IS NULL";
                try (PreparedStatement ps = conn.prepareStatement(sqlRoot)) {
                    ps.setInt(1, rootId);
                    ResultSet rs = ps.executeQuery();
                    if (!rs.next() || rs.getInt(1) != accountId) {
                        return false;
                    }
                }
            } else {
                return false;
            }
        }
        String sql = "INSERT INTO Review (ParentReviewID, OrderID, ProductID, AccountID, Rating, Comment, ImageURL, CreatedAt, UpdatedAt) VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE(), GETDATE())";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            if (parentReviewId != null) {
                ps.setInt(1, parentReviewId);
            } else {
                ps.setNull(1, java.sql.Types.INTEGER);
            }
            if (orderId != null && orderId > 0) {
                ps.setInt(2, orderId);
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
            ps.setInt(3, productId);
            ps.setInt(4, accountId);
            if (rating != null) {
                ps.setInt(5, rating);
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }
            ps.setString(6, comment);
            ps.setString(7, imageUrl);
            return ps.executeUpdate() > 0;
        }
    }

    // Kiểm tra customer có thể sửa review trong 30 ngày không
    public boolean canEditReview(int reviewId, int accountId) throws SQLException {
        String sql = "SELECT r.CreatedAt, r.AccountID, a.Role, r.ParentReviewID FROM Review r JOIN Account a ON r.AccountID = a.AccountID WHERE r.ReviewID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reviewId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int reviewerAccountId = rs.getInt("AccountID");
                int reviewerRole = rs.getInt("Role");
                Timestamp createdAt = rs.getTimestamp("CreatedAt");
                Integer parentReviewId = rs.getInt("ParentReviewID");

                // Chỉ cho phép sửa review/reply của chính mình
                if (reviewerAccountId != accountId) {
                    return false;
                }

                // Staff có thể sửa review/reply của mình bất kỳ lúc nào
                if (reviewerRole == 2) {
                    return true;
                }

                // Customer chỉ sửa được review/reply của mình trong 30 ngày
                if (reviewerRole == 0) {
                    // Tìm review gốc để kiểm tra
                    int rootReviewId = reviewId;
                    if (parentReviewId != null) {
                        // Nếu là reply, tìm review gốc
                        rootReviewId = findRootReviewId(reviewId);
                    }

                    // Kiểm tra review gốc có quá 30 ngày không
                    String rootSql = "SELECT r.CreatedAt, a.Role FROM Review r JOIN Account a ON r.AccountID = a.AccountID WHERE r.ReviewID = ?";
                    try (PreparedStatement rootPs = conn.prepareStatement(rootSql)) {
                        rootPs.setInt(1, rootReviewId);
                        ResultSet rootRs = rootPs.executeQuery();
                        if (rootRs.next()) {
                            int rootRole = rootRs.getInt("Role");
                            Timestamp rootCreatedAt = rootRs.getTimestamp("CreatedAt");

                            // Nếu review gốc là của customer và quá 30 ngày, thì không cho sửa bất kỳ
                            // review/reply nào
                            if (rootRole == 0) {
                                long currentTime = System.currentTimeMillis();
                                long rootTime = rootCreatedAt.getTime();
                                long rootDaysDiff = (currentTime - rootTime) / (1000 * 60 * 60 * 24);
                                if (rootDaysDiff > 30) {
                                    return false;
                                }
                            }
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    // Kiểm tra customer có thể xóa review không (trong 30 ngày)
    public boolean canDeleteReview(int reviewId, int accountId) throws SQLException {
        String sql = "SELECT r.CreatedAt, r.AccountID, a.Role, r.ParentReviewID FROM Review r JOIN Account a ON r.AccountID = a.AccountID WHERE r.ReviewID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reviewId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int reviewerAccountId = rs.getInt("AccountID");
                int reviewerRole = rs.getInt("Role");
                Timestamp createdAt = rs.getTimestamp("CreatedAt");
                Integer parentReviewId = rs.getInt("ParentReviewID");

                // Chỉ cho phép xóa review/reply của chính mình
                if (reviewerAccountId != accountId) {
                    return false;
                }

                // Staff có thể xóa review/reply của mình bất kỳ lúc nào
                if (reviewerRole == 2) {
                    return true;
                }

                // Customer chỉ xóa được review/reply của mình trong 30 ngày
                if (reviewerRole == 0) {
                    // Tìm review gốc để kiểm tra
                    int rootReviewId = reviewId;
                    if (parentReviewId != null) {
                        // Nếu là reply, tìm review gốc
                        rootReviewId = findRootReviewId(reviewId);
                    }

                    // Kiểm tra review gốc có quá 30 ngày không
                    String rootSql = "SELECT r.CreatedAt, a.Role FROM Review r JOIN Account a ON r.AccountID = a.AccountID WHERE r.ReviewID = ?";
                    try (PreparedStatement rootPs = conn.prepareStatement(rootSql)) {
                        rootPs.setInt(1, rootReviewId);
                        ResultSet rootRs = rootPs.executeQuery();
                        if (rootRs.next()) {
                            int rootRole = rootRs.getInt("Role");
                            Timestamp rootCreatedAt = rootRs.getTimestamp("CreatedAt");

                            // Nếu review gốc là của customer và quá 30 ngày, thì không cho xóa bất kỳ
                            // review/reply nào
                            if (rootRole == 0) {
                                long currentTime = System.currentTimeMillis();
                                long rootTime = rootCreatedAt.getTime();
                                long rootDaysDiff = (currentTime - rootTime) / (1000 * 60 * 60 * 24);
                                if (rootDaysDiff > 30) {
                                    return false;
                                }
                            }
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }

    // Tìm review gốc từ review ID bất kỳ
    private int findRootReviewId(int reviewId) throws SQLException {
        String sql = "SELECT ParentReviewID FROM Review WHERE ReviewID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reviewId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Integer parentId = rs.getInt("ParentReviewID");
                if (rs.wasNull()) {
                    parentId = null;
                }
                if (parentId != null) {
                    // Nếu có parent, tiếp tục tìm lên
                    return findRootReviewId(parentId);
                } else {
                    // Nếu không có parent, đây là review gốc
                    return reviewId;
                }
            }
        }
        return reviewId;
    }

    // Cập nhật review
    public boolean updateReview(int reviewId, int accountId, Integer rating, String comment, String imageUrl)
            throws SQLException {
        // Kiểm tra quyền sửa
        if (!canEditReview(reviewId, accountId)) {
            return false;
        }

        StringBuilder sql = new StringBuilder("UPDATE Review SET Comment = ?");
        if (rating != null) {
            sql.append(", Rating = ?");
        }
        if (imageUrl != null && !imageUrl.isEmpty()) {
            sql.append(", ImageURL = ?");
        }
        sql.append(" WHERE ReviewID = ? AND AccountID = ?");

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            ps.setString(paramIndex++, comment);
            if (rating != null) {
                ps.setInt(paramIndex++, rating);
            }
            if (imageUrl != null && !imageUrl.isEmpty()) {
                ps.setString(paramIndex++, imageUrl);
            }
            ps.setInt(paramIndex++, reviewId);
            ps.setInt(paramIndex, accountId);

            return ps.executeUpdate() > 0;
        }
    }

    // Lấy danh sách reply cho 1 review (không lọc Status để staff thấy tất cả)
    public List<model.Review> getRepliesByParentId(int parentReviewId) throws SQLException {
        List<model.Review> replies = new java.util.ArrayList<>();
        String sql = "SELECT r.*, a.FullName, a.Username, a.Role FROM Review r "
                + "JOIN Account a ON r.AccountID = a.AccountID "
                + "WHERE r.ParentReviewID = ? "
                + "ORDER BY r.CreatedAt ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, parentReviewId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.Review r = new model.Review();
                r.setReviewID(rs.getInt("ReviewID"));
                r.setOrderID(rs.getInt("OrderID"));
                r.setProductID(rs.getInt("ProductID"));
                r.setAccountID(rs.getInt("AccountID"));
                r.setRating(rs.getInt("Rating"));
                r.setComment(rs.getString("Comment"));
                r.setImageURL(rs.getString("ImageURL"));
                r.setCreatedAt(rs.getTimestamp("CreatedAt"));
                r.setStatus(rs.getString("Status"));
                String fullName = rs.getString("FullName");
                String username = rs.getString("Username");
                r.setAccountName(fullName != null && !fullName.isEmpty() ? fullName : username);
                r.setUserName(rs.getString("Username"));
                r.setAccountRole(rs.getInt("Role"));
                // Lấy replies lồng cho từng reply (tối đa 2 cấp)
                r.setReplies(getRepliesByParentId(r.getReviewID()));
                replies.add(r);
            }
        }
        return replies;
    }

    // Lấy danh sách review gốc cho 1 sản phẩm, kèm replies và accountRole
    public List<model.Review> getReviewsByProductId(int productId) throws SQLException {
        List<model.Review> list = new java.util.ArrayList<>();
        String sql = "SELECT r.*, a.FullName, a.Username, a.Role FROM Review r JOIN Account a ON r.AccountID = a.AccountID WHERE r.ProductID = ? AND r.ParentReviewID IS NULL AND r.Status = 'VISIBLE' ORDER BY r.CreatedAt ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.Review r = new model.Review();
                r.setReviewID(rs.getInt("ReviewID"));
                r.setOrderID(rs.getInt("OrderID"));
                r.setProductID(rs.getInt("ProductID"));
                r.setAccountID(rs.getInt("AccountID"));
                r.setRating(rs.getInt("Rating"));
                r.setComment(rs.getString("Comment"));
                r.setImageURL(rs.getString("ImageURL"));
                r.setCreatedAt(rs.getTimestamp("CreatedAt"));
                String fullName = rs.getString("FullName");
                String username = rs.getString("Username");
                r.setAccountName(fullName != null && !fullName.isEmpty() ? fullName : username);
                r.setUserName(rs.getString("Username"));
                r.setAccountRole(rs.getInt("Role"));
                // Lấy replies cho review này
                r.setReplies(getRepliesByParentId(r.getReviewID()));
                list.add(r);
            }
        }
        return list;
    }

    public int countReviewsByProductId(int productId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Review WHERE ProductID = ? AND ParentReviewID IS NULL";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public double getAverageRatingByProductId(int productId) throws SQLException {
        String sql = "SELECT AVG(CAST(Rating AS FLOAT)) FROM Review WHERE ProductID = ? AND ParentReviewID IS NULL AND Rating IS NOT NULL";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0.0;
    }

    public Integer getOrderIdForReview(int accountId, int productId) throws SQLException {
        String sql = "SELECT TOP 1 o.OrderID FROM [Order] o "
                + "JOIN OrderDetail od ON o.OrderID = od.OrderID "
                + "WHERE o.AccountID = ? AND od.ProductID = ? AND o.PaymentStatus = N'Đã thanh toán' "
                + "ORDER BY o.OrderDate DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            ps.setInt(2, productId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return null;
    }

    // Lấy review theo ID
    public model.Review getReviewById(int reviewId) throws SQLException {
        String sql = "SELECT r.*, a.FullName, a.Username, a.Role FROM Review r JOIN Account a ON r.AccountID = a.AccountID WHERE r.ReviewID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reviewId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                model.Review r = new model.Review();
                r.setReviewID(rs.getInt("ReviewID"));
                r.setOrderID(rs.getInt("OrderID"));
                r.setProductID(rs.getInt("ProductID"));
                r.setAccountID(rs.getInt("AccountID"));
                r.setRating(rs.getInt("Rating"));
                r.setComment(rs.getString("Comment"));
                r.setImageURL(rs.getString("ImageURL"));
                r.setCreatedAt(rs.getTimestamp("CreatedAt"));
                Integer parentReviewId = rs.getInt("ParentReviewID");
                if (rs.wasNull()) {
                    r.setParentReviewID(null);
                } else {
                    r.setParentReviewID(parentReviewId);
                }
                String fullName = rs.getString("FullName");
                String username = rs.getString("Username");
                r.setAccountName(fullName != null && !fullName.isEmpty() ? fullName : username);
                r.setUserName(rs.getString("Username"));
                r.setAccountRole(rs.getInt("Role"));
                return r;
            }
        }
        return null;
    }

    // Lấy toàn bộ review GỐC (ParentReviewID IS NULL), join Account và Product, kèm
    // danh sách replies
    public List<model.Review> getAllReviewsWithAccountAndProduct() throws SQLException {
        List<model.Review> list = new ArrayList<>();
        String sql = "SELECT r.*, a.Username, a.FullName, a.Role, p.ProductName "
                + "FROM Review r "
                + "JOIN Account a ON r.AccountID = a.AccountID "
                + "JOIN Product p ON r.ProductID = p.ProductID "
                + "WHERE r.ParentReviewID IS NULL "
                + "ORDER BY r.CreatedAt DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.Review r = new model.Review();
                r.setReviewID(rs.getInt("ReviewID"));
                r.setProductID(rs.getInt("ProductID"));
                r.setAccountID(rs.getInt("AccountID"));
                r.setRating(rs.getInt("Rating"));
                r.setComment(rs.getString("Comment"));
                r.setImageURL(rs.getString("ImageURL"));
                r.setCreatedAt(rs.getTimestamp("CreatedAt"));
                r.setParentReviewID(null);
                r.setAccountName(rs.getString("FullName"));
                r.setUserName(rs.getString("Username"));
                r.setAccountRole(rs.getInt("Role"));
                r.setProductName(rs.getString("ProductName"));
                r.setStatus(rs.getString("Status"));
                // Load replies cho review gốc
                List<model.Review> replies = getRepliesByParentId(r.getReviewID());
                r.setReplies(replies);
                list.add(r);
            }
        }
        return list;
    }

    // Cập nhật trạng thái review (VISIBLE/HIDDEN)
    public void updateReviewStatus(int reviewId, String status) throws SQLException {
        String sql = "UPDATE Review SET Status = ? WHERE ReviewID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, reviewId);
            ps.executeUpdate();
        }
    }

    // Xóa review và tất cả replies của nó (recursive)
    public void deleteReview(int reviewId) throws SQLException {
        // Đầu tiên, xóa tất cả replies con (recursive)
        deleteAllReplies(reviewId);

        // Lấy imageURL trước khi xóa review gốc
        String imageUrl = null;
        String getSql = "SELECT ImageURL FROM Review WHERE ReviewID = ?";
        try (PreparedStatement ps = conn.prepareStatement(getSql)) {
            ps.setInt(1, reviewId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                imageUrl = rs.getString("ImageURL");
            }
        } catch (Exception e) {
            // Ignore error when getting imageURL
        }

        // Xóa file ảnh vật lý nếu có
        if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.startsWith("http")) {
            File imgFile = new File("C:/EcoMarts/ReviewImages", imageUrl);
            if (imgFile.exists()) {
                imgFile.delete();
            }
        }

        // Cuối cùng, xóa review gốc
        String sql = "DELETE FROM Review WHERE ReviewID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reviewId);
            ps.executeUpdate();
        }
    }

    // Helper method: Xóa tất cả replies của một review (recursive)
    private void deleteAllReplies(int parentReviewId) throws SQLException {
        // Lấy danh sách tất cả replies trực tiếp
        String selectSql = "SELECT ReviewID, ImageURL FROM Review WHERE ParentReviewID = ?";
        List<Integer> replyIds = new ArrayList<>();
        List<String> imageUrls = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
            ps.setInt(1, parentReviewId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                replyIds.add(rs.getInt("ReviewID"));
                String imageUrl = rs.getString("ImageURL");
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    imageUrls.add(imageUrl);
                }
            }
        }

        // Xóa replies của từng reply con (recursive)
        for (int replyId : replyIds) {
            deleteAllReplies(replyId);
        }

        // Xóa file ảnh vật lý của các replies
        for (String imageUrl : imageUrls) {
            if (!imageUrl.startsWith("http")) {
                File imgFile = new File("C:/EcoMarts/ReviewImages", imageUrl);
                if (imgFile.exists()) {
                    imgFile.delete();
                }
            }
        }

        // Xóa tất cả replies trực tiếp
        if (!replyIds.isEmpty()) {
            String deleteSql = "DELETE FROM Review WHERE ParentReviewID = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
                ps.setInt(1, parentReviewId);
                ps.executeUpdate();
            }
        }
    }

    // Lấy toàn bộ review GỐC theo tên sản phẩm, kèm replies
    public List<model.Review> getAllReviewsWithAccountAndProductByProductName(String keyword) throws SQLException {
        List<model.Review> list = new ArrayList<>();
        String sql = "SELECT r.*, a.Username, a.FullName, a.Role, p.ProductName "
                + "FROM Review r "
                + "JOIN Account a ON r.AccountID = a.AccountID "
                + "JOIN Product p ON r.ProductID = p.ProductID "
                + "WHERE LOWER(p.ProductName) LIKE ? AND r.ParentReviewID IS NULL "
                + "ORDER BY r.CreatedAt DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword.toLowerCase() + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.Review r = new model.Review();
                r.setReviewID(rs.getInt("ReviewID"));
                r.setProductID(rs.getInt("ProductID"));
                r.setAccountID(rs.getInt("AccountID"));
                r.setRating(rs.getInt("Rating"));
                r.setComment(rs.getString("Comment"));
                r.setImageURL(rs.getString("ImageURL"));
                r.setCreatedAt(rs.getTimestamp("CreatedAt"));
                r.setParentReviewID(null);
                r.setAccountName(rs.getString("FullName"));
                r.setUserName(rs.getString("Username"));
                r.setAccountRole(rs.getInt("Role"));
                r.setProductName(rs.getString("ProductName"));
                r.setStatus(rs.getString("Status"));
                // Load replies cho review gốc
                List<model.Review> replies = getRepliesByParentId(r.getReviewID());
                r.setReplies(replies);
                list.add(r);
            }
        }
        return list;
    }

    // Lấy các phản hồi staff chưa đọc cho customer, bao gồm cả phản hồi vào reply
    // con
    public List<model.Review> getUnreadStaffRepliesForCustomer(int customerId) throws SQLException {
        List<model.Review> list = new ArrayList<>();
        String sql = "SELECT r.*, a.FullName, a.Username, a.Role, p.ProductName FROM Review r " +
                "JOIN Account a ON r.AccountID = a.AccountID " +
                "JOIN Product p ON r.ProductID = p.ProductID " +
                "WHERE a.Role = 2 AND r.IsRead = 0 AND r.Status = 'VISIBLE' ORDER BY r.CreatedAt DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int replyId = rs.getInt("ReviewID");
                int rootId = findRootReviewId(replyId);
                // Kiểm tra review gốc có thuộc customer này không
                try (PreparedStatement psRoot = conn
                        .prepareStatement("SELECT AccountID FROM Review WHERE ReviewID = ?")) {
                    psRoot.setInt(1, rootId);
                    ResultSet rsRoot = psRoot.executeQuery();
                    if (!rsRoot.next() || rsRoot.getInt(1) != customerId) {
                        continue;
                    }
                }

                model.Review r = new model.Review();
                r.setReviewID(replyId);
                r.setOrderID(rs.getInt("OrderID"));
                r.setProductID(rs.getInt("ProductID"));
                r.setAccountID(rs.getInt("AccountID"));
                r.setRating(rs.getInt("Rating"));
                r.setComment(rs.getString("Comment"));
                r.setImageURL(rs.getString("ImageURL"));
                r.setCreatedAt(rs.getTimestamp("CreatedAt"));
                Integer parentReviewId = rs.getInt("ParentReviewID");
                if (rs.wasNull()) {
                    r.setParentReviewID(null);
                } else {
                    r.setParentReviewID(parentReviewId);
                }
                r.setAccountName(rs.getString("FullName"));
                r.setUserName(rs.getString("Username"));
                r.setAccountRole(rs.getInt("Role"));
                r.setStatus(rs.getString("Status"));
                r.setIsRead(rs.getBoolean("IsRead"));
                r.setProductName(rs.getString("ProductName"));
                list.add(r);
            }
        }
        return list;
    }

    // Đánh dấu tất cả phản hồi staff cho customer là đã đọc
    public void markAllStaffRepliesAsRead(int customerId) throws SQLException {
        // Lấy các reply staff thuộc thread của customer và đang chưa đọc
        String sql = "SELECT r.ReviewID FROM Review r WHERE r.IsRead = 0 AND (SELECT Role FROM Account WHERE AccountID = r.AccountID) = 2";
        List<Integer> toMark = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int replyId = rs.getInt(1);
                int rootId = findRootReviewId(replyId);
                try (PreparedStatement psRoot = conn
                        .prepareStatement("SELECT AccountID FROM Review WHERE ReviewID = ?")) {
                    psRoot.setInt(1, rootId);
                    ResultSet rsRoot = psRoot.executeQuery();
                    if (rsRoot.next() && rsRoot.getInt(1) == customerId) {
                        toMark.add(replyId);
                    }
                }
            }
        }
        if (!toMark.isEmpty()) {
            StringBuilder inClause = new StringBuilder();
            for (int i = 0; i < toMark.size(); i++) {
                if (i > 0)
                    inClause.append(",");
                inClause.append("?");
            }
            String update = "UPDATE Review SET IsRead = 1 WHERE ReviewID IN (" + inClause + ")";
            try (PreparedStatement psUp = conn.prepareStatement(update)) {
                for (int i = 0; i < toMark.size(); i++) {
                    psUp.setInt(i + 1, toMark.get(i));
                }
                psUp.executeUpdate();
            }
        }
    }

    // Đánh dấu một reply là đã đọc
    public void markReplyAsRead(int reviewId) throws SQLException {
        String sql = "UPDATE Review SET IsRead = 1 WHERE ReviewID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reviewId);
            ps.executeUpdate();
        }
    }

}
