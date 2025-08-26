<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="model.Product" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Date" %>
<%
    Product product = (Product) request.getAttribute("productDetail");
    Map<String, Object> manufacturerInfo = (Map<String, Object>) request.getAttribute("manufacturerInfo");
    Date expiryDate = (Date) request.getAttribute("expiryDate");
    Double boxQty = (Double) request.getAttribute("boxQty");
    Double unitQty = (Double) request.getAttribute("unitQty");
    Double kgQty = (Double) request.getAttribute("kgQty");
    
    // Format số
    java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols();
    symbols.setGroupingSeparator('.');
    java.text.DecimalFormat formatter = new java.text.DecimalFormat("#,###", symbols);
    
    // Chỉ dựa theo ParentID
    boolean isFruit = false;
    boolean isBeverageOrMilk = false;
    if (product.getCategory() != null) {
        Integer parentId = product.getCategory().getParentID();
        isFruit = (parentId != null && parentId == 3);
        isBeverageOrMilk = (parentId != null && (parentId == 1 || parentId == 2));
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
                            <a href="${pageContext.request.contextPath}/admin/product" class="btn btn-secondary">Quay lại</a>
                        </div>
                        <div class="card-body">
                            <div class="row">
                                <!-- Ảnh bên trái -->
                                <div class="col-md-5 text-center">
                                    <img src="<%= request.getContextPath()%>/ImageServlet?name=<%= product.getImageURL()%>" alt="Ảnh sản phẩm" class="img-fluid rounded shadow" style="max-height:350px;">
                                </div>
                                <!-- Thông tin bên phải -->
                                <div class="col-md-7">
                                    <h3 class="mb-3"><%= product.getProductName()%></h3>
                                    <table class="table table-borderless">
                                        <tr><th>Mã sản phẩm:</th><td><%= product.getProductID()%></td></tr>
                                        
                                        <% if (isFruit) { %>
                                        <!-- Form cho trái cây -->
                                        <tr>
                                            <th>Giá bán:</th>
                                            <td>
                                                <% if (product.getPriceUnit() != null) { %>
                                                    <%= formatter.format(product.getPriceUnit())%> đ / kg
                                                <% } else { %>
                                                    - đ
                                                <% } %>
                                            </td>
                                        </tr>
                                        <tr>
                                            <th>Số lượng:</th>
                                            <td>
                                                <% if (kgQty != null && kgQty > 0) { %>
                                                    <% if (kgQty == Math.floor(kgQty)) { %>
                                                        <%= ((Double)kgQty).longValue() %> kg
                                                    <% } else { %>
                                                        <%= new java.text.DecimalFormat("#,##0.0").format(kgQty) %> kg
                                                    <% } %>
                                                <% } else { %>
                                                    - kg
                                                <% } %>
                                            </td>
                                        </tr>
                                        <% } else { %>
                                        <!-- Form cho sản phẩm khác -->
                                        <tr>
                                            <th>Giá 1 thùng:</th>
                                            <td>
                                                <% if (product.getPrice() != null) { %>
                                                    <%= formatter.format(product.getPrice())%> đ
                                                <% } else { %>
                                                    - đ
                                                <% } %>
                                            </td>
                                        </tr>
                                        <tr>
                                            <th>Giá 1 <%= product.getItemUnitName()%>:</th>
                                            <td>
                                                <% if (product.getPriceUnit() != null) { %>
                                                    <%= formatter.format(product.getPriceUnit())%> đ
                                                <% } else { %>
                                                    - đ
                                                <% } %>
                                            </td>
                                        </tr>
                                        <tr>
                                            <th>Giá 1 lốc:</th>
                                            <td>
                                                <% if (isBeverageOrMilk) { %>
                                                    <% if (product.getPricePack() != null) { %>
                                                        <%= formatter.format(product.getPricePack())%> đ
                                                    <% } else { %>
                                                        - đ
                                                    <% } %>
                                                <% } else { %>
                                                    không được thiết lập
                                                <% } %>
                                            </td>
                                        </tr>
                                        <tr>
                                            <th>Số lượng thùng:</th>
                                            <td>
                                                <% if (boxQty != null && boxQty > 0) { %>
                                                    <% if (boxQty == Math.floor(boxQty)) { %>
                                                        <%= ((Double)boxQty).longValue() %>
                                                    <% } else { %>
                                                        <%= new java.text.DecimalFormat("#").format(boxQty) %>
                                                    <% } %>
                                                    <%= product.getBoxUnitName() %>
                                                <% } else { %>
                                                    - <%= product.getBoxUnitName() %>
                                                <% } %>
                                            </td>
                                        </tr>
                                        <tr>
                                            <th>Số lượng <%= product.getItemUnitName() %>:</th>
                                            <td>
                                                <% if (unitQty != null && unitQty > 0) { %>
                                                    <% if (unitQty == Math.floor(unitQty)) { %>
                                                        <%= ((Double)unitQty).longValue() %>
                                                    <% } else { %>
                                                        <%= new java.text.DecimalFormat("#").format(unitQty) %>
                                                    <% } %>
                                                    <%= product.getItemUnitName() %>
                                                <% } else { %>
                                                    - <%= product.getItemUnitName() %>
                                                <% } %>
                                            </td>
                                        </tr>
                                        <tr>
                                            <th>Số lượng lốc:</th>
                                            <td>
                                                <%
                                                    java.util.Map<String, Object> invMap = (java.util.Map<String, Object>) request.getAttribute("inventory");
                                                    java.util.List<java.util.Map<String, Object>> packList = invMap != null ? (java.util.List<java.util.Map<String, Object>>) invMap.get("PACK_LIST") : null;
                                                    if (packList == null || packList.isEmpty()) {
                                                        if (isBeverageOrMilk) {
                                                %>
                                                    - lốc
                                                <%
                                                        } else {
                                                %>
                                                    không được thiết lập
                                                <%
                                                        }
                                                    } else {
                                                        int showLimit = 2;
                                                        for (int i = 0; i < packList.size(); i++) {
                                                            java.util.Map<String, Object> p = packList.get(i);
                                                            Object qs = p.get("quantity");
                                                            Object sz = p.get("packSize");
                                                            double qdv = qs instanceof Number ? ((Number) qs).doubleValue() : 0d;
                                                            int s = sz instanceof Number ? ((Number) sz).intValue() : 0;
                                                            String line;
                                                            if (qdv == Math.floor(qdv)) {
                                                                line = ((long) qdv) + " lốc / " + s + " " + product.getItemUnitName();
                                                            } else {
                                                                line = new java.text.DecimalFormat("#").format(qdv) + " lốc / " + s + " " + product.getItemUnitName();
                                                            }
                                                            if (i < showLimit) {
                                                %>
                                                                <div><%= line %></div>
                                                <%
                                                            } else if (i == showLimit) {
                                                %>
                                                                <div id="more-packs" style="display:none;">
                                                                    <div><%= line %></div>
                                                <%
                                                            } else if (i > showLimit) {
                                                %>
                                                                    <div><%= line %></div>
                                                <%
                                                            }
                                                        }
                                                        if (packList.size() > showLimit) {
                                                %>
                                                                </div>
                                                                <button type="button" class="btn btn-link p-0" onclick="
                                                                    (function(btn){
                                                                        var more = document.getElementById('more-packs');
                                                                        if(more.style.display==='none'){ more.style.display='block'; btn.innerText='Thu gọn'; }
                                                                        else { more.style.display='none'; btn.innerText='Xem thêm'; }
                                                                    })(this)
                                                                ">Xem thêm</button>
                                                <%
                                                        }
                                                    }
                                                %>
                                            </td>
                                        </tr>
                                        <% } %>
                                        
                                        <tr><th>Đơn vị nhỏ nhất:</th><td><%= product.getItemUnitName()%></td></tr>
                                        <tr>
                                            <th>Trạng thái:</th>
                                            <td>
                                                <% if (product.getStockQuantity() <= 0) { %>
                                                <span class="badge bg-danger">Hết hàng</span>
                                                <% } else if (product.getStockQuantity() <= 10) { %>
                                                <span class="badge bg-warning">Sắp hết</span>
                                                <% } else { %>
                                                <span class="badge bg-success">Còn hàng</span>
                                                <% }%>
                                            </td>
                                        </tr>
                                        <tr><th>Danh mục:</th><td><%= product.getCategory() != null ? product.getCategory().getCategoryName() : ""%></td></tr>
                                        <tr>
                                            <th>Nhà sản xuất:</th>
                                            <td>
                                                <% if (manufacturerInfo != null && manufacturerInfo.get("companyName") != null) { %>
                                                    <%= manufacturerInfo.get("companyName") %>
                                                <% } else { %>
                                                    -
                                                <% } %>
                                            </td>
                                        </tr>
                                        <tr>
                                            <th>Ngày nhập kho:</th>
                                            <td>
                                                <% if (manufacturerInfo != null && manufacturerInfo.get("dateIn") != null) { %>
                                                    <%= new java.text.SimpleDateFormat("dd/MM/yyyy").format(manufacturerInfo.get("dateIn")) %>
                                                <% } else { %>
                                                    -
                                                <% } %>
                                            </td>
                                        </tr>
                                        <tr>
                                            <th>Hạn sử dụng:</th>
                                            <td>
                                                <% if (expiryDate != null) { %>
                                                    <%= new java.text.SimpleDateFormat("dd/MM/yyyy").format(expiryDate) %>
                                                <% } else { %>
                                                    -
                                                <% } %>
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