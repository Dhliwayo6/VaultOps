import React, { useState } from 'react';
import { exportToExcel, exportToCsv } from '@api/exportApi';
import { ConditionStatus, Usage } from '@constants/assets';
import { FaFileExcel, FaFileCsv, FaInfoCircle, FaSpinner } from 'react-icons/fa';

export default function Reports() {
  const [condition, setCondition] = useState('');
  const [usage, setUsage] = useState('');
  const [category, setCategory] = useState('');
  const [location, setLocation] = useState('');
  
  const [isExporting, setIsExporting] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const triggerDownload = (blob, fileExtension) => {
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    
    const timestamp = new Date().toISOString().replace(/[:.]/g, '-').slice(0, 19);
    a.download = `vaultops-export-${timestamp}.${fileExtension}`;
    
    document.body.appendChild(a);
    a.click();
    
    // Clean up
    setTimeout(() => {
      a.remove();
      window.URL.revokeObjectURL(url);
    }, 100);
  };

  const handleExport = async (format) => {
    setIsExporting(true);
    setError('');
    setSuccess('');

    const filters = {
      category: category.trim() || undefined,
      condition: condition || undefined,
      usage: usage || undefined,
      location: location.trim() || undefined
    };

    try {
      let blob;
      if (format === 'excel') {
        blob = await exportToExcel(filters);
        triggerDownload(blob, 'xlsx');
      } else {
        blob = await exportToCsv(filters);
        triggerDownload(blob, 'csv');
      }
      setSuccess(`Vault audit registry successfully exported as ${format.toUpperCase()}!`);
    } catch (err) {
      console.error(err);
      setError(err.message || `Failed to compile ${format.toUpperCase()} report. Please check server status.`);
    } finally {
      setIsExporting(false);
    }
  };

  return (
    <div className="space-y-10">
      {/* Header */}
      <header>
        <h1 className="text-3xl md:text-4xl font-black tracking-tight text-text-primary">Audit Reports</h1>
        <p className="text-text-secondary font-medium">Download formatted excel spreadsheets and raw CSV data dumps of your assets</p>
      </header>

      {/* Main Grid */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        
        {/* Left Column: Filter Form */}
        <section className="lg:col-span-1 bg-surface-elevated border border-border-token rounded-card p-6 md:p-8 shadow-elevation flex flex-col justify-between">
          <div className="space-y-6">
            <h3 className="text-lg font-bold text-text-primary border-b border-border-token pb-3">Export Filters</h3>
            
            {/* Category Filter */}
            <div className="space-y-2">
              <label htmlFor="report-category" className="text-xs font-black uppercase tracking-widest text-text-secondary">Category</label>
              <input 
                id="report-category"
                type="text" 
                value={category}
                onChange={(e) => setCategory(e.target.value)}
                placeholder="e.g. Laptop, Server, Router"
                className="w-full h-11 px-4 bg-bg-base border border-border-token rounded-xl focus:border-accent focus:bg-surface-elevated focus:outline-none focus-visible:ring-2 focus-visible:ring-accent font-semibold text-sm transition-all text-text-primary"
              />
            </div>

            {/* Location Filter */}
            <div className="space-y-2">
              <label htmlFor="report-location" className="text-xs font-black uppercase tracking-widest text-text-secondary">Location</label>
              <input 
                id="report-location"
                type="text" 
                value={location}
                onChange={(e) => setLocation(e.target.value)}
                placeholder="e.g. HQ Block A, Room 302"
                className="w-full h-11 px-4 bg-bg-base border border-border-token rounded-xl focus:border-accent focus:bg-surface-elevated focus:outline-none focus-visible:ring-2 focus-visible:ring-accent font-semibold text-sm transition-all text-text-primary"
              />
            </div>

            {/* Condition Filter */}
            <div className="space-y-2">
              <label htmlFor="report-condition" className="text-xs font-black uppercase tracking-widest text-text-secondary">Condition Status</label>
              <select
                id="report-condition"
                value={condition}
                onChange={(e) => setCondition(e.target.value)}
                className="w-full h-11 px-4 bg-bg-base border border-border-token rounded-xl focus:border-accent focus:bg-surface-elevated focus:outline-none focus-visible:ring-2 focus-visible:ring-accent text-xs font-black uppercase tracking-wider text-text-secondary"
              >
                <option value="">All Conditions</option>
                {Object.values(ConditionStatus).map((status) => (
                  <option key={status} value={status}>{status.replace(/_/g, ' ')}</option>
                ))}
              </select>
            </div>

            {/* Usage Status Filter */}
            <div className="space-y-2">
              <label htmlFor="report-usage" className="text-xs font-black uppercase tracking-widest text-text-secondary">Usage Status</label>
              <select
                id="report-usage"
                value={usage}
                onChange={(e) => setUsage(e.target.value)}
                className="w-full h-11 px-4 bg-bg-base border border-border-token rounded-xl focus:border-accent focus:bg-surface-elevated focus:outline-none focus-visible:ring-2 focus-visible:ring-accent text-xs font-black uppercase tracking-wider text-text-secondary"
              >
                <option value="">All Statuses</option>
                {Object.values(Usage).map((status) => (
                  <option key={status} value={status}>{status.replace(/_/g, ' ')}</option>
                ))}
              </select>
            </div>
          </div>

          <div className="mt-8 p-4 bg-bg-base rounded-2xl border border-border-token flex items-start gap-3">
            <FaInfoCircle className="text-accent mt-0.5 flex-shrink-0" />
            <p className="text-xs text-text-secondary font-medium leading-relaxed">
              Leaving all filters blank will compile the entire asset catalog into the exported spreadsheet or CSV file.
            </p>
          </div>
        </section>

        {/* Right Column: Export Channels */}
        <section className="lg:col-span-2 space-y-6">
          
          {/* Notification Feedback */}
          {error && (
            <div role="alert" className="p-4 bg-red-500/10 border border-red-500/20 rounded-2xl text-xs font-semibold text-red-500 text-center animate-in fade-in duration-200">
              {error}
            </div>
          )}
          {success && (
            <div role="status" className="p-4 bg-emerald-500/10 border border-emerald-500/20 rounded-2xl text-xs font-semibold text-emerald-700 dark:text-emerald-400 text-center animate-in fade-in duration-200">
              {success}
            </div>
          )}

          {/* Cards Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            
            {/* Excel Export Card */}
            <div className="bg-surface-elevated border border-border-token rounded-card p-8 flex flex-col justify-between gap-6 transition-all duration-300 hover:-translate-y-1 hover:border-accent/40 shadow-elevation">
              <div className="space-y-4">
                <div className="w-12 h-12 rounded-2xl bg-emerald-500/10 flex items-center justify-center text-emerald-500">
                  <FaFileExcel className="text-2xl" />
                </div>
                <h4 className="text-xl font-bold text-text-primary">Excel Spreadsheet</h4>
                <p className="text-sm text-text-secondary leading-relaxed">
                  Best for human reviewing. Features structured tables, colored badges, dates, pricing columns, and dynamic formatting.
                </p>
              </div>
              
              <button
                disabled={isExporting}
                onClick={() => handleExport('excel')}
                className="w-full py-4 bg-accent hover:bg-accent-hover disabled:bg-slate-700/30 text-white rounded-2xl font-black text-sm uppercase tracking-widest transition-all flex items-center justify-center gap-2 cursor-pointer shadow-glow focus-visible:ring-2 focus-visible:ring-accent focus-visible:outline-none focus-visible:ring-offset-2 dark:focus-visible:ring-offset-bg-base"
              >
                {isExporting ? (
                  <>
                    <FaSpinner className="animate-spin" /> Compiling...
                  </>
                ) : (
                  'Export to Excel'
                )}
              </button>
            </div>

            {/* CSV Export Card */}
            <div className="bg-surface-elevated border border-border-token rounded-card p-8 flex flex-col justify-between gap-6 transition-all duration-300 hover:-translate-y-1 hover:border-accent/40 shadow-elevation">
              <div className="space-y-4">
                <div className="w-12 h-12 rounded-2xl bg-cyan-500/10 flex items-center justify-center text-cyan-500">
                  <FaFileCsv className="text-2xl" />
                </div>
                <h4 className="text-xl font-bold text-text-primary">Raw CSV File</h4>
                <p className="text-sm text-text-secondary leading-relaxed">
                  Best for data pipelines. A lightweight, standard comma-separated text file suitable for importing into external systems.
                </p>
              </div>

              <button
                disabled={isExporting}
                onClick={() => handleExport('csv')}
                className="w-full py-4 bg-bg-base border border-border-token hover:bg-surface-elevated disabled:bg-slate-700/10 text-text-primary rounded-2xl font-black text-sm uppercase tracking-widest transition-all flex items-center justify-center gap-2 cursor-pointer focus-visible:ring-2 focus-visible:ring-accent focus-visible:outline-none"
              >
                {isExporting ? (
                  <>
                    <FaSpinner className="animate-spin" /> Compiling...
                  </>
                ) : (
                  'Export to CSV'
                )}
              </button>
            </div>
          </div>
        </section>
      </div>
    </div>
  );
}
