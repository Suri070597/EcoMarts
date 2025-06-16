package model;

import java.util.Date;

/**
 * Account model class that matches the database schema
 */
public class Account {
    private int accountID;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private String phone;
    private String address;
    private String gender;
    private int role;
    private String position;
    private String status;
    private String tokenValue;
    private String tokenStatus;
    private Date tokenCreatedAt;
    private Date tokenExpiresAt;

    // Default constructor
    public Account() {
    }

    // Full constructor
    public Account(int accountID, String username, String password, String email, String fullName, String phone,
            String address, String gender, int role, String position, String status,
            String tokenValue, String tokenStatus, Date tokenCreatedAt, Date tokenExpiresAt) {
        this.accountID = accountID;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.gender = gender;
        this.role = role;
        this.position = position;
        this.status = status;
        this.tokenValue = tokenValue;
        this.tokenStatus = tokenStatus;
        this.tokenCreatedAt = tokenCreatedAt;
        this.tokenExpiresAt = tokenExpiresAt;
    }

    // Basic constructor (without token fields)
    public Account(int accountID, String username, String password, String email, String fullName, String phone,
            String address, String gender, int role, String position, String status) {
        this.accountID = accountID;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.phone = phone;
        this.address = address;
        this.gender = gender;
        this.role = role;
        this.position = position;
        this.status = status;
    }

    // Getters and Setters
    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public String getTokenStatus() {
        return tokenStatus;
    }

    public void setTokenStatus(String tokenStatus) {
        this.tokenStatus = tokenStatus;
    }

    public Date getTokenCreatedAt() {
        return tokenCreatedAt;
    }

    public void setTokenCreatedAt(Date tokenCreatedAt) {
        this.tokenCreatedAt = tokenCreatedAt;
    }

    public Date getTokenExpiresAt() {
        return tokenExpiresAt;
    }

    public void setTokenExpiresAt(Date tokenExpiresAt) {
        this.tokenExpiresAt = tokenExpiresAt;
    }

    @Override
    public String toString() {
        return "Account{" + "accountID=" + accountID + ", username=" + username + ", email=" + email +
                ", fullName=" + fullName + ", phone=" + phone + ", role=" + role + ", status=" + status + '}';
    }
}