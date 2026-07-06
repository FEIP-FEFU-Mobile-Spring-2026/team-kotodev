package ru.fefu.store.ui.catalog

import ru.fefu.store.domain.model.Category
import ru.fefu.store.domain.model.Product

sealed interface CatalogUiState {

    data object Loading : CatalogUiState

    data class Error(
        val message: String
    ) : CatalogUiState

    data class Content(
        val categories: List<Category>,
        val selectedCategoryId: String,
        val products: List<Product>
    ) : CatalogUiState
}