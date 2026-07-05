import React, { useState, useEffect } from 'react';
import { importAssets, downloadExcelTemplate, downloadCsvTemplate, getImportLogs } from '@api/importApi';

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
        <h2 className="text-4xl font-black tracking-tight text-slate-900">
          Import Assets
        </h2>
        <p className="text-slate-500 font-medium mt-1">Upload inventory in bulk from Excel or CSV files</p>
      </div>

      {msg.text && (
        <div className={`p-4 border rounded-2xl text-sm font-semibold ${
          msg.type === 'success' ? 'bg-emerald-50 border-emerald-100 text-emerald-600' : 'bg-red-50 border-red-100 text-red-600'
        }`}>
          {msg.text}
        </div>
      )}

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Upload Form */}
        <div className="lg:col-span-2 bg-white border-2 border-slate-200 rounded-3xl p-8 shadow-sm space-y-6">
          <h3 className="text-xl font-bold">New Batch Import</h3>
          <form onSubmit={handleUpload} className="space-y-6">
            <div className="flex flex-col gap-2">
              <label className="text-xs font-black uppercase tracking-wider text-slate-400">Select File (.xlsx or .csv)</label>
              <input
                id="file-input"
                type="file"
                accept=".xlsx,.csv"
                onChange={handleFileChange}
                className="w-full p-4 border-2 border-dashed border-slate-200 rounded-2xl cursor-pointer hover:border-[#0EA5E9] transition-all font-semibold text-sm"
              />
            </div>

            <div className="flex items-center gap-3 bg-slate-50 p-4 rounded-2xl border border-slate-100">
              <input
                id="dry-run"
                type="checkbox"
                checked={dryRun}
                onChange={(e) => setDryRun(e.target.checked)}
                className="w-5 h-5 rounded border-slate-300 text-[#0EA5E9] focus:ring-[#0EA5E9]"
              />
              <div className="text-sm">
                <label htmlFor="dry-run" className="font-bold text-slate-700 cursor-pointer">Dry Run Validation Only</label>
                <p className="text-slate-400 font-medium text-xs mt-0.5">Parse, check validation rules and enums without writing records to database.</p>
              </div>
            </div>

            <button
              type="submit"
              disabled={uploading}
              className="w-full py-4 bg-[#0EA5E9] hover:bg-[#0284c7] text-white rounded-2xl font-black uppercase tracking-widest transition-all disabled:opacity-50"
            >
              {uploading ? 'Processing Batch...' : 'Start Import Job'}
            </button>
          </form>
        </div>

        {/* Template Downloads */}
        <div className="bg-white border-2 border-slate-200 rounded-3xl p-8 shadow-sm space-y-6">
          <h3 className="text-xl font-bold">Templates</h3>
          <p className="text-slate-400 font-medium text-sm">Download baseline templates to prepare your asset lists with correct fields and formats.</p>
          <div className="flex flex-col gap-3">
            <button
              onClick={() => downloadTemplate('excel')}
              className="w-full py-3 border-2 border-slate-100 hover:border-[#0EA5E9] rounded-2xl font-bold text-sm text-slate-700 transition-all flex items-center justify-center gap-2"
            >
              Excel Template (.xlsx)
            </button>
            <button
              onClick={() => downloadTemplate('csv')}
              className="w-full py-3 border-2 border-slate-100 hover:border-[#0EA5E9] rounded-2xl font-bold text-sm text-slate-700 transition-all flex items-center justify-center gap-2"
            >
              CSV Template (.csv)
            </button>
          </div>
        </div>
      </div>

      {/* History logs */}
      <div className="space-y-4">
        <h3 className="text-xl font-bold">Import Job History</h3>
        {loadingLogs ? (
          <div className="flex justify-center py-6">
            <div className="w-6 h-6 border-4 border-slate-200 border-t-[#0EA5E9] rounded-full animate-spin" />
          </div>
        ) : (
          <div className="bg-white border-2 border-slate-200 rounded-3xl overflow-hidden shadow-sm">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="border-b-2 border-slate-100 text-[10px] uppercase tracking-widest font-black text-slate-400">
                  <th className="px-8 py-5">Job ID</th>
                  <th className="px-8 py-5">File</th>
                  <th className="px-8 py-5">Size</th>
                  <th className="px-8 py-5">Status</th>
                  <th className="px-8 py-5">Summary</th>
                  <th className="px-8 py-5 text-right">Acquired At</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-100">
                {logs.map(log => (
                  <tr key={log.id} className="group hover:bg-slate-50 transition-colors">
                    <td className="px-8 py-6 font-mono text-xs text-[#0EA5E9] font-bold">#{log.id}</td>
                    <td className="px-8 py-6 font-bold text-slate-900">{log.fileName}</td>
                    <td className="px-8 py-6 font-semibold text-slate-500">{(log.fileSize / 1024).toFixed(1)} KB</td>
                    <td className="px-8 py-6">
                      <span className={`px-3 py-1 rounded-full text-[10px] font-black uppercase tracking-widest ${
                        log.status === 'COMPLETED' ? 'bg-emerald-50 text-emerald-600' :
                        log.status === 'FAILED' ? 'bg-red-50 text-red-600' : 'bg-slate-100 text-slate-500'
                      }`}>
                        {log.status}
                      </span>
                    </td>
                    <td className="px-8 py-6 font-medium text-slate-600 text-xs">
                      {log.status === 'COMPLETED' ? (
                        <span className="text-slate-600">Created: <b>{log.processedCount}</b> / Errors: <b>{log.errorCount}</b></span>
                      ) : log.status === 'FAILED' ? (
                        <span className="text-red-500">{log.errorMessage || 'Job failed'}</span>
                      ) : (
                        <span>Processing...</span>
                      )}
                    </td>
                    <td className="px-8 py-6 text-right text-slate-400 font-semibold text-xs">
                      {new Date(log.startedAt).toLocaleString()}
                    </td>
                  </tr>
                ))}
                {logs.length === 0 && (
                  <tr>
                    <td colSpan="6" className="text-center py-8 text-slate-400 font-bold">
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
