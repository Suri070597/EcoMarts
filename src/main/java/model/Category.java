package model;

import java.util.List;

/**
 *
 * @author MSI Gaming
 */
public class Category {

    private int categoryID;
    private String categoryName;
    private Integer parentID;
    private List<Category> children;

    public Category() {
    }
    

    public Category(int categoryID, String categoryName, Integer parentID) {
        this.categoryID = categoryID;
        this.categoryName = categoryName;
        this.parentID = parentID;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getParentID() {
        return parentID;
    }

    public void setParentID(Integer parentID) {
        this.parentID = parentID;
    }

    public List<Category> getChildren() {
        return children;
    }

    public void setChildren(List<Category> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "Category{"
                + "categoryID=" + categoryID
                + ", categoryName='" + categoryName + '\''
                + ", parentID=" + parentID
                + '}';
    }
}
