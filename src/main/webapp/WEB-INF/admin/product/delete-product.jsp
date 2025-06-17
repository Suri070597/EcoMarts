<%@page import="model.Product"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    Product mo = (Product) request.getAttribute("mo");
%>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Delete Product</title>
        <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/admin.css?version=<%= System.currentTimeMillis()%>">
        <link rel="stylesheet"
              href="${pageContext.request.contextPath}/assets/css/sidebar.css?version=<%= System.currentTimeMillis()%>">
    </head>
    <body>
        <div class="container-fluid">
            <%-- Include admin sidebar --%>
            <jsp:include page="../components/sidebar.jsp" />

            <div class="main-content">
                <div class="container">
                    <h1>Delete Product</h1>
                    <% if (mo == null) { %>
                    <p>There is no product with that id</p> <br>
                    <a href="${pageContext.request.contextPath}/admin/product" class="btn btn-secondary" id="back"><i class="bi bi-arrow-return-left"></i> Back</a>
                    <% } else {%>
                    <h1>Delete Product ID <%= mo.getProductID()%></h1>
                    <form method="POST" action="${pageContext.request.contextPath}/admin/product">
                        <input type="hidden" name="action" value="delete" />
                        <input type="hidden" name="id" value="<%= mo.getProductID()%>" />
                        <a href="${pageContext.request.contextPath}/admin/product" class="btn btn-secondary" id="back">
                            <i class="bi bi-arrow-return-left"></i> Back
                        </a>
                        <button type="submit" class="btn btn-danger" id="submit">
                            <i class="bi bi-trash"></i> Delete
                        </button>
                    </form>
                    <% }%>

                </div>
            </div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
    </body>
</html>