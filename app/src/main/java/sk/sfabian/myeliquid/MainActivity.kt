package sk.sfabian.myeliquid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import sk.sfabian.myeliquid.repository.IngredientInventoryRepository
import sk.sfabian.myeliquid.repository.api.ApiClient
import sk.sfabian.myeliquid.repository.room.AppDatabase
import sk.sfabian.myeliquid.repository.Repository
import sk.sfabian.myeliquid.repository.api.IngredientSseHandler
import sk.sfabian.myeliquid.ui.screens.BottomNavigationBar
import sk.sfabian.myeliquid.ui.screens.HomeScreen
import sk.sfabian.myeliquid.ui.screens.IngredientsScreen
import sk.sfabian.myeliquid.ui.screens.MovementsScreen
import sk.sfabian.myeliquid.ui.theme.MyELiquidTheme
import sk.sfabian.myeliquid.ui.viewmodel.IngredientInventoryViewModel
import sk.sfabian.myeliquid.ui.viewmodel.IngredientInventoryViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                val repository = Repository(
                    database = AppDatabase.getDatabase(this@MainActivity)
                )
                repository.fetchAndStoreAllData()
            }
            setContent {
                MyELiquidTheme {
                    MainScreen()
                }
            }
        }
    }
}

fun onMenuClick() {
    println("Menu clicked - default implementation")
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val repository = IngredientInventoryRepository(
        database.ingredientDao(), ApiClient.ingredientApi,
        categoryDao = database.categoryDao(),
        subcategoryDao = database.subCategoryDao()
    )
    val sseHandler = IngredientSseHandler(ApiClient.ingredientApi, database.ingredientDao())
    val factory = IngredientInventoryViewModelFactory(repository, sseHandler)
    val viewModel: IngredientInventoryViewModel = ViewModelProvider(
        context as ComponentActivity,
        factory
    )[IngredientInventoryViewModel::class.java]
    if (Configuration.getBoolean("enable_sse", false)) {
        sseHandler.startListening()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val currentDestination =
                        navController.currentBackStackEntryAsState().value?.destination?.route
                    Text(
                        text = when (currentDestination) {
                            "home" -> "Admin Panel"
                            "ingredients" -> "Ingrediencie"
                            else -> "Môj E-Liquid"
                        },
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onMenuClick() }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {
            composable("home") { HomeScreen(navController) }
            composable("ingredients") {
                IngredientsScreen(viewModel = viewModel, navController)
            }
            composable(
                route = "movements/{ingredientId}",
                arguments = listOf(navArgument("ingredientId") { type = NavType.StringType })
                ) { backStackEntry ->
                val ingredientId = backStackEntry.arguments?.getString("ingredientId")
                if (ingredientId != null) {
                    MovementsScreen(
                        viewModel,
                        ingredientId = ingredientId,
                        onBack = { navController.navigate("ingredients") }
                    )
                }
            }
        }
    }
}