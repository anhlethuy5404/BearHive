document.addEventListener("DOMContentLoaded", () => {
    const messages = document.querySelectorAll(".message")
    if (messages.length > 0) {
        setTimeout(() => {
            messages.forEach((message) => {
                message.style.animation = "slideUp 0.5s ease forwards"
            })
        }, 5000)
    }
})

if (!document.querySelector("style#animations")) {
    const style = document.createElement("style")
    style.id = "animations"
    style.textContent = `
      @keyframes slideUp {
        from {
          transform: translate(-50%, 0);
          opacity: 1;
        }
        to {
          transform: translate(-50%, -100%);
          opacity: 0;
        }
      }
      
      @keyframes ripple {
        to {
          transform: scale(100);
          opacity: 0;
        }
      }
    `
    document.head.appendChild(style)
}
