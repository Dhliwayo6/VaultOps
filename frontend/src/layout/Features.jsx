import React from 'react'
import { featureData } from '@constants/features'
import { FaChartBar, FaShieldAlt, FaHistory, FaFileExport, FaSearch, FaClipboardList, FaKey, FaLock, FaMapMarkerAlt, FaColumns } from 'react-icons/fa'

const iconMap = {
  chart: <FaChartBar className="text-accent" />,
  shield: <FaShieldAlt className="text-accent" />,
  sync: <FaHistory className="text-accent" />,
  export: <FaFileExport className="text-accent" />,
  search: <FaSearch className="text-accent" />,
  logs: <FaClipboardList className="text-accent" />,
  key: <FaKey className="text-accent" />,
  lock: <FaLock className="text-accent" />,
  location: <FaMapMarkerAlt className="text-accent" />,
  sidebar: <FaColumns className="text-accent" />
};

export default function Features() {
  return (
      <section className="py-24 px-6 border-t border-border-token">
        <div className="max-w-7xl mx-auto">
          <div className="text-center mb-20">
            <h2 className="text-xs font-black uppercase tracking-[0.3em] text-accent mb-4">Core Engine</h2>
            <h3 className="text-4xl font-black font-display tracking-tight text-text-primary">Built for Efficiency & Accuracy</h3>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
            {featureData.map((f, i) => (
              <div 
                key={i} 
                className="bg-surface-elevated p-8 rounded-card border border-border-token hover:border-accent/40 hover:-translate-y-1 transition-all duration-300 shadow-elevation group"
              >
                <div className="text-3xl mb-6 group-hover:scale-110 transition-transform inline-block select-none">
                  {iconMap[f.icon] || f.icon}
                </div>
                <h4 className="text-xl font-black text-text-primary mb-4">{f.title}</h4>
                <p className="text-text-secondary font-medium leading-relaxed">
                  {f.desc}
                </p>
              </div>
            ))}
          </div>
        </div>
      </section>
  )
}
