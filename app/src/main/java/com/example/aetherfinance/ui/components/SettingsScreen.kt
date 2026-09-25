package com.example.aetherfinance.ui.components

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aetherfinance.data.model.CurrencyConfig
import com.example.aetherfinance.data.model.CustomTagEntity
import com.example.aetherfinance.data.model.TransactionEntity
import com.example.aetherfinance.data.model.UserPreferenceEntity
import com.example.aetherfinance.data.util.FinanceConstants
import com.example.aetherfinance.ui.theme.ExpenseRed
import com.example.aetherfinance.ui.theme.IncomeGreen
import com.example.aetherfinance.ui.theme.OutlineVariant
import com.example.aetherfinance.ui.theme.PrimaryBlue
import com.example.aetherfinance.ui.theme.TextPrimary
import com.example.aetherfinance.ui.theme.TextSecondary
import com.example.aetherfinance.ui.theme.TextTertiary

fun shareText(context: Context, text: String, title: String) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, title)
    context.startActivity(shareIntent)
}

@Composable
fun SettingsScreen(
    currentCurrency: CurrencyConfig,
    monthlyBudgetLimit: Double,
    transactions: List<TransactionEntity>,
    tags: List<CustomTagEntity>,
    onUpdateCurrency: (CurrencyConfig) -> Unit,
    onUpdateBudget: (Double) -> Unit,
    onResetData: () -> Unit,
    onExportCsv: () -> String,
    onExportJson: () -> String,
    onImportJson: (String) -> Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var budgetInput by remember(monthlyBudgetLimit) {
        mutableStateOf(String.format(java.util.Locale.US, "%.0f", monthlyBudgetLimit))
    }
    var showSavedBudgetNotice by remember { mutableStateOf(false) }

    var showResetDialog by remember { mutableStateOf(false) }
    var showRestoreDialog by remember { mutableStateOf(false) }
    var restoreJsonText by remember { mutableStateOf("") }
    var restoreError by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("settings_screen")
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "BASE CURRENCY",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextTertiary,
                letterSpacing = 1.sp
            )
        }

        // Currency Grid Selector
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    FinanceConstants.SUPPORTED_CURRENCIES.chunked(2).forEach { rowCurrencies ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowCurrencies.forEach { curr ->
                                val isSelected = curr.code == currentCurrency.code
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("currency_option_${curr.code}")
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(if (isSelected) PrimaryBlue else MaterialTheme.colorScheme.background)
                                        .clickable { onUpdateCurrency(curr) }
                                        .padding(horizontal = 12.dp, vertical = 10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = curr.symbol,
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) Color.White else PrimaryBlue
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = curr.code,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = if (isSelected) Color.White.copy(alpha = 0.9f) else TextSecondary
                                                )
                                            }
                                            Text(
                                                text = curr.name,
                                                fontSize = 10.sp,
                                                color = if (isSelected) Color.White.copy(alpha = 0.8f) else TextTertiary
                                            )
                                        }

                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = Color.White,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "MONTHLY BUDGET CAP",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextTertiary,
                letterSpacing = 1.sp
            )
        }

        // Budget Cap Input Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentCurrency.symbol,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = budgetInput,
                            onValueChange = { budgetInput = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("settings_budget_input")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val parsed = budgetInput.toDoubleOrNull()
                                if (parsed != null && parsed >= 0) {
                                    onUpdateBudget(parsed)
                                    showSavedBudgetNotice = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.testTag("settings_save_budget_button")
                        ) {
                            Text(if (showSavedBudgetNotice) "Saved" else "Update")
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "DATA & EXPORT",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextTertiary,
                letterSpacing = 1.sp
            )
        }

        // Data Management Card
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
            ) {
                Column {
                    // Export CSV
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val csv = onExportCsv()
                                shareText(context, csv, "Export Aether Finance CSV")
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(IncomeGreen.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.FileDownload, contentDescription = null, tint = IncomeGreen, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Export CSV Spreadsheet", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Text("Compatible with Google Sheets & Excel", fontSize = 11.sp, color = TextTertiary)
                        }
                    }

                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(OutlineVariant))

                    // Backup JSON
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val json = onExportJson()
                                shareText(context, json, "Aether Finance Backup JSON")
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(PrimaryBlue.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.FileDownload, contentDescription = null, tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Backup JSON Snapshot", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Text("Preserves all transactions and custom tags", fontSize = 11.sp, color = TextTertiary)
                        }
                    }

                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(OutlineVariant))

                    // Restore JSON
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showRestoreDialog = true }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF6750A4).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.FileUpload, contentDescription = null, tint = Color(0xFF6750A4), modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Restore Backup JSON", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                            Text("Paste backup JSON snapshot to restore data", fontSize = 11.sp, color = TextTertiary)
                        }
                    }

                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(OutlineVariant))

                    // Reset Demo Data
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showResetDialog = true }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(ExpenseRed.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = ExpenseRed, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Reset to Demo Data", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = ExpenseRed)
                            Text("Restores default transactions, tags and ₹ INR limit", fontSize = 11.sp, color = TextTertiary)
                        }
                    }
                }
            }
        }

        item {
            // App Info Card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, OutlineVariant)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Aether Finance", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("Version 1.0 • Android Edition", fontSize = 11.sp, color = TextTertiary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Minimal, mobile-first personal finance tracker with custom tagging, visual clarity, and precise budget insights.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(18.dp))
        }
    }

    // Reset Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset to Demo Data?") },
            text = { Text("This will reset all current transactions and custom tags to default starter data.") },
            confirmButton = {
                Button(
                    onClick = {
                        onResetData()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed)
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Restore Backup Dialog
    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            title = { Text("Restore Backup") },
            text = {
                Column {
                    Text("Paste the JSON backup string below:", fontSize = 12.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = restoreJsonText,
                        onValueChange = {
                            restoreJsonText = it
                            restoreError = false
                        },
                        placeholder = { Text("{\"version\": 1, ...}") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        isError = restoreError
                    )
                    if (restoreError) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Invalid backup JSON format", color = ExpenseRed, fontSize = 11.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val success = onImportJson(restoreJsonText.trim())
                        if (success) {
                            showRestoreDialog = false
                            restoreJsonText = ""
                        } else {
                            restoreError = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text("Restore")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
