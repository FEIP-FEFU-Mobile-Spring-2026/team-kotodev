package ru.fefu.store.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import ru.fefu.store.data.connectivity.ConnectivityObserver
import ru.fefu.store.data.database.StoreDatabase
import ru.fefu.store.data.network.NetworkCatalogDataSource
import ru.fefu.store.data.repository.CachedCatalogRepository
import ru.fefu.store.data.repository.CartRepository
import ru.fefu.store.data.repository.CatalogRepository
import ru.fefu.store.data.repository.RoomCartRepository

interface AppContainer {
    val catalogRepository: CatalogRepository
    val cartRepository: CartRepository
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val database: StoreDatabase by lazy {
        Room.databaseBuilder(
            context.applicationContext,
            StoreDatabase::class.java,
            "store.db",
        )
            .addMigrations(MIGRATION_1_2)
            .build()
    }

    private val networkCatalogDataSource: NetworkCatalogDataSource by lazy {
        NetworkCatalogDataSource()
    }

    private val connectivityObserver: ConnectivityObserver by lazy {
        ConnectivityObserver(
            context = context.applicationContext,
        )
    }

    override val catalogRepository: CatalogRepository by lazy {
        CachedCatalogRepository(
            networkCatalogDataSource = networkCatalogDataSource,
            catalogDao = database.catalogDao(),
            connectivityObserver = connectivityObserver,
        )
    }

    override val cartRepository: CartRepository by lazy {
        RoomCartRepository(
            cartDao = database.cartDao(),
            catalogRepository = catalogRepository,
        )
    }

    private companion object {

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `cart_items` (
                        `productId` TEXT NOT NULL,
                        `sizeId` TEXT NOT NULL,
                        `quantity` INTEGER NOT NULL,
                        PRIMARY KEY(`productId`, `sizeId`)
                    )
                    """.trimIndent(),
                )
            }
        }
    }
}
