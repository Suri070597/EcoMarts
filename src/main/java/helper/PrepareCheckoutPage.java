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
 * @author thach ngoc thoi
 */
public class PrepareCheckoutPage {

    public void prepareCheckoutPage(HttpServletRequest request, Account account, CartItem buyNowItem, Product product) {
        try {
            buyNowItem.setProduct(product);

            CategoryDAO categoryDAO = new CategoryDAO();
            List<Category> categories = categoryDAO.getAllCategoriesWithChildren();
            request.setAttribute("categories", categories);

            Double basePrice;
            String packageType = buyNowItem.getPackageType();
            if (packageType == null) {
                packageType = "UNIT";
            }

            switch (packageType.toUpperCase()) {
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
                        basePrice = product.getPriceUnit();
                    }
                    break;

                case "BOX":
                    basePrice = product.getPrice();
                    break;

                case "KG":
                case "UNIT":
                default:
                    basePrice = product.getPriceUnit();
                    break;
            }

            if (basePrice == null) {
                basePrice = 0.0;
            }

            // Check promotion and tính tiền
            double finalPrice = basePrice;

            PromotionDAO promotionDAO = new PromotionDAO();
            Promotion activePromotion = promotionDAO.getValidPromotionForProduct(product.getProductID());

            if (activePromotion != null) {
                double discountPercent = activePromotion.getDiscountPercent();
                finalPrice = basePrice * (1 - discountPercent / 100);
                request.setAttribute("appliedPromotion", activePromotion);
                request.setAttribute("originalPrice", basePrice);
            }

            // Set final price 
            product.setPrice(finalPrice);
            double itemTotal = finalPrice * buyNowItem.getQuantity();

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

            // Update stock 
            product.setStockQuantity(
                    ("PACK".equalsIgnoreCase(buyNowItem.getPackageType()) && buyNowItem.getPackSize() != null)
                    ? new ProductDAO().getPackQuantity(buyNowItem.getProductID(), buyNowItem.getPackSize())
                    : new ProductDAO().getQuantityByPackageType(buyNowItem.getProductID(), buyNowItem.getPackageType() != null ? buyNowItem.getPackageType() : "UNIT")
            );

            VoucherDAO voucherDAO = new VoucherDAO();
            List<Voucher> availableVouchers = voucherDAO.getVouchersByAccountId(account.getAccountID());

            List<Voucher> validVouchers = new ArrayList<>();
            Timestamp now = new Timestamp(System.currentTimeMillis());

            for (Voucher voucher : availableVouchers) {
                if (voucher.isActive()
                        && now.after(voucher.getStartDate())
                        && now.before(voucher.getEndDate())
                        && itemTotal >= voucher.getMinOrderValue()
                        && voucher.getUsageCount() < voucher.getMaxUsage()) {

                    if (voucher.getCategoryID() == null
                            || voucher.getCategoryID() == product.getCategory().getCategoryID()
                            || voucher.getCategoryID() == product.getCategory().getParentID()) {
                        validVouchers.add(voucher);
                    }
                }
            }

            request.setAttribute("buyNowItem", buyNowItem);
            request.setAttribute("itemTotal", itemTotal);
            request.setAttribute("totalAmount", itemTotal); 
            request.setAttribute("validVouchers", validVouchers);

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
