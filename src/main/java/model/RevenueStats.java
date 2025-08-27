package model;

public class RevenueStats {

    private String productName;
    private double totalQuantity;
    private double totalRevenue;
    private int month;
    private String unit; // label for unit (e.g., thùng, lốc 6 chai, kg, ...)

    public RevenueStats() {
    }

    public RevenueStats(String productName, double totalQuantity, double totalRevenue) {
        this.productName = productName;
        this.totalQuantity = totalQuantity;
        this.totalRevenue = totalRevenue;
    }

    public RevenueStats(String productName, String unit, double totalQuantity, double totalRevenue) {
        this.productName = productName;
        this.unit = unit;
        this.totalQuantity = totalQuantity;
        this.totalRevenue = totalRevenue;
    }

    // Getters & Setters
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setTotalQuantity(double totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public String getProductName() {
        return productName;
    }

    public double getTotalQuantity() {
        return totalQuantity;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
