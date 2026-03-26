const HOLD_EXPIRES_AT_KEY = "holdExpiresAt";
const SELECTED_SEATS_KEY = "selectedSeatsData";
const SEAT_TOTAL_KEY = "seatTotal";
const CURRENT_SHOWTIME_KEY = "currentBookingShowtimeId";
const COMBO_TOTAL_KEY = "comboTotal";
const GRAND_TOTAL_KEY = "grandTotal";

//test hold ghế
const currentUserId = 1;
//const currentUserId = 2;

let selectedSeats = [];
let seatPageData = null;
let holdExpiresAt = null;

document.addEventListener("DOMContentLoaded", async () => {
    const showtimeId = getShowtimeIdFromUrl();
    sessionStorage.setItem(CURRENT_SHOWTIME_KEY, String(showtimeId));

    if (!showtimeId) {
        alert("Thiếu showtimeId");
        return;
    }

    try {
        const response = await fetch(`/api/showtimes/${showtimeId}/seat-map?userId=${currentUserId}`);
        if (!response.ok) {
            throw new Error("Không tải được sơ đồ ghế");
        }

        seatPageData = await response.json();

        renderTicketInfo(seatPageData);
        renderSeatMap(seatPageData.seats || []);
        restoreSelectedSeatsFromSession();
        holdExpiresAt = initHoldTimer();
        startCountdown();

        const continueBtn = document.getElementById("continue-btn");
        if (continueBtn) {
            continueBtn.addEventListener("click", () => {
                if (!selectedSeats.length) {
                    alert("Vui lòng chọn ít nhất 1 ghế");
                    return;
                }

                persistSelectedSeats();
                window.location.href = `/payment?showtimeId=${showtimeId}`;
            });
        }

        const exitBookingBtn = document.getElementById("exit-booking-btn");
        if (exitBookingBtn) {
            exitBookingBtn.addEventListener("click", async (e) => {
                e.preventDefault();
                await leaveBookingFlow();
                window.location.href = "/movie-detail?id=1";
            });
        }

    } catch (error) {
        console.error(error);
        alert("Đã xảy ra lỗi khi tải trang chọn ghế");
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

    if (poster) poster.src = data.posterUrl || "";
    if (title) title.textContent = data.movieTitle || "";
    if (ageRating) ageRating.textContent = data.ageRating || "";
    if (cinema) cinema.textContent = `Rạp: ${data.cinemaName || ""}`;
    if (auditorium) auditorium.textContent = `Phòng: ${data.auditoriumName || ""}`;
    if (datetime) {
        datetime.textContent = `Suất: ${formatDateTime(data.startTime)} - ${formatHourMinute(data.endTime)}`;
    }

    updateSelectedSummary();
}

function renderSeatMap(seats) {
    const seatMap = document.getElementById("seat-map");
    if (!seatMap) return;

    seatMap.innerHTML = "";

    const grouped = groupSeatsByRow(seats);
    const rowKeys = Object.keys(grouped).sort();
    const lastRowKey = rowKeys[rowKeys.length - 1];

    rowKeys.forEach(rowKey => {
        const row = grouped[rowKey].sort((a, b) => a.seatNumber - b.seatNumber);
        const isCoupleRow = rowKey === lastRowKey;

        const rowEl = document.createElement("div");
        rowEl.className = "seat-row";

        const label = document.createElement("div");
        label.className = "row-label";
        label.textContent = rowKey;

        const seatsWrap = document.createElement("div");
        seatsWrap.className = `row-seats ${isCoupleRow ? "couple-row-wrap" : ""}`;

        if (isCoupleRow) {
            const coupleWrap = document.createElement("div");
            coupleWrap.className = "couple-row";

            row.forEach(seat => {
                coupleWrap.appendChild(createSeatButton(seat, true));
            });

            seatsWrap.appendChild(coupleWrap);
        } else {
            const leftWrap = document.createElement("div");
            leftWrap.className = "seat-group";

            const aisle = document.createElement("div");
            aisle.className = "seat-aisle";

            const rightWrap = document.createElement("div");
            rightWrap.className = "seat-group";

            const splitIndex = Math.ceil(row.length / 2);
            const leftSeats = row.slice(0, splitIndex);
            const rightSeats = row.slice(splitIndex);

            leftSeats.forEach(seat => leftWrap.appendChild(createSeatButton(seat, false)));
            rightSeats.forEach(seat => rightWrap.appendChild(createSeatButton(seat, false)));

            seatsWrap.appendChild(leftWrap);
            seatsWrap.appendChild(aisle);
            seatsWrap.appendChild(rightWrap);
        }

        rowEl.appendChild(label);
        rowEl.appendChild(seatsWrap);
        seatMap.appendChild(rowEl);
    });
}

function createSeatButton(seat, isCoupleRow) {
    const btn = document.createElement("button");

    const isVip = seat.seatType === "VIP";
    const isBlocked = seat.sold || seat.held; // held sẽ hiển thị như sold
    const statusClass = isBlocked ? "sold" : "available";
    const vipClass = isVip ? "vip" : "";
    const coupleClass = isCoupleRow ? "couple" : "";

    btn.className = `seat ${statusClass} ${vipClass} ${coupleClass}`.trim();
    btn.textContent = seat.seatNumber;

    btn.dataset.id = seat.seatId;
    btn.dataset.label = isCoupleRow
        ? `${seat.seatRow}${seat.seatNumber} (đôi)`
        : `${seat.seatRow}${seat.seatNumber}`;
    btn.dataset.price = seat.finalPrice || 0;
    btn.dataset.seatType = seat.seatType || "";

    if (!isBlocked) {
        btn.addEventListener("click", () => toggleSeat(btn));
    }

    return btn;
}

async function toggleSeat(button) {
    const seatId = Number(button.dataset.id);
    const existingIndex = selectedSeats.findIndex(s => s.id === String(seatId));
    const showtimeId = getShowtimeIdFromUrl();

    try {
        if (existingIndex >= 0) {
            const response = await fetch(`/api/showtimes/${showtimeId}/hold-seats`, {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    userId: currentUserId,
                    seatIds: [seatId]
                })
            });

            if (!response.ok) {
                throw new Error("Không thể bỏ giữ ghế");
            }

            selectedSeats.splice(existingIndex, 1);
            button.classList.remove("selected");
            button.classList.add("available");
        } else {
            const response = await fetch(`/api/showtimes/${showtimeId}/hold-seats`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    userId: currentUserId,
                    seatIds: [seatId]
                })
            });

            if (!response.ok) {
                throw new Error("Ghế đang được người khác giữ");
            }

            selectedSeats.push({
                id: String(seatId),
                label: button.dataset.label,
                price: Number(button.dataset.price),
                seatType: button.dataset.seatType
            });

            button.classList.remove("available");
            button.classList.add("selected");
        }

        updateSelectedSummary();
        persistSelectedSeats();
    } catch (error) {
        alert(error.message);
        await reloadSeatMap();
    }
}

