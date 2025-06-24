package model;

/**
 * Staff model class that matches the database schema
 */
public class Staff {
    private int staffID;
    private int accountID;
    private String fullName;
    private String email;
    private String phone;
    private String gender;
    private String address;
    private String status;
    
    // For joining with Account table
    private Account account;

    // Default constructor
    public Staff() {
    }

    // Full constructor
    public Staff(int staffID, int accountID, String fullName, String email, String phone, 
                 String gender, String address, String status) {
        this.staffID = staffID;
        this.accountID = accountID;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.address = address;
        this.status = status;
    }

    // Getters and Setters
    public int getStaffID() {
        return staffID;
    }

    public void setStaffID(int staffID) {
        this.staffID = staffID;
    }

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    @Override
    public String toString() {
        return "Staff{" + "staffID=" + staffID + ", accountID=" + accountID + 
               ", fullName=" + fullName + ", email=" + email + ", phone=" + phone + 
               ", gender=" + gender + ", status=" + status + '}';
    }
} 