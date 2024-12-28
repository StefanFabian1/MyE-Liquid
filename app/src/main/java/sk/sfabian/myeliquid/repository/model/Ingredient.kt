package sk.sfabian.myeliquid.repository.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ingredients")
data class Ingredient(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0,
    val mongoId: String? = null,
    val name: String,
    val quantity: Double,
    val unit: String,
    val unitPrice: Double,
    val category: String?,
    val subcategory: String?,
    val brand: String?,
    val description: String?
)