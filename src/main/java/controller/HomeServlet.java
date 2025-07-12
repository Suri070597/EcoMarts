package controller;

import dao.CategoryDAO;
import dao.ProductDAO;
import dao.ViewProductDAO;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import model.Category;
import model.Product;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get categories from database
        CategoryDAO categoryDAO = new CategoryDAO();
        List<Category> categories = categoryDAO.getAllCategoriesWithChildren();
        request.setAttribute("categories", categories);

        // Get products
        ProductDAO dao = new ProductDAO();
        List<Product> list = dao.getAll();
        request.setAttribute("products", list);

        // Lấy 7 danh sách sản phẩm theo từng ParentID
        ViewProductDAO viewDao = new ViewProductDAO();
        request.setAttribute("featuredProducts1", viewDao.getFeaturedProductsByPage(1, 0, 6));
        request.setAttribute("featuredProducts2", viewDao.getFeaturedProductsByPage(2, 0, 6));
        request.setAttribute("featuredProducts3", viewDao.getFeaturedProductsByPage(3, 0, 6));
        request.setAttribute("featuredProducts4", viewDao.getFeaturedProductsByPage(4, 0, 6));
        request.setAttribute("featuredProducts5", viewDao.getFeaturedProductsByPage(5, 0, 6));
        request.setAttribute("featuredProducts6", viewDao.getFeaturedProductsByPage(6, 0, 6));
        request.setAttribute("featuredProducts7", viewDao.getFeaturedProductsByPage(7, 0, 6));

        request.getRequestDispatcher("/WEB-INF/customer/homePage.jsp").forward(request, response);
    }
}
