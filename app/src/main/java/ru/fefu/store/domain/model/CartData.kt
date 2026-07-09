package ru.fefu.store.domain.model

data class CartData(
    val items: List<CartLineItem>
) {
    val totalPriceInKopecks: Long
        get() = items.sumOf { item -> item.totalPriceInKopecks }

    val totalQuantity: Int
        get() = items.sumOf { item -> item.quantity }
}