<%@page import="model.Product"%>
<%@page import="java.util.List"%>
<%@page import="dao.productDAO"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    request.setCharacterEncoding("UTF-8");
    response.setContentType("text/html; charset=UTF-8");
    productDAO dao = new productDAO();
    List<Product> product = dao.getAll();
%>

<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <title>Product Management</title>
        <link rel="stylesheet" href="../assets/css/admin.css"/>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
        <style>
            /* Style for the Action column to ensure buttons are on the same row */
            .action-buttons {
                display: flex;
                gap: 10px;
                align-items: center;
                white-space: nowrap;
            }

            /* Style for the Create Date column to prevent line break */
            .date-column {
                white-space: nowrap; /* Prevent text from wrapping */
                min-width: 150px; /* Ensure enough width to hold the date */
            }
        </style>
    </head>
    <body>
        <div class="container">
            <h1>Product list</h1>
            <div class="d-grid gap-2 d-md-flex justify-content-md-end" style="margin: 5px;">
                <a href="./CRUD_Product/create-product.jsp" class="btn btn-success me-md-2">Create</a>
            </div>
        </div>
        <%
            if (product != null && !product.isEmpty()) {
        %>
        <div class="container">
            <table class="table table-striped table-hover">
                <tbody>
                    <tr style="font-weight: bold">
                        <td>ID</td>
                        <td>Product Name</td>
                        <td>Price</td>
                        <td>Quantity</td>
                        <td>Unit</td>                        
                        <td>Description</td>
                        <td>Image</td>
                        <td class="date-column">Create Date</td>
                        <td>Action</td>
                    </tr>
                    <%
                        for (Product pro : product) {
                    %>
                    <tr>
                        <td><%= pro.getProductID()%></td>
                        <td><%= pro.getProductName()%></td>
                        <td><%= pro.getPrice()%></td>
                        <td><%= pro.getQuantity()%></td>
                        <td><%= pro.getUnit()%></td>
                        <td><%= pro.getDescription()%></td>
                        <td>
                            <img src="<%= request.getContextPath() + "/" + pro.getImage()%>" alt="Product Image" style="max-width: 100px; max-height: 100px;">
                        </td>

                        <td class="date-column"><%= pro.getCreatedAt()%></td>
                        <td class="action-buttons">
                            <a href="edit.jsp" class="btn btn-primary">Edit</a>
                            <a href="" class="btn btn-danger">Delete</a>
                        </td>
                    </tr>
                    <%
                        }
                    %>
                </tbody>
            </table>
            <% } else {
            %>
            <div class="container">
                <h1 style="color: red; text-align: center; margin: 30px">There is no data!</h1>
            </div>
            <%
                }
            %>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
    </body>
</html>