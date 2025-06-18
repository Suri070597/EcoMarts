package controller;

import java.io.IOException;
import java.util.List;

import dao.VoucherDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Voucher;

@WebServlet(name = "AdminVoucherServlet", urlPatterns = {"/admin/voucher"})
public class AdminVoucherServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String view = request.getParameter("view");
        String action = request.getParameter("action");
        VoucherDAO voucherDAO = new VoucherDAO();

        if (action != null && action.equals("delete")) {
            int id = Integer.parseInt(request.getParameter("id"));
            boolean result = voucherDAO.deleteVoucher(id);
            if (!result) {
                request.setAttribute("errorMessage", "Không thể xóa voucher này do đã phát sinh dữ liệu liên quan!");
                List<Voucher> vouchers = voucherDAO.getAllVouchers();
                request.setAttribute("vouchers", vouchers);
                request.getRequestDispatcher("/WEB-INF/admin/voucher/manage-voucher.jsp").forward(request, response);
                return;
            }
            response.sendRedirect(request.getContextPath() + "/admin/voucher");
            return;
        }
        if (action != null && action.equals("status")) {
            int id = Integer.parseInt(request.getParameter("id"));
            boolean currentStatus = Boolean.parseBoolean(request.getParameter("status")); // convert thành boolean
            boolean newStatus = !currentStatus; // đảo trạng thái
            boolean result = voucherDAO.updateVoucherStatus(id, newStatus);
            response.sendRedirect(request.getContextPath() + "/admin/voucher");
            return;
        }

        if (view != null) {
            switch (view) {
                case "create":
                    request.getRequestDispatcher("/WEB-INF/admin/voucher/create-voucher.jsp").forward(request, response);
                    break;
                case "edit": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    Voucher voucher = voucherDAO.getVoucherById(id);
                    if (voucher != null) {
                        request.setAttribute("voucher", voucher);
                        request.getRequestDispatcher("/WEB-INF/admin/voucher/edit-voucher.jsp").forward(request, response);
                    } else {
                        response.sendRedirect(request.getContextPath() + "/admin/voucher");
                    }
                    break;
                }
                case "detail": {
                    int id = Integer.parseInt(request.getParameter("id"));
                    Voucher voucher = voucherDAO.getVoucherById(id);
                    if (voucher != null) {
                        request.setAttribute("voucher", voucher);
                        request.getRequestDispatcher("/WEB-INF/admin/voucher/voucher-detail.jsp").forward(request, response);
                    } else {
                        response.sendRedirect(request.getContextPath() + "/admin/voucher");
                    }
                    break;
                }
                default: {
                    String keyword = request.getParameter("search");
                    List<Voucher> vouchers;
                    if (keyword != null && !keyword.trim().isEmpty()) {
                        vouchers = voucherDAO.searchVouchers(keyword);
                        request.setAttribute("keyword", keyword);
                    } else {
                        vouchers = voucherDAO.getAllVouchers();
                    }

                    int totalVouchers = voucherDAO.countVouchers();

                    request.setAttribute("vouchers", vouchers);
                    request.setAttribute("totalVouchers", totalVouchers);

                    request.getRequestDispatcher("/WEB-INF/admin/voucher/manage-voucher.jsp").forward(request, response);
                    break;
                }
            }
        } else {
            List<Voucher> vouchers = voucherDAO.getAllVouchers();
            int totalVouchers = voucherDAO.countVouchers();

            request.setAttribute("vouchers", vouchers);
            request.setAttribute("totalVouchers", totalVouchers);

            request.getRequestDispatcher("/WEB-INF/admin/voucher/manage-voucher.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        VoucherDAO voucherDAO = new VoucherDAO();

        if ("create".equals(action)) {
            try {
                Voucher voucher = extractVoucherFromRequest(request);
                boolean res = voucherDAO.insertVoucher(voucher);

                if (res) {
                    response.sendRedirect(request.getContextPath() + "/admin/voucher");
                } else {
                    request.setAttribute("errorMessage", "Failed to create voucher. Please try again.");
                    request.setAttribute("voucher", voucher);
                    request.getRequestDispatcher("/WEB-INF/admin/voucher/create-voucher.jsp").forward(request, response);
                }
            } catch (Exception e) {
                request.setAttribute("errorMessage", "Error: " + e.getMessage());
                request.getRequestDispatcher("/WEB-INF/admin/voucher/create-voucher.jsp").forward(request, response);
            }
        } else if ("edit".equals(action)) {
            try {
                int id = Integer.parseInt(request.getParameter("id"));
                Voucher voucher = extractVoucherFromRequest(request);
                voucher.setVoucherID(id);
                boolean res = voucherDAO.updateVoucher(voucher);

                if (res) {
                    response.sendRedirect(request.getContextPath() + "/admin/voucher");
                } else {
                    request.setAttribute("errorMessage", "Failed to update voucher. Please try again.");
                    request.setAttribute("voucher", voucher);
                    request.getRequestDispatcher("/WEB-INF/admin/voucher/edit-voucher.jsp").forward(request, response);
                }
            } catch (Exception e) {
                request.setAttribute("errorMessage", "Error: " + e.getMessage());
                request.getRequestDispatcher("/WEB-INF/admin/voucher/edit-voucher.jsp").forward(request, response);
            }
        }
    }

    private Voucher extractVoucherFromRequest(HttpServletRequest request) {
        Voucher voucher = new Voucher();
        voucher.setVoucherCode(request.getParameter("voucherCode"));
        voucher.setDescription(request.getParameter("description"));
        voucher.setDiscountAmount(Double.parseDouble(request.getParameter("discountAmount")));
        voucher.setMinOrderValue(Double.parseDouble(request.getParameter("minOrderValue")));
        voucher.setMaxUsage(Integer.parseInt(request.getParameter("maxUsage")));
        voucher.setUsageCount(Integer.parseInt(request.getParameter("usageCount")));
        voucher.setStartDate(java.sql.Timestamp.valueOf(request.getParameter("startDate") + " 00:00:00"));
        voucher.setEndDate(java.sql.Timestamp.valueOf(request.getParameter("endDate") + " 23:59:59"));
        voucher.setActive("on".equals(request.getParameter("isActive")));

        String categoryIdParam = request.getParameter("categoryID");
        if (categoryIdParam != null && !categoryIdParam.trim().isEmpty()) {
            voucher.setCategoryID(Integer.parseInt(categoryIdParam));
        } else {
            voucher.setCategoryID(null);
        }

        return voucher;
    }
}
