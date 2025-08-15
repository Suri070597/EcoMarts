package controller.supplier;

import dao.CategoryDAO;
import dao.ProductDAO;
import dao.ReceiverDAO;
import dao.SupplierDAO;
import dao.StockDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.Category;
import model.Product;
import model.Supplier;
import model.Receiver;
import com.google.gson.Gson;

@WebServlet(name = "StockInFormServlet", urlPatterns = {"/staff/stockin"})
public class StockInFormServlet extends HttpServlet {

    private ProductDAO productDAO;
    private SupplierDAO supplierDAO;
    private ReceiverDAO receiverDAO;
    private CategoryDAO categoryDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        productDAO = new ProductDAO();
        supplierDAO = new SupplierDAO();
        receiverDAO = new ReceiverDAO();
        categoryDAO = new CategoryDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // 1. Lấy danh mục
            List<Category> categoryList = categoryDAO.getAllCategories();

            // 2. Lấy sản phẩm theo từng danh mục
            Map<Category, List<Product>> productsGrouped = new LinkedHashMap<>();
            List<Product> allProducts = new ArrayList<>();
            for (Category category : categoryList) {
                List<Product> products = productDAO.getProductsByCategory(category.getCategoryID());
                productsGrouped.put(category, products);
                allProducts.addAll(products);
            }

            // 3. Gửi danh sách đầy đủ sản phẩm để <select> hiển thị ngay
            request.setAttribute("products", allProducts);

            // 4. Nhà cung cấp & Người nhận
            List<Supplier> supplierList = supplierDAO.getAllSuppliers();
            List<Receiver> receiverList = receiverDAO.getAllReceivers();

            // 5. Set attributes cho JSP
            request.setAttribute("categories", categoryList);
            request.setAttribute("productsGrouped", productsGrouped);
            request.setAttribute("suppliers", supplierList);
            request.setAttribute("receivers", receiverList);

            // 6. Gửi dữ liệu JSON cho JavaScript lọc
            try {
                Gson gson = new Gson();
                String productsGroupedJson = gson.toJson(productsGrouped);
                request.setAttribute("productsGroupedJson", productsGroupedJson);
            } catch (Exception e) {
                request.setAttribute("productsGroupedJson", "{}");
                e.printStackTrace();
            }

            // 7. Thống kê số lượng
            request.setAttribute("totalCategories", categoryList.size());
            request.setAttribute("totalProducts", allProducts.size());
            request.setAttribute("totalSuppliers", supplierList.size());
            request.setAttribute("totalReceivers", receiverList.size());

            // 8. Forward đến JSP
            RequestDispatcher dispatcher = request.getRequestDispatcher(
                    "/WEB-INF/staff/Warehouse_management/stockin-form.jsp"
            );
            dispatcher.forward(request, response);

        } catch (Exception e) {
            request.setAttribute("errorMessage", "Lỗi khi tải dữ liệu: " + e.getMessage());
            request.setAttribute("errorDetails", e.toString());
            RequestDispatcher dispatcher = request.getRequestDispatcher(
                    "/WEB-INF/staff/Warehouse_management/stockin-form.jsp"
            );
            dispatcher.forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String[] productIds = request.getParameterValues("productId");
        String[] quantities = request.getParameterValues("quantity");
        String supplierIdStr = request.getParameter("supplierId");
        String receiverIdStr = request.getParameter("receiverId");

        if (productIds == null || productIds.length == 0) {
            request.setAttribute("errorMessage", "Vui lòng chọn ít nhất một sản phẩm");
            doGet(request, response);
            return;
        }

        if (supplierIdStr == null || supplierIdStr.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng chọn nhà cung cấp");
            doGet(request, response);
            return;
        }

        if (receiverIdStr == null || receiverIdStr.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Vui lòng chọn người nhận");
            doGet(request, response);
            return;
        }

        try {
            int supplierId = Integer.parseInt(supplierIdStr);
            int receiverId = Integer.parseInt(receiverIdStr);
            StockDAO stockDAO = new StockDAO();

            for (int i = 0; i < productIds.length; i++) {
                if (productIds[i] == null || productIds[i].trim().isEmpty()) {
                    continue;
                }

                int productId = Integer.parseInt(productIds[i]);
                double quantity = 0.0;

                if (quantities != null && i < quantities.length && quantities[i] != null && !quantities[i].trim().isEmpty()) {
                    quantity = Double.parseDouble(quantities[i]);
                }

                if (quantity <= 0) {
                    continue;
                }

                stockDAO.addStockIn(productId, quantity, supplierId, receiverId);
            }

            response.sendRedirect(request.getContextPath() + "/staff/stockin");

        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Dữ liệu không hợp lệ: " + e.getMessage());
            doGet(request, response);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Lỗi cơ sở dữ liệu: " + e.getMessage());
            doGet(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Lỗi không xác định: " + e.getMessage());
            doGet(request, response);
        }
    }
}
