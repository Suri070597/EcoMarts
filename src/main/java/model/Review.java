package model;

import java.sql.Timestamp;
import java.util.List;

public class Review {

    private int reviewID;
    private int productID;
    private int accountID;
    private int rating; // 1-5 stars
    private String comment;
    private String imageURL;
    private Timestamp createdAt;
    private String accountName;
    private String userName;
    private int accountRole; // 0: customer, 1: admin, 2: staff, ...
    private List<Review> replies; // reply cho review gốc
    private int orderID;
    private Integer parentReviewID; // ID của review cha (null nếu là review gốc)
    private String productName;
    private String status;
    private boolean isRead; // Thông báo đã đọc hay chưa

    // For joining with other tables
    private Product product;
    private Account account;

    public Review() {
    }

    public Review(int reviewID, int productID, int accountID, int rating, String comment, String imageURL,
            Timestamp createdAt) {
        this.reviewID = reviewID;
        this.productID = productID;
        this.accountID = accountID;
        this.rating = rating;
        this.comment = comment;
        this.imageURL = imageURL;
        this.createdAt = createdAt;
    }

    public int getReviewID() {
        return reviewID;
    }

    public void setReviewID(int reviewID) {
        this.reviewID = reviewID;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
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

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public int getAccountRole() {
        return accountRole;
    }

    public void setAccountRole(int accountRole) {
        this.accountRole = accountRole;
    }

    public List<Review> getReplies() {
        return replies;
    }

    public void setReplies(List<Review> replies) {
        this.replies = replies;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public Integer getParentReviewID() {
        return parentReviewID;
    }

    public void setParentReviewID(Integer parentReviewID) {
        this.parentReviewID = parentReviewID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isIsRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    @Override
    public String toString() {
        return "Review{"
                + "reviewID=" + reviewID
                + ", productID=" + productID
                + ", accountID=" + accountID
                + ", rating=" + rating
                + ", comment='" + comment + '\''
                + ", imageURL='" + imageURL + '\''
                + ", createdAt=" + createdAt
                + '}';
    }
}
