package controller.supplier;

import dao.SupplierDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;
import model.Supplier;

@WebServlet(urlPatterns = {"/admin/supplier"})
public class SupplierServlet extends HttpServlet {
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
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet NewServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet NewServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
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
        String view = request.getParameter("view");
        String action = request.getParameter("action");
        SupplierDAO supplierDAO = new SupplierDAO();

        if (action != null) {
            if (action.equals("delete")) {
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    boolean result = supplierDAO.deleteSupplier(id);
                    response.sendRedirect(request.getContextPath() + "/admin/supplier");
                    return;
                } catch (NumberFormatException e) {
                    request.setAttribute("errorMessage", "Invalid supplier ID.");
                    // Chuyển tiếp đến trang lỗi hoặc danh sách nhà cung cấp
                    request.getRequestDispatcher("/WEB-INF/admin/supplier/manage-supplier.jsp").forward(request, response);
                    return;
                }
            } else if (action.equals("status")) {
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    String status = request.getParameter("status");
                    if (status == null || (!status.equals("1") && !status.equals("0"))) {
                        request.setAttribute("errorMessage", "Invalid status value.");
                        request.getRequestDispatcher("/WEB-INF/admin/supplier/manage-supplier.jsp").forward(request, response);
                        return;
                    }
                    int newStatus = "1".equals(status) ? 0 : 1;
                    boolean result = supplierDAO.updateSupplierStatus(id, newStatus);
                    if (!result) {
                        request.setAttribute("errorMessage", "Failed to update supplier status.");
                        request.getRequestDispatcher("/WEB-INF/admin/supplier/manage-supplier.jsp").forward(request, response);
                        return;
                    }
                    response.sendRedirect(request.getContextPath() + "/admin/supplier");
                    return;
                } catch (NumberFormatException e) {
                    request.setAttribute("errorMessage", "Invalid supplier ID.");
                    request.getRequestDispatcher("/WEB-INF/admin/supplier/manage-supplier.jsp").forward(request, response);
                    return;
                } catch (Exception e) {
                    request.setAttribute("errorMessage", "Error updating status: " + e.getMessage());
                    request.getRequestDispatcher("/WEB-INF/admin/supplier/manage-supplier.jsp").forward(request, response);
                    return;
                }
            }
        }
        if (view != null) {
            switch (view) {
                case "create":
                    request.getRequestDispatcher("/WEB-INF/admin/supplier/create-supplier.jsp").forward(request, response);
                    break;
                case "edit":
                    try {
                        int id = Integer.parseInt(request.getParameter("id"));
                        Supplier supplier = supplierDAO.getSupplierById(id);
                        if (supplier != null) {
                            request.setAttribute("supplier", supplier);
                            request.getRequestDispatcher("/WEB-INF/admin/supplier/edit-supplier.jsp").forward(request, response);
                        } else {
                            request.setAttribute("errorMessage", "Supplier not found.");
                            response.sendRedirect(request.getContextPath() + "/admin/supplier");
                        }
                    } catch (NumberFormatException e) {
                        request.setAttribute("errorMessage", "Invalid supplier ID.");
                        response.sendRedirect(request.getContextPath() + "/admin/supplier");
                    } catch (Exception e) {
                        request.setAttribute("errorMessage", "Error retrieving supplier: " + e.getMessage());
                        response.sendRedirect(request.getContextPath() + "/admin/supplier");
                    }
                    break;

                case "detail":
                    // Xử lý xem chi tiết
                    int supplierId = Integer.parseInt(request.getParameter("id"));
                    Supplier supplierDetail = supplierDAO.getSupplierById(supplierId);
                    if (supplierDetail != null) {
                        request.setAttribute("supplier", supplierDetail);
                        request.getRequestDispatcher("/WEB-INF/admin/supplier/supplier-detail.jsp").forward(request, response);
                    } else {
                        response.sendRedirect(request.getContextPath() + "/admin/supplier");
                    }
                    break;
                default:
                    // Hiển thị danh sách nhà cung cấp
                    String keyword = request.getParameter("search");
                    List<Supplier> suppliers;
                    if (keyword != null && !keyword.trim().isEmpty()) {
                        suppliers = supplierDAO.searchSuppliers(keyword);
                        request.setAttribute("keyword", keyword);
                    } else {
                        suppliers = supplierDAO.getAllSuppliers();
                    }

                    int totalSuppliers = supplierDAO.countSuppliers();
                    int activeSuppliers = supplierDAO.countSuppliersByStatus(1);
                    int inactiveSuppliers = supplierDAO.countSuppliersByStatus(0);

                    request.setAttribute("suppliers", suppliers);
                    request.setAttribute("totalSuppliers", totalSuppliers);
                    request.setAttribute("activeSuppliers", activeSuppliers);
                    request.setAttribute("inactiveSuppliers", inactiveSuppliers);

                    request.getRequestDispatcher("/WEB-INF/admin/supplier/manage-supplier.jsp").forward(request, response);
                    break;
            }
        } else {
            // Nếu không có tham số view, hiển thị danh sách nhà cung cấp
            List<Supplier> suppliers = supplierDAO.getAllSuppliers();
            int totalSuppliers = supplierDAO.countSuppliers();
            int activeSuppliers = supplierDAO.countSuppliersByStatus(1);
            int inactiveSuppliers = supplierDAO.countSuppliersByStatus(0);

            request.setAttribute("suppliers", suppliers);
            request.setAttribute("totalSuppliers", totalSuppliers);
            request.setAttribute("activeSuppliers", activeSuppliers);
            request.setAttribute("inactiveSuppliers", inactiveSuppliers);

            request.getRequestDispatcher("/WEB-INF/admin/supplier/manage-supplier.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        SupplierDAO supplierDAO = new SupplierDAO();

        if ("create".equals(action)) {
            try {
                String brandName = request.getParameter("brandName");
                String companyName = request.getParameter("companyName");
                String address = request.getParameter("address");
                String email = request.getParameter("email");
                String phone = request.getParameter("phone");
                int status = Integer.parseInt(request.getParameter("status"));

                Supplier supplier = new Supplier();
                supplier.setBrandName(brandName);
                supplier.setCompanyName(companyName);
                supplier.setAddress(address);
                supplier.setEmail(email);
                supplier.setPhone(phone);
                supplier.setStatus(status);

                boolean res = supplierDAO.insert(supplier);

                if (res) {
                    response.sendRedirect(request.getContextPath() + "/admin/supplier");
                } else {
                    request.setAttribute("errorMessage", "Failed to create supplier. Please try again.");
                    request.setAttribute("supplier", supplier); // Return the data back to the form
                    response.sendRedirect(request.getContextPath() + "/admin/supplier");
                }
            } catch (Exception e) {
                request.setAttribute("errorMessage", "Error: " + e.getMessage());
                response.sendRedirect(request.getContextPath() + "/admin/supplier?action=create");

            }
        } else if ("edit".equals(action)) {
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                String brandName = request.getParameter("brandName");
                String companyName = request.getParameter("companyName");
                String address = request.getParameter("address");
                String email = request.getParameter("email");
                String phone = request.getParameter("phone");
                int status = Integer.parseInt(request.getParameter("status"));

                // Kiểm tra dữ liệu
                if (brandName == null || brandName.trim().isEmpty() || companyName == null || companyName.trim().isEmpty()) {
                    throw new Exception("Brand Name and Company Name are required.");
                }
                if (email == null || !email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
                    throw new Exception("Invalid email format.");
                }
                if (phone == null || !phone.matches("[0-9]{10}")) {
                    throw new Exception("Phone number must be 10 digits.");
                }
                if (status != 0 && status != 1) {
                    throw new Exception("Invalid status value.");
                }

                Supplier supplier = new Supplier();
                supplier.setSupplierID(id);
                supplier.setBrandName(brandName);
                supplier.setCompanyName(companyName);
                supplier.setAddress(address != null ? address : "");
                supplier.setEmail(email);
                supplier.setPhone(phone);
                supplier.setStatus(status);

                boolean res = supplierDAO.update(supplier);

                if (res) {
                    response.sendRedirect(request.getContextPath() + "/admin/supplier");
                } else {
                    request.setAttribute("errorMessage", "Failed to update supplier. Please try again.");
                    request.setAttribute("supplier", supplier);
                    request.getRequestDispatcher("/WEB-INF/admin/supplier/edit-supplier.jsp").forward(request, response);
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Invalid ID or status format.");
                request.getRequestDispatcher("/WEB-INF/admin/supplier/edit-supplier.jsp").forward(request, response);
            } catch (Exception e) {
                request.setAttribute("errorMessage", "Error: " + e.getMessage());
                request.getRequestDispatcher("/WEB-INF/admin/supplier/edit-supplier.jsp").forward(request, response);
            }
        }
    }

    @Override
    public String getServletInfo() {
        return "Servlet for managing suppliers in the admin panel";
    }
}
