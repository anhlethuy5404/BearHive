document.addEventListener("DOMContentLoaded", () => {
    const createBackgroundPattern = () => {
        const patternContainer = document.createElement("div")
        patternContainer.className = "background-pattern"
        patternContainer.style.position = "fixed"
        patternContainer.style.top = "0"
        patternContainer.style.left = "0"
        patternContainer.style.width = "100%"
        patternContainer.style.height = "100%"
        patternContainer.style.zIndex = "-2"
        patternContainer.style.opacity = "0.4"
        patternContainer.style.pointerEvents = "none"

        const dotSize = 4
        const spacing = 30
        const rows = Math.ceil(window.innerHeight / spacing)
        const cols = Math.ceil(window.innerWidth / spacing)

        for (let i = 0; i < rows; i++) {
            for (let j = 0; j < cols; j++) {
                const dot = document.createElement("div")
                dot.style.position = "absolute"
                dot.style.width = `${dotSize}px`
                dot.style.height = `${dotSize}px`
                dot.style.borderRadius = "50%"
                dot.style.backgroundColor = "#ffc107"
                dot.style.top = `${i * spacing}px`
                dot.style.left = `${j * spacing}px`
                dot.style.opacity = Math.random() * 0.5 + 0.1

                patternContainer.appendChild(dot)
            }
        }

        document.body.appendChild(patternContainer)
    }

    createBackgroundPattern()

    let resizeTimer
    window.addEventListener("resize", () => {
        clearTimeout(resizeTimer)
        resizeTimer = setTimeout(() => {
            const oldPattern = document.querySelector(".background-pattern")
            if (oldPattern) {
                oldPattern.remove()
            }
            createBackgroundPattern()
        }, 250)
    })
})
