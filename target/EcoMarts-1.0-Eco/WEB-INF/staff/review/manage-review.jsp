<%@page import="model.Review"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Quản lý đánh giá</title>
        <link rel="shortcut icon" href="${pageContext.request.contextPath}/assets/img/eco.png" type="image/x-icon">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis()%>">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis()%>">
    </head>
    <style>
        th, td {
            white-space: nowrap;
        }
        .table-container {
            overflow-x: auto;
        }
    </style>
    <body>
        <div class="container-fluid">
            <%-- Sidebar --%>
            <jsp:include page="../components/sidebar.jsp" />

            <div class="main-content">
                <div class="card">
                    <div class="card-header">
                        <div class="header-actions d-flex justify-content-between align-items-center">
                            <h1 class="card-title">Danh sách đánh giá</h1>
                            <form action="<%= request.getContextPath() %>/staff/manage-review" method="get" class="d-flex align-items-center gap-2">
                                <input type="text" name="search" class="form-control form-control-sm" placeholder="Tìm kiếm theo tên sản phẩm..." value="<%= request.getParameter("search") != null ? request.getParameter("search") : "" %>">
                                <button type="submit" class="btn btn-sm btn-primary">Tìm kiếm</button>
                            </form>
                        </div>
                    </div>

                    <%
                        List<Review> reviewList = (List<Review>) request.getAttribute("reviewList");
                    %>

                    <div class="table-container">
                        <% if (reviewList != null && !reviewList.isEmpty()) { %>
                        <table class="table table-striped table-hover text-center align-middle">
                            <thead>
                                <tr>
                                    <!-- <th>Review ID</th> -->
                                    <th>Tên người dùng</th>
                                    <th>Tên sản phẩm</th>
                                    <th>Nội dung đánh giá</th>
                                    <th>Đánh giá sao</th>
                                    <th>Hình ảnh</th>
                                    <th>Ngày đánh giá</th>
                                    <th>Trạng thái</th>
                                    <!--<th>Parent Review ID</th>-->
                                    <th>Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (Review r : reviewList) { %>
                                <tr>
                                    <!-- <td><%= r.getReviewID() %></td> -->
                                    <td>
                                        <% if (r.getAccountRole() == 2) { %>
                                            <%= r.getUserName() %> (Nhân viên)
                                        <% } else { %>
                                            <%= r.getUserName() %>
                                        <% } %>
                                    </td>
                                    <td>
                                        <a href="<%= request.getContextPath() %>/ProductDetail?id=<%= r.getProductID() %>" target="_blank">
                                            <%= r.getProductName() %>
                                        </a>
                                    </td>
                                    <td style="max-width:250px;"><%= r.getComment() %></td>
                                    <td>
                                        <% int fullStars = r.getRating(); %>
                                        <% for (int i = 0; i < fullStars; i++) { %>
                                            <i class="fas fa-star text-warning"></i>
                                        <% } %>
                                        <% for (int i = fullStars; i < 5; i++) { %>
                                            <i class="far fa-star text-warning"></i>
                                        <% } %>
                                    </td>
                                    <td>
                                        <% if (r.getImageURL() != null && !r.getImageURL().isEmpty()) { %>
                                            <% if (r.getImageURL().startsWith("http")) { %>
                                                <img src="<%= r.getImageURL() %>" alt="Review Image" style="width: 80px; height: auto;">
                                            <% } else { %>
                                                <img src="<%= request.getContextPath() %>/ImageServlet_2?name=<%= r.getImageURL() %>" alt="Review Image" style="width: 80px; height: auto;">
                                            <% } %>
                                        <% } else { %>
                                            <span class="text-muted">(Không có)</span>
                                        <% } %>
                                    </td>
                                    <td>
                                        <%
                                            java.sql.Timestamp createdAt = r.getCreatedAt();
                                            if (createdAt != null) {
                                                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
                                                out.print(sdf.format(createdAt));
                                            } else {
                                                out.print("(Không có)");
                                            }
                                        %>
                                    </td>
                                    <td>
                                        <% String status = r.getStatus(); %>
                                        <% if ("VISIBLE".equalsIgnoreCase(status)) { %>
                                            <span class="badge bg-success">Hiển thị</span>
                                        <% } else if ("HIDDEN".equalsIgnoreCase(status)) { %>
                                            <span class="badge bg-secondary">Ẩn</span>
                                        <% } else { %>
                                            <span class="badge bg-warning text-dark">Chờ duyệt</span>
                                        <% } %>
                                    </td>
                                    <!--<td><%= r.getParentReviewID() != null ? r.getParentReviewID() : "(Root)" %></td>-->
                                    <td>
                                        <div class="d-flex gap-2 justify-content-center">
                                            <% if ("VISIBLE".equalsIgnoreCase(status)) { %>
                                            <a href="<%= request.getContextPath() %>/staff/manage-review?action=hide&id=<%= r.getReviewID() %>" class="btn btn-sm btn-warning" title="Ẩn đánh giá">
                                                <i class="fas fa-eye-slash"></i>
                                            </a>
                                            <% } else { %>
                                            <a href="<%= request.getContextPath() %>/staff/manage-review?action=show&id=<%= r.getReviewID() %>" class="btn btn-sm btn-success" title="Hiển thị đánh giá">
                                                <i class="fas fa-eye"></i>
                                            </a>
                                            <% } %>
                                            <a href="#" class="btn btn-sm btn-danger" title="Xóa đánh giá" onclick="confirmDelete(event, <%= r.getReviewID() %>);">
                                                <i class="fas fa-trash"></i>
                                            </a>
                                        </div>
                                    </td>
                                </tr>
                                <% } %>
                            </tbody>
                        </table>
                        <% } else { %>
                        <div class="text-center">
                            <h1 class="text-danger my-4">Không có dữ liệu đánh giá!</h1>
                        </div>
                        <% } %>
                    </div>
                </div>
            </div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
        <script>
            function confirmDelete(event, reviewId) {
                event.preventDefault();
                Swal.fire({
                    title: 'Xác nhận xóa đánh giá',
                    text: 'Bạn có chắc chắn muốn xóa đánh giá này không?',
                    icon: 'warning',
                    showCancelButton: true,
                    confirmButtonColor: '#d33',
                    cancelButtonColor: '#3085d6',
                    confirmButtonText: 'Xóa',
                    cancelButtonText: 'Hủy'
                }).then((result) => {
                    if (result.isConfirmed) {
                        window.location.href = '<%= request.getContextPath() %>/staff/manage-review?action=delete&id=' + reviewId;
                    }
                });
                return false;
            }
        </script>
    </body>
</html>