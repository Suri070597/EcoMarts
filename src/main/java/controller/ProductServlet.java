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
                List<Product> list = dao.getAll();
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
                    double boxPrice = Double.parseDouble(request.getParameter("boxPrice"));
                    int boxQuantity = Integer.parseInt(request.getParameter("boxQuantity"));
                    int unitPerBox = Integer.parseInt(request.getParameter("unitPerBox"));
                    String boxUnitName = request.getParameter("boxUnitName");
                    String itemUnitName = request.getParameter("itemUnitName");
                    String pDescription = request.getParameter("pDescription");

                    int categoryID = Integer.parseInt(request.getParameter("categoryID"));
                    int supplierID = Integer.parseInt(request.getParameter("supplierID"));

                    String manufactureDateStr = request.getParameter("manufactureDate");
                    String expiryMonthsStr = request.getParameter("expirySelect");

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date manufactureDate = sdf.parse(manufactureDateStr);

                    int months = Integer.parseInt(expiryMonthsStr);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(manufactureDate);
                    cal.add(Calendar.MONTH, months);
                    Date expirationDate = cal.getTime();

                    Date today = new Date();
                    if (manufactureDate.after(today)) {
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

                    if (expirationDate.before(today)) {
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

                    // Tính số lượng lẻ và giá lẻ
                    int stockQuantity = boxQuantity * unitPerBox;
                    double price = boxPrice / unitPerBox;

                    int res = dao.insert(pName, price, pDescription, stockQuantity, pImage, itemUnitName,
                            createdAt, categoryID, supplierID, manufactureDate, expirationDate,
                            unitPerBox, boxUnitName, itemUnitName);

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
                    request.setAttribute("error", "❌ Đã xảy ra lỗi khi tạo sản phẩm.");
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
                    double boxPrice = Double.parseDouble(request.getParameter("boxPrice"));
                    int boxQuantity = Integer.parseInt(request.getParameter("boxQuantity"));
                    int unitPerBox = Integer.parseInt(request.getParameter("unitPerBox"));
                    String boxUnitName = request.getParameter("boxUnitName");
                    String itemUnitName = request.getParameter("itemUnitName");
                    String description = request.getParameter("pDescription");
                    int categoryId = Integer.parseInt(request.getParameter("categoryID"));
                    int supplierId = Integer.parseInt(request.getParameter("supplierID"));
                    String manufactureDateStr1 = request.getParameter("manufactureDate");
                    String expirySelect = request.getParameter("expirySelect");

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
                        request.setAttribute("dataCate", dao.getCategory());
                        request.setAttribute("dataSup", dao.getAllSuppliers());
                        request.getRequestDispatcher("/WEB-INF/admin/product/edit-product.jsp").forward(request,
                                response);
                        return;
                    }

                    // Tính ngày hết hạn
                    int months = Integer.parseInt(expirySelect);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(manufactureDate);
                    cal.add(Calendar.MONTH, months);
                    Date expirationDate = cal.getTime();

                    Date today = new Date();
                    Calendar twoYearsAgo = Calendar.getInstance();
                    twoYearsAgo.add(Calendar.YEAR, -2);

                    // Kiểm tra hợp lệ ngày
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
                        request.setAttribute("mo", existing); // <-- fix ở đây
                        request.setAttribute("dataCate", dao.getCategory());
                        request.setAttribute("dataSup", dao.getAllSuppliers());
                        request.setAttribute("dataCate", dao.getCategory());
                        request.setAttribute("dataSup", dao.getAllSuppliers());
                        request.getRequestDispatcher("/WEB-INF/admin/product/edit-product.jsp").forward(request,
                                response);
                        return;
                    }

                    // Xử lý ảnh
                    Part filePart1 = request.getPart("pImage");
                    String fileName1 = Paths.get(filePart1.getSubmittedFileName()).getFileName().toString();
                    String image;

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

                    // Tính số lượng lẻ và giá lẻ
                    int stockQuantity = boxQuantity * unitPerBox;
                    double price = boxPrice / unitPerBox;

                    Product product = new Product(id1, name, price, description, stockQuantity, image, itemUnitName,
                            createdAt,
                            manufactureDate, expirationDate);
                    product.setUnitPerBox(unitPerBox);
                    product.setBoxUnitName(boxUnitName);
                    product.setItemUnitName(itemUnitName);

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
                        request.setAttribute("mo", existing); // <-- fix ở đây
                        request.setAttribute("dataCate", dao.getCategory());
                        request.setAttribute("dataSup", dao.getAllSuppliers());
                        request.setAttribute("dataCate", dao.getCategory());
                        request.setAttribute("dataSup", dao.getAllSuppliers());
                        request.getRequestDispatcher("/WEB-INF/admin/product/edit-product.jsp").forward(request,
                                response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    request.setAttribute("error", "❌ Đã xảy ra lỗi khi cập nhật sản phẩm.");
                    request.getRequestDispatcher("/WEB-INF/admin/product/edit-product.jsp").forward(request, response);
                }
                break;

        }
    }

    @Override
    public String getServletInfo() {
        return "ProductServlet handles CRUD for products";
    }
}
