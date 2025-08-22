<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta charset="UTF-8">
        <title>Voucher của tôi</title>
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <style>
            :root{
                --bg:#fff6ec;
                --card:#fff;
                --ink:#1a1a1a;
                --muted:#6b5b45;
                --brand:#d2b48c;   /* tan */
                --brand-2:#c3a476;
                --line:#e8d9c3;
                --ok:#0f766e;
                --danger:#b91c1c;
            }
            *{
                box-sizing:border-box
            }
            html,body{
                margin:0;
                padding:0
            }
            body{
                color:var(--ink);
                background:
                    radial-gradient(1200px 600px at 10% -10%, #fff 20%, transparent 60%),
                    radial-gradient(1200px 600px at 110% 10%, #fff 20%, transparent 60%),
                    var(--bg);
                font-family: ui-sans-serif, system-ui, -apple-system, "Segoe UI", Roboto, Arial, sans-serif;
                line-height:1.5;
            }
            .container{
                max-width:1080px;
                margin:32px auto;
                padding:0 16px;
            }

            /* Header bar */
            .bar{
                display:flex;
                align-items:center;
                gap:12px;
                flex-wrap:wrap;
                background:linear-gradient(180deg,#fff, #fffaf3);
                border:1px solid var(--line);
                border-radius:16px;
                padding:16px;
                box-shadow:0 10px 30px rgba(0,0,0,.06);
            }
            .back{
                display:inline-flex;
                align-items:center;
                gap:8px;
                padding:10px 14px;
                border:1px solid var(--line);
                background:var(--card);
                border-radius:10px;
                text-decoration:none;
                color:var(--ink);
                font-weight:600;
                transition:.2s transform,.2s background;
            }
            .back:hover{
                transform:translateY(-1px);
                background:#fffdf8
            }
            .title{
                display:flex;
                align-items:center;
                gap:10px;
                margin-left:auto;
                flex:1;
            }
            .title h1{
                font-size:20px;
                margin:0;
                font-weight:800;
                letter-spacing:.2px;
            }
            .count{
                font-size:13px;
                color:var(--muted);
                background:#fff;
                border:1px solid var(--line);
                padding:4px 8px;
                border-radius:999px;
            }
            .search{
                display:flex;
                align-items:center;
                gap:8px;
                background:#fff;
                border:1px solid var(--line);
                border-radius:12px;
                padding:10px 12px;
                min-width:260px;
            }
            .search input{
                border:none;
                outline:none;
                background:transparent;
                width:100%;
                font-size:14px;
                color:var(--ink);
            }

            /* Table card */
            .card{
                background:var(--card);
                border:1px solid var(--line);
                border-radius:16px;
                margin-top:18px;
                box-shadow:0 16px 40px rgba(0,0,0,.07);
                overflow:hidden;
            }
            .table-wrap{
                width:100%;
                overflow:auto
            }
            table{
                width:100%;
                border-collapse:separate;
                border-spacing:0;
                min-width:760px;
            }
            thead th{
                position:sticky;
                top:0;
                z-index:1;
                background:linear-gradient(180deg,#f3e8d7,#ecd9bf);
                color:#000;
                text-align:left;
                font-size:13px;
                letter-spacing:.3px;
                padding:12px 16px;
                border-bottom:1px solid var(--line);
            }
            tbody td{
                padding:14px 16px;
                border-bottom:1px solid #f1e7d6;
                background:#fff;
                vertical-align:middle;
            }
            tbody tr:hover td{
                background:#fffdf7
            }
            .code{
                display:flex;
                align-items:center;
                gap:10px;
                font-family: ui-monospace, SFMono-Regular, Menlo, Consolas, monospace;
                font-weight:700;
            }
            .copy{
                border:1px solid var(--line);
                background:#fff;
                border-radius:8px;
                padding:6px 8px;
                font-size:12px;
                cursor:pointer;
                transition:.15s transform, .15s background;
            }
            .copy:hover{
                transform:translateY(-1px);
                background:#fffaf2
            }
            .discount{
                font-weight:800
            }
            .currency{
                opacity:.8;
                font-weight:600
            }
            .desc{
                color:#3d3428
            }

            /* Badges */
            .badge{
                display:inline-flex;
                align-items:center;
                gap:6px;
                padding:6px 10px;
                border-radius:999px;
                font-size:12px;
                font-weight:700;
                letter-spacing:.2px;
                border:1px solid;
            }
            .badge.ok{
                color:var(--ok);
                border-color:rgba(15,118,110,.25);
                background:#ecfdf5
            }
            .badge.bad{
                color:var(--danger);
                border-color:rgba(185,28,28,.25);
                background:#fef2f2
            }

            /* Empty state */
            .empty{
                text-align:center;
                padding:40px 24px;
                color:var(--muted);
            }
            .empty .emo{
                font-size:44px;
            }
            .empty p{
                margin:10px 0 0 0
            }

            /* Footer button (mobile helper) */
            .footer-back{
                display:none;
                margin-top:16px;
                text-align:center;
            }
            @media (max-width:640px){
                .footer-back{
                    display:block
                }
                .title{
                    order:3;
                    width:100%;
                    margin-left:0
                }
                .search{
                    flex:1
                }
            }

            /* Tiny toast */
            .toast{
                position:fixed;
                left:50%;
                bottom:24px;
                transform:translateX(-50%);
                background:#111;
                color:#fff;
                padding:10px 14px;
                border-radius:10px;
                box-shadow:0 10px 20px rgba(0,0,0,.25);
                font-size:14px;
                opacity:0;
                pointer-events:none;
                transition:opacity .25s, transform .25s;
            }
            .toast.show{
                opacity:1;
                transform:translateX(-50%) translateY(-6px)
            }
        </style>
    </head>
    <body>
        <div class="container">

            <div class="bar">
                <a class="back" href="${pageContext.request.contextPath}/home" aria-label="Quay lại trang chủ">
                    <!-- inline chevron -->
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" aria-hidden="true"><path d="M15 18l-6-6 6-6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/></svg>
                    Trang chủ
                </a>

                <div class="title">
                    <h1>Voucher của bạn</h1>
                    <c:if test="${not empty vouchers}">
                        <span class="count">${fn:length(vouchers)} voucher</span>
                    </c:if>
                </div>

                <div class="search" role="search">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" aria-hidden="true"><path d="M21 21l-4.35-4.35M10.5 18a7.5 7.5 0 1 1 0-15 7.5 7.5 0 0 1 0 15Z" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>
                    <input id="filter" type="text" placeholder="Tìm voucher theo mã, mô tả..." oninput="filterRows()">
                </div>
            </div>

            <c:if test="${empty vouchers}">
                <div class="card">
                    <div class="empty">
                        <div class="emo">🎟️</div>
                        <h3>Chưa có voucher nào</h3>
                        <p>Hãy quay lại cửa hàng để săn ưu đãi bạn nhé!</p>
                    </div>
                </div>
                <div class="footer-back">
                    <a class="back" href="${pageContext.request.contextPath}/home">← Quay lại trang chủ</a>
                </div>
            </c:if>

            <c:if test="${not empty vouchers}">
                <div class="card">
                    <div class="table-wrap">
                        <table aria-describedby="list-voucher">
                            <thead>
                                <tr>
                                    <th style="min-width:220px">Mã Voucher</th>
                                    <th style="min-width:160px">Giảm giá</th>
                                    <th style="min-width:200px">Hạn sử dụng</th>
                                    <th>Mô tả</th>
                                    <th style="min-width:140px">Đơn hàng tối thiểu</th>
                                </tr>
                            </thead>
                            <tbody id="tbody">
                                <c:forEach items="${vouchers}" var="v">
                                    <tr>
                                        <td>
                                            <span class="code">
                                                <span>${v.voucherCode}</span>
                                                <button class="copy" type="button" onclick="copyCode(this)" data-code="${v.voucherCode}" aria-label="Copy mã"></button>
                                            </span>
                                        </td>
                                        <td>
                                            <span class="discount">
                                                <fmt:formatNumber value="${v.discountAmount}" type="number" groupingUsed="true"/>
                                            </span>
                                            <span class="currency">₫</span>
                                        </td>
                                        <td class="end-date">
                                            <!-- Hiển thị nguyên văn ngày từ server -->
                                            ${v.endDate}
                                        </td>
                                        <td class="desc">${v.description}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty v.minOrderValue}">
                                                    <span class="discount">
                                                        <fmt:formatNumber value="${v.minOrderValue}"
                                                                          type="number"
                                                                          groupingUsed="true"
                                                                          minFractionDigits="0"
                                                                          maxFractionDigits="0"/>
                                                    </span>
                                                    <span class="currency">₫</span>
                                                </c:when>
                                                <c:otherwise>—</c:otherwise>
                                            </c:choose>
                                        </td>


                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </div>
                <div class="footer-back">
                    <a class="back" href="${pageContext.request.contextPath}/home">← Trang chủ</a>
                </div>
            </c:if>
        </div>

        <!-- Toast -->
        <div id="toast" class="toast" role="status" aria-live="polite"></div>

        <script>
            // Copy voucher code
            function copyCode(btn) {
                const code = btn.dataset.code || btn.previousElementSibling?.textContent?.trim();
                if (!code)
                    return;
                navigator.clipboard.writeText(code).then(() => {
                    showToast("Đã copy mã: " + code);
                });
            }

            // Filter rows by code/description
            function filterRows() {
                const q = (document.getElementById('filter').value || '').toLowerCase().trim();
                const rows = document.querySelectorAll('#tbody tr');
                rows.forEach(r => {
                    const code = r.querySelector('.code')?.innerText?.toLowerCase() || '';
                    const desc = r.querySelector('.desc')?.innerText?.toLowerCase() || '';
                    r.style.display = (code.includes(q) || desc.includes(q)) ? '' : 'none';
                });
            }

            // Badge status by end date (client-side check)
            function updateStatuses() {
                const now = new Date();
                document.querySelectorAll('#tbody tr').forEach(tr => {
                    const cell = tr.querySelector('.end-date');
                    const badge = tr.querySelector('[data-status]');
                    if (!cell || !badge)
                        return;

                    // Try parse many formats gracefully
                    const raw = (cell.textContent || '').trim()
                            .replace(/\./g, '/')        // 31.12.2025 -> 31/12/2025
                            .replace('T', ' ');         // ISO -> space
                    const d = new Date(raw);

                    // If cannot parse, just hide badge
                    if (isNaN(d.getTime())) {
                        badge.textContent = '—';
                        badge.className = 'badge';
                        return;
                    }

                    if (d >= now) {
                        badge.textContent = 'Còn hạn';
                        badge.className = 'badge ok';
                    } else {
                        badge.textContent = 'Hết hạn';
                        badge.className = 'badge bad';
                    }
                });
            }

            function showToast(msg) {
                const t = document.getElementById('toast');
                t.textContent = msg;
                t.classList.add('show');
                clearTimeout(window.__toastTimer);
                window.__toastTimer = setTimeout(() => t.classList.remove('show'), 1600);
            }

            // Init after DOM ready
            (function () {
                updateStatuses();
                // re-check status every midnight tick (approx)
                setInterval(updateStatuses, 60 * 60 * 1000);
            })();
        </script>
    </body>
</html>
