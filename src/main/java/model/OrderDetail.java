package model;

public class OrderDetail {

    private int orderDetailID;
    private int orderID;
    private int productID;
    private double quantity;  // Đổi thành double để hỗ trợ số lượng thập phân
    private double unitPrice;
    private double subTotal;
    private String productName;
    private Product product;
    private String orderStatus;
    private String unit;  // Thêm trường đơn vị

    public OrderDetail() {
    }

    public OrderDetail(int orderDetailID, int orderID, int productID, double quantity, double unitPrice) {
        this.orderDetailID = orderDetailID;
        this.orderID = orderID;
        this.productID = productID;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subTotal = quantity * unitPrice;
    }

    public int getOrderDetailID() {
        return orderDetailID;
    }

    public void setOrderDetailID(int orderDetailID) {
        this.orderDetailID = orderDetailID;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
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
        this.subTotal = this.quantity * this.unitPrice;
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        this.subTotal = this.quantity * this.unitPrice;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderStatus() {
        return orderStatus;
    }
public OrderDetail(int productID, double quantity) {
    this.productID = productID;
    this.quantity = quantity;
}

    @Override
    public String toString() {
        return "OrderDetail{"
                + "orderDetailID=" + orderDetailID
                + ", orderID=" + orderID
                + ", productID=" + productID
                + ", quantity=" + quantity
                + ", unitPrice=" + unitPrice
                + ", subTotal=" + subTotal
                + '}';
    }
}
