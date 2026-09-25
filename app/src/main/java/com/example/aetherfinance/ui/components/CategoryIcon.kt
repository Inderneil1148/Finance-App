package com.example.aetherfinance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.aetherfinance.ui.theme.parseHexColor

fun getCategoryIconVector(iconName: String): ImageVector {
    return when (iconName) {
        "Utensils", "Restaurant" -> Icons.Default.Restaurant
        "ShoppingCart" -> Icons.Default.ShoppingCart
        "Home" -> Icons.Default.Home
        "DirectionsCar", "Car" -> Icons.Default.DirectionsCar
        "ShoppingBag" -> Icons.Default.ShoppingBag
        "Movie", "Film" -> Icons.Default.Movie
        "Favorite", "Activity" -> Icons.Default.Favorite
        "ElectricBolt", "Zap" -> Icons.Default.ElectricBolt
        "Work", "Briefcase" -> Icons.Default.Work
        "Paid", "DollarSign" -> Icons.Default.Paid
        "Laptop" -> Icons.Default.Laptop
        else -> Icons.Default.Category
    }
}

@Composable
fun CategoryBadge(
    iconName: String,
    colorHex: String,
    modifier: Modifier = Modifier,
    badgeSize: Dp = 38.dp,
    iconSize: Dp = 20.dp
) {
    val tintColor = parseHexColor(colorHex)
    val bgColor = tintColor.copy(alpha = 0.15f)

    Box(
        modifier = modifier
            .size(badgeSize)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = getCategoryIconVector(iconName),
            contentDescription = null,
            tint = tintColor,
            modifier = Modifier.size(iconSize)
        )
    }
}
