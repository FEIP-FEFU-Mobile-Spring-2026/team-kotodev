package ru.fefu.store.domain.model

data class CartLineItem(val product: Product, val size: ProductSize, val quantity: Int) {
    val totalPriceInKopecks: Long
        get() = product.priceInKopecks * quantity
}
