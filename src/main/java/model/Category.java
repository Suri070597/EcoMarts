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
    private String imageURL;
    private List<Product> products;


    public Category() {
    }
    
public Category(int categoryID, String categoryName, Integer parentID, String imageURL) {
    this.categoryID = categoryID;
    this.categoryName = categoryName;
    this.parentID = parentID;
    this.imageURL = imageURL; // ❗ thiếu dòng này trong code cũ
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

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
    public List<Product> getProducts() {
    return products;
}

public void setProducts(List<Product> products) {
    this.products = products;
}

    @Override
    public String toString() {
        return "Category{"
                + "categoryID=" + categoryID
                + ", categoryName='" + categoryName + '\''
                + ", parentID=" + parentID
                + '}';
    }

    public void setDescription(String string) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
