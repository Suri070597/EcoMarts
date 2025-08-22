/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.ManufacturerDAO;
import dao.ProductDAO;
import dao.StockDAO;
import dao.SupplierDAO;
import jakarta.servlet.RequestDispatcher;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.List;
import model.Manufacturer;
import model.StockIn;
import model.StockInDetail;
import model.Supplier;

/**
 *
 * @author ADMIN
 */
@WebServlet(name = "AdminInventoryServlet", urlPatterns = {"/admin/inventory"})
public class AdminInventoryServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String service = request.getParameter("service");

        if (service == null) {
            service = "listInventory";
        }

        if (service.equals("listInventory")) {
            String supplierIdStr = request.getParameter("supplierId");
            StockDAO stockDAO = new StockDAO();
            ManufacturerDAO manufacturerDAO = new ManufacturerDAO();

            try {
                //Lấy danh sách inventory            
                List<StockIn> stockIns ;

                if (supplierIdStr != null && !supplierIdStr.isEmpty()) {
                    int supplierId = Integer.parseInt(supplierIdStr);
                    stockIns = stockDAO.getStockInByManufacturer(supplierId);
                    request.setAttribute("supplierId", supplierId);
                } else {
                    stockIns = stockDAO.getAllStockIns();
                }

                // Với mỗi StockIn, load chi tiết
                for (StockIn s : stockIns) {
                    List<StockInDetail> details = stockDAO.getDetailsByStockInID(s.getStockInID());
                    s.setDetails(details); // StockIn có field List<StockInDetail> details
                }

                List<Manufacturer> supplierList = manufacturerDAO.getAllManufacturers();

                request.setAttribute("suppliers", supplierList);
                request.setAttribute("stockIns", stockIns);
                RequestDispatcher dispatcher = request.getRequestDispatcher(
                        "/WEB-INF/admin/inventory/inventory.jsp"
                );
                dispatcher.forward(request, response);
            } catch (Exception e) {
                // Xử lý lỗi theo MVC pattern
                request.setAttribute("errorMessage", "Lỗi khi tải dữ liệu: " + e.getMessage());
                request.setAttribute("errorDetails", e.toString());

                // Forward đến trang lỗi hoặc trang hiện tại với thông báo lỗi
                RequestDispatcher dispatcher = request.getRequestDispatcher(
                        "/WEB-INF/admin/inventory/inventory.jsp"
                );
                dispatcher.forward(request, response);
            }
        }

        if (service.equals("detail")) {
            String idStr = request.getParameter("id");
            if (idStr != null) {
                try {
                    int stockInID = Integer.parseInt(idStr);

                    StockDAO stockDAO = new StockDAO();

                    // Lấy StockIn theo ID
                    StockIn stock = stockDAO.getStockInByID(stockInID);

                    if (stock != null) {
                        // Lấy chi tiết phiếu nhập
                        List<StockInDetail> details = stockDAO.getDetailsByStockInID(stockInID);
                        stock.setDetails(details);

                        request.setAttribute("stock", stock);
                    } else {
                        request.setAttribute("errorMessage", "Không tìm thấy phiếu nhập với ID: " + stockInID);
                    }

                    int totalQuantity = 0;
                    long totalPrice = 0;
                    for (StockInDetail d : stock.getDetails()) {
                        totalQuantity += d.getQuantity();
                        totalPrice += d.getQuantity() * d.getUnitPrice();
                    }

                    request.setAttribute("totalQuantity", totalQuantity);
                    request.setAttribute("totalPrice", totalPrice);

                } catch (NumberFormatException | SQLException e) {
                    request.setAttribute("errorMessage", "Lỗi khi tải dữ liệu: " + e.getMessage());
                }
            } else {
                request.setAttribute("errorMessage", "ID phiếu nhập không hợp lệ.");
            }

            // Forward đến trang chi tiết
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/admin/inventory/inventory-detail.jsp");
            dispatcher.forward(request, response);
        }

        if (service.equals("approve")) {
            String stockInIdStr = request.getParameter("id");
            int stockInID = Integer.parseInt(stockInIdStr);

            StockDAO stockDAO = new StockDAO();

            try {
                // Lấy chi tiết StockIn
                List<StockInDetail> details = stockDAO.getDetailsByStockInID(stockInID);

                // Approve
                stockDAO.approveStockIn(stockInID, details);

                // Redirect về danh sách với thông báo thành công
                response.sendRedirect(request.getContextPath() + "/admin/inventory?message=approved");
            } catch (SQLException e) {
                e.printStackTrace();
                // Forward tới trang lỗi hoặc hiển thị thông báo
                request.setAttribute("errorMessage", "Không thể duyệt phiếu nhập: " + e.getMessage());
                RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/admin/inventory/inventory.jsp");
                dispatcher.forward(request, response);
            }
        }

        if (service.equals("reject")) {
            String stockInIdStr = request.getParameter("id");
            int stockInID = Integer.parseInt(stockInIdStr);

            StockDAO stockDAO = new StockDAO();

            try {
                // Lấy chi tiết StockIn
                List<StockInDetail> details = stockDAO.getDetailsByStockInID(stockInID);

                // Approve
                stockDAO.rejectStockIn(stockInID, details);

                // Redirect về danh sách với thông báo thành công
                response.sendRedirect(request.getContextPath() + "/admin/inventory?message=rejected");
            } catch (SQLException e) {
                e.printStackTrace();
                // Forward tới trang lỗi hoặc hiển thị thông báo
                request.setAttribute("errorMessage", "Không thể duyệt phiếu nhập: " + e.getMessage());
                RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/admin/inventory/inventory.jsp");
                dispatcher.forward(request, response);
            }
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
