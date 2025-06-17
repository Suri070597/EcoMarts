/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import db.DBContext;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
public class ProductDAO extends DBContext {

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
                product = new Product(id, proName, proPrice, description, quantity, ImageURL, unit, createdAt);
                product.setCategory(category);
                product.setSupplier(supplier);  // nếu có field supplier
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
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

    public List<Product> searchProductsByName(String keyword) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM Product WHERE ProductName LIKE ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Product p = new Product();
                p.setProductID(rs.getInt("ProductID"));
                p.setProductName(rs.getString("ProductName"));
                p.setPrice(rs.getDouble("Price"));
                p.setDescription(rs.getString("Description"));
                p.setStockQuantity(rs.getInt("StockQuantity"));
                p.setImageURL(rs.getString("ImageURL"));
                p.setUnit(rs.getString("Unit"));
                p.setCreatedAt(rs.getTimestamp("CreatedAt"));
                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void main(String[] args) {
        ProductDAO dao = new ProductDAO(); // Đảm bảo DBContext đã kết nối thành công

        // 👇 In ra danh sách sản phẩm ban đầu
        System.out.println("=== Danh sách sản phẩm trước khi xóa ===");
        List<Product> productsBefore = dao.getAll();
        for (Product p : productsBefore) {
            System.out.println("➡️ ID: " + p.getProductID() + " | Name: " + p.getProductName());
        }

        // 👇 ID sản phẩm bạn muốn xóa (nhớ đảm bảo ID này tồn tại)
        int productIdToDelete = 20;

        // 👇 Gọi phương thức delete
        boolean deleted = dao.delete(productIdToDelete);
        System.out.println("\n❗Kết quả xóa sản phẩm có ID " + productIdToDelete + ": " + (deleted ? "Thành công ✅" : "Thất bại ❌"));

        // 👇 Kiểm tra lại danh sách sau khi xóa
        System.out.println("\n=== Danh sách sản phẩm sau khi xóa ===");
        List<Product> productsAfter = dao.getAll();
        for (Product p : productsAfter) {
            System.out.println("➡️ ID: " + p.getProductID() + " | Name: " + p.getProductName());
        }

        // 👇 Kiểm tra xem sản phẩm đã thực sự bị xóa chưa
        boolean stillExists = productsAfter.stream().anyMatch(p -> p.getProductID() == productIdToDelete);
        if (!stillExists) {
            System.out.println("\n✅ Sản phẩm đã được xóa khỏi hệ thống.");
        } else {
            System.out.println("\n❌ Sản phẩm vẫn còn trong hệ thống.");
        }
    }
}
