package com.example.aetherfinance.ui.theme

import androidx.compose.ui.graphics.Color

val PrimaryBlue = Color(0xFF0B57D0)
val PrimaryBlueVariant = Color(0xFF1A73E8)
val PrimaryContainer = Color(0xFFD3E3FD)
val OnPrimaryContainer = Color(0xFF041E49)

val SurfaceLight = Color(0xFFF0F4F9)
val SurfaceCard = Color(0xFFFFFFFF)
val OutlineVariant = Color(0xFFE0E2EC)
val OutlineSubtle = Color(0xFFC4C7C5)

val TextPrimary = Color(0xFF1F1F1F)
val TextSecondary = Color(0xFF444746)
val TextTertiary = Color(0xFF747775)

val IncomeGreen = Color(0xFF146C2E)
val IncomeGreenContainer = Color(0xFFE7F8ED)
val ExpenseRed = Color(0xFFB3261E)
val ExpenseRedContainer = Color(0xFFF9DEDC)

val WarningAmber = Color(0xFFE37400)
val WarningAmberContainer = Color(0xFFFEF7E0)

fun parseHexColor(hexString: String, defaultColor: Color = PrimaryBlue): Color {
    return try {
        val clean = hexString.replace("#", "")
        val colorInt = when (clean.length) {
            6 -> "FF$clean".toLong(16)
            8 -> clean.toLong(16)
            else -> return defaultColor
        }
        Color(colorInt)
    } catch (_: Exception) {
        defaultColor
    }
}
