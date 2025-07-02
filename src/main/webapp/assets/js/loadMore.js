const offsets = {};
const limitPerLoad = 3;

document.querySelectorAll(".see-more-btn").forEach(btn => {
    const parentId = btn.dataset.parent;
    const targetId = btn.dataset.target;

    if (!parentId) {
        console.error("❌ data-parent không tồn tại trên nút");
        return;
    }

    // Đảm bảo parentId là string key
    if (!(parentId in offsets)) {
        offsets[parentId] = 6;
    }

    btn.addEventListener("click", function () {
        const offset = offsets[parentId];

        // ✅ Kiểm tra kỹ
        if (isNaN(offset)) {
            console.error("❌ offset bị NaN, parentId:", parentId, "offsets:", offsets);
            return;
        }

        console.log("✅ Gửi request với:", {
            offset,
            parentId,
            limit: limitPerLoad
        });

        fetch(`loadMoreFeatured?offset=${offset}&parentId=${parentId}&limit=${limitPerLoad}`)
            .then(res => {
                if (!res.ok) throw new Error("Lỗi server: " + res.status);
                return res.text();
            })
            .then(html => {
                document.getElementById(targetId).insertAdjacentHTML("beforeend", html);
                offsets[parentId] += limitPerLoad;
            })
            .catch(err => {
                console.error("❌ Lỗi fetch hoặc server:", err);
            });
    });
});
