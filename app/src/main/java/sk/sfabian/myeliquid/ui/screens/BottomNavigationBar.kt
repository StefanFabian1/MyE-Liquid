package sk.sfabian.myeliquid.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SoupKitchen
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("home", "Domov", Icons.Default.Home),
        BottomNavItem("ingredients", "Ingrediencie", Icons.Default.SoupKitchen),
        BottomNavItem("products", "Products", Icons.Default.ShoppingCart)
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        items.forEach { item ->
            val currentRoute =
                navController.currentBackStackEntryAsState().value?.destination?.route
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(imageVector = item.icon, contentDescription = item.title)
                },
                label = { Text(item.title) },
                alwaysShowLabel = true
            )
        }
    }
}