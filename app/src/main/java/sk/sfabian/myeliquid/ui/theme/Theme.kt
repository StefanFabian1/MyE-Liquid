package sk.sfabian.myeliquid.ui.theme
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Light Mode Farebná schéma
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF4A90E2),            // Svetlomodrá
    onPrimary = Color.White,                // Text na hlavnej farbe
    primaryContainer = Color(0xFF80BDF0),   // Svetlejšia modrá pre povrchy
    onPrimaryContainer = Color(0xFF00274D), // Text na svetlejších povrchoch

    secondary = Color(0xFFF5A623),          // Jemná oranžová
    onSecondary = Color.White,              // Text na sekundárnej farbe
    secondaryContainer = Color(0xD7FFD280), // Svetlá oranžová
    onSecondaryContainer = Color(0xFF5A2A00), // Text na svetlých povrchoch

    background = Color(0xFFF7F9FC),         // Jemné svetlé pozadie
    onBackground = Color(0xFF1C1F26),       // Text na pozadí

    surface = Color.White,                  // Povrchové komponenty
    onSurface = Color(0xFF1C1F26),          // Text na povrchu

    error = Color(0xFFFF4C4C),              // Červená pre chyby
    onError = Color.White                   // Text na chybových povrchoch
)

// Dark Mode Farebná schéma
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF80BDF0),            // Svetlomodrá
    onPrimary = Color(0xFF00274D),          // Text na hlavnej farbe
    primaryContainer = Color(0xFF4A90E2),   // Tmavomodrá pre povrchy
    onPrimaryContainer = Color.White,       // Text na povrchoch

    secondary = Color(0xFFFFD280),          // Svetlá oranžová
    onSecondary = Color(0xFF5A2A00),        // Text na sekundárnej farbe
    secondaryContainer = Color(0xFFF5A623), // Oranžová pre povrchy
    onSecondaryContainer = Color.White,     // Text na svetlých povrchoch

    background = Color(0xFF1C1F26),         // Tmavé pozadie
    onBackground = Color(0xFFF7F9FC),       // Text na pozadí

    surface = Color(0xFF2E3338),            // Povrchové komponenty
    onSurface = Color(0xFFF7F9FC),          // Text na povrchu

    error = Color(0xFFFF4C4C),              // Červená pre chyby
    onError = Color(0xFF3B0000)             // Text na chybových povrchoch
)

@Composable
fun MyELiquidTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    //nepouziva sa --- s tymto sa treba pohrat, celkom zaujimave
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}