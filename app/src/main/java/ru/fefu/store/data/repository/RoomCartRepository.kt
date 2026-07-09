package ru.fefu.store.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import ru.fefu.store.data.database.CartDao
import ru.fefu.store.domain.model.CartData
import ru.fefu.store.domain.model.CartLineItem

class RoomCartRepository(private val cartDao: CartDao, private val catalogRepository: CatalogRepository) : CartRepository {

    override fun observeCart(): Flow<CartData> {
        return combine(
            cartDao.observeCartItems(),
            catalogRepository.observeCatalog(),
        ) { cartItems, catalog ->
            val lineItems = cartItems.mapNotNull { cartItem ->
                val product = catalog.products.firstOrNull { product ->
                    product.id == cartItem.productId
                } ?: return@mapNotNull null

                val size = product.sizes.firstOrNull { size ->
                    size.id == cartItem.sizeId
                } ?: return@mapNotNull null

                CartLineItem(
                    product = product,
                    size = size,
                    quantity = cartItem.quantity,
                )
            }

            CartData(
                items = lineItems,
            )
        }.distinctUntilChanged()
    }

    override fun observeTotalQuantity(): Flow<Int> = cartDao.observeTotalQuantity()

    override suspend fun addToCart(productId: String, sizeId: String) {
        cartDao.addOrIncrease(
            productId = productId,
            sizeId = sizeId,
        )
    }

    override suspend fun increaseQuantity(productId: String, sizeId: String) {
        cartDao.changeQuantity(
            productId = productId,
            sizeId = sizeId,
            delta = 1,
        )
    }

    override suspend fun decreaseQuantity(productId: String, sizeId: String) {
        cartDao.changeQuantity(
            productId = productId,
            sizeId = sizeId,
            delta = -1,
        )
    }

    override suspend fun removeFromCart(productId: String, sizeId: String) {
        cartDao.removeCartItem(
            productId = productId,
            sizeId = sizeId,
        )
    }

    override suspend fun clearCart() {
        cartDao.clearCart()
    }
}
