package ru.fefu.store.di

import android.content.Context
import ru.fefu.store.data.local.AssetCatalogDataSource
import ru.fefu.store.data.repository.AssetCatalogRepository
import ru.fefu.store.data.repository.CatalogRepository

interface AppContainer {
    val catalogRepository: CatalogRepository
}

class DefaultAppContainer(
    private val context: Context
) : AppContainer {

    override val catalogRepository: CatalogRepository by lazy {
        AssetCatalogRepository(
            assetCatalogDataSource = AssetCatalogDataSource(
                context = context.applicationContext
            )
        )
    }
}