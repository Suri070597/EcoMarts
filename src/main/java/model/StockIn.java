/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Date;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class StockIn {

    private int stockInID;
    private int supplierID;
    private String supplierName;
    private String receiverName;
    private int receiverID;
    private Date DateIn;
    private String note;
    private String status;
    private List<StockInDetail> details;

    public StockIn() {
    }

    public StockIn(int supplierID, int receiverID, Date DateIn, String note) {
        this.supplierID = supplierID;
        this.receiverID = receiverID;
        this.DateIn = DateIn;
        this.note = note;
    }

    public StockIn(int stockInID, int supplierID, int receiverID, Date DateIn, String note, String status) {
        this.stockInID = stockInID;
        this.supplierID = supplierID;
        this.receiverID = receiverID;
        this.DateIn = DateIn;
        this.note = note;
        this.status = status;
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
     * @return the supplierID
     */
    public int getSupplierID() {
        return supplierID;
    }

    /**
     * @param supplierID the supplierID to set
     */
    public void setSupplierID(int supplierID) {
        this.supplierID = supplierID;
    }

    /**
     * @return the receiverID
     */
    public int getReceiverID() {
        return receiverID;
    }

    /**
     * @param receiverID the receiverID to set
     */
    public void setReceiverID(int receiverID) {
        this.receiverID = receiverID;
    }

    /**
     * @return the DateIn
     */
    public Date getDateIn() {
        return DateIn;
    }

    /**
     * @param DateIn the DateIn to set
     */
    public void setDateIn(Date DateIn) {
        this.DateIn = DateIn;
    }

    /**
     * @return the note
     */
    public String getNote() {
        return note;
    }

    /**
     * @param note the note to set
     */
    public void setNote(String note) {
        this.note = note;
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
     * @return the details
     */
    public List<StockInDetail> getDetails() {
        return details;
    }

    /**
     * @param details the details to set
     */
    public void setDetails(List<StockInDetail> details) {
        this.details = details;
    }

    /**
     * @return the supplierName
     */
    public String getSupplierName() {
        return supplierName;
    }

    /**
     * @param supplierName the supplierName to set
     */
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    /**
     * @return the receiverName
     */
    public String getReceiverName() {
        return receiverName;
    }

    /**
     * @param receiverName the receiverName to set
     */
    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

}
