<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="../header.jsp" />

<link rel="stylesheet"
      href="${pageContext.request.contextPath}/assets/css/shortcuts.css?version=<%= System.currentTimeMillis()%>">
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

<main style="margin: 80px 0 0 200px" class="container my-4">
    <section style="margin:60px 0 0 70px" class="hero-section">
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
    <section style="margin: 0 0 0 50px" class="category-section category-section--shortcuts" data-aos="fade-up">
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

    <div style="margin: -80px 0 0 0">
        <div style="margin: 80px 0 0 70px">
            <div  class="section-title">
                <i class="fa-solid fa-ticket"></i> Deal chớp nhoáng !!!
            </div>
            <hr style="margin: 0 0 5px 0px" class="section-divider"/>
        </div>


    </div>

    <style>
        .section-title {
            font-size: 1.5rem;
            font-weight: 600;
            color: #a67c52; /* màu vàng nâu giống mẫu */
            display: flex;
            align-items: center;
            gap: 6px;
        }

        .section-title i {
            color: #a67c52;
        }

        .section-divider {
            border: none;
            border-top: 2px solid red; /* màu nhạt */
            margin: 4px 0 16px;
        }


    </style>
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
</main>


<jsp:include page="../footer.jsp" />
