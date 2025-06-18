<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Đăng nhập</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background: #fff6ec;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
        }
        .login-box {
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
            text-align: center;
            width: 350px;
        }
        h2 {
            margin-bottom: 20px;
        }
        .error {
            color: red;
            margin-bottom: 15px;
        }
        input[type="email"], input[type="password"] {
            width: 90%;
            padding: 12px;
            margin: 10px 0;
            border: 1px solid #ccc;
            border-radius: 8px;
        }
        .btn {
            width: 95%;
            padding: 12px;
            background-color: #28a745;
            color: white;
            border: none;
            border-radius: 8px;
            font-size: 16px;
            cursor: pointer;
            margin-top: 10px;
        }
        .btn:hover {
            background-color: #00b07e;
        }
        .links {
            margin-top: 15px;
            font-size: 14px;
        }
        .links a {
            color: #0066cc;
            text-decoration: none;
            margin: 0 5px;
        }
        .google-btn {
            background-color: #28a745;
            color: white;
            text-decoration: none;
            display: inline-block;
            padding: 12px;
            border-radius: 8px;
            width: 95%;
            text-align: center;
        }
    </style>
</head>
<body>
    <div class="login-box">
        <h2>Good to see you again</h2>
        <% if (session.getAttribute("error") != null) { %>
            <p class="error"><%= session.getAttribute("error") %></p>
            <% session.removeAttribute("error"); %>
        <% } %>
        <form action="<%= request.getContextPath() %>/login" method="post">
            <input type="email" name="email" placeholder="e.g. elon@tesla.com" required>
            <input type="password" name="password" placeholder="e.g. ilovemangools123" required>
            <button class="btn" type="submit">Sign in</button>
        </form>
        <div class="links">
            <a href="<%= request.getContextPath() %>/register">Don't have an account?</a> | <a href="#">Forgot password?</a>
        </div>
        <hr style="margin: 20px 0;">
        <a href="https://accounts.google.com/o/oauth2/auth?scope=email profile openid&redirect_uri=http://localhost:8080/FPT_XANH-1.0-SNAPSHOT/Logingoogle&response_type=code&client_id=565975258765-q57qb0mksqf8e0endoqkjojscnehluu9.apps.googleusercontent.com" class="google-btn">
            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-google" viewBox="0 0 16 16">
                <path d="M15.545 6.558a9.42 9.42 0 0 1 .139 1.626c0 2.434-.87 4.492-2.384 5.885h.002C11.978 15.292 10.158 16 8 16A8 8 0 1 1 8 0a7.689 7.689 0 0 1 5.352 2.082l-2.284 2.284A4.347 4.347 0 0 0 8 3.166c-2.087 0-3.86 1.408-4.492 3.304a4.792 4.792 0 0 0 0 3.063h.003c.635 1.893 2.405 3.301 4.492 3.301 1.078 0 2.004-.276 2.722-.764h-.003a3.702 3.702 0 0 0 1.599-2.431H8v-3.08h7.545z" />
            </svg>
            <span class="ms-2 fs-6">Sign in with Google</span>
        </a>
    </div>
</body>
</html>