package ru.fefu.store.data.network

class CatalogApiException(
    val statusCode: Int,
    message: String
) : Exception(message)