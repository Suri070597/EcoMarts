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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import model.Category;
import model.Product;
import model.Supplier;
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

@WebServlet(name = "StockInFormServlet", urlPatterns = {"/staff/stockin"})
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
// 1.4. Lấy danh sách inventory            
            List<StockIn> stockIns = stockDAO.getAllStockIns();

            
            if (action != null && action.equals("searchInventory")) {
                String keyword = request.getParameter("keyword");
                if (keyword != null && !keyword.trim().isEmpty()) {
                    // Tìm theo mã nhập, tên nhà cung cấp, hoặc tên người nhận
                    stockIns = stockDAO.searchStockIn(keyword.trim());
                }
            }

            // Với mỗi StockIn, load chi tiết
            for (StockIn s : stockIns) {
                List<StockInDetail> details = stockDAO.getDetailsByStockInID(s.getStockInID());
                s.setDetails(details); // StockIn có field List<StockInDetail> details
            }

            request.setAttribute("stockIns", stockIns);

            // ===== PHẦN 2: GỬI DỮ LIỆU ĐẾN JSP (View Layer) =====
            // 2.3. Gửi dữ liệu nhà cung cấp và người nhận
            request.setAttribute("suppliers", manufactureList);
            request.setAttribute("receivers", adminList);

            // 2.3. Gửi thông tin bổ sung cho JSP
            request.setAttribute("totalSuppliers", manufactureList.size());
            request.setAttribute("totalReceivers", adminList.size());

            // ===== PHẦN 3: FORWARD ĐẾN JSP =====
            RequestDispatcher dispatcher = request.getRequestDispatcher(
                    "/WEB-INF/staff/Warehouse_management/stockin-form.jsp"
            );
            dispatcher.forward(request, response);

        } catch (Exception e) {
            // Xử lý lỗi theo MVC pattern
            request.setAttribute("errorMessage", "Lỗi khi tải dữ liệu: " + e.getMessage());
            request.setAttribute("errorDetails", e.toString());

            // Forward đến trang lỗi hoặc trang hiện tại với thông báo lỗi
            RequestDispatcher dispatcher = request.getRequestDispatcher(
                    "/WEB-INF/staff/Warehouse_management/stockin-form.jsp"
            );
            dispatcher.forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Đọc dạng mảng nhiều dòng
        String[] productIds = request.getParameterValues("productId");
        String[] quantities = request.getParameterValues("quantity");
        String[] prices = request.getParameterValues("price");
        String[] packageTypes = request.getParameterValues("packageType");
        String[] packSizes = request.getParameterValues("packSize");
        String[] expiryDates = request.getParameterValues("expiryDate");
        String supplierIdStr = request.getParameter("supplierId");
        String receiverIdStr = request.getParameter("receiverId");
        String dateStr = request.getParameter("date");
        String note = request.getParameter("note");

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
            System.out.println("SupplierID: " + supplierID + ", ReceiverID: " + receiverID);

            StockIn stock = new StockIn(supplierID, receiverID, java.sql.Date.valueOf(dateStr), note);
            System.out.println("Created StockIn: " + stock);

            List<Inventory> invList = new ArrayList<>();
            List<StockInDetail> detailList = new ArrayList<>();

            for (int i = 0; i < productIds.length; i++) {
                int pid = Integer.parseInt(productIds[i]);
                double qty = Double.parseDouble(quantities[i]);
                double price = Double.parseDouble(prices[i]);
                String pkgType = packageTypes[i];
                int pSize = Integer.parseInt(packSizes[i]);
                Date expiryDate = null;
                if (expiryDates != null && expiryDates[i] != null && !expiryDates[i].trim().isEmpty()) {
                    expiryDate = Date.valueOf(expiryDates[i]); // chuyển từ String sang java.sql.Date
                }

                System.out.println("Processing productId=" + pid
                        + ", qty=" + qty
                        + ", price=" + price
                        + ", pkgType=" + pkgType
                        + ", packSize=" + pSize);

                Inventory inv = new Inventory(pid, pkgType, qty, price, pSize, Date.valueOf(dateStr));
                StockInDetail detail = new StockInDetail(qty, price, expiryDate);

                invList.add(inv);
                detailList.add(detail);
            }

            System.out.println("Inventory list size: " + invList.size());
            System.out.println("Detail list size: " + detailList.size());

            StockDAO dao = new StockDAO();
            dao.createStockInFull(stock, invList, detailList);
            System.out.println("StockIn transaction created successfully.");

            response.sendRedirect(request.getContextPath() + "/staff/stockin?success=1");

        } catch (NumberFormatException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Dữ liệu không hợp lệ: " + e.getMessage());
            doGet(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi cơ sở dữ liệu: " + e.getMessage());
            doGet(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi không xác định: " + e.getMessage());
            doGet(request, response);
        }
    }
}
