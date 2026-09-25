package com.example.aetherfinance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aetherfinance.data.model.CurrencyConfig
import com.example.aetherfinance.data.model.CustomTagEntity
import com.example.aetherfinance.data.model.TransactionEntity
import com.example.aetherfinance.data.util.FinanceConstants
import com.example.aetherfinance.data.util.Formatters
import com.example.aetherfinance.ui.theme.ExpenseRed
import com.example.aetherfinance.ui.theme.IncomeGreen
import com.example.aetherfinance.ui.theme.OutlineVariant
import com.example.aetherfinance.ui.theme.TextPrimary
import com.example.aetherfinance.ui.theme.TextSecondary
import com.example.aetherfinance.ui.theme.TextTertiary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TransactionItem(
    transaction: TransactionEntity,
    currency: CurrencyConfig,
    availableTags: List<CustomTagEntity>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryInfo = FinanceConstants.DEFAULT_CATEGORIES.find { it.name == transaction.category }
    val iconName = categoryInfo?.iconName ?: "Category"
    val colorHex = categoryInfo?.colorHex ?: "#8E8E93"

    val isExpense = transaction.type == "EXPENSE"
    val amountColor = if (isExpense) ExpenseRed else IncomeGreen
    val amountPrefix = if (isExpense) "-" else "+"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .testTag("transaction_item_${transaction.id}")
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Category Badge
        CategoryBadge(
            iconName = iconName,
            colorHex = colorHex,
            badgeSize = 42.dp,
            iconSize = 22.dp
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Center: Title, category, note & tag chips
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = transaction.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = transaction.category,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
                if (!transaction.note.isNullOrBlank()) {
                    Text(
                        text = " • ${transaction.note}",
                        fontSize = 11.sp,
                        color = TextTertiary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // Custom Tag Pills
            if (transaction.customTags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    transaction.customTags.take(3).forEach { tagName ->
                        val tagEntity = availableTags.find { it.name == tagName }
                        TagChip(
                            name = tagName,
                            colorHex = tagEntity?.color
                        )
                    }
                    if (transaction.customTags.size > 3) {
                        Text(
                            text = "+${transaction.customTags.size - 3}",
                            fontSize = 10.sp,
                            color = TextTertiary,
                            modifier = Modifier.align(Alignment.CenterVertically)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Right side: Amount and Time
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "$amountPrefix${Formatters.formatCurrency(transaction.amount, currency)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
            if (!transaction.time.isNullOrBlank()) {
                Text(
                    text = transaction.time,
                    fontSize = 10.sp,
                    color = TextTertiary
                )
            }
        }
    }
}
