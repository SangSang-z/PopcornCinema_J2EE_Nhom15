const PAYMENT_TX_KEY = "paymentTx";
const LAST_PAID_ORDER_KEY = "lastPaidOrderCode";

document.addEventListener("DOMContentLoaded", async () => {
    const orderCode = resolveOrderCode();

    if (!orderCode) {
        renderInlineError("Không tìm thấy mã đơn hàng. Bạn hãy quay lại bước thanh toán hoặc mở từ mã đơn đã thanh toán.");
        return;
    }

    syncTicketSuccessUrl(orderCode);

    try {
        const data = await loadSuccessInfoWithRetry(orderCode);
        renderSuccessInfo(data);
        cleanupAfterSuccess(orderCode);
    } catch (error) {
        console.error("Lỗi tải thông tin vé:", error);
        renderInlineError("Không tải được thông tin vé. Bạn thử tải lại trang sau vài giây nhé.");
    }
});

function resolveOrderCode() {
    const params = new URLSearchParams(window.location.search);
    const orderCodeFromUrl = params.get("orderCode");
    if (orderCodeFromUrl) {
        return orderCodeFromUrl;
    }

    const lastPaidOrderCode = sessionStorage.getItem(LAST_PAID_ORDER_KEY);
    if (lastPaidOrderCode) {
        return lastPaidOrderCode;
    }

    try {
        const tx = JSON.parse(sessionStorage.getItem(PAYMENT_TX_KEY) || "null");
        return tx?.orderCode || "";
    } catch (error) {
        console.error("Không đọc được paymentTx trong sessionStorage:", error);
        sessionStorage.removeItem(PAYMENT_TX_KEY);
        return "";
    }
}

async function loadSuccessInfoWithRetry(orderCode, maxAttempts = 6, delayMs = 800) {
    let lastError;

    for (let attempt = 1; attempt <= maxAttempts; attempt++) {
        try {
            const res = await fetch(`/api/tickets/success-info?orderCode=${encodeURIComponent(orderCode)}`);
            if (!res.ok) {
                const errorText = await res.text();
                throw new Error(errorText || `HTTP ${res.status}`);
            }

            return await res.json();
        } catch (error) {
            lastError = error;
            if (attempt < maxAttempts) {
                await sleep(delayMs);
            }
        }
    }

    throw lastError;
}

function renderSuccessInfo(data) {
    const poster = document.getElementById("ticket-poster");
    if (poster) {
        poster.src = data.posterUrl || "";
        poster.alt = data.movieTitle ? `Poster ${data.movieTitle}` : "Poster phim";
        poster.onerror = () => {
            poster.style.display = "none";
        };
    }

    document.getElementById("movie-title").textContent = data.movieTitle || "Không rõ tên phim";
    document.getElementById("movie-age-rating").textContent = data.ageRating || "";
    document.getElementById("booking-code").textContent = `Mã đặt vé: ${data.bookingCode || ""}`;
    document.getElementById("cinema-name").textContent = `Rạp: ${data.cinemaName || ""}`;
    document.getElementById("auditorium-name").textContent = `Phòng: ${data.auditoriumName || ""}`;
    document.getElementById("showtime-text").textContent = `Suất: ${data.showtimeText || ""}`;
    document.getElementById("seats-text").textContent = `Ghế: ${data.seatsText || "Chưa có thông tin"}`;
    document.getElementById("combo-text").textContent = `Combo: ${data.comboText || "Không có"}`;
    document.getElementById("seat-total").textContent = `Tiền ghế: ${formatCurrency(data.seatTotal || 0)}`;
    document.getElementById("combo-total").textContent = `Combo: ${formatCurrency(data.comboTotal || 0)}`;
    document.getElementById("discount-total").textContent = `Giảm giá: ${formatCurrency(data.discountAmount || 0)}`;
    document.getElementById("grand-total").textContent = formatCurrency(data.totalAmount || 0);
    document.getElementById("payment-status").textContent = `Trạng thái: ${humanizePaymentStatus(data.paymentStatus || "PAID")}`;
}

function renderInlineError(message) {
    const movieTitle = document.getElementById("movie-title");
    if (movieTitle) {
        movieTitle.textContent = "Không tải được thông tin vé";
    }

    const fields = [
        ["booking-code", "Mã đặt vé"],
        ["cinema-name", "Rạp"],
        ["auditorium-name", "Phòng"],
        ["showtime-text", "Suất"],
        ["seats-text", "Ghế"],
        ["combo-text", "Combo"]
    ];

    fields.forEach(([id, label]) => {
        const element = document.getElementById(id);
        if (element) {
            element.textContent = `${label}: ---`;
        }
    });

    const totalIds = ["seat-total", "combo-total", "discount-total", "grand-total", "payment-status"];
    totalIds.forEach(id => {
        const element = document.getElementById(id);
        if (element) {
            element.textContent = id === "grand-total" ? formatCurrency(0) : "---";
        }
    });

    const noteBox = document.querySelector(".ticket-note, .note-box, .right-note, .booking-note, .checkout-note") || document.querySelector(".ticket-success-right, .ticket-right, .ticket-aside");
    if (noteBox && !document.getElementById("ticket-success-inline-error")) {
        const errorEl = document.createElement("p");
        errorEl.id = "ticket-success-inline-error";
        errorEl.textContent = message;
        errorEl.style.color = "#ffd3d3";
        noteBox.appendChild(errorEl);
    } else if (!noteBox) {
        alert(message);
    }
}

function cleanupAfterSuccess(orderCode) {
    sessionStorage.removeItem(PAYMENT_TX_KEY);
    sessionStorage.setItem(LAST_PAID_ORDER_KEY, orderCode);
}

function syncTicketSuccessUrl(orderCode) {
    const url = new URL(window.location.href);
    if (orderCode && url.searchParams.get("orderCode") !== orderCode) {
        url.searchParams.set("orderCode", orderCode);
        window.history.replaceState({}, "", url);
    }
}

function humanizePaymentStatus(status) {
    const normalized = String(status || "").toUpperCase();
    switch (normalized) {
        case "PAID":
            return "Đã thanh toán";
        case "PENDING":
            return "Đang chờ thanh toán";
        case "CANCELLED":
            return "Đã hủy";
        case "EXPIRED":
            return "Đã hết hạn";
        default:
            return status;
    }
}

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

function formatCurrency(value) {
    return Number(value || 0).toLocaleString("vi-VN") + " đ";
}
