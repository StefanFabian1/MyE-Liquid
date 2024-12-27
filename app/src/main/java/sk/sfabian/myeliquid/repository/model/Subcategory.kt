package sk.sfabian.myeliquid.repository.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subcategories")
data class Subcategory (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String
)
