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
import helper.PrepareCheckoutPage;

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

        // Kiểm tra xem trong session có cờ "checkoutFromCart" hay không.
        // Nếu có, nghĩa là người dùng đang thực hiện thanh toán từ giỏ hàng.
        boolean isFromCart = session.getAttribute("checkoutFromCart") != null;

        // Nếu đang mua hàng từ giỏ hàng
        if (isFromCart) {
            // Lấy danh sách sản phẩm trong giỏ hàng từ session
            List<CartItem> cartItems = (List<CartItem>) session.getAttribute("cartItems");
            if (cartItems == null || cartItems.isEmpty()) {
                session.setAttribute("cartError", "Giỏ hàng của bạn đang trống. Vui lòng thử lại.");
                response.sendRedirect("cart");
                return;
            }

            // Đảm bảo các giá trị tổng tiền, voucher hợp lệ và thông tin user có sẵn cho JSP
            Object cartTotal = session.getAttribute("cartTotal");
            if (cartTotal != null) {
                request.setAttribute("itemTotal", cartTotal);
                request.setAttribute("totalAmount", cartTotal);
            }
            Object validVouchers = session.getAttribute("validVouchers");
            // Lấy danh sách voucher hợp lệ từ session
            if (validVouchers != null) {
                request.setAttribute("validVouchers", validVouchers);
            }
            Object userInfo = session.getAttribute("userInfo");
            // Lấy thông tin người dùng từ session
            if (userInfo != null) {
                request.setAttribute("userInfo", userInfo);
            }

            // Chuyển tiếp (forward) sang trang checkout buy-now.jsp để hiển thị form thanh toán
            request.getRequestDispatcher("/WEB-INF/customer/buy-now.jsp").forward(request, response);
            return;
        }

        // Nếu không phải checkout từ giỏ hàng, kiểm tra xem có "buyNowItem" trong session không (mua ngay 1 sản phẩm)
        CartItem buyNowItem = (CartItem) session.getAttribute("buyNowItem");
        if (buyNowItem == null) {
            // No product in session, redirect to home
            session.setAttribute("cartError", "Không thể tiến hành mua ngay. Vui lòng thử lại.");
            response.sendRedirect("home");
            return;
        }

        // Get product information
        ProductDAO productDAO = new ProductDAO();
        // Lấy thông tin chi tiết sản phẩm từ DB bằng ID của buyNowItem
        Product product = productDAO.getProductById(buyNowItem.getProductID());
        if (product == null) {
            session.setAttribute("cartError", "Không tìm thấy sản phẩm. Vui lòng thử lại.");
            response.sendRedirect("home");
            return;
        }

        // Kiểm tra số lượng tồn kho của sản phẩm dựa vào loại gói (BOX, PACK, UNIT, ...)
        double stockQuantity;
        // Nếu loại gói là PACK (so sánh không phân biệt hoa thường) 
        // và có thông tin kích thước pack (packSize không null)
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

        // Gán đối tượng Product vừa lấy từ DB vào cho buyNowItem (CartItem)
        buyNowItem.setProduct(product);

        // Xác định nhãn đơn vị hiển thị (unitLabel) dựa trên loại package mà user chọn
        String unitLabel;
        if ("PACK".equalsIgnoreCase(buyNowItem.getPackageType()) && buyNowItem.getPackSize() != null) {
            // Nếu user mua theo lốc (PACK) và có size (VD: lốc 6 lon, 8 lon, …)
            String itemUnitName = product.getItemUnitName();
            unitLabel = "Lốc" + (buyNowItem.getPackSize() != null
                    ? (" " + buyNowItem.getPackSize() + " " + (itemUnitName != null ? itemUnitName : "đơn vị"))
                    : "");
        } else if ("BOX".equalsIgnoreCase(String.valueOf(buyNowItem.getPackageType()))) {
            String boxUnitName = product.getBoxUnitName();
            unitLabel = boxUnitName != null ? boxUnitName : "thùng";
        } else if ("KG".equalsIgnoreCase(String.valueOf(buyNowItem.getPackageType()))) {
            unitLabel = "kg";
        } else {
            String itemUnitName = product.getItemUnitName();
            unitLabel = itemUnitName != null ? itemUnitName : "đơn vị";
        }
        // Set nhãn đơn vị và số lượng tồn kho (stockQuantity đã check ở trên) cho sản phẩm
        product.setUnit(unitLabel);
        product.setStockQuantity(stockQuantity);

        // Lấy thông tin tài khoản đầy đủ (bao gồm địa chỉ, số điện thoại, …) để điền vào form giao hàng
        AccountDAO accountDAO = new AccountDAO();
        Account fullAccount = accountDAO.getUserDetail(account.getAccountID());

        // Chuẩn bị dữ liệu cho trang checkout (tính toán khuyến mãi, shipping, …)
        PrepareCheckoutPage pre = new PrepareCheckoutPage();
        pre.prepareCheckoutPage(request, account, buyNowItem, product); // Get available vouchers for the user

        // Lấy danh sách voucher mà user này sở hữu
        VoucherDAO voucherDAO = new VoucherDAO();
        List<Voucher> availableVouchers = voucherDAO.getVouchersByAccountId(account.getAccountID());

        // Tạo danh sách voucher hợp lệ (chỉ giữ lại voucher còn hiệu lực)
        List<Voucher> validVouchers = new ArrayList<>();
        Timestamp now = new Timestamp(System.currentTimeMillis());

        // Tính tổng tiền hàng (chưa áp dụng voucher/khuyến mãi)
        double itemTotal = product.getPrice() * buyNowItem.getQuantity();

        // Duyệt qua từng voucher của user để kiểm tra điều kiện hợp lệ
        for (Voucher voucher : availableVouchers) {
            if (voucher.isActive()
                    && now.after(voucher.getStartDate())
                    && now.before(voucher.getEndDate())
                    && itemTotal >= voucher.getMinOrderValue()
                    && voucher.getUsageCount() < voucher.getMaxUsage()) {

                // Check if voucher is applicable to this product's category
                Integer voucherCategoryId = voucher.getCategoryID();
                Category productCategory = product.getCategory();

                // voucher dùng cho tất cả sản phẩm
                if (voucherCategoryId == null
                        || (productCategory != null && (voucherCategoryId.equals(productCategory.getCategoryID())
                        || (productCategory.getParentID() != null
                        && voucherCategoryId.equals(productCategory.getParentID()))))) {
                    validVouchers.add(voucher);
                }
            }
        }

        // Làm tròn tổng tiền về bội số của 1000 (VD: 125,500 → 126,000)
        double roundedItemTotal = Math.round(itemTotal / 1000.0) * 1000;
        request.setAttribute("buyNowItem", buyNowItem);
        request.setAttribute("itemTotal", roundedItemTotal);
        request.setAttribute("totalAmount", roundedItemTotal);
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

        // Lấy tham số action từ request (VD: initiate, processSingle, initiateCart, processCart)
        String action = request.getParameter("action");

        if ("initiate".equals(action)) {
            // Nếu action = initiate → bắt đầu quy trình "Mua ngay" cho 1 sản phẩm
            initiateOrder(request, response, session, account);
        } else if ("processSingle".equals(action)) {
            // Nếu action = processSingle → xử lý đặt hàng (thanh toán) cho 1 sản phẩm "Mua ngay"
            processOrder(request, response, session, account);
        } else if ("initiateCart".equals(action)) {
            // Nếu action = initiateCart → bắt đầu quy trình đặt hàng cho nhiều sản phẩm trong giỏ hàng
            initiateFromCart(request, response, session, account);
        } else if ("processCart".equals(action)) {
            // Nếu action = processCart → xử lý đặt hàng (thanh toán) cho toàn bộ giỏ hàng
            processCartOrder(request, response, session, account);
        } else {
            // Nếu action không thuộc 4 loại trên → coi là không hợp lệ
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
            // Lấy loại gói (packageType: BOX, PACK, UNIT, KG...) từ request
            String packageType = request.getParameter("packageType");
            // Lấy giá trị packSize (số lượng lon trong 1 lốc nếu packageType = PACK)
            String packSizeStr = request.getParameter("packSize");
            // Biến lưu packSize thực tế (Integer để có thể null nếu không truyền)
            Integer packSize = null;
            if (packSizeStr != null && !packSizeStr.trim().isEmpty()) {
                try {
                    packSize = Integer.parseInt(packSizeStr);
                } catch (Exception ignore) {
                }
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

            // Xóa dữ liệu phiên thanh toán trước đó để tránh xung đột
            session.removeAttribute("cartItems");
            session.removeAttribute("checkoutFromCart");

            // Lưu đối tượng sản phẩm mua ngay vào session
            session.setAttribute("buyNowItem", buyNowItem);

            // Chuyển hướng sang servlet “buy-now” (phương thức GET) để hiển thị trang thanh toán ngay
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

                // Điều chỉnh lại số lượng mua = số lượng tối đa còn trong kho
                buyNowItem.setQuantity(stockQty);

                // Nạp lại thông tin checkout với số lượng đã chỉnh sửa
                PrepareCheckoutPage pre = new PrepareCheckoutPage();
                pre.prepareCheckoutPage(request, account, buyNowItem, product);
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

                // Gọi phương thức chuẩn bị dữ liệu (ví dụ: tổng tiền, voucher hợp lệ, thông tin sản phẩm, tài khoản) và gắn vào request  
                PrepareCheckoutPage pre = new PrepareCheckoutPage();
                pre.prepareCheckoutPage(request, account, buyNowItem, product);
                request.getRequestDispatcher("/WEB-INF/customer/buy-now.jsp").forward(request, response);
                return;
            }

            // Validate phone number
            if (!validatePhoneNumber(shippingPhone)) {
                request.setAttribute("error",
                        "Số điện thoại không hợp lệ. Vui lòng nhập số điện thoại hợp lệ.");
                request.setAttribute("recipientName", recipientName);
                request.setAttribute("shippingAddress", shippingAddress);
                request.setAttribute("shippingPhone", shippingPhone);
                request.setAttribute("notes", notes);

                PrepareCheckoutPage pre = new PrepareCheckoutPage();
                pre.prepareCheckoutPage(request, account, buyNowItem, product);
                request.getRequestDispatcher("/WEB-INF/customer/buy-now.jsp").forward(request, response);
                return;
            }

            // Re-prepare checkout page to ensure consistent pricing
            PrepareCheckoutPage pre = new PrepareCheckoutPage();
            pre.prepareCheckoutPage(request, account, buyNowItem, product);

            // Tính tổng số tiền đơn hàng dựa trên giá sản phẩm và số lượng (chưa tính giảm giá phức tạp khác)  
            double totalAmount = product.getPrice() * buyNowItem.getQuantity();

            // Nếu trong đối tượng buyNowItem chưa có thông tin Product thì gán Product hiện tại vào  
            if (buyNowItem.getProduct() == null) {
                buyNowItem.setProduct(product);
            }
            if (buyNowItem.getProduct() != null) {
                // Tạo biến unitLabel để hiển thị đơn vị tính (lốc, thùng, kg, lon, trái, …) cho đúng loại sản phẩm
                String unitLabel;
                if ("PACK".equalsIgnoreCase(buyNowItem.getPackageType()) && buyNowItem.getPackSize() != null) {
                    String itemUnitName = product.getItemUnitName();
                    unitLabel = "Lốc" + (buyNowItem.getPackSize() != null
                            ? (" " + buyNowItem.getPackSize() + " " + (itemUnitName != null ? itemUnitName : "đơn vị"))
                            : "");
                } else if ("BOX".equalsIgnoreCase(String.valueOf(buyNowItem.getPackageType()))) {
                    String boxUnitName = product.getBoxUnitName();
                    unitLabel = boxUnitName != null ? boxUnitName : "thùng";
                } else if ("KG".equalsIgnoreCase(String.valueOf(buyNowItem.getPackageType()))) {
                    unitLabel = "kg";
                } else {
                    String itemUnitName = product.getItemUnitName();
                    unitLabel = itemUnitName != null ? itemUnitName : "đơn vị";
                }

                // Khai báo biến basePrice để lưu giá cơ bản của sản phẩm theo loại gói (PACK, BOX, UNIT, ...)
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
                    // Các trường hợp còn lại (mua theo đơn vị lẻ, kg, trái, lon, ...)  
                    // thì lấy giá đơn vị (Unit price)
                    basePrice = product.getPriceUnit();
                    if (basePrice == null) {
                        // Nếu giá unit bị null thì set mặc định = 0.0 để tránh lỗi NullPointerException
                        basePrice = 0.0;
                    }
                }

                // Gán nhãn đơn vị tính (lon, thùng, lốc 6 lon, kg, ...) cho sản phẩm trong buyNowItem  
                // để hiển thị đúng trong giao diện checkout
                buyNowItem.getProduct().setUnit(unitLabel);
                // Cập nhật số lượng tồn kho hiện tại vào sản phẩm để hiển thị cho người dùng
                buyNowItem.getProduct().setStockQuantity(stockQty);
                // Gán giá cơ bản (theo loại mua) cho sản phẩm trong buyNowItem  
                // Giá này sẽ được dùng để hiển thị và lưu vào chi tiết đơn hàng
                buyNowItem.getProduct().setPrice(basePrice);
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

                            // Lấy số tiền giảm giá từ voucher đã áp dụng và gán vào biến discountAmount
                            discountAmount = appliedVoucher.getDiscountAmount();
                            // Trừ số tiền giảm giá ra khỏi tổng tiền đơn hàng
                            totalAmount -= discountAmount;

                            // Đảm bảo rằng tổng tiền không bị âm sau khi trừ giảm giá
                            // Nếu âm thì gán về 0 (vì tổng thanh toán nhỏ nhất phải là 0)
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

                            pre.prepareCheckoutPage(request, account, buyNowItem, product);
                            request.getRequestDispatcher("/WEB-INF/customer/buy-now.jsp").forward(request, response);
                            return;
                        }
                    } else {
                        // Khởi tạo biến errorMsg để lưu thông báo lỗi (nếu mã giảm giá không hợp lệ)
                        String errorMsg = "";
                        if (now.before(appliedVoucher.getStartDate())) {
                            // Kiểm tra nếu thời gian hiện tại nhỏ hơn ngày bắt đầu của voucher
                            errorMsg = "Mã giảm giá chưa có hiệu lực";
                            // → Nghĩa là voucher chưa thể sử dụng
                        } else if (now.after(appliedVoucher.getEndDate())) {
                            // Kiểm tra nếu thời gian hiện tại lớn hơn ngày kết thúc của voucher
                            errorMsg = "Mã giảm giá đã hết hạn";
                            // → Voucher đã hết hạn sử dụng
                        } else if (totalAmount < appliedVoucher.getMinOrderValue()) {
                            // Kiểm tra nếu tổng giá trị đơn hàng nhỏ hơn giá trị tối thiểu mà voucher yêu cầu
                            errorMsg = "Đơn hàng không đủ giá trị tối thiểu để áp dụng mã giảm giá";
                            // → Người dùng chưa đạt điều kiện giá trị đơn hàng tối thiểu
                        } else if (appliedVoucher.getUsageCount() >= appliedVoucher.getMaxUsage()) {
                            // Kiểm tra nếu số lần voucher đã được dùng >= số lần tối đa cho phép
                            errorMsg = "Mã giảm giá đã đạt giới hạn sử dụng";
                            // → Voucher không thể sử dụng thêm
                        }

                        request.setAttribute("error", errorMsg);
                        request.setAttribute("recipientName", recipientName);
                        request.setAttribute("shippingAddress", shippingAddress);
                        request.setAttribute("shippingPhone", shippingPhone);
                        request.setAttribute("notes", notes);

                        pre.prepareCheckoutPage(request, account, buyNowItem, product);
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

                    pre.prepareCheckoutPage(request, account, buyNowItem, product);
                    request.getRequestDispatcher("/WEB-INF/customer/buy-now.jsp").forward(request, response);
                    return;
                }
            }

            // Tính thuế VAT (8%) dựa trên tổng tiền sau khuyến mãi nhưng trước khi áp dụng voucher.
            // Lưu ý: cộng lại discountAmount để ra giá trị trước khi trừ voucher, sau đó nhân 8%.
            double vat = (totalAmount + discountAmount) * 0.08;

            // Tính tổng tiền cuối cùng: tổng sau khi trừ khuyến mãi/voucher rồi cộng thêm VAT.
            double finalTotal = totalAmount + vat;

            // Đảm bảo rằng tổng tiền cuối cùng không bị âm. Nếu âm thì đặt bằng 0.
            if (finalTotal < 0) {
                finalTotal = 0;
            }

            // Create new order
            Order newOrder = new Order();
            newOrder.setAccountID(account.getAccountID());
            newOrder.setOrderDate(new Timestamp(System.currentTimeMillis()));
            newOrder.setTotalAmount(finalTotal); // Lưu tổng tiền trước VAT (VAT sẽ được tính trong DAO)
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

                // Nếu có áp dụng voucher và số tiền giảm giá > 0:
                // → Lưu lại việc sử dụng voucher vào database (liên kết voucher với đơn hàng, user và số tiền giảm).
                if (appliedVoucher != null && discountAmount > 0) {
                    orderDAO.recordVoucherUsage(appliedVoucher.getVoucherID(), account.getAccountID(), orderId,
                            discountAmount);
                }

                // Cập nhật lại số lượng tồn kho của sản phẩm sau khi đặt hàng thành công:
                // Lấy số lượng hiện tại trong Product trừ đi số lượng khách đã mua (buyNowItem.getQuantity()).
                productDAO.updateProductStock(product.getProductID(),
                        product.getStockQuantity() - buyNowItem.getQuantity());

                // Order history will be created after successful redirect to avoid foreign key
                // issues
                // Handle payment redirection if needed
                if ("VNPay".equals(paymentMethod)) {
                    // Store order ID in session for payment processing
                    session.setAttribute("pendingOrderId", orderId);
                    session.setAttribute("pendingAmount", finalTotal);

                    // Tạo URL thanh toán VNPay với orderId
                    String paymentUrl = VNPayUtil.getPaymentUrl(request, response, finalTotal, orderId);

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
                    response.sendRedirect(
                            request.getContextPath() + "/orderDetail?orderID=" + orderId + "&newOrder=true");
                }
            } else {
                // Failed to create order
                request.setAttribute("error", "Đã có lỗi xảy ra khi đặt hàng. Vui lòng thử lại.");
                pre.prepareCheckoutPage(request, account, buyNowItem, product);
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
                    PrepareCheckoutPage pre = new PrepareCheckoutPage();
                    pre.prepareCheckoutPage(request, account, buyNowItem, product);
                }
            }

            request.getRequestDispatcher("/WEB-INF/customer/buy-now.jsp").forward(request, response);
        }
    }

    /**
     * Validate Vietnamese phone number
     */
    private boolean validatePhoneNumber(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }

        // Pattern for Vietnamese mobile numbers (10 digits)
        String pattern = "^(0|\\+84)[0-9]{9}$";
        return phone.matches(pattern);
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
                response.sendRedirect(
                        request.getContextPath() + "/orderDetail?orderID=" + pendingOrderId + "&newOrder=true");
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

            // Quantity changes from the client are ignored; use quantities from
            // session/cart
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
                request.setAttribute("error",
                        "Số điện thoại không hợp lệ. Vui lòng nhập số điện thoại Việt Nam hợp lệ.");
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
                    request.setAttribute("error",
                            "Sản phẩm " + (product != null ? product.getProductName() : ("#" + item.getProductID()))
                            + " không đủ số lượng yêu cầu. Còn lại: " + stockQty);
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

                    // Check for active promotion and get final price
                    OrderDAO orderDAO = new OrderDAO();
                    double finalPrice = orderDAO.getFinalPriceForPackageType(item);

                    // Store promotion info for display
                    PromotionDAO promotionDAO = new PromotionDAO();
                    Promotion activePromotion = promotionDAO.getValidPromotionForProduct(product.getProductID());

                    if (activePromotion != null) {
                        request.setAttribute("promotion_" + product.getProductID(), activePromotion);
                        request.setAttribute("originalPrice_" + product.getProductID(), basePrice);
                        request.setAttribute("discountPercent_" + product.getProductID(), activePromotion.getDiscountPercent());
                    }

                    product.setPrice(finalPrice);
                    totalAmount += finalPrice * item.getQuantity();
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

            // Calculate VAT (8% of total after promotion, BEFORE voucher)
            double vat = (totalAmount + discountAmount) * 0.08;

            // Final total = total after promotion - voucher + VAT
            double finalTotal = totalAmount + vat;

            // Store voucher information for display
            if (appliedVoucher != null) {
                request.setAttribute("appliedVoucher", appliedVoucher);
                request.setAttribute("voucherDiscount", discountAmount);
            }

            // Ensure final total is not negative (same logic as ReorderServlet)
            if (finalTotal < 0) {
                finalTotal = 0;
            }

            // Create new order
            Order newOrder = new Order();
            newOrder.setAccountID(account.getAccountID());
            newOrder.setOrderDate(new Timestamp(System.currentTimeMillis()));

            // Lưu finalTotal (tổng tiền cuối cùng đã bao gồm VAT và áp dụng voucher) vào TotalAmount
            // để khi OrderDAO tính toán lại, nó sẽ sử dụng giá trị này
            newOrder.setTotalAmount(finalTotal);

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
                    orderDAO.recordVoucherUsage(appliedVoucher.getVoucherID(), account.getAccountID(), orderId,
                            discountAmount);
                }

                // Update product stock
                for (CartItem item : cartItems) {
                    Product product = item.getProduct();
                    productDAO.updateProductStock(product.getProductID(),
                            product.getStockQuantity() - item.getQuantity());
                }

                // Clear cart items
                CartItemDAO cartItemDAO = new CartItemDAO();
                for (CartItem item : cartItems) {
                    cartItemDAO.removeCartItem(item.getCartItemID());
                }

                // Order history will be created after successful redirect to avoid foreign key
                // issues
                // Handle payment redirection if needed
                if ("VNPay".equals(paymentMethod)) {
                    // Store order ID in session for payment processing
                    session.setAttribute("pendingOrderId", orderId);
                    session.setAttribute("pendingAmount", finalTotal);

                    // Tạo URL thanh toán VNPay với orderId
                    String paymentUrl = VNPayUtil.getPaymentUrl(request, response, finalTotal, orderId);

                    // Chuyển hướng đến trang thanh toán VNPay
                    response.sendRedirect(paymentUrl);
                } else {
                    // For COD or other methods, redirect to order confirmation page
                    session.setAttribute("orderSuccess", true);
                    session.setAttribute("successMessage", "Đặt hàng thành công! Cảm ơn quý khách đã mua sắm.");
                    session.removeAttribute("cartItems");
                    session.removeAttribute("checkoutFromCart");

                    // Redirect to order detail page with flag to create history
                    response.sendRedirect(
                            request.getContextPath() + "/orderDetail?orderID=" + orderId + "&newOrder=true");
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
                session.setAttribute("cartError",
                        "Giỏ hàng của bạn đang trống. Vui lòng thêm sản phẩm vào giỏ trước khi thanh toán.");
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

                PromotionDAO promoDAO = new PromotionDAO();
                double finalPrice = promoDAO.applyPromotion(product);

                request.setAttribute("originalPrice_" + product.getProductID(), product.getPrice());
            }

            // If stock issues and cart is now empty, redirect back to cart
            if (hasStockIssues) {
                if (cartItems.isEmpty()) {
                    session.setAttribute("cartError", "Tất cả sản phẩm trong giỏ hàng hiện không có sẵn.");
                    response.sendRedirect("cart");
                    return;
                }
                session.setAttribute("cartWarning",
                        "Một số sản phẩm đã được điều chỉnh số lượng do kho không đủ hàng.");
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
