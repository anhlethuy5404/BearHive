document.addEventListener("DOMContentLoaded", () => {

    const signupForm = document.querySelector('form[action="/signup"]')
    const resetForm = document.querySelector('form[action="/reset"]')


    const setupPasswordValidation = (form, passwordField, confirmField) => {
        if (!form || !passwordField || !confirmField) return


        const indicator = document.createElement("div")
        indicator.className = "password-match-indicator"
        confirmField.parentNode.insertBefore(indicator, confirmField.nextSibling)

        const checkPasswords = () => {
            const password = passwordField.value
            const confirm = confirmField.value

            if (confirm.length === 0) {
                indicator.className = "password-match-indicator"
            } else if (password === confirm) {
                indicator.className = "password-match-indicator matching"
            } else {
                indicator.className = "password-match-indicator not-matching"
            }
        }
        passwordField.addEventListener("input", checkPasswords)
        confirmField.addEventListener("input", checkPasswords)

        form.addEventListener("submit", (event) => {
            if (passwordField.value !== confirmField.value) {
                event.preventDefault()

                const errorMessage = document.createElement("div")
                errorMessage.className = "message error"
                errorMessage.textContent = "Mật khẩu không hợp lệ!"

                document.body.appendChild(errorMessage)

                setTimeout(() => {
                    errorMessage.remove()
                }, 3000)
                confirmField.style.borderColor = "var(--danger)"
                confirmField.focus()
            }
        })
    }
    if (signupForm) {
        const passwordField = document.getElementById("signupPassword")
        const confirmField = document.getElementById("confirmPassword")
        setupPasswordValidation(signupForm, passwordField, confirmField)
    }
    if (resetForm) {
        const passwordInputs = resetForm.querySelectorAll('input[type="password"]')
        if (passwordInputs.length >= 2) {
            setupPasswordValidation(resetForm, passwordInputs[0], passwordInputs[1])
        }
    }
})
