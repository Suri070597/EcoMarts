package controller;

import dao.ViewProductDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Product;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "LoadMoreFeaturedServlet", urlPatterns = {"/loadMoreFeatured"})
public class LoadMoreFeaturedServlet extends HttpServlet {

    private ViewProductDAO dao = new ViewProductDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int offset = Integer.parseInt(request.getParameter("offset"));
        int limit = Integer.parseInt(request.getParameter("limit"));
        int parentId = Integer.parseInt(request.getParameter("parentId"));

        try {
            if (request.getParameter("offset") != null) {
                offset = Integer.parseInt(request.getParameter("offset"));
            }
            if (request.getParameter("parentId") != null) {
                parentId = Integer.parseInt(request.getParameter("parentId"));
            }
            if (request.getParameter("limit") != null) {
                limit = Integer.parseInt(request.getParameter("limit"));
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        List<Product> products = dao.getFeaturedProductsByPage(parentId, offset, limit);
        request.setAttribute("products", products);
        request.getRequestDispatcher("/WEB-INF/customer/loadMoreFeatured.jsp").forward(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet for loading more featured products via AJAX";
    }
}
