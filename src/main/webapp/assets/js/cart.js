/**
 * EcoMarts - Giỏ hàng JavaScript
 * 
 * File này xử lý tất cả chức năng liên quan đến giỏ hàng:
 * - Thêm sản phẩm vào giỏ hàng
 * - Cập nhật số lượng sản phẩm trong giỏ hàng trên UI
 * - Hiển thị thông báo
 */

// Sử dụng IIFE (Immediately Invoked Function Expression) để tránh xung đột biến toàn cục
(function () {
    // Kiểm tra nếu đã khởi tạo
    if (window.ecomartsCartInitialized) {
        console.log('EcomartsCart đã được khởi tạo trước đó');
        return;
    }

    // Đánh dấu đã khởi tạo
    window.ecomartsCartInitialized = true;

    /**
     * Class quản lý giỏ hàng
     */
    class EcomartsCart {
        constructor() {
            this.init();
        }

        /**
         * Khởi tạo các chức năng giỏ hàng
         */
        init() {
            console.log('Initializing EcoMarts Cart');
            try {
                // Thiết lập các nút tăng/giảm số lượng
                this.setupQuantityButtons();

                // Thiết lập các nút xóa sản phẩm
                this.setupRemoveButtons();

                // Thiết lập modal xác nhận
                this.setupConfirmationModal();

                // Thiết lập các nút giỏ hàng
                this.setupCartButtons();

                // Thiết lập Observer để theo dõi thay đổi DOM
                this.setupMutationObserver();

                // Cập nhật số lượng sản phẩm trong giỏ hàng khi trang tải xong
                this.updateCartCount();

                console.log('EcoMarts Cart initialized successfully');
            } catch (error) {
                console.error('Error initializing EcoMarts Cart:', error);
            }
        }

        /**
         * Tính lại tổng theo các item đang được tick trong cart.jsp
         */
        updateSelectedTotals() {
            try {
                const itemCheckboxes = document.querySelectorAll('.item-select');
                if (!itemCheckboxes || itemCheckboxes.length === 0) return;

                const selected = Array.from(itemCheckboxes).filter(cb => cb.checked);
                let total = 0;
                selected.forEach(cb => {
                    const row = cb.closest('.cart-item');
                    const itemTotalEl = row && row.querySelector('.item-total');
                    if (itemTotalEl) {
                        const priceText = itemTotalEl.textContent.trim();
                        const price = parseFloat(priceText.replace(/[^0-9]/g, ''));
                        if (!isNaN(price)) total += price;
                    }
                });

                const formattedTotal = new Intl.NumberFormat('vi-VN').format(total);
                const cartTotalEl = document.querySelector('.cart-total');
                if (cartTotalEl) cartTotalEl.textContent = formattedTotal + ' ₫';
                const subtotalEl = document.getElementById('cart-subtotal-amount');
                if (subtotalEl) subtotalEl.textContent = formattedTotal + ' ₫';
                const countSpan = document.getElementById('cart-item-count');
                if (countSpan) countSpan.textContent = `Tạm tính (${selected.length} sản phẩm)`;
            } catch (e) {
                console.error('Lỗi khi tính lại tổng đã chọn:', e);
            }
        }

        /**
         * Định dạng giá tiền một cách an toàn
         * @param {number|string} price - Giá tiền cần định dạng
         * @param {string} currency - Ký hiệu tiền tệ (mặc định: đ)
         * @returns {string} Giá đã được định dạng
         */
        formatPrice(price, currency = 'đ') {
            // Chuyển đổi giá thành số
            let numericPrice;

            // Xử lý chuỗi nếu price là chuỗi (có thể chứa định dạng)
            if (typeof price === 'string') {
                // Loại bỏ tất cả các ký tự không phải số và dấu chấm
                const cleanedPrice = price.replace(/[^0-9.]/g, '');
                numericPrice = parseFloat(cleanedPrice);
            } else {
                numericPrice = parseFloat(price);
            }

            // Kiểm tra nếu giá không phải là số hợp lệ
            if (isNaN(numericPrice)) {
                console.error('Invalid price value:', price);
                return '0 ' + currency;
            }

            // Không làm tròn, chỉ định dạng số
            try {
                return numericPrice.toLocaleString('vi-VN') + ' ' + currency;
            } catch (error) {
                console.error('Error formatting price:', error);
                return numericPrice.toString() + ' ' + currency;
            }
        }

        /**
         * Thiết lập modal xác nhận
         */
        setupConfirmationModal() {
            // Kiểm tra nếu modal đã tồn tại
            if (document.getElementById('ecomart-confirm-modal')) {
                return;
            }

            // Tạo modal Bootstrap
            const modalHTML = `
                <div class="modal fade" id="ecomart-confirm-modal" tabindex="-1" aria-labelledby="ecomart-confirm-modal-title" aria-hidden="true">
                    <div class="modal-dialog modal-dialog-centered">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title" id="ecomart-confirm-modal-title">Xác nhận</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div class="modal-body" id="ecomart-confirm-modal-body">
                                <!-- Nội dung xác nhận -->
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                                <button type="button" class="btn btn-danger" id="ecomart-confirm-modal-confirm">Xác nhận</button>
                            </div>
                        </div>
                    </div>
                </div>
            `;

            // Thêm modal vào document
            const modalContainer = document.createElement('div');
            modalContainer.innerHTML = modalHTML;
            document.body.appendChild(modalContainer.firstElementChild);
        }

        /**
         * Hiển thị hộp thoại xác nhận
         * @param {string} message - Nội dung xác nhận
         * @param {Function} onConfirm - Hàm callback khi người dùng xác nhận
         */
        showConfirm(message, onConfirm) {
            // Kiểm tra xem đã có container chưa
            let confirmContainer = document.querySelector('.confirm-container');

            if (!confirmContainer) {
                // Tạo container nếu chưa có
                confirmContainer = document.createElement('div');
                confirmContainer.className = 'confirm-container';
                confirmContainer.style.cssText = `
                    position: fixed;
                    top: 0;
                    left: 0;
                    width: 100%;
                    height: 100%;
                    background-color: rgba(0,0,0,0.5);
                    display: flex;
                    justify-content: center;
                    align-items: center;
                    z-index: 9999;
                `;
                document.body.appendChild(confirmContainer);
            }

            // Tạo hộp thoại xác nhận
            const confirmDialog = document.createElement('div');
            confirmDialog.className = 'confirm-dialog';
            confirmDialog.style.cssText = `
                background-color: white;
                padding: 20px;
                border-radius: 8px;
                box-shadow: 0 4px 8px rgba(0,0,0,0.2);
                max-width: 400px;
                width: 90%;
                text-align: center;
            `;

            // Nội dung xác nhận
            const messageElement = document.createElement('p');
            messageElement.textContent = message;
            messageElement.style.marginBottom = '20px';
            confirmDialog.appendChild(messageElement);

            // Nút hủy
            const cancelButton = document.createElement('button');
            cancelButton.textContent = 'Hủy';
            cancelButton.style.cssText = `
                background-color: #f1f1f1;
                border: none;
                padding: 8px 16px;
                margin-right: 10px;
                border-radius: 4px;
                cursor: pointer;
            `;
            cancelButton.addEventListener('click', () => {
                confirmContainer.remove();
            });
            confirmDialog.appendChild(cancelButton);

            // Nút xác nhận
            const confirmButton = document.createElement('button');
            confirmButton.textContent = 'Xác nhận';
            confirmButton.style.cssText = `
                background-color: #f44336;
                color: white;
                border: none;
                padding: 8px 16px;
                border-radius: 4px;
                cursor: pointer;
            `;
            confirmButton.addEventListener('click', () => {
                confirmContainer.remove();
                if (typeof onConfirm === 'function') {
                    onConfirm();
                }
            });
            confirmDialog.appendChild(confirmButton);

            // Thêm hộp thoại vào container
            confirmContainer.appendChild(confirmDialog);
        }

        /**
         * Thiết lập MutationObserver để theo dõi DOM mới
         */
        setupMutationObserver() {
            // Tạo observer để theo dõi các phần tử mới được thêm vào DOM
            const observer = new MutationObserver((mutations) => {
                let shouldResetup = false;

                mutations.forEach((mutation) => {
                    if (mutation.type === 'childList' && mutation.addedNodes.length > 0) {
                        mutation.addedNodes.forEach((node) => {
                            if (node.nodeType === Node.ELEMENT_NODE) {
                                // Tìm kiếm các nút thêm vào giỏ hàng trong DOM mới
                                const hasCartButtons = node.querySelector('.add-to-cart-btn') ||
                                    node.querySelector('.action-btn i.fa-cart-plus') ||
                                    node.classList.contains('product-card') ||
                                    node.querySelector('.product-card');

                                // Tìm kiếm header với cart link
                                const hasCartLinks = node.querySelector('.header-icons a[href*="cart"]');

                                if (hasCartButtons || hasCartLinks) {
                                    shouldResetup = true;
                                }
                            }
                        });
                    }
                });

                // Chỉ cài đặt lại các nút nếu cần thiết
                if (shouldResetup) {
                    console.log('Phát hiện DOM mới có nút giỏ hàng hoặc cart link, thiết lập lại');
                    this.setupCartButtons();
                    this.updateCartCount();
                }
            });

            // Theo dõi toàn bộ document (chỉ thay đổi node, không theo dõi thuộc tính)
            observer.observe(document.body, {
                childList: true,
                subtree: true,
                attributes: false
            });

            console.log('Đã thiết lập MutationObserver');
        }

        /**
         * Thiết lập các nút thêm vào giỏ hàng
         */
        setupCartButtons() {
            this.setupActionButtons();
            this.setupAddToCartButtons();
            console.log('Hoàn tất thiết lập các nút giỏ hàng');
        }

        /**
         * Thiết lập các nút action-btn (nút tròn có icon giỏ hàng)
         */
        setupActionButtons() {
            // Tìm tất cả các nút action-btn với icon giỏ hàng
            const actionBtns = document.querySelectorAll('.product-actions .action-btn.add-to-cart-action, .product-actions .action-btn:has(i.fa-cart-plus), .product-actions .action-btn i.fa-cart-plus');
            console.log(`Tìm thấy ${actionBtns.length} nút action-btn giỏ hàng`);

            // Xử lý từng nút
            actionBtns.forEach(btn => {
                // Kiểm tra xem nút đã có event listener chưa
                if (btn.hasAttribute('data-cart-initialized')) {
                    return; // Đã được khởi tạo rồi
                }

                // Thêm event listener mới
                btn.addEventListener('click', (e) => {
                    e.preventDefault();
                    e.stopPropagation();

                    // Lấy đúng phần tử button
                    const button = e.target.tagName === 'I' ? e.target.parentElement : e.target;

                    // Tìm product-card cha chứa nút này
                    const productCard = button.closest('.product-card');
                    if (!productCard) {
                        console.error('Không tìm thấy product-card chứa nút');
                        return;
                    }

                    // Lấy ID sản phẩm từ thuộc tính data-product-id
                    const productId = productCard.getAttribute('data-product-id');
                    if (!productId) {
                        console.error('Không tìm thấy data-product-id');
                        this.showNotification('Không thể xác định sản phẩm để thêm vào giỏ hàng.', 'error');
                        return;
                    }

                    // Kiểm tra số lượng tồn kho
                    const stockQuantity = parseInt(productCard.getAttribute('data-stock-quantity') || 0);
                    if (stockQuantity <= 0) {
                        console.error('Sản phẩm đã hết hàng');
                        this.showNotification('Sản phẩm đã hết hàng.', 'error');
                        return;
                    }

                    console.log(`Thêm sản phẩm ID=${productId} vào giỏ hàng (từ action-btn), tồn kho=${stockQuantity}`);
                    this.addToCart(productId, 1);
                });

                // Đánh dấu nút đã được khởi tạo
                btn.setAttribute('data-cart-initialized', 'true');
            });
        }

        /**
         * Thiết lập các nút add-to-cart-btn (nút lớn "Giỏ hàng")
         */
        setupAddToCartButtons() {
            // Tìm tất cả các nút add-to-cart-btn
            const addToCartBtns = document.querySelectorAll('.add-to-cart-btn, button[data-product-id]:not(.action-btn)');
            console.log(`Tìm thấy ${addToCartBtns.length} nút add-to-cart-btn`);

            // Xử lý từng nút
            addToCartBtns.forEach(btn => {
                // Kiểm tra xem nút đã có event listener chưa
                if (btn.hasAttribute('data-cart-initialized')) {
                    return; // Đã được khởi tạo rồi
                }

                // Thêm event listener mới
                btn.addEventListener('click', (e) => {
                    e.preventDefault();

                    // Lấy ID sản phẩm từ thuộc tính data-product-id
                    let productId = btn.getAttribute('data-product-id');
                    let stockQuantity = parseInt(btn.getAttribute('data-stock-quantity') || 0);

                    if (!productId) {
                        // Thử tìm từ product-card cha
                        const productCard = btn.closest('.product-card');
                        if (productCard && productCard.getAttribute('data-product-id')) {
                            productId = productCard.getAttribute('data-product-id');
                            stockQuantity = parseInt(productCard.getAttribute('data-stock-quantity') || 0);
                        } else {
                            console.error('Không tìm thấy data-product-id');
                            this.showNotification('Không thể xác định sản phẩm để thêm vào giỏ hàng.', 'error');
                            return;
                        }
                    }

                    // Kiểm tra số lượng tồn kho
                    if (stockQuantity <= 0) {
                        console.error('Sản phẩm đã hết hàng');
                        this.showNotification('Sản phẩm đã hết hàng.', 'error');
                        return;
                    }

                    console.log(`Thêm sản phẩm ID=${productId} vào giỏ hàng, tồn kho=${stockQuantity}`);
                    this.addToCart(productId, 1);
                });

                // Đánh dấu nút đã được khởi tạo
                btn.setAttribute('data-cart-initialized', 'true');
            });
        }

        /**
         * Thiết lập các chức năng AJAX cho giỏ hàng
         */
        setupCartAjaxFunctions() {
            // Kiểm tra nếu đang ở trang giỏ hàng
            if (window.location.pathname.includes('cart')) {
                console.log('Thiết lập các chức năng AJAX cho trang giỏ hàng');

                // Thiết lập nút tăng/giảm số lượng
                this.setupQuantityButtons();

                // Thiết lập nút xóa sản phẩm
                this.setupRemoveButtons();

                // Thiết lập input số lượng
                this.setupQuantityInputs();
            }
        }

        /**
         * Thiết lập nút tăng/giảm số lượng
         */
        setupQuantityButtons() {
            // Lưu giá trị ban đầu cho tất cả các input số lượng
            const quantityInputs = document.querySelectorAll('input[name="quantity"]');
            quantityInputs.forEach(input => {
                input.setAttribute('data-original-value', input.value);
            });

            // Tìm tất cả các nút giảm số lượng
            const decrementButtons = document.querySelectorAll('.btn-outline-secondary.btn-sm:first-child, .quantity-decrease');
            decrementButtons.forEach(button => {
                button.addEventListener('click', (e) => {
                    e.preventDefault();
                    const form = button.closest('form');
                    const quantityInput = form.querySelector('input[name="quantity"]');
                    const currentVal = parseFloat(quantityInput.value);
                    // Nếu đơn vị là kg thì step = 0.1, ngược lại lấy step từ input hoặc mặc định 1
                    const unit = quantityInput.closest('.cart-item')?.querySelector('.product-name + p')?.textContent?.trim();
                    let step = 1;
                    if (unit && unit.includes('kg')) {
                        step = 0.1;
                    } else {
                        step = parseFloat(quantityInput.getAttribute('step')) || 1;
                    }

                    if (!isNaN(currentVal) && currentVal > step) {
                        const newVal = Math.max(currentVal - step, step);
                        quantityInput.setAttribute('data-original-value', currentVal);
                        quantityInput.value = (step < 1 ? newVal.toFixed(1) : newVal.toFixed(0));
                        const cartItemID = form.querySelector('input[name="cartItemID"]').value;
                        this.updateCartItemQuantity(cartItemID, newVal, form);
                    } else if (!isNaN(currentVal) && currentVal <= step) {
                        const cartItemID = form.querySelector('input[name="cartItemID"]').value;
                        this.showConfirm('Bạn có chắc chắn muốn xóa sản phẩm này khỏi giỏ hàng?', () => {
                            this.removeCartItem(cartItemID, form);
                        });
                    }
                });
            });

            // Tìm tất cả các nút tăng số lượng
            const incrementButtons = document.querySelectorAll('.btn-outline-secondary.btn-sm:last-child, .quantity-increase');
            incrementButtons.forEach(button => {
                button.addEventListener('click', (e) => {
                    e.preventDefault();
                    const form = button.closest('form');
                    const quantityInput = form.querySelector('input[name="quantity"]');
                    const currentVal = parseFloat(quantityInput.value);
                    // Nếu đơn vị là kg thì step = 0.1, ngược lại lấy step từ input hoặc mặc định 1
                    const unit = quantityInput.closest('.cart-item')?.querySelector('.product-name + p')?.textContent?.trim();
                    let step = 1;
                    if (unit && unit.includes('kg')) {
                        step = 0.1;
                    } else {
                        step = parseFloat(quantityInput.getAttribute('step')) || 1;
                    }
                    const maxStock = parseFloat(quantityInput.getAttribute('data-max-stock') || quantityInput.getAttribute('max') || Number.MAX_SAFE_INTEGER);

                    if (!isNaN(currentVal)) {
                        if (currentVal + step <= maxStock) {
                            const newVal = currentVal + step;
                            quantityInput.setAttribute('data-original-value', currentVal);
                            quantityInput.value = (step < 1 ? newVal.toFixed(1) : newVal.toFixed(0));
                            const cartItemID = form.querySelector('input[name="cartItemID"]').value;
                            this.updateCartItemQuantity(cartItemID, newVal, form);
                        } else {
                            this.showNotification(`Chỉ còn ${maxStock} sản phẩm trong kho`, 'warning');
                        }
                    }
                });
            });
        }

        /**
         * Thiết lập input số lượng
         */
        setupQuantityInputs() {
            // Tìm tất cả các input số lượng
            const quantityInputs = document.querySelectorAll('input[name="quantity"].quantity-input');
            quantityInputs.forEach(input => {
                // Xóa sự kiện oninput cũ
                input.removeAttribute('oninput');

                // Thêm sự kiện input mới
                input.addEventListener('change', (e) => {
                    e.preventDefault();
                    const form = input.closest('form');
                    const cartItemID = form.querySelector('input[name="cartItemID"]').value;
                    const newVal = parseFloat(input.value);
                    const maxStock = parseFloat(input.getAttribute('data-max-stock') || input.getAttribute('max') || Number.MAX_SAFE_INTEGER);

                    // Tìm thông báo lỗi nếu có
                    const stockWarning = form.querySelector('.stock-warning');

                    // Kiểm tra giá trị hợp lệ
                    if (isNaN(newVal) || newVal < 0.01) {
                        input.value = 1;
                        if (stockWarning) stockWarning.style.display = 'none';
                        this.updateCartItemQuantity(cartItemID, 1, form);
                    } else if (newVal > maxStock) {
                        input.value = maxStock;
                        // Hiển thị cảnh báo khi vượt quá tồn kho - chỉ hiện trong form
                        if (stockWarning) {
                            stockWarning.textContent = `Chỉ còn ${maxStock} sản phẩm trong kho`;
                            stockWarning.style.display = 'block';
                            setTimeout(() => {
                                stockWarning.style.display = 'none';
                            }, 3000);
                        }
                        this.updateCartItemQuantity(cartItemID, maxStock, form);
                    } else {
                        if (stockWarning) stockWarning.style.display = 'none';
                        this.updateCartItemQuantity(cartItemID, newVal, form);
                    }
                });

                // Thêm sự kiện keydown để xử lý Enter
                input.addEventListener('keydown', (e) => {
                    if (e.key === 'Enter') {
                        e.preventDefault();
                        const form = input.closest('form');
                        const cartItemID = form.querySelector('input[name="cartItemID"]').value;
                        const newVal = parseFloat(input.value);
                        const maxStock = parseFloat(input.getAttribute('data-max-stock') || input.getAttribute('max') || Number.MAX_SAFE_INTEGER);
                        if (isNaN(newVal) || newVal < 0.01) {
                            input.value = 1;
                            this.updateCartItemQuantity(cartItemID, 1, form);
                        } else if (newVal > maxStock) {
                            input.value = maxStock;
                            this.updateCartItemQuantity(cartItemID, maxStock, form);
                        } else {
                            this.updateCartItemQuantity(cartItemID, newVal, form);
                        }
                    }
                });
            });

            console.log('Đã thiết lập input số lượng');
        }

        /**
         * Thiết lập nút xóa sản phẩm
         */
        setupRemoveButtons() {
            const removeButtons = document.querySelectorAll('button[title="Xóa"]');
            removeButtons.forEach(button => {
                button.addEventListener('click', (e) => {
                    e.preventDefault();
                    const form = button.closest('form');
                    const cartItemID = form.querySelector('input[name="cartItemID"]').value;

                    // Hiển thị xác nhận trước khi xóa
                    this.showConfirm('Bạn có chắc chắn muốn xóa sản phẩm này khỏi giỏ hàng?', () => {
                        this.removeCartItem(cartItemID, form);
                    });
                });
            });
        }

        /**
         * Thêm sản phẩm vào giỏ hàng
         * @param {string} productId - ID của sản phẩm
         * @param {number} quantity - Số lượng sản phẩm
         */
        addToCart(productId, quantity) {
            console.log(`Thêm vào giỏ hàng: productId=${productId}, quantity=${quantity}`);

            // Xác định URL API dựa vào đường dẫn hiện tại
            let cartUrl = `cart?action=add&productID=${productId}&quantity=${quantity}`;

            // Điều chỉnh URL nếu đang ở trong thư mục con hoặc servlet
            if (window.location.pathname.includes('/customer/') ||
                window.location.pathname.includes('/ProductDetail')) {
                cartUrl = `../cart?action=add&productID=${productId}&quantity=${quantity}`;
            } else if (window.location.pathname.includes('/ViewAllProductServlet')) {
                // Khi truy cập từ ViewAllProductServlet, sử dụng URL tương đối
                cartUrl = `cart?action=add&productID=${productId}&quantity=${quantity}`;
            }

            console.log(`Gửi request đến: ${cartUrl}`);

            // Gọi AJAX
            fetch(cartUrl, {
                method: 'POST',
                headers: {
                    'X-Requested-With': 'XMLHttpRequest',
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP error! Status: ${response.status}`);
                    }

                    // Kiểm tra content-type
                    const contentType = response.headers.get('content-type');
                    if (contentType && contentType.includes('application/json')) {
                        return response.json();
                    } else {
                        // Nếu không phải JSON, đọc text và ném lỗi
                        return response.text().then(text => {
                            console.error('Server returned non-JSON response:', text);
                            throw new Error('Server returned non-JSON response');
                        });
                    }
                })
                .then(data => {
                    console.log('Server response:', data);

                    if (!data.success) {
                        // Xử lý trường hợp lỗi từ server
                        throw new Error(data.message || 'Không thể thêm sản phẩm vào giỏ hàng');
                    }

                    // Cập nhật badge hiển thị số lượng sản phẩm trong giỏ hàng
                    if (data.cartSize !== undefined) {
                        this.updateAllCartBadges(data.cartSize);
                    }

                    // Hiển thị thông báo thành công
                    this.showNotification(data.message || 'Đã thêm sản phẩm vào giỏ hàng');
                })
                .catch(error => {
                    console.error('Error:', error);

                    // Kiểm tra nếu là lỗi hết hàng
                    if (error.message && error.message.includes('Không đủ số lượng')) {
                        this.showNotification(error.message, 'error');
                    } else {
                        this.showNotification('Có lỗi khi thêm vào giỏ hàng: ' + error.message, 'error');
                    }
                });
        }

        /**
         * Cập nhật tất cả cart badges trên trang
         * @param {number} count - Số lượng sản phẩm trong giỏ hàng
         */
        updateAllCartBadges(count) {
            // Tìm tất cả các cart links trên trang
            const cartLinks = document.querySelectorAll('a[href*="cart"]');

            cartLinks.forEach(cartLink => {
                // Tìm badge trong link giỏ hàng
                let badge = cartLink.querySelector('.badge') ||
                    cartLink.querySelector('.bg-danger') ||
                    cartLink.querySelector('.rounded-pill');

                // Nếu badge đã tồn tại
                if (badge) {
                    badge.textContent = count;
                    badge.style.display = count > 0 ? 'inline' : 'none';
                    badge.classList.add('badge-update');

                    // Xóa class animation sau khi hoàn thành
                    setTimeout(() => {
                        badge.classList.remove('badge-update');
                    }, 500);

                    console.log('Đã cập nhật badge hiện có: ' + count);
                }
                // Nếu badge chưa tồn tại và số lượng > 0
                else if (count > 0 && cartLink.closest('.header-icons')) {
                    const newBadge = document.createElement('span');
                    newBadge.className = 'badge bg-danger rounded-pill badge-update';
                    newBadge.textContent = count;
                    cartLink.appendChild(newBadge);

                    // Xóa class animation sau khi hoàn thành
                    setTimeout(() => {
                        newBadge.classList.remove('badge-update');
                    }, 500);

                    console.log('Đã tạo badge mới: ' + count);
                }
            });

            // Đảm bảo cập nhật badge trên tất cả các trang
            this.updateCartBadge(count);
        }

        /**
         * Cập nhật số lượng sản phẩm trên biểu tượng giỏ hàng
         */
        updateCartCount() {
            // Xác định URL API dựa vào đường dẫn hiện tại
            let cartCountUrl = 'cart?action=count';

            // Điều chỉnh URL nếu đang ở trong thư mục con hoặc servlet
            if (window.location.pathname.includes('/customer/') ||
                window.location.pathname.includes('/ProductDetail')) {
                cartCountUrl = '../cart?action=count';
            } else if (window.location.pathname.includes('/ViewAllProductServlet')) {
                // Khi truy cập từ ViewAllProductServlet, sử dụng URL tương đối
                cartCountUrl = `cart?action=count`;
            }

            console.log(`Lấy số lượng giỏ hàng từ: ${cartCountUrl}`);

            // Gửi request đến server
            fetch(cartCountUrl)
                .then(response => {
                    if (!response.ok) {
                        throw new Error('Network response was not ok');
                    }
                    return response.text();
                })
                .then(count => {
                    console.log(`Số lượng giỏ hàng: ${count}`);

                    // Chuyển đổi count thành số nguyên
                    const countInt = parseInt(count.trim(), 10);

                    // Cập nhật tất cả các badges
                    this.updateAllCartBadges(countInt);
                })
                .catch(error => {
                    console.error('Lỗi khi cập nhật số lượng giỏ hàng:', error);
                });
        }

        /**
         * Cập nhật số lượng sản phẩm trong giỏ hàng
         * @param {number} cartItemID - ID của item trong giỏ hàng
         * @param {number} quantity - Số lượng mới
         * @param {HTMLElement} form - Form chứa sản phẩm
         */
        updateCartItemQuantity(cartItemID, quantity, form) {
            console.log(`Cập nhật số lượng: cartItemID=${cartItemID}, quantity=${quantity}`);

            // Tạo URL với query parameters thay vì body
            const url = `cart?action=update&cartItemID=${cartItemID}&quantity=${quantity}`;
            console.log('Request URL:', url);

            fetch(url, {
                method: 'POST',
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                }
            })
                .then(response => {
                    console.log('Response status:', response.status);
                    console.log('Response headers:', [...response.headers.entries()]);

                    if (!response.ok) {
                        throw new Error(`HTTP error! Status: ${response.status}`);
                    }

                    // Đọc response text trước
                    return response.text().then(text => {
                        console.log('Raw response:', text);

                        // Kiểm tra nếu text rỗng
                        if (!text || text.trim() === '') {
                            throw new Error('Empty response from server');
                        }

                        try {
                            // Thử parse JSON
                            return JSON.parse(text);
                        } catch (e) {
                            console.error('Failed to parse JSON:', e);
                            throw new Error('Invalid JSON response: ' + text.substring(0, 100));
                        }
                    });
                })
                .then(data => {
                    console.log('Parsed response:', data);

                    if (data.success) {
                        // Cập nhật tổng giá trị sản phẩm
                        const cartItem = form.closest('.cart-item');
                        const itemTotalElement = cartItem ? cartItem.querySelector('.item-total') : null;
                        if (itemTotalElement && data.itemTotal !== undefined) {
                            itemTotalElement.textContent = this.formatPrice(data.itemTotal);
                        }

                        // Sau khi cập nhật giá trị item, tính lại tổng dựa trên các item đang được tick
                        this.updateSelectedTotals();

                        // Cập nhật số lượng sản phẩm trong badge
                        if (data.itemCount !== undefined) {
                            this.updateAllCartBadges(data.itemCount);
                        }

                        // Cập nhật giá trị input
                        const quantityInput = form.querySelector('input[name="quantity"]');
                        if (quantityInput && data.updatedQuantity !== undefined) {
                            quantityInput.value = data.updatedQuantity;
                            // Cập nhật giá trị gốc
                            quantityInput.setAttribute('data-original-value', data.updatedQuantity);
                        }

                        // Hiển thị thông báo thành công
                        this.showNotification(data.message || 'Đã cập nhật số lượng');
                        console.log('Cập nhật giỏ hàng thành công');
                    } else {
                        // Hiển thị thông báo lỗi bằng notification
                        this.showNotification(data.message || 'Có lỗi xảy ra khi cập nhật giỏ hàng', 'error');

                        // Khôi phục giá trị số lượng ban đầu nếu có lỗi và server trả về số lượng hợp lệ
                        if (data.validQuantity !== undefined) {
                            const quantityInput = form.querySelector('input[name="quantity"]');
                            if (quantityInput) {
                                quantityInput.value = data.validQuantity;
                                // Cập nhật giá trị gốc
                                quantityInput.setAttribute('data-original-value', data.validQuantity);
                            }
                        }
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    this.showNotification('Có lỗi xảy ra khi cập nhật giỏ hàng: ' + error.message, 'error');

                    // Khôi phục giá trị số lượng ban đầu từ data-original-value
                    const quantityInput = form.querySelector('input[name="quantity"]');
                    if (quantityInput) {
                        const originalValue = quantityInput.getAttribute('data-original-value');
                        if (originalValue && !isNaN(parseInt(originalValue))) {
                            quantityInput.value = parseInt(originalValue);
                        } else {
                            // Fallback to 1 if original value is not valid
                            quantityInput.value = 1;
                        }
                    }
                });
        }

        /**
         * Xóa sản phẩm khỏi giỏ hàng
         * @param {number} cartItemID - ID của item trong giỏ hàng
         * @param {HTMLElement} form - Form chứa sản phẩm
         */
        removeCartItem(cartItemID, form) {
            console.log(`Xóa sản phẩm: cartItemID=${cartItemID}`);

            // Tạo URL với query parameters thay vì body
            const url = `cart?action=remove&cartItemID=${cartItemID}`;
            console.log('Request URL:', url);

            fetch(url, {
                method: 'POST',
                headers: {
                    'X-Requested-With': 'XMLHttpRequest'
                }
            })
                .then(response => {
                    console.log('Response status:', response.status);
                    console.log('Response headers:', [...response.headers.entries()]);

                    if (!response.ok) {
                        throw new Error(`HTTP error! Status: ${response.status}`);
                    }

                    // Đọc response text trước
                    return response.text().then(text => {
                        console.log('Raw response:', text);

                        // Kiểm tra nếu text rỗng
                        if (!text || text.trim() === '') {
                            throw new Error('Empty response from server');
                        }

                        try {
                            // Thử parse JSON
                            return JSON.parse(text);
                        } catch (e) {
                            console.error('Failed to parse JSON:', e);
                            throw new Error('Invalid JSON response: ' + text.substring(0, 100));
                        }
                    });
                })
                .then(data => {
                    console.log('Parsed response:', data);

                    if (data.success) {
                        // Tìm và xóa phần tử khỏi DOM
                        const cartItemRow = form.closest('.cart-item');
                        if (cartItemRow) {
                            cartItemRow.remove();
                        }

                        // Sau khi xóa item, tính lại tổng dựa trên các item đang được tick
                        this.updateSelectedTotals();

                        // Cập nhật số lượng sản phẩm trong badge trên header
                        if (data.cartCount !== undefined) {
                            console.log('Cập nhật badge trên header với số lượng:', data.cartCount);
                            this.updateAllCartBadges(data.cartCount);
                        }

                        // Hiển thị thông báo thành công
                        this.showNotification('Đã xóa sản phẩm khỏi giỏ hàng', 'success');

                        // Kiểm tra nếu giỏ hàng trống
                        if (data.cartCount === 0) {
                            // Hiển thị thông báo giỏ hàng trống
                            const cartContainer = document.querySelector('.cart-container');
                            if (cartContainer) {
                                cartContainer.innerHTML = `
                                <div class="empty-cart">
                                    <i class="fas fa-shopping-cart fa-4x mb-3 text-muted"></i>
                                    <h5>Giỏ hàng của bạn đang trống</h5>
                                    <p class="text-muted mb-4">Thêm sản phẩm vào giỏ hàng để tiến hành thanh toán</p>
                                    <a href="home" class="btn shop-more-btn">Tiếp tục mua sắm</a>
                                </div>
                            `;
                            }
                        }
                    } else {
                        // Hiển thị thông báo lỗi
                        this.showNotification(data.message || 'Có lỗi xảy ra khi xóa sản phẩm khỏi giỏ hàng', 'error');
                    }
                })
                .catch(error => {
                    console.error('Error:', error);
                    this.showNotification('Có lỗi xảy ra khi xóa sản phẩm khỏi giỏ hàng: ' + error.message, 'error');
                });
        }

        /**
         * Hiển thị thông báo
         * @param {string} message - Nội dung thông báo
         * @param {string} type - Loại thông báo (success, error, warning, info)
         */
        showNotification(message, type = 'success') {
            // Kiểm tra xem đã có container thông báo chưa
            let notificationContainer = document.querySelector('.notification-container');

            if (!notificationContainer) {
                // Tạo container nếu chưa có
                notificationContainer = document.createElement('div');
                notificationContainer.className = 'notification-container';
                notificationContainer.style.cssText = `
                    position: fixed;
                    top: 80px;
                    right: 20px;
                    z-index: 9999;
                `;
                document.body.appendChild(notificationContainer);
            }

            // Tạo thông báo mới
            const notification = document.createElement('div');
            notification.className = `notification notification-${type}`;

            // Thêm icon phù hợp với loại thông báo
            let icon = '';
            switch (type) {
                case 'success':
                    icon = '<i class="fas fa-check-circle" style="margin-right: 8px;"></i>';
                    break;
                case 'error':
                    icon = '<i class="fas fa-exclamation-circle" style="margin-right: 8px;"></i>';
                    break;
                case 'warning':
                    icon = '<i class="fas fa-exclamation-triangle" style="margin-right: 8px;"></i>';
                    break;
                case 'info':
                    icon = '<i class="fas fa-info-circle" style="margin-right: 8px;"></i>';
                    break;
            }

            notification.style.cssText = `
                background-color: ${type === 'error' ? '#f44336' : type === 'warning' ? '#ff9800' : type === 'info' ? '#2196F3' : '#4CAF50'};
                color: white;
                padding: 15px;
                margin-bottom: 10px;
                border-radius: 4px;
                box-shadow: 0 2px 5px rgba(0,0,0,0.2);
                max-width: 300px;
                display: flex;
                align-items: center;
            `;

            notification.innerHTML = icon + message;

            // Thêm nút đóng
            const closeButton = document.createElement('span');
            closeButton.innerHTML = '&times;';
            closeButton.style.cssText = `
                margin-left: 8px;
                cursor: pointer;
                font-size: 20px;
                font-weight: bold;
                margin-left: auto;
            `;
            closeButton.addEventListener('click', () => {
                notification.style.opacity = '0';
                setTimeout(() => {
                    notification.remove();
                }, 300);
            });
            notification.appendChild(closeButton);

            // Thêm vào container
            notificationContainer.appendChild(notification);

            // Hiệu ứng hiển thị
            notification.style.opacity = '0';
            notification.style.transform = 'translateX(50px)';
            notification.style.transition = 'all 0.3s ease';

            setTimeout(() => {
                notification.style.opacity = '1';
                notification.style.transform = 'translateX(0)';
            }, 10);

            // Tự động xóa sau 5 giây
            setTimeout(() => {
                notification.style.opacity = '0';
                notification.style.transform = 'translateX(50px)';
                setTimeout(() => {
                    notification.remove();
                }, 300);
            }, 5000);
        }

        /**
         * Cập nhật số lượng sản phẩm trong badge
         * @param {number} count - Số lượng sản phẩm
         */
        updateCartBadge(count) {
            // Cập nhật tất cả các badge trong header
            const cartLinks = document.querySelectorAll('a[href*="cart"]');

            cartLinks.forEach(cartLink => {
                // Tìm badge trong link giỏ hàng
                let badge = cartLink.querySelector('.badge') ||
                    cartLink.querySelector('.bg-danger') ||
                    cartLink.querySelector('.rounded-pill');

                // Nếu badge đã tồn tại
                if (badge) {
                    badge.textContent = count;
                    badge.style.display = count > 0 ? 'inline-block' : 'none';
                    badge.classList.add('badge-update');

                    // Xóa class animation sau khi hoàn thành
                    setTimeout(() => {
                        badge.classList.remove('badge-update');
                    }, 500);

                    console.log('Đã cập nhật badge giỏ hàng trong header: ' + count);
                }
                // Nếu badge chưa tồn tại và số lượng > 0
                else if (count > 0) {
                    const newBadge = document.createElement('span');
                    newBadge.className = 'badge bg-danger rounded-pill badge-update';
                    newBadge.textContent = count;
                    cartLink.appendChild(newBadge);

                    // Xóa class animation sau khi hoàn thành
                    setTimeout(() => {
                        newBadge.classList.remove('badge-update');
                    }, 500);

                    console.log('Đã tạo badge mới trong header: ' + count);
                }
            });
        }
    }

    // Khởi tạo giỏ hàng khi trang đã sẵn sàng
    document.addEventListener('DOMContentLoaded', () => {
        window.ecomartsCart = new EcomartsCart();
    });
})();