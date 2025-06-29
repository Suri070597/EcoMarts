<%-- 
    Document   : productDetail
    Created on : Jun 23, 2025, 10:15:23 AM
    Author     : LNQB
--%>

<%@page import="java.text.SimpleDateFormat"%>
<%@page import="model.Supplier"%>
<%@page import="model.Category"%>
<%@page import="java.util.List"%>
<%@page import="model.Product"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
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
                            <p><span class="sao">4.7</span> <span class="text-warning">★★★★☆</span> | <span class="sao">100</span> Đánh Giá</p>
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

                            <div class="d-flex gap-2 mb-3">
                                <strong>Số Lượng: </strong><input type="number" class="form-control w-25" value="1" min="1">

                            </div>
                            <div>
                                <button class="btn btn-outline-danger">
                                    <i class="fa-solid fa-cart-shopping"></i> Thêm Vào Giỏ Hàng
                                </button>
                                <button class="btn btn-danger">Mua Ngay</button>
                            </div>
                            <div>
                                <a href="#" class="me-2"><i class="fab fa-facebook"></i></a>
                                <a href="#" class="me-2"><i class="fab fa-twitter"></i></a>
                                <a href="#"><i class="fab fa-instagram"></i></a>
                            </div>
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
                                <h4 class="product-text" class="card-title">Đánh Giá (3)</h4>
                                <div class="review-comments">
                                    <div class="review-comment">
                                        <h5>John Doe <span class="text-warning">★★★★☆</span></h5>
                                        <p class="text-muted">17/06/2025</p>
                                        <p>Gói rau tuyệt vời! Tươi và giao hàng đúng hẹn. Rất khuyến khích!</p>
                                    </div>
                                    <div class="review-comment">
                                        <h5>Jane Smith <span class="text-warning">★★★★★</span></h5>
                                        <p class="text-muted">16/06/2025</p>
                                        <p>Chất lượng tuyệt vời và đa dạng. Sẽ mua lại!</p>
                                    </div>
                                    <div class="review-comment">
                                        <h5>Mike Johnson <span class="text-warning">★★★☆☆</span></h5>
                                        <p class="text-muted">15/06/2025</p>
                                        <p>Sản phẩm tốt, nhưng bao bì có thể cải thiện hơn.</p>
                                    </div>
                                </div>
                                <nav aria-label="Page navigation">
                                    <ul class="pagination justify-content-center">
                                        <li class="page-item"><a class="page-link" href="#">Trước</a></li>
                                        <li class="page-item"><a class="page-link" href="#">1</a></li>
                                        <li class="page-item"><a class="page-link active" href="#">2</a></li>
                                        <li class="page-item"><a class="page-link" href="#">3</a></li>
                                        <li class="page-item"><a class="page-link" href="#">Sau</a></li>
                                    </ul>
                                </nav>
                                <form>
                                    <div class="mb-3">
                                        <label for="rating" class="form-label">Xếp Hạng Của Bạn</label>
                                        <div class="star-rating">
                                            <input type="radio" id="star5" name="rating" value="5"><label for="star5"
                                                                                                          class="fas fa-star"></label>
                                            <input type="radio" id="star4" name="rating" value="4"><label for="star4"
                                                                                                          class="fas fa-star"></label>
                                            <input type="radio" id="star3" name="rating" value="3"><label for="star3"
                                                                                                          class="fas fa-star"></label>
                                            <input type="radio" id="star2" name="rating" value="2"><label for="star2"
                                                                                                          class="fas fa-star"></label>
                                            <input type="radio" id="star1" name="rating" value="1"><label for="star1"
                                                                                                          class="fas fa-star"></label>
                                        </div>
                                    </div>
                                    <div class="mb-3">
                                        <label for="comment" class="form-label">Bình Luận Của Bạn</label>
                                        <textarea class="form-control" id="comment" rows="6"></textarea>
                                    </div>
                                    <div class="mb-3">
                                        <label for="image" class="form-label">Tải Ảnh Lên</label>
                                        <input type="file" class="form-control" id="image" accept="image/*">
                                    </div>
                                    <button type="button" class="btn btn-success">Gửi Đánh Giá</button>
                                </form>
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
                                    <div class="card h-100 text-center san-pham-lq">
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
    </body>
</html>