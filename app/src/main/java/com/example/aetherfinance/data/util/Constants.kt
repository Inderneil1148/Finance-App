package com.example.aetherfinance.data.util

import com.example.aetherfinance.data.model.CategoryItem
import com.example.aetherfinance.data.model.CurrencyConfig
import com.example.aetherfinance.data.model.CustomTagEntity
import com.example.aetherfinance.data.model.TransactionEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object FinanceConstants {

    val DEFAULT_CATEGORIES = listOf(
        CategoryItem("cat-food", "Food & Dining", "Utensils", "#FF9500", "EXPENSE"),
        CategoryItem("cat-groceries", "Groceries", "ShoppingCart", "#34C759", "EXPENSE"),
        CategoryItem("cat-housing", "Housing & Rent", "Home", "#007AFF", "EXPENSE"),
        CategoryItem("cat-transport", "Transportation", "DirectionsCar", "#5856D6", "EXPENSE"),
        CategoryItem("cat-shopping", "Shopping", "ShoppingBag", "#AF52DE", "EXPENSE"),
        CategoryItem("cat-entertainment", "Entertainment", "Movie", "#FF2D55", "EXPENSE"),
        CategoryItem("cat-health", "Health & Wellness", "Favorite", "#FF3B30", "EXPENSE"),
        CategoryItem("cat-utilities", "Bills & Utilities", "ElectricBolt", "#FFCC00", "EXPENSE"),
        CategoryItem("cat-work", "Work & Tech", "Work", "#00C7BE", "EXPENSE"),
        CategoryItem("cat-salary", "Salary & Income", "Paid", "#30B0C7", "INCOME"),
        CategoryItem("cat-freelance", "Side Projects", "Laptop", "#34C759", "INCOME"),
        CategoryItem("cat-other", "Miscellaneous", "Category", "#8E8E93", "BOTH")
    )

    val DEFAULT_TAGS = listOf(
        CustomTagEntity("tag-coffee", "coffee", "#FF9500", 1710000000000L),
        CustomTagEntity("tag-dining-out", "dining-out", "#FF2D55", 1710000001000L),
        CustomTagEntity("tag-groceries", "groceries", "#34C759", 1710000002000L),
        CustomTagEntity("tag-commute", "commute", "#007AFF", 1710000003000L),
        CustomTagEntity("tag-subscription", "subscription", "#AF52DE", 1710000004000L),
        CustomTagEntity("tag-tax-deductible", "tax-deductible", "#5856D6", 1710000005000L),
        CustomTagEntity("tag-fitness", "fitness", "#FF3B30", 1710000006000L),
        CustomTagEntity("tag-travel", "travel", "#00C7BE", 1710000007000L),
        CustomTagEntity("tag-work", "work", "#8E8E93", 1710000008000L)
    )

    val TAG_COLOR_PALETTE = listOf(
        "#007AFF",
        "#34C759",
        "#5856D6",
        "#FF9500",
        "#FF2D55",
        "#AF52DE",
        "#FF3B30",
        "#00C7BE",
        "#8E8E93"
    )

    val SUPPORTED_CURRENCIES = listOf(
        CurrencyConfig("INR", "₹", "Indian Rupee", "prefix"),
        CurrencyConfig("USD", "$", "US Dollar", "prefix"),
        CurrencyConfig("EUR", "€", "Euro", "prefix"),
        CurrencyConfig("GBP", "£", "British Pound", "prefix"),
        CurrencyConfig("JPY", "¥", "Japanese Yen", "prefix"),
        CurrencyConfig("CAD", "CA$", "Canadian Dollar", "prefix"),
        CurrencyConfig("AUD", "A$", "Australian Dollar", "prefix"),
        CurrencyConfig("CHF", "CHF", "Swiss Franc", "prefix")
    )

    fun getOffsetDate(dayOffset: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -dayOffset)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(calendar.time)
    }

    fun getTodayDate(): String = getOffsetDate(0)

    fun generateSeedTransactions(): List<TransactionEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            TransactionEntity(
                id = "tx-1",
                type = "EXPENSE",
                amount = 280.00,
                title = "Artisan Espresso & Pastry",
                category = "Food & Dining",
                customTags = listOf("coffee"),
                date = getOffsetDate(0),
                time = "08:42",
                note = "Morning flat white & croissant",
                createdAt = now - 3600000L * 2
            ),
            TransactionEntity(
                id = "tx-2",
                type = "EXPENSE",
                amount = 2450.00,
                title = "Fresh Market Groceries",
                category = "Groceries",
                customTags = listOf("groceries"),
                date = getOffsetDate(0),
                time = "12:15",
                note = "Fresh produce, milk, vegetables & fruit",
                createdAt = now - 3600000L * 4
            ),
            TransactionEntity(
                id = "tx-3",
                type = "EXPENSE",
                amount = 450.00,
                title = "Metro Transit Smart Card",
                category = "Transportation",
                customTags = listOf("commute"),
                date = getOffsetDate(1),
                time = "09:05",
                note = "Metro card monthly refill",
                createdAt = now - 3600000L * 26
            ),
            TransactionEntity(
                id = "tx-4",
                type = "EXPENSE",
                amount = 1850.00,
                title = "Bistro Laurent Dinner",
                category = "Food & Dining",
                customTags = listOf("dining-out"),
                date = getOffsetDate(1),
                time = "20:10",
                note = "Dinner with Marcus",
                createdAt = now - 3600000L * 30
            ),
            TransactionEntity(
                id = "tx-5",
                type = "INCOME",
                amount = 125000.00,
                title = "Monthly Salary Credit",
                category = "Salary & Income",
                customTags = emptyList(),
                date = getOffsetDate(3),
                time = "06:00",
                note = "Primary corporate net deposit",
                createdAt = now - 3600000L * 72
            ),
            TransactionEntity(
                id = "tx-6",
                type = "EXPENSE",
                amount = 28000.00,
                title = "Apartment Monthly Rent",
                category = "Housing & Rent",
                customTags = emptyList(),
                date = getOffsetDate(4),
                time = "10:00",
                note = "Residential lease transfer",
                createdAt = now - 3600000L * 96
            ),
            TransactionEntity(
                id = "tx-7",
                type = "EXPENSE",
                amount = 1499.00,
                title = "GitHub & Cloud Subscriptions",
                category = "Work & Tech",
                customTags = listOf("subscription", "tax-deductible", "work"),
                date = getOffsetDate(5),
                time = "11:30",
                note = "Developer tooling renewal",
                createdAt = now - 3600000L * 120
            ),
            TransactionEntity(
                id = "tx-8",
                type = "EXPENSE",
                amount = 2500.00,
                title = "Fitness & Bouldering Pass",
                category = "Health & Wellness",
                customTags = listOf("fitness"),
                date = getOffsetDate(6),
                time = "14:00",
                note = "Monthly climbing access pass",
                createdAt = now - 3600000L * 144
            ),
            TransactionEntity(
                id = "tx-9",
                type = "INCOME",
                amount = 35000.00,
                title = "Design System Consulting",
                category = "Side Projects",
                customTags = listOf("tax-deductible", "work"),
                date = getOffsetDate(8),
                time = "16:45",
                note = "Milestone 2 design audit",
                createdAt = now - 3600000L * 190
            ),
            TransactionEntity(
                id = "tx-10",
                type = "EXPENSE",
                amount = 720.00,
                title = "Roastery Pour Over & Beans",
                category = "Food & Dining",
                customTags = listOf("coffee", "groceries"),
                date = getOffsetDate(10),
                time = "09:20",
                note = "Single origin whole bean pack",
                createdAt = now - 3600000L * 240
            ),
            TransactionEntity(
                id = "tx-11",
                type = "EXPENSE",
                amount = 1850.00,
                title = "Electric & Fiber Broadband",
                category = "Bills & Utilities",
                customTags = listOf("tax-deductible"),
                date = getOffsetDate(12),
                time = "15:10",
                note = "Home broadband fiber utility",
                createdAt = now - 3600000L * 280
            ),
            TransactionEntity(
                id = "tx-12",
                type = "EXPENSE",
                amount = 3800.00,
                title = "Weekend Rail Tickets",
                category = "Transportation",
                customTags = listOf("travel"),
                date = getOffsetDate(20),
                time = "17:30",
                note = "Weekend trip booking",
                createdAt = now - 3600000L * 480
            ),
            TransactionEntity(
                id = "tx-13",
                type = "EXPENSE",
                amount = 5200.00,
                title = "Annual Tech Conference Pass",
                category = "Work & Tech",
                customTags = listOf("work", "tax-deductible"),
                date = getOffsetDate(27),
                time = "14:20",
                note = "Design & Engineering summit pass",
                createdAt = now - 3600000L * 648
            )
        )
    }
}
