<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="model.Product" %>
<%
    Product product = (Product) request.getAttribute("productDetail");
    boolean isFruit = false;
    if (product.getCategory() != null) {
        int cateId = product.getCategory().getCategoryID();
        Integer parentId = product.getCategory().getParentID();
        isFruit = (cateId == 3) || (parentId != null && parentId == 3);
    }
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
                            <a href="${pageContext.request.contextPath}/staff/product" class="btn btn-secondary">Quay lại</a>
                        </div>
                        <div class="card-body">
                            <div class="row">
                                <!-- áº¢nh bÃªn trÃ¡i -->
                                <div class="col-md-5 text-center">
                                    <img src="<%= request.getContextPath()%>/ImageServlet?name=<%= product.getImageURL()%>" alt="áº¢nh sáº£n pháº©m" class="img-fluid rounded shadow" style="max-height:350px;">
                                </div>
                                <!-- ThÃ´ng tin bÃªn pháº£i -->
                                <div class="col-md-7">
                                    <h3 class="mb-3"><%= product.getProductName()%></h3>
                                    <table class="table table-borderless">
                                        <tr><th>Mã sản phẩm:</th><td><%= product.getProductID()%></td></tr>
                                        <% if (isFruit) { %>
                                        <%
                                            java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols();
                                            symbols.setGroupingSeparator('.');
                                            java.text.DecimalFormat formatter = new java.text.DecimalFormat("#,###", symbols);
                                        %>
                                        <tr>
                                            <th>Giá bán:</th>
                                            <td><%= formatter.format(product.getPrice())%> đ / kg</td>
                                        </tr>
                                        <tr><th>Số lượng tồn kho:</th>
                                            <td>
                                                <% double kg = product.getStockQuantity();
                                                    String display;
                                                    if (kg == Math.floor(kg)) {
                                                        display = new java.text.DecimalFormat("#,##0").format(kg) + "kg";
                                                    } else {
                                                        display = new java.text.DecimalFormat("#,##0.0").format(kg) + "kg";
                                                    }
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
                                        <%
                                            java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols();
                                            symbols.setGroupingSeparator('.');
                                            java.text.DecimalFormat formatter = new java.text.DecimalFormat("#,###", symbols);
                                        %>
                                        <tr>
                                            <th>Giá 1 <%= product.getBoxUnitName()%>:</th>
                                            <td><%= formatter.format(product.getPrice())%> đ</td>
                                        </tr>
                                        <tr>
                                            <th>Giá 1 <%= product.getItemUnitName()%>:</th>
                                            <td>
                                                <%= product.getUnitPerBox() > 0 ? formatter.format(product.getPrice() / product.getUnitPerBox()) : "-"%> đ
                                            </td>
                                        </tr>
                                        <%
                                            java.util.Map<String, Object> inv = (java.util.Map<String, Object>) request.getAttribute("inventory");
                                            Double boxQty = null, unitQty = null, packQty = null, packPrice = null;
                                            if (inv != null) {
                                                Object bq = inv.get("BOX_Quantity");
                                                if (bq instanceof Number) boxQty = ((Number) bq).doubleValue();
                                                Object uq = inv.get("UNIT_Quantity");
                                                if (uq instanceof Number) unitQty = ((Number) uq).doubleValue();
                                                Object pq = inv.get("PACK_Quantity");
                                                if (pq instanceof Number) packQty = ((Number) pq).doubleValue();
                                                Object pp = inv.get("PACK_Price");
                                                if (pp instanceof Number) packPrice = ((Number) pp).doubleValue();
                                            }
                                            double qty = boxQty != null ? boxQty : product.getStockQuantity();
                                        %>
                                        <tr>
                                            <th>Số lượng thùng:</th>
                                            <td>
                                                <%
                                                    if (qty == Math.floor(qty)) {
                                                        out.print((long) qty);
                                                    } else {
                                                        out.print(new java.text.DecimalFormat("#").format(qty));
                                                    }
                                                    out.print(" " + product.getBoxUnitName());
                                                %>
                                            </td>
                                        </tr>
                                        <%
                                            if (unitQty != null && unitQty > 0) {
                                        %>
                                        <tr>
                                            <th>Số lượng <%= product.getItemUnitName() %>:</th>
                                            <td>
                                                <%
                                                    double uqv = unitQty;
                                                    if (uqv == Math.floor(uqv)) {
                                                        out.print((long) uqv);
                                                    } else {
                                                        out.print(new java.text.DecimalFormat("#").format(uqv));
                                                    }
                                                    out.print(" " + product.getItemUnitName());
                                                %>
                                            </td>
                                        </tr>
                                        <%
                                            }
                                            java.util.List<java.util.Map<String, Object>> packList = inv != null ? (java.util.List<java.util.Map<String, Object>>) inv.get("PACK_LIST") : null;
                                            if (packList != null && !packList.isEmpty()) {
                                        %>
                                        <tr>
                                            <th>Lốc (nhiều quy cách):</th>
                                            <td>
                                                <%
                                                    for (java.util.Map<String, Object> p : packList) {
                                                        Object qs = p.get("quantity");
                                                        Object ps = p.get("price");
                                                        Object sz = p.get("packSize");
                                                        double qdv = qs instanceof Number ? ((Number) qs).doubleValue() : 0d;
                                                        double pdv = ps instanceof Number ? ((Number) ps).doubleValue() : 0d;
                                                        int s = sz instanceof Number ? ((Number) sz).intValue() : 0;
                                                        if (s > 0) {
                                                            StringBuilder line = new StringBuilder();
                                                            line.append(s).append(" ").append(product.getItemUnitName()).append("/lốc: ");
                                                            if (qdv == Math.floor(qdv)) {
                                                                line.append((long) qdv);
                                                            } else {
                                                                line.append(new java.text.DecimalFormat("#").format(qdv));
                                                            }
                                                            line.append(" lốc — ").append(formatter.format(pdv)).append(" đ");
                                                            out.print(line.toString());
                                                            out.print("<br/>");
                                                        }
                                                    }
                                                %>
                                            </td>
                                        </tr>
                                        <%
                                            }
                                        %>
                                        <tr><th>Số lượng trong 1 thùng:</th><td><%= product.getUnitPerBox()%> <%= product.getItemUnitName()%> / <%= product.getBoxUnitName()%></td></tr>
                                        <tr><th>Đơn vị nhỏ nhất:</th><td><%= product.getItemUnitName()%></td></tr>
                                        <% } %>
                                        <tr><th>Trạng thái:</th>
                                            <td>
                                                <% if (product.getStockQuantity() <= 0) { %>
                                                <span class="badge bg-danger">Hết hàng</span>
                                                <% } else if (product.getStockQuantity() <= 10) { %>
                                                <span class="badge bg-warning">Sắp hết</span>
                                                <% } else { %>
                                                <span class="badge bg-success">Còn hàng</span>
                                                <% } %>
                                            </td>
                                        </tr>
                                        <tr><th>Danh mục:</th><td><%= product.getCategory() != null ? product.getCategory().getCategoryName() : ""%></td></tr>
                                        <tr><th>Nhà sản xuất:</th><td><%= product.getManufacturer() != null ? product.getManufacturer().getCompanyName() : ""%></td></tr>
                                        <tr>
                                            <% if (isFruit) { %>
                                            <th>Ngày nhập kho:</th>
                                            <% } else { %>
                                            <th>Ngày sản xuất:</th>
                                            <% } %>
                                            <td>
                                                <%= product.getManufactureDate() != null ? new java.text.SimpleDateFormat("dd/MM/yyyy").format(product.getManufactureDate()) : ""%>
                                            </td>
                                        </tr>
                                        <tr><th>Hạn sử dụng:</th>
                                            <td>
                                                <%= product.getExpirationDate() != null ? new java.text.SimpleDateFormat("dd/MM/yyyy").format(product.getExpirationDate()) : ""%>
                                            </td>
                                        </tr>
                                        <tr><th>Mô tả:</th>
                                            <td style="white-space: pre-line;"><%= product.getDescription()%></td>
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