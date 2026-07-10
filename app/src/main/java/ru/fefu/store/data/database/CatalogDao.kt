package ru.fefu.store.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CatalogDao {

    @Query("SELECT * FROM categories ORDER BY sortOrder")
    abstract fun observeCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM products ORDER BY sortOrder")
    abstract fun observeProducts(): Flow<List<ProductEntity>>

    @Query("SELECT COUNT(*) FROM products")
    abstract suspend fun getProductsCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertCategories(categories: List<CategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun insertProducts(products: List<ProductEntity>)

    @Query("DELETE FROM categories")
    protected abstract suspend fun clearCategories()

    @Query("DELETE FROM products")
    protected abstract suspend fun clearProducts()

    @Transaction
    open suspend fun replaceCatalog(
        categories: List<CategoryEntity>,
        products: List<ProductEntity>
    ) {
        clearProducts()
        clearCategories()
        insertCategories(categories)
        insertProducts(products)
    }
}