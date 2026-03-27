
const HOLD_EXPIRES_AT_KEY = "holdExpiresAt";
const SELECTED_SEATS_KEY = "selectedSeatsData";
const SEAT_TOTAL_KEY = "seatTotal";
const CURRENT_SHOWTIME_KEY = "currentBookingShowtimeId";
const COMBO_TOTAL_KEY = "comboTotal";
const GRAND_TOTAL_KEY = "grandTotal";

let comboData = [];
let comboPageData = null;
let holdExpiresAt = null;

function getCurrentUserId() {
    return Number(document.getElementById("app-user")?.value || 0);
}

document.addEventListener("DOMContentLoaded", async () => {
    const showtimeId = getShowtimeIdFromUrl();
    const currentUserId = getCurrentUserId();

    console.log("payment page showtimeId =", showtimeId);
    console.log("current url =", window.location.href);

    if (!showtimeId) {
        alert("Thiếu showtimeId");
        return;
    }

        if (!currentUserId) {
        alert("Không lấy được userId, vui lòng đăng nhập lại");
        return;
    }

    try {
        const [seatMapResponse, comboResponse, selectedComboResponse] = await Promise.all([
            fetch(`/api/showtimes/${showtimeId}/seat-map?userId=${currentUserId}`),
            fetch(`/api/combos`),
            fetch(`/api/showtimes/${showtimeId}/booking-combos?userId=${currentUserId}`)
        ]);

        if (!seatMapResponse.ok) throw new Error("Không tải được thông tin vé");
        if (!comboResponse.ok) throw new Error("Không tải được danh sách combo");
        if (!selectedComboResponse.ok) throw new Error("Không tải được combo đã chọn");

        comboPageData = await seatMapResponse.json();

        const combosFromApi = await comboResponse.json();
        const selectedCombos = await selectedComboResponse.json();
        const selectedMap = new Map(selectedCombos.map(item => [item.comboId, item.quantity]));

        comboData = combosFromApi.map(item => ({
            ...item,
            quantity: selectedMap.get(item.id) || 0
        }));

        renderTicketInfo(comboPageData);
        renderComboList();
        bindActionLinks(showtimeId);
        holdExpiresAt = Number(sessionStorage.getItem(HOLD_EXPIRES_AT_KEY) || 0);
        if (!holdExpiresAt || holdExpiresAt <= Date.now()) {
            alert("Phiên giữ ghế đã hết hạn");
            window.location.href = "/movie-detail?id=1";
            return;
        }
        startCountdown();
    } catch (error) {
        console.error(error);
        alert("Đã xảy ra lỗi khi tải trang bắp nước");
    }
});

function getShowtimeIdFromUrl() {
    const params = new URLSearchParams(window.location.search);
    return params.get("showtimeId");
}

function renderTicketInfo(data) {
    const poster = document.getElementById("ticket-poster");
    const title = document.getElementById("ticket-title");
    const ageRating = document.getElementById("ticket-age-rating");
    const cinema = document.getElementById("ticket-cinema");
    const auditorium = document.getElementById("ticket-auditorium");
    const datetime = document.getElementById("ticket-datetime");
    const seats = document.getElementById("ticket-seats");

    const selectedSeats = JSON.parse(sessionStorage.getItem(SELECTED_SEATS_KEY) || "[]");
    const seatTotal = Number(sessionStorage.getItem(SEAT_TOTAL_KEY) || "0");

    if (poster) poster.src = data.posterUrl || "";
    if (title) title.textContent = data.movieTitle || "";
    if (ageRating) ageRating.textContent = data.ageRating || "";
    if (cinema) cinema.textContent = `Rạp: ${data.cinemaName || ""}`;
    if (auditorium) auditorium.textContent = `Phòng: ${data.auditoriumName || ""}`;
    if (datetime) datetime.textContent = `Suất: ${formatDateTime(data.startTime)} - ${formatHourMinute(data.endTime)}`;
    if (seats) seats.textContent = `Ghế: ${selectedSeats.length ? selectedSeats.map(s => s.label).join(", ") : "Chưa chọn"}`;

    updateSummary(seatTotal);
}

function renderComboList() {
    const comboList = document.getElementById("combo-list");
    if (!comboList) return;

    comboList.innerHTML = "";

    comboData.forEach(combo => {
        const row = document.createElement("div");
        row.className = "combo-row";
        row.innerHTML = `
            <div>
                <div class="combo-name">${combo.name}</div>
                <div class="combo-desc">${combo.description}</div>
            </div>
            <div class="combo-price">${formatCurrency(combo.price)}</div>
            <div class="combo-qty">
                <button type="button" class="qty-btn minus" data-id="${combo.id}">-</button>
                <span class="qty-value" id="qty-${combo.id}">${combo.quantity}</span>
                <button type="button" class="qty-btn plus" data-id="${combo.id}">+</button>
            </div>
        `;
        comboList.appendChild(row);
    });

    bindQtyEvents();
}

function bindQtyEvents() {
    document.querySelectorAll(".qty-btn.minus").forEach(btn => {
        btn.addEventListener("click", () => changeQuantity(Number(btn.dataset.id), -1));
    });

    document.querySelectorAll(".qty-btn.plus").forEach(btn => {
        btn.addEventListener("click", () => changeQuantity(Number(btn.dataset.id), 1));
    });
}

async function changeQuantity(comboId, delta) {
    const currentUserId = getCurrentUserId();
    const combo = comboData.find(item => item.id === comboId);
    if (!combo) return;

    combo.quantity = Math.max(0, combo.quantity + delta);

    const qtyEl = document.getElementById(`qty-${combo.id}`);
    if (qtyEl) qtyEl.textContent = combo.quantity;

    await saveComboSelectionToBackend();
    updateSummary(Number(sessionStorage.getItem("seatTotal") || "0"));
}

