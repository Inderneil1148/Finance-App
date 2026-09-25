package com.example.aetherfinance.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aetherfinance.data.model.CurrencyConfig
import com.example.aetherfinance.ui.theme.PrimaryBlue

@Composable
fun EditBudgetDialog(
    isOpen: Boolean,
    currentLimit: Double,
    currency: CurrencyConfig,
    onDismiss: () -> Unit,
    onSave: (Double) -> Unit
) {
    if (!isOpen) return

    var inputText by remember(currentLimit) {
        mutableStateOf(String.format(java.util.Locale.US, "%.0f", currentLimit))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Monthly Budget", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Set your target monthly spending ceiling:", fontSize = 12.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = currency.symbol,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("budget_dialog_input")
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsed = inputText.toDoubleOrNull()
                    if (parsed != null && parsed >= 0) {
                        onSave(parsed)
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("budget_dialog_save")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
