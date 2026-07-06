package ru.fefu.store.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.fefu.store.data.local.AssetCatalogDataSource
import ru.fefu.store.domain.model.CatalogData
import ru.fefu.store.domain.model.Category

class AssetCatalogRepository(
    private val assetCatalogDataSource: AssetCatalogDataSource
) : CatalogRepository {

    override suspend fun getCatalog(): CatalogData = withContext(Dispatchers.IO) {
        val catalog = assetCatalogDataSource.loadCatalog()

        val newProducts = catalog.products.filter { product ->
            NEW_TAG in product.tags
        }

        val categories = if (newProducts.isNotEmpty()) {
            listOf(NEW_CATEGORY) + catalog.categories
        } else {
            catalog.categories
        }

        catalog.copy(categories = categories)
    }

    private companion object {
        const val NEW_TAG = "New"

        val NEW_CATEGORY = Category(
            id = "cat_new",
            name = "Новинки"
        )
    }
}