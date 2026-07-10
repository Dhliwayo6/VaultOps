import React, { useState, useEffect, useCallback } from 'react';
import { getAuditLogs } from '@api/auditApi';

const ACTION_TYPES = [
  { value: '', label: 'All Actions' },
  { value: 'CREATE_ASSET', label: 'Create Asset' },
  { value: 'UPDATE_ASSET', label: 'Update Asset' },
  { value: 'DELETE_ASSET', label: 'Delete Asset' },
  { value: 'CREATE_MAINTENANCE', label: 'Create Maintenance' },
  { value: 'UPDATE_MAINTENANCE', label: 'Update Maintenance' },
  { value: 'DELETE_MAINTENANCE', label: 'Delete Maintenance' },
  { value: 'CREATE_MIGRATION', label: 'Create Migration' },
  { value: 'UPDATE_MIGRATION', label: 'Update Migration' },
  { value: 'DELETE_MIGRATION', label: 'Delete Migration' },
  { value: 'IMPORT_COMPLETED', label: 'Import Completed' },
  { value: 'IMPORT_FAILED', label: 'Import Failed' },
  { value: 'OTP_VERIFICATION_SUCCESS', label: 'OTP Success' },
  { value: 'OTP_VERIFICATION_FAILURE', label: 'OTP Failure' },
  { value: 'LOGIN_SUCCESS', label: 'Login Success' },
  { value: 'LOGIN_FAILURE', label: 'Login Failure' },
  { value: 'REGISTER_USER', label: 'Register User' }
];

const getBadgeStyle = (action) => {
  const isDelete = action.includes('DELETE');
  const isFailure = action.includes('FAILURE') || action.includes('FAILED');
  const isCreate = action.includes('CREATE') || action.includes('SUCCESS') || action.includes('REGISTER');
  
  if (isDelete || isFailure) {
    return 'bg-red-500/10 text-red-600 dark:text-red-400 border border-red-500/20';
  }
  if (isCreate) {
    return 'bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 border border-emerald-500/20';
  }
  return 'bg-amber-500/10 text-amber-600 dark:text-amber-400 border border-amber-500/20';
};

const formatTimestamp = (isoString) => {
  if (!isoString) return '';
  const date = new Date(isoString);
  return date.toLocaleString('en-US', {
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  });
};

