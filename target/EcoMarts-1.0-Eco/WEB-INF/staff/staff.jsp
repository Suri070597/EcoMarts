<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Staff</title>
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <link rel="stylesheet" href="../assets/css/admin.css"/>
        <style>
            /* Ensure the container takes up the full viewport height */
            body, html {
                margin: 0;
                padding: 0;
                height: 100%;
                font-family: 'Roboto', sans-serif;
            }

            .container {
                display: flex;
                min-height: 100vh; /* Full viewport height */
            }

            /* Sidebar styling */
            .sidebar {
                width: 250px; /* Fixed sidebar width */
                background-color: #f4f4f4;
                flex-shrink: 0; /* Prevent sidebar from shrinking */
            }

            .logo {
                text-align: center;
                padding: 20px;
            }

            .logo img {
                max-width: 100%;
                height: auto;
            }

            .menu {
                list-style: none;
                padding: 0;
                margin: 0;
            }

            .menu li {
                padding: 15px 20px;
                cursor: pointer;
            }

            .menu li:hover {
                background-color: #e0e0e0;
            }

            .menu li a {
                text-decoration: none;
                color: #333;
                display: flex;
                align-items: center;
                gap: 10px;
            }

            /* Content area styling */
            .content {
                flex: 1; /* Take up remaining space */
                display: flex;
                flex-direction: column;
            }

            /* Iframe styling to fill the content area */
            #contentFrame {
                flex: 1; /* Fill the entire content area */
                width: 100%;
                height: 100%;
                border: none; /* Remove iframe border */
            }
        </style>
    </head>
    <body>
        <div class="container">
            <aside class="sidebar">
                <div class="logo">
                    <a href="../homePage.jsp"><img src="../assets/img/eco.png" alt="Logo"></a>
                </div>
                <ul class="menu">
                    <li><i class="fas fa-receipt"></i> Order</li>
                    <li>
                        <a href="#" onclick="loadContent('../product.jsp')">
                            <i class="fas fa-box"></i> Product <a href="#" onclick="loadContent('../product.jsp')">
                        </a>
                    </li>
                    <li><i class="fas fa-receipt"></i> Management Voucher</li>
                    <li><i class="fas fa-receipt"></i> Management Feedback</li>
                </ul>
            </aside>
            <main class="content">
                <iframe id="contentFrame" src="" frameborder="0"></iframe>
            </main>
        </div>
        <script>
            function loadContent(page) {
                document.getElementById('contentFrame').src = page;
                window.history.pushState(null, '', '?page=' + page);
            }

            // Check URL parameter on page load
            window.onload = function () {
                const urlParams = new URLSearchParams(window.location.search);
                const page = urlParams.get('page');
                if (page) {
                    document.getElementById('contentFrame').src = page;
                }
            };
        </script>
    </body>
</html>