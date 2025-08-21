/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.nio.file.Paths;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.Part;
import model.Account;
import dao.FeedBackDAO;

/**
 *
 * @author LNQB
 */
@WebServlet(name = "ReviewServlet", urlPatterns = {"/Review"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
        maxFileSize = 1024 * 1024 * 10, // 10 MB
        maxRequestSize = 1024 * 1024 * 15 // 15 MB
)
public class ReviewServlet extends HttpServlet {

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
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet ReviewServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet ReviewServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the
    // + sign on the left to edit the code.">
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
        String action = request.getParameter("action");
        if ("delete".equals(action)) {
            // Xử lý xóa review
            String reviewIdStr = request.getParameter("reviewId");
            if (reviewIdStr != null && !reviewIdStr.isEmpty()) {
                try {
                    int reviewId = Integer.parseInt(reviewIdStr);
                    FeedBackDAO dao = new FeedBackDAO();

                    // Kiểm tra quyền xóa
                    Account acc = (Account) request.getSession().getAttribute("account");
                    if (acc != null && dao.canDeleteReview(reviewId, acc.getAccountID())) {
                        dao.deleteReview(reviewId);
                        request.getSession().setAttribute("message", "Xóa đánh giá thành công!");
                    } else {
                        request.getSession().setAttribute("message",
                                "Bạn chỉ có thể xóa đánh giá của chính mình và trong vòng 30 ngày!");
                    }

                    // Redirect về trang trước
                    String referer = request.getHeader("Referer");
                    if (referer != null) {
                        response.sendRedirect(referer);
                    } else {
                        response.sendRedirect("home");
                    }
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    request.getSession().setAttribute("message", "Có lỗi xảy ra khi xóa đánh giá!");
                    response.sendRedirect("home");
                    return;
                }
            }
        } else if ("edit".equals(action)) {
            // Xử lý sửa review - forward về ProductDetail
            String reviewIdStr = request.getParameter("reviewId");
            if (reviewIdStr != null && !reviewIdStr.isEmpty()) {
                try {
                    int reviewId = Integer.parseInt(reviewIdStr);
                    FeedBackDAO dao = new FeedBackDAO();
                    model.Review editingReview = dao.getReviewById(reviewId);

                    if (editingReview != null) {
                        // Kiểm tra quyền sửa
                        Account acc = (Account) request.getSession().getAttribute("account");
                        if (acc != null && dao.canEditReview(reviewId, acc.getAccountID())) {
                            boolean isRootReview = (editingReview.getParentReviewID() == null);
                            request.setAttribute("editingReview", editingReview);
                            request.setAttribute("isRootReview", isRootReview);

                            // Forward về ProductDetailServlet với productId
                            String productId = String.valueOf(editingReview.getProductID());
                            request.getRequestDispatcher("ProductDetail?id=" + productId).forward(request, response);
                            return;
                        } else {
                            request.getSession().setAttribute("message",
                                    "Bạn chỉ có thể sửa đánh giá của chính mình và trong vòng 30 ngày!");
                            response.sendRedirect("ProductDetail?id=" + editingReview.getProductID());
                            return;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        processRequest(request, response);
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
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        String ratingStr = request.getParameter("rating");
        String comment = request.getParameter("comment");
        String orderIdStr = request.getParameter("orderId");
        String productIdStr = request.getParameter("productId");
        String parentReviewIdStr = request.getParameter("parentReviewId");
        String reviewIdStr = request.getParameter("reviewId");

        // Lấy accountId từ session đăng nhập
        Account acc = (Account) request.getSession().getAttribute("account");
        if (acc == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        int accountId = acc.getAccountID();

        // Xử lý file upload
        Part filePart = request.getPart("image");
        String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
        String imageUrl = null;
        boolean invalidImage = false;
        if (fileName != null && !fileName.isEmpty()) {
            // Kiểm tra định dạng file ảnh
            String lowerFileName = fileName.toLowerCase();
            if (!(lowerFileName.endsWith(".jpg") || lowerFileName.endsWith(".jpeg")
                    || lowerFileName.endsWith(".png"))) {
                request.getSession().setAttribute("message", "Chỉ cho phép tải lên các tệp ảnh (jpg, jpeg, png)!");
                response.sendRedirect("ProductDetail?id=" + (productIdStr != null ? productIdStr : ""));
                return;
            } else {
                String uploadPath = "C:\\EcoMarts\\ReviewImages";
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }
                String savedFileName = System.currentTimeMillis() + "_" + fileName;
                filePart.write(uploadPath + File.separator + savedFileName);
                imageUrl = savedFileName; // chỉ lưu tên file
            }
        }

        // Gọi DAO để xử lý
        FeedBackDAO dao = new FeedBackDAO();
        boolean success = false;
        String customError = null;
        String from = request.getParameter("from");

        try {
            if ("edit".equals(action) && reviewIdStr != null && !reviewIdStr.isEmpty()) {
                // Xử lý sửa review
                int reviewId = Integer.parseInt(reviewIdStr);
                Integer rating = null;
                if (ratingStr != null && !ratingStr.isEmpty()) {
                    rating = Integer.parseInt(ratingStr);
                }

                // Kiểm tra quyền sửa
                if (!dao.canEditReview(reviewId, accountId)) {
                    customError = "Bạn chỉ có thể sửa đánh giá của chính mình và trong vòng 30 ngày!";
                } else {
                    success = dao.updateReview(reviewId, accountId, rating, comment, imageUrl);
                    if (success) {
                        request.getSession().setAttribute("message", "Cập nhật đánh giá thành công!");
                    } else {
                        customError = "Không thể cập nhật đánh giá!";
                    }
                }
            } else {
                // Xử lý thêm review/reply mới (logic cũ)
                Integer parentReviewId = (parentReviewIdStr == null || parentReviewIdStr.isEmpty()) ? null
                        : Integer.parseInt(parentReviewIdStr);

                // Kiểm tra dữ liệu đầu vào
                if (parentReviewId == null) {
                    // Review gốc: bắt buộc rating, orderId, productId, comment
                    if (ratingStr == null || ratingStr.isEmpty()
                            || orderIdStr == null || orderIdStr.isEmpty()
                            || productIdStr == null || productIdStr.isEmpty()
                            || comment == null || comment.isEmpty()) {
                        // Nếu thiếu rating, thông báo riêng
                        if (ratingStr == null || ratingStr.isEmpty()) {
                            request.getSession().setAttribute("message", "Vui lòng chọn số sao đánh giá cho sản phẩm!");
                        } else {
                            request.getSession().setAttribute("message",
                                    "Bạn không đủ điều kiện để đánh giá sản phẩm!");
                        }
                        response.sendRedirect("ProductDetail?id=" + (productIdStr != null ? productIdStr : ""));
                        return;
                    }
                } else {
                    // Reply: KHÔNG bắt buộc orderId, chỉ cần productId, comment
                    if (productIdStr == null || productIdStr.isEmpty()
                            || comment == null || comment.isEmpty()) {
                        request.getSession().setAttribute("message", "Vui lòng điền đầy đủ thông tin trả lời!");
                        response.sendRedirect("ProductDetail?id=" + (productIdStr != null ? productIdStr : ""));
                        return;
                    }
                }

                Integer rating = null;
                if (ratingStr != null && !ratingStr.isEmpty()) {
                    rating = Integer.parseInt(ratingStr);
                }
                int productId = Integer.parseInt(productIdStr);
                Integer orderId = null;
                if (orderIdStr != null && !orderIdStr.isEmpty()) {
                    orderId = Integer.valueOf(orderIdStr);
                }

                success = dao.addReview(parentReviewId, orderId, productId, accountId, rating, comment, imageUrl);
                if (success) {
                    request.getSession().setAttribute("message",
                            parentReviewId == null ? "Gửi đánh giá thành công!" : "Gửi trả lời thành công!");
                } else {
                    // Chỉ còn 2 thông báo lỗi
                    if (parentReviewId == null) {
                        try {
                            if (dao.isReviewExists(orderId, productId, accountId)) {
                                customError = "Mỗi đơn hàng chỉ được đánh giá một lần.";
                            }
                        } catch (Exception ex) {
                            ex.getStackTrace();
                        }
                    } else {
                        customError = "Không thể gửi đánh giá/trả lời!";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            customError = "Có lỗi xảy ra!";
        }

        if (customError != null) {
            request.getSession().setAttribute("message", customError);
        }

        // Redirect: nếu đến từ staff manage-review thì quay lại đó
        if ("staff".equals(from)) {
            response.sendRedirect("staff/manage-review");
        } else {
            // Redirect về ProductDetailServlet để load lại dữ liệu sản phẩm
            response.sendRedirect("ProductDetail?id=" + productIdStr);
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
