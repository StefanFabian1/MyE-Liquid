package sk.sfabian.myeliquid.repository.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    //private const val BASE_URL = "https://myeliquid-ef5596600b57.herokuapp.com/api/"
    private const val BASE_URL = "http://10.0.2.2:8080/api/"

    private val client = OkHttpClient.Builder()
        .readTimeout(2, TimeUnit.MINUTES) // Timeout na čítanie dát
        .writeTimeout(2, TimeUnit.MINUTES) // Timeout na odosielanie dát
        .connectTimeout(30, TimeUnit.SECONDS) // Timeout na pripojenie
        .build()

    val ingredientApi: IngredientInventoryApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IngredientInventoryApi::class.java)
    }
}
