package sk.sfabian.myeliquid.repository.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category (
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String
)