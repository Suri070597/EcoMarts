package model;

import java.sql.Timestamp;

public class Review {
    private int reviewID;
    private int productID;
    private int accountID;
    private int rating; // 1-5 stars
    private String comment;
    private String imageURL;
    private Timestamp createdAt;

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

    @Override
    public String toString() {
        return "Review{" +
                "reviewID=" + reviewID +
                ", productID=" + productID +
                ", accountID=" + accountID +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}