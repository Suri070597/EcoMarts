// Test file để kiểm tra logic tính giá
public class TestOrderPrice {
    
    public static void main(String[] args) {
        // Test các trường hợp PackageType khác nhau
        testPriceCalculation("UNIT", 12.0, null, 12.0, 24.0, 36.0);
        testPriceCalculation("BOX", 12.0, null, 12.0, 24.0, 36.0);
        testPriceCalculation("PACK", 12.0, 6, 12.0, 24.0, 36.0);
        testPriceCalculation("KG", 12.0, null, 12.0, 24.0, 36.0);
    }
    
    private static void testPriceCalculation(String packageType, double priceUnit, Integer packSize, 
                                           double priceBox, double pricePack, double expectedPrice) {
        System.out.println("Testing PackageType: " + packageType);
        System.out.println("PriceUnit: " + priceUnit + ", PackSize: " + packSize);
        System.out.println("PriceBox: " + priceBox + ", PricePack: " + pricePack);
        
        double calculatedPrice = 0.0;
        
        if ("KG".equalsIgnoreCase(packageType) || "UNIT".equalsIgnoreCase(packageType)) {
            calculatedPrice = priceUnit;
        } else if ("BOX".equalsIgnoreCase(packageType)) {
            calculatedPrice = priceBox;
        } else if ("PACK".equalsIgnoreCase(packageType)) {
            if (pricePack > 0) {
                calculatedPrice = pricePack;
            } else if (priceUnit > 0 && packSize != null) {
                calculatedPrice = priceUnit * packSize;
            } else {
                calculatedPrice = 0.0;
            }
        }
        
        System.out.println("Expected: " + expectedPrice + ", Calculated: " + calculatedPrice);
        System.out.println("Test " + (calculatedPrice == expectedPrice ? "PASSED" : "FAILED"));
        System.out.println("---");
    }
}
