package controller.supplier;

import dao.SupplierDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import model.Supplier;

/**
 *
 * @author HuuDuc
 */
@WebServlet(urlPatterns = {"/admin/supplier"})
public class SupplierServlet extends HttpServlet {

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
                        request.setAttribute("errorMessage", "Giá trị trạng thái không hợp lệ.");
                        request.getRequestDispatcher("/WEB-INF/admin/supplier/manage-supplier.jsp").forward(request, response);
                        return;
                    }
                    int newStatus = "1".equals(status) ? 0 : 1;
                    boolean result = supplierDAO.updateSupplierStatus(id, newStatus);
                    if (!result) {
                        request.setAttribute("errorMessage", "Cập nhật trạng thái nhà sản xuất thất bại.");
                        request.getRequestDispatcher("/WEB-INF/admin/supplier/manage-supplier.jsp").forward(request, response);
                        return;
                    }
                    response.sendRedirect(request.getContextPath() + "/admin/supplier");
                    return;
                } catch (NumberFormatException e) {
                    request.setAttribute("errorMessage", "ID nhà sản xuất không hợp lệ.");
                    request.getRequestDispatcher("/WEB-INF/admin/supplier/manage-supplier.jsp").forward(request, response);
                    return;
                } catch (Exception e) {
                    request.setAttribute("errorMessage", "Lỗi khi cập nhật trạng thái: " + e.getMessage());
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
                            request.setAttribute("errorMessage", "Không tìm thấy nhà sản xuất.");
                            response.sendRedirect(request.getContextPath() + "/admin/supplier");
                        }
                    } catch (NumberFormatException e) {
                        request.setAttribute("errorMessage", "ID nhà sản xuất không hợp lệ.");
                        response.sendRedirect(request.getContextPath() + "/admin/supplier");
                    } catch (Exception e) {
                        request.setAttribute("errorMessage", "Lỗi khi lấy thông tin nhà cung cấp: " + e.getMessage());
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
                    request.setAttribute("errorMessage", "Tạo nhà cung cấp thất bại. Vui lòng thử lại.");
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
                    throw new Exception("Tên thương hiệu và tên công ty là bắt buộc.");
                }
                if (email == null || !email.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
                    throw new Exception("Định dạng email không hợp lệ.");
                }
                if (phone == null || !phone.matches("[0-9]{10}")) {
                    throw new Exception("Số điện thoại phải có 10 chữ số.");
                }
                if (status != 0 && status != 1) {
                    throw new Exception("Giá trị trạng thái không hợp lệ.");
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
                    request.setAttribute("errorMessage", "Cập nhật nhà sản xuất thất bại. Vui lòng thử lại.");
                    request.setAttribute("supplier", supplier);
                    request.getRequestDispatcher("/WEB-INF/admin/supplier/edit-supplier.jsp").forward(request, response);
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Định dạng ID hoặc trạng thái không hợp lệ.");
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
