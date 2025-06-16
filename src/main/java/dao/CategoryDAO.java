package dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.Category;
import db.DBContext;

public class CategoryDAO extends DBContext {

    public List<Category> getAllCategoriesWithChildren() {
        List<Category> parents = new ArrayList<>();
        String parentSql = "SELECT * FROM Category WHERE ParentID IS NULL";

        try {
            ResultSet rs = execSelectQuery(parentSql);
            while (rs.next()) {
                int id = rs.getInt("CategoryID");
                Category parent = new Category(id, rs.getString("CategoryName"), null);
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
                    parentId
                );
                cat.setChildren(getChildCategories(cat.getCategoryID())); // đệ quy
                list.add(cat);
            }
        } catch (Exception e) {
            System.err.println("Lỗi getChildCategories: " + e.getMessage());
        }

        return list;
    }
}
