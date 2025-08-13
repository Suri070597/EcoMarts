/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import db.DBContext;
import java.sql.SQLException;
import model.Category;
import model.InventoryTransaction;
import model.Product;
import model.Supplier;

/**
 *
 * @author LNQB
 */
public class ProductDAO extends DBContext {

    public List<Product> getAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.CategoryName, c.ParentID, s.CompanyName,\n"
                + "       uc.UnitPerBox as UC_UnitPerBox, uc.BoxUnitName as UC_BoxUnitName, uc.ItemUnitName as UC_ItemUnitName,\n"
                + "       uc.UnitsPerPack as UC_UnitsPerPack, uc.BoxPrice as UC_BoxPrice, uc.UnitPrice as UC_UnitPrice, uc.PackPrice as UC_PackPrice\n"
                + "FROM Product p\n"
                + "JOIN Category c ON p.CategoryID = c.CategoryID\n"
                + "JOIN Supplier s ON p.SupplierID = s.SupplierID\n"
                + "LEFT JOIN ProductUnitConversion uc ON uc.ProductID = p.ProductID";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Category cat = new Category();
                cat.setCategoryID(rs.getInt("CategoryID"));
                cat.setCategoryName(rs.getString("CategoryName"));
                cat.setParentID(rs.getInt("ParentID"));

                Supplier sup = new Supplier();
                sup.setSupplierID(rs.getInt("supplierID"));
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
                p.setSupplier(sup);

                // Bổ sung các trường đóng gói
                p.setUnitPerBox(rs.getInt("UC_UnitPerBox"));
                p.setBoxUnitName(rs.getString("UC_BoxUnitName"));
                p.setItemUnitName(rs.getString("UC_ItemUnitName"));
                p.setUnitsPerPack(rs.getInt("UC_UnitsPerPack"));
                // Giá theo đơn vị
                try {
                    p.setBoxPrice(rs.getDouble("UC_BoxPrice"));
                } catch (Exception ignore) {
                }
                try {
                    p.setUnitPrice(rs.getDouble("UC_UnitPrice"));
                } catch (Exception ignore) {
                }
                try {
                    p.setPackPrice(rs.getDouble("UC_PackPrice"));
                } catch (Exception ignore) {
                }

                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Product> getAllIncludingOutOfStock() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.CategoryName, c.ParentID, s.CompanyName,\n"
                + "       uc.UnitPerBox as UC_UnitPerBox, uc.BoxUnitName as UC_BoxUnitName, uc.ItemUnitName as UC_ItemUnitName,\n"
                + "       uc.UnitsPerPack as UC_UnitsPerPack, uc.BoxPrice as UC_BoxPrice, uc.UnitPrice as UC_UnitPrice, uc.PackPrice as UC_PackPrice\n"
                + "FROM Product p\n"
                + "JOIN Category c ON p.CategoryID = c.CategoryID\n"
                + "JOIN Supplier s ON p.SupplierID = s.SupplierID\n"
                + "LEFT JOIN ProductUnitConversion uc ON uc.ProductID = p.ProductID";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Category cat = new Category();
                cat.setCategoryID(rs.getInt("CategoryID"));
                cat.setCategoryName(rs.getString("CategoryName"));
                cat.setParentID(rs.getInt("ParentID"));

                Supplier sup = new Supplier();
                sup.setSupplierID(rs.getInt("supplierID"));
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
                p.setSupplier(sup);

