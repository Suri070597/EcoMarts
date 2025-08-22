// Load More Functionality

document.addEventListener('DOMContentLoaded', function () {
    const offsets = {};
    const limitPerLoad = 3;

    const buttons = document.querySelectorAll(".see-more-btn");

    buttons.forEach((btn) => {
        const parentId = btn.getAttribute('data-parent');
        const type = btn.getAttribute('data-type');
        const targetId = btn.getAttribute('data-target');

        if ((!parentId && type !== 'featured') || !targetId) {
            console.error("❌ Nút thiếu data-parent hoặc data-target:", btn);
            return;
        }

        const key = type === 'featured' ? 'featured' : parentId;
        if (!(key in offsets)) {
            offsets[key] = 6;
        }

        btn.addEventListener("click", function (e) {
            e.preventDefault();

            const offset = offsets[key];

            btn.disabled = true;
            btn.innerHTML = "Đang tải...";

            const url = type === 'featured'
                ? `loadMoreFeatured?offset=${offset}&parentId=0&limit=${limitPerLoad}&type=featured`
                : `loadMoreFeatured?offset=${offset}&parentId=${parentId}&limit=${limitPerLoad}`;

            fetch(url)
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP error! status: ${response.status}`);
                    }
                    return response.text();
                })
                .then(html => {
                    if (html && html.trim() !== '' && html !== 'error') {
                        const container = document.getElementById(targetId);
                        if (container) {
                            container.insertAdjacentHTML('beforeend', html);
                            offsets[key] += limitPerLoad;

                            if (window.ecomartsCart && typeof window.ecomartsCart.setupCartButtons === 'function') {
                                window.ecomartsCart.setupCartButtons();
                            }
                        } else {
                            console.error('❌ Không tìm thấy container:', targetId);
                        }

                        checkMoreProducts(key, btn, type);
                    } else {
                        btn.disabled = true;
                        btn.innerHTML = "Không còn sản phẩm";
                        btn.style.opacity = "0.5";
                    }
                })
                .catch(error => {
                    console.error('❌ Lỗi fetch:', error);
                    btn.disabled = false;
                    btn.innerHTML = "Xem thêm sản phẩm <i class=\"fas fa-arrow-right\"></i>";
                });
        });
    });

    function checkMoreProducts(key, btn, type) {
        const nextOffset = offsets[key];

        const url = type === 'featured'
            ? `loadMoreFeatured?offset=${nextOffset}&parentId=0&limit=1&type=featured`
            : `loadMoreFeatured?offset=${nextOffset}&parentId=${key}&limit=1`;
        fetch(url)
            .then(response => response.text())
            .then(html => {
                if (html && html.trim() !== '' && html !== 'error') {
                    btn.disabled = false;
                    btn.innerHTML = "Xem thêm sản phẩm <i class=\"fas fa-arrow-right\"></i>";
                    btn.style.opacity = "1";
                } else {
                    btn.disabled = true;
                    btn.innerHTML = "Không còn sản phẩm";
                    btn.style.opacity = "0.5";
                }
            })
            .catch(error => {
                console.error('❌ Lỗi kiểm tra sản phẩm:', error);
                btn.disabled = false;
                btn.innerHTML = "Xem thêm sản phẩm <i class=\"fas fa-arrow-right\"></i>";
            });
    }
}); 