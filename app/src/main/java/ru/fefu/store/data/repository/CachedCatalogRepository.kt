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
import ru.fefu.store.domain.catalog.CatalogCategoryBuilder
import ru.fefu.store.domain.model.CatalogData

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

        val categories = CatalogCategoryBuilder.withNewCategoryIfNeeded(
            categories = categoryEntities.map { categoryEntity ->
                categoryEntity.toDomain()
            },
            products = products
        )

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
}
