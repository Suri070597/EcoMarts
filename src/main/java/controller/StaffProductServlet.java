/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.ProductDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import model.Category;
import model.Product;

/**
 *
 * @author LNQB
 */
@WebServlet(name = "StaffProduct", urlPatterns = { "/staff/product" })
public class StaffProductServlet extends HttpServlet {

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
            out.println("<title>Servlet StaffProduct</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet StaffProduct at " + request.getContextPath() + "</h1>");
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
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }
        ProductDAO dao = new ProductDAO();
        List<Category> listCategory = dao.getCategory();
        switch (action) {
            case "list":
                List<Product> list = dao.getAllIncludingOutOfStock();
                request.setAttribute("dataCate", listCategory);
                request.setAttribute("data", list);

                // Counters for stock status cards
                final int LOW_STOCK_THRESHOLD = 10;
                int inStock = dao.countInStock(LOW_STOCK_THRESHOLD);
                int lowStock = dao.countLowStock(LOW_STOCK_THRESHOLD);
                int outOfStock = dao.countOutOfStock();
                request.setAttribute("inStockCount", inStock);
                request.setAttribute("lowStockCount", lowStock);
                request.setAttribute("outOfStockCount", outOfStock);

                request.getRequestDispatcher("/WEB-INF/staff/product/product.jsp").forward(request, response);
                break;
            case "search":
                String keyword = request.getParameter("keyword");
                List<Product> searchResults = dao.searchProductsByName(keyword);
                request.setAttribute("dataCate", listCategory);
                request.setAttribute("data", searchResults);
                request.setAttribute("keyword", keyword);

                // Counters for stock status cards based on search results
                final int SEARCH_LOW_STOCK_THRESHOLD = 10;
                int searchInStock = 0, searchLowStock = 0, searchOutOfStock = 0;

                for (Product p : searchResults) {
                    double stock = p.getStockQuantity();
                    if (stock > SEARCH_LOW_STOCK_THRESHOLD) {
                        searchInStock++;
                    } else if (stock > 0) {
                        searchLowStock++;
                    } else {
                        searchOutOfStock++;
                    }
                }

                request.setAttribute("inStockCount", searchInStock);
                request.setAttribute("lowStockCount", searchLowStock);
                request.setAttribute("outOfStockCount", searchOutOfStock);

                request.getRequestDispatcher("/WEB-INF/staff/product/product.jsp").forward(request, response);
                break;
            case "detail":
                String idDetailRaw = request.getParameter("id");
                try {
                    int idDetail = Integer.parseInt(idDetailRaw);
                    Product productDetail = dao.getProductById(idDetail);
                    java.util.Map<String, Object> inventory = dao.getProductInventory(idDetail);
                    request.setAttribute("productDetail", productDetail);
                    request.setAttribute("inventory", inventory);
                    request.getRequestDispatcher("/WEB-INF/staff/product/product-detail.jsp").forward(request,
                            response);
                } catch (Exception e) {
                    e.printStackTrace();
                    response.sendRedirect(request.getContextPath() + "/staff/product");
                }
                break;
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
        ProductDAO dao = new ProductDAO();
        String action = request.getParameter("action");
        PrintWriter out = response.getWriter();
        if (action == null) {
            response.sendRedirect("/staff/product");
            return;
        }
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
