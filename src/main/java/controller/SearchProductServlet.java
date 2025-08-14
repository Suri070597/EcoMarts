/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.CategoryDAO;
import dao.SearchProductsDAO;
import dao.FeedBackDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import model.Category;
import model.Product;

/**
 *
 * @author LNQB
 */
@WebServlet(name = "SearchProductServlet", urlPatterns = { "/SearchProduct" })
public class SearchProductServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet SearchProductServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet SearchProductServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the
    // + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        CategoryDAO categoryDAO = new CategoryDAO();
        List<Category> categories = categoryDAO.getAllCategoriesWithChildren();
        request.setAttribute("categories", categories);
        String categoryIdRaw = request.getParameter("categoryId");
        String keyword = request.getParameter("keyword");

        SearchProductsDAO dao = new SearchProductsDAO();
        try {
            List<Product> result = dao.searchProductsByKeyword(keyword);
            request.setAttribute("searchResult", result);
            request.setAttribute("searchKeyword", keyword);

            // Lấy rating trung bình và số lượt đánh giá cho từng sản phẩm
            try {
                FeedBackDAO fbDao = new FeedBackDAO();
                Map<Integer, Double> avgRatingMap = new HashMap<>();
                Map<Integer, Integer> reviewCountMap = new HashMap<>();
                Map<Integer, Double> unitPriceMap = new HashMap<>();

                for (Product p : result) {
                    int pid = p.getProductID();
                    double avg = fbDao.getAverageRatingByProductId(pid);
                    int count = fbDao.countReviewsByProductId(pid);
                    avgRatingMap.put(pid, avg);
                    reviewCountMap.put(pid, count);

                    // Lấy giá unit (lon) từ ProductPackaging
                    dao.ProductDAO productDao = new dao.ProductDAO();
                    Double unitPrice = productDao.getUnitPrice(pid);
                    unitPriceMap.put(pid, unitPrice);
                }

                request.setAttribute("avgRatingMap", avgRatingMap);
                request.setAttribute("reviewCountMap", reviewCountMap);
                request.setAttribute("unitPriceMap", unitPriceMap);
            } catch (Exception e) {
                e.printStackTrace();
            }

            request.getRequestDispatcher("./WEB-INF/customer/searchProductResult.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi khi tìm kiếm sản phẩm");
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
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
