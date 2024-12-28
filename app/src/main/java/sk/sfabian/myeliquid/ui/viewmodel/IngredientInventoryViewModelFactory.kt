package sk.sfabian.myeliquid.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import sk.sfabian.myeliquid.repository.IngredientInventoryRepository
import sk.sfabian.myeliquid.repository.api.IngredientSseHandler

class IngredientInventoryViewModelFactory(
    private val repository: IngredientInventoryRepository,
    private val sseHandler: IngredientSseHandler
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IngredientInventoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return IngredientInventoryViewModel(
                repository,
                sseHandler
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}