function saveComboSelection() {
    const selectedCombos = comboData
        .filter(item => item.quantity > 0)
        .map(item => ({
            id: item.id,
            name: item.name,
            price: item.price,
            quantity: item.quantity
        }));

    sessionStorage.setItem("selectedCombos", JSON.stringify(selectedCombos));
}

function updateSummary(seatTotal) {
    const comboTotal = comboData.reduce((sum, item) => sum + item.price * item.quantity, 0);
    const total = seatTotal + comboTotal;

    const seatTotalEl = document.getElementById("ticket-seat-total");
    const comboTotalEl = document.getElementById("ticket-combo-total");
    const totalEl = document.getElementById("ticket-total");

    if (seatTotalEl) seatTotalEl.textContent = `Tiền ghế: ${formatCurrency(seatTotal)}`;
    if (comboTotalEl) comboTotalEl.textContent = `Combo: ${formatCurrency(comboTotal)}`;
    if (totalEl) totalEl.textContent = formatCurrency(total);

    sessionStorage.setItem("comboTotal", comboTotal);
    sessionStorage.setItem("grandTotal", total);
}

function bindActionLinks(showtimeId) {
    console.log("bindActionLinks showtimeId =", showtimeId);

    const backBtn = document.getElementById("back-btn");
    const nextBtn = document.getElementById("next-btn");

    console.log("backBtn =", backBtn);
    console.log("nextBtn =", nextBtn);

    if (backBtn) {
        backBtn.addEventListener("click", (e) => {
            e.preventDefault();
            console.log("click back, showtimeId =", showtimeId);
            window.location.href = `/seats?showtimeId=${showtimeId}`;
        });
    }

    if (nextBtn) {
        nextBtn.addEventListener("click", (e) => {
            e.preventDefault();
            console.log("click next, showtimeId =", showtimeId);
            console.log("target url =", `/checkout?showtimeId=${showtimeId}`);
            window.location.href = `/checkout?showtimeId=${showtimeId}`;
        });
    }
}

function startCountdown() {
    const countdownEl = document.getElementById("countdown");
    if (!countdownEl) return;

    const renderCountdown = () => {
        const diff = holdExpiresAt - Date.now();
        const remainingSeconds = Math.floor(diff / 1000);

        if (remainingSeconds <= 0) {
            clearBookingSession();
            alert("Hết thời gian giữ ghế");
            window.location.href = "/movie-detail?id=1";
            return false;
        }

        const minutes = String(Math.floor(remainingSeconds / 60)).padStart(2, "0");
        const seconds = String(remainingSeconds % 60).padStart(2, "0");
        countdownEl.textContent = `${minutes}:${seconds}`;
        return true;
    };

    if (!renderCountdown()) return;

    const interval = setInterval(() => {
        if (!renderCountdown()) {
            clearInterval(interval);
        }
    }, 1000);
}

function formatCurrency(value) {
    return Number(value).toLocaleString("vi-VN") + " đ";
}

function formatDateTime(dateTimeString) {
    if (!dateTimeString) return "";
    const date = new Date(dateTimeString);
    return date.toLocaleString("vi-VN", {
        hour: "2-digit",
        minute: "2-digit",
        day: "2-digit",
        month: "2-digit",
        year: "numeric"
    });
}

function formatHourMinute(dateTimeString) {
    if (!dateTimeString) return "";
    const date = new Date(dateTimeString);
    return date.toLocaleTimeString("vi-VN", {
        hour: "2-digit",
        minute: "2-digit",
        hour12: false
    });
}

async function saveComboSelectionToBackend() {
    const currentUserId = getCurrentUserId();
    const showtimeId = getShowtimeIdFromUrl();

    const items = comboData
        .filter(item => item.quantity > 0)
        .map(item => ({
            comboId: item.id,
            quantity: item.quantity
        }));

    try {
        const response = await fetch(`/api/showtimes/${showtimeId}/booking-combos`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                userId: currentUserId,
                items: items
            })
        });

        if (!response.ok) {
            throw new Error("Không lưu được combo");
        }
    } catch (error) {
        console.error(error);
        alert("Lưu combo thất bại");
    }
}

function clearBookingSession() {
    sessionStorage.removeItem(HOLD_EXPIRES_AT_KEY);
    sessionStorage.removeItem(SELECTED_SEATS_KEY);
    sessionStorage.removeItem(SEAT_TOTAL_KEY);
    sessionStorage.removeItem(COMBO_TOTAL_KEY);
    sessionStorage.removeItem(GRAND_TOTAL_KEY);
    sessionStorage.removeItem(CURRENT_SHOWTIME_KEY);
}

async function leaveBookingFlow() {
    const showtimeId = sessionStorage.getItem(CURRENT_SHOWTIME_KEY) || getShowtimeIdFromUrl();
    const selectedSeats = JSON.parse(sessionStorage.getItem(SELECTED_SEATS_KEY) || "[]");

    try {
        if (selectedSeats.length) {
            await fetch(`/api/showtimes/${showtimeId}/hold-seats`, {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    userId: currentUserId,
                    seatIds: selectedSeats.map(s => Number(s.id))
                })
            });
        }

        await fetch(`/api/showtimes/${showtimeId}/booking-combos?userId=${currentUserId}`, {
            method: "DELETE"
        });
    } catch (error) {
        console.error("Lỗi khi thoát flow đặt vé:", error);
    } finally {
        clearBookingSession();
    }
}