package controller;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import dao.CartItemDAO;
import dao.CategoryDAO;
import dao.ProductDAO;
import dao.PromotionDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Account;
import model.CartItem;
import model.Category;
import model.Product;
import model.Promotion;

/**
 * Servlet for handling shopping cart operations
 */
@WebServlet(name = "CartServlet", urlPatterns = {"/cart"})
public class CartServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * Handles GET requests for displaying the cart
     *
     * @param request
     * @param response
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Get account from session
        HttpSession session = request.getSession();
        Account account = (Account) session.getAttribute("account");

        // Initialize cart DAO
        CartItemDAO cartItemDAO = new CartItemDAO();

        // Check if this is a count request
        String action = request.getParameter("action");
        if ("count".equals(action)) {
            getCartCount(request, response, account, cartItemDAO);
            return;
        }

        // Redirect to login if not logged in
        if (account == null) {
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

        // Get cart items
        List<CartItem> activeItems = cartItemDAO.getCartItems(account.getAccountID(), "Active");
        List<CartItem> savedItems = cartItemDAO.getCartItems(account.getAccountID(), "Saved");

        System.out.println(
                "Cart page request: Found " + activeItems.size() + " active items for user " + account.getUsername());

// Tính tổng giỏ hàng có áp dụng khuyến mãi
double cartTotal = 0;
PromotionDAO promoDAO = new PromotionDAO();

for (CartItem item : activeItems) {
    Product p = item.getProduct();

    Promotion promo = promoDAO.getValidPromotionForProduct(p.getProductID());
    if (promo != null) {
        double basePrice = p.getPrice();
        double discountPercent = promo.getDiscountPercent();
        double finalPrice = basePrice * (1 - discountPercent / 100);

        // có áp dụng giảm giá
        cartTotal += finalPrice * item.getQuantity();

        // setAttribute để JSP lấy ra
        request.setAttribute("promotion_" + p.getProductID(), promo);
        request.setAttribute("originalPrice_" + p.getProductID(), basePrice);
        request.setAttribute("discountPercent_" + p.getProductID(), discountPercent);
        request.setAttribute("finalPrice_" + p.getProductID(), finalPrice);
    } else {
        // Không có promotion tính giá gốc
        cartTotal += p.getPrice() * item.getQuantity();
    }
}

        // Set attributes for JSP
        request.setAttribute("activeItems", activeItems);
        request.setAttribute("savedItems", savedItems);
        request.setAttribute("cartTotal", cartTotal);

        // Check for messages
        String cartMessage = (String) session.getAttribute("cartMessage");
        if (cartMessage != null) {
            request.setAttribute("cartMessage", cartMessage);
            session.removeAttribute("cartMessage");
        }

        String cartError = (String) session.getAttribute("cartError");
        if (cartError != null) {
            request.setAttribute("cartError", cartError);
            session.removeAttribute("cartError");
        }

        // Forward to cart JSP
        request.getRequestDispatcher("/WEB-INF/customer/cart.jsp").forward(request, response);
    }

    /**
     * Get cart item count for AJAX requests
     */
    private void getCartCount(HttpServletRequest request, HttpServletResponse response, Account account,
            CartItemDAO cartItemDAO)
            throws IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        // If user is not logged in, return 0
        if (account == null) {
            response.getWriter().write("0");
            return;
        }

        // If user is not a customer, return 0
        if (account.getRole() != 0) {
            response.getWriter().write("0");
            return;
        }

        // Get cart item count
        int count = cartItemDAO.countCartItems(account.getAccountID(), "Active");
        response.getWriter().write(String.valueOf(count));
    }

    /**
     * Handles POST requests for cart actions
     *
     * @param request
     * @param response
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Account account = (Account) session.getAttribute("account");

        // Get action parameter from either query string or form data
        String action = request.getParameter("action");
        System.out.println("Cart action: " + action);
        System.out.println("Request parameters: " + request.getParameterMap().keySet());

        CartItemDAO cartItemDAO = new CartItemDAO();

        // Check for AJAX requests
        String xRequestedWith = request.getHeader("X-Requested-With");
        boolean isAjax = "XMLHttpRequest".equals(xRequestedWith);
        System.out.println("Is AJAX request: " + isAjax);

        // Check if user is logged in
        if (account == null) {
            if (isAjax) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"success\":false,\"message\":\"Vui lòng đăng nhập\"}");
                return;
            }
            response.sendRedirect("login");
            return;
        }

        // Check if user is a customer
        if (account.getRole() != 0) {
            if (isAjax) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter()
                        .write("{\"success\":false,\"message\":\"Chỉ khách hàng mới có thể sử dụng giỏ hàng\"}");
                return;
            }

            // Redirect based on role
            if (account.getRole() == 1) {
                response.sendRedirect("admin");
            } else if (account.getRole() == 2) {
                response.sendRedirect("staff");
            } else {
                response.sendRedirect("home");
            }
            return;
        }

        if (action == null) {
            if (isAjax) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"success\":false,\"message\":\"Thiếu tham số action\"}");
                return;
            }
            response.sendRedirect("cart");
            return;
        }

        // Process based on action
        switch (action) {
            case "add":
                addToCart(request, response, account, cartItemDAO);
                break;

            case "update":
                updateCartItem(request, response, isAjax, cartItemDAO);
                break;

            case "remove":
                removeCartItem(request, response, cartItemDAO);
                break;
                
            case "removeSelected":
                removeSelectedItems(request, response, account, cartItemDAO);
                break;

            case "saveForLater":
                saveForLater(request, response, cartItemDAO);
                break;

            case "moveToCart":
                moveToCart(request, response, cartItemDAO);
                break;

            case "clear":
                clearCart(request, response, account, cartItemDAO);
                break;

            default:
                if (isAjax) {
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write("{\"success\":false,\"message\":\"Hành động không hợp lệ\"}");
                } else {
                    response.sendRedirect("cart");
                }
                break;
        }
    }

    /**
     * Add product to cart
     */
    private void addToCart(HttpServletRequest request, HttpServletResponse response,
            Account account, CartItemDAO cartItemDAO) throws IOException {
        // Check if this is an AJAX request
        String xRequestedWith = request.getHeader("X-Requested-With");
        boolean isAjax = "XMLHttpRequest".equals(xRequestedWith);

        // Thiết lập content-type cho AJAX request
        if (isAjax) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
        }

        try {
            // Check if user is logged in
            if (account == null) {
                if (isAjax) {
                    response.getWriter().write(
                            "{\"success\":false,\"message\":\"Vui lòng đăng nhập để thêm sản phẩm vào giỏ hàng\"}");
                    return;
                }

                response.sendRedirect("login");
                return;
            }

            // Check if user is a customer
            if (account.getRole() != 0) {
                if (isAjax) {
                    response.getWriter()
                            .write("{\"success\":false,\"message\":\"Chỉ khách hàng mới có thể sử dụng giỏ hàng\"}");
                    return;
                }

                // Redirect based on role
                if (account.getRole() == 1) {
                    response.sendRedirect("admin");
                } else if (account.getRole() == 2) {
                    response.sendRedirect("staff");
                } else {
                    response.sendRedirect("home");
                }
                return;
            }

            int productID = Integer.parseInt(request.getParameter("productID"));
            double quantity = Double.parseDouble(request.getParameter("quantity"));
            String packageTypeReq = request.getParameter("packageType"); // UNIT | BOX | PACK | KG
            String packSizeStr = request.getParameter("packSize");
            Integer packSize = null;
            if (packSizeStr != null && !packSizeStr.trim().isEmpty()) {
                try { packSize = Integer.parseInt(packSizeStr); } catch (Exception ignore) {}
            }

            // Validate quantity
            if (quantity <= 0) {
                quantity = 1;
            }

            // Get the actual stock quantity by package type (KG for fruits, UNIT otherwise)
            ProductDAO productDAO = new ProductDAO();
            Product product = productDAO.getProductById(productID);
            String packageType = "UNIT";
            try {
                if (product != null && product.getCategory() != null) {
                    int parentId = product.getCategory().getParentID();
                    if (parentId == 3) {
                        packageType = "KG";
                    }
                }
            } catch (Exception ignore) {}
            // Override by explicit request when not fruit
            if (packageTypeReq != null && !"KG".equals(packageType)) {
                packageType = packageTypeReq;
            }
            // Normalize pack selection for subsequent checks
            Integer effectivePackSize = ("PACK".equalsIgnoreCase(packageType) && packSize != null) ? packSize : null;

            double stockQuantity;
            if ("PACK".equalsIgnoreCase(packageType) && packSize != null) {
                stockQuantity = productDAO.getPackQuantity(productID, packSize);
            } else {
                stockQuantity = productDAO.getQuantityByPackageType(productID, packageType);
            }

            // Check existing quantity in cart with same package selection
            CartItem existing = cartItemDAO.getCartItemByProductAndPackage(account.getAccountID(), productID, "Active", packageType, effectivePackSize);
            double requestedTotal = quantity + (existing != null ? existing.getQuantity() : 0.0);

            // Check if stock is sufficient for total intended quantity
            if (stockQuantity < requestedTotal) {
                if (isAjax) {
                    String errorMessage;
                    if (existing != null && existing.getQuantity() > 0) {
                        errorMessage = "Bạn đã có " + existing.getQuantity() + " sản phẩm trong giỏ hàng. Không thể thêm số lượng đã chọn vào giỏ hàng vì sẽ vượt quá giới hạn mua hàng của bạn.";
                    } else {
                        errorMessage = "Không đủ số lượng trong kho.";
                    }
                    response.getWriter().write("{\"success\":false,\"message\":\"" + errorMessage + "\"}");
                    return;
                }

                String errorMessage;
                if (existing != null && existing.getQuantity() > 0) {
                    errorMessage = "Bạn đã có " + existing.getQuantity() + " sản phẩm trong giỏ hàng. Không thể thêm số lượng đã chọn vào giỏ hàng vì sẽ vượt quá giới hạn mua hàng của bạn.";
                } else {
                    errorMessage = "Không đủ số lượng trong kho.";
                }
                request.getSession().setAttribute("cartError", errorMessage);
                response.sendRedirect("ProductDetail?id=" + productID);
                return;
            }

            // Add to cart with selected package type and pack size
            // upsert behavior: increment if exists (same packageType+packSize), else insert
            cartItemDAO.upsertCartItem(account.getAccountID(), productID, quantity, packageType, effectivePackSize);
            boolean success = true;

            if (isAjax) {
                if (success) {
                    // Tính toán số lượng sản phẩm trong giỏ hàng
                    int cartCount = cartItemDAO.countCartItems(account.getAccountID(), "Active");

                    // Trả về JSON với thông tin cập nhật
                    String json = String.format(
                            "{\"success\":true,\"message\":\"Đã thêm sản phẩm vào giỏ hàng\",\"cartSize\":%d}",
                            cartCount);
                    response.getWriter().write(json);
                } else {
                    response.getWriter()
                            .write("{\"success\":false,\"message\":\"Không thể thêm sản phẩm vào giỏ hàng\"}");
                }
                return;
            }

            // Redirect based on result
            if (success) {
                // Get referring URL or default to product page
                String referer = request.getHeader("Referer");
                String redirectURL = (referer != null && !referer.isEmpty()) ? referer
                        : "ProductDetail?id=" + productID;

                request.getSession().setAttribute("cartMessage", "Đã thêm sản phẩm vào giỏ hàng");
                response.sendRedirect(redirectURL);
            } else {
                request.getSession().setAttribute("cartError", "Không thể thêm sản phẩm vào giỏ hàng");
                response.sendRedirect("ProductDetail?id=" + productID);
            }
        } catch (NumberFormatException e) {
            if (isAjax) {
                response.getWriter().write("{\"success\":false,\"message\":\"Dữ liệu không hợp lệ\"}");
            } else {
                response.sendRedirect("home");
            }
        } catch (Exception e) {
            if (isAjax) {
                String errorMessage = e.getMessage().replace("\"", "'");
                response.getWriter().write("{\"success\":false,\"message\":\"Lỗi: " + errorMessage + "\"}");
            } else {
                request.getSession().setAttribute("cartError", "Lỗi: " + e.getMessage());
                response.sendRedirect("home");
            }
        }
    }

    /**
     * Update cart item quantity
     */
    private void updateCartItem(HttpServletRequest request, HttpServletResponse response, boolean isAjax, CartItemDAO cartItemDAO)
            throws IOException {
        // Đảm bảo content-type được thiết lập ngay từ đầu
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Log request parameters
            System.out.println("updateCartItem called with parameters:");
            System.out.println("cartItemID: " + request.getParameter("cartItemID"));
            System.out.println("quantity: " + request.getParameter("quantity"));

            // Check if user is logged in
            HttpSession session = request.getSession();
            Account account = (Account) session.getAttribute("account");

            if (account == null) {
                System.out.println("User not logged in");
                response.getWriter()
                        .write("{\"success\":false,\"message\":\"Vui lòng đăng nhập để cập nhật giỏ hàng\"}");
                return;
            }

            // Check if user is a customer
            if (account.getRole() != 0) {
                System.out.println("User is not a customer, role: " + account.getRole());
                response.getWriter()
                        .write("{\"success\":false,\"message\":\"Chỉ khách hàng mới có thể cập nhật giỏ hàng\"}");
                return;
            }

            int cartItemID = Integer.parseInt(request.getParameter("cartItemID"));
            double quantity = Double.parseDouble(request.getParameter("quantity"));

            // Validate quantity
            if (quantity <= 0) {
                quantity = 1;
            }

            // Get the cart item
            CartItem cartItem = cartItemDAO.getCartItemById(cartItemID);
            if (cartItem == null) {
                System.out.println("Cart item not found: " + cartItemID);
                response.getWriter()
                        .write("{\"success\":false,\"message\":\"Không tìm thấy sản phẩm trong giỏ hàng\"}");
                return;
            }

            // Get current quantity in cart
            double currentQuantity = cartItem.getQuantity();

            // Check stock based on the item's selected package (BOX/UNIT/PACK/KG)
            ProductDAO productDAO = new ProductDAO();
            String packageType = cartItem.getPackageType() != null ? cartItem.getPackageType() : "UNIT";
            Double stockQuantity;
            if ("PACK".equalsIgnoreCase(packageType) && cartItem.getPackSize() != null) {
                stockQuantity = productDAO.getPackQuantity(cartItem.getProductID(), cartItem.getPackSize());
            } else {
                stockQuantity = productDAO.getQuantityByPackageType(cartItem.getProductID(), packageType);
            }
            System.out.println("Updating cart item: ID=" + cartItemID + ", Current quantity=" + currentQuantity
                    + ", New quantity=" + quantity + ", Stock quantity=" + stockQuantity);

            // Only validate stock if increasing quantity
            // Always allow decreasing quantity even if current quantity exceeds stock
            if (quantity > currentQuantity && stockQuantity < quantity) {
                String errorMessage = "Không đủ số lượng trong kho. Hiện tại chỉ còn " + stockQuantity + " sản phẩm.";
                System.out.println("Stock insufficient: " + errorMessage);
                if (isAjax) {
                    String json = String.format(
                            "{\"success\":false,"
                            + "\"message\":\"%s\"," 
                            + "\"validQuantity\":%.2f}",
                            errorMessage,
                            Math.min(currentQuantity, stockQuantity)
                    );
                    response.getWriter().write(json);
                } else {
                    session.setAttribute("errorMessage", errorMessage);
                    response.sendRedirect(request.getContextPath() + "/cart");
                }
                return;
            }

            // Update cart item quantity
            boolean success = quantity <= 0 ? cartItemDAO.removeCartItem(cartItemID)
                    : cartItemDAO.updateCartItemQuantity(cartItemID, quantity);
            System.out.println("Update result: " + (success ? "success" : "failed"));

            double itemTotal = 0;
            itemTotal = cartItem.getProduct().getPrice() * quantity;

            if (success) {
                // Calculate new cart total
                List<CartItem> activeItems = cartItemDAO.getCartItems(account.getAccountID(), "Active");
                double cartTotalCalc = 0;
                for (CartItem it : activeItems) {
                    if (it.getProduct() != null) {
                        cartTotalCalc += it.getProduct().getPrice() * it.getQuantity();
                    }
                }
                double cartTotal = cartTotalCalc;
                int totalItems = cartItemDAO.countCartItems(account.getAccountID(), "Active");

                System.out.println(
                        "Cart update successful: Item total=" + itemTotal + ", Cart total=" + cartTotal
                        + ", Total items=" + totalItems);

                // Ensure values are valid numbers and properly formatted for JSON
                if (Double.isNaN(cartTotal)) {
                    cartTotal = 0;
                }
                if (Double.isNaN(itemTotal)) {
                    itemTotal = 0;
                }

                // Get active cart items count
                int activeItemsCount = activeItems.size();
                System.out.println("isAjax: " + isAjax);
                if (isAjax) {
                    // Return JSON with updated information
                    String json = String.format(java.util.Locale.US,
                            "{\"success\":true,"
                            + "\"message\":\"Đã cập nhật số lượng\","
                            + "\"cartTotal\":%.2f,"
                            + "\"itemTotal\":%.2f,"
                            + "\"updatedQuantity\":%.2f,"
                            + "\"totalItems\":%d,"
                            + "\"itemCount\":%d}",
                            cartTotal,
                            itemTotal,
                            quantity,
                            totalItems,
                            activeItemsCount);
                    response.getWriter().write(json);
                    System.out.println("Response sent: " + json);
                } else {
                    response.sendRedirect(request.getContextPath() + "/cart");
                }

            } else {
                System.out.println("Failed to update cart item quantity");
                response.getWriter().write("{\"success\":false,\"message\":\"Không thể cập nhật số lượng\"}");
            }
        } catch (NumberFormatException e) {
            System.err.println("Number format exception: " + e.getMessage());
            e.printStackTrace();
            response.getWriter().write("{\"success\":false,\"message\":\"Dữ liệu không hợp lệ\"}");
        } catch (Exception e) {
            System.err.println("Error updating cart: " + e.getMessage());
            e.printStackTrace();
            String safeMessage = e.getMessage();
            if (safeMessage != null) {
                safeMessage = safeMessage.replace("\"", "'");
            } else {
                safeMessage = "Unknown error";
            }
            response.getWriter().write("{\"success\":false,\"message\":\"Lỗi: " + safeMessage + "\"}");
        }
    }

    /**
     * Remove item from cart
     */
    private void removeCartItem(HttpServletRequest request, HttpServletResponse response, CartItemDAO cartItemDAO)
            throws IOException {
        // Đảm bảo content-type được thiết lập ngay từ đầu
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Check if user is logged in
            HttpSession session = request.getSession();
            Account account = (Account) session.getAttribute("account");

            if (account == null) {
                response.getWriter()
                        .write("{\"success\":false,\"message\":\"Vui lòng đăng nhập để xóa sản phẩm khỏi giỏ hàng\"}");
                return;
            }

            // Check if user is a customer
            if (account.getRole() != 0) {
                response.getWriter().write(
                        "{\"success\":false,\"message\":\"Chỉ khách hàng mới có thể xóa sản phẩm khỏi giỏ hàng\"}");
                return;
            }

            int cartItemID = Integer.parseInt(request.getParameter("cartItemID"));

            // Get the cart item to check account ID for cart total calculation
            CartItem item = cartItemDAO.getCartItemById(cartItemID);
            if (item == null) {
                response.getWriter().write("{\"success\":false,\"message\":\"Không tìm thấy sản phẩm\"}");
                return;
            }

            // Store account ID for later use
            int accountID = item.getAccountID();

            // Remove item
            boolean success = cartItemDAO.updateCartItemStatus(cartItemID, "Removed");

            if (success) {
                // Calculate new cart total
                List<CartItem> activeItems = cartItemDAO.getCartItems(accountID, "Active");
                double cartTotal = 0;
                for (CartItem it : activeItems) {
                    if (it.getProduct() != null) {
                        cartTotal += it.getProduct().getPrice() * it.getQuantity();
                    }
                }
                int cartCount = cartItemDAO.countCartItems(accountID, "Active");

                // Ensure cartTotal is a valid number
                if (Double.isNaN(cartTotal)) {
                    cartTotal = 0;
                }

                // Get active cart items count (reuse the list fetched for total)
                int activeItemsCount = activeItems.size();

                // Return JSON with updated info - use raw numeric value instead of formatted
                // string
                String json = String.format(Locale.US,
                        "{\"success\":true,"
                        + "\"message\":\"Đã xóa sản phẩm khỏi giỏ hàng\","
                        + "\"cartTotal\":%.2f,"
                        + "\"cartCount\":%d,"
                        + "\"itemCount\":%d}",
                        cartTotal,
                        cartCount,
                        activeItemsCount);
                response.getWriter().write(json);
            } else {
                response.getWriter().write("{\"success\":false,\"message\":\"Không thể xóa sản phẩm\"}");
            }
        } catch (NumberFormatException e) {
            System.err.println("Number format exception: " + e.getMessage());
            response.getWriter().write("{\"success\":false,\"message\":\"ID sản phẩm không hợp lệ\"}");
        } catch (Exception e) {
            System.err.println("Error removing cart item: " + e.getMessage());
            e.printStackTrace();
            response.getWriter()
                    .write("{\"success\":false,\"message\":\"Lỗi: " + e.getMessage().replace("\"", "'") + "\"}");
        }
    }

    /**
     * Save item for later
     */
    private void saveForLater(HttpServletRequest request, HttpServletResponse response, CartItemDAO cartItemDAO)
            throws IOException {
        try {
            // Check if user is logged in
            HttpSession session = request.getSession();
            Account account = (Account) session.getAttribute("account");

            if (account == null) {
                handleAjaxError(request, response, "Vui lòng đăng nhập để lưu sản phẩm");
                return;
            }

            // Check if user is a customer
            if (account.getRole() != 0) {
                handleAjaxError(request, response, "Chỉ khách hàng mới có thể lưu sản phẩm");
                return;
            }

            int cartItemID = Integer.parseInt(request.getParameter("cartItemID"));
            cartItemDAO.updateCartItemStatus(cartItemID, "SavedForLater");
            response.sendRedirect("cart");
        } catch (NumberFormatException e) {
            response.sendRedirect("cart");
        }
    }

    /**
     * Move saved item to active cart
     */
    private void moveToCart(HttpServletRequest request, HttpServletResponse response, CartItemDAO cartItemDAO)
            throws IOException {
        // Check if this is an AJAX request
        String xRequestedWith = request.getHeader("X-Requested-With");
        boolean isAjax = "XMLHttpRequest".equals(xRequestedWith);

        // Thiết lập content-type cho AJAX request
        if (isAjax) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
        }

        try {
            // Check if user is logged in
            HttpSession session = request.getSession();
            Account account = (Account) session.getAttribute("account");

            if (account == null) {
                if (isAjax) {
                    response.getWriter()
                            .write("{\"success\":false,\"message\":\"Vui lòng đăng nhập để cập nhật giỏ hàng\"}");
                } else {
                    request.getSession().setAttribute("cartError", "Vui lòng đăng nhập để cập nhật giỏ hàng");
                    response.sendRedirect("cart");
                }
                return;
            }

            // Check if user is a customer
            if (account.getRole() != 0) {
                if (isAjax) {
                    response.getWriter()
                            .write("{\"success\":false,\"message\":\"Chỉ khách hàng mới có thể cập nhật giỏ hàng\"}");
                } else {
                    request.getSession().setAttribute("cartError", "Chỉ khách hàng mới có thể cập nhật giỏ hàng");
                    response.sendRedirect("cart");
                }
                return;
            }

            int cartItemID = Integer.parseInt(request.getParameter("cartItemID"));

            // Get the cart item to check product details
            CartItem item = cartItemDAO.getCartItemById(cartItemID);
            if (item == null) {
                if (isAjax) {
                    response.getWriter().write("{\"success\":false,\"message\":\"Không tìm thấy sản phẩm\"}");
                } else {
                    request.getSession().setAttribute("cartError", "Không tìm thấy sản phẩm");
                    response.sendRedirect("cart");
                }
                return;
            }

            // Check if stock is sufficient using ProductDAO directly
            ProductDAO productDAO = new ProductDAO();
            double stockQuantity = productDAO.getStockQuantityById(item.getProductID());

            if (stockQuantity < item.getQuantity()) {
                String errorMessage = "Không đủ số lượng trong kho. Hiện tại chỉ còn " + stockQuantity + " sản phẩm.";

                if (isAjax) {
                    response.getWriter().write("{\"success\":false,\"message\":\"" + errorMessage + "\"}");
                } else {
                    request.getSession().setAttribute("cartError", errorMessage);
                    response.sendRedirect("cart");
                }
                return;
            }

            boolean success = cartItemDAO.updateCartItemStatus(cartItemID, "Active");

            if (isAjax) {
                if (success) {
                    // Calculate new cart total
                    List<CartItem> activeItems = cartItemDAO.getCartItems(account.getAccountID(), "Active");
                    double cartTotal = 0;
                    for (CartItem it : activeItems) {
                        if (it.getProduct() != null) {
                            cartTotal += it.getProduct().getPrice() * it.getQuantity();
                        }
                    }
                    int cartCount = cartItemDAO.countCartItems(account.getAccountID(), "Active");

                    // Format cart total
                    java.text.DecimalFormat df = new java.text.DecimalFormat("#,###");
                    String formattedTotal = df.format(cartTotal) + " VNĐ";

                    // Return JSON with updated info
                    String json = String.format(
                            "{\"success\":true,"
                            + "\"message\":\"Đã chuyển sản phẩm vào giỏ hàng\","
                            + "\"cartTotal\":\"%s\","
                            + "\"cartCount\":%d}",
                            formattedTotal,
                            cartCount);
                    response.getWriter().write(json);
                } else {
                    response.getWriter()
                            .write("{\"success\":false,\"message\":\"Không thể chuyển sản phẩm vào giỏ hàng\"}");
                }
            } else {
                // For non-AJAX requests
                if (success) {
                    request.getSession().setAttribute("cartMessage", "Đã chuyển sản phẩm vào giỏ hàng");
                } else {
                    request.getSession().setAttribute("cartError", "Không thể chuyển sản phẩm vào giỏ hàng");
                }
                response.sendRedirect("cart");
            }
        } catch (NumberFormatException e) {
            if (isAjax) {
                response.getWriter().write("{\"success\":false,\"message\":\"ID sản phẩm không hợp lệ\"}");
            } else {
                request.getSession().setAttribute("cartError", "ID sản phẩm không hợp lệ");
                response.sendRedirect("cart");
            }
        } catch (Exception e) {
            if (isAjax) {
                String errorMessage = e.getMessage().replace("\"", "'");
                response.getWriter().write("{\"success\":false,\"message\":\"Lỗi: " + errorMessage + "\"}");
            } else {
                request.getSession().setAttribute("cartError", "Lỗi: " + e.getMessage());
                response.sendRedirect("cart");
            }
        }
    }

    /**
     * Clear all items from cart
     */
    private void clearCart(HttpServletRequest request, HttpServletResponse response, Account account, CartItemDAO cartItemDAO)
            throws IOException {
        // Check if user is a customer
        if (account == null || account.getRole() != 0) {
            request.getSession().setAttribute("cartError", "Chỉ khách hàng mới có thể xóa giỏ hàng");
            response.sendRedirect("cart");
            return;
        }

        cartItemDAO.updateCartItemsStatus(account.getAccountID(), "Active", "Removed");
        response.sendRedirect("cart");
    }

    /**
     * Remove selected items from cart
     */
    private void removeSelectedItems(HttpServletRequest request, HttpServletResponse response, Account account, CartItemDAO cartItemDAO)
            throws IOException {
        try {
            // Get the selected item IDs
            String selectedItems = request.getParameter("selectedItems");
            if (selectedItems == null || selectedItems.trim().isEmpty()) {
                request.getSession().setAttribute("cartError", "Không có sản phẩm nào được chọn để xóa");
                response.sendRedirect("cart");
                return;
            }
            
            // Split the comma-separated list of IDs
            String[] itemIds = selectedItems.split(",");
            int removedCount = 0;
            ProductDAO productDAO = new ProductDAO();
            
            // Remove each selected item
            for (String itemIdStr : itemIds) {
                try {
                    int itemId = Integer.parseInt(itemIdStr.trim());
                    
                    // Get the cart item to check product details before removal
                    CartItem item = cartItemDAO.getCartItemById(itemId);
                    if (item == null) {
                        continue;
                    }
                    
                    // Verify the item belongs to this user
                    if (item.getAccountID() != account.getAccountID()) {
                        continue;
                    }
                    
                    // Get product to restore stock
                    Product product = item.getProduct();
                    if (product == null) {
                        // If product info not in cart item, try to get it from database
                        product = productDAO.getProductById(item.getProductID());
                    }
                    
                    // Remove the item
                    boolean removed = cartItemDAO.updateCartItemStatus(itemId, "Removed");
                    if (removed && product != null) {
                        // Increment removed counter
                        removedCount++;
                    }
                } catch (NumberFormatException e) {
                    // Skip invalid IDs
                    continue;
                }
            }
            
            // Set appropriate message
            if (removedCount > 0) {
                request.getSession().setAttribute("cartMessage", "Đã xóa " + removedCount + " sản phẩm khỏi giỏ hàng");
            } else {
                request.getSession().setAttribute("cartError", "Không thể xóa sản phẩm khỏi giỏ hàng");
            }
            
            // Redirect back to cart
            response.sendRedirect("cart");
            
        } catch (Exception e) {
            request.getSession().setAttribute("cartError", "Lỗi khi xóa sản phẩm: " + e.getMessage());
            response.sendRedirect("cart");
        }
    }
    
    /**
     * Helper method to handle AJAX errors
     */
    private void handleAjaxError(HttpServletRequest request, HttpServletResponse response, String message)
            throws IOException {
        // Check if this is an AJAX request
        String xRequestedWith = request.getHeader("X-Requested-With");
        if ("XMLHttpRequest".equals(xRequestedWith)) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"success\":false,\"message\":\"" + message + "\"}");
        } else {
            // For non-AJAX requests, set session error and redirect
            request.getSession().setAttribute("cartError", message);
            response.sendRedirect("cart");
        }
    }
}
