package controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.AccountDAO;
import dao.CartItemDAO;
import dao.CategoryDAO;
import dao.OrderDAO;
import dao.ProductDAO;
import dao.PromotionDAO;
import dao.VoucherDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Account;
import model.CartItem;
import model.Category;
import model.Order;
import model.Product;
import model.Promotion;
import model.Voucher;
import util.VNPayUtil;

/**
 * Servlet for handling "Buy Now" functionality
 */
@WebServlet(name = "BuyNowServlet", urlPatterns = {"/buy-now", "/buy-now/vnpay"})
public class BuyNowServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * Handles GET requests for Buy Now checkout page
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Kiểm tra xem đây có phải là callback từ VNPay không
        String requestURI = request.getRequestURI();
        if (requestURI.contains("/buy-now/vnpay")) {
            // Xử lý callback từ VNPay
            processVNPayCallback(request, response);
            return;
        }

        // Get account from session
        HttpSession session = request.getSession();
        Account account = (Account) session.getAttribute("account");

        // Redirect to login if not logged in
        if (account == null) {
            session.setAttribute("redirectAfterLogin", "buy-now");
            response.sendRedirect("login");
            return;
        }

        // Check if user is a customer (role = 0)
        if (account.getRole() != 0) {
            // Not a customer, redirect based on role
            if (account.getRole() == 1) {
                response.sendRedirect("admin");
            } else if (account.getRole() == 2) {
                response.sendRedirect("staff");
            } else {
                response.sendRedirect("home");
            }
            return;
        }

        // Get categories from database
        CategoryDAO categoryDAO = new CategoryDAO();
        List<Category> categories = categoryDAO.getAllCategoriesWithChildren();
        request.setAttribute("categories", categories);

        // Check if buying from cart
        boolean isFromCart = session.getAttribute("checkoutFromCart") != null;

        if (isFromCart) {
            // Process cart checkout
            List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
            if (cartItems == null || cartItems.isEmpty()) {
                session.setAttribute("cartError", "Giỏ hàng của bạn đang trống. Vui lòng thử lại.");
                response.sendRedirect("cart");
                return;
            }

            // Ensure totals and helper attrs are available for JSP
            Object cartTotal = session.getAttribute("cartTotal");
            if (cartTotal != null) {
                request.setAttribute("itemTotal", cartTotal);
                request.setAttribute("totalAmount", cartTotal);
            }
            Object validVouchers = session.getAttribute("validVouchers");
            if (validVouchers != null) {
                request.setAttribute("validVouchers", validVouchers);
            }
            Object userInfo = session.getAttribute("userInfo");
            if (userInfo != null) {
                request.setAttribute("userInfo", userInfo);
            }

            // Forward to checkout page with cart items
            request.getRequestDispatcher("/WEB-INF/customer/buy-now.jsp").forward(request, response);
            return;
        }

        // Check if we have the product info in session for single product buy now
        CartItem buyNowItem = (CartItem) session.getAttribute("buyNowItem");
        if (buyNowItem == null) {
            // No product in session, redirect to home
            session.setAttribute("cartError", "Không thể tiến hành mua ngay. Vui lòng thử lại.");
            response.sendRedirect("home");
            return;
        }

        // Get product information
        ProductDAO productDAO = new ProductDAO();
        Product product = productDAO.getProductById(buyNowItem.getProductID());
        if (product == null) {
            session.setAttribute("cartError", "Không tìm thấy sản phẩm. Vui lòng thử lại.");
            response.sendRedirect("home");
            return;
        }

        // Check product availability using selected package
        double stockQuantity;
        if ("PACK".equalsIgnoreCase(buyNowItem.getPackageType()) && buyNowItem.getPackSize() != null) {
            stockQuantity = productDAO.getPackQuantity(buyNowItem.getProductID(), buyNowItem.getPackSize());
        } else {
            String effType = buyNowItem.getPackageType() != null ? buyNowItem.getPackageType() : "UNIT";
            stockQuantity = productDAO.getQuantityByPackageType(buyNowItem.getProductID(), effType);
        }
        if (stockQuantity < buyNowItem.getQuantity()) {
            session.setAttribute("cartError",
                    "Số lượng sản phẩm không đủ. Hiện chỉ còn " + stockQuantity + " sản phẩm.");
            response.sendRedirect("ProductDetail?id=" + product.getProductID());
            return;
        }

        // Set product for the buy now item
        buyNowItem.setProduct(product);

        // Derive effective unit label based on selected package and set stock
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
        product.setStockQuantity(stockQuantity);

        // Get account info for shipping
        AccountDAO accountDAO = new AccountDAO();
        Account fullAccount = accountDAO.getUserDetail(account.getAccountID());

            // Calculate total price including promotions
            prepareCheckoutPage(request, account, buyNowItem, product);        // Get available vouchers for the user
        VoucherDAO voucherDAO = new VoucherDAO();
        List<Voucher> availableVouchers = voucherDAO.getVouchersByAccountId(account.getAccountID());

            // Filter valid vouchers (active and not expired)
            List<Voucher> validVouchers = new ArrayList<>();
            Timestamp now = new Timestamp(System.currentTimeMillis());
            
            // Calculate total amount before voucher
            double itemTotal = product.getPrice() * buyNowItem.getQuantity();

            for (Voucher voucher : availableVouchers) {
                if (voucher.isActive()
                        && now.after(voucher.getStartDate())
                        && now.before(voucher.getEndDate())
                        && itemTotal >= voucher.getMinOrderValue()
                        && voucher.getUsageCount() < voucher.getMaxUsage()) {

                    // Check if voucher is applicable to this product's category
                    Integer voucherCategoryId = voucher.getCategoryID();
                    Category productCategory = product.getCategory();
                    
                    if (voucherCategoryId == null 
                            || (productCategory != null && (
                                voucherCategoryId.equals(productCategory.getCategoryID())
                                || (productCategory.getParentID() != null 
                                    && voucherCategoryId.equals(productCategory.getParentID()))
                            ))) {
                        validVouchers.add(voucher);
                    }
                }
            }

            // Calculate rounded totals for consistent display
            double roundedItemTotal = Math.round(itemTotal / 1000.0) * 1000;        // Set attributes for the checkout page
        request.setAttribute("buyNowItem", buyNowItem);
        request.setAttribute("itemTotal", roundedItemTotal);
        request.setAttribute("totalAmount", roundedItemTotal); // Initial total before voucher

        // Add some debug log to verify values
        System.out.println("Setting totalAmount attribute: " + roundedItemTotal);
        request.setAttribute("validVouchers", validVouchers);
        request.setAttribute("userInfo", fullAccount);

        // Forward to buy now checkout page
        request.getRequestDispatcher("/WEB-INF/customer/buy-now.jsp").forward(request, response);
    }

    /**
     * Handles POST requests for Buy Now functionality
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get account from session
        HttpSession session = request.getSession();
        Account account = (Account) session.getAttribute("account");

        // Check if user is logged in
        if (account == null) {
            session.setAttribute("redirectAfterLogin", "buy-now");
            response.sendRedirect("login");
            return;
        }

        // Check if user is a customer
        if (account.getRole() != 0) {
            if (account.getRole() == 1) {
                response.sendRedirect("admin");
            } else if (account.getRole() == 2) {
                response.sendRedirect("staff");
            } else {
                response.sendRedirect("home");
            }
            return;
        }

        // Get the action parameter
        String action = request.getParameter("action");

        if ("initiate".equals(action)) {
            // Handle initiate buy now process for single product
            initiateOrder(request, response, session, account);
        } else if ("processSingle".equals(action)) {
            // Handle process buy now order for single product
            processOrder(request, response, session, account);
        } else if ("initiateCart".equals(action)) {
            // Handle buy now from cart
            initiateFromCart(request, response, session, account);
        } else if ("processCart".equals(action)) {
            // Handle process order from cart
            processCartOrder(request, response, session, account);
        } else {
            // Invalid action
            session.setAttribute("cartError", "Hành động không hợp lệ");
            response.sendRedirect("home");
        }
    }

    /**
     * Initiate buy now order from product page
     */
    private void initiateOrder(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, Account account) throws IOException {
        try {
            // Get product ID and quantity from request
            int productID = Integer.parseInt(request.getParameter("productID"));
            double quantity = Double.parseDouble(request.getParameter("quantity"));
            String packageType = request.getParameter("packageType");
            String packSizeStr = request.getParameter("packSize");
            Integer packSize = null;
            if (packSizeStr != null && !packSizeStr.trim().isEmpty()) {
                try { packSize = Integer.parseInt(packSizeStr); } catch (Exception ignore) {}
            }

            // Validate quantity
            if (quantity <= 0) {
                quantity = 1;
            }

            // Check product availability per selected package
            ProductDAO productDAO = new ProductDAO();
            double stockQuantity;
            if ("PACK".equalsIgnoreCase(packageType) && packSize != null) {
                stockQuantity = productDAO.getPackQuantity(productID, packSize);
            } else {
                String effType = (packageType != null && !packageType.trim().isEmpty()) ? packageType : "UNIT";
                stockQuantity = productDAO.getQuantityByPackageType(productID, effType);
            }

            if (stockQuantity < quantity) {
                session.setAttribute("cartError",
                        "Không đủ số lượng trong kho. Hiện tại chỉ còn " + stockQuantity + " sản phẩm.");
                response.sendRedirect("ProductDetail?id=" + productID);
                return;
            }

            // Create a cart item object for the buy now process
            CartItem buyNowItem = new CartItem();
            buyNowItem.setProductID(productID);
            buyNowItem.setQuantity(quantity);
            buyNowItem.setPackageType(packageType);
            buyNowItem.setPackSize(packSize);
            buyNowItem.setAccountID(account.getAccountID());
            buyNowItem.setAddedAt(new Timestamp(new Date().getTime()));
            buyNowItem.setStatus("BuyNow");

            // Clear any existing checkout sessions to avoid conflicts
            session.removeAttribute("cartItems");
            session.removeAttribute("checkoutFromCart");

            // Store in session
            session.setAttribute("buyNowItem", buyNowItem);

            // Redirect to buy-now GET to display checkout page
            response.sendRedirect("buy-now");

        } catch (NumberFormatException e) {
            session.setAttribute("cartError", "Dữ liệu không hợp lệ");
            response.sendRedirect("home");
        } catch (Exception e) {
            session.setAttribute("cartError", "Lỗi: " + e.getMessage());
            response.sendRedirect("home");
        }
    }

    /**
     * Process buy now order after checkout confirmation
     */
    private void processOrder(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, Account account) throws IOException, ServletException {
        try {
            // Get the buy now item from session for single product checkout
            CartItem buyNowItem = (CartItem) session.getAttribute("buyNowItem");

            if (buyNowItem == null) {
                session.setAttribute("cartError", "Phiên mua hàng đã hết hạn. Vui lòng thử lại.");
                response.sendRedirect("home");
                return;
            }

            // Quantity changes from the client are ignored; use existing session quantity

            // Get product information and check stock again
            ProductDAO productDAO = new ProductDAO();
            Product product = productDAO.getProductById(buyNowItem.getProductID());

            if (product == null) {
                session.setAttribute("cartError", "Không tìm thấy sản phẩm. Vui lòng thử lại.");
                response.sendRedirect("home");
                return;
            }

            // Check stock availability again per selected package
            double stockQty;
            if ("PACK".equalsIgnoreCase(buyNowItem.getPackageType()) && buyNowItem.getPackSize() != null) {
                stockQty = productDAO.getPackQuantity(buyNowItem.getProductID(), buyNowItem.getPackSize());
            } else {
                String effType = buyNowItem.getPackageType() != null ? buyNowItem.getPackageType() : "UNIT";
                stockQty = productDAO.getQuantityByPackageType(buyNowItem.getProductID(), effType);
            }
            if (stockQty < buyNowItem.getQuantity()) {
                request.setAttribute("error", "Sản phẩm đã hết hàng hoặc không đủ số lượng yêu cầu. Hiện chỉ còn "
                        + stockQty);

                // Adjust quantity to maximum available
                buyNowItem.setQuantity(stockQty);

                // Re-populate the form with corrected quantity
                prepareCheckoutPage(request, account, buyNowItem, product);
                request.getRequestDispatcher("/WEB-INF/customer/buy-now.jsp").forward(request, response);
                return;
            }

            // Get order details from form
            String recipientName = request.getParameter("recipientName");
            String shippingAddress = request.getParameter("shippingAddress");
            String shippingPhone = request.getParameter("shippingPhone");
            String paymentMethod = request.getParameter("paymentMethod");
            String voucherCode = request.getParameter("voucherCode");
            String notes = request.getParameter("notes");

            // Validate required fields
            if (recipientName == null || recipientName.trim().isEmpty()
                    || shippingAddress == null || shippingAddress.trim().isEmpty()
                    || shippingPhone == null || shippingPhone.trim().isEmpty()
                    || paymentMethod == null || paymentMethod.trim().isEmpty()) {

                request.setAttribute("error", "Vui lòng điền đầy đủ thông tin giao hàng");

                // Re-populate the form data
                request.setAttribute("recipientName", recipientName);
                request.setAttribute("shippingAddress", shippingAddress);
                request.setAttribute("shippingPhone", shippingPhone);
                request.setAttribute("notes", notes);

                prepareCheckoutPage(request, account, buyNowItem, product);
                request.getRequestDispatcher("/WEB-INF/customer/buy-now.jsp").forward(request, response);
                return;
            }

            // Validate phone number
            if (!validatePhoneNumber(shippingPhone)) {
                request.setAttribute("error", "Số điện thoại không hợp lệ. Vui lòng nhập số điện thoại Việt Nam hợp lệ.");
                request.setAttribute("recipientName", recipientName);
                request.setAttribute("shippingAddress", shippingAddress);
                request.setAttribute("shippingPhone", shippingPhone);
                request.setAttribute("notes", notes);

                prepareCheckoutPage(request, account, buyNowItem, product);
                request.getRequestDispatcher("/WEB-INF/customer/buy-now.jsp").forward(request, response);
                return;
            }

            // Re-prepare checkout page to ensure consistent pricing
            prepareCheckoutPage(request, account, buyNowItem, product);
            
            // Get the final price including any promotions
            double totalAmount = product.getPrice() * buyNowItem.getQuantity();

            // Ensure the product attached to buyNowItem carries the effective unit price
            if (buyNowItem.getProduct() == null) {
                buyNowItem.setProduct(product);
            }
            if (buyNowItem.getProduct() != null) {
                // Set effective unit label and stock for display consistency
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

                // Calculate base price based on package type
                Double basePrice;
                if ("PACK".equalsIgnoreCase(buyNowItem.getPackageType()) && buyNowItem.getPackSize() != null) {
                    Double pricePack = product.getPricePack();
                    if (pricePack != null) {
                        basePrice = pricePack;
                    } else {
                        Double unitPrice = product.getPriceUnit();
                        basePrice = unitPrice != null ? unitPrice * buyNowItem.getPackSize() : 0.0;
                    }
                } else if ("BOX".equalsIgnoreCase(String.valueOf(buyNowItem.getPackageType()))) {
                    basePrice = product.getPrice();
                } else {
                    basePrice = product.getPriceUnit();
                    if (basePrice == null) basePrice = 0.0;
                }

                buyNowItem.getProduct().setUnit(unitLabel);
                buyNowItem.getProduct().setStockQuantity(stockQty);
                buyNowItem.getProduct().setPrice(basePrice); // Set base price for order detail
            }
            double discountAmount = 0;

            // Apply voucher if provided
            Voucher appliedVoucher = null;
            if (voucherCode != null && !voucherCode.trim().isEmpty()) {
                VoucherDAO voucherDAO = new VoucherDAO();
                appliedVoucher = voucherDAO.getVoucherByCode(voucherCode);

                if (appliedVoucher != null && appliedVoucher.isActive()) {
                    // Validate voucher
                    Timestamp now = new Timestamp(System.currentTimeMillis());

                    if (now.after(appliedVoucher.getStartDate())
                            && now.before(appliedVoucher.getEndDate())
                            && totalAmount >= appliedVoucher.getMinOrderValue()
                            && appliedVoucher.getUsageCount() < appliedVoucher.getMaxUsage()) {

                        // Check if voucher is applicable to this product's category
                        if (appliedVoucher.getCategoryID() == null
                                || appliedVoucher.getCategoryID() == product.getCategory().getCategoryID()
                                || appliedVoucher.getCategoryID() == product.getCategory().getParentID()) {

                            // Apply discount
                            discountAmount = appliedVoucher.getDiscountAmount();
                            totalAmount -= discountAmount;

                            // Ensure total is not negative
                            if (totalAmount < 0) {
                                totalAmount = 0;
                            }
                        } else {
                            // Voucher not applicable to this category
                            request.setAttribute("error", "Mã giảm giá không áp dụng cho sản phẩm này");

                            request.setAttribute("recipientName", recipientName);
                            request.setAttribute("shippingAddress", shippingAddress);
                            request.setAttribute("shippingPhone", shippingPhone);
                            request.setAttribute("notes", notes);

                            prepareCheckoutPage(request, account, buyNowItem, product);
                            request.getRequestDispatcher("/WEB-INF/customer/buy-now.jsp").forward(request, response);
                            return;
                        }
                    } else {
                        // Voucher expired or not valid
                        String errorMsg = "";
                        if (now.before(appliedVoucher.getStartDate())) {
                            errorMsg = "Mã giảm giá chưa có hiệu lực";
                        } else if (now.after(appliedVoucher.getEndDate())) {
                            errorMsg = "Mã giảm giá đã hết hạn";
                        } else if (totalAmount < appliedVoucher.getMinOrderValue()) {
                            errorMsg = "Đơn hàng không đủ giá trị tối thiểu để áp dụng mã giảm giá";
                        } else if (appliedVoucher.getUsageCount() >= appliedVoucher.getMaxUsage()) {
                            errorMsg = "Mã giảm giá đã đạt giới hạn sử dụng";
                        }

                        request.setAttribute("error", errorMsg);
                        request.setAttribute("recipientName", recipientName);
                        request.setAttribute("shippingAddress", shippingAddress);
                        request.setAttribute("shippingPhone", shippingPhone);
                        request.setAttribute("notes", notes);

                        prepareCheckoutPage(request, account, buyNowItem, product);
                        request.getRequestDispatcher("/WEB-INF/customer/buy-now.jsp").forward(request, response);
                        return;
                    }
                } else {
                    // Voucher not found or inactive
                    request.setAttribute("error", "Mã giảm giá không tồn tại hoặc không còn hiệu lực");

                    request.setAttribute("recipientName", recipientName);
                    request.setAttribute("shippingAddress", shippingAddress);
                    request.setAttribute("shippingPhone", shippingPhone);
                    request.setAttribute("notes", notes);

                    prepareCheckoutPage(request, account, buyNowItem, product);
                    request.getRequestDispatcher("/WEB-INF/customer/buy-now.jsp").forward(request, response);
                    return;
                }
            }

            // Create new order
            Order newOrder = new Order();
            newOrder.setAccountID(account.getAccountID());
            newOrder.setOrderDate(new Timestamp(System.currentTimeMillis()));
            newOrder.setTotalAmount(totalAmount);
            newOrder.setShippingAddress(shippingAddress);
            newOrder.setShippingPhone(shippingPhone);
            newOrder.setPaymentMethod(paymentMethod);
            newOrder.setNotes(notes);
            newOrder.setPaymentStatus("Chưa thanh toán");

            // Set initial order status
            newOrder.setOrderStatus("Đang xử lý");

            // Save order and get order ID
            OrderDAO orderDAO = new OrderDAO();
            int orderId = orderDAO.createOrder(newOrder, buyNowItem);

            if (orderId > 0) {
                // Order created successfully

                // Apply voucher usage if applicable
                if (appliedVoucher != null && discountAmount > 0) {
                    orderDAO.recordVoucherUsage(appliedVoucher.getVoucherID(), account.getAccountID(), orderId, discountAmount);
                }

                // Update product stock
                productDAO.updateProductStock(product.getProductID(), product.getStockQuantity() - buyNowItem.getQuantity());

                // Order history will be created after successful redirect to avoid foreign key issues
                // Handle payment redirection if needed
                if ("VNPay".equals(paymentMethod)) {
                    // Store order ID in session for payment processing
                    session.setAttribute("pendingOrderId", orderId);
                    session.setAttribute("pendingAmount", totalAmount);

                    // Tạo URL thanh toán VNPay với orderId
                    String paymentUrl = VNPayUtil.getPaymentUrl(request, response, totalAmount, orderId);

                    // Chuyển hướng đến trang thanh toán VNPay
                    response.sendRedirect(paymentUrl);
                } else {
                    // For COD or other methods, redirect to order confirmation page
                    session.setAttribute("orderSuccess", true);
                    session.setAttribute("orderMessage", "Đặt hàng thành công! Cảm ơn bạn đã mua sắm.");

                    // Store success message in session
                    session.setAttribute("successMessage", "Đặt hàng thành công! Cảm ơn quý khách đã mua sắm.");

                    // Clear all checkout related sessions to avoid conflicts
                    session.removeAttribute("buyNowItem");
                    session.removeAttribute("cartItems");
                    session.removeAttribute("checkoutFromCart");

                    // Redirect to order detail page with flag to create history
                    response.sendRedirect(request.getContextPath() + "/orderDetail?orderID=" + orderId + "&newOrder=true");
                }
            } else {
                // Failed to create order
                request.setAttribute("error", "Đã có lỗi xảy ra khi đặt hàng. Vui lòng thử lại.");
                prepareCheckoutPage(request, account, buyNowItem, product);
                request.getRequestDispatcher("/WEB-INF/customer/buy-now.jsp").forward(request, response);
            }

        } catch (Exception e) {
            // Handle exceptions
            request.setAttribute("error", "Lỗi xử lý đơn hàng: " + e.getMessage());

            // Get the buy now item from session and try to prepare the checkout page
            CartItem buyNowItem = (CartItem) session.getAttribute("buyNowItem");
            if (buyNowItem != null) {
                ProductDAO productDAO = new ProductDAO();
                Product product = productDAO.getProductById(buyNowItem.getProductID());
                if (product != null) {
                    prepareCheckoutPage(request, account, buyNowItem, product);
                }
            }

            request.getRequestDispatcher("/WEB-INF/customer/buy-now.jsp").forward(request, response);
        }
    }

    /**
     * Helper method to prepare the checkout page
     */
    private void prepareCheckoutPage(HttpServletRequest request, Account account, CartItem buyNowItem, Product product) {
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
            
            // Debug log for base price
            System.out.println("Debug - Product ID: " + product.getProductID());
            System.out.println("Debug - Base Price before promotion: " + basePrice);
            
            PromotionDAO promotionDAO = new PromotionDAO();
            Promotion activePromotion = promotionDAO.getValidPromotionForProduct(product.getProductID());
            
            if (activePromotion != null) {
                // Calculate discounted price
                double discountPercent = activePromotion.getDiscountPercent();
                finalPrice = basePrice * (1 - discountPercent / 100);
                
                // Debug log for promotion calculation
                System.out.println("Debug - Found active promotion with " + discountPercent + "% discount");
                System.out.println("Debug - Final price after promotion: " + finalPrice);
                
                // Store promotion info for display
                request.setAttribute("appliedPromotion", activePromotion); 
                request.setAttribute("originalPrice", basePrice);
            } else {
                System.out.println("Debug - No active promotion found for product");
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

            // Add some debug log to verify values
            System.out.println("Setting totalAmount attribute in prepareCheckoutPage: " + itemTotal);

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

    /**
     * Validate Vietnamese phone number
     */
    private boolean validatePhoneNumber(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }

        // Pattern for Vietnamese mobile numbers (10 digits, starting with 03, 05, 07, 08, 09)
        String pattern = "^(0|\\+84)[3|5|7|8|9][0-9]{8}$";
        return phone.matches(pattern);
    }
    /**
     * Calculate base price for a product based on package type.
     * Extracted from prepareCheckoutPage for testing.
     */

    /**
     * Test method to verify price calculation and promotion application
     */
    public static void testPriceCalculation() {
        try {
            System.out.println("\n=== Test 1: Product Price Calculation ===");
            
            // Tạo test product
            Product testProduct = new Product();
            testProduct.setProductID(6);
            testProduct.setProductName("Sữa tươi Vinamilk 180ml");
            testProduct.setPrice(100000.0);      // priceBox
            testProduct.setPriceUnit(10000.0);   // priceUnit
            testProduct.setPricePack(50000.0);   // pricePack
            
            // Tạo category cho product
            Category category = new Category();
            category.setCategoryID(1);
            category.setParentID(null);
            testProduct.setCategory(category);
            
            // Test case 1: BOX package
            CartItem testItem = new CartItem();
            testItem.setProductID(6);
            testItem.setQuantity(2.0);
            testItem.setPackageType("BOX");
            System.out.println("\nTest case 1 - BOX package:");
            System.out.println("Expected price: 100,000");
            System.out.println("Actual price: " + calculateBasePrice(testProduct, testItem));
            
            // Test case 2: PACK package
            testItem.setPackageType("PACK");
            testItem.setPackSize(6);
            System.out.println("\nTest case 2 - PACK package (size=6):");
            System.out.println("Expected price: 50,000");
            System.out.println("Actual price: " + calculateBasePrice(testProduct, testItem));
            
            // Test case 3: UNIT package
            testItem.setPackageType("UNIT");
            testItem.setPackSize(null);
            System.out.println("\nTest case 3 - UNIT package:");
            System.out.println("Expected price: 10,000");
            System.out.println("Actual price: " + calculateBasePrice(testProduct, testItem));

            System.out.println("\n=== Test 2: Voucher Application ===");
            
            // Tạo test voucher
            Voucher testVoucher = new Voucher();
            testVoucher.setActive(true);
            testVoucher.setStartDate(new Timestamp(System.currentTimeMillis() - 86400000)); // Yesterday
            testVoucher.setEndDate(new Timestamp(System.currentTimeMillis() + 86400000));   // Tomorrow
            testVoucher.setMinOrderValue(50000.0);
            testVoucher.setMaxUsage(100);
            testVoucher.setUsageCount(0);
            testVoucher.setCategoryID(1); // Same as product category
            
            List<Voucher> testVouchers = new ArrayList<>();
            testVouchers.add(testVoucher);
            
            // Test case 4: Valid voucher
            System.out.println("\nTest case 4 - Valid voucher:");
            System.out.println("Total amount: 200,000 (2 * 100,000)");
            System.out.println("Min order value: 50,000");
            System.out.println("Category match: Yes");
            boolean isValid = false;
            double itemTotal = testProduct.getPrice() * testItem.getQuantity();
            
            if (testVoucher.isActive()
                    && itemTotal >= testVoucher.getMinOrderValue()
                    && testVoucher.getUsageCount() < testVoucher.getMaxUsage()) {

                Integer voucherCategoryId = testVoucher.getCategoryID();
                Category productCategory = testProduct.getCategory();
                
                if (voucherCategoryId == null 
                        || (productCategory != null && (
                            voucherCategoryId.equals(productCategory.getCategoryID())
                            || (productCategory.getParentID() != null 
                                && voucherCategoryId.equals(productCategory.getParentID()))
                        ))) {
                    isValid = true;
                }
            }
            System.out.println("Voucher valid: " + isValid);
            
            // Test case 5: Category mismatch
            System.out.println("\nTest case 5 - Category mismatch:");
            testVoucher.setCategoryID(99); // Different category
            isValid = false;
            if (testVoucher.isActive()
                    && itemTotal >= testVoucher.getMinOrderValue()
                    && testVoucher.getUsageCount() < testVoucher.getMaxUsage()) {

                Integer voucherCategoryId = testVoucher.getCategoryID();
                Category productCategory = testProduct.getCategory();
                
                if (voucherCategoryId == null 
                        || (productCategory != null && (
                            voucherCategoryId.equals(productCategory.getCategoryID())
                            || (productCategory.getParentID() != null 
                                && voucherCategoryId.equals(productCategory.getParentID()))
                        ))) {
                    isValid = true;
                }
            }
            System.out.println("Expected: false (category mismatch)");
            System.out.println("Actual: " + isValid);

            System.out.println("\n=== Test Completed ===");
            
        } catch (Exception e) {
            System.out.println("Test failed with error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Helper method to calculate base price
    private static double calculateBasePrice(Product product, CartItem item) {
        if (product == null || item == null) return 0.0;
        
        String packageType = item.getPackageType();
        if (packageType == null) packageType = "UNIT";
        
        Double basePrice = 0.0;
        switch(packageType.toUpperCase()) {
            case "PACK":
                if (item.getPackSize() != null) {
                    Double pricePack = product.getPricePack();
                    if (pricePack != null) {
                        basePrice = pricePack;
                    } else {
                        Double unitPrice = product.getPriceUnit();
                        basePrice = unitPrice != null ? unitPrice * item.getPackSize() : 0.0;
                    }
                } else {
                    basePrice = product.getPriceUnit();
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
        
        return basePrice != null ? basePrice : 0.0;
    }
    
    /**
     * Main method to run tests
     */
    public static void main(String[] args) {
        testPriceCalculation();
    }

    /**
     * Xử lý callback từ VNPay sau khi thanh toán
     */
    private void processVNPayCallback(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Account account = (Account) session.getAttribute("account");

        // Kiểm tra đăng nhập
        if (account == null) {
            session.setAttribute("redirectAfterLogin", "home");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Lấy thông tin đơn hàng từ session
        Integer pendingOrderId = (Integer) session.getAttribute("pendingOrderId");
        if (pendingOrderId == null) {
            session.setAttribute("errorMessage", "Không tìm thấy thông tin đơn hàng");
            response.sendRedirect(request.getContextPath() + "/customer/reorder");
            return;
        }

        // Lấy các tham số trả về từ VNPay
        Map<String, String> vnpParams = new HashMap<>();
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String paramValue = request.getParameter(paramName);
            if (paramValue != null && !paramValue.isEmpty()) {
                vnpParams.put(paramName, paramValue);
            }
        }

        // Kiểm tra kết quả thanh toán
        String vnpResponseCode = request.getParameter("vnp_ResponseCode");

        OrderDAO orderDAO = new OrderDAO();
        Order order = orderDAO.getOrderById(pendingOrderId);

        if (order == null) {
            session.setAttribute("errorMessage", "Không tìm thấy đơn hàng");
            response.sendRedirect(request.getContextPath() + "/customer/reorder");
            return;
        }

        // Kiểm tra đơn hàng thuộc về người dùng hiện tại
        if (order.getAccountID() != account.getAccountID()) {
            session.setAttribute("errorMessage", "Bạn không có quyền truy cập đơn hàng này");
            response.sendRedirect(request.getContextPath() + "/customer/reorder");
            return;
        }

        if ("00".equals(vnpResponseCode)) {
            // Thanh toán thành công
            // Cập nhật trạng thái thanh toán
            orderDAO.updateOrderStatus(pendingOrderId, "Đang xử lý");
            boolean updated = orderDAO.updatePaymentStatus(pendingOrderId, "Đã thanh toán");

            if (updated) {

                // Thông báo thành công
                session.setAttribute("successMessage", "Thanh toán thành công! Cảm ơn quý khách đã mua sắm.");

                // Xóa thông tin đơn hàng khỏi session
                session.removeAttribute("pendingOrderId");
                session.removeAttribute("pendingAmount");
                session.removeAttribute("buyNowItem");
                session.removeAttribute("cartItems");
                session.removeAttribute("checkoutFromCart");

                // Chuyển hướng đến trang chi tiết đơn hàng
                response.sendRedirect(request.getContextPath() + "/orderDetail?orderID=" + pendingOrderId + "&newOrder=true");
            } else {
                // Lỗi cập nhật trạng thái
                session.setAttribute("errorMessage", "Đã xảy ra lỗi khi cập nhật trạng thái thanh toán");
                response.sendRedirect(request.getContextPath() + "/orderDetail?orderID=" + pendingOrderId);
            }
        } else {
            // Thanh toán thất bại
            // Cập nhật trạng thái đơn hàng thành "Đã hủy"
            orderDAO.cancelOrder(pendingOrderId);

            // Thông báo lỗi
            session.setAttribute("errorMessage", "Thanh toán không thành công. Mã lỗi: " + vnpResponseCode);
            response.sendRedirect(request.getContextPath() + "/orderDetail?orderID=" + pendingOrderId);
        }
    }

    /**
     * Process order from cart items
     */
    private void processCartOrder(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, Account account) throws IOException, ServletException {
        try {
            // Get cart items from session
            List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");

            if (cartItems == null || cartItems.isEmpty()) {
                session.setAttribute("cartError", "Giỏ hàng của bạn đang trống. Vui lòng thử lại.");
                response.sendRedirect("cart");
                return;
            }

            // Quantity changes from the client are ignored; use quantities from session/cart

            // Get order details from form
            String recipientName = request.getParameter("recipientName");
            String shippingAddress = request.getParameter("shippingAddress");
            String shippingPhone = request.getParameter("shippingPhone");
            String paymentMethod = request.getParameter("paymentMethod");
            String voucherCode = request.getParameter("voucherCode");
            String notes = request.getParameter("notes");

            // Validate required fields
            if (recipientName == null || recipientName.trim().isEmpty()
                    || shippingAddress == null || shippingAddress.trim().isEmpty()
                    || shippingPhone == null || shippingPhone.trim().isEmpty()
                    || paymentMethod == null || paymentMethod.trim().isEmpty()) {

                request.setAttribute("error", "Vui lòng điền đầy đủ thông tin giao hàng");

                // Re-populate the form data
                request.setAttribute("recipientName", recipientName);
                request.setAttribute("shippingAddress", shippingAddress);
                request.setAttribute("shippingPhone", shippingPhone);
                request.setAttribute("notes", notes);

                // Redirect back to checkout page
                request.getRequestDispatcher("/WEB-INF/customer/buy-now.jsp").forward(request, response);
                return;
            }

            // Validate phone number
            if (!validatePhoneNumber(shippingPhone)) {
                request.setAttribute("error", "Số điện thoại không hợp lệ. Vui lòng nhập số điện thoại Việt Nam hợp lệ.");
                request.setAttribute("recipientName", recipientName);
                request.setAttribute("shippingAddress", shippingAddress);
                request.setAttribute("shippingPhone", shippingPhone);
                request.setAttribute("notes", notes);

                request.getRequestDispatcher("/WEB-INF/customer/buy-now.jsp").forward(request, response);
                return;
            }

            // Calculate total amount
            double totalAmount = 0;
            ProductDAO productDAO = new ProductDAO();

            // Validate products and calculate total (respect selected package/unit)
            for (CartItem item : cartItems) {
                Product product = item.getProduct();
                if (product == null) {
                    // Fallback: fetch and compute effective fields on the fly
                    product = productDAO.getProductById(item.getProductID());
                }

                // Re-check stock by package selection
                double stockQty;
                if ("PACK".equalsIgnoreCase(item.getPackageType()) && item.getPackSize() != null) {
                    stockQty = productDAO.getPackQuantity(item.getProductID(), item.getPackSize());
                } else {
                    String effType = item.getPackageType() != null ? item.getPackageType() : "UNIT";
                    stockQty = productDAO.getQuantityByPackageType(item.getProductID(), effType);
                }
                if (stockQty < item.getQuantity()) {
                    request.setAttribute("error", "Sản phẩm " + (product != null ? product.getProductName() : ("#" + item.getProductID())) + " không đủ số lượng yêu cầu. Còn lại: " + stockQty);
                    request.getRequestDispatcher("/WEB-INF/customer/buy-now.jsp").forward(request, response);
                    return;
                }

                // Calculate base price
                double basePrice = 0.0;
                if (product != null) {
                    if ("PACK".equalsIgnoreCase(item.getPackageType()) && item.getPackSize() != null) {
                        Double pricePack = product.getPricePack();
                        if (pricePack != null) {
                            basePrice = pricePack;
                        } else {
                            Double unitPrice = product.getPriceUnit();
                            basePrice = unitPrice != null ? unitPrice * item.getPackSize() : 0.0;
                        }
                    } else if ("BOX".equalsIgnoreCase(String.valueOf(item.getPackageType()))) {
                        basePrice = product.getPrice() != null ? product.getPrice() : 0.0;
                    } else {
                        basePrice = product.getPriceUnit() != null ? product.getPriceUnit() : 0.0;
                    }

                    // Check for active promotion
                    PromotionDAO promotionDAO = new PromotionDAO();
                    Promotion activePromotion = promotionDAO.getValidPromotionForProduct(product.getProductID());
                    
                    if (activePromotion != null) {
                        // Calculate discounted price
                        double discountPercent = activePromotion.getDiscountPercent();
                        double finalPrice = basePrice * (1 - discountPercent / 100);
                        // Store promotion info for display
                        request.setAttribute("promotion_" + product.getProductID(), activePromotion);
                        request.setAttribute("originalPrice_" + product.getProductID(), basePrice);
                        product.setPrice(finalPrice); // Set discounted price to product
                        totalAmount += finalPrice * item.getQuantity();
                    } else {
                        product.setPrice(basePrice);
                        totalAmount += basePrice * item.getQuantity();
                    }
                }
            }

            // Apply voucher if provided
            double discountAmount = 0;
            Voucher appliedVoucher = null;
            if (voucherCode != null && !voucherCode.trim().isEmpty()) {
                VoucherDAO voucherDAO = new VoucherDAO();
                appliedVoucher = voucherDAO.getVoucherByCode(voucherCode);

                if (appliedVoucher != null && appliedVoucher.isActive()) {
                    // Validate voucher
                    Timestamp now = new Timestamp(System.currentTimeMillis());

                    if (now.after(appliedVoucher.getStartDate())
                            && now.before(appliedVoucher.getEndDate())
                            && totalAmount >= appliedVoucher.getMinOrderValue()
                            && appliedVoucher.getUsageCount() < appliedVoucher.getMaxUsage()) {

                        // Apply discount
                        discountAmount = appliedVoucher.getDiscountAmount();
                        totalAmount -= discountAmount;

                        // Ensure total is not negative
                        if (totalAmount < 0) {
                            totalAmount = 0;
                        }
                    } else {
                        // Voucher expired or not valid
                        request.setAttribute("error", "Mã giảm giá không hợp lệ hoặc đã hết hạn");
                        request.getRequestDispatcher("/WEB-INF/customer/buy-now.jsp").forward(request, response);
                        return;
                    }
                } else {
                    // Voucher not found
                    request.setAttribute("error", "Mã giảm giá không tồn tại");
                    request.getRequestDispatcher("/WEB-INF/customer/buy-now.jsp").forward(request, response);
                    return;
                }
            }

            // Create new order
            Order newOrder = new Order();
            newOrder.setAccountID(account.getAccountID());
            newOrder.setOrderDate(new Timestamp(System.currentTimeMillis()));
            newOrder.setTotalAmount(totalAmount);
            newOrder.setShippingAddress(shippingAddress);
            newOrder.setShippingPhone(shippingPhone);
            newOrder.setPaymentMethod(paymentMethod);
            newOrder.setNotes(notes);
            newOrder.setPaymentStatus("Chưa thanh toán");

            // Set initial order status
            newOrder.setOrderStatus("Đang xử lý");

            // Save order and get order ID
            OrderDAO orderDAO = new OrderDAO();
            int orderId = orderDAO.createOrderFromCart(newOrder, cartItems);

            if (orderId > 0) {
                // Order created successfully

                // Apply voucher usage if applicable
                if (appliedVoucher != null && discountAmount > 0) {
                    orderDAO.recordVoucherUsage(appliedVoucher.getVoucherID(), account.getAccountID(), orderId, discountAmount);
                }

                // Update product stock
                for (CartItem item : cartItems) {
                    Product product = item.getProduct();
                    productDAO.updateProductStock(product.getProductID(), product.getStockQuantity() - item.getQuantity());
                }

                // Clear cart items
                CartItemDAO cartItemDAO = new CartItemDAO();
                for (CartItem item : cartItems) {
                    cartItemDAO.removeCartItem(item.getCartItemID());
                }

                // Order history will be created after successful redirect to avoid foreign key issues
                // Handle payment redirection if needed
                if ("VNPay".equals(paymentMethod)) {
                    // Store order ID in session for payment processing
                    session.setAttribute("pendingOrderId", orderId);
                    session.setAttribute("pendingAmount", totalAmount);

                    // Tạo URL thanh toán VNPay với orderId
                    String paymentUrl = VNPayUtil.getPaymentUrl(request, response, totalAmount, orderId);

                    // Chuyển hướng đến trang thanh toán VNPay
                    response.sendRedirect(paymentUrl);
                } else {
                    // For COD or other methods, redirect to order confirmation page
                    session.setAttribute("orderSuccess", true);
                    session.setAttribute("successMessage", "Đặt hàng thành công! Cảm ơn quý khách đã mua sắm.");
                    session.removeAttribute("cartItems");
                    session.removeAttribute("checkoutFromCart");

                    // Redirect to order detail page with flag to create history
                    response.sendRedirect(request.getContextPath() + "/orderDetail?orderID=" + orderId + "&newOrder=true");
                }
            } else {
                // Failed to create order
                request.setAttribute("error", "Đã có lỗi xảy ra khi đặt hàng. Vui lòng thử lại.");
                request.getRequestDispatcher("/WEB-INF/customer/buy-now.jsp").forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", "Lỗi xử lý đơn hàng: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/customer/buy-now.jsp").forward(request, response);
        }
    }

    /**
     * Initiate buy now process from cart items
     */
    private void initiateFromCart(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, Account account) throws IOException {
        try {
            // Check if we have selected items
            String selectedItemsStr = request.getParameter("selectedItems");
            List<Integer> selectedItemIds = new ArrayList<>();
            
            if (selectedItemsStr != null && !selectedItemsStr.trim().isEmpty()) {
                String[] itemIds = selectedItemsStr.split(",");
                for (String itemId : itemIds) {
                    try {
                        selectedItemIds.add(Integer.parseInt(itemId.trim()));
                    } catch (NumberFormatException e) {
                        // Skip invalid IDs
                    }
                }
            }
            
            // Get cart items for this user
            CartItemDAO cartItemDAO = new CartItemDAO();
            List<CartItem> allCartItems = cartItemDAO.getCartByAccountId(account.getAccountID(), false);
            List<CartItem> cartItems = new ArrayList<>();
            
            // Filter cart items based on selected IDs if any, otherwise use all items
            if (!selectedItemIds.isEmpty()) {
                for (CartItem item : allCartItems) {
                    if (selectedItemIds.contains(item.getCartItemID())) {
                        cartItems.add(item);
                    }
                }
            } else {
                cartItems = allCartItems;
            }

            // Check if cart is empty
            if (cartItems == null || cartItems.isEmpty()) {
                session.setAttribute("cartError", "Giỏ hàng của bạn đang trống. Vui lòng thêm sản phẩm vào giỏ trước khi thanh toán.");
                response.sendRedirect("cart");
                return;
            }

            // Validate stock availability and update product info
            boolean hasStockIssues = false;
            double itemTotal = 0;
            ProductDAO productDAO = new ProductDAO();

            for (CartItem item : cartItems) {
                Product product = item.getProduct(); // Coming from DAO with effective unit/price
                if (product == null) {
                    // Fallback if not set
                    product = productDAO.getProductById(item.getProductID());
                    item.setProduct(product);
                }

                // Check stock availability by selected package
                double stockQty;
                if ("PACK".equalsIgnoreCase(item.getPackageType()) && item.getPackSize() != null) {
                    stockQty = productDAO.getPackQuantity(item.getProductID(), item.getPackSize());
                } else {
                    String effType = item.getPackageType() != null ? item.getPackageType() : "UNIT";
                    stockQty = productDAO.getQuantityByPackageType(item.getProductID(), effType);
                }

                if (stockQty < item.getQuantity()) {
                    hasStockIssues = true;
                    // Adjust quantity to available stock
                    if (stockQty > 0) {
                        item.setQuantity(stockQty);
                        cartItemDAO.updateCartItemQuantity(item.getCartItemID(), stockQty);
                    } else {
                        // Remove item if no stock available
                        cartItemDAO.removeCartItem(item.getCartItemID());
                        continue;
                    }
                }

                // Calculate item total using effective price/unit already set on product by DAO
                double effectivePrice = (product != null && product.getPrice() != null) ? product.getPrice() : 0.0;
                itemTotal += effectivePrice * item.getQuantity();
            }

            // If stock issues and cart is now empty, redirect back to cart
            if (hasStockIssues) {
                if (cartItems.isEmpty()) {
                    session.setAttribute("cartError", "Tất cả sản phẩm trong giỏ hàng hiện không có sẵn.");
                    response.sendRedirect("cart");
                    return;
                }
                session.setAttribute("cartWarning", "Một số sản phẩm đã được điều chỉnh số lượng do kho không đủ hàng.");
            }

            // Calculate item total
            double roundedItemTotal = itemTotal;

            // Get categories from database
            CategoryDAO categoryDAO = new CategoryDAO();
            List<Category> categories = categoryDAO.getAllCategoriesWithChildren();
            request.setAttribute("categories", categories);

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
                    validVouchers.add(voucher);
                }
            }

            // Get account info
            AccountDAO accountDAO = new AccountDAO();
            Account fullAccount = accountDAO.getUserDetail(account.getAccountID());

            // Store data in session for checkout page
            session.setAttribute("cartItems", cartItems);
            session.setAttribute("cartTotal", roundedItemTotal);
            session.setAttribute("validVouchers", validVouchers);
            session.setAttribute("userInfo", fullAccount);
            session.setAttribute("checkoutFromCart", true);

            // Forward to buy-now GET to display checkout page
            response.sendRedirect("buy-now");

        } catch (Exception e) {
            session.setAttribute("cartError", "Lỗi khi xử lý giỏ hàng: " + e.getMessage());
            response.sendRedirect("cart");
        }
    }
}
