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

    @Override
    public String toString() {
        return "CartItem{" +
                "cartItemID=" + cartItemID +
                ", accountID=" + accountID +
                ", productID=" + productID +
                ", quantity=" + quantity +
                ", addedAt=" + addedAt +
                ", status='" + status + '\'' +
                '}';
    }
}