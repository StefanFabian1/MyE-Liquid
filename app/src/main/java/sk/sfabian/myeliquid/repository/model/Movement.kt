package sk.sfabian.myeliquid.repository.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movements")
data class Movement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mongoId: String? = null,
    val ingredientLocalId: Long,
    val quantity: Double,
    val totalPrice: Double,
    val type: String,
    val timestamp: Long = System.currentTimeMillis()
)