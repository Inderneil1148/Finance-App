package com.example.aetherfinance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import kotlin.math.abs

@Composable
fun OverviewCard(
    totalIncome: Double,
    totalExpense: Double,
    currency: CurrencyConfig,
    monthlyBudgetLimit: Double,
    onEditBudgetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val netBalance = totalIncome - totalExpense
    val savingsRate = if (totalIncome > 0) (netBalance / totalIncome) * 100 else 0.0
    val budgetProgress = if (monthlyBudgetLimit > 0) (totalExpense / monthlyBudgetLimit).toFloat() else 0f
    val remainingBudget = monthlyBudgetLimit - totalExpense
    val isOverBudget = remainingBudget < 0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("overview_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            // Net Balance Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Net Cash Flow",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary
                    )
                    Text(
                        text = Formatters.formatCurrency(netBalance, currency, alwaysShowSign = true),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (netBalance >= 0) TextPrimary else ExpenseRed
                    )
                }

                // Savings rate capsule
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (netBalance >= 0) IncomeGreenContainer else ExpenseRedContainer)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "${Formatters.formatPercentage(savingsRate)} Savings",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (netBalance >= 0) IncomeGreen else ExpenseRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Income / Expense Stats Split Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Income
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(IncomeGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = IncomeGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Income",
                            fontSize = 10.sp,
                            color = TextTertiary,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "+${Formatters.formatCurrency(totalIncome, currency)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = IncomeGreen
                        )
                    }
                }

                // Spending
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(ExpenseRed.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = ExpenseRed,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Expenses",
                            fontSize = 10.sp,
                            color = TextTertiary,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "-${Formatters.formatCurrency(totalExpense, currency)}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ExpenseRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Monthly Budget Progress Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, OutlineVariant.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Monthly Budget Limit",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                    Text(
                        text = "Edit Limit",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue,
                        modifier = Modifier
                            .testTag("edit_budget_button")
                            .clickable(onClick = onEditBudgetClick)
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = Formatters.formatCurrency(totalExpense, currency),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "of ${Formatters.formatCurrency(monthlyBudgetLimit, currency)}",
                        fontSize = 11.sp,
                        color = TextTertiary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Progress Bar
                val progressColor = when {
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
                    color = progressColor,
                    trackColor = OutlineVariant,
                    strokeCap = StrokeCap.Round
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${(budgetProgress * 100).toInt()}% used",
                        fontSize = 10.sp,
                        color = TextTertiary,
                        fontWeight = FontWeight.Medium
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isOverBudget) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = ExpenseRed,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Over by ${Formatters.formatCurrency(abs(remainingBudget), currency)}",
                                fontSize = 10.sp,
                                color = ExpenseRed,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = IncomeGreen,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${Formatters.formatCurrency(remainingBudget, currency)} left",
                                fontSize = 10.sp,
                                color = IncomeGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
