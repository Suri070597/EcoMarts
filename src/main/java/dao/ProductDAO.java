/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.Product;
import db.DBContext;

/**
 *
 * @author LNQB
 */
public class ProductDAO extends DBContext {

    public List<Product> getAll() {
        List<Product> product = new ArrayList<>();
        String sql = "select p.ProductID, p.ProductName, p.Price, p.Description, p.StockQuantity, p.ImageURL, p.Unit, p.CreatedAt from Product as p";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("ProductID");
                String ProductName = rs.getString("ProductName");
                double Price = rs.getDouble("Price");
                String Description = rs.getString("Description");
                int StockQuantity = rs.getInt("StockQuantity");
                String ImageURL = rs.getString("ImageURL");
                String Unit = rs.getString("Unit");
                Timestamp date = rs.getTimestamp("CreatedAt");

                Product pro = new Product(id, ProductName, Price, Description, StockQuantity, ImageURL, Unit, date);
                product.add(pro);
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return product;
    }

    public int insert(String name, double price, String description, int stockQuantity, String imageURL, String unit,
            Timestamp date) {
        // String getMaxId = "Select max(id) as maxid from movies";
        // int nextId;
        // try {
        // PreparedStatement st = conn.prepareStatement(getMaxId);
        // ResultSet rs = st.executeQuery();
        // if (rs.next()) {
        // nextId = rs.getInt("maxid") + 1;
        // System.out.println("id: " + nextId);
        String sql = "INSERT INTO Product (ProductName, Price, [Description], StockQuantity, ImageURL, Unit, CreatedAt)\n"
                + "VALUES\n"
                + "(?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement st1 = conn.prepareStatement(sql);
            // st1.setInt(1, nextId);
            st1.setString(1, name);
            st1.setDouble(2, price);
            st1.setString(3, description);
            st1.setInt(4, stockQuantity);
            st1.setString(5, imageURL);
            st1.setString(6, unit);
            st1.setTimestamp(7, date);
            int rowsAffected = st1.executeUpdate();
            st1.close();
            if (rowsAffected > 0) {
                return 1; // Thêm thành công
            } else {
                return 0; // Thêm không thành công
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return 0;
        }
        // }
        // return 0;
        // } catch (Exception e) {
        // System.out.println(e.getMessage());
        // return 0;
        // }
    }

    public static void main(String[] args) {
        ProductDAO dao = new ProductDAO();

        // Dữ liệu mẫu để insert
        String name = "Nước mắm Phú Quốc";
        double price = 45000.0;
        String description = "Nước mắm truyền thống Phú Quốc, đậm đà hương vị Việt Nam";
        int stockQuantity = 50;
        String imageURL = "nuocmam.jpg";
        String unit = "chai"; // Ví dụ: chai, gói,...
        Timestamp date = new Timestamp(System.currentTimeMillis());

        // Gọi insert()
        int result = dao.insert(name, price, description, stockQuantity, imageURL, unit, date);

        if (result == 1) {
            System.out.println("Insert thành công!");
        } else {
            System.out.println("Insert thất bại!");
        }

        // List<Product> productList = dao.getAll();
        //
        // for (Product p : productList) {
        // System.out.println("ID: " + p.getProductID());
        // System.out.println("Name: " + p.getProductName());
        // System.out.println("Price: " + p.getPrice());
        // System.out.println("Description: " + p.getDescription());
        // System.out.println("Quantity: " + p.getStockQuantity());
        // System.out.println("Image: " + p.getImageURL());
        // System.out.println("Unit: " + p.getUnit());
        // System.out.println("CreatedAt: " + p.getCreatedAt());
        // System.out.println("-------------------------");
        // }
        // }
    }
}
