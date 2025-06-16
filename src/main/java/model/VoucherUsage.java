package model;

import java.sql.Timestamp;

public class VoucherUsage {
    private int voucherUsageID;
    private int voucherID;
    private int accountID;
    private int orderID;
    private Timestamp usedDate;
    private double discountAmount;

    // For joining with other tables
    private Voucher voucher;
    private Account account;
    private Order order;

    public VoucherUsage() {
    }

    public VoucherUsage(int voucherUsageID, int voucherID, int accountID, int orderID, Timestamp usedDate,
            double discountAmount) {
        this.voucherUsageID = voucherUsageID;
        this.voucherID = voucherID;
        this.accountID = accountID;
        this.orderID = orderID;
        this.usedDate = usedDate;
        this.discountAmount = discountAmount;
    }

    public int getVoucherUsageID() {
        return voucherUsageID;
    }

    public void setVoucherUsageID(int voucherUsageID) {
        this.voucherUsageID = voucherUsageID;
    }

    public int getVoucherID() {
        return voucherID;
    }

    public void setVoucherID(int voucherID) {
        this.voucherID = voucherID;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public Timestamp getUsedDate() {
        return usedDate;
    }

    public void setUsedDate(Timestamp usedDate) {
        this.usedDate = usedDate;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Voucher getVoucher() {
        return voucher;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "VoucherUsage{" +
                "voucherUsageID=" + voucherUsageID +
                ", voucherID=" + voucherID +
                ", accountID=" + accountID +
                ", orderID=" + orderID +
                ", usedDate=" + usedDate +
                ", discountAmount=" + discountAmount +
                '}';
    }
}