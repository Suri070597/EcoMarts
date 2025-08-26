package controller;

import dao.ProductDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.File;
import static java.lang.System.out;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import model.Category;
import model.Product;
import model.Manufacturer;

@WebServlet(name = "ProductServlet", urlPatterns = { "/admin/product" })
@MultipartConfig
public class ProductServlet extends HttpServlet {

    private static final String IMAGE_UPLOAD_DIR = "C:/ProductImages";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        ProductDAO dao = new ProductDAO();
        List<Category> listCategory = dao.getCategory();
        switch (action) {
            case "list":
                List<Product> list = dao.getAll();
                request.setAttribute("dataCate", listCategory);
                request.setAttribute("data", list);

                // Counters for stock status cards
                final int LOW_STOCK_THRESHOLD = 5;
                int inStock = dao.countInStock(LOW_STOCK_THRESHOLD);
                int lowStock = dao.countLowStock(LOW_STOCK_THRESHOLD);
                int outOfStock = dao.countOutOfStock();
                request.setAttribute("inStockCount", inStock);
                request.setAttribute("lowStockCount", lowStock);
                request.setAttribute("outOfStockCount", outOfStock);

                request.getRequestDispatcher("/WEB-INF/admin/product/product.jsp").forward(request, response);
                break;
            case "create":
                request.setAttribute("dataCate", dao.getCategory());
                request.getRequestDispatcher("/WEB-INF/admin/product/create-product.jsp").forward(request, response);
                break;
            case "delete":
                String idRaw = request.getParameter("id");
                int id = 0;
                Product mo = null;
                try {
                    id = Integer.parseInt(idRaw);
                    mo = dao.getProductById(id);
                    request.setAttribute("mo", mo);
                    request.getRequestDispatcher("/WEB-INF/admin/product/delete-product.jsp").forward(request,
                            response);
                } catch (Exception e) {
                    out.println(e.getMessage());
                }
                break;
            case "update":
                String idRaw1 = request.getParameter("id");
                int id1 = 0;
                try {
                    id1 = Integer.parseInt(idRaw1);
                    mo = dao.getProductById(id1);
                    request.setAttribute("mo", mo);
                    request.setAttribute("dataCate", dao.getCategory());
                    request.getRequestDispatcher("/WEB-INF/admin/product/edit-product.jsp").forward(request, response);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "search":
                String keyword = request.getParameter("keyword");
                List<Product> searchResults = dao.searchProductsByName(keyword);
                request.setAttribute("dataCate", listCategory);
                request.setAttribute("data", searchResults);
                request.setAttribute("keyword", keyword);

                // Counters for stock status cards based on search results
                final int SEARCH_LOW_STOCK_THRESHOLD = 5;
                int searchInStock = 0, searchLowStock = 0, searchOutOfStock = 0;

                for (Product p : searchResults) {
                    double stock = p.getStockQuantity();
                    if (stock > SEARCH_LOW_STOCK_THRESHOLD) {
                        searchInStock++;
                    } else if (stock > 0) {
                        searchLowStock++;
                    } else {
                        searchOutOfStock++;
                    }
                }

                request.setAttribute("inStockCount", searchInStock);
                request.setAttribute("lowStockCount", searchLowStock);
                request.setAttribute("outOfStockCount", searchOutOfStock);

                request.getRequestDispatcher("/WEB-INF/admin/product/product.jsp").forward(request, response);
                break;
            case "detail":
                String idDetailRaw = request.getParameter("id");
                try {
                    int idDetail = Integer.parseInt(idDetailRaw);
                    Product productDetail = dao.getProductById(idDetail);
                    Map<String, Object> inventory = dao.getProductInventory(idDetail);

                    // Lấy thông tin nhà sản xuất và ngày nhập kho
                    Map<String, Object> manufacturerInfo = dao.getLatestManufacturerInfo(idDetail);
                    Date expiryDate = dao.getLatestExpiryDate(idDetail);

                    // Lấy số lượng theo package type
                    double boxQty = dao.getQuantityByPackageType(idDetail, "BOX");
                    double unitQty = dao.getQuantityByPackageType(idDetail, "UNIT");
                    double packQty = dao.getQuantityByPackageType(idDetail, "PACK");
                    double kgQty = dao.getQuantityByPackageType(idDetail, "KG");

                    // Kiểm tra xem có phải sản phẩm nước giải khát hoặc sữa không
                    boolean isBeverageOrMilk = dao.isBeverageOrMilkCategory(idDetail);

                    request.setAttribute("productDetail", productDetail);
                    request.setAttribute("inventory", inventory);
                    request.setAttribute("manufacturerInfo", manufacturerInfo);
                    request.setAttribute("expiryDate", expiryDate);
                    request.setAttribute("boxQty", boxQty);
                    request.setAttribute("unitQty", unitQty);
                    request.setAttribute("packQty", packQty);
                    request.setAttribute("kgQty", kgQty);
                    request.setAttribute("isBeverageOrMilk", isBeverageOrMilk);

                    request.getRequestDispatcher("/WEB-INF/admin/product/product-detail.jsp").forward(request,
                            response);
                } catch (Exception e) {
                    e.printStackTrace();
                    response.sendRedirect(request.getContextPath() + "/admin/product");
                }
                break;
            case "convert":
                try {
                    String productIdStr = request.getParameter("id");
                    System.out.println("Convert action - Product ID: " + productIdStr);

                    if (productIdStr == null || productIdStr.trim().isEmpty()) {
                        System.out.println("Product ID is null or empty");
                        response.sendRedirect(request.getContextPath() + "/admin/product?error=invalid_product_id");
                        return;
                    }

                    int productId = Integer.parseInt(productIdStr);
                    System.out.println("Parsed Product ID: " + productId);

                    Product product = dao.getProductById(productId);
                    System.out.println("Retrieved product: " + (product != null ? product.getProductName() : "NULL"));

                    if (product == null) {
                        System.out.println("Product not found");
                        response.sendRedirect(request.getContextPath() + "/admin/product?error=product_not_found");
                        return;
                    }

                    request.setAttribute("product", product);
                    System.out.println("Forwarding to convert-product.jsp");
                    request.getRequestDispatcher("/WEB-INF/admin/product/convert-product.jsp").forward(request,
                            response);

                } catch (NumberFormatException e) {
                    System.err.println("NumberFormatException: " + e.getMessage());
                    response.sendRedirect(request.getContextPath() + "/admin/product?error=invalid_product_id_format");
                } catch (Exception e) {
                    System.err.println("Exception in convert: " + e.getMessage());
                    e.printStackTrace();
                    response.sendRedirect(request.getContextPath() + "/admin/product?error=convert_exception");
                }
                break;
            case "setPrice":
                try {
                    String productIdStr = request.getParameter("id");
                    System.out.println("SetPrice action - Product ID: " + productIdStr);

                    if (productIdStr == null || productIdStr.trim().isEmpty()) {
                        System.out.println("Product ID is null or empty");
                        response.sendRedirect(request.getContextPath() + "/admin/product?error=invalid_product_id");
                        return;
                    }

                    int productId = Integer.parseInt(productIdStr);
                    System.out.println("Parsed Product ID: " + productId);

                    Product product = dao.getProductById(productId);
                    System.out.println("Retrieved product: " + (product != null ? product.getProductName() : "NULL"));

                    if (product == null) {
                        System.out.println("Product not found");
                        response.sendRedirect(request.getContextPath() + "/admin/product?error=product_not_found");
                        return;
                    }

                    request.setAttribute("product", product);
                    System.out.println("Forwarding to set-price.jsp");
                    request.getRequestDispatcher("/WEB-INF/admin/product/set-price.jsp").forward(request,
                            response);

                } catch (NumberFormatException e) {
                    System.err.println("NumberFormatException: " + e.getMessage());
                    response.sendRedirect(request.getContextPath() + "/admin/product?error=invalid_product_id_format");
                } catch (Exception e) {
                    System.err.println("Exception in setPrice: " + e.getMessage());
                    e.printStackTrace();
                    response.sendRedirect(request.getContextPath() + "/admin/product?error=setprice_exception");
                }
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
                    String itemUnitName = request.getParameter("itemUnitName");
                    String unitPerBoxStr = request.getParameter("unitPerBox");
                    String boxUnitName = request.getParameter("boxUnitName");

                    if (pName == null || pName.trim().isEmpty()) {
                        request.setAttribute("error", "Vui lòng nhập tên sản phẩm.");
                        request.setAttribute("dataCate", dao.getCategory());
                        request.getRequestDispatcher("/WEB-INF/admin/product/create-product.jsp").forward(request,
                                response);
                        return;
                    }
                    if (categoryIDStr == null || categoryIDStr.trim().isEmpty()) {
                        request.setAttribute("error", "Vui lòng chọn danh mục sản phẩm.");
                        request.setAttribute("dataCate", dao.getCategory());
                        request.getRequestDispatcher("/WEB-INF/admin/product/create-product.jsp").forward(request,
                                response);
                        return;
                    }
                    if (itemUnitName == null || itemUnitName.trim().isEmpty()) {
                        request.setAttribute("error", "Vui lòng chọn đơn vị nhỏ nhất.");
                        request.setAttribute("dataCate", dao.getCategory());
                        request.getRequestDispatcher("/WEB-INF/admin/product/create-product.jsp").forward(request,
                                response);
                        return;
                    }

                    int categoryID = Integer.parseInt(categoryIDStr);
                    List<Category> allCate = dao.getCategory();
                    boolean isFruit = (categoryID == 3);
                    for (Category c : allCate) {
                        if (c.getCategoryID() == categoryID && c.getParentID() != null && c.getParentID() == 3) {
                            isFruit = true;
                            break;
                        }
                    }

                    Integer unitPerBox = null;
                    String boxUnit = null;
                    if (!isFruit) {
                        if (unitPerBoxStr == null || unitPerBoxStr.trim().isEmpty()) {
                            request.setAttribute("error", "Vui lòng nhập số lượng sản phẩm trong 1 thùng.");
                            request.setAttribute("dataCate", dao.getCategory());
                            request.getRequestDispatcher("/WEB-INF/admin/product/create-product.jsp").forward(request,
                                    response);
                            return;
                        }
                        unitPerBox = Integer.parseInt(unitPerBoxStr);
                        boxUnit = (boxUnitName == null || boxUnitName.trim().isEmpty()) ? "thùng" : boxUnitName.trim();
                    } else {
                        unitPerBox = 1;
                        boxUnit = itemUnitName; // không có thùng cho trái cây
                    }

                    // Xử lý ảnh
                    Part filePart = request.getPart("pImage");
                    String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                    if (!fileName.isEmpty()) {
                        File uploadDir = new File(IMAGE_UPLOAD_DIR);
                        if (!uploadDir.exists())
                            uploadDir.mkdirs();
                        filePart.write(IMAGE_UPLOAD_DIR + File.separator + fileName);
                    }
                    String pImage = fileName;

                    // Theo schema mới: giá có thể NULL; không nhập ở form tạo
                    Double priceBox = null;
                    Double priceUnit = null;
                    Double pricePack = null;

                    int res = dao.insertNewProduct(pName, categoryID, priceBox, priceUnit, pricePack,
                            unitPerBox, boxUnit, itemUnitName, pDescription, pImage);

                    if (res == 1) {
                        response.sendRedirect(request.getContextPath() + "/admin/product");
                        return;
                    } else {
                        request.setAttribute("error", "❌ Thêm sản phẩm thất bại.");
                        request.setAttribute("dataCate", dao.getCategory());
                        request.getRequestDispatcher("/WEB-INF/admin/product/create-product.jsp").forward(request,
                                response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    request.setAttribute("error", "❌ Đã xảy ra lỗi khi tạo sản phẩm: " + e.getMessage());
                    request.setAttribute("dataCate", dao.getCategory());
                    request.getRequestDispatcher("/WEB-INF/admin/product/create-product.jsp").forward(request,
                            response);
                }
                break;

            case "delete":
                try {
                    String idRaw = request.getParameter("id");
                    if (idRaw == null || idRaw.trim().isEmpty()) {
                        response.sendRedirect(request.getContextPath() + "/admin/product?error=invalid_id");
                        return;
                    }

                    int id = Integer.parseInt(idRaw);
                    Product productToDelete = dao.getProductById(id);

                    if (productToDelete == null) {
                        response.sendRedirect(request.getContextPath() + "/admin/product?error=product_not_found");
                        return;
                    }

                    // Thực hiện xóa sản phẩm
                    boolean deleteSuccess = dao.delete(id);

                    if (deleteSuccess) {
                        response.sendRedirect(request.getContextPath() + "/admin/product");
                    } else {
                        response.sendRedirect(request.getContextPath() + "/admin/product?error=delete_failed&id=" + id);
                    }
                } catch (NumberFormatException e) {
                    response.sendRedirect(request.getContextPath() + "/admin/product?error=invalid_id_format");
                } catch (Exception e) {
                    e.printStackTrace();
                    response.sendRedirect(request.getContextPath() + "/admin/product?error=delete_exception");
                }
                break;

            case "update":
                try {
                    int productId = Integer.parseInt(request.getParameter("id"));
                    String productName = request.getParameter("pName");
                    String description = request.getParameter("pDescription");
                    int categoryId = Integer.parseInt(request.getParameter("categoryID"));

                    // Lấy thông tin đóng gói
                    String unitPerBoxStr = request.getParameter("unitPerBox");
                    String boxUnitName = request.getParameter("boxUnitName");
                    String itemUnitName = request.getParameter("itemUnitName");

                    // Xác định có phải trái cây không
                    List<Category> allCate = dao.getCategory();
                    boolean isFruit = false;
                    for (Category c : allCate) {
                        if (c.getCategoryID() == categoryId) {
                            isFruit = (c.getParentID() != null && c.getParentID() == 3);
                            break;
                        }
                    }

                    // Validate dữ liệu
                    if (productName == null || productName.trim().isEmpty()) {
                        throw new IllegalArgumentException("Tên sản phẩm không được để trống.");
                    }

                    if (isFruit) {
                        // Validate trái cây
                        itemUnitName = "kg"; // Mặc định cho trái cây
                    } else {
                        // Validate sản phẩm thường
                        if (unitPerBoxStr == null || unitPerBoxStr.trim().isEmpty()) {
                            throw new IllegalArgumentException("Vui lòng nhập số lượng sản phẩm trong 1 thùng.");
                        }
                        if (boxUnitName == null || boxUnitName.trim().isEmpty()) {
                            throw new IllegalArgumentException("Vui lòng nhập đơn vị thùng.");
                        }
                        if (itemUnitName == null || itemUnitName.trim().isEmpty()) {
                            throw new IllegalArgumentException("Vui lòng chọn đơn vị nhỏ nhất.");
                        }
                    }

                    // Xử lý hình ảnh
                    Part filePart = request.getPart("pImage");
                    String imageFileName = null;
                    if (filePart != null && filePart.getSize() > 0) {
                        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                        if (fileName != null && !fileName.isEmpty()) {
                            String fileExtension = fileName.substring(fileName.lastIndexOf("."));
                            imageFileName = System.currentTimeMillis() + fileExtension;
                            File uploadDir = new File(IMAGE_UPLOAD_DIR);
                            if (!uploadDir.exists()) {
                                uploadDir.mkdirs();
                            }
                            filePart.write(IMAGE_UPLOAD_DIR + File.separator + imageFileName);
                        }
                    }

                    // Lấy sản phẩm hiện tại để giữ nguyên hình ảnh nếu không thay đổi
                    Product currentProduct = dao.getProductById(productId);
                    if (currentProduct == null) {
                        throw new IllegalArgumentException("Không tìm thấy sản phẩm.");
                    }

                    // Thực hiện update
                    String finalImageURL = imageFileName != null ? imageFileName : currentProduct.getImageURL();

                    boolean updateSuccess = dao.updateProduct(
                            productId, productName, description, categoryId,
                            null, null, null, // Không cập nhật giá
                            unitPerBoxStr, boxUnitName, itemUnitName,
                            finalImageURL);

                    // Xóa ảnh cũ nếu có ảnh mới
                    if (updateSuccess && imageFileName != null && currentProduct.getImageURL() != null) {
                        File oldImageFile = new File(IMAGE_UPLOAD_DIR, currentProduct.getImageURL());
                        if (oldImageFile.exists()) {
                            oldImageFile.delete();
                        }
                    }

                    if (updateSuccess) {
                        // Redirect về trang danh sách sản phẩm
                        response.sendRedirect(request.getContextPath() + "/admin/product");
                    } else {
                        throw new Exception("Cập nhật sản phẩm thất bại.");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    request.setAttribute("error", "❌ Đã xảy ra lỗi khi cập nhật sản phẩm: " + e.getMessage());
                    try {
                        int productId = Integer.parseInt(request.getParameter("id"));
                        Product existing = dao.getProductById(productId);
                        request.setAttribute("mo", existing);
                        request.setAttribute("dataCate", dao.getCategory());
                        request.getRequestDispatcher("/WEB-INF/admin/product/edit-product.jsp").forward(request,
                                response);
                    } catch (Exception ex) {
                        response.sendRedirect(request.getContextPath() + "/admin/product");
                    }
                }
                break;

            case "convertUnits":
                try {
                    int productId = Integer.parseInt(request.getParameter("productId"));
                    int boxesToConvert = Integer.parseInt(request.getParameter("boxesToConvert"));
                    String conversionType = request.getParameter("conversionType");
                    int packSize = 0;
                    String packSizeStr = request.getParameter("packSize");

                    if (packSizeStr != null && !packSizeStr.trim().isEmpty()) {
                        packSize = Integer.parseInt(packSizeStr);
                    }

                    // Validate input
                    if (boxesToConvert <= 0) {
                        request.setAttribute("error", "Số lượng thùng phải lớn hơn 0");
                        Product product = dao.getProductById(productId);
                        request.setAttribute("product", product);
                        request.getRequestDispatcher("/WEB-INF/admin/product/convert-product.jsp").forward(request,
                                response);
                        return;
                    }

                    // Get current product to validate
                    Product currentProduct = dao.getProductById(productId);
                    if (currentProduct == null) {
                        request.setAttribute("error", "Không tìm thấy sản phẩm");
                        request.getRequestDispatcher("/WEB-INF/admin/product/convert-product.jsp").forward(request,
                                response);
                        return;
                    }

                    if (boxesToConvert > currentProduct.getStockQuantity()) {
                        request.setAttribute("error", "Số lượng thùng chuyển đổi vượt quá số lượng hiện có");
                        request.setAttribute("product", currentProduct);
                        request.getRequestDispatcher("/WEB-INF/admin/product/convert-product.jsp").forward(request,
                                response);
                        return;
                    }

                    // Validate pack size for pack conversion
                    if (conversionType.equals("pack") || conversionType.equals("both")) {
                        if (packSize <= 0) {
                            request.setAttribute("error", "Vui lòng nhập số lon = 1 lốc");
                            request.setAttribute("product", currentProduct);
                            request.getRequestDispatcher("/WEB-INF/admin/product/convert-product.jsp").forward(request,
                                    response);
                            return;
                        }
                        if (packSize < 2 || packSize >= currentProduct.getUnitPerBox()) {
                            request.setAttribute("error",
                                    "Số đơn vị/lốc phải từ 2 đến " + (currentProduct.getUnitPerBox() - 1));
                            request.setAttribute("product", currentProduct);
                            request.getRequestDispatcher("/WEB-INF/admin/product/convert-product.jsp").forward(request,
                                    response);
                            return;
                        }

                        // Kiểm tra số lon trong 1 thùng phải chia hết cho packSize
                        if (currentProduct.getUnitPerBox() % packSize != 0) {
                            request.setAttribute("error",
                                    "Số lon trong 1 thùng (" + currentProduct.getUnitPerBox() +
                                            ") không chia hết cho " + packSize + " lon/lốc - không được dư lon");
                            request.setAttribute("product", currentProduct);
                            request.getRequestDispatcher("/WEB-INF/admin/product/convert-product.jsp").forward(request,
                                    response);
                            return;
                        }
                    }

                    // Perform conversion
                    boolean success = dao.convertUnits(productId, boxesToConvert, conversionType, packSize);

                    if (success) {
                        // Hiển thị thông báo ngay trên trang chuyển đổi
                        Product refreshed = dao.getProductById(productId);
                        request.setAttribute("product", refreshed);
                        request.setAttribute("success", "Chuyển đổi đơn vị thành công.");
                        request.getRequestDispatcher("/WEB-INF/admin/product/convert-product.jsp").forward(request,
                                response);
                    } else {
                        request.setAttribute("error", "Chuyển đổi thất bại");
                        request.setAttribute("product", currentProduct);
                        request.getRequestDispatcher("/WEB-INF/admin/product/convert-product.jsp").forward(request,
                                response);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    request.setAttribute("error", "Đã xảy ra lỗi khi chuyển đổi: " + e.getMessage());
                    try {
                        int productId = Integer.parseInt(request.getParameter("productId"));
                        Product product = dao.getProductById(productId);
                        request.setAttribute("product", product);
                        request.getRequestDispatcher("/WEB-INF/admin/product/convert-product.jsp").forward(request,
                                response);
                    } catch (Exception ex) {
                        response.sendRedirect(request.getContextPath() + "/admin/product");
                    }
                }
                break;

            case "updatePrice":
                try {
                    String productIdStr = request.getParameter("productId");
                    String priceBoxStr = request.getParameter("priceBox");
                    String priceUnitStr = request.getParameter("priceUnit");
                    String pricePackStr = request.getParameter("pricePack");

                    if (productIdStr == null || productIdStr.trim().isEmpty()) {
                        response.sendRedirect(request.getContextPath() + "/admin/product?error=invalid_product_id");
                        return;
                    }

                    int productId = Integer.parseInt(productIdStr);
                    Product currentProduct = dao.getProductById(productId);

                    if (currentProduct == null) {
                        response.sendRedirect(request.getContextPath() + "/admin/product?error=product_not_found");
                        return;
                    }

                    // Parse price values, allowing null for empty fields
                    Double priceBox = null;
                    Double priceUnit = null;
                    Double pricePack = null;

                    if (priceBoxStr != null && !priceBoxStr.trim().isEmpty()) {
                        try {
                            priceBox = Double.parseDouble(priceBoxStr);
                            if (priceBox < 0) {
                                request.setAttribute("error", "Giá thùng không được âm");
                                request.setAttribute("product", currentProduct);
                                request.getRequestDispatcher("/WEB-INF/admin/product/set-price.jsp").forward(request,
                                        response);
                                return;
                            }
                        } catch (NumberFormatException e) {
                            request.setAttribute("error", "Giá thùng không hợp lệ");
                            request.setAttribute("product", currentProduct);
                            request.getRequestDispatcher("/WEB-INF/admin/product/set-price.jsp").forward(request,
                                    response);
                            return;
                        }
                    }

                    if (priceUnitStr != null && !priceUnitStr.trim().isEmpty()) {
                        try {
                            priceUnit = Double.parseDouble(priceUnitStr);
                            if (priceUnit < 0) {
                                request.setAttribute("error", "Giá đơn vị không được âm");
                                request.setAttribute("product", currentProduct);
                                request.getRequestDispatcher("/WEB-INF/admin/product/set-price.jsp").forward(request,
                                        response);
                                return;
                            }
                        } catch (NumberFormatException e) {
                            request.setAttribute("error", "Giá đơn vị không hợp lệ");
                            request.setAttribute("product", currentProduct);
                            request.getRequestDispatcher("/WEB-INF/admin/product/set-price.jsp").forward(request,
                                    response);
                            return;
                        }
                    }

                    if (pricePackStr != null && !pricePackStr.trim().isEmpty()) {
                        try {
                            pricePack = Double.parseDouble(pricePackStr);
                            if (pricePack < 0) {
                                request.setAttribute("error", "Giá lốc không được âm");
                                request.setAttribute("product", currentProduct);
                                request.getRequestDispatcher("/WEB-INF/admin/product/set-price.jsp").forward(request,
                                        response);
                                return;
                            }
                        } catch (NumberFormatException e) {
                            request.setAttribute("error", "Giá lốc không hợp lệ");
                            request.setAttribute("product", currentProduct);
                            request.getRequestDispatcher("/WEB-INF/admin/product/set-price.jsp").forward(request,
                                    response);
                            return;
                        }
                    }

                    // Kiểm tra validation đặc biệt cho nước giải khát, sữa và các loại khác
                    if (currentProduct.getCategory() != null) {
                        int parentId = currentProduct.getCategory().getParentID();
                        boolean isFruit = parentId == 3;
                        boolean isBeverageOrMilk = parentId == 1 || parentId == 2;

                        // Kiểm tra xem có thể nhập giá cho unit không (cho tất cả các loại trừ trái
                        // cây)
                        if (!isFruit && priceUnit != null && priceUnit > 0) {
                            double unitQuantity = dao.getQuantityByPackageType(productId, "UNIT");
                            if (unitQuantity <= 0) {
                                request.setAttribute("error",
                                        "Không thể nhập giá cho đơn vị khi chưa có số lượng sau chuyển đổi. Vui lòng thực hiện chuyển đổi đơn vị trước.");
                                request.setAttribute("product", currentProduct);
                                request.getRequestDispatcher("/WEB-INF/admin/product/set-price.jsp").forward(request,
                                        response);
                                return;
                            }
                        }

                        // Kiểm tra xem có thể nhập giá cho pack không (chỉ cho nước giải khát và sữa)
                        if (isBeverageOrMilk && pricePack != null && pricePack > 0) {
                            double packQuantity = dao.getQuantityByPackageType(productId, "PACK");
                            if (packQuantity <= 0) {
                                request.setAttribute("error",
                                        "Không thể nhập giá cho lốc khi chưa có số lượng sau chuyển đổi. Vui lòng thực hiện chuyển đổi đơn vị trước.");
                                request.setAttribute("product", currentProduct);
                                request.getRequestDispatcher("/WEB-INF/admin/product/set-price.jsp").forward(request,
                                        response);
                                return;
                            }
                        }
                    }

                    // Kiểm tra logic giá: UNIT ≤ PACK ≤ BOX
                    if (priceBox != null && priceUnit != null && priceBox > 0 && priceUnit > 0) {
                        if (priceUnit > priceBox) {
                            request.setAttribute("error", "❌ Lỗi logic giá: Giá đơn vị (" + priceUnit
                                    + "đ) không được lớn hơn giá thùng (" + priceBox + "đ)!");
                            request.setAttribute("product", currentProduct);
                            request.getRequestDispatcher("/WEB-INF/admin/product/set-price.jsp").forward(request,
                                    response);
                            return;
                        }
                    }

                    if (pricePack != null && priceUnit != null && pricePack > 0 && priceUnit > 0) {
                        if (priceUnit > pricePack) {
                            request.setAttribute("error", "❌ Lỗi logic giá: Giá đơn vị (" + priceUnit
                                    + "đ) không được lớn hơn giá lốc (" + pricePack + "đ)!");
                            request.setAttribute("product", currentProduct);
                            request.getRequestDispatcher("/WEB-INF/admin/product/set-price.jsp").forward(request,
                                    response);
                            return;
                        }
                    }

                    if (pricePack != null && priceBox != null && pricePack > 0 && priceBox > 0) {
                        if (pricePack > priceBox) {
                            request.setAttribute("error", "❌ Lỗi logic giá: Giá lốc (" + pricePack
                                    + "đ) không được lớn hơn giá thùng (" + priceBox + "đ)!");
                            request.setAttribute("product", currentProduct);
                            request.getRequestDispatcher("/WEB-INF/admin/product/set-price.jsp").forward(request,
                                    response);
                            return;
                        }
                    }

                    // Update product prices
                    boolean success = dao.updateProductPrice(productId, priceBox, priceUnit, pricePack);

                    if (success) {
                        request.setAttribute("success", "Cập nhật giá thành công");
                        request.setAttribute("product", currentProduct);
                        request.getRequestDispatcher("/WEB-INF/admin/product/set-price.jsp").forward(request, response);
                    } else {
                        request.setAttribute("error", "Cập nhật giá thất bại");
                        request.setAttribute("product", currentProduct);
                        request.getRequestDispatcher("/WEB-INF/admin/product/set-price.jsp").forward(request, response);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    request.setAttribute("error", "Đã xảy ra lỗi khi cập nhật giá: " + e.getMessage());
                    try {
                        int productId = Integer.parseInt(request.getParameter("productId"));
                        Product product = dao.getProductById(productId);
                        request.setAttribute("product", product);
                        request.getRequestDispatcher("/WEB-INF/admin/product/set-price.jsp").forward(request, response);
                    } catch (Exception ex) {
                        response.sendRedirect(request.getContextPath() + "/admin/product");
                    }
                }
                break;

            case "getProductInventory":
                try {
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    PrintWriter jsonOut = response.getWriter();

                    int productId = Integer.parseInt(request.getParameter("productId"));
                    Map<String, Object> inventory = dao.getProductInventory(productId);

                    double boxQuantity = (Double) inventory.getOrDefault("BOX_Quantity", 0.0);
                    double boxPrice = (Double) inventory.getOrDefault("BOX_Price", 0.0);
                    double unitQuantity = (Double) inventory.getOrDefault("UNIT_Quantity", 0.0);
                    double packQuantity = (Double) inventory.getOrDefault("PACK_Quantity", 0.0);

                    jsonOut.print("{\"success\": true, \"boxQuantity\": " + boxQuantity + ", \"boxPrice\": " + boxPrice
                            + ", \"unitQuantity\": " + unitQuantity + ", \"packQuantity\": " + packQuantity + "}");

                } catch (Exception e) {
                    e.printStackTrace();
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    PrintWriter jsonOut = response.getWriter();
                    jsonOut.print("{\"success\": false, \"message\": \"Lỗi: " + e.getMessage() + "\"}");
                }
                break;

        }
    }

    @Override
    public String getServletInfo() {
        return "ProductServlet handles CRUD for products";
    }

    private Date truncateTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}
