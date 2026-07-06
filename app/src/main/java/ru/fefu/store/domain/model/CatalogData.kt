package ru.fefu.store.domain.model

data class CatalogData(
    val categories: List<Category>,
    val products: List<Product>
)