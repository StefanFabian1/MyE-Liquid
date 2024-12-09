import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import sk.sfabian.myeliquid.ui.activity.Ingredient

class IngredientInventoryViewModel : ViewModel() {
    private val _ingredients = MutableStateFlow<List<Ingredient>>(
        listOf(
            Ingredient(1, "Nicotine", "50ml"),
            Ingredient(2, "VG", "200ml"),
            Ingredient(3, "PG", "150ml"),
            Ingredient(4, "Strawberry Flavor", "30ml")
        )
    )
    val ingredients: StateFlow<List<Ingredient>> = _ingredients.asStateFlow()

    fun addIngredient(ingredient: Ingredient) {
        _ingredients.value += ingredient
    }

    fun updateIngredient(ingredient: Ingredient) {
        _ingredients.value = _ingredients.value.map {
            if (it.id == ingredient.id) ingredient else it
        }
    }

    fun deleteIngredient(ingredient: Ingredient) {
        _ingredients.value = _ingredients.value.filter { it.id != ingredient.id }
    }
}