package controller;

import dao.FeedBackDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Review;
import java.io.IOException;

@WebServlet(name = "ReadNotificationServlet", urlPatterns = { "/read-notification" })
public class ReadNotificationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String reviewIdStr = request.getParameter("reviewId");
        if (reviewIdStr != null) {
            try {
                int reviewId = Integer.parseInt(reviewIdStr);
                FeedBackDAO dao = new FeedBackDAO();
                // Đánh dấu đã đọc
                dao.markReplyAsRead(reviewId);
                // Lấy review để lấy productId
                Review reply = dao.getReviewById(reviewId);
                if (reply != null) {
                    int productId = reply.getProductID();
                    response.sendRedirect(request.getContextPath() + "/ProductDetail?id=" + productId);
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        response.sendRedirect("home");
    }
}
