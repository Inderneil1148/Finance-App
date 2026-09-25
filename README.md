# Aether Finance — Android Edition

A minimal, precision personal finance tracker built for Android using Kotlin, Jetpack Compose, Material Design 3, and Room local database persistence.

## Features

- **Ledger & Cash Flow**:
  - Live income and expense tracking with real-time net balance and savings rate calculations.
  - Monthly budget limit pacing with Material 3 progress bar and alert indicators.
  - Transactions grouped chronologically by date ("Today", "Yesterday", and formatted dates) with daily subtotals.
  - Quick filtering by custom tags (#coffee, #dining-out, #commute, #subscription, etc.).
  - Search across transactions, payees, notes, categories, and tags.
  - Time filter chips (This Month, Last 30 Days, Last Month, All Time).
  - Type filter pills (All, Expenses, Income).

- **Custom Tag Architecture**:
  - Color-coded custom tag tagging engine.
  - Tag spending volume rankings with transaction counts and percentage breakdown.
  - Untagged transactions monitor and spend percentage tracking.
  - Inline tag creation with color palette selection (#007AFF, #34C759, #FF9500, #FF2D55, etc.).
  - Tag editing, renaming, and confirmation deletion.
  - Quick-filter ledger by tapping any tag.

- **Analytics & Budgeting**:
  - 4-metric overview cards: Total Income, Total Spending, Net Flow (Surplus/Deficit), and Savings Rate.
  - Monthly Budget Target progress card with dynamic pacing indicators.
  - Spend by Category ranking with visual category badge icons, counts, and progress bars.

- **Settings & Preferences**:
  - Multi-currency support (INR ₹, USD $, EUR €, GBP £, JPY ¥, CAD CA$, AUD A$, CHF CHF).
  - Customizable monthly budget ceiling.
  - CSV Spreadsheet Export for Google Sheets and Excel.
  - JSON backup snapshot export and restore.
  - Demo data reset option.

## Architecture & Tech Stack

- **UI Framework**: Jetpack Compose with Material Design 3 (M3)
- **Language**: Kotlin 2.1
- **Architecture**: MVVM (Model-View-ViewModel) + Repository Pattern
- **Local Persistence**: Android Room Database (`AppDatabase`, `FinanceDao`, `Converters`)
- **State Management**: Kotlin Coroutines & `StateFlow` observed with `collectAsStateWithLifecycle()`
- **Adaptive Launcher Icon**: Custom adaptive Material You icon with multi-density mipmaps and vector fallbacks.

## Project Structure

```
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/aetherfinance/
│       │   ├── MainActivity.kt
│       │   ├── data/
│       │   │   ├── local/
│       │   │   │   ├── AppDatabase.kt
│       │   │   │   ├── Converters.kt
│       │   │   │   └── FinanceDao.kt
│       │   │   ├── model/
│       │   │   │   └── FinanceModels.kt
│       │   │   ├── repository/
│       │   │   │   └── FinanceRepository.kt
│       │   │   └── util/
│       │   │       ├── Constants.kt
│       │   │       └── Formatters.kt
│       │   └── ui/
│       │       ├── components/
│       │       │   ├── AnalyticsScreen.kt
│       │       │   ├── CategoryIcon.kt
│       │       │   ├── EditBudgetDialog.kt
│       │       │   ├── LedgerScreen.kt
│       │       │   ├── OverviewCard.kt
│       │       │   ├── SettingsScreen.kt
│       │       │   ├── TagAnalyticsScreen.kt
│       │       │   ├── TagChip.kt
│       │       │   ├── TagFilterBar.kt
│       │       │   ├── TransactionItem.kt
│       │       │   └── TransactionModal.kt
│       │       ├── theme/
│       │       │   ├── Color.kt
│       │       │   ├── Theme.kt
│       │       │   └── Type.kt
│       │       └── viewmodel/
│       │           └── FinanceViewModel.kt
│       └── res/
│           ├── drawable/
│           ├── mipmap-*/
│           └── values/
│               ├── colors.xml
│               ├── strings.xml
│               └── themes.xml
├── gradle/
│   └── libs.versions.toml
├── build.gradle.kts
├── settings.gradle.kts
└── metadata.json
```
