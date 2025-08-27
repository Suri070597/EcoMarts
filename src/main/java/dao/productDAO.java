package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import db.DBContext;
import model.Category;
import model.Manufacturer;
import model.Product;

public class ProductDAO extends DBContext {

    public List<Product> getAll() {
        List<Product> list = new ArrayList<>();

        String sql = "SELECT p.*, c.categoryName, c.parentID, "
                + "COALESCE(box_inv.Quantity, 0) as BoxQuantity, "
                + "COALESCE(kg_inv.Quantity, 0) as KgQuantity "
                + "FROM Product p "
                + "JOIN Category c ON p.categoryID = c.categoryID "
                + "LEFT JOIN Inventory box_inv ON p.ProductID = box_inv.ProductID AND box_inv.PackageType = 'BOX' "
                + "LEFT JOIN Inventory kg_inv ON p.ProductID = kg_inv.ProductID AND kg_inv.PackageType = 'KG'";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Category cat = new Category();
                cat.setCategoryID(rs.getInt("categoryID"));
                cat.setCategoryName(rs.getString("categoryName"));
                cat.setParentID(rs.getInt("parentID"));

                Product p = new Product();
                p.setProductID(rs.getInt("productID"));
                p.setProductName(rs.getString("productName"));
                p.setPrice(rs.getObject("PriceBox", Double.class)); // Lấy giá thùng từ PriceBox
                p.setPriceUnit(rs.getObject("PriceUnit", Double.class)); // Lấy giá đơn vị từ PriceUnit
                p.setDescription(rs.getString("description"));

                // Lấy số lượng dựa theo loại sản phẩm
                double boxQty = rs.getDouble("BoxQuantity");
                double kgQty = rs.getDouble("KgQuantity");

                // Nếu là trái cây (parentID = 3) thì lấy số lượng KG, ngược lại lấy số lượng
                // BOX
                if (cat.getParentID() == 3) {
                    p.setStockQuantity(kgQty);
                } else {
                    p.setStockQuantity(boxQty);
                }

                p.setImageURL(rs.getString("ImageURL"));
                p.setCreatedAt(rs.getTimestamp("createdAt"));
                p.setCategory(cat);
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
        String sql = "SELECT COUNT(*) AS cnt FROM Product p "
                + "JOIN Category c ON p.categoryID = c.categoryID "
                + "LEFT JOIN Inventory box_inv ON p.ProductID = box_inv.ProductID AND box_inv.PackageType = 'BOX' "
                + "LEFT JOIN Inventory kg_inv ON p.ProductID = kg_inv.ProductID AND kg_inv.PackageType = 'KG' "
                + "WHERE (c.parentID = 3 AND COALESCE(kg_inv.Quantity, 0) > ?) "
                + "   OR (c.parentID != 3 AND COALESCE(box_inv.Quantity, 0) > ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lowStockThreshold);
            ps.setInt(2, lowStockThreshold);
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
        String sql = "SELECT COUNT(*) AS cnt FROM Product p "
                + "JOIN Category c ON p.categoryID = c.categoryID "
                + "LEFT JOIN Inventory box_inv ON p.ProductID = box_inv.ProductID AND box_inv.PackageType = 'BOX' "
                + "LEFT JOIN Inventory kg_inv ON p.ProductID = kg_inv.ProductID AND kg_inv.PackageType = 'KG' "
                + "WHERE (c.parentID = 3 AND COALESCE(kg_inv.Quantity, 0) > 0 AND COALESCE(kg_inv.Quantity, 0) <= ?) "
                + "   OR (c.parentID != 3 AND COALESCE(box_inv.Quantity, 0) > 0 AND COALESCE(box_inv.Quantity, 0) <= ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lowStockThreshold);
            ps.setInt(2, lowStockThreshold);
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
        String sql = "SELECT COUNT(*) AS cnt FROM Product p "
                + "JOIN Category c ON p.categoryID = c.categoryID "
                + "LEFT JOIN Inventory box_inv ON p.ProductID = box_inv.ProductID AND box_inv.PackageType = 'BOX' "
                + "LEFT JOIN Inventory kg_inv ON p.ProductID = kg_inv.ProductID AND kg_inv.PackageType = 'KG' "
                + "WHERE (c.parentID = 3 AND COALESCE(kg_inv.Quantity, 0) <= 0) "
                + "   OR (c.parentID != 3 AND COALESCE(box_inv.Quantity, 0) <= 0)";
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

    public int insertNewProduct(String productName,
            int categoryId,
            Double priceBox,
            Double priceUnit,
            Double pricePack,
            Integer unitPerBox,
            String boxUnitName,
            String itemUnitName,
            String description,
            String imageUrl) {
        String sql = "INSERT INTO Product (ProductName, CategoryID, PriceBox, PriceUnit, PricePack, UnitPerBox, BoxUnitName, ItemUnitName, Description, ImageURL) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, productName);
            ps.setInt(2, categoryId);
            if (priceBox == null) {
                ps.setNull(3, java.sql.Types.DECIMAL);
            } else {
                ps.setDouble(3, priceBox);
            }
            if (priceUnit == null) {
                ps.setNull(4, java.sql.Types.DECIMAL);
            } else {
                ps.setDouble(4, priceUnit);
            }
            if (pricePack == null) {
                ps.setNull(5, java.sql.Types.DECIMAL);
            } else {
                ps.setDouble(5, pricePack);
            }
            ps.setInt(6, unitPerBox != null ? unitPerBox : 1);
            ps.setString(7, boxUnitName != null ? boxUnitName : "thùng");
            ps.setString(8, itemUnitName);
            if (description == null || description.trim().isEmpty()) {
                ps.setNull(9, java.sql.Types.NVARCHAR);
            } else {
                ps.setString(9, description);
            }
            if (imageUrl == null || imageUrl.trim().isEmpty()) {
                ps.setNull(10, java.sql.Types.NVARCHAR);
            } else {
                ps.setString(10, imageUrl);
            }
            int affected = ps.executeUpdate();
            return affected > 0 ? 1 : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public Product getProductById(int id) {
        Product product = null;
        String sql = "SELECT p.*, c.categoryName, c.parentID, "
                + "COALESCE(box_inv.Quantity, 0) as BoxQuantity, "
                + "COALESCE(kg_inv.Quantity, 0) as KgQuantity "
                + "FROM Product p "
                + "JOIN Category c ON p.categoryID = c.categoryID "
                + "LEFT JOIN Inventory box_inv ON p.ProductID = box_inv.ProductID AND box_inv.PackageType = 'BOX' "
                + "LEFT JOIN Inventory kg_inv ON p.ProductID = kg_inv.ProductID AND kg_inv.PackageType = 'KG' "
                + "WHERE p.ProductID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // Product info
                String proName = rs.getString("productName");
                String description = rs.getString("description");
                String imageURL = rs.getString("ImageURL");
                Timestamp createdAt = rs.getTimestamp("createdAt");

                int categoryId = rs.getInt("categoryID");
                String categoryName = rs.getString("categoryName");
                int parentId = rs.getInt("parentID");
                Category category = new Category(categoryId, categoryName, parentId, null);

                // Lấy số lượng dựa theo loại sản phẩm
                double boxQty = rs.getDouble("BoxQuantity");
                double kgQty = rs.getDouble("KgQuantity");
                double stockQuantity;

                if (parentId == 3) {
                    stockQuantity = kgQty; // Trái cây lấy KG
                } else {
                    stockQuantity = boxQty; // Sản phẩm khác lấy BOX
                }

                product = new Product();
                product.setProductID(id);
                product.setProductName(proName);
                product.setDescription(description);
                product.setStockQuantity(stockQuantity);
                product.setImageURL(imageURL);
                product.setCreatedAt(createdAt);
                product.setCategory(category);

                // Lấy các trường giá mới - xử lý nullable
                Double priceBox = rs.getObject("PriceBox", Double.class);
                Double priceUnit = rs.getObject("PriceUnit", Double.class);
                Double pricePack = rs.getObject("PricePack", Double.class);

                product.setPrice(priceBox); // Giá thùng
                product.setPriceUnit(priceUnit); // Giá unit
                product.setPricePack(pricePack); // Giá pack

                product.setUnitPerBox(rs.getInt("UnitPerBox"));
                product.setBoxUnitName(rs.getString("BoxUnitName"));
                product.setItemUnitName(rs.getString("ItemUnitName"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return product;
    }

    //
    public boolean delete(int id) {
        String sqlCartItem = "DELETE FROM CartItem WHERE ProductID = ?";
        String sqlOrderDetail = "DELETE FROM OrderDetail WHERE ProductID = ?";
        String sqlReview = "DELETE FROM Review WHERE ProductID = ?";
        String sqlPromotion = "DELETE FROM Product_Promotion WHERE ProductID = ?";
        String sqlStockInDetail = "DELETE FROM StockInDetail WHERE InventoryID IN (SELECT InventoryID FROM Inventory WHERE ProductID = ?)";
        String sqlInventory = "DELETE FROM Inventory WHERE ProductID = ?";
        String sqlProduct = "DELETE FROM Product WHERE ProductID = ?";
        try {
            // 1. Xóa CartItem
            PreparedStatement ps1 = conn.prepareStatement(sqlCartItem);
            ps1.setInt(1, id);
            ps1.executeUpdate();
            // 2. Xóa OrderDetail
            PreparedStatement ps2 = conn.prepareStatement(sqlOrderDetail);
            ps2.setInt(1, id);
            ps2.executeUpdate();
            // 3. Xóa Review
            PreparedStatement ps3 = conn.prepareStatement(sqlReview);
            ps3.setInt(1, id);
            ps3.executeUpdate();
            // 4. Xóa Product_Promotion
            PreparedStatement ps4 = conn.prepareStatement(sqlPromotion);
            ps4.setInt(1, id);
            ps4.executeUpdate();
            // 5. ✅ XÓA StockInDetail TRƯỚC Inventory
            PreparedStatement ps5 = conn.prepareStatement(sqlStockInDetail);
            ps5.setInt(1, id);
            ps5.executeUpdate();
            // 6. Xóa Inventory
            PreparedStatement ps6 = conn.prepareStatement(sqlInventory);
            ps6.setInt(1, id);
            ps6.executeUpdate();
            // 7. Cuối cùng xóa Product
            PreparedStatement ps7 = conn.prepareStatement(sqlProduct);
            ps7.setInt(1, id);
            int affectedRows = ps7.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật sản phẩm với thông tin mới
     */
    public boolean updateProduct(int productId, String productName, String description, int categoryId,
            String priceBoxStr, String priceUnitStr, String pricePackStr,
            String unitPerBoxStr, String boxUnitName, String itemUnitName,
            String imageURL) {
        try {
            // Xác định có phải trái cây không
            boolean isFruit = false;
            String sqlCheckFruit = "SELECT CASE WHEN c.CategoryID = 3 OR c.ParentID = 3 THEN 1 ELSE 0 END AS IsFruit "
                    + "FROM Category c WHERE c.CategoryID = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlCheckFruit)) {
                ps.setInt(1, categoryId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        isFruit = rs.getInt("IsFruit") == 1;
                    }
                }
            }

            // Chuẩn bị giá trị - không cập nhật giá
            Integer unitPerBox = null;

            if (isFruit) {
                // Trái cây: giữ nguyên giá cũ
                unitPerBox = 1;
                boxUnitName = "kg";
                itemUnitName = "kg";
            } else {
                // Sản phẩm thường: giữ nguyên giá cũ
                if (unitPerBoxStr != null && !unitPerBoxStr.trim().isEmpty()) {
                    unitPerBox = Integer.parseInt(unitPerBoxStr);
                }
            }

            // SQL update - không cập nhật giá
            String sql = "UPDATE Product SET "
                    + "ProductName = ?, "
                    + "CategoryID = ?, "
                    + "UnitPerBox = ?, "
                    + "BoxUnitName = ?, "
                    + "ItemUnitName = ?, "
                    + "Description = ?, "
                    + "ImageURL = ? "
                    + "WHERE ProductID = ?";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, productName);
                ps.setInt(2, categoryId);

                if (unitPerBox != null) {
                    ps.setInt(3, unitPerBox);
                } else {
                    ps.setNull(3, java.sql.Types.INTEGER);
                }

                ps.setString(4, boxUnitName);
                ps.setString(5, itemUnitName);

                if (description != null && !description.trim().isEmpty()) {
                    ps.setString(6, description);
                } else {
                    ps.setNull(6, java.sql.Types.NVARCHAR);
                }

                ps.setString(7, imageURL);
                ps.setInt(8, productId);

                int affectedRows = ps.executeUpdate();
                return affectedRows > 0;
            }
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

    public List<Product> searchProductsByName(String keyword) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.categoryName, c.parentID, "
                + "COALESCE(box_inv.Quantity, 0) as BoxQuantity, "
                + "COALESCE(kg_inv.Quantity, 0) as KgQuantity "
                + "FROM Product p "
                + "JOIN Category c ON p.categoryID = c.categoryID "
                + "LEFT JOIN Inventory box_inv ON p.ProductID = box_inv.ProductID AND box_inv.PackageType = 'BOX' "
                + "LEFT JOIN Inventory kg_inv ON p.ProductID = kg_inv.ProductID AND kg_inv.PackageType = 'KG' "
                + "WHERE p.ProductName LIKE ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Product p = new Product();
                p.setProductID(rs.getInt("productID"));
                p.setProductName(rs.getString("productName"));
                p.setPrice(rs.getObject("PriceBox", Double.class)); // Lấy giá thùng từ PriceBox
                p.setPriceUnit(rs.getObject("PriceUnit", Double.class)); // Lấy giá đơn vị từ PriceUnit
                p.setDescription(rs.getString("description"));

                // Lấy số lượng dựa theo loại sản phẩm
                double boxQty = rs.getDouble("BoxQuantity");
                double kgQty = rs.getDouble("KgQuantity");

                // Set Category trước để kiểm tra parentID
                Category c = new Category();
                c.setCategoryID(rs.getInt("categoryID"));
                c.setCategoryName(rs.getString("categoryName"));
                c.setParentID(rs.getInt("parentID"));
                p.setCategory(c);

                // Nếu là trái cây (parentID = 3) thì lấy số lượng KG, ngược lại lấy số lượng
                // BOX
                if (c.getParentID() == 3) {
                    p.setStockQuantity(kgQty);
                } else {
                    p.setStockQuantity(boxQty);
                }

                p.setImageURL(rs.getString("ImageURL"));
                p.setCreatedAt(rs.getTimestamp("createdAt"));
                p.setStatus(rs.getString("Status"));
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
                p.setPrice(rs.getObject("Price", Double.class));
                p.setDescription(rs.getString("Description"));
                p.setStockQuantity(rs.getDouble("StockQuantity"));
                p.setImageURL(rs.getString("ImageURL"));
                p.setUnit(rs.getString("Unit"));
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
                Double rawPrice = rs.getObject("Price", Double.class);
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

            Double price = product.getPrice();
            if (price == null) {
                price = 0.0;
            }
            double unitPrice = price / product.getUnitPerBox();
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

            String sql = "INSERT INTO ProductUnitConversion (ProductID, UnitPerBoxChange, UnitsPerPackChange, BoxQuantity, PackSize, ConversionDate) VALUES (?, ?, ?, ?, ?, GETDATE())";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, productId);
                ps.setInt(2, unitCount != null ? unitCount : 0);
                if (packCount != null) {
                    ps.setInt(3, packCount);
                } else {
                    ps.setNull(3, java.sql.Types.INTEGER);
                }
                ps.setInt(4, boxesToConvert);
                if (packSize > 0 && ("pack".equals(conversionType) || "both".equals(conversionType))) {
                    ps.setInt(5, packSize);
                } else {
                    ps.setNull(5, java.sql.Types.INTEGER);
                }

                int result = ps.executeUpdate();
                if (result > 0) {
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
     * Update Inventory table with converted units
     *
     * @param productId The product ID
     * @param unitQuantity Number of units (lon) converted
     * @param packQuantity Number of packs (lốc) converted (can be null)
     * @param unitPrice Price per unit
     * @param packPrice Price per pack (can be null)
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
                    + "    UPDATE SET Quantity = Quantity + ?, LastUpdated = GETDATE() "
                    + "WHEN NOT MATCHED THEN "
                    + "    INSERT (ProductID, PackageType, Quantity, PackSize, LastUpdated) "
                    + "    VALUES (?, 'UNIT', ?, 0, GETDATE());";

            try (PreparedStatement ps = conn.prepareStatement(sqlUnit)) {
                ps.setInt(1, productId);
                ps.setInt(2, unitQuantity);
                ps.setInt(3, productId);
                ps.setInt(4, unitQuantity);
                ps.executeUpdate();
            }

            // Update or insert PACK (lốc) if exists
            if (packQuantity != null) {
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
                        + "    UPDATE SET Quantity = Quantity + ?, LastUpdated = GETDATE() "
                        + "WHEN NOT MATCHED THEN "
                        + "    INSERT (ProductID, PackageType, Quantity, PackSize, LastUpdated) "
                        + "    VALUES (?, 'PACK', ?, ?, GETDATE());";

                try (PreparedStatement ps = conn.prepareStatement(sqlPack)) {
                    ps.setInt(1, productId);
                    ps.setInt(2, packSize);
                    ps.setInt(3, packQuantity);
                    ps.setInt(4, productId);
                    ps.setInt(5, packQuantity);
                    ps.setInt(6, packSize);
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
     * @param productId The product ID
     * @param boxesToConvert Số thùng được chuyển đổi
     * @param unitCount Số đơn vị được tạo
     * @param packCount Số lốc được tạo
     * @param packSize Số đơn vị trong 1 lốc
     * @param unitPrice Giá 1 đơn vị
     * @param packPrice Giá 1 lốc
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
                        + "    UPDATE SET Quantity = Quantity + ?, LastUpdated = GETDATE() "
                        + "WHEN NOT MATCHED THEN "
                        + "    INSERT (ProductID, PackageType, Quantity, PackSize, LastUpdated) "
                        + "    VALUES (?, 'UNIT', ?, 0, GETDATE());";

                try (PreparedStatement ps = conn.prepareStatement(sqlUnit)) {
                    ps.setInt(1, productId);
                    ps.setInt(2, unitCount);
                    ps.setInt(3, productId);
                    ps.setInt(4, unitCount);
                    ps.executeUpdate();
                }
            }

            if (packCount != null && packCount > 0) {
                // Kiểm tra xem đã có PACK với PackSize này chưa
                String sqlCheckPack = "SELECT Quantity FROM Inventory WHERE ProductID = ? AND PackageType = 'PACK' AND PackSize = ?";
                double currentPackQuantity = 0;
                boolean packRowExists = false;
                try (PreparedStatement psCheck = conn.prepareStatement(sqlCheckPack)) {
                    psCheck.setInt(1, productId);
                    psCheck.setInt(2, packSize);
                    ResultSet rs = psCheck.executeQuery();
                    if (rs.next()) {
                        packRowExists = true;
                        currentPackQuantity = rs.getDouble("Quantity");
                    }
                }

                // Cập nhật hoặc thêm mới PACK
                if (packRowExists) {
                    String sqlUpdatePack = "UPDATE Inventory SET Quantity = ?, LastUpdated = GETDATE() "
                            + "WHERE ProductID = ? AND PackageType = 'PACK' AND PackSize = ?";
                    try (PreparedStatement ps = conn.prepareStatement(sqlUpdatePack)) {
                        ps.setDouble(1, currentPackQuantity + packCount);
                        ps.setInt(2, productId);
                        ps.setInt(3, packSize);
                        ps.executeUpdate();
                    }
                } else {
                    String sqlInsertPack = "INSERT INTO Inventory (ProductID, PackageType, Quantity, PackSize, LastUpdated) "
                            + "VALUES (?, 'PACK', ?, ?, GETDATE())";
                    try (PreparedStatement ps = conn.prepareStatement(sqlInsertPack)) {
                        ps.setInt(1, productId);
                        ps.setDouble(2, packCount);
                        ps.setInt(3, packSize);
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
     * @param productId ID sản phẩm
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
        
        // Lấy thông tin inventory
        String inventorySql = "SELECT PackageType, Quantity, PackSize FROM Inventory WHERE ProductID = ?";
        // Lấy thông tin giá từ Product
        String productSql = "SELECT PriceBox, PriceUnit, PricePack, ItemUnitName, BoxUnitName FROM Product WHERE ProductID = ?";


        try (PreparedStatement ps = conn.prepareStatement(inventorySql)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();

            double totalPackQty = 0.0;
            while (rs.next()) {
                String packageType = rs.getString("PackageType");
                double qty = rs.getDouble("Quantity");

                int packSize = rs.getInt("PackSize");


                if ("PACK".equalsIgnoreCase(packageType)) {
                    Map<String, Object> p = new HashMap<>();
                    p.put("packSize", packSize);
                    p.put("quantity", qty);
                    packList.add(p);
                    totalPackQty += qty;
                } else {
                    String effectiveType = packageType;
                    if ("KG".equalsIgnoreCase(packageType)) {
                        effectiveType = "UNIT"; // Chuẩn hóa key để phía gọi không cần đổi
                    }
                    inventory.put(effectiveType + "_Quantity", qty);
                }
            }
            
            if (!packList.isEmpty()) {
                inventory.put("PACK_LIST", packList);
                inventory.put("PACK_Quantity", totalPackQty);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Lấy thông tin giá từ Product
        try (PreparedStatement ps = conn.prepareStatement(productSql)) {
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                inventory.put("BOX_Price", rs.getObject("PriceBox", Double.class));
                inventory.put("UNIT_Price", rs.getObject("PriceUnit", Double.class));
                inventory.put("PACK_Price", rs.getObject("PricePack", Double.class));
                inventory.put("ItemUnitName", rs.getString("ItemUnitName"));
                inventory.put("BoxUnitName", rs.getString("BoxUnitName"));
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
        if (categoryIds == null || categoryIds.isEmpty()) {
            return result;
        }

        // Chunk nếu cần (ví dụ 500 id mỗi lần)
        final int CHUNK = 500;
        for (int offset = 0; offset < categoryIds.size(); offset += CHUNK) {
            List<Integer> part = categoryIds.subList(offset, Math.min(offset + CHUNK, categoryIds.size()));
            String placeholders = String.join(",", Collections.nCopies(part.size(), "?"));
            String sql = "SELECT ProductID FROM Product WHERE CategoryID IN (" + placeholders + ")";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (int i = 0; i < part.size(); i++) {
                    ps.setInt(i + 1, part.get(i));
                }
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(rs.getInt(1));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * Cập nhật giá bán lẻ cho sản phẩm
     */
    public boolean updateProductPrice(int productId, Double priceBox, Double priceUnit, Double pricePack) {
        String sql = "UPDATE Product SET "
                + "PriceBox = ?, "
                + "PriceUnit = ?, "
                + "PricePack = ? "
                + "WHERE ProductID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            // Set giá trị, nếu null thì set null
            if (priceBox != null) {
                ps.setDouble(1, priceBox);
            } else {
                ps.setNull(1, java.sql.Types.DOUBLE);
            }

            if (priceUnit != null) {
                ps.setDouble(2, priceUnit);
            } else {
                ps.setNull(2, java.sql.Types.DOUBLE);
            }

            if (pricePack != null) {
                ps.setDouble(3, pricePack);
            } else {
                ps.setNull(3, java.sql.Types.DOUBLE);
            }

            ps.setInt(4, productId);

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

//
//    public double getQuantityByPackageType(int productId, String packageType) {
//        double qty = 0.0;
//        try {
//            String sql = "SELECT COALESCE(Quantity, 0) AS Q FROM Inventory WHERE ProductID = ? AND PackageType = ?";
//            try (PreparedStatement ps = conn.prepareStatement(sql)) {
//                ps.setInt(1, productId);
//                ps.setString(2, packageType);
//                try (ResultSet rs = ps.executeQuery()) {
//                    if (rs.next()) {
//                        qty = rs.getDouble("Q");
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return qty;
//    }

    /**
     * Get PACK quantity filtered by packSize
     */
    public double getPackQuantity(int productId, int packSize) {
        double qty = 0.0;
        try {
            String sql = "SELECT COALESCE(Quantity, 0) AS Q FROM Inventory WHERE ProductID = ? AND PackageType = 'PACK' AND PackSize = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, productId);
                ps.setInt(2, packSize);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        qty = rs.getDouble("Q");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return qty;
    }
    
     public List<Product> getProductsByCategoryExpandedFiltered(int categoryId) {
        List<Product> list = new ArrayList<>();
        boolean isFruitCategory = false;
        try {
            String catSql = "SELECT CASE WHEN c.CategoryID = 3 OR c.ParentID = 3 THEN 1 ELSE 0 END AS IsFruit FROM Category c WHERE c.CategoryID = ?";
            try (PreparedStatement ps = conn.prepareStatement(catSql)) {
                ps.setInt(1, categoryId);
                try (ResultSet crs = ps.executeQuery()) {
                    if (crs.next()) {
                        isFruitCategory = crs.getInt("IsFruit") == 1;
                    }
                }
            }

            String sql = "SELECT p.ProductID FROM Product p WHERE p.CategoryID IN (SELECT c.CategoryID FROM Category c WHERE c.ParentID = ? OR c.CategoryID = ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, categoryId);
                ps.setInt(2, categoryId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int pid = rs.getInt(1);
                        Product p = getProductById(pid);
                        if (p == null) {
                            continue;
                        }

                        String packageType = isFruitCategory ? "KG" : "UNIT";
                        double qty = getQuantityByPackageType(pid, packageType);
                        if (qty <= 0) {
                            continue;
                        }

                        Double priceUnit = p.getPriceUnit();
                        if (priceUnit == null) {
                            Product full = getProductById(pid);
                            if (full != null) {
                                priceUnit = full.getPriceUnit();
                            }
                        }
                        String itemUnit = p.getItemUnitName();
                        if (itemUnit == null || itemUnit.trim().isEmpty()) {
                            itemUnit = getItemUnitName(pid);
                        }
                        if (priceUnit == null || itemUnit == null || itemUnit.trim().isEmpty()) {
                            continue;
                        }

                        p.setStockQuantity(qty);
                        list.add(p);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Get latest manufacturer that supplied this product based on latest
     * StockIn.DateIn
     */
    public Manufacturer getLatestManufacturerForProduct(int productId) {
        String sql = "SELECT TOP 1 m.ManufacturerID, m.CompanyName, si.DateIn\n"
                + "FROM Inventory i\n"
                + "JOIN StockInDetail sid ON sid.InventoryID = i.InventoryID\n"
                + "JOIN StockIn si ON si.StockInID = sid.StockInID\n"
                + "JOIN Manufacturer m ON m.ManufacturerID = si.ManufacturerID\n"
                + "WHERE i.ProductID = ?\n"
                + "ORDER BY si.DateIn DESC, si.StockInID DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Manufacturer m = new Manufacturer();
                    m.setManufacturerID(rs.getInt("ManufacturerID"));
                    m.setCompanyName(rs.getString("CompanyName"));
                    return m;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get latest StockIn date for the product (used as manufacture/import date
     * on detail)
     */
    public Date getLatestStockInDateForProduct(int productId) {
        String sql = "SELECT TOP 1 si.DateIn\n"
                + "FROM Inventory i\n"
                + "JOIN StockInDetail sid ON sid.InventoryID = i.InventoryID\n"
                + "JOIN StockIn si ON si.StockInID = sid.StockInID\n"
                + "WHERE i.ProductID = ?\n"
                + "ORDER BY si.DateIn DESC, si.StockInID DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Timestamp t = rs.getTimestamp(1);
                    return t != null ? new Date(t.getTime()) : null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get latest ExpiryDate for the product, if ExpiryDate column exists in
     * StockIn or StockInDetail. Returns null if not available.
     */
    public Date getLatestExpiryDateForProduct(int productId) {
        try {
            // Check if ExpiryDate exists in StockInDetail
            String checkDetail = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'StockInDetail' AND COLUMN_NAME = 'ExpiryDate'";
            try (PreparedStatement ps = conn.prepareStatement(checkDetail)) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        String q = "SELECT TOP 1 sid.ExpiryDate\n"
                                + "FROM Inventory i\n"
                                + "JOIN StockInDetail sid ON sid.InventoryID = i.InventoryID\n"
                                + "JOIN StockIn si ON si.StockInID = sid.StockInID\n"
                                + "WHERE i.ProductID = ? AND sid.ExpiryDate IS NOT NULL\n"
                                + "ORDER BY si.DateIn DESC, si.StockInID DESC";
                        try (PreparedStatement ps2 = conn.prepareStatement(q)) {
                            ps2.setInt(1, productId);
                            try (ResultSet rs2 = ps2.executeQuery()) {
                                if (rs2.next()) {
                                    Timestamp t = rs2.getTimestamp(1);
                                    return t != null ? new Date(t.getTime()) : null;
                                }
                            }
                        }
                    }
                }
            }

            // Check if ExpiryDate exists in StockIn
            String checkHeader = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'StockIn' AND COLUMN_NAME = 'ExpiryDate'";
            try (PreparedStatement ps = conn.prepareStatement(checkHeader)) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        String q = "SELECT TOP 1 si.ExpiryDate\n"
                                + "FROM Inventory i\n"
                                + "JOIN StockInDetail sid ON sid.InventoryID = i.InventoryID\n"
                                + "JOIN StockIn si ON si.StockInID = sid.StockInID\n"
                                + "WHERE i.ProductID = ? AND si.ExpiryDate IS NOT NULL\n"
                                + "ORDER BY si.DateIn DESC, si.StockInID DESC";
                        try (PreparedStatement ps2 = conn.prepareStatement(q)) {
                            ps2.setInt(1, productId);
                            try (ResultSet rs2 = ps2.executeQuery()) {
                                if (rs2.next()) {
                                    Timestamp t = rs2.getTimestamp(1);
                                    return t != null ? new Date(t.getTime()) : null;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lấy thông tin nhà sản xuất của lần nhập gần nhất
     */
    public Map<String, Object> getLatestManufacturerInfo(int productId) {
        Map<String, Object> result = new HashMap<>();
        String sql = "SELECT TOP 1 m.ManufacturerID, m.BrandName, m.CompanyName, si.DateIn, si.StockInID "
                + "FROM Product p "
                + "JOIN Inventory i ON i.ProductID = p.ProductID "
                + "JOIN StockInDetail sid ON sid.InventoryID = i.InventoryID "
                + "JOIN StockIn si ON si.StockInID = sid.StockInID "
                + "JOIN Manufacturer m ON m.ManufacturerID = si.ManufacturerID "
                + "WHERE p.ProductID = ? "
                + "ORDER BY si.DateIn DESC, si.StockInID DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result.put("manufacturerID", rs.getInt("ManufacturerID"));
                    result.put("brandName", rs.getString("BrandName"));
                    result.put("companyName", rs.getString("CompanyName"));
                    result.put("dateIn", rs.getTimestamp("DateIn"));
                    result.put("stockInID", rs.getInt("StockInID"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Lấy ngày hết hạn của lô nhập gần nhất
     */
    public Date getLatestExpiryDate(int productId) {
        String sql = "SELECT TOP 1 si.ExpiryDate "
                + "FROM Product p "
                + "JOIN Inventory i ON i.ProductID = p.ProductID "
                + "JOIN StockInDetail sid ON sid.InventoryID = i.InventoryID "
                + "JOIN StockIn si ON si.StockInID = sid.StockInID "
                + "WHERE p.ProductID = ? AND si.ExpiryDate IS NOT NULL "
                + "ORDER BY si.DateIn DESC, si.StockInID DESC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDate("ExpiryDate");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Lấy số lượng theo package type cụ thể
     */
    public double getQuantityByPackageType(int productId, String packageType) {
        String sql = "SELECT COALESCE(Quantity, 0) as Quantity FROM Inventory WHERE ProductID = ? AND PackageType = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.setString(2, packageType);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("Quantity");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    /**
     * Kiểm tra xem sản phẩm có thuộc danh mục nước giải khát hoặc sữa không
     */
    public boolean isBeverageOrMilkCategory(int productId) {
        String sql = "SELECT CASE WHEN c.CategoryID IN (1, 2) OR c.ParentID IN (1, 2) THEN 1 ELSE 0 END as IsBeverageOrMilk "
                + "FROM Product p "
                + "JOIN Category c ON p.CategoryID = c.CategoryID "
                + "WHERE p.ProductID = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("IsBeverageOrMilk") == 1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
