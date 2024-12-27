package sk.sfabian.myeliquid.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import sk.sfabian.myeliquid.Configuration
import sk.sfabian.myeliquid.repository.api.IngredientInventoryApi
import sk.sfabian.myeliquid.repository.api.IngredientSseHandler
import sk.sfabian.myeliquid.repository.model.Category
import sk.sfabian.myeliquid.repository.model.Ingredient
import sk.sfabian.myeliquid.repository.model.Movement
import sk.sfabian.myeliquid.repository.model.Subcategory
import sk.sfabian.myeliquid.repository.room.CategoryDao
import sk.sfabian.myeliquid.repository.room.IngredientInventoryDao
import sk.sfabian.myeliquid.repository.room.MovementDao
import sk.sfabian.myeliquid.repository.room.SubcategoryDao

class IngredientInventoryRepository(
    private val ingredientDao: IngredientInventoryDao,
    private val ingredientApi: IngredientInventoryApi,
    private val movementDao: MovementDao,
    private val categoryDao: CategoryDao,
    private val subcategoryDao: SubcategoryDao
) {

    private val sseHandler = IngredientSseHandler(ingredientApi, ingredientDao)

    init {
        if (Configuration.getBoolean("enable_sse", false)) {
            sseHandler.startListening()
        }
    }

    fun getIngredients(): Flow<List<Ingredient>> = ingredientDao.getAllIngredients()

    //TODO fetchChangedIngredients - pridame timestamp do ingrediencie a Sync preferences
    /* sablona - podla nej pojdeme, fetchAndStoreIngredients sa bude volat le pri spusteni apky
    class SyncPreferences(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("sync_prefs", Context.MODE_PRIVATE)

    var lastUpdated: Long
        get() = sharedPreferences.getLong("last_updated", 0)
        set(value) = sharedPreferences.edit().putLong("last_updated", value).apply()
    }
     */
    suspend fun fetchAndStoreIngredients() {
        val mutex = Mutex()
        mutex.withLock {
            val remoteIngredients = ingredientApi.fetchIngredients()

            val categories = remoteIngredients.map { it.category }.toSet()
            val subcategories = remoteIngredients
                .mapNotNull { it.subcategory }
                .toSet()

            ingredientDao.replaceAllIngredients(remoteIngredients)
            categoryDao.replaceAllCategories(categories.map { Category(text = it) })
            subcategoryDao.replaceAllSubcategories(subcategories.map { Subcategory(text = it) })
        }
    }

    suspend fun addMovement(name: String, unit: String, quantity: Double, totalPrice: Double) {
        val ingredient = ingredientDao.getIngredientByName(name) ?: run {
            val newIngredient = Ingredient(
                name = name,
                unit = unit,
                unitPrice = 0.0,
                quantity = quantity,
                category = "základ",
                subcategory = null,
                brand = null,
                description = null
            )
            val localId = ingredientDao.insertIngredient(newIngredient)
            newIngredient.copy(localId = localId)
        }
        val movement = Movement(
            ingredientLocalId = ingredient.localId,
            quantity = quantity,
            totalPrice = totalPrice,
            type = "ADD"
        )
        movementDao.insertMovement(movement)

        // Synchronizácia s MongoDB
        try {
            val ingredientMongoId = ingredientApi.addIngredientMovement(
                ingredient,
                movement
            )
            if (!ingredientMongoId.isNullOrBlank()) {
                ingredientDao.updateIngredientMongoId(ingredient.localId, ingredientMongoId)
            }

        } catch (e: Exception) {
            // TODO: univerzalne hlasky - server nie je dostupny / nemate pristup na internet / interna chyba servra
            // Ak synchronizácia zlyhá, pohyb ostane lokálne
            e.printStackTrace()
        }
    }

    suspend fun deleteIngredient(ingredient: Ingredient) {
        ingredient.mongoId?.let { ingredientApi.deleteIngredient(it) }
        //ingredientDao.deleteIngredient(ingredient.id)
    }

    fun getCategories(): Flow<List<Category>> = categoryDao.getAllCategories()

    suspend fun isCategoryUsed(category: Category): Boolean {
        return ingredientDao.countIngredientsInCategory(category.text) > 0
    }
}
