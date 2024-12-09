package sk.sfabian.myeliquid.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import sk.sfabian.myeliquid.repository.IngredientInventoryRepository

class IngredientInventoryViewModelFactory(
    private val repository: IngredientInventoryRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IngredientInventoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return IngredientInventoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}