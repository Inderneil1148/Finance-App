import { useCallback, useRef } from 'react';

export type HapticType = 'light' | 'medium' | 'heavy' | 'selection' | 'success' | 'warning' | 'error';

// Standard Apple-inspired vibration patterns (in milliseconds)
const VIBRATION_PATTERNS: Record<HapticType, number | number[]> = {
  selection: 8,
  light: 12,
  medium: 22,
  heavy: 35,
  success: [15, 60, 25],      // Crisp double-tap like Apple Pay
  warning: [25, 60, 30],      // Two firm pulses
  error: [35, 50, 35, 50, 45], // Urgent triple-pulse
};

/**
 * Custom hook to trigger tactile vibration feedback on mobile and touch devices.
 * Uses the Web Vibration API (`navigator.vibrate`) when available, and provides
 * an optional subtle acoustic Taptic micro-click via Web Audio API for devices
 * that restrict physical vibration (such as iOS WebKit).
 */
export function useHaptics() {
  const audioCtxRef = useRef<AudioContext | null>(null);

  // Lazy initialize Web Audio context on user gesture
  const getAudioContext = useCallback(() => {
    if (typeof window === 'undefined') return null;
    if (!audioCtxRef.current) {
      const AudioCtx = window.AudioContext || (window as unknown as { webkitAudioContext: typeof AudioContext }).webkitAudioContext;
      if (AudioCtx) {
        try {
          audioCtxRef.current = new AudioCtx();
        } catch {
          audioCtxRef.current = null;
        }
      }
    }
    if (audioCtxRef.current && audioCtxRef.current.state === 'suspended') {
      audioCtxRef.current.resume().catch(() => {});
    }
    return audioCtxRef.current;
  }, []);

  // Subtle acoustic micro-thump (Apple Taptic Engine click simulation)
  const playTactileAudioFeedback = useCallback((frequency: number, durationMs: number, gainValue = 0.04) => {
    try {
      const ctx = getAudioContext();
      if (!ctx) return;

      const osc = ctx.createOscillator();
      const gain = ctx.createGain();

      osc.type = 'sine';
      osc.frequency.setValueAtTime(frequency, ctx.currentTime);
      // Quick pitch drop for tactile thud sensation
      osc.frequency.exponentialRampToValueAtTime(40, ctx.currentTime + durationMs / 1000);

      gain.gain.setValueAtTime(gainValue, ctx.currentTime);
      gain.gain.exponentialRampToValueAtTime(0.0001, ctx.currentTime + durationMs / 1000);

      osc.connect(gain);
      gain.connect(ctx.destination);

      osc.start();
      osc.stop(ctx.currentTime + durationMs / 1000);
    } catch {
      // Audio feedback failed or blocked, ignore silently
    }
  }, [getAudioContext]);

  // Main vibration trigger
  const trigger = useCallback((type: HapticType = 'light') => {
    // 1. Hardware physical vibration via Web Vibration API
    if (typeof window !== 'undefined' && 'navigator' in window && typeof navigator.vibrate === 'function') {
      try {
        const pattern = VIBRATION_PATTERNS[type] || 15;
        navigator.vibrate(pattern);
      } catch {
        // Silently catch any platform vibration restrictions
      }
    }

    // 2. High-precision tactile acoustic micro-tick for Apple-like tactile confirmation
    if (type === 'selection') {
      playTactileAudioFeedback(180, 8, 0.025);
    } else if (type === 'light') {
      playTactileAudioFeedback(160, 12, 0.035);
    } else if (type === 'medium') {
      playTactileAudioFeedback(130, 18, 0.05);
    } else if (type === 'heavy') {
      playTactileAudioFeedback(100, 25, 0.07);
    } else if (type === 'success') {
      playTactileAudioFeedback(180, 14, 0.05);
      setTimeout(() => playTactileAudioFeedback(240, 20, 0.06), 75);
    } else if (type === 'warning' || type === 'error') {
      playTactileAudioFeedback(90, 25, 0.06);
      setTimeout(() => playTactileAudioFeedback(80, 30, 0.07), 80);
    }
  }, [playTactileAudioFeedback]);

  // Convenience helper methods
  const tap = useCallback((style: HapticType = 'light') => trigger(style), [trigger]);
  const selection = useCallback(() => trigger('selection'), [trigger]);
  const success = useCallback(() => trigger('success'), [trigger]);
  const warning = useCallback(() => trigger('warning'), [trigger]);
  const error = useCallback(() => trigger('error'), [trigger]);

  const isVibrationSupported = typeof window !== 'undefined' && 'navigator' in window && typeof navigator.vibrate === 'function';

  return {
    trigger,
    tap,
    selection,
    success,
    warning,
    error,
    isVibrationSupported,
  };
}
