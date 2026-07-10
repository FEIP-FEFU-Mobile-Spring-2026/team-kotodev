package ru.fefu.store.data.network

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import ru.fefu.store.domain.model.CatalogData
import ru.fefu.store.domain.model.Category
import ru.fefu.store.domain.model.Product
import ru.fefu.store.domain.model.ProductSize

class NetworkCatalogDataSource {

    suspend fun loadCatalog(): CatalogData = withContext(Dispatchers.IO) {
        val connection = (URL(CatalogApiConfig.CATALOG_URL).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = TIMEOUT_MILLIS
            readTimeout = TIMEOUT_MILLIS
            setRequestProperty("Authorization", "Bearer ${CatalogApiConfig.CATALOG_TOKEN}")
            setRequestProperty("Accept", "application/json")
        }

        try {
            val statusCode = connection.responseCode

            if (statusCode !in HTTP_SUCCESS_RANGE) {
                throw CatalogApiException(
                    statusCode = statusCode,
                    message = "Catalog API returned HTTP $statusCode"
                )
            }

            val responseBody = connection.inputStream
                .bufferedReader()
                .use { reader -> reader.readText() }

            parseCatalog(JSONObject(responseBody))
        } catch (exception: CatalogApiException) {
            throw exception
        } catch (exception: IOException) {
            throw exception
        } finally {
            connection.disconnect()
        }
    }

    private fun parseCatalog(root: JSONObject): CatalogData {
        return CatalogData(
            categories = parseCategories(root),
            products = parseProducts(root)
        )
    }

    private fun parseCategories(root: JSONObject): List<Category> {
        val categoriesArray = root.getJSONArray("categories")

        return buildList {
            for (index in 0 until categoriesArray.length()) {
                val categoryObject = categoriesArray.getJSONObject(index)

                add(
                    Category(
                        id = categoryObject.getString("id"),
                        name = categoryObject.getString("name")
                    )
                )
            }
        }
    }

    private fun parseProducts(root: JSONObject): List<Product> {
        val itemsArray = root.getJSONArray("items")

        return buildList {
            for (index in 0 until itemsArray.length()) {
                val itemObject = itemsArray.getJSONObject(index)

                add(
                    Product(
                        id = itemObject.getString("id"),
                        name = itemObject.getString("name"),
                        shortDescription = itemObject.getString("shortDescription"),
                        longDescription = itemObject.getString("longDescription"),
                        priceInKopecks = itemObject.getLong("priceInKopecks"),
                        imageUrl = itemObject.getString("imageUrl"),
                        tags = parseTags(itemObject),
                        sizes = parseSizes(itemObject),
                        categoryId = itemObject.getString("categoryId"),
                        material = itemObject.getString("material"),
                        weight = itemObject.getString("weight"),
                        season = itemObject.getString("season"),
                        countryOfOrigin = itemObject.getString("countryOfOrigin")
                    )
                )
            }
        }
    }

    private fun parseTags(itemObject: JSONObject): List<String> {
        val tagsArray = itemObject.getJSONArray("tags")

        return buildList {
            for (index in 0 until tagsArray.length()) {
                add(tagsArray.getString(index))
            }
        }
    }

    private fun parseSizes(itemObject: JSONObject): List<ProductSize> {
        val sizesArray = itemObject.getJSONArray("sizes")

        return buildList {
            for (index in 0 until sizesArray.length()) {
                val sizeObject = sizesArray.getJSONObject(index)

                add(
                    ProductSize(
                        id = sizeObject.getString("id"),
                        name = sizeObject.getString("name")
                    )
                )
            }
        }
    }

    private companion object {
        const val TIMEOUT_MILLIS = 15_000
        val HTTP_SUCCESS_RANGE = 200..299
    }
}