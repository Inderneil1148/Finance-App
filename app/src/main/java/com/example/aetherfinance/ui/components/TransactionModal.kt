package com.example.aetherfinance.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.aetherfinance.data.model.CurrencyConfig
import com.example.aetherfinance.data.model.CustomTagEntity
import com.example.aetherfinance.data.model.TransactionEntity
import com.example.aetherfinance.data.util.FinanceConstants
import com.example.aetherfinance.data.util.FinanceConstants.TAG_COLOR_PALETTE
import com.example.aetherfinance.ui.theme.ExpenseRed
import com.example.aetherfinance.ui.theme.ExpenseRedContainer
import com.example.aetherfinance.ui.theme.IncomeGreen
import com.example.aetherfinance.ui.theme.IncomeGreenContainer
import com.example.aetherfinance.ui.theme.OutlineVariant
import com.example.aetherfinance.ui.theme.PrimaryBlue
import com.example.aetherfinance.ui.theme.TextPrimary
import com.example.aetherfinance.ui.theme.TextSecondary
import com.example.aetherfinance.ui.theme.TextTertiary
import com.example.aetherfinance.ui.theme.parseHexColor
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TransactionModal(
    isOpen: Boolean,
    editingTransaction: TransactionEntity?,
    currency: CurrencyConfig,
    availableTags: List<CustomTagEntity>,
    onClose: () -> Unit,
    onSave: (TransactionEntity) -> Unit,
    onDelete: (String) -> Unit,
    onCreateTag: (String, String) -> Unit
) {
    if (!isOpen) return

    val isEditing = editingTransaction != null
    var type by remember(editingTransaction) {
        mutableStateOf(editingTransaction?.type ?: "EXPENSE")
    }
    var amountText by remember(editingTransaction) {
        mutableStateOf(editingTransaction?.amount?.let { String.format(Locale.US, "%.2f", it) } ?: "")
    }
    var title by remember(editingTransaction) {
        mutableStateOf(editingTransaction?.title ?: "")
    }
    var note by remember(editingTransaction) {
        mutableStateOf(editingTransaction?.note ?: "")
    }
    var selectedCategory by remember(editingTransaction) {
        mutableStateOf(
            editingTransaction?.category ?: FinanceConstants.DEFAULT_CATEGORIES.first().name
        )
    }
    var selectedTags by remember(editingTransaction) {
        mutableStateOf(editingTransaction?.customTags ?: emptyList())
    }
    var dateText by remember(editingTransaction) {
        mutableStateOf(editingTransaction?.date ?: FinanceConstants.getTodayDate())
    }

    // New Tag Inline Creation
    var isCreatingTag by remember { mutableStateOf(false) }
    var newTagName by remember { mutableStateOf("") }
    var newTagColor by remember { mutableStateOf(TAG_COLOR_PALETTE.first()) }

    var isConfirmingDelete by remember { mutableStateOf(false) }

    val filteredCategories = FinanceConstants.DEFAULT_CATEGORIES.filter {
        it.type == type || it.type == "BOTH"
    }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 28.dp)
                .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                .testTag("transaction_dialog"),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp)
            ) {
                // Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.testTag("dialog_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary
                        )
                    }

                    Text(
                        text = if (isEditing) "Edit Transaction" else "New Transaction",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary
                    )

                    Button(
                        onClick = {
                            val parsedAmount = amountText.toDoubleOrNull()
                            if (parsedAmount != null && parsedAmount > 0 && title.isNotBlank()) {
                                val cal = Calendar.getInstance()
                                val timeFormat = SimpleDateFormat("HH:mm", Locale.US)
                                val tx = TransactionEntity(
                                    id = editingTransaction?.id ?: UUID.randomUUID().toString(),
                                    type = type,
                                    amount = parsedAmount,
                                    title = title.trim(),
                                    category = selectedCategory,
                                    customTags = selectedTags,
                                    date = dateText,
                                    time = editingTransaction?.time ?: timeFormat.format(cal.time),
                                    note = note.trim().ifBlank { null },
                                    createdAt = editingTransaction?.createdAt ?: System.currentTimeMillis()
                                )
                                onSave(tx)
                                onClose()
                            }
                        },
                        enabled = title.isNotBlank() && (amountText.toDoubleOrNull() ?: 0.0) > 0,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("save_transaction_button")
                    ) {
                        Text(if (isEditing) "Save" else "Add")
                    }
                }

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Segmented Type Selector (Expense / Income)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (type == "EXPENSE") ExpenseRedContainer else Color.Transparent)
                                .clickable {
                                    type = "EXPENSE"
                                    val firstExp = FinanceConstants.DEFAULT_CATEGORIES.firstOrNull { it.type == "EXPENSE" || it.type == "BOTH" }
                                    if (firstExp != null) selectedCategory = firstExp.name
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Expense",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (type == "EXPENSE") ExpenseRed else TextSecondary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (type == "INCOME") IncomeGreenContainer else Color.Transparent)
                                .clickable {
                                    type = "INCOME"
                                    val firstInc = FinanceConstants.DEFAULT_CATEGORIES.firstOrNull { it.type == "INCOME" || it.type == "BOTH" }
                                    if (firstInc != null) selectedCategory = firstInc.name
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Income",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (type == "INCOME") IncomeGreen else TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Amount Field
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White)
                            .padding(16.dp)
                    ) {
                        Column {
                            Text(
                                text = "Amount",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextTertiary
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = currency.symbol,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryBlue
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                OutlinedTextField(
                                    value = amountText,
                                    onValueChange = { amountText = it },
                                    placeholder = { Text("0.00", fontSize = 28.sp) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color.Transparent,
                                        unfocusedBorderColor = Color.Transparent
                                    ),
                                    textStyle = androidx.compose.ui.text.TextStyle(
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("tx_amount_input")
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Title & Note Fields
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White)
                            .padding(14.dp)
                    ) {
                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Payee / Title") },
                            placeholder = { Text("e.g. Coffee, Groceries, Rent") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("tx_title_input")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = note,
                            onValueChange = { note = it },
                            label = { Text("Note (Optional)") },
                            placeholder = { Text("Add any details…") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("tx_note_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Category Selector
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White)
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "Category",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            filteredCategories.forEach { cat ->
                                val isSelected = selectedCategory == cat.name
                                val catColor = parseHexColor(cat.colorHex)

                                Row(
                                    modifier = Modifier
                                        .testTag("category_option_${cat.name}")
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) PrimaryBlue else OutlineVariant.copy(alpha = 0.5f))
                                        .clickable { selectedCategory = cat.name }
                                        .padding(horizontal = 10.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) Color.White else catColor)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = cat.name,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else TextPrimary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Custom Tags Section
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White)
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Tags (${selectedTags.size})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { isCreatingTag = !isCreatingTag }
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = PrimaryBlue,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "New Tag",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryBlue
                                )
                            }
                        }

                        // Inline Tag Creator
                        if (isCreatingTag) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(10.dp)
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
                                            .testTag("new_tag_input")
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Button(
                                        onClick = {
                                            val clean = newTagName.trim()
                                            if (clean.isNotBlank()) {
                                                onCreateTag(clean, newTagColor)
                                                if (!selectedTags.contains(clean)) {
                                                    selectedTags = selectedTags + clean
                                                }
                                                newTagName = ""
                                                isCreatingTag = false
                                            }
                                        },
                                        enabled = newTagName.isNotBlank(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                                    ) {
                                        Text("Add")
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Color Palette
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    TAG_COLOR_PALETTE.forEach { colorHex ->
                                        val isColorSelected = newTagColor == colorHex
                                        Box(
                                            modifier = Modifier
                                                .size(22.dp)
                                                .clip(CircleShape)
                                                .background(parseHexColor(colorHex))
                                                .clickable { newTagColor = colorHex }
                                                .then(
                                                    if (isColorSelected) Modifier.border(2.dp, Color.White, CircleShape) else Modifier
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isColorSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Tags Selection Chips
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            availableTags.forEach { tag ->
                                val isSelected = selectedTags.contains(tag.name)
                                TagChip(
                                    name = tag.name,
                                    colorHex = tag.color,
                                    isSelected = isSelected,
                                    onClick = {
                                        selectedTags = if (isSelected) {
                                            selectedTags - tag.name
                                        } else {
                                            selectedTags + tag.name
                                        }
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Date Row
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White)
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Date",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextSecondary
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                val todayStr = FinanceConstants.getTodayDate()
                                val yesterdayStr = FinanceConstants.getOffsetDate(1)

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (dateText == todayStr) PrimaryBlue else OutlineVariant.copy(alpha = 0.5f))
                                        .clickable { dateText = todayStr }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Today",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (dateText == todayStr) Color.White else TextPrimary
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (dateText == yesterdayStr) PrimaryBlue else OutlineVariant.copy(alpha = 0.5f))
                                        .clickable { dateText = yesterdayStr }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Yesterday",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (dateText == yesterdayStr) Color.White else TextPrimary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = dateText,
                            onValueChange = { dateText = it },
                            placeholder = { Text("YYYY-MM-DD") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("tx_date_input")
                        )
                    }

                    // Delete Button if editing
                    if (isEditing) {
                        Spacer(modifier = Modifier.height(18.dp))
                        Button(
                            onClick = {
                                if (isConfirmingDelete) {
                                    editingTransaction?.let { onDelete(it.id) }
                                    onClose()
                                } else {
                                    isConfirmingDelete = true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("delete_transaction_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isConfirmingDelete) "Tap Again to Confirm Delete" else "Delete Transaction"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
