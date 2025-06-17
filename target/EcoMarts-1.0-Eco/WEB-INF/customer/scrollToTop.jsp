<%-- 
    Document   : scrollToTop
    Created on : May 19, 2025, 1:31:52 PM
    Author     : LNQB
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <style>
        #scrollToTopBtn {
            position: fixed;
            bottom: 20px;
            right: 20px;
            width: 50px;
            height: 50px;
            background-color: #4CAF50;
            color: white;
            border: none;
            border-radius: 50%;
            font-size: 20px;
            cursor: pointer;
            display: flex;
            align-items: center;
            justify-content: center;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
            z-index: 1000;
            transition: background-color 0.3s ease;
        }

        #scrollToTopBtn:hover {
            background-color: #45a049;
        }

        /* Add some content to test scrolling */
        .content {
            height: 1000px;
            padding: 20px;
            font-size: 16px;
            line-height: 1.6;
        }
    </style>
    <body>
        <button id="scrollToTopBtn">↑</button>

        <script>
            const scrollToTopBtn = document.getElementById('scrollToTopBtn');

            scrollToTopBtn.addEventListener('click', () => {
                window.scrollTo({top: 0, behavior: 'smooth'});
            });
        </script>
    </body>
</html>
