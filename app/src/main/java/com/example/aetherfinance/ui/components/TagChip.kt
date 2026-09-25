package com.example.aetherfinance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aetherfinance.ui.theme.OutlineVariant
import com.example.aetherfinance.ui.theme.TextPrimary
import com.example.aetherfinance.ui.theme.parseHexColor

@Composable
fun TagChip(
    name: String,
    colorHex: String? = null,
    isSelected: Boolean = false,
    onRemove: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val tagColor = if (colorHex != null) parseHexColor(colorHex) else MaterialTheme.colorScheme.primary

    val backgroundColor = when {
        isSelected -> TextPrimary
        else -> OutlineVariant.copy(alpha = 0.6f)
    }

    val contentColor = when {
        isSelected -> Color.White
        else -> TextPrimary
    }

    Row(
        modifier = modifier
            .testTag("tag_chip_$name")
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(if (isSelected) Color.White else tagColor)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = "#$name",
            color = contentColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
        if (onRemove != null) {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove tag $name",
                tint = contentColor.copy(alpha = 0.7f),
                modifier = Modifier
                    .size(13.dp)
                    .clickable(onClick = onRemove)
            )
        }
    }
}
