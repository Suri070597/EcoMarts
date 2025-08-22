package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.DBContext;
import model.CartItem;
import model.Product;

/**
 * Data Access Object for CartItem operations
 */
public class CartItemDAO extends DBContext {

    /**
     * Add a product to the user's cart
     * 
     * @param accountID The ID of the account
     * @param productID The ID of the product to add
     * @param quantity  The quantity to add
     * @return true if successfully added, false otherwise
     */
    public boolean addToCart(int accountID, int productID, double quantity) {
        String sql = "INSERT INTO CartItem (AccountID, ProductID, Quantity, AddedAt, Status) VALUES (?, ?, ?, GETDATE(), 'Active')";

        try {
            // Kiểm tra kết nối
            if (conn == null || conn.isClosed()) {
                System.err.println("Connection is closed or null in addToCart");
                return false;
            }

            System.out.println("Attempting to add to cart: accountID=" + accountID + ", productID=" + productID
                    + ", quantity=" + quantity);

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, accountID);
            ps.setInt(2, productID);
            ps.setDouble(3, quantity);

            int affectedRows = ps.executeUpdate();
            ps.close();

            System.out.println("addToCart affected rows: " + affectedRows);

            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error adding item to cart: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all cart items for a specific user with a specific status
     * 
     * @param accountID The ID of the account
     * @param status    The status of cart items to retrieve (Active, SavedForLater,
     *                  Removed)
     * @return List of CartItem objects
     */
    public List<CartItem> getCartItems(int accountID, String status) {
        List<CartItem> cartItems = new ArrayList<>();

        String sql = "SELECT ci.*, p.ProductName, p.Price, p.ImageURL, p.Unit, p.BoxUnitName, p.ItemUnitName, p.UnitPerBox, p.StockQuantity, p.Status as ProductStatus "
                +
                "FROM CartItem ci " +
                "JOIN Product p ON ci.ProductID = p.ProductID " +
                "WHERE ci.AccountID = ? AND ci.Status = ? " +
                "ORDER BY ci.AddedAt DESC";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, accountID);
            ps.setString(2, status);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CartItem item = new CartItem();
                item.setCartItemID(rs.getInt("CartItemID"));
                item.setAccountID(rs.getInt("AccountID"));
                item.setProductID(rs.getInt("ProductID"));
                item.setQuantity(rs.getDouble("Quantity"));
                item.setAddedAt(rs.getTimestamp("AddedAt"));
                item.setStatus(rs.getString("Status"));

                // Create product object with essential information
                Product product = new Product();
                product.setProductID(rs.getInt("ProductID"));
                product.setProductName(rs.getString("ProductName"));
                product.setPrice(rs.getDouble("Price"));
                product.setImageURL(rs.getString("ImageURL"));
                product.setUnit(rs.getString("Unit"));
                product.setBoxUnitName(rs.getString("BoxUnitName"));
                product.setItemUnitName(rs.getString("ItemUnitName"));
                product.setUnitPerBox(rs.getInt("UnitPerBox"));
                product.setStockQuantity(rs.getDouble("StockQuantity"));
                product.setStatus(rs.getString("ProductStatus"));

                // Set the product to the cart item
                item.setProduct(product);

                cartItems.add(item);
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error getting cart items: " + e.getMessage());
        }

