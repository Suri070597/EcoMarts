<%@page import="model.Review"%>
<%@page import="model.Account"%>
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
            white-space: normal;
            word-wrap: break-word;
            overflow-wrap: anywhere;
        }

        /* CSS đặc biệt cho cột nội dung đánh giá */
        .review-content {
            white-space: normal !important;
            word-wrap: break-word;
            max-width: 250px;
            min-width: 200px;
            text-align: left;
            padding: 8px 12px;
            vertical-align: top;
        }

        /* CSS cho nút xem thêm */
        .review-text-container, .reply-text-container {
            position: relative;
        }

        .review-text-short, .reply-text-short {
            display: block;
            white-space: normal;
            word-wrap: break-word;
            overflow-wrap: break-word;
        }

        .review-text-full, .reply-text-full {
            display: none;
            white-space: normal;
            word-wrap: break-word;
            overflow-wrap: break-word;
            max-width: 100%;
        }

        .btn-link {
            color: #8b6c42;
            text-decoration: none;
            font-size: 0.875rem;
            padding: 0;
            margin: 0;
        }

        .btn-link:hover {
            color: #6a5232;
            text-decoration: underline;
        }

        /* CSS cho cột ngày đánh giá để tránh bị che */
        .review-date {
            white-space: nowrap;
            min-width: 120px;
        }

        /* CSS cho cột trạng thái */
        .review-status {
            white-space: nowrap;
            min-width: 100px;
        }

        /* CSS cho cột thao tác */
        .review-actions {
            white-space: normal;
            min-width: 100px;
        }
        .review-table {
            width: 100%;
            table-layout: fixed;
        }
        .review-table thead th {
            white-space: nowrap;
        }
        .review-table th:nth-child(1) {
            white-space: nowrap;
        }
        .review-table td:nth-child(1) {
            white-space: nowrap;
        }

        .table-container {
            overflow-x: auto;
        }

        /* CSS cho link tên sản phẩm - đồng bộ với theme beige */
        .product-link {
            color: #8b6c42;
            text-decoration: none;
            font-weight: 500;
            transition: all 0.3s ease;
            padding: 6px 12px;
            border-radius: 6px;
            background: linear-gradient(135deg, #f5f2ea 0%, #e8d7bc 100%);
            border: 1px solid #d4b78f;
            display: inline-block;
            min-width: 120px;
            box-shadow: 0 1px 3px rgba(139, 108, 66, 0.1);
        }

        .product-link:hover {
            color: #6a5232;
            text-decoration: none;
            background: linear-gradient(135deg, #e8d7bc 0%, #d4b78f 100%);
            border-color: #b89c70;
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(139, 108, 66, 0.2);
        }

        .product-link:active {
            transform: translateY(0);
            box-shadow: 0 2px 6px rgba(139, 108, 66, 0.15);
        }

        /* Hiệu ứng focus cho accessibility */
        .product-link:focus {
            outline: 2px solid #8b6c42;
            outline-offset: 2px;
        }

        /* Responsive cho mobile */
        @media (max-width: 768px) {
            .product-link {
                font-size: 0.9em;
                padding: 3px 6px;
                min-width: 100px;
            }

            .review-content {
                max-width: 150px;
                min-width: 120px;
            }
        }
        /* Modal reply styling */
        .reply-modal .modal-header {
            background: linear-gradient(135deg, #8b6c42 0%, #6a5232 100%);
            color: #fff;
        }
        .reply-cell {
            padding-left: 0 !important;
            padding-right: 0 !important;
        }
        .reply-thread {
            position: relative;
            margin-top: 8px;
            background: transparent;
            border: 0;
            border-radius: 0;
            padding: 0;
        }
        /* Căn trái đều; vẽ đường dọc cho TỪNG reply để luôn đồng bộ */
        .reply-item {
            position: relative;
            margin: 8px 0;
        }
        .reply-item:before {
            content: "";
            position: absolute;
            left: -16px;
            top: 0;
            bottom: 0;
            border-left: 2px dashed #e0cba4;
        }
        .bubble {
            background: #fffdf7;
            border: 1px solid #e8d7bc;
            border-radius: 8px;
            padding: 10px 12px;
            box-shadow: 0 1px 3px rgba(139,108,66,0.06);
            width: 100%;
            box-sizing: border-box;
        }
        .bubble:before {
            content: "";
            position: absolute;
            left: -8px;
            top: 12px;
            width:0;
            height:0;
            border-top:8px solid transparent;
            border-bottom:8px solid transparent;
            border-right:8px solid #e8d7bc;
        }
        .bubble:after {
            content: "";
            position: absolute;
            left: -7px;
            top: 12px;
            width:0;
            height:0;
            border-top:8px solid transparent;
            border-bottom:8px solid transparent;
            border-right:8px solid #fffdf7;
        }
        .bubble-staff {
            background:#f7fbff;
            border-color:#c8def2;
        }
        .bubble-staff:after {
            border-right-color:#f7fbff;
        }
        .badge-staff {
            background:#0d6efd;
            color:#fff;
            font-size:.75rem;
        }
        .badge-customer {
            background:#8b6c42;
            color:#fff;
            font-size:.75rem;
        }
        .reply-toggle {
            cursor: pointer;
            color: #8b6c42;
        }
        .reply-toggle:hover {
            color: #6a5232;
        }
        .reply-content {
            display: none;
        }
        .reply-content.show {
            display: block;
        }
        .nested-reply {
            margin-left: 20px;
            border-left: 2px solid #d4b78f;
            padding-left: 10px;
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
                            <form action="<%= request.getContextPath()%>/staff/manage-review" method="get" class="d-flex align-items-center gap-2">
                                <input type="text" name="search" class="form-control form-control-sm" placeholder="Tìm kiếm theo tên sản phẩm..." value="<%= request.getParameter("search") != null ? request.getParameter("search") : ""%>">
                                <button type="submit" class="btn btn-sm btn-primary">Tìm kiếm</button>
                            </form>
                        </div>
                    </div>

                    <%
                        List<Review> reviewList = (List<Review>) request.getAttribute("reviewList");
                        java.util.Map<Integer, java.util.List<Review>> flatRepliesMap = (java.util.Map<Integer, java.util.List<Review>>) request.getAttribute("flatRepliesMap");
                    %>

                    <% String flash = (String) session.getAttribute("message"); %>
                    <% if (flash != null) {%>
                    <div class="alert alert-info alert-dismissible fade show m-3" role="alert">
                        <%= flash%>
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                    <% session.removeAttribute("message"); %>
                    <% } %>
                    <%
                        Account staffAcc = (Account) session.getAttribute("account");
                        int currentStaffId = (staffAcc != null ? staffAcc.getAccountID() : -1);
                    %>

                    <div class="table-container">
                        <% if (reviewList != null && !reviewList.isEmpty()) { %>
                        <table class="table table-striped table-hover text-center align-middle review-table">
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
                                <% for (Review r : reviewList) {%>
                                <tr>
                                    <!-- <td><%= r.getReviewID()%></td> -->
                                    <td>
                                        <div class="d-flex align-items-center gap-2 justify-content-center">
                                            <%= r.getUserName()%>
                                            <% if (r.getAccountRole() == 2) { %>
                                            <span class="badge badge-staff">Nhân viên</span>
                                            <% }%>
                                        </div>
                                    </td>
                                    <td>
                                        <%= r.getProductName()%>
                                    </td>
                                    <td class="review-content">
                                        <div class="review-text-container">
                                            <div class="review-text-short" id="short-<%= r.getReviewID()%>">
                                                <%= r.getComment().length() > 100 ? r.getComment().substring(0, 100) + "..." : r.getComment()%>
                                            </div>
                                            <div class="review-text-full" id="full-<%= r.getReviewID()%>" style="display: none;">
                                                <%= r.getComment()%>
                                            </div>
                                            <% if (r.getComment().length() > 100) {%>
                                            <button type="button" class="btn btn-link btn-sm p-0 mt-1" onclick="toggleReviewText(<%= r.getReviewID()%>)">
                                                <span id="toggle-text-<%= r.getReviewID()%>">Xem thêm</span>
                                            </button>
                                            <% } %>
                                        </div>
                                    </td>
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
                                        <% if (r.getImageURL().startsWith("http")) {%>
                                        <img src="<%= r.getImageURL()%>" alt="Review Image" style="width: 80px; height: auto;">
                                        <% } else {%>
                                        <img src="<%= request.getContextPath()%>/ImageServlet_2?name=<%= r.getImageURL()%>" alt="Review Image" style="width: 80px; height: auto;">
                                        <% } %>
                                        <% } else { %>
                                        <span class="text-muted">(Không có)</span>
                                        <% } %>
                                    </td>
                                    <td class="review-date">
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
                                    <td class="review-status">
                                        <% String status = r.getStatus(); %>
                                        <% if ("VISIBLE".equalsIgnoreCase(status)) { %>
                                        <span class="badge bg-success">Hiển thị</span>
                                        <% } else if ("HIDDEN".equalsIgnoreCase(status)) { %>
                                        <span class="badge bg-secondary">Ẩn</span>
                                        <% } else { %>
                                        <span class="badge bg-warning text-dark">Chờ duyệt</span>
                                        <% }%>
                                    </td>
                                    <!--<td><%= r.getParentReviewID() != null ? r.getParentReviewID() : "(Root)"%></td>-->
                                    <td class="review-actions">
                                        <div class="d-flex gap-2 justify-content-center">
                                            <% if ("VISIBLE".equalsIgnoreCase(status)) {%>
                                            <a href="<%= request.getContextPath()%>/staff/manage-review?action=hide&id=<%= r.getReviewID()%>" class="btn btn-sm btn-warning" title="Ẩn đánh giá">
                                                <i class="fas fa-eye-slash"></i>
                                            </a>
                                            <% } else {%>
                                            <a href="<%= request.getContextPath()%>/staff/manage-review?action=show&id=<%= r.getReviewID()%>" class="btn btn-sm btn-success" title="Hiển thị đánh giá">
                                                <i class="fas fa-eye"></i>
                                            </a>
                                            <% } %>
                                            <% if (r.getAccountRole() != 2) {%>
                                            <button type="button" class="btn btn-sm btn-info" title="Trả lời"
                                                    data-review-id="<%= r.getReviewID()%>"
                                                    data-product-id="<%= r.getProductID()%>"
                                                    data-customer-name="<%= (r.getUserName() != null ? r.getUserName().replace("\"", "&quot;") : "")%>"
                                                    data-product-name="<%= (r.getProductName() != null ? r.getProductName().replace("\"", "&quot;") : "")%>"
                                                    data-original-comment="<%= (r.getComment() != null ? r.getComment().replace("\"", "&quot;") : "")%>"
                                                    onclick="openReplyModalFrom(this)">
                                                <i class="fas fa-reply"></i>
                                            </button>
                                            <% }%>
                                            <a href="#" class="btn btn-sm btn-danger" title="Xóa đánh giá" onclick="confirmDelete(event, <%= r.getReviewID()%>);">
                                                <i class="fas fa-trash"></i>
                                            </a>
                                        </div>
                                        <% if (flatRepliesMap != null && flatRepliesMap.get(r.getReviewID()) != null && !flatRepliesMap.get(r.getReviewID()).isEmpty()) {%>
                                        <div class="mt-2 text-center">
                                            <button type="button" class="btn btn-sm btn-outline-secondary" id="toggle-btn-<%= r.getReviewID()%>"
                                                    data-target-row="replies-row-<%= r.getReviewID()%>"
                                                    onclick="toggleReplyRow(this)">
                                                <i class="fas fa-chevron-down"></i> Hiển thị thêm bình luận
                                            </button>
                                        </div>
                                        <% } %>
                                    </td>
                                </tr>
                                <% if (flatRepliesMap != null && flatRepliesMap.get(r.getReviewID()) != null && !flatRepliesMap.get(r.getReviewID()).isEmpty()) {%>
                                <tr id="replies-row-<%= r.getReviewID()%>" style="display: none;">
                                    <td colspan="8" class="reply-cell">
                                        <div class="me-0">
                                            <div class="d-flex justify-content-between align-items-center mb-2">
                                                <h6 class="text-muted mb-0"><i class="fas fa-reply"></i> Phản hồi</h6>
                                            </div>
                                            <div id="replies-<%= r.getReviewID()%>" class="reply-content reply-thread show" style="margin-left: 0; margin-right: 0;">
                                                <% for (Review rep : flatRepliesMap.get(r.getReviewID())) {%>
                                                <div class="reply-item" style="margin-left: <%= (rep.getDepth() > 0 ? rep.getDepth() * 24 : 0)%>px; margin-right: 0;">
                                                    <div class="bubble <%= (rep.getAccountRole() == 2 ? "bubble-staff" : "")%>" style="position: relative; z-index: 2; margin-left: 0; display: block; width: 100%;">
                                                        <div class="d-flex justify-content-between align-items-center">
                                                            <div class="d-flex align-items-center gap-2">
                                                                <strong><%= rep.getUserName()%></strong>
                                                                <% if (rep.getAccountRole() == 2) { %>
                                                                <span class="badge badge-staff">Nhân viên</span>
                                                                <% } %>
                                                                <small class="text-muted">
                                                                    <%
                                                                        if (rep.getCreatedAt() != null) {
                                                                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
                                                                            out.print(sdf.format(rep.getCreatedAt()));
                                                                        }
                                                                    %>
                                                                </small>
                                                            </div>
                                                            <div class="d-flex gap-1">
                                                                <% if (rep.getAccountRole() != 2) {%>
                                                                <button type="button" class="btn btn-sm btn-outline-info" title="Trả lời"
                                                                        data-review-id="<%= rep.getReviewID()%>"
                                                                        data-product-id="<%= r.getProductID()%>"
                                                                        data-customer-name="<%= (rep.getUserName() != null ? rep.getUserName().replace("\"", "&quot;") : "")%>"
                                                                        data-product-name="<%= (r.getProductName() != null ? r.getProductName().replace("\"", "&quot;") : "")%>"
                                                                        data-original-comment="<%= (rep.getComment() != null ? rep.getComment().replace("\"", "&quot;") : "")%>"
                                                                        onclick="openReplyModalFrom(this)">
                                                                    <i class="fas fa-reply"></i>
                                                                </button>
                                                                <% } %>
                                                                <% if (rep.getAccountRole() == 2 && rep.getAccountID() == currentStaffId) {%>
                                                                <button type="button" class="btn btn-sm btn-outline-primary" title="Chỉnh sửa"
                                                                        data-review-id="<%= rep.getReviewID()%>"
                                                                        data-product-id="<%= r.getProductID()%>"
                                                                        data-current-comment="<%= (rep.getComment() != null ? rep.getComment().replace("\"", "&quot;") : "")%>"
                                                                        onclick="openEditModalFrom(this)">
                                                                    <i class="fas fa-edit"></i>
                                                                </button>
                                                                <% }%>
                                                                <button type="button" class="btn btn-sm btn-outline-danger" title="Xóa phản hồi" onclick="confirmDelete(event, <%= rep.getReviewID()%>);">
                                                                    <i class="fas fa-trash"></i>
                                                                </button>
                                                            </div>
                                                        </div>
                                                        <div class="mt-2">
                                                            <div class="reply-text-container">
                                                                <div class="reply-text-short" id="short-reply-<%= rep.getReviewID()%>">
                                                                    <%= rep.getComment().length() > 80 ? rep.getComment().substring(0, 80) + "..." : rep.getComment()%>
                                                                </div>
                                                                <div class="reply-text-full" id="full-reply-<%= rep.getReviewID()%>" style="display: none;">
                                                                    <%= rep.getComment()%>
                                                                </div>
                                                                <% if (rep.getComment().length() > 80) {%>
                                                                <button type="button" class="btn btn-link btn-sm p-0 mt-1" onclick="toggleReplyText(<%= rep.getReviewID()%>)">
                                                                    <span id="toggle-reply-text-<%= rep.getReviewID()%>">Xem thêm</span>
                                                                </button>
                                                                <% } %>
                                                            </div>
                                                        </div>
                                                        <% if (rep.getImageURL() != null && !rep.getImageURL().isEmpty()) { %>
                                                        <div class="mt-2">
                                                            <% if (rep.getImageURL().startsWith("http")) {%>
                                                            <img src="<%= rep.getImageURL()%>" alt="Reply Image" style="max-width:200px;border-radius:8px;border:1px solid #ddd;">
                                                            <% } else {%>
                                                            <img src="<%= request.getContextPath()%>/ImageServlet_2?name=<%= rep.getImageURL()%>" alt="Reply Image" style="max-width:200px;border-radius:8px;border:1px solid #ddd;">
                                                            <% } %>
                                                        </div>
                                                        <% } %>
                                                    </div>
                                                </div>

                                                <%-- Bỏ render lồng nhau; đã dùng danh sách phẳng --%>
                                                <% if (false && rep.getReplies() != null && !rep.getReplies().isEmpty()) {%>
                                                <div class="nested-reply mt-2">
                                                    <div class="d-flex justify-content-between align-items-center mb-2">
                                                        <small class="text-muted">
                                                            <i class="fas fa-reply"></i> Phản hồi (<%= rep.getReplies().size()%>)
                                                        </small>
                                                        <button class="btn btn-sm btn-outline-secondary reply-toggle" onclick="toggleNestedReplies(this)">
                                                            <i class="fas fa-chevron-down"></i> Hiển thị
                                                        </button>
                                                    </div>
                                                    <div class="reply-content">
                                                        <% for (Review rep2 : rep.getReplies()) {%>
                                                        <div class="reply-item">
                                                            <div class="d-flex justify-content-between">
                                                                <div>
                                                                    <strong><%= rep2.getUserName()%></strong>
                                                                    <% if (rep2.getAccountRole() == 2) { %>
                                                                    <span class="badge bg-primary">Nhân viên</span>
                                                                    <% } %>
                                                                </div>
                                                                <div class="d-flex align-items-center gap-2">
                                                                    <small class="text-muted">
                                                                        <%
                                                                            if (rep2.getCreatedAt() != null) {
                                                                                java.text.SimpleDateFormat sdf2 = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
                                                                                out.print(sdf2.format(rep2.getCreatedAt()));
                                                                            }
                                                                        %>
                                                                    </small>
                                                                    <% if (rep2.getAccountRole() != 2) {%>
                                                                    <button type="button" class="btn btn-sm btn-outline-info" title="Trả lời" onclick="openReplyModal(<%= rep2.getReviewID()%>, <%= r.getProductID()%>, '<%= (rep2.getUserName() != null ? rep2.getUserName().replace("'", "\\'") : "")%>', '<%= (r.getProductName() != null ? r.getProductName().replace("'", "\\'") : "")%>', '<%= (rep2.getComment() != null ? rep2.getComment().replace("'", "\\'").replace("\"", "\\\"") : "")%>')">
                                                                        <i class="fas fa-reply"></i>
                                                                    </button>
                                                                    <% }%>
                                                                </div>
                                                            </div>
                                                            <div class="mt-2"><%= rep2.getComment()%></div>
                                                            <% if (rep2.getImageURL() != null && !rep2.getImageURL().isEmpty()) { %>
                                                            <div class="mt-2">
                                                                <% if (rep2.getImageURL().startsWith("http")) {%>
                                                                <img src="<%= rep2.getImageURL()%>" alt="Reply Image" style="max-width:200px;border-radius:8px;border:1px solid #ddd;">
                                                                <% } else {%>
                                                                <img src="<%= request.getContextPath()%>/ImageServlet_2?name=<%= rep2.getImageURL()%>" alt="Reply Image" style="max-width:200px;border-radius:8px;border:1px solid #ddd;">
                                                                <% } %>
                                                            </div>
                                                            <% } %>
                                                            <% if (rep2.getReplies() != null && !rep2.getReplies().isEmpty()) {%>
                                                            <div class="nested-reply mt-2">
                                                                <div class="d-flex justify-content-between align-items-center mb-2">
                                                                    <small class="text-muted">
                                                                        <i class="fas fa-reply"></i> Phản hồi (<%= rep2.getReplies().size()%>)
                                                                    </small>
                                                                    <button class="btn btn-sm btn-outline-secondary reply-toggle" onclick="toggleNestedReplies(this)">
                                                                        <i class="fas fa-chevron-down"></i> Hiển thị
                                                                    </button>
                                                                </div>
                                                                <div class="reply-content">
                                                                    <% for (Review rep3 : rep2.getReplies()) {%>
                                                                    <div class="reply-item">
                                                                        <div class="d-flex justify-content-between">
                                                                            <div>
                                                                                <strong><%= rep3.getUserName()%></strong>
                                                                                <% if (rep3.getAccountRole() == 2) { %>
                                                                                <span class="badge bg-primary">Nhân viên</span>
                                                                                <% } %>
                                                                            </div>
                                                                            <div class="d-flex align-items-center gap-2">
                                                                                <small class="text-muted">
                                                                                    <%
                                                                                        if (rep3.getCreatedAt() != null) {
                                                                                            java.text.SimpleDateFormat sdf3 = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
                                                                                            out.print(sdf3.format(rep3.getCreatedAt()));
                                                                                        }
                                                                                    %>
                                                                                </small>
                                                                                <% if (rep3.getAccountRole() != 2) {%>
                                                                                <button type="button" class="btn btn-sm btn-outline-info" title="Trả lời" onclick="openReplyModal(<%= rep3.getReviewID()%>, <%= r.getProductID()%>, '<%= (rep3.getUserName() != null ? rep3.getUserName().replace("'", "\\'") : "")%>', '<%= (r.getProductName() != null ? r.getProductName().replace("'", "\\'") : "")%>', '<%= (rep3.getComment() != null ? rep3.getComment().replace("'", "\\'").replace("\"", "\\\"") : "")%>')">
                                                                                    <i class="fas fa-reply"></i>
                                                                                </button>
                                                                                <% }%>
                                                                            </div>
                                                                        </div>
                                                                        <div class="mt-2"><%= rep3.getComment()%></div>
                                                                        <% if (rep3.getImageURL() != null && !rep3.getImageURL().isEmpty()) { %>
                                                                        <div class="mt-2">
                                                                            <% if (rep3.getImageURL().startsWith("http")) {%>
                                                                            <img src="<%= rep3.getImageURL()%>" alt="Reply Image" style="max-width:200px;border-radius:8px;border:1px solid #ddd;">
                                                                            <% } else {%>
                                                                            <img src="<%= request.getContextPath()%>/ImageServlet_2?name=<%= rep3.getImageURL()%>" alt="Reply Image" style="max-width:200px;border-radius:8px;border:1px solid #ddd;">
                                                                            <% } %>
                                                                        </div>
                                                                        <% } %>
                                                                    </div>
                                                                    <% } %>
                                                                </div>
                                                            </div>
                                                            <% } %>
                                                        </div>
                                                        <% } %>
                                                    </div>
                                                </div>
                                                <% } %>
                                            </div>
                                            <% } %>
                                        </div>
                                        </div>
                                    </td>
                                </tr>
                                <% } %>
                                <% } %>
                            </tbody>
                        </table>
                        <% } else { %>
                        <div class="text-center">
                            <h1 class="text-danger my-4">Không có dữ liệu đánh giá!</h1>
                        </div>
                        <% }%>
                    </div>
                </div>
            </div>
        </div>
        <!-- Modal Reply -->
        <div class="modal fade reply-modal" id="replyModal" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title"><i class="fas fa-reply"></i> Phản hồi đánh giá</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <form action="<%= request.getContextPath()%>/staff/reply-review" method="post" enctype="multipart/form-data">
                        <div class="modal-body">
                            <input type="hidden" id="reviewId" name="reviewId">
                            <input type="hidden" id="productId" name="productId">
                            <div class="mb-2">
                                <label class="form-label">Khách hàng</label>
                                <input type="text" id="customerName" class="form-control" readonly>
                            </div>
                            <div class="mb-2">
                                <label class="form-label">Sản phẩm</label>
                                <input type="text" id="productName" class="form-control" readonly>
                            </div>
                            <div class="mb-2">
                                <label class="form-label">Đánh giá cần trả lời</label>
                                <textarea id="originalComment" class="form-control" rows="3" readonly></textarea>
                            </div>
                            <div class="mb-2">
                                <label class="form-label">Nội dung phản hồi <span class="text-danger">*</span></label>
                                <textarea name="comment" id="replyComment" class="form-control" rows="4" required></textarea>
                            </div>
                            <div class="mb-2">
                                <label class="form-label">Ảnh (tùy chọn)</label>
                                <input type="file" name="image" id="replyImage" class="form-control" accept="image/*">
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                            <button type="submit" class="btn btn-primary">Gửi phản hồi</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <!-- Modal Edit -->
        <div class="modal fade reply-modal" id="editModal" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title"><i class="fas fa-edit"></i> Chỉnh sửa bình luận của bạn</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <form action="<%= request.getContextPath()%>/Review" method="post" enctype="multipart/form-data">
                        <div class="modal-body">
                            <input type="hidden" name="action" value="edit">
                            <input type="hidden" name="reviewId" id="editReviewId">
                            <input type="hidden" name="productId" id="editProductId">
                            <input type="hidden" name="from" value="staff">
                            <div class="mb-2">
                                <label class="form-label">Nội dung <span class="text-danger">*</span></label>
                                <textarea name="comment" id="editComment" class="form-control" rows="5" required></textarea>
                            </div>
                            <div class="mb-2">
                                <label class="form-label">Ảnh (tùy chọn)</label>
                                <input type="file" name="image" class="form-control" accept="image/*">
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                            <button type="submit" class="btn btn-primary">Cập nhật</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
        <script>
                                                                                    function openReplyModalFrom(btn) {
                                                                                        try {
                                                                                            const reviewId = btn.getAttribute('data-review-id');
                                                                                            const productId = btn.getAttribute('data-product-id');
                                                                                            const customerName = btn.getAttribute('data-customer-name') || '';
                                                                                            const productName = btn.getAttribute('data-product-name') || '';
                                                                                            const originalComment = btn.getAttribute('data-original-comment') || '';
                                                                                            openReplyModal(reviewId, productId, customerName, productName, originalComment);
                                                                                        } catch (e) {
                                                                                            console.error('openReplyModalFrom error', e);
                                                                                        }
                                                                                    }
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
                                                                                                window.location.href = '<%= request.getContextPath()%>/staff/manage-review?action=delete&id=' + reviewId;
                                                                                            }
                                                                                        });
                                                                                        return false;
                                                                                    }

                                                                                    function openReplyModal(reviewId, productId, customerName, productName, originalComment) {
                                                                                        document.getElementById('reviewId').value = reviewId;
                                                                                        document.getElementById('productId').value = productId;
                                                                                        document.getElementById('customerName').value = customerName || '';
                                                                                        document.getElementById('productName').value = productName || '';
                                                                                        document.getElementById('originalComment').value = originalComment || '';
                                                                                        document.getElementById('replyComment').value = '';
                                                                                        document.getElementById('replyImage').value = '';
                                                                                        var modal = new bootstrap.Modal(document.getElementById('replyModal'));
                                                                                        modal.show();
                                                                                    }

                                                                                    function openEditModalFrom(btn) {
                                                                                        const reviewId = btn.getAttribute('data-review-id');
                                                                                        const productId = btn.getAttribute('data-product-id');
                                                                                        const currentComment = btn.getAttribute('data-current-comment') || '';
                                                                                        document.getElementById('editReviewId').value = reviewId;
                                                                                        document.getElementById('editProductId').value = productId;
                                                                                        document.getElementById('editComment').value = currentComment;
                                                                                        var modal = new bootstrap.Modal(document.getElementById('editModal'));
                                                                                        modal.show();
                                                                                    }

                                                                                    function toggleReviewText(reviewId) {
                                                                                        const shortText = document.getElementById('short-' + reviewId);
                                                                                        const fullText = document.getElementById('full-' + reviewId);
                                                                                        const toggleBtn = document.getElementById('toggle-text-' + reviewId);

                                                                                        if (shortText.style.display !== 'none') {
                                                                                            shortText.style.display = 'none';
                                                                                            fullText.style.display = 'block';
                                                                                            toggleBtn.textContent = 'Thu gọn';
                                                                                        } else {
                                                                                            shortText.style.display = 'block';
                                                                                            fullText.style.display = 'none';
                                                                                            toggleBtn.textContent = 'Xem thêm';
                                                                                        }
                                                                                    }

                                                                                    function toggleReplyText(replyId) {
                                                                                        const shortText = document.getElementById('short-reply-' + replyId);
                                                                                        const fullText = document.getElementById('full-reply-' + replyId);
                                                                                        const toggleBtn = document.getElementById('toggle-reply-text-' + replyId);

                                                                                        if (shortText.style.display !== 'none') {
                                                                                            shortText.style.display = 'none';
                                                                                            fullText.style.display = 'block';
                                                                                            toggleBtn.textContent = 'Thu gọn';
                                                                                        } else {
                                                                                            shortText.style.display = 'block';
                                                                                            fullText.style.display = 'none';
                                                                                            toggleBtn.textContent = 'Xem thêm';
                                                                                        }
                                                                                    }

                                                                                    function toggleReplies(button) {
                                                                                        const targetId = button.getAttribute('data-target');
                                                                                        const content = document.getElementById(targetId);
                                                                                        const icon = button.querySelector('i');
                                                                                        const count = button.getAttribute('data-count') || '';
                                                                                        if (content.classList.contains('show')) {
                                                                                            content.classList.remove('show');
                                                                                            icon.className = 'fas fa-chevron-down';
                                                                                            button.innerHTML = `<i class="fas fa-chevron-down"></i> Hiển thị thêm bình luận (${count})`;
                                                                                        } else {
                                                                                            content.classList.add('show');
                                                                                            icon.className = 'fas fa-chevron-up';
                                                                                            button.innerHTML = '<i class="fas fa-chevron-up"></i> Ẩn bớt bình luận';
                                                                                        }
                                                                                    }

                                                                                    function toggleReplyRow(btn) {
                                                                                        const rowId = btn.getAttribute('data-target-row');
                                                                                        const row = document.getElementById(rowId);
                                                                                        const count = '';
                                                                                        if (!row)
                                                                                            return;
                                                                                        const isHidden = (row.style.display === 'none' || window.getComputedStyle(row).display === 'none');
                                                                                        if (isHidden) {
                                                                                            row.style.display = 'table-row';
                                                                                            btn.innerHTML = '<i class="fas fa-chevron-up"></i> Ẩn bớt bình luận';
                                                                                        } else {
                                                                                            row.style.display = 'none';
                                                                                            btn.innerHTML = '<i class="fas fa-chevron-down"></i> Hiển thị thêm bình luận';
                                                                                        }
                                                                                    }
        </script>
    </body>
</html>