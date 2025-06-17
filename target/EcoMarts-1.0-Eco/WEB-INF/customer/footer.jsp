<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <link rel="shortcut icon" href="assets/img/eco.png" type="image/x-icon">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
        <script defer src="./script.js"></script>
        <script defer src="./script_header.js"></script>
        <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
        <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
        <link rel="stylesheet" href="./assets/css/main.css?version=<%= System.currentTimeMillis() %>"/>
        <title>Footer</title>
    </head>
    <body>
        <footer class="footer">
            <div class="footer-container">
                <div class="footer-section">
                    <h4>Liên Hệ</h4>
                    <p><i class="fas fa-clock mr-2"></i> Giờ bán hàng: 7:00 - 21:30</p>
                    <p><i class="fas fa-phone mr-2"></i> Hỗ trợ: 1900 1234</p>
                    <p><i class="fas fa-clock mr-2"></i> Khiếu nại: 8:00 - 20:00</p>
                    <p><i class="fas fa-phone mr-2"></i> Hotline: 1800 5678</p>
                </div>
                <div class="footer-section">
                    <h4>Liên Kết Nhanh</h4>
                    <ul>
                        <li><a href="#">Giới Thiệu</a></li>
                        <li><a href="#">Chính Sách Giao Hàng</a></li>
                        <li><a href="#">Chính Sách Đổi Trả</a></li>
                        <li><a href="#">Hỏi Đáp</a></li>
                        <li><a href="#">Tuyển Dụng</a></li>
                    </ul>
                </div>
                <div class="footer-section">
                    <h4>Kết Nối Với Chúng Tôi</h4>
                    <div class="social-links">
                        <a href="https://facebook.com"><i class="fab fa-facebook-f mr-2"></i> Facebook</a>
                        <a href="https://instagram.com"><i class="fab fa-instagram mr-2"></i> Instagram</a>
                        <a href="https://youtube.com"><i class="fab fa-youtube mr-2"></i> YouTube</a>
                        <a href="https://tiktok.com"><i class="fab fa-tiktok mr-2"></i> TikTok</a>
                    </div>
                </div>
                <div class="footer-section">
                    <h4>Tải Ứng Dụng</h4>
                    <div class="app-download">
                        <a href="https://play.google.com"><img
                                src="https://yardhype.com/wp-content/uploads/2022/09/Android-app-on-google-play.svg_-1024x336.png"
                                alt="Google Play"></a>
                        <a href="https://www.apple.com/app-store"><img
                                src="https://vignette.wikia.nocookie.net/postknight/images/b/b1/Apple_app_store_icon.png/revision/latest?cb=20170703032754"
                                alt="App Store"></a>
                    </div>
                </div>
                <div class="footer-section">
                    <h4>Đối Tác Của Chúng Tôi</h4>
                    <ul>
                        <li><a href="https://fpt.com" class="partner-link"><img
                                    src="https://th.bing.com/th/id/OIP.BvLL0y8-7584lmJBx8Eg4QHaE8?w=254&h=180&c=7&r=0&o=7&cb=iwp2&dpr=1.3&pid=1.7&rm=3"
                                    alt="FPT Logo" class="partner-logo"> FPT Corporation</a></li>
                        <li><a href="https://sumitomocorp.com" class="partner-link"><img
                                    src="https://th.bing.com/th/id/OIP.sIqNZUMhnb-8j7tvIt2exwHaEK?o=7&cb=iwp2rm=3&rs=1&pid=ImgDetMain"
                                    alt="Sumitomo Logo" class="partner-logo"> Sumitomo Corporation</a></li>
                        <li><a href="https://sbigroup.co.jp" class="partner-link"><img
                                    src="https://th.bing.com/th/id/OIP.gBd4CgT4aCdaxAJncTv7MQAAAA?cb=iwp2&rs=1&pid=ImgDetMain"
                                    alt="SBI Logo" class="partner-logo"> SBI Holdings</a></li>
                        <li><a href="https://vingroup.net" class="partner-link"><img
                                    src="https://th.bing.com/th/id/OIP.krbMM5gbsD7GrhwBZxp2RwHaEJ?cb=iwp2&rs=1&pid=ImgDetMain"
                                    alt="Vingroup Logo" class="partner-logo"> Vingroup</a></li>
                        <li><a href="https://synnex.com" class="partner-link"><img
                                    src="https://th.bing.com/th/id/OIP.xKBxSY1vK6acjNEcF6FrHgHaHa?o=7&cb=iwp2rm=3&rs=1&pid=ImgDetMain"
                                    alt="Synnex Logo" class="partner-logo"> Synnex Corporation</a></li>
                    </ul>
                </div>
            </div>
            <div class="footer-bottom">
                <p>&copy; 2025 EcoMart. All Rights Reserved.</p>
            </div>
        </footer>
    </body>
</html>