package ru.fefu.store

import ru.fefu.store.domain.model.Category
import ru.fefu.store.domain.model.Product
import ru.fefu.store.domain.model.ProductSize

object TestFixtures {

    fun category(id: String = "cat_outerwear", name: String = "Верхняя одежда"): Category {
        return Category(
            id = id,
            name = name
        )
    }

    fun product(
        id: String = "product_1",
        name: String = "Блейзер прямого кроя",
        priceInKopecks: Long = 2_970_00,
        categoryId: String = "cat_outerwear",
        tags: List<String> = emptyList(),
        sizes: List<ProductSize> = listOf(size())
    ): Product {
        return Product(
            id = id,
            name = name,
            shortDescription = "Серый",
            longDescription = "Описание товара",
            priceInKopecks = priceInKopecks,
            imageUrl = "https://example.com/image.png",
            tags = tags,
            sizes = sizes,
            categoryId = categoryId,
            material = "Хлопок",
            weight = "300 г",
            season = "Демисезон",
            countryOfOrigin = "Россия"
        )
    }

    fun size(id: String = "size_m", name: String = "M"): ProductSize {
        return ProductSize(
            id = id,
            name = name
        )
    }
}
