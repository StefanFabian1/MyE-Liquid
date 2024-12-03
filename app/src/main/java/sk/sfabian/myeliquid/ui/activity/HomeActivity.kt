package sk.sfabian.myeliquid.ui.activity

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DebugDarkTheme()
            AppTheme {
                HomeActivityLayout()
            }
        }
    }
}

@Composable
fun DebugDarkTheme() {
    val isDark = isSystemInDarkTheme()
    Log.d("ThemeCheck", "Dark theme active: $isDark")
}

@Composable
fun HomeActivityLayout() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MyE-Liquid") },
                navigationIcon = {
                    IconButton (onClick = { /* TODO: Open hamburger menu */ }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = MaterialTheme.colorScheme.background)
                    }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.background
                )
            )
        }
    )
    { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Uvítací text
            Text(
                text = "Welcome, Admin!",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Štatistické karty
            StatisticsGrid()

            Spacer(modifier = Modifier.height(32.dp))

            // Kritické upozornenia
            CriticalNotifications()
        }
    }
}

@Composable
fun StatisticsGrid() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatisticsCard("7", "Completed Tasks")
            StatisticsCard("16", "Large Dilutions")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatisticsCard("3", "Small Dilutions")
            StatisticsCard("1", "Ingredient Inventory")
        }
    }
}

@Composable
fun StatisticsCard(value: String, label: String) {
    val configuration = LocalConfiguration.current
    val cardSize = if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 210.dp else 180.dp
    Card(
        modifier = Modifier
            .size(cardSize)
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun CriticalNotifications() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Notifications",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Príklad upozornení
        val notifications = listOf(
            "Low stock: Nicotine",
            "Expiration: Strawberry Aroma",
            "Maturation complete: Tobacco Base"
        )

        notifications.forEach { notification ->
            Text(
                text = "- $notification",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColorScheme else LightColorScheme,
        typography = MaterialTheme.typography,
        content = content
    )
}

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4CAF50), // Jemná zelená (hlavná farba)
    onPrimary = Color(0xFFFFFFFF), // Biely text na primárnej farbe
    primaryContainer = Color(0xFFA5D6A7), // Svetlá zelená pre zvýraznenia
    onPrimaryContainer = Color(0xFF002411), // Tmavý text na svetlom pozadí

    secondary = Color(0xFF3F51B5), // Jemná modrá (doplnková farba)
    onSecondary = Color(0xFFFFFFFF), // Biely text na sekundárnej farbe
    secondaryContainer = Color(0xFFC5CAE9), // Svetlá modrá pre doplnkové pozadie
    onSecondaryContainer = Color(0xFF1A237E), // Tmavý text na svetlom modrom pozadí

    background = Color(0xFFECF7EC),
    onBackground = Color(0xFF1B5E20), // Tmavý text na svetlom pozadí

    surface = Color(0xFFFFFFFF), // Biela pre karty a prvky
    onSurface = Color(0xFF212121), // Tmavý text na bielom pozadí

    error = Color(0xFFF44336), // Červená pre chyby alebo upozornenia
    onError = Color(0xFFFFFFFF), // Biely text na chybovej farbe

    outline = Color(0xFFBDBDBD), // Svetlosivá pre obrysy a menej výrazné prvky
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFFB2E0B2),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFF03DAC6),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF00574B),
    onSecondaryContainer = Color.White,
    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0),
    error = Color(0xFFCF6679),
    onError = Color.Black
)

@Preview(showBackground = true)
@Composable
fun HomeActivityPreview() {
    MaterialTheme {
        HomeActivityLayout()
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeActivityPreviewDark() {
    MaterialTheme {
        HomeActivityLayout()
    }
}