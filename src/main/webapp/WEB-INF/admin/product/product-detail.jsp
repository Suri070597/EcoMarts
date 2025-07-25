<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="model.Product" %>
<%
    Product product = (Product) request.getAttribute("productDetail");
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Chi Tiết Sản Phẩm</title>
   <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/img/eco.png" type="image/x-icon">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis()%>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis()%>">
    </head>
    <body>
        <div class="container-fluid">
            <%-- Sidebar --%>
            <jsp:include page="../components/sidebar.jsp" />

            <div class="main-content">
        <div class="container mt-4">
            <div class="card">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <h2>Chi Tiết Sản Phẩm</h2>
                    <a href="${pageContext.request.contextPath}/admin/product" class="btn btn-secondary">Quay lại</a>
                </div>
                <div class="card-body">
                    <div class="row">
                        <!-- áº¢nh bÃªn trÃ¡i -->
                        <div class="col-md-5 text-center">
                            <img src="<%= request.getContextPath()%>/ImageServlet?name=<%= product.getImageURL() %>" alt="áº¢nh sáº£n pháº©m" class="img-fluid rounded shadow" style="max-height:350px;">
                        </div>
                        <!-- ThÃ´ng tin bÃªn pháº£i -->
                        <div class="col-md-7">
                            <h3 class="mb-3"><%= product.getProductName() %></h3>
                            <table class="table table-borderless">
                                <tr><th>Mã sản phẩm:</th><td><%= product.getProductID() %></td></tr>
                                <% 
                                    boolean isFruit = false;
                                    if (product.getCategory() != null) {
                                        int cateId = product.getCategory().getCategoryID();
                                        Integer parentId = product.getCategory().getParentID();
                                        isFruit = (cateId == 3) || (parentId != null && parentId == 3);
                                    }
                                %>
                                <% if (isFruit) { %>
                                    <tr><th>Giá bán:</th><td><%= new java.text.DecimalFormat("#,###").format(product.getPrice()) %> VNĐ / kg</td></tr>
                                    <tr><th>Số lượng tồn kho:</th>
                                        <td>
                                            <% double kg = product.getStockQuantity();
                                               String display = new java.text.DecimalFormat("#,##0").format(kg) + "kg";
                                               if (kg >= 1000) {
                                                   double ton = kg / 1000.0;
                                                   display += " (" + new java.text.DecimalFormat("#.#").format(ton) + " tấn)";
                                               } else if (kg >= 100) {
                                                   double ta = kg / 100.0;
                                                   display += " (" + new java.text.DecimalFormat("#.#").format(ta) + " tạ)";
                                               }
                                               out.print(display);
                                            %>
                                        </td>
                                    </tr>
                                <% } else { %>
                                    <tr><th>Giá lẻ:</th><td><%= new java.text.DecimalFormat("#,###").format(product.getPrice()) %> VNĐ / <%= product.getUnit() %></td></tr>
                                    <tr><th>Giá thùng:</th><td><%= product.getUnitPerBox() > 0 ? new java.text.DecimalFormat("#,###").format(product.getPrice() * product.getUnitPerBox()) : "-" %> VNĐ / <%= product.getBoxUnitName() %></td></tr>
                                    <tr><th>Số lượng tồn kho:</th><td>
    <% double qty = product.getStockQuantity();
       int unitPerBox = product.getUnitPerBox();
       String boxUnit = product.getBoxUnitName();
       String itemUnit = product.getItemUnitName();
       if (qty == Math.floor(qty)) {
           out.print((long)qty);
       } else {
           out.print(new java.text.DecimalFormat("#").format(qty));
       }
       out.print(" " + (itemUnit != null ? itemUnit : product.getUnit()));
       if (unitPerBox > 1 && boxUnit != null && !boxUnit.isEmpty()) {
           double boxQty = qty / unitPerBox;
           out.print(" (");
           if (boxQty == Math.floor(boxQty)) {
               out.print((long)boxQty);
           } else {
               out.print(new java.text.DecimalFormat("#").format(boxQty));
           }
           out.print(" " + boxUnit + ")");
       }
    %>
</td></tr>
                                    <tr><th>Số lượng trong 1 thùng:</th><td><%= product.getUnitPerBox() %> <%= product.getItemUnitName() %> / <%= product.getBoxUnitName() %></td></tr>
                                    <tr><th>Đơn vị thùng:</th><td><%= product.getBoxUnitName() %></td></tr>
                                    <tr><th>Đơn vị nhỏ nhất:</th><td><%= product.getItemUnitName() %></td></tr>
                                <% } %>
                                <tr><th>Danh mục:</th><td><%= product.getCategory() != null ? product.getCategory().getCategoryName() : "" %></td></tr>
                                <tr><th>Nhà cung cấp:</th><td><%= product.getSupplier() != null ? product.getSupplier().getCompanyName() : "" %></td></tr>
                                <tr>
                                    <% if (isFruit) { %>
                                        <th>Ngày nhập kho:</th>
                                    <% } else { %>
                                        <th>Ngày sản xuất:</th>
                                    <% } %>
                                    <td>
                                        <%= product.getManufactureDate() != null ? new java.text.SimpleDateFormat("dd/MM/yyyy").format(product.getManufactureDate()) : "" %>
                                    </td>
                                </tr>
                                <tr><th>Hạn sử dụng:</th>
                                    <td>
                                        <%= product.getExpirationDate() != null ? new java.text.SimpleDateFormat("dd/MM/yyyy").format(product.getExpirationDate()) : "" %>
                                    </td>
                                </tr>
                                <tr><th>Mô tả:</th>
                                    <td style="white-space: pre-line;"><%= product.getDescription() %></td>
                                </tr>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 