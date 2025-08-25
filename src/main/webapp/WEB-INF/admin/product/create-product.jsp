<%@page import="model.Manufacturer"%>
<%@page import="model.Category"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    List<Category> cate = (List<Category>) request.getAttribute("dataCate");
    List<Manufacturer> sup = (List<Manufacturer>) request.getAttribute("dataSup");
%>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Tạo sản phẩm mới</title>
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/css/bootstrap-icons.min.css">
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis()%>">
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis()%>">
    </head>
    <body>
        <div class="container-fluid">
            <%-- Include admin sidebar --%>
            <jsp:include page="../components/sidebar.jsp" />

            <div class="main-content">
                <div class="container">
                    <h1 style="margin-top: 45px;">Tạo sản phẩm mới</h1>
                    <div id="imageError" class="alert alert-danger d-none" role="alert"></div>

                    <% String error = (String) request.getAttribute("error"); %>
                    <% if (error != null) {%>
                    <div class="alert alert-danger"><%= error%></div>
                    <% } %>
                    <form id="createForm" method="post" action="${pageContext.request.contextPath}/admin/product?action=create" enctype="multipart/form-data">
                        <div class="mb-3">
                            <label class="form-label">Tên sản phẩm</label>
                            <input type="text" class="form-control" name="pName" required />
                        </div>
                        
                        <div class="mb-3">
                            <label class="form-label">Thể loại</label>
                            <select name="categoryID" class="form-select" required>
                                <option value="">-- Chọn thể loại --</option>
                                <% if (cate != null) {
                                        for (Category c : cate) {
                                            if (c.getParentID() != 0) {
                                                String parentName = "";
                                                for (Category p : cate) {
                                                    if (c.getParentID() == p.getCategoryID()) {
                                                        parentName = p.getCategoryName() + " > ";
                                                        break;
                                                    }
                                                }
                                                String display = parentName + c.getCategoryName();
                                %>
                                <option value="<%= c.getCategoryID()%>"><%= display%></option>
                                <%
                                            }
                                        }
                                    }
                                %>
                            </select>
                        </div>

                        <!-- Giá và số lượng sẽ được thiết lập riêng biệt -->
                        <div class="mb-3 box-related">
                            <label class="form-label">Số lượng sản phẩm trong 1 thùng</label>
                            <input type="number" min="1" class="form-control" name="unitPerBox" id="unitPerBox" />
                            <div class="form-text text-muted">Ví dụ: 24 lon trong 1 thùng, 12 chai trong 1 thùng...</div>
                        </div>

                        <div class="mb-3 box-related">
                            <label class="form-label">Đơn vị thùng</label>
                            <input type="text" class="form-control" value="thùng" readonly>
                            <input type="hidden" name="boxUnitName" id="boxUnitName" value="thùng">
                        </div>
                        <div class="mb-3" id="item-unit-group">
                            <label class="form-label">Đơn vị nhỏ nhất</label>
                            <select class="form-select" name="itemUnitName" id="itemUnitName" required>
                                <option value="">-- Chọn đơn vị nhỏ nhất --</option>
                                <!-- Đơn vị đóng gói -->
                                <option value="gói">gói</option>
                                <option value="túi">túi</option>
                                <option value="lốc">lốc</option>
                                <option value="cái">cái</option>
                                <!-- Đơn vị nước -->
                                <option value="lon">lon</option>
                                <option value="chai">chai</option>
                                <option value="hộp">hộp</option>
                                <!-- Đơn vị bánh kẹo -->
                                <option value="thanh">thanh</option>
                                <option value="viên">viên</option>
                                <option value="miếng">miếng</option>
                                <!-- Đơn vị mẹ và bé -->
                                <option value="tấm">tấm</option>
                                <option value="bộ">bộ</option>
                                <option value="cặp">cặp</option>
                                <!-- Đơn vị mỹ phẩm -->
                                <option value="tuýp">tuýp</option>
                                <option value="lọ">lọ</option>
                                <option value="bình">bình</option>
                                <option value="thỏi">thỏi</option>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Mô tả sản phẩm</label>
                            <textarea class="form-control" name="pDescription" rows="4" required></textarea>
                        </div>


                        <div class="mb-3">
                            <label class="form-label">Hình ảnh sản phẩm</label>
                            <input type="file" class="form-control" name="pImage" id="pImage" accept=".jpg,.jpeg,.png" required>
                        </div>

                        <!-- Ngày sản xuất và hạn sử dụng sẽ được nhập trong quá trình nhập kho -->


                        
                        <div class="mb-3">
                            <label class="form-label">Nhà sản xuất</label>
                            <select name="manufacturerID" class="form-select" required>
                                <option value="">-- Chọn nhà sản xuất --</option>
                                <% if (sup != null) {
                                        for (Manufacturer s : sup) {
                                            if (s.getStatus() == 1) {
                                %>
                                <option value="<%= s.getManufacturerId()%>"><%= s.getCompanyName()%></option>
                                <%      }
                                    }
                                }
                                %>
                            </select>
                        </div>
                        <a href="${pageContext.request.contextPath}/admin/product" class="btn btn-secondary" id="back"><i class="bi bi-arrow-return-left"></i> Quay Lại</a>
                        <button type="submit" class="btn btn-primary" id="submit"><i class="bi bi-file-earmark-plus"></i> Tạo sản phẩm</button>
                    </form>
                </div>
            </div>
            <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
            <script src="${pageContext.request.contextPath}/assets/js/handelImg.js"></script>
            <script>
                // Form đã được đơn giản hóa - không còn logic phức tạp cho trái cây
            </script>

    </body>
</html>