                // Bổ sung các trường đóng gói
                p.setUnitPerBox(rs.getInt("UC_UnitPerBox"));
                p.setBoxUnitName(rs.getString("UC_BoxUnitName"));
                p.setItemUnitName(rs.getString("UC_ItemUnitName"));
                p.setUnitsPerPack(rs.getInt("UC_UnitsPerPack"));
                // Giá theo đơn vị
                try {
                    p.setBoxPrice(rs.getDouble("UC_BoxPrice"));
                } catch (Exception ignore) {
                }
                try {
                    p.setUnitPrice(rs.getDouble("UC_UnitPrice"));
                } catch (Exception ignore) {
                }
                try {
                    p.setPackPrice(rs.getDouble("UC_PackPrice"));
                } catch (Exception ignore) {
                }

                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int insert(String name, double price, String description, double quantity,
            String ImageURL, String unit, Timestamp createdAt,
            int categoryID, int supplierID,
            Date manufactureDate, Date expirationDate,
            int unitPerBox, String boxUnitName, String itemUnitName, int unitsPerPack,
            Double boxPrice, Double unitPrice, Double packPrice) {

        if (price < 1000) {
            price *= 1000;
        }

        // Note: conversion-related columns are stored in ProductUnitConversion table
        // now
        String sql = "INSERT INTO Product (productName, price, description, StockQuantity, ImageURL, unit, createdAt, categoryID, supplierID, ManufactureDate, ExpirationDate) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setString(3, description);
            ps.setDouble(4, quantity);
            ps.setString(5, ImageURL);
            ps.setString(6, unit);
            ps.setTimestamp(7, createdAt);
            ps.setInt(8, categoryID);
            ps.setInt(9, supplierID);
            ps.setDate(10, new java.sql.Date(manufactureDate.getTime()));
            ps.setDate(11, new java.sql.Date(expirationDate.getTime()));

            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    // public Product getProductById(int id) {
    // Product product = null;
    // String sql = "SELECT p.*, c.CategoryName, c.ParentID, s.SupplierID,
    // s.CompanyName \n"
    // + "FROM product p \n"
    // + "LEFT JOIN Category c ON p.CategoryID = c.CategoryID \n"
    // + "LEFT JOIN Supplier s ON p.SupplierID = s.SupplierID \n"
    // + "WHERE p.ProductID = ?";
    // try {
    // PreparedStatement ps = conn.prepareStatement(sql);
    // ps.setInt(1, id);
    // ResultSet rs = ps.executeQuery();
    //
    // if (rs.next()) {
    // // Product info
    // String proName = rs.getString("ProductName");
    // double proPrice = rs.getDouble("Price");
    // String description = rs.getString("Description");
    // int quantity = rs.getInt("StockQuantity");
    // String imageURL = rs.getString("ImageURL");
    // String unit = rs.getString("Unit");
    // Timestamp createdAt = rs.getTimestamp("CreatedAt");
    //
    // Date manufactureDate = rs.getDate("ManufactureDate");
    // Date expirationDate = rs.getDate("ExpirationDate");
    //
    // int categoryId = rs.getInt("CategoryID");
    // String categoryName = rs.getString("CategoryName");
    // int parentId = rs.getInt("ParentID");
    // Category category = new Category(categoryId, categoryName, parentId);
    //
    // int supplierId = rs.getInt("SupplierID");
    // String supplierName = rs.getString("CompanyName");
    // Supplier supplier = new Supplier(supplierId, supplierName);
    //
    // product = new Product(id, proName, proPrice, description, quantity, imageURL,
    // unit, createdAt, manufactureDate, expirationDate);
    // product.setCategory(category);
    // product.setSupplier(supplier);
    // }
    // } catch (Exception e) {
    // System.out.println("Error in getProductById: " + e.getMessage());
    // }
    // return product;
    // }
    public Product getProductById(int id) {
        Product product = null;
        String sql = "SELECT p.*, \n"
                + "       c.CategoryName, c.ParentID, \n"
                + "       s.SupplierID, s.CompanyName,\n"
                + "       i.Quantity AS InventoryQuantity, i.LastUpdated,\n"
                + "       uc.UnitPerBox as UC_UnitPerBox, uc.BoxUnitName as UC_BoxUnitName, uc.ItemUnitName as UC_ItemUnitName,\n"
                + "       uc.UnitsPerPack as UC_UnitsPerPack, uc.BoxPrice as UC_BoxPrice, uc.UnitPrice as UC_UnitPrice, uc.PackPrice as UC_PackPrice\n"
                + "FROM product p\n"
                + "LEFT JOIN Category c ON p.CategoryID = c.CategoryID\n"
                + "LEFT JOIN Supplier s ON p.SupplierID = s.SupplierID\n"
                + "LEFT JOIN Inventory i ON p.ProductID = i.ProductID\n"
                + "LEFT JOIN ProductUnitConversion uc ON uc.ProductID = p.ProductID\n"
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

                int supplierId = rs.getInt("SupplierID");
                String supplierName = rs.getString("CompanyName");
                Supplier supplier = new Supplier(supplierId, supplierName);

                int inventoryQty = rs.getInt("InventoryQuantity");
                Timestamp lastUpdated = rs.getTimestamp("LastUpdated");
                InventoryTransaction inventory = new InventoryTransaction(id, inventoryQty, lastUpdated);

                product = new Product(id, proName, proPrice, description, quantity, imageURL, unit, createdAt,
                        manufactureDate, expirationDate);
                product.setCategory(category);
                product.setSupplier(supplier);
                product.setInventory(inventory);
                // Bổ sung các trường đóng gói
                product.setUnitPerBox(rs.getInt("UC_UnitPerBox"));
                product.setBoxUnitName(rs.getString("UC_BoxUnitName"));
                product.setItemUnitName(rs.getString("UC_ItemUnitName"));
                product.setUnitsPerPack(rs.getInt("UC_UnitsPerPack"));
                // Giá theo đơn vị
                try {
                    product.setBoxPrice(rs.getDouble("UC_BoxPrice"));
                } catch (Exception ignore) {
                }
                try {
                    product.setUnitPrice(rs.getDouble("UC_UnitPrice"));
                } catch (Exception ignore) {
                }
                try {
                    product.setPackPrice(rs.getDouble("UC_PackPrice"));
                } catch (Exception ignore) {
                }
            }
        } catch (Exception e) {
            System.out.println("Error in getProductById: " + e.getMessage());
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
                + "SupplierID = ? "
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
            ps.setInt(11, product.getSupplier().getSupplierId());
            ps.setInt(12, product.getProductID());

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;

        } catch (Exception e) {
            System.out.println("Error during update: " + e.getMessage());
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

    public List<Supplier> getAllSuppliers() {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT * FROM Supplier";

        try {
            PreparedStatement st = conn.prepareStatement(sql);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                Supplier s = new Supplier();
                s.setSupplierID(rs.getInt("SupplierID"));
                s.setCompanyName(rs.getString("CompanyName"));
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
                    SELECT p.*, c.CategoryName, c.ParentID, s.SupplierID, s.CompanyName
                    FROM Product p
                    JOIN Category c ON p.CategoryID = c.CategoryID
                    JOIN Supplier s ON p.SupplierID = s.SupplierID
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

                // Set Supplier
                Supplier s = new Supplier();
                s.setSupplierID(rs.getInt("SupplierID"));
                s.setCompanyName(rs.getString("CompanyName"));
                p.setSupplier(s);

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
                list.add(p);
            }
        } catch (Exception e) {
            System.out.println("Error getting related products: " + e.getMessage());
        }
        return list;
    }

