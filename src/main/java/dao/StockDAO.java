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
     * @param quantity  số lượng nhập
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
     * @param quantity  số lượng cần cộng thêm
     */
    public void updateProductStock(int productId, double quantity) throws SQLException {
        String sql = "UPDATE Inventory SET Quantity = Quantity + ? WHERE ProductID = ? AND PackageType = 'BOX'";

        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, quantity);
            ps.setInt(2, productId);
            ps.executeUpdate();
        }
    }

    /**
     * Thêm nhập kho và cập nhật số lượng sản phẩm
     *
     * @param productId  ID sản phẩm
     * @param quantity   số lượng nhập
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

    // Test method để kiểm tra kết nối database
    public boolean testConnection() {
        try {
            String sql = "SELECT 1 as test";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                    ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt("test") == 1;
            }
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }

    public void createStockInFull(StockIn stockIn,
            List<Inventory> invList,
            List<StockInDetail> detailList) throws SQLException {

        String sqlStockIn = "INSERT INTO StockIn (ManufacturerID, ReceiverID, DateIn, Note, Status) VALUES (?, ?, ?, ?, ?)";
        // KHÔNG cập nhật Inventory khi tạo StockIn - chỉ tạo StockInDetail
        // Inventory sẽ được cập nhật khi ADMIN DUYỆT phiếu nhập kho
        String sqlDetail = "INSERT INTO StockInDetail (StockInID, Quantity, UnitPrice, ProductID, PackageType, LotNumber, ManufactureDate, ExpiryDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement psStock = null;
        PreparedStatement psDetail = null;
        ResultSet rsStock = null;

        try {
            conn.setAutoCommit(false);

            // 1. Insert StockIn
            psStock = conn.prepareStatement(sqlStockIn, Statement.RETURN_GENERATED_KEYS);
            psStock.setInt(1, stockIn.getManufacturerID());
            psStock.setInt(2, stockIn.getReceiverID());
            psStock.setDate(3, stockIn.getDateIn());
            psStock.setString(4, stockIn.getNote());
            psStock.setString(5, "Pending"); // Set status mặc định là Pending
            int rowsStock = psStock.executeUpdate();
            System.out.println("Inserted StockIn rows: " + rowsStock);

            rsStock = psStock.getGeneratedKeys();
            int stockInID = 0;
            if (rsStock.next()) {
                stockInID = rsStock.getInt(1);
            }
            stockIn.setStockInID(stockInID);
            System.out.println("Generated StockInID: " + stockInID);

            // 2. Prepare statements
            psDetail = conn.prepareStatement(sqlDetail);

            // 3. Loop over lists - CHỈ tạo StockInDetail, KHÔNG tạo Inventory
            for (int i = 0; i < invList.size(); i++) {
                Inventory inv = invList.get(i);
                StockInDetail detail = detailList.get(i);

                // CHỈ tạo StockInDetail (không cần InventoryID ban đầu)
                // Inventory sẽ được tạo khi Admin duyệt
                psDetail.setInt(1, stockInID);
                psDetail.setDouble(2, inv.getQuantity()); // Số lượng dự kiến nhập
                psDetail.setDouble(3, inv.getCostPrice() != null ? inv.getCostPrice() : 0.0); // Giá nhập kho
                psDetail.setInt(4, inv.getProductID()); // ProductID
                psDetail.setString(5, inv.getPackageType()); // PackageType

                // Sử dụng LotNumber từ form hoặc tạo mới nếu không có
                String lotNumber = detail.getLotNumber();
                if (lotNumber == null || lotNumber.trim().isEmpty()) {
                    lotNumber = "LOT_" + stockInID + "_" + System.currentTimeMillis();
                }
                psDetail.setString(6, lotNumber); // LotNumber
                psDetail.setDate(7, detail.getManufactureDate()); // ManufactureDate
                psDetail.setDate(8, detail.getExpiryDate()); // ExpiryDate
                int rowsDetail = psDetail.executeUpdate();
                System.out.println("Inserted StockInDetail rows: " + rowsDetail
                        + " | StockInID=" + stockInID
                        + " | InventoryID=0 (pending approval)");
            }

            conn.commit();
            System.out.println("Transaction committed successfully!");

        } catch (SQLException e) {
            conn.rollback();
            System.err.println("Transaction rolled back! Error: " + e.getMessage());
            throw e;
        } finally {
            // Đóng resources
            if (psDetail != null) {
                try {
                    psDetail.close();
                } catch (SQLException e) {
                    System.err.println("Error closing psDetail: " + e.getMessage());
                }
            }
            if (psStock != null) {
                try {
                    psStock.close();
                } catch (SQLException e) {
                    System.err.println("Error closing psStock: " + e.getMessage());
                }
            }
            // Đóng ResultSet nếu có
            if (rsStock != null) {
                try {
                    rsStock.close();
                } catch (SQLException e) {
                    System.err.println("Error closing rsStock: " + e.getMessage());
                }
            }
            conn.setAutoCommit(true);
        }
    }

    // Lấy tất cả StockIn
    public List<StockIn> getAllStockIns() throws SQLException {
        List<StockIn> list = new ArrayList<>();
        String sql = """
                    SELECT s.StockInID, s.DateIn, s.Status, m.CompanyName AS ManufacturerName, r.FullName AS ReceiverName
                    FROM StockIn s
                    JOIN Manufacturer m ON s.ManufacturerID = m.ManufacturerID
                    JOIN Account r ON s.ReceiverID = r.AccountID
                    ORDER BY s.StockInID DESC
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                StockIn s = new StockIn();
                s.setStockInID(rs.getInt("StockInID"));
                s.setDateIn(rs.getDate("DateIn"));
                s.setStatus(rs.getString("Status"));
                s.setManufacturerName(rs.getString("ManufacturerName")); // ✅
                s.setReceiverName(rs.getString("ReceiverName"));
                s.setDetails(new ArrayList<>());
                list.add(s);
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
                    JOIN Manufacturer m ON s.ManufacturerID = m.ManufacturerID
                    JOIN Account r ON s.ReceiverID = r.AccountID
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
                    s.setManufacturerName(rs.getString("ManufacturerName")); // ✅
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
        String sql = "SELECT d.StockInDetailID, d.InventoryID, d.ProductID, d.PackageType, d.Quantity, d.UnitPrice, d.LotNumber, d.ManufactureDate, d.ExpiryDate "
                + "FROM StockInDetail d "
                + "WHERE d.StockInID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, stockInID);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StockInDetail d = new StockInDetail();
                    d.setStockInDetailID(rs.getInt("StockInDetailID"));
                    d.setInventoryID(rs.getInt("InventoryID"));
                    d.setProductID(rs.getInt("ProductID"));
                    d.setPackageType(rs.getString("PackageType"));
                    // Bỏ PackSize - không cần thiết
                    d.setQuantity(rs.getDouble("Quantity"));
                    d.setUnitPrice(rs.getDouble("UnitPrice"));
                    d.setLotNumber(rs.getString("LotNumber"));
                    d.setManufactureDate(rs.getDate("ManufactureDate"));
                    d.setExpiryDate(rs.getDate("ExpiryDate"));
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
                    SELECT s.StockInID, s.DateIn, s.Status, m.CompanyName AS ManufacturerName, r.FullName AS ReceiverName
                    FROM StockIn s
                    JOIN Manufacturer m ON s.ManufacturerID = m.ManufacturerID
                    JOIN Account r ON s.ReceiverID = r.AccountID
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
                    stock.setManufacturerName(rs.getString("ManufacturerName")); // ✅
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

    public void rejectStockIn(int stockInID, List<StockInDetail> details) throws SQLException {
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

    public void approveStockIn(int stockInID, List<StockInDetail> details) throws SQLException {
        try {
            conn.setAutoCommit(false);

            // Cập nhật trạng thái StockIn
            updateStockInStatus(stockInID, "Completed");

            // Tạo Inventory records khi duyệt StockIn
            for (StockInDetail detail : details) {
                // Lấy thông tin từ StockInDetail
                String sqlGetProductInfo = "SELECT ProductID, PackageType, LotNumber, ManufactureDate, ExpiryDate FROM StockInDetail WHERE StockInDetailID = ?";

                int productID = 0;
                String packageType = "";
                String lotNumber = "";
                java.sql.Date manufactureDate = null;
                java.sql.Date expiryDate = null;

                try (PreparedStatement psInfo = conn.prepareStatement(sqlGetProductInfo)) {
                    psInfo.setInt(1, detail.getStockInDetailID());
                    try (ResultSet rs = psInfo.executeQuery()) {
                        if (rs.next()) {
                            productID = rs.getInt("ProductID");
                            packageType = rs.getString("PackageType");
                            lotNumber = rs.getString("LotNumber");
                            manufactureDate = rs.getDate("ManufactureDate");
                            expiryDate = rs.getDate("ExpiryDate");
                        }
                    }
                }

                // Tạo Inventory record mới với thông tin lô hàng
                String sqlCreateInventory = "INSERT INTO Inventory (ProductID, PackageType, Quantity, UnitPrice, CostPrice, LotNumber, ExpiryDate, LotDate) VALUES (?, ?, ?, NULL, ?, ?, ?, GETDATE())";
                int inventoryID = 0;

                try (PreparedStatement psCreate = conn.prepareStatement(sqlCreateInventory,
                        Statement.RETURN_GENERATED_KEYS)) {
                    psCreate.setInt(1, productID);
                    psCreate.setString(2, packageType);
                    psCreate.setDouble(3, detail.getQuantity()); // Số lượng thực tế
                    psCreate.setDouble(4, detail.getUnitPrice()); // CostPrice
                    psCreate.setString(5, lotNumber); // LotNumber
                    psCreate.setDate(6, expiryDate); // ExpiryDate
                    // Bỏ RemainingQuantity - không cần thiết
                    psCreate.executeUpdate();

                    ResultSet rsCreate = psCreate.getGeneratedKeys();
                    if (rsCreate.next()) {
                        inventoryID = rsCreate.getInt(1);
                    }
                }

                // Cập nhật StockInDetail với InventoryID mới
                String sqlUpdateDetail = "UPDATE StockInDetail SET InventoryID = ? WHERE StockInDetailID = ?";
                try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdateDetail)) {
                    psUpdate.setInt(1, inventoryID);
                    psUpdate.setInt(2, detail.getStockInDetailID());
                    psUpdate.executeUpdate();
                }

                System.out.println("Created Inventory ID " + inventoryID + " with " + detail.getQuantity()
                        + " units, CostPrice: " + detail.getUnitPrice());
            }

            conn.commit();
            System.out.println("StockIn approval completed successfully");
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

    /**
     * Lấy Inventory để bán hàng - đơn giản hóa, không cần FIFO phức tạp
     */
    public List<Inventory> getInventoryForSale(int productId, double quantity) throws SQLException {
        List<Inventory> result = new ArrayList<>();

        // Lấy tất cả Inventory có sẵn
        String sql = """
                SELECT * FROM Inventory
                WHERE ProductID = ? AND PackageType = 'BOX' AND Quantity > 0
                ORDER BY LotDate ASC
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Inventory inv = new Inventory();
                    inv.setInventoryID(rs.getInt("InventoryID"));
                    inv.setProductID(rs.getInt("ProductID"));
                    inv.setPackageType(rs.getString("PackageType"));
                    inv.setQuantity(rs.getDouble("Quantity"));
                    inv.setCostPrice(rs.getDouble("CostPrice"));
                    inv.setUnitPrice(rs.getDouble("UnitPrice"));
                    inv.setLotNumber(rs.getString("LotNumber"));
                    result.add(inv);
                }
            }
        }

        return result;
    }

    /**
     * Cập nhật số lượng sau khi bán hàng - đơn giản hóa
     */
    public void updateInventoryAfterSale(int inventoryId, double soldQuantity) throws SQLException {
        String sql = "UPDATE Inventory SET Quantity = Quantity - ? WHERE InventoryID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, soldQuantity);
            ps.setInt(2, inventoryId);
            ps.executeUpdate();
        }
    }

}
