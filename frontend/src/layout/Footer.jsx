import React from 'react'

export default function Footer() {
  return (
      <footer className="bg-surface-elevated pt-20 pb-10 border-t border-border-token px-6 transition-colors">
        <div className="max-w-7xl mx-auto">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-12 mb-16">
            <div className="col-span-2 md:col-span-1">
              <div className="flex items-center gap-2 mb-6">
                <div className="w-6 h-6 rounded-full logo-3d flex items-center justify-center text-white font-black text-[8px] tracking-wider">VO</div>
                <span className="text-xl font-black font-display tracking-tighter text-text-primary">VaultOps</span>
              </div>
              <p className="text-text-secondary font-medium">The intelligent way to manage corporate inventory.</p>
            </div>
            {Object.entries({
              Product: ['Features']
            }).map(([title, items]) => (
              <div key={title}>
                <h5 className="font-black text-text-primary uppercase text-xs tracking-widest mb-6">{title}</h5>
                <ul className="space-y-4">
                  {items.map((item) => (
                    <li key={item}>
                      <a href="#" className="text-text-secondary font-medium hover:text-text-primary transition-colors">{item}</a>
                    </li>
                  ))}
                </ul>
              </div>
            ))}
          </div>
          <div className="pt-8 border-t border-border-token flex flex-col md:flex-row justify-between items-center gap-4">
            <p className="text-text-secondary font-bold text-xs uppercase tracking-widest">
              &copy; 2026 VaultOps. Secure Asset Management.
            </p>
            <div className="flex gap-6">
                <span className="text-xs font-black text-accent tracking-widest animate-pulse">STATUS: ALL SYSTEMS OPERATIONAL</span>
            </div>
          </div>
        </div>
      </footer>
  )
}
