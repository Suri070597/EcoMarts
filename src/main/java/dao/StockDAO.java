package dao;

import db.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Inventory;
import model.StockIn;
import model.StockInDetail;

public class StockDAO extends DBContext {

    /**
     * Thêm bản ghi nhập kho vào Inventory
     *
     * @param productId ID sản phẩm
     * @param quantity số lượng nhập
     */
    public void addStockIn(int productId, double quantity) throws SQLException {
        String sql = """
            INSERT INTO Inventory (ProductID, Quantity, LastUpdated)
            VALUES (?, ?, GETDATE())
        """;

        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);
            ps.setDouble(2, quantity);
            ps.executeUpdate();
        }
    }

    /**
     * Cập nhật số lượng tồn kho của sản phẩm
     *
     * @param productId ID sản phẩm
     * @param quantity số lượng cần cộng thêm
     */
    public void updateProductStock(int productId, double quantity) throws SQLException {
        String sql = """
            UPDATE Product 
            SET StockQuantity = StockQuantity + ? 
            WHERE ProductID = ?
        """;

        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, quantity);
            ps.setInt(2, productId);
            ps.executeUpdate();
        }
    }

    /**
     * Thêm nhập kho và cập nhật số lượng sản phẩm
     *
     * @param productId ID sản phẩm
     * @param quantity số lượng nhập
     * @param supplierId ID nhà cung cấp (để ghi log)
     * @param receiverId ID người nhận (để ghi log)
     */
    public void addStockIn(int productId, double quantity, int supplierId, int receiverId) throws SQLException {
        // Thêm vào bảng Inventory
        addStockIn(productId, quantity);

        // Cập nhật số lượng tồn kho trong bảng Product
        updateProductStock(productId, quantity);

        // TODO: Có thể thêm bảng StockInLog để lưu thông tin supplier và receiver
        // INSERT INTO StockInLog (ProductID, Quantity, SupplierID, ReceiverID, Date)
    }

    public void createStockInFull(StockIn stockIn,
            List<Inventory> invList,
            List<StockInDetail> detailList) throws SQLException {

        String sqlStockIn = "INSERT INTO StockIn (SupplierID, ReceiverID, DateIn, Note) VALUES (?, ?, ?, ?)";
        String sqlInventory = "INSERT INTO Inventory (ProductID, PackageType, Quantity, UnitPrice, PackSize) VALUES (?, ?, ?, ?, ?)";
        String sqlDetail = "INSERT INTO StockInDetail (StockInID, InventoryID, Quantity, UnitPrice) VALUES (?, ?, ?, ?)";

        PreparedStatement psStock = null;
        PreparedStatement psInventory = null;
        PreparedStatement psDetail = null;

        try {
            conn.setAutoCommit(false);

            // 1. Insert StockIn
            psStock = conn.prepareStatement(sqlStockIn, Statement.RETURN_GENERATED_KEYS);
            psStock.setInt(1, stockIn.getSupplierID());
            psStock.setInt(2, stockIn.getReceiverID());
            psStock.setDate(3, stockIn.getDateIn());
            psStock.setString(4, stockIn.getNote());
            psStock.executeUpdate();

            ResultSet rsStock = psStock.getGeneratedKeys();
            int stockInID = 0;
            if (rsStock.next()) {
                stockInID = rsStock.getInt(1);
            }
            stockIn.setStockInID(stockInID);

            // 2. Prepare statements
            psInventory = conn.prepareStatement(sqlInventory, Statement.RETURN_GENERATED_KEYS);
            psDetail = conn.prepareStatement(sqlDetail);

            // 3. Loop over lists
            for (int i = 0; i < invList.size(); i++) {
                Inventory inv = invList.get(i);

                psInventory.setInt(1, inv.getProductID());
                psInventory.setString(2, inv.getPackageType());
                psInventory.setDouble(3, inv.getQuantity());
                psInventory.setDouble(4, inv.getUnitPrice());
                psInventory.setInt(5, inv.getPackSize());
                psInventory.executeUpdate();

                ResultSet rsInv = psInventory.getGeneratedKeys();
                int inventoryID = 0;
                if (rsInv.next()) {
                    inventoryID = rsInv.getInt(1);
                }
                inv.setInventoryID(inventoryID);

                StockInDetail detail = detailList.get(i);
                psDetail.setInt(1, stockInID);
                psDetail.setInt(2, inventoryID);
                psDetail.setDouble(3, detail.getQuantity());
                psDetail.setDouble(4, detail.getUnitPrice());
                psDetail.executeUpdate();
            }

            conn.commit();

        } catch (SQLException e) {
            conn.rollback();
            throw e; // ném ngược ra servlet
        } finally {
            conn.setAutoCommit(true);
        }
    }

    // Lấy tất cả StockIn
    public List<StockIn> getAllStockIns() throws SQLException {
        List<StockIn> list = new ArrayList<>();
        String sql = "SELECT s.StockInID, s.DateIn, s.Status, sup.CompanyName AS SupplierName, r.FullName AS ReceiverName "
                + "FROM StockIn s "
                + "JOIN Supplier sup ON s.SupplierID = sup.SupplierID "
                + "JOIN Account r ON s.ReceiverID = r.AccountID "
                + "ORDER BY s.StockInID DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                StockIn s = new StockIn();
                s.setStockInID(rs.getInt("StockInID"));
                s.setDateIn(rs.getDate("DateIn"));
                s.setStatus(rs.getString("Status"));
                s.setSupplierName(rs.getString("SupplierName"));
                s.setReceiverName(rs.getString("ReceiverName"));
                s.setDetails(new ArrayList<>()); // khởi tạo list rỗng
                list.add(s);
            }
        }
        return list;
    }

    public List<StockIn> getStockInBySupplier(int supplierId) throws SQLException {
        List<StockIn> list = new ArrayList<>();
        String sql = """
        SELECT s.StockInID, s.DateIn, s.Status,
               sup.CompanyName AS SupplierName,
               r.FullName AS ReceiverName
        FROM StockIn s
        JOIN Supplier sup ON s.SupplierID = sup.SupplierID
        JOIN Account r ON s.ReceiverID = r.AccountID
        WHERE s.SupplierID = ?
        ORDER BY s.DateIn DESC
    """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, supplierId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StockIn s = new StockIn();
                    s.setStockInID(rs.getInt("StockInID"));
                    s.setDateIn(rs.getDate("DateIn"));
                    s.setStatus(rs.getString("Status"));
                    s.setSupplierName(rs.getString("SupplierName"));
                    s.setReceiverName(rs.getString("ReceiverName"));
                    list.add(s);
                }
            }
        }
        return list;
    }

    // Lấy StockInDetail theo StockInID
    public List<StockInDetail> getDetailsByStockInID(int stockInID) throws SQLException {
        List<StockInDetail> list = new ArrayList<>();
        String sql = "SELECT d.InventoryID, p.ProductName, d.Quantity, d.UnitPrice "
                + "FROM StockInDetail d "
                + "JOIN Inventory i ON d.InventoryID = i.InventoryID "
                + "JOIN Product p ON i.ProductID = p.ProductID "
                + "WHERE d.StockInID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, stockInID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StockInDetail d = new StockInDetail();
                    d.setInventoryID(rs.getInt("InventoryID"));
                    d.setProductName(rs.getString("ProductName"));
                    d.setQuantity(rs.getDouble("Quantity"));
                    d.setUnitPrice(rs.getDouble("UnitPrice"));
                    list.add(d);
                }
            }
        }
        return list;
    }

    // 3. Lấy 1 StockIn theo ID (dùng cho trang chi tiết)
    public StockIn getStockInByID(int stockInID) throws SQLException {
        StockIn stock = null;
        String sql = "SELECT s.StockInID, s.DateIn, s.Status, sup.CompanyName AS SupplierName, r.FullName AS ReceiverName "
                + "FROM StockIn s "
                + "JOIN Supplier sup ON s.SupplierID = sup.SupplierID "
                + "JOIN Account r ON s.ReceiverID = r.AccountID "
                + "WHERE s.StockInID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, stockInID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stock = new StockIn();
                    stock.setStockInID(rs.getInt("StockInID"));
                    stock.setDateIn(rs.getDate("DateIn"));
                    stock.setStatus(rs.getString("Status"));
                    stock.setSupplierName(rs.getString("SupplierName"));
                    stock.setReceiverName(rs.getString("ReceiverName"));
                    stock.setDetails(new ArrayList<>()); // khởi tạo list chi tiết rỗng
                }
            }
        }
        return stock;
    }

    public int getProductIdByInventoryId(int inventoryId) throws SQLException {
        String sql = "SELECT ProductID FROM Inventory WHERE InventoryID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, inventoryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("ProductID");
                } else {
                    throw new SQLException("InventoryID không tồn tại: " + inventoryId);
                }
            }
        }
    }

    public void rejectStockIn(int stockInID, List<StockInDetail> details) throws SQLException {
        try {
            conn.setAutoCommit(false);

            // Cập nhật StockIn (Canceled)
            updateStockInStatus(stockInID, "Canceled");

            for (StockInDetail detail : details) {
                updateInventoryStatus(detail.getInventoryID(), "Không phê duyệt");
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public void approveStockIn(int stockInID, List<StockInDetail> details) throws SQLException {
        try {
            conn.setAutoCommit(false);

            // Cập nhật trạng thái StockIn
            updateStockInStatus(stockInID, "Completed");

            // Cập nhật từng Inventory và Product
            for (StockInDetail detail : details) {
                updateInventoryStatus(detail.getInventoryID(), "Đã duyệt");
                int productId = getProductIdByInventoryId(detail.getInventoryID());
                updateProductStock(productId, detail.getQuantity());
            }

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    // Cập nhật trạng thái StockIn
    public void updateStockInStatus(int stockInID, String status) throws SQLException {
        String sql = "UPDATE StockIn SET Status = ? WHERE StockInID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, stockInID);
            ps.executeUpdate();
        }
    }

// Cập nhật trạng thái Inventory
    public void updateInventoryStatus(int inventoryID, String status) throws SQLException {
        String sql = "UPDATE Inventory SET Status = ? WHERE InventoryID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, inventoryID);
            ps.executeUpdate();
        }
    }

}
