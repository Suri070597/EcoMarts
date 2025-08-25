package controller;

import dao.ProductDAO;
import model.Product;
import model.Category;
import model.Manufacturer;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@WebServlet(name = "ProductServlet", urlPatterns = { "/admin/product" })
@MultipartConfig(fileSizeThreshold = 1024 * 1024, // 1 MB
        maxFileSize = 1024 * 1024 * 10, // 10 MB
        maxRequestSize = 1024 * 1024 * 50 // 50 MB
)
public class ProductServlet extends HttpServlet {

    private static final String IMAGE_UPLOAD_DIR = "C:\\Users\\LNQB\\Downloads\\Project_List-123456-main\\EcoMarts\\src\\main\\webapp\\images\\products";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ProductDAO dao = new ProductDAO();
        String action = request.getParameter("action");

        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "list":
                try {
                    List<Product> products = dao.getAllIncludingOutOfStock();
                    List<Category> categories = dao.getCategory();

                    // Counters for stock status cards
                    final int LOW_STOCK_THRESHOLD = 10;
                    int inStock = dao.countInStock(LOW_STOCK_THRESHOLD);
                    int lowStock = dao.countLowStock(LOW_STOCK_THRESHOLD);
                    int outOfStock = dao.countOutOfStock();

                    request.setAttribute("products", products);
                    request.setAttribute("dataCate", categories);
                    request.setAttribute("inStockCount", inStock);
                    request.setAttribute("lowStockCount", lowStock);
                    request.setAttribute("outOfStockCount", outOfStock);

                    request.getRequestDispatcher("/WEB-INF/admin/product/product.jsp").forward(request, response);
                } catch (Exception e) {
                    e.printStackTrace();
                    response.sendRedirect(request.getContextPath() + "/admin/product");
                }
                break;

            case "create":
                try {
                    request.setAttribute("dataCate", dao.getCategory());
                    request.setAttribute("dataSup", dao.getActiveManufacturers());
                    request.getRequestDispatcher("/WEB-INF/admin/product/create-product.jsp").forward(request,
                            response);
                } catch (Exception e) {
                    e.printStackTrace();
                    response.sendRedirect(request.getContextPath() + "/admin/product");
                }
                break;

