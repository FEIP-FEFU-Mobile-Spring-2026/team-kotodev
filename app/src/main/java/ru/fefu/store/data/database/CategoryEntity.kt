package ru.fefu.store.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.fefu.store.domain.model.Category

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val sortOrder: Int
)

fun CategoryEntity.toDomain(): Category {
    return Category(
        id = id,
        name = name
    )
}

fun Category.toEntity(sortOrder: Int): CategoryEntity {
    return CategoryEntity(
        id = id,
        name = name,
        sortOrder = sortOrder
    )
}