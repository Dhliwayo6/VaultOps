import React from 'react'

export default function Footer() {
  return (
      <footer className="bg-white pt-20 pb-10 border-t-2 border-slate-50 px-6">
        <div className="max-w-7xl mx-auto">
          <div className="grid grid-cols-2 md:grid-cols-4 gap-12 mb-16">
            <div className="col-span-2 md:col-span-1">
              <div className="flex items-center gap-2 mb-6">
                <div className="w-6 h-6 bg-[#0EA5E9] rounded flex items-center justify-center text-white font-black text-sm">V</div>
                <span className="text-xl font-black tracking-tighter">VaultOps</span>
              </div>
              <p className="text-slate-500 font-medium">The intelligent way to manage corporate inventory.</p>
            </div>
            {['Product', 'Resources', 'Company'].map((title) => (
              <div key={title}>
                <h5 className="font-black text-slate-900 uppercase text-xs tracking-widest mb-6">{title}</h5>
                <ul className="space-y-4">
                  {['Features', 'Integrations', 'Pricing'].map((item) => (
                    <li key={item}>
                      <a href="#" className="text-slate-500 font-medium hover:text-[#0EA5E9] transition-colors">{item}</a>
                    </li>
                  ))}
                </ul>
              </div>
            ))}
          </div>
          <div className="pt-8 border-t border-slate-100 flex flex-col md:flex-row justify-between items-center gap-4">
            <p className="text-slate-400 font-bold text-xs uppercase tracking-widest">
              &copy; 2026 VaultOps. Secure Asset Management.
            </p>
            <div className="flex gap-6">
                <span className="text-xs font-black text-slate-300">STATUS: ALL SYSTEMS OPERATIONAL</span>
            </div>
          </div>
        </div>
      </footer>
  )
}