/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 *
 * @author LNQB
 */
public class Product {

    private int productID;
    private String productName;
    private double price;
    private String description;
    private int stockQuantity;
    private String imageURL;
    private String unit;
    private Timestamp createdAt;
    private int categoryID;
    private int supplierID;
    private String status;

    // Constructors
    public Product() {
    }

    public Product(int productID, String productName, double price, String description, int stockQuantity,
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

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
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

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
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

    @Override
    public String toString() {
        return "Product{" + "productID=" + productID + ", productName=" + productName + ", price=" + price
                + ", description=" + description + ", stockQuantity=" + stockQuantity + ", imageURL=" + imageURL
                + ", unit=" + unit + ", createdAt=" + createdAt + ", categoryID=" + categoryID + ", supplierID="
                + supplierID + ", status=" + status + '}';
    }
}
