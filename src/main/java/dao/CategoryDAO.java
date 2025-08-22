package dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Category;
import db.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
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
                        rs.getString("ImageURL"));
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
                        parentId, rs.getString("ImageURL")
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

    public boolean deleteCategory(int id) {
        String sql = "DELETE FROM Category WHERE CategoryID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    //====================================================//
    public Category getCategoryById(int id) {
        String sql = "SELECT * FROM Category WHERE CategoryID = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Category(
                            rs.getInt("CategoryID"),
                            rs.getString("CategoryName"),
                            rs.getObject("ParentID") != null ? rs.getInt("ParentID") : null,
                            rs.getString("ImageURL")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Mở rộng các category gốc thành toàn bộ hậu duệ (bao gồm chính nó). Không
     * dùng CTE: lặp SELECT theo frontier.
     */
    public List<Integer> getDescendantCategoryIds(List<Integer> rootIds) {
        List<Integer> all = new ArrayList<>();
        if (rootIds == null || rootIds.isEmpty()) {
            return all;
        }

        // bỏ trùng + copy
        LinkedHashSet<Integer> set = new LinkedHashSet<>(rootIds);
        all.addAll(set);

        List<Integer> frontier = new ArrayList<>(set);
        final String baseSql = "SELECT CategoryID FROM Category WHERE ParentID IN (%s)";

        try {
            while (!frontier.isEmpty()) {
                String placeholders = String.join(",", Collections.nCopies(frontier.size(), "?"));
                String sql = String.format(baseSql, placeholders);

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    for (int i = 0; i < frontier.size(); i++) {
                        ps.setInt(i + 1, frontier.get(i));
                    }
                    List<Integer> next = new ArrayList<>();
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            int cid = rs.getInt(1);
                            if (!set.contains(cid)) {
                                set.add(cid);
                                next.add(cid);
                            }
                        }
                    }
                    frontier = next;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>(set);
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT CategoryID, CategoryName, ParentID FROM Category ORDER BY CategoryID";

        try (Connection conn = new DBContext().getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Category c = new Category();
                c.setCategoryID(rs.getInt("CategoryID"));
                c.setCategoryName(rs.getString("CategoryName"));
                c.setParentID(rs.getInt("ParentID"));
                categories.add(c);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categories;
    }

}
