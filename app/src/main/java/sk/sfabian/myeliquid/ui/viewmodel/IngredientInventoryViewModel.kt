import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class Ingredient(
    val id: Int,
    val name: String,
    val quantity: String
)

class IngredientInventoryViewModel : ViewModel() {

    // MutableStateFlow pre internú manipuláciu
    private val _ingredients = MutableStateFlow<List<Ingredient>>(emptyList())
    // Verejný StateFlow pre pozorovanie z UI
    val ingredients: StateFlow<List<Ingredient>> = _ingredients

    init {
        loadIngredients()
    }

    // Načítanie ingrediencií (simulované dáta)
    private fun loadIngredients() {
        viewModelScope.launch {
            _ingredients.value = listOf(
                Ingredient(1, "Nicotine", "50ml"),
                Ingredient(2, "VG", "200ml"),
                Ingredient(3, "PG", "150ml"),
                Ingredient(4, "Strawberry Flavor", "30ml")
            )
        }
    }

    // Pridanie ingrediencie
    fun addIngredient(ingredient: Ingredient) {
        viewModelScope.launch {
            _ingredients.value = _ingredients.value + ingredient
        }
    }

    // Odstránenie ingrediencie
    fun deleteIngredient(ingredient: Ingredient) {
        viewModelScope.launch {
            _ingredients.value = _ingredients.value.filter { it.id != ingredient.id }
        }
    }
}
