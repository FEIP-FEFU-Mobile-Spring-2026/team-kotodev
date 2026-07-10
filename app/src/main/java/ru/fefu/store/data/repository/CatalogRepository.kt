package ru.fefu.store.data.repository

import kotlinx.coroutines.flow.Flow
import ru.fefu.store.domain.model.CatalogData

interface CatalogRepository {

    fun observeCatalog(): Flow<CatalogData>

    suspend fun refreshCatalog(): CatalogRefreshResult

    suspend fun hasCachedCatalog(): Boolean
}