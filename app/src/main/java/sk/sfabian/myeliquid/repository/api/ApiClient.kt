package sk.sfabian.myeliquid.repository.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://myeliquid-ef5596600b57.herokuapp.com/api/"
    //local
    //private const val BASE_URL = "http://10.0.2.2:8080/api/"

    val ingredientApi: IngredientInventoryApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IngredientInventoryApi::class.java)
    }
}
