import React from 'react';
import { useDashboardStats } from './hooks/useDashboardStats';
import AddAssetButton from '@features/assets/components/AddAssetButton';
import { useNavigate } from 'react-router-dom';
import { ROUTES } from '@constants/routes';

import Loading from '@components/Loading';
import ErrorState from '@components/ErrorState';

const Dashboard = () => {
  const { stats, recentAssets, vaultCapacity, serviceNotice, isLoading, error, refetch } = useDashboardStats();
  const navigate = useNavigate();

  if (isLoading) {
    return <Loading message="Fetching vault diagnostics..." />;
  }

  if (error) {
    return (
      <div className="py-12">
        <ErrorState title="Dashboard Error" message={error} onRetry={refetch} />
      </div>
    );
  }

  return (
    <div className="space-y-10">
      {/* Header */}
      <header className="flex flex-col md:flex-row md:items-end justify-between gap-4">
        <div>
          <h1 className="text-3xl md:text-4xl font-black tracking-tight text-text-primary">Dashboard</h1>
          <p className="text-text-secondary font-medium">Control your vault today</p>
        </div>
        <AddAssetButton />
      </header>
 
      {/* Bento Grid Stats */}
      <ul className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4" aria-label="Vault Statistics">
        {stats.map((stat, i) => (
          <li key={i} className={`${stat.color} p-6 rounded-card border border-border-token flex flex-col justify-between min-h-[140px] transition-transform hover:-translate-y-1 cursor-default shadow-elevation`}>
            <span className="text-xs font-black uppercase tracking-widest opacity-70">{stat.label}</span>
            <span className="text-4xl font-black">{stat.value}</span>
          </li>
        ))}
      </ul>
 
      {/* Main Content Area */}
      <div className="grid grid-cols-1 xl:grid-cols-3 gap-8">
        
        {/* Active Items List */}
        <section className="xl:col-span-2 space-y-4">
          <div className="flex items-center justify-between px-2">
            <h3 className="text-xl font-bold text-text-primary">Active Inventory</h3>
            <button 
              onClick={() => navigate(ROUTES.ASSETS)}
              className="text-sm font-bold text-accent cursor-pointer hover:underline focus-visible:ring-2 focus-visible:ring-accent focus-visible:outline-none rounded px-1"
            >
              View Registry
            </button>
          </div>
 
          <div className="bg-surface-elevated border border-border-token rounded-card overflow-hidden shadow-elevation">
            {/* Desktop Table - Hidden on Mobile */}
            <table className="hidden md:table w-full text-left border-collapse">
              <thead>
                <tr className="border-b border-border-token text-[10px] uppercase tracking-widest font-black text-text-secondary">
                  <th className="px-8 py-5">Asset</th>
                  <th className="px-8 py-5">Assigned To</th>
                  <th className="px-8 py-5">Status</th>
                  <th className="px-8 py-5 text-right">Activity</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-border-token">
                {recentAssets.map((asset) => (
                  <tr key={asset.id} className="group hover:bg-bg-base transition-colors">
                    <td className="px-8 py-6">
                      <p className="font-bold text-text-primary">{asset.name}</p>
                      <p className="text-xs font-mono text-accent">{asset.id}</p>
                    </td>
                    <td className="px-8 py-6 font-semibold text-text-secondary">{asset.user}</td>
                    <td className="px-8 py-6">
                      <span className="px-3 py-1 bg-accent/10 text-accent rounded-full text-[10px] font-black uppercase">
                        {asset.status}
                      </span>
                    </td>
                    <td className="px-8 py-6 text-right text-sm text-text-secondary font-medium">{asset.lastUpdated}</td>
                  </tr>
                ))}
              </tbody>
            </table>
 
            {/* Mobile Cards - Visible only on Small Screens */}
            <ul className="md:hidden divide-y divide-border-token" aria-label="Recent Asset Inventory">
              {recentAssets.map((asset) => (
                <li key={asset.id} className="p-6 space-y-3">
                  <div className="flex justify-between items-start">
                    <div>
                      <p className="font-bold text-text-primary">{asset.name}</p>
                      <p className="text-xs font-mono text-accent">{asset.id}</p>
                    </div>
                    <span className="px-3 py-1 bg-accent/10 text-accent rounded-full text-[10px] font-black uppercase">
                      {asset.status}
                    </span>
                  </div>
                  <div className="flex justify-between text-sm">
                    <span className="text-text-secondary">User: <b className="text-text-primary">{asset.user}</b></span>
                    <span className="text-text-secondary">{asset.lastUpdated}</span>
                  </div>
                </li>
              ))}
            </ul>
          </div>
        </section>
 
        {/* Sidebar Activity Widget */}
        <section className="bg-surface-elevated border border-border-token rounded-card p-8 shadow-elevation">
          <h3 className="text-xl font-bold mb-6 text-text-primary">Storage Health</h3>
          <div className="space-y-6">
            <div className="space-y-2">
              <div className="flex justify-between text-sm font-bold text-text-primary">
                <span>Vault Capacity</span>
                <span>{vaultCapacity}%</span>
              </div>
              <div 
                role="progressbar" 
                aria-valuenow={vaultCapacity} 
                aria-valuemin="0" 
                aria-valuemax="100" 
                aria-label="Vault storage capacity usage"
                className="w-full h-3 bg-bg-base rounded-full overflow-hidden"
              >
                <div className="h-full bg-accent rounded-full transition-all duration-500" style={{ width: `${vaultCapacity}%` }} />
              </div>
            </div>
            <div className="p-4 bg-bg-base rounded-2xl border border-border-token">
              <p className="text-xs font-bold text-text-secondary uppercase mb-2">Notice</p>
              <p className="text-sm font-medium text-text-secondary">{serviceNotice}</p>
            </div>
          </div>
        </section>
 
      </div>
    </div>
  );
};

export default Dashboard;
