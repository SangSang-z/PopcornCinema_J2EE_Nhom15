let movieDetailData = null;
let selectedCity = "";
let selectedCinemaId = "";
let selectedDate = null;

let allShowDates = [];
let visibleStartIndex = 0;
const VISIBLE_DAYS = 5;

const currentUserId = (() => {
    const raw = document.getElementById("app-user")?.value;
    const parsed = Number(raw);
    return Number.isFinite(parsed) && parsed > 0 ? parsed : null;
})();
const HOLD_EXPIRES_AT_KEY = "holdExpiresAt";
const SELECTED_SEATS_KEY = "selectedSeatsData";
const SEAT_TOTAL_KEY = "seatTotal";
const CURRENT_SHOWTIME_KEY = "currentBookingShowtimeId";
const COMBO_TOTAL_KEY = "comboTotal";
const GRAND_TOTAL_KEY = "grandTotal";

document.addEventListener("DOMContentLoaded", async () => {
    const movieId = getMovieIdFromUrl() || 1;

    try {
        const response = await fetch(`/api/movies/${movieId}/detail`);

        if (!response.ok) {
            throw new Error("Không lấy được dữ liệu phim");
        }

        const movie = await response.json();
        movieDetailData = movie;

        renderMovieInfo(movie);
        renderRelatedMovies(movie.relatedMovies || []);
        renderShowDates(movie.showDates || []);

        await loadCities();
        bindFilters();
    } catch (error) {
        console.error(error);
        document.getElementById("movie-title").textContent = "Không tải được dữ liệu phim";
        document.getElementById("movie-description-text").textContent = "Đã xảy ra lỗi khi tải chi tiết phim.";
    }
});

function getMovieIdFromUrl() {
    const pathParts = window.location.pathname.split("/").filter(Boolean);

    const lastPart = pathParts[pathParts.length - 1];
    const id = Number(lastPart);

    if (!Number.isNaN(id) && id > 0) {
        return id;
    }

    const params = new URLSearchParams(window.location.search);
    const queryId = Number(params.get("id"));

    if (!Number.isNaN(queryId) && queryId > 0) {
        return queryId;
    }

    return null;
}

function renderMovieInfo(movie) {
    setText("movie-title", movie.title);
    setText("movie-age-rating", movie.ageRating);
    setText("movie-duration", `${movie.durationMinutes || 0} phút`);
    setText("movie-language", movie.language);
    setText("movie-director", movie.director);
    setText("movie-release-date", formatDate(movie.releaseDate));
    setText("movie-description-text", movie.description || "Đang cập nhật nội dung phim.");
    setText("movie-rating", "⭐ Đang cập nhật");

    const genres = (movie.genres || []).map(g => g.name).join(", ");
    const actors = (movie.actors || []).map(a => a.name).join(", ");

    setText("movie-genres", genres || "Đang cập nhật");
    setText("movie-actors", actors || "Đang cập nhật");

    const poster = movie.posterUrl || "";
    const banner = movie.bannerUrl || "";
    const posterImg = document.getElementById("movie-poster-img");
    const bannerImg = document.getElementById("movie-banner-img");

    if (posterImg) {
        posterImg.src = poster;
        posterImg.alt = movie.title || "Poster phim";
    }

    if (bannerImg) {
        bannerImg.src = movie.bannerUrl || movie.posterUrl;
        bannerImg.alt = movie.title || "Banner phim";
    }
}

function renderRelatedMovies(relatedMovies) {
    const container = document.getElementById("related-movies-container");
    if (!container) return;

    container.innerHTML = "";

    if (!relatedMovies.length) {
        container.innerHTML = "<p>Chưa có phim liên quan.</p>";
        return;
    }

    const topMovies = relatedMovies.slice(0, 3);

    topMovies.forEach(movie => {
        const movieLink = `/movie-detail?id=${movie.id}`;

        const html = `
            <div class="side-movie">
                <a href="${movieLink}" class="side-movie-link">
                    <div class="movie-card">
                        <img src="${movie.posterUrl || ""}" alt="${escapeHtml(movie.title || "")}">
                        <span class="age-badge">${movie.ageRating || ""}</span>
                    </div>
                    <p class="movie-name">${escapeHtml(movie.title || "")}</p>
                </a>
            </div>
        `;
        container.insertAdjacentHTML("beforeend", html);
    });
}

