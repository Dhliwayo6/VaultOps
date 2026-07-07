import React from 'react';
import { useNavigate } from 'react-router-dom';
import { ROUTES } from '@constants/routes';
import OrbitGraphic from '@components/OrbitGraphic';

export default function Hero() {
  const navigate = useNavigate();

  return (
    <section className="pt-16 pb-20 px-6 max-w-7xl mx-auto">
      <div className="grid grid-cols-1 lg:grid-cols-12 gap-12 items-center">
        
        {/* Left Column: Hero Text */}
        <div className="lg:col-span-7 space-y-8 text-center lg:text-left">
          <h1 className="text-5xl md:text-7xl font-black font-display tracking-tight text-text-primary leading-[1.05]">
            Track Your Corporate <span className="text-transparent bg-clip-text bg-gradient-to-r from-accent to-cyan-500 dark:to-emerald-400">Assets</span>.
          </h1>
          <p className="text-xl text-text-secondary font-medium leading-relaxed max-w-2xl mx-auto lg:mx-0">
            Move beyond messy spreadsheets. Centralize your hardware inventory, 
            automate bulk updates, and gain absolute visibility over your assets' lifecycle.
          </p>
          <div className="flex flex-col sm:flex-row gap-4 justify-center lg:justify-start">
            <button 
              onClick={() => navigate(ROUTES.SIGN_UP)}
              className="btn-primary"
            >
              Get Started for Free
            </button>
            <button 
              onClick={() => navigate(ROUTES.SIGN_IN)}
              className="btn-secondary"
            >
              Sign In
            </button>
          </div>
        </div>

        {/* Right Column: Orbit Graphic */}
        <div className="lg:col-span-5 flex items-center justify-center">
          <OrbitGraphic />
        </div>
      </div>
    </section>
  );
}
