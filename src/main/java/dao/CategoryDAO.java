package dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Category;
import db.DBContext;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CategoryDAO extends DBContext {

    public List<Category> getAllCategoriesWithChildren() {
        List<Category> parents = new ArrayList<>();
        String parentSql = "SELECT * FROM Category WHERE ParentID IS NULL";

        try {
            ResultSet rs = execSelectQuery(parentSql);
            while (rs.next()) {
                int id = rs.getInt("CategoryID");
                Category parent = new Category(id, rs.getString("CategoryName"), 
                        null, 
                        rs.getString("ImageURLURL"));
                parent.setChildren(getChildCategories(id));
                parents.add(parent);
            }
        } catch (Exception e) {
            System.err.println("Lỗi getAllCategoriesWithChildren: " + e.getMessage());
        }

        return parents;
    }

    private List<Category> getChildCategories(int parentId) {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Category WHERE ParentID = ?";

        try {
            ResultSet rs = execSelectQuery(sql, new Object[]{parentId});
            while (rs.next()) {
                Category cat = new Category(
                        rs.getInt("CategoryID"),
                        rs.getString("CategoryName"),
                        parentId, rs.getString("ImageURLURL")
                );
                cat.setChildren(getChildCategories(cat.getCategoryID())); // đệ quy
                list.add(cat);
            }
        } catch (Exception e) {
            System.err.println("Lỗi getChildCategories: " + e.getMessage());
        }

        return list;
    }

    public List<Category> getParentCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM Category WHERE ParentID IS NULL";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Category(
                        rs.getInt("CategoryID"),
                        rs.getString("CategoryName"),
                        null, rs.getString("ImageURL")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Map<Integer, List<Category>> getChildCategoriesGrouped() {
        Map<Integer, List<Category>> map = new HashMap<>();
        String sql = "SELECT * FROM Category WHERE ParentID IS NOT NULL";
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int parentId = rs.getInt("ParentID");
                Category cat = new Category(
                        rs.getInt("CategoryID"),
                        rs.getString("CategoryName"),
                        parentId, rs.getString("ImageURL")
                );
                map.computeIfAbsent(parentId, k -> new ArrayList<>()).add(cat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    public void insertCategory(String name, int parentId) {
        String sql = "INSERT INTO Category (CategoryName, ParentID) VALUES (?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, parentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteCategory(int id) {
        String sql = "DELETE FROM Category WHERE CategoryID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
