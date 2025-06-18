/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller.register;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "StaffServlet", urlPatterns = {"/staff"})
public class StaffServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("Accessing staff page");
        Integer role = (Integer) request.getSession().getAttribute("role");
        String email = (String) request.getSession().getAttribute("email");

        if (role != null && role == 2 && email != null) {
            System.out.println("Staff access granted: email=" + email);
            request.getRequestDispatcher("/WEB-INF/staff/staff.jsp").forward(request, response);
        } else {
            System.out.println("Unauthorized access to staff page: email=" + (email != null ? email : "null") + ", role=" + (role != null ? role : "null"));
            request.getSession().setAttribute("error", "Vui lòng đăng nhập với tài khoản nhân viên!");
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }

    @Override
    public String getServletInfo() {
        return "Handles staff page access";
    }
}