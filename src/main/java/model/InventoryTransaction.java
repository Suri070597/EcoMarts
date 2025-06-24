package model;

import java.sql.Timestamp;

public class InventoryTransaction {

    private int transactionID;
    private int productID;
    private int quantity; // Positive for stock in, negative for stock out
    private String transactionType; // 'Purchase', 'Sale', 'Return', 'Adjustment'
    private String reference; // Order ID or Purchase ID
    private String notes;
    private Timestamp transactionDate;
    private Integer accountID; // Staff who made the transaction (can be null)

    // For joining with other tables
    private Product product;
    private Account account;

    public InventoryTransaction() {
    }

    public InventoryTransaction(int productID, int quantity, Timestamp transactionDate) {
        this.productID = productID;
        this.quantity = quantity;
        this.transactionDate = transactionDate;
    }

    public InventoryTransaction(int transactionID, int productID, int quantity, String transactionType,
            String reference, String notes, Timestamp transactionDate, Integer accountID) {
        this.transactionID = transactionID;
        this.productID = productID;
        this.quantity = quantity;
        this.transactionType = transactionType;
        this.reference = reference;
        this.notes = notes;
        this.transactionDate = transactionDate;
        this.accountID = accountID;
    }

    public int getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(int transactionID) {
        this.transactionID = transactionID;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Timestamp getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Timestamp transactionDate) {
        this.transactionDate = transactionDate;
    }

    public Integer getAccountID() {
        return accountID;
    }

    public void setAccountID(Integer accountID) {
        this.accountID = accountID;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "InventoryTransaction{"
                + "transactionID=" + transactionID
                + ", productID=" + productID
                + ", quantity=" + quantity
                + ", transactionType='" + transactionType + '\''
                + ", reference='" + reference + '\''
                + ", notes='" + notes + '\''
                + ", transactionDate=" + transactionDate
                + ", accountID=" + accountID
                + '}';
    }
}
