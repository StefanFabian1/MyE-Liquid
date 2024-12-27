package sk.sfabian.myeliquid.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import sk.sfabian.myeliquid.repository.IngredientInventoryRepository
import sk.sfabian.myeliquid.repository.model.Category
import sk.sfabian.myeliquid.repository.model.Ingredient

class IngredientInventoryViewModel(private val repository: IngredientInventoryRepository) :
    ViewModel() {

    val ingredients: StateFlow<List<Ingredient>> = repository.getIngredients()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

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

    fun addNewIngredient(name: String, quantity: Double, unit: String, price: Double) {
        println("TODO")
    }

    fun addIngredientMovement(ingredient: Ingredient, quantityAdded: Double, totalPrice: Double) {
        println("TODO")
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
        navController.navigate("movements/" + ingredient.localId) {
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
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

}