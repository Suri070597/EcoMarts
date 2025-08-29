/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package helper;

import dao.AccountDAO;
import dao.CategoryDAO;
import dao.ProductDAO;
import dao.PromotionDAO;
import dao.VoucherDAO;
import jakarta.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import model.Account;
import model.CartItem;
import model.Category;
import model.Product;
import model.Promotion;
import model.Voucher;

/**
 *
 * @author thach
 */
public class PrepareCheckoutPage {
        public void prepareCheckoutPage(HttpServletRequest request, Account account, CartItem buyNowItem, Product product) {
        try {
            // Set product for the buy now item
            buyNowItem.setProduct(product);

            // Get categories from database 
            CategoryDAO categoryDAO = new CategoryDAO();
            List<Category> categories = categoryDAO.getAllCategoriesWithChildren();
            request.setAttribute("categories", categories);

            // Get base price based on package type
            Double basePrice;
            String packageType = buyNowItem.getPackageType();
            if (packageType == null) packageType = "UNIT";
            
            switch(packageType.toUpperCase()) {
                case "PACK":
                    if (buyNowItem.getPackSize() != null) {
                        Double pricePack = product.getPricePack();
                        if (pricePack != null) {
                            basePrice = pricePack;
                        } else {
                            Double unitPrice = product.getPriceUnit();
                            basePrice = unitPrice != null ? unitPrice * buyNowItem.getPackSize() : 0.0;
                        }
                    } else {
                        basePrice = product.getPriceUnit(); // Fallback to unit price if pack size not specified
                    }
                    break;
                    
                case "BOX":
                    basePrice = product.getPrice(); // price field holds priceBox value
                    break;
                    
                case "KG":
                case "UNIT":
                default:
                    basePrice = product.getPriceUnit();
                    break;
            }
            
            if (basePrice == null) basePrice = 0.0;
            
            // Check for active promotion and calculate final price
            double finalPrice = basePrice;
            
            PromotionDAO promotionDAO = new PromotionDAO();
            Promotion activePromotion = promotionDAO.getValidPromotionForProduct(product.getProductID());
            
            if (activePromotion != null) {
                double discountPercent = activePromotion.getDiscountPercent();
                finalPrice = basePrice * (1 - discountPercent / 100);
                request.setAttribute("appliedPromotion", activePromotion); 
                request.setAttribute("originalPrice", basePrice);
            }

            // Set final price and calculate total
            product.setPrice(finalPrice);
            double itemTotal = finalPrice * buyNowItem.getQuantity();

            // Set effective unit and stock for display
            String unitLabel;
            if ("PACK".equalsIgnoreCase(buyNowItem.getPackageType()) && buyNowItem.getPackSize() != null) {
                String itemUnitName = product.getItemUnitName();
                unitLabel = "Lốc" + (buyNowItem.getPackSize() != null ? (" " + buyNowItem.getPackSize() + " " + (itemUnitName != null ? itemUnitName : "đơn vị")) : "");
            } else if ("BOX".equalsIgnoreCase(String.valueOf(buyNowItem.getPackageType()))) {
                String boxUnitName = product.getBoxUnitName();
                unitLabel = boxUnitName != null ? boxUnitName : "thùng";
            } else if ("KG".equalsIgnoreCase(String.valueOf(buyNowItem.getPackageType()))) {
                unitLabel = "kg";
            } else {
                String itemUnitName = product.getItemUnitName();
                unitLabel = itemUnitName != null ? itemUnitName : "đơn vị";
            }
            product.setUnit(unitLabel);

            // Update stock quantity
            product.setStockQuantity(
                ("PACK".equalsIgnoreCase(buyNowItem.getPackageType()) && buyNowItem.getPackSize() != null)
                    ? new ProductDAO().getPackQuantity(buyNowItem.getProductID(), buyNowItem.getPackSize())
                    : new ProductDAO().getQuantityByPackageType(buyNowItem.getProductID(), buyNowItem.getPackageType() != null ? buyNowItem.getPackageType() : "UNIT")
            );

            // Get available vouchers for the user
            VoucherDAO voucherDAO = new VoucherDAO();
            List<Voucher> availableVouchers = voucherDAO.getVouchersByAccountId(account.getAccountID());

            // Filter valid vouchers (active and not expired)
            List<Voucher> validVouchers = new ArrayList<>();
            Timestamp now = new Timestamp(System.currentTimeMillis());

            for (Voucher voucher : availableVouchers) {
                if (voucher.isActive()
                        && now.after(voucher.getStartDate())
                        && now.before(voucher.getEndDate())
                        && itemTotal >= voucher.getMinOrderValue()
                        && voucher.getUsageCount() < voucher.getMaxUsage()) {

                    // Check if voucher is applicable to this product's category
                    if (voucher.getCategoryID() == null
                            || voucher.getCategoryID() == product.getCategory().getCategoryID()
                            || voucher.getCategoryID() == product.getCategory().getParentID()) {
                        validVouchers.add(voucher);
                    }
                }
            }

            // Set checkout page attributes
            request.setAttribute("buyNowItem", buyNowItem);
            request.setAttribute("itemTotal", itemTotal);
            request.setAttribute("totalAmount", itemTotal); // Initial total before voucher
            request.setAttribute("validVouchers", validVouchers);



            // Get user info if not already set
            if (request.getAttribute("userInfo") == null) {
                AccountDAO accountDAO = new AccountDAO();
                Account userInfo = accountDAO.getUserDetail(account.getAccountID());
                request.setAttribute("userInfo", userInfo);
            }
        } catch (Exception e) {
            request.setAttribute("error", "Lỗi khi chuẩn bị trang thanh toán: " + e.getMessage());
        }
    }

}
