import React from 'react';
import { FaArrowLeft } from "react-icons/fa6";
import { useNavigate } from "react-router-dom";

export default function NavigateBackButton({title}) {
    const navigate = useNavigate();
  return (
        <button 
            onClick={() => navigate(-1)}
            className="group flex items-center gap-2.5 px-4 py-2 border border-border-token hover:border-accent bg-surface-elevated/50 hover:bg-accent/5 text-text-secondary hover:text-text-primary text-xs font-black uppercase tracking-widest rounded-full transition-all duration-300 shadow-xs hover:shadow-[0_0_15px_rgba(20,184,166,0.15)] cursor-pointer focus-visible:ring-2 focus-visible:ring-accent focus-visible:outline-none"
        >
            <FaArrowLeft className="text-xs transition-transform duration-300 group-hover:-translate-x-1" />
            <span>{title}</span>
        </button>
    )
}
