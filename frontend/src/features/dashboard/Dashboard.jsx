import React, { useState, useEffect } from 'react';
import { useDashboardStats } from './hooks/useDashboardStats';
import { useNavigate } from 'react-router-dom';
import { ROUTES } from '@constants/routes';
import { exportToExcel } from '@api/exportApi';
import { getLocations } from '@api/locationsApi';

import Loading from '@components/Loading';
import ErrorState from '@components/ErrorState';
import { DoughnutStat, StackedBarStat, TrendLineStat } from './components/ChartWrapper';
import AdminActivityFeed from './components/AdminActivityFeed';
import { Package, Activity, Database, Wrench, AlertTriangle } from 'lucide-react';

const AnimatedNumber = ({ value }) => {
  const [displayValue, setDisplayValue] = useState(0);

  useEffect(() => {
    const prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    const isTest = typeof process !== 'undefined' && process.env?.NODE_ENV === 'test';
    if (prefersReducedMotion || isTest) {
      setDisplayValue(value);
      return;
    }

    const endValue = parseInt(value, 10);
    if (isNaN(endValue)) {
      setDisplayValue(value);
      return;
    }

    let startTimestamp = null;
    const duration = 400; // ms

    const step = (timestamp) => {
      if (!startTimestamp) startTimestamp = timestamp;
      const progress = Math.min((timestamp - startTimestamp) / duration, 1);
      setDisplayValue(Math.floor(progress * endValue));

      if (progress < 1) {
        window.requestAnimationFrame(step);
      }
    };

    window.requestAnimationFrame(step);
  }, [value]);

  return <span>{displayValue.toLocaleString()}</span>;
};

const getStatIcon = (label) => {
  switch (label) {
    case "Total Assets": return Package;
    case "In Use": return Activity;
    case "In Storage": return Database;
    case "In Service": return Wrench;
    case "Damaged": return AlertTriangle;
    default: return Package;
  }
};

const formatCurrency = (value) => {
  if (value === undefined || value === null) return 'R 0.00';
  return new Intl.NumberFormat('en-ZA', { style: 'currency', currency: 'ZAR' }).format(value);
};

