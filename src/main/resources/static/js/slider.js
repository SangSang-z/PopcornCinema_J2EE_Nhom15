const slides = document.querySelectorAll(".slide");
const nextBtn = document.querySelector(".next");
const prevBtn = document.querySelector(".prev");
const dotsContainer = document.querySelector(".dots");

if (slides.length && dotsContainer && nextBtn && prevBtn) {
    let currentSlide = 0;

    /* CREATE DOTS */
    slides.forEach((_, index) => {
        const dot = document.createElement("div");
        dot.classList.add("dot");

        if (index === 0) dot.classList.add("active");

        dot.addEventListener("click", () => showSlide(index));

        dotsContainer.appendChild(dot);
    });

    const dots = document.querySelectorAll(".dot");

    /* SHOW SLIDE */
    function showSlide(index){
        slides.forEach(slide => slide.classList.remove("active"));
        dots.forEach(dot => dot.classList.remove("active"));

        slides[index].classList.add("active");
        dots[index].classList.add("active");

        currentSlide = index;
    }

    /* NEXT */
    function nextSlide(){
        currentSlide++;

        if (currentSlide >= slides.length) {
            currentSlide = 0;
        }

        showSlide(currentSlide);
    }

    /* PREV */
    function prevSlide(){
        currentSlide--;

        if (currentSlide < 0) {
            currentSlide = slides.length - 1;
        }

        showSlide(currentSlide);
    }

    nextBtn.addEventListener("click", nextSlide);
    prevBtn.addEventListener("click", prevSlide);

    /* AUTO SLIDE */
    setInterval(nextSlide, 5000);
}

// ================= HOME HERO MANUAL SLIDER =================
const homeHeroSlider = document.querySelector(".home-hero-slider");
const homeHeroTrack = homeHeroSlider ? homeHeroSlider.querySelector(".slides") : null;
const homeHeroSlides = homeHeroSlider ? homeHeroSlider.querySelectorAll(".home-hero-slide") : [];
const homeHeroMedia = homeHeroSlider ? homeHeroSlider.closest(".home-hero-media") : null;
const homeHeroPrev = document.querySelector(".home-hero-prev");
const homeHeroNext = document.querySelector(".home-hero-next");
const homeHeroDots = document.querySelector(".home-hero-dots");

if (homeHeroSlider && homeHeroTrack && homeHeroSlides.length && homeHeroPrev && homeHeroNext && homeHeroDots && homeHeroMedia) {
    let homeHeroIndex = 0;

    function updateHeroAspect(index) {
        const img = homeHeroSlides[index].querySelector(".home-hero-image");
        if (!img) return;

        const applyAspect = () => {
            if (!img.naturalWidth || !img.naturalHeight) return;
            const ratio = (img.naturalWidth / img.naturalHeight).toFixed(3);
            homeHeroMedia.style.setProperty("--hero-aspect", ratio);
        };

        if (img.complete) {
            applyAspect();
        } else {
            img.addEventListener("load", applyAspect, { once: true });
        }
    }

    // Create dots
    homeHeroSlides.forEach((_, index) => {
        const dot = document.createElement("div");
        dot.classList.add("home-hero-dot");

        if (index === 0) dot.classList.add("active");

        dot.addEventListener("click", () => goToHomeHero(index));
        homeHeroDots.appendChild(dot);
    });

    const homeHeroDotItems = homeHeroDots.querySelectorAll(".home-hero-dot");

    function goToHomeHero(index) {
        homeHeroIndex = index;

        homeHeroSlides.forEach(slide => slide.classList.remove("active"));
        homeHeroSlides[index].classList.add("active");

        homeHeroDotItems.forEach(dot => dot.classList.remove("active"));
        homeHeroDotItems[index].classList.add("active");

        updateHeroAspect(index);
    }

    function nextHomeHero() {
        const nextIndex = (homeHeroIndex + 1) % homeHeroSlides.length;
        goToHomeHero(nextIndex);
    }

    function prevHomeHero() {
        const prevIndex = (homeHeroIndex - 1 + homeHeroSlides.length) % homeHeroSlides.length;
        goToHomeHero(prevIndex);
    }

    homeHeroNext.addEventListener("click", nextHomeHero);
    homeHeroPrev.addEventListener("click", prevHomeHero);

    // Initialize
    goToHomeHero(0);
}
