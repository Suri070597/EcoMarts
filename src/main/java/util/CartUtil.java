package util;

import java.util.List;

import dao.CartItemDAO;
import model.CartItem;

/**
 * Utility class for shopping cart operations
 * Provides methods to manage cart items for customers
 */
public class CartUtil {
    
    private CartItemDAO cartItemDAO;
    
    public CartUtil() {
        cartItemDAO = new CartItemDAO();
    }
    
    /**
     * Add a product to the user's cart
     * 
     * @param accountID The ID of the account
     * @param productID The ID of the product to add
     * @param quantity The quantity to add
     * @return true if successfully added, false otherwise
     */
    public boolean addToCart(int accountID, int productID, int quantity) {
        // Check if product exists in the cart already
        CartItem existingItem = cartItemDAO.getCartItemByProductId(accountID, productID, "Active");
        
        if (existingItem != null) {
            // Update quantity if the product is already in the cart
            return updateCartItemQuantity(existingItem.getCartItemID(), existingItem.getQuantity() + quantity);
        } else {
            // Add new item to cart
            return cartItemDAO.addToCart(accountID, productID, quantity);
        }
    }
    
    /**
     * Get all cart items for a specific user with a specific status
     * 
     * @param accountID The ID of the account
     * @param status The status of cart items to retrieve (Active, SavedForLater, Removed)
     * @return List of CartItem objects
     */
    public List<CartItem> getCartItems(int accountID, String status) {
        return cartItemDAO.getCartItems(accountID, status);
    }
    
    /**
     * Get a specific cart item by its ID
     * 
     * @param cartItemID The ID of the cart item
     * @return CartItem object or null if not found
     */
    public CartItem getCartItemById(int cartItemID) {
        return cartItemDAO.getCartItemById(cartItemID);
    }
    
    /**
     * Get a cart item by product ID for a specific user and status
     * 
     * @param accountID The ID of the account
     * @param productID The ID of the product
     * @param status The status to check for (Active, SavedForLater, Removed)
     * @return CartItem object or null if not found
     */
    public CartItem getCartItemByProductId(int accountID, int productID, String status) {
        return cartItemDAO.getCartItemByProductId(accountID, productID, status);
    }
    
    /**
     * Update the quantity of a cart item
     * 
     * @param cartItemID The ID of the cart item
     * @param newQuantity The new quantity
     * @return true if successfully updated, false otherwise
     */
    public boolean updateCartItemQuantity(int cartItemID, int newQuantity) {
        if (newQuantity <= 0) {
            // If quantity is 0 or negative, remove the item from cart
            return removeCartItem(cartItemID);
        }
        
        return cartItemDAO.updateCartItemQuantity(cartItemID, newQuantity);
    }
    
    /**
     * Remove a cart item (set status to 'Removed')
     * 
     * @param cartItemID The ID of the cart item
     * @return true if successfully removed, false otherwise
     */
    public boolean removeCartItem(int cartItemID) {
        return cartItemDAO.updateCartItemStatus(cartItemID, "Removed");
    }
    
    /**
     * Save a cart item for later (set status to 'SavedForLater')
     * 
     * @param cartItemID The ID of the cart item
     * @return true if successfully saved for later, false otherwise
     */
    public boolean saveForLater(int cartItemID) {
        return cartItemDAO.updateCartItemStatus(cartItemID, "SavedForLater");
    }
    
    /**
     * Move a saved item back to active cart
     * 
     * @param cartItemID The ID of the cart item
     * @return true if successfully moved to cart, false otherwise
     */
    public boolean moveToCart(int cartItemID) {
        return cartItemDAO.updateCartItemStatus(cartItemID, "Active");
    }
    
    /**
     * Clear all items from a user's cart
     * 
     * @param accountID The ID of the account
     * @param status The status of items to clear (Active, SavedForLater, Removed)
     * @return true if successfully cleared, false otherwise
     */
    public boolean clearCart(int accountID, String status) {
        return cartItemDAO.updateCartItemsStatus(accountID, status, "Removed");
    }
    
    /**
     * Calculate the total price of all active items in a user's cart
     * 
     * @param accountID The ID of the account
     * @return The total price of all items
     */
    public double calculateCartTotal(int accountID) {
        double total = 0;
        List<CartItem> cartItems = cartItemDAO.getCartItems(accountID, "Active");
        
        for (CartItem item : cartItems) {
            if (item.getProduct() != null) {
                total += item.getProduct().getPrice() * item.getQuantity();
            }
        }
        
        return total;
    }
    
    /**
     * Count the number of active items in a user's cart
     * 
     * @param accountID The ID of the account
     * @return The number of items
     */
    public int getCartItemCount(int accountID) {
        System.out.println("CartUtil.getCartItemCount called for accountID: " + accountID);
        int count = cartItemDAO.countCartItems(accountID, "Active");
        System.out.println("CartUtil.getCartItemCount returned: " + count);
        return count;
    }
    
    /**
     * Check if the product stock quantity is sufficient for the requested quantity
     * 
     * @param productID The ID of the product
     * @param requestedQuantity The quantity requested
     * @return true if stock is sufficient, false otherwise
     */
    public boolean isStockSufficient(int productID, int requestedQuantity) {
        return cartItemDAO.isStockSufficient(productID, requestedQuantity);
    }
} 