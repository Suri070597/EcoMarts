/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import db.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Product;

/**
 *
 * @author LNQB
 */
public class ViewProductDAO extends DBContext {

    public List<Product> getProductsByCategory(int parentCategoryId) {
        List<Product> list = new ArrayList<>();
        String sql = """
            SELECT p.productID, p.ProductName, p.Price, p.ImageURL, p.Unit
                        FROM Product p
                        JOIN Category c ON p.CategoryID = c.CategoryID
                        WHERE c.ParentID = ?
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, parentCategoryId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Product p = new Product();
                p.setProductID(rs.getInt("ProductID"));
                p.setProductName(rs.getString("ProductName"));
                p.setPrice(rs.getDouble("Price"));
                p.setImageURL(rs.getString("ImageURL"));
                p.setUnit(rs.getString("Unit"));
                list.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Product> getMilkProducts() {
        return getProductsByCategory(2); // Sữa các loại
    }

    public List<Product> getCandyProducts() {
        return getProductsByCategory(4); // Bánh kẹo các loại
    }

    public List<Product> getFruitProducts() {
        return getProductsByCategory(3); // Trái cây các loại
    }

    public List<Product> getMotherBabyProducts() {
        return getProductsByCategory(5); // Mẹ và bé
    }

    public List<Product> getDrinkProducts() {
        return getProductsByCategory(1); // Nước giải khát
    }

    public List<Product> getCosmeticProducts() {
        return getProductsByCategory(6); // Mỹ phẩm
    }

    public List<Product> getFeaturedProducts() {
        return getProductsByCategory(7); // "Sản phẩm nổi bật" là danh mục cha của ID 48
    }

    public static void main(String[] args) {
        ViewProductDAO dao = new ViewProductDAO();

        // Test từng danh mục
        System.out.println("=== SẢN PHẨM SỮA ===");
        printProductList(dao.getMilkProducts());

        System.out.println("\n=== BÁNH KẸO ===");
        printProductList(dao.getCandyProducts());

        System.out.println("\n=== TRÁI CÂY ===");
        printProductList(dao.getFruitProducts());

        System.out.println("\n=== MẸ VÀ BÉ ===");
        printProductList(dao.getMotherBabyProducts());

        System.out.println("\n=== NƯỚC GIẢI KHÁT ===");
        printProductList(dao.getDrinkProducts());

        System.out.println("\n=== MỸ PHẨM ===");
        printProductList(dao.getCosmeticProducts());

        System.out.println("\n=== SẢN PHẨM NỔI BẬT ===");
        printProductList(dao.getFeaturedProducts());
    }

    // In danh sách sản phẩm
    private static void printProductList(List<Product> list) {
        if (list.isEmpty()) {
            System.out.println("Không có sản phẩm nào.");
        } else {
            for (Product p : list) {
                System.out.println("Tên: " + p.getProductName());
                System.out.println("Giá: " + p.getPrice() + " VNĐ / " + p.getUnit());
                System.out.println("Hình ảnh: " + p.getImageURL());
                System.out.println("---------------------------");
            }
        }
    }
}
