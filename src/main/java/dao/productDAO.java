package dao;

import java.sql.PreparedStatement;

import java.sql.ResultSet;

import java.sql.Timestamp;

import java.text.ParseException;

import java.util.ArrayList;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import db.DBContext;
import java.sql.SQLException;
import java.util.Collections;
import model.Category;
import model.InventoryTransaction;
import model.Product;
import model.Manufacturer;

public class ProductDAO extends DBContext {

    public List<Product> getAll() {

        List<Product> list = new ArrayList<>();

        String sql = "SELECT p.*, c.categoryName, c.parentID, s.CompanyName FROM Product p \n"
                + "                                JOIN Category c ON p.categoryID = c.categoryID \n"
                + "                               JOIN Manufacturer s ON p.manufacturerID = s.manufacturerID";
        try {

            PreparedStatement ps = conn.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Category cat = new Category();
                cat.setCategoryID(rs.getInt("categoryID"));
                cat.setCategoryName(rs.getString("categoryName"));
                cat.setParentID(rs.getInt("parentID"));
                Manufacturer sup = new Manufacturer();
                sup.setManufacturerID(rs.getInt("manufacturerID"));
                sup.setCompanyName(rs.getString("CompanyName"));
                Product p = new Product();
                p.setProductID(rs.getInt("productID"));
                p.setProductName(rs.getString("productName"));
                p.setPrice(rs.getDouble("price"));
                p.setDescription(rs.getString("description"));
                p.setImageURL(rs.getString("ImageURL"));
                p.setCreatedAt(rs.getTimestamp("createdAt"));
                p.setCategory(cat);
                p.setManufacturer(sup);
                // Bổ sung các trường đóng gói
                p.setUnitPerBox(rs.getInt("UnitPerBox"));
                p.setBoxUnitName(rs.getString("BoxUnitName"));
                p.setItemUnitName(rs.getString("ItemUnitName"));
                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ====================
    // Stock status counters for admin product dashboard
    // ====================

    /**
     * Đếm số sản phẩm còn hàng (số lượng > lowStockThreshold)
     */
    public int countInStock(int lowStockThreshold) {
        // Count products with any inventory greater than threshold
        String sql = "SELECT COUNT(DISTINCT i.ProductID) AS cnt FROM Inventory i WHERE i.Quantity > ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lowStockThreshold);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Đếm số sản phẩm gần hết hàng (1..lowStockThreshold)
     */
    public int countLowStock(int lowStockThreshold) {
        // Count products with low inventory (between 0 and threshold)
        String sql = "SELECT COUNT(DISTINCT i.ProductID) AS cnt FROM Inventory i WHERE i.Quantity > 0 AND i.Quantity <= ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lowStockThreshold);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Đếm số sản phẩm hết hàng (số lượng = 0)
     */
    public int countOutOfStock() {
        // Count products with no inventory or zero quantity
        String sql = "SELECT COUNT(DISTINCT p.ProductID) AS cnt FROM Product p LEFT JOIN Inventory i ON p.ProductID = i.ProductID WHERE i.ProductID IS NULL OR i.Quantity <= 0";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("cnt");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Product> getAllIncludingOutOfStock() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.categoryName, c.parentID, s.CompanyName FROM Product p \n"
                + "                                JOIN Category c ON p.categoryID = c.categoryID \n"
                + "                               JOIN Manufacturer s ON p.manufacturerID = s.manufacturerID";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Category cat = new Category();
                cat.setCategoryID(rs.getInt("categoryID"));
                cat.setCategoryName(rs.getString("categoryName"));
                cat.setParentID(rs.getInt("parentID"));
                Manufacturer sup = new Manufacturer();
                sup.setManufacturerID(rs.getInt("manufacturerID"));
                sup.setCompanyName(rs.getString("CompanyName"));
                Product p = new Product();
                p.setProductID(rs.getInt("productID"));
                p.setProductName(rs.getString("productName"));
                p.setPrice(rs.getDouble("price"));
                p.setDescription(rs.getString("description"));
                p.setImageURL(rs.getString("ImageURL"));
                p.setCreatedAt(rs.getTimestamp("createdAt"));
                p.setCategory(cat);
                p.setManufacturer(sup);
                // Bổ sung các trường đóng gói
                p.setUnitPerBox(rs.getInt("UnitPerBox"));
                p.setBoxUnitName(rs.getString("BoxUnitName"));
                p.setItemUnitName(rs.getString("ItemUnitName"));
                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int insert(String name, Double price, String description,
            String ImageURL, Timestamp createdAt,
            int categoryID, int manufacturerID,
            int unitPerBox, String boxUnitName, String itemUnitName) {
        String sql = "INSERT INTO Product (productName, price, description, ImageURL, createdAt, categoryID, manufacturerID, UnitPerBox, BoxUnitName, ItemUnitName) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setObject(2, price); // Có thể NULL
            ps.setString(3, description);
            ps.setString(4, ImageURL);
            ps.setTimestamp(5, createdAt);
            ps.setInt(6, categoryID);
            ps.setInt(7, manufacturerID);
            ps.setInt(8, unitPerBox);
            ps.setString(9, boxUnitName);
            ps.setString(10, itemUnitName);

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                int productId = -1;
                if (rs.next()) {
                    productId = rs.getInt(1);
                    // Không tạo inventory ngay - sẽ được tạo khi nhập kho
                }
                return productId > 0 ? 1 : 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Product getProductById(int id) {
        Product product = null;
        String sql = "SELECT p.*, \n"
                + "       c.CategoryName, c.ParentID, \n"
                + "       s.ManufacturerID, s.CompanyName\n"
                + "FROM product p\n"
                + "LEFT JOIN Category c ON p.CategoryID = c.CategoryID\n"
                + "LEFT JOIN Manufacturer s ON p.ManufacturerID = s.ManufacturerID\n"
                + "WHERE p.ProductID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // Product info
                String proName = rs.getString("ProductName");
                double proPrice = rs.getDouble("Price");
                String description = rs.getString("Description");
                String imageURL = rs.getString("ImageURL");
                Timestamp createdAt = rs.getTimestamp("CreatedAt");
                // Removed ManufactureDate and ExpirationDate from Product table
                int categoryId = rs.getInt("CategoryID");
                String categoryName = rs.getString("CategoryName");
                int parentId = rs.getInt("ParentID");
                Category category = new Category(categoryId, categoryName, parentId, rs.getString("ImageURL"));
                int manufacturerId = rs.getInt("ManufacturerID");
                String manufacturerName = rs.getString("CompanyName");
                Manufacturer manufacturer = new Manufacturer(manufacturerId, manufacturerName);
                // Bỏ inventory info - không cần thiết cho set-price
                product = new Product();
                product.setProductID(id);
                product.setProductName(proName);
                product.setPrice(proPrice);
                product.setDescription(description);
                product.setImageURL(imageURL);
                product.setCreatedAt(createdAt);
                product.setCategory(category);
                product.setManufacturer(manufacturer);
                // Bỏ inventory - không cần thiết cho set-price
                product.setUnitPerBox(rs.getInt("UnitPerBox"));
                product.setBoxUnitName(rs.getString("BoxUnitName"));
                product.setItemUnitName(rs.getString("ItemUnitName"));
            }

        } catch (Exception e) {

        }
        return product;
    }

    //
    public boolean delete(int id) {
        try {
            // Bắt đầu transaction
            conn.setAutoCommit(false);

            // Thứ tự xóa theo FK constraints (child tables trước, parent tables sau)
            // Xóa theo thứ tự: StockInDetail -> StockIn -> CartItem -> Review ->
            // Product_Promotion -> Inventory -> Product

            // 1. Xóa StockInDetail (tham chiếu đến Inventory)
            String sqlStockInDetail = "DELETE FROM StockInDetail WHERE InventoryID IN (SELECT InventoryID FROM Inventory WHERE ProductID = ?)";
            try (PreparedStatement ps1 = conn.prepareStatement(sqlStockInDetail)) {
                ps1.setInt(1, id);
                int deleted1 = ps1.executeUpdate();
                System.out.println("Deleted StockInDetail rows: " + deleted1);
            }

            // 2. Xóa StockIn records không còn StockInDetail nào (orphaned records)
            String sqlStockIn = "DELETE FROM StockIn WHERE StockInID NOT IN (SELECT DISTINCT StockInID FROM StockInDetail)";
            try (PreparedStatement ps2 = conn.prepareStatement(sqlStockIn)) {
                int deleted2 = ps2.executeUpdate();
                System.out.println("Deleted orphaned StockIn rows: " + deleted2);
            }

            // 3. Xóa CartItem
            String sqlCartItem = "DELETE FROM CartItem WHERE ProductID = ?";
            try (PreparedStatement ps3 = conn.prepareStatement(sqlCartItem)) {
                ps3.setInt(1, id);
                int deleted3 = ps3.executeUpdate();
                System.out.println("Deleted CartItem rows: " + deleted3);
            }

            // 4. Xóa Review
            String sqlReview = "DELETE FROM Review WHERE ProductID = ?";
            try (PreparedStatement ps4 = conn.prepareStatement(sqlReview)) {
                ps4.setInt(1, id);
                int deleted4 = ps4.executeUpdate();
                System.out.println("Deleted Review rows: " + deleted4);
            }

            // 5. Xóa Product_Promotion
            String sqlPromotion = "DELETE FROM Product_Promotion WHERE ProductID = ?";
            try (PreparedStatement ps5 = conn.prepareStatement(sqlPromotion)) {
                ps5.setInt(1, id);
                int deleted5 = ps5.executeUpdate();
                System.out.println("Deleted Product_Promotion rows: " + deleted5);
            }

            // 6. Kiểm tra OrderDetail - KHÔNG xóa nếu đã có đơn hàng
            String sqlCheckOrder = "SELECT COUNT(*) FROM OrderDetail WHERE ProductID = ?";
            try (PreparedStatement psCheck = conn.prepareStatement(sqlCheckOrder)) {
                psCheck.setInt(1, id);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        System.out.println("Cannot delete product: Found " + rs.getInt(1) + " order records");
                        conn.rollback();
                        return false; // Không thể xóa vì đã có đơn hàng
                    }
                }
            }

            // 7. Xóa Inventory
            String sqlInventory = "DELETE FROM Inventory WHERE ProductID = ?";
            try (PreparedStatement ps6 = conn.prepareStatement(sqlInventory)) {
                ps6.setInt(1, id);
                int deleted6 = ps6.executeUpdate();
                System.out.println("Deleted Inventory rows: " + deleted6);
            }

            // 8. Xóa Product (cuối cùng)
            String sqlProduct = "DELETE FROM Product WHERE ProductID = ?";
            try (PreparedStatement ps7 = conn.prepareStatement(sqlProduct)) {
                ps7.setInt(1, id);
                int deleted7 = ps7.executeUpdate();
                System.out.println("Deleted Product rows: " + deleted7);

                if (deleted7 > 0) {
                    conn.commit();
                    System.out.println("Product deletion completed successfully");
                    return true;
                } else {
                    conn.rollback();
                    System.out.println("Product not found");
                    return false;
                }
            }

        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (Exception rollbackEx) {
                rollbackEx.printStackTrace();
            }
            System.out.println("Error deleting product: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean update(Product product) {
        String sql = "UPDATE Product SET "
                + "ProductName = ?, "
                + "Price = ?, "
                + "Description = ?, "
                + "ImageURL = ?, "
                + "CreatedAt = ?, "
                + "CategoryID = ?, "
                + "ManufacturerID = ?, "
                + "UnitPerBox = ?, "
                + "BoxUnitName = ?, "
                + "ItemUnitName = ? "
                + "WHERE ProductID = ?";
        double price = product.getPrice();
        if (price < 1000) {
            price *= 1000;
        }
        try {

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, product.getProductName());
            ps.setDouble(2, price);
            ps.setString(3, product.getDescription());
            ps.setString(4, product.getImageURL());
            ps.setTimestamp(5, product.getCreatedAt());
            ps.setInt(6, product.getCategory().getCategoryID());
            ps.setInt(7, product.getManufacturer().getManufacturerId());
            ps.setInt(8, product.getUnitPerBox());
            ps.setString(9, product.getBoxUnitName());
            ps.setString(10, product.getItemUnitName());
            ps.setInt(11, product.getProductID());
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Category> getCategory() {

        List<Category> list = new ArrayList<>();

        String sql = "SELECT * FROM Category";

        try {

            PreparedStatement st = conn.prepareStatement(sql);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {

                Category c = new Category();

                c.setCategoryID(rs.getInt("CategoryID"));

                c.setCategoryName(rs.getString("CategoryName"));

                c.setParentID(rs.getInt("ParentID"));

                list.add(c);

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return list;

    }

    public List<Manufacturer> getAllManufacturers() {
        List<Manufacturer> list = new ArrayList<>();
        String sql = "SELECT * FROM Manufacturer";

        try {

            PreparedStatement st = conn.prepareStatement(sql);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Manufacturer s = new Manufacturer();
                s.setManufacturerID(rs.getInt("ManufacturerID"));
                s.setCompanyName(rs.getString("CompanyName"));
                try {
                    s.setStatus(rs.getInt("Status"));
                } catch (Exception ignore) {
                }
                list.add(s);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Manufacturer> getActiveManufacturers() {
        List<Manufacturer> list = new ArrayList<>();
        String sql = "SELECT ManufacturerID, CompanyName, [Status] FROM Manufacturer WHERE [Status] = 1 ORDER BY CompanyName";

        try {

            PreparedStatement st = conn.prepareStatement(sql);

            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Manufacturer s = new Manufacturer();
                s.setManufacturerID(rs.getInt("ManufacturerID"));
                s.setCompanyName(rs.getString("CompanyName"));
                try {
                    s.setStatus(rs.getInt("Status"));
                } catch (Exception ignore) {
                }
                list.add(s);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Product> searchProductsByName(String keyword) {

        List<Product> list = new ArrayList<>();
        String sql = """
                    SELECT p.*, c.CategoryName, c.ParentID, s.ManufacturerID, s.CompanyName
                    FROM Product p
                    JOIN Category c ON p.CategoryID = c.CategoryID
                    JOIN Manufacturer s ON p.ManufacturerID = s.ManufacturerID
                    WHERE p.ProductName LIKE ?
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Product p = new Product();
                p.setProductID(rs.getInt("ProductID"));
                p.setProductName(rs.getString("ProductName"));
                p.setPrice(rs.getDouble("Price"));
                p.setDescription(rs.getString("Description"));
                // StockQuantity is now handled in Inventory table
                p.setImageURL(rs.getString("ImageURL"));
                // Unit is now handled by ItemUnitName/BoxUnitName
                p.setCreatedAt(rs.getTimestamp("CreatedAt"));
                p.setStatus(rs.getString("Status"));
                // Set Category
                Category c = new Category();
                c.setCategoryID(rs.getInt("CategoryID"));
                c.setCategoryName(rs.getString("CategoryName"));
                c.setParentID(rs.getInt("ParentID"));
                p.setCategory(c);
                // Set Manufacturer
                Manufacturer s = new Manufacturer();
                s.setManufacturerID(rs.getInt("ManufacturerID"));
                s.setCompanyName(rs.getString("CompanyName"));
                p.setManufacturer(s);
                p.setUnitPerBox(rs.getInt("UnitPerBox"));
                p.setBoxUnitName(rs.getString("BoxUnitName"));
                p.setItemUnitName(rs.getString("ItemUnitName"));
                list.add(p);
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
        return list;
    }

    public List<Product> searchProductsByName1(String keyword) {
        List<Product> list = new ArrayList<>();
        String sql = """
                    SELECT p.*, c.CategoryName, c.ParentID, m.ManufacturerID, m.CompanyName
                    FROM Product p
                    JOIN Category c ON p.CategoryID = c.CategoryID
                    JOIN Manufacturer m ON p.ManufacturerID = m.ManufacturerID
                    WHERE p.ProductName LIKE ?
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Product p = new Product();
                p.setProductID(rs.getInt("ProductID"));
                p.setProductName(rs.getString("ProductName"));
                p.setPrice(rs.getDouble("Price"));
                p.setDescription(rs.getString("Description"));
                // StockQuantity is now handled in Inventory table
                p.setImageURL(rs.getString("ImageURL"));
                // Unit is now handled by ItemUnitName/BoxUnitName
                p.setCreatedAt(rs.getTimestamp("CreatedAt"));
                p.setStatus(rs.getString("Status"));
                p.setBoxUnitName(rs.getString("BoxUnitName"));

                // Set Category
                Category c = new Category();
                c.setCategoryID(rs.getInt("CategoryID"));
                c.setCategoryName(rs.getString("CategoryName"));
                c.setParentID(rs.getInt("ParentID"));
                p.setCategory(c);

                // Set Manufacturer
                Manufacturer m = new Manufacturer();
                m.setManufacturerID(rs.getInt("ManufacturerID"));
                m.setCompanyName(rs.getString("CompanyName"));
                p.setManufacturer(m);

                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Product> getRelatedProductsByParentCategory(int parentId, int excludeProductId) {

        List<Product> list = new ArrayList<>();

        String sql = "SELECT p.* FROM Product p "
                + "JOIN Category c ON p.CategoryID = c.CategoryID "
                + "WHERE c.ParentID = ? AND p.ProductID != ? AND p.StockQuantity > 0";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, parentId);

            ps.setInt(2, excludeProductId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Product p = new Product();

                p.setProductID(rs.getInt("ProductID"));

                p.setProductName(rs.getString("ProductName"));

                double rawPrice = rs.getDouble("Price");

                // long roundedPrice = Math.round(rawPrice / 1000.0) * 1000;
                p.setPrice(rawPrice);

                p.setImageURL(rs.getString("ImageURL"));

                try {
                    p.setBoxUnitName(rs.getString("BoxUnitName"));
                    p.setItemUnitName(rs.getString("ItemUnitName"));
                } catch (Exception ignore) {
                }
                list.add(p);

            }

        } catch (Exception e) {

        }

        return list;

    }

    public List<Product> getProductsByCategoryAndSub(int parentCategoryId) {

        List<Product> list = new ArrayList<>();

        String sql = """
                    SELECT p.*, c.CategoryName, c.ParentID, s.ManufacturerID, s.CompanyName
                    FROM Product p

                    JOIN Category c ON p.CategoryID = c.CategoryID
                    JOIN Manufacturer s ON p.ManufacturerID = s.ManufacturerID
                    WHERE p.CategoryID IN (
                        SELECT CategoryID FROM Category
                        WHERE ParentID = ? OR CategoryID = ?
                    )

                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, parentCategoryId); // danh mục con

            ps.setInt(2, parentCategoryId); // chính nó

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                Product p = new Product();

                p.setProductID(rs.getInt("ProductID"));

                p.setProductName(rs.getString("ProductName"));

                double rawPrice = rs.getDouble("Price");

                // long roundedPrice = Math.round(rawPrice / 1000.0) * 1000;
                p.setPrice(rawPrice);

                p.setDescription(rs.getString("Description"));

                // StockQuantity is now handled in Inventory table

                p.setImageURL(rs.getString("ImageURL"));

                // Unit is now handled by ItemUnitName/BoxUnitName

                p.setCreatedAt(rs.getTimestamp("CreatedAt"));

                // Set Category
                Category c = new Category();

                c.setCategoryID(rs.getInt("CategoryID"));

                c.setCategoryName(rs.getString("CategoryName"));

                c.setParentID(rs.getInt("ParentID"));

                p.setCategory(c);

                // Set Manufacturer
                Manufacturer s = new Manufacturer();
                s.setManufacturerID(rs.getInt("ManufacturerID"));
                s.setCompanyName(rs.getString("CompanyName"));
                p.setManufacturer(s);

                list.add(p);

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return list;

    }

    public String getCategoryNameById(int id) {

        String categoryName = null;

        String sql = "SELECT c.CategoryName FROM Category as c WHERE c.CategoryID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                categoryName = rs.getString("CategoryName");

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return categoryName;

    }

    public double getStockQuantityById(int productId) {

        double stockQuantity = 0;

        String sql = "SELECT SUM(Quantity) as TotalQuantity FROM Inventory WHERE ProductID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                stockQuantity = rs.getDouble("TotalQuantity");

            }

        } catch (Exception e) {

        }

        return stockQuantity;

    }

    public static void main(String[] args) throws ParseException {

        ProductDAO dao = new ProductDAO();

        int parentCategoryId = 1; // Thay đổi ID này tùy theo dữ liệu thực tế của bạn

        List<Product> products = dao.getProductsByCategoryAndSub(parentCategoryId);

        if (products.isEmpty()) {

        } else {

            for (Product p : products) {

            }

        }

    }

    public void updateQuantity(int productId, int addQuantity) {
        // Update BOX inventory instead of Product table
        String sql = "UPDATE Inventory SET Quantity = Quantity + ? WHERE ProductID = ? AND PackageType = 'BOX'";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, addQuantity);
            ps.setInt(2, productId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean updateProductStock(int productId, double newStockQuantity) {

        String sql = "UPDATE Inventory SET Quantity = ? WHERE ProductID = ? AND PackageType = 'BOX'";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, newStockQuantity);

            ps.setInt(2, productId);

            int result = ps.executeUpdate();

            return result > 0;

        } catch (SQLException e) {

            e.printStackTrace();

            return false;

        }

    }

    public boolean convertUnits(int productId, int boxesToConvert, String conversionType, int packSize) {
        try {
            Product product = getProductById(productId);
            if (product == null) {
                return false;
            }

            int totalUnits = boxesToConvert * product.getUnitPerBox();
            Integer packCount = null;
            Integer unitCount = null;

            double unitPrice = product.getPrice() / product.getUnitPerBox();
            Double packPrice = null;

            switch (conversionType) {
                case "unit":
                    unitCount = totalUnits;
                    break;
                case "pack":
                    if (packSize > 0) {
                        if (product.getUnitPerBox() % packSize != 0) {
                            return false;
                        }
                        packCount = totalUnits / packSize;
                        packPrice = unitPrice * packSize;
                    }
                    break;
                case "both":
                    if (packSize > 0) {
                        if (product.getUnitPerBox() % packSize != 0) {
                            return false;
                        }
                        packCount = totalUnits / packSize;
                        packPrice = unitPrice * packSize;
                        // Khi chuyển đổi cả hai, tạo cả lon và lốc
                        unitCount = totalUnits;
                    }
                    break;
                default:
                    return false;
            }

            String sql = "INSERT INTO ProductUnitConversion (ProductID, UnitPerBoxChange, UnitsPerPackChange, UnitPrice, PackPrice, BoxQuantity, PackSize, LotSelection, SpecificLotId, ConversionDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE())";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, productId);
                ps.setInt(2, unitCount != null ? unitCount : 0);
                if (packCount != null) {
                    ps.setInt(3, packCount);
                } else {
                    ps.setNull(3, java.sql.Types.INTEGER);
                }
                ps.setDouble(4, unitPrice);
                if (packPrice != null) {
                    ps.setDouble(5, packPrice);
                } else {
                    ps.setNull(5, java.sql.Types.INTEGER);
                }
                ps.setInt(6, boxesToConvert);
                ps.setInt(7, packSize);
                ps.setString(8, "all"); // Mặc định là tất cả các lô
                ps.setNull(9, java.sql.Types.INTEGER); // Không có lô cụ thể

                int result = ps.executeUpdate();
                if (result > 0) {
                    double currentStock = 0;
                    double newStockQuantity = currentStock - boxesToConvert;

                    if (!updateProductStock(productId, newStockQuantity)) {
                        return false;
                    }

                    return updateInventoryWithBox(productId, boxesToConvert, unitCount, packCount, packSize, unitPrice,
                            packPrice);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Lấy số lượng từ một lô cụ thể
     */
    public double getInventoryQuantityByLotId(int inventoryId) {
        String sql = "SELECT Quantity FROM Inventory WHERE InventoryID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, inventoryId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("Quantity");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    /**
     * Lấy tổng số lượng theo loại package từ tất cả các lô
     */
    public double getTotalInventoryQuantityByType(int productId, String packageType) {
        String sql = "SELECT SUM(Quantity) as TotalQuantity FROM Inventory WHERE ProductID = ? AND PackageType = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.setString(2, packageType);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Double total = rs.getDouble("TotalQuantity");
                return total != null ? total : 0.0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    /**
     * Chuyển đổi đơn vị với thông tin lô cụ thể
     */
    public boolean convertUnitsWithLot(int productId, int boxesToConvert, String conversionType, int packSize,
            String lotSelection, int specificLotId) {
        try {
            System.out.println("=== DEBUG: convertUnitsWithLot ===");
            System.out.println("ProductID: " + productId);
            System.out.println("BoxesToConvert: " + boxesToConvert);
            System.out.println("ConversionType: " + conversionType);
            System.out.println("PackSize: " + packSize);
            System.out.println("LotSelection: " + lotSelection);
            System.out.println("SpecificLotId: " + specificLotId);

            Product product = getProductById(productId);
            if (product == null) {
                System.out.println("ERROR: Product not found");
                return false;
            }
            System.out.println("Product found: " + product.getProductName());
            System.out.println("UnitPerBox: " + product.getUnitPerBox());
            System.out.println("Price: " + product.getPrice());

            int totalUnits = boxesToConvert * product.getUnitPerBox();
            Integer packCount = null;
            Integer unitCount = null;

            double unitPrice = product.getPrice() / product.getUnitPerBox();
            Double packPrice = null;

            System.out.println("TotalUnits: " + totalUnits);
            System.out.println("UnitPrice: " + unitPrice);

            switch (conversionType) {
                case "unit":
                    unitCount = totalUnits;
                    System.out.println("Case unit - UnitCount: " + unitCount);
                    break;
                case "pack":
                    if (packSize > 0) {
                        if (product.getUnitPerBox() % packSize != 0) {
                            System.out.println("ERROR: UnitPerBox not divisible by packSize");
                            return false;
                        }
                        packCount = totalUnits / packSize;
                        packPrice = unitPrice * packSize;
                        System.out.println("Case pack - PackCount: " + packCount + ", PackPrice: " + packPrice);
                    }
                    break;
                case "both":
                    if (packSize > 0) {
                        if (product.getUnitPerBox() % packSize != 0) {
                            System.out.println("ERROR: UnitPerBox not divisible by packSize");
                            return false;
                        }
                        packCount = totalUnits / packSize;
                        packPrice = unitPrice * packSize;
                        // Khi chuyển đổi cả hai, tạo cả lon và lốc
                        unitCount = totalUnits;
                        System.out.println("Case both - UnitCount: " + unitCount + ", PackCount: " + packCount
                                + ", PackPrice: " + packPrice);
                    }
                    break;
                default:
                    System.out.println("ERROR: Invalid conversion type: " + conversionType);
                    return false;
            }

            // Lưu lịch sử chuyển đổi
            System.out.println("=== SQL INSERT DEBUG ===");
            System.out.println("UnitCount: " + unitCount);
            System.out.println("PackCount: " + packCount);
            System.out.println("UnitPrice: " + unitPrice);
            System.out.println("PackPrice: " + packPrice);
            System.out.println("BoxQuantity: " + boxesToConvert);
            System.out.println("PackSize: " + packSize);
            System.out.println("LotSelection: " + lotSelection);
            System.out.println("SpecificLotId: " + specificLotId);

            String sql = "INSERT INTO ProductUnitConversion (ProductID, UnitPerBoxChange, UnitsPerPackChange, UnitPrice, PackPrice, BoxQuantity, PackSize, LotSelection, SpecificLotId, ConversionDate) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE())";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, productId);
                ps.setInt(2, unitCount != null ? unitCount : 0);
                if (packCount != null) {
                    ps.setInt(3, packCount);
                } else {
                    ps.setNull(3, java.sql.Types.INTEGER);
                }
                ps.setDouble(4, unitPrice);
                if (packPrice != null) {
                    ps.setDouble(5, packPrice);
                } else {
                    ps.setNull(5, java.sql.Types.INTEGER);
                }
                ps.setInt(6, boxesToConvert);
                ps.setInt(7, packSize);
                ps.setString(8, lotSelection);
                if ("specific".equals(lotSelection) && specificLotId > 0) {
                    ps.setInt(9, specificLotId);
                } else {
                    ps.setNull(9, java.sql.Types.INTEGER);
                }

                int result = ps.executeUpdate();
                System.out.println("SQL INSERT result: " + result);
                if (result > 0) {
                    System.out.println("SQL INSERT successful, updating inventory...");
                    // Cập nhật số lượng trong bảng Inventory
                    if ("specific".equals(lotSelection) && specificLotId > 0) {
                        // Cập nhật lô cụ thể
                        System.out.println("Updating specific lot: " + specificLotId);
                        if (!updateInventoryQuantityByLotId(specificLotId, boxesToConvert)) {
                            System.out.println("ERROR: Failed to update specific lot");
                            return false;
                        }
                    } else {
                        // Cập nhật tất cả các lô BOX
                        System.out.println("Updating all BOX lots");
                        if (!updateAllInventoryQuantityByType(productId, "BOX", boxesToConvert)) {
                            System.out.println("ERROR: Failed to update all BOX lots");
                            return false;
                        }
                    }

                    // Thêm các lô mới vào bảng Inventory
                    System.out.println("Adding new inventory from conversion...");
                    boolean addResult = addNewInventoryFromConversion(productId, unitCount, packCount, packSize,
                            unitPrice,
                            packPrice);
                    System.out.println("Add new inventory result: " + addResult);
                    return addResult;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Cập nhật số lượng của một lô cụ thể
     */
    private boolean updateInventoryQuantityByLotId(int inventoryId, int boxesToConvert) {
        String sql = "UPDATE Inventory SET Quantity = Quantity - ? WHERE InventoryID = ? AND Quantity >= ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, boxesToConvert);
            ps.setInt(2, inventoryId);
            ps.setInt(3, boxesToConvert);
            int result = ps.executeUpdate();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật số lượng của tất cả các lô theo loại package
     */
    private boolean updateAllInventoryQuantityByType(int productId, String packageType, int boxesToConvert) {
        String sql = "UPDATE Inventory SET Quantity = Quantity - ? WHERE ProductID = ? AND PackageType = ? AND Quantity >= ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, boxesToConvert);
            ps.setInt(2, productId);
            ps.setString(3, packageType);
            ps.setInt(4, boxesToConvert);
            int result = ps.executeUpdate();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Thêm các lô mới vào bảng Inventory sau khi chuyển đổi
     */
    private boolean addNewInventoryFromConversion(int productId, Integer unitCount, Integer packCount, int packSize,
            double unitPrice, Double packPrice) {
        try {
            System.out.println("=== addNewInventoryFromConversion DEBUG ===");
            System.out.println("ProductID: " + productId);
            System.out.println("UnitCount: " + unitCount);
            System.out.println("PackCount: " + packCount);
            System.out.println("UnitPrice: " + unitPrice);
            System.out.println("PackPrice: " + packPrice);

            // Thêm lô UNIT nếu có
            if (unitCount != null && unitCount > 0) {
                // Kiểm tra xem đã có lô UNIT chưa
                String checkUnitSql = "SELECT InventoryID, Quantity FROM Inventory WHERE ProductID = ? AND PackageType = 'UNIT'";
                try (PreparedStatement checkPs = conn.prepareStatement(checkUnitSql)) {
                    checkPs.setInt(1, productId);
                    ResultSet rs = checkPs.executeQuery();

                    if (rs.next()) {
                        // Đã có lô UNIT, cập nhật số lượng
                        int existingInventoryId = rs.getInt("InventoryID");
                        double existingQuantity = rs.getDouble("Quantity");
                        double newQuantity = existingQuantity + unitCount;

                        System.out.println("Updating existing UNIT lot - ID: " + existingInventoryId +
                                ", Old quantity: " + existingQuantity +
                                ", New quantity: " + newQuantity);

                        String updateUnitSql = "UPDATE Inventory SET Quantity = ? WHERE InventoryID = ?";
                        try (PreparedStatement updatePs = conn.prepareStatement(updateUnitSql)) {
                            updatePs.setDouble(1, newQuantity);
                            updatePs.setInt(2, existingInventoryId);
                            updatePs.executeUpdate();
                        }
                    } else {
                        // Chưa có lô UNIT, tạo mới
                        System.out.println("Creating new UNIT lot");
                        String sqlUnit = "INSERT INTO Inventory (ProductID, PackageType, Quantity, CostPrice, LotNumber, LotDate, ExpiryDate) VALUES (?, 'UNIT', ?, ?, ?, GETDATE(), DATEADD(year, 1, GETDATE()))";
                        try (PreparedStatement ps = conn.prepareStatement(sqlUnit)) {
                            ps.setInt(1, productId);
                            ps.setDouble(2, unitCount);
                            ps.setDouble(3, unitPrice);
                            ps.setString(4, "CONV_" + System.currentTimeMillis());
                            ps.executeUpdate();
                        }
                    }
                }
            }

            // Thêm lô PACK nếu có
            if (packCount != null && packCount > 0 && packPrice != null) {
                // Kiểm tra xem đã có lô PACK với cùng PackSize chưa
                String checkPackSql = "SELECT InventoryID, Quantity FROM Inventory WHERE ProductID = ? AND PackageType = 'PACK' AND PackSize = ?";
                try (PreparedStatement checkPs = conn.prepareStatement(checkPackSql)) {
                    checkPs.setInt(1, productId);
                    checkPs.setInt(2, packSize);
                    ResultSet rs = checkPs.executeQuery();

                    if (rs.next()) {
                        // Đã có lô PACK với cùng PackSize, cập nhật số lượng
                        int existingInventoryId = rs.getInt("InventoryID");
                        double existingQuantity = rs.getDouble("Quantity");
                        double newQuantity = existingQuantity + packCount;

                        System.out.println("Updating existing PACK lot - ID: " + existingInventoryId +
                                ", PackSize: " + packSize +
                                ", Old quantity: " + existingQuantity +
                                ", New quantity: " + newQuantity);

                        String updatePackSql = "UPDATE Inventory SET Quantity = ? WHERE InventoryID = ?";
                        try (PreparedStatement updatePs = conn.prepareStatement(updatePackSql)) {
                            updatePs.setDouble(1, newQuantity);
                            updatePs.setInt(2, existingInventoryId);
                            updatePs.executeUpdate();
                        }
                    } else {
                        // Chưa có lô PACK, tạo mới
                        System.out.println("Creating new PACK lot with PackSize: " + packSize);
                        String sqlPack = "INSERT INTO Inventory (ProductID, PackageType, Quantity, CostPrice, PackSize, LotNumber, LotDate, ExpiryDate) VALUES (?, 'PACK', ?, ?, ?, ?, GETDATE(), DATEADD(year, 1, GETDATE()))";
                        try (PreparedStatement ps = conn.prepareStatement(sqlPack)) {
                            ps.setInt(1, productId);
                            ps.setDouble(2, packCount);
                            ps.setDouble(3, packPrice);
                            ps.setInt(4, packSize);
                            ps.setString(5, "CONV_" + System.currentTimeMillis());
                            ps.executeUpdate();
                        }
                    }
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy danh sách các lô hàng BOX có sẵn cho một sản phẩm
     */
    public List<Map<String, Object>> getAvailableBoxLots(int productId) {
        List<Map<String, Object>> lots = new ArrayList<>();
        String sql = "SELECT InventoryID, LotNumber, Quantity, CostPrice, LotDate, ExpiryDate FROM Inventory WHERE ProductID = ? AND PackageType = 'BOX' AND Quantity > 0 ORDER BY LotDate DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> lot = new HashMap<>();
                lot.put("inventoryID", rs.getInt("InventoryID"));
                lot.put("lotNumber", rs.getString("LotNumber"));
                lot.put("quantity", rs.getDouble("Quantity"));
                lot.put("costPrice", rs.getDouble("CostPrice"));
                lot.put("lotDate", rs.getDate("LotDate"));
                lot.put("expiryDate", rs.getDate("ExpiryDate"));
                lots.add(lot);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lots;
    }

    /**
     * Get conversion history for a product
     * 
     * @param productId The product ID
     * @return List of conversion records
     */
    public List<Map<String, Object>> getConversionHistory(int productId) {
        List<Map<String, Object>> conversions = new ArrayList<>();
        String sql = "SELECT * FROM ProductUnitConversion WHERE ProductID = ? ORDER BY ConversionDate DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> conversion = new HashMap<>();
                conversion.put("conversionId", rs.getInt("ConversionID"));
                conversion.put("unitPerBoxChange", rs.getInt("UnitPerBoxChange"));
                conversion.put("unitsPerPackChange", rs.getObject("UnitsPerPackChange"));
                conversion.put("unitPrice", rs.getObject("UnitPrice"));
                conversion.put("packPrice", rs.getObject("PackPrice"));
                conversion.put("boxQuantity", rs.getObject("BoxQuantity"));
                conversion.put("packSize", rs.getObject("PackSize"));
                conversion.put("lotSelection", rs.getString("LotSelection"));
                conversion.put("specificLotId", rs.getObject("SpecificLotId"));
                conversion.put("conversionDate", rs.getTimestamp("ConversionDate"));
                conversions.add(conversion);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return conversions;
    }

    /**
     * Update Inventory table with converted units
     * 
     * @param productId    The product ID
     * @param unitQuantity Number of units (lon) converted
     * @param packQuantity Number of packs (lốc) converted (can be null)
     * @param unitPrice    Price per unit
     * @param packPrice    Price per pack (can be null)
     * @return true if update was successful, false otherwise
     */
    public boolean updateInventory(int productId, int unitQuantity, Integer packQuantity, double unitPrice,
            Double packPrice) {
        try {
            // Update or insert UNIT (lon)
            String sqlUnit = "MERGE Inventory AS target "
                    + "USING (SELECT ? AS ProductID, 'UNIT' AS PackageType) AS source "
                    + "ON (target.ProductID = source.ProductID AND target.PackageType = source.PackageType) "
                    + "WHEN MATCHED THEN "
                    + "    UPDATE SET Quantity = Quantity + ?, UnitPrice = ?, LastUpdated = GETDATE() "
                    + "WHEN NOT MATCHED THEN "
                    + "    INSERT (ProductID, PackageType, Quantity, UnitPrice, LastUpdated) "
                    + "    VALUES (?, 'UNIT', ?, ?, GETDATE());";

            try (PreparedStatement ps = conn.prepareStatement(sqlUnit)) {
                ps.setInt(1, productId);
                ps.setInt(2, unitQuantity);
                ps.setDouble(3, unitPrice);
                ps.setInt(4, productId);
                ps.setInt(5, unitQuantity);
                ps.setDouble(6, unitPrice);
                ps.executeUpdate();
            }

            // Update or insert PACK (lốc) if exists
            if (packQuantity != null && packPrice != null) {
                // Derive PackSize from last conversion inputs: units per pack (lonToLoc)
                int packSize = 0;
                try {
                    // Safe best-effort: compute from quantities if divisible
                    if (unitQuantity > 0 && packQuantity > 0 && unitQuantity % packQuantity == 0) {
                        packSize = unitQuantity / packQuantity;
                    }
                } catch (Exception ignore) {
                }

                String sqlPack = "MERGE Inventory AS target "
                        + "USING (SELECT ? AS ProductID, 'PACK' AS PackageType) AS source "
                        + "ON (target.ProductID = source.ProductID AND target.PackageType = source.PackageType) "
                        + "WHEN MATCHED THEN "
                        + "    UPDATE SET Quantity = Quantity + ?, UnitPrice = ?, LastUpdated = GETDATE() "
                        + "WHEN NOT MATCHED THEN "
                        + "    INSERT (ProductID, PackageType, Quantity, UnitPrice, LastUpdated) "
                        + "    VALUES (?, 'PACK', ?, ?, GETDATE());";

                try (PreparedStatement ps = conn.prepareStatement(sqlPack)) {
                    ps.setInt(1, productId);
                    ps.setInt(2, packQuantity);
                    ps.setDouble(3, packPrice);
                    ps.setInt(4, productId);
                    ps.setInt(5, packQuantity);
                    ps.setDouble(6, packPrice);
                    ps.executeUpdate();
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update Inventory table with new conversion logic
     * 
     * @param productId The product ID
     * @param unitCount Number of units to create (can be null)
     * @param packCount Number of packs to create (can be null)
     * @param packSize  Size of each pack
     * @param unitPrice Price per unit
     * @param packPrice Price per pack (can be null)
     * @return true if update was successful, false otherwise
     */
    public boolean updateInventoryNew(int productId, Integer unitCount, Integer packCount, int packSize,
            double unitPrice,
            Double packPrice) {
        try {
            if (unitCount != null && unitCount > 0) {
                String sqlUnit = "MERGE Inventory AS target "
                        + "USING (SELECT ? AS ProductID, 'UNIT' AS PackageType) AS source "
                        + "ON (target.ProductID = source.ProductID AND target.PackageType = source.PackageType) "
                        + "WHEN MATCHED THEN "
                        + "    UPDATE SET Quantity = Quantity + ?, UnitPrice = ?, LastUpdated = GETDATE() "
                        + "WHEN NOT MATCHED THEN "
                        + "    INSERT (ProductID, PackageType, Quantity, UnitPrice, LastUpdated) "
                        + "    VALUES (?, 'UNIT', ?, ?, GETDATE());";

                try (PreparedStatement ps = conn.prepareStatement(sqlUnit)) {
                    ps.setInt(1, productId);
                    ps.setInt(2, unitCount);
                    ps.setDouble(3, unitPrice);
                    ps.setInt(4, productId);
                    ps.setInt(5, unitCount);
                    ps.setDouble(6, unitPrice);
                    ps.executeUpdate();
                }
            }

            // Update or insert PACK (lốc) if needed
            if (packCount != null && packCount > 0 && packPrice != null) {
                String sqlPack = "MERGE Inventory AS target "
                        + "USING (SELECT ? AS ProductID, 'PACK' AS PackageType) AS source "
                        + "ON (target.ProductID = source.ProductID AND target.PackageType = source.PackageType) "
                        + "WHEN MATCHED THEN "
                        + "    UPDATE SET Quantity = Quantity + ?, UnitPrice = ?, LastUpdated = GETDATE() "
                        + "WHEN NOT MATCHED THEN "
                        + "    INSERT (ProductID, PackageType, Quantity, UnitPrice, LastUpdated) "
                        + "    VALUES (?, 'PACK', ?, ?, GETDATE());";

                try (PreparedStatement ps = conn.prepareStatement(sqlPack)) {
                    ps.setInt(1, productId);
                    ps.setInt(2, packCount);
                    ps.setDouble(3, packPrice);
                    ps.setInt(4, productId);
                    ps.setInt(5, packCount);
                    ps.setDouble(6, packPrice);
                    ps.executeUpdate();
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật inventory khi chuyển đổi sản phẩm (bao gồm cả BOX)
     *
     * @param productId      The product ID
     * @param boxesToConvert Số thùng được chuyển đổi
     * @param unitCount      Số đơn vị được tạo
     * @param packCount      Số lốc được tạo
     * @param packSize       Số đơn vị trong 1 lốc
     * @param unitPrice      Giá 1 đơn vị
     * @param packPrice      Giá 1 lốc
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean updateInventoryWithBox(int productId, int boxesToConvert, Integer unitCount, Integer packCount,
            int packSize,
            double unitPrice, Double packPrice) {
        try {
            String sqlGetBox = "SELECT Quantity FROM Inventory WHERE ProductID = ? AND PackageType = 'BOX'";
            double currentBoxQuantity = 0;
            try (PreparedStatement ps = conn.prepareStatement(sqlGetBox)) {
                ps.setInt(1, productId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    currentBoxQuantity = rs.getDouble("Quantity");
                }
            }

            double newBoxQuantity = currentBoxQuantity - boxesToConvert;

            String sqlBox = "UPDATE Inventory SET Quantity = ?, LastUpdated = GETDATE() "
                    + "WHERE ProductID = ? AND PackageType = 'BOX'";
            try (PreparedStatement ps = conn.prepareStatement(sqlBox)) {
                ps.setDouble(1, newBoxQuantity);
                ps.setInt(2, productId);
                ps.executeUpdate();
            }

            if (unitCount != null && unitCount > 0) {
                String sqlUnit = "MERGE Inventory AS target "
                        + "USING (SELECT ? AS ProductID, 'UNIT' AS PackageType, 0 AS PackSize) AS source "
                        + "ON (target.ProductID = source.ProductID AND target.PackageType = source.PackageType AND target.PackSize = source.PackSize) "
                        + "WHEN MATCHED THEN "
                        + "    UPDATE SET Quantity = Quantity + ?, UnitPrice = ?, LastUpdated = GETDATE() "
                        + "WHEN NOT MATCHED THEN "
                        + "    INSERT (ProductID, PackageType, Quantity, UnitPrice, PackSize, LastUpdated) "
                        + "    VALUES (?, 'UNIT', ?, ?, 0, GETDATE());";

                try (PreparedStatement ps = conn.prepareStatement(sqlUnit)) {
                    ps.setInt(1, productId);
                    ps.setInt(2, unitCount);
                    ps.setDouble(3, unitPrice);
                    ps.setInt(4, productId);
                    ps.setInt(5, unitCount);
                    ps.setDouble(6, unitPrice);
                    ps.executeUpdate();
                }
            }

            if (packCount != null && packCount > 0 && packPrice != null) {
                // Kiểm tra xem đã có PACK với PackSize này chưa
                String sqlCheckPack = "SELECT Quantity FROM Inventory WHERE ProductID = ? AND PackageType = 'PACK' AND PackSize = ?";
                double currentPackQuantity = 0;
                try (PreparedStatement psCheck = conn.prepareStatement(sqlCheckPack)) {
                    psCheck.setInt(1, productId);
                    psCheck.setInt(2, packSize);
                    ResultSet rs = psCheck.executeQuery();
                    if (rs.next()) {
                        currentPackQuantity = rs.getDouble("Quantity");
                    }
                }

                // Cập nhật hoặc thêm mới PACK
                if (currentPackQuantity > 0) {
                    String sqlUpdatePack = "UPDATE Inventory SET Quantity = ?, UnitPrice = ?, LastUpdated = GETDATE() "
                            + "WHERE ProductID = ? AND PackageType = 'PACK' AND PackSize = ?";
                    try (PreparedStatement ps = conn.prepareStatement(sqlUpdatePack)) {
                        ps.setDouble(1, currentPackQuantity + packCount);
                        ps.setDouble(2, packPrice);
                        ps.setInt(3, productId);
                        ps.setInt(4, packSize);
                        ps.executeUpdate();
                    }
                } else {
                    String sqlInsertPack = "INSERT INTO Inventory (ProductID, PackageType, Quantity, UnitPrice, PackSize, LastUpdated) "
                            + "VALUES (?, 'PACK', ?, ?, ?, GETDATE())";
                    try (PreparedStatement ps = conn.prepareStatement(sqlInsertPack)) {
                        ps.setInt(1, productId);
                        ps.setDouble(2, packCount);
                        ps.setDouble(3, packPrice);
                        ps.setInt(4, packSize);
                        ps.executeUpdate();
                    }
                }
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy giá unit (lon) từ Inventory cho việc hiển thị trên trang home
     * 
     * @param productId The product ID
     * @return Giá của 1 lon, null nếu không có
     */
    public Double getUnitPrice(int productId) {
        try {
            String sql = "SELECT TOP 1 UnitPrice FROM Inventory WHERE ProductID = ? AND PackageType IN ('KG','UNIT') ORDER BY CASE WHEN PackageType = 'KG' THEN 0 ELSE 1 END";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, productId);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    return rs.getDouble("UnitPrice");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lấy giá theo loại đóng gói cụ thể từ Inventory
     *
     * @param productId   ID sản phẩm
     * @param packageType 'BOX' | 'UNIT' | 'PACK' | 'KG'
     * @return UnitPrice hoặc null nếu không có
     */
    public Double getPriceByPackageType(int productId, String packageType) {
        try {
            String sql = "SELECT TOP 1 UnitPrice FROM Inventory WHERE ProductID = ? AND PackageType = ? ORDER BY LastUpdated DESC";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, productId);
                ps.setString(2, packageType);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getDouble("UnitPrice");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lấy giá thùng (BOX) từ Inventory
     */
    public Double getBoxPrice(int productId) {
        return getPriceByPackageType(productId, "BOX");
    }

    /**
     * Lấy giá đơn vị nhỏ nhất (UNIT) từ Inventory. KHÔNG bao gồm 'KG'.
     */
    public Double getUnitOnlyPrice(int productId) {
        return getPriceByPackageType(productId, "UNIT");
    }

    /**
     * Lấy tên đơn vị nhỏ nhất (ItemUnitName) từ bảng Product
     */
    public String getItemUnitName(int productId) {
        try {
            String sql = "SELECT ItemUnitName FROM Product WHERE ProductID = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, productId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getString("ItemUnitName");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get current inventory for a product
     * 
     * @param productId The product ID
     * @return Map containing inventory information
     */
    public Map<String, Object> getProductInventory(int productId) {
        Map<String, Object> inventory = new HashMap<>();
        List<Map<String, Object>> packList = new ArrayList<>();
        String sql = "SELECT PackageType, Quantity, UnitPrice, CostPrice FROM Inventory WHERE ProductID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();

            double totalPackQty = 0.0;
            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                String packageType = rs.getString("PackageType");
                double qty = rs.getDouble("Quantity");
                double price = rs.getDouble("UnitPrice");
                Double costPrice = rs.getObject("CostPrice") != null ? rs.getDouble("CostPrice") : null;
                // Bỏ PackSize - không cần thiết

                System.out.println("DEBUG: Found inventory - PackageType: " + packageType + ", Quantity: " + qty
                        + ", Price: " + price + ", CostPrice: " + costPrice);

                if ("PACK".equalsIgnoreCase(packageType)) {
                    Map<String, Object> p = new HashMap<>();
                    // Bỏ packSize - không cần thiết
                    p.put("quantity", qty);
                    p.put("price", price);
                    p.put("costPrice", costPrice);
                    packList.add(p);
                    totalPackQty += qty;
                } else {
                    String effectiveType = packageType;
                    if ("KG".equalsIgnoreCase(packageType)) {
                        effectiveType = "UNIT"; // Chuẩn hóa key để phía gọi không cần đổi
                    } else if ("thùng".equalsIgnoreCase(packageType)) {
                        effectiveType = "BOX"; // Chuẩn hóa "thùng" thành "BOX"
                    }
                    inventory.put(effectiveType + "_Quantity", qty);
                    inventory.put(effectiveType + "_Price", price);
                    if (costPrice != null) {
                        inventory.put(effectiveType + "_CostPrice", costPrice);
                    }

                    // For BOX type (including "thùng"), also create a BOX object similar to PACK
                    // for easier access
                    if ("BOX".equalsIgnoreCase(effectiveType) || "thùng".equalsIgnoreCase(packageType)) {
                        Map<String, Object> boxInfo = new HashMap<>();
                        boxInfo.put("quantity", qty);
                        boxInfo.put("price", price);
                        boxInfo.put("costPrice", costPrice);
                        inventory.put("BOX", boxInfo);
                    }
                }
            }

            if (!hasData) {
                System.out.println("DEBUG: No inventory data found for ProductID: " + productId);
            }

            if (!packList.isEmpty()) {
                inventory.put("PACK_LIST", packList);
                inventory.put("PACK_Quantity", totalPackQty);
            }
        } catch (Exception e) {
            System.out.println("DEBUG: Exception in getProductInventory: " + e.getMessage());
            e.printStackTrace();
        }

        return inventory;
    }

    /**
     * Lấy danh sách các lô hàng cụ thể của một sản phẩm
     * 
     * @param productId ID của sản phẩm
     * @return List các lô hàng với thông tin chi tiết
     */
    public List<Map<String, Object>> getProductLots(int productId) {
        List<Map<String, Object>> lots = new ArrayList<>();
        String sql = "SELECT InventoryID, PackageType, Quantity, UnitPrice, CostPrice, LotNumber, LotDate, ExpiryDate "
                +
                "FROM Inventory WHERE ProductID = ? ORDER BY LotDate ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> lot = new HashMap<>();
                lot.put("inventoryID", rs.getInt("InventoryID"));
                lot.put("packageType", rs.getString("PackageType"));
                lot.put("quantity", rs.getDouble("Quantity"));
                lot.put("unitPrice", rs.getObject("UnitPrice"));
                lot.put("costPrice", rs.getObject("CostPrice"));
                lot.put("lotNumber", rs.getString("LotNumber"));
                lot.put("lotDate", rs.getDate("LotDate"));
                lot.put("expiryDate", rs.getDate("ExpiryDate"));
                lots.add(lot);
            }
        } catch (Exception e) {
            System.out.println("DEBUG: Exception in getProductLots: " + e.getMessage());
            e.printStackTrace();
        }

        return lots;
    }

    /**
     * Cập nhật giá bán cho một lô hàng cụ thể
     * 
     * @param inventoryID ID của lô hàng
     * @param unitPrice   Giá bán mới
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean updateLotPrice(int inventoryID, double unitPrice) {
        String sql = "UPDATE Inventory SET UnitPrice = ? WHERE InventoryID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, unitPrice);
            ps.setInt(2, inventoryID);
            int rows = ps.executeUpdate();
            System.out.println("Updated Inventory.UnitPrice for lot " + inventoryID + ": " + rows + " rows");
            return rows > 0;
        } catch (Exception e) {
            System.out.println("DEBUG: Exception in updateLotPrice: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy ProductID của sản phẩm vừa được insert
     * 
     * @return ProductID của sản phẩm vừa tạo, -1 nếu thất bại
     */
    private int getLastInsertedProductId() {
        try {
            // Sử dụng OUTPUT để lấy ID vừa insert
            String sql = "SELECT MAX(ProductID) AS ProductID FROM Product";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    int productId = rs.getInt("ProductID");

                    return productId;
                }
            }
        } catch (Exception e) {

            e.printStackTrace();
        }
        return -1;
    }

    private boolean createInventoryBox(int productId, double quantity, double price) {
        try {

            String sql = "INSERT INTO Inventory (ProductID, PackageType, Quantity, UnitPrice, PackSize, LastUpdated) VALUES (?, 'BOX', ?, ?, 0, GETDATE())";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, productId);
                ps.setDouble(2, quantity);
                ps.setDouble(3, price);
                int result = ps.executeUpdate();

                return result > 0;
            }
        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }

    private boolean createInventoryUnit(int productId, double quantity, double price) {
        try {

            String sql = "INSERT INTO Inventory (ProductID, PackageType, Quantity, UnitPrice, PackSize, LastUpdated) VALUES (?, 'KG', ?, ?, 0, GETDATE())";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, productId);
                ps.setDouble(2, quantity);
                ps.setDouble(3, price);
                int result = ps.executeUpdate();

                return result > 0;
            }
        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }

    public boolean checkInventoryTable() {
        try {
            String sql = "SELECT TOP 1 * FROM Inventory";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.executeQuery();

                return true;
            }
        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }

    private boolean updateInventoryBox(int productId, double quantity, double price) {
        try {

            String sql = "MERGE Inventory AS target "
                    + "USING (SELECT ? AS ProductID, 'BOX' AS PackageType, 0 AS PackSize) AS source "
                    + "ON (target.ProductID = source.ProductID AND target.PackageType = source.PackageType AND target.PackSize = source.PackSize) "
                    + "WHEN MATCHED THEN "
                    + "    UPDATE SET Quantity = ?, UnitPrice = ?, LastUpdated = GETDATE() "
                    + "WHEN NOT MATCHED THEN "
                    + "    INSERT (ProductID, PackageType, Quantity, UnitPrice, PackSize, LastUpdated) "
                    + "    VALUES (?, 'BOX', ?, ?, 0, GETDATE());";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, productId);
                ps.setDouble(2, quantity);
                ps.setDouble(3, price);
                ps.setInt(4, productId);
                ps.setDouble(5, quantity);
                ps.setDouble(6, price);
                int result = ps.executeUpdate();

                return result > 0;
            }
        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }

    private boolean updateInventoryUnit(int productId, double quantity, double price) {
        try {

            String sql = "MERGE Inventory AS target "
                    + "USING (SELECT ? AS ProductID, 'KG' AS PackageType, 0 AS PackSize) AS source "
                    + "ON (target.ProductID = source.ProductID AND target.PackageType = source.PackageType AND target.PackSize = source.PackSize) "
                    + "WHEN MATCHED THEN "
                    + "    UPDATE SET Quantity = ?, UnitPrice = ?, LastUpdated = GETDATE() "
                    + "WHEN NOT MATCHED THEN "
                    + "    INSERT (ProductID, PackageType, Quantity, UnitPrice, PackSize, LastUpdated) "
                    + "    VALUES (?, 'KG', ?, ?, 0, GETDATE());";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, productId);
                ps.setDouble(2, quantity);
                ps.setDouble(3, price);
                ps.setInt(4, productId);
                ps.setDouble(5, quantity);
                ps.setDouble(6, price);
                int result = ps.executeUpdate();

                return result > 0;
            }
        } catch (Exception e) {

            e.printStackTrace();
            return false;
        }
    }

    private boolean isFruitCategory(int categoryId) {
        String sql = "SELECT CASE WHEN c.CategoryID = 3 OR c.ParentID = 3 THEN 1 ELSE 0 END AS IsFruit FROM Category c WHERE c.CategoryID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("IsFruit") == 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // ====================
    // đừng xóa tôi có sài
    // ==========================

    public List<Integer> getProductIdsByCategoryIdsExpanded(List<Integer> categoryIds) {
        List<Integer> result = new ArrayList<>();
        if (categoryIds == null || categoryIds.isEmpty())
            return result;

        // Chunk nếu cần (ví dụ 500 id mỗi lần)
        final int CHUNK = 500;
        for (int offset = 0; offset < categoryIds.size(); offset += CHUNK) {
            List<Integer> part = categoryIds.subList(offset, Math.min(offset + CHUNK, categoryIds.size()));
            String placeholders = String.join(",", Collections.nCopies(part.size(), "?"));
            String sql = "SELECT ProductID FROM Product WHERE CategoryID IN (" + placeholders + ")";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (int i = 0; i < part.size(); i++)
                    ps.setInt(i + 1, part.get(i));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next())
                        result.add(rs.getInt(1));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Cập nhật giá bán cho sản phẩm
     * 
     * @param productId    ID sản phẩm
     * @param sellingPrice Giá bán mới
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean updateProductPrice(int productId, double sellingPrice) {
        try {
            // Bước 1: Cập nhật Product.Price
            String sqlProduct = "UPDATE Product SET Price = ? WHERE ProductID = ?";
            try (PreparedStatement psProduct = conn.prepareStatement(sqlProduct)) {
                psProduct.setDouble(1, sellingPrice);
                psProduct.setInt(2, productId);
                int resultProduct = psProduct.executeUpdate();

                if (resultProduct > 0) {
                    // Bước 2: Cập nhật Inventory.UnitPrice cho tất cả package types
                    String sqlInventory = "UPDATE Inventory SET UnitPrice = ? WHERE ProductID = ?";
                    try (PreparedStatement psInventory = conn.prepareStatement(sqlInventory)) {
                        psInventory.setDouble(1, sellingPrice);
                        psInventory.setInt(2, productId);
                        int resultInventory = psInventory.executeUpdate();

                        System.out.println("Updated Product.Price: " + resultProduct + " rows");
                        System.out.println("Updated Inventory.UnitPrice: " + resultInventory + " rows");

                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật giá bán cho tất cả các lô của sản phẩm (chỉ Inventory.UnitPrice,
     * không ảnh hưởng Product.Price)
     * 
     * @param productId    ID sản phẩm
     * @param sellingPrice Giá bán mới cho tất cả lô
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean updateAllLotPrices(int productId, double sellingPrice) {
        try {
            // Chỉ cập nhật Inventory.UnitPrice cho tất cả lô của sản phẩm
            String sqlInventory = "UPDATE Inventory SET UnitPrice = ? WHERE ProductID = ?";
            try (PreparedStatement psInventory = conn.prepareStatement(sqlInventory)) {
                psInventory.setDouble(1, sellingPrice);
                psInventory.setInt(2, productId);
                int resultInventory = psInventory.executeUpdate();

                System.out.println("Updated Inventory.UnitPrice for all lots: " + resultInventory + " rows");
                return resultInventory > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật giá tham khảo cho sản phẩm (chỉ Product.Price, không ảnh hưởng
     * Inventory.UnitPrice)
     * 
     * @param productId      ID sản phẩm
     * @param referencePrice Giá tham khảo mới
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean updateProductReferencePrice(int productId, double referencePrice) {
        try {
            // 1) Lấy UnitPerBox từ Product để tính giá đơn vị (lon)
            int unitPerBox = 0;
            String getUnitPerBoxSql = "SELECT UnitPerBox FROM Product WHERE ProductID = ?";
            try (PreparedStatement ps = conn.prepareStatement(getUnitPerBoxSql)) {
                ps.setInt(1, productId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        unitPerBox = rs.getInt("UnitPerBox");
                    }
                }
            }

            // Mặc định nếu thiếu dữ liệu, không chia để tránh chia cho 0
            Double unitReferencePrice = null;
            if (unitPerBox > 0) {
                unitReferencePrice = referencePrice / unitPerBox;
            }

            // 2) Tìm PackSize đại diện từ Inventory (PACK) nếu có để tính giá lốc
            Integer representativePackSize = null;
            String getPackSizeSql = "SELECT TOP 1 PackSize FROM Inventory WHERE ProductID = ? AND PackageType = 'PACK' AND PackSize IS NOT NULL GROUP BY PackSize ORDER BY COUNT(*) DESC";
            try (PreparedStatement ps = conn.prepareStatement(getPackSizeSql)) {
                ps.setInt(1, productId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        representativePackSize = rs.getInt("PackSize");
                        if (rs.wasNull())
                            representativePackSize = null;
                    }
                }
            }

            Double packReferencePrice = null;
            if (unitReferencePrice != null && representativePackSize != null && representativePackSize > 0) {
                packReferencePrice = unitReferencePrice * representativePackSize;
            }

            // 3) Cập nhật Product với cả 3 trường tham khảo
            String sql = "UPDATE Product SET Price = ?, UnitReferencePrice = ?, PackReferencePrice = ? WHERE ProductID = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setDouble(1, referencePrice);
                if (unitReferencePrice == null) {
                    ps.setNull(2, java.sql.Types.DECIMAL);
                } else {
                    ps.setDouble(2, unitReferencePrice);
                }
                if (packReferencePrice == null) {
                    ps.setNull(3, java.sql.Types.DECIMAL);
                } else {
                    ps.setDouble(3, packReferencePrice);
                }
                ps.setInt(4, productId);
                int result = ps.executeUpdate();

                System.out.println("Updated Product reference prices: Price, UnitReferencePrice, PackReferencePrice => "
                        + result + " rows");
                return result > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy tổng số lượng tồn kho của một sản phẩm từ Inventory table
     * 
     * @param productId ID sản phẩm
     * @return Tổng số lượng tồn kho, hoặc 0.0 nếu không có
     */
    public Double getProductStockQuantity(int productId) {
        String sql = "SELECT SUM(Quantity) AS TotalQuantity FROM Inventory WHERE ProductID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Double totalQuantity = rs.getDouble("TotalQuantity");
                    return rs.wasNull() ? 0.0 : totalQuantity;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

}
