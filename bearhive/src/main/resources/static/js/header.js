function toggleMenu() {
    document.querySelector(".dropdown-menu").classList.toggle("show");
}
document.addEventListener("click", function(event) {
    const dropdown = document.querySelector(".dropdown");
    if(!dropdown.contains(event.target)) {
        document.querySelector(".dropdown-menu").classList.remove("show");
    }
});