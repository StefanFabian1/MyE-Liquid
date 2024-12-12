package sk.sfabian.myeliquid.repository.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.bson.types.ObjectId

@Entity(tableName = "ingredients")
data class Ingredient(
    @PrimaryKey val id: ObjectId = ObjectId.get(),
    val name: String,
    val quantity: String
)