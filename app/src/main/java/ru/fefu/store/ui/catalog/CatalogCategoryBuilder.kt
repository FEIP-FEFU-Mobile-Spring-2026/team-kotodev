package ru.fefu.store.domain.catalog

import ru.fefu.store.domain.CatalogConstants
import ru.fefu.store.domain.model.Category
import ru.fefu.store.domain.model.Product

object CatalogCategoryBuilder {

    fun withNewCategoryIfNeeded(categories: List<Category>, products: List<Product>): List<Category> {
        val hasNewProducts = products.any { product ->
            product.tags.any { tag ->
                tag.equals(CatalogConstants.NEW_TAG, ignoreCase = true)
            }
        }

        if (!hasNewProducts) {
            return categories
        }

        val alreadyHasNewCategory = categories.any { category ->
            category.id == CatalogConstants.NEW_CATEGORY_ID
        }

        if (alreadyHasNewCategory) {
            return categories
        }

        return listOf(
            Category(
                id = CatalogConstants.NEW_CATEGORY_ID,
                name = CatalogConstants.NEW_CATEGORY_NAME
            )
        ) + categories
    }
}
