package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Account;
import util.VNPayUtil;

/**
 * Servlet để xử lý thanh toán VNPay
 */
@WebServlet(name = "VNPayServlet", urlPatterns = {"/vnpay"})
public class VNPayServlet extends HttpServlet {

    /**
     * Xử lý yêu cầu GET - tạo URL thanh toán và chuyển hướng
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Account account = (Account) session.getAttribute("account");

        // Kiểm tra đăng nhập
        if (account == null) {
            session.setAttribute("redirectAfterLogin", "home");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Lấy thông tin đơn hàng từ tham số
        String orderIdStr = request.getParameter("orderId");
        String amountStr = request.getParameter("amount");

        if (orderIdStr == null || amountStr == null) {
            session.setAttribute("errorMessage", "Thiếu thông tin thanh toán");
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        try {
            int orderId = Integer.parseInt(orderIdStr);
            double amount = Double.parseDouble(amountStr);

            // Lưu thông tin đơn hàng vào session để xử lý sau khi thanh toán
            session.setAttribute("pendingOrderId", orderId);
            session.setAttribute("pendingAmount", amount);

            // Tạo URL thanh toán VNPay với orderId
            String paymentUrl = VNPayUtil.getPaymentUrl(request, response, amount, orderId);
            
            // Ghi log thông tin thanh toán
            System.out.println("Redirecting to VNPay payment URL: " + paymentUrl);
            System.out.println("Order ID: " + orderId + ", Amount: " + amount);

            // Chuyển hướng đến trang thanh toán VNPay
            response.sendRedirect(paymentUrl);
        } catch (NumberFormatException e) {
            session.setAttribute("errorMessage", "Thông tin thanh toán không hợp lệ");
            response.sendRedirect(request.getContextPath() + "/home");
        }
    }

    /**
     * Xử lý yêu cầu POST - không sử dụng
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Chuyển hướng đến phương thức GET
        doGet(request, response);
    }
}