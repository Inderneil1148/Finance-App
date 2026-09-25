package com.example.aetherfinance.data.util

import com.example.aetherfinance.data.model.CurrencyConfig
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

object Formatters {

    fun formatCurrency(
        amount: Double,
        currency: CurrencyConfig,
        hideDecimals: Boolean = false,
        alwaysShowSign: Boolean = false
    ): String {
        val isJpy = currency.code == "JPY"
        val decimals = if (hideDecimals || isJpy) 0 else 2
        val locale = if (currency.code == "INR") Locale("en", "IN") else Locale.US
        val numberFormat = NumberFormat.getNumberInstance(locale).apply {
            minimumFractionDigits = decimals
            maximumFractionDigits = decimals
        }

        val absVal = abs(amount)
        val formattedNum = numberFormat.format(absVal)

        val formattedAmount = if (currency.placement == "prefix") {
            "${currency.symbol}$formattedNum"
        } else {
            "$formattedNum ${currency.symbol}"
        }

        val sign = when {
            amount < 0 -> "-"
            alwaysShowSign && amount > 0 -> "+"
            else -> ""
        }

        return if (sign.isNotEmpty()) "$sign$formattedAmount" else formattedAmount
    }

    fun formatDateLabel(dateString: String): String {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val date = sdf.parse(dateString) ?: return dateString

            val calToday = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val calTarget = Calendar.getInstance().apply {
                time = date
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val diffDays = (calToday.timeInMillis - calTarget.timeInMillis) / (24 * 60 * 60 * 1000)

            when (diffDays) {
                0L -> "Today"
                1L -> "Yesterday"
                else -> {
                    val outFormat = if (calToday.get(Calendar.YEAR) == calTarget.get(Calendar.YEAR)) {
                        SimpleDateFormat("EEE, MMM d", Locale.US)
                    } else {
                        SimpleDateFormat("MMM d, yyyy", Locale.US)
                    }
                    outFormat.format(date)
                }
            }
        } catch (_: Exception) {
            dateString
        }
    }

    fun formatPercentage(value: Double): String {
        return "${value.roundToInt()}%"
    }
}
