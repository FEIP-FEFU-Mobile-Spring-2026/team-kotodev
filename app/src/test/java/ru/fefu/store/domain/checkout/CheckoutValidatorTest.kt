package ru.fefu.store.domain.checkout

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CheckoutValidatorTest {

    @Test
    fun `empty name is invalid`() {
        val result = CheckoutValidator.isNameValid("   ")

        assertFalse(result)
    }

    @Test
    fun `valid name is accepted`() {
        val result = CheckoutValidator.isNameValid("Антон")

        assertTrue(result)
    }

    @Test
    fun `email without domain zone is invalid`() {
        val result = CheckoutValidator.isEmailValid("anton@mail")

        assertFalse(result)
    }

    @Test
    fun `valid email is accepted`() {
        val result = CheckoutValidator.isEmailValid("anton@gmail.com")

        assertTrue(result)
    }

    @Test
    fun `checkout is unavailable when cart is empty`() {
        val result = CheckoutValidator.isCheckoutAvailable(
            name = "Антон",
            email = "anton@gmail.com",
            hasItems = false
        )

        assertFalse(result)
    }

    @Test
    fun `checkout is available when name email and cart are valid`() {
        val result = CheckoutValidator.isCheckoutAvailable(
            name = "Антон",
            email = "anton@gmail.com",
            hasItems = true
        )

        assertTrue(result)
    }
}
