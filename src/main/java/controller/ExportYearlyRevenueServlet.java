package controller;

import dao.OrderDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet("/admin/export-yearly-revenue")
public class ExportYearlyRevenueServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int year = Integer.parseInt(request.getParameter("year"));
        OrderDAO dao = new OrderDAO();
        List<Map<String, Object>> data = dao.getProductSalesByYear(year);

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Revenue " + year);

            // Tạo tiêu đề cột
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("No.");
            header.createCell(1).setCellValue("Product Name");
            header.createCell(2).setCellValue("Quantity Sold");
            header.createCell(3).setCellValue("Total Revenue");

            // Ghi dữ liệu
            int rowNum = 1;
            int index = 1;
            for (Map<String, Object> item : data) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(index++);
                row.createCell(1).setCellValue((String) item.get("productName"));
                row.createCell(2).setCellValue((int) item.get("totalQuantity"));
                row.createCell(3).setCellValue((double) item.get("totalRevenue"));
            }

            // Auto resize column
            for (int i = 0; i <= 3; i++) {
                sheet.autoSizeColumn(i);
            }

            // Cấu hình response
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=revenue-" + year + ".xlsx");

            workbook.write(response.getOutputStream());
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error generating Excel file.");
        }
    }
}
