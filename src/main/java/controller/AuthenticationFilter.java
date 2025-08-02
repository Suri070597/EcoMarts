package controller;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Filter to protect admin and staff URLs from unauthorized access
 * Ensures users are logged in and have proper role before accessing restricted areas
 */
@WebFilter(filterName = "AuthenticationFilter", urlPatterns = {"/admin/*", "/staff/*", "/admin", "/staff"})
public class AuthenticationFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        
        String requestURI = httpRequest.getRequestURI();
        System.out.println("AuthenticationFilter checking: " + requestURI);
        
        boolean isLoggedIn = session != null && session.getAttribute("account") != null;
        Integer role = null;
        if (session != null) {
            role = (Integer) session.getAttribute("role");
        }
        
        // Check if the requested URL is an admin URL
        boolean isAdminURL = requestURI.contains("/admin");
        // Check if the requested URL is a staff URL
        boolean isStaffURL = requestURI.contains("/staff");
        
        if (isLoggedIn) {
            // If user is logged in, check appropriate role for URL
            if (isAdminURL && role != null && role == 1) {
                // User is logged in as admin and accessing admin URL - allow
                chain.doFilter(request, response);
            } else if (isStaffURL && role != null && role == 2) {
                // User is logged in as staff and accessing staff URL - allow
                chain.doFilter(request, response);
            } else {
                // User is logged in but doesn't have appropriate permissions
                if (session != null) {
                    session.setAttribute("error", "Bạn không có quyền truy cập vào trang này!");
                }
                httpResponse.sendRedirect(httpRequest.getContextPath() + "/home");
            }
        } else {
            // User is not logged in, redirect to login page with error message
            if (session != null) {
                session.setAttribute("error", "Vui lòng đăng nhập để tiếp tục!");
            }
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/login");
        }
    }

    @Override
    public void destroy() {
        // Cleanup code if needed
    }
}