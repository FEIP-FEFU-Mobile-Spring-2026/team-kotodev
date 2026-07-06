package ru.fefu.store.ui.catalog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.fefu.store.data.repository.CatalogRepository
import ru.fefu.store.domain.model.Category
import ru.fefu.store.domain.model.Product
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.createSavedStateHandle

class CatalogViewModel(
    private val repository: CatalogRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow<CatalogUiState>(CatalogUiState.Loading)
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    private var allCategories: List<Category> = emptyList()
    private var allProducts: List<Product> = emptyList()

    init {
        loadCatalog()
    }

    fun loadCatalog() {
        _uiState.value = CatalogUiState.Loading

        viewModelScope.launch {
            try {
                val catalog = repository.getCatalog()

                allCategories = catalog.categories
                allProducts = catalog.products

                val restoredCategoryId = savedStateHandle.get<String>(KEY_SELECTED_CATEGORY_ID)

                val selectedCategoryId = when {
                    restoredCategoryId != null && allCategories.any { it.id == restoredCategoryId } -> {
                        restoredCategoryId
                    }

                    allCategories.isNotEmpty() -> {
                        allCategories.first().id
                    }

                    else -> {
                        ""
                    }
                }

                savedStateHandle[KEY_SELECTED_CATEGORY_ID] = selectedCategoryId
                showCategory(selectedCategoryId)
            } catch (exception: Exception) {
                _uiState.value = CatalogUiState.Error(
                    message = "Не удалось загрузить каталог"
                )
            }
        }
    }

    fun selectCategory(categoryId: String) {
        savedStateHandle[KEY_SELECTED_CATEGORY_ID] = categoryId
        showCategory(categoryId)
    }

    private fun showCategory(categoryId: String) {
        val filteredProducts = when (categoryId) {
            NEW_CATEGORY_ID -> allProducts.filter { product ->
                NEW_TAG in product.tags
            }

            else -> allProducts.filter { product ->
                product.categoryId == categoryId
            }
        }

        _uiState.value = CatalogUiState.Content(
            categories = allCategories,
            selectedCategoryId = categoryId,
            products = filteredProducts
        )
    }

    class Factory(
        private val repository: CatalogRepository
    ) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            modelClass: Class<T>,
            extras: CreationExtras
        ): T {
            val savedStateHandle = extras.createSavedStateHandle()

            return CatalogViewModel(
                repository = repository,
                savedStateHandle = savedStateHandle
            ) as T
        }
    }

    private companion object {
        const val KEY_SELECTED_CATEGORY_ID = "selected_category_id"
        const val NEW_CATEGORY_ID = "cat_new"
        const val NEW_TAG = "New"
    }
}