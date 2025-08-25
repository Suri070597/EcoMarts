/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Date;

/**
 *
 * @author ADMIN
 */
public class Inventory {
    private int inventoryID;
    private int productID;
    private String productName;
    private String packageType;
    private double quantity;
    private Double unitPrice; // Giá bán dựa theo Product.Price
    private Double costPrice; // Giá nhập kho thực tế (chỉ cho BOX)
    private int packSize;
    private Date lastUpdated;
    private String status;

    // Thêm các trường mới cho FIFO
    private String lotNumber; // Số lô hàng
    private Date expiryDate; // Hạn sử dụng
    // Bỏ RemainingQuantity - không cần thiết
    private Date lotDate; // Ngày tạo lô

    public Inventory() {
    }

    public Inventory(int inventoryID, int productID, String productName, double quantity, String status) {
        this.inventoryID = inventoryID;
        this.productID = productID;
        this.productName = productName;
        this.quantity = quantity;
        this.status = status;
    }

    public Inventory(int productID, String packageType, double quantity, Double unitPrice, Double costPrice) {
        this.productID = productID;
        this.packageType = packageType;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.costPrice = costPrice;
        this.lotDate = new Date(System.currentTimeMillis()); // Set LotDate mặc định
    }

    public Inventory(int inventoryID, int productID, String productName, String packageType, double quantity,
            Double unitPrice, Double costPrice, int packSize, Date lastUpdated, String status) {
        this.inventoryID = inventoryID;
        this.productID = productID;
        this.productName = productName;
        this.packageType = packageType;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.costPrice = costPrice;
        this.packSize = packSize;
        this.lastUpdated = lastUpdated;
        this.status = status;
    }

    /**
     * @return the inventoryID
     */
    public int getInventoryID() {
        return inventoryID;
    }

    /**
     * @param inventoryID the inventoryID to set
     */
    public void setInventoryID(int inventoryID) {
        this.inventoryID = inventoryID;
    }

    /**
     * @return the productID
     */
    public int getProductID() {
        return productID;
    }

    /**
     * @param productID the productID to set
     */
    public void setProductID(int productID) {
        this.productID = productID;
    }

    /**
     * @return the productName
     */
    public String getProductName() {
        return productName;
    }

    /**
     * @param productName the productName to set
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * @return the quantity
     */
    public double getQuantity() {
        return quantity;
    }

    /**
     * @param quantity the quantity to set
     */
    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    /**
     * @return the unitPrice
     */
    public Double getUnitPrice() {
        return unitPrice;
    }

    /**
     * @param unitPrice the unitPrice to set
     */
    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    /**
     * @return the costPrice
     */
    public Double getCostPrice() {
        return costPrice;
    }

    /**
     * @param costPrice the costPrice to set
     */
    public void setCostPrice(Double costPrice) {
        this.costPrice = costPrice;
    }

    /**
     * @return the lastUpdated
     */
    public Date getLastUpdated() {
        return lastUpdated;
    }

    /**
     * @param lastUpdated the lastUpdated to set
     */
    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the packageType
     */
    public String getPackageType() {
        return packageType;
    }

    /**
     * @param packageType the packageType to set
     */
    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    /**
     * @return the lotNumber
     */
    public String getLotNumber() {
        return lotNumber;
    }

    /**
     * @param lotNumber the lotNumber to set
     */
    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }

    /**
     * @return the expiryDate
     */
    public Date getExpiryDate() {
        return expiryDate;
    }

    /**
     * @param expiryDate the expiryDate to set
     */
    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    /**
     * @return the lotDate
     */
    public Date getLotDate() {
        return lotDate;
    }

    /**
     * @param lotDate the lotDate to set
     */
    public void setLotDate(Date lotDate) {
        this.lotDate = lotDate;
    }

    @Override
    public String toString() {
        return "Inventory{" + "inventoryID=" + inventoryID + ", productID=" + productID + ", productName=" + productName
                + ", packageType=" + packageType + ", quantity=" + quantity + ", unitPrice=" + unitPrice
                + ", costPrice=" + costPrice + ", packSize="
                + packSize + ", lastUpdated=" + lastUpdated + ", status=" + status + ", lotNumber=" + lotNumber
                + ", lotDate=" + lotDate + '}';
    }
}
