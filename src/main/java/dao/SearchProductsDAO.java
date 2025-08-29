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
        StringBuilder sql = new StringBuilder(
                """
                            SELECT p.*, (SELECT TOP 1 sd.ExpiryDate FROM StockInDetail sd JOIN StockIn si ON sd.StockInID = si.StockInID JOIN Inventory inv ON sd.InventoryID = inv.InventoryID WHERE inv.ProductID = p.ProductID AND si.Status = 'Completed' ORDER BY si.DateIn DESC, sd.StockInDetailID DESC) as LatestExpiryDate
                            FROM Product p
                            JOIN Category c ON p.CategoryID = c.CategoryID
                            LEFT JOIN Category pc ON c.ParentID = pc.CategoryID
                            WHERE 1=1 AND
                        """);

        // Tạo điều kiện cho mỗi từ khóa với OR logic (product/category only)
        List<String> conditions = new ArrayList<>();
        for (int i = 0; i < words.length; i++) {
            conditions.add(
                    "(p.ProductName LIKE ? OR c.CategoryName LIKE ? OR pc.CategoryName LIKE ?)");
        }
        sql.append(String.join(" OR ", conditions)); // Sử dụng OR cho các từ

        try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
            int index = 1;
            for (String word : words) {
                String like = "%" + word + "%";
                for (int j = 0; j < 3; j++) { // 3 cột mỗi từ
                    stmt.setString(index++, like);
                }
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Product p = new Product();
                p.setProductID(rs.getInt("ProductID"));
                p.setProductName(rs.getString("ProductName"));
                p.setDescription(rs.getString("Description"));
                // Dùng schema mới: lấy PriceUnit và ItemUnitName để hiển thị về sau
                try {
                    p.setPriceUnit(rs.getObject("PriceUnit", Double.class));
                } catch (Exception ignore) {
                }
                try {
                    p.setPrice(rs.getObject("PriceBox", Double.class));
                } catch (Exception ignore) {
                }
                try {
                    p.setPricePack(rs.getObject("PricePack", Double.class));
                } catch (Exception ignore) {
                }
                p.setImageURL(rs.getString("ImageURL"));
                p.setCategoryID(rs.getInt("CategoryID"));
                // Bổ sung các trường đơn vị
                try {
                    p.setUnitPerBox(rs.getInt("UnitPerBox"));
                } catch (Exception ignore) {
                }
                try {
                    p.setBoxUnitName(rs.getString("BoxUnitName"));
                } catch (Exception ignore) {
                }
                try {
                    p.setItemUnitName(rs.getString("ItemUnitName"));
                } catch (Exception ignore) {
                }
                // Thêm ngày hết hạn
                try {
                    p.setExpirationDate(rs.getDate("LatestExpiryDate"));
                } catch (Exception ignore) {
                }
                productList.add(p);
            }
        }

        return productList;
    }

    public static void main(String[] args) {
        SearchProductsDAO dao = new SearchProductsDAO();
        try {
            String keyword = "ngon"; // ví dụ
            List<Product> products = dao.searchProductsByKeyword(keyword);

            for (Product p : products) {
                System.out.println("ID: " + p.getProductID());
                System.out.println("Tên: " + p.getProductName());
                System.out.println("Mô tả: " + p.getDescription());
                System.out.println("Giá Unit: " + p.getPriceUnit());
                System.out.println("Ảnh: " + p.getImageURL());
                System.out.println("-----------------------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
