/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */


document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("createForm");
    const imageInput = document.getElementById("pImage");
    const imageErrorDiv = document.getElementById("imageError");

    form.addEventListener("submit", function (e) {
    const file = imageInput.files[0];
    const allowedTypes = ["image/jpeg", "image/png"];

    if (file) {
        if (!allowedTypes.includes(file.type)) {
            e.preventDefault();
            imageErrorDiv.textContent = "Chỉ chấp nhận ảnh định dạng JPG hoặc PNG!";
            imageErrorDiv.classList.remove("d-none");
        } else if (file.size > 2 * 1024 * 1024) {
            e.preventDefault();
            imageErrorDiv.textContent = "Ảnh không được vượt quá 2MB!";
            imageErrorDiv.classList.remove("d-none");
        } else {
            imageErrorDiv.classList.add("d-none");
        }
    }
});

});