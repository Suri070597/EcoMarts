const offsets = {};
const limitPerLoad = 3; // Chỉ tải thêm 3 sản phẩm mỗi lần

document.querySelectorAll(".see-more-btn").forEach(btn => {
    const parentId = btn.dataset.parent;
    const targetId = btn.dataset.target;

    if (!offsets[parentId]) {
//        offsets[parentId] = 6; // nếu bạn hiển thị trước 6 sp
    }

    btn.addEventListener("click", function () {
        const offset = offsets[parentId];
        fetch(`loadMoreFeatured?offset=${offset}&parentId=${parentId}&limit=${limitPerLoad}`)
            .then(res => res.text())
            .then(html => {
                document.getElementById(targetId).insertAdjacentHTML("beforeend", html);
                offsets[parentId] += limitPerLoad;
            })
            .catch(err => console.error("Lỗi load thêm sản phẩm:", err));
    });
});
