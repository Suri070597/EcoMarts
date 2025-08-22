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

            // Filter results per rule: 1,2,3 keep; others require UNIT
            java.util.List<Product> filteredResult = new java.util.ArrayList<>();
            dao.ProductDAO productDao = new dao.ProductDAO();
            java.util.Map<Integer, Integer> parentIdMap = new java.util.HashMap<>();
            for (Product p : result) {
                int pid = p.getProductID();
                int parentId = 0;
                try {
                    Product full = productDao.getProductById(pid);
                    if (full != null && full.getCategory() != null) {
                        parentId = full.getCategory().getParentID();
                        // ensure names are present on p for JSP labels
                        try {
                            p.setBoxUnitName(full.getBoxUnitName());
                        } catch (Exception ignore) {
                        }
                        try {
                            p.setItemUnitName(full.getItemUnitName());
                        } catch (Exception ignore) {
                        }
                        try {
                            p.setUnitPerBox(full.getUnitPerBox());
                        } catch (Exception ignore) {
                        }
                    }
                } catch (Exception ignore) {
                }
                parentIdMap.put(pid, parentId);

                if (parentId == 1 || parentId == 2 || parentId == 3) {
                    filteredResult.add(p);
                } else {
                    Double unitOnly = productDao.getUnitOnlyPrice(pid);
                    if (unitOnly != null) {
                        filteredResult.add(p);
                    }
                }
            }

            // Build display strings per MVC
            java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols();
            symbols.setGroupingSeparator('.');
            java.text.DecimalFormat formatter = new java.text.DecimalFormat("#,###", symbols);
            java.util.Map<Integer, String> priceDisplayMap = new java.util.HashMap<>();
            java.util.Map<Integer, Double> unitPriceMap = new java.util.HashMap<>();
            for (Product p : filteredResult) {
                int pid = p.getProductID();
                Integer parentId = parentIdMap.get(pid);
                Double unitPrice = productDao.getUnitPrice(pid); // UNIT or KG
                unitPriceMap.put(pid, unitPrice);
                String display;
                if (parentId != null && (parentId == 1 || parentId == 2)) {
                    // Drinks & Milk: box price with unit-per-box suffix
                    Double boxPrice = productDao.getBoxPrice(pid);
                    if (boxPrice == null)
                        boxPrice = p.getPrice();
                    StringBuilder sb = new StringBuilder();
                    sb.append(formatter.format(boxPrice)).append(" đ / thùng");
                    try {
                        Integer upb = p.getUnitPerBox();
                        String iun = p.getItemUnitName();
                        if (upb == null || upb <= 0 || iun == null || iun.trim().isEmpty()) {
                            Product full = productDao.getProductById(pid);
                            if (full != null) {
                                upb = full.getUnitPerBox();
                                iun = full.getItemUnitName();
                            }
                        }
                        if (upb != null && upb > 0 && iun != null && !iun.trim().isEmpty()) {
                            sb.append(" (").append(upb).append(" ").append(iun).append(")");
                        }
                    } catch (Exception ignore) {
                    }
                    display = sb.toString();
                } else if (parentId != null && parentId == 3) {
                    if (unitPrice != null) {
                        display = formatter.format(unitPrice) + " đ / kg";
                    } else {
                        String boxLabel = (p.getBoxUnitName() != null && !p.getBoxUnitName().trim().isEmpty())
                                ? p.getBoxUnitName()
                                : "thùng";
                        display = formatter.format(p.getPrice()) + " đ / " + boxLabel;
                    }
                } else {
                    if (unitPrice != null) {
                        String itemLabel = (p.getItemUnitName() != null && !p.getItemUnitName().trim().isEmpty())
                                ? p.getItemUnitName()
                                : "cái";
                        display = formatter.format(unitPrice) + " đ / " + itemLabel;
                    } else {
                        String boxLabel = (p.getBoxUnitName() != null && !p.getBoxUnitName().trim().isEmpty())
                                ? p.getBoxUnitName()
                                : "thùng";
                        display = formatter.format(p.getPrice()) + " đ / " + boxLabel;
                    }
                }
                priceDisplayMap.put(pid, display);
            }

            request.setAttribute("searchResult", filteredResult);
            request.setAttribute("searchKeyword", keyword);
            request.setAttribute("parentIdMap", parentIdMap);
            request.setAttribute("priceDisplayMap", priceDisplayMap);

            // Lấy rating trung bình và số lượt đánh giá cho từng sản phẩm
            try {
                FeedBackDAO fbDao = new FeedBackDAO();
                Map<Integer, Double> avgRatingMap = new HashMap<>();
                Map<Integer, Integer> reviewCountMap = new HashMap<>();

                for (Product p : filteredResult) {
                    int pid = p.getProductID();
                    double avg = fbDao.getAverageRatingByProductId(pid);
                    int count = fbDao.countReviewsByProductId(pid);
                    avgRatingMap.put(pid, avg);
                    reviewCountMap.put(pid, count);
                }

                request.setAttribute("avgRatingMap", avgRatingMap);
                request.setAttribute("reviewCountMap", reviewCountMap);
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
