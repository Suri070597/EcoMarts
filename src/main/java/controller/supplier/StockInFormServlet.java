package controller.supplier;

import dao.CategoryDAO;
import dao.ProductDAO;
import dao.StockDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import model.Product;
import com.google.gson.Gson;
import dao.AccountDAO;
import dao.InventoryDAO;
import dao.ManufacturerDAO;
import java.sql.Date;
import java.util.ArrayList;
import model.Account;
import model.Inventory;
import model.Manufacturer;
import model.StockIn;
import model.StockInDetail;

@WebServlet(name = "StockInFormServlet", urlPatterns = { "/staff/stockin" })
public class StockInFormServlet extends HttpServlet {

    // Khai báo các DAO làm thuộc tính của servlet
    private ProductDAO productDAO;
    private ManufacturerDAO manufactureDAO;
    private AccountDAO accountDAO;
    private CategoryDAO categoryDAO;
    private InventoryDAO inventoryDAO;
    private StockDAO stockDAO;

    @Override
    public void init() throws ServletException {
        super.init(); // Đảm bảo servlet container khởi tạo đầy đủ
        productDAO = new ProductDAO();
        manufactureDAO = new ManufacturerDAO();
        accountDAO = new AccountDAO();
        categoryDAO = new CategoryDAO();
        inventoryDAO = new InventoryDAO();
        stockDAO = new StockDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        // Nếu là AJAX search
        if ("search".equalsIgnoreCase(action)) {
            String keyword = request.getParameter("keyword");
            List<Product> products = productDAO.searchProductsByName1(keyword);

            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(new Gson().toJson(products));
            return; // Dừng ở đây, không forward JSP
        }

        try {
            // ===== PHẦN 1: LẤY DỮ LIỆU TỪ DATABASE (Model Layer) =====
            // 1.3. Lấy danh sách nhà cung cấp
            List<Manufacturer> manufactureList = manufactureDAO.getManufacturersByStatus(1);

            // 1.4. Lấy danh sách admin
            List<Account> adminList = accountDAO.getAccountsByRole(1);

            // ===== PHẦN 2: GỬI DỮ LIỆU ĐẾN JSP (View Layer) =====
            // 2.3. Gửi dữ liệu nhà cung cấp và người nhận TRƯỚC
            request.setAttribute("suppliers", manufactureList);
            request.setAttribute("receivers", adminList);

            // 2.3. Gửi thông tin bổ sung cho JSP
            request.setAttribute("totalSuppliers", manufactureList.size());
            request.setAttribute("totalReceivers", adminList.size());

            // 1.4. Lấy danh sách inventory (SAU KHI ĐÃ SET SUPPLIERS/RECEIVERS)
            try {
                List<StockIn> stockIns = stockDAO.getAllStockIns();
                if (stockIns != null) {
                    for (StockIn s : stockIns) {
                        try {
                            List<StockInDetail> details = stockDAO.getDetailsByStockInID(s.getStockInID());
                            s.setDetails(details);
                        } catch (Exception e) {
                            s.setDetails(new ArrayList<>());
                        }
                    }
                    request.setAttribute("stockIns", stockIns);
                } else {
                    request.setAttribute("stockIns", new ArrayList<>());
                }
            } catch (Exception e) {
                request.setAttribute("stockIns", new ArrayList<>());
            }

            // ===== PHẦN 3: FORWARD ĐẾN JSP =====
            RequestDispatcher dispatcher = request.getRequestDispatcher(
                    "/WEB-INF/staff/Warehouse_management/stockin-form.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            // Xử lý lỗi theo MVC pattern
            request.setAttribute("errorMessage", "Lỗi khi tải dữ liệu: " + e.getMessage());
            request.setAttribute("errorDetails", e.toString());

            // Forward đến trang lỗi hoặc trang hiện tại với thông báo lỗi
            RequestDispatcher dispatcher = request.getRequestDispatcher(
                    "/WEB-INF/staff/Warehouse_management/stockin-form.jsp");
            dispatcher.forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Xử lý submit phiếu nhập

        // Đọc dạng mảng nhiều dòng
        String[] productIds = request.getParameterValues("productId");
        String[] quantities = request.getParameterValues("quantity");
        String[] prices = request.getParameterValues("price");
        String[] packageTypes = request.getParameterValues("packageType");
        // Bỏ packSizes - không cần thiết
        String[] lotNumbers = request.getParameterValues("lotNumber");
        String[] manufactureDates = request.getParameterValues("manufactureDate");
        String[] expiryDates = request.getParameterValues("expiryDate");
        String supplierIdStr = request.getParameter("supplierId");
        String receiverIdStr = request.getParameter("receiverId");
        String dateStr = request.getParameter("date");
        String note = request.getParameter("note");

        //

        // Kiểm tra dữ liệu đầu vào
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
            int supplierID = Integer.parseInt(request.getParameter("supplierId"));
            int receiverID = Integer.parseInt(request.getParameter("receiverId"));

            StockIn stock = new StockIn(supplierID, receiverID, java.sql.Date.valueOf(dateStr), note);
            stock.setStatus("Pending");

            List<Inventory> invList = new ArrayList<>();
            List<StockInDetail> detailList = new ArrayList<>();

            for (int i = 0; i < productIds.length; i++) {
                // Kiểm tra bảo vệ để tránh ArrayIndexOutOfBoundsException
                if (i >= quantities.length || i >= prices.length || i >= packageTypes.length) {
                    System.err.println("Array length mismatch at index " + i);
                    System.err.println("productIds: " + productIds.length + ", quantities: " + quantities.length +
                            ", prices: " + prices.length + ", packageTypes: " + packageTypes.length);
                    throw new IllegalArgumentException("Array length mismatch");
                }

                int pid = Integer.parseInt(productIds[i]);
                double qty = Double.parseDouble(quantities[i]);
                double price = Double.parseDouble(prices[i]);
                String pkgType = packageTypes[i];
                // Bỏ packSize - không cần thiết
                String lotNumber = lotNumbers != null && i < lotNumbers.length ? lotNumbers[i] : null;
                Date manufactureDate = manufactureDates != null && i < manufactureDates.length
                        && !manufactureDates[i].trim().isEmpty()
                                ? Date.valueOf(manufactureDates[i])
                                : null;
                Date expiryDate = expiryDates != null && i < expiryDates.length && !expiryDates[i].trim().isEmpty()
                        ? Date.valueOf(expiryDates[i])
                        : null;

                //

                // Sử dụng constructor đúng: (productID, packageType, quantity, unitPrice,
                // costPrice)
                Inventory inv = new Inventory(pid, pkgType, qty, null, price);
                StockInDetail detail = new StockInDetail(qty, price);

                // Set các field cần thiết cho StockInDetail
                detail.setProductID(pid);
                detail.setPackageType(pkgType);
                // Bỏ PackSize - không cần thiết
                detail.setLotNumber(lotNumber);
                detail.setManufactureDate(manufactureDate);
                detail.setExpiryDate(expiryDate);

                invList.add(inv);
                detailList.add(detail);
            }

            if (!stockDAO.testConnection()) {
                throw new SQLException("Database connection test failed");
            }

            // Sử dụng stockDAO đã được khởi tạo trong init() thay vì tạo mới
            stockDAO.createStockInFull(stock, invList, detailList);

            response.sendRedirect(request.getContextPath() + "/staff/stockin?success=1");

        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Dữ liệu không hợp lệ: " + e.getMessage());
            doGet(request, response);
        } catch (SQLException e) {
            request.setAttribute("errorMessage", "Lỗi cơ sở dữ liệu: " + e.getMessage());
            request.setAttribute("errorMessage", "Lỗi cơ sở dữ liệu: " + e.getMessage());
            doGet(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Lỗi không xác định: " + e.getMessage());
            request.setAttribute("errorMessage", "Lỗi không xác định: " + e.getMessage());
            doGet(request, response);
        }
    }
}
