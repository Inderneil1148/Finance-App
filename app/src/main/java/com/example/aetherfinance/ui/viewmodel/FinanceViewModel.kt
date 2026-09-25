package com.example.aetherfinance.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aetherfinance.data.local.AppDatabase
import com.example.aetherfinance.data.model.CurrencyConfig
import com.example.aetherfinance.data.model.CustomTagEntity
import com.example.aetherfinance.data.model.NavigationTab
import com.example.aetherfinance.data.model.TimeFilter
import com.example.aetherfinance.data.model.TransactionEntity
import com.example.aetherfinance.data.model.UserPreferenceEntity
import com.example.aetherfinance.data.repository.FinanceRepository
import com.example.aetherfinance.data.util.FinanceConstants
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

data class FinanceUiState(
    val activeTab: NavigationTab = NavigationTab.LEDGER,
    val timeFilter: TimeFilter = TimeFilter.THIS_MONTH,
    val selectedTag: String? = null,
    val typeFilter: String = "ALL", // "ALL", "EXPENSE", "INCOME"
    val searchQuery: String = "",
    val isTransactionModalOpen: Boolean = false,
    val editingTransaction: TransactionEntity? = null,
    val isEditBudgetModalOpen: Boolean = false
)

class FinanceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FinanceRepository

    init {
        val db = AppDatabase.getInstance(application)
        repository = FinanceRepository(db.financeDao())
    }

    private val _uiState = MutableStateFlow(FinanceUiState())
    val uiState: StateFlow<FinanceUiState> = _uiState.asStateFlow()

    val allTransactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTags: StateFlow<List<CustomTagEntity>> = repository.allTags
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userPreferences: StateFlow<UserPreferenceEntity> = repository.userPreferences
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UserPreferenceEntity()
        )

    // Filtered Transactions
    val filteredTransactions: StateFlow<List<TransactionEntity>> = combine(
        allTransactions,
        _uiState
    ) { transactions, state ->
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val now = Calendar.getInstance()
        val currentYear = now.get(Calendar.YEAR)
        val currentMonth = String.format(Locale.US, "%02d", now.get(Calendar.MONTH) + 1)
        val thisMonthPrefix = "$currentYear-$currentMonth"

        val lastMonthCal = Calendar.getInstance().apply { add(Calendar.MONTH, -1) }
        val lastMonthPrefix = "${lastMonthCal.get(Calendar.YEAR)}-${String.format(Locale.US, "%02d", lastMonthCal.get(Calendar.MONTH) + 1)}"

        val d30Cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -30) }
        val d30Str = sdf.format(d30Cal.time)

        transactions.filter { tx ->
            // Time filter
            val matchesTime = when (state.timeFilter) {
                TimeFilter.THIS_MONTH -> tx.date.startsWith(thisMonthPrefix)
                TimeFilter.LAST_30_DAYS -> tx.date >= d30Str
                TimeFilter.LAST_MONTH -> tx.date.startsWith(lastMonthPrefix)
                TimeFilter.ALL_TIME -> true
            }

            // Tag filter
            val matchesTag = state.selectedTag == null || tx.customTags.contains(state.selectedTag)

            // Type filter
            val matchesType = when (state.typeFilter) {
                "EXPENSE" -> tx.type == "EXPENSE"
                "INCOME" -> tx.type == "INCOME"
                else -> true
            }

            // Search Query
            val matchesSearch = state.searchQuery.isBlank() ||
                    tx.title.contains(state.searchQuery, ignoreCase = true) ||
                    tx.category.contains(state.searchQuery, ignoreCase = true) ||
                    (tx.note?.contains(state.searchQuery, ignoreCase = true) == true) ||
                    tx.customTags.any { it.contains(state.searchQuery, ignoreCase = true) }

            matchesTime && matchesTag && matchesType && matchesSearch
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setActiveTab(tab: NavigationTab) {
        _uiState.value = _uiState.value.copy(activeTab = tab)
    }

    fun setTimeFilter(filter: TimeFilter) {
        _uiState.value = _uiState.value.copy(timeFilter = filter)
    }

    fun setSelectedTag(tagName: String?) {
        _uiState.value = _uiState.value.copy(selectedTag = tagName)
    }

    fun setTypeFilter(type: String) {
        _uiState.value = _uiState.value.copy(typeFilter = type)
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun openNewTransactionModal() {
        _uiState.value = _uiState.value.copy(
            isTransactionModalOpen = true,
            editingTransaction = null
        )
    }

    fun openEditTransactionModal(transaction: TransactionEntity) {
        _uiState.value = _uiState.value.copy(
            isTransactionModalOpen = true,
            editingTransaction = transaction
        )
    }

    fun closeTransactionModal() {
        _uiState.value = _uiState.value.copy(
            isTransactionModalOpen = false,
            editingTransaction = null
        )
    }

    fun setEditBudgetModalOpen(open: Boolean) {
        _uiState.value = _uiState.value.copy(isEditBudgetModalOpen = open)
    }

    fun saveTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            if (allTransactions.value.any { it.id == transaction.id }) {
                repository.updateTransaction(transaction)
            } else {
                repository.addTransaction(transaction)
            }
        }
    }

    fun deleteTransaction(id: String) {
        viewModelScope.launch {
            repository.deleteTransaction(id)
        }
    }

    fun createTag(name: String, color: String) {
        viewModelScope.launch {
            val clean = name.trim().lowercase().replace("[^a-z0-9_-]".toRegex(), "-")
            if (clean.isNotBlank() && allTags.value.none { it.name == clean }) {
                val tag = CustomTagEntity(
                    id = "tag-${UUID.randomUUID()}",
                    name = clean,
                    color = color
                )
                repository.addTag(tag)
            }
        }
    }

    fun updateTag(tag: CustomTagEntity) {
        viewModelScope.launch {
            repository.updateTag(tag)
        }
    }

    fun deleteTag(id: String) {
        viewModelScope.launch {
            repository.deleteTag(id)
        }
    }

    fun updateCurrency(currency: CurrencyConfig) {
        viewModelScope.launch {
            val current = userPreferences.value
            repository.updatePreferences(
                current.copy(
                    currencyCode = currency.code,
                    currencySymbol = currency.symbol,
                    currencyName = currency.name,
                    currencyPlacement = currency.placement
                )
            )
        }
    }

    fun updateMonthlyBudget(limit: Double) {
        viewModelScope.launch {
            val current = userPreferences.value
            repository.updatePreferences(current.copy(monthlyBudgetLimit = limit))
        }
    }

    fun resetData() {
        viewModelScope.launch {
            repository.resetToSeedData()
        }
    }

    fun exportCsv(): String {
        val prefs = userPreferences.value
        return repository.generateCsv(allTransactions.value, prefs.currencySymbol)
    }

    fun exportJson(): String {
        return repository.exportBackupJson(
            allTransactions.value,
            allTags.value,
            userPreferences.value
        )
    }

    fun importJson(json: String): Boolean {
        var success = false
        viewModelScope.launch {
            success = repository.restoreBackupJson(json)
        }
        return success
    }
}
