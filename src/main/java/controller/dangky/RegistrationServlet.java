package controller.dangky;

import dao.AccountDAO1;
import dao.TokenDAO;

import jakarta.mail.MessagingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Account;
import model.Token;
import db.EmailUtil;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@WebServlet(name = "RegistrationServlet", urlPatterns = {"/RegistrationServlet"})
public class RegistrationServlet extends HttpServlet {

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
            out.println("<title>Servlet NewServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet NewServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
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
        System.out.println("Redirecting to /register");
        response.sendRedirect(request.getContextPath() + "/register");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String address = request.getParameter("address");
        String gender = request.getParameter("gender");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        System.out.println("Thử đăng ký: tên người dùng=" + username + ", email=" + email + ", số điện thoại=" + phone + ", địa chỉ=" + address + ", giới tính=" + gender);

        // Kiểm tra các trường bắt buộc
        if (username == null || username.trim().isEmpty()
                || fullName == null || fullName.trim().isEmpty()
                || email == null || email.trim().isEmpty()
                || phone == null || phone.trim().isEmpty()
                || password == null || password.trim().isEmpty()
                || confirmPassword == null || confirmPassword.trim().isEmpty()) {
            System.out.println("Thiếu các trường bắt buộc");
            request.setAttribute("error", "Vui lòng điền đầy đủ thông tin!");
            request.getRequestDispatcher("WEB-INF/customer/dangky/register.jsp").forward(request, response);
            return;
        }

        // Kiểm tra mật khẩu khớp
        if (!password.equals(confirmPassword)) {
            System.out.println("Mật khẩu và xác nhận mật khẩu không khớp");
            request.setAttribute("error", "Mật khẩu xác nhận không khớp!");
            request.getRequestDispatcher("WEB-INF/customer/dangky/register.jsp").forward(request, response);
            return;
        }

        // Kiểm tra định dạng mật khẩu
        String passwordRegex = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{6,}$";
        if (!password.matches(passwordRegex)) {
            System.out.println("Định dạng mật khẩu không hợp lệ cho email=" + email);
            request.setAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự, chứa chữ, số, và ký tự đặc biệt!");
            request.getRequestDispatcher("WEB-INF/customer/dangky/register.jsp").forward(request, response);
            return;
        }

        // Kiểm tra định dạng số điện thoại
        String phoneRegex = "^\\d{10}$";
        if (!phone.matches(phoneRegex)) {
            System.out.println("Định dạng số điện thoại không hợp lệ: " + phone);
            request.setAttribute("error", "Số điện thoại phải có đúng 10 chữ số!");
            request.getRequestDispatcher("WEB-INF/customer/dangky/register.jsp").forward(request, response);
            return;
        }

        AccountDAO1 accountDAO1 = new AccountDAO1();
        try {
            // Kiểm tra email hoặc username đã tồn tại
            if (accountDAO1.checkEmailExists(email)) {
                System.out.println("Email đã tồn tại: " + email);
                request.setAttribute("error", "Email đã được sử dụng!");
                request.getRequestDispatcher("WEB-INF/customer/dangky/register.jsp").forward(request, response);
                return;
            }
            if (accountDAO1.checkUsernameExists(username)) {
                System.out.println("Tên người dùng đã tồn tại: " + username);
                request.setAttribute("error", "Tên người dùng đã được sử dụng!");
                request.getRequestDispatcher("WEB-INF/customer/dangky/register.jsp").forward(request, response);
                return;
            }

            // Tạo tài khoản khách hàng (Role=0)
            Account account = new Account();
            account.setUsername(username);
            account.setPassword(password);
            account.setEmail(email.trim().toLowerCase());
            account.setFullName(fullName);
            account.setPhone(phone);
            account.setAddress(address);
            account.setGender(gender);
            account.setRole(0);
            account.setStatus("Pending");

            int accountId = accountDAO1.insertAccount(account);
            System.out.println("Tài khoản đã được đăng ký với ID: " + accountId);

            // Tạo và gửi OTP
            String otp = EmailUtil.generateOtp();
            try {
                EmailUtil.sendOtpEmail(email, otp);
                System.out.println("OTP đã được gửi đến " + email + ": " + otp);
            } catch (MessagingException e) {
                System.out.println("Gửi email chứa OTP đến " + email + " thất bại: " + e.getMessage());
                request.setAttribute("error", "Lỗi gửi email OTP. Vui lòng kiểm tra email và thử lại.");
                request.getRequestDispatcher("WEB-INF/customer/dangky/register.jsp").forward(request, response);
                return;
            }

            // Lưu token OTP
            Token token = new Token();
            token.setAccountId(accountId);
            token.setToken(otp.trim());
            token.setStatus("unused");
            Timestamp timeAdd = Timestamp.valueOf(LocalDateTime.now());
            token.setTimeAdd(timeAdd);
            token.setTimeExp(Timestamp.valueOf(LocalDateTime.now().plusMinutes(10)));
            System.out.println("Thêm token: AccountID=" + accountId + ", Token=" + otp + ", Time_Add=" + timeAdd + ", Time_Exp=" + token.getTimeExp());

            TokenDAO tokenDAO = new TokenDAO();
            tokenDAO.insertToken(token);
            System.out.println("Đã lưu token cho AccountID=" + accountId + ", Token=" + otp);

            // Lưu session
            request.getSession().setAttribute("accountId", accountId);
            response.sendRedirect(request.getContextPath() + "/otp");
        } catch (SQLException e) {
            System.out.println("Lỗi cơ sở dữ liệu trong quá trình đăng ký với email=" + email + ": " + e.getMessage());
            request.setAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            request.getRequestDispatcher("WEB-INF/customer/dangky/register.jsp").forward(request, response);
        }
    }

    @Override
    public String getServletInfo() {
        return "Handles customer registration";
    }
}
