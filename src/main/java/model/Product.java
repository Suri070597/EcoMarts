/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Timestamp;
import java.util.Date;

/**
 *
 * @author LNQB
 */
public class Product {

    private int productID;
    private String productName;
    private Double price; // Có thể NULL ban đầu
    private String description;
    private String imageURL;
    private Timestamp createdAt;
    private int categoryID;
    private int manufacturerID;
    private String status;
    private Category category;
    private Manufacturer manufacturer;
    private InventoryTransaction inventory;
    private int unitPerBox;
    private String boxUnitName;
    private String itemUnitName;

    public InventoryTransaction getInventory() {
        return inventory;
    }

    public void setInventory(InventoryTransaction inventory) {
        this.inventory = inventory;
    }

    public Product(InventoryTransaction inventory) {
        this.inventory = inventory;
    }

    public Category getCategory() {
        return category;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public Product(Category category, Manufacturer manufacturer) {
        this.category = category;
        this.manufacturer = manufacturer;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Manufacturer manufacturer) {
        this.manufacturer = manufacturer;
    }

    // Constructors
    public Product() {
    }

    public Product(int productID, String productName, Double price, String description,
            String imageURL, Timestamp createdAt) {
        this.productID = productID;
        this.productName = productName;
        this.price = price;
        this.description = description;
        this.imageURL = imageURL;
        this.createdAt = createdAt;
    }

    public Product(int productID, String productName, Double price, String description,
            String imageURL, Timestamp createdAt, int categoryID, int manufacturerID, String status,
            int unitPerBox, String boxUnitName, String itemUnitName) {
        this.productID = productID;
        this.productName = productName;
        this.price = price;
        this.description = description;
        this.imageURL = imageURL;
        this.createdAt = createdAt;
        this.categoryID = categoryID;
        this.manufacturerID = manufacturerID;
        this.status = status;
        this.unitPerBox = unitPerBox;
        this.boxUnitName = boxUnitName;
        this.itemUnitName = itemUnitName;
    }

    public Product(String productName, Double price, String description, String imageURL,
            int categoryID, int manufacturerID, String status,
            int unitPerBox, String boxUnitName, String itemUnitName) {
        this.productName = productName;
        this.price = price;
        this.description = description;
        this.imageURL = imageURL;
        this.categoryID = categoryID;
        this.manufacturerID = manufacturerID;
        this.status = status;
        this.unitPerBox = unitPerBox;
        this.boxUnitName = boxUnitName;
        this.itemUnitName = itemUnitName;
    }

    // Getters and Setters
    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public int getManufacturerID() {
        return manufacturerID;
    }

    public void setManufacturerID(int manufacturerID) {
        this.manufacturerID = manufacturerID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getUnitPerBox() {
        return unitPerBox;
    }

    public void setUnitPerBox(int unitPerBox) {
        this.unitPerBox = unitPerBox;
    }

    public String getBoxUnitName() {
        return boxUnitName;
    }

    public void setBoxUnitName(String boxUnitName) {
        this.boxUnitName = boxUnitName;
    }

    public String getItemUnitName() {
        return itemUnitName;
    }

    public void setItemUnitName(String itemUnitName) {
        this.itemUnitName = itemUnitName;
    }

    @Override
    public String toString() {
        return "Product{" + "productID=" + productID + ", productName=" + productName + ", price=" + price
                + ", description=" + description + ", imageURL=" + imageURL + ", createdAt=" + createdAt
                + ", categoryID=" + categoryID + ", manufacturerID=" + manufacturerID + ", status=" + status
                + ", unitPerBox=" + unitPerBox + ", boxUnitName=" + boxUnitName + ", itemUnitName=" + itemUnitName
                + '}';
    }
}
