package ru.fefu.store.data.repository

import kotlinx.coroutines.flow.Flow
import ru.fefu.store.domain.model.CartData

interface CartRepository {

    fun observeCart(): Flow<CartData>

    fun observeTotalQuantity(): Flow<Int>

    suspend fun addToCart(productId: String, sizeId: String)

    suspend fun increaseQuantity(productId: String, sizeId: String)

    suspend fun decreaseQuantity(productId: String, sizeId: String)

    suspend fun removeFromCart(productId: String, sizeId: String)

    suspend fun clearCart()
}
