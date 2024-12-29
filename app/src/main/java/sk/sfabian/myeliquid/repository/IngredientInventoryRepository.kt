package sk.sfabian.myeliquid.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import sk.sfabian.myeliquid.Configuration
import sk.sfabian.myeliquid.repository.api.IngredientInventoryApi
import sk.sfabian.myeliquid.repository.model.Category
import sk.sfabian.myeliquid.repository.model.Ingredient
import sk.sfabian.myeliquid.repository.model.Movement
import sk.sfabian.myeliquid.repository.model.Subcategory
import sk.sfabian.myeliquid.repository.room.CategoryDao
import sk.sfabian.myeliquid.repository.room.IngredientInventoryDao
import sk.sfabian.myeliquid.repository.room.SubcategoryDao

class IngredientInventoryRepository(
    private val ingredientDao: IngredientInventoryDao,
    private val ingredientApi: IngredientInventoryApi,
    private val categoryDao: CategoryDao,
    private val subcategoryDao: SubcategoryDao
) {
    fun getIngredients(): Flow<List<Ingredient>> = ingredientDao.getAllIngredients()

    suspend fun fetchAndStoreIngredients() {
        val mutex = Mutex()
        mutex.withLock {
            val remoteIngredients = ingredientApi.fetchIngredients()

            val categories = remoteIngredients
                .mapNotNull { it.category }
                .toSet()

            val subcategories = remoteIngredients
                .mapNotNull { it.subcategory }
                .toSet()

            ingredientDao.replaceAllIngredients(remoteIngredients)
            categoryDao.replaceAllCategories(categories.map { Category(text = it) })
            subcategoryDao.replaceAllSubcategories(subcategories.map { Subcategory(text = it) })
        }
    }

    suspend fun addMovement(ingredientId: String, movement: Movement) {
        try {
            ingredientApi.addMovement(
                ingredientId,
                quantity = movement.quantity,
                totalPrice = movement.totalPrice,
                type = movement.type
            )
            if (Configuration.getBoolean("enable_sse", false)) {
                val updatedIngredient = ingredientApi.fetchIngredient(ingredientId)

                val localIngredient = ingredientDao.getIngredientByMongoIdAndName(
                    mongoId = ingredientId,
                    name = updatedIngredient.name
                )

                if (localIngredient != null) {
                    val ingredientToUpdate = updatedIngredient.copy(localId = localIngredient.localId)
                    ingredientDao.update(ingredientToUpdate)
                } else {
                    println("No matching local ingredient found for mongoId: $ingredientId and name: ${updatedIngredient.name}")
                }
            }
        } catch (e: Exception) {
            println("Error adding movement: ${e.message}")
            // Ak synchronizácia zlyhá, môžete pohyb uložiť lokálne alebo spraviť retry mechanizmus
        }
    }


    suspend fun deleteIngredient(ingredient: Ingredient) {
        withContext(Dispatchers.IO) {
            try {
                ingredient.mongoId?.let { mongoId ->
                    ingredientApi.deleteIngredient(mongoId)
                }
                if (Configuration.getBoolean("enable_sse", false)) {
                    ingredientDao.deleteIngredient(ingredient.localId)
                }
            } catch (e: Exception) {
                println("Chyba pri mazani ingrediencie: ${e.message}")
            }
        }
    }

    suspend fun updateIngredient(ingredient: Ingredient) {
        withContext(Dispatchers.IO) {
            try {
                ingredient.mongoId?.let { mongoId ->
                    ingredientApi.updateIngredient(mongoId, ingredient)
                }
                if (Configuration.getBoolean("enable_sse", false)) {
                    ingredientDao.update(ingredient)
                }
            } catch (e: Exception) {
                println("Chyba pri aktualizácii ingrediencie: ${e.message}")
            }
        }
    }

    fun getCategories(): Flow<List<Category>> = categoryDao.getAllCategories()

    suspend fun isCategoryUsed(category: Category): Boolean {
        return ingredientDao.countIngredientsInCategory(category.text) > 0
    }

    suspend fun addIngredient(newIngredient: Ingredient) {
        withContext(Dispatchers.IO) {
            try {

                val savedIngredient = ingredientApi.addIngredient(
                    newIngredient.copy(quantity = 0.0, unitPrice = 0.0)
                )

                val initialMovementQuantity = newIngredient.quantity
                val initialMovementTotalPrice = newIngredient.unitPrice


                savedIngredient.mongoId?.let { mongoId ->
                    addMovement(
                        mongoId, Movement(
                            quantity = initialMovementQuantity,
                            totalPrice = initialMovementTotalPrice,
                            type = "ADD",
                            timestamp = System.currentTimeMillis()
                        )
                    )
                }
                if (Configuration.getBoolean("enable_sse", false)) {
                    val updatedIngredient = savedIngredient.copy(
                        quantity = newIngredient.quantity,
                        unitPrice = newIngredient.unitPrice / newIngredient.quantity
                    )

                    ingredientDao.insert(updatedIngredient)
                }
            } catch (e: Exception) {
                println("Chyba pri pridávaní ingrediencie: ${e.message}")
            }
        }
    }

    suspend fun getMovementsForIngredient(ingredientId: String): List<Movement> {
        return try {
            ingredientApi.getMovements(ingredientId)
        } catch (e: Exception) {
            println("Error fetching movements: ${e.message}")
            emptyList()
        }
    }

    suspend fun searchIngredients(query: String): Flow<List<Ingredient>> {
        return try {
            val onlineResults = ingredientApi.searchIngredients(query)
            flowOf(onlineResults)
        } catch (e: Exception) {
            println("Error searching ingredients online: ${e.message}")
            flowOf(emptyList())
        }
    }
}