        return cartItems;
    }

    /**
     * Get a specific cart item by its ID
     * 
     * @param cartItemID The ID of the cart item
     * @return CartItem object or null if not found
     */
    public CartItem getCartItemById(int cartItemID) {
        String sql = "SELECT ci.*, p.ProductName, p.Price, p.ImageURL, p.Unit, p.BoxUnitName, p.ItemUnitName, p.UnitPerBox, p.StockQuantity, p.Status as ProductStatus "
                +
                "FROM CartItem ci " +
                "JOIN Product p ON ci.ProductID = p.ProductID " +
                "WHERE ci.CartItemID = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, cartItemID);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                CartItem item = new CartItem();
                item.setCartItemID(rs.getInt("CartItemID"));
                item.setAccountID(rs.getInt("AccountID"));
                item.setProductID(rs.getInt("ProductID"));
                item.setQuantity(rs.getDouble("Quantity"));
                item.setAddedAt(rs.getTimestamp("AddedAt"));
                item.setStatus(rs.getString("Status"));

                // Create product object with essential information
                Product product = new Product();
                product.setProductID(rs.getInt("ProductID"));
                product.setProductName(rs.getString("ProductName"));
                product.setPrice(rs.getDouble("Price"));
                product.setImageURL(rs.getString("ImageURL"));
                product.setUnit(rs.getString("Unit"));
                product.setBoxUnitName(rs.getString("BoxUnitName"));
                product.setItemUnitName(rs.getString("ItemUnitName"));
                product.setUnitPerBox(rs.getInt("UnitPerBox"));
                product.setStockQuantity(rs.getDouble("StockQuantity"));
                product.setStatus(rs.getString("ProductStatus"));

                // Set the product to the cart item
                item.setProduct(product);

                rs.close();
                ps.close();
                return item;
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error getting cart item by ID: " + e.getMessage());
        }

        return null;
    }

    /**
     * Get a cart item by product ID for a specific user and status
     * 
     * @param accountID The ID of the account
     * @param productID The ID of the product
     * @param status    The status to check for (Active, SavedForLater, Removed)
     * @return CartItem object or null if not found
     */
    public CartItem getCartItemByProductId(int accountID, int productID, String status) {
        String sql = "SELECT * FROM CartItem WHERE AccountID = ? AND ProductID = ? AND Status = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, accountID);
            ps.setInt(2, productID);
            ps.setString(3, status);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                CartItem item = new CartItem();
                item.setCartItemID(rs.getInt("CartItemID"));
                item.setAccountID(rs.getInt("AccountID"));
                item.setProductID(rs.getInt("ProductID"));
                item.setQuantity(rs.getDouble("Quantity"));
                item.setAddedAt(rs.getTimestamp("AddedAt"));
                item.setStatus(rs.getString("Status"));

                rs.close();
                ps.close();
                return item;
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error getting cart item by product ID: " + e.getMessage());
        }

        return null;
    }

    /**
     * Update the quantity of a cart item
     * 
     * @param cartItemID  The ID of the cart item
     * @param newQuantity The new quantity
     * @return true if successfully updated, false otherwise
     */
    public boolean updateCartItemQuantity(int cartItemID, double newQuantity) {
        String sql = "UPDATE CartItem SET Quantity = ? WHERE CartItemID = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setDouble(1, newQuantity);
            ps.setInt(2, cartItemID);

            int affectedRows = ps.executeUpdate();
            ps.close();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating cart item quantity: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update the status of a cart item
     * 
     * @param cartItemID The ID of the cart item
     * @param status     The new status
     * @return true if successfully updated, false otherwise
     */
    public boolean updateCartItemStatus(int cartItemID, String status) {
        String sql = "UPDATE CartItem SET Status = ? WHERE CartItemID = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, cartItemID);

            int affectedRows = ps.executeUpdate();
            ps.close();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating cart item status: " + e.getMessage());
            return false;
        }
    }

    /**
     * Clear all items from a user's cart by updating status
     * 
     * @param accountID     The ID of the account
     * @param currentStatus The current status of items to update
     * @param newStatus     The new status for the items
     * @return true if successfully cleared, false otherwise
     */
    public boolean updateCartItemsStatus(int accountID, String currentStatus, String newStatus) {
        String sql = "UPDATE CartItem SET Status = ? WHERE AccountID = ? AND Status = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newStatus);
            ps.setInt(2, accountID);
            ps.setString(3, currentStatus);

            int affectedRows = ps.executeUpdate();
            ps.close();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error clearing cart: " + e.getMessage());
            return false;
        }
    }

    /**
     * Count the number of items in a user's cart with a specific status
     * 
     * @param accountID The ID of the account
     * @param status    The status of items to count
     * @return The number of items
     */
    public int countCartItems(int accountID, String status) {
        String sql = "SELECT COUNT(*) as ItemCount FROM CartItem WHERE AccountID = ? AND Status = ?";

        try {
            // Kiểm tra kết nối
            if (conn == null || conn.isClosed()) {
                System.err.println("Connection is closed or null in countCartItems");
                return 0;
            }

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, accountID);
            ps.setString(2, status);

            System.out.println("Executing query: " + sql + " with accountID=" + accountID + ", status=" + status);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt("ItemCount");
                System.out.println(
                        "Found " + count + " items in cart for accountID=" + accountID + " with status=" + status);
                rs.close();
                ps.close();
                return count;
            } else {
                System.out.println("No results returned from count query");
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error counting cart items: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * Check if the product stock quantity is sufficient for the requested quantity
     * 
     * @param productID         The ID of the product
     * @param requestedQuantity The quantity requested
     * @return true if stock is sufficient, false otherwise
     */
    public boolean isStockSufficient(int productID, double requestedQuantity) {
        String sql = "SELECT StockQuantity FROM Product WHERE ProductID = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, productID);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                double stockQuantity = rs.getDouble("StockQuantity");
                boolean isEnough = stockQuantity >= requestedQuantity;

                rs.close();
                ps.close();
                return isEnough;
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            System.err.println("Error checking stock: " + e.getMessage());
        }

        return false;
    }
    
    public void upsertCartItem(int accountID, int productID, double quantity) {
        String select = "SELECT Quantity FROM CartItem WHERE AccountID = ? AND ProductID = ? AND Status = N'Active'";
        String update = "UPDATE CartItem SET Quantity = Quantity + ? WHERE AccountID = ? AND ProductID = ? AND Status = N'Active'";
        String insert = "INSERT INTO CartItem (AccountID, ProductID, Quantity, AddedAt, Status) VALUES (?, ?, ?, GETDATE(), N'Active')";

        try {
            PreparedStatement ps = conn.prepareStatement(select);
            ps.setInt(1, accountID);
            ps.setInt(2, productID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Nếu đã tồn tại thì cập nhật số lượng
                PreparedStatement ups = conn.prepareStatement(update);
                ups.setDouble(1, quantity);
                ups.setInt(2, accountID);
                ups.setInt(3, productID);
                ups.executeUpdate();
                ups.close();
            } else {
                // Nếu chưa có thì thêm mới
                PreparedStatement ins = conn.prepareStatement(insert);
                ins.setInt(1, accountID);
                ins.setInt(2, productID);
                ins.setDouble(3, quantity);
                ins.executeUpdate();
                ins.close();
            }
            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Get all cart items for a specific user with active status
     * 
     * @param accountID The ID of the account
     * @param isSavedForLater Whether to get saved for later items
     * @return List of CartItem objects
     */
    public List<CartItem> getCartByAccountId(int accountID, boolean isSavedForLater) {
        String status = isSavedForLater ? "SavedForLater" : "Active";
        return getCartItems(accountID, status);
    }
    
    /**
     * Remove a cart item completely
     * 
     * @param cartItemID The ID of the cart item to remove
     * @return true if successfully removed, false otherwise
     */
    public boolean removeCartItem(int cartItemID) {
        String sql = "DELETE FROM CartItem WHERE CartItemID = ?";
        
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, cartItemID);
            
            int affectedRows = ps.executeUpdate();
            ps.close();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error removing cart item: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}