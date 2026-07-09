package ru.fefu.store.data.repository

import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import ru.fefu.store.data.connectivity.ConnectivityObserver
import ru.fefu.store.data.database.CatalogDao
import ru.fefu.store.data.database.toDomain
import ru.fefu.store.data.database.toEntity
import ru.fefu.store.data.network.CatalogApiException
import ru.fefu.store.data.network.NetworkCatalogDataSource
import ru.fefu.store.domain.CatalogConstants
import ru.fefu.store.domain.model.CatalogData
import ru.fefu.store.domain.model.Category
import ru.fefu.store.domain.model.Product

class CachedCatalogRepository(
    private val networkCatalogDataSource: NetworkCatalogDataSource,
    private val catalogDao: CatalogDao,
    private val connectivityObserver: ConnectivityObserver,
) : CatalogRepository {

    override fun observeCatalog(): Flow<CatalogData> = combine(
        catalogDao.observeCategories(),
        catalogDao.observeProducts(),
    ) { categoryEntities, productEntities ->
        val products = productEntities.map { productEntity ->
            productEntity.toDomain()
        }

        val categories = categoryEntities
            .map { categoryEntity -> categoryEntity.toDomain() }
            .withNewCategoryIfNeeded(products)

        CatalogData(
            categories = categories,
            products = products,
        )
    }.distinctUntilChanged()

    override suspend fun refreshCatalog(): CatalogRefreshResult {
        if (!connectivityObserver.isOnline()) {
            return CatalogRefreshResult.NoInternet
        }

        return try {
            val catalog = networkCatalogDataSource.loadCatalog()

            catalogDao.replaceCatalog(
                categories = catalog.categories.mapIndexed { index, category ->
                    category.toEntity(sortOrder = index)
                },
                products = catalog.products.mapIndexed { index, product ->
                    product.toEntity(sortOrder = index)
                },
            )

            CatalogRefreshResult.Success
        } catch (exception: CatalogApiException) {
            CatalogRefreshResult.Error(exception)
        } catch (exception: IOException) {
            CatalogRefreshResult.NoInternet
        } catch (exception: Exception) {
            CatalogRefreshResult.Error(exception)
        }
    }

    override suspend fun hasCachedCatalog(): Boolean = catalogDao.getProductsCount() > 0

    private fun List<Category>.withNewCategoryIfNeeded(products: List<Product>): List<Category> {
        val hasNewProducts = products.any { product ->
            product.tags.any { tag ->
                tag.equals(CatalogConstants.NEW_TAG, ignoreCase = true)
            }
        }

        if (!hasNewProducts) {
            return this
        }

        val alreadyHasNewCategory = any { category ->
            category.id == CatalogConstants.NEW_CATEGORY_ID
        }

        if (alreadyHasNewCategory) {
            return this
        }

        return listOf(
            Category(
                id = CatalogConstants.NEW_CATEGORY_ID,
                name = CatalogConstants.NEW_CATEGORY_NAME,
            ),
        ) + this
    }
}
