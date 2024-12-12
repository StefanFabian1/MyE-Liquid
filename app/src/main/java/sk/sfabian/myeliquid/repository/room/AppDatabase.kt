package sk.sfabian.myeliquid.repository.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import sk.sfabian.myeliquid.repository.model.Ingredient

@Database(entities = [Ingredient::class], version = 1, exportSchema = false)
@TypeConverters(ObjectIdConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun ingredientDao(): IngredientInventoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ingredient_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
