package controller.staff;

import dao.FeedBackDAO;
import model.Account;
import model.Review;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

@WebServlet("/staff/reply-review")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, // 1MB
        maxFileSize = 5 * 1024 * 1024, // 5MB
        maxRequestSize = 10 * 1024 * 1024 // 10MB
)
public class StaffReplyServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        Account account = (Account) request.getSession().getAttribute("account");
        if (account == null || account.getRole() != 2) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Không có quyền truy cập");
            return;
        }

        String reviewIdStr = request.getParameter("reviewId");
        String productIdStr = request.getParameter("productId");
        String comment = request.getParameter("comment");

        if (reviewIdStr == null || productIdStr == null || comment == null || comment.trim().isEmpty()) {
            request.getSession().setAttribute("message", "Vui lòng nhập đầy đủ thông tin phản hồi!");
            response.sendRedirect(request.getContextPath() + "/staff/manage-review");
            return;
        }

        try {
            int parentReviewId = Integer.parseInt(reviewIdStr);
            int productId = Integer.parseInt(productIdStr);

            FeedBackDAO fbDao = new FeedBackDAO();
            Review original = fbDao.getReviewById(parentReviewId);
            if (original == null) {
                request.getSession().setAttribute("message", "Không tìm thấy đánh giá gốc!");
                response.sendRedirect(request.getContextPath() + "/staff/manage-review");
                return;
            }

            // Chỉ cho phép reply đánh giá của customer (không phải staff)
            if (original.getAccountRole() == 2) {
                request.getSession().setAttribute("message", "Chỉ reply đánh giá của khách hàng!");
                response.sendRedirect(request.getContextPath() + "/staff/manage-review");
                return;
            }

            // Sản phẩm phải khớp
            if (original.getProductID() != productId) {
                request.getSession().setAttribute("message", "Sản phẩm không khớp với đánh giá!");
                response.sendRedirect(request.getContextPath() + "/staff/manage-review");
                return;
            }

            // Xử lý upload ảnh (tùy chọn)
            String imageUrl = null;
            Part imagePart = request.getPart("image");
            if (imagePart != null && imagePart.getSize() > 0) {
                String submitted = imagePart.getSubmittedFileName();
                if (submitted != null && !submitted.isEmpty()) {
                    String lower = submitted.toLowerCase();
                    if (!(lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png")
                            || lower.endsWith(".gif"))) {
                        request.getSession().setAttribute("message", "Chỉ chấp nhận file ảnh (JPG, JPEG, PNG, GIF)!");
                        response.sendRedirect(request.getContextPath() + "/staff/manage-review");
                        return;
                    }
                    String fileName = System.currentTimeMillis() + "_" + Paths.get(submitted).getFileName().toString();
                    String uploadPath = "C:\\EcoMarts\\ReviewImages";
                    File dir = new File(uploadPath);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    imagePart.write(uploadPath + File.separator + fileName);
                    imageUrl = fileName;
                }
            }

            boolean ok = fbDao.addReview(
                    parentReviewId, // parent
                    original.getOrderID() != 0 ? original.getOrderID() : null, // reply: cho phép null
                    productId,
                    account.getAccountID(),
                    null, // rating null cho reply
                    comment.trim(),
                    imageUrl);

            if (ok) {
                request.getSession().setAttribute("message", "Phản hồi thành công!");
            } else {
                request.getSession().setAttribute("message", "Không thể gửi phản hồi!");
            }
        } catch (NumberFormatException nfe) {
            request.getSession().setAttribute("message", "Dữ liệu không hợp lệ!");
        } catch (Exception ex) {
            ex.printStackTrace();
            request.getSession().setAttribute("message", "Lỗi hệ thống: " + ex.getMessage());
        }

        response.sendRedirect(request.getContextPath() + "/staff/manage-review");
    }
}
