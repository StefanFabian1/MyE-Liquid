package sk.sfabian.myeliquid.repository

import sk.sfabian.myeliquid.repository.api.ApiClient
import sk.sfabian.myeliquid.repository.room.AppDatabase

class Repository(
    private val database: AppDatabase
) {
    suspend fun fetchAndStoreAllData() {
        val repository = IngredientInventoryRepository(
            database.ingredientDao(), ApiClient.ingredientApi,
            movementDao = database.movementDao(),
            categoryDao = database.categoryDao(),
            subcategoryDao = database.subCategoryDao()
        )
        repository.fetchAndStoreIngredients()
        //TODO ak pribudnu dalsie entity zavolame ich aktualizaciu
    }
}