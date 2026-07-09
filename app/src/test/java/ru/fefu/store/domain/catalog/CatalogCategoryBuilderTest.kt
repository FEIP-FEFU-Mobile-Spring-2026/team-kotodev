package ru.fefu.store.domain.catalog

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.fefu.store.TestFixtures
import ru.fefu.store.domain.CatalogConstants

class CatalogCategoryBuilderTest {

    @Test
    fun `new category is added when catalog contains new products`() {
        val categories = listOf(
            TestFixtures.category(
                id = "cat_outerwear",
                name = "Верхняя одежда"
            )
        )

        val products = listOf(
            TestFixtures.product(
                id = "product_1",
                tags = listOf(CatalogConstants.NEW_TAG)
            )
        )

        val result = CatalogCategoryBuilder.withNewCategoryIfNeeded(
            categories = categories,
            products = products
        )

        assertEquals(CatalogConstants.NEW_CATEGORY_ID, result.first().id)
        assertEquals(2, result.size)
    }

    @Test
    fun `new category is not added when there are no new products`() {
        val categories = listOf(
            TestFixtures.category(
                id = "cat_outerwear",
                name = "Верхняя одежда"
            )
        )

        val products = listOf(
            TestFixtures.product(
                id = "product_1",
                tags = emptyList()
            )
        )

        val result = CatalogCategoryBuilder.withNewCategoryIfNeeded(
            categories = categories,
            products = products
        )

        assertEquals(categories, result)
    }

    @Test
    fun `new category is not duplicated`() {
        val categories = listOf(
            TestFixtures.category(
                id = CatalogConstants.NEW_CATEGORY_ID,
                name = CatalogConstants.NEW_CATEGORY_NAME
            ),
            TestFixtures.category(
                id = "cat_outerwear",
                name = "Верхняя одежда"
            )
        )

        val products = listOf(
            TestFixtures.product(
                id = "product_1",
                tags = listOf(CatalogConstants.NEW_TAG)
            )
        )

        val result = CatalogCategoryBuilder.withNewCategoryIfNeeded(
            categories = categories,
            products = products
        )

        assertEquals(2, result.size)
        assertEquals(CatalogConstants.NEW_CATEGORY_ID, result.first().id)
    }
}
