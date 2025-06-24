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
import model.Product;

/**
 *
 * @author LNQB
 */
@WebServlet(name = "ProductDetailServlet", urlPatterns = {"/ProductDetail"})
public class ProductDetailServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ProductDetailServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ProductDetailServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ProductDAO dao = new ProductDAO();
        String idRaw = request.getParameter("id");

        try {
            if (idRaw == null || idRaw.trim().isEmpty()) {
                request.setAttribute("errorMessage", "ID sản phẩm không hợp lệ");
                request.getRequestDispatcher("/WEB-INF/customer/productDetail.jsp").forward(request, response);
                return;
            }

            int id = Integer.parseInt(idRaw);
            Product mo = dao.getProductById(id);

            if (mo == null) {
                request.setAttribute("errorMessage", "Sản phẩm với ID " + id + " không tồn tại");
                request.setAttribute("productId", id);
                request.getRequestDispatcher("/WEB-INF/customer/productDetail.jsp").forward(request, response);
                return;
            }

            if (mo.getCategory() != null) {
                int parentId = mo.getCategory().getParentID();
                List<Product> relatedProducts = dao.getRelatedProductsByParentCategory(parentId, id);
                request.setAttribute("relatedProducts", relatedProducts);
            }

            request.setAttribute("mo", mo);
            request.setAttribute("dataCate", dao.getCategory());
            request.setAttribute("dataSup", dao.getAllSuppliers());
            request.getRequestDispatcher("/WEB-INF/customer/productDetail.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "ID sản phẩm phải là một số hợp lệ: " + idRaw);
            request.getRequestDispatcher("/WEB-INF/customer/productDetail.jsp").forward(request, response);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
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
