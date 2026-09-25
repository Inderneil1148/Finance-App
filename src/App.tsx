import React, { useState, useMemo, useEffect } from 'react';
import {
  Transaction,
  CustomTag,
  CurrencyConfig,
  BudgetConfig,
  ActiveTab,
  TimeFilter,
} from './types/finance';
import {
  loadStoredTransactions,
  saveTransactions,
  loadStoredTags,
  saveStoredTags,
  loadStoredCurrency,
  saveStoredCurrency,
  loadStoredBudget,
  saveStoredBudget,
  generateSeedTransactions,
} from './utils/storage';
import { DEFAULT_CATEGORIES, DEFAULT_TAGS, TAG_COLOR_PALETTE } from './utils/constants';
import { useHaptics } from './hooks/useHaptics';
import { Header } from './components/Header';
import { BottomNav } from './components/BottomNav';
import { OverviewCard } from './components/OverviewCard';
import { TagFilterBar } from './components/TagFilterBar';
import { TransactionList } from './components/TransactionList';
import { TagAnalytics } from './components/TagAnalytics';
import { AnalyticsView } from './components/AnalyticsView';
import { TransactionModal } from './components/TransactionModal';
import { SettingsModal } from './components/SettingsModal';
import { AndroidInstallBanner } from './components/AndroidInstallBanner';
import { AndroidSnackbar, SnackbarMessage } from './components/AndroidSnackbar';
import { AndroidApkModal } from './components/AndroidApkModal';
import { AndroidQuickSettings } from './components/AndroidQuickSettings';
import { AndroidRecentsModal } from './components/AndroidRecentsModal';
import { Wifi, Signal, BatteryMedium, Triangle, Circle, Square } from 'lucide-react';

