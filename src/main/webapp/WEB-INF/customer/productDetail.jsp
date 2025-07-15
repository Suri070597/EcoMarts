<%-- 
    Document   : productDetail
    Created on : Jun 23, 2025, 10:15:23 AM
    Author     : LNQB
--%>

<%@page import="dao.FeedBackDAO"%>
<%@page import="model.Review"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="model.Supplier"%>
<%@page import="model.Category"%>
<%@page import="java.util.List"%>
<%@page import="model.Product"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%
    Product mo = (Product) request.getAttribute("mo");
    List<Category> dataCate = (List<Category>) request.getAttribute("dataCate");
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    String nsx = "", hsd = "";
    if (mo != null) {
        nsx = sdf.format(mo.getManufactureDate());
        hsd = sdf.format(mo.getExpirationDate());
    }
%>
<% if (mo == null) { %>
<div style="color:red;">Không tìm thấy thông tin sản phẩm.</div>
return;
<% }%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="robots" content="index, follow">
        <title>Chi Tiết Sản Phẩm</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link rel="shortcut icon" href="assets/img/eco.png" type="image/x-icon">
        <!-- Google Font -->
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap"
              rel="stylesheet">
        <!-- Font Awesome -->
        <link rel="stylesheet"
              href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <!-- Main CSS -->
        <link rel="stylesheet" href="./assets/css/main.css?version=<%= System.currentTimeMillis()%>" />
        <!-- Home CSS -->
        <link rel="stylesheet" href="./assets/css/home.css?version=<%= System.currentTimeMillis()%>">
        <script defer src="./assets/js/homeJs.js"></script>
        <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/assets/css/productDetail.css">

        <!-- Animate on scroll -->
        <link rel="stylesheet" href="https://unpkg.com/aos@next/dist/aos.css" />
    </head>

    <body>
        <jsp:include page="header.jsp" />

        <!-- Main content -->
        <div class="main-content2">
            <%
                String errorMessage = (String) request.getAttribute("errorMessage");
                if (errorMessage != null && !errorMessage.isEmpty()) {
            %>
            <div class="alert alert-danger text-center" role="alert">
                <%= errorMessage%>
            </div>
            <%
            } else {
            %>
            <% if (session.getAttribute("cartMessage") != null) { %>
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                ${sessionScope.cartMessage}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
            <% session.removeAttribute("cartMessage"); %>
            <% } %>

            <% if (session.getAttribute("cartError") != null) { %>
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                ${sessionScope.cartError}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
            <% session.removeAttribute("cartError"); %>
            <% } %>

            <% if (mo != null) {%>
            <div class="container mt-5">
                <div class="row">
                    <div class="col-md-6">
                        <img src="<%= request.getContextPath()%>/ImageServlet?name=<%= mo.getImageURL()%>" 
                             alt="<%= mo.getProductName()%>" class="img-fluid product-image">
                    </div>
                    <div class="col-md-6">
                        <h2 class="product-name"><%= mo.getProductName()%></h2>
                        <div class="promotion-section">
                            <div>
                                <span class="price-discount">đ28.000</span> <span class="original-price">đ47.000</span> <span
                                    class="text-success">-40%</span>
                            </div>
                            <div class="promotion-timer mt-2">⏳ KẾT THÚC TRONG 01:04:09</div>
                        </div>
                        <div class="product-detail">
                            <p><span class="sao">
                                    <fmt:formatNumber value="${avgRating}" maxFractionDigits="1" minFractionDigits="1"/>
                                </span>
                                <span class="text-warning">
                                    <c:forEach begin="1" end="${fullStars}"><i class="fas fa-star"></i></c:forEach><c:if test="${halfStar}"><i class="fas fa-star-half-alt"></i></c:if><c:forEach begin="1" end="${emptyStars}"><i class="far fa-star"></i></c:forEach>
                                </span> | <span class="sao">${reviewCount}</span> Đánh Giá</p>
                            <p class="price-range"><%= new java.text.DecimalFormat("#,###").format(mo.getPrice())%> VND</p>
                            <%
                                Category child = mo.getCategory();
                                String parentName = "N/A";
                                String childName = (child != null) ? child.getCategoryName() : "Unknown";
                                int parentId = -1;

                                if (child != null) {
                                    parentId = child.getParentID();
                                    for (Category c : dataCate) {
                                        if (c.getCategoryID() == parentId) {
                                            parentName = c.getCategoryName();
                                            break;
                                        }
                                    }
                                }
                            %>

                            <p><strong>Thể Loại:</strong>
                                <%
                                    if (parentId == 7) {
                                        out.print(parentName); // chỉ in cha
                                    } else if (!"N/A".equals(parentName)) {
                                        out.print(parentName + " > " + childName); // in cha > con
                                    } else {
                                        out.print(childName); // chỉ có con
                                    }
                                %>
                            </p>
                            <p><strong>Số Lượng Tồn Kho:</strong> <%=mo.getInventory().getQuantity()%></p>
                            <p><strong>Hạn Sử Dụng:</strong>  <%=nsx%> – <%=hsd%></p>
                            <p><strong>Nhà Cung Cấp:</strong> <%=mo.getSupplier().getCompanyName()%></p>

                            <form action="cart" method="post">
                                <input type="hidden" name="action" value="add">
                                <input type="hidden" name="productID" value="<%= mo.getProductID()%>">

                            <div class="d-flex gap-2 mb-3">
                                    <strong>Số Lượng: </strong>
                                    <input type="number" id="product-quantity" name="quantity" class="form-control w-25" value="1" min="1" max="<%= mo.getAvailableQuantity()%>">
                                    <div class="form-text text-danger" id="quantity-warning" style="display: none;">Số lượng vượt quá tồn kho!</div>
                            </div>

                            <div>
                                    <button type="submit" id="add-to-cart-btn" class="btn btn-outline-danger">
                                    <i class="fa-solid fa-cart-shopping"></i> Thêm Vào Giỏ Hàng
                                </button>
                                    <a href="cart" class="btn btn-danger">Mua Ngay</a>
                            </div>

                                <script>
                                    document.addEventListener('DOMContentLoaded', function () {
                                        const quantityInput = document.getElementById('product-quantity');
                                        const quantityWarning = document.getElementById('quantity-warning');
                                        const addToCartBtn = document.getElementById('add-to-cart-btn');
                                        const maxStock = <%= mo.getAvailableQuantity()%>;

                                        // Validate quantity when changed
                                        quantityInput.addEventListener('input', function () {
                                            const quantity = parseInt(this.value);
                                            if (isNaN(quantity) || quantity <= 0) {
                                                this.value = 1;
                                                quantityWarning.style.display = 'none';
                                                addToCartBtn.disabled = false;
                                            } else if (quantity > maxStock) {
                                                quantityWarning.style.display = 'block';
                                                quantityWarning.textContent = 'Số lượng tối đa có thể mua: ' + maxStock;
                                                addToCartBtn.disabled = true;
                                            } else {
                                                quantityWarning.style.display = 'none';
                                                addToCartBtn.disabled = false;
                                            }
                                        });

                                        // Prevent form submission if quantity is invalid
                                        const form = quantityInput.closest('form');
                                        form.addEventListener('submit', function (event) {
                                            const quantity = parseInt(quantityInput.value);
                                            if (isNaN(quantity) || quantity <= 0 || quantity > maxStock) {
                                                event.preventDefault();
                                                quantityWarning.style.display = 'block';
                                                quantityWarning.textContent = quantity > maxStock
                                                        ? 'Số lượng tối đa có thể mua: ' + maxStock
                                                        : 'Vui lòng nhập số lượng hợp lệ';
                                            }
                                        });
                                    });
                                </script>
                            </form>
                            <div>
                                <a href="#" class="me-2"><i class="fab fa-facebook"></i></a>
                                <a href="#" class="me-2"><i class="fab fa-twitter"></i></a>
                                <a href="#"><i class="fab fa-instagram"></i></a>
                            </div>
                    </div>
                </div>

                <div class="row mt-5">
                    <div class="col-12">
                        <div class="card">
                            <div class="card-body">
                                <h4 class="product-text" class="card-title">Thông Tin Sản Phẩm</h4>
                                <p class="card-text"><%=mo.getDescription().replaceAll("\n", "<br/>")%></p>
                            </div>
                        </div>
                    </div>
                </div>
                <%}%>

                <div class="row mt-5">
                    <div class="col-12">
                        <div class="card">
                            <div class="card-body">
                                    <h4 class="product-text" class="card-title">Đánh Giá (${reviewCount})</h4>                                
                                <div class="review-comments">
                                        <c:if test="${empty reviewList}">
                                            <p class="text-muted">Không có đánh giá nào.</p>
                                        </c:if>
                                        <c:forEach var="review" items="${reviewList}">
                                            <div class="review-comment mb-3">
                                                <div class="d-flex justify-content-between align-items-start">
                                                    <div>
                                                        <h5 class="mb-1">
                                                            <strong>${review.userName}<c:if test="${review.accountRole == 2}"> (Staff)</c:if></strong>
                                                            <c:if test="${review.accountRole != 2}">
                                                                <span class="text-warning">
                                                                    <c:forEach begin="1" end="${review.rating}" var="i">★</c:forEach><c:forEach begin="1" end="${5 - review.rating}" var="i">☆</c:forEach>
                                                                    </span>
                                                            </c:if>
                                                            <span class="text-muted small ms-2">
                                                                <fmt:formatDate value="${review.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                                            </span>
                                                        </h5>
                                                    </div>
                                                    <div class="action-buttons">
                                                        <c:if test="${sessionScope.account != null && sessionScope.account.role == 2}">
                                                            <button type="button" class="btn btn-link btn-sm" onclick="setReply('${review.reviewID}', '${review.orderID}', '${review.productID}')">Trả lời</button>
                                                        </c:if>
                                                        <!-- Nút sửa review -->
                                                        <c:if test="${sessionScope.account != null && review.accountID == sessionScope.account.accountID}">
                                                            <a href="Review?action=edit&reviewId=${review.reviewID}" class="btn btn-link btn-sm">Sửa</a>
                                                        </c:if>
                                                    </div>
                                                </div>
                                                <div style="white-space: pre-wrap;">${review.comment}</div>
                                                <c:if test="${not empty review.imageURL}">
                                                    <div>
                                                        <img src="${pageContext.request.contextPath}/ImageServlet_2?name=${review.imageURL}" alt="Ảnh review" style="max-width:150px;max-height:150px;">
                                                    </div>
                                                </c:if>
                                                <c:if test="${not empty review.replies}">
                                                    <div class="toggle-reply-box my-2">
                                                        <button type="button" class="btn btn-outline-info btn-sm" style="border-radius: 20px; font-weight: 500;"
                                                                onclick="toggleReply('${review.reviewID}')">
                                                            <span id="arrow${review.reviewID}" style="font-size: 1.1em;">&#9654;</span>
                                                            <span class="ms-1">Xem phản hồi của nhân viên</span>
                                                        </button>
                                                    </div>
                                                </c:if>
                                                <div id="replySection${review.reviewID}" style="display:none;">
                                                    <c:forEach var="reply" items="${review.replies}">
                                                        <div class="review-reply ms-4 mt-2">
                                                            <div class="d-flex justify-content-between align-items-start">
                                                                <div>
                                                                    <strong>${reply.userName}<c:if test="${reply.accountRole == 2}"> (Staff)</c:if></strong>
                                                                    <c:if test="${reply.accountRole != 2}">
                                                                        <span class="text-warning">
                                                                            <c:forEach begin="1" end="${reply.rating}" var="i">★</c:forEach>
                                                                            <c:forEach begin="1" end="${5 - reply.rating}" var="i">☆</c:forEach>
                                                                            </span>
                                                                    </c:if>
                                                                    <span class="text-muted small ms-2">
                                                                        <fmt:formatDate value="${reply.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                                                    </span>
                                                                </div>
                                                                <div class="action-buttons">
                                                                    <c:if test="${sessionScope.account != null && sessionScope.account.role == 0 && reply.accountRole == 2}">
                                                                        <button type="button" class="btn btn-link btn-sm" onclick="setReply('${reply.reviewID}', '${reply.orderID}', '${reply.productID}')">Trả lời nhân viên</button>
                                                                    </c:if>
                                                                    <!-- Nút sửa reply -->
                                                                    <c:if test="${sessionScope.account != null && reply.accountID == sessionScope.account.accountID}">
                                                                        <a href="Review?action=edit&reviewId=${reply.reviewID}" class="btn btn-link btn-sm">Sửa</a>
                                                                    </c:if>
                                                                </div>
                                                            </div>
                                                            <div style="white-space: pre-wrap;">${reply.comment}</div>
                                                            <c:if test="${not empty reply.imageURL}">
                                                                <div>
                                                                    <img src="${pageContext.request.contextPath}/ImageServlet_2?name=${reply.imageURL}" alt="Ảnh reply" style="max-width:120px;max-height:120px;">
                                                                </div>
                                                            </c:if>
                                                            <c:if test="${not empty reply.replies}">
                                                                <div class="review-reply ms-4 mt-2" style="background: #f8f9fa;">
                                                                    <c:forEach var="subreply" items="${reply.replies}">
                                                                        <div class="d-flex justify-content-between align-items-start">
                                                                            <div>
                                                                                <strong>${subreply.userName}</strong>
                                                                                <span class="text-muted small ms-2">
                                                                                    <fmt:formatDate value="${subreply.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                                                                </span>
                                                                            </div>
                                                                            <div class="action-buttons">
                                                                                <!-- Nút sửa subreply -->
                                                                                <c:if test="${sessionScope.account != null && subreply.accountID == sessionScope.account.accountID}">
                                                                                    <a href="Review?action=edit&reviewId=${subreply.reviewID}" class="btn btn-link btn-sm">Sửa</a>
                                                                                </c:if>
                                                                            </div>
                                                                        </div>
                                                                        <div style="white-space: pre-wrap;">${subreply.comment}</div>
                                                                        <c:if test="${not empty subreply.imageURL}">
                                                                            <div>
                                                                                <img src="${pageContext.request.contextPath}/ImageServlet_2?name=${subreply.imageURL}" alt="Ảnh reply" style="max-width:120px;max-height:120px;">
                                                                            </div>
                                                                        </c:if>
                                                                    </c:forEach>
                                                                </div>
                                                            </c:if>
                                    </div>
                                                    </c:forEach>
                                    </div>
                                    </div>
                                        </c:forEach>
                                </div>
                                <nav aria-label="Page navigation">
                                    <ul class="pagination justify-content-center">
                                            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                                <a class="page-link" href="ProductDetail?id=${mo.productID}&page=${currentPage - 1}">Trước</a>
                                            </li>
                                            <c:forEach var="i" begin="1" end="${totalPages}">
                                                <li class="page-item ${i == currentPage ? 'active' : ''}">
                                                    <a class="page-link" href="ProductDetail?id=${mo.productID}&page=${i}">${i}</a>
                                                </li>
                                            </c:forEach>
                                            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                                <a class="page-link" href="ProductDetail?id=${mo.productID}&page=${currentPage + 1}">Sau</a>
                                            </li>
                                    </ul>
                                </nav>
                                    <c:if test="${not empty message}">
                                        <div class="alert alert-info text-center" style="margin-top: 10px">${message}</div>
                                    </c:if>
                                    <!-- Thông báo cho guest hoặc admin -->
                                    <c:if test="${sessionScope.account == null}">
                                        <div class="alert alert-info mt-3">Bạn cần <a href="login.jsp">đăng nhập</a> để gửi đánh giá hoặc phản hồi.</div>
                                    </c:if>
                                    <c:if test="${sessionScope.account != null && sessionScope.account.role == 1}">
                                        <div class="alert alert-warning mt-3">Tài khoản admin không được phép gửi đánh giá hoặc phản hồi.</div>
                                    </c:if>
                                    <!-- Chỉ hiển thị form review nếu là customer hoặc staff -->
                                    <c:if test="${sessionScope.account != null && (sessionScope.account.role == 0 || sessionScope.account.role == 2)}">
                                        <form method="post" action="Review" enctype="multipart/form-data" id="reviewForm">
                                            <input type="hidden" name="parentReviewId" id="parentReviewId" value="" />
                                            <input type="hidden" name="reviewId" id="reviewId" value="${editingReview.reviewID}" />
                                            <input type="hidden" name="action" id="action" value="${not empty editingReview ? 'edit' : 'add'}" />
                                            <input type="hidden" name="orderId" value="${orderId}">
                                            <input type="hidden" name="productId" value="<%=mo.getProductID()%>">
                                            <c:if test="${sessionScope.account != null && sessionScope.account.role == 0}">
                                                <div class="mb-3" id="ratingSection" style="${empty editingReview ? 'display:block' : (editingReview.parentReviewID == null or editingReview.parentReviewID == 0 ? 'display:block' : 'display:none')}">
                                        <label for="rating" class="form-label">Xếp Hạng Của Bạn</label>
                                        <div class="star-rating">
                                                        <input type="radio" id="star5" name="rating" value="5" ${not empty editingReview and editingReview.rating == 5 ? 'checked' : ''} ${empty editingReview or (editingReview.parentReviewID == null or editingReview.parentReviewID == 0) ? 'required' : ''}><label for="star5" class="fas fa-star"></label>
                                                        <input type="radio" id="star4" name="rating" value="4" ${not empty editingReview and editingReview.rating == 4 ? 'checked' : ''}><label for="star4" class="fas fa-star"></label>
                                                        <input type="radio" id="star3" name="rating" value="3" ${not empty editingReview and editingReview.rating == 3 ? 'checked' : ''}><label for="star3" class="fas fa-star"></label>
                                                        <input type="radio" id="star2" name="rating" value="2" ${not empty editingReview and editingReview.rating == 2 ? 'checked' : ''}><label for="star2" class="fas fa-star"></label>
                                                        <input type="radio" id="star1" name="rating" value="1" ${not empty editingReview and editingReview.rating == 1 ? 'checked' : ''}><label for="star1" class="fas fa-star"></label>
                                        </div>
                                    </div>
                                            </c:if>
                                    <div class="mb-3">
                                                <label for="comment" class="form-label">Bình Luận/Trả Lời</label>
                                                <textarea class="form-control" id="comment" name="comment" rows="6" required>${editingReview.comment}</textarea>
                                    </div>
                                    <div class="mb-3">
                                        <label for="image" class="form-label">Tải Ảnh Lên</label>
                                                <input type="file" class="form-control" id="image" name="image" accept="image/*">
                                                <c:if test="${not empty editingReview.imageURL}">
                                                    <div class="mt-2">
                                                        <small class="text-muted">Ảnh hiện tại:</small><br>
                                                        <img src="${pageContext.request.contextPath}/ImageServlet_2?name=${editingReview.imageURL}" alt="Ảnh hiện tại" style="max-width:150px;max-height:150px;border-radius:5px;margin-top:5px;">
                                                    </div>
                                                </c:if>
                                    </div>
                                            <button type="submit" class="btn btn-success">${not empty editingReview ? 'Cập nhật' : 'Gửi'}</button>
                                            <c:if test="${not empty editingReview}">
                                                <a href="ProductDetail?id=<%=mo.getProductID()%>" class="btn btn-secondary ms-2">Hủy</a>
                                            </c:if>
                                </form>
                                    </c:if>
                            </div>
                        </div>
                    </div>
                </div>

                <h4 class="mb-4 product-text">Sản Phẩm Liên Quan</h4>
                <div id="relatedProductCarousel" class="carousel slide" data-bs-ride="false">
                    <div class="carousel-inner">
                        <%
                            List<Product> related = (List<Product>) request.getAttribute("relatedProducts");
                            int count = 0;
                            if (related != null && !related.isEmpty()) {
                                for (int i = 0; i < related.size(); i += 4) {
                        %>
                        <div class="carousel-item <%= (i == 0 ? "active" : "")%>">
                            <div class="row">
                                <%
                                    for (int j = i; j < i + 4 && j < related.size(); j++) {
                                        Product p = related.get(j);
                                %>
                                <div class="col-md-3">
                                        <div class="card h-100 text-center san-pham-lq" style="margin-top: 10px">
                                        <a href="<%= request.getContextPath()%>/ProductDetail?id=<%= p.getProductID()%>">
                                            <img src="<%= request.getContextPath()%>/ImageServlet?name=<%= p.getImageURL()%>"
                                                 class="card-img-top img-fluid product-image1" style="height: 350px; object-fit: cover;" alt="<%= p.getProductName()%>">
                                            <div class="card-body">
                                                <p class="card-title mb-1"><%= p.getProductName()%></p>
                                                <p class="text-danger fw-bold"><%= new java.text.DecimalFormat("#,###").format(p.getPrice())%>đ</p>
                                            </div>
                                        </a>
                                    </div>
                                </div>
                                <% } %>
                            </div>
                        </div>
                        <% }
                        } else { %>
                        <p class="text-muted1">Không có sản phẩm liên quan.</p>
                        <% }%>
                    </div>

                    <button class="carousel-control-prev custom-carousel-btn" type="button" data-bs-target="#relatedProductCarousel" data-bs-slide="prev">
                        <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                        <span class="visually-hidden">Previous</span>
                    </button>

                    <button class="carousel-control-next custom-carousel-btn" type="button" data-bs-target="#relatedProductCarousel" data-bs-slide="next">
                        <span class="carousel-control-next-icon" aria-hidden="true"></span>
                        <span class="visually-hidden">Next</span>
                    </button>
                </div>
            </div>
        </div>
        <jsp:include page="footer.jsp" />
        <%
            }
        %>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        <script src="https://kit.fontawesome.com/a076d05399.js" crossorigin="anonymous"></script>
            <script src="${pageContext.request.contextPath}/assets/js/cart.js?version=<%= System.currentTimeMillis() %>"></script>
            <script>
                                                                            function setReply(parentId, orderId, productId) {
                                                                                document.getElementById('parentReviewId').value = parentId;
                                                                                document.getElementsByName('orderId')[0].value = orderId;
                                                                                document.getElementsByName('productId')[0].value = productId;
                                                                                document.getElementById('action').value = 'add';
                                                                                document.getElementById('reviewId').value = '';
                                                                                document.getElementById('comment').focus();
                                                                                document.getElementById('comment').value = '';

                                                                                // Reset rating
                                                                                var stars = document.getElementsByName('rating');
                                                                                for (var i = 0; i < stars.length; i++) {
                                                                                    stars[i].checked = false;
                                                                                }

                                                                                // Ẩn/hiện rating
                                                                                var ratingSection = document.getElementById('ratingSection');
                                                                                if (ratingSection) {
                                                                                    if (parentId) {
                                                                                        ratingSection.style.display = 'none';
                                                                                        // Bỏ required cho rating khi reply
                                                                                        for (var i = 0; i < stars.length; i++) {
                                                                                            stars[i].required = false;
                                                                                        }
                                                                                    } else {
                                                                                        ratingSection.style.display = 'block';
                                                                                        // Bắt buộc required khi review gốc
                                                                                        for (var i = 0; i < stars.length; i++) {
                                                                                            stars[i].required = true;
                                                                                        }
                                                                                    }
                                                                                }

                                                                                // Reset file input
                                                                                document.getElementById('image').value = '';

                                                                                // Đổi text button
                                                                                document.querySelector('#reviewForm button[type="submit"]').textContent = 'Gửi';
                                                                            }
            </script>
            <script>
                function toggleReply(reviewId) {
                    var section = document.getElementById('replySection' + reviewId);
                    var arrow = document.getElementById('arrow' + reviewId);
                    if (section.style.display === 'none') {
                        section.style.display = 'block';
                        arrow.innerHTML = '&#9660;'; // ▼
                    } else {
                        section.style.display = 'none';
                        arrow.innerHTML = '&#9654;'; // ▶
                    }
                }
            </script>
            <style>
                .toggle-reply-box {
                    display: flex;
                    align-items: center;
                    margin-bottom: 0.5rem;
                }
                .toggle-reply-box button {
                    transition: background 0.2s, color 0.2s;
                }
                .toggle-reply-box button:hover {
                    background: #e3f2fd;
                    color: #1976d2;
                }
                .action-buttons {
                    display: flex;
                    gap: 8px;
                    flex-wrap: wrap;
                }
                .action-buttons .btn-link {
                    padding: 2px 8px;
                    font-size: 0.875rem;
                    color: #007bff;
                    text-decoration: none;
                    border-radius: 4px;
                    transition: all 0.2s;
                }
                .action-buttons .btn-link:hover {
                    background-color: #f8f9fa;
                    color: #0056b3;
                    text-decoration: none;
                }
                .review-comment, .review-reply {
                    border-left: 3px solid #e9ecef;
                    padding-left: 15px;
                    margin-bottom: 15px;
                }
                .review-reply {
                    border-left-color: #dee2e6;
                }
            </style>
    </body>
</html>