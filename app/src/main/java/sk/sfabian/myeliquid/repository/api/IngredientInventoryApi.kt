package sk.sfabian.myeliquid.repository.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import sk.sfabian.myeliquid.repository.model.Ingredient

interface IngredientInventoryApi {
    @GET("ingredients")
    suspend fun fetchIngredients(): List<Ingredient>

    @POST("ingredients")
    suspend fun addIngredient(@Body ingredient: Ingredient)

    @DELETE("ingredients/{id}")
    suspend fun deleteIngredient(@Path("id") id: String)

    @Streaming
    @GET("ingredients/stream")
    fun streamIngredients(): Call<ResponseBody>
}
