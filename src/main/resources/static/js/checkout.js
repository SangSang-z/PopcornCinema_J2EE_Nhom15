const PAYMENT_TX_KEY = "paymentTx";
function getCurrentUserId() {
    return Number(document.getElementById("app-user")?.value || 0);
}

document.addEventListener("DOMContentLoaded", async () => {
    const showtimeId = new URLSearchParams(window.location.search).get("showtimeId");
    const currentUserId = getCurrentUserId();

    if (!showtimeId) {
        alert("Thiếu showtimeId trên URL");
        return;
    }

    if (!currentUserId) {
        alert("Bạn chưa đăng nhập hoặc không lấy được userId");
        return;
    }

    try {
        await loadSummary(showtimeId);
        await loadPromotions();
    } catch (error) {
        console.error("Lỗi khi tải trang checkout:", error);
        alert("Không tải được dữ liệu checkout");
        return;
    }

    document.getElementById("back-btn").href = `/payment?showtimeId=${showtimeId}`;

    document.getElementById("continue-btn").addEventListener("click", async () => {
        const promotionId = document.getElementById("promotion-select").value || null;

        try {
            sessionStorage.removeItem("paymentTx"); 
            sessionStorage.removeItem("lastPaidOrderCode");
            const response = await fetch(`/api/showtimes/${showtimeId}/payment-transactions`, {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    userId: currentUserId,
                    promotionId: promotionId
                })
            });

            if (!response.ok) {
                const text = await response.text();
                console.error("Create payment transaction failed:", text);
                alert("Không tạo được giao dịch thanh toán");
                return;
            }

            const tx = await response.json();
            sessionStorage.setItem(PAYMENT_TX_KEY, JSON.stringify(tx));
            window.location.href = `/checkout-qr?showtimeId=${showtimeId}&orderCode=${encodeURIComponent(tx.orderCode)}`;
        } catch (error) {
            console.error("Lỗi khi tạo giao dịch:", error);
            alert("Không tạo được giao dịch thanh toán");
        }
    });

    const applyPromoBtn = document.getElementById("apply-promo-btn");
    if (applyPromoBtn) {
        applyPromoBtn.addEventListener("click", async () => {
            try {
                await loadSummary(showtimeId);
            } catch (error) {
                console.error("Lỗi khi áp dụng khuyến mãi:", error);
                alert("Không áp dụng được khuyến mãi");
            }
        });
    }
});

async function loadSummary(showtimeId) {
     const currentUserId = getCurrentUserId();
    const promotionId = document.getElementById("promotion-select")?.value || "";
    const url = promotionId
        ? `/api/showtimes/${showtimeId}/checkout-summary?userId=${currentUserId}&promotionId=${promotionId}`
        : `/api/showtimes/${showtimeId}/checkout-summary?userId=${currentUserId}`;

    const res = await fetch(url);

    if (!res.ok) {
        const text = await res.text();
        console.error("checkout-summary failed:", text);
        throw new Error("checkout-summary failed");
    }

    const data = await res.json();

    document.getElementById("ticket-title").textContent = data.movieTitle || "";
    document.getElementById("ticket-age-rating").textContent = data.ageRating || "";
    document.getElementById("ticket-poster").src = data.posterUrl || "";
    document.getElementById("ticket-cinema").textContent = `Rạp: ${data.cinemaName || ""}`;
    document.getElementById("ticket-auditorium").textContent = `Phòng: ${data.auditoriumName || ""}`;
    document.getElementById("ticket-datetime").textContent = `Suất: ${data.startTimeText || ""}`;
    document.getElementById("ticket-seats").textContent = `Ghế: ${data.seatsText || ""}`;
    document.getElementById("ticket-seat-total").textContent = `Tiền ghế: ${formatCurrency(data.seatTotal || 0)}`;
    document.getElementById("ticket-combo-total").textContent = `Combo: ${formatCurrency(data.comboTotal || 0)}`;
    document.getElementById("ticket-discount").textContent = `Giảm giá: ${formatCurrency(data.discountAmount || 0)}`;
    document.getElementById("ticket-total").textContent = formatCurrency(data.totalAmount || 0);
}

async function loadPromotions() {
    const res = await fetch(`/api/promotions/active`);

    if (!res.ok) {
        const text = await res.text();
        console.error("promotions failed:", text);
        throw new Error("promotions failed");
    }

    const promotions = await res.json();
    const select = document.getElementById("promotion-select");
    if (!select) return;

    promotions.forEach(p => {
        const option = document.createElement("option");
        option.value = p.id;

        const label = p.code
            ? `${p.code} - ${p.title || ""}`
            : (p.title || "Khuyến mãi");

        option.textContent = label;
        select.appendChild(option);
    });
}

function formatCurrency(value) {
    return Number(value).toLocaleString("vi-VN") + " đ";
}