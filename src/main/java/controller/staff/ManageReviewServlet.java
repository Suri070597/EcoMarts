package controller.staff;

import dao.FeedBackDAO;
import dao.ProductDAO;
import dao.AccountDAO;
import model.Review;
import model.Product;
import model.Account;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/staff/manage-review")
public class ManageReviewServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        String idStr = request.getParameter("id");
        try {
            FeedBackDAO fbDao = new FeedBackDAO();
            String search = request.getParameter("search");
            if (action != null && idStr != null) {
                int reviewId = Integer.parseInt(idStr);
                if ("hide".equals(action)) {
                    fbDao.updateReviewStatus(reviewId, "HIDDEN");
                    response.sendRedirect(request.getContextPath() + "/staff/manage-review");
                    return;
                } else if ("show".equals(action)) {
                    fbDao.updateReviewStatus(reviewId, "VISIBLE");
                    response.sendRedirect(request.getContextPath() + "/staff/manage-review");
                    return;
                } else if ("delete".equals(action)) {
                    fbDao.deleteReview(reviewId);
                    response.sendRedirect(request.getContextPath() + "/staff/manage-review");
                    return;
                }
            }
            List<Review> reviewList;
            if (search != null && !search.trim().isEmpty()) {
                reviewList = fbDao.getAllReviewsWithAccountAndProductByProductName(search.trim());
            } else {
                reviewList = fbDao.getAllReviewsWithAccountAndProduct();
            }
            // Đảm bảo reviewList không null
            if (reviewList == null)
                reviewList = new java.util.ArrayList<>();
            System.out.println(
                    "DEBUG: Review list size in servlet: " + (reviewList != null ? reviewList.size() : "null"));
            request.setAttribute("reviewList", reviewList);
            request.getRequestDispatcher("/WEB-INF/staff/review/manage-review.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(500, "Lỗi xử lý review");
        }
    }
}