const Dashboard = () => {
  const [selectedLocationId, setSelectedLocationId] = useState('');
  const [locations, setLocations] = useState([]);

  useEffect(() => {
    getLocations()
      .then(data => setLocations(data || []))
      .catch(err => console.error("Failed to load locations", err));
  }, []);

  const {
    stats,
    alerts,
    recentAssets,
    vaultCapacity,
    serviceNotice,
    isLoading,
    error,
    refetch,
    // Charts data
    allocationData,
    categoryData,
    trendData,
    financialData,
    chartsLoading,
    chartsError,
    isAdmin
  } = useDashboardStats(selectedLocationId ? parseInt(selectedLocationId, 10) : null);

  const navigate = useNavigate();
  const [isExporting, setIsExporting] = useState(false);

  const triggerDownload = (blob, fileExtension) => {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    const timestamp = new Date().toISOString().replace(/[:.]/g, '-').slice(0, 19);
    a.download = `vaultops-export-${timestamp}.${fileExtension}`;
    document.body.appendChild(a);
    a.click();
    setTimeout(() => {
      a.remove();
      window.URL.revokeObjectURL(url);
    }, 100);
  };

  const handleExportRegistry = async () => {
    setIsExporting(true);
    try {
      const blob = await exportToExcel({});
      triggerDownload(blob, 'xlsx');
    } catch (err) {
      console.error("Export failed", err);
      alert(err.message || "Failed to export registry.");
    } finally {
      setIsExporting(false);
    }
  };

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
      <header className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-3xl md:text-4xl font-black tracking-tight text-text-primary">Dashboard</h1>
          <p className="text-text-secondary font-medium">Control your vault today</p>
        </div>
        <div className="flex items-center gap-2">
          <label htmlFor="location-filter" className="text-xs font-black uppercase tracking-widest text-text-secondary">
            Filter:
          </label>
          <select
            id="location-filter"
            value={selectedLocationId}
            onChange={(e) => setSelectedLocationId(e.target.value)}
            className="px-4 py-2.5 bg-surface-elevated border border-border-token rounded-xl text-sm font-bold text-text-primary focus:outline-none focus:border-accent transition-colors"
          >
            <option value="">All Locations</option>
            {locations.map(loc => (
              <option key={loc.id} value={loc.id}>{loc.name}</option>
            ))}
          </select>
        </div>
      </header>
  
      {/* Overview Zone */}
      <section className="relative space-y-6" aria-label="Overview Statistics">
        <div className="absolute inset-x-0 -top-10 -bottom-10 bg-[radial-gradient(circle_at_top,rgba(245,158,11,0.07),transparent_70%)] pointer-events-none z-0" />
        <div className="relative z-10">
          <p className="text-[10px] md:text-xs font-black uppercase tracking-widest text-text-secondary mb-1">Overview</p>
          <h2 className="text-xl md:text-2xl font-black text-text-primary">Vault Statistics</h2>
        </div>
 
        {/* Bento Grid Stats */}
        <ul className="relative z-10 grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4" aria-label="Vault Statistics">
          {stats.map((stat, i) => {
            const isHeadline = stat.label === "Total Assets";
            const IconComponent = getStatIcon(stat.label);
            
            if (isHeadline) {
              return (
                <li 
                  key={i} 
                  className="col-span-2 md:col-span-2 lg:col-span-2 p-8 rounded-card bg-surface-elevated border border-accent/40 flex flex-col justify-between min-h-[160px] transition-all duration-300 hover:-translate-y-1 hover:border-accent shadow-glow cursor-default"
                >
                  <div className="flex justify-between items-start">
                    <span className="text-[10px] md:text-xs font-black uppercase tracking-widest text-text-secondary">
                      {stat.label}
                    </span>
                    <div className="w-12 h-12 rounded-full bg-accent/10 flex items-center justify-center text-accent flex-shrink-0">
                      <IconComponent className="w-6 h-6" />
                    </div>
                  </div>
                  <span className="text-5xl md:text-6xl font-black text-accent mt-4">
                    <AnimatedNumber value={stat.value} />
                  </span>
                </li>
              );
            }
            
            return (
              <li 
                key={i} 
                className="col-span-1 p-6 rounded-card bg-surface-elevated border border-border-token flex flex-col justify-between min-h-[140px] transition-all duration-300 hover:-translate-y-1 hover:border-accent/40 shadow-elevation hover:shadow-glow cursor-default"
              >
                <div className="flex justify-between items-start">
                  <span className="text-[10px] md:text-xs font-black uppercase tracking-widest text-text-secondary">
                    {stat.label}
                  </span>
                  <div className="w-10 h-10 rounded-full bg-accent/10 flex items-center justify-center text-accent flex-shrink-0">
                    <IconComponent className="w-5 h-5" />
                  </div>
                </div>
                <span className="text-3xl md:text-4xl font-black text-text-primary mt-4">
                  <AnimatedNumber value={stat.value} />
                </span>
              </li>
            );
          })}
        </ul>
      </section>

      {/* Trends Zone */}
      <section className="space-y-6" aria-label="Analytical Visualizations">
        <div className="px-2">
          <p className="text-[10px] md:text-xs font-black uppercase tracking-widest text-text-secondary mb-1">Trends</p>
          <h2 className="text-xl md:text-2xl font-black text-text-primary">Visual Diagnostics</h2>
        </div>

        {/* Row 1: Pie, Bar & Gated Financial */}
        <div className={`grid grid-cols-1 ${isAdmin ? 'lg:grid-cols-3' : 'md:grid-cols-2'} gap-8`}>
          {/* Asset Allocation */}
          <div className="bg-surface-elevated border border-border-token rounded-card p-6 shadow-elevation flex flex-col justify-between min-h-[280px]">
            <div>
              <h4 className="text-sm font-black uppercase tracking-widest text-text-secondary mb-1">Asset Allocation</h4>
              <p className="text-xs text-text-secondary font-medium">Current lifecycle deployment split</p>
            </div>
            <div className="mt-4 flex-1">
              <DoughnutStat data={allocationData} isLoading={chartsLoading} error={chartsError} />
            </div>
          </div>

          {/* Category Condition */}
          <div className="bg-surface-elevated border border-border-token rounded-card p-6 shadow-elevation flex flex-col justify-between min-h-[280px]">
            <div>
              <h4 className="text-sm font-black uppercase tracking-widest text-text-secondary mb-1">Category Health</h4>
              <p className="text-xs text-text-secondary font-medium">Device quality distributions by type</p>
            </div>
            <div className="mt-4 flex-1">
              <StackedBarStat data={categoryData} isLoading={chartsLoading} error={chartsError} />
            </div>
          </div>

          {/* Gated Financial Card (Admin Only) */}
          {isAdmin && (
            <div className="bg-surface-elevated border border-border-token rounded-card p-6 shadow-elevation flex flex-col justify-between min-h-[280px]">
              <div>
                <div className="flex justify-between items-center mb-1">
                  <h4 className="text-sm font-black uppercase tracking-widest text-text-secondary">Financial Valuation</h4>
                  <span className="text-[10px] font-black uppercase bg-accent/15 text-accent px-2 py-0.5 rounded-full">Admin Only</span>
                </div>
                <p className="text-xs text-text-secondary font-medium">Asset inventory & maintenance costs</p>
              </div>
              
              {chartsLoading ? (
                <div className="flex-1 flex flex-col justify-center space-y-4 animate-pulse mt-4">
                  <div className="h-10 bg-text-secondary/10 rounded-xl" />
                  <div className="h-10 bg-text-secondary/10 rounded-xl" />
                  <div className="h-10 bg-text-secondary/10 rounded-xl" />
                </div>
              ) : financialData ? (
                <div className="mt-4 flex-1 flex flex-col justify-around divide-y divide-border-token/50">
                  <div className="py-2 flex justify-between items-center">
                    <span className="text-xs font-semibold text-text-secondary">Total Asset Value</span>
                    <span className="text-lg font-black text-text-primary">{formatCurrency(financialData.totalAssetValuation)}</span>
                  </div>
                  <div className="py-2 flex justify-between items-center">
                    <span className="text-xs font-semibold text-text-secondary">Average Asset Value</span>
                    <span className="text-lg font-black text-text-primary">{formatCurrency(financialData.averageAssetValue)}</span>
                  </div>
                  <div className="py-2 flex justify-between items-center">
                    <span className="text-xs font-semibold text-text-secondary">Total Repair Cost</span>
                    <span className="text-lg font-black text-red-600 dark:text-red-400">{formatCurrency(financialData.totalMaintenanceExpenditure)}</span>
                  </div>
                </div>
              ) : (
                <div className="flex-1 flex items-center justify-center text-text-secondary text-sm">
                  Valuation data unavailable.
                </div>
              )}
            </div>
          )}
        </div>

        {/* Row 2: Maintenance and Registration Trends */}
        <div className="bg-surface-elevated border border-border-token rounded-card p-6 shadow-elevation min-h-[300px]">
          <div>
            <h4 className="text-sm font-black uppercase tracking-widest text-text-secondary mb-1">Volume & Repair Trends</h4>
            <p className="text-xs text-text-secondary font-medium">6-month activity log of assets and maintenance</p>
          </div>
          <div className="mt-6">
            <TrendLineStat data={trendData} isLoading={chartsLoading} error={chartsError} />
          </div>
        </div>
      </section>
 
      {/* Main Content Area */}
      <div className="grid grid-cols-1 xl:grid-cols-3 gap-8">
        
        {/* Active Items List & Admin Feed */}
        <div className="xl:col-span-2 space-y-8">
          <section className="space-y-4">
            <div className="flex items-end justify-between px-2">
              <div>
                <p className="text-[10px] md:text-xs font-black uppercase tracking-widest text-text-secondary mb-1">Inventory</p>
                <h3 className="text-xl md:text-2xl font-black text-text-primary">Active Inventory</h3>
              </div>
              <button 
                onClick={() => navigate(ROUTES.ASSETS)}
                className="text-sm font-bold text-text-secondary cursor-pointer hover:text-accent hover:underline focus-visible:ring-2 focus-visible:ring-accent focus-visible:outline-none rounded px-1 transition-colors"
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
                        <p className="text-xs font-mono text-text-secondary">{asset.id}</p>
                      </td>
                      <td className="px-8 py-6 font-semibold text-text-secondary">{asset.user}</td>
                      <td className="px-8 py-6">
                        <span className="px-3 py-1 bg-text-secondary/10 text-text-secondary rounded-full text-[10px] font-black uppercase">
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
                        <p className="text-xs font-mono text-text-secondary">{asset.id}</p>
                      </div>
                      <span className="px-3 py-1 bg-text-secondary/10 text-text-secondary rounded-full text-[10px] font-black uppercase">
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

          {isAdmin && <AdminActivityFeed />}
        </div>
  
        {/* Sidebar Activity Widget */}
        <div className="space-y-8">
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
                  <div className="h-full bg-text-primary rounded-full transition-all duration-500" style={{ width: `${vaultCapacity}%` }} />
                </div>
              </div>
            </div>
          </section>

          {/* Alerts Feed */}
          <section className="bg-surface-elevated border border-border-token rounded-card p-8 shadow-elevation">
            <p className="text-[10px] md:text-xs font-black uppercase tracking-widest text-text-secondary mb-1">Alerts</p>
            <h3 className="text-xl md:text-2xl font-black mb-6 text-text-primary">System Alerts</h3>
            {alerts.length === 0 ? (
              <div className="p-4 bg-emerald-500/10 border border-emerald-500/20 rounded-2xl flex items-center gap-3">
                <div className="w-8 h-8 rounded-full bg-emerald-500/20 text-emerald-600 dark:text-emerald-400 flex items-center justify-center flex-shrink-0">
                  <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                    <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z" clipRule="evenodd" />
                  </svg>
                </div>
                <div>
                  <p className="text-sm font-bold text-emerald-800 dark:text-emerald-400">All systems nominal</p>
                  <p className="text-xs text-emerald-700/80 dark:text-emerald-500">No issues require attention.</p>
                </div>
              </div>
            ) : (
              <div className="space-y-4">
                {alerts.map((alert) => (
                  <div 
                    key={alert.id} 
                    className={`p-4 rounded-2xl border transition-all duration-300 ${
                      alert.type === 'danger' ? 'bg-red-500/10 border-red-500/20 hover:border-red-500/40' :
                      alert.type === 'warning' ? 'bg-amber-500/10 border-amber-500/20 hover:border-amber-500/40' :
                      'bg-blue-500/10 border-blue-500/20 hover:border-blue-500/40'
                    }`}
                  >
                    <div className="flex gap-3">
                      <div className={`w-8 h-8 rounded-full flex items-center justify-center flex-shrink-0 ${
                        alert.type === 'danger' ? 'bg-red-500/20 text-red-600 dark:text-red-400' :
                        alert.type === 'warning' ? 'bg-amber-500/20 text-amber-600 dark:text-amber-400' :
                        'bg-blue-500/20 text-blue-600 dark:text-blue-400'
                      }`}>
                        {alert.type === 'danger' && (
                          <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                            <path strokeLinecap="round" strokeLinejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                          </svg>
                        )}
                        {alert.type === 'warning' && (
                          <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                            <path strokeLinecap="round" strokeLinejoin="round" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
                          </svg>
                        )}
                        {alert.type === 'info' && (
                          <svg xmlns="http://www.w3.org/2000/svg" className="h-4 w-4" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                            <path strokeLinecap="round" strokeLinejoin="round" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
                          </svg>
                        )}
                      </div>
                      <div className="flex-1">
                        <h4 className={`text-sm font-bold ${
                          alert.type === 'danger' ? 'text-red-900 dark:text-red-300' :
                          alert.type === 'warning' ? 'text-amber-900 dark:text-amber-300' :
                          'text-blue-900 dark:text-blue-300'
                        }`}>{alert.title}</h4>
                        <p className="text-xs text-text-secondary mt-1 font-medium leading-relaxed">{alert.message}</p>
                        
                        <button
                          onClick={() => navigate(alert.link)}
                          className={`mt-3 px-3 py-1.5 rounded-lg text-xs font-black uppercase tracking-wider transition-all cursor-pointer ${
                            alert.type === 'danger' ? 'bg-red-600 hover:bg-red-700 text-white shadow-sm' :
                            alert.type === 'warning' ? 'bg-amber-600 hover:bg-amber-700 text-white shadow-sm' :
                            'bg-blue-600 hover:bg-blue-700 text-white shadow-sm'
                          }`}
                        >
                          {alert.actionLabel}
                        </button>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </section>

          {/* Quick Actions Panel */}
          <section className="bg-surface-elevated border border-border-token rounded-card p-8 shadow-elevation">
            <p className="text-[10px] md:text-xs font-black uppercase tracking-widest text-text-secondary mb-1">Quick Actions</p>
            <h3 className="text-xl md:text-2xl font-black mb-6 text-text-primary">Quick Actions</h3>
            <div className="flex flex-col gap-3">
              <button
                onClick={() => navigate(`${ROUTES.ASSETS}?usage=SERVICE`)}
                className="flex items-center gap-4 p-4 rounded-xl border border-border-token/50 bg-bg-base hover:bg-surface-elevated hover:border-accent transition-all text-left w-full group cursor-pointer focus-visible:ring-2 focus-visible:ring-accent focus-visible:outline-none"
              >
                <div className="w-10 h-10 rounded-lg bg-text-secondary/10 text-text-secondary flex items-center justify-center font-bold group-hover:bg-accent/10 group-hover:text-accent group-hover:scale-105 transition-all flex-shrink-0">
                  <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                    <path strokeLinecap="round" strokeLinejoin="round" d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
                    <path strokeLinecap="round" strokeLinejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
                  </svg>
                </div>
                <div>
                  <p className="text-sm font-bold text-text-primary">Request Maintenance</p>
                  <p className="text-xs text-text-secondary">Send assets for repairs</p>
                </div>
              </button>

              <button
                onClick={() => navigate(`${ROUTES.ASSETS}?assignment=UNASSIGNED`)}
                className="flex items-center gap-4 p-4 rounded-xl border border-border-token/50 bg-bg-base hover:bg-surface-elevated hover:border-accent transition-all text-left w-full group cursor-pointer focus-visible:ring-2 focus-visible:ring-accent focus-visible:outline-none"
              >
                <div className="w-10 h-10 rounded-lg bg-text-secondary/10 text-text-secondary flex items-center justify-center font-bold group-hover:bg-accent/10 group-hover:text-accent group-hover:scale-105 transition-all flex-shrink-0">
                  <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                    <path strokeLinecap="round" strokeLinejoin="round" d="M8 7h12m0 0l-4-4m4 4l-4 4m0 6H4m0 0l4 4m-4-4l4-4" />
                  </svg>
                </div>
                <div>
                  <p className="text-sm font-bold text-text-primary">Check-In / Out</p>
                  <p className="text-xs text-text-secondary">Manage asset allocations</p>
                </div>
              </button>

              <button
                onClick={() => navigate(`${ROUTES.ASSETS}?condition=DAMAGED`)}
                className="flex items-center gap-4 p-4 rounded-xl border border-border-token/50 bg-bg-base hover:bg-surface-elevated hover:border-accent transition-all text-left w-full group cursor-pointer focus-visible:ring-2 focus-visible:ring-accent focus-visible:outline-none"
              >
                <div className="w-10 h-10 rounded-lg bg-text-secondary/10 text-text-secondary flex items-center justify-center font-bold group-hover:bg-accent/10 group-hover:text-accent group-hover:scale-105 transition-all flex-shrink-0">
                  <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                    <path strokeLinecap="round" strokeLinejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
                  </svg>
                </div>
                <div>
                  <p className="text-sm font-bold text-text-primary">Report Damage</p>
                  <p className="text-xs text-text-secondary">Update asset condition status</p>
                </div>
              </button>

              {isAdmin && (
                <>
                  <button
                    onClick={() => navigate(ROUTES.IMPORT)}
                    className="flex items-center gap-4 p-4 rounded-xl border border-border-token/50 bg-bg-base hover:bg-surface-elevated hover:border-accent transition-all text-left w-full group cursor-pointer focus-visible:ring-2 focus-visible:ring-accent focus-visible:outline-none"
                  >
                    <div className="w-10 h-10 rounded-lg bg-text-secondary/10 text-text-secondary flex items-center justify-center font-bold group-hover:bg-accent/10 group-hover:text-accent group-hover:scale-105 transition-all flex-shrink-0">
                      <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                        <path strokeLinecap="round" strokeLinejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12" />
                      </svg>
                    </div>
                    <div>
                      <p className="text-sm font-bold text-text-primary">Bulk Import</p>
                      <p className="text-xs text-text-secondary">Upload batch assets via Excel</p>
                    </div>
                  </button>

                  <button
                    onClick={handleExportRegistry}
                    disabled={isExporting}
                    className="flex items-center gap-4 p-4 rounded-xl border border-border-token/50 bg-bg-base hover:bg-surface-elevated hover:border-accent transition-all text-left w-full group cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed focus-visible:ring-2 focus-visible:ring-accent focus-visible:outline-none"
                  >
                    <div className="w-10 h-10 rounded-lg bg-text-secondary/10 text-text-secondary flex items-center justify-center font-bold group-hover:bg-accent/10 group-hover:text-accent group-hover:scale-105 transition-all flex-shrink-0">
                      {isExporting ? (
                        <div className="w-5 h-5 border-2 border-accent border-t-transparent rounded-full animate-spin" />
                      ) : (
                        <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth={2}>
                          <path strokeLinecap="round" strokeLinejoin="round" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
                        </svg>
                      )}
                    </div>
                    <div>
                      <p className="text-sm font-bold text-text-primary">Export Registry</p>
                      <p className="text-xs text-text-secondary">Download Excel data sheet</p>
                    </div>
                  </button>
                </>
              )}
            </div>
          </section>
        </div>
  
      </div>
    </div>
  );
};

export default Dashboard;
