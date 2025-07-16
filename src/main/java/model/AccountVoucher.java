package model;

import java.sql.Timestamp;

public class AccountVoucher {

    private int accountVoucherID;
    private int accountID;
    private int voucherID;
    private Timestamp dateAssigned;

    // Constructors
    public AccountVoucher() {
    }

    public AccountVoucher(int accountVoucherID, int accountID, int voucherID, Timestamp dateAssigned) {
        this.accountVoucherID = accountVoucherID;
        this.accountID = accountID;
        this.voucherID = voucherID;
        this.dateAssigned = dateAssigned;
    }

    // Getters and Setters
    public int getAccountVoucherID() {
        return accountVoucherID;
    }

    public void setAccountVoucherID(int accountVoucherID) {
        this.accountVoucherID = accountVoucherID;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public int getVoucherID() {
        return voucherID;
    }

    public void setVoucherID(int voucherID) {
        this.voucherID = voucherID;
    }

    public Timestamp getDateAssigned() {
        return dateAssigned;
    }

    public void setDateAssigned(Timestamp dateAssigned) {
        this.dateAssigned = dateAssigned;
    }
}
