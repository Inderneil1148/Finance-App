import React from 'react';
import { ActiveTab } from '../types/finance';
import { Wallet, Tags, BarChart3, Plus } from 'lucide-react';
import { useHaptics } from '../hooks/useHaptics';

interface BottomNavProps {
  activeTab: ActiveTab;
  onTabChange: (tab: ActiveTab) => void;
  onOpenAddModal: () => void;
  isMobileSimulator?: boolean;
}

export const BottomNav: React.FC<BottomNavProps> = ({
  activeTab,
  onTabChange,
  onOpenAddModal,
  isMobileSimulator = false,
}) => {
  const { tap } = useHaptics();

  const handleTabClick = (tab: ActiveTab) => {
    tap('selection');
    onTabChange(tab);
  };

  const handleAddClick = () => {
    tap('heavy');
    onOpenAddModal();
  };

  return (
    <>
      {/* Android Material Design 3 Floating Action Button (FAB) */}
      <div
        className={`${
          isMobileSimulator
            ? 'absolute bottom-20 right-4 z-30'
            : 'fixed bottom-22 right-4 sm:right-6 md:hidden z-30'
        }`}
      >
        <button
          type="button"
          onClick={handleAddClick}
          className="w-14 h-14 rounded-2xl bg-[#0B57D0] hover:bg-[#1A73E8] text-white flex items-center justify-center shadow-[0_4px_16px_rgba(11,87,208,0.4)] active:scale-95 transition-all cursor-pointer group"
          aria-label="Add Transaction"
          title="Add Transaction (Android FAB)"
        >
          <Plus size={26} strokeWidth={2.5} className="group-active:rotate-90 transition-transform" />
        </button>
      </div>

      {/* Android Material 3 Navigation Bar */}
      <nav
        aria-label="Android Bottom Navigation"
        className={`z-40 bg-[#F0F4F9]/95 backdrop-blur-md border-t border-[#E0E2EC] pb-[env(safe-area-inset-bottom,0px)] transition-all ${
          isMobileSimulator
            ? 'sticky bottom-0 w-full'
            : 'fixed bottom-0 left-0 right-0 md:hidden'
        }`}
      >
        <div className="max-w-md mx-auto px-4 h-18 flex items-center justify-around">
          {/* Tab 1: Wallet / Ledger */}
          <button
            type="button"
            onClick={() => handleTabClick('ledger')}
            className="flex flex-col items-center justify-center flex-1 py-1 cursor-pointer group select-none"
          >
            <div
              className={`w-16 h-8 rounded-full flex items-center justify-center transition-all duration-200 ${
                activeTab === 'ledger'
                  ? 'bg-[#D3E3FD] text-[#041E49] scale-100'
                  : 'text-[#444746] group-hover:bg-[#E0E2EC]/50'
              }`}
            >
              <Wallet size={20} strokeWidth={activeTab === 'ledger' ? 2.4 : 2} />
            </div>
            <span
              className={`text-[11px] tracking-tight mt-1 transition-colors ${
                activeTab === 'ledger'
                  ? 'font-bold text-[#041E49]'
                  : 'font-medium text-[#444746]'
              }`}
            >
              Wallet
            </span>
          </button>

          {/* Tab 2: Tags */}
          <button
            type="button"
            onClick={() => handleTabClick('tags')}
            className="flex flex-col items-center justify-center flex-1 py-1 cursor-pointer group select-none"
          >
            <div
              className={`w-16 h-8 rounded-full flex items-center justify-center transition-all duration-200 ${
                activeTab === 'tags'
                  ? 'bg-[#D3E3FD] text-[#041E49] scale-100'
                  : 'text-[#444746] group-hover:bg-[#E0E2EC]/50'
              }`}
            >
              <Tags size={20} strokeWidth={activeTab === 'tags' ? 2.4 : 2} />
            </div>
            <span
              className={`text-[11px] tracking-tight mt-1 transition-colors ${
                activeTab === 'tags'
                  ? 'font-bold text-[#041E49]'
                  : 'font-medium text-[#444746]'
              }`}
            >
              Tags
            </span>
          </button>

          {/* Tab 3: Insights / Analytics */}
          <button
            type="button"
            onClick={() => handleTabClick('analytics')}
            className="flex flex-col items-center justify-center flex-1 py-1 cursor-pointer group select-none"
          >
            <div
              className={`w-16 h-8 rounded-full flex items-center justify-center transition-all duration-200 ${
                activeTab === 'analytics'
                  ? 'bg-[#D3E3FD] text-[#041E49] scale-100'
                  : 'text-[#444746] group-hover:bg-[#E0E2EC]/50'
              }`}
            >
              <BarChart3 size={20} strokeWidth={activeTab === 'analytics' ? 2.4 : 2} />
            </div>
            <span
              className={`text-[11px] tracking-tight mt-1 transition-colors ${
                activeTab === 'analytics'
                  ? 'font-bold text-[#041E49]'
                  : 'font-medium text-[#444746]'
              }`}
            >
              Insights
            </span>
          </button>
        </div>
      </nav>
    </>
  );
};
