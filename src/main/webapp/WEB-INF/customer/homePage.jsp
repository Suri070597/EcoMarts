<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.*" %>
<%@ page import="model.*" %>
<%@ page import="dao.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    ViewProductDAO dao = new ViewProductDAO();
    List<Category> cate = (List<Category>) request.getAttribute("dataCate");
    List<Product> product = (List<Product>) request.getAttribute("data");
    List<Product> drinkProducts = (List<Product>) request.getAttribute("featuredProducts1");
    List<Product> FruitProducts = (List<Product>) request.getAttribute("featuredProducts3");
    List<Product> CandyProducts = (List<Product>) request.getAttribute("featuredProducts4");
    List<Product> CosmeticProducts = (List<Product>) request.getAttribute("featuredProducts6");
    List<Product> MilkProducts = (List<Product>) request.getAttribute("featuredProducts2");
    List<Product> MotherBabyProducts = (List<Product>) request.getAttribute("featuredProducts5");
    List<Product> featuredProducts = (List<Product>) request.getAttribute("featuredProducts7");
    java.util.Map<Integer, String> priceDisplayMap = (java.util.Map<Integer, String>) request.getAttribute("priceDisplayMap");
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
        <!-- Bootstrap CSS -->
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <!-- Main CSS -->
        <link rel="stylesheet" href="./assets/css/main.css?version=<%= System.currentTimeMillis()%>" />
        <!-- Home CSS -->
        <link rel="stylesheet" href="./assets/css/home.css?version=<%= System.currentTimeMillis()%>">
        <script defer src="./assets/js/homeJs.js"></script>
        <!-- Animate on scroll -->
        <!-- shortcuts on scroll -->
        <link rel="stylesheet" href="./assets/css/shortcuts.css?version=<%= System.currentTimeMillis()%>">

        <link rel="stylesheet" href="https://unpkg.com/aos@next/dist/aos.css" />
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
            <!-- Thêm scope để CSS không ảnh hưởng chỗ khác -->
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
                        <a href="${pageContext.request.contextPath}/voucher-shortcuts">
                            <div class="category-icon">
                                <i class="fa-solid fa-ticket-simple" aria-hidden="true"></i>
                            </div>
                            <p>Mã Voucher</p>
                        </a>
                    </div>

                    <!-- Flash Sale -->
                    <div class="category-item">
                        <a href="${pageContext.request.contextPath}/flashsale-shortcuts">
                            <div class="category-icon">
                                <i class="fa-solid fa-bolt" aria-hidden="true"></i>
                            </div>
                            <p>Flash Sale</p>
                        </a>
                    </div>

                    <!-- Ưu đãi Hè 2025 -->
                    <div class="category-item">
                        <a href="${pageContext.request.contextPath}/seasonal-shortcuts">
                            <div class="category-icon">
                                <i class="fa-solid fa-sun" aria-hidden="true"></i>
                            </div>
                            <p>Ưu đãi Hè 2025</p>
                        </a>
                    </div>

                </div>
            </section>



            <!-- Hot Products Section -->
            <!--mở đầu-->

            <section class="banner-section" data-aos="fade-up">
                <img src="assets/img/freecompress-hero-banner-pc_202504181036599398.jpg" alt="Banner" class="full-width-banner">
            </section>

            <section class="product-section" data-aos="fade-up">
                <div class="section-header">
                    <div class="section-title">
                        <i class="fas fa-glass-cheers"></i> Sản phẩm nổi bật
                    </div>
                    <%-- <a href="ViewAllProductServlet?type=featured" class="view-all">Xem tất cả <i class="fas fa-chevron-right"></i></a>     --%>
                </div>
                <div class="product-grid" id="featured-Products">
                    <%
                        if (featuredProducts != null && !featuredProducts.isEmpty()) {
                            for (Product p : featuredProducts) {
                    %>
                    <div class="product-card" data-product-id="<%= p.getProductID()%>" data-stock-quantity="<%= p.getStockQuantity()%>">
                        <% if (p.getStockQuantity() <= 0) { %>
                        <div class="product-badge out-of-stock">Hết hàng</div>
                        <% }%>
                        <div class="product-image-container">
                            <img src="ImageServlet?name=<%= p.getImageURL()%>" alt="<%= p.getProductName()%>" class="product-image">
                            <div class="product-actions">
                                <button class="action-btn add-to-cart-action" data-product-id="<%= p.getProductID()%>" data-stock-quantity="<%= p.getStockQuantity()%>" <%= p.getStockQuantity() <= 0 ? "disabled style='opacity:0.5;cursor:not-allowed;'" : ""%>><i class="fas fa-cart-plus"></i></button>
                                <a href="<%= request.getContextPath()%>/ProductDetail?id=<%= p.getProductID()%>" class="action-btn"><i class="fas fa-eye"></i></a>
                            </div>
                        </div>
                        <div class="product-info">
                            <h3 class="product-name"><%= p.getProductName()%></h3>
                            <div class="product-rating">
                                <%
                                    double avg = 0.0;
                                    int count = 0;
                                    if (request.getAttribute("avgRatingMap") != null && request.getAttribute("reviewCountMap") != null) {
                                        java.util.Map<Integer, Double> avgRatingMap = (java.util.Map<Integer, Double>) request.getAttribute("avgRatingMap");
                                        java.util.Map<Integer, Integer> reviewCountMap = (java.util.Map<Integer, Integer>) request.getAttribute("reviewCountMap");
                                        avg = avgRatingMap.getOrDefault(p.getProductID(), 0.0);
                                        count = reviewCountMap.getOrDefault(p.getProductID(), 0);
                                    }
                                    int fullStars = (int) avg;
                                    boolean halfStar = (avg - fullStars) >= 0.25 && (avg - fullStars) < 0.75;
                                    int emptyStars = 5 - fullStars - (halfStar ? 1 : 0);
                                    for (int i = 0; i < fullStars; i++) { %>
                                <i class="fas fa-star"></i>
                                <% } %>
                                <% if (halfStar) { %>
                                <i class="fas fa-star-half-alt"></i>
                                <% } %>
                                <% for (int i = 0; i < emptyStars; i++) { %>
                                <i class="far fa-star"></i>
                                <% }%>
                                <span>(<%= count%>)</span>
                            </div>
                            <div class="product-price">
                                <%
                                    String display = priceDisplayMap != null ? priceDisplayMap.get(p.getProductID()) : null;
                                    if (display != null) out.print(display);
                                %>
                            </div>
                            <div class="button-group">
                                <button class="add-to-cart-btn" data-product-id="<%= p.getProductID()%>" data-stock-quantity="<%= p.getStockQuantity()%>" <%= p.getStockQuantity() <= 0 ? "disabled style='opacity:0.5;cursor:not-allowed;'" : ""%>><i class="fas fa-shopping-cart"></i> Giỏ hàng</button>
                                <form action="<%= request.getContextPath()%>/buy-now" method="post" style="display: inline;"> 
                                    <input type="hidden" name="productID" value="<%= p.getProductID()%>"> 
                                    <input type="hidden" name="quantity" value="1"> 
                                    <input type="hidden" name="action" value="initiate"> 
                                    <input type="hidden" name="packageType" value="<%= "kg".equalsIgnoreCase(p.getUnit()) ? "KG" : "UNIT" %>"> 
                                    <input type="hidden" name="packSize" value="0"> 
                                    <button type="submit" class="buy-now-btn" <%= p.getStockQuantity() <= 0 ? "disabled style='opacity:0.5;cursor:not-allowed;'" : ""%>>Mua ngay</button> 
                                </form>
                            </div>
                        </div>
                    </div>
                    <%
                        }
                    } else {
                    %>
                    <p class="no-featured-products">Không có sản phẩm nào để hiển thị.</p>
                    <%
                        }
                    %>
                </div>
                <% if (featuredProducts != null && featuredProducts.size() >= 6) { %>
                <button id="load-more-featured" class="see-more-btn"
                        data-type="featured" data-target="featured-Products">Xem thêm sản phẩm <i class="fas fa-arrow-right"></i></button>
                    <% } %>
            </section>

            <!--kết thúc-->

            <!--mở đầu-->

            <section class="banner-section" data-aos="fade-up">
                <img src="assets/img/freecompress-hero-banner-pc_202504181036599398.jpg" alt="Banner" class="full-width-banner">
            </section>

            <section class="product-section" data-aos="fade-up">
                <div class="section-header">
                    <div class="section-title">
                        <i class="fas fa-glass-cheers"></i> Nước giải khát
                    </div>
                    <a href="ViewAllProductServlet?categoryId=1" class="view-all">Xem tất cả <i class="fas fa-chevron-right"></i></a>    
                </div>
                <div class="product-grid" id="drink-products">
                    <%
                        if (drinkProducts != null && !drinkProducts.isEmpty()) {
                            for (Product p : drinkProducts) {
                    %>
                    <div class="product-card" data-product-id="<%= p.getProductID()%>" data-stock-quantity="<%= p.getStockQuantity()%>">
                        <% if (p.getStockQuantity() <= 0) { %>
                        <div class="product-badge out-of-stock">Hết hàng</div>
                        <% }%>
                        <div class="product-image-container">
                            <img src="ImageServlet?name=<%= p.getImageURL()%>" alt="<%= p.getProductName()%>" class="product-image">
                            <div class="product-actions">
                                <button class="action-btn add-to-cart-action" data-product-id="<%= p.getProductID()%>" data-stock-quantity="<%= p.getStockQuantity()%>" <%= p.getStockQuantity() <= 0 ? "disabled style='opacity:0.5;cursor:not-allowed;'" : ""%>><i class="fas fa-cart-plus"></i></button>
                                <a href="<%= request.getContextPath()%>/ProductDetail?id=<%= p.getProductID()%>" class="action-btn"><i class="fas fa-eye"></i></a>
                            </div>
                        </div>
                        <div class="product-info">
                            <h3 class="product-name"><%= p.getProductName()%></h3>
                            <div class="product-rating">
                                <%
                                    double avg = 0.0;
                                    int count = 0;
                                    if (request.getAttribute("avgRatingMap") != null && request.getAttribute("reviewCountMap") != null) {
                                        java.util.Map<Integer, Double> avgRatingMap = (java.util.Map<Integer, Double>) request.getAttribute("avgRatingMap");
                                        java.util.Map<Integer, Integer> reviewCountMap = (java.util.Map<Integer, Integer>) request.getAttribute("reviewCountMap");
                                        avg = avgRatingMap.getOrDefault(p.getProductID(), 0.0);
                                        count = reviewCountMap.getOrDefault(p.getProductID(), 0);
                                    }
                                    int fullStars = (int) avg;
                                    boolean halfStar = (avg - fullStars) >= 0.25 && (avg - fullStars) < 0.75;
                                    int emptyStars = 5 - fullStars - (halfStar ? 1 : 0);
                                    for (int i = 0; i < fullStars; i++) { %>
                                <i class="fas fa-star"></i>
                                <% } %>
                                <% if (halfStar) { %>
                                <i class="fas fa-star-half-alt"></i>
                                <% } %>
                                <% for (int i = 0; i < emptyStars; i++) { %>
                                <i class="far fa-star"></i>
                                <% }%>
                                <span>(<%= count%>)</span>
                            </div>
                            <div class="product-price">
                                <%
                                    String display = priceDisplayMap != null ? priceDisplayMap.get(p.getProductID()) : null;
                                    if (display != null) out.print(display);
                                %>
                            </div>
                            <div class="button-group">
                                <button class="add-to-cart-btn" data-product-id="<%= p.getProductID()%>" data-stock-quantity="<%= p.getStockQuantity()%>" <%= p.getStockQuantity() <= 0 ? "disabled style='opacity:0.5;cursor:not-allowed;'" : ""%>><i class="fas fa-shopping-cart"></i> Giỏ hàng</button>
                                <form action="<%= request.getContextPath()%>/buy-now" method="post" style="display: inline;"> 
                                    <input type="hidden" name="productID" value="<%= p.getProductID()%>"> 
                                    <input type="hidden" name="quantity" value="1"> 
                                    <input type="hidden" name="action" value="initiate"> 
                                    <input type="hidden" name="packageType" value="<%= "kg".equalsIgnoreCase(p.getUnit()) ? "KG" : "UNIT" %>"> 
                                    <input type="hidden" name="packSize" value="0"> 
                                    <button type="submit" class="buy-now-btn" <%= p.getStockQuantity() <= 0 ? "disabled style='opacity:0.5;cursor:not-allowed;'" : ""%>>Mua ngay</button> 
                                </form>
                            </div>
                        </div>
                    </div>
                    <%
                        }
                    } else {
                    %>
                    <p class="no-featured-products">Không có sản phẩm nào để hiển thị.</p>
                    <%
                        }
                    %>
                </div>
                <% if (drinkProducts != null && drinkProducts.size() >= 6) { %>
                <button id="load-more-drink" class="see-more-btn"
                        data-parent="1" data-target="drink-products">Xem thêm sản phẩm <i class="fas fa-arrow-right"></i></button>
                    <% } %>
            </section>

            <!--kết thúc-->

            <!--mở đầu-->

            <section class="banner-section" data-aos="fade-up">
                <img src="assets/img/freecompress-hero-banner-pc_202504181036599398.jpg" alt="Banner" class="full-width-banner">
            </section>

            <section class="product-section" data-aos="fade-up">
                <div class="section-header">
                    <div class="section-title">
                        <i class="fas fa-apple-alt"></i> Sữa
                    </div>
                    <a href="ViewAllProductServlet?categoryId=2" class="view-all">Xem tất cả <i class="fas fa-chevron-right"></i></a>               
                </div>
                <div class="product-grid" id="milk-products">
                    <%
                        if (MilkProducts != null && !MilkProducts.isEmpty()) {
                            for (Product p : MilkProducts) {
                    %>
                    <div class="product-card" data-product-id="<%= p.getProductID()%>" data-stock-quantity="<%= p.getStockQuantity()%>">
                        <% if (p.getStockQuantity() <= 0) { %>
                        <div class="product-badge out-of-stock">Hết hàng</div>
                        <% }%>
                        <div class="product-image-container">
                            <img src="ImageServlet?name=<%= p.getImageURL()%>" alt="<%= p.getProductName()%>" class="product-image">
                            <div class="product-actions">
                                <button class="action-btn add-to-cart-action" data-product-id="<%= p.getProductID()%>" data-stock-quantity="<%= p.getStockQuantity()%>" <%= p.getStockQuantity() <= 0 ? "disabled style='opacity:0.5;cursor:not-allowed;'" : ""%>><i class="fas fa-cart-plus"></i></button>
                                <a href="<%= request.getContextPath()%>/ProductDetail?id=<%= p.getProductID()%>" class="action-btn"><i class="fas fa-eye"></i></a>
                            </div>
                        </div>
                        <div class="product-info">
                            <h3 class="product-name"><%= p.getProductName()%></h3>
                            <div class="product-rating">
                                <%
                                    double avg = 0.0;
                                    int count = 0;
                                    if (request.getAttribute("avgRatingMap") != null && request.getAttribute("reviewCountMap") != null) {
                                        java.util.Map<Integer, Double> avgRatingMap = (java.util.Map<Integer, Double>) request.getAttribute("avgRatingMap");
                                        java.util.Map<Integer, Integer> reviewCountMap = (java.util.Map<Integer, Integer>) request.getAttribute("reviewCountMap");
                                        avg = avgRatingMap.getOrDefault(p.getProductID(), 0.0);
                                        count = reviewCountMap.getOrDefault(p.getProductID(), 0);
                                    }
                                    int fullStars = (int) avg;
                                    boolean halfStar = (avg - fullStars) >= 0.25 && (avg - fullStars) < 0.75;
                                    int emptyStars = 5 - fullStars - (halfStar ? 1 : 0);
                                    for (int i = 0; i < fullStars; i++) { %>
                                <i class="fas fa-star"></i>
                                <% } %>
                                <% if (halfStar) { %>
                                <i class="fas fa-star-half-alt"></i>
                                <% } %>
                                <% for (int i = 0; i < emptyStars; i++) { %>
                                <i class="far fa-star"></i>
                                <% }%>
                                <span>(<%= count%>)</span>
                            </div>
                            <div class="product-price">
                                <%
                                    String display = priceDisplayMap != null ? priceDisplayMap.get(p.getProductID()) : null;
                                    if (display != null) out.print(display);
                                %>
                            </div>

                            <div class="button-group">
                                <button class="add-to-cart-btn" data-product-id="<%= p.getProductID()%>" data-stock-quantity="<%= p.getStockQuantity()%>" <%= p.getStockQuantity() <= 0 ? "disabled style='opacity:0.5;cursor:not-allowed;'" : ""%>><i class="fas fa-shopping-cart"></i> Giỏ hàng</button>
                                <form action="<%= request.getContextPath()%>/buy-now" method="post" style="display: inline;"> 
                                    <input type="hidden" name="productID" value="<%= p.getProductID()%>"> 
                                    <input type="hidden" name="quantity" value="1"> 
                                    <input type="hidden" name="action" value="initiate"> 
                                    <input type="hidden" name="packageType" value="<%= "kg".equalsIgnoreCase(p.getUnit()) ? "KG" : "UNIT" %>"> 
                                    <input type="hidden" name="packSize" value="0"> 
                                    <button type="submit" class="buy-now-btn" <%= p.getStockQuantity() <= 0 ? "disabled style='opacity:0.5;cursor:not-allowed;'" : ""%>>Mua ngay</button> 
                                </form>
                            </div>
                        </div>
                    </div>
                    <%
                        }
                    } else {
                    %>
                    <p class="no-featured-products">Không có sản phẩm nào để hiển thị.</p>
                    <%
                        }
                    %>
                </div>
                <% if (MilkProducts != null && MilkProducts.size() >= 6) { %>
                <button id="load-more-milk" class="see-more-btn"
                        data-parent="2" data-target="milk-products">
                    Xem thêm sản phẩm <i class="fas fa-arrow-right"></i>
                </button>
                <% } %>            
            </section>

            <!--kết thúc--> 

            <!--mở đầu-->

            <section class="banner-section" data-aos="fade-up">
                <img src="assets/img/freecompress-hero-banner-pc_202504181036599398.jpg" alt="Banner" class="full-width-banner">
            </section>

            <section class="product-section" data-aos="fade-up">
                <div class="section-header">
                    <div class="section-title">
                        <i class="fas fa-apple-alt"></i> Trái cây
                    </div>
                    <a href="ViewAllProductServlet?categoryId=3" class="view-all">Xem tất cả <i class="fas fa-chevron-right"></i></a>               
                </div>
                <div class="product-grid" id="fruit-products">
                    <%
                        if (FruitProducts != null && !FruitProducts.isEmpty()) {
                            for (Product p : FruitProducts) {
                    %>
                    <div class="product-card" data-product-id="<%= p.getProductID()%>" data-stock-quantity="<%= p.getStockQuantity()%>">
                        <% if (p.getStockQuantity() <= 0) { %>
                        <div class="product-badge out-of-stock">Hết hàng</div>
                        <% }%>
                        <div class="product-image-container">
                            <img src="ImageServlet?name=<%= p.getImageURL()%>" alt="<%= p.getProductName()%>" class="product-image">
                            <div class="product-actions">
                                <button class="action-btn add-to-cart-action" data-product-id="<%= p.getProductID()%>" data-stock-quantity="<%= p.getStockQuantity()%>" <%= p.getStockQuantity() <= 0 ? "disabled style='opacity:0.5;cursor:not-allowed;'" : ""%>><i class="fas fa-cart-plus"></i></button>
                                <a href="<%= request.getContextPath()%>/ProductDetail?id=<%= p.getProductID()%>" class="action-btn"><i class="fas fa-eye"></i></a>
                            </div>
                        </div>
                        <div class="product-info">
                            <h3 class="product-name"><%= p.getProductName()%></h3>
                            <div class="product-rating">
                                <%
                                    double avg = 0.0;
                                    int count = 0;
                                    if (request.getAttribute("avgRatingMap") != null && request.getAttribute("reviewCountMap") != null) {
                                        java.util.Map<Integer, Double> avgRatingMap = (java.util.Map<Integer, Double>) request.getAttribute("avgRatingMap");
                                        java.util.Map<Integer, Integer> reviewCountMap = (java.util.Map<Integer, Integer>) request.getAttribute("reviewCountMap");
                                        avg = avgRatingMap.getOrDefault(p.getProductID(), 0.0);
                                        count = reviewCountMap.getOrDefault(p.getProductID(), 0);
                                    }
                                    int fullStars = (int) avg;
                                    boolean halfStar = (avg - fullStars) >= 0.25 && (avg - fullStars) < 0.75;
                                    int emptyStars = 5 - fullStars - (halfStar ? 1 : 0);
                                    for (int i = 0; i < fullStars; i++) { %>
                                <i class="fas fa-star"></i>
                                <% } %>
                                <% if (halfStar) { %>
                                <i class="fas fa-star-half-alt"></i>
                                <% } %>
                                <% for (int i = 0; i < emptyStars; i++) { %>
                                <i class="far fa-star"></i>
                                <% }%>
                                <span>(<%= count%>)</span>
                            </div>
                            <div class="product-price">
                                <%
                                    String display = priceDisplayMap != null ? priceDisplayMap.get(p.getProductID()) : null;
                                    if (display != null) out.print(display);
                                %>
                            </div>
                            <div class="button-group">
                                <button class="add-to-cart-btn" data-product-id="<%= p.getProductID()%>" data-stock-quantity="<%= p.getStockQuantity()%>" <%= p.getStockQuantity() <= 0 ? "disabled style='opacity:0.5;cursor:not-allowed;'" : ""%>><i class="fas fa-shopping-cart"></i> Giỏ hàng</button>
                                <form action="<%= request.getContextPath()%>/buy-now" method="post" style="display: inline;"> 
                                    <input type="hidden" name="productID" value="<%= p.getProductID()%>"> 
                                    <input type="hidden" name="quantity" value="1"> 
                                    <input type="hidden" name="action" value="initiate"> 
                                    <input type="hidden" name="packageType" value="<%= "kg".equalsIgnoreCase(p.getUnit()) ? "KG" : "UNIT" %>"> 
                                    <input type="hidden" name="packSize" value="0"> 
                                    <button type="submit" class="buy-now-btn" <%= p.getStockQuantity() <= 0 ? "disabled style='opacity:0.5;cursor:not-allowed;'" : ""%>>Mua ngay</button> 
                                </form>
                            </div>
                        </div>
                    </div>
                    <%
                        }
                    } else {
                    %>
                    <p class="no-featured-products">Không có sản phẩm nào để hiển thị.</p>
                    <%
                        }
                    %>
                </div>
                <% if (FruitProducts != null && FruitProducts.size() >= 6) { %>
                <button id="load-more-fruit" class="see-more-btn"
                        data-parent="3" data-target="fruit-products">
                    Xem thêm sản phẩm <i class="fas fa-arrow-right"></i>
                </button>
                <% } %>
            </section>

            <!--kết thúc--> 

            <!--mở đầu-->

            <section class="banner-section" data-aos="fade-up">
                <img src="assets/img/freecompress-hero-banner-pc_202504181036599398.jpg" alt="Banner" class="full-width-banner">
            </section>

            <section class="product-section" data-aos="fade-up">
                <div class="section-header">
                    <div class="section-title">
                        <i class="fas fa-apple-alt"></i> Bánh Kẹo
                    </div>
                    <a href="ViewAllProductServlet?categoryId=4" class="view-all">Xem tất cả <i class="fas fa-chevron-right"></i></a>              
                </div>
                <div class="product-grid" id="snack-products">
                    <%
                        if (CandyProducts != null && !CandyProducts.isEmpty()) {
                            for (Product p : CandyProducts) {
                    %>
                    <div class="product-card" data-product-id="<%= p.getProductID()%>" data-stock-quantity="<%= p.getStockQuantity()%>">
                        <% if (p.getStockQuantity() <= 0) { %>
                        <div class="product-badge out-of-stock">Hết hàng</div>
                        <% }%>
                        <div class="product-image-container">
                            <img src="ImageServlet?name=<%= p.getImageURL()%>" alt="<%= p.getProductName()%>" class="product-image">
                            <div class="product-actions">
                                <button class="action-btn add-to-cart-action" data-product-id="<%= p.getProductID()%>" data-stock-quantity="<%= p.getStockQuantity()%>" <%= p.getStockQuantity() <= 0 ? "disabled style='opacity:0.5;cursor:not-allowed;'" : ""%>><i class="fas fa-cart-plus"></i></button>
                                <a href="<%= request.getContextPath()%>/ProductDetail?id=<%= p.getProductID()%>" class="action-btn"><i class="fas fa-eye"></i></a>
                            </div>
                        </div>
                        <div class="product-info">
                            <h3 class="product-name"><%= p.getProductName()%></h3>
                            <div class="product-rating">
                                <%
                                    double avg = 0.0;
                                    int count = 0;
                                    if (request.getAttribute("avgRatingMap") != null && request.getAttribute("reviewCountMap") != null) {
                                        java.util.Map<Integer, Double> avgRatingMap = (java.util.Map<Integer, Double>) request.getAttribute("avgRatingMap");
                                        java.util.Map<Integer, Integer> reviewCountMap = (java.util.Map<Integer, Integer>) request.getAttribute("reviewCountMap");
                                        avg = avgRatingMap.getOrDefault(p.getProductID(), 0.0);
                                        count = reviewCountMap.getOrDefault(p.getProductID(), 0);
                                    }
                                    int fullStars = (int) avg;
                                    boolean halfStar = (avg - fullStars) >= 0.25 && (avg - fullStars) < 0.75;
                                    int emptyStars = 5 - fullStars - (halfStar ? 1 : 0);
                                    for (int i = 0; i < fullStars; i++) { %>
                                <i class="fas fa-star"></i>
                                <% } %>
                                <% if (halfStar) { %>
                                <i class="fas fa-star-half-alt"></i>
                                <% } %>
                                <% for (int i = 0; i < emptyStars; i++) { %>
                                <i class="far fa-star"></i>
                                <% }%>
                                <span>(<%= count%>)</span>
                            </div>
                            <div class="product-price">
                                <%
                                    String display = priceDisplayMap != null ? priceDisplayMap.get(p.getProductID()) : null;
                                    if (display != null) out.print(display);
                                %>
                            </div>
                            <div class="button-group">
                                <button class="add-to-cart-btn" data-product-id="<%= p.getProductID()%>" data-stock-quantity="<%= p.getStockQuantity()%>" <%= p.getStockQuantity() <= 0 ? "disabled style='opacity:0.5;cursor:not-allowed;'" : ""%>><i class="fas fa-shopping-cart"></i> Giỏ hàng</button>
                                <form action="<%= request.getContextPath()%>/buy-now" method="post" style="display: inline;"> 
                                    <input type="hidden" name="productID" value="<%= p.getProductID()%>"> 
                                    <input type="hidden" name="quantity" value="1"> 
                                    <input type="hidden" name="action" value="initiate"> 
                                    <input type="hidden" name="packageType" value="<%= "kg".equalsIgnoreCase(p.getUnit()) ? "KG" : "UNIT" %>"> 
                                    <input type="hidden" name="packSize" value="0"> 
                                    <button type="submit" class="buy-now-btn" <%= p.getStockQuantity() <= 0 ? "disabled style='opacity:0.5;cursor:not-allowed;'" : ""%>>Mua ngay</button> 
                                </form>
                            </div>
                        </div>
                    </div>
                    <%
                        }
                    } else {
                    %>
                    <p class="no-featured-products">Không có sản phẩm nào để hiển thị.</p>
                    <%
                        }
                    %>
                </div>
                <% if (CandyProducts != null && CandyProducts.size() >= 6) { %>
                <button id="load-more-snack" class="see-more-btn"
                        data-parent="4" data-target="snack-products">
                    Xem thêm sản phẩm <i class="fas fa-arrow-right"></i>
                </button>
                <% } %>
            </section>

            <!--kết thúc--> 




            <!--mở đầu-->

            <section class="banner-section" data-aos="fade-up">
                <img src="assets/img/freecompress-hero-banner-pc_202504181036599398.jpg" alt="Banner" class="full-width-banner">
            </section>

            <section class="product-section" data-aos="fade-up">
                <div class="section-header">
                    <div class="section-title">
                        <i class="fas fa-apple-alt"></i> Mẹ Và Bé
                    </div>
                    <a href="ViewAllProductServlet?categoryId=5" class="view-all">Xem tất cả <i class="fas fa-chevron-right"></i></a>             
                </div>
                <div class="product-grid" id="baby-products">
                    <%
                        if (MotherBabyProducts != null && !MotherBabyProducts.isEmpty()) {
                            for (Product p : MotherBabyProducts) {
                    %>
                    <div class="product-card" data-product-id="<%= p.getProductID()%>" data-stock-quantity="<%= p.getStockQuantity()%>">
                        <% if (p.getStockQuantity() <= 0) { %>
                        <div class="product-badge out-of-stock">Hết hàng</div>
                        <% }%>
                        <div class="product-image-container">
                            <img src="ImageServlet?name=<%= p.getImageURL()%>" alt="<%= p.getProductName()%>" class="product-image">
                            <div class="product-actions">
                                <button class="action-btn add-to-cart-action" data-product-id="<%= p.getProductID()%>" data-stock-quantity="<%= p.getStockQuantity()%>" <%= p.getStockQuantity() <= 0 ? "disabled style='opacity:0.5;cursor:not-allowed;'" : ""%>><i class="fas fa-cart-plus"></i></button>
                                <a href="<%= request.getContextPath()%>/ProductDetail?id=<%= p.getProductID()%>" class="action-btn"><i class="fas fa-eye"></i></a>
                            </div>
                        </div>
                        <div class="product-info">
                            <h3 class="product-name"><%= p.getProductName()%></h3>
                            <div class="product-rating">
                                <%
                                    double avg = 0.0;
                                    int count = 0;
                                    if (request.getAttribute("avgRatingMap") != null && request.getAttribute("reviewCountMap") != null) {
                                        java.util.Map<Integer, Double> avgRatingMap = (java.util.Map<Integer, Double>) request.getAttribute("avgRatingMap");
                                        java.util.Map<Integer, Integer> reviewCountMap = (java.util.Map<Integer, Integer>) request.getAttribute("reviewCountMap");
                                        avg = avgRatingMap.getOrDefault(p.getProductID(), 0.0);
                                        count = reviewCountMap.getOrDefault(p.getProductID(), 0);
                                    }
                                    int fullStars = (int) avg;
                                    boolean halfStar = (avg - fullStars) >= 0.25 && (avg - fullStars) < 0.75;
                                    int emptyStars = 5 - fullStars - (halfStar ? 1 : 0);
                                    for (int i = 0; i < fullStars; i++) { %>
                                <i class="fas fa-star"></i>
                                <% } %>
                                <% if (halfStar) { %>
                                <i class="fas fa-star-half-alt"></i>
                                <% } %>
                                <% for (int i = 0; i < emptyStars; i++) { %>
                                <i class="far fa-star"></i>
                                <% }%>
                                <span>(<%= count%>)</span>
                            </div>
                            <div class="product-price">
                                <%
                                    String display = priceDisplayMap != null ? priceDisplayMap.get(p.getProductID()) : null;
                                    if (display != null) out.print(display);
                                %>
                            </div>
                            <div class="button-group">
                                <button class="add-to-cart-btn" data-product-id="<%= p.getProductID()%>" data-stock-quantity="<%= p.getStockQuantity()%>" <%= p.getStockQuantity() <= 0 ? "disabled style='opacity:0.5;cursor:not-allowed;'" : ""%>><i class="fas fa-shopping-cart"></i> Giỏ hàng</button>
                                <form action="<%= request.getContextPath()%>/buy-now" method="post" style="display: inline;"> 
                                    <input type="hidden" name="productID" value="<%= p.getProductID()%>"> 
                                    <input type="hidden" name="quantity" value="1"> 
                                    <input type="hidden" name="action" value="initiate"> 
                                    <input type="hidden" name="packageType" value="<%= "kg".equalsIgnoreCase(p.getUnit()) ? "KG" : "UNIT" %>"> 
                                    <input type="hidden" name="packSize" value="0"> 
                                    <button type="submit" class="buy-now-btn" <%= p.getStockQuantity() <= 0 ? "disabled style='opacity:0.5;cursor:not-allowed;'" : ""%>>Mua ngay</button> 
                                </form>
                            </div>
                        </div>
                    </div>
                    <%
                        }
                    } else {
                    %>
                    <p class="no-featured-products">Không có sản phẩm nào để hiển thị.</p>
                    <%
                        }
                    %>
                </div>
                <% if (MotherBabyProducts != null && MotherBabyProducts.size() >= 6) { %>
                <button id="load-more-baby" class="see-more-btn"
                        data-parent="5" data-target="baby-products">
                    Xem thêm sản phẩm <i class="fas fa-arrow-right"></i>
                </button>
                <% } %>            
            </section>

            <!--kết thúc--> 

            <!--mở đầu-->

            <section class="banner-section" data-aos="fade-up">
                <img src="assets/img/freecompress-hero-banner-pc_202504181036599398.jpg" alt="Banner" class="full-width-banner">
            </section>

            <section class="product-section" data-aos="fade-up">
                <div class="section-header">
                    <div class="section-title">
                        <i class="fas fa-apple-alt"></i> Mỹ Phẩm
                    </div>
                    <a href="ViewAllProductServlet?categoryId=6" class="view-all">Xem tất cả <i class="fas fa-chevron-right"></i></a>           
                </div>
                <div class="product-grid" id="cosmetic-products">
                    <%
                        if (CosmeticProducts != null && !CosmeticProducts.isEmpty()) {
                            for (Product p : CosmeticProducts) {
                    %>
                    <div class="product-card" data-product-id="<%= p.getProductID()%>" data-stock-quantity="<%= p.getStockQuantity()%>">
                        <% if (p.getStockQuantity() <= 0) { %>
                        <div class="product-badge out-of-stock">Hết hàng</div>
                        <% }%>
                        <div class="product-image-container">
                            <img src="ImageServlet?name=<%= p.getImageURL()%>" alt="<%= p.getProductName()%>" class="product-image">
                            <div class="product-actions">
                                <button class="action-btn add-to-cart-action" data-product-id="<%= p.getProductID()%>" data-stock-quantity="<%= p.getStockQuantity()%>" <%= p.getStockQuantity() <= 0 ? "disabled style='opacity:0.5;cursor:not-allowed;'" : ""%>><i class="fas fa-cart-plus"></i></button>
                                <a href="<%= request.getContextPath()%>/ProductDetail?id=<%= p.getProductID()%>" class="action-btn"><i class="fas fa-eye"></i></a>
                            </div>
                        </div>
                        <div class="product-info">
                            <h3 class="product-name"><%= p.getProductName()%></h3>
                            <div class="product-rating">
                                <%
                                    double avg = 0.0;
                                    int count = 0;
                                    if (request.getAttribute("avgRatingMap") != null && request.getAttribute("reviewCountMap") != null) {
                                        java.util.Map<Integer, Double> avgRatingMap = (java.util.Map<Integer, Double>) request.getAttribute("avgRatingMap");
                                        java.util.Map<Integer, Integer> reviewCountMap = (java.util.Map<Integer, Integer>) request.getAttribute("reviewCountMap");
                                        avg = avgRatingMap.getOrDefault(p.getProductID(), 0.0);
                                        count = reviewCountMap.getOrDefault(p.getProductID(), 0);
                                    }
                                    int fullStars = (int) avg;
                                    boolean halfStar = (avg - fullStars) >= 0.25 && (avg - fullStars) < 0.75;
                                    int emptyStars = 5 - fullStars - (halfStar ? 1 : 0);
                                    for (int i = 0; i < fullStars; i++) { %>
                                <i class="fas fa-star"></i>
                                <% } %>
                                <% if (halfStar) { %>
                                <i class="fas fa-star-half-alt"></i>
                                <% } %>
                                <% for (int i = 0; i < emptyStars; i++) { %>
                                <i class="far fa-star"></i>
                                <% }%>
                                <span>(<%= count%>)</span>
                            </div>
                            <div class="product-price">
                                <%
                                    String display = priceDisplayMap != null ? priceDisplayMap.get(p.getProductID()) : null;
                                    if (display != null) out.print(display);
                                %>
                            </div>
                            <div class="button-group">
                                <button class="add-to-cart-btn" data-product-id="<%= p.getProductID()%>" data-stock-quantity="<%= p.getStockQuantity()%>" <%= p.getStockQuantity() <= 0 ? "disabled style='opacity:0.5;cursor:not-allowed;'" : ""%>><i class="fas fa-shopping-cart"></i> Giỏ hàng</button>
                                <form action="<%= request.getContextPath()%>/buy-now" method="post" style="display: inline;"> 
                                    <input type="hidden" name="productID" value="<%= p.getProductID()%>"> 
                                    <input type="hidden" name="quantity" value="1"> 
                                    <input type="hidden" name="action" value="initiate"> 
                                    <input type="hidden" name="packageType" value="<%= "kg".equalsIgnoreCase(p.getUnit()) ? "KG" : "UNIT" %>"> 
                                    <input type="hidden" name="packSize" value="0"> 
                                    <button type="submit" class="buy-now-btn" <%= p.getStockQuantity() <= 0 ? "disabled style='opacity:0.5;cursor:not-allowed;'" : ""%>>Mua ngay</button> 
                                </form>
                            </div>
                        </div>
                    </div>
                    <%
                        }
                    } else {
                    %>
                    <p class="no-featured-products">Không có sản phẩm nào để hiển thị.</p>
                    <%
                        }
                    %>
                </div>
                <% if (CosmeticProducts != null && CosmeticProducts.size() >= 6) { %>
                <button id="load-more-cosmetic" class="see-more-btn"
                        data-parent="6" data-target="cosmetic-products">
                    Xem thêm sản phẩm <i class="fas fa-arrow-right"></i>
                </button>
                <% }%>            
            </section>

            <!--kết thúc--> 

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
                        <a href="https://eva.vn/tu-van-nha-cua/cach-de-thit-ca-tuan-khong-hoi-khong-hong-nhieu-nguoi-dung-tu-lanh-hon-20-nam-khong-biet-c172a455815.html" class="tip-item">
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
        <script src="${pageContext.request.contextPath}/assets/js/cart.js?version=<%= System.currentTimeMillis()%>"></script>
        <script src="https://unpkg.com/aos@next/dist/aos.js"></script>
        <script>
                        // Initialize AOS
                        AOS.init({
                            duration: 800,
                            easing: 'ease-in-out',
                            once: true,
                            delay: 100
                        });

                        // Make sure cart functionality is initialized
                        if (typeof setupCartButtons === 'function') {
                            setupCartButtons();
                        }

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


        <!-- Load More JavaScript -->
        <script src="${pageContext.request.contextPath}/assets/js/loadMore.js?version=<%= System.currentTimeMillis()%>"></script>
    </body>

</html>