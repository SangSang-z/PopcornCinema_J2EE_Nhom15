document.addEventListener("DOMContentLoaded", async () => {
    const movieId = getMovieIdFromUrl() || getMovieIdFromDom();

    if (!movieId) {
        setText("upcoming-title", "Không tìm thấy phim");
        setText("upcoming-description-text", "Thiếu mã phim để hiển thị chi tiết.");
        return;
    }

    try {
        const response = await fetch(`/api/movies/${movieId}/detail`);

        if (!response.ok) {
            throw new Error("Không lấy được dữ liệu phim");
        }

        const movie = await response.json();
        renderUpcomingInfo(movie);
    } catch (error) {
        console.error(error);
        setText("upcoming-title", "Không tải được dữ liệu phim");
        setText("upcoming-description-text", "Đã xảy ra lỗi khi tải chi tiết phim.");
    }
});

function getMovieIdFromDom() {
    const holder = document.getElementById("upcoming-detail-page");
    const raw = holder?.getAttribute("data-movie-id");
    const parsed = Number(raw);
    return Number.isFinite(parsed) && parsed > 0 ? parsed : null;
}

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

function renderUpcomingInfo(movie) {
    setText("upcoming-title", movie.title);
    setText("upcoming-age-rating", movie.ageRating || "T");
    setText("upcoming-duration", `${movie.durationMinutes || 0} phút`);
    setText("upcoming-language", movie.language || "Đang cập nhật");
    setText("upcoming-director", movie.director || "Đang cập nhật");
    setText("upcoming-description-text", movie.description || "Đang cập nhật nội dung phim.");

    const genres = (movie.genres || []).map(g => g.name).join(", ");
    const actors = (movie.actors || []).map(a => a.name).join(", ");

    setText("upcoming-genres", genres || "Đang cập nhật");
    setText("upcoming-actors", actors || "Đang cập nhật");

    const releaseDate = movie.releaseDate ? formatDate(movie.releaseDate) : "Đang cập nhật";
    setText("upcoming-release-date", `Khởi chiếu: ${releaseDate}`);

    const poster = movie.posterUrl || "/images/no-image.png";
    const banner = movie.bannerUrl || poster;

    const posterImg = document.getElementById("upcoming-poster-img");
    const bannerImg = document.getElementById("upcoming-banner-img");

    if (posterImg) {
        posterImg.src = poster;
        posterImg.alt = movie.title || "Poster phim";
    }

    if (bannerImg) {
        bannerImg.src = banner;
        bannerImg.alt = movie.title || "Banner phim";
    }
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