const AdminActivityFeed = () => {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [polling, setPolling] = useState(false);
  const [error, setError] = useState(null);

  // Filters
  const [actionType, setActionType] = useState('');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  
  // Pagination
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const size = 10;

  const fetchLogs = useCallback(async (isPollingCall = false) => {
    if (!isPollingCall) setLoading(true);
    else setPolling(true);

    try {
      const data = await getAuditLogs({
        actionType,
        startDate,
        endDate,
        page,
        size
      });
      setLogs(data.content || []);
      setTotalPages(data.totalPages || 0);
      setError(null);
    } catch (err) {
      console.error('Failed to load audit logs:', err);
      // Avoid overwriting logs with error on background polling
      if (!isPollingCall) {
        setError(err.message || 'Failed to fetch audit log trail.');
      }
    } finally {
      setLoading(false);
      setPolling(false);
    }
  }, [actionType, startDate, endDate, page]);

  // Initial load and filter/page change triggers
  useEffect(() => {
    fetchLogs(false);
  }, [fetchLogs]);

  // Background Polling (every 30 seconds)
  useEffect(() => {
    const timer = setInterval(() => {
      fetchLogs(true);
    }, 30000);

    return () => clearInterval(timer);
  }, [fetchLogs]);

  const handleResetFilters = () => {
    setActionType('');
    setStartDate('');
    setEndDate('');
    setPage(0);
  };

  return (
    <section 
      id="admin-activity-feed-panel"
      className="bg-surface-elevated/80 backdrop-blur-md border border-border-token rounded-card shadow-elevation p-6 md:p-8 space-y-6"
      aria-label="Admin Activity Feed"
    >
      <div className="flex items-center justify-between">
        <div>
          <div className="flex items-center gap-2">
            <h3 className="text-xl font-bold text-text-primary">Admin Activity Feed</h3>
            {polling && (
              <span 
                className="w-2.5 h-2.5 bg-emerald-500 rounded-full animate-ping"
                title="Polling active updates..."
              />
            )}
          </div>
          <p className="text-xs text-text-secondary font-medium">Real-time system audit logs</p>
        </div>
        <button
          onClick={() => fetchLogs(false)}
          className="text-xs font-bold text-accent hover:underline cursor-pointer focus-visible:outline-none"
        >
          Refresh Feed
        </button>
      </div>

      {/* Filter Toolbar */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 bg-bg-base/40 p-4 rounded-2xl border border-border-token/50">
        <div className="flex flex-col gap-1">
          <label htmlFor="audit-action-type-select" className="text-[10px] font-black uppercase tracking-widest text-text-secondary">
            Action Type
          </label>
          <select
            id="audit-action-type-select"
            value={actionType}
            onChange={(e) => { setActionType(e.target.value); setPage(0); }}
            className="w-full bg-surface-elevated border border-border-token rounded-input px-3 py-2 text-sm text-text-primary focus:outline-none focus:border-accent"
          >
            {ACTION_TYPES.map(opt => (
              <option key={opt.value} value={opt.value}>{opt.label}</option>
            ))}
          </select>
        </div>

        <div className="flex flex-col gap-1">
          <label htmlFor="audit-start-date-input" className="text-[10px] font-black uppercase tracking-widest text-text-secondary">
            Start Date
          </label>
          <input
            type="date"
            id="audit-start-date-input"
            value={startDate}
            onChange={(e) => { setStartDate(e.target.value); setPage(0); }}
            className="w-full bg-surface-elevated border border-border-token rounded-input px-3 py-2 text-sm text-text-primary focus:outline-none focus:border-accent"
          />
        </div>

        <div className="flex flex-col gap-1">
          <label htmlFor="audit-end-date-input" className="text-[10px] font-black uppercase tracking-widest text-text-secondary">
            End Date
          </label>
          <div className="flex gap-2">
            <input
              type="date"
              id="audit-end-date-input"
              value={endDate}
              onChange={(e) => { setEndDate(e.target.value); setPage(0); }}
              className="w-full bg-surface-elevated border border-border-token rounded-input px-3 py-2 text-sm text-text-primary focus:outline-none focus:border-accent"
            />
            {(actionType || startDate || endDate) && (
              <button
                onClick={handleResetFilters}
                className="px-3 bg-red-500/10 hover:bg-red-500/20 text-red-500 border border-red-500/25 rounded-input text-xs font-bold transition-all cursor-pointer"
                title="Reset Filters"
              >
                Clear
              </button>
            )}
          </div>
        </div>
      </div>

      {/* Logs Feed List */}
      {error ? (
        <div className="p-4 bg-red-500/10 border border-red-500/20 rounded-2xl text-center">
          <p className="text-sm font-bold text-red-800 dark:text-red-400">{error}</p>
        </div>
      ) : loading ? (
        <div className="space-y-4 animate-pulse" aria-hidden="true">
          {[...Array(3)].map((_, i) => (
            <div key={i} className="h-16 bg-bg-base/60 border border-border-token rounded-2xl" />
          ))}
        </div>
      ) : logs.length === 0 ? (
        <div className="p-12 text-center border border-dashed border-border-token rounded-2xl">
          <p className="text-sm font-semibold text-text-secondary">No activity logs match the selected criteria.</p>
        </div>
      ) : (
        <div className="border border-border-token rounded-2xl overflow-hidden bg-bg-base/10 shadow-sm">
          <ul id="audit-logs-list" className="divide-y divide-border-token" aria-label="Audited Events">
            {logs.map((logItem) => (
              <li key={logItem.id} className="p-4 hover:bg-bg-base/30 transition-colors flex flex-col sm:flex-row sm:items-center justify-between gap-3">
                <div className="space-y-1.5 flex-1">
                  <div className="flex flex-wrap items-center gap-2">
                    <span className={`px-2 py-0.5 rounded-full text-[9px] font-black uppercase tracking-wider ${getBadgeStyle(logItem.actionType)}`}>
                      {logItem.actionType.replace(/_/g, ' ')}
                    </span>
                    <span className="text-[10px] font-mono font-semibold text-text-secondary bg-bg-base px-2 py-0.5 rounded">
                      User: {logItem.actingUser}
                    </span>
                  </div>
                  <p className="text-sm font-semibold text-text-primary leading-snug">
                    {logItem.description}
                  </p>
                  {logItem.resourceType && (
                    <p className="text-[10px] font-mono text-accent">
                      {logItem.resourceType} Ref: {logItem.resourceId || 'N/A'}
                    </p>
                  )}
                </div>
                <div className="text-left sm:text-right flex-shrink-0">
                  <span className="text-[11px] font-bold text-text-secondary">
                    {formatTimestamp(logItem.timestamp)}
                  </span>
                </div>
              </li>
            ))}
          </ul>
        </div>
      )}

      {/* Pagination Controls */}
      {totalPages > 1 && (
        <div className="flex items-center justify-between pt-2 border-t border-border-token/50">
          <button
            id="audit-prev-page-btn"
            disabled={page === 0}
            onClick={() => setPage(p => Math.max(0, p - 1))}
            className="px-4 py-2 border border-border-token rounded-input text-xs font-bold text-text-primary hover:bg-bg-base/50 disabled:opacity-50 disabled:cursor-not-allowed transition-all cursor-pointer focus:outline-none"
          >
            Previous
          </button>
          <span className="text-xs font-bold text-text-secondary">
            Page {page + 1} of {totalPages}
          </span>
          <button
            id="audit-next-page-btn"
            disabled={page >= totalPages - 1}
            onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))}
            className="px-4 py-2 border border-border-token rounded-input text-xs font-bold text-text-primary hover:bg-bg-base/50 disabled:opacity-50 disabled:cursor-not-allowed transition-all cursor-pointer focus:outline-none"
          >
            Next
          </button>
        </div>
      )}
    </section>
  );
};

export default AdminActivityFeed;
