package sk.sfabian.myeliquid.repository.api

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import sk.sfabian.myeliquid.repository.model.Ingredient
import sk.sfabian.myeliquid.repository.model.Movement

interface IngredientInventoryApi {
    @GET("ingredients")
    suspend fun fetchIngredients(): List<Ingredient>

    @GET("ingredients/ingredient/{id}")
    suspend fun fetchIngredient(@Path("id") id: String): Ingredient

    @DELETE("ingredients/{id}")
    suspend fun deleteIngredient(@Path("id") id: String)

    @Streaming
    @GET("ingredients/stream")
    fun streamIngredients(): Call<ResponseBody>

    @POST("ingredients/ingredient")
    suspend fun addIngredient(@Body ingredient: Ingredient): Ingredient

    @PUT("ingredients/ingredient/{id}")
    suspend fun updateIngredient(
        @Path("id") ingredientId: String,
        @Body ingredient: Ingredient
    )

    @GET("ingredients/{id}/movements")
    suspend fun getMovements(
        @Path("id") ingredientId: String
    ): List<Movement>

    @POST("ingredients/{id}/movements")
    suspend fun addMovement(
        @Path("id") ingredientId: String,
        @Query("quantity") quantity: Double,
        @Query("totalPrice") totalPrice: Double,
        @Query("type") type: String
    ): Response<Unit>

    @GET("ingredients/search")
    suspend fun searchIngredients(@Query("query") query: String): List<Ingredient>
}
