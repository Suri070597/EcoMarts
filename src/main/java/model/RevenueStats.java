package model;

public class RevenueStats {

    private String productName;
    private int totalQuantity;
    private double totalRevenue;
    private int month;
    // Quantities split by package type
    private double boxQuantity;   // PackageType = 'BOX'
    private double packQuantity;  // PackageType = 'PACK'
    private double unitQuantity;  // PackageType = 'UNIT'
    private double kgQuantity;    // PackageType = 'KG'
    private String itemUnitName;  // Display name for UNIT from Product.ItemUnitName
    // Revenue split by package type
    private double boxRevenue;
    private double packRevenue;
    private double unitRevenue;
    private double kgRevenue;

    public RevenueStats() {
    }

    public RevenueStats(String productName, int totalQuantity, double totalRevenue) {
        this.productName = productName;
        this.totalQuantity = totalQuantity;
        this.totalRevenue = totalRevenue;
    }

    public RevenueStats(String productName, double boxQuantity, double packQuantity, double unitQuantity, double kgQuantity, double totalRevenue) {
        this.productName = productName;
        this.boxQuantity = boxQuantity;
        this.packQuantity = packQuantity;
        this.unitQuantity = unitQuantity;
        this.kgQuantity = kgQuantity;
        this.totalQuantity = (int) Math.round((boxQuantity + packQuantity + unitQuantity + kgQuantity));
        this.totalRevenue = totalRevenue;
    }

    // Getters & Setters
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setTotalQuantity(int totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public String getProductName() {
        return productName;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public String getItemUnitName() {
        return itemUnitName;
    }

    public void setItemUnitName(String itemUnitName) {
        this.itemUnitName = itemUnitName;
    }

    public double getBoxQuantity() {
        return boxQuantity;
    }

    public void setBoxQuantity(double boxQuantity) {
        this.boxQuantity = boxQuantity;
    }

    public double getPackQuantity() {
        return packQuantity;
    }

    public void setPackQuantity(double packQuantity) {
        this.packQuantity = packQuantity;
    }

    public double getUnitQuantity() {
        return unitQuantity;
    }

    public void setUnitQuantity(double unitQuantity) {
        this.unitQuantity = unitQuantity;
    }

    public double getKgQuantity() {
        return kgQuantity;
    }

    public void setKgQuantity(double kgQuantity) {
        this.kgQuantity = kgQuantity;
    }

    public double getBoxRevenue() {
        return boxRevenue;
    }

    public void setBoxRevenue(double boxRevenue) {
        this.boxRevenue = boxRevenue;
    }

    public double getPackRevenue() {
        return packRevenue;
    }

    public void setPackRevenue(double packRevenue) {
        this.packRevenue = packRevenue;
    }

    public double getUnitRevenue() {
        return unitRevenue;
    }

    public void setUnitRevenue(double unitRevenue) {
        this.unitRevenue = unitRevenue;
    }

    public double getKgRevenue() {
        return kgRevenue;
    }

    public void setKgRevenue(double kgRevenue) {
        this.kgRevenue = kgRevenue;
    }

    public String getFormattedQuantity() {
        StringBuilder sb = new StringBuilder();
        if (boxQuantity > 0) {
            sb.append("Thùng ").append(trimTrailingZeros(boxQuantity));
        }
        if (packQuantity > 0) {
            if (sb.length() > 0) sb.append(", ");
            sb.append("Lốc ").append(trimTrailingZeros(packQuantity));
        }
        if (kgQuantity > 0) {
            if (sb.length() > 0) sb.append(", ");
            sb.append("Kg ").append(trimTrailingZeros(kgQuantity));
        }
        if (unitQuantity > 0) {
            if (sb.length() > 0) sb.append(", ");
            String unitLabel = (itemUnitName != null && !itemUnitName.isEmpty()) ? itemUnitName : "Cái";
            sb.append(unitLabel).append(" ").append(trimTrailingZeros(unitQuantity));
        }
        if (sb.length() == 0) {
            return "0";
        }
        return sb.toString();
    }

    private String trimTrailingZeros(double value) {
        if (value == (long) value) {
            return String.format("%d", (long) value);
        } else {
            return String.valueOf(value);
        }
    }
}