function renderShowDates(showDates) {
    const daysContainer = document.getElementById("showtime-days");
    const contentContainer = document.getElementById("showtime-content");

    if (!daysContainer || !contentContainer) return;

    daysContainer.innerHTML = "";
    contentContainer.innerHTML = "";

    if (!showDates.length) {
        contentContainer.innerHTML = "<p>Hiện chưa có lịch chiếu.</p>";
        return;
    }

    const uniqueDates = [...new Set(showDates.map(item => item.date))];

    allShowDates = uniqueDates.map(dateStr => {
        const d = new Date(dateStr);
        const dayNames = [
            "Chủ Nhật", "Thứ Hai", "Thứ Ba", "Thứ Tư",
            "Thứ Năm", "Thứ Sáu", "Thứ Bảy"
        ];

        const dd = String(d.getDate()).padStart(2, "0");
        const mm = String(d.getMonth() + 1).padStart(2, "0");

        return {
            date: dateStr,
            dayName: dayNames[d.getDay()],
            dayValue: `${dd}-${mm}`
        };
    });

    if (allShowDates.length > 0 && !selectedDate) {
        selectedDate = allShowDates[0].date;
    }

    ensureSelectedDateVisible();
    bindShowtimeNav();
    renderShowtimeDays();

    const selectedShowDate = showDates.find(d => d.date === selectedDate);
    if (selectedShowDate) {
        renderShowtimeContent(selectedShowDate);
    }
}

function renderShowtimeContent(showDate) {
    const contentContainer = document.getElementById("showtime-content");
    if (!contentContainer) return;

    contentContainer.innerHTML = "";

    if (!showDate.cinemas || !showDate.cinemas.length) {
        contentContainer.innerHTML = "<p>Không có suất chiếu cho ngày này.</p>";
        return;
    }

    let filteredCinemas = [...showDate.cinemas];

    if (selectedCity) {
        filteredCinemas = filteredCinemas.filter(cinema => cinema.city === selectedCity);
    }

    if (selectedCinemaId) {
        filteredCinemas = filteredCinemas.filter(
            cinema => String(cinema.cinemaId) === String(selectedCinemaId)
        );
    }

    if (!filteredCinemas.length) {
        contentContainer.innerHTML = "<p>Không có suất chiếu phù hợp với bộ lọc đã chọn.</p>";
        return;
    }

    filteredCinemas.forEach(cinema => {
        const timesHtml = (cinema.times || []).map(time => `
            <button
                type="button"
                class="showtime-link"
                data-showtime-id="${time.showtimeId}">
                ${formatTime(time.startTime)}
            </button>
        `).join("");

        const html = `
            <div class="cinema-block">
                <h4>${escapeHtml(cinema.cinemaName || "")}</h4>
                <div class="time-list">
                    ${timesHtml}
                </div>
            </div>
        `;

        contentContainer.insertAdjacentHTML("beforeend", html);
            const showtimeButtons = contentContainer.querySelectorAll(".showtime-link");
                showtimeButtons.forEach(button => {
                    button.addEventListener("click", async () => {
                        const newShowtimeId = button.getAttribute("data-showtime-id");
                        await startNewBookingFlow(newShowtimeId);
                    });
        });
    });
}

function setText(id, value) {
    const el = document.getElementById(id);
    if (el) {
        el.textContent = value || "";
    }
}

function formatDate(dateString) {
    if (!dateString) return "";
    const date = new Date(dateString);
    return date.toLocaleDateString("vi-VN");
}

function formatDateShort(dateString) {
    if (!dateString) return "";
    const date = new Date(dateString);
    return date.toLocaleDateString("vi-VN", {
        day: "2-digit",
        month: "2-digit"
    });
}

function formatTime(dateTimeString) {
    if (!dateTimeString) return "";
    const date = new Date(dateTimeString);
    return date.toLocaleTimeString("vi-VN", {
        hour: "2-digit",
        minute: "2-digit",
        hour12: false
    });
}

function getVietnameseWeekday(date) {
    const weekdays = [
        "Chủ Nhật",
        "Thứ Hai",
        "Thứ Ba",
        "Thứ Tư",
        "Thứ Năm",
        "Thứ Sáu",
        "Thứ Bảy"
    ];
    return weekdays[date.getDay()];
}

function escapeHtml(str) {
    return String(str)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}

async function loadCities() {
    const citySelect = document.getElementById("city-filter");
    if (!citySelect) return;

    const response = await fetch("/api/movies/cities");
    if (!response.ok) return;

    const cities = await response.json();

    citySelect.innerHTML = `<option value="">Toàn quốc</option>`;
    cities.forEach(city => {
        citySelect.insertAdjacentHTML(
            "beforeend",
            `<option value="${escapeHtml(city)}">${escapeHtml(city)}</option>`
        );
    });

    await loadCinemasByCity("");
}

async function loadCinemasByCity(city) {
    const cinemaSelect = document.getElementById("cinema-filter");
    if (!cinemaSelect) return;

    const url = city
        ? `/api/movies/cinemas?city=${encodeURIComponent(city)}`
        : `/api/movies/cinemas`;

    const response = await fetch(url);
    if (!response.ok) return;

    const cinemas = await response.json();

    const uniqueMap = new Map();
    cinemas.forEach(cinema => {
        const key = `${cinema.name}|${cinema.city}`;
        if (!uniqueMap.has(key)) {
            uniqueMap.set(key, cinema);
        }
    });

    const uniqueCinemas = [...uniqueMap.values()];

    cinemaSelect.innerHTML = `<option value="">Tất cả rạp</option>`;
    uniqueCinemas.forEach(cinema => {
        cinemaSelect.insertAdjacentHTML(
            "beforeend",
            `<option value="${cinema.id}">${escapeHtml(cinema.name)}</option>`
        );
    });
}

