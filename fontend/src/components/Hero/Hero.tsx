import React from 'react'
import { useNavigate } from 'react-router-dom'

export default function Hero() {
    const navigate = useNavigate();
  return (
      <section className="pt-24 pb-20 px-6">
        <div className="max-w-4xl mx-auto text-center">
          <h1 className="text-5xl md:text-7xl font-black tracking-tight text-slate-900 mb-8">
            Track Your Corporate Assets.
          </h1>
          <p className="text-xl text-slate-500 font-medium leading-relaxed max-w-2xl mx-auto">
            Move beyond messy spreadsheets. Centralize your hardware tracking, 
            automate bulk updates, and gain total visibility over your assets' lifecycle.
          </p>
          <div className="mt-10 flex flex-col sm:flex-row gap-4 justify-center">
            <button 
               onClick={() => navigate('/sign-up')}
               className="px-8 py-4 bg-slate-900 text-white rounded-2xl font-black text-lg hover:bg-slate-800 transition-all shadow-xl shadow-slate-200"
            >
              Get Started for Free
            </button>
            <button 
                onClick={() => navigate('/sign-in')}
                className="px-8 py-4 bg-white border-2 border-slate-200 text-slate-900 rounded-2xl font-black text-lg hover:bg-slate-50 transition-all">
              Sign In
            </button>
          </div>
        </div>
      </section>
  )
}