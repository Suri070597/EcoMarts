/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.CategoryDAO;
import dao.ProductDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.File;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.List;
import model.Category;
import model.Product;

/**
 *
 * @author LNQB
 */
@WebServlet(name = "ProductServlet", urlPatterns = { "/Product" })
@MultipartConfig
public class ProductServlet extends HttpServlet {

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
            out.println("<title>Servlet ProductServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ProductServlet at " + request.getContextPath() + "</h1>");
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

        // Load categories for sidebar navigation
        CategoryDAO categoryDAO = new CategoryDAO();
        List<Category> categories = categoryDAO.getAllCategoriesWithChildren();
        request.setAttribute("categories", categories);

        ProductDAO dao = new ProductDAO();
        switch (action) {
            case "list":
                List<Product> list = dao.getAll();
                request.setAttribute("list", list);
                request.getRequestDispatcher("product.jsp").forward(request, response);
                break;
            case "create":
                List<Product> listProduct = dao.getAll();
                request.setAttribute("listPro", listProduct);
                request.getRequestDispatcher("./CRUD_Product/create-product.jsp").forward(request, response);
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
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        ProductDAO dao = new ProductDAO();
        switch (action) {
            case "create":
                String pName = request.getParameter("pName");
                double pPrice = Double.parseDouble(request.getParameter("pPrice"));
                int pQuantity = Integer.parseInt(request.getParameter("pQuanity"));
                String pUnit = request.getParameter("pUnit");
                String pDescription = request.getParameter("pDescription");

                // Handle image upload
                Part filePart = request.getPart("pImage");
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

                // Đường dẫn lưu ảnh (ví dụ thư mục images trong webapp)
                String uploadPath = getServletContext().getRealPath("") + "images";
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdir();
                }

                filePart.write(uploadPath + File.separator + fileName);

                String pImage = "images/" + fileName; // Lưu đường dẫn để lưu DB

                Timestamp date = new Timestamp(System.currentTimeMillis());

                int res = dao.insert(pName, pPrice, pDescription, pQuantity, pImage, pUnit, date);

                if (res == 1) {
                    response.sendRedirect("Product");
                } else {
                    response.sendRedirect("Product?action=create");
                }
                break;
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
