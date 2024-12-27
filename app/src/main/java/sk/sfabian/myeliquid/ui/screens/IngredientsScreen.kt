package sk.sfabian.myeliquid.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import sk.sfabian.myeliquid.repository.model.Category
import sk.sfabian.myeliquid.repository.model.Ingredient
import sk.sfabian.myeliquid.ui.viewmodel.IngredientInventoryViewModel

@Composable
fun IngredientsScreen(viewModel: IngredientInventoryViewModel, navController: NavHostController) {
    viewModel.fetchIngredients()
    val ingredients by viewModel.ingredients.collectAsState(initial = emptyList())
    val categories by viewModel.categories.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showDetailDialog by remember { mutableStateOf(false) }
    var selectedIngredient by remember { mutableStateOf<Ingredient?>(null) }
    var showAddNewDialog by remember { mutableStateOf(false) }


    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Top
            ) {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(ingredients) { ingredient ->
                        IngredientCard(
                            ingredient = ingredient,
                            onAddClick = {
                                selectedIngredient = ingredient
                                showAddDialog = true
                            },
                            onDetailsClick = {
                                selectedIngredient = ingredient
                                showDetailDialog = true
                            },
                            onMovementsClick = {
                                // Navigácia na pohyby obrazovku
                                viewModel.navigateToMovements(
                                    ingredient,
                                    navController
                                )
                            }
                        )
                    }
                }
            }
            if (showAddDialog && selectedIngredient != null) {
                AddIngredientDialog(
                    ingredient = selectedIngredient!!,
                    onDismiss = { showAddDialog = false },
                    onConfirm = { quantity, price ->
                        viewModel.addIngredientMovement(
                            ingredient = selectedIngredient!!,
                            quantityAdded = quantity,
                            totalPrice = price
                        )
                    }
                )
            }

            if (showDetailDialog && selectedIngredient != null) {
                IngredientDetailDialog(
                    ingredient = selectedIngredient!!,
                    onDismiss = { showDetailDialog = false },
                    onEditClick = {
                        // Navigácia alebo iná logika pre editáciu
                        //viewModel.editIngredient(selectedIngredient!!)
                        showDetailDialog = false
                    }
                )
            }

            if (showAddNewDialog) {
                AddNewIngredientDialog(
                    categories = categories,
                    onDismiss = { showAddNewDialog = false },
                    onAddCategory = { categoryName ->
                        viewModel.addNewCategory(categoryName)
                    },
                    onDeleteCategory = { categoryName ->
                        viewModel.deleteCategory(categoryName)
                    },
                    onConfirm = { name, quantity, unit, price ->
                        viewModel.addNewIngredient(name, quantity, unit, price)
                    },
                    viewModel
                )
            }

            FloatingActionButton(
                onClick = { showAddNewDialog = true },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Pridať ingredienciu")
            }
        }
    }
}

