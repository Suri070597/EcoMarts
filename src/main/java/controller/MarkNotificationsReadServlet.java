package controller;

import dao.FeedBackDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Account;
import java.io.IOException;

@WebServlet(name = "MarkNotificationsReadServlet", urlPatterns = { "/mark-notifications-read" })
public class MarkNotificationsReadServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Account acc = (Account) request.getSession().getAttribute("account");
        if (acc != null) {
            try {
                FeedBackDAO dao = new FeedBackDAO();
                dao.markAllStaffRepliesAsRead(acc.getAccountID());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // Quay lại trang trước hoặc về home
        String referer = request.getHeader("referer");
        if (referer != null) {
            response.sendRedirect(referer);
        } else {
            response.sendRedirect("home");
        }
    }
}
