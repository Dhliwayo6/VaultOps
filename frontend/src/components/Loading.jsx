import React from 'react';

export default function Loading({ message = 'Loading data from vault...' }) {
  return (
    <div className="flex flex-col items-center justify-center py-12 px-4 gap-4 animate-in fade-in duration-300">
      <div className="relative w-12 h-12">
        <div className="absolute inset-0 border-4 border-slate-100 rounded-full" />
        <div className="absolute inset-0 border-4 border-t-[#0EA5E9] rounded-full animate-spin" />
      </div>
      <p className="text-slate-400 font-bold text-xs uppercase tracking-widest animate-pulse">
        {message}
      </p>
    </div>
  );
}
