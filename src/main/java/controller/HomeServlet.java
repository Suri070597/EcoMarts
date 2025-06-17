package controller;

import dao.CategoryDAO;
import dao.productDAO;
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
        productDAO dao = new productDAO();
        List<Product> list = dao.getAll();

        request.setAttribute("products", list);
        request.getRequestDispatcher("/WEB-INF/customer/homePage.jsp").forward(request, response);
    }
}
