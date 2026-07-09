package ru.fefu.store.domain.model

data class Product(
    val id: String,
    val name: String,
    val shortDescription: String,
    val longDescription: String,
    val priceInKopecks: Long,
    val imageUrl: String,
    val tags: List<String>,
    val sizes: List<ProductSize>,
    val categoryId: String,
    val material: String,
    val weight: String,
    val season: String,
    val countryOfOrigin: String,
)
