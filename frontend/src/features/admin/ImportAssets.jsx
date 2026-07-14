import React, { useState, useEffect } from 'react';
import { importAssets, downloadExcelTemplate, downloadCsvTemplate, getImportLogs } from '@api/importApi';
import { FaSpinner, FaUpload, FaFileExcel, FaFileCsv } from 'react-icons/fa';

export default function ImportAssets() {
  const [file, setFile] = useState(null);
  const [dryRun, setDryRun] = useState(false);
  const [uploading, setUploading] = useState(false);
  const [msg, setMsg] = useState({ text: '', type: '' });
  const [logs, setLogs] = useState([]);
  const [loadingLogs, setLoadingLogs] = useState(true);

  const fetchLogs = async () => {
    setLoadingLogs(true);
    try {
      const data = await getImportLogs();
      setLogs(data || []);
    } catch (err) {
      console.error(err);
    } finally {
      setLoadingLogs(false);
    }
  };

  useEffect(() => {
    fetchLogs();
  }, []);

  const handleFileChange = (e) => {
    if (e.target.files && e.target.files[0]) {
      setFile(e.target.files[0]);
    }
  };

  const handleUpload = async (e) => {
    e.preventDefault();
    if (!file) {
      setMsg({ text: 'Please select a file to import.', type: 'error' });
      return;
    }

    setUploading(true);
    setMsg({ text: '', type: '' });

    try {
      const data = await importAssets(file, dryRun);
      setMsg({ text: `${data.message || 'Import job started successfully!'}`, type: 'success' });
      setFile(null);
      // Reset file input element
      const fileInput = document.getElementById('file-input');
      if (fileInput) fileInput.value = '';
      fetchLogs();
    } catch (err) {
      console.error(err);
      setMsg({ text: err.message || 'Failed to start import job.', type: 'error' });
    } finally {
      setUploading(false);
    }
  };

  const downloadTemplate = async (format) => {
    try {
      const blob = format === 'excel' ? await downloadExcelTemplate() : await downloadCsvTemplate();
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `asset-import-template.${format === 'excel' ? 'xlsx' : 'csv'}`;
      document.body.appendChild(a);
      a.click();
      a.remove();
      window.URL.revokeObjectURL(url);
    } catch (err) {
      console.error(err);
      setMsg({ text: err.message || 'Network error downloading template.', type: 'error' });
    }
  };

  return (
    <section className="w-full min-h-full flex flex-col gap-8 animate-in fade-in duration-500">
      <div>
        <h2 className="text-3xl md:text-4xl font-black tracking-tight text-text-primary">
          Import Assets
        </h2>
        <p className="text-text-secondary font-medium mt-1">Upload inventory in bulk from Excel or CSV files</p>
      </div>

      {msg.text && (
        <div role="alert" className={`p-4 border rounded-2xl text-sm font-semibold text-center ${
          msg.type === 'success' ? 'bg-emerald-500/10 border-emerald-500/20 text-emerald-700 dark:text-emerald-400' : 'bg-red-500/10 border-red-500/20 text-red-700 dark:text-red-400'
        }`}>
          {msg.text}
        </div>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Upload Form */}
        <div className="lg:col-span-2 bg-surface-elevated border border-border-token rounded-card p-6 md:p-8 shadow-elevation space-y-6">
          <h3 className="text-xl font-bold text-text-primary">New Batch Import</h3>
          <form onSubmit={handleUpload} className="space-y-6">
            <div className="flex flex-col gap-2">
              <label htmlFor="file-input" className="text-xs font-black uppercase tracking-wider text-text-secondary">Select File (.xlsx or .csv)</label>
              <input
                id="file-input"
                type="file"
                accept=".xlsx,.csv"
                onChange={handleFileChange}
                className="w-full p-4 border border-dashed border-border-token rounded-2xl cursor-pointer hover:border-accent focus-visible:ring-2 focus-visible:ring-accent focus-visible:outline-none transition-all font-semibold text-sm text-text-secondary bg-bg-base"
              />
            </div>

            <div className="flex items-center gap-3 bg-bg-base p-4 rounded-2xl border border-border-token">
              <input
                id="dry-run"
                type="checkbox"
                checked={dryRun}
                onChange={(e) => setDryRun(e.target.checked)}
                className="w-5 h-5 rounded border-border-token text-accent focus:ring-accent focus-visible:ring-2 focus-visible:ring-accent focus-visible:outline-none accent-accent cursor-pointer"
              />
              <div className="text-sm">
                <label htmlFor="dry-run" className="font-bold text-text-primary cursor-pointer select-none">Dry Run Validation Only</label>
                <p className="text-text-secondary font-medium text-xs mt-0.5">Validate file structure and format requirements without saving any changes.</p>
              </div>
            </div>

            <button
              type="submit"
              disabled={uploading}
              className="w-full py-4 bg-accent hover:bg-accent-hover text-white rounded-2xl font-black uppercase tracking-widest transition-all disabled:opacity-50 flex items-center justify-center gap-2 cursor-pointer shadow-glow focus-visible:ring-2 focus-visible:ring-accent focus-visible:outline-none focus-visible:ring-offset-2 dark:focus-visible:ring-offset-bg-base"
            >
              {uploading ? (
                <>
                  <FaSpinner className="animate-spin" /> Processing Batch...
                </>
              ) : (
                <>
                  <FaUpload /> Start Import Job
                </>
              )}
            </button>
          </form>
        </div>

        {/* Template Downloads */}
        <div className="bg-surface-elevated border border-border-token rounded-card p-6 md:p-8 shadow-elevation space-y-6 flex flex-col justify-between">
          <div className="space-y-6">
            <h3 className="text-xl font-bold text-text-primary">Templates</h3>
            <p className="text-text-secondary font-medium text-sm">Download baseline templates to prepare your asset lists with correct fields and formats.</p>
          </div>
          
          <div className="flex flex-col gap-3 mt-6">
            <button
              onClick={() => downloadTemplate('excel')}
              className="w-full py-3 bg-bg-base border border-border-token hover:bg-surface-elevated rounded-2xl font-bold text-sm text-text-primary transition-all flex items-center justify-center gap-2 cursor-pointer focus-visible:ring-2 focus-visible:ring-accent focus-visible:outline-none"
            >
              <FaFileExcel className="text-emerald-500" /> Excel Template (.xlsx)
            </button>
            <button
              onClick={() => downloadTemplate('csv')}
              className="w-full py-3 bg-bg-base border border-border-token hover:bg-surface-elevated rounded-2xl font-bold text-sm text-text-primary transition-all flex items-center justify-center gap-2 cursor-pointer focus-visible:ring-2 focus-visible:ring-accent focus-visible:outline-none"
            >
              <FaFileCsv className="text-cyan-500" /> CSV Template (.csv)
            </button>
          </div>
        </div>
      </div>

      {/* History logs */}
      <div className="space-y-4">
        <h3 className="text-xl font-bold text-text-primary">Import Job History</h3>
        {loadingLogs ? (
          <div className="flex justify-center py-6">
            <FaSpinner className="w-6 h-6 text-accent animate-spin" />
          </div>
        ) : (
          <div className="bg-surface-elevated border border-border-token rounded-card overflow-x-auto shadow-elevation">
            <table className="w-full min-w-[800px] text-left border-collapse">
              <thead>
                <tr className="border-b border-border-token text-[10px] uppercase tracking-widest font-black text-text-secondary">
                  <th className="px-8 py-5">Job ID</th>
                  <th className="px-8 py-5">File</th>
                  <th className="px-8 py-5">Size</th>
                  <th className="px-8 py-5">Status</th>
                  <th className="px-8 py-5">Summary</th>
                  <th className="px-8 py-5 text-right">Acquired At</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-border-token">
                {logs.map(log => (
                  <tr key={log.id} className="group hover:bg-bg-base transition-colors">
                    <td className="px-8 py-6 font-mono text-xs text-accent font-bold">#{log.id}</td>
                    <td className="px-8 py-6 font-bold text-text-primary">{log.fileName}</td>
                    <td className="px-8 py-6 font-semibold text-text-secondary">{(log.fileSize / 1024).toFixed(1)} KB</td>
                    <td className="px-8 py-6">
                      <span className={`px-3 py-1 rounded-full text-[10px] font-black uppercase tracking-widest ${
                        log.status === 'COMPLETED' ? 'bg-emerald-500/10 text-emerald-700 dark:text-emerald-400' :
                        log.status === 'FAILED' ? 'bg-red-500/10 text-red-700 dark:text-red-400' : 'bg-bg-base text-text-secondary border border-border-token'
                      }`}>
                        {log.status}
                      </span>
                    </td>
                    <td className="px-8 py-6 font-medium text-text-secondary text-xs">
                      {log.status === 'COMPLETED' ? (
                        <span>Created: <b>{log.processedCount}</b> / Errors: <b>{log.errorCount}</b></span>
                      ) : log.status === 'FAILED' ? (
                        <span className="text-red-500">{log.errorMessage || 'Job failed'}</span>
                      ) : (
                        <span>Processing...</span>
                      )}
                    </td>
                    <td className="px-8 py-6 text-right text-text-secondary font-semibold text-xs">
                      {new Date(log.startedAt).toLocaleString()}
                    </td>
                  </tr>
                ))}
                {logs.length === 0 && (
                  <tr>
                    <td colSpan="6" className="text-center py-8 text-text-secondary font-bold">
                      No import history found.
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </section>
  );
}
