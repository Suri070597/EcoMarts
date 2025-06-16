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
    </head>
    <body>
        <div class="main-container">
            <div class="content">
                <div class="content-container">
                    <h1>Delete Product</h1>
                    <%
                        if (mo == null) {
                            out.print("<p>There is no product with that id</p>");
                    %>
                    <a href="Product" class="btn btn-secondary" id="back"><i class="bi bi-arrow-return-left"></i> Back</a>
                    <%
                    } else {
                    %>
                    <form method="POST" action="/admin/product?action=delete">
                        <input type="hidden" name="id" value="<%= mo.getProductID()%>" />
                        <a href="Product" class="btn btn-secondary" id="back"><i class="bi bi-arrow-return-left"></i> Back</a>
                        <button type="submit" class="btn btn-danger" id="submit"><i class="bi bi-trash"></i> Delete</button>
                    </form>
                    <%
                        }
                    %>
                </div>
            </div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
    </body>
</html>