/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 *
 * @author LNQB
 */
public class Product {

    private int productID;
    private String productName;
    private double price;
    private String description;
    private double stockQuantity;
    private String imageURL;
    private String unit;
    private Timestamp createdAt;
    private int categoryID;
    private int supplierID;
    private String status;
    private Date manufactureDate;
    private Date expirationDate;
    private Category category;
    private Supplier supplier;
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

    public Product(Category category, Supplier supplier) {
        this.category = category;
        this.supplier = supplier;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    // Constructors
    public Product() {
    }

    public Product(int productID, String productName, double price, String description, double stockQuantity,
            String imageURL, String unit, Timestamp createdAt) {
        this.productID = productID;
        this.productName = productName;
        this.price = price;
        this.description = description;
        this.stockQuantity = stockQuantity;
        this.imageURL = imageURL;
        this.unit = unit;
        this.createdAt = createdAt;
        // this.categoryID = categoryID;
        // this.supplierID = supplierID;
        // this.status = status;
    }

    public Product(int productID, String productName, double price, String description, double stockQuantity,
            String imageURL, String unit, Timestamp createdAt,
            Date manufactureDate, Date expirationDate) {
        this.productID = productID;
        this.productName = productName;
        this.price = price;
        this.description = description;
        this.stockQuantity = stockQuantity;
        this.imageURL = imageURL;
        this.unit = unit;
        this.createdAt = createdAt;
        this.manufactureDate = manufactureDate;
        this.expirationDate = expirationDate;
    }

    public Date getManufactureDate() {
        return manufactureDate;
    }

    public void setManufactureDate(Date manufactureDate) {
        this.manufactureDate = manufactureDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(double stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public int getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(int supplierID) {
        this.supplierID = supplierID;
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

    /**
     * Gets the quantity from stockQuantity field
     *
     * @return The stock quantity
     */
    public double getAvailableQuantity() {
        return this.stockQuantity;
    }

    @Override
    public String toString() {
        return "Product{" + "productID=" + productID + ", productName=" + productName + ", price=" + price
                + ", description=" + description + ", stockQuantity=" + stockQuantity + ", imageURL=" + imageURL
                + ", unit=" + unit + ", createdAt=" + createdAt + ", categoryID=" + categoryID + ", supplierID="
                + supplierID + ", status=" + status + ", manufactureDate=" + manufactureDate + ", expirationDate="
                + expirationDate + ", category=" + category + ", supplier=" + supplier + '}';
    }
}
