import React from 'react';

interface Asset {
  id: string;
  name: string;
  user: string;
  status: 'In Use' | 'In Storage' | 'In Service' | 'Damaged';
  lastUpdated: string;
}

const Dashboard: React.FC = () => {
  const stats = [
    { label: "Total Assets", value: "1,284", color: "bg-white text-slate-800" },
    { label: "In Use", value: "856", color: "bg-[#0EA5E9] text-white" }, // Primary accent
    { label: "In Storage", value: "342", color: "bg-white text-slate-800" },
    { label: "In Service", value: "76", color: "bg-white text-slate-800" },
    { label: "Damaged", value: "10", color: "bg-red-50 text-red-600" },
  ];

  const recentAssets: Asset[] = [
    { id: "SK-9920", name: "Dell Precision 5570", user: "Mandisi", status: "In Use", lastUpdated: "2 mins ago" },
    { id: "SK-8812", name: "Sony A7 IV Camera", user: "Sarah Smith", status: "In Use", lastUpdated: "1 hour ago" },
    { id: "SK-4421", name: "Logitech MX Master 3", user: "Alex Agu", status: "In Storage", lastUpdated: "Yesterday" },
  ];

  return (
    <div className="space-y-10">
      {/* Header */}
      <header className="flex flex-col md:flex-row md:items-end justify-between gap-4">
        <div>
          <h1 className="text-4xl font-black tracking-tight text-slate-900">Dashboard</h1>
          <p className="text-slate-500 font-medium">Control your vault today</p>
        </div>
        <div className="flex gap-2">
          <button className="px-5 py-2.5 bg-white border-2 border-slate-200 rounded-xl font-bold text-sm hover:bg-slate-50 transition-all">Export Report</button>
          <button className="px-5 py-2.5 bg-[#0EA5E9] text-white rounded-xl font-bold text-sm active:scale-95 transition-all">+ Add Asset</button>
        </div>
      </header>

      {/* Bento Grid Stats */}
      <section className="grid grid-cols-2 lg:grid-cols-5 gap-4">
        {stats.map((stat, i) => (
          <div key={i} className={`${stat.color} p-6 rounded-2xl border-2 border-slate-200 flex flex-col justify-between min-h-[140px] transition-transform hover:-translate-y-1 cursor-default`}>
            <span className="text-xs font-black uppercase tracking-widest opacity-70">{stat.label}</span>
            <span className="text-4xl font-black">{stat.value}</span>
          </div>
        ))}
      </section>

      {/* Main Content Area */}
      <div className="grid grid-cols-1 xl:grid-cols-3 gap-8">
        
        {/* Active Items List (The "Interesting" bit) */}
        <section className="xl:col-span-2 space-y-4">
          <div className="flex items-center justify-between px-2">
            <h3 className="text-xl font-bold">Active Inventory</h3>
            <span className="text-sm font-bold text-[#0EA5E9] cursor-pointer">View Registry</span>
          </div>

          <div className="bg-white border-2 border-slate-200 rounded-3xl overflow-hidden">
            {/* Desktop Table - Hidden on Mobile */}
            <table className="hidden md:table w-full text-left border-collapse">
              <thead>
                <tr className="border-b-2 border-slate-100 text-[10px] uppercase tracking-widest font-black text-slate-400">
                  <th className="px-8 py-5">Asset</th>
                  <th className="px-8 py-5">Assigned To</th>
                  <th className="px-8 py-5">Status</th>
                  <th className="px-8 py-5 text-right">Activity</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {recentAssets.map((asset) => (
                  <tr key={asset.id} className="group hover:bg-slate-50 transition-colors">
                    <td className="px-8 py-6">
                      <p className="font-bold text-slate-900">{asset.name}</p>
                      <p className="text-xs font-mono text-[#0EA5E9]">{asset.id}</p>
                    </td>
                    <td className="px-8 py-6 font-semibold text-slate-600">{asset.user}</td>
                    <td className="px-8 py-6">
                      <span className="px-3 py-1 bg-blue-50 text-[#0EA5E9] rounded-full text-[10px] font-black uppercase">
                        {asset.status}
                      </span>
                    </td>
                    <td className="px-8 py-6 text-right text-sm text-slate-400 font-medium">{asset.lastUpdated}</td>
                  </tr>
                ))}
              </tbody>
            </table>

            {/* Mobile Cards - Visible only on Small Screens */}
            <div className="md:hidden divide-y divide-slate-100">
              {recentAssets.map((asset) => (
                <div key={asset.id} className="p-6 space-y-3">
                  <div className="flex justify-between items-start">
                    <div>
                      <p className="font-bold text-slate-900">{asset.name}</p>
                      <p className="text-xs font-mono text-[#0EA5E9]">{asset.id}</p>
                    </div>
                    <span className="px-3 py-1 bg-blue-50 text-[#0EA5E9] rounded-full text-[10px] font-black uppercase">
                      {asset.status}
                    </span>
                  </div>
                  <div className="flex justify-between text-sm">
                    <span className="text-slate-500">User: <b className="text-slate-800">{asset.user}</b></span>
                    <span className="text-slate-400">{asset.lastUpdated}</span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </section>

        {/* Sidebar Activity Widget */}
        <section className="bg-white border-2 border-slate-200 rounded-3xl p-8">
          <h3 className="text-xl font-bold mb-6">Storage Health</h3>
          <div className="space-y-6">
            <div className="space-y-2">
              <div className="flex justify-between text-sm font-bold">
                <span>Vault Capacity</span>
                <span>82%</span>
              </div>
              <div className="w-full h-3 bg-slate-100 rounded-full overflow-hidden">
                <div className="h-full bg-[#0EA5E9] w-[82%] rounded-full" />
              </div>
            </div>
            <div className="p-4 bg-slate-50 rounded-2xl border border-slate-100">
              <p className="text-xs font-bold text-slate-400 uppercase mb-2">Notice</p>
              <p className="text-sm font-medium text-slate-600">3 items are marked for service this week. Check the maintenance log.</p>
            </div>
          </div>
        </section>

      </div>
    </div>
  );
};

export default Dashboard;