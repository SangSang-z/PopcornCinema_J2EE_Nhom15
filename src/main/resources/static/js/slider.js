const slides = document.querySelectorAll(".slide");
const nextBtn = document.querySelector(".next");
const prevBtn = document.querySelector(".prev");
const dotsContainer = document.querySelector(".dots");

let currentSlide = 0;

/* CREATE DOTS */

slides.forEach((_,index)=>{

    const dot=document.createElement("div");
    dot.classList.add("dot");

    if(index===0) dot.classList.add("active");

    dot.addEventListener("click",()=>showSlide(index));

    dotsContainer.appendChild(dot);

});

const dots=document.querySelectorAll(".dot");

/* SHOW SLIDE */

function showSlide(index){

    slides.forEach(slide=>slide.classList.remove("active"));
    dots.forEach(dot=>dot.classList.remove("active"));

    slides[index].classList.add("active");
    dots[index].classList.add("active");

    currentSlide=index;
}

/* NEXT */

function nextSlide(){

    currentSlide++;

    if(currentSlide>=slides.length){
        currentSlide=0;
    }

    showSlide(currentSlide);
}

/* PREV */

function prevSlide(){

    currentSlide--;

    if(currentSlide<0){
        currentSlide=slides.length-1;
    }

    showSlide(currentSlide);
}

nextBtn.addEventListener("click",nextSlide);
prevBtn.addEventListener("click",prevSlide);

/* AUTO SLIDE */

setInterval(nextSlide,5000);