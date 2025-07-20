/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dao.OrderDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import model.RevenueStats;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author nguye
 */
@WebServlet("/admin/export-monthly-revenue")
public class ExportMonthlyRevenueServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int month = Integer.parseInt(request.getParameter("month"));
        int year = Integer.parseInt(request.getParameter("year"));

        OrderDAO dao = new OrderDAO();
        List<RevenueStats> data = dao.getMonthlyRevenueDetails(month, year);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Monthly Revenue");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Product Name");
        header.createCell(1).setCellValue("Quantity Sold");
        header.createCell(2).setCellValue("Total Revenue");

        int rowNum = 1;
        for (RevenueStats stats : data) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(stats.getProductName());
            row.createCell(1).setCellValue(stats.getTotalQuantity());
            row.createCell(2).setCellValue(stats.getTotalRevenue());
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=Monthly_Revenue.xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
