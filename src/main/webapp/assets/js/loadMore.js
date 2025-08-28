// Load More Functionality
console.log('🔧 LoadMore.js đã được load');

document.addEventListener('DOMContentLoaded', function () {
    console.log('🚀 LoadMore.js đã được khởi tạo');

    const offsets = {};
    const limitPerLoad = 3;

    const buttons = document.querySelectorAll(".see-more-btn");
    console.log('📋 Tìm thấy', buttons.length, 'nút "xem thêm"');

    buttons.forEach((btn, index) => {
        const parentId = btn.getAttribute('data-parent');
        const targetId = btn.getAttribute('data-target');

        console.log(`🔍 Nút ${index + 1}: parentId="${parentId}", targetId="${targetId}"`);

        if (!parentId || !targetId) {
            console.error("❌ Nút thiếu data-parent hoặc data-target:", btn);
            return;
        }

        if (!(parentId in offsets)) {
            offsets[parentId] = 6;
        }

        btn.addEventListener("click", function (e) {
            e.preventDefault();

            const offset = offsets[parentId];
            console.log(`🖱️ Bấm nút: parentId=${parentId}, offset=${offset}`);

            btn.disabled = true;
            btn.innerHTML = "Đang tải...";

            const url = `loadMoreFeatured?offset=${offset}&parentId=${parentId}&limit=${limitPerLoad}`;
            console.log('🌐 Gửi request đến:', url);

            fetch(url)
                .then(response => {
                    console.log('📡 Response status:', response.status);
                    if (!response.ok) {
                        throw new Error(`HTTP error! status: ${response.status}`);
                    }
                    return response.text();
                })
                .then(html => {
                    console.log('📄 Response length:', html.length);
                    console.log('📄 Response preview:', html.substring(0, 100));

                    if (html && html.trim() !== '' && html !== 'error') {
                        const container = document.getElementById(targetId);
                        if (container) {
                            container.insertAdjacentHTML('beforeend', html);
                            offsets[parentId] += limitPerLoad;
                            console.log('✅ Đã thêm sản phẩm thành công, offset mới:', offsets[parentId]);

                            if (window.ecomartsCart && typeof window.ecomartsCart.setupCartButtons === 'function') {
                                console.log('🛒 Khởi tạo lại cart functionality');
                                window.ecomartsCart.setupCartButtons();
                            }
                        } else {
                            console.error('❌ Không tìm thấy container:', targetId);
                        }

                        checkMoreProducts(parentId, btn);
                    } else {
                        console.log('📭 Không còn sản phẩm để load');
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

    function checkMoreProducts(parentId, btn) {
        const nextOffset = offsets[parentId];
        console.log('🔍 Kiểm tra còn sản phẩm không, offset:', nextOffset);

        fetch(`loadMoreFeatured?offset=${nextOffset}&parentId=${parentId}&limit=1`)
            .then(response => response.text())
            .then(html => {
                if (html && html.trim() !== '' && html !== 'error') {
                    console.log('✅ Còn sản phẩm để load');
                    btn.disabled = false;
                    btn.innerHTML = "Xem thêm sản phẩm <i class=\"fas fa-arrow-right\"></i>";
                    btn.style.opacity = "1";
                } else {
                    console.log('📭 Không còn sản phẩm');
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