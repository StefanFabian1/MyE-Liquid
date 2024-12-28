package sk.sfabian.myeliquid.repository.api

import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import sk.sfabian.myeliquid.repository.model.Ingredient
import sk.sfabian.myeliquid.repository.room.IngredientInventoryDao

class IngredientSseHandler(
    private val api: IngredientInventoryApi,
    private val dao: IngredientInventoryDao
) {

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    var onIngredientsUpdated: (() -> Unit)? = null

    fun startListening() {
        val call = api.streamIngredients()
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        coroutineScope.launch {
                            val reader = body.charStream().buffered()
                            reader.useLines { lines ->
                                lines.forEach { line ->
                                    if (line.startsWith("data:")) {
                                        val data = line.removePrefix("data:").trim()
                                        handleSseEvent(data)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    restartListening()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                restartListening()
            }
        })
    }

    private fun handleSseEvent(data: String) {
        try {
            when (data.first()) {
                'A', 'U' -> {
                    val ingredient = parseJson<Ingredient>(data.substring(1))

                    coroutineScope.launch {
                        val localIngredient = ingredient.mongoId?.let {
                            dao.getIngredientByMongoIdAndName(
                                mongoId = it,
                                name = ingredient.name
                            )
                        }
                        if (localIngredient != null) {
                            val ingredientToUpdate = ingredient.copy(localId = localIngredient.localId)
                            dao.update(ingredientToUpdate)
                        } else {
                            dao.insertIngredient(ingredient)
                        }
                        onIngredientsUpdated?.invoke()
                    }
                }
                'D' -> {
                    val delData = parseJson<Map<String, String>>(data.substring(1))
                    val id = delData["_id"]
                    coroutineScope.launch {
                        if (id != null) {
                            dao.deleteIngredientByMongoId(id)
                            onIngredientsUpdated?.invoke()
                        }
                    }
                }

            }
        } catch (e: Exception) {
            Log.e("IngredientSseHandler", "Error parsing SSE data: $data", e)
        }
    }

    private inline fun <reified T> parseJson(json: String): T {
        return Gson().fromJson(json, T::class.java)
    }

    private fun restartListening() {
        Log.w("IngredientSseHandler", "SSE connection lost, attempting to reconnect...")
        coroutineScope.launch {
            delay(5000) // Pauza pred opätovným pripojením
            startListening()
        }
    }
}