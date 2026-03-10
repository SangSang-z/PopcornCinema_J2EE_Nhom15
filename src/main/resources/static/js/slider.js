let slides = document.querySelectorAll(".slide");
let currentSlide = 0;

function showSlide(index){
    slides.forEach(slide=>{
        slide.classList.remove("active");
    });
    slides[index].classList.add("active");
}

function nextSlide(){
    currentSlide++;
    if(currentSlide >= slides.length){
        currentSlide = 0;
    }
    showSlide(currentSlide);
}
setInterval(nextSlide,4000);