package ru.fefu.store.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
abstract class CartDao {

    @Query("SELECT * FROM cart_items ORDER BY productId, sizeId")
    abstract fun observeCartItems(): Flow<List<CartItemEntity>>

    @Query("SELECT COALESCE(SUM(quantity), 0) FROM cart_items")
    abstract fun observeTotalQuantity(): Flow<Int>

    @Query(
        """
        SELECT * FROM cart_items 
        WHERE productId = :productId AND sizeId = :sizeId 
        LIMIT 1
        """
    )
    protected abstract suspend fun getCartItem(
        productId: String,
        sizeId: String
    ): CartItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    protected abstract suspend fun upsertCartItem(cartItem: CartItemEntity)

    @Query(
        """
        DELETE FROM cart_items 
        WHERE productId = :productId AND sizeId = :sizeId
        """
    )
    abstract suspend fun removeCartItem(
        productId: String,
        sizeId: String
    )

    @Query("DELETE FROM cart_items")
    abstract suspend fun clearCart()

    @Transaction
    open suspend fun addOrIncrease(
        productId: String,
        sizeId: String
    ) {
        val existingItem = getCartItem(
            productId = productId,
            sizeId = sizeId
        )

        if (existingItem == null) {
            upsertCartItem(
                CartItemEntity(
                    productId = productId,
                    sizeId = sizeId,
                    quantity = 1
                )
            )
        } else {
            upsertCartItem(
                existingItem.copy(
                    quantity = existingItem.quantity + 1
                )
            )
        }
    }

    @Transaction
    open suspend fun changeQuantity(
        productId: String,
        sizeId: String,
        delta: Int
    ) {
        val existingItem = getCartItem(
            productId = productId,
            sizeId = sizeId
        ) ?: return

        val newQuantity = existingItem.quantity + delta

        if (newQuantity <= 0) {
            removeCartItem(
                productId = productId,
                sizeId = sizeId
            )
        } else {
            upsertCartItem(
                existingItem.copy(
                    quantity = newQuantity
                )
            )
        }
    }
}