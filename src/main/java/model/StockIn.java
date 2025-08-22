package model;

import java.sql.Date;
import java.util.List;

public class StockIn {

    private int stockInID;
    private int manufacturerID;
    private String manufacturerName;
    private String receiverName;
    private int receiverID;
    private Date dateIn;
    private String note;
    private String status;
    private List<StockInDetail> details;

    public StockIn() {
    }

    public StockIn(int manufacturerID, int receiverID, Date dateIn, String note) {
        this.manufacturerID = manufacturerID;
        this.receiverID = receiverID;
        this.dateIn = dateIn;
        this.note = note;
    }

    public StockIn(int stockInID, int manufacturerID, int receiverID, Date dateIn, String note, String status) {
        this.stockInID = stockInID;
        this.manufacturerID = manufacturerID;
        this.receiverID = receiverID;
        this.dateIn = dateIn;
        this.note = note;
        this.status = status;
    }

    public int getStockInID() {
        return stockInID;
    }

    public void setStockInID(int stockInID) {
        this.stockInID = stockInID;
    }

    public int getManufacturerID() {
        return manufacturerID;
    }

    public void setManufacturerID(int manufacturerID) {
        this.manufacturerID = manufacturerID;
    }

    public int getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(int receiverID) {
        this.receiverID = receiverID;
    }

    public Date getDateIn() {
        return dateIn;
    }

    public void setDateIn(Date dateIn) {
        this.dateIn = dateIn;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<StockInDetail> getDetails() {
        return details;
    }

    public void setDetails(List<StockInDetail> details) {
        this.details = details;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }
}
