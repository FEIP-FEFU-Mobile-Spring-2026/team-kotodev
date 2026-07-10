package ru.fefu.store.ui.catalog

import ru.fefu.store.domain.model.Category
import ru.fefu.store.domain.model.Product

sealed interface CatalogUiState {

    data object Loading : CatalogUiState

    data class Error(val title: String, val description: String) : CatalogUiState

    data class Content(
        val categories: List<Category>,
        val selectedCategoryId: String,
        val products: List<Product>,
        val isRefreshing: Boolean,
        val isOffline: Boolean,
    ) : CatalogUiState
}
