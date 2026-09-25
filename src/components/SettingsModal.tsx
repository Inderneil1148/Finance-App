import React, { useState } from 'react';
import { CurrencyConfig, BudgetConfig, Transaction, CustomTag } from '../types/finance';
import { SUPPORTED_CURRENCIES } from '../utils/formatters';
import { exportToCSV } from '../utils/storage';
import {
  Coins,
  Target,
  Download,
  Upload,
  RotateCcw,
  Check,
  X,
} from 'lucide-react';
import { useHaptics } from '../hooks/useHaptics';
import { NumiLogo } from './NumiLogo';

interface SettingsModalProps {
  isOpen: boolean;
  onClose: () => void;
  currency: CurrencyConfig;
  onUpdateCurrency: (c: CurrencyConfig) => void;
  budget: BudgetConfig;
  onUpdateBudget: (b: BudgetConfig) => void;
  transactions: Transaction[];
  tags: CustomTag[];
  onResetData: () => void;
  onImportData: (data: { transactions: Transaction[]; tags: CustomTag[] }) => void;
}

export const SettingsModal: React.FC<SettingsModalProps> = ({
  isOpen,
  onClose,
  currency,
  onUpdateCurrency,
  budget,
  onUpdateBudget,
  transactions,
  tags,
  onResetData,
  onImportData,
}) => {
  const { tap, success, warning } = useHaptics();
  const [budgetInput, setBudgetInput] = useState(budget.monthlyLimit.toString());
  const [savedBudgetNotice, setSavedBudgetNotice] = useState(false);
  const [isConfirmingReset, setIsConfirmingReset] = useState(false);
  const [importNotice, setImportNotice] = useState<string | null>(null);

  if (!isOpen) return null;

  const handleBudgetSave = (e: React.FormEvent) => {
    e.preventDefault();
    const val = parseFloat(budgetInput);
    if (!isNaN(val) && val >= 0) {
      success();
      onUpdateBudget({ ...budget, monthlyLimit: val });
      setSavedBudgetNotice(true);
      setTimeout(() => setSavedBudgetNotice(false), 2000);
    }
  };

  const handleExportJSON = () => {
    tap('light');
    const data = {
      version: 1,
      exportedAt: new Date().toISOString(),
      currency,
      budget,
      tags,
      transactions,
    };
    const blob = new Blob([JSON.stringify(data, null, 2)], {
      type: 'application/json',
    });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `numi-finance-backup-${new Date().toISOString().slice(0, 10)}.json`;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  const handleExportCSVFile = () => {
    tap('light');
    exportToCSV(transactions, currency.symbol);
  };

  const handleCurrencySelect = (curr: CurrencyConfig) => {
    tap('medium');
    onUpdateCurrency(curr);
  };

  const handleDoneClick = () => {
    tap('light');
    onClose();
  };

  const handleImportJSON = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (!file) return;

    const reader = new FileReader();
    reader.onload = (event) => {
      try {
        const parsed = JSON.parse(event.target?.result as string);
        if (Array.isArray(parsed.transactions)) {
          success();
          onImportData({
            transactions: parsed.transactions,
            tags: Array.isArray(parsed.tags) ? parsed.tags : tags,
          });
          setImportNotice('✓ Data restored successfully');
          setTimeout(() => {
            setImportNotice(null);
            onClose();
          }, 1200);
        } else {
          warning();
          setImportNotice('✕ Invalid backup file format');
          setTimeout(() => setImportNotice(null), 3000);
        }
      } catch {
        warning();
        setImportNotice('✕ Could not read JSON file');
        setTimeout(() => setImportNotice(null), 3000);
      }
    };
    reader.readAsText(file);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center p-0 sm:p-4 bg-black/50 backdrop-blur-xs transition-opacity animate-in fade-in duration-200">
      <div
        className="w-full max-w-md bg-[#F0F4F9] rounded-t-[28px] sm:rounded-3xl shadow-2xl flex flex-col max-h-[90vh] overflow-hidden text-[#1F1F1F]"
        role="dialog"
        aria-modal="true"
        aria-labelledby="settings-title"
      >
        {/* Android MD3 Drag Handle */}
        <div className="pt-3 pb-1 flex justify-center">
          <div className="w-8 h-1 bg-[#747775]/40 rounded-full" />
        </div>

        {/* Android Material Design 3 Top App Bar Header */}
        <div className="flex items-center justify-between px-5 py-2.5 bg-[#F0F4F9] border-b border-[#E0E2EC]">
          <button
            type="button"
            onClick={handleDoneClick}
            className="w-8 h-8 rounded-full flex items-center justify-center text-[#444746] hover:bg-[#E0E2EC]/50 cursor-pointer"
            aria-label="Close settings"
          >
            <X size={18} />
          </button>
          <h2 id="settings-title" className="text-base font-bold text-[#1F1F1F] tracking-tight">
            Settings & Preferences
          </h2>
          <button
            type="button"
            onClick={handleDoneClick}
            className="px-3.5 py-1 text-xs font-semibold text-white bg-[#0B57D0] hover:bg-[#1A73E8] rounded-full transition-all shadow-xs cursor-pointer active:scale-95"
          >
            Done
          </button>
        </div>

        {/* Material Design 3 Inset Settings Body */}
        <div className="flex-1 overflow-y-auto p-4 space-y-5">
          {/* Currency Section */}
          <div className="space-y-1.5">
            <span className="text-[12px] font-semibold text-[#444746] uppercase tracking-wider px-2 flex items-center gap-1.5">
              <Coins size={14} className="text-[#0B57D0]" />
              <span>Base Currency</span>
            </span>

            <div className="bg-white rounded-2xl shadow-xs border border-[#E0E2EC] overflow-hidden">
              <div className="p-2 grid grid-cols-2 sm:grid-cols-4 gap-1.5">
                {SUPPORTED_CURRENCIES.map((curr) => {
                  const isSelected = curr.code === currency.code;
                  return (
                    <button
                      key={curr.code}
                      type="button"
                      onClick={() => handleCurrencySelect(curr)}
                      className={`p-2.5 rounded-xl text-left transition-all cursor-pointer active:scale-95 ${
                        isSelected
                          ? 'bg-[#0B57D0] text-white shadow-xs'
                          : 'bg-[#F0F4F9] text-[#1F1F1F] hover:bg-[#E0E2EC]'
                      }`}
                    >
                      <div className="flex items-center justify-between">
                        <span className="font-bold text-sm">
                          {curr.symbol}
                        </span>
                        <span className="text-[10px] uppercase font-semibold opacity-85">
                          {curr.code}
                        </span>
                      </div>
                      <div className="text-[10px] truncate opacity-85 mt-0.5 font-medium">
                        {curr.name}
                      </div>
                    </button>
                  );
                })}
              </div>
            </div>
          </div>

          {/* Monthly Budget Cap */}
          <div className="space-y-1.5">
            <span className="text-[12px] font-semibold text-[#444746] uppercase tracking-wider px-2 flex items-center gap-1.5">
              <Target size={14} className="text-[#0B57D0]" />
              <span>Monthly Budget Cap</span>
            </span>

            <form
              onSubmit={handleBudgetSave}
              className="bg-white rounded-2xl p-4 shadow-xs border border-[#E0E2EC] space-y-3"
            >
              <div className="flex items-center gap-3">
                <span className="text-xl font-bold text-[#0B57D0] tabular-nums">
                  {currency.symbol}
                </span>
                <input
                  id="budget-input"
                  type="number"
                  min="0"
                  step="50"
                  value={budgetInput}
                  onChange={(e) => setBudgetInput(e.target.value)}
                  className="w-full text-xl font-bold tracking-tight text-[#1F1F1F] focus:outline-none tabular-nums bg-transparent"
                />
                <button
                  type="submit"
                  className="px-3.5 py-1.5 text-xs font-semibold bg-[#0B57D0] text-white rounded-full hover:bg-[#1A73E8] transition-colors flex items-center gap-1 shrink-0 shadow-xs cursor-pointer"
                >
                  {savedBudgetNotice ? <Check size={13} strokeWidth={2.5} /> : null}
                  <span>{savedBudgetNotice ? 'Saved' : 'Update'}</span>
                </button>
              </div>
            </form>
          </div>

          {/* Data Management Section */}
          <div className="space-y-1.5">
            <span className="text-[12px] font-semibold text-[#444746] uppercase tracking-wider px-2">
              Data & Export
            </span>

            <div className="bg-white rounded-2xl shadow-xs border border-[#E0E2EC] divide-y divide-[#E0E2EC] overflow-hidden">
              <button
                type="button"
                onClick={handleExportCSVFile}
                className="w-full px-4 py-3.5 flex items-center justify-between text-left hover:bg-[#F0F4F9] transition-colors cursor-pointer"
              >
                <div className="flex items-center gap-3">
                  <div className="w-8 h-8 rounded-xl bg-[#146C2E]/15 text-[#146C2E] flex items-center justify-center">
                    <Download size={16} />
                  </div>
                  <div>
                    <span className="text-sm font-medium text-[#1F1F1F] block">
                      Export CSV Spreadsheet
                    </span>
                    <span className="text-[11px] text-[#444746]">
                      Compatible with Google Sheets, Excel
                    </span>
                  </div>
                </div>
              </button>

              <button
                type="button"
                onClick={handleExportJSON}
                className="w-full px-4 py-3.5 flex items-center justify-between text-left hover:bg-[#F0F4F9] transition-colors cursor-pointer"
              >
                <div className="flex items-center gap-3">
                  <div className="w-8 h-8 rounded-xl bg-[#0B57D0]/15 text-[#0B57D0] flex items-center justify-center">
                    <Download size={16} />
                  </div>
                  <div>
                    <span className="text-sm font-medium text-[#1F1F1F] block">
                      Backup JSON Snapshot
                    </span>
                    <span className="text-[11px] text-[#444746]">
                      Preserves all transactions and custom tags
                    </span>
                  </div>
                </div>
              </button>

              <label className="w-full px-4 py-3.5 flex items-center justify-between text-left hover:bg-[#F0F4F9] transition-colors cursor-pointer">
                <div className="flex items-center gap-3">
                  <div className="w-8 h-8 rounded-xl bg-[#6750A4]/15 text-[#6750A4] flex items-center justify-center">
                    <Upload size={16} />
                  </div>
                  <div>
                    <span className="text-sm font-medium text-[#1F1F1F] block">
                      Restore Backup File
                    </span>
                    <span className="text-[11px] text-[#444746]">
                      {importNotice || 'Select an existing .json snapshot'}
                    </span>
                  </div>
                </div>
                <input
                  type="file"
                  accept=".json"
                  onChange={handleImportJSON}
                  className="hidden"
                />
              </label>

              <button
                type="button"
                onClick={() => {
                  warning();
                  if (isConfirmingReset) {
                    onResetData();
                    onClose();
                  } else {
                    setIsConfirmingReset(true);
                  }
                }}
                className={`w-full px-4 py-3.5 flex items-center justify-between text-left transition-colors cursor-pointer active:scale-95 ${
                  isConfirmingReset ? 'bg-[#B3261E]/10' : 'hover:bg-[#B3261E]/5'
                }`}
              >
                <div className="flex items-center gap-3 text-[#B3261E]">
                  <div className="w-8 h-8 rounded-xl bg-[#B3261E]/15 text-[#B3261E] flex items-center justify-center">
                    <RotateCcw size={16} />
                  </div>
                  <span className="text-sm font-medium">
                    {isConfirmingReset
                      ? 'Tap Again to Confirm Reset (₹ INR)'
                      : 'Reset to Demo Data (₹ INR)'}
                  </span>
                </div>
              </button>
            </div>
          </div>

          {/* About Numi Brand Card with Official Typography Logo */}
          <div className="p-4 bg-[#05070B] text-white rounded-2xl border border-white/10 shadow-sm flex flex-col sm:flex-row items-start sm:items-center justify-between gap-3">
            <div className="flex items-center gap-3">
              <NumiLogo variant="badge" size={46} className="shrink-0" />
              <div>
                <div className="flex items-center gap-1.5">
                  <span className="text-sm font-bold text-white tracking-tight">Numi</span>
                  <span className="text-[10px] font-semibold bg-[#8CE322]/20 text-[#8CE322] px-1.5 py-0.5 rounded-full border border-[#8CE322]/30">
                    Official
                  </span>
                </div>
                <p className="text-[11px] text-white/70 mt-0.5 leading-snug">
                  Clean, private expense tracking with instant haptic feedback.
                </p>
              </div>
            </div>
            <NumiLogo size={22} theme="dark" showGlow className="shrink-0 hidden sm:block" />
          </div>
        </div>
      </div>
    </div>
  );
};
