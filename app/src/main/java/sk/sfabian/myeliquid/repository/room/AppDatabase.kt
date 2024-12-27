package sk.sfabian.myeliquid.repository.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import sk.sfabian.myeliquid.repository.model.Category
import sk.sfabian.myeliquid.repository.model.Ingredient
import sk.sfabian.myeliquid.repository.model.Movement
import sk.sfabian.myeliquid.repository.model.Subcategory

@Database(entities = [Ingredient::class, Movement::class, Category::class, Subcategory::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun ingredientDao(): IngredientInventoryDao
    abstract fun movementDao(): MovementDao
    abstract fun categoryDao(): CategoryDao
    abstract fun subCategoryDao(): SubcategoryDao

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
