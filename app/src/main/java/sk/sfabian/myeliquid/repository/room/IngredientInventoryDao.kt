package sk.sfabian.myeliquid.repository.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import sk.sfabian.myeliquid.repository.model.Category
import sk.sfabian.myeliquid.repository.model.Ingredient
import sk.sfabian.myeliquid.repository.model.Movement
import sk.sfabian.myeliquid.repository.model.Subcategory

@Dao
interface IngredientInventoryDao {

    //READ
    @Query("SELECT * FROM ingredients")
    fun getAllIngredients(): Flow<List<Ingredient>>

    @Query("SELECT * FROM ingredients WHERE name = :name")
    suspend fun getIngredientByName(name: String): Ingredient?

    @Query("UPDATE ingredients SET mongoId = :mongoId WHERE localId = :localId")
    suspend fun updateIngredientMongoId(localId: Long, mongoId: String)

    @Query("SELECT COUNT(*) FROM ingredients WHERE category = :categoryText")
    suspend fun countIngredientsInCategory(categoryText: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIngredient(ingredient: Ingredient): Long

    @Query("DELETE FROM ingredients WHERE localId = :id")
    suspend fun deleteIngredient(id: Long)

    @Query("DELETE FROM ingredients WHERE mongoId = :mongoId")
    suspend fun deleteIngredientByMongoId(mongoId: String)

    @Transaction
    suspend fun replaceAllIngredients(ingredients: List<Ingredient>) {
        deleteAll()
        insertAll(ingredients)
    }

    @Query("DELETE FROM ingredients")
    suspend fun deleteAll()

    @Insert
    suspend fun insertAll(ingredients: List<Ingredient>)

    @Insert
    suspend fun insert(ingredient: Ingredient)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(ingredient: Ingredient)

    @Query("SELECT * FROM ingredients WHERE mongoId = :mongoId AND name = :name LIMIT 1")
    suspend fun getIngredientByMongoIdAndName(mongoId: String, name: String): Ingredient?
}

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<Category>>

    @Transaction
    suspend fun replaceAllCategories(categories: List<Category>) {
        deleteAll()
        insertAll(categories)
    }

    @Query("DELETE FROM categories")
    suspend fun deleteAll()

    @Insert
    suspend fun insertAll(ingredients: List<Category>)
}

@Dao
interface SubcategoryDao {
    @Transaction
    suspend fun replaceAllSubcategories(subcategories: List<Subcategory>) {
        deleteAll()
        insertAll(subcategories)
    }

    @Query("DELETE FROM subcategories")
    suspend fun deleteAll()

    @Insert
    suspend fun insertAll(ingredients: List<Subcategory>)
}