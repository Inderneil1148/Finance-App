import React, { useState } from 'react';
import { usePWAInstall } from '../hooks/usePWAInstall';
import { Download, Sparkles, X } from 'lucide-react';
import { useHaptics } from '../hooks/useHaptics';

export const AndroidInstallBanner: React.FC = () => {
  const { isInstallable, isInstalled, install } = usePWAInstall();
  const { tap, success } = useHaptics();
  const [isDismissed, setIsDismissed] = useState(false);

  if (isInstalled || isDismissed) {
    return null;
  }

  const handleInstallClick = async () => {
    tap('medium');
    const installed = await install();
    if (installed) {
      success();
    }
  };

  return (
    <div className="w-full bg-[#E8F0FE] border-b border-[#D3E3FD] px-4 py-2.5 flex items-center justify-between gap-3 text-[#041E49] transition-all">
      <div className="flex items-center gap-2.5 min-w-0">
        <div className="w-8 h-8 rounded-xl bg-[#0B57D0] text-white flex items-center justify-center shrink-0 shadow-xs">
          <Download size={16} />
        </div>
        <div className="min-w-0">
          <p className="text-xs font-bold truncate flex items-center gap-1.5">
            <span>Install Aether Android App</span>
            <span className="text-[10px] font-semibold bg-[#D3E3FD] px-1.5 py-0.5 rounded-full text-[#0B57D0]">
              Fast & Offline
            </span>
          </p>
          <p className="text-[11px] text-[#444746] truncate">
            Add to your Android home screen with native gestures & instant haptics
          </p>
        </div>
      </div>

      <div className="flex items-center gap-2 shrink-0">
        <button
          type="button"
          onClick={handleInstallClick}
          className="px-3 py-1.5 rounded-full bg-[#0B57D0] hover:bg-[#1A73E8] text-white text-xs font-semibold shadow-xs flex items-center gap-1.5 cursor-pointer active:scale-95 transition-all"
        >
          <Sparkles size={13} />
          <span>{isInstallable ? 'Install APK' : 'Install'}</span>
        </button>
        <button
          type="button"
          onClick={() => {
            tap('light');
            setIsDismissed(true);
          }}
          className="p-1 rounded-full text-[#444746] hover:bg-black/5 cursor-pointer"
          aria-label="Dismiss banner"
        >
          <X size={15} />
        </button>
      </div>
    </div>
  );
};
