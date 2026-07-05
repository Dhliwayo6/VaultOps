import React from 'react';

export default function ErrorState({ 
  title = 'API Request Failed', 
  message = 'An unexpected error occurred while communicating with the server.', 
  onRetry 
}) {
  return (
    <div className="w-full max-w-lg mx-auto bg-white border-2 border-red-100 rounded-3xl p-8 flex flex-col items-center gap-6 text-center animate-in zoom-in-95 duration-300">
      <div className="w-12 h-12 bg-red-50 rounded-2xl flex items-center justify-center border border-red-100">
        <span className="text-red-500 font-black text-2xl">!</span>
      </div>
      
      <div className="space-y-2">
        <h3 className="text-slate-900 font-black text-lg uppercase tracking-tight">{title}</h3>
        <p className="text-slate-500 font-semibold text-sm leading-relaxed">{message}</p>
      </div>

      {onRetry && (
        <button
          onClick={onRetry}
          className="px-6 py-3 bg-slate-900 hover:bg-slate-800 text-white rounded-2xl font-black uppercase text-xs tracking-widest transition-all active:scale-[0.98]"
        >
          Try Again
        </button>
      )}
    </div>
  );
}
