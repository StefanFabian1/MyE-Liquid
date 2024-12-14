package sk.sfabian.myeliquid.repository.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import sk.sfabian.myeliquid.repository.model.Ingredient

@Dao
interface IngredientInventoryDao {

        @Query("SELECT * FROM ingredients")
        fun getAllIngredients(): Flow<List<Ingredient>>

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertIngredient(ingredient: Ingredient)

        @Query("DELETE FROM ingredients WHERE id = :id")
        suspend fun deleteIngredient(id: String)

        @Transaction
        suspend fun replaceAllIngredients(ingredients: List<Ingredient>) {
            deleteAll()
            insertAll(ingredients)
        }

        @Query("DELETE FROM ingredients")
        suspend fun deleteAll()

        @Insert
        suspend fun insertAll(ingredients: List<Ingredient>)
    }
