<%@page import="java.util.List"%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Create Page</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
        <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.min.css">
    </head>
    <body>
        <div class="container">
            <!-- Thanh điều hướng -->
            <h1 style="margin-top: 45px;">Create product</h1>
            <form method="post" action="../Product?action=create" enctype="multipart/form-data">

                <div class="mb-3">
                    <label class="form-label">Product Name</label>
                    <input type="text" class="form-control" name="pName" id="pName" required  />
                </div>

                <div class="mb-3">
                    <label class="form-label">Product Price</label>
                    <input type="number" min="0" step="any" class="form-control" name="pPrice" id="pPrice" required  />
                </div>

                <div class="mb-3">
                    <label class="form-label">Product Quantity</label>
                    <input type="number" min="0" class="form-control" name="pQuanity" id="pQuanity" required  />
                </div>

                <div class="mb-3">
                    <label class="form-label">Product Unit</label>
                    <input type="text" class="form-control" name="pUnit" id="pUnit" required  />
                </div> 

                <div class="mb-3">
                    <label class="form-label">Product Description</label>
                    <input type="text" class="form-control" name="pDescription" id="pDescription" required  />
                </div>

                <div class="mb-3">
                    <label class="form-label">Product Image</label>
                    <input type="file" class="form-control" name="pImage" id="pImage" required accept="image/*">
                </div>

                <a href="../product.jsp" class="btn btn-secondary" id="back"><i class="bi bi-arrow-return-left"></i> Back</a>
                <button type="submit" class="btn btn-primary" id="submit"><i class="bi bi-file-earmark-plus"></i> Create</button>
            </form>

        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
    </body>
</html>
