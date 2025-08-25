package model;

import java.sql.Timestamp;

public class CartItem {
    private int cartItemID;
    private int accountID;
    private int productID;
    private double quantity;
    private Timestamp addedAt;
    private String status; // Active, SavedForLater, Removed

    // For joining with Product table
    private Product product;

    // Selected packaging info
    private String packageType; // 'UNIT' | 'PACK' | 'BOX' | 'KG'
    private Integer packSize;   // only for PACK
    private String displayUnitName; // e.g., "lon", "lốc (8 lon)", "thùng (24 lon)"
    private Double unitPrice;   // price per selected package (what user pays)
    // Available quantity in Inventory corresponding to selected packageType/packSize
    private Integer availableQuantity;

    public CartItem() {
    }

    public CartItem(int cartItemID, int accountID, int productID, double quantity, Timestamp addedAt, String status) {
        this.cartItemID = cartItemID;
        this.accountID = accountID;
        this.productID = productID;
        this.quantity = quantity;
        this.addedAt = addedAt;
        this.status = status;
    }

    public int getCartItemID() {
        return cartItemID;
    }

    public void setCartItemID(int cartItemID) {
        this.cartItemID = cartItemID;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public Timestamp getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Timestamp addedAt) {
        this.addedAt = addedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public Integer getPackSize() {
        return packSize;
    }

    public void setPackSize(Integer packSize) {
        this.packSize = packSize;
    }

    public String getDisplayUnitName() {
        return displayUnitName;
    }

    public void setDisplayUnitName(String displayUnitName) {
        this.displayUnitName = displayUnitName;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "cartItemID=" + cartItemID +
                ", accountID=" + accountID +
                ", productID=" + productID +
                ", quantity=" + quantity +
                ", addedAt=" + addedAt +
                ", status='" + status + '\'' +
                ", packageType='" + packageType + '\'' +
                ", packSize=" + packSize +
                ", displayUnitName='" + displayUnitName + '\'' +
                ", unitPrice=" + unitPrice +
                '}';
    }
}