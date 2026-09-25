package com.example.aetherfinance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.aetherfinance.data.model.CurrencyConfig
import com.example.aetherfinance.data.model.NavigationTab
import com.example.aetherfinance.ui.components.AnalyticsScreen
import com.example.aetherfinance.ui.components.EditBudgetDialog
import com.example.aetherfinance.ui.components.LedgerScreen
import com.example.aetherfinance.ui.components.SettingsScreen
import com.example.aetherfinance.ui.components.TagAnalyticsScreen
import com.example.aetherfinance.ui.components.TransactionModal
import com.example.aetherfinance.ui.theme.AetherFinanceTheme
import com.example.aetherfinance.ui.theme.PrimaryBlue
import com.example.aetherfinance.ui.theme.TextPrimary
import com.example.aetherfinance.ui.theme.TextSecondary
import com.example.aetherfinance.ui.viewmodel.FinanceViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: FinanceViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AetherFinanceTheme {
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
                val filteredTransactions by viewModel.filteredTransactions.collectAsStateWithLifecycle()
                val allTags by viewModel.allTags.collectAsStateWithLifecycle()
                val userPrefs by viewModel.userPreferences.collectAsStateWithLifecycle()

                val currentCurrency = remember(userPrefs) {
                    CurrencyConfig(
                        code = userPrefs.currencyCode,
                        symbol = userPrefs.currencySymbol,
                        name = userPrefs.currencyName,
                        placement = userPrefs.currencyPlacement
                    )
                }

                // Handle system back navigation
                BackHandler(enabled = uiState.activeTab != NavigationTab.LEDGER || uiState.isTransactionModalOpen || uiState.isEditBudgetModalOpen) {
                    when {
                        uiState.isTransactionModalOpen -> viewModel.closeTransactionModal()
                        uiState.isEditBudgetModalOpen -> viewModel.setEditBudgetModalOpen(false)
                        uiState.activeTab != NavigationTab.LEDGER -> viewModel.setActiveTab(NavigationTab.LEDGER)
                    }
                }

                val totalIncome = remember(filteredTransactions) {
                    filteredTransactions.filter { it.type == "INCOME" }.sumOf { it.amount }
                }
                val totalExpense = remember(filteredTransactions) {
                    filteredTransactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = {
                                Text(
                                    text = when (uiState.activeTab) {
                                        NavigationTab.LEDGER -> "Aether Finance"
                                        NavigationTab.TAGS -> "Custom Tags"
                                        NavigationTab.ANALYTICS -> "Analytics & Budget"
                                        NavigationTab.SETTINGS -> "Settings"
                                    },
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            },
                            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                containerColor = MaterialTheme.colorScheme.background
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = Color.White,
                            modifier = Modifier.testTag("bottom_nav")
                        ) {
                            NavigationBarItem(
                                selected = uiState.activeTab == NavigationTab.LEDGER,
                                onClick = { viewModel.setActiveTab(NavigationTab.LEDGER) },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.ReceiptLong,
                                        contentDescription = "Ledger",
                                        modifier = Modifier.size(22.dp)
                                    )
                                },
                                label = { Text("Ledger", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PrimaryBlue,
                                    selectedTextColor = PrimaryBlue,
                                    indicatorColor = PrimaryBlue.copy(alpha = 0.12f)
                                ),
                                modifier = Modifier.testTag("nav_tab_ledger")
                            )

                            NavigationBarItem(
                                selected = uiState.activeTab == NavigationTab.TAGS,
                                onClick = { viewModel.setActiveTab(NavigationTab.TAGS) },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.LocalOffer,
                                        contentDescription = "Tags",
                                        modifier = Modifier.size(22.dp)
                                    )
                                },
                                label = { Text("Tags", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PrimaryBlue,
                                    selectedTextColor = PrimaryBlue,
                                    indicatorColor = PrimaryBlue.copy(alpha = 0.12f)
                                ),
                                modifier = Modifier.testTag("nav_tab_tags")
                            )

                            NavigationBarItem(
                                selected = uiState.activeTab == NavigationTab.ANALYTICS,
                                onClick = { viewModel.setActiveTab(NavigationTab.ANALYTICS) },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.PieChart,
                                        contentDescription = "Analytics",
                                        modifier = Modifier.size(22.dp)
                                    )
                                },
                                label = { Text("Analytics", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PrimaryBlue,
                                    selectedTextColor = PrimaryBlue,
                                    indicatorColor = PrimaryBlue.copy(alpha = 0.12f)
                                ),
                                modifier = Modifier.testTag("nav_tab_analytics")
                            )

                            NavigationBarItem(
                                selected = uiState.activeTab == NavigationTab.SETTINGS,
                                onClick = { viewModel.setActiveTab(NavigationTab.SETTINGS) },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Settings,
                                        contentDescription = "Settings",
                                        modifier = Modifier.size(22.dp)
                                    )
                                },
                                label = { Text("Settings", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PrimaryBlue,
                                    selectedTextColor = PrimaryBlue,
                                    indicatorColor = PrimaryBlue.copy(alpha = 0.12f)
                                ),
                                modifier = Modifier.testTag("nav_tab_settings")
                            )
                        }
                    },
                    floatingActionButton = {
                        if (uiState.activeTab == NavigationTab.LEDGER) {
                            FloatingActionButton(
                                onClick = { viewModel.openNewTransactionModal() },
                                containerColor = PrimaryBlue,
                                contentColor = Color.White,
                                modifier = Modifier.testTag("fab_add_transaction")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Transaction",
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(innerPadding)
                    ) {
                        when (uiState.activeTab) {
                            NavigationTab.LEDGER -> {
                                LedgerScreen(
                                    transactions = filteredTransactions,
                                    totalIncome = totalIncome,
                                    totalExpense = totalExpense,
                                    currency = currentCurrency,
                                    monthlyBudgetLimit = userPrefs.monthlyBudgetLimit,
                                    availableTags = allTags,
                                    selectedTag = uiState.selectedTag,
                                    timeFilter = uiState.timeFilter,
                                    typeFilter = uiState.typeFilter,
                                    searchQuery = uiState.searchQuery,
                                    onSelectTag = { viewModel.setSelectedTag(it) },
                                    onSelectTimeFilter = { viewModel.setTimeFilter(it) },
                                    onSelectTypeFilter = { viewModel.setTypeFilter(it) },
                                    onSearchQueryChange = { viewModel.setSearchQuery(it) },
                                    onEditBudgetClick = { viewModel.setEditBudgetModalOpen(true) },
                                    onTransactionClick = { viewModel.openEditTransactionModal(it) }
                                )
                            }
                            NavigationTab.TAGS -> {
                                TagAnalyticsScreen(
                                    transactions = allTransactions,
                                    tags = allTags,
                                    currency = currentCurrency,
                                    onCreateTag = { name, color -> viewModel.createTag(name, color) },
                                    onUpdateTag = { viewModel.updateTag(it) },
                                    onDeleteTag = { viewModel.deleteTag(it) },
                                    onFilterByTag = { tagName ->
                                        viewModel.setSelectedTag(tagName)
                                        viewModel.setActiveTab(NavigationTab.LEDGER)
                                    }
                                )
                            }
                            NavigationTab.ANALYTICS -> {
                                AnalyticsScreen(
                                    transactions = allTransactions,
                                    currency = currentCurrency,
                                    monthlyBudgetLimit = userPrefs.monthlyBudgetLimit,
                                    onEditBudgetClick = { viewModel.setEditBudgetModalOpen(true) }
                                )
                            }
                            NavigationTab.SETTINGS -> {
                                SettingsScreen(
                                    currentCurrency = currentCurrency,
                                    monthlyBudgetLimit = userPrefs.monthlyBudgetLimit,
                                    transactions = allTransactions,
                                    tags = allTags,
                                    onUpdateCurrency = { viewModel.updateCurrency(it) },
                                    onUpdateBudget = { viewModel.updateMonthlyBudget(it) },
                                    onResetData = { viewModel.resetData() },
                                    onExportCsv = { viewModel.exportCsv() },
                                    onExportJson = { viewModel.exportJson() },
                                    onImportJson = { viewModel.importJson(it) }
                                )
                            }
                        }
                    }
                }

                // Transaction Modal Bottom Sheet / Dialog
                TransactionModal(
                    isOpen = uiState.isTransactionModalOpen,
                    editingTransaction = uiState.editingTransaction,
                    currency = currentCurrency,
                    availableTags = allTags,
                    onClose = { viewModel.closeTransactionModal() },
                    onSave = { viewModel.saveTransaction(it) },
                    onDelete = { viewModel.deleteTransaction(it) },
                    onCreateTag = { name, color -> viewModel.createTag(name, color) }
                )

                // Edit Budget Dialog
                EditBudgetDialog(
                    isOpen = uiState.isEditBudgetModalOpen,
                    currentLimit = userPrefs.monthlyBudgetLimit,
                    currency = currentCurrency,
                    onDismiss = { viewModel.setEditBudgetModalOpen(false) },
                    onSave = { viewModel.updateMonthlyBudget(it) }
                )
            }
        }
    }
}
