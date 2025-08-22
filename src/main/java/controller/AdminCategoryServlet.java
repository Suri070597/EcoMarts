package controller;

import dao.CategoryDAO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Category;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "AdimCategoryServlet", urlPatterns = {"/admin/category", "/admin/createCategory", "/admin/deleteCategory"})
public class AdminCategoryServlet extends HttpServlet {
    private final CategoryDAO dao = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String uri = request.getRequestURI();

        if (uri.endsWith("/admin/category")) {
            // Hiển thị cây danh mục
            List<Category> parents = dao.getParentCategories();
            Map<Integer, List<Category>> childMap = dao.getChildCategoriesGrouped();

            request.setAttribute("parents", parents);
            request.setAttribute("childMap", childMap);
            request.getRequestDispatcher("/WEB-INF/admin/category/category.jsp").forward(request, response);

        } else if (uri.endsWith("/admin/createCategory")) {
            // Hiển thị form thêm danh mục
            List<Category> parents = dao.getParentCategories();
            request.setAttribute("parents", parents);
            request.getRequestDispatcher("/WEB-INF/admin/category/createCategory.jsp").forward(request, response);


        } else if (uri.endsWith("/admin/deleteCategory")) {
            // Xóa danh mục
            String redirectBase = request.getContextPath() + "/admin/category";
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                boolean ok = dao.deleteCategory(id);
                if (ok) {
                    response.sendRedirect(redirectBase + "?type=success&message=" + java.net.URLEncoder.encode("Xóa danh mục thành công", java.nio.charset.StandardCharsets.UTF_8));
                } else {
                    response.sendRedirect(redirectBase + "?type=error&message=" + java.net.URLEncoder.encode("Không thể xóa vì có liên kết dữ liệu liên quan", java.nio.charset.StandardCharsets.UTF_8));
                }
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect(redirectBase + "?type=error&message=" + java.net.URLEncoder.encode("Không thể xóa vì có liên kết dữ liệu liên quan", java.nio.charset.StandardCharsets.UTF_8));
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Xử lý thêm danh mục con
        String nameRaw = request.getParameter("categoryName");
        String parentIdRaw = request.getParameter("parentID");
        String redirectBase = request.getContextPath() + "/admin/category";

        try {
            String name = nameRaw != null ? nameRaw.trim() : "";
            int parentID = Integer.parseInt(parentIdRaw);

            // Validate: không cho kí tự đặc biệt, kí tự đầu tiên không được là số
            boolean valid = name.matches("^(?!\\d)[\\p{L}\\p{M}\\d ]+$");

            if (!valid) {
                request.setAttribute("errorMessage", "Tên danh mục không hợp lệ: không chứa kí tự đặc biệt và không bắt đầu bằng số.");
                request.setAttribute("categoryName", name);
                request.setAttribute("selectedParentID", parentID);
                // cần lại danh sách parent
                List<Category> parents = dao.getParentCategories();
                request.setAttribute("parents", parents);
                request.getRequestDispatcher("/WEB-INF/admin/category/createCategory.jsp").forward(request, response);
                return;
            }

            dao.insertCategory(name, parentID);
            response.sendRedirect(redirectBase + "?type=success&message=" + java.net.URLEncoder.encode("Thêm danh mục thành công", java.nio.charset.StandardCharsets.UTF_8));
        } catch (Exception e) {
            e.printStackTrace();
            // Quay lại form với lỗi
            request.setAttribute("errorMessage", "Đã xảy ra lỗi. Vui lòng kiểm tra và thử lại.");
            request.setAttribute("categoryName", nameRaw);
            try {
                request.setAttribute("selectedParentID", Integer.parseInt(parentIdRaw));
            } catch (Exception ignore) {}
            List<Category> parents = dao.getParentCategories();
            request.setAttribute("parents", parents);
            request.getRequestDispatcher("/WEB-INF/admin/category/createCategory.jsp").forward(request, response);
        }
    }
}
