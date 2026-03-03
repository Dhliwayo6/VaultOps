
import React from 'react'
import { featureData } from './FeatureTools'

export default function Features() {
  return (
      <section className="py-24 bg-slate-50 px-6">
        <div className="max-w-7xl mx-auto">
          <div className="text-center mb-20">
            <h2 className="text-xs font-black uppercase tracking-[0.3em] text-[#0EA5E9] mb-4">Core Engine</h2>
            <h3 className="text-4xl font-black tracking-tight text-slate-900">Built for Efficiency & Accuracy</h3>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            {featureData.map((f, i) => (
              <div key={i} className="bg-white p-8 rounded-3xl border-2 border-slate-100 hover:border-[#0EA5E9]/30 transition-all group">
                <div className="text-4xl mb-6 group-hover:scale-110 transition-transform inline-block">
                  {f.icon}
                </div>
                <h4 className="text-xl font-black text-slate-900 mb-4">{f.title}</h4>
                <p className="text-slate-500 font-medium leading-relaxed">
                  {f.desc}
                </p>
              </div>
            ))}
          </div>
        </div>
      </section>
  )
}