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
                p.setStockQuantity(rs.getDouble("StockQuantity"));
                p.setImageURL(rs.getString("ImageURL"));
                p.setUnit(rs.getString("unit"));
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
                p.setStockQuantity(rs.getDouble("StockQuantity"));
                p.setImageURL(rs.getString("ImageURL"));
                p.setUnit(rs.getString("unit"));
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

    public int insert(String name, double price, String description, double quantity,
            String ImageURL, String unit, Timestamp createdAt,
            int categoryID, int manufacturerID,
            Date manufactureDate, Date expirationDate,
            int unitPerBox, String boxUnitName, String itemUnitName) {
        if (price < 1000) {
            price *= 1000;
        }
        String sql = "INSERT INTO Product (productName, price, description, StockQuantity, ImageURL, unit, createdAt, categoryID, manufacturerID, ManufactureDate, ExpirationDate, UnitPerBox, BoxUnitName, ItemUnitName) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setString(3, description);
            ps.setDouble(4, quantity);
            ps.setString(5, ImageURL);
            ps.setString(6, unit);
            ps.setTimestamp(7, createdAt);
            ps.setInt(8, categoryID);
            ps.setInt(9, manufacturerID);
            ps.setDate(10, new java.sql.Date(manufactureDate.getTime()));
            ps.setDate(11, new java.sql.Date(expirationDate.getTime()));
            ps.setInt(12, unitPerBox);
            ps.setString(13, boxUnitName);
            ps.setString(14, itemUnitName);

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                int productId = -1;
                if (rs.next()) {
                    productId = rs.getInt(1);
                    if (!checkInventoryTable()) {
                        return 1;
                    }
                    if (productId > 0) {
                        if (isFruitCategory(categoryID)) {
                            createInventoryUnit(productId, quantity, price);
                        } else {
                            createInventoryBox(productId, quantity, price);
                        }
                    }
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
                + "       s.ManufacturerID, s.CompanyName,\n"
                + "       i.Quantity AS InventoryQuantity, i.LastUpdated\n"
                + "FROM product p\n"
                + "LEFT JOIN Category c ON p.CategoryID = c.CategoryID\n"
                + "LEFT JOIN Manufacturer s ON p.ManufacturerID = s.ManufacturerID\n"
                + "LEFT JOIN Inventory i ON p.ProductID = i.ProductID\n"
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
                double quantity = rs.getDouble("StockQuantity");
                String imageURL = rs.getString("ImageURL");
                String unit = rs.getString("Unit");
                Timestamp createdAt = rs.getTimestamp("CreatedAt");
                Date manufactureDate = rs.getDate("ManufactureDate");
                Date expirationDate = rs.getDate("ExpirationDate");
                int categoryId = rs.getInt("CategoryID");
                String categoryName = rs.getString("CategoryName");
                int parentId = rs.getInt("ParentID");
                Category category = new Category(categoryId, categoryName, parentId, rs.getString("ImageURL"));
                int manufacturerId = rs.getInt("ManufacturerID");
                String manufacturerName = rs.getString("CompanyName");
                Manufacturer manufacturer = new Manufacturer(manufacturerId, manufacturerName);
                int inventoryQty = rs.getInt("InventoryQuantity");
                Timestamp lastUpdated = rs.getTimestamp("LastUpdated");
                InventoryTransaction inventory = new InventoryTransaction(id, inventoryQty, lastUpdated);
                product = new Product(id, proName, proPrice, description, quantity, imageURL, unit, createdAt,
                        manufactureDate, expirationDate);
                product.setCategory(category);
                product.setManufacturer(manufacturer);
                product.setInventory(inventory);
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
        String sqlCartItem = "DELETE FROM CartItem WHERE ProductID = ?";
        String sqlOrderDetail = "DELETE FROM OrderDetail WHERE ProductID = ?";
        String sqlReview = "DELETE FROM Review WHERE ProductID = ?";
        String sqlPromotion = "DELETE FROM Product_Promotion WHERE ProductID = ?";
        String sqlInventory = "DELETE FROM Inventory WHERE ProductID = ?";
        String sqlProduct = "DELETE FROM Product WHERE ProductID = ?";
        try {
            PreparedStatement ps1 = conn.prepareStatement(sqlCartItem);
            ps1.setInt(1, id);
            ps1.executeUpdate();
            PreparedStatement ps2 = conn.prepareStatement(sqlOrderDetail);
            ps2.setInt(1, id);
            ps2.executeUpdate();
            PreparedStatement ps3 = conn.prepareStatement(sqlReview);
            ps3.setInt(1, id);
            ps3.executeUpdate();
            PreparedStatement ps4 = conn.prepareStatement(sqlPromotion);
            ps4.setInt(1, id);
            ps4.executeUpdate();
            PreparedStatement ps5 = conn.prepareStatement(sqlInventory);
            ps5.setInt(1, id);
            ps5.executeUpdate();
            PreparedStatement ps6 = conn.prepareStatement(sqlProduct);
            ps6.setInt(1, id);
            int affectedRows = ps6.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean update(Product product) {
        String sql = "UPDATE Product SET "
                + "ProductName = ?, "
                + "Price = ?, "
                + "Description = ?, "
                + "StockQuantity = ?, "
                + "ImageURL = ?, "
                + "Unit = ?, "
                + "CreatedAt = ?, "
                + "ManufactureDate = ?, "
                + "ExpirationDate = ?, "
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
            ps.setDouble(4, product.getStockQuantity());
            ps.setString(5, product.getImageURL());
            ps.setString(6, product.getUnit());
            ps.setTimestamp(7, product.getCreatedAt());
            ps.setDate(8, new java.sql.Date(product.getManufactureDate().getTime()));
            ps.setDate(9, new java.sql.Date(product.getExpirationDate().getTime()));
            ps.setInt(10, product.getCategory().getCategoryID());
            ps.setInt(11, product.getManufacturer().getManufacturerId());
            ps.setInt(12, product.getUnitPerBox());
            ps.setString(13, product.getBoxUnitName());
            ps.setString(14, product.getItemUnitName());
            ps.setInt(15, product.getProductID());
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                if (isFruitCategory(product.getCategory().getCategoryID())) {
                    updateInventoryUnit(product.getProductID(), product.getStockQuantity(), price);
                } else {
                    updateInventoryBox(product.getProductID(), product.getStockQuantity(), price);
                }
            }
            return affectedRows > 0;
        } catch (Exception e) {
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
                    WHERE p.ProductName LIKE ? AND p.StockQuantity > 0
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
                p.setStockQuantity(rs.getDouble("StockQuantity"));
                p.setImageURL(rs.getString("ImageURL"));
                p.setUnit(rs.getString("Unit"));
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

                p.setStockQuantity(rs.getDouble("StockQuantity"));

                p.setImageURL(rs.getString("ImageURL"));

                p.setUnit(rs.getString("Unit"));

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

        String sql = "SELECT StockQuantity FROM Product WHERE ProductID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {

                stockQuantity = rs.getDouble("StockQuantity");

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

        String sql = "UPDATE Product SET Quantity = Quantity + ? WHERE ProductID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, addQuantity);

            ps.setInt(2, productId);

            ps.executeUpdate();

        } catch (SQLException e) {

            e.printStackTrace();

        }

    }

    public boolean updateProductStock(int productId, double newStockQuantity) {

        String sql = "UPDATE Product SET StockQuantity = ? WHERE ProductID = ?";

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

            String sql = "INSERT INTO ProductUnitConversion (ProductID, UnitPerBoxChange, UnitsPerPackChange, UnitPrice, PackPrice, BoxQuantity, PackSize, ConversionDate) VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE())";
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
                if (packSize > 0 && ("pack".equals(conversionType) || "both".equals(conversionType))) {
                    ps.setInt(7, packSize);
                } else {
                    ps.setNull(7, java.sql.Types.INTEGER);
                }

                int result = ps.executeUpdate();
                if (result > 0) {
                    double currentStock = product.getStockQuantity();
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
                    + "USING (SELECT ? AS ProductID, 'UNIT' AS PackageType, 0 AS PackSize) AS source "
                    + "ON (target.ProductID = source.ProductID AND target.PackageType = source.PackageType AND target.PackSize = source.PackSize) "
                    + "WHEN MATCHED THEN "
                    + "    UPDATE SET Quantity = Quantity + ?, UnitPrice = ?, LastUpdated = GETDATE() "
                    + "WHEN NOT MATCHED THEN "
                    + "    INSERT (ProductID, PackageType, Quantity, UnitPrice, PackSize, LastUpdated) "
                    + "    VALUES (?, 'UNIT', ?, ?, 0, GETDATE());";

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
                        + "USING (SELECT ? AS ProductID, 'PACK' AS PackageType, ? AS PackSize) AS source "
                        + "ON (target.ProductID = source.ProductID AND target.PackageType = source.PackageType AND target.PackSize = source.PackSize) "
                        + "WHEN MATCHED THEN "
                        + "    UPDATE SET Quantity = Quantity + ?, UnitPrice = ?, LastUpdated = GETDATE() "
                        + "WHEN NOT MATCHED THEN "
                        + "    INSERT (ProductID, PackageType, Quantity, UnitPrice, PackSize, LastUpdated) "
                        + "    VALUES (?, 'PACK', ?, ?, ?, GETDATE());";

                try (PreparedStatement ps = conn.prepareStatement(sqlPack)) {
                    ps.setInt(1, productId);
                    ps.setInt(2, packSize);
                    ps.setInt(3, packQuantity);
                    ps.setDouble(4, packPrice);
                    ps.setInt(5, productId);
                    ps.setInt(6, packQuantity);
                    ps.setDouble(7, packPrice);
                    ps.setInt(8, packSize);
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

            // Update or insert PACK (lốc) if needed
            if (packCount != null && packCount > 0 && packPrice != null) {
                String sqlPack = "MERGE Inventory AS target "
                        + "USING (SELECT ? AS ProductID, 'PACK' AS PackageType, ? AS PackSize) AS source "
                        + "ON (target.ProductID = source.ProductID AND target.PackageType = source.PackageType AND target.PackSize = source.PackSize) "
                        + "WHEN MATCHED THEN "
                        + "    UPDATE SET Quantity = Quantity + ?, UnitPrice = ?, LastUpdated = GETDATE() "
                        + "WHEN NOT MATCHED THEN "
                        + "    INSERT (ProductID, PackageType, Quantity, UnitPrice, PackSize, LastUpdated) "
                        + "    VALUES (?, 'PACK', ?, ?, ?, GETDATE());";

                try (PreparedStatement ps = conn.prepareStatement(sqlPack)) {
                    ps.setInt(1, productId);
                    ps.setInt(2, packSize);
                    ps.setInt(3, packCount);
                    ps.setDouble(4, packPrice);
                    ps.setInt(5, productId);
                    ps.setInt(6, packCount);
                    ps.setDouble(7, packPrice);
                    ps.setInt(8, packSize);
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
        String sql = "SELECT PackageType, Quantity, UnitPrice, PackSize FROM Inventory WHERE ProductID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();

            double totalPackQty = 0.0;
            while (rs.next()) {
                String packageType = rs.getString("PackageType");
                double qty = rs.getDouble("Quantity");
                double price = rs.getDouble("UnitPrice");
                int packSize = 0;
                try {
                    packSize = rs.getInt("PackSize");
                } catch (Exception ignore) {
                }

                if ("PACK".equalsIgnoreCase(packageType)) {
                    Map<String, Object> p = new HashMap<>();
                    p.put("packSize", packSize);
                    p.put("quantity", qty);
                    p.put("price", price);
                    packList.add(p);
                    totalPackQty += qty;
                } else {
                    String effectiveType = packageType;
                    if ("KG".equalsIgnoreCase(packageType)) {
                        effectiveType = "UNIT"; // Chuẩn hóa key để phía gọi không cần đổi
                    }
                    inventory.put(effectiveType + "_Quantity", qty);
                    inventory.put(effectiveType + "_Price", price);
                }
            }
            if (!packList.isEmpty()) {
                inventory.put("PACK_LIST", packList);
                inventory.put("PACK_Quantity", totalPackQty);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return inventory;
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

}
