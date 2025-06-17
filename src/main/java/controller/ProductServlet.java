package controller;

import dao.productDAO;
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
import java.util.List;
import model.Category;
import model.Product;
import model.Supplier;

@WebServlet(name = "ProductServlet", urlPatterns = {"/admin/product"})
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
        productDAO dao = new productDAO();
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
                    request.getRequestDispatcher("/WEB-INF/admin/product/delete-product.jsp").forward(request, response);
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
            default:
                String keyword = request.getParameter("search");
//                List<Product> list;
                if (keyword != null && !keyword.trim().isEmpty()) {
                    list = dao.searchProductsByName(keyword);
                    request.setAttribute("keyword", keyword);
                } else {
                    list = dao.getAll();
                }
                request.setAttribute("dataCate", listCategory);
                request.setAttribute("data", list);

                request.getRequestDispatcher("/WEB-INF/admin/product/product.jsp").forward(request, response);
                break;

        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        productDAO dao = new productDAO();
        String action = request.getParameter("action");
        PrintWriter out = response.getWriter();
        if (action == null) {
            response.sendRedirect("/admin/product");
            return;
        }

        switch (action) {
            case "create":
                String pName = request.getParameter("pName");
                double pPrice = Double.parseDouble(request.getParameter("pPrice"));
                int pQuantity = Integer.parseInt(request.getParameter("pQuanity"));
                String pUnit = request.getParameter("pUnit");
                String pDescription = request.getParameter("pDescription");

                int categoryID = Integer.parseInt(request.getParameter("categoryID"));
                int supplierID = Integer.parseInt(request.getParameter("supplierID"));

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
                Timestamp date = new Timestamp(System.currentTimeMillis());

                int res = dao.insert(pName, pPrice, pDescription, pQuantity, pImage, pUnit, date, categoryID, supplierID);

                if (res == 1) {
                    response.sendRedirect(request.getContextPath() + "/admin/product");
                } else {
                    response.sendRedirect(request.getContextPath() + "/admin/product?action=create");
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
                int id1 = Integer.parseInt(request.getParameter("id"));
                String name = request.getParameter("pName");
                double price = Double.parseDouble(request.getParameter("pPrice"));
                int quantity = Integer.parseInt(request.getParameter("pQuantity"));
                String unit = request.getParameter("pUnit");
                String description = request.getParameter("pDescription");
                int categoryId = Integer.parseInt(request.getParameter("categoryID"));
                int supplierId = Integer.parseInt(request.getParameter("supplierID"));

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
                Product product = new Product(id1, name, price, description, quantity, image, unit, createdAt);

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
                    response.sendRedirect(request.getContextPath() + "/admin/product?action=update&id=" + id1);
                }
                break;
        }
    }

    @Override
    public String getServletInfo() {
        return "ProductServlet handles CRUD for products";
    }
}
