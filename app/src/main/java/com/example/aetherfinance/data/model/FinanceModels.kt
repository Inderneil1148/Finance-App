package com.example.aetherfinance.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
enum class TransactionType {
    EXPENSE,
    INCOME
}

@Serializable
@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    val id: String,
    val type: String, // "EXPENSE" or "INCOME"
    val amount: Double,
    val title: String,
    val category: String,
    val customTags: List<String>,
    val date: String, // YYYY-MM-DD
    val time: String? = null, // HH:mm
    val note: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
@Entity(tableName = "custom_tags")
data class CustomTagEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val color: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
data class CategoryItem(
    val id: String,
    val name: String,
    val iconName: String,
    val colorHex: String,
    val type: String // "EXPENSE", "INCOME", or "BOTH"
)

@Serializable
data class CurrencyConfig(
    val code: String = "INR",
    val symbol: String = "₹",
    val name: String = "Indian Rupee",
    val placement: String = "prefix"
)

@Serializable
data class BudgetConfig(
    val monthlyLimit: Double = 50000.0
)

@Serializable
@Entity(tableName = "user_preferences")
data class UserPreferenceEntity(
    @PrimaryKey
    val id: Int = 1,
    val currencyCode: String = "INR",
    val currencySymbol: String = "₹",
    val currencyName: String = "Indian Rupee",
    val currencyPlacement: String = "prefix",
    val monthlyBudgetLimit: Double = 50000.0
)

enum class TimeFilter(val label: String) {
    THIS_MONTH("This Month"),
    LAST_30_DAYS("Last 30 Days"),
    LAST_MONTH("Last Month"),
    ALL_TIME("All Time")
}

enum class NavigationTab(val label: String) {
    LEDGER("Ledger"),
    TAGS("Tags"),
    ANALYTICS("Analytics"),
    SETTINGS("Settings")
}
