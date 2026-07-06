package ru.fefu.store.data.repository

import ru.fefu.store.domain.model.CatalogData

interface CatalogRepository {

    suspend fun getCatalog(): CatalogData
}