<%-- 
    Document   : view-all
    Created on : Jun 29, 2025, 11:59:13 PM
    Author     : LNQB
--%>

<%@page import="model.Category"%>
<%@page import="dao.ViewProductDAO"%>
<%@ page import="java.util.*, model.Product" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%@ page import="model.*" %>
<%@ page import="dao.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%
    List<Product> products = (List<Product>) request.getAttribute("productList");
    ViewProductDAO dao = new ViewProductDAO();
    int categoryId = (int) request.getAttribute("categoryId");
    Map<Integer, Double> avgRatingMap = (Map<Integer, Double>) request.getAttribute("avgRatingMap");
    Map<Integer, Integer> reviewCountMap = (Map<Integer, Integer>) request.getAttribute("reviewCountMap");

    List<Category> cate = (List<Category>) request.getAttribute("dataCate");
    List<Product> product = (List<Product>) request.getAttribute("data");
    List<Product> drinkProducts = (List<Product>) request.getAttribute("featuredProducts1");
    List<Product> FruitProducts = (List<Product>) request.getAttribute("featuredProducts3");
    List<Product> CandyProducts = (List<Product>) request.getAttribute("featuredProducts4");
    List<Product> CosmeticProducts = (List<Product>) request.getAttribute("featuredProducts6");
    List<Product> MilkProducts = (List<Product>) request.getAttribute("featuredProducts2");
    List<Product> MotherBabyProducts = (List<Product>) request.getAttribute("featuredProducts5");
    List<Product> featuredProducts = (List<Product>) request.getAttribute("featuredProducts7");
%>

