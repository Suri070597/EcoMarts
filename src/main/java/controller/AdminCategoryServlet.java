package controller;

import dao.CategoryDAOV1;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Category;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "AdimCategoryServlet", urlPatterns = {"/admin/category", "/admin/createCategory", "/admin/deleteCategory"})
public class AdminCategoryServlet extends HttpServlet {
    private final CategoryDAOV1 dao = new CategoryDAOV1();

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
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                dao.deleteCategory(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            response.sendRedirect("category");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Xử lý thêm danh mục con
        String name = request.getParameter("categoryName");
        int parentID = Integer.parseInt(request.getParameter("parentID"));
        dao.insertCategory(name, parentID);
        response.sendRedirect("category");
    }
}
