package ru.fefu.store.domain.model

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.fefu.store.TestFixtures

class CartDataTest {

    @Test
    fun `totalQuantity returns sum of item quantities`() {
        val cart = CartData(
            items = listOf(
                CartLineItem(
                    product = TestFixtures.product(id = "product_1"),
                    size = TestFixtures.size(id = "size_m"),
                    quantity = 2
                ),
                CartLineItem(
                    product = TestFixtures.product(id = "product_2"),
                    size = TestFixtures.size(id = "size_l"),
                    quantity = 3
                )
            )
        )

        assertEquals(5, cart.totalQuantity)
    }

    @Test
    fun `totalPriceInKopecks returns sum of line item prices`() {
        val cart = CartData(
            items = listOf(
                CartLineItem(
                    product = TestFixtures.product(
                        id = "product_1",
                        priceInKopecks = 1_000_00
                    ),
                    size = TestFixtures.size(id = "size_m"),
                    quantity = 2
                ),
                CartLineItem(
                    product = TestFixtures.product(
                        id = "product_2",
                        priceInKopecks = 500_00
                    ),
                    size = TestFixtures.size(id = "size_l"),
                    quantity = 3
                )
            )
        )

        assertEquals(3_500_00, cart.totalPriceInKopecks)
    }

    @Test
    fun `cart line item total price depends on quantity`() {
        val item = CartLineItem(
            product = TestFixtures.product(
                priceInKopecks = 2_970_00
            ),
            size = TestFixtures.size(),
            quantity = 4
        )

        assertEquals(11_880_00, item.totalPriceInKopecks)
    }
}
