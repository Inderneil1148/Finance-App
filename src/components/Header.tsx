import React from 'react';
import { ActiveTab, TimeFilter, CurrencyConfig } from '../types/finance';
import { Settings, Smartphone, Monitor, Plus, Wallet, Tags, BarChart3, Check } from 'lucide-react';
import { useHaptics } from '../hooks/useHaptics';

interface HeaderProps {
  activeTab: ActiveTab;
  timeFilter: TimeFilter;
  onTimeFilterChange: (tf: TimeFilter) => void;
  onOpenSettings: () => void;
  isMobileSimulator: boolean;
  onToggleSimulator: () => void;
  currency?: CurrencyConfig;
  onTabChange?: (tab: ActiveTab) => void;
  onOpenAddModal?: () => void;
}

export const Header: React.FC<HeaderProps> = ({
  activeTab,
  timeFilter,
  onTimeFilterChange,
  onOpenSettings,
  isMobileSimulator,
  onToggleSimulator,
  currency,
  onTabChange,
  onOpenAddModal,
}) => {
  const { tap, selection } = useHaptics();
  const currentSymbol = currency?.symbol || '₹';
  const currentCode = currency?.code || 'INR';

  const handleTimeClick = (tf: TimeFilter) => {
    selection();
    onTimeFilterChange(tf);
  };

  const handleSettingsClick = () => {
    tap('light');
    onOpenSettings();
  };

  const handleSimulatorClick = () => {
    tap('medium');
    onToggleSimulator();
  };

  const handleAddClick = () => {
    tap('medium');
    if (onOpenAddModal) onOpenAddModal();
  };

  const handleTabClick = (tab: ActiveTab) => {
    selection();
    if (onTabChange) onTabChange(tab);
  };

  return (
    <header className="sticky top-0 z-30 w-full bg-[#F0F4F9]/95 backdrop-blur-md border-b border-[#E0E2EC] transition-colors">
      <div className="w-full max-w-7xl mx-auto px-3.5 sm:px-6 py-2.5 sm:py-3 flex flex-col md:flex-row items-center justify-between gap-3">
        {/* Top/Left Row: Android Brand, Currency Chip, and Action Buttons */}
        <div className="w-full md:w-auto flex items-center justify-between gap-3">
          <div className="flex items-center gap-2.5">
            {/* Android App Logo Icon */}
            <div className="w-8 h-8 rounded-xl bg-[#0B57D0] flex items-center justify-center text-white shadow-xs">
              <Wallet size={18} strokeWidth={2.4} />
            </div>

            <div className="flex items-baseline gap-1.5">
              <span className="text-lg sm:text-xl font-bold tracking-tight text-[#1F1F1F] select-none">
                Aether
              </span>
              <span className="text-[11px] font-semibold text-[#0B57D0] bg-[#D3E3FD] px-2 py-0.5 rounded-full">
                Android
              </span>
            </div>

            {/* Currency Chip - Material Design 3 Filter Chip */}
            <button
              type="button"
              onClick={handleSettingsClick}
              className="inline-flex items-center gap-1.5 px-3 py-1 text-xs font-semibold bg-white hover:bg-[#E8F0FE] text-[#1F1F1F] rounded-full transition-all active:scale-95 cursor-pointer shadow-xs border border-[#C4C7C5]"
              title="Currency Setting (Tap to change)"
            >
              <span className="font-bold text-[#0B57D0]">{currentSymbol}</span>
              <span className="text-[11px] text-[#444746]">{currentCode}</span>
            </button>
          </div>

          {/* Desktop Navigation Tabs (Material Design 3 style) */}
          {onTabChange && !isMobileSimulator && (
            <nav className="hidden md:flex items-center gap-1 p-1 bg-[#E0E2EC]/60 rounded-full text-xs font-medium">
              <button
                type="button"
                onClick={() => handleTabClick('ledger')}
                className={`flex items-center gap-1.5 px-4 py-1.5 rounded-full transition-all min-h-[32px] cursor-pointer ${
                  activeTab === 'ledger'
                    ? 'bg-[#0B57D0] text-white font-semibold shadow-xs'
                    : 'text-[#444746] hover:text-[#1F1F1F] hover:bg-white/40'
                }`}
              >
                <Wallet size={15} strokeWidth={2} />
                <span>Wallet</span>
              </button>
              <button
                type="button"
                onClick={() => handleTabClick('tags')}
                className={`flex items-center gap-1.5 px-4 py-1.5 rounded-full transition-all min-h-[32px] cursor-pointer ${
                  activeTab === 'tags'
                    ? 'bg-[#0B57D0] text-white font-semibold shadow-xs'
                    : 'text-[#444746] hover:text-[#1F1F1F] hover:bg-white/40'
                }`}
              >
                <Tags size={15} strokeWidth={2} />
                <span>Tags</span>
              </button>
              <button
                type="button"
                onClick={() => handleTabClick('analytics')}
                className={`flex items-center gap-1.5 px-4 py-1.5 rounded-full transition-all min-h-[32px] cursor-pointer ${
                  activeTab === 'analytics'
                    ? 'bg-[#0B57D0] text-white font-semibold shadow-xs'
                    : 'text-[#444746] hover:text-[#1F1F1F] hover:bg-white/40'
                }`}
              >
                <BarChart3 size={15} strokeWidth={2} />
                <span>Insights</span>
              </button>
            </nav>
          )}

          {/* Mobile Right Action Icons (Settings) */}
          <div className="flex md:hidden items-center gap-1.5">
            <button
              type="button"
              onClick={handleSettingsClick}
              className="w-9 h-9 rounded-full flex items-center justify-center text-[#444746] hover:text-[#1F1F1F] hover:bg-[#E0E2EC]/60 transition-colors cursor-pointer active:scale-95"
              aria-label="Settings"
            >
              <Settings size={20} strokeWidth={2} />
            </button>
          </div>
        </div>

        {/* Center / Right Row: Material Design 3 Segmented Filter Chips */}
        <div className="w-full md:w-auto flex items-center justify-between md:justify-end gap-2 sm:gap-3">
          <div className="flex-1 md:flex-initial flex items-center p-1 bg-[#E0E2EC]/70 rounded-full text-xs">
            <button
              type="button"
              onClick={() => handleTimeClick('this-month')}
              className={`flex-1 md:flex-initial px-3.5 py-1.5 rounded-full text-xs font-semibold transition-all min-h-[30px] cursor-pointer active:scale-95 flex items-center justify-center gap-1.5 ${
                timeFilter === 'this-month'
                  ? 'bg-white text-[#041E49] shadow-xs ring-1 ring-black/5 font-bold'
                  : 'text-[#444746] hover:text-[#1F1F1F]'
              }`}
            >
              {timeFilter === 'this-month' && <Check size={13} className="text-[#0B57D0]" />}
              <span>This Month</span>
            </button>
            <button
              type="button"
              onClick={() => handleTimeClick('last-30-days')}
              className={`flex-1 md:flex-initial px-3.5 py-1.5 rounded-full text-xs font-semibold transition-all min-h-[30px] cursor-pointer active:scale-95 flex items-center justify-center gap-1.5 ${
                timeFilter === 'last-30-days'
                  ? 'bg-white text-[#041E49] shadow-xs ring-1 ring-black/5 font-bold'
                  : 'text-[#444746] hover:text-[#1F1F1F]'
              }`}
            >
              {timeFilter === 'last-30-days' && <Check size={13} className="text-[#0B57D0]" />}
              <span>Last 30 Days</span>
            </button>
            <button
              type="button"
              onClick={() => handleTimeClick('all-time')}
              className={`flex-1 md:flex-initial px-3.5 py-1.5 rounded-full text-xs font-semibold transition-all min-h-[30px] cursor-pointer active:scale-95 flex items-center justify-center gap-1.5 ${
                timeFilter === 'all-time'
                  ? 'bg-white text-[#041E49] shadow-xs ring-1 ring-black/5 font-bold'
                  : 'text-[#444746] hover:text-[#1F1F1F]'
              }`}
            >
              {timeFilter === 'all-time' && <Check size={13} className="text-[#0B57D0]" />}
              <span>All Time</span>
            </button>
          </div>

          {/* Desktop Right Actions: New Transaction + Android Phone Frame Simulator + Settings */}
          <div className="hidden md:flex items-center gap-2">
            {onOpenAddModal && (
              <button
                type="button"
                onClick={handleAddClick}
                className="flex items-center gap-1.5 px-4 py-1.5 bg-[#0B57D0] hover:bg-[#1A73E8] text-white rounded-full text-xs font-semibold shadow-xs active:scale-95 transition-all cursor-pointer min-h-[34px]"
              >
                <Plus size={16} strokeWidth={2.5} />
                <span>New Transaction</span>
              </button>
            )}

            <button
              type="button"
              onClick={handleSimulatorClick}
              className={`flex items-center gap-1.5 px-3 py-1.5 text-xs font-medium rounded-full transition-all min-h-[34px] cursor-pointer active:scale-95 ${
                isMobileSimulator
                  ? 'bg-[#1F1F1F] text-white shadow-xs'
                  : 'text-[#444746] hover:text-[#1F1F1F] bg-[#E0E2EC]/70 hover:bg-[#E0E2EC]'
              }`}
              title={isMobileSimulator ? 'Switch to Full Desktop View' : 'Preview in Android Phone Frame (Pixel)'}
            >
              {isMobileSimulator ? <Monitor size={15} /> : <Smartphone size={15} />}
              <span className="hidden lg:inline">{isMobileSimulator ? 'Full View' : 'Pixel Phone Frame'}</span>
            </button>

            <button
              type="button"
              onClick={handleSettingsClick}
              className="w-9 h-9 rounded-full flex items-center justify-center text-[#444746] hover:text-[#1F1F1F] hover:bg-[#E0E2EC]/60 transition-colors cursor-pointer active:scale-95"
              aria-label="Settings"
            >
              <Settings size={19} strokeWidth={2} />
            </button>
          </div>
        </div>
      </div>
    </header>
  );
};
