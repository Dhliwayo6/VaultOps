import React from 'react';
import { FaShieldAlt, FaKey, FaBoxOpen, FaChartLine } from 'react-icons/fa';

export default function OrbitGraphic() {
  return (
    <div className="relative w-72 h-72 sm:w-80 sm:h-80 md:w-96 md:h-96 mx-auto flex items-center justify-center">
      {/* Outer spinning ring */}
      <div className="absolute inset-0 border border-dashed border-border-token rounded-full animate-orbit" />
      {/* Middle spinning ring (reverse direction) */}
      <div className="absolute inset-8 border border-border-token rounded-full animate-[orbit_40s_linear_infinite_reverse] opacity-70" />
      {/* Inner ring */}
      <div className="absolute inset-20 border border-dashed border-border-token rounded-full opacity-50" />
      
      {/* Center Vault Core */}
      <div className="w-16 h-16 md:w-20 md:h-20 bg-accent/15 border border-accent/30 rounded-full flex items-center justify-center shadow-glow animate-pulse-slow">
        <div className="w-8 h-8 md:w-10 md:h-10 bg-accent rounded-full flex items-center justify-center text-white font-bold text-base md:text-lg tracking-wider">
          VO
        </div>
      </div>
      
      {/* Badge 1 - Shield (outer ring top) */}
      <div className="absolute top-0 left-1/2 -translate-x-1/2 -translate-y-1/2 bg-surface-elevated border border-border-token w-10 h-10 rounded-full flex items-center justify-center shadow-elevation hover:border-accent transition-colors">
        <FaShieldAlt className="text-base text-accent" />
      </div>
      {/* Badge 2 - Key (middle ring bottom-left) */}
      <div className="absolute bottom-10 left-10 bg-surface-elevated border border-border-token w-10 h-10 rounded-full flex items-center justify-center shadow-elevation hover:border-accent transition-colors">
        <FaKey className="text-base text-accent" />
      </div>
      {/* Badge 3 - Database (outer ring right-center) */}
      <div className="absolute top-1/2 right-0 translate-x-1/2 -translate-y-1/2 bg-surface-elevated border border-border-token w-10 h-10 rounded-full flex items-center justify-center shadow-elevation hover:border-accent transition-colors">
        <FaBoxOpen className="text-base text-accent" />
      </div>
      {/* Badge 4 - Activity (inner ring top-right) */}
      <div className="absolute top-20 right-20 bg-surface-elevated border border-border-token w-10 h-10 rounded-full flex items-center justify-center shadow-elevation hover:border-accent transition-colors">
        <FaChartLine className="text-base text-accent" />
      </div>
    </div>
  );
}
