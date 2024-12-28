package sk.sfabian.myeliquid.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import sk.sfabian.myeliquid.repository.model.Movement
import sk.sfabian.myeliquid.ui.viewmodel.IngredientInventoryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MovementsScreen(
    viewModel: IngredientInventoryViewModel,
    ingredientId: String,
    onBack: () -> Unit
) {
    val movements by viewModel.movements.collectAsState()

    LaunchedEffect (Unit) {
        viewModel.fetchMovementsForIngredient(ingredientId)
    }

    DisposableEffect (Unit) {
        onDispose {
            viewModel.clearMovementsCache()
        }
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text("Pohyby ingrediencie") },
                navigationIcon = {
                    IconButton (onClick = onBack) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Späť")
                    }
                }
            )
        }
    ) {
        LazyColumn (
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(movements) { movement ->
                MovementCard(movement)
            }
        }
    }
}

@Composable
fun MovementCard(movement: Movement) {
    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column (
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Typ: ${movement.type}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Množstvo: ${movement.quantity}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Celková cena: ${movement.totalPrice} €",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Čas: ${movement.timestamp.formatAsDateTime()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun Long.formatAsDateTime(): String {
    val date = Date(this)
    val format = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    return format.format(date)
}