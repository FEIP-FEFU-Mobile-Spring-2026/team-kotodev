package ru.fefu.store.ui.catalog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.fefu.store.data.repository.CatalogRefreshResult
import ru.fefu.store.data.repository.CatalogRepository
import ru.fefu.store.domain.CatalogConstants
import ru.fefu.store.domain.model.Category
import ru.fefu.store.domain.model.Product

class CatalogViewModel(private val repository: CatalogRepository, private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private val _uiState = MutableStateFlow<CatalogUiState>(CatalogUiState.Loading)
    val uiState: StateFlow<CatalogUiState> = _uiState.asStateFlow()

    private var allCategories: List<Category> = emptyList()
    private var allProducts: List<Product> = emptyList()

    private var isRefreshing = false
    private var isOffline = false
    private var refreshJob: Job? = null

    init {
        observeCatalogCache()
        refreshCatalog()
    }

    fun refreshCatalog() {
        if (refreshJob?.isActive == true) {
            return
        }

        refreshJob = viewModelScope.launch {
            val hasCache = repository.hasCachedCatalog()

            isRefreshing = true

            if (hasCache && allProducts.isNotEmpty()) {
                showSelectedCategory()
            } else {
                _uiState.value = CatalogUiState.Loading
            }

            when (repository.refreshCatalog()) {
                CatalogRefreshResult.Success -> {
                    isRefreshing = false
                    isOffline = false

                    if (allProducts.isNotEmpty()) {
                        showSelectedCategory()
                    }
                }

                CatalogRefreshResult.NoInternet -> {
                    isRefreshing = false
                    isOffline = true

                    val hasCatalogToShow = repository.hasCachedCatalog() || allProducts.isNotEmpty()

                    if (hasCatalogToShow) {
                        if (allProducts.isNotEmpty()) {
                            showSelectedCategory()
                        }
                    } else {
                        _uiState.value = CatalogUiState.Error(
                            title = "Нет сети",
                            description = "Каталог пока не сохранён на устройстве. Подключитесь к интернету и попробуйте снова.",
                        )
                    }
                }

                is CatalogRefreshResult.Error -> {
                    isRefreshing = false

                    val hasCatalogToShow = repository.hasCachedCatalog() || allProducts.isNotEmpty()

                    if (hasCatalogToShow) {
                        if (allProducts.isNotEmpty()) {
                            showSelectedCategory()
                        }
                    } else {
                        _uiState.value = CatalogUiState.Error(
                            title = "Не удалось загрузить каталог",
                            description = "Проверьте подключение к интернету и попробуйте снова.",
                        )
                    }
                }
            }
        }
    }

    fun selectCategory(categoryId: String) {
        savedStateHandle[KEY_SELECTED_CATEGORY_ID] = categoryId
        showCategory(categoryId)
    }

    private fun observeCatalogCache() {
        viewModelScope.launch {
            repository.observeCatalog().collect { catalog ->
                allCategories = catalog.categories
                allProducts = catalog.products

                if (allProducts.isNotEmpty()) {
                    val selectedCategoryId = getValidSelectedCategoryId()
                    savedStateHandle[KEY_SELECTED_CATEGORY_ID] = selectedCategoryId
                    showCategory(selectedCategoryId)
                }
            }
        }
    }

    private fun showSelectedCategory() {
        if (allProducts.isEmpty()) {
            return
        }

        val selectedCategoryId = getValidSelectedCategoryId()
        savedStateHandle[KEY_SELECTED_CATEGORY_ID] = selectedCategoryId
        showCategory(selectedCategoryId)
    }

    private fun getValidSelectedCategoryId(): String {
        val restoredCategoryId = savedStateHandle.get<String>(KEY_SELECTED_CATEGORY_ID)

        return when {
            restoredCategoryId != null && allCategories.any { category -> category.id == restoredCategoryId } -> {
                restoredCategoryId
            }

            allCategories.isNotEmpty() -> {
                allCategories.first().id
            }

            else -> {
                ""
            }
        }
    }

    private fun showCategory(categoryId: String) {
        val filteredProducts = when (categoryId) {
            CatalogConstants.NEW_CATEGORY_ID -> allProducts.filter { product ->
                product.tags.any { tag ->
                    tag.equals(CatalogConstants.NEW_TAG, ignoreCase = true)
                }
            }

            else -> allProducts.filter { product ->
                product.categoryId == categoryId
            }
        }

        _uiState.value = CatalogUiState.Content(
            categories = allCategories,
            selectedCategoryId = categoryId,
            products = filteredProducts,
            isRefreshing = isRefreshing,
            isOffline = isOffline,
        )
    }

    class Factory(private val repository: CatalogRepository) : ViewModelProvider.Factory {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            val savedStateHandle = extras.createSavedStateHandle()

            return CatalogViewModel(
                repository = repository,
                savedStateHandle = savedStateHandle,
            ) as T
        }
    }

    private companion object {
        const val KEY_SELECTED_CATEGORY_ID = "selected_category_id"
    }
}
