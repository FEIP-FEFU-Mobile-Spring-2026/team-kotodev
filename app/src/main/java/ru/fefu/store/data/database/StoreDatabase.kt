package ru.fefu.store.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        CategoryEntity::class,
        ProductEntity::class,
        CartItemEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class StoreDatabase : RoomDatabase() {

    abstract fun catalogDao(): CatalogDao

    abstract fun cartDao(): CartDao
}