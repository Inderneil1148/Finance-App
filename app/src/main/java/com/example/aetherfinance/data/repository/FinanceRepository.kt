package com.example.aetherfinance.data.repository

import com.example.aetherfinance.data.local.FinanceDao
import com.example.aetherfinance.data.model.CustomTagEntity
import com.example.aetherfinance.data.model.TransactionEntity
import com.example.aetherfinance.data.model.UserPreferenceEntity
import com.example.aetherfinance.data.util.FinanceConstants
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class BackupPayload(
    val version: Int = 1,
    val exportedAt: Long = System.currentTimeMillis(),
    val currencyCode: String,
    val currencySymbol: String,
    val monthlyBudgetLimit: Double,
    val tags: List<CustomTagEntity>,
    val transactions: List<TransactionEntity>
)

class FinanceRepository(private val dao: FinanceDao) {

    val allTransactions: Flow<List<TransactionEntity>> = dao.getAllTransactions()
    val allTags: Flow<List<CustomTagEntity>> = dao.getAllTags()
    val userPreferences: Flow<UserPreferenceEntity?> = dao.getUserPreferences()

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    suspend fun addTransaction(tx: TransactionEntity) {
        dao.insertTransaction(tx)
    }

    suspend fun updateTransaction(tx: TransactionEntity) {
        dao.updateTransaction(tx)
    }

    suspend fun deleteTransaction(id: String) {
        dao.deleteTransactionById(id)
    }

    suspend fun addTag(tag: CustomTagEntity) {
        dao.insertTag(tag)
    }

    suspend fun updateTag(tag: CustomTagEntity) {
        dao.updateTag(tag)
    }

    suspend fun deleteTag(id: String) {
        dao.deleteTagById(id)
    }

    suspend fun updatePreferences(prefs: UserPreferenceEntity) {
        dao.saveUserPreferences(prefs)
    }

    suspend fun resetToSeedData() {
        dao.clearAllTransactions()
        dao.clearAllTags()
        dao.insertTags(FinanceConstants.DEFAULT_TAGS)
        dao.insertTransactions(FinanceConstants.generateSeedTransactions())
        dao.saveUserPreferences(
            UserPreferenceEntity(
                currencyCode = "INR",
                currencySymbol = "₹",
                currencyName = "Indian Rupee",
                currencyPlacement = "prefix",
                monthlyBudgetLimit = 50000.0
            )
        )
    }

    fun generateCsv(transactions: List<TransactionEntity>, currencySymbol: String): String {
        val sb = StringBuilder()
        sb.append("ID,Date,Time,Type,Amount,Currency,Title,Category,Custom Tags,Notes\n")
        transactions.forEach { tx ->
            val safeTitle = tx.title.replace("\"", "\"\"")
            val safeCategory = tx.category.replace("\"", "\"\"")
            val safeTags = tx.customTags.joinToString("; ").replace("\"", "\"\"")
            val safeNote = (tx.note ?: "").replace("\"", "\"\"")
            sb.append("\"${tx.id}\",")
            sb.append("\"${tx.date}\",")
            sb.append("\"${tx.time ?: ""}\",")
            sb.append("\"${tx.type}\",")
            sb.append("${String.format(java.util.Locale.US, "%.2f", tx.amount)},")
            sb.append("\"$currencySymbol\",")
            sb.append("\"$safeTitle\",")
            sb.append("\"$safeCategory\",")
            sb.append("\"$safeTags\",")
            sb.append("\"$safeNote\"\n")
        }
        return sb.toString()
    }

    fun exportBackupJson(
        transactions: List<TransactionEntity>,
        tags: List<CustomTagEntity>,
        prefs: UserPreferenceEntity
    ): String {
        val payload = BackupPayload(
            version = 1,
            exportedAt = System.currentTimeMillis(),
            currencyCode = prefs.currencyCode,
            currencySymbol = prefs.currencySymbol,
            monthlyBudgetLimit = prefs.monthlyBudgetLimit,
            tags = tags,
            transactions = transactions
        )
        return json.encodeToString(payload)
    }

    suspend fun restoreBackupJson(jsonString: String): Boolean {
        return try {
            val payload = json.decodeFromString<BackupPayload>(jsonString)
            dao.clearAllTransactions()
            dao.clearAllTags()
            dao.insertTags(payload.tags)
            dao.insertTransactions(payload.transactions)
            dao.saveUserPreferences(
                UserPreferenceEntity(
                    currencyCode = payload.currencyCode,
                    currencySymbol = payload.currencySymbol,
                    monthlyBudgetLimit = payload.monthlyBudgetLimit
                )
            )
            true
        } catch (_: Exception) {
            false
        }
    }
}
