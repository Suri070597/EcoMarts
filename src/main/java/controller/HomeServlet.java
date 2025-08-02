package controller;

import dao.CategoryDAO;
import dao.ProductDAO;
import dao.ViewProductDAO;
import dao.FeedBackDAO;
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

        // Lấy rating trung bình và số lượt đánh giá cho từng sản phẩm
        try {
            FeedBackDAO fbDao = new FeedBackDAO();
            java.util.Map<Integer, Double> avgRatingMap = new java.util.HashMap<>();
            java.util.Map<Integer, Integer> reviewCountMap = new java.util.HashMap<>();
            List<List<Product>> allProductLists = java.util.Arrays.asList(
                    (List<Product>) request.getAttribute("featuredProducts1"),
                    (List<Product>) request.getAttribute("featuredProducts2"),
                    (List<Product>) request.getAttribute("featuredProducts3"),
                    (List<Product>) request.getAttribute("featuredProducts4"),
                    (List<Product>) request.getAttribute("featuredProducts5"),
                    (List<Product>) request.getAttribute("featuredProducts6"),
                    (List<Product>) request.getAttribute("featuredProducts7"));
            for (List<Product> plist : allProductLists) {
                if (plist != null) {
                    for (Product p : plist) {
                        int pid = p.getProductID();
                        if (!avgRatingMap.containsKey(pid)) {
                            double avg = fbDao.getAverageRatingByProductId(pid);
                            int count = fbDao.countReviewsByProductId(pid);
                            avgRatingMap.put(pid, avg);
                            reviewCountMap.put(pid, count);
                        }
                    }
                }
            }
            request.setAttribute("avgRatingMap", avgRatingMap);
            request.setAttribute("reviewCountMap", reviewCountMap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        request.getRequestDispatcher("/WEB-INF/customer/homePage.jsp").forward(request, response);
    }
}
