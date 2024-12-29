package sk.sfabian.myeliquid.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import sk.sfabian.myeliquid.repository.IngredientInventoryRepository
import sk.sfabian.myeliquid.repository.api.IngredientSseHandler
import sk.sfabian.myeliquid.repository.model.Category
import sk.sfabian.myeliquid.repository.model.Ingredient
import sk.sfabian.myeliquid.repository.model.Movement

@OptIn(FlowPreview::class)
class IngredientInventoryViewModel(
    private val repository: IngredientInventoryRepository,
    private val sseHandler: IngredientSseHandler
) :
    ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _ingredients = MutableStateFlow<List<Ingredient>>(emptyList())
    val ingredients: StateFlow<List<Ingredient>> = _ingredients

    private val _movements = MutableStateFlow<List<Movement>>(emptyList())
    val movements: StateFlow<List<Movement>> = _movements

    init {
        viewModelScope.launch {
            sseHandler.onIngredientsUpdated = {
                refreshIngredients()
            }

            searchQuery
                .debounce(500)
                .distinctUntilChanged()
                .collect { query ->
                    _ingredients.value = if (query.isBlank()) {
                        repository.getIngredients().firstOrNull() ?: emptyList()
                    } else {
                        repository.searchIngredients(query).firstOrNull() ?: emptyList()
                    }
                }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun fetchIngredients() {
        viewModelScope.launch {
            repository.fetchAndStoreIngredients()
        }
    }

    fun deleteIngredient(ingredient: Ingredient) {
        viewModelScope.launch {
            repository.deleteIngredient(ingredient)
        }
    }

    fun addNewIngredient(
        name: String,
        category: Category?,
        quantity: Double,
        unit: String,
        price: Double
    ) {
        if (name.isBlank() || quantity <= 0 || unit.isBlank() || price < 0) {
            println("Neplatné vstupné údaje")
            return
        }

        viewModelScope.launch {
            try {
                val newIngredient = Ingredient(
                    name = name,
                    quantity = quantity,
                    unit = unit,
                    unitPrice = price,
                    category = category?.text,
                    subcategory = null, // Zatiaľ null,
                    brand = null, // Voliteľné
                    description = null // Voliteľné
                )
                repository.addIngredient(newIngredient)
            } catch (e: Exception) {
                println("Chyba pri pridávaní ingrediencie: ${e.message}")
            }
        }
    }

    val categories: StateFlow<List<Category>> = repository.getCategories()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addNewCategory(name: String) {
        viewModelScope.launch {
            //repository.insertCategory(Category(text = name))
        }
    }

    fun navigateToMovements(ingredient: Ingredient, navController: NavController) {
        navController.navigate("movements/${ingredient.mongoId}") {

        }
    }

    suspend fun isCategoryUsed(category: Category): Boolean {
        return repository.isCategoryUsed(category)
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            if (!repository.isCategoryUsed(category)) {
                //repository.deleteCategory(category)
            } else {
                Log.e("Category", "Kategóriu nie je možné zmazať, je používaná.")
            }
        }
    }

    fun updateIngredient(ingredient: Ingredient) {
        viewModelScope.launch {
            repository.updateIngredient(ingredient)
        }
    }

    fun fetchMovementsForIngredient(ingredientId: String) {
        viewModelScope.launch {
            val fetchedMovements = repository.getMovementsForIngredient(ingredientId)
            _movements.value = fetchedMovements
        }
    }

    fun addIngredientMovement(
        ingredientId: String,
        quantity: Double,
        totalPrice: Double,
        type: String
    ) {
        viewModelScope.launch {
            val movement = Movement(
                quantity = quantity,
                totalPrice = totalPrice,
                type = type,
                timestamp = System.currentTimeMillis()
            )
            repository.addMovement(ingredientId, movement)
        }
    }

    fun clearMovementsCache() {
        _movements.value = emptyList()
    }

    private fun refreshIngredients() {
        viewModelScope.launch {
            _ingredients.value = repository.getIngredients().firstOrNull() ?: emptyList()
        }
    }
}