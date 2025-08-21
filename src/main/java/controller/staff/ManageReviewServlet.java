package controller.staff;

import dao.FeedBackDAO;

import model.Review;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

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
                try {
                    int reviewId = Integer.parseInt(idStr);
                    if ("hide".equals(action)) {
                        fbDao.updateReviewStatus(reviewId, "HIDDEN");
                        request.getSession().setAttribute("message", "Đã ẩn đánh giá thành công!");
                        response.sendRedirect(request.getContextPath() + "/staff/manage-review");
                        return;
                    } else if ("show".equals(action)) {
                        fbDao.updateReviewStatus(reviewId, "VISIBLE");
                        request.getSession().setAttribute("message", "Đã hiển thị đánh giá thành công!");
                        response.sendRedirect(request.getContextPath() + "/staff/manage-review");
                        return;
                    } else if ("delete".equals(action)) {
                        fbDao.deleteReview(reviewId);
                        request.getSession().setAttribute("message", "Đã xóa đánh giá thành công!");
                        response.sendRedirect(request.getContextPath() + "/staff/manage-review");
                        return;
                    }
                } catch (NumberFormatException ex) {
                    request.getSession().setAttribute("message", "ID đánh giá không hợp lệ");
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
            System.out.println(
                    "GỠ LỖI: Kích thước danh sách đánh giá trong servlet: "
                            + (reviewList != null ? reviewList.size() : "null"));

            request.setAttribute("reviewList", reviewList);
            // Tạo map phẳng hóa replies theo review gốc
            java.util.Map<Integer, List<Review>> flatRepliesMap = new java.util.HashMap<>();
            try {
                if (reviewList != null) {
                    for (Review root : reviewList) {
                        List<Review> flat = new ArrayList<>();
                        flattenReplies(root.getReplies(), flat, 0);
                        flatRepliesMap.put(root.getReviewID(), flat);
                    }
                }
            } catch (Exception ex) {
                // Không để vỡ trang nếu lỗi phẳng hóa
                ex.printStackTrace();
            }
            request.setAttribute("flatRepliesMap", flatRepliesMap);
            request.getRequestDispatcher("/WEB-INF/staff/review/manage-review.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("message", "Lỗi xử lý review: " + e.getMessage());
            request.setAttribute("reviewList", new ArrayList<Review>());
            request.getRequestDispatcher("/WEB-INF/staff/review/manage-review.jsp").forward(request, response);
        }
    }

    private void flattenReplies(List<Review> replies, List<Review> out, int depth) {
        if (replies == null)
            return;
        for (Review child : replies) {
            child.setDepth(depth);
            out.add(child);
            flattenReplies(child.getReplies(), out, depth + 1);
        }
    }
}
