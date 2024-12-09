package sk.sfabian.myeliquid.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import sk.sfabian.myeliquid.repository.IngredientInventoryRepository
import sk.sfabian.myeliquid.repository.model.Ingredient

class IngredientInventoryViewModel(private val repository: IngredientInventoryRepository) : ViewModel() {

    val ingredients: StateFlow<List<Ingredient>> = repository.ingredients.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun fetchIngredients() {
        viewModelScope.launch {
            repository.fetchAndStoreIngredients()
        }
    }

    fun addIngredient(ingredient: Ingredient) {
        viewModelScope.launch {
            repository.addIngredient(ingredient)
        }
    }

    fun deleteIngredient(ingredient: Ingredient) {
        viewModelScope.launch {
            repository.deleteIngredient(ingredient)
        }
    }
}