@Composable
fun IngredientCard(
    ingredient: Ingredient,
    onAddClick: () -> Unit,
    onDetailsClick: () -> Unit,
    onMovementsClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onDetailsClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Textové informácie naľavo
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = ingredient.name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Detail Icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = "Množstvo: ${formatQuantity(ingredient.quantity)} ${ingredient.unit}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Kategória: základ",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            // Tlačidlá napravo, vertikálne zarovnané
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Tlačidlo "Pridať" s rovnakou šírkou
                Button(
                    onClick = onAddClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .width(120.dp) // Rovnaká šírka tlačidiel
                        .height(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Pridať",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = "Pridať",
                        modifier = Modifier.padding(start = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Button(
                    onClick = onMovementsClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .width(120.dp) // Rovnaká šírka tlačidiel
                        .height(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Pohyby",
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.onSecondary
                    )
                    Text(
                        text = "Pohyby",
                        modifier = Modifier.padding(start = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun AddIngredientDialog(
    ingredient: Ingredient,
    onDismiss: () -> Unit,
    onConfirm: (Double, Double) -> Unit
) {
    var quantity by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Pridať množstvo pre ${ingredient.name}") },
        text = {
            Column {
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Množstvo") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Cena (€)") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val quantityValue = quantity.toDoubleOrNull() ?: 0.0
                val priceValue = price.toDoubleOrNull() ?: 0.0
                onConfirm(quantityValue, priceValue)
                onDismiss()
            }) {
                Text("Pridať")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Zrušiť")
            }
        }
    )
}

@Composable
fun IngredientDetailDialog(
    ingredient: Ingredient,
    onDismiss: () -> Unit,
    onEditClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Detail Ingrediencie") },
        text = {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Názov: ${ingredient.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Množstvo: ${formatQuantity(ingredient.quantity)} ${ingredient.unit}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Cena: ${ingredient.unitPrice}€ / ${ingredient.unit}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "ID: ${ingredient.mongoId}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        confirmButton = {
            Button(onClick = onEditClick) {
                Text("Editovať")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Zavrieť")
            }
        }
    )
}

@SuppressLint("DefaultLocale")
@Composable
fun formatQuantity(quantity: Double): String {
    return if (quantity % 1.0 == 0.0) {
        quantity.toInt().toString() // Bez desatinných miest
    } else {
        String.format("%.2f", quantity) // Max na dve desatinné miesta
    }
}

@Composable
fun AddNewIngredientDialog(
    categories: List<Category>,
    onDismiss: () -> Unit,
    onAddCategory: (String) -> Unit,
    onDeleteCategory: (Category) -> Unit,
    onConfirm: (String, Double, String, Double) -> Unit,
    viewModel: IngredientInventoryViewModel
) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Pridať novú ingredienciu") },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Názov") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Množstvo") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it },
                    label = { Text("Jednotka") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Cena (€)") },
                    singleLine = true
                )
                CategorySelector(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it },
                    onAddCategory = onAddCategory,
                    onDeleteCategory = onDeleteCategory
                )
/*
                val category: String,
                val subcategory: String?,
                val brand: String?,
                val description: String?

 */
            }
        },
        confirmButton = {
            Button(onClick = {
                val quantityValue = quantity.toDoubleOrNull() ?: 0.0
                val priceValue = price.toDoubleOrNull() ?: 0.0
                val categoryText = selectedCategory?.text ?: ""
                onConfirm(name, quantityValue, unit, priceValue)
                onDismiss()
            }) {
                Text("Pridať")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Zrušiť")
            }
        }
    )

    if (showAddCategoryDialog) {
        AddCategoryDialog(
            onDismiss = { showAddCategoryDialog = false },
            onConfirm = { newCategory ->
                onAddCategory(newCategory)
                showAddCategoryDialog = false
            }
        )
    }
}

@Composable
fun CategorySelector(
    categories: List<Category>,
    selectedCategory: Category?,
    onCategorySelected: (Category?) -> Unit,
    onAddCategory: (String) -> Unit,
    onDeleteCategory: (Category) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }

    Column {
        Text(text = "Vyberte kategóriu", style = MaterialTheme.typography.labelLarge)

        // Dropdown Trigger
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(4.dp))
                .clickable { expanded = true }
                .padding(12.dp)
        ) {
            Text(
                text = selectedCategory?.text ?: "Vyberte kategóriu",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // DropdownMenu
        DropdownMenu (
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            categories.forEach { category ->
                DropdownMenuItem(
                    onClick = {
                        onCategorySelected(category)
                        expanded = false
                    },
                    text = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = category.text, style = MaterialTheme.typography.bodyLarge)
                            IconButton(
                                onClick = {
                                    onDeleteCategory(category)
                                    expanded = false
                                },
                                enabled = true // Vložiť podmienku, ak kategória nie je použitá
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Zmazať kategóriu",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                )
            }
            DropdownMenuItem(
                onClick = {
                    showAddCategoryDialog = true
                    expanded = false
                },
                text = { Text("Pridať novú kategóriu") }
            )
        }

        // Dialóg pre pridanie kategórie
        if (showAddCategoryDialog) {
            AddCategoryDialog(
                onDismiss = { showAddCategoryDialog = false },
                onConfirm = { newCategory ->
                    onAddCategory(newCategory)
                    showAddCategoryDialog = false
                }
            )
        }
    }
}



@Composable
fun AddCategoryDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var categoryName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Pridať novú kategóriu") },
        text = {
            OutlinedTextField(
                value = categoryName,
                onValueChange = { categoryName = it },
                label = { Text("Názov kategórie") },
                singleLine = true
            )
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(categoryName)
            }) {
                Text("Pridať")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Zrušiť")
            }
        }
    )
}