            case "edit":
                try {
                    int productId = Integer.parseInt(request.getParameter("id"));
                    Product product = dao.getProductById(productId);
                    if (product != null) {
                        request.setAttribute("product", product);
                        request.setAttribute("dataCate", dao.getCategory());
                        request.setAttribute("dataSup", dao.getActiveManufacturers());
                        request.getRequestDispatcher("/WEB-INF/admin/product/edit-product.jsp").forward(request,
                                response);
                    } else {
                        response.sendRedirect(request.getContextPath() + "/admin/product");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    response.sendRedirect(request.getContextPath() + "/admin/product");
                }
                break;

            case "update":
                try {
                    int productId = Integer.parseInt(request.getParameter("id"));
                    Product product = dao.getProductById(productId);
                    if (product != null) {
                        request.setAttribute("product", product);
                        request.setAttribute("dataCate", dao.getCategory());
                        request.setAttribute("dataSup", dao.getActiveManufacturers());
                        request.getRequestDispatcher("/WEB-INF/admin/product/edit-product.jsp").forward(request,
                                response);
                    } else {
                        response.sendRedirect(request.getContextPath() + "/admin/product");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    response.sendRedirect(request.getContextPath() + "/admin/product");
                }
                break;

            case "delete":
                try {
                    int productId = Integer.parseInt(request.getParameter("id"));
                    boolean success = dao.delete(productId);
                    if (success) {
                        response.sendRedirect(request.getContextPath() + "/admin/product?message=deleted");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/admin/product?message=delete_failed");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    response.sendRedirect(request.getContextPath() + "/admin/product");
                }
                break;

            case "search":
                try {
                    String keyword = request.getParameter("keyword");
                    List<Product> searchResults = dao.searchProductsByName(keyword);
                    List<Category> categories = dao.getCategory();

                    // Counters for stock status cards based on search results
                    final int SEARCH_LOW_STOCK_THRESHOLD = 10;
                    int searchInStock = 0, searchLowStock = 0, searchOutOfStock = 0;

                    for (Product p : searchResults) {
                        Map<String, Object> productInventory = dao.getProductInventory(p.getProductID());
                        double stock = 0;
                        if (productInventory != null && productInventory.containsKey("BOX_Quantity")) {
                            stock = (Double) productInventory.get("BOX_Quantity");
                        }
                        if (stock > SEARCH_LOW_STOCK_THRESHOLD) {
                            searchInStock++;
                        } else if (stock > 0) {
                            searchLowStock++;
                        } else {
                            searchOutOfStock++;
                        }
                    }

                    request.setAttribute("products", searchResults);
                    request.setAttribute("dataCate", categories);
                    request.setAttribute("keyword", keyword);
                    request.setAttribute("inStockCount", searchInStock);
                    request.setAttribute("lowStockCount", searchLowStock);
                    request.setAttribute("outOfStockCount", searchOutOfStock);

                    request.getRequestDispatcher("/WEB-INF/admin/product/product.jsp").forward(request, response);
                } catch (Exception e) {
                    e.printStackTrace();
                    response.sendRedirect(request.getContextPath() + "/admin/product");
                }
                break;

            case "detail":
                try {
                    int productId = Integer.parseInt(request.getParameter("id"));
                    Product productDetail = dao.getProductById(productId);
                    Map<String, Object> inventory = dao.getProductInventory(productId);
                    request.setAttribute("productDetail", productDetail);
                    request.setAttribute("inventory", inventory);
                    request.getRequestDispatcher("/WEB-INF/admin/product/product-detail.jsp").forward(request,
                            response);
                } catch (Exception e) {
                    e.printStackTrace();
                    response.sendRedirect(request.getContextPath() + "/admin/product");
                }
                break;

            case "set-price":
                try {
                    String idStr = request.getParameter("id");
                    System.out.println("GET set-price - ID parameter: " + idStr);

                    if (idStr != null && !idStr.trim().isEmpty()) {
                        int idConvert = Integer.parseInt(idStr);
                        System.out.println("GET set-price - Parsed ID: " + idConvert);

                        Product product = dao.getProductById(idConvert);
                        System.out.println("GET set-price - Product found: "
                                + (product != null ? product.getProductName() : "NULL"));

                        if (product != null) {
                            // Lấy thông tin inventory cho sản phẩm
                            Map<String, Object> inventoryData = dao.getProductInventory(idConvert);
                            request.setAttribute("inventoryData", inventoryData);

                            // Lấy danh sách các lô hàng
                            List<Map<String, Object>> lots = dao.getProductLots(idConvert);
                            request.setAttribute("lots", lots);

                            request.setAttribute("productPrice", product);
                            request.getRequestDispatcher("/WEB-INF/admin/product/set-price.jsp").forward(request,
                                    response);
                        } else {
                            response.sendRedirect(request.getContextPath() + "/admin/product?error=product_not_found");
                        }
                    } else {
                        response.sendRedirect(request.getContextPath() + "/admin/product?error=invalid_id");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    response.sendRedirect(request.getContextPath() + "/admin/product?error=server_error");
                }
                break;

            case "convert":
                try {
                    String idStr = request.getParameter("id");
                    if (idStr != null && !idStr.trim().isEmpty()) {
                        int idConvert = Integer.parseInt(idStr);
                        Product productConvert = dao.getProductById(idConvert);

                        if (productConvert != null) {
                            // Lấy danh sách các lô BOX có sẵn
                            List<Map<String, Object>> availableLots = dao.getAvailableBoxLots(idConvert);

                            request.setAttribute("productConvert", productConvert);
                            request.setAttribute("availableLots", availableLots);
                            request.getRequestDispatcher("/WEB-INF/admin/product/convert.jsp").forward(request,
                                    response);
                        } else {
                            response.sendRedirect(request.getContextPath() + "/admin/product?error=product_not_found");
                        }
                    } else {
                        response.sendRedirect(request.getContextPath() + "/admin/product?error=invalid_id");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    response.sendRedirect(request.getContextPath() + "/admin/product?error=server_error");
                }
                break;

            default:
                response.sendRedirect(request.getContextPath() + "/admin/product");
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        ProductDAO dao = new ProductDAO();
        String action = request.getParameter("action");
        PrintWriter out = response.getWriter();
        if (action == null) {
            response.sendRedirect("/admin/product");
            return;
        }

        switch (action) {
            case "create":
                try {
                    String pName = request.getParameter("pName");
                    String pDescription = request.getParameter("pDescription");
                    String categoryIDStr = request.getParameter("categoryID");
                    String manufacturerIDStr = request.getParameter("manufacturerID");
                    // Validate required fields
                    if (pName == null || pName.trim().isEmpty()) {
                        request.setAttribute("error", "Vui lòng nhập tên sản phẩm.");
                        request.setAttribute("dataCate", dao.getCategory());
                        request.setAttribute("dataSup", dao.getActiveManufacturers());
                        request.getRequestDispatcher("/WEB-INF/admin/product/create-product.jsp").forward(request,
                                response);
                        return;
                    }
                    if (categoryIDStr == null || categoryIDStr.trim().isEmpty()) {
                        request.setAttribute("error", "Vui lòng chọn danh mục sản phẩm.");
                        request.setAttribute("dataCate", dao.getCategory());
                        request.setAttribute("dataSup", dao.getActiveManufacturers());
                        request.getRequestDispatcher("/WEB-INF/admin/product/create-product.jsp").forward(request,
                                response);
                        return;
                    }
                    if (manufacturerIDStr == null || manufacturerIDStr.trim().isEmpty()) {
                        request.setAttribute("error", "Vui lòng chọn nhà cung cấp.");
                        request.setAttribute("dataCate", dao.getCategory());
                        request.setAttribute("dataSup", dao.getActiveManufacturers());
                        request.getRequestDispatcher("/WEB-INF/admin/product/create-product.jsp").forward(request,
                                response);
                        return;
                    }

                    int categoryID = Integer.parseInt(categoryIDStr);
                    int manufacturerID = Integer.parseInt(manufacturerIDStr);
                    // Xử lý ảnh
                    Part filePart = request.getPart("pImage");
                    String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                    if (!fileName.isEmpty()) {
                        File uploadDir = new File(IMAGE_UPLOAD_DIR);
                        if (!uploadDir.exists()) {
                            uploadDir.mkdirs();
                        }
                        filePart.write(IMAGE_UPLOAD_DIR + File.separator + fileName);
                    }
                    String pImage = fileName;
                    Timestamp createdAt = new Timestamp(System.currentTimeMillis());

                    // Đặt giá mặc định là NULL - sẽ được thiết lập riêng biệt
                    Double price = null;

                    // Lấy thông tin đơn vị
                    int unitPerBox = Integer.parseInt(request.getParameter("unitPerBox"));
                    String boxUnitName = request.getParameter("boxUnitName");
                    String itemUnitName = request.getParameter("itemUnitName");
                    int res = dao.insert(pName, price, pDescription, pImage,
                            createdAt, categoryID, manufacturerID,
                            unitPerBox, boxUnitName, itemUnitName);

                    if (res == 1) {
                        response.sendRedirect(request.getContextPath() + "/admin/product");
                        return;
                    } else {
                        request.setAttribute("error", "❌ Thêm sản phẩm thất bại.");
                        request.setAttribute("dataCate", dao.getCategory());
                        request.setAttribute("dataSup", dao.getActiveManufacturers());
                        request.getRequestDispatcher("/WEB-INF/admin/product/create-product.jsp").forward(request,
                                response);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    request.setAttribute("error", "❌ Đã xảy ra lỗi khi tạo sản phẩm: " + e.getMessage());
                    request.setAttribute("dataCate", dao.getCategory());
                    request.setAttribute("dataSup", dao.getActiveManufacturers());
                    request.getRequestDispatcher("/WEB-INF/admin/product/create-product.jsp").forward(request,
                            response);
                }
                break;

            case "edit":
                try {
                    int productId = Integer.parseInt(request.getParameter("productId"));
                    String pName = request.getParameter("pName");
                    String pDescription = request.getParameter("pDescription");
                    String categoryIDStr = request.getParameter("categoryID");
                    String manufacturerIDStr = request.getParameter("manufacturerID");

                    // Validate required fields
                    if (pName == null || pName.trim().isEmpty()) {
                        request.setAttribute("error", "Vui lòng nhập tên sản phẩm.");
                        Product product = dao.getProductById(productId);
                        request.setAttribute("product", product);
                        request.setAttribute("dataCate", dao.getCategory());
                        request.setAttribute("dataSup", dao.getActiveManufacturers());
                        request.getRequestDispatcher("/WEB-INF/admin/product/edit-product.jsp").forward(request,
                                response);
                        return;
                    }

                    int categoryID = Integer.parseInt(categoryIDStr);
                    int manufacturerID = Integer.parseInt(manufacturerIDStr);

                    // Xử lý ảnh
                    Part filePart = request.getPart("pImage");
                    String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                    String pImage = null;
                    if (!fileName.isEmpty()) {
                        File uploadDir = new File(IMAGE_UPLOAD_DIR);
                        if (!uploadDir.exists()) {
                            uploadDir.mkdirs();
                        }
                        filePart.write(IMAGE_UPLOAD_DIR + File.separator + fileName);
                        pImage = fileName;
                    }

                    // Lấy thông tin đơn vị
                    int unitPerBox = Integer.parseInt(request.getParameter("unitPerBox"));
                    String boxUnitName = request.getParameter("boxUnitName");
                    String itemUnitName = request.getParameter("itemUnitName");

                    // Tạo Product object để update
                    Product productToUpdate = new Product(productId, pName, null, pDescription, pImage, null);
                    productToUpdate.setUnitPerBox(unitPerBox);
                    productToUpdate.setBoxUnitName(boxUnitName);
                    productToUpdate.setItemUnitName(itemUnitName);

                    // Set category và manufacturer
                    Category category = new Category();
                    category.setCategoryID(categoryID);
                    productToUpdate.setCategory(category);

                    Manufacturer manufacturer = new Manufacturer();
                    manufacturer.setManufacturerID(manufacturerID);
                    productToUpdate.setManufacturer(manufacturer);

                    boolean success = dao.update(productToUpdate);

                    if (success) {
                        response.sendRedirect(request.getContextPath() + "/admin/product?message=updated");
                    } else {
                        request.setAttribute("error", "❌ Cập nhật sản phẩm thất bại.");
                        Product product = dao.getProductById(productId);
                        request.setAttribute("product", product);
                        request.setAttribute("dataCate", dao.getCategory());
                        request.setAttribute("dataSup", dao.getActiveManufacturers());
                        request.getRequestDispatcher("/WEB-INF/admin/product/edit-product.jsp").forward(request,
                                response);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    request.setAttribute("error", "❌ Đã xảy ra lỗi khi cập nhật sản phẩm: " + e.getMessage());
                    Product product = dao.getProductById(Integer.parseInt(request.getParameter("productId")));
                    request.setAttribute("product", product);
                    request.setAttribute("dataCate", dao.getCategory());
                    request.setAttribute("dataSup", dao.getActiveManufacturers());
                    request.getRequestDispatcher("/WEB-INF/admin/product/edit-product.jsp").forward(request,
                            response);
                }
                break;

            case "set-reference-price":
                try {
                    int productId = Integer.parseInt(request.getParameter("productId"));
                    String referencePriceStr = request.getParameter("referencePrice");

                    if (referencePriceStr == null || referencePriceStr.trim().isEmpty()) {
                        response.sendRedirect(request.getContextPath() + "/admin/product?action=set-price&id="
                                + productId + "&error=invalid_reference_price");
                        return;
                    }

                    double referencePrice = Double.parseDouble(referencePriceStr);
                    if (referencePrice < 0) {
                        response.sendRedirect(request.getContextPath() + "/admin/product?action=set-price&id="
                                + productId + "&error=invalid_reference_price");
                        return;
                    }

                    boolean updated = dao.updateProductReferencePrice(productId, referencePrice);
                    if (updated) {
                        response.sendRedirect(request.getContextPath() + "/admin/product?action=set-price&id="
                                + productId + "&success=reference_price_updated");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/admin/product?action=set-price&id="
                                + productId + "&error=reference_price_update_failed");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    int productId = -1;
                    try {
                        productId = Integer.parseInt(request.getParameter("productId"));
                    } catch (Exception ignore) {
                    }
                    String redirect = request.getContextPath() + "/admin/product?action=set-price";
                    if (productId > -1) {
                        redirect += "&id=" + productId + "&error=reference_price_error";
                    } else {
                        redirect += "&error=reference_price_error";
                    }
                    response.sendRedirect(redirect);
                }
                break;

            case "set-price":
                try {
                    System.out.println("Processing set-price POST request");

                    int productId = Integer.parseInt(request.getParameter("productId"));
                    String sellingPriceStr = request.getParameter("sellingPrice");
                    String inventoryIdStr = request.getParameter("inventoryId");

                    System.out.println("Product ID: " + productId + ", Selling Price: " + sellingPriceStr
                            + ", Inventory ID: " + inventoryIdStr);

                    if (sellingPriceStr == null || sellingPriceStr.trim().isEmpty()) {
                        response.sendRedirect(request.getContextPath() + "/admin/product?action=set-price&id="
                                + productId + "&error=invalid_price");
                        return;
                    }

                    double sellingPrice = Double.parseDouble(sellingPriceStr);

                    // Lấy thông tin sản phẩm để kiểm tra
                    System.out.println("Getting product info for ID: " + productId);
                    Product product = dao.getProductById(productId);
                    if (product == null) {
                        response.sendRedirect(request.getContextPath() + "/admin/product?action=set-price&id="
                                + productId + "&error=product_not_found");
                        return;
                    }

                    System.out.println("Product found: " + product.getProductName());

                    // Lấy thông tin inventory
                    Map<String, Object> inventoryData = dao.getProductInventory(productId);
                    System.out.println("Inventory data: " + inventoryData);
                    List<Map<String, Object>> lots = dao.getProductLots(productId);
                    System.out.println("Lots count: " + lots.size());

                    boolean updateSuccess = false;

                    if (inventoryIdStr != null && !inventoryIdStr.trim().isEmpty()) {
                        // Cập nhật giá cho lô cụ thể
                        int inventoryId = Integer.parseInt(inventoryIdStr);
                        System.out.println("Updating price for specific lot: " + inventoryId);

                        // Lấy cost price của lô này
                        Object costPriceObj = null;
                        for (Map<String, Object> lot : lots) {
                            if (lot.get("inventoryID").equals(inventoryId)) {
                                costPriceObj = lot.get("costPrice");
                                break;
                            }
                        }

                        System.out.println("DEBUG: costPriceObj type: "
                                + (costPriceObj != null ? costPriceObj.getClass().getName() : "null"));
                        System.out.println("DEBUG: costPriceObj value: " + costPriceObj);

                        double costPrice = 0.0;
                        if (costPriceObj != null) {
                            if (costPriceObj instanceof BigDecimal) {
                                costPrice = ((BigDecimal) costPriceObj).doubleValue();
                            } else if (costPriceObj instanceof Double) {
                                costPrice = (Double) costPriceObj;
                            } else if (costPriceObj instanceof Number) {
                                costPrice = ((Number) costPriceObj).doubleValue();
                            }
                        }

                        System.out.println("DEBUG: Converted BigDecimal to double: " + costPrice);
                        System.out.println("Selling price: " + sellingPrice + ", Cost price: " + costPrice);

                        updateSuccess = dao.updateLotPrice(inventoryId, sellingPrice);
                        System.out.println("updateLotPrice result: " + updateSuccess);

                    } else {
                        // Cập nhật giá cho tất cả các lô
                        System.out.println("Updating price for all lots");
                        updateSuccess = dao.updateAllLotPrices(productId, sellingPrice);
                    }

                    if (updateSuccess) {
                        System.out.println("Price update successful, redirecting back to set-price page");
                        String redirectUrl = request.getContextPath() + "/admin/product?action=set-price&id="
                                + productId + "&success=price_updated";
                        if (inventoryIdStr != null && !inventoryIdStr.trim().isEmpty()) {
                            redirectUrl += "&inventoryId=" + inventoryIdStr;
                        }
                        System.out.println("Redirecting to: " + redirectUrl);
                        response.sendRedirect(redirectUrl);
                    } else {
                        response.sendRedirect(request.getContextPath() + "/admin/product?action=set-price&id="
                                + productId + "&error=update_failed");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    int productId = Integer.parseInt(request.getParameter("productId"));
                    response.sendRedirect(request.getContextPath() + "/admin/product?action=set-price&id=" + productId
                            + "&error=server_error");
                }
                break;

            case "convertUnits":
                try {
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    PrintWriter jsonOut = response.getWriter();

                    int productId = Integer.parseInt(request.getParameter("productId"));
                    int boxesToConvert = Integer.parseInt(request.getParameter("boxesToConvert"));
                    String conversionType = request.getParameter("conversionType");
                    int packSize = 0;
                    String packSizeStr = request.getParameter("packSize");
                    if (packSizeStr != null && !packSizeStr.trim().isEmpty()) {
                        packSize = Integer.parseInt(packSizeStr);
                    }

                    // Thêm tham số để chọn lô cụ thể hoặc tất cả
                    String lotSelection = request.getParameter("lotSelection");
                    int specificLotId = -1;
                    if ("specific".equals(lotSelection)) {
                        String lotIdStr = request.getParameter("specificLotId");
                        if (lotIdStr != null && !lotIdStr.trim().isEmpty()) {
                            specificLotId = Integer.parseInt(lotIdStr);
                        }
                    }

                    // Validate input
                    if (boxesToConvert <= 0) {
                        jsonOut.print("{\"success\": false, \"message\": \"Số lượng thùng phải lớn hơn 0\"}");
                        return;
                    }

                    // Get current product to validate
                    Product currentProduct = dao.getProductById(productId);
                    if (currentProduct == null) {
                        jsonOut.print("{\"success\": false, \"message\": \"Không tìm thấy sản phẩm\"}");
                        return;
                    }

                    // Lấy số lượng từ Inventory theo lô được chọn
                    double currentStock = 0;
                    if ("specific".equals(lotSelection) && specificLotId > 0) {
                        // Lấy số lượng từ lô cụ thể
                        currentStock = dao.getInventoryQuantityByLotId(specificLotId);
                    } else {
                        // Lấy tổng số lượng từ tất cả các lô BOX
                        currentStock = dao.getTotalInventoryQuantityByType(productId, "BOX");
                    }

                    if (currentStock <= 0) {
                        jsonOut.print(
                                "{\"success\": false, \"message\": \"❌ This product is out of stock! Cannot perform conversion.\"}");
                        return;
                    }

                    if (boxesToConvert > currentStock) {
                        jsonOut.print(
                                "{\"success\": false, \"message\": \"Số lượng thùng chuyển đổi vượt quá số lượng hiện có (\" + currentStock + \" thùng)\"}");
                        return;
                    }

                    // Validate pack size for pack conversion
                    if (conversionType.equals("pack")) {
                        if (packSize <= 0) {
                            jsonOut.print("{\"success\": false, \"message\": \"Vui lòng nhập số lon = 1 lốc\"}");
                            return;
                        }
                        if (packSize < 2 || packSize >= currentProduct.getUnitPerBox()) {
                            jsonOut.print("{\"success\": false, \"message\": \"Số đơn vị/lốc phải từ 2 đến "
                                    + (currentProduct.getUnitPerBox() - 1)
                                    + ". Không thể tạo lốc có số đơn vị bằng hoặc lớn hơn số đơn vị trong thùng\"}");
                            return;
                        }

                        int totalUnits = boxesToConvert * currentProduct.getUnitPerBox();
                        if (totalUnits % packSize != 0) {
                            jsonOut.print("{\"success\": false, \"message\": \"Số đơn vị không chia hết cho " + packSize
                                    + "\"}");
                            return;
                        }
                    } else if (conversionType.equals("both")) {
                        // Bắt buộc nhập số lon = 1 lốc khi chọn chuyển đổi cả 2
                        if (packSize <= 0) {
                            jsonOut.print(
                                    "{\"success\": false, \"message\": \"Khi chọn chuyển đổi cả 2, vui lòng nhập số lon = 1 lốc\"}");
                            return;
                        }
                        if (packSize < 2 || packSize >= currentProduct.getUnitPerBox()) {
                            jsonOut.print("{\"success\": false, \"message\": \"Số đơn vị/lốc phải từ 2 đến "
                                    + (currentProduct.getUnitPerBox() - 1)
                                    + ". Không thể tạo lốc có số đơn vị bằng hoặc lớn hơn số đơn vị trong thùng\"}");
                            return;
                        }

                        int totalUnits = boxesToConvert * currentProduct.getUnitPerBox();
                        if (totalUnits % packSize != 0) {
                            jsonOut.print("{\"success\": false, \"message\": \"Số đơn vị không chia hết cho " + packSize
                                    + "\"}");
                            return;
                        }
                    }

                    // Perform conversion với thông tin lô
                    boolean success = dao.convertUnitsWithLot(productId, boxesToConvert, conversionType, packSize,
                            lotSelection, specificLotId);

                    if (success) {
                        jsonOut.print("{\"success\": true, \"message\": \"Chuyển đổi thành công\"}");
                    } else {
                        jsonOut.print("{\"success\": false, \"message\": \"Chuyển đổi thất bại\"}");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    PrintWriter jsonOut = response.getWriter();
                    jsonOut.print("{\"success\": false, \"message\": \"Lỗi: " + e.getMessage() + "\"}");
                }
                break;

            default:
                response.sendRedirect(request.getContextPath() + "/admin/product");
                break;
        }
    }
}
