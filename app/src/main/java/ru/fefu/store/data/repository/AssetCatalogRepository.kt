package ru.fefu.store.data.repository

import ru.fefu.store.data.local.AssetCatalogDataSource
import ru.fefu.store.domain.CatalogConstants
import ru.fefu.store.domain.model.CatalogData
import ru.fefu.store.domain.model.Category

class AssetCatalogRepository(
    private val assetCatalogDataSource: AssetCatalogDataSource
) : CatalogRepository {

    override suspend fun getCatalog(): CatalogData {
        val catalog = assetCatalogDataSource.loadCatalog()

        val newProducts = catalog.products.filter { product ->
            CatalogConstants.NEW_TAG in product.tags
        }

        val categories = if (newProducts.isNotEmpty()) {
            listOf(NEW_CATEGORY) + catalog.categories
        } else {
            catalog.categories
        }

        return catalog.copy(categories = categories)
    }

    private companion object {
        val NEW_CATEGORY = Category(
            id = CatalogConstants.NEW_CATEGORY_ID,
            name = CatalogConstants.NEW_CATEGORY_NAME
        )
    }
}