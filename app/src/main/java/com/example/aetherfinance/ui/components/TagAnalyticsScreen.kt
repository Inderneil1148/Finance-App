package com.example.aetherfinance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aetherfinance.data.model.CurrencyConfig
import com.example.aetherfinance.data.model.CustomTagEntity
import com.example.aetherfinance.data.model.TransactionEntity
import com.example.aetherfinance.data.util.FinanceConstants.TAG_COLOR_PALETTE
import com.example.aetherfinance.data.util.Formatters
import com.example.aetherfinance.ui.theme.ExpenseRed
import com.example.aetherfinance.ui.theme.OutlineVariant
import com.example.aetherfinance.ui.theme.PrimaryBlue
import com.example.aetherfinance.ui.theme.TextPrimary
import com.example.aetherfinance.ui.theme.TextSecondary
import com.example.aetherfinance.ui.theme.TextTertiary
import com.example.aetherfinance.ui.theme.parseHexColor

data class TagStat(
    val tag: CustomTagEntity,
    val spend: Double,
    val count: Int,
    val percentage: Double
)

@Composable
fun TagAnalyticsScreen(
    transactions: List<TransactionEntity>,
    tags: List<CustomTagEntity>,
    currency: CurrencyConfig,
    onCreateTag: (String, String) -> Unit,
    onUpdateTag: (CustomTagEntity) -> Unit,
    onDeleteTag: (String) -> Unit,
    onFilterByTag: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isCreating by remember { mutableStateOf(false) }
    var newTagName by remember { mutableStateOf("") }
    var newTagColor by remember { mutableStateOf(TAG_COLOR_PALETTE.first()) }

    var editingTagId by remember { mutableStateOf<String?>(null) }
    var editTagName by remember { mutableStateOf("") }
    var editTagColor by remember { mutableStateOf("") }
    var confirmDeleteTagId by remember { mutableStateOf<String?>(null) }

    val totalExpense = transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }
    val untaggedTransactions = transactions.filter { it.type == "EXPENSE" && it.customTags.isEmpty() }
    val untaggedTotal = untaggedTransactions.sumOf { it.amount }
    val untaggedCount = untaggedTransactions.size

    val tagStats = remember(transactions, tags) {
        val spendMap = mutableMapOf<String, Double>()
        val countMap = mutableMapOf<String, Int>()

        tags.forEach {
            spendMap[it.name] = 0.0
            countMap[it.name] = 0
        }

        transactions.filter { it.type == "EXPENSE" }.forEach { tx ->
            tx.customTags.forEach { tagName ->
                spendMap[tagName] = (spendMap[tagName] ?: 0.0) + tx.amount
                countMap[tagName] = (countMap[tagName] ?: 0) + 1
            }
        }

        tags.map { tag ->
            val spend = spendMap[tag.name] ?: 0.0
            val count = countMap[tag.name] ?: 0
            val pct = if (totalExpense > 0) (spend / totalExpense) * 100 else 0.0
            TagStat(tag, spend, count, pct)
        }.sortedByDescending { it.spend }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("tag_analytics_screen")
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // Header summary card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Custom Tags",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "${tags.size} tags organized",
                                fontSize = 12.sp,
                                color = TextTertiary
                            )
                        }

                        Button(
                            onClick = { isCreating = !isCreating },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            modifier = Modifier.testTag("new_tag_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Tag")
                        }
                    }

                    // Create Form
                    if (isCreating) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.background)
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = newTagName,
                                    onValueChange = { newTagName = it.lowercase().replace(" ", "-") },
                                    placeholder = { Text("tag-name (e.g. coffee)") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("create_tag_input")
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        val clean = newTagName.trim()
                                        if (clean.isNotBlank()) {
                                            onCreateTag(clean, newTagColor)
                                            newTagName = ""
                                            isCreating = false
                                        }
                                    },
                                    enabled = newTagName.isNotBlank(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                                ) {
                                    Text("Add")
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                TAG_COLOR_PALETTE.forEach { hex ->
                                    val isColSelected = newTagColor == hex
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(parseHexColor(hex))
                                            .clickable { newTagColor = hex }
                                            .then(
                                                if (isColSelected) Modifier.border(2.dp, Color.White, CircleShape) else Modifier
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (isColSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Untagged status alert
                    if (untaggedCount > 0) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(PrimaryBlue.copy(alpha = 0.08f))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Untagged: $untaggedCount transactions (${Formatters.formatCurrency(untaggedTotal, currency)})",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = PrimaryBlue
                            )
                            val pct = if (totalExpense > 0) (untaggedTotal / totalExpense) * 100 else 0.0
                            Text(
                                text = "${Formatters.formatPercentage(pct)} of spend",
                                fontSize = 10.sp,
                                color = TextTertiary
                            )
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "SPEND BY TAG",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextTertiary,
                letterSpacing = 1.sp
            )
        }

        items(tagStats, key = { it.tag.id }) { stat ->
            val isEditing = editingTagId == stat.tag.id

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    if (isEditing) {
                        // Editing UI
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = editTagName,
                                onValueChange = { editTagName = it.lowercase().replace(" ", "-") },
                                singleLine = true,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = {
                                val clean = editTagName.trim()
                                if (clean.isNotBlank()) {
                                    onUpdateTag(stat.tag.copy(name = clean, color = editTagColor))
                                    editingTagId = null
                                }
                            }) {
                                Icon(Icons.Default.Check, contentDescription = "Save", tint = PrimaryBlue)
                            }
                            IconButton(onClick = { editingTagId = null }) {
                                Icon(Icons.Default.Close, contentDescription = "Cancel", tint = TextSecondary)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            TAG_COLOR_PALETTE.forEach { hex ->
                                val isColSelected = editTagColor == hex
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
                                        .clip(CircleShape)
                                        .background(parseHexColor(hex))
                                        .clickable { editTagColor = hex }
                                        .then(
                                            if (isColSelected) Modifier.border(2.dp, Color.White, CircleShape) else Modifier
                                        )
                                )
                            }
                        }
                    } else {
                        // Display Tag Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clickable { onFilterByTag(stat.tag.name) }
                                    .weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(parseHexColor(stat.tag.color))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "#${stat.tag.name}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "(${stat.count} tx)",
                                    fontSize = 11.sp,
                                    color = TextTertiary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Filter",
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(14.dp)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = Formatters.formatCurrency(stat.spend, currency),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = {
                                        editingTagId = stat.tag.id
                                        editTagName = stat.tag.name
                                        editTagColor = stat.tag.color
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit tag",
                                        tint = TextTertiary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        if (confirmDeleteTagId == stat.tag.id) {
                                            onDeleteTag(stat.tag.id)
                                            confirmDeleteTagId = null
                                        } else {
                                            confirmDeleteTagId = stat.tag.id
                                        }
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete tag",
                                        tint = if (confirmDeleteTagId == stat.tag.id) ExpenseRed else TextTertiary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Progress Bar
                        LinearProgressIndicator(
                            progress = { (stat.percentage / 100).toFloat().coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = parseHexColor(stat.tag.color),
                            trackColor = OutlineVariant,
                            strokeCap = StrokeCap.Round
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${Formatters.formatPercentage(stat.percentage)} of total",
                                fontSize = 10.sp,
                                color = TextTertiary
                            )
                            if (stat.count > 0 && stat.spend > 0) {
                                Text(
                                    text = "avg ${Formatters.formatCurrency(stat.spend / stat.count, currency)}/tx",
                                    fontSize = 10.sp,
                                    color = TextTertiary
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}
