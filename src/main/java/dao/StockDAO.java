package dao;

import db.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StockDAO {

    /**
     * Thêm bản ghi nhập kho vào Inventory
     * @param productId ID sản phẩm
     * @param quantity số lượng nhập
     */
    public void addStockIn(int productId, double quantity) throws SQLException {
        String sql = """
            INSERT INTO Inventory (ProductID, Quantity, LastUpdated)
            VALUES (?, ?, GETDATE())
        """;

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);
            ps.setDouble(2, quantity);
            ps.executeUpdate();
        }
    }

    /**
     * Cập nhật số lượng tồn kho của sản phẩm
     * @param productId ID sản phẩm
     * @param quantity số lượng cần cộng thêm
     */
    public void updateProductStock(int productId, double quantity) throws SQLException {
        String sql = """
            UPDATE Product 
            SET StockQuantity = StockQuantity + ? 
            WHERE ProductID = ?
        """;

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, quantity);
            ps.setInt(2, productId);
            ps.executeUpdate();
        }
    }

    /**
     * Thêm nhập kho và cập nhật số lượng sản phẩm
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
}
