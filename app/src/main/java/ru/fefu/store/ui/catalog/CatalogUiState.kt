package ru.fefu.store.ui.catalog

import ru.fefu.store.domain.model.Category
import ru.fefu.store.domain.model.Product

sealed interface CatalogUiState {

    data object Loading : CatalogUiState

    data object Error : CatalogUiState

    data class Content(
        val categories: List<Category>,
        val selectedCategoryId: String,
        val products: List<Product>
    ) : CatalogUiState
}