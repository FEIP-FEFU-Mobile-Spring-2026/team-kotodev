package ru.fefu.store.data.local

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import ru.fefu.store.domain.model.CatalogData
import ru.fefu.store.domain.model.Category
import ru.fefu.store.domain.model.Product
import ru.fefu.store.domain.model.ProductSize

class AssetCatalogDataSource(private val context: Context) {

    suspend fun loadCatalog(): CatalogData = withContext(Dispatchers.IO) {
        val json = context.assets
            .open(PRODUCTS_FILE_NAME)
            .bufferedReader()
            .use { it.readText() }

        val root = JSONObject(json)

        val categories = parseCategories(root)
        val products = parseProducts(root)

        CatalogData(
            categories = categories,
            products = products,
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
                        name = categoryObject.getString("name"),
                    ),
                )
            }
        }
    }

    private fun parseProducts(root: JSONObject): List<Product> {
        val itemsArray = root.getJSONArray("items")

        return buildList {
            for (index in 0 until itemsArray.length()) {
                val itemObject = itemsArray.getJSONObject(index)

                val tagsArray = itemObject.getJSONArray("tags")
                val tags = buildList {
                    for (tagIndex in 0 until tagsArray.length()) {
                        add(tagsArray.getString(tagIndex))
                    }
                }

                val sizesArray = itemObject.getJSONArray("sizes")
                val sizes = buildList {
                    for (sizeIndex in 0 until sizesArray.length()) {
                        val sizeObject = sizesArray.getJSONObject(sizeIndex)

                        add(
                            ProductSize(
                                id = sizeObject.getString("id"),
                                name = sizeObject.getString("name"),
                            ),
                        )
                    }
                }

                add(
                    Product(
                        id = itemObject.getString("id"),
                        name = itemObject.getString("name"),
                        shortDescription = itemObject.getString("shortDescription"),
                        longDescription = itemObject.getString("longDescription"),
                        priceInKopecks = itemObject.getLong("priceInKopecks"),
                        imageUrl = itemObject.getString("imageUrl"),
                        tags = tags,
                        sizes = sizes,
                        categoryId = itemObject.getString("categoryId"),
                        material = itemObject.getString("material"),
                        weight = itemObject.getString("weight"),
                        season = itemObject.getString("season"),
                        countryOfOrigin = itemObject.getString("countryOfOrigin"),
                    ),
                )
            }
        }
    }

    private companion object {
        const val PRODUCTS_FILE_NAME = "products.json"
    }
}