function updateSelectedSummary() {
    const seatsText = selectedSeats.length
        ? selectedSeats.map(s => s.label).join(", ")
        : "Chưa chọn";

    const total = selectedSeats.reduce((sum, seat) => sum + seat.price, 0);

    const ticketSeats = document.getElementById("ticket-seats");
    const ticketPrice = document.getElementById("ticket-price");
    const ticketTotal = document.getElementById("ticket-total");
    const continueBtn = document.getElementById("continue-btn");

    if (ticketSeats) ticketSeats.textContent = `Ghế: ${seatsText}`;
    if (ticketPrice) ticketPrice.textContent = `Giá ghế: ${formatCurrency(seatPageData?.basePrice || 0)}`;
    if (ticketTotal) ticketTotal.textContent = formatCurrency(total);
    if (continueBtn) continueBtn.disabled = selectedSeats.length === 0;
}

function groupSeatsByRow(seats) {
    return seats.reduce((acc, seat) => {
        if (!acc[seat.seatRow]) {
            acc[seat.seatRow] = [];
        }
        acc[seat.seatRow].push(seat);
        return acc;
    }, {});
}

function startCountdown() {
    const countdownEl = document.getElementById("countdown");
    if (!countdownEl) return;

    const renderCountdown = () => {
        const diff = holdExpiresAt - Date.now();
        const remainingSeconds = Math.floor(diff / 1000);

        if (remainingSeconds <= 0) {
            releaseAllHeldSeats().finally(() => {
                clearBookingSession();
                alert("Hết thời gian giữ ghế");
                window.location.href = "/movie-detail?id=1";
            });
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

async function reloadSeatMap() {
    const showtimeId = getShowtimeIdFromUrl();

    const response = await fetch(`/api/showtimes/${showtimeId}/seat-map?userId=${currentUserId}`);
    if (!response.ok) return;

    seatPageData = await response.json();
    renderSeatMap(seatPageData.seats || []);
}

async function releaseAllHeldSeats() {
    const showtimeId = getShowtimeIdFromUrl();
    if (!selectedSeats.length) return;

    try {
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
    } catch (error) {
        console.error("Lỗi khi nhả ghế:", error);
    }
}

function initHoldTimer() {
    const existing = sessionStorage.getItem(HOLD_EXPIRES_AT_KEY);

    if (existing) {
        return Number(existing);
    }

    const expiresAt = Date.now() + 5 * 60 * 1000;
    sessionStorage.setItem(HOLD_EXPIRES_AT_KEY, String(expiresAt));
    return expiresAt;
}

function persistSelectedSeats() {
    sessionStorage.setItem(SELECTED_SEATS_KEY, JSON.stringify(selectedSeats));
    sessionStorage.setItem(
        SEAT_TOTAL_KEY,
        String(selectedSeats.reduce((sum, seat) => sum + seat.price, 0))
    );
}

function restoreSelectedSeatsFromSession() {
    const saved = JSON.parse(sessionStorage.getItem(SELECTED_SEATS_KEY) || "[]");
    if (!saved.length) return;

    selectedSeats = [];

    saved.forEach(savedSeat => {
        const btn = document.querySelector(`.seat[data-id="${savedSeat.id}"]`);
        if (!btn) return;

        if (btn.classList.contains("sold")) return;

        btn.classList.remove("available");
        btn.classList.add("selected");

        selectedSeats.push({
            id: String(savedSeat.id),
            label: savedSeat.label,
            price: Number(savedSeat.price),
            seatType: savedSeat.seatType
        });
    });

    updateSelectedSummary();
}

async function leaveBookingFlow() {
    const currentShowtimeId = sessionStorage.getItem(CURRENT_SHOWTIME_KEY) || getShowtimeIdFromUrl();

    try {
        if (selectedSeats.length) {
            await fetch(`/api/showtimes/${currentShowtimeId}/hold-seats`, {
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

        await fetch(`/api/showtimes/${currentShowtimeId}/booking-combos?userId=${currentUserId}`, {
            method: "DELETE"
        });
    } catch (error) {
        console.error("Lỗi khi thoát flow đặt vé:", error);
    } finally {
        clearBookingSession();
    }
}