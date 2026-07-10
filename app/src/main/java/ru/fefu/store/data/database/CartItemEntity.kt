package ru.fefu.store.data.database

import androidx.room.Entity

@Entity(
    tableName = "cart_items",
    primaryKeys = [
        "productId",
        "sizeId",
    ],
)
data class CartItemEntity(val productId: String, val sizeId: String, val quantity: Int)
