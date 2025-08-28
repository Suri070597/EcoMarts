package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DBContext;
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

        String sqlStockIn = "INSERT INTO StockIn (ManufacturerID, ReceiverID, DateIn, Note) VALUES (?, ?, ?, ?)";
        String sqlInsertInv = "INSERT INTO Inventory (ProductID, PackageType, Quantity, PackSize, LastUpdated) VALUES (?, ?, 0, ?, GETDATE())";
        String sqlCheckInv = "SELECT InventoryID FROM Inventory WHERE ProductID = ? AND PackageType = ? AND PackSize = ?";
        String sqlDetail = "INSERT INTO StockInDetail (StockInID, InventoryID, Quantity, UnitPrice, ExpiryDate) VALUES (?, ?, ?, ?, ?)";

        try (
                PreparedStatement psStock = conn.prepareStatement(sqlStockIn, Statement.RETURN_GENERATED_KEYS); PreparedStatement psCheckInv = conn.prepareStatement(sqlCheckInv); PreparedStatement psInsertInv = conn.prepareStatement(sqlInsertInv, Statement.RETURN_GENERATED_KEYS); PreparedStatement psDetail = conn.prepareStatement(sqlDetail)) {
            conn.setAutoCommit(false);

            // 1. Insert StockIn
            psStock.setInt(1, stockIn.getManufacturerID());
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
            System.out.println("Generated StockInID: " + stockInID);

            // 2. Loop over inventory & details
            for (int i = 0; i < invList.size(); i++) {
                Inventory inv = invList.get(i);

                int inventoryID = 0;

                // 2.1 Check if inventory exists
                psCheckInv.setInt(1, inv.getProductID());
                psCheckInv.setString(2, inv.getPackageType());
                psCheckInv.setInt(3, inv.getPackSize());
                ResultSet rsCheck = psCheckInv.executeQuery();

                if (rsCheck.next()) {
                    // Exists
                    inventoryID = rsCheck.getInt("InventoryID");
                    System.out.println("Found existing InventoryID: " + inventoryID);
                } else {
                    // Insert new inventory with Quantity = 0
                    psInsertInv.setInt(1, inv.getProductID());
                    psInsertInv.setString(2, inv.getPackageType());
                    psInsertInv.setInt(3, inv.getPackSize());
                    psInsertInv.executeUpdate();

                    ResultSet rsNewInv = psInsertInv.getGeneratedKeys();
                    if (rsNewInv.next()) {
                        inventoryID = rsNewInv.getInt(1);
                    }
                    System.out.println("Created new InventoryID: " + inventoryID);
                }

                // 2.2 Insert into StockInDetail
                StockInDetail detail = detailList.get(i);
                psDetail.setInt(1, stockInID);
                psDetail.setInt(2, inventoryID);
                psDetail.setDouble(3, detail.getQuantity());
                psDetail.setDouble(4, detail.getUnitPrice());
                psDetail.setDate(5, detail.getExpirationDate());
                psDetail.executeUpdate();

                System.out.println("Inserted StockInDetail: StockInID=" + stockInID + ", InventoryID=" + inventoryID);
            }

            conn.commit();
            System.out.println("Transaction committed successfully!");

        } catch (SQLException e) {
            conn.rollback();
            System.err.println("Transaction rolled back! Error: " + e.getMessage());
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    // Lấy tất cả StockIn
    public List<StockIn> getAllStockIns() throws SQLException {
        List<StockIn> list = new ArrayList<>();
        String sql = """
            SELECT s.StockInID, s.DateIn, s.Status,
                   m.CompanyName AS ManufacturerName,
                   r.FullName AS ReceiverName
            FROM StockIn s
            LEFT JOIN Manufacturer m ON s.ManufacturerID = m.ManufacturerID
            LEFT JOIN Account r ON s.ReceiverID = r.AccountID
            ORDER BY s.StockInID DESC
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                StockIn s = new StockIn();
                s.setStockInID(rs.getInt("StockInID"));
                s.setDateIn(rs.getDate("DateIn"));
                s.setStatus(rs.getString("Status"));
                s.setManufacturerName(rs.getString("ManufacturerName"));  // ✅
                s.setReceiverName(rs.getString("ReceiverName"));
                s.setDetails(new ArrayList<>());
                list.add(s);
            }
        }
        return list;
    }

    public List<StockIn> searchStockIn(String keyword) throws SQLException {
        List<StockIn> list = new ArrayList<>();
        String sql = "SELECT s.StockInID, s.DateIn, s.Status, "
                + "       m.CompanyName AS ManufacturerName, "
                + "       r.FullName AS ReceiverName "
                + "FROM StockIn s "
                + "LEFT JOIN Manufacturer m ON s.ManufacturerID = m.ManufacturerID "
                + "LEFT JOIN Account r ON s.ReceiverID = r.AccountID "
                + "WHERE CAST(s.StockInID AS VARCHAR(50)) LIKE ? OR m.CompanyName LIKE ? OR r.FullName LIKE ? "
                + "ORDER BY s.DateIn DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setString(3, kw);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StockIn s = new StockIn();
                    s.setStockInID(rs.getInt("StockInID"));
                    s.setDateIn(rs.getDate("DateIn"));
                    s.setStatus(rs.getString("Status"));
                    s.setManufacturerName(rs.getString("ManufacturerName"));  
                    s.setReceiverName(rs.getString("ReceiverName"));
                    s.setDetails(new ArrayList<>());
                    list.add(s);
                }
            }
        }
        return list;
    }

    public List<StockIn> getStockInByManufacturer(int manufacturerId) throws SQLException {
        List<StockIn> list = new ArrayList<>();
        String sql = """
            SELECT s.StockInID, s.DateIn, s.Status,
                   m.CompanyName AS ManufacturerName,
                   r.FullName AS ReceiverName
            FROM StockIn s
            LEFT JOIN Manufacturer m ON s.ManufacturerID = m.ManufacturerID
            LEFT JOIN Account r ON s.ReceiverID = r.AccountID
            WHERE s.ManufacturerID = ?
            ORDER BY s.DateIn DESC
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, manufacturerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StockIn s = new StockIn();
                    s.setStockInID(rs.getInt("StockInID"));
                    s.setDateIn(rs.getDate("DateIn"));
                    s.setStatus(rs.getString("Status"));
                    s.setManufacturerName(rs.getString("ManufacturerName"));  // ✅
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
        String sql = "SELECT d.InventoryID, p.ProductName, d.Quantity, d.UnitPrice, d.ExpiryDate "
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
                    d.setExpirationDate(rs.getDate("ExpiryDate"));
                    list.add(d);
                }
            }
        }
        return list;
    }

    // 3. Lấy 1 StockIn theo ID (dùng cho trang chi tiết)
    public StockIn getStockInByID(int stockInID) throws SQLException {
        StockIn stock = null;
        String sql = """
            SELECT s.StockInID, s.DateIn, s.Status,
                   m.CompanyName AS ManufacturerName,
                   r.FullName AS ReceiverName
            FROM StockIn s
            LEFT JOIN Manufacturer m ON s.ManufacturerID = m.ManufacturerID
            LEFT JOIN Account r ON s.ReceiverID = r.AccountID
            WHERE s.StockInID = ?
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, stockInID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stock = new StockIn();
                    stock.setStockInID(rs.getInt("StockInID"));
                    stock.setDateIn(rs.getDate("DateIn"));
                    stock.setStatus(rs.getString("Status"));
                    stock.setManufacturerName(rs.getString("ManufacturerName"));  // ✅
                    stock.setReceiverName(rs.getString("ReceiverName"));
                    stock.setDetails(new ArrayList<>());
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

    public void rejectStockIn(int stockInID) throws SQLException {
        try {
            conn.setAutoCommit(false);

            // Cập nhật StockIn (Canceled)
            updateStockInStatus(stockInID, "Canceled");

            conn.commit();
        } catch (SQLException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public void approveStockIn(int stockInID) throws SQLException {
        String sqlUpdateStockIn = "UPDATE StockIn SET Status = 'Completed' WHERE StockInID = ?";
        String sqlUpdateInventory
                = "UPDATE i "
                + "SET i.Quantity = i.Quantity + d.Quantity "
                + "FROM Inventory i "
                + "JOIN StockInDetail d ON i.InventoryID = d.InventoryID "
                + "WHERE d.StockInID = ?";

        try (
                PreparedStatement psStock = conn.prepareStatement(sqlUpdateStockIn); PreparedStatement psInv = conn.prepareStatement(sqlUpdateInventory)) {
            conn.setAutoCommit(false);

            // 1. Cập nhật trạng thái StockIn
            psStock.setInt(1, stockInID);
            psStock.executeUpdate();

            // 2. Cập nhật tồn kho cho tất cả Inventory liên quan
            psInv.setInt(1, stockInID);
            psInv.executeUpdate();

            conn.commit();
            System.out.println("StockIn " + stockInID + " approved and inventory updated!");
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

}
