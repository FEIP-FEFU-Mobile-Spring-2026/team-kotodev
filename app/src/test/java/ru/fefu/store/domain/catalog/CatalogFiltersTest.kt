package ru.fefu.store.domain.catalog

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.fefu.store.TestFixtures
import ru.fefu.store.domain.CatalogConstants

class CatalogFiltersTest {

    @Test
    fun `regular category returns products from selected category`() {
        val products = listOf(
            TestFixtures.product(
                id = "product_1",
                categoryId = "cat_outerwear"
            ),
            TestFixtures.product(
                id = "product_2",
                categoryId = "cat_shirts"
            )
        )

        val result = CatalogFilters.filterProductsByCategory(
            products = products,
            categoryId = "cat_outerwear"
        )

        assertEquals(listOf("product_1"), result.map { product -> product.id })
    }

    @Test
    fun `new category returns products with new tag ignoring case`() {
        val products = listOf(
            TestFixtures.product(
                id = "product_1",
                tags = listOf("New")
            ),
            TestFixtures.product(
                id = "product_2",
                tags = listOf("sale")
            ),
            TestFixtures.product(
                id = "product_3",
                tags = listOf("new")
            )
        )

        val result = CatalogFilters.filterProductsByCategory(
            products = products,
            categoryId = CatalogConstants.NEW_CATEGORY_ID
        )

        assertEquals(
            listOf("product_1", "product_3"),
            result.map { product -> product.id }
        )
    }
}
