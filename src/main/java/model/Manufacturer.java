package model;

public class Manufacturer {

    private int manufacturerID;
    private String brandName;
    private String companyName;
    private String address;
    private String email;
    private String phone;
    private int status; // Đang hợp tác, Ngừng hợp tác, etc.

    public Manufacturer() {
    }

    public Manufacturer(int manufacturerID, String companyName) {
        this.manufacturerID = manufacturerID;
        this.companyName = companyName;
    }

    public Manufacturer(int manufacturerID, String brandName, String companyName, String address, String email, String phone,
            int status) {
        this.manufacturerID = manufacturerID;
        this.brandName = brandName;
        this.companyName = companyName;
        this.address = address;
        this.email = email;
        this.phone = phone;
        this.status = status;
    }

    public int getManufacturerID() {
        return manufacturerID;
    }

    public void setManufacturerID(int manufacturerID) {
        this.manufacturerID = manufacturerID;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getManufacturerId() {
        return manufacturerID;
    }
    
    @Override
    public String toString() {
        return "Manufacturer{"
                + "manufacturerID=" + manufacturerID
                + ", brandName='" + brandName + '\''
                + ", companyName='" + companyName + '\''
                + ", address='" + address + '\''
                + ", email='" + email + '\''
                + ", phone='" + phone + '\''
                + ", status='" + status + '\''
                + '}';
    }

    
}
