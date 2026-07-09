package ru.fefu.store.ui.cart

import ru.fefu.store.domain.model.CartLineItem

data class CartUiState(
    val items: List<CartLineItem> = emptyList(),
    val totalQuantity: Int = 0,
    val totalPriceInKopecks: Long = 0L,
    val customerName: String = "",
    val customerEmail: String = "",
    val comment: String = "",
    val isCheckoutAvailable: Boolean = false,
    val isOrderSuccessVisible: Boolean = false
)