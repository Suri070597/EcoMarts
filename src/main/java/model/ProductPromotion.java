package model;

public class ProductPromotion {
    private int productPromotionID;
    private int productID;
    private int promotionID;

    // For joining with other tables
    private Product product;
    private Promotion promotion;

    public ProductPromotion() {
    }

    public ProductPromotion(int productPromotionID, int productID, int promotionID) {
        this.productPromotionID = productPromotionID;
        this.productID = productID;
        this.promotionID = promotionID;
    }

    public int getProductPromotionID() {
        return productPromotionID;
    }

    public void setProductPromotionID(int productPromotionID) {
        this.productPromotionID = productPromotionID;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public int getPromotionID() {
        return promotionID;
    }

    public void setPromotionID(int promotionID) {
        this.promotionID = promotionID;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Promotion getPromotion() {
        return promotion;
    }

    public void setPromotion(Promotion promotion) {
        this.promotion = promotion;
    }

    @Override
    public String toString() {
        return "ProductPromotion{" +
                "productPromotionID=" + productPromotionID +
                ", productID=" + productID +
                ", promotionID=" + promotionID +
                '}';
    }
}