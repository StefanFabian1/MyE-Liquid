package sk.sfabian.myeliquid.repository.api

import sk.sfabian.myeliquid.repository.model.Ingredient

class MockIngredientApi : IngredientInventoryApi {
    private val ingredients = mutableListOf(
        Ingredient(1, "Nicotine", "50ml"),
        Ingredient(2, "VG", "200ml"),
        Ingredient(3, "PG", "150ml")
    )

    override suspend fun fetchIngredients(): List<Ingredient> {
        return ingredients
    }

    override suspend fun addIngredient(ingredient: Ingredient) {
        ingredients.add(ingredient)
    }

    override suspend fun deleteIngredient(id: Int) {
        ingredients.removeIf { it.id == id }
    }
}
