package ru.fefu.store.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONArray
import org.json.JSONObject
import ru.fefu.store.domain.model.Product
import ru.fefu.store.domain.model.ProductSize

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val shortDescription: String,
    val longDescription: String,
    val priceInKopecks: Long,
    val imageUrl: String,
    val tagsJson: String,
    val sizesJson: String,
    val categoryId: String,
    val material: String,
    val weight: String,
    val season: String,
    val countryOfOrigin: String,
    val sortOrder: Int,
)

fun ProductEntity.toDomain(): Product = Product(
    id = id,
    name = name,
    shortDescription = shortDescription,
    longDescription = longDescription,
    priceInKopecks = priceInKopecks,
    imageUrl = imageUrl,
    tags = decodeTags(tagsJson),
    sizes = decodeSizes(sizesJson),
    categoryId = categoryId,
    material = material,
    weight = weight,
    season = season,
    countryOfOrigin = countryOfOrigin,
)

fun Product.toEntity(sortOrder: Int): ProductEntity = ProductEntity(
    id = id,
    name = name,
    shortDescription = shortDescription,
    longDescription = longDescription,
    priceInKopecks = priceInKopecks,
    imageUrl = imageUrl,
    tagsJson = encodeTags(tags),
    sizesJson = encodeSizes(sizes),
    categoryId = categoryId,
    material = material,
    weight = weight,
    season = season,
    countryOfOrigin = countryOfOrigin,
    sortOrder = sortOrder,
)

private fun encodeTags(tags: List<String>): String {
    val jsonArray = JSONArray()

    tags.forEach { tag ->
        jsonArray.put(tag)
    }

    return jsonArray.toString()
}

private fun decodeTags(value: String): List<String> {
    val jsonArray = JSONArray(value)

    return buildList {
        for (index in 0 until jsonArray.length()) {
            add(jsonArray.getString(index))
        }
    }
}

private fun encodeSizes(sizes: List<ProductSize>): String {
    val jsonArray = JSONArray()

    sizes.forEach { size ->
        jsonArray.put(
            JSONObject()
                .put("id", size.id)
                .put("name", size.name),
        )
    }

    return jsonArray.toString()
}

private fun decodeSizes(value: String): List<ProductSize> {
    val jsonArray = JSONArray(value)

    return buildList {
        for (index in 0 until jsonArray.length()) {
            val sizeObject = jsonArray.getJSONObject(index)

            add(
                ProductSize(
                    id = sizeObject.getString("id"),
                    name = sizeObject.getString("name"),
                ),
            )
        }
    }
}