<!DOCTYPE html>
<html>

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>EcoMart - Cửa hàng mua sắm trực tuyến</title>
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
        <!-- Animate on scroll -->
        <link rel="stylesheet" href="https://unpkg.com/aos@next/dist/aos.css" />
        <!-- shortcuts on scroll -->
        <link rel="stylesheet" href="./assets/css/shortcuts.css?version=<%= System.currentTimeMillis()%>">
    </head>

    <body>
        <jsp:include page="header.jsp" />

        <!-- Main content -->
        <div class="main-content">
            <!-- Hero Banner Section -->
            <section class="hero-section">
                <div class="slideshow-container">
                    <!-- Full-width images with number and caption text -->
                    <div class="mySlides fade">
                        <div class="numbertext">1 / 4</div>
                        <a href="#"><img
                                src="https://cdnv2.tgdd.vn/bhx-static/bhx/7910/tc-moi-1800480_202502081609261675.jpg"
                                alt="Banner 1"></a>
                    </div>
                    <div class="mySlides fade">
                        <div class="numbertext">2 / 4</div>
                        <a href="#"><img
                                src="https://cdnv2.tgdd.vn/bhx-static/bhx/7910/freecompress-pc-1800x480-2_202505071644493334.jpg"
                                alt="Banner 2"></a>
                    </div>
                    <div class="mySlides fade">
                        <div class="numbertext">3 / 4</div>
                        <a href="#"><img
                                src="https://cdnv2.tgdd.vn/bhx-static/bhx/7910/freecompress-1800x480_202502190847402842.jpg"
                                alt="Banner 3"></a>
                    </div>
                    <div class="mySlides fade">
                        <div class="numbertext">4 / 4</div>
                        <a href="#"><img
                                src="https://cdnv2.tgdd.vn/bhx-static/bhx/7910/freecompress-pc-1800x480-2_202503061602312848.jpg"
                                alt="Banner 4"></a>
                    </div>
                    <!-- Next and previous buttons -->
                    <a class="prev" onclick="plusSlides(-1)">&#10094;</a>
                    <a class="next" onclick="plusSlides(1)">&#10095;</a>
                </div>

                <!-- The dots/circles -->
                <div class="dots-container">
                    <span class="dot" onclick="currentSlide(1)"></span>
                    <span class="dot" onclick="currentSlide(2)"></span>
                    <span class="dot" onclick="currentSlide(3)"></span>
                    <span class="dot" onclick="currentSlide(4)"></span>
                </div>
            </section>

            <!-- Category Navigation -->
            <section class="category-section category-section--shortcuts" data-aos="fade-up">
                <div class="category-wrapper">

                    <!-- Đơn hàng của bạn -->
                    <div class="category-item">
                        <a href="${pageContext.request.contextPath}/reorder">
                            <div class="category-icon">
                                <i class="fa-solid fa-clipboard-list" aria-hidden="true"></i>
                            </div>
                            <p>Đơn hàng của bạn</p>
                        </a>
                    </div>

                    <!-- Mã Voucher -->
                    <div class="category-item">
                        <a href="${pageContext.request.contextPath}/reorder">
                            <div class="category-icon">
                                <i class="fa-solid fa-ticket-simple" aria-hidden="true"></i>
                            </div>
                            <p>Mã Voucher</p>
                        </a>
                    </div>

                    <!-- Flash Sale -->
                    <div class="category-item">
                        <a href="${pageContext.request.contextPath}/reorder">
                            <div class="category-icon">
                                <i class="fa-solid fa-bolt" aria-hidden="true"></i>
                            </div>
                            <p>Flash Sale</p>
                        </a>
                    </div>

                    <!-- Ưu đãi Hè 2025 -->
                    <div class="category-item">
                        <a href="${pageContext.request.contextPath}/reorder">
                            <div class="category-icon">
                                <i class="fa-solid fa-sun" aria-hidden="true"></i>
                            </div>
                            <p>Ưu đãi Hè 2025</p>
                        </a>
                    </div>

                </div>
            </section>


            <section class="product-section" data-aos="fade-up">
                <div class="section-header">
                    <div class="section-title">
                        <%= request.getAttribute("categoryName")%>
                    </div>
                </div>
                <div class="product-grid">
                    <%
                        if (products != null && !products.isEmpty()) {
                            for (Product p : products) {
                    %>
                    <div class="product-card" data-product-id="<%= p.getProductID()%>" data-stock-quantity="<%= 0%>">
                        <% if (0 <= 0) { %>
                        <div class="product-badge out-of-stock">Hết hàng</div>
                        <% }%>
                        <div class="product-image-container">
                            <img src="ImageServlet?name=<%= p.getImageURL()%>" alt="<%= p.getProductName()%>" class="product-image">
                            <div class="product-actions">
                                <button class="action-btn add-to-cart-action" data-product-id="<%= p.getProductID()%>" data-stock-quantity="<%= 0%>" <%= 0 <= 0 ? "disabled style='opacity:0.5;cursor:not-allowed;'" : ""%>><i class="fas fa-cart-plus"></i></button>
                                <a href="ProductDetail?id=<%= p.getProductID()%>" class="action-btn"><i class="fas fa-eye"></i></a>
                            </div>
                        </div>
                        <div class="product-info">
                            <h3 class="product-name"><%= p.getProductName()%></h3>
                            <div class="product-rating">
                                <%
                                    int productId = p.getProductID();
                                    Double avgRating = avgRatingMap != null ? avgRatingMap.get(productId) : 0.0;
                                    Integer reviewCount = reviewCountMap != null ? reviewCountMap.get(productId) : 0;

                                    if (avgRating == null) {
                                        avgRating = 0.0;
                                    }
                                    if (reviewCount == null) {
                                        reviewCount = 0;
                                    }

                                    int fullStars = avgRating.intValue();
                                    boolean hasHalfStar = (avgRating - fullStars) >= 0.5;
                                %>
                                <% for (int i = 0; i < fullStars; i++) { %>
                                <i class="fas fa-star"></i>
                                <% } %>
                                <% if (hasHalfStar) { %>
                                <i class="fas fa-star-half-alt"></i>
                                <% } %>
                                <% for (int i = fullStars + (hasHalfStar ? 1 : 0); i < 5; i++) { %>
                                <i class="far fa-star"></i>
                                <% }%>
                                <span>(<%= reviewCount%>)</span>
                            </div>

                            <div class="product-price">
                                <%
                                    // Ưu tiên hiển thị chuỗi giá đã được format sẵn từ Controller (chuẩn MVC)
                                    java.util.Map<Integer, String> priceDisplayMap = (java.util.Map<Integer, String>) request.getAttribute("priceDisplayMap");
                                    if (priceDisplayMap != null) {
                                        String display = priceDisplayMap.get(p.getProductID());
                                        if (display != null && !display.trim().isEmpty()) {
                                            out.print(display);
                                        } else {
                                            // Fallback cũ nếu map chưa được cung cấp
                                            java.util.Map<Integer, Double> unitPriceMap = (java.util.Map<Integer, Double>) request.getAttribute("unitPriceMap");
                                            Double unitPrice = null;
                                            if (unitPriceMap != null) {
                                                unitPrice = unitPriceMap.get(p.getProductID());
                                            }
                                            java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols();
                                            symbols.setGroupingSeparator('.');
                                            java.text.DecimalFormat formatter = new java.text.DecimalFormat("#,###", symbols);
                                            if (unitPrice != null) {
                                                out.print(formatter.format(unitPrice) + " đ / lon");
                                            } else {
                                                out.print(formatter.format(p.getPrice()) + " đ / thùng");
                                            }
                                        }
                                    } else {
                                        // Fallback khi không có priceDisplayMap (trường hợp xem tất cả theo danh mục cũ)
                                        java.util.Map<Integer, Double> unitPriceMap = (java.util.Map<Integer, Double>) request.getAttribute("unitPriceMap");
                                        Double unitPrice = null;
                                        if (unitPriceMap != null) {
                                            unitPrice = unitPriceMap.get(p.getProductID());
                                        }
                                        java.text.DecimalFormatSymbols symbols = new java.text.DecimalFormatSymbols();
                                        symbols.setGroupingSeparator('.');
                                        java.text.DecimalFormat formatter = new java.text.DecimalFormat("#,###", symbols);
                                        if (unitPrice != null) {
                                            out.print(formatter.format(unitPrice) + " đ / lon");
                                        } else {
                                            out.print(formatter.format(p.getPrice()) + " đ / thùng");
                                        }
                                    }
                                %>
                            </div>
                            <div class="button-group">
                                <button class="add-to-cart-btn" data-product-id="<%= p.getProductID()%>" data-stock-quantity="<%= 0%>" <%= 0 <= 0 ? "disabled style='opacity:0.5;cursor:not-allowed;'" : ""%>><i class="fas fa-shopping-cart"></i> Giỏ hàng</button>
                                <form action="<%= request.getContextPath()%>/buy-now" method="post" style="display: inline;"> 
                                    <input type="hidden" name="productID" value="<%= p.getProductID()%>"> 
                                    <input type="hidden" name="quantity" value="1"> 
                                    <input type="hidden" name="action" value="initiate"> 
                                    <button type="submit" class="buy-now-btn" <%= 0 <= 0 ? "disabled style='opacity:0.5;cursor:not-allowed;'" : ""%>>Mua ngay</button> 
                                </form>
                            </div>
                        </div>
                    </div>
                    <%
                        }
                    } else {
                    %>
                    <p>Không có sản phẩm nào thuộc danh mục này.</p>
                    <%
                        }
                    %>
                </div>
            </section>

            <!-- Tips Section -->
            <section class="tips-section" data-aos="fade-up">
                <div class="section-header">
                    <div class="section-title">
                        <i class="fas fa-lightbulb"></i> Mẹo vặt hữu ích
                    </div>
                </div>

                <div class="tips-container">
                    <div class="tips-video">
                        <iframe width="560" height="315"
                                src="https://www.youtube.com/embed/jN58QsGK4WI?si=2bBTyZJ0KCMstDbu"
                                title="YouTube video player" frameborder="0"
                                allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
                                referrerpolicy="strict-origin-when-cross-origin" allowfullscreen></iframe>
                    </div>

                    <div class="tips-list">
                        <a href="https://danavimart.vn/blogs/news/4-cach-rua-rau-cu-qua-sach-dung-cach-tai-nha-an-toan-cho-suc-khoe" class="tip-item">
                            <div class="tip-image">
                                <img src="https://th.bing.com/th/id/OIP.z6N7VnK2aWx6YlRmR-gpCwHaEK?w=200&h=112&rs=1&qlt=80&o=6&dpr=1.3&pid=3.1"
                                     alt="Làm sạch rau củ">
                            </div>
                            <div class="tip-content">
                                <h3 class="tip-title">Cách làm sạch rau củ nhanh chóng</h3>
                                <p class="tip-desc">Học cách rửa rau củ đúng cách để loại bỏ bụi bẩn và hóa
                                    chất.</p>
                            </div>
                        </a>

                        <a href="https://eva.vn/tu-van-nha-cua/cach-de-thit-ca-tuan-khong-hoi-khong-hong-nhieu-nguoi-dung-tu-lanh-hon-20-nam-khong-biet-c172a455815.html" class="tip-item">
                            <div class="tip-image">
                                <img src="https://th.bing.com/th/id/OIP.quVOFTDt4CAdc7VhwyocagHaEo?w=295&h=184&c=7&r=0&o=7&cb=iwp2&dpr=1.3&pid=1.7&rm=3"
                                     alt="Bảo quản thịt cá">
                            </div>
                            <div class="tip-content">
                                <h3 class="tip-title">Bí quyết bảo quản thịt cá tươi lâu</h3>
                                <p class="tip-desc">Mẹo giữ thịt cá tươi ngon trong tủ lạnh hoặc tủ đông.
                                </p>
                            </div>
                        </a>

                        <a href="https://afamily.vn/nhung-cach-thong-minh-de-giam-lang-phi-thuc-pham-20230411120935868.chn" class="tip-item">
                            <div class="tip-image">
                                <img src="https://th.bing.com/th/id/OIP.-2dh96wDCZIaPp8k1q3b7gHaE8?w=269&h=180&c=7&r=0&o=7&cb=iwp2&dpr=1.3&pid=1.7&rm=3"
                                     alt="Chống lãng phí">
                            </div>
                            <div class="tip-content">
                                <h3 class="tip-title">Mẹo chống lãng phí thực phẩm</h3>
                                <p class="tip-desc">Cách tận dụng thức ăn thừa để tiết kiệm và bảo vệ môi
                                    trường.</p>
                            </div>
                        </a>

                        <a href="https://drinkocany.com/nuoc-uong-giai-nhiet-mua-he/" class="tip-item">
                            <div class="tip-image">
                                <img src="https://th.bing.com/th/id/OIP.qtsxKJLVM8WZX27VbcEp3AHaE8?w=242&h=180&c=7&r=0&o=7&cb=iwp2&dpr=1.3&pid=1.7&rm=3"
                                     alt="Nước uống tại nhà">
                            </div>
                            <div class="tip-content">
                                <h3 class="tip-title">Cách làm nước uống giải nhiệt tại nhà</h3>
                                <p class="tip-desc">Công thức đơn giản để tạo ra thức uống mát lành từ trái
                                    cây tươi.</p>
                            </div>
                        </a>
                    </div>
                </div>
            </section>
        </div>

        <jsp:include page="scrollToTop.jsp" />
        <jsp:include page="footer.jsp" />
        <!-- JavaScript -->
        <script src="https://unpkg.com/aos@next/dist/aos.js"></script>
        <script>
                        // Initialize AOS
                        AOS.init({
                            duration: 800,
                            easing: 'ease-in-out',
                            once: true,
                            delay: 100
                        });

                        // Current slide index
                        let slideIndex = 1;
                        showSlides(slideIndex);

                        // Auto slide every 4 seconds
                        setInterval(function () {
                            plusSlides(1);
                        }, 4000);

                        // Next/previous controls
                        function plusSlides(n) {
                            showSlides(slideIndex += n);
                        }

                        // Thumbnail image controls
                        function currentSlide(n) {
                            showSlides(slideIndex = n);
                        }

                        function showSlides(n) {
                            let i;
                            let slides = document.getElementsByClassName("mySlides");
                            let dots = document.getElementsByClassName("dot");
                            if (n > slides.length) {
                                slideIndex = 1
                            }
                            if (n < 1) {
                                slideIndex = slides.length
                            }
                            for (i = 0; i < slides.length; i++) {
                                slides[i].style.display = "none";
                            }
                            for (i = 0; i < dots.length; i++) {
                                dots[i].className = dots[i].className.replace(" active", "");
                            }
                            slides[slideIndex - 1].style.display = "block";
                            dots[slideIndex - 1].className += " active";
                        }
        </script>
        <script src="${pageContext.request.contextPath}/assets/js/loadMore.js"></script>
        <script src="${pageContext.request.contextPath}/assets/js/cart.js?version=<%= System.currentTimeMillis()%>"></script>
    </body>

</html>
