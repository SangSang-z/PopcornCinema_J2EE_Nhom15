/* =========================================================
   GLOBAL INIT
========================================================= */

document.addEventListener("DOMContentLoaded", function () {

    initAuthTabs();
    initSidebarActive();
    initUserDropdown();
    initSearch();
    initPosterPreview();
    initMovieModal();
    initChart();

});


/* =========================================================
   AUTH FORM SWITCH
========================================================= */

function initAuthTabs(){

    const login = document.getElementById("loginForm");
    const register = document.getElementById("registerForm");
    const tabs = document.querySelectorAll(".tab");

    if(!login || !register || tabs.length < 2) return;

    window.showLogin = function(){
        login.classList.add("active");
        register.classList.remove("active");

        tabs[0].classList.add("active");
        tabs[1].classList.remove("active");
    }

    window.showRegister = function(){
        register.classList.add("active");
        login.classList.remove("active");

        tabs[1].classList.add("active");
        tabs[0].classList.remove("active");
    }
}


/* =========================================================
   SIDEBAR ACTIVE
========================================================= */

function initSidebarActive(){

    const items = document.querySelectorAll(".menu-item");

    if(!items.length) return;

    items.forEach(item => {
        item.addEventListener("click", function(){
            items.forEach(i => i.classList.remove("active"));
            this.classList.add("active");
        });
    });

}


/* =========================================================
   USER DROPDOWN
========================================================= */

function initUserDropdown(){

    document.addEventListener("click", function (e) {

        const trigger = e.target.closest(".user-trigger");
        const menu = document.getElementById("userDropdown");

        if (!menu) return;

        // click vào avatar
        if (trigger) {
            e.stopPropagation();
            menu.classList.toggle("show");
        }
        // click ngoài
        else {
            menu.classList.remove("show");
        }

    });

}

/* =========================================================
   SEARCH
========================================================= */

function initSearch(){

    const features = [
        {name:"Quản lý phim", url:"/admin/movies"},
        {name:"Thêm phim", url:"/admin/movies/create"},
        {name:"Suất chiếu", url:"/admin/showtimes"},
        {name:"Đơn hàng", url:"/admin/orders"},
        {name:"Người dùng", url:"/admin/users"},
        {name:"Khuyến mãi", url:"/admin/promotions"}
    ];

    const input = document.getElementById("adminSearch");
    const result = document.getElementById("searchResult");

    if(!input || !result) return;

    input.addEventListener("input", function(){

        const keyword = this.value.toLowerCase();
        result.innerHTML = "";

        if(keyword === ""){
            result.style.display = "none";
            return;
        }

        const filtered = features.filter(f =>
            f.name.toLowerCase().includes(keyword)
        );

        filtered.forEach(item => {

            const div = document.createElement("div");
            div.className = "search-item";
            div.innerText = item.name;

            div.onclick = () => window.location.href = item.url;

            result.appendChild(div);
        });

        result.style.display = "flex";
    });

}


/* =========================================================
   POSTER PREVIEW
========================================================= */

function initPosterPreview(){

    const input = document.querySelector("input[name='posterFile']");
    const preview = document.getElementById("posterPreview");

    if(!input || !preview) return;

    input.addEventListener("change", function(e){

        const file = e.target.files[0];
        if(!file) return;

        const reader = new FileReader();

        reader.onload = function(){
            preview.src = reader.result;
        }

        reader.readAsDataURL(file);

    });

}


/* =========================================================
   MOVIE MODAL
========================================================= */

function initMovieModal(){

    const modal = document.getElementById("movieDetailModal");
    if(!modal) return;

    modal.addEventListener("show.bs.modal", function(event){

        const button = event.relatedTarget;
        if(!button) return;

        const title = button.getAttribute("data-title");
        const director = button.getAttribute("data-director");
        const duration = button.getAttribute("data-duration");
        const statusRaw = button.getAttribute("data-status");
        const description = button.getAttribute("data-description");
        const poster = button.getAttribute("data-poster");

        let status = "";
        if (statusRaw === "NOW_SHOWING") status = "Đang chiếu";
        else if (statusRaw === "COMING_SOON") status = "Sắp chiếu";

        document.getElementById("modalTitle").innerText = title || "";
        document.getElementById("modalDirector").innerText = director || "";
        document.getElementById("modalDuration").innerText = duration || "";
        document.getElementById("modalStatus").innerText = status;
        document.getElementById("modalDescription").innerText = description || "";

        document.getElementById("modalPoster").src =
            poster || "/images/no-image.png";

    });

}


/* =========================================================
   CHART (DASHBOARD)
========================================================= */

function initChart(){

    const ctx = document.getElementById("revenueChart");
    if(!ctx || typeof Chart === "undefined") return;

    new Chart(ctx,{
        type:"line",
        data:{
            labels:["T2","T3","T4","T5","T6","T7","CN"],
            datasets:[{
                label:"Doanh thu",
                data:[120,190,300,250,420,500,620],
                borderColor:"#2ecc71",
                backgroundColor:"rgba(46,204,113,0.2)",
                fill:true,
                tension:0.4
            }]
        },
        options:{
            plugins:{ legend:{display:false} }
        }
    });

}