package ru.fefu.store.di

import android.content.Context
import androidx.room.Room
import ru.fefu.store.data.connectivity.ConnectivityObserver
import ru.fefu.store.data.database.StoreDatabase
import ru.fefu.store.data.network.NetworkCatalogDataSource
import ru.fefu.store.data.repository.CachedCatalogRepository
import ru.fefu.store.data.repository.CatalogRepository

interface AppContainer {
    val catalogRepository: CatalogRepository
}

class DefaultAppContainer(
    private val context: Context
) : AppContainer {

    private val database: StoreDatabase by lazy {
        Room.databaseBuilder(
            context.applicationContext,
            StoreDatabase::class.java,
            "store.db"
        ).build()
    }

    private val networkCatalogDataSource: NetworkCatalogDataSource by lazy {
        NetworkCatalogDataSource()
    }

    private val connectivityObserver: ConnectivityObserver by lazy {
        ConnectivityObserver(
            context = context.applicationContext
        )
    }

    override val catalogRepository: CatalogRepository by lazy {
        CachedCatalogRepository(
            networkCatalogDataSource = networkCatalogDataSource,
            catalogDao = database.catalogDao(),
            connectivityObserver = connectivityObserver
        )
    }
}