function bindFilters() {
    const citySelect = document.getElementById("city-filter");
    const cinemaSelect = document.getElementById("cinema-filter");

    if (citySelect) {
        citySelect.addEventListener("change", async (e) => {
            selectedCity = e.target.value;
            selectedCinemaId = "";

            await loadCinemasByCity(selectedCity);
            rerenderCurrentShowDate();
        });
    }

    if (cinemaSelect) {
        cinemaSelect.addEventListener("change", (e) => {
            selectedCinemaId = e.target.value;
            rerenderCurrentShowDate();
        });
    }
}

function rerenderCurrentShowDate() {
    if (!selectedDate || !movieDetailData) return;

    const selectedShowDate = movieDetailData.showDates.find(
        item => item.date === selectedDate
    );

    if (selectedShowDate) {
        renderShowtimeContent(selectedShowDate);
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

async function startNewBookingFlow(newShowtimeId) {
    if (!currentUserId) {
        window.location.href = "/login";
        return;
    }

    const oldShowtimeId = sessionStorage.getItem(CURRENT_SHOWTIME_KEY);
    const savedSeats = JSON.parse(sessionStorage.getItem(SELECTED_SEATS_KEY) || "[]");

    try {
        if (oldShowtimeId && savedSeats.length) {
            await fetch(`/api/showtimes/${oldShowtimeId}/hold-seats`, {
                method: "DELETE",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    userId: currentUserId,
                    seatIds: savedSeats.map(seat => Number(seat.id))
                })
            });
        }

        if (oldShowtimeId) {
            await fetch(`/api/showtimes/${oldShowtimeId}/booking-combos?userId=${currentUserId}`, {
                method: "DELETE"
            });
        }
    } catch (error) {
        console.error("Lỗi khi reset flow cũ:", error);
    } finally {
        clearBookingSession();
        window.location.href = `/seats?showtimeId=${newShowtimeId}`;
    }
}

function renderShowtimeDays() {
    const daysContainer = document.getElementById("showtime-days");
    const prevBtn = document.getElementById("showtime-prev");
    const nextBtn = document.getElementById("showtime-next");

    if (!daysContainer) return;

    daysContainer.innerHTML = "";

    const visibleDates = allShowDates.slice(visibleStartIndex, visibleStartIndex + VISIBLE_DAYS);

    visibleDates.forEach(dateItem => {
        const btn = document.createElement("button");
        btn.className = "day-btn";

        btn.setAttribute("data-date", dateItem.date); // ✅ QUAN TRỌNG

        if (selectedDate === dateItem.date) {
            btn.classList.add("active");
        }

        btn.innerHTML = `
            <span class="day-top">${dateItem.dayName}</span>
            <span class="day-bottom">${dateItem.dayValue}</span>
        `;

        btn.addEventListener("click", () => {
            selectedDate = dateItem.date;
            renderShowtimeDays();

            const selectedShowDate = movieDetailData.showDates.find(
                d => d.date === selectedDate
            );

            if (selectedShowDate) {
                renderShowtimeContent(selectedShowDate);
            }
        });

        daysContainer.appendChild(btn);
    });

    if (prevBtn) {
        prevBtn.style.display = visibleStartIndex > 0 ? "inline-flex" : "none";
    }

    if (nextBtn) {
        nextBtn.style.display =
            visibleStartIndex + VISIBLE_DAYS < allShowDates.length ? "inline-flex" : "none";
    }
}

function bindShowtimeNav() {
    const prevBtn = document.getElementById("showtime-prev");
    const nextBtn = document.getElementById("showtime-next");

    if (prevBtn) {
        prevBtn.onclick = () => {
            if (visibleStartIndex > 0) {
                visibleStartIndex--;
                renderShowtimeDays();
            }
        };
    }

    if (nextBtn) {
        nextBtn.onclick = () => {
            if (visibleStartIndex + VISIBLE_DAYS < allShowDates.length) {
                visibleStartIndex++;
                renderShowtimeDays();
            }
        };
    }
}

function ensureSelectedDateVisible() {
    const selectedIndex = allShowDates.findIndex(item => item.date === selectedDate);

    if (selectedIndex === -1) return;

    if (selectedIndex < visibleStartIndex) {
        visibleStartIndex = selectedIndex;
    } else if (selectedIndex >= visibleStartIndex + VISIBLE_DAYS) {
        visibleStartIndex = selectedIndex - VISIBLE_DAYS + 1;
    }
}
