package controller.dangky;

import dao.AccountDAO1;
import dao.TokenDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Token;
import java.io.IOException;
import java.sql.SQLException;

@WebServlet(name = "OtpVerificationServlet", urlPatterns = {"/otp"})
public class OtpVerificationServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("Forwarding to /dangnhap/otp.jsp");
        request.getRequestDispatcher("WEB-INF/customer/dangky/otp.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String otp = request.getParameter("otp");
        Integer accountId = (Integer) request.getSession().getAttribute("accountId");

        System.out.println("OTP verification attempt: accountId=" + accountId + ", otp=" + otp);

        if (otp == null || otp.trim().isEmpty() || accountId == null) {
            System.out.println("Missing OTP or accountId");
            request.setAttribute("error", "Vui lòng nhập mã OTP!");
            request.getRequestDispatcher("WEB-INF/customer/dangky/otp.jsp").forward(request, response);
            return;
        }

        TokenDAO tokenDAO = new TokenDAO();
        try {
            Token token = tokenDAO.getValidToken(otp, accountId);
            if (token != null) {
                tokenDAO.updateTokenStatus(token.getTokenId(), "used");
                AccountDAO1 accountDAO = new AccountDAO1();
                accountDAO.updateAccountStatus(accountId, "Active");
                System.out.println("OTP verified successfully for accountId=" + accountId);
                request.getSession().removeAttribute("accountId");
                response.sendRedirect(request.getContextPath() + "/login");
            } else {
                System.out.println("Invalid or expired OTP for accountId=" + accountId);
                request.setAttribute("error", "Mã OTP không hợp lệ hoặc đã hết hạn!");
                request.getRequestDispatcher("WEB-INF/customer/dangky/otp.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            System.out.println("Database error during OTP verification: " + e.getMessage());
            request.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            request.getRequestDispatcher("WEB-INF/customer/dangky/otp.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Handles OTP verification";
    }
}