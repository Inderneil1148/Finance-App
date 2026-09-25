import React from 'react';
import { usePWAInstall } from '../hooks/usePWAInstall';
import { useHaptics } from '../hooks/useHaptics';
import {
  Download,
  Share2,
  Smartphone,
  CheckCircle2,
  ExternalLink,
  X,
  Sparkles,
  ArrowRight,
  ShieldCheck,
  Zap,
} from 'lucide-react';

interface AndroidApkModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export const AndroidApkModal: React.FC<AndroidApkModalProps> = ({ isOpen, onClose }) => {
  const { isInstallable, isInstalled, install } = usePWAInstall();
  const { tap, success } = useHaptics();

  if (!isOpen) return null;

  const handleInstallClick = async () => {
    tap('medium');
    const result = await install();
    if (result) {
      success();
      onClose();
    }
  };

  const handleShareToAndroid = async () => {
    tap('light');
    if (navigator.share) {
      try {
        await navigator.share({
          title: 'Aether Finance — Android App',
          text: 'Track expenses and tags on your Android device with Aether Finance.',
          url: window.location.href,
        });
        success();
      } catch {
        // Share cancelled
      }
    } else {
      // Fallback copy to clipboard
      navigator.clipboard.writeText(window.location.href);
      success();
      alert('App link copied to clipboard! Open it in Chrome on your Android phone.');
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center p-0 sm:p-4 bg-black/60 backdrop-blur-xs transition-opacity animate-in fade-in duration-200">
      <div
        className="w-full max-w-lg bg-[#F0F4F9] rounded-t-[28px] sm:rounded-3xl shadow-2xl flex flex-col max-h-[92vh] overflow-hidden text-[#1F1F1F]"
        role="dialog"
        aria-modal="true"
        aria-labelledby="apk-modal-title"
      >
        {/* Android MD3 Drag Handle */}
        <div className="pt-3 pb-1 flex justify-center">
          <div className="w-8 h-1 bg-[#747775]/40 rounded-full" />
        </div>

        {/* Header */}
        <div className="flex items-center justify-between px-5 py-3 border-b border-[#E0E2EC] bg-[#F0F4F9]">
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 rounded-xl bg-[#0B57D0] text-white flex items-center justify-center shadow-xs">
              <Smartphone size={18} strokeWidth={2.4} />
            </div>
            <div>
              <h2 id="apk-modal-title" className="text-base font-bold text-[#1F1F1F] tracking-tight">
                Install on Android
              </h2>
              <p className="text-[11px] text-[#444746]">Native WebAPK & Home Screen App</p>
            </div>
          </div>
          <button
            type="button"
            onClick={() => {
              tap('light');
              onClose();
            }}
            className="w-8 h-8 rounded-full flex items-center justify-center text-[#444746] hover:bg-[#E0E2EC]/60 transition-colors cursor-pointer"
            aria-label="Close"
          >
            <X size={18} />
          </button>
        </div>

        {/* Content */}
        <div className="flex-1 overflow-y-auto p-4 space-y-4">
          {/* Main Hero Card */}
          <div className="bg-white rounded-2xl p-4 sm:p-5 shadow-xs border border-[#E0E2EC] space-y-3">
            <div className="flex items-start justify-between gap-3">
              <div>
                <span className="text-[11px] font-bold text-[#0B57D0] uppercase tracking-wider bg-[#D3E3FD] px-2 py-0.5 rounded-full inline-block mb-1">
                  Android Native Experience
                </span>
                <h3 className="text-lg font-bold text-[#1F1F1F]">
                  Run Aether as a Full Android App
                </h3>
                <p className="text-xs text-[#444746] mt-1 leading-relaxed">
                  Install Aether Finance directly on your Android phone with offline support,
                  Material You UI, adaptive app icon, and system haptics. No Google Play Store account required.
                </p>
              </div>
            </div>

            {/* Feature Pills */}
            <div className="grid grid-cols-2 gap-2 pt-1 text-xs">
              <div className="flex items-center gap-2 p-2 bg-[#F0F4F9] rounded-xl text-[#041E49]">
                <Zap size={15} className="text-[#0B57D0] shrink-0" />
                <span className="font-semibold text-[11px]">Instant Offline Access</span>
              </div>
              <div className="flex items-center gap-2 p-2 bg-[#F0F4F9] rounded-xl text-[#041E49]">
                <ShieldCheck size={15} className="text-[#146C2E] shrink-0" />
                <span className="font-semibold text-[11px]">100% Private & Local</span>
              </div>
            </div>

            {/* Primary Action Button */}
            <div className="pt-2">
              {isInstalled ? (
                <div className="w-full py-3 bg-[#146C2E]/10 border border-[#146C2E]/30 rounded-xl text-[#146C2E] flex items-center justify-center gap-2 text-sm font-semibold">
                  <CheckCircle2 size={18} />
                  <span>Aether is installed on this device</span>
                </div>
              ) : (
                <button
                  type="button"
                  onClick={handleInstallClick}
                  className="w-full py-3 bg-[#0B57D0] hover:bg-[#1A73E8] text-white rounded-xl text-sm font-semibold shadow-xs flex items-center justify-center gap-2 cursor-pointer transition-all active:scale-98"
                >
                  <Sparkles size={16} />
                  <span>{isInstallable ? 'Install to Android Device' : 'Install Android WebAPK'}</span>
                </button>
              )}
            </div>
          </div>

          {/* Step-by-Step Android Installation Guide */}
          <div className="bg-white rounded-2xl p-4 shadow-xs border border-[#E0E2EC] space-y-3">
            <h4 className="text-xs font-bold text-[#444746] uppercase tracking-wider">
              Installation Steps on Android (Chrome / Samsung Internet)
            </h4>

            <div className="space-y-2.5 text-xs text-[#1F1F1F]">
              <div className="flex items-start gap-2.5 p-2 bg-[#F0F4F9] rounded-xl">
                <span className="w-5 h-5 rounded-full bg-[#0B57D0] text-white font-bold text-[11px] flex items-center justify-center shrink-0 mt-0.5">
                  1
                </span>
                <div>
                  <p className="font-semibold">Open in Chrome or Samsung Internet</p>
                  <p className="text-[11px] text-[#444746]">
                    Navigate to this URL on your Android smartphone browser.
                  </p>
                </div>
              </div>

              <div className="flex items-start gap-2.5 p-2 bg-[#F0F4F9] rounded-xl">
                <span className="w-5 h-5 rounded-full bg-[#0B57D0] text-white font-bold text-[11px] flex items-center justify-center shrink-0 mt-0.5">
                  2
                </span>
                <div>
                  <p className="font-semibold">Tap the Browser Menu (⋮)</p>
                  <p className="text-[11px] text-[#444746]">
                    Tap the three vertical dots located in the top-right or bottom-right corner.
                  </p>
                </div>
              </div>

              <div className="flex items-start gap-2.5 p-2 bg-[#F0F4F9] rounded-xl">
                <span className="w-5 h-5 rounded-full bg-[#0B57D0] text-white font-bold text-[11px] flex items-center justify-center shrink-0 mt-0.5">
                  3
                </span>
                <div>
                  <p className="font-semibold">Tap &quot;Install app&quot; or &quot;Add to Home screen&quot;</p>
                  <p className="text-[11px] text-[#444746]">
                    Android will generate a high-res launcher icon and add it to your App Drawer.
                  </p>
                </div>
              </div>
            </div>
          </div>

          {/* Quick Share to Android Phone */}
          <div className="bg-white rounded-2xl p-4 shadow-xs border border-[#E0E2EC] flex items-center justify-between gap-3">
            <div>
              <p className="text-xs font-bold text-[#1F1F1F]">Share to your Android Device</p>
              <p className="text-[11px] text-[#444746]">
                Send link via WhatsApp, Quick Share, or Email to open on your phone.
              </p>
            </div>
            <button
              type="button"
              onClick={handleShareToAndroid}
              className="px-3 py-2 bg-[#F0F4F9] hover:bg-[#E0E2EC] text-[#0B57D0] rounded-xl text-xs font-semibold flex items-center gap-1.5 shrink-0 cursor-pointer active:scale-95 transition-all"
            >
              <Share2 size={14} />
              <span>Share Link</span>
            </button>
          </div>
        </div>

        {/* Footer */}
        <div className="p-4 border-t border-[#E0E2EC] bg-[#F0F4F9] flex justify-end">
          <button
            type="button"
            onClick={() => {
              tap('light');
              onClose();
            }}
            className="px-5 py-2 text-xs font-semibold text-white bg-[#0B57D0] hover:bg-[#1A73E8] rounded-full transition-all cursor-pointer shadow-xs active:scale-95"
          >
            Got It
          </button>
        </div>
      </div>
    </div>
  );
};
