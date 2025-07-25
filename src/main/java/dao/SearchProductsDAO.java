/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import db.DBContext;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Product;

/**
 *
 * @author LNQB
 */
public class SearchProductsDAO extends DBContext {

    public List<Product> searchProductsByKeyword(String keyword) throws SQLException {
        List<Product> productList = new ArrayList<>();

        if (keyword == null || keyword.trim().length() < 2) {
            return productList;
        }

        String[] words = keyword.trim().split("\\s+");
        StringBuilder sql = new StringBuilder("""
                    SELECT p.*
                    FROM Product p
                    JOIN Category c ON p.CategoryID = c.CategoryID
                    LEFT JOIN Category pc ON c.ParentID = pc.CategoryID
                    JOIN Supplier s ON p.SupplierID = s.SupplierID
                    WHERE
                """);

        // Tạo điều kiện cho mỗi từ khóa với OR logic
        List<String> conditions = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            conditions.add(
                    "(p.ProductName LIKE ? OR c.CategoryName LIKE ? OR pc.CategoryName LIKE ? OR s.CompanyName LIKE ?)");
        }
        sql.append(String.join(" OR ", conditions)); // Sử dụng OR cho các từ

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int index = 1;
            for (String word : words) {
                String like = "%" + word + "%";
                for (int j = 0; j < 4; j++) { // 4 cột mỗi từ
                    stmt.setString(index++, like);
                }
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Product p = new Product();
                p.setProductID(rs.getInt("ProductID"));
                p.setProductName(rs.getString("ProductName"));
                p.setDescription(rs.getString("Description"));
                double rawPrice = rs.getDouble("Price");
                long roundedPrice = Math.round(rawPrice / 1000.0) * 1000;
                p.setPrice(roundedPrice);
                p.setStockQuantity(rs.getDouble("StockQuantity"));
                p.setImageURL(rs.getString("ImageURL"));
                p.setUnit(rs.getString("Unit"));
                p.setManufactureDate(rs.getDate("ManufactureDate"));
                p.setExpirationDate(rs.getDate("ExpirationDate"));
                p.setCategoryID(rs.getInt("CategoryID"));
                p.setSupplierID(rs.getInt("SupplierID"));
                productList.add(p);
            }
        }

        return productList;
    }

    public static void main(String[] args) {
        SearchProductsDAO dao = new SearchProductsDAO();
        try {
            String keyword = "ngon"; // ví dụ: tìm sản phẩm có chứa "sữa" trong tên, mô tả, loại, hoặc nhà cung cấp
            List<Product> products = dao.searchProductsByKeyword(keyword);

            for (Product p : products) {
                System.out.println("ID: " + p.getProductID());
                System.out.println("Tên: " + p.getProductName());
                System.out.println("Mô tả: " + p.getDescription());
                System.out.println("Giá: " + p.getPrice());
                System.out.println("Ảnh: " + p.getImageURL());
                System.out.println("-----------------------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
