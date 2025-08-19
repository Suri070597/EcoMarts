package model;

import java.sql.Timestamp;

public class Voucher {
    private int voucherID;
    private String voucherCode;
    private String description;
    private double discountAmount;
    private double minOrderValue;
    private int maxUsage;
    private int usageCount;
    private Timestamp startDate;
    private Timestamp endDate;
    private boolean isActive;
    private Integer categoryID; // Can be null if applicable to all categories
    private Integer claimLimit; // Max number of claims that can be taken by users (null => unlimited)

    // For joining with other tables
    private Category category;

    public Voucher() {
    }

    public Voucher(int voucherID, String voucherCode, String description, double discountAmount,
            double minOrderValue, int maxUsage, int usageCount, Timestamp startDate,
            Timestamp endDate, boolean isActive, Integer categoryID) {
        this.voucherID = voucherID;
        this.voucherCode = voucherCode;
        this.description = description;
        this.discountAmount = discountAmount;
        this.minOrderValue = minOrderValue;
        this.maxUsage = maxUsage;
        this.usageCount = usageCount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = isActive;
        this.categoryID = categoryID;
    }

    public int getVoucherID() {
        return voucherID;
    }

    public void setVoucherID(int voucherID) {
        this.voucherID = voucherID;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getMinOrderValue() {
        return minOrderValue;
    }

    public void setMinOrderValue(double minOrderValue) {
        this.minOrderValue = minOrderValue;
    }

    public int getMaxUsage() {
        return maxUsage;
    }

    public void setMaxUsage(int maxUsage) {
        this.maxUsage = maxUsage;
    }

    public int getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(int usageCount) {
        this.usageCount = usageCount;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Integer getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(Integer categoryID) {
        this.categoryID = categoryID;
    }

    public Integer getClaimLimit() {
        return claimLimit;
    }

    public void setClaimLimit(Integer claimLimit) {
        this.claimLimit = claimLimit;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Voucher{" +
                "voucherID=" + voucherID +
                ", voucherCode='" + voucherCode + '\'' +
                ", description='" + description + '\'' +
                ", discountAmount=" + discountAmount +
                ", minOrderValue=" + minOrderValue +
                ", maxUsage=" + maxUsage +
                ", usageCount=" + usageCount +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", isActive=" + isActive +
                ", categoryID=" + categoryID +
                ", claimLimit=" + claimLimit +
                '}';
    }
}