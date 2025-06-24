package model;

/**
 *
 * @author MSI Gaming
 */
public class AccountManager {
    private int accountID;
    private String username;
    private String password;
    private String email;
    private String phone;
    private int role;
    private String status;

    // Constructor, getters, setters
    public AccountManager(int accountID, String username, String password, String email, String phone, int role, String status) {
        this.accountID = accountID;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.status = status;
    }

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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Account{" + "accountID=" + accountID + ", username=" + username + ", password=" + password + ", email=" + email + ", phone=" + phone + ", role=" + role + ", status=" + status + '}';
    }
}
