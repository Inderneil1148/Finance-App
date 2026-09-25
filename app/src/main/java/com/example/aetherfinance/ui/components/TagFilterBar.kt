package com.example.aetherfinance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.aetherfinance.data.model.CustomTagEntity
import com.example.aetherfinance.ui.theme.OutlineVariant
import com.example.aetherfinance.ui.theme.PrimaryBlue
import com.example.aetherfinance.ui.theme.TextPrimary
import com.example.aetherfinance.ui.theme.TextSecondary
import com.example.aetherfinance.ui.theme.parseHexColor

@Composable
fun TagFilterBar(
    tags: List<CustomTagEntity>,
    selectedTag: String?,
    onSelectTag: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .testTag("tag_filter_bar")
            .horizontalScroll(scrollState)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // "All Tags" Pill
        val isAllSelected = selectedTag == null
        Box(
            modifier = Modifier
                .testTag("filter_chip_all")
                .clip(RoundedCornerShape(16.dp))
                .background(if (isAllSelected) PrimaryBlue else Color.White)
                .clickable { onSelectTag(null) }
                .padding(horizontal = 12.dp, vertical = 7.dp)
        ) {
            Text(
                text = "All Tags",
                fontSize = 12.sp,
                fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isAllSelected) Color.White else TextSecondary
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Individual Tags
        tags.forEach { tag ->
            val isSelected = selectedTag == tag.name
            val tagColor = parseHexColor(tag.color)

            Row(
                modifier = Modifier
                    .testTag("filter_chip_${tag.name}")
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isSelected) PrimaryBlue else Color.White)
                    .clickable { onSelectTag(if (isSelected) null else tag.name) }
                    .padding(horizontal = 12.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color.White else tagColor)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "#${tag.name}",
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}
