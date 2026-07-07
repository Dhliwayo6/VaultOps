import React from 'react';
import { useTheme } from '@context/ThemeContext';
import { HiSun, HiMoon } from 'react-icons/hi';

export default function ThemeToggle({ className = "" }) {
  const { theme, toggleTheme } = useTheme();

  return (
    <button
      onClick={toggleTheme}
      className={`p-2.5 rounded-xl transition-all duration-300 bg-slate-100 hover:bg-slate-200 dark:bg-white/5 dark:hover:bg-white/10 text-text-secondary hover:text-text-primary focus:outline-none cursor-pointer focus:ring-2 focus:ring-accent flex items-center justify-center ${className}`}
      aria-label={`Switch to ${theme === 'light' ? 'dark' : 'light'} theme`}
    >
      <div className="relative w-5 h-5 overflow-hidden">
        {/* Sun Icon */}
        <div
          className={`absolute inset-0 transition-transform duration-500 ease-out flex items-center justify-center ${
            theme === 'light' ? 'translate-y-0 rotate-0 scale-100' : '-translate-y-8 rotate-90 scale-50'
          }`}
        >
          <HiSun className="text-lg text-amber-600 dark:text-amber-500" />
        </div>
        {/* Moon Icon */}
        <div
          className={`absolute inset-0 transition-transform duration-500 ease-out flex items-center justify-center ${
            theme === 'dark' ? 'translate-y-0 rotate-0 scale-100' : 'translate-y-8 -rotate-90 scale-50'
          }`}
        >
          <HiMoon className="text-lg text-amber-500 dark:text-amber-400" />
        </div>
      </div>
    </button>
  );
}
