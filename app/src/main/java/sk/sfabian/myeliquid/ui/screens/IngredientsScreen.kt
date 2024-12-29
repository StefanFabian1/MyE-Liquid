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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Popup
import androidx.navigation.NavHostController
import sk.sfabian.myeliquid.Configuration
import sk.sfabian.myeliquid.repository.model.Category
import sk.sfabian.myeliquid.repository.model.Ingredient
import sk.sfabian.myeliquid.repository.model.Subcategory
import sk.sfabian.myeliquid.ui.viewmodel.IngredientInventoryViewModel

@Composable
fun IngredientsScreen(viewModel: IngredientInventoryViewModel, navController: NavHostController) {
    if (!Configuration.getBoolean("enable_sse", false)) {
        viewModel.fetchIngredients()
    }
    val ingredients by viewModel.ingredients.collectAsState(initial = emptyList())
    val searchQuery by viewModel.searchQuery.collectAsState()
    val categories by viewModel.categories.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showDetailDialog by remember { mutableStateOf(false) }
    var selectedIngredient by remember { mutableStateOf<Ingredient?>(null) }
    var showAddNewDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.updateSearchQuery(it) },
                        label = { Text("Hľadať ingrediencie") },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Vyhľadať"
                            )
                        }
                    )
                    IconButton(
                        onClick = { /* Clear search */ viewModel.updateSearchQuery("") },
                        modifier = Modifier.size(48.dp).padding(top = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CleaningServices,
                            contentDescription = "Zrušiť vyhľadávanie",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

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
                    onConfirm = { quantity, totalPrice, type ->
                        viewModel.addIngredientMovement(
                            ingredientId = selectedIngredient!!.mongoId ?: "",
                            quantity = quantity,
                            totalPrice = totalPrice,
                            type = type
                        )
                        showAddDialog = false
                    }
                )
            }

            if (showDetailDialog && selectedIngredient != null) {
                IngredientDetailDialog(
                    ingredient = selectedIngredient!!,
                    onDismiss = { showDetailDialog = false },
                    onEditClick = {
                        showDetailDialog = false
                        showEditDialog = true
                    },
                    onDeleteClick = {
                        viewModel.deleteIngredient(selectedIngredient!!)
                        showDetailDialog = false
                    }
                )
            }

            if (showEditDialog && selectedIngredient != null) {
                IngredientEditDialog(
                    ingredient = selectedIngredient!!,
                    onDismiss = { showEditDialog = false },
                    onConfirm = { updatedIngredient ->
                        viewModel.updateIngredient(updatedIngredient)
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
                    onConfirm = { name, category, quantity, unit, price ->
                        viewModel.addNewIngredient(name, category, quantity, unit, price)
                    }
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
                    text = "Kategória: ${ingredient.category}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            // Tlačidlá napravo, vertikálne zarovnané
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = onAddClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .width(120.dp)
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
    onConfirm: (Double, Double, String) -> Unit
) {
    var quantity by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var movementType by remember { mutableStateOf("ADD") }

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
                DropdownSelector(
                    items = listOf("ADD", "REMOVE"),
                    selectedItem = movementType,
                    onItemSelected = {
                        if (it != null) {
                            movementType = it
                        }
                    },
                    label = "Typ pohybu",
                    itemText = { it }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val quantityValue = quantity.toDoubleOrNull() ?: 0.0
                val priceValue = price.toDoubleOrNull() ?: 0.0
                onConfirm(quantityValue, priceValue, movementType)
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
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Detail Ingrediencie")
                IconButton(
                    onClick = { onDeleteClick() },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "Zmazať ingredienciu",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Názov: ${ingredient.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Kategória: ${ingredient.category}",
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
                if (ingredient.category.equals("aróma")) {
                    Text(
                        text = "Subkategória: ${ingredient.subcategory}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Brand: ${ingredient.brand}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Popis: ${ingredient.description}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
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
    onConfirm: (String, Category?, Double, String, Double) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }

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
                CategorySelector(
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it },
                    onAddCategory = onAddCategory,
                    onDeleteCategory = onDeleteCategory
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
                onConfirm(name, selectedCategory, quantityValue, unit, priceValue)
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
fun CategorySelector(
    categories: List<Category>,
    selectedCategory: Category?,
    onCategorySelected: (Category?) -> Unit,
    onAddCategory: (String) -> Unit,
    onDeleteCategory: (Category) -> Unit
) {
    var showAddCategoryDialog by remember { mutableStateOf(false) }

    DropdownSelector(
        items = categories,
        selectedItem = selectedCategory,
        onItemSelected = onCategorySelected,
        onAddItem = { showAddCategoryDialog = true },
        onDeleteItem = onDeleteCategory,
        label = "Kategória",
        itemText = { it.text }
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
fun SubcategorySelector(
    subcategories: List<Subcategory>,
    selectedSubcategory: Subcategory?,
    onSubcategorySelected: (Subcategory?) -> Unit,
    onAddSubcategory: (String) -> Unit,
    onDeleteSubcategory: (Subcategory) -> Unit
) {
    DropdownSelector(
        items = subcategories,
        selectedItem = selectedSubcategory,
        onItemSelected = onSubcategorySelected,
        onAddItem = { onAddSubcategory("Nová subkategória") },
        onDeleteItem = onDeleteSubcategory,
        label = "Subkategória",
        itemText = { it.text }
    )
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

@Composable
fun ClickableOutlinedTextField(
    value: String,
    onClick: () -> Unit,
    label: String,
    trailingIcon: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() } // Celý Box je klikateľný
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true, // Zamedzenie vstupu
            trailingIcon = trailingIcon,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent) // Transparentnosť, aby kliknutie na Box nebolo blokované
        )

        // Prekrytie, ktoré zachytáva kliknutia
        Box(
            modifier = Modifier
                .matchParentSize() // Prekrytie celej veľkosti OutlinedTextField
                .clickable { onClick() } // Získanie kliknutí
        )
    }
}

@Composable
fun <T> DropdownSelector(
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T?) -> Unit,
    onAddItem: (() -> Unit)? = null,
    onDeleteItem: ((T) -> Unit)? = null,
    label: String,
    itemText: (T) -> String
) {
    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    ClickableOutlinedTextField(
        value = selectedItem?.let(itemText) ?: "",
        onClick = { expanded = true },
        label = label,
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Rozbaľovací zoznam",
                tint = MaterialTheme.colorScheme.onSurface
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { coordinates ->
                textFieldSize = coordinates.size.toSize()
            }
    )

    if (expanded) {
        Popup(
            alignment = Alignment.TopStart,
            onDismissRequest = { expanded = false },
            offset = IntOffset(0, textFieldSize.height.toInt() * 2)
        ) {
            Column(
                modifier = Modifier
                    .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(8.dp)
            ) {
                items.forEach { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onItemSelected(item)
                                expanded = false
                            }
                            .padding(start = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = itemText(item),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        if (onDeleteItem != null) {
                            IconButton(
                                onClick = {
                                    onDeleteItem(item)
                                    expanded = false
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Cancel,
                                    contentDescription = "Zmazať položku",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
                }
                /*
                if (onAddItem != null) {
                    TextButton(
                        onClick = {
                            onAddItem()
                            expanded = false
                        }
                    ) {
                        Text("Pridať novú položku")
                    }
                }
                */
            }
        }
    }
}

@Composable
fun IngredientEditDialog(
    ingredient: Ingredient,
    onDismiss: () -> Unit,
    onConfirm: (Ingredient) -> Unit
) {
    var name by remember { mutableStateOf(ingredient.name) }
    var quantity by remember { mutableStateOf(ingredient.quantity.toString()) }
    var unit by remember { mutableStateOf(ingredient.unit) }
    var price by remember { mutableStateOf(ingredient.unitPrice.toString()) }
    var category by remember { mutableStateOf(ingredient.category) }
    var subcategory by remember { mutableStateOf(ingredient.subcategory ?: "") }
    var brand by remember { mutableStateOf(ingredient.brand ?: "") }
    var description by remember { mutableStateOf(ingredient.description ?: "") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Upraviť ingredienciu") },
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
                category?.let { text ->
                    OutlinedTextField(
                        value = text,
                        onValueChange = { category = it },
                        label = { Text("Kategória") },
                        singleLine = true
                    )
                }
                OutlinedTextField(
                    value = subcategory,
                    onValueChange = { subcategory = it },
                    label = { Text("Subkategória") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = brand,
                    onValueChange = { brand = it },
                    label = { Text("Značka") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Popis") },
                    singleLine = false,
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val updatedIngredient = ingredient.copy(
                    name = name,
                    quantity = quantity.toDoubleOrNull() ?: 0.0,
                    unit = unit,
                    unitPrice = price.toDoubleOrNull() ?: 0.0,
                    category = category,
                    subcategory = subcategory.takeIf { it.isNotBlank() },
                    brand = brand.takeIf { it.isNotBlank() },
                    description = description.takeIf { it.isNotBlank() }
                )
                onConfirm(updatedIngredient)
                onDismiss()
            }) {
                Text("Uložiť")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Zrušiť")
            }
        }
    )
}