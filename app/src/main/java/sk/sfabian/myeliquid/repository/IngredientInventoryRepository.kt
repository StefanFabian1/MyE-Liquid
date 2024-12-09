package sk.sfabian.myeliquid.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import sk.sfabian.myeliquid.repository.api.MockIngredientApi
import sk.sfabian.myeliquid.repository.model.Ingredient
import sk.sfabian.myeliquid.repository.room.IngredientInventoryDao

class IngredientInventoryRepository(
    private val ingredientDao: IngredientInventoryDao,
    private val ingredientApi: MockIngredientApi
) {

    private val mutex = Mutex()

    val ingredients: Flow<List<Ingredient>> = ingredientDao.getAllIngredients()

    suspend fun fetchAndStoreIngredients() {
        mutex.withLock {
            val remoteIngredients = ingredientApi.fetchIngredients()
            ingredientDao.replaceAllIngredients(remoteIngredients)
        }
    }

    suspend fun addIngredient(ingredient: Ingredient) {
        ingredientApi.addIngredient(ingredient)
        ingredientDao.insertIngredient(ingredient)
    }

    suspend fun deleteIngredient(ingredient: Ingredient) {
        ingredientApi.deleteIngredient(ingredient.id)
        ingredientDao.deleteIngredient(ingredient.id)
    }
}
