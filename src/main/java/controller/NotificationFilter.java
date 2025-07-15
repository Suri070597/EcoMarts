package controller;

import dao.FeedBackDAO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Account;
import java.io.IOException;
import java.util.List;

@WebFilter(filterName = "NotificationFilter", urlPatterns = { "/home", "/cart", "/ProductDetail",
        "/ViewAllProductServlet", "/customer/*", "/SearchProduct" })
public class NotificationFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpSession session = req.getSession(false);
        if (session != null) {
            Account acc = (Account) session.getAttribute("account");
            if (acc != null && acc.getRole() == 0) { // chỉ customer
                try {
                    FeedBackDAO dao = new FeedBackDAO();
                    List<model.Review> unreadList = dao.getUnreadStaffRepliesForCustomer(acc.getAccountID());
                    request.setAttribute("unreadList", unreadList);
                    request.setAttribute("unreadCount", unreadList != null ? unreadList.size() : 0);
                } catch (Exception e) {
                    request.setAttribute("unreadList", null);
                    request.setAttribute("unreadCount", 0);
                }
            }
        }
        chain.doFilter(request, response);
    }
}