    public List<Product> getProductsByCategoryAndSub(int parentCategoryId) {
        List<Product> list = new ArrayList<>();

        String sql = """
                    SELECT p.*, c.CategoryName, c.ParentID, s.SupplierID, s.CompanyName
                    FROM Product p
                    JOIN Category c ON p.CategoryID = c.CategoryID
                    JOIN Supplier s ON p.SupplierID = s.SupplierID
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

                // Set Supplier
                Supplier s = new Supplier();
                s.setSupplierID(rs.getInt("SupplierID"));
                s.setCompanyName(rs.getString("CompanyName"));
                p.setSupplier(s);

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
            System.out.println("Error getting stock quantity: " + e.getMessage());
        }

        return stockQuantity;
    }

    public static void main(String[] args) throws ParseException {
        ProductDAO dao = new ProductDAO();

        int parentCategoryId = 1; // Thay đổi ID này tùy theo dữ liệu thực tế của bạn

        List<Product> products = dao.getProductsByCategoryAndSub(parentCategoryId);

        if (products.isEmpty()) {
            System.out.println("Không có sản phẩm nào thuộc category cha có ID = " + parentCategoryId);
        } else {
            System.out.println("Danh sách sản phẩm thuộc category cha có ID = " + parentCategoryId + ":");
            for (Product p : products) {
                System.out.println("Product ID: " + p.getProductID());
                System.out.println("Name: " + p.getProductName());
                System.out.println("Price: " + p.getPrice());
                System.out.println(
                        "Category ID: " + (p.getCategory() != null ? p.getCategory().getCategoryID() : "null"));
                System.out.println("-----------------------------");
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

    /**
     * Update product stock quantity
     * 
     * @param productId        The product ID
     * @param newStockQuantity The new stock quantity to set
     * @return true if update was successful, false otherwise
     */
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

    /**
     * Decrease product stock by a number of boxes (must be >= 0 and <= current
     * stock)
     */
    public boolean decrementProductStock(int productId, int openedBoxes) {
        String sql = "UPDATE Product SET StockQuantity = StockQuantity - ? WHERE ProductID = ? AND StockQuantity >= ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, openedBoxes);
            ps.setInt(2, productId);
            ps.setInt(3, openedBoxes);
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Insert or update unit conversion settings and unit-specific prices in
     * separate table ProductUnitConversion
     */
    public boolean upsertUnitConversion(int productId,
            int unitPerBox,
            int unitsPerPack,
            String boxUnitName,
            String itemUnitName,
            Double boxPrice,
            Double unitPrice,
            Double packPrice) {
        String sql = "IF EXISTS (SELECT 1 FROM ProductUnitConversion WHERE ProductID = ?)\n"
                + "UPDATE ProductUnitConversion SET UnitPerBox = ?, UnitsPerPack = ?, BoxUnitName = ?, ItemUnitName = ?, BoxPrice = ?, UnitPrice = ?, PackPrice = ? WHERE ProductID = ?\n"
                + "ELSE\n"
                + "INSERT INTO ProductUnitConversion (ProductID, UnitPerBox, UnitsPerPack, BoxUnitName, ItemUnitName, BoxPrice, UnitPrice, PackPrice) VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            int idx = 1;
            // IF EXISTS
            ps.setInt(idx++, productId);
            // UPDATE SET ... WHERE ProductID = ?
            ps.setInt(idx++, unitPerBox);
            ps.setInt(idx++, unitsPerPack);
            ps.setString(idx++, boxUnitName);
            ps.setString(idx++, itemUnitName);
            ps.setDouble(idx++, boxPrice != null ? boxPrice : 0);
            if (unitPrice != null) {
                ps.setDouble(idx++, unitPrice);
            } else {
                ps.setNull(idx++, java.sql.Types.DECIMAL);
            }
            if (packPrice != null) {
                ps.setDouble(idx++, packPrice);
            } else {
                ps.setNull(idx++, java.sql.Types.DECIMAL);
            }
            ps.setInt(idx++, productId);
            // ELSE INSERT (...)
            ps.setInt(idx++, productId);
            ps.setInt(idx++, unitPerBox);
            ps.setInt(idx++, unitsPerPack);
            ps.setString(idx++, boxUnitName);
            ps.setString(idx++, itemUnitName);
            ps.setDouble(idx++, boxPrice != null ? boxPrice : 0);
            if (unitPrice != null) {
                ps.setDouble(idx++, unitPrice);
            } else {
                ps.setNull(idx++, java.sql.Types.DECIMAL);
            }
            if (packPrice != null) {
                ps.setDouble(idx++, packPrice);
            } else {
                ps.setNull(idx++, java.sql.Types.DECIMAL);
            }
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
