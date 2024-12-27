package sk.sfabian.myeliquid.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import sk.sfabian.myeliquid.R

// Definícia fontov
private val AdminFontFamily = FontFamily(
    Font(R.font.roboto_regular, FontWeight.Normal),
    Font(R.font.roboto_medium, FontWeight.Medium),
    Font(R.font.roboto_bold, FontWeight.Bold)
)

// Definícia typografie pre aplikáciu
val AppTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = AdminFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp
    ),
    displayMedium = TextStyle(
        fontFamily = AdminFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 28.sp
    ),
    displaySmall = TextStyle(
        fontFamily = AdminFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 24.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = AdminFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = AdminFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = AdminFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = AdminFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = AdminFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = AdminFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    labelLarge = TextStyle(
        fontFamily = AdminFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    ),
    labelMedium = TextStyle(
        fontFamily = AdminFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    ),
    labelSmall = TextStyle(
        fontFamily = AdminFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp
    )
)
