package com.example.aetherfinance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aetherfinance.data.model.CurrencyConfig
import com.example.aetherfinance.data.model.CustomTagEntity
import com.example.aetherfinance.data.model.TimeFilter
import com.example.aetherfinance.data.model.TransactionEntity
import com.example.aetherfinance.data.util.Formatters
import com.example.aetherfinance.ui.theme.ExpenseRedContainer
import com.example.aetherfinance.ui.theme.IncomeGreenContainer
import com.example.aetherfinance.ui.theme.OutlineVariant
import com.example.aetherfinance.ui.theme.PrimaryBlue
import com.example.aetherfinance.ui.theme.TextPrimary
import com.example.aetherfinance.ui.theme.TextSecondary
import com.example.aetherfinance.ui.theme.TextTertiary

@Composable
fun LedgerScreen(
    transactions: List<TransactionEntity>,
    totalIncome: Double,
    totalExpense: Double,
    currency: CurrencyConfig,
    monthlyBudgetLimit: Double,
    availableTags: List<CustomTagEntity>,
    selectedTag: String?,
    timeFilter: TimeFilter,
    typeFilter: String,
    searchQuery: String,
    onSelectTag: (String?) -> Unit,
    onSelectTimeFilter: (TimeFilter) -> Unit,
    onSelectTypeFilter: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onEditBudgetClick: () -> Unit,
    onTransactionClick: (TransactionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    // Group transactions by date
    val groupedTransactions = remember(transactions) {
        transactions.groupBy { it.date }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("ledger_screen")
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Overview Summary Card
        item {
            Spacer(modifier = Modifier.height(2.dp))
            OverviewCard(
                totalIncome = totalIncome,
                totalExpense = totalExpense,
                currency = currency,
                monthlyBudgetLimit = monthlyBudgetLimit,
                onEditBudgetClick = onEditBudgetClick
            )
        }

        // Custom Tag Filter Bar
        item {
            TagFilterBar(
                tags = availableTags,
                selectedTag = selectedTag,
                onSelectTag = onSelectTag
            )
        }

        // Search Bar & Filter Controls
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White)
                    .padding(10.dp)
            ) {
                // Search TextField
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search transactions, payees, tags…", fontSize = 12.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = TextTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = OutlineVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("search_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Type Filter (All / Expense / Income)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("ALL" to "All", "EXPENSE" to "Expenses", "INCOME" to "Income").forEach { (typeKey, label) ->
                        val isSelected = typeFilter == typeKey
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .testTag("type_filter_$typeKey")
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.background)
                                .clickable { onSelectTypeFilter(typeKey) }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Time Filter Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TimeFilter.entries.forEach { tf ->
                        val isSelected = timeFilter == tf
                        Box(
                            modifier = Modifier
                                .testTag("time_filter_${tf.name}")
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) TextPrimary else MaterialTheme.colorScheme.background)
                                .clickable { onSelectTimeFilter(tf) }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = tf.label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else TextSecondary
                            )
                        }
                    }
                }
            }
        }

        // Transactions grouped by date
        if (groupedTransactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(OutlineVariant.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ReceiptLong,
                                contentDescription = null,
                                tint = TextTertiary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No transactions found",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Try adjusting your filters or search query",
                            fontSize = 11.sp,
                            color = TextTertiary
                        )
                    }
                }
            }
        } else {
            groupedTransactions.forEach { (dateStr, txList) ->
                // Date Section Header
                item(key = "header_$dateStr") {
                    val dayExpense = txList.filter { it.type == "EXPENSE" }.sumOf { it.amount }
                    val dayIncome = txList.filter { it.type == "INCOME" }.sumOf { it.amount }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = Formatters.formatDateLabel(dateStr).uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextTertiary,
                            letterSpacing = 0.8.sp
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (dayExpense > 0) {
                                Text(
                                    text = "-${Formatters.formatCurrency(dayExpense, currency)}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextTertiary
                                )
                            }
                            if (dayIncome > 0) {
                                Text(
                                    text = "+${Formatters.formatCurrency(dayIncome, currency)}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PrimaryBlue
                                )
                            }
                        }
                    }
                }

                // Transaction Items for that date
                items(txList, key = { it.id }) { tx ->
                    TransactionItem(
                        transaction = tx,
                        currency = currency,
                        availableTags = availableTags,
                        onClick = { onTransactionClick(tx) }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(72.dp))
        }
    }
}
