/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import db.DBContext;
import java.util.Date;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.Category;
import model.Product;
import model.Supplier;

/**
 *
 * @author LNQB
 */
public class productDAO extends DBContext {

    public List<Product> getAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.categoryName, c.parentID, s.CompanyName FROM Product p \n"
                + "                                JOIN Category c ON p.categoryID = c.categoryID \n"
                + "                               JOIN Supplier s ON p.supplierID = s.supplierID";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Category cat = new Category();
                cat.setCategoryID(rs.getInt("categoryID"));
                cat.setCategoryName(rs.getString("categoryName"));
                cat.setParentID(rs.getInt("parentID"));

                Supplier sup = new Supplier();
                sup.setSupplierID(rs.getInt("supplierID"));
                sup.setCompanyName(rs.getString("CompanyName"));

                Product p = new Product();
                p.setProductID(rs.getInt("productID"));
                p.setProductName(rs.getString("productName"));
                p.setPrice(rs.getDouble("price"));
                p.setDescription(rs.getString("description"));
                p.setStockQuantity(rs.getInt("StockQuantity"));
                p.setImageURL(rs.getString("ImageURL"));
                p.setUnit(rs.getString("unit"));
                p.setCreatedAt(rs.getTimestamp("createdAt"));
                p.setCategory(cat);
                p.setSupplier(sup);

                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int insert(String name, double price, String description, int quantity,
            String ImageURL, String unit, Timestamp createdAt,
            int categoryID, int supplierID) {
        String sql = "INSERT INTO Product (productName, price, description, StockQuantity, ImageURL, unit, createdAt, categoryID, supplierID) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setString(3, description);
            ps.setInt(4, quantity);
            ps.setString(5, ImageURL);
            ps.setString(6, unit);
            ps.setTimestamp(7, createdAt);
            ps.setInt(8, categoryID);
            ps.setInt(9, supplierID);
            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Product getProductById(int id) {
        Product product = null;
        String sql = "SELECT p.*, c.CategoryName, c.ParentID, s.SupplierID, s.CompanyName \n"
                + "                             FROM product p \n"
                + "                             LEFT JOIN Category c ON p.CategoryID = c.CategoryID \n"
                + "                               LEFT JOIN supplier s ON p.SupplierID = s.SupplierID \n"
                + "                               WHERE p.ProductID = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Product info
                String proName = rs.getString("ProductName");
                double proPrice = rs.getDouble("Price");
                String description = rs.getString("Description");
                int quantity = rs.getInt("StockQuantity");
                String ImageURL = rs.getString("ImageURL");
                String unit = rs.getString("Unit");
                Timestamp createdAt = rs.getTimestamp("CreatedAt");

                // Category info
                int categoryId = rs.getInt("CategoryID");
                String categoryName = rs.getString("CategoryName");
                int parentId = rs.getInt("ParentID");
                Category category = new Category(categoryId, categoryName, parentId);

                // Supplier info
                int supplierId = rs.getInt("SupplierID");
                String supplierName = rs.getString("CompanyName");
                Supplier supplier = new Supplier(supplierId, supplierName);

                // Create Product
                product = new Product(parentId, proName, proPrice, description, quantity, ImageURL, unit, createdAt);
                product.setCategory(category);
                product.setSupplier(supplier);  // nếu có field supplier
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
        return product;
    }

//
    public int delete(int id) {
        String sql1 = "DELETE FROM Product_Promotion WHERE ProductID = ?";
        String sql2 = "DELETE FROM InventoryTransaction WHERE ProductID = ?";
        String sql3 = "DELETE FROM Product WHERE ProductID = ?";

        int rowsAffected = 0;

        PreparedStatement ps1 = null, ps2 = null, ps3 = null;

        try {
            conn.setAutoCommit(false);

            // Xóa Promotion_Product
            ps1 = conn.prepareStatement(sql1);
            ps1.setInt(1, id);
            ps1.executeUpdate();

            // Xóa Inventory
            ps2 = conn.prepareStatement(sql2);
            ps2.setInt(1, id);
            ps2.executeUpdate();

            // Xóa Product
            ps3 = conn.prepareStatement(sql3);
            ps3.setInt(1, id);
            rowsAffected = ps3.executeUpdate();

            conn.commit();

        } catch (Exception e) {
            System.out.println("Delete Error: " + e.getMessage());
            try {
                conn.rollback();
            } catch (Exception ex) {
                System.out.println("Rollback failed: " + ex.getMessage());
            }
            rowsAffected = -1;
        } finally {
            try {
                if (ps1 != null) {
                    ps1.close();
                }
                if (ps2 != null) {
                    ps2.close();
                }
                if (ps3 != null) {
                    ps3.close();
                }
                conn.setAutoCommit(true);
            } catch (Exception e) {
                System.out.println("Cleanup failed: " + e.getMessage());
            }
        }

        return rowsAffected;
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
                + "CategoryID = ?, "
                + "SupplierID = ? "
                + "WHERE ProductID = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, product.getProductName());
            ps.setDouble(2, product.getPrice());
            ps.setString(3, product.getDescription());
            ps.setInt(4, product.getStockQuantity());
            ps.setString(5, product.getImageURL());
            ps.setString(6, product.getUnit());
            ps.setTimestamp(7, product.getCreatedAt());
            ps.setInt(8, product.getCategory().getCategoryID());
            ps.setInt(9, product.getSupplier().getSupplierId());
            ps.setInt(10, product.getProductID());

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

    public static void main(String[] args) {
        productDAO dao = new productDAO();

        // Test delete()
        System.out.println("=================================");
        System.out.println("🔹 Test xóa Product");

        int deleteProductId = 4; // <-- Thay ID này bằng ProductID bạn muốn test
        int result = dao.delete(deleteProductId);

        if (result > 0) {
            System.out.println("✅ Xóa sản phẩm có ID = " + deleteProductId + " thành công.");
        } else if (result == 0) {
            System.out.println("⚠️ Không tìm thấy sản phẩm để xóa với ID = " + deleteProductId);
        } else {
            System.out.println("❌ Đã xảy ra lỗi khi xóa sản phẩm có ID = " + deleteProductId);
        }

    }
}
