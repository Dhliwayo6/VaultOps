import React from 'react';
import { FaArrowLeft } from "react-icons/fa6";
import { useNavigate } from "react-router-dom";

export default function NavigateBackButton({title}) {
    const navigate = useNavigate();
  return (
        <button 
        onClick={() => navigate(-1)}
        className="flex items-center gap-2 text-slate-500 hover:text-red-600 transition-colors font-semibold"
        >
            <FaArrowLeft />
            {title}
        </button>
    )
}
