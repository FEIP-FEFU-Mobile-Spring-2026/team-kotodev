package ru.fefu.store.domain.checkout

object CheckoutValidator {

    private val emailRegex = Regex(
        pattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    )

    fun isNameValid(name: String): Boolean {
        return name.trim().isNotEmpty()
    }

    fun isEmailValid(email: String): Boolean {
        return emailRegex.matches(email.trim())
    }

    fun isCheckoutAvailable(name: String, email: String, hasItems: Boolean): Boolean {
        return hasItems && isNameValid(name) && isEmailValid(email)
    }
}
