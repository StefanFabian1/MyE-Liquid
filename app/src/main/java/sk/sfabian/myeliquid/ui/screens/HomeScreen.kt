package sk.sfabian.myeliquid.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.SoupKitchen
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController

@Composable
fun HomeScreen(navController: NavHostController) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            WelcomeSection()

            // Odkazy na ďalšie obrazovky
            NavigationLinks(navController)
        }
    }
}

@Composable
fun WelcomeSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Vitajte, Jožko Mrkvička",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Tu spravujete Vaše ingrediencie, recepty a produkty s ľahkosťou.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun NavigationLinks(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 22.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        NavigationCard(
            title = "Ingrediencie",
            description = "View and manage your ingredient inventory.",
            icon = Icons.Default.SoupKitchen,
            onClick = {
                navController.navigate("ingredients") {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
        NavigationCard(
            title = "Recipes",
            description = "Create and edit e-liquid recipes.",
            icon = Icons.Default.Receipt,
            onClick = { navController.navigate("recipes") }
        )
        NavigationCard(
            title = "Products",
            description = "Prepare and showcase final products.",
            icon = Icons.Default.Inventory,
            onClick = { navController.navigate("products") }
        )
    }
}

@Composable
fun NavigationCard(title: String, description: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = "$title icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 16.dp)
                )
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowForwardIos,
                contentDescription = "Navigate to $title",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}