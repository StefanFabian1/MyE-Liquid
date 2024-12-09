package sk.sfabian.myeliquid.repository.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://your-api-endpoint.com/"

    val ingredientApi: IngredientInventoryApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IngredientInventoryApi::class.java)
    }
}
