import React, { useId } from 'react';

export interface NumiLogoProps {
  /**
   * 'default': Just the typography (lime line + dot + "numi" wordmark) with transparent background
   * 'badge': Typography inside an obsidian black squircle card (ideal for app icons, avatars, install dialogs)
   */
  variant?: 'default' | 'badge';
  /**
   * Overall height in pixels (default: 36)
   */
  size?: number;
  /**
   * Color theme for wordmark: 'light' (dark text for white/light UI) | 'dark' (white text for dark UI) | 'auto'
   */
  theme?: 'auto' | 'light' | 'dark';
  /**
   * If true, wraps the logo in an obsidian black card (alias for variant="badge")
   */
  badge?: boolean;
  className?: string;
  showGlow?: boolean;
  onClick?: () => void;
}

export const NumiLogo: React.FC<NumiLogoProps> = ({
  variant = 'default',
  size = 36,
  theme = 'auto',
  badge = false,
  className = '',
  showGlow = false,
  onClick,
}) => {
  const rawId = useId();
  const uid = rawId.replace(/[^a-zA-Z0-9_-]/g, '_');
  const limeGlowId = `limeGlow_${uid}`;

  const isBadge = badge || variant === 'badge';
  const isDarkText = !isBadge && theme === 'light';

  // Typography Vector: Aspect ratio is 310 wide by 94 high
  const renderTypography = (height: number) => {
    const width = height * (310 / 94);

    return (
      <svg
        xmlns="http://www.w3.org/2000/svg"
        viewBox="0 0 310 100"
        height={height}
        width={width}
        className="select-none overflow-visible shrink-0"
        aria-label="numi"
        role="img"
      >
        <defs>
          <filter id={limeGlowId} x="-20%" y="-100%" width="140%" height="300%">
            <feGaussianBlur stdDeviation={showGlow || isBadge ? 2.5 : 1.5} result="blur" />
            <feMerge>
              <feMergeNode in="blur" />
              <feMergeNode in="SourceGraphic" />
            </feMerge>
          </filter>
        </defs>

        {/* Signature Electric Lime Accent Line & Circular Dot (#8CE322) */}
        <g filter={`url(#${limeGlowId})`}>
          {/* Horizontal line ending at center of the 'i' */}
          <line
            x1="8"
            y1="14"
            x2="298"
            y2="14"
            stroke="#8CE322"
            strokeWidth="6.8"
            strokeLinecap="round"
          />
          {/* Circular accent dot placed directly above the vertical stem of the 'i' */}
          <circle cx="298" cy="14" r="11.5" fill="#8CE322" />
        </g>

        {/* Bold Minimalist Wordmark "numi" (dotless 'i' aligned under the lime dot) */}
        <g fill={isDarkText ? '#0F172A' : '#FFFFFF'}>
          {/* 'n' (x: 8 to 76) */}
          <path d="M 8,36 L 26,36 L 26,47 C 31,38 41,33 53,33 C 67,33 76,41 76,57 L 76,96 L 58,96 L 58,61 C 58,52 54,47 46,47 C 37,47 32,53 26,59 L 26,96 L 8,96 Z" />

          {/* 'u' (x: 90 to 158) */}
          <path d="M 90,36 L 108,36 L 108,71 C 108,80 112,85 120,85 C 129,85 134,79 140,73 L 140,36 L 158,36 L 158,96 L 140,96 L 140,85 C 135,94 125,99 113,99 C 99,99 90,91 90,75 Z" />

          {/* 'm' (x: 172 to 280) */}
          <path d="M 172,36 L 190,36 L 190,47 C 194,38 202,33 212,33 C 222,33 229,38 233,47 C 238,38 247,33 258,33 C 272,33 280,41 280,57 L 280,96 L 262,96 L 262,61 C 262,52 259,47 252,47 C 245,47 240,52 236,58 L 236,96 L 218,96 L 218,61 C 218,52 215,47 208,47 C 201,47 196,52 190,58 L 190,96 L 172,96 Z" />

          {/* 'i' stem (x: 289 to 307, width 18 - center is exactly 298) */}
          <rect x="289" y="36" width="18" height="60" rx="1.5" />
        </g>
      </svg>
    );
  };

  if (isBadge) {
    const innerHeight = Math.round(size * 0.42);
    return (
      <div
        className={`bg-black text-white rounded-2xl p-2.5 shadow-md border border-white/10 flex items-center justify-center shrink-0 select-none ${className}`}
        style={{ width: size, height: size }}
        onClick={onClick}
      >
        {renderTypography(innerHeight)}
      </div>
    );
  }

  return (
    <div
      className={`inline-flex items-center shrink-0 select-none ${className}`}
      onClick={onClick}
    >
      {renderTypography(size)}
    </div>
  );
};
