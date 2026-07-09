package ru.fefu.store.domain.catalog

import ru.fefu.store.domain.CatalogConstants
import ru.fefu.store.domain.model.Product

object CatalogFilters {

    fun filterProductsByCategory(
        products: List<Product>,
        categoryId: String
    ): List<Product> {
        return when (categoryId) {
            CatalogConstants.NEW_CATEGORY_ID -> {
                products.filter { product ->
                    product.tags.any { tag ->
                        tag.equals(CatalogConstants.NEW_TAG, ignoreCase = true)
                    }
                }
            }

            else -> {
                products.filter { product ->
                    product.categoryId == categoryId
                }
            }
        }
    }
}
