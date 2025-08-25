/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.CategoryDAO;
import dao.ProductDAO;
import dao.OrderDAO;
import dao.FeedBackDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import model.Category;
import model.Product;

/**
 *
 * @author LNQB
 */
@WebServlet(name = "ViewAllProductServlet", urlPatterns = { "/ViewAllProductServlet" })
public class ViewAllProductServlet extends HttpServlet {

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
            out.println("<title>Servlet ProductByCategoryServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ProductByCategoryServlet at " + request.getContextPath() + "</h1>");
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
        String typeParam = request.getParameter("type");
        try {

            ProductDAO dao = new ProductDAO();
            List<Product> productList;
            String categoryName;

            if ("featured".equalsIgnoreCase(typeParam)) {
                // Build featured list from top-selling products with filtering rules
                OrderDAO orderDao = new OrderDAO();
                List<java.util.Map<String, Object>> rows = orderDao.getTopSellingProducts(200);
                List<Product> feats = new java.util.ArrayList<>();
                java.util.Map<Integer, String> priceDisplayMap = new java.util.HashMap<>();
                // Helper for formatting
                java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols();
                symbols.setGroupingSeparator('.');
                java.text.DecimalFormat formatter = new java.text.DecimalFormat("#,###", symbols);
                for (java.util.Map<String, Object> row : rows) {
                    int pid = ((Number) row.get("productId")).intValue();
                    Product p = dao.getProductById(pid);
                    if (p == null)
                        continue;
                    int parentId = 0;
                    try {
                        if (p.getCategory() != null) {
                            parentId = p.getCategory().getParentID();
                        }
                    } catch (Exception ignore) {
                    }

                    if (parentId == 1 || parentId == 2) {
                        feats.add(p); // Drinks & Milk always included
                        Double boxPrice = dao.getBoxPrice(p.getProductID());
                        if (boxPrice == null)
                            boxPrice = p.getPrice();
                        StringBuilder sb = new StringBuilder();
                        sb.append(formatter.format(boxPrice)).append(" đ / thùng");
                        try {
                            Integer upb = p.getUnitPerBox();
                            String iun = p.getItemUnitName();
                            if (upb == null || upb <= 0 || iun == null || iun.trim().isEmpty()) {
                                Product full = dao.getProductById(p.getProductID());
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
                        priceDisplayMap.put(p.getProductID(), sb.toString());
                    } else if (parentId == 3) {
                        // Fruits: keep original product price/unit (e.g., per kg)
                        feats.add(p);
                        String unitLabel = (p.getItemUnitName() != null && !p.getItemUnitName().trim().isEmpty()) ? p.getItemUnitName() : "kg";
                        priceDisplayMap.put(p.getProductID(), formatter.format(p.getPrice()) + " đ / " + unitLabel);
                    } else {
                        // Other categories: require UNIT price
                        Double unitPrice = dao.getUnitOnlyPrice(p.getProductID());
                        String itemUnit = dao.getItemUnitName(p.getProductID());
                        if (unitPrice != null && itemUnit != null && !itemUnit.trim().isEmpty()) {
                            feats.add(p);
                            priceDisplayMap.put(p.getProductID(), formatter.format(unitPrice) + " đ / " + itemUnit);
                        }
                    }
                }
                productList = feats;
                categoryName = "Sản phẩm nổi bật";
                request.setAttribute("categoryId", 7); // virtual category id for Featured
                request.setAttribute("productList", productList);
                request.setAttribute("categoryName", categoryName);
                request.setAttribute("priceDisplayMap", priceDisplayMap);
            } else {
                int parsedCategoryId = Integer.parseInt(categoryIdRaw);
                List<Product> originalList = dao.getProductsByCategoryAndSub(parsedCategoryId);
                categoryName = dao.getCategoryNameById(parsedCategoryId);

                // Build priceDisplayMap and filter list according to rules
                java.util.List<Product> filtered = new java.util.ArrayList<>();
                java.util.Map<Integer, String> priceDisplayMap = new java.util.HashMap<>();
                // formatter
                java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols();
                symbols.setGroupingSeparator('.');
                java.text.DecimalFormat formatter = new java.text.DecimalFormat("#,###", symbols);

                for (Product p : originalList) {
                    int parentId = 0;
                    try {
                        if (p.getCategory() != null) {
                            parentId = p.getCategory().getParentID();
                        }
                    } catch (Exception ignore) {
                    }

                    String display = null;
                    if (parentId == 1 || parentId == 2) {
                        // Drinks & Milk: show BOX price and append (UnitPerBox ItemUnitName)
                        Double boxPrice = dao.getBoxPrice(p.getProductID());
                        if (boxPrice == null)
                            boxPrice = p.getPrice();
                        StringBuilder sb = new StringBuilder();
                        sb.append(formatter.format(boxPrice)).append(" đ / thùng");
                        try {
                            Integer upb = p.getUnitPerBox();
                            String iun = p.getItemUnitName();
                            if (upb == null || upb <= 0 || iun == null || iun.trim().isEmpty()) {
                                Product full = dao.getProductById(p.getProductID());
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
                        filtered.add(p);
                    } else if (parentId == 3) {
                        // Fruits: giữ nguyên như cũ (giá gốc + đơn vị từ Product)
                        String unitLabel = (p.getItemUnitName() != null && !p.getItemUnitName().trim().isEmpty()) ? p.getItemUnitName() : "kg";
                        display = formatter.format(p.getPrice()) + " đ / " + unitLabel;
                        filtered.add(p);
                    } else {
                        // Other categories: require UNIT; hide if missing
                        Double unitPrice = dao.getUnitOnlyPrice(p.getProductID());
                        String itemUnit = dao.getItemUnitName(p.getProductID());
                        if (unitPrice != null && itemUnit != null && !itemUnit.trim().isEmpty()) {
                            display = formatter.format(unitPrice) + " đ / " + itemUnit;
                            filtered.add(p);
                        }
                    }
                    if (display != null) {
                        priceDisplayMap.put(p.getProductID(), display);
                    }
                }

                productList = filtered;
                request.setAttribute("productList", productList);
                request.setAttribute("categoryId", parsedCategoryId);
                request.setAttribute("categoryName", categoryName);
                request.setAttribute("priceDisplayMap", priceDisplayMap);
            }

            // Lấy rating trung bình và số lượt đánh giá cho từng sản phẩm
            try {
                FeedBackDAO fbDao = new FeedBackDAO();
                Map<Integer, Double> avgRatingMap = new HashMap<>();
                Map<Integer, Integer> reviewCountMap = new HashMap<>();
                Map<Integer, Double> unitPriceMap = new HashMap<>();

                for (Product p : (List<Product>) request.getAttribute("productList")) {
                    int pid = p.getProductID();
                    double avg = fbDao.getAverageRatingByProductId(pid);
                    int count = fbDao.countReviewsByProductId(pid);
                    avgRatingMap.put(pid, avg);
                    reviewCountMap.put(pid, count);
                    // Keep providing unit price map for existing JSP logic
                    Double unitPrice = dao.getUnitPrice(pid);
                    unitPriceMap.put(pid, unitPrice);
                }

                request.setAttribute("avgRatingMap", avgRatingMap);
                request.setAttribute("reviewCountMap", reviewCountMap);
                request.setAttribute("unitPriceMap", unitPriceMap);
            } catch (Exception e) {
                e.printStackTrace();
            }

            request.getRequestDispatcher("/WEB-INF/customer/view-all.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid category ID");
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
