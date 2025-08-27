/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.Date;

/**
 *
 * @author ADMIN
 */
public class StockInDetail {

    private int stockInDetailID;
    private int stockInID;
    private int inventoryID;
    private String productName;
    private double quantity;
    private double unitPrice;
    private Date expiryDate;

    public StockInDetail() {
    }

    public StockInDetail(double quantity, double unitPrice) {
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public StockInDetail(int stockInDetailID, int stockInID, int inventoryID, double quantity, double unitPrice) {
        this.stockInDetailID = stockInDetailID;
        this.stockInID = stockInID;
        this.inventoryID = inventoryID;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    /**
     * @return the stockInDetailID
     */
    public int getStockInDetailID() {
        return stockInDetailID;
    }

    /**
     * @param stockInDetailID the stockInDetailID to set
     */
    public void setStockInDetailID(int stockInDetailID) {
        this.stockInDetailID = stockInDetailID;
    }

    /**
     * @return the stockInID
     */
    public int getStockInID() {
        return stockInID;
    }

    /**
     * @param stockInID the stockInID to set
     */
    public void setStockInID(int stockInID) {
        this.stockInID = stockInID;
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
    public double getUnitPrice() {
        return unitPrice;
    }

    /**
     * @param unitPrice the unitPrice to set
     */
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
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

}
