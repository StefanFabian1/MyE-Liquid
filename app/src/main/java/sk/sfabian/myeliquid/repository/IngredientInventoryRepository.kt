package sk.sfabian.myeliquid.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import sk.sfabian.myeliquid.repository.api.IngredientInventoryApi
import sk.sfabian.myeliquid.repository.model.Ingredient
import sk.sfabian.myeliquid.repository.room.IngredientInventoryDao

class IngredientInventoryRepository(
    private val ingredientDao: IngredientInventoryDao,
    private val ingredientApi: IngredientInventoryApi
) {

    private val mutex = Mutex()

    val ingredients: Flow<List<Ingredient>> = ingredientDao.getAllIngredients()

    suspend fun fetchAndStoreIngredients() {
        mutex.withLock {
            val remoteIngredients = ingredientApi.fetchIngredients()
            Log.d("API Response", "Fetched ingredients: $remoteIngredients")
            ingredientDao.replaceAllIngredients(remoteIngredients)
        }
    }

    suspend fun addIngredient(ingredient: Ingredient) {
        ingredientApi.addIngredient(ingredient)
        ingredientDao.insertIngredient(ingredient)
    }

    suspend fun deleteIngredient(ingredient: Ingredient) {
        ingredientApi.deleteIngredient(ingredient.id.toHexString())
        ingredientDao.deleteIngredient(ingredient.id)
    }
}
