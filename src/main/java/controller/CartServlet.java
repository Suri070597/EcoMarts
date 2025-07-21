package controller;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import dao.CategoryDAO;
import dao.ProductDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Account;
import model.CartItem;
import model.Category;
import util.CartUtil;

/**
 * Servlet for handling shopping cart operations
 */
@WebServlet(name = "CartServlet", urlPatterns = { "/cart" })
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

        // Initialize cart utility
        CartUtil cartUtil = new CartUtil();

        // Check if this is a count request
        String action = request.getParameter("action");
        if ("count".equals(action)) {
            getCartCount(request, response, account, cartUtil);
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
        List<CartItem> activeItems = cartUtil.getCartItems(account.getAccountID(), "Active");
        List<CartItem> savedItems = cartUtil.getCartItems(account.getAccountID(), "Saved");

        System.out.println(
                "Cart page request: Found " + activeItems.size() + " active items for user " + account.getUsername());

        // Calculate cart total
        double cartTotal = cartUtil.calculateCartTotal(account.getAccountID());

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
            CartUtil cartUtil)
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
        int count = cartUtil.getCartItemCount(account.getAccountID());
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

        CartUtil cartUtil = new CartUtil();

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
                addToCart(request, response, account, cartUtil);
                break;

            case "update":
                updateCartItem(request, response, cartUtil);
                break;

            case "remove":
                removeCartItem(request, response, cartUtil);
                break;

            case "saveForLater":
                saveForLater(request, response, cartUtil);
                break;

            case "moveToCart":
                moveToCart(request, response, cartUtil);
                break;

            case "clear":
                clearCart(request, response, account, cartUtil);
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
            Account account, CartUtil cartUtil) throws IOException {
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

            // Validate quantity
            if (quantity <= 0) {
                quantity = 1;
            }

            // Get the actual stock quantity
            ProductDAO productDAO = new ProductDAO();
            double stockQuantity = productDAO.getStockQuantityById(productID);

            // Check if stock is sufficient
            if (stockQuantity < quantity) {
                if (isAjax) {
                    String errorMessage = "Không đủ số lượng trong kho. Hiện tại chỉ còn " + stockQuantity
                            + " sản phẩm.";
                    response.getWriter().write("{\"success\":false,\"message\":\"" + errorMessage + "\"}");
                    return;
                }

                request.getSession().setAttribute("cartError",
                        "Không đủ số lượng trong kho. Hiện tại chỉ còn " + stockQuantity + " sản phẩm.");
                response.sendRedirect("ProductDetail?id=" + productID);
                return;
            }

            // Add to cart
            boolean success = cartUtil.addToCart(account.getAccountID(), productID, quantity);

            if (isAjax) {
                if (success) {
                    // Tính toán số lượng sản phẩm trong giỏ hàng
                    int cartCount = cartUtil.getCartItemCount(account.getAccountID());

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
    private void updateCartItem(HttpServletRequest request, HttpServletResponse response, CartUtil cartUtil)
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
            CartItem cartItem = cartUtil.getCartItemById(cartItemID);
            if (cartItem == null) {
                System.out.println("Cart item not found: " + cartItemID);
                response.getWriter()
                        .write("{\"success\":false,\"message\":\"Không tìm thấy sản phẩm trong giỏ hàng\"}");
                return;
            }

            // Get current quantity in cart
            double currentQuantity = cartItem.getQuantity();

            // Check if stock is sufficient using ProductDAO directly
            ProductDAO productDAO = new ProductDAO();
            double stockQuantity = productDAO.getStockQuantityById(cartItem.getProductID());

            System.out.println("Updating cart item: ID=" + cartItemID + ", Current quantity=" + currentQuantity +
                    ", New quantity=" + quantity + ", Stock quantity=" + stockQuantity);

            // Only validate stock if increasing quantity
            // Always allow decreasing quantity even if current quantity exceeds stock
            if (quantity > currentQuantity && stockQuantity < quantity) {
                String errorMessage = "Không đủ số lượng trong kho. Hiện tại chỉ còn " + stockQuantity + " sản phẩm.";
                System.out.println("Stock insufficient: " + errorMessage);
                String json = String.format(
                        "{\"success\":false," +
                                "\"message\":\"%s\"," +
                                "\"validQuantity\":%d}",
                        errorMessage,
                        Math.min(currentQuantity, stockQuantity) // Ensure valid quantity doesn't exceed stock
                );
                response.getWriter().write(json);
                return;
            }

            // Update cart item quantity
            boolean success = cartUtil.updateCartItemQuantity(cartItemID, quantity);
            System.out.println("Update result: " + (success ? "success" : "failed"));

            // Get updated cart item to calculate total
            CartItem updatedItem = cartUtil.getCartItemById(cartItemID);
            double itemTotal = 0;
            if (updatedItem != null && updatedItem.getProduct() != null) {
                itemTotal = updatedItem.getProduct().getPrice() * updatedItem.getQuantity();
            }
            // Làm tròn itemTotal về nghìn đồng
            long roundedItemTotal = Math.round(itemTotal / 1000.0) * 1000;

            if (success) {
                // Calculate new cart total
                double cartTotal = cartUtil.calculateCartTotal(account.getAccountID());
                // Làm tròn cartTotal về nghìn đồng
                long roundedCartTotal = Math.round(cartTotal / 1000.0) * 1000;
                int totalItems = cartUtil.getCartItemCount(account.getAccountID());

                System.out.println(
                        "Cart update successful: Item total=" + roundedItemTotal + ", Cart total=" + roundedCartTotal +
                                ", Total items=" + totalItems);

                // Ensure values are valid numbers and properly formatted for JSON
                if (Double.isNaN(cartTotal))
                    cartTotal = 0;
                if (Double.isNaN(itemTotal))
                    itemTotal = 0;

                // Get active cart items count
                List<CartItem> activeItems = cartUtil.getCartItems(account.getAccountID(), "Active");
                int activeItemsCount = activeItems.size();

                // Return JSON with updated information
                String json = String.format(java.util.Locale.US,
                        "{\"success\":true," +
                                "\"message\":\"Đã cập nhật số lượng\"," +
                                "\"cartTotal\":%d," +
                                "\"itemTotal\":%d," +
                                "\"updatedQuantity\":%.2f," +
                                "\"totalItems\":%d," +
                                "\"itemCount\":%d}",
                        roundedCartTotal,
                        roundedItemTotal,
                        quantity,
                        totalItems,
                        activeItemsCount);
                response.getWriter().write(json);
                System.out.println("Response sent: " + json);
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
    private void removeCartItem(HttpServletRequest request, HttpServletResponse response, CartUtil cartUtil)
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
            CartItem item = cartUtil.getCartItemById(cartItemID);
            if (item == null) {
                response.getWriter().write("{\"success\":false,\"message\":\"Không tìm thấy sản phẩm\"}");
                return;
            }

            // Store account ID for later use
            int accountID = item.getAccountID();

            // Remove item
            boolean success = cartUtil.removeCartItem(cartItemID);

            if (success) {
                // Calculate new cart total
                double cartTotal = cartUtil.calculateCartTotal(accountID);
                int cartCount = cartUtil.getCartItemCount(accountID);

                // Ensure cartTotal is a valid number
                if (Double.isNaN(cartTotal))
                    cartTotal = 0;

                // Get active cart items count
                List<CartItem> activeItems = cartUtil.getCartItems(accountID, "Active");
                int activeItemsCount = activeItems.size();

                // Return JSON with updated info - use raw numeric value instead of formatted
                // string
                String json = String.format(Locale.US,
                        "{\"success\":true," +
                                "\"message\":\"Đã xóa sản phẩm khỏi giỏ hàng\"," +
                                "\"cartTotal\":%.2f," +
                                "\"cartCount\":%d," +
                                "\"itemCount\":%d}",
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
    private void saveForLater(HttpServletRequest request, HttpServletResponse response, CartUtil cartUtil)
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
            cartUtil.saveForLater(cartItemID);
            response.sendRedirect("cart");
        } catch (NumberFormatException e) {
            response.sendRedirect("cart");
        }
    }

    /**
     * Move saved item to active cart
     */
    private void moveToCart(HttpServletRequest request, HttpServletResponse response, CartUtil cartUtil)
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
            CartItem item = cartUtil.getCartItemById(cartItemID);
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

            boolean success = cartUtil.moveToCart(cartItemID);

            if (isAjax) {
                if (success) {
                    // Calculate new cart total
                    double cartTotal = cartUtil.calculateCartTotal(account.getAccountID());
                    int cartCount = cartUtil.getCartItemCount(account.getAccountID());

                    // Format cart total
                    java.text.DecimalFormat df = new java.text.DecimalFormat("#,###");
                    String formattedTotal = df.format(cartTotal) + " VNĐ";

                    // Return JSON with updated info
                    String json = String.format(
                            "{\"success\":true," +
                                    "\"message\":\"Đã chuyển sản phẩm vào giỏ hàng\"," +
                                    "\"cartTotal\":\"%s\"," +
                                    "\"cartCount\":%d}",
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
    private void clearCart(HttpServletRequest request, HttpServletResponse response, Account account, CartUtil cartUtil)
            throws IOException {
        // Check if user is a customer
        if (account == null || account.getRole() != 0) {
            request.getSession().setAttribute("cartError", "Chỉ khách hàng mới có thể xóa giỏ hàng");
            response.sendRedirect("cart");
            return;
        }

        cartUtil.clearCart(account.getAccountID(), "Active");
        response.sendRedirect("cart");
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
