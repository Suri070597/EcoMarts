package controller;

import dao.ViewProductDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import model.Product;

@WebServlet(name = "SimpleTestServlet", urlPatterns = { "/simpleTest" })
public class SimpleTestServlet extends HttpServlet {

    private ViewProductDAO dao = new ViewProductDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<html><head><title>Simple Test</title></head><body>");
        out.println("<h1>Test Database Connection</h1>");

        try {
            // Test danh mục 1
            out.println("<h2>Danh mục 1 (Nước giải khát):</h2>");
            List<Product> products1 = dao.getFeaturedProductsByPage(1, 0, 6);
            out.println("<p>Số sản phẩm: " + products1.size() + "</p>");

            List<Product> products2 = dao.getFeaturedProductsByPage(1, 6, 3);
            out.println("<p>Số sản phẩm tiếp theo: " + products2.size() + "</p>");

            // Test danh mục 7
            out.println("<h2>Danh mục 7 (Sản phẩm nổi bật):</h2>");
            List<Product> products3 = dao.getFeaturedProductsByPage(7, 0, 6);
            out.println("<p>Số sản phẩm: " + products3.size() + "</p>");

            List<Product> products4 = dao.getFeaturedProductsByPage(7, 6, 3);
            out.println("<p>Số sản phẩm tiếp theo: " + products4.size() + "</p>");

        } catch (Exception e) {
            out.println("<p style='color: red;'>Lỗi: " + e.getMessage() + "</p>");
            e.printStackTrace();
        }

        out.println("</body></html>");
    }
}