export default function App() {
  const { tap, success, warning } = useHaptics();

  // Core Data States
  const [transactions, setTransactions] = useState<Transaction[]>(loadStoredTransactions);
  const [tags, setTags] = useState<CustomTag[]>(loadStoredTags);
  const [currency, setCurrency] = useState<CurrencyConfig>(loadStoredCurrency);
  const [budget, setBudget] = useState<BudgetConfig>(loadStoredBudget);

  // View States
  const [activeTab, setActiveTab] = useState<ActiveTab>('ledger');
  const [timeFilter, setTimeFilter] = useState<TimeFilter>('this-month');
  const [selectedTag, setSelectedTag] = useState<string | null>(null);
  const [searchQuery, setSearchQuery] = useState<string>('');
  const [typeFilter, setTypeFilter] = useState<'all' | 'expense' | 'income'>('all');

  // Modals
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingTx, setEditingTx] = useState<Transaction | null>(null);
  const [isSettingsOpen, setIsSettingsOpen] = useState(false);
  const [isApkModalOpen, setIsApkModalOpen] = useState(false);
  const [isQuickSettingsOpen, setIsQuickSettingsOpen] = useState(false);
  const [isRecentsOpen, setIsRecentsOpen] = useState(false);

  // Android Mobile Phone Simulator Mode (Default true to showcase authentic Android experience)
  const [isMobileSimulator, setIsMobileSimulator] = useState(true);
  const [useThreeButtonNav, setUseThreeButtonNav] = useState(true);

  // Real-time Android System Clock
  const [currentTime, setCurrentTime] = useState(() => {
    const d = new Date();
    return d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', hour12: false });
  });

  useEffect(() => {
    const timer = setInterval(() => {
      const d = new Date();
      setCurrentTime(d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', hour12: false }));
    }, 10000);
    return () => clearInterval(timer);
  }, []);

  // Android Snackbar Feedback
  const [snackbar, setSnackbar] = useState<SnackbarMessage | null>(null);

  // Android Back Navigation (Hardware / Browser Popstate)
  useEffect(() => {
    const handlePopState = () => {
      if (isQuickSettingsOpen) {
        setIsQuickSettingsOpen(false);
      } else if (isRecentsOpen) {
        setIsRecentsOpen(false);
      } else if (isApkModalOpen) {
        setIsApkModalOpen(false);
      } else if (isModalOpen) {
        setIsModalOpen(false);
        setEditingTx(null);
      } else if (isSettingsOpen) {
        setIsSettingsOpen(false);
      } else if (activeTab !== 'ledger') {
        setActiveTab('ledger');
      }
    };
    window.addEventListener('popstate', handlePopState);
    return () => window.removeEventListener('popstate', handlePopState);
  }, [isQuickSettingsOpen, isRecentsOpen, isApkModalOpen, isModalOpen, isSettingsOpen, activeTab]);

  const openTransactionModal = (tx: Transaction | null = null) => {
    tap('medium');
    setEditingTx(tx);
    setIsModalOpen(true);
    if (typeof window !== 'undefined') {
      window.history.pushState({ modal: 'transaction' }, '');
    }
  };

  const closeTransactionModal = () => {
    setIsModalOpen(false);
    setEditingTx(null);
  };

  const openSettingsModal = () => {
    tap('light');
    setIsSettingsOpen(true);
    if (typeof window !== 'undefined') {
      window.history.pushState({ modal: 'settings' }, '');
    }
  };

  const closeSettingsModal = () => {
    setIsSettingsOpen(false);
  };

  const handleAndroidBackButton = () => {
    tap('light');
    if (isQuickSettingsOpen) {
      setIsQuickSettingsOpen(false);
    } else if (isRecentsOpen) {
      setIsRecentsOpen(false);
    } else if (isApkModalOpen) {
      setIsApkModalOpen(false);
    } else if (isModalOpen) {
      closeTransactionModal();
    } else if (isSettingsOpen) {
      closeSettingsModal();
    } else if (activeTab !== 'ledger') {
      setActiveTab('ledger');
    } else {
      setSnackbar({
        id: String(Date.now()),
        text: 'Aether is running in background • Press Home to minimize',
        type: 'info',
      });
    }
  };

  const handleAndroidHomeButton = () => {
    tap('light');
    setIsQuickSettingsOpen(false);
    setIsRecentsOpen(false);
    setIsApkModalOpen(false);
    if (isModalOpen) closeTransactionModal();
    if (isSettingsOpen) closeSettingsModal();
    setActiveTab('ledger');
  };

  const handleAndroidRecentsButton = () => {
    tap('medium');
    setIsRecentsOpen(true);
  };

  const handleTabChange = (tab: ActiveTab) => {
    tap('light');
    setActiveTab(tab);
  };

  const handleTimeFilterChange = (tf: TimeFilter) => {
    tap('light');
    setTimeFilter(tf);
  };

  // Filter transactions by timeFilter
  const timeFilteredTransactions = useMemo(() => {
    const now = new Date();
    const currentYear = now.getFullYear();
    const currentMonth = String(now.getMonth() + 1).padStart(2, '0');
    const thisMonthPrefix = `${currentYear}-${currentMonth}`;

    const d30 = new Date(now.getFullYear(), now.getMonth(), now.getDate());
    d30.setDate(d30.getDate() - 30);
    const y30 = d30.getFullYear();
    const m30 = String(d30.getMonth() + 1).padStart(2, '0');
    const day30 = String(d30.getDate()).padStart(2, '0');
    const thirtyDaysAgoStr = `${y30}-${m30}-${day30}`;

    return transactions.filter((tx) => {
      if (timeFilter === 'this-month') {
        return tx.date.startsWith(thisMonthPrefix);
      }
      if (timeFilter === 'last-30-days') {
        return tx.date >= thirtyDaysAgoStr;
      }
      return true; // all-time
    });
  }, [transactions, timeFilter]);

  // Aggregate period figures
  const { periodIncome, periodExpense } = useMemo(() => {
    let inc = 0;
    let exp = 0;
    timeFilteredTransactions.forEach((tx) => {
      if (tx.type === 'income') {
        inc += tx.amount;
      } else {
        exp += tx.amount;
      }
    });
    return { periodIncome: inc, periodExpense: exp };
  }, [timeFilteredTransactions]);

  // Count transactions per tag for the filter bar
  const tagCounts = useMemo(() => {
    const counts: Record<string, number> = {};
    timeFilteredTransactions.forEach((tx) => {
      if (tx.customTags) {
        tx.customTags.forEach((t) => {
          counts[t] = (counts[t] || 0) + 1;
        });
      }
    });
    return counts;
  }, [timeFilteredTransactions]);

  // Data Actions
  const handleSaveTransaction = (
    txData: Omit<Transaction, 'id' | 'createdAt'> & { id?: string }
  ) => {
    success();
    if (txData.id) {
      // Update
      const updated = transactions.map((t) =>
        t.id === txData.id
          ? {
              ...t,
              ...txData,
              id: txData.id,
              createdAt: t.createdAt,
            }
          : t
      );
      setTransactions(updated);
      saveTransactions(updated);
      setSnackbar({
        id: String(Date.now()),
        text: 'Transaction updated successfully',
        type: 'success',
      });
    } else {
      // Create new
      const newTx: Transaction = {
        id: `tx-${Date.now()}-${Math.random().toString(36).substring(2, 6)}`,
        type: txData.type,
        amount: txData.amount,
        title: txData.title,
        category: txData.category,
        customTags: txData.customTags || [],
        date: txData.date,
        time: new Date().toLocaleTimeString([], {
          hour: '2-digit',
          minute: '2-digit',
          hour12: false,
        }),
        note: txData.note,
        createdAt: Date.now(),
      };
      const updated = [newTx, ...transactions];
      setTransactions(updated);
      saveTransactions(updated);
      setSnackbar({
        id: String(Date.now()),
        text: 'Transaction added to ledger',
        type: 'success',
      });
    }
  };

  const handleDeleteTransaction = (id: string) => {
    warning();
    const updated = transactions.filter((t) => t.id !== id);
    setTransactions(updated);
    saveTransactions(updated);
    setSnackbar({
      id: String(Date.now()),
      text: 'Transaction removed',
      type: 'warning',
    });
  };

  const handleCreateTag = (name: string, color?: string): CustomTag => {
    const clean = name.trim().toLowerCase().replace(/[^a-z0-9_-]/g, '-');
    const existing = tags.find((t) => t.name === clean);
    if (existing) return existing;

    const newTag: CustomTag = {
      id: `tag-${Date.now()}`,
      name: clean,
      color: color || TAG_COLOR_PALETTE[Math.floor(Math.random() * TAG_COLOR_PALETTE.length)].hex,
      createdAt: Date.now(),
    };
    const updated = [...tags, newTag];
    setTags(updated);
    saveStoredTags(updated);
    setSnackbar({
      id: String(Date.now()),
      text: `Created tag #${clean}`,
      type: 'success',
    });
    return newTag;
  };

  const handleUpdateTag = (id: string, newName: string, newColor: string) => {
    const oldTag = tags.find((t) => t.id === id);
    if (!oldTag) return;

    const updatedTags = tags.map((t) =>
      t.id === id ? { ...t, name: newName, color: newColor } : t
    );
    setTags(updatedTags);
    saveStoredTags(updatedTags);

    if (oldTag.name !== newName) {
      const updatedTx = transactions.map((tx) => {
        if (tx.customTags && tx.customTags.includes(oldTag.name)) {
          return {
            ...tx,
            customTags: tx.customTags.map((tg) => (tg === oldTag.name ? newName : tg)),
          };
        }
        return tx;
      });
      setTransactions(updatedTx);
      saveTransactions(updatedTx);

      if (selectedTag === oldTag.name) {
        setSelectedTag(newName);
      }
    }
  };

  const handleDeleteTag = (id: string, tagName: string) => {
    const updatedTags = tags.filter((t) => t.id !== id);
    setTags(updatedTags);
    saveStoredTags(updatedTags);

    if (selectedTag === tagName) {
      setSelectedTag(null);
    }
    setSnackbar({
      id: String(Date.now()),
      text: `Deleted tag #${tagName}`,
      type: 'info',
    });
  };

  const handleResetData = () => {
    warning();
    const seeded = generateSeedTransactions();
    setTransactions(seeded);
    saveTransactions(seeded);
    setTags(DEFAULT_TAGS);
    saveStoredTags(DEFAULT_TAGS);
    setSnackbar({
      id: String(Date.now()),
      text: 'Reset to demo transactions & tags',
      type: 'info',
    });
  };

  const handleImportData = (data: { transactions: Transaction[]; tags: CustomTag[] }) => {
    success();
    setTransactions(data.transactions);
    saveTransactions(data.transactions);
    if (data.tags && data.tags.length > 0) {
      setTags(data.tags);
      saveStoredTags(data.tags);
    }
    setSnackbar({
      id: String(Date.now()),
      text: 'Backup data restored successfully',
      type: 'success',
    });
  };

  const handleFilterByTag = (tagName: string) => {
    tap('light');
    setSelectedTag(tagName);
    setActiveTab('ledger');
  };

  return (
    <div className="min-h-screen bg-[#F0F4F9] text-[#1F1F1F] flex flex-col items-center">
      {/* Container wrapper: Android responsive layout or simulated Android phone chassis */}
      <div
        className={`w-full flex-1 flex flex-col transition-all duration-300 ${
          isMobileSimulator
            ? 'max-w-[420px] max-sm:max-w-none max-sm:my-0 max-sm:border-0 max-sm:rounded-none max-sm:shadow-none sm:my-5 sm:rounded-[44px] sm:border-[10px] sm:border-[#202124] shadow-[0_25px_60px_rgba(0,0,0,0.35)] overflow-hidden bg-[#F0F4F9] min-h-[92vh] max-sm:min-h-screen relative'
            : 'max-w-7xl mx-auto'
        }`}
      >
        {/* Simulated Android Status Bar (Visible in Android Phone Mode) */}
        {isMobileSimulator && (
          <div
            onClick={() => {
              tap('light');
              setIsQuickSettingsOpen(true);
            }}
            className="w-full bg-[#F0F4F9] pt-2 px-5 pb-1 flex items-center justify-between text-[11px] font-medium text-[#1F1F1F] select-none shrink-0 z-40 border-b border-[#E0E2EC]/50 cursor-pointer hover:bg-[#E8F0FE]/60 transition-colors"
            title="Tap to pull down Android Quick Settings & Notifications"
          >
            {/* Clock & Notification indicator */}
            <div className="flex items-center gap-1.5 font-bold">
              <span>{currentTime}</span>
              <span className="w-1.5 h-1.5 rounded-full bg-[#0B57D0]" title="Aether notification active" />
            </div>

            {/* Central Android Camera Punch-Hole */}
            <div className="w-3.5 h-3.5 rounded-full bg-[#121212] border border-[#303030] flex items-center justify-center">
              <div className="w-1 h-1 rounded-full bg-[#082040]" />
            </div>

            {/* Android Status Icons */}
            <div className="flex items-center gap-1.5 text-[10px]">
              <span className="text-[9px] font-bold text-[#444746] mr-0.5">5G</span>
              <Wifi size={12} strokeWidth={2.4} />
              <Signal size={12} strokeWidth={2.4} />
              <div className="flex items-center gap-0.5 font-semibold text-[10px]">
                <span>88%</span>
                <BatteryMedium size={13} strokeWidth={2.2} />
              </div>
            </div>
          </div>
        )}

        {/* In-App Android PWA Install Banner */}
        <AndroidInstallBanner onOpenApkModal={() => setIsApkModalOpen(true)} />

        {/* Android Material Design 3 Top App Bar */}
        <Header
          activeTab={activeTab}
          timeFilter={timeFilter}
          onTimeFilterChange={handleTimeFilterChange}
          onOpenSettings={openSettingsModal}
          isMobileSimulator={isMobileSimulator}
          onToggleSimulator={() => {
            tap('medium');
            setIsMobileSimulator(!isMobileSimulator);
          }}
          currency={currency}
          onTabChange={handleTabChange}
          onOpenAddModal={() => openTransactionModal(null)}
          onOpenApkModal={() => setIsApkModalOpen(true)}
        />

        {/* Main Tab Content */}
        <main
          className={`flex-1 p-3.5 sm:p-5 lg:p-6 space-y-4 sm:space-y-6 ${
            isMobileSimulator ? 'pb-24' : 'pb-24 md:pb-12'
          }`}
        >
          {activeTab === 'ledger' && (
            <div className="grid grid-cols-1 lg:grid-cols-12 gap-4 sm:gap-6 items-start">
              {/* Left Column: Overview & Tag Filter */}
              <div className="lg:col-span-5 xl:col-span-4 space-y-4 lg:sticky lg:top-20">
                <OverviewCard
                  income={periodIncome}
                  expense={periodExpense}
                  currency={currency}
                  monthlyBudget={budget.monthlyLimit}
                  onOpenAnalytics={() => setActiveTab('analytics')}
                  timeFilter={timeFilter}
                />

                <TagFilterBar
                  tags={tags}
                  selectedTag={selectedTag}
                  onSelectTag={setSelectedTag}
                  onOpenTagManager={() => setActiveTab('tags')}
                  tagCounts={tagCounts}
                />
              </div>

              {/* Right Column: Transaction Feed */}
              <div className="lg:col-span-7 xl:col-span-8 min-w-0">
                <TransactionList
                  transactions={timeFilteredTransactions}
                  categories={DEFAULT_CATEGORIES}
                  tags={tags}
                  currency={currency}
                  searchQuery={searchQuery}
                  onSearchChange={setSearchQuery}
                  selectedTag={selectedTag}
                  onSelectTag={setSelectedTag}
                  typeFilter={typeFilter}
                  onTypeFilterChange={setTypeFilter}
                  onEditTransaction={(tx) => openTransactionModal(tx)}
                  onAddNew={() => openTransactionModal(null)}
                  timeFilter={timeFilter}
                />
              </div>
            </div>
          )}

          {activeTab === 'tags' && (
            <TagAnalytics
              transactions={timeFilteredTransactions}
              tags={tags}
              currency={currency}
              onCreateTag={handleCreateTag}
              onUpdateTag={handleUpdateTag}
              onDeleteTag={handleDeleteTag}
              onFilterByTag={handleFilterByTag}
            />
          )}

          {activeTab === 'analytics' && (
            <AnalyticsView
              transactions={timeFilteredTransactions}
              categories={DEFAULT_CATEGORIES}
              currency={currency}
              budget={budget}
              onEditBudget={openSettingsModal}
            />
          )}
        </main>

        {/* Android Material 3 Bottom Navigation Bar with FAB */}
        <BottomNav
          activeTab={activeTab}
          onTabChange={handleTabChange}
          onOpenAddModal={() => openTransactionModal(null)}
          isMobileSimulator={isMobileSimulator}
        />

        {/* Android System Navigation Bar (Simulated Phone frame bottom) */}
        {isMobileSimulator && (
          <div className="w-full bg-[#F0F4F9] border-t border-[#E0E2EC]/50 shrink-0 select-none z-40 pb-1">
            {useThreeButtonNav ? (
              /* Android 3-Button Navigation Bar */
              <div className="h-10 flex items-center justify-around px-8 text-[#444746]">
                {/* Back triangle */}
                <button
                  type="button"
                  onClick={handleAndroidBackButton}
                  className="p-2 hover:bg-black/5 active:bg-black/10 rounded-full transition-colors cursor-pointer"
                  title="Android Back Button"
                >
                  <Triangle size={15} className="-rotate-90 fill-current" />
                </button>
                {/* Home circle */}
                <button
                  type="button"
                  onClick={handleAndroidHomeButton}
                  className="p-2 hover:bg-black/5 active:bg-black/10 rounded-full transition-colors cursor-pointer"
                  title="Android Home Button"
                >
                  <Circle size={15} strokeWidth={2.5} />
                </button>
                {/* Recents square */}
                <button
                  type="button"
                  onClick={handleAndroidRecentsButton}
                  className="p-2 hover:bg-black/5 active:bg-black/10 rounded-full transition-colors cursor-pointer"
                  title="Android Recents Button"
                >
                  <Square size={15} strokeWidth={2.5} />
                </button>
              </div>
            ) : (
              /* Android Gesture Navigation Bar */
              <div
                className="py-2.5 flex items-center justify-center cursor-pointer group"
                onClick={() => setUseThreeButtonNav(!useThreeButtonNav)}
                title="Tap to toggle between Gesture Navigation and 3-Button Navigation"
              >
                <div className="w-28 h-1 bg-[#1F1F1F]/40 group-hover:bg-[#1F1F1F]/70 rounded-full transition-colors" />
              </div>
            )}
          </div>
        )}

        {/* Transaction Add/Edit Sheet Modal */}
        <TransactionModal
          isOpen={isModalOpen}
          onClose={closeTransactionModal}
          onSave={handleSaveTransaction}
          onDelete={handleDeleteTransaction}
          editingTransaction={editingTx}
          categories={DEFAULT_CATEGORIES}
          availableTags={tags}
          onCreateTag={handleCreateTag}
          currency={currency}
        />

        {/* Settings & Preferences Sheet Modal */}
        <SettingsModal
          isOpen={isSettingsOpen}
          onClose={closeSettingsModal}
          currency={currency}
          onUpdateCurrency={(c) => {
            setCurrency(c);
            saveStoredCurrency(c);
          }}
          budget={budget}
          onUpdateBudget={(b) => {
            setBudget(b);
            saveStoredBudget(b);
          }}
          transactions={transactions}
          tags={tags}
          onResetData={handleResetData}
          onImportData={handleImportData}
        />

        {/* Android Quick Settings & Notification Shade */}
        <AndroidQuickSettings
          isOpen={isQuickSettingsOpen}
          onClose={() => setIsQuickSettingsOpen(false)}
          onOpenAddModal={() => openTransactionModal(null)}
          onOpenWallet={() => setActiveTab('ledger')}
          currencySymbol={currency.symbol}
          monthlyBudget={budget.monthlyLimit}
          totalExpense={periodExpense}
        />

        {/* Android Recent Apps Multitasking Switcher */}
        <AndroidRecentsModal
          isOpen={isRecentsOpen}
          onClose={() => setIsRecentsOpen(false)}
          onSelectApp={() => {
            setActiveTab('ledger');
            setIsRecentsOpen(false);
          }}
        />

        {/* Android APK & Install Hub Modal */}
        <AndroidApkModal
          isOpen={isApkModalOpen}
          onClose={() => setIsApkModalOpen(false)}
        />

        {/* Material Design 3 Snackbar Toast */}
        <AndroidSnackbar
          message={snackbar}
          onDismiss={() => setSnackbar(null)}
        />
      </div>
    </div>
  );
}
