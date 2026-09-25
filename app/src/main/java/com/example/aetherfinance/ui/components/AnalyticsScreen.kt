package com.example.aetherfinance.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aetherfinance.data.model.CategoryItem
import com.example.aetherfinance.data.model.CurrencyConfig
import com.example.aetherfinance.data.model.TransactionEntity
import com.example.aetherfinance.data.util.FinanceConstants
import com.example.aetherfinance.data.util.Formatters
import com.example.aetherfinance.ui.theme.ExpenseRed
import com.example.aetherfinance.ui.theme.ExpenseRedContainer
import com.example.aetherfinance.ui.theme.IncomeGreen
import com.example.aetherfinance.ui.theme.IncomeGreenContainer
import com.example.aetherfinance.ui.theme.OutlineVariant
import com.example.aetherfinance.ui.theme.PrimaryBlue
import com.example.aetherfinance.ui.theme.TextPrimary
import com.example.aetherfinance.ui.theme.TextSecondary
import com.example.aetherfinance.ui.theme.TextTertiary
import com.example.aetherfinance.ui.theme.WarningAmber
import com.example.aetherfinance.ui.theme.parseHexColor
import kotlin.math.abs

data class CategorySpend(
    val category: CategoryItem,
    val totalSpend: Double,
    val count: Int,
    val percentage: Double
)

@Composable
fun AnalyticsScreen(
    transactions: List<TransactionEntity>,
    currency: CurrencyConfig,
    monthlyBudgetLimit: Double,
    onEditBudgetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalIncome = remember(transactions) {
        transactions.filter { it.type == "INCOME" }.sumOf { it.amount }
    }
    val totalExpense = remember(transactions) {
        transactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }
    }
    val netBalance = totalIncome - totalExpense
    val savingsRate = if (totalIncome > 0) (netBalance / totalIncome) * 100 else 0.0

    val categoryBreakdown = remember(transactions) {
        val mapSpend = mutableMapOf<String, Double>()
        val mapCount = mutableMapOf<String, Int>()

        transactions.filter { it.type == "EXPENSE" }.forEach { tx ->
            mapSpend[tx.category] = (mapSpend[tx.category] ?: 0.0) + tx.amount
            mapCount[tx.category] = (mapCount[tx.category] ?: 0) + 1
        }

        FinanceConstants.DEFAULT_CATEGORIES
            .filter { it.type == "EXPENSE" || it.type == "BOTH" }
            .map { cat ->
                val spent = mapSpend[cat.name] ?: 0.0
                val count = mapCount[cat.name] ?: 0
                val pct = if (totalExpense > 0) (spent / totalExpense) * 100 else 0.0
                CategorySpend(cat, spent, count, pct)
            }
            .filter { it.totalSpend > 0 }
            .sortedByDescending { it.totalSpend }
    }

    val budgetProgress = if (monthlyBudgetLimit > 0) (totalExpense / monthlyBudgetLimit).toFloat() else 0f
    val remainingBudget = monthlyBudgetLimit - totalExpense
    val isOverBudget = remainingBudget < 0

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("analytics_screen")
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            // 4-Card Metric Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Income Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Income", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(IncomeGreen.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = IncomeGreen, modifier = Modifier.size(12.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "+${Formatters.formatCurrency(totalIncome, currency)}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = IncomeGreen
                        )
                        Text("Total earned", fontSize = 10.sp, color = TextTertiary)
                    }
                }

                // Expense Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Spending", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(ExpenseRed.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = ExpenseRed, modifier = Modifier.size(12.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "-${Formatters.formatCurrency(totalExpense, currency)}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = ExpenseRed
                        )
                        Text("Total spent", fontSize = 10.sp, color = TextTertiary)
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Net Flow Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Net Flow", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (netBalance >= 0) IncomeGreenContainer else ExpenseRedContainer)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (netBalance >= 0) "Surplus" else "Deficit",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (netBalance >= 0) IncomeGreen else ExpenseRed
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = Formatters.formatCurrency(netBalance, currency, alwaysShowSign = true),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text("Period balance", fontSize = 10.sp, color = TextTertiary)
                    }
                }

                // Savings Rate Card
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Savings Rate", fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PrimaryBlue.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("Rate", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = Formatters.formatPercentage(savingsRate),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                        Text("Retained income", fontSize = 10.sp, color = TextTertiary)
                    }
                }
            }
        }

        // Monthly Budget Pacing Card
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
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
                                text = "Monthly Budget Target",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Pacing and monthly limit",
                                fontSize = 11.sp,
                                color = TextTertiary
                            )
                        }

                        Button(
                            onClick = onEditBudgetClick,
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue.copy(alpha = 0.12f)),
                            modifier = Modifier.testTag("analytics_edit_budget")
                        ) {
                            Text("Edit Limit", color = PrimaryBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = Formatters.formatCurrency(totalExpense, currency),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "target ${Formatters.formatCurrency(monthlyBudgetLimit, currency)}",
                            fontSize = 12.sp,
                            color = TextTertiary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val pColor = when {
                        budgetProgress > 1f -> ExpenseRed
                        budgetProgress > 0.85f -> WarningAmber
                        else -> PrimaryBlue
                    }

                    LinearProgressIndicator(
                        progress = { budgetProgress.coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = pColor,
                        trackColor = OutlineVariant,
                        strokeCap = StrokeCap.Round
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${(budgetProgress * 100).toInt()}% used",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                        if (isOverBudget) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = ExpenseRed, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Over by ${Formatters.formatCurrency(abs(remainingBudget), currency)}",
                                    fontSize = 11.sp,
                                    color = ExpenseRed,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = IncomeGreen, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${Formatters.formatCurrency(remainingBudget, currency)} left",
                                    fontSize = 11.sp,
                                    color = IncomeGreen,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "SPEND BY CATEGORY",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextTertiary,
                letterSpacing = 1.sp
            )
        }

        items(categoryBreakdown, key = { it.category.id }) { item ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CategoryBadge(
                                iconName = item.category.iconName,
                                colorHex = item.category.colorHex,
                                badgeSize = 34.dp,
                                iconSize = 18.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = item.category.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "${item.count} transactions",
                                    fontSize = 10.sp,
                                    color = TextTertiary
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = Formatters.formatCurrency(item.totalSpend, currency),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = Formatters.formatPercentage(item.percentage),
                                fontSize = 10.sp,
                                color = TextTertiary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { (item.percentage / 100).toFloat().coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = parseHexColor(item.category.colorHex),
                        trackColor = OutlineVariant,
                        strokeCap = StrokeCap.Round
                    )
                }
            }
        }

        if (categoryBreakdown.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No expense transactions recorded yet.", color = TextTertiary, fontSize = 13.sp)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}
