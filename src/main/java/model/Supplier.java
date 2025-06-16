package model;

public class Supplier {
    private int supplierID;
    private String brandName;
    private String companyName;
    private String address;
    private String email;
    private String phone;
    private String status; // Đang hợp tác, Ngừng hợp tác, etc.

    public Supplier() {
    }

    public Supplier(int supplierID, String brandName, String companyName, String address, String email, String phone,
            String status) {
        this.supplierID = supplierID;
        this.brandName = brandName;
        this.companyName = companyName;
        this.address = address;
        this.email = email;
        this.phone = phone;
        this.status = status;
    }

    public int getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(int supplierID) {
        this.supplierID = supplierID;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Supplier{" +
                "supplierID=" + supplierID +
                ", brandName='" + brandName + '\'' +
                ", companyName='" + companyName + '\'' +
                ", address='" + address + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}