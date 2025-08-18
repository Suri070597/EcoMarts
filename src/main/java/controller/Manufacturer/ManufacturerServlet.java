package controller.Manufacturer;

import dao.ManufacturerDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import model.Manufacturer;

/**
 *
 * @author HuuDuc
 */
@WebServlet(urlPatterns = {"/admin/manufacturer"})
public class ManufacturerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String view = request.getParameter("view");
        String action = request.getParameter("action");
        ManufacturerDAO manufacturerDAO = new ManufacturerDAO();

        if (action != null) {
            if (action.equals("delete")) {
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    boolean result = manufacturerDAO.deleteManufacturer(id);
                    String base = request.getContextPath() + "/admin/manufacturer";
                    if (result) {
                        response.sendRedirect(base + "?type=success&message=" + java.net.URLEncoder.encode("Xóa nhà sản xuất thành công", java.nio.charset.StandardCharsets.UTF_8));
                    } else {
                        response.sendRedirect(base + "?type=error&message=" + java.net.URLEncoder.encode("Không thể xóa vì có liên kết dữ liệu liên quan", java.nio.charset.StandardCharsets.UTF_8));
                    }
                    return;
                } catch (NumberFormatException e) {
                    String base = request.getContextPath() + "/admin/manufacturer";
                    response.sendRedirect(base + "?type=error&message=" + java.net.URLEncoder.encode("ID nhà sản xuất không hợp lệ", java.nio.charset.StandardCharsets.UTF_8));
                    return;
                }
            } else if (action.equals("status")) {
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    String status = request.getParameter("status");
                    if (status == null || (!status.equals("1") && !status.equals("0"))) {
                        String base = request.getContextPath() + "/admin/manufacturer";
                        response.sendRedirect(base + "?type=error&message=" + java.net.URLEncoder.encode("Giá trị trạng thái không hợp lệ", java.nio.charset.StandardCharsets.UTF_8));
                        return;
                    }
                    int newStatus = "1".equals(status) ? 0 : 1;
                    boolean result = manufacturerDAO.updateManufacturerStatus(id, newStatus);
                    if (!result) {
                        String base = request.getContextPath() + "/admin/manufacturer";
                        response.sendRedirect(base + "?type=error&message=" + java.net.URLEncoder.encode("Cập nhật trạng thái nhà sản xuất thất bại", java.nio.charset.StandardCharsets.UTF_8));
                        return;
                    }
                    response.sendRedirect(request.getContextPath() + "/admin/manufacturer?type=success&message=" + java.net.URLEncoder.encode("Cập nhật trạng thái thành công", java.nio.charset.StandardCharsets.UTF_8));
                    return;
                } catch (NumberFormatException e) {
                    String base = request.getContextPath() + "/admin/manufacturer";
                    response.sendRedirect(base + "?type=error&message=" + java.net.URLEncoder.encode("ID nhà sản xuất không hợp lệ", java.nio.charset.StandardCharsets.UTF_8));
                    return;
                } catch (Exception e) {
                    String base = request.getContextPath() + "/admin/manufacturer";
                    response.sendRedirect(base + "?type=error&message=" + java.net.URLEncoder.encode("Lỗi khi cập nhật trạng thái", java.nio.charset.StandardCharsets.UTF_8));
                    return;
                }
            }
        }
        if (view != null) {
            switch (view) {
                case "create":
                    request.getRequestDispatcher("/WEB-INF/admin/manufacturer/create-manufacturer.jsp").forward(request, response);
                    break;
                case "edit":
                    try {
                        int id = Integer.parseInt(request.getParameter("id"));
                        Manufacturer manufacturer = manufacturerDAO.getManufacturerById(id);
                        if (manufacturer != null) {
                            request.setAttribute("manufacturer", manufacturer);
                            request.getRequestDispatcher("/WEB-INF/admin/manufacturer/edit-manufacturer.jsp").forward(request, response);
                        } else {
                            request.setAttribute("errorMessage", "Không tìm thấy nhà sản xuất.");
                            response.sendRedirect(request.getContextPath() + "/admin/manufacturer");
                        }
                    } catch (NumberFormatException e) {
                        request.setAttribute("errorMessage", "ID nhà sản xuất không hợp lệ.");
                        response.sendRedirect(request.getContextPath() + "/admin/manufacturer");
                    } catch (Exception e) {
                        request.setAttribute("errorMessage", "Lỗi khi lấy thông tin nhà cung cấp: " + e.getMessage());
                        response.sendRedirect(request.getContextPath() + "/admin/manufacturer");
                    }
                    break;

                case "detail":
                    // Xử lý xem chi tiết
                    int manufacturerId = Integer.parseInt(request.getParameter("id"));
                    Manufacturer manufacturerDetail = manufacturerDAO.getManufacturerById(manufacturerId);
                    if (manufacturerDetail != null) {
                        request.setAttribute("manufacturer", manufacturerDetail);
                        request.getRequestDispatcher("/WEB-INF/admin/manufacturer/manufacturer-detail.jsp").forward(request, response);
                    } else {
                        response.sendRedirect(request.getContextPath() + "/admin/manufacturer");
                    }
                    break;
                default:
                    // Hiển thị danh sách nhà cung cấp
                    String keyword = request.getParameter("search");
                    List<Manufacturer> manufacturers;
                    if (keyword != null && !keyword.trim().isEmpty()) {
                        manufacturers = manufacturerDAO.searchManufacturers(keyword);
                        request.setAttribute("keyword", keyword);
                    } else {
                        manufacturers = manufacturerDAO.getAllManufacturers();
                    }

                    int totalManufacturers = manufacturerDAO.countManufacturers();
                    int activeManufacturers = manufacturerDAO.countManufacturersByStatus(1);
                    int inactiveManufacturers = manufacturerDAO.countManufacturersByStatus(0);

                    request.setAttribute("manufacturers", manufacturers);
                    request.setAttribute("totalManufacturers", totalManufacturers);
                    request.setAttribute("activeManufacturers", activeManufacturers);
                    request.setAttribute("inactiveManufacturers", inactiveManufacturers);

                    request.getRequestDispatcher("/WEB-INF/admin/manufacturer/manage-manufacturer.jsp").forward(request, response);
                    break;
            }
        } else {
            // Nếu không có tham số view, hiển thị danh sách nhà cung cấp
            List<Manufacturer> manufacturers = manufacturerDAO.getAllManufacturers();
            int totalManufacturers = manufacturerDAO.countManufacturers();
            int activeManufacturers = manufacturerDAO.countManufacturersByStatus(1);
            int inactiveManufacturers = manufacturerDAO.countManufacturersByStatus(0);

            request.setAttribute("manufacturers", manufacturers);
            request.setAttribute("totalManufacturers", totalManufacturers);
            request.setAttribute("activeManufacturers", activeManufacturers);
            request.setAttribute("inactiveManufacturers", inactiveManufacturers);

            request.getRequestDispatcher("/WEB-INF/admin/manufacturer/manage-manufacturer.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        ManufacturerDAO manufacturerDAO = new ManufacturerDAO();

        if ("create".equals(action)) {
            try {
                String brandName = request.getParameter("brandName");
                String companyName = request.getParameter("companyName");
                String address = request.getParameter("address");
                String email = request.getParameter("email");
                String phone = request.getParameter("phone");
                int status = Integer.parseInt(request.getParameter("status"));

                Manufacturer manufacturer = new Manufacturer();
                manufacturer.setBrandName(brandName);
                manufacturer.setCompanyName(companyName);
                manufacturer.setAddress(address);
                manufacturer.setEmail(email);
                manufacturer.setPhone(phone);
                manufacturer.setStatus(status);

                boolean res = manufacturerDAO.insert(manufacturer);

                if (res) {
                    response.sendRedirect(request.getContextPath() + "/admin/manufacturer");
                } else {
                    request.setAttribute("errorMessage", "Tạo nhà cung cấp thất bại. Vui lòng thử lại.");
                    request.setAttribute("manufacturer", manufacturer); // Return the data back to the form
                    response.sendRedirect(request.getContextPath() + "/admin/manufacturer");
                }
            } catch (Exception e) {
                request.setAttribute("errorMessage", "Error: " + e.getMessage());
                response.sendRedirect(request.getContextPath() + "/admin/manufacturer?action=create");

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

                Manufacturer manufacturer = new Manufacturer();
                manufacturer.setManufacturerID(id);
                manufacturer.setBrandName(brandName);
                manufacturer.setCompanyName(companyName);
                manufacturer.setAddress(address != null ? address : "");
                manufacturer.setEmail(email);
                manufacturer.setPhone(phone);
                manufacturer.setStatus(status);

                boolean res = manufacturerDAO.update(manufacturer);

                if (res) {
                    response.sendRedirect(request.getContextPath() + "/admin/manufacturer");
                } else {
                    request.setAttribute("errorMessage", "Cập nhật nhà sản xuất thất bại. Vui lòng thử lại.");
                    request.setAttribute("manufacturer", manufacturer);
                    request.getRequestDispatcher("/WEB-INF/admin/manufacturer/edit-manufacturer.jsp").forward(request, response);
                }
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Định dạng ID hoặc trạng thái không hợp lệ.");
                request.getRequestDispatcher("/WEB-INF/admin/manufacturer/edit-manufacturer.jsp").forward(request, response);
            } catch (Exception e) {
                request.setAttribute("errorMessage", "Error: " + e.getMessage());
                request.getRequestDispatcher("/WEB-INF/admin/manufacturer/edit-manufacturer.jsp").forward(request, response);
            }
        }
    }

    @Override
    public String getServletInfo() {
        return "Servlet for managing manufacturers in the admin panel";
    }
}
