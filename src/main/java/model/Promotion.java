package model;

import java.sql.Timestamp;

public class Promotion {

    private int promotionID;
    private String promotionName;
    private String description;
    private double discountPercent;
    private Timestamp startDate;
    private Timestamp endDate;
    private boolean active;
    private int promoType;    // 0 = Flash Sale, 1 = Seasonal
    private int applyScope;   // 0 = Tất cả, 1 = Theo danh mục

    private Category category; // liên kết trực tiếp đến Category

    // Constants
    public static final int SCOPE_ALL = 0;
    public static final int SCOPE_CATEGORY = 1;
    public static final int TYPE_FLASH = 0;
    public static final int TYPE_SEASONAL = 1;

    // ===== Constructors =====
    public Promotion() {
    }

    public Promotion(int promotionID, String promotionName, String description,
            double discountPercent, Timestamp startDate, Timestamp endDate,
            boolean active, int promoType, int applyScope, Category category) {
        this.promotionID = promotionID;
        this.promotionName = promotionName;
        this.description = description;
        this.discountPercent = discountPercent;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = active;
        this.promoType = promoType;
        this.applyScope = applyScope;
        this.category = category;
    }

    public Promotion(String promotionName, String description,
            double discountPercent, Timestamp startDate, Timestamp endDate,
            boolean active, int promoType, int applyScope, Category category) {
        this(0, promotionName, description, discountPercent, startDate, endDate,
                active, promoType, applyScope, category);
    }

    // ===== Getters & Setters =====
    public int getPromotionID() {
        return promotionID;
    }

    public void setPromotionID(int promotionID) {
        this.promotionID = promotionID;
    }

    public String getPromotionName() {
        return promotionName;
    }

    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
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
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getPromoType() {
        return promoType;
    }

    public void setPromoType(int promoType) {
        this.promoType = promoType;
    }

    public int getApplyScope() {
        return applyScope;
    }

    public void setApplyScope(int applyScope) {
        this.applyScope = applyScope;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    // ===== toString =====
    @Override
    public String toString() {
        return "Promotion{"
                + "promotionID=" + promotionID
                + ", promotionName='" + promotionName + '\''
                + ", description='" + description + '\''
                + ", discountPercent=" + discountPercent
                + ", startDate=" + startDate
                + ", endDate=" + endDate
                + ", active=" + active
                + ", promoType=" + promoType
                + ", applyScope=" + applyScope
                + ", category=" + (category != null ? category.getCategoryName() : "null")
                + '}';
    }
}
