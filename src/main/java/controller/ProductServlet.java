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
import model.Category;
import model.Product;
import model.Supplier;

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
                List<Product> list = dao.getAllIncludingOutOfStock();
                request.setAttribute("dataCate", listCategory);
                request.setAttribute("data", list);

                request.getRequestDispatcher("/WEB-INF/admin/product/product.jsp").forward(request, response);
                break;
            case "create":
                request.setAttribute("dataCate", dao.getCategory());
                request.setAttribute("dataSup", dao.getAllSuppliers());
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
                    request.setAttribute("dataSup", dao.getAllSuppliers());
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
                request.getRequestDispatcher("/WEB-INF/admin/product/product.jsp").forward(request, response);
                break;
            case "detail":
                String idDetailRaw = request.getParameter("id");
                try {
                    int idDetail = Integer.parseInt(idDetailRaw);
                    Product productDetail = dao.getProductById(idDetail);
                    request.setAttribute("productDetail", productDetail);
                    request.getRequestDispatcher("/WEB-INF/admin/product/product-detail.jsp").forward(request,
                            response);
                } catch (Exception e) {
                    e.printStackTrace();
                    response.sendRedirect(request.getContextPath() + "/admin/product");
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
                    String supplierIDStr = request.getParameter("supplierID");
                    String manufactureDateStr = request.getParameter("manufactureDate");
                    String expiryMonthsStr = request.getParameter("expirySelect");

                    // Validate required fields
                    if (pName == null || pName.trim().isEmpty()) {
                        request.setAttribute("error", "Vui lòng nhập tên sản phẩm.");
                        request.setAttribute("dataCate", dao.getCategory());
                        request.setAttribute("dataSup", dao.getAllSuppliers());
                        request.getRequestDispatcher("/WEB-INF/admin/product/create-product.jsp").forward(request,
                                response);
                        return;
                    }
                    if (categoryIDStr == null || categoryIDStr.trim().isEmpty()) {
                        request.setAttribute("error", "Vui lòng chọn danh mục sản phẩm.");
                        request.setAttribute("dataCate", dao.getCategory());
                        request.setAttribute("dataSup", dao.getAllSuppliers());
                        request.getRequestDispatcher("/WEB-INF/admin/product/create-product.jsp").forward(request,
                                response);
                        return;
                    }
                    if (supplierIDStr == null || supplierIDStr.trim().isEmpty()) {
                        request.setAttribute("error", "Vui lòng chọn nhà cung cấp.");
                        request.setAttribute("dataCate", dao.getCategory());
                        request.setAttribute("dataSup", dao.getAllSuppliers());
                        request.getRequestDispatcher("/WEB-INF/admin/product/create-product.jsp").forward(request,
                                response);
                        return;
                    }
                    if (manufactureDateStr == null || manufactureDateStr.trim().isEmpty()) {
                        request.setAttribute("error", "Vui lòng nhập ngày sản xuất.");
                        request.setAttribute("dataCate", dao.getCategory());
                        request.setAttribute("dataSup", dao.getAllSuppliers());
                        request.getRequestDispatcher("/WEB-INF/admin/product/create-product.jsp").forward(request,
                                response);
                        return;
                    }

                    int categoryID = Integer.parseInt(categoryIDStr);
                    int supplierID = Integer.parseInt(supplierIDStr);
                    // Lấy danh sách category để xác định trái cây
                    List<Category> allCate = dao.getCategory();
                    boolean isFruit = (categoryID == 3);
                    for (Category c : allCate) {
                        if (c.getCategoryID() == categoryID && c.getParentID() != null && c.getParentID() == 3) {
                            isFruit = true;
                            break;
                        }
                    }
                    // Chỉ kiểm tra hạn sử dụng nếu không phải trái cây
                    if (!isFruit && (expiryMonthsStr == null || expiryMonthsStr.trim().isEmpty())) {
                        request.setAttribute("error", "Vui lòng chọn hạn sử dụng.");
                        request.setAttribute("dataCate", dao.getCategory());
                        request.setAttribute("dataSup", dao.getAllSuppliers());
                        request.getRequestDispatcher("/WEB-INF/admin/product/create-product.jsp").forward(request,
                                response);
                        return;
                    }

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date manufactureDate = sdf.parse(manufactureDateStr);
                    int months = 0;
                    if (!isFruit) {
                        months = Integer.parseInt(expiryMonthsStr);
                    }
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(manufactureDate);
                    if (!isFruit) {
                        cal.add(Calendar.MONTH, months);
                    }
                    Date expirationDate = cal.getTime();
                    Date today = new Date();
                    // Chuẩn hóa ngày về 00:00:00 để so sánh
                    Date todayTrunc = truncateTime(today);
                    if (manufactureDate.after(todayTrunc)) {
                        request.setAttribute("error", "Ngày sản xuất không được ở tương lai.");
                        request.setAttribute("dataCate", dao.getCategory());
                        request.setAttribute("dataSup", dao.getAllSuppliers());
                        request.getRequestDispatcher("/WEB-INF/admin/product/create-product.jsp").forward(request,
                                response);
                        return;
                    }
                    // Không cho phép ngày sản xuất quá 2 năm về trước
                    Calendar twoYearsAgo = Calendar.getInstance();
                    twoYearsAgo.setTime(today);
                    twoYearsAgo.add(Calendar.YEAR, -2); // Trừ đi 2 năm
                    Date twoYearsBefore = twoYearsAgo.getTime();
                    if (manufactureDate.before(twoYearsBefore)) {
                        request.setAttribute("error", "Ngày sản xuất không được quá 2 năm trước.");
                        request.setAttribute("dataCate", dao.getCategory());
                        request.setAttribute("dataSup", dao.getAllSuppliers());
                        request.getRequestDispatcher("/WEB-INF/admin/product/create-product.jsp").forward(request,
                                response);
                        return;
                    }
                    if (expirationDate.before(todayTrunc)) {
                        request.setAttribute("error", "Ngày hết hạn đã trôi qua.");
                        request.setAttribute("dataCate", dao.getCategory());
                        request.setAttribute("dataSup", dao.getAllSuppliers());
                        request.getRequestDispatcher("/WEB-INF/admin/product/create-product.jsp").forward(request,
                                response);
                        return;
                    }
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
                    double price;
                    double stockQuantity;
                    int unitPerBox;
                    int unitsPerPack = 1; // Mặc định là 1
                    String boxUnitName;
                    String itemUnitName;
                    if (isFruit) {
                        String fruitPriceStr = request.getParameter("fruitPrice");
                        String fruitQtyStr = request.getParameter("fruitQuantity");
                        String fruitExpiryDaysStr = request.getParameter("fruitExpiryDays");
                        if (fruitPriceStr == null || fruitPriceStr.trim().isEmpty()) {
                            request.setAttribute("error", "Vui lòng nhập giá cho trái cây.");
                            request.setAttribute("dataCate", dao.getCategory());
                            request.setAttribute("dataSup", dao.getAllSuppliers());
                            request.getRequestDispatcher("/WEB-INF/admin/product/create-product.jsp").forward(request,
                                    response);
                            return;
                        }
                        if (fruitQtyStr == null || fruitQtyStr.trim().isEmpty()) {
                            request.setAttribute("error", "Vui lòng nhập số lượng (kg) cho trái cây.");
                            request.setAttribute("dataCate", dao.getCategory());
                            request.setAttribute("dataSup", dao.getAllSuppliers());
                            request.getRequestDispatcher("/WEB-INF/admin/product/create-product.jsp").forward(request,
                                    response);
                            return;
                        }
                        if (fruitExpiryDaysStr == null || fruitExpiryDaysStr.trim().isEmpty()) {
                            request.setAttribute("error", "Vui lòng nhập hạn sử dụng (ngày) cho trái cây.");
                            request.setAttribute("dataCate", dao.getCategory());
                            request.setAttribute("dataSup", dao.getAllSuppliers());
                            request.getRequestDispatcher("/WEB-INF/admin/product/create-product.jsp").forward(request,
                                    response);
                            return;
                        }
                        price = Double.parseDouble(fruitPriceStr);
                        stockQuantity = Double.parseDouble(fruitQtyStr);
                        int fruitExpiryDays = Integer.parseInt(fruitExpiryDaysStr);
                        // Chỉ cho phép số lượng là số nguyên dương
                        if (stockQuantity <= 0 || stockQuantity != Math.floor(stockQuantity)) {
                            request.setAttribute("error", "Số lượng trái cây phải là số nguyên dương (kg).");
                            request.setAttribute("dataCate", dao.getCategory());
                            request.setAttribute("dataSup", dao.getAllSuppliers());
                            request.getRequestDispatcher("/WEB-INF/admin/product/create-product.jsp").forward(request,
                                    response);
                            return;
                        }
                        if (price <= 0 || fruitExpiryDays <= 0) {
                            request.setAttribute("error", "Giá và hạn sử dụng phải lớn hơn 0.");
                            request.setAttribute("dataCate", dao.getCategory());
                            request.setAttribute("dataSup", dao.getAllSuppliers());
                            request.getRequestDispatcher("/WEB-INF/admin/product/create-product.jsp").forward(request,
                                    response);
                            return;
                        }
                        Calendar calFruit = Calendar.getInstance();
                        calFruit.setTime(manufactureDate);
                        calFruit.add(Calendar.DATE, fruitExpiryDays);
                        expirationDate = calFruit.getTime();
                        unitPerBox = 1;
                        boxUnitName = "kg";
                        itemUnitName = "kg";
                        unitsPerPack = 1; // Trái cây không có đơn vị trung gian
                        // Tính ngày hết hạn
                        Date expirationTrunc = truncateTime(expirationDate);
                        if (expirationTrunc.before(todayTrunc)) {
                            request.setAttribute("error", "Ngày hết hạn đã trôi qua.");
                            request.setAttribute("dataCate", dao.getCategory());
                            request.setAttribute("dataSup", dao.getAllSuppliers());
                            request.getRequestDispatcher("/WEB-INF/admin/product/create-product.jsp").forward(request,
                                    response);
                            return;
                        }
                    } else {
                        double boxPrice = Double.parseDouble(request.getParameter("boxPrice"));
                        stockQuantity = Double.parseDouble(request.getParameter("stockQuantity"));

                        // Kiểm tra validation
                        if (boxPrice <= 0) {
                            request.setAttribute("error", "Giá 1 thùng phải lớn hơn 0.");
                            request.setAttribute("dataCate", dao.getCategory());
                            request.setAttribute("dataSup", dao.getAllSuppliers());
                            request.getRequestDispatcher("/WEB-INF/admin/product/create-product.jsp").forward(request,
                                    response);
                            return;
                        }
                        if (stockQuantity <= 0) {
                            request.setAttribute("error", "Số lượng thùng phải lớn hơn 0.");
                            request.setAttribute("dataCate", dao.getCategory());
                            request.setAttribute("dataSup", dao.getAllSuppliers());
                            request.getRequestDispatcher("/WEB-INF/admin/product/create-product.jsp").forward(request,
                                    response);
                            return;
                        }

                        // Đặt giá trị mặc định cho chuyển đổi đơn vị
                        // Sẽ được cập nhật sau khi admin sử dụng nút chuyển đổi
                        unitPerBox = 0; // Chưa thiết lập: 1 thùng = 0 đơn vị nhỏ nhất (sẽ cấu hình sau)
                        price = boxPrice; // Lưu GIÁ 1 THÙNG vào cột Price để tương thích

                        // Đặt tên đơn vị mặc định
                        boxUnitName = "thùng";
                        itemUnitName = "lon"; // Mặc định là lon
                        unitsPerPack = 0; // Chưa có đơn vị trung gian

                        // Chuẩn hóa ngày hết hạn
                        Date expirationTrunc = truncateTime(expirationDate);
                        if (expirationTrunc.before(todayTrunc)) {
                            request.setAttribute("error", "Ngày hết hạn đã trôi qua.");
                            request.setAttribute("dataCate", dao.getCategory());
                            request.setAttribute("dataSup", dao.getAllSuppliers());
                            request.getRequestDispatcher("/WEB-INF/admin/product/create-product.jsp").forward(request,
                                    response);
                            return;
                        }
                    }
                    // Giá theo từng đơn vị mới
                    Double insertBoxPrice;
                    Double insertUnitPrice = null;
                    Double insertPackPrice = null;
                    if (isFruit) {
                        insertBoxPrice = 0.0; // Trái cây không có giá thùng
                    } else {
                        insertBoxPrice = price; // đang lưu Price = BoxPrice ở bước trên (packaged)
                    }

                    int res = dao.insert(pName, price, pDescription, stockQuantity, pImage, itemUnitName,
                            createdAt, categoryID, supplierID, manufactureDate, expirationDate,
                            unitPerBox, boxUnitName, itemUnitName, unitsPerPack,
                            insertBoxPrice, insertUnitPrice, insertPackPrice);

                    if (res == 1) {
                        response.sendRedirect(request.getContextPath() + "/admin/product");
                        return;
                    } else {
                        request.setAttribute("error", "❌ Thêm sản phẩm thất bại.");
                        request.setAttribute("dataCate", dao.getCategory());
                        request.setAttribute("dataSup", dao.getAllSuppliers());
                        request.getRequestDispatcher("/WEB-INF/admin/product/create-product.jsp").forward(request,
                                response);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    request.setAttribute("error", "❌ Đã xảy ra lỗi khi tạo sản phẩm: " + e.getMessage());
                    request.setAttribute("dataCate", dao.getCategory());
                    request.setAttribute("dataSup", dao.getAllSuppliers());
                    request.getRequestDispatcher("/WEB-INF/admin/product/create-product.jsp").forward(request,
                            response);
                }
                break;

            case "delete":
                String idRaw = request.getParameter("id");
                int id = Integer.parseInt(idRaw);
                if (dao.delete(id)) {
                    response.sendRedirect(request.getContextPath() + "/admin/product");
                } else {
                    response.sendRedirect(request.getContextPath() + "/admin/product?action=delete&id=" + id);
                }
                break;

            case "update":
                try {
                    int id1 = Integer.parseInt(request.getParameter("id"));
                    String name = request.getParameter("pName");
                    String description = request.getParameter("pDescription");
                    int categoryId = Integer.parseInt(request.getParameter("categoryID"));
                    int supplierId = Integer.parseInt(request.getParameter("supplierID"));
                    String manufactureDateStr1 = request.getParameter("manufactureDate");
                    String expirySelect = request.getParameter("expirySelect");
                    String fruitExpiryDaysStr = request.getParameter("fruitExpiryDays");
                    String fruitPriceStr = request.getParameter("fruitPrice");
                    String fruitQtyStr = request.getParameter("fruitQuantity");
                    String boxPriceStr = request.getParameter("boxPrice");
                    // Removed: unitPerBoxStr, unitsPerPackStr (conversion handled separately)

                    // Kiểm tra và chuyển đổi ngày sản xuất
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date manufactureDate;
                    try {
                        if (manufactureDateStr1 == null || manufactureDateStr1.isEmpty()) {
                            throw new IllegalArgumentException("❌ Ngày sản xuất không được để trống.");
                        }
                        manufactureDate = sdf.parse(manufactureDateStr1);
                    } catch (Exception e) {
                        request.setAttribute("error", "❌ Ngày sản xuất không hợp lệ. Định dạng phải là yyyy-MM-dd.");
                        Product existing = dao.getProductById(id1);
                        request.setAttribute("mo", existing); // <-- fix ở đây
                        request.setAttribute("dataCate", dao.getCategory());
                        request.setAttribute("dataSup", dao.getAllSuppliers());
                        request.getRequestDispatcher("/WEB-INF/admin/product/edit-product.jsp").forward(request,
                                response);
                        return;
                    }

                    // Xác định có phải trái cây không
                    List<Category> allCate = dao.getCategory();
                    boolean isFruit = (categoryId == 3);
                    for (Category c : allCate) {
                        if (c.getCategoryID() == categoryId && c.getParentID() != null && c.getParentID() == 3) {
                            isFruit = true;
                            break;
                        }
                    }

                    double price = 0;
                    double stockQuantity = 0;
                    int unitPerBox = 1;
                    int unitsPerPack = 1; // Mặc định là 1
                    String boxUnitName = "thùng";
                    String itemUnitName = "lon";
                    String image;
                    Date expirationDate = null;
                    Double boxPriceValue = null;
                    Double unitPriceValue = null;
                    Double packPriceValue = null;

                    if (isFruit) {
                        // Validate các trường trái cây
                        if (fruitPriceStr == null || fruitPriceStr.trim().isEmpty()) {
                            request.setAttribute("error", "Vui lòng nhập giá cho trái cây.");
                            Product existing = dao.getProductById(id1);
                            request.setAttribute("mo", existing);
                            request.setAttribute("dataCate", dao.getCategory());
                            request.setAttribute("dataSup", dao.getAllSuppliers());
                            request.getRequestDispatcher("/WEB-INF/admin/product/edit-product.jsp").forward(request,
                                    response);
                            return;
                        }
                        if (fruitQtyStr == null || fruitQtyStr.trim().isEmpty()) {
                            request.setAttribute("error", "Vui lòng nhập số lượng (kg) cho trái cây.");
                            Product existing = dao.getProductById(id1);
                            request.setAttribute("mo", existing);
                            request.setAttribute("dataCate", dao.getCategory());
                            request.setAttribute("dataSup", dao.getAllSuppliers());
                            request.getRequestDispatcher("/WEB-INF/admin/product/edit-product.jsp").forward(request,
                                    response);
                            return;
                        }
                        if (fruitExpiryDaysStr == null || fruitExpiryDaysStr.trim().isEmpty()) {
                            request.setAttribute("error", "Vui lòng nhập hạn sử dụng (ngày) cho trái cây.");
                            Product existing = dao.getProductById(id1);
                            request.setAttribute("mo", existing);
                            request.setAttribute("dataCate", dao.getCategory());
                            request.setAttribute("dataSup", dao.getAllSuppliers());
                            request.getRequestDispatcher("/WEB-INF/admin/product/edit-product.jsp").forward(request,
                                    response);
                            return;
                        }
                        price = Double.parseDouble(fruitPriceStr);
                        stockQuantity = Double.parseDouble(fruitQtyStr);
                        int fruitExpiryDays = Integer.parseInt(fruitExpiryDaysStr);
                        // Tính ngày hết hạn
                        Calendar calFruit = Calendar.getInstance();
                        calFruit.setTime(manufactureDate);
                        calFruit.add(Calendar.DATE, fruitExpiryDays);
                        expirationDate = calFruit.getTime();
                        unitPerBox = 1;
                        boxUnitName = "kg";
                        itemUnitName = "kg";
                        unitsPerPack = 1; // Trái cây không có đơn vị trung gian
                        boxPriceValue = 0.0; // không áp dụng giá thùng
                        unitPriceValue = price; // giá theo kg
                        packPriceValue = null;
                    } else {
                        // Sản phẩm thường
                        double boxPrice = Double.parseDouble(boxPriceStr);
                        stockQuantity = Double.parseDouble(request.getParameter("stockQuantity"));

                        // Kiểm tra validation
                        if (boxPrice <= 0) {
                            request.setAttribute("error", "Giá 1 thùng phải lớn hơn 0.");
                            Product existing = dao.getProductById(id1);
                            request.setAttribute("mo", existing);
                            request.setAttribute("dataCate", dao.getCategory());
                            request.setAttribute("dataSup", dao.getAllSuppliers());
                            request.getRequestDispatcher("/WEB-INF/admin/product/edit-product.jsp").forward(request,
                                    response);
                            return;
                        }
                        if (stockQuantity <= 0) {
                            request.setAttribute("error", "Số lượng thùng phải lớn hơn 0.");
                            Product existing = dao.getProductById(id1);
                            request.setAttribute("mo", existing);
                            request.setAttribute("dataCate", dao.getCategory());
                            request.setAttribute("dataSup", dao.getAllSuppliers());
                            request.getRequestDispatcher("/WEB-INF/admin/product/edit-product.jsp").forward(request,
                                    response);
                            return;
                        }

                        // Lấy giá trị hiện tại từ database nếu có, nếu không thì đặt mặc định
                        Product existingProduct = dao.getProductById(id1);
                        if (existingProduct != null) {
                            if (existingProduct.getUnitPerBox() > 0) {
                                unitPerBox = existingProduct.getUnitPerBox();
                                price = boxPrice / unitPerBox; // đơn giá nhỏ nhất
                                unitPriceValue = price;
                                // lonToLoc = unitPerBox / unitsPerPack (nếu có)
                                if (existingProduct.getUnitsPerPack() > 0) {
                                    int lonToLoc = unitPerBox / existingProduct.getUnitsPerPack();
                                    packPriceValue = unitPriceValue * lonToLoc;
                                }
                            } else {
                                unitPerBox = 0; // chưa cấu hình
                                price = boxPrice; // lưu giá 1 thùng
                                unitPriceValue = null;
                                packPriceValue = null;
                            }
                            unitsPerPack = existingProduct.getUnitsPerPack(); // giữ nguyên (có thể = 0)
                            boxPriceValue = boxPrice;
                        } else {
                            unitPerBox = 0;
                            unitsPerPack = 0;
                            price = boxPrice;
                            boxPriceValue = boxPrice;
                            unitPriceValue = null;
                            packPriceValue = null;
                        }

                        // Đặt tên đơn vị mặc định
                        boxUnitName = "thùng";
                        itemUnitName = "lon"; // Mặc định là lon

                        int months = Integer.parseInt(expirySelect);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(manufactureDate);
                        cal.add(Calendar.MONTH, months);
                        expirationDate = cal.getTime();
                    }

                    // Kiểm tra hợp lệ ngày
                    Date today = new Date();
                    Calendar twoYearsAgo = Calendar.getInstance();
                    twoYearsAgo.add(Calendar.YEAR, -2);
                    String err = null;
                    if (manufactureDate.after(today)) {
                        err = "Ngày sản xuất không được ở tương lai.";
                    } else if (manufactureDate.before(twoYearsAgo.getTime())) {
                        err = "Ngày sản xuất không được quá 2 năm trước.";
                    } else if (expirationDate.before(today)) {
                        err = "Ngày hết hạn đã trôi qua.";
                    }
                    if (err != null) {
                        request.setAttribute("error", err);
                        Product existing = dao.getProductById(id1);
                        request.setAttribute("mo", existing);
                        request.setAttribute("dataCate", dao.getCategory());
                        request.setAttribute("dataSup", dao.getAllSuppliers());
                        request.getRequestDispatcher("/WEB-INF/admin/product/edit-product.jsp").forward(request,
                                response);
                        return;
                    }

                    // Xử lý ảnh
                    Part filePart1 = request.getPart("pImage");
                    String fileName1 = Paths.get(filePart1.getSubmittedFileName()).getFileName().toString();
                    if (!fileName1.isEmpty()) {
                        File uploadDir = new File(IMAGE_UPLOAD_DIR);
                        if (!uploadDir.exists()) {
                            uploadDir.mkdirs();
                        }
                        filePart1.write(IMAGE_UPLOAD_DIR + File.separator + fileName1);
                        image = fileName1;
                    } else {
                        Product existing = dao.getProductById(id1);
                        image = existing.getImageURL();
                    }

                    Timestamp createdAt = new Timestamp(System.currentTimeMillis());

                    Product product = new Product(id1, name, price, description, stockQuantity, image, itemUnitName,
                            createdAt,
                            manufactureDate, expirationDate);
                    product.setUnitPerBox(unitPerBox);
                    product.setBoxUnitName(boxUnitName);
                    product.setItemUnitName(itemUnitName);
                    product.setUnitsPerPack(unitsPerPack);
                    if (boxPriceValue != null)
                        product.setBoxPrice(boxPriceValue);
                    if (unitPriceValue != null)
                        product.setUnitPrice(unitPriceValue);
                    if (packPriceValue != null)
                        product.setPackPrice(packPriceValue);

                    Category category = new Category();
                    category.setCategoryID(categoryId);
                    product.setCategory(category);

                    Supplier supplier = new Supplier();
                    supplier.setSupplierID(supplierId);
                    product.setSupplier(supplier);

                    boolean result = dao.update(product);

                    if (result) {
                        response.sendRedirect(request.getContextPath() + "/admin/product");
                    } else {
                        request.setAttribute("error", "❌ Cập nhật sản phẩm thất bại.");
                        Product existing = dao.getProductById(id1);
                        request.setAttribute("mo", existing);
                        request.setAttribute("dataCate", dao.getCategory());
                        request.setAttribute("dataSup", dao.getAllSuppliers());
                        request.getRequestDispatcher("/WEB-INF/admin/product/edit-product.jsp").forward(request,
                                response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    request.setAttribute("error", "❌ Đã xảy ra lỗi khi cập nhật sản phẩm.");
                    try {
                        int id1 = Integer.parseInt(request.getParameter("id"));
                        Product existing = dao.getProductById(id1);
                        request.setAttribute("mo", existing);
                        request.setAttribute("dataCate", dao.getCategory());
                        request.setAttribute("dataSup", dao.getAllSuppliers());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    request.getRequestDispatcher("/WEB-INF/admin/product/edit-product.jsp").forward(request, response);
                }
                break;

            case "updateUnitConversion":
                try {
                    // Set response type to JSON
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    PrintWriter jsonOut = response.getWriter();

                    // Get parameters from request
                    String productIdStr = request.getParameter("productId");
                    String thungToLonStr = request.getParameter("thungToLon");
                    String lonToLocStr = request.getParameter("lonToLoc");

                    // Validate parameters
                    if (productIdStr == null || thungToLonStr == null) {
                        jsonOut.print("{\"success\": false, \"message\": \"Thiếu thông tin cần thiết\"}");
                        return;
                    }

                    int productId = Integer.parseInt(productIdStr);
                    int thungToLon = Integer.parseInt(thungToLonStr);
                    int lonToLoc = Integer.parseInt(lonToLocStr != null && !lonToLocStr.isEmpty() ? lonToLocStr : "0");

                    // Validate business logic
                    if (thungToLon <= 0) {
                        jsonOut.print(
                                "{\"success\": false, \"message\": \"Số lượng lon trong 1 thùng phải lớn hơn 0\"}");
                        return;
                    }

                    if (lonToLoc > 0 && lonToLoc >= thungToLon) {
                        jsonOut.print(
                                "{\"success\": false, \"message\": \"Số lượng lon trong 1 lốc phải nhỏ hơn số lượng lon trong 1 thùng\"}");
                        return;
                    }

                    // Get existing product to update
                    Product existingProduct = dao.getProductById(productId);
                    if (existingProduct == null) {
                        jsonOut.print("{\"success\": false, \"message\": \"Không tìm thấy sản phẩm\"}");
                        return;
                    }
                    // Capture previous state before any mutation
                    int prevUnitPerBox = 0;
                    try {
                        prevUnitPerBox = existingProduct.getUnitPerBox();
                    } catch (Exception ignore) {
                    }
                    double prevStockQuantity = existingProduct.getStockQuantity();

                    // Update the product with new conversion rates (kept in model for
                    // compatibility)
                    existingProduct.setUnitPerBox(thungToLon);

                    // Tính toán số lượng lốc trong 1 thùng
                    // Nếu lonToLoc > 0 (có đơn vị trung gian), thì 1 thùng = thungToLon/lonToLoc
                    // lốc
                    // Ví dụ: 1 thùng = 24 lon, 6 lon = 1 lốc => 1 thùng = 24/6 = 4 lốc
                    int soLocTrongThung = 0;
                    if (lonToLoc > 0) {
                        // Kiểm tra xem có chia hết không
                        if (thungToLon % lonToLoc == 0) {
                            soLocTrongThung = thungToLon / lonToLoc;
                        } else {
                            // Nếu không chia hết, thông báo lỗi
                            jsonOut.print("{\"success\": false, \"message\": \"Số lượng lon trong 1 thùng ("
                                    + thungToLon + ") phải chia hết cho số lon trong 1 lốc (" + lonToLoc
                                    + "). Hiện tại sẽ bị dư " + (thungToLon % lonToLoc) + " lon.\"}");
                            return;
                        }
                    }
                    existingProduct.setUnitsPerPack(soLocTrongThung);

                    // Update price calculation based on new unitPerBox
                    // Xác định giá 1 thùng hiện tại dựa trên trạng thái hiện có:
                    // - Nếu unitPerBox > 0: price = giá 1 đơn vị nhỏ nhất (lon) => giá 1 thùng =
                    // price * unitPerBox
                    // - Nếu unitPerBox = 0: price = giá 1 thùng
                    double currentBoxPrice;
                    if (existingProduct.getBoxPrice() > 0) {
                        currentBoxPrice = existingProduct.getBoxPrice();
                    } else if (existingProduct.getUnitPerBox() > 0) {
                        currentBoxPrice = existingProduct.getPrice() * existingProduct.getUnitPerBox();
                    } else {
                        currentBoxPrice = existingProduct.getPrice();
                    }

                    // Tính lại giá 1 lon dựa trên giá 1 thùng và số lon mới
                    // Ví dụ: Giá 1 thùng = 240,000, unitPerBox = 1 → Giá 1 lon = 240,000
                    // Sau khi chuyển đổi: unitPerBox = 24 → Giá 1 lon = 240,000 / 24 = 10,000
                    double newUnitPrice = currentBoxPrice / thungToLon;
                    existingProduct.setPrice(newUnitPrice); // giữ tương thích cũ
                    // Persist conversion and prices in dedicated table
                    String boxUnitName = existingProduct.getBoxUnitName() != null ? existingProduct.getBoxUnitName()
                            : "thùng";
                    String itemUnitName = existingProduct.getItemUnitName() != null ? existingProduct.getItemUnitName()
                            : "lon";
                    Double packPrice = null;
                    if (soLocTrongThung > 0) {
                        int lonToLocForPrice = thungToLon / soLocTrongThung; // = lonToLoc
                        packPrice = newUnitPrice * lonToLocForPrice;
                    }
                    boolean upsertOk = dao.upsertUnitConversion(existingProduct.getProductID(),
                            thungToLon, soLocTrongThung, boxUnitName, itemUnitName,
                            currentBoxPrice, newUnitPrice, packPrice);
                    if (!upsertOk) {
                        jsonOut.print("{\"success\": false, \"message\": \"Không thể lưu chuyển đổi\"}");
                        return;
                    }

                    // Save to database
                    boolean updateResult = dao.update(existingProduct);

                    // Auto open 1 box whenever conversion value changes (and stock allows)
                    int autoOpenedBoxes = 0;
                    if (updateResult && prevUnitPerBox != thungToLon && prevStockQuantity >= 1) {
                        if (dao.decrementProductStock(productId, 1)) {
                            existingProduct.setStockQuantity(prevStockQuantity - 1);
                            autoOpenedBoxes = 1;
                        }
                    }

                    if (updateResult) {
                        jsonOut.print(
                                "{\"success\": true, \"message\": \"Cập nhật chuyển đổi đơn vị thành công\", \"autoOpenedBoxes\": "
                                        + autoOpenedBoxes + ", \"newStockQuantity\": "
                                        + existingProduct.getStockQuantity() + "}");
                    } else {
                        jsonOut.print("{\"success\": false, \"message\": \"Cập nhật thất bại\"}");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    response.setContentType("application/json");
                    response.setCharacterEncoding("UTF-8");
                    PrintWriter jsonOut = response.getWriter();
                    jsonOut.print("{\"success\": false, \"message\": \"Đã xảy ra lỗi: \" + e.getMessage()}");
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
