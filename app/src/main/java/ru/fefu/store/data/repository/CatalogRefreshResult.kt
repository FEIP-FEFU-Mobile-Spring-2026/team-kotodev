package ru.fefu.store.data.repository

sealed interface CatalogRefreshResult {

    data object Success : CatalogRefreshResult

    data object NoInternet : CatalogRefreshResult

    data class Error(
        val throwable: Throwable
    ) : CatalogRefreshResult
}