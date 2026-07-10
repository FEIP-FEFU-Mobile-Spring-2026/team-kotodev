package ru.fefu.store.data.database

import org.junit.Assert.assertEquals
import org.junit.Test
import ru.fefu.store.TestFixtures

class ProductEntityMappingTest {

    @Test
    fun `product maps to entity and back without losing main fields`() {
        val product = TestFixtures.product(
            id = "product_1",
            name = "Кардиган из хлопка",
            priceInKopecks = 14_999_00,
            categoryId = "cat_cardigans",
            tags = listOf("New", "Hit"),
            sizes = listOf(
                TestFixtures.size(
                    id = "size_xl",
                    name = "XL"
                ),
                TestFixtures.size(
                    id = "size_xxl",
                    name = "XXL"
                )
            )
        )

        val entity = product.toEntity(sortOrder = 3)
        val restoredProduct = entity.toDomain()

        assertEquals(product.id, restoredProduct.id)
        assertEquals(product.name, restoredProduct.name)
        assertEquals(product.priceInKopecks, restoredProduct.priceInKopecks)
        assertEquals(product.categoryId, restoredProduct.categoryId)
        assertEquals(product.tags, restoredProduct.tags)
        assertEquals(product.sizes, restoredProduct.sizes)
        assertEquals(3, entity.sortOrder)
    }
}
