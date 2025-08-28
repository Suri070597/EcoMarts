/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import db.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.io.File;

/**
 *
 * @author LNQB
 */
public class FeedBackDAO extends DBContext {

    public boolean canReview(int accountId, int orderId, int productId) throws SQLException {
        String sql = "SELECT 1 FROM [Order] o "
                + "JOIN OrderDetail od ON o.OrderID = od.OrderID "
                + "WHERE o.AccountID = ? AND o.OrderID = ? AND od.ProductID = ? AND o.PaymentStatus = N'Đã thanh toán' "
                + "AND NOT EXISTS (SELECT 1 FROM Review r WHERE r.AccountID = ? AND r.ProductID = ? AND r.Comment IS NOT NULL AND r.ParentReviewID IS NULL AND r.ReviewID IN "
                + "(SELECT ReviewID FROM Review WHERE ProductID = ? AND AccountID = ?))";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, accountId);
            ps.setInt(2, orderId);
            ps.setInt(3, productId);
            ps.setInt(4, accountId);
            ps.setInt(5, productId);
            ps.setInt(6, productId);
            ps.setInt(7, accountId);

            ResultSet rs = ps.executeQuery();
            return rs.next();
        }
    }

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
    public boolean addReview(Integer parentReviewId, int orderId, int productId, int accountId, Integer rating,
            String comment, String imageUrl) throws SQLException {
        // Không cho phép nếu tài khoản không active
        if (!isAccountActive(accountId)) {
            return false;
        }
        int role = getAccountRole(accountId);
        // Chỉ customer mới được review gốc
        if (parentReviewId == null) {
            if (role != 0) {
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
                // Staff reply vào review gốc của customer
                String sqlCheckParentRole = "SELECT a.Role FROM Review r JOIN Account a ON r.AccountID = a.AccountID WHERE r.ReviewID = ? AND r.ParentReviewID IS NULL";
                try (PreparedStatement ps = conn.prepareStatement(sqlCheckParentRole)) {
                    ps.setInt(1, parentReviewId);
                    ResultSet rs = ps.executeQuery();
                    if (!rs.next() || rs.getInt(1) != 0) {
                        return false;
                    }
                }
            } else if (role == 0) {
                // Customer reply vào reply của staff (trả lời phản hồi nhân viên)
                // Kiểm tra parentReviewId là reply của staff, cha là review gốc của customer,
                // và accountId là chủ review gốc đó
                String sql = "SELECT r0.ReviewID FROM Review r1 "
                        + "JOIN Review r0 ON r1.ParentReviewID = r0.ReviewID "
                        + "JOIN Account a1 ON r1.AccountID = a1.AccountID "
                        + "WHERE r1.ReviewID = ? AND r1.ParentReviewID IS NOT NULL "
                        + "AND a1.Role = 2 AND r0.AccountID = ? AND r0.ParentReviewID IS NULL";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, parentReviewId);
                    ps.setInt(2, accountId);
                    ResultSet rs = ps.executeQuery();
                    if (!rs.next()) {
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
            ps.setInt(2, orderId);
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
                    System.out.println("DEBUG: Kiểm tra customer có thể sửa review ID=" + reviewId + ", parentReviewId="
                            + parentReviewId);

                    // Tìm review gốc để kiểm tra
                    int rootReviewId = reviewId;
                    if (parentReviewId != null) {
                        // Nếu là reply, tìm review gốc
                        rootReviewId = findRootReviewId(reviewId);
                        System.out.println("DEBUG: Review ID=" + reviewId + " có parent=" + parentReviewId
                                + ", tìm được root=" + rootReviewId);
                    } else {
                        System.out.println("DEBUG: Review ID=" + reviewId + " là review gốc");
                    }

                    // Kiểm tra review gốc có quá 30 ngày không
                    String rootSql = "SELECT r.CreatedAt, a.Role FROM Review r JOIN Account a ON r.AccountID = a.AccountID WHERE r.ReviewID = ?";
                    try (PreparedStatement rootPs = conn.prepareStatement(rootSql)) {
                        rootPs.setInt(1, rootReviewId);
                        ResultSet rootRs = rootPs.executeQuery();
                        if (rootRs.next()) {
                            int rootRole = rootRs.getInt("Role");
                            Timestamp rootCreatedAt = rootRs.getTimestamp("CreatedAt");

                            System.out.println("DEBUG: Review gốc ID=" + rootReviewId + ", role=" + rootRole
                                    + ", ngày tạo=" + rootCreatedAt);

                            // Nếu review gốc là của customer và quá 30 ngày, thì không cho sửa bất kỳ
                            // review/reply nào
                            if (rootRole == 0) {
                                long currentTime = System.currentTimeMillis();
                                long rootTime = rootCreatedAt.getTime();
                                long rootDaysDiff = (currentTime - rootTime) / (1000 * 60 * 60 * 24);
                                System.out.println("DEBUG: Review gốc là customer, số ngày=" + rootDaysDiff);
                                if (rootDaysDiff > 30) {
                                    System.out.println("DEBUG: Review gốc quá 30 ngày, không cho sửa");
                                    return false;
                                } else {
                                    System.out.println("DEBUG: Review gốc chưa quá 30 ngày, cho phép sửa");
                                }
                            } else {
                                System.out.println("DEBUG: Review gốc là staff, cho phép sửa");
                            }
                        } else {
                            System.out.println("DEBUG: Không tìm thấy review gốc ID=" + rootReviewId);
                        }
                    }

                    System.out.println("DEBUG: Cho phép customer sửa review ID=" + reviewId);
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
                System.out.println("DEBUG: findRootReviewId - Review ID=" + reviewId + ", Parent ID=" + parentId);
                if (parentId != null) {
                    // Nếu có parent, tiếp tục tìm lên
                    return findRootReviewId(parentId);
                } else {
                    // Nếu không có parent, đây là review gốc
                    System.out.println("DEBUG: findRootReviewId - Tìm được review gốc ID=" + reviewId);
                    return reviewId;
                }
            }
        }
        System.out.println("DEBUG: findRootReviewId - Không tìm thấy review ID=" + reviewId);
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

    // Lấy danh sách reply cho 1 review gốc (trả về List<Review> thay vì ResultSet)
    public List<model.Review> getRepliesByParentId(int parentReviewId) throws SQLException {
        List<model.Review> replies = new java.util.ArrayList<>();
        String sql = "SELECT r.*, a.FullName, a.Username, a.Role FROM Review r JOIN Account a ON r.AccountID = a.AccountID WHERE r.ParentReviewID = ? AND r.Status = 'VISIBLE' ORDER BY r.CreatedAt ASC";
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

    // Lấy toàn bộ review, join Account và Product, phục vụ staff quản lý
    public List<model.Review> getAllReviewsWithAccountAndProduct() throws SQLException {
        List<model.Review> list = new ArrayList<>();
        String sql = "SELECT r.*, a.Username, a.FullName, a.Role, p.ProductName "
                + "FROM Review r "
                + "JOIN Account a ON r.AccountID = a.AccountID "
                + "JOIN Product p ON r.ProductID = p.ProductID "
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
                Integer parentReviewId = rs.getInt("ParentReviewID");
                if (rs.wasNull()) {
                    r.setParentReviewID(null);
                } else {
                    r.setParentReviewID(parentReviewId);
                }
                r.setAccountName(rs.getString("FullName"));
                r.setUserName(rs.getString("Username"));
                r.setAccountRole(rs.getInt("Role"));
                r.setProductName(rs.getString("ProductName"));
                r.setStatus(rs.getString("Status"));
                list.add(r);
                System.out.println("DEBUG: Review loaded: ID=" + r.getReviewID() + ", Status=" + r.getStatus()
                        + ", Product=" + r.getProductName() + ", User=" + r.getUserName());
            }
        }
        System.out.println("DEBUG: Total reviews loaded: " + list.size());
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

    // Xóa review
    public void deleteReview(int reviewId) throws SQLException {
        // Lấy imageURL trước khi xóa
        String imageUrl = null;
        String getSql = "SELECT ImageURL FROM Review WHERE ReviewID = ?";
        try (PreparedStatement ps = conn.prepareStatement(getSql)) {
            ps.setInt(1, reviewId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                imageUrl = rs.getString("ImageURL");
            }
        } catch (Exception e) {
            System.out.println("[deleteReview] Lỗi khi lấy imageURL: " + e.getMessage());
        }
        // Xóa file ảnh vật lý nếu có
        if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.startsWith("http")) {
            File imgFile = new File("C:/EcoMarts/ReviewImages", imageUrl);
            if (imgFile.exists()) {
                boolean deleted = imgFile.delete();
                System.out.println("[deleteReview] Xóa file ảnh " + imgFile.getAbsolutePath() + ": " + deleted);
            }
        }
        // Xóa review trong DB
        String sql = "DELETE FROM Review WHERE ReviewID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reviewId);
            ps.executeUpdate();
        }
    }

    // Lấy toàn bộ review, join Account và Product, lọc theo tên sản phẩm
    public List<model.Review> getAllReviewsWithAccountAndProductByProductName(String keyword) throws SQLException {
        List<model.Review> list = new ArrayList<>();
        String sql = "SELECT r.*, a.Username, a.FullName, a.Role, p.ProductName "
                + "FROM Review r "
                + "JOIN Account a ON r.AccountID = a.AccountID "
                + "JOIN Product p ON r.ProductID = p.ProductID "
                + "WHERE LOWER(p.ProductName) LIKE ? "
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
                Integer parentReviewId = rs.getInt("ParentReviewID");
                if (rs.wasNull()) {
                    r.setParentReviewID(null);
                } else {
                    r.setParentReviewID(parentReviewId);
                }
                r.setAccountName(rs.getString("FullName"));
                r.setUserName(rs.getString("Username"));
                r.setAccountRole(rs.getInt("Role"));
                r.setProductName(rs.getString("ProductName"));
                r.setStatus(rs.getString("Status"));
                list.add(r);
            }
        }
        return list;
    }

    // Lấy các phản hồi staff chưa đọc cho customer
    public List<model.Review> getUnreadStaffRepliesForCustomer(int customerId) throws SQLException {
        List<model.Review> list = new ArrayList<>();
        String sql = "SELECT r.*, a.FullName, a.Username, a.Role, p.ProductName FROM Review r " +
                "JOIN Account a ON r.AccountID = a.AccountID " +
                "JOIN Product p ON r.ProductID = p.ProductID " +
                "WHERE r.ParentReviewID IN (SELECT ReviewID FROM Review WHERE AccountID = ? AND ParentReviewID IS NULL) "
                +
                "AND a.Role = 2 AND r.IsRead = 0 AND r.Status = 'VISIBLE' ORDER BY r.CreatedAt DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
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
                r.setProductName(rs.getString("ProductName")); // Lấy tên sản phẩm
                list.add(r);
            }
        }
        return list;
    }

    // Đánh dấu tất cả phản hồi staff cho customer là đã đọc
    public void markAllStaffRepliesAsRead(int customerId) throws SQLException {
        String sql = "UPDATE Review SET IsRead = 1 WHERE ParentReviewID IN (SELECT ReviewID FROM Review WHERE AccountID = ? AND ParentReviewID IS NULL) AND IsRead = 0 AND (SELECT Role FROM Account WHERE AccountID = Review.AccountID) = 2";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.executeUpdate();
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

    public static void main(String[] args) {
        FeedBackDAO dao = new FeedBackDAO();
        try {
            List<model.Review> reviews = dao.getAllReviewsWithAccountAndProduct();
            System.out.println("TEST: Tổng số review lấy được: " + (reviews != null ? reviews.size() : "null"));
            if (reviews != null) {
                for (model.Review r : reviews) {
                    System.out.println("ReviewID=" + r.getReviewID()
                            + ", ProductName=" + r.getProductName()
                            + ", UserName=" + r.getUserName()
                            + ", Status=" + r.getStatus()
                            + ", Comment=" + r.getComment());
                }
            }
        } catch (Exception e) {
            System.out.println("TEST: Lỗi khi lấy review: " + e.getMessage());
            e.printStackTrace();
        } finally {
            dao.closeConnection();
        }
    }
}