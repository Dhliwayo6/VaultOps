import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { useAssets } from './hooks/useAssets';
import { Assignment, ConditionStatus, Usage } from '@constants/assets';
import { useAuth } from '@context/AuthContext';
import { createAsset, updateAsset, deleteAsset } from '@api/assetsApi';
import { getLocations } from '@api/locationsApi';
import Loading from '@components/Loading';
import ErrorState from '@components/ErrorState';
import { useFocusTrap } from '@hooks/useFocusTrap';

export default function Assets() {
  const { user } = useAuth();
  const [searchParams, setSearchParams] = useSearchParams();
  const {
    assets: filteredAssets,
    isLoading,
    error,
    page,
    setPage,
    sortBy,
    setSortBy,
    direction,
    setDirection,
    totalPages,
    totalElements,
    searchTerm,
    setSearchTerm,
    conditionFilter,
    setConditionFilter,
    usageFilter,
    setUsageFilter,
    assignmentFilter,
    setAssignmentFilter,
    locationFilter,
    setLocationFilter,
    warrantyExpiringFilter,
    setWarrantyExpiringFilter,
    refetch
  } = useAssets();

  const [locations, setLocations] = useState([]);

  useEffect(() => {
    getLocations()
      .then(data => setLocations(data || []))
      .catch(err => console.error("Failed to load locations", err));
  }, []);

  // Modal form state
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingAsset, setEditingAsset] = useState(null);
  const modalRef = useFocusTrap(isModalOpen, () => setIsModalOpen(false));
  
  const [formName, setFormName] = useState('');
  const [formType, setFormType] = useState('');
  const [formLocation, setFormLocation] = useState('');
  const [formSerialNumber, setFormSerialNumber] = useState('');
  const [formPurchasePrice, setFormPurchasePrice] = useState('');
  const [formPurchaseDate, setFormPurchaseDate] = useState('');
  const [formConditionStatus, setFormConditionStatus] = useState(ConditionStatus.EXCELLENT);
  const [formUsageStatus, setFormUsageStatus] = useState(Usage.STORAGE);
  const [formAssignment, setFormAssignment] = useState(Assignment.UNASSIGNED);
  const [formAssignedTo, setFormAssignedTo] = useState('');
  
  const [formError, setFormError] = useState('');
  const [isSaving, setIsSaving] = useState(false);

  // Sync sorting direction
  const handleSortChange = (newSortBy) => {
    if (sortBy === newSortBy) {
      setDirection(prev => prev === 'ASC' ? 'DESC' : 'ASC');
    } else {
      setSortBy(newSortBy);
      setDirection('DESC');
    }
  };

  // Reset form
  const resetForm = () => {
    setFormName('');
    setFormType('');
    setFormLocation('1');
    setFormSerialNumber('');
    setFormPurchasePrice('');
    setFormPurchaseDate('');
    setFormConditionStatus(ConditionStatus.EXCELLENT);
    setFormUsageStatus(Usage.STORAGE);
    setFormAssignment(Assignment.UNASSIGNED);
    setFormAssignedTo('');
    setFormError('');
  };

  // Open modal for adding
  const handleOpenAddModal = () => {
    setEditingAsset(null);
    resetForm();
    setIsModalOpen(true);
  };

  // Open modal for editing
  const handleOpenEditModal = (asset) => {
    setEditingAsset(asset);
    setFormName(asset.name || '');
    setFormType(asset.type || '');
    setFormLocation(asset.location ? asset.location.id.toString() : '1');
    setFormSerialNumber(asset.serialNumber || '');
    setFormPurchasePrice(asset.purchasePrice !== null && asset.purchasePrice !== undefined ? asset.purchasePrice.toString() : '');
    setFormPurchaseDate(asset.purchaseDate || '');
    setFormConditionStatus(asset.conditionStatus || ConditionStatus.EXCELLENT);
    setFormUsageStatus(asset.usageStatus || Usage.STORAGE);
    setFormAssignment(asset.assignment || Assignment.UNASSIGNED);
    setFormAssignedTo(asset.assignedTo || '');
    setFormError('');
    setIsModalOpen(true);
  };

  useEffect(() => {
    if (searchParams.get('create') === 'true') {
      handleOpenAddModal();
      const newParams = new URLSearchParams(searchParams);
      newParams.delete('create');
      setSearchParams(newParams);
    }
    const cond = searchParams.get('condition');
    if (cond) {
      setConditionFilter(cond);
    }
    const usage = searchParams.get('usage');
    if (usage) {
      setUsageFilter(usage);
    }
    const warranty = searchParams.get('warrantyExpiring');
    if (warranty === 'true') {
      setWarrantyExpiringFilter(true);
    }
  }, [searchParams, setSearchParams, setConditionFilter, setUsageFilter, setWarrantyExpiringFilter]);

  // Handle Form submit
  const handleSaveAssetAsync = async (e) => {
    e.preventDefault();
    if (!formName.trim() || !formType.trim() || !formLocation.trim()) {
      setFormError('Please fill in Name, Type, and Location.');
      return;
    }

    setIsSaving(true);
    setFormError('');

    const payload = {
      name: formName.trim(),
      type: formType.trim(),
      location: { id: parseInt(formLocation, 10) },
      serialNumber: formSerialNumber.trim() || null,
      purchasePrice: formPurchasePrice.trim() ? parseFloat(formPurchasePrice) : null,
      purchaseDate: formPurchaseDate.trim() || null,
      conditionStatus: formConditionStatus,
      usageStatus: formUsageStatus,
      assignment: formAssignment,
      assignedTo: formAssignment === Assignment.ASSIGNED ? formAssignedTo.trim() : null
    };

    try {
      if (editingAsset) {
        await updateAsset(editingAsset.id, payload);
      } else {
        await createAsset(payload);
      }
      setIsModalOpen(false);
      resetForm();
      refetch();
    } catch (err) {
      console.error(err);
      setFormError(err.message || 'Failed to save asset. Check fields or serial number uniqueness.');
    } finally {
      setIsSaving(false);
    }
  };

  // Handle Delete asset
  const handleDeleteAssetAsync = async (id) => {
    if (!window.confirm("Are you sure you want to delete this asset? This action is permanent.")) return;

    try {
      await deleteAsset(id);
      refetch();
    } catch (err) {
      console.error(err);
      alert(err.message || 'Failed to delete asset.');
    }
  };

  return (
    <section className="w-full min-h-full flex flex-col gap-8 animate-in fade-in duration-500">
      {/* Header */}
      <div className="w-full flex flex-col sm:flex-row justify-between items-start sm:items-end gap-4">
        <div>
          <h2 className="text-3xl md:text-4xl font-black tracking-tight text-text-primary font-display">
            All Assets
          </h2>
          <p className="text-text-secondary font-medium mt-1">Manage and track your vault inventory</p>
        </div>
        
        <div className="flex gap-4 items-center w-full sm:w-auto">
          {/* Sort Buttons */}
          <div className="flex gap-2 bg-surface-elevated border border-border-token p-1 rounded-xl shadow-xs">
            <button 
              onClick={() => handleSortChange(sortBy === 'createdAt' || sortBy === 'date' ? 'createdAt' : 'createdAt')}
              className={`px-4 py-2 rounded-lg text-xs font-black uppercase tracking-wider transition-all flex items-center gap-1 cursor-pointer focus-visible:ring-2 focus-visible:ring-accent focus-visible:outline-none ${
                sortBy === 'createdAt' || sortBy === 'date' ? 'bg-accent text-white shadow-xs' : 'text-text-secondary hover:text-text-primary'
              }`}
            >
              Date {sortBy === 'createdAt' && (direction === 'ASC' ? '↑' : '↓')}
            </button>
            <button 
              onClick={() => handleSortChange('name')}
              className={`px-4 py-2 rounded-lg text-xs font-black uppercase tracking-wider transition-all flex items-center gap-1 cursor-pointer focus-visible:ring-2 focus-visible:ring-accent focus-visible:outline-none ${
                sortBy === 'name' ? 'bg-accent text-white shadow-xs' : 'text-text-secondary hover:text-text-primary'
              }`}
            >
              Name {sortBy === 'name' && (direction === 'ASC' ? '↑' : '↓')}
            </button>
          </div>

          {/* Add Asset Button (Admins only) */}
          {user?.role === 'ADMIN' && (
            <button
              onClick={handleOpenAddModal}
              className="px-6 py-3 bg-accent hover:bg-accent-hover text-white rounded-xl font-black text-xs uppercase tracking-widest transition-all active:scale-[0.98] shadow-glow ml-auto sm:ml-0 cursor-pointer focus-visible:ring-2 focus-visible:ring-accent focus-visible:outline-none focus-visible:ring-offset-2 dark:focus-visible:ring-offset-bg-base"
            >
              Add Asset
            </button>
          )}
        </div>

      </div>

      {/* Filter and Search controls */}
      <div className="flex flex-col lg:flex-row gap-4 items-stretch lg:items-center justify-between bg-surface-elevated border border-border-token p-4 rounded-card shadow-elevation">
        <div className="flex flex-col sm:flex-row gap-4 items-stretch sm:items-center flex-1">
          {/* Search Box */}
          <div className="relative flex-1 max-w-md">
            <label htmlFor="search-assets" className="sr-only">Search assets by name</label>
            <input
              id="search-assets"
              type="text"
              placeholder="Search assets by name..."
              value={searchTerm}
              onChange={e => setSearchTerm(e.target.value)}
              className="w-full h-11 pl-4 pr-10 bg-bg-base border border-border-token rounded-xl focus:border-accent focus:bg-surface-elevated focus:outline-none focus-visible:ring-2 focus-visible:ring-accent font-semibold text-sm transition-all text-text-primary"
            />
            {searchTerm && (
              <button 
                onClick={() => setSearchTerm('')}
                className="absolute right-3 top-3 text-text-secondary hover:text-text-primary font-black text-xs cursor-pointer focus-visible:ring-2 focus-visible:ring-accent focus-visible:outline-none rounded"
                aria-label="Clear search"
              >
                ✕
              </button>
            )}
          </div>

          {/* Filters Selects */}
          <div className="flex flex-wrap gap-2">
            <label htmlFor="filter-condition" className="sr-only">Filter by Condition</label>
            <select
              id="filter-condition"
              value={conditionFilter}
              onChange={e => setConditionFilter(e.target.value)}
              className="h-11 px-4 bg-bg-base border border-border-token rounded-xl focus:border-accent focus:bg-surface-elevated focus:outline-none focus-visible:ring-2 focus-visible:ring-accent text-xs font-black uppercase tracking-wider text-text-secondary"
            >
              <option value="">All Conditions</option>
              {Object.values(ConditionStatus).map(c => (
                <option key={c} value={c}>{c}</option>
              ))}
            </select>

            <label htmlFor="filter-usage" className="sr-only">Filter by Usage Status</label>
            <select
              id="filter-usage"
              value={usageFilter}
              onChange={e => setUsageFilter(e.target.value)}
              className="h-11 px-4 bg-bg-base border border-border-token rounded-xl focus:border-accent focus:bg-surface-elevated focus:outline-none focus-visible:ring-2 focus-visible:ring-accent text-xs font-black uppercase tracking-wider text-text-secondary"
            >
              <option value="">All Usage Statuses</option>
              {Object.values(Usage).map(u => (
                <option key={u} value={u}>{u.replace('_', ' ')}</option>
              ))}
            </select>

            <label htmlFor="filter-assignment" className="sr-only">Filter by Assignment</label>
            <select
              id="filter-assignment"
              value={assignmentFilter}
              onChange={e => setAssignmentFilter(e.target.value)}
              className="h-11 px-4 bg-bg-base border border-border-token rounded-xl focus:border-accent focus:bg-surface-elevated focus:outline-none focus-visible:ring-2 focus-visible:ring-accent text-xs font-black uppercase tracking-wider text-text-secondary"
            >
              <option value="">All Assignments</option>
              {Object.values(Assignment).map(a => (
                <option key={a} value={a}>{a}</option>
              ))}
            </select>

            <label htmlFor="filter-location" className="sr-only">Filter by Location</label>
            <select
              id="filter-location"
              value={locationFilter}
              onChange={e => setLocationFilter(e.target.value)}
              className="h-11 px-4 bg-bg-base border border-border-token rounded-xl focus:border-accent focus:bg-surface-elevated focus:outline-none focus-visible:ring-2 focus-visible:ring-accent text-xs font-black uppercase tracking-wider text-text-secondary"
            >
              <option value="">All Locations</option>
              {locations.map(loc => (
                <option key={loc.id} value={loc.id}>{loc.name}</option>
              ))}
            </select>
          </div>
        </div>
      </div>

      {/* Main Content Area */}
      {isLoading ? (
        <Loading message="Acquiring secure inventory list..." />
      ) : error ? (
        <ErrorState title="Inventory Fetch Error" message={error} onRetry={refetch} />
      ) : filteredAssets.length === 0 ? (
        <div className="bg-surface-elevated border border-border-token border-dashed rounded-card p-12 text-center shadow-elevation">
          <p className="text-text-secondary font-bold text-sm uppercase tracking-widest">No assets found</p>
          <p className="text-text-secondary/60 text-xs mt-1">Try adjusting your filters or search terms</p>
        </div>
      ) : (
        <div className="space-y-8">
          {/* Asset Inventory Registry */}
          <ul className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6" aria-label="Asset Inventory Registry">
            {filteredAssets.map(asset => {
              const {
                id,
                name,
                type,
                location,
                assignment,
                serialNumber,
                purchasePrice,
                purchaseDate,
                conditionStatus,
                usageStatus,
                assignedTo,
              } = asset;

              return (
                <li 
                  className="bg-surface-elevated border border-border-token rounded-card p-8 flex flex-col gap-6 transition-all duration-300 hover:-translate-y-1 hover:border-accent/40 shadow-elevation hover:shadow-glow" 
                  key={id}
                >
                  {/* Card Header */}
                  <div className="w-full flex items-center justify-between">
                    <h2 className="text-accent font-black font-mono text-sm tracking-tighter">
                      VO-{id.toString().padStart(4, '0')}
                    </h2>

                    <span className={`px-3 py-1 rounded-full text-[10px] font-black uppercase tracking-widest ${
                      usageStatus === Usage.IN_USE ? 'bg-accent/15 text-accent' :
                      usageStatus === Usage.SERVICE ? 'bg-amber-500/10 text-amber-800 dark:text-amber-400' :
                      'bg-bg-base text-text-secondary border border-border-token'
                    }`}>
                      {usageStatus.replace('_', ' ')}
                    </span>
                  </div>

                  {/* Asset Info */}
                  <div className="space-y-1">
                    <h2 className="text-2xl font-black text-text-primary leading-tight">
                      {name}
                    </h2>

                    <div>
                      <div className="flex items-center gap-2 font-bold text-text-secondary text-sm">
                        <p>{type}</p>
                        <span className="w-1.5 h-1.5 bg-border-token rounded-full"></span>
                        <p>{location?.name || 'Unassigned'}</p>
                      </div>
                      <h2 className="text-xs font-mono text-text-secondary/60 mt-1 uppercase tracking-widest">
                        SN: {serialNumber || 'N/A'}
                      </h2>
                    </div>
                  </div>

                  {/* Assigned to Details */}
                  {assignment === Assignment.ASSIGNED ? (
                    <div className="bg-bg-base p-4 rounded-2xl border border-border-token">
                      <p className="text-[10px] font-black text-text-secondary uppercase tracking-widest mb-1">Assigned To</p>
                      <h2 className="font-bold text-text-primary flex items-center gap-2">
                        <div className="w-2 h-2 bg-emerald-400 rounded-full"></div>
                        {assignedTo || 'Unspecified User'}
                      </h2>
                    </div>
                  ) : (
                    <div className="bg-bg-base p-4 rounded-2xl border border-dashed border-border-token">
                      <p className="text-xs font-bold text-text-secondary italic">Available for assignment</p>
                    </div>
                  )}

                  {/* Price and Acquired Date Footer */}
                  <div className="mt-auto pt-4 border-t border-border-token flex flex-col gap-4">
                    <div className="w-full flex items-end justify-between">
                      <div>
                        <h2 className="text-2xl font-black text-text-primary">
                          R {(purchasePrice || 0).toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
                        </h2>
                        <p className="text-[10px] font-bold text-text-secondary uppercase tracking-tight">
                          Acquired: {purchaseDate ? new Date(purchaseDate).toLocaleDateString() : 'N/A'}
                        </p>
                      </div>

                      {/* Condition Status Indicator */}
                      <div className={`px-3 py-1 rounded-lg border font-black text-[10px] uppercase tracking-tighter ${
                        conditionStatus === ConditionStatus.EXCELLENT ? 'border-emerald-500/20 text-emerald-700 dark:text-emerald-400 bg-emerald-500/10' :
                        conditionStatus === ConditionStatus.GOOD ? 'border-accent/20 text-accent bg-accent/10' :
                        conditionStatus === ConditionStatus.FAIR ? 'border-amber-500/20 text-amber-800 dark:text-amber-400 bg-amber-500/10' :
                        'border-red-500/20 text-red-700 dark:text-red-400 bg-red-500/10'
                      }`}>
                        {conditionStatus}
                      </div>
                    </div>

                    {/* Admin Action Buttons */}
                    {user?.role === 'ADMIN' && (
                      <div className="flex gap-2 w-full">
                        <button
                          onClick={() => handleOpenEditModal(asset)}
                          className="flex-1 py-2 bg-bg-base border border-border-token hover:bg-surface-elevated text-text-primary rounded-xl font-black text-[10px] uppercase tracking-wider transition-all cursor-pointer focus-visible:ring-2 focus-visible:ring-accent focus-visible:outline-none"
                          aria-label={`Edit asset ${name}`}
                        >
                          Edit
                        </button>
                        <button
                          onClick={() => handleDeleteAssetAsync(id)}
                          className="flex-1 py-2 bg-red-500/10 border border-red-500/20 hover:bg-red-500/20 text-red-700 dark:text-red-400 rounded-xl font-black text-[10px] uppercase tracking-wider transition-all cursor-pointer focus-visible:ring-2 focus-visible:ring-red-500 focus-visible:outline-none"
                          aria-label={`Delete asset ${name}`}
                        >
                          Delete
                        </button>
                      </div>
                    )}
                  </div>
                </li>
              );
            })}
          </ul>

          {/* Pagination Controls */}
          {!searchTerm.trim() && totalPages > 1 && (
            <div className="w-full flex flex-col sm:flex-row items-center justify-between border-t border-border-token pt-6 gap-4">
              <p className="text-sm font-bold text-text-secondary">
                Showing {filteredAssets.length} of {totalElements} Assets
              </p>
              <div className="flex gap-2">
                <button
                  disabled={page === 0}
                  onClick={() => setPage(page - 1)}
                  className="px-4 py-2 border border-border-token rounded-xl text-xs font-black uppercase tracking-wider text-text-secondary disabled:opacity-50 disabled:cursor-not-allowed hover:bg-bg-base transition-all cursor-pointer focus-visible:ring-2 focus-visible:ring-accent focus-visible:outline-none"
                >
                  Previous
                </button>
                <div className="flex items-center px-4 bg-bg-base rounded-xl border border-border-token">
                  <span className="text-xs font-black text-text-primary">Page {page + 1} of {totalPages}</span>
                </div>
                <button
                  disabled={page >= totalPages - 1}
                  onClick={() => setPage(page + 1)}
                  className="px-4 py-2 border border-border-token rounded-xl text-xs font-black uppercase tracking-wider text-text-secondary disabled:opacity-50 disabled:cursor-not-allowed hover:bg-bg-base transition-all cursor-pointer focus-visible:ring-2 focus-visible:ring-accent focus-visible:outline-none"
                >
                  Next
                </button>
              </div>
            </div>
          )}
        </div>
      )}

      {/* Add/Edit Asset Modal Dialog */}
      {isModalOpen && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-900/60 backdrop-blur-xs p-4 overflow-y-auto animate-in fade-in duration-200">
          <div ref={modalRef} className="bg-surface-elevated border border-border-token rounded-card p-8 shadow-2xl w-full max-w-2xl max-h-[90vh] overflow-y-auto flex flex-col relative animate-in zoom-in-95 duration-200">
            {/* Close Button */}
            <button 
              onClick={() => setIsModalOpen(false)} 
              className="absolute right-6 top-6 text-text-secondary hover:text-text-primary font-black text-lg transition-colors cursor-pointer focus-visible:ring-2 focus-visible:ring-accent focus-visible:outline-none rounded-lg p-1"
              aria-label="Close modal"
            >
              ✕
            </button>

            {/* Modal Title */}
            <div className="mb-6">
              <h3 className="text-2xl font-black text-text-primary font-display">
                {editingAsset ? 'Edit Asset Parameters' : 'Register New Asset'}
              </h3>
              <p className="text-text-secondary text-xs font-bold uppercase mt-1 tracking-widest">
                {editingAsset ? `ID: VO-${editingAsset.id.toString().padStart(4, '0')}` : 'Establish vault record'}
              </p>
            </div>

            {/* Error Notification */}
            {formError && (
              <div role="alert" className="mb-6 p-4 bg-red-500/10 border border-red-500/20 rounded-2xl text-xs font-semibold text-red-500 text-center">
                {formError}
              </div>
            )}

            {/* Modal Form */}
            <form onSubmit={handleSaveAssetAsync} className="space-y-6 flex-1">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {/* Name */}
                <div className="space-y-2">
                  <label htmlFor="form-asset-name" className="text-[10px] font-black uppercase tracking-widest text-text-secondary px-1">Asset Name *</label>
                  <input
                    id="form-asset-name"
                    type="text"
                    required
                    aria-required="true"
                    placeholder="e.g. Server Node A"
                    value={formName}
                    onChange={e => setFormName(e.target.value)}
                    className="w-full h-11 px-4 bg-bg-base border border-border-token rounded-xl focus:border-accent focus:bg-surface-elevated focus:outline-none focus-visible:ring-2 focus-visible:ring-accent font-semibold text-sm transition-all text-text-primary"
                  />
                </div>

                {/* Type */}
                <div className="space-y-2">
                  <label htmlFor="form-asset-type" className="text-[10px] font-black uppercase tracking-widest text-text-secondary px-1">Asset Type *</label>
                  <input
                    id="form-asset-type"
                    type="text"
                    required
                    aria-required="true"
                    placeholder="e.g. Hardware, License"
                    value={formType}
                    onChange={e => setFormType(e.target.value)}
                    className="w-full h-11 px-4 bg-bg-base border border-border-token rounded-xl focus:border-accent focus:bg-surface-elevated focus:outline-none focus-visible:ring-2 focus-visible:ring-accent font-semibold text-sm transition-all text-text-primary"
                  />
                </div>

                {/* Location */}
                <div className="space-y-2">
                  <label htmlFor="form-asset-location" className="text-[10px] font-black uppercase tracking-widest text-text-secondary px-1">Location *</label>
                  <select
                    id="form-asset-location"
                    required
                    aria-required="true"
                    value={formLocation}
                    onChange={e => setFormLocation(e.target.value)}
                    className="w-full h-11 px-4 bg-bg-base border border-border-token rounded-xl focus:border-accent focus:bg-surface-elevated focus:outline-none focus-visible:ring-2 focus-visible:ring-accent font-semibold text-sm transition-all text-text-primary"
                  >
                    <option value="" disabled>Select a location</option>
                    {locations.map(loc => (
                      <option key={loc.id} value={loc.id}>{loc.name}</option>
                    ))}
                  </select>
                </div>

                {/* Serial Number */}
                <div className="space-y-2">
                  <label htmlFor="form-asset-serial" className="text-[10px] font-black uppercase tracking-widest text-text-secondary px-1">Serial Number</label>
                  <input
                    id="form-asset-serial"
                    type="text"
                    placeholder="e.g. SN-998273-X"
                    value={formSerialNumber}
                    onChange={e => setFormSerialNumber(e.target.value)}
                    className="w-full h-11 px-4 bg-bg-base border border-border-token rounded-xl focus:border-accent focus:bg-surface-elevated focus:outline-none focus-visible:ring-2 focus-visible:ring-accent font-semibold text-sm transition-all text-text-primary"
                  />
                </div>

                {/* Price */}
                <div className="space-y-2">
                  <label htmlFor="form-asset-price" className="text-[10px] font-black uppercase tracking-widest text-text-secondary px-1">Purchase Price (R)</label>
                  <input
                    id="form-asset-price"
                    type="number"
                    step="0.01"
                    placeholder="e.g. 15999.99"
                    value={formPurchasePrice}
                    onChange={e => setFormPurchasePrice(e.target.value)}
                    className="w-full h-11 px-4 bg-bg-base border border-border-token rounded-xl focus:border-accent focus:bg-surface-elevated focus:outline-none focus-visible:ring-2 focus-visible:ring-accent font-semibold text-sm transition-all text-text-primary"
                  />
                </div>

                {/* Purchase Date */}
                <div className="space-y-2">
                  <label htmlFor="form-asset-date" className="text-[10px] font-black uppercase tracking-widest text-text-secondary px-1">Acquired Date</label>
                  <input
                    id="form-asset-date"
                    type="date"
                    value={formPurchaseDate}
                    onChange={e => setFormPurchaseDate(e.target.value)}
                    className="w-full h-11 px-4 bg-bg-base border border-border-token rounded-xl focus:border-accent focus:bg-surface-elevated focus:outline-none focus-visible:ring-2 focus-visible:ring-accent font-semibold text-sm text-text-secondary transition-all"
                  />
                </div>

                {/* Condition Status */}
                <div className="space-y-2">
                  <label htmlFor="form-asset-condition" className="text-[10px] font-black uppercase tracking-widest text-text-secondary px-1">Condition Status *</label>
                  <select
                    id="form-asset-condition"
                    value={formConditionStatus}
                    onChange={e => setFormConditionStatus(e.target.value)}
                    className="w-full h-11 px-4 bg-bg-base border border-border-token rounded-xl focus:border-accent focus:bg-surface-elevated focus:outline-none focus-visible:ring-2 focus-visible:ring-accent font-semibold text-sm text-text-primary transition-all"
                  >
                    {Object.values(ConditionStatus).map(c => (
                      <option key={c} value={c}>{c}</option>
                    ))}
                  </select>
                </div>

                {/* Usage Status */}
                <div className="space-y-2">
                  <label htmlFor="form-asset-usage" className="text-[10px] font-black uppercase tracking-widest text-text-secondary px-1">Usage Status *</label>
                  <select
                    id="form-asset-usage"
                    value={formUsageStatus}
                    onChange={e => setFormUsageStatus(e.target.value)}
                    className="w-full h-11 px-4 bg-bg-base border border-border-token rounded-xl focus:border-accent focus:bg-surface-elevated focus:outline-none focus-visible:ring-2 focus-visible:ring-accent font-semibold text-sm text-text-primary transition-all"
                  >
                    {Object.values(Usage).map(u => (
                      <option key={u} value={u}>{u.replace('_', ' ')}</option>
                    ))}
                  </select>
                </div>

                {/* Assignment Status */}
                <div className="space-y-2">
                  <label htmlFor="form-asset-assignment" className="text-[10px] font-black uppercase tracking-widest text-text-secondary px-1">Assignment status *</label>
                  <select
                    id="form-asset-assignment"
                    value={formAssignment}
                    onChange={e => {
                      setFormAssignment(e.target.value);
                      if (e.target.value === Assignment.UNASSIGNED) {
                        setFormAssignedTo('');
                      }
                    }}
                    className="w-full h-11 px-4 bg-bg-base border border-border-token rounded-xl focus:border-accent focus:bg-surface-elevated focus:outline-none focus-visible:ring-2 focus-visible:ring-accent font-semibold text-sm text-text-primary transition-all"
                  >
                    {Object.values(Assignment).map(a => (
                      <option key={a} value={a}>{a}</option>
                    ))}
                  </select>
                </div>

                {/* Assigned To */}
                <div className="space-y-2">
                  <label htmlFor="form-asset-assigned-to" className="text-[10px] font-black uppercase tracking-widest text-text-secondary px-1">Assigned To User</label>
                  <input
                    id="form-asset-assigned-to"
                    type="text"
                    disabled={formAssignment === Assignment.UNASSIGNED}
                    placeholder="e.g. John Doe"
                    value={formAssignedTo}
                    onChange={e => setFormAssignedTo(e.target.value)}
                    className="w-full h-11 px-4 bg-bg-base border border-border-token rounded-xl focus:border-accent focus:bg-surface-elevated focus:outline-none focus-visible:ring-2 focus-visible:ring-accent font-semibold text-sm transition-all disabled:opacity-50 disabled:cursor-not-allowed text-text-primary"
                  />
                </div>
              </div>

              {/* Action Buttons */}
              <div className="flex gap-4 pt-4 border-t border-border-token">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  disabled={isSaving}
                  className="flex-1 py-4 border border-border-token hover:bg-bg-base text-text-secondary rounded-2xl font-black text-sm uppercase tracking-widest transition-all cursor-pointer focus-visible:ring-2 focus-visible:ring-accent focus-visible:outline-none"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={isSaving}
                  className="flex-1 py-4 bg-accent hover:bg-accent-hover text-white rounded-2xl font-black text-sm uppercase tracking-widest transition-all flex items-center justify-center gap-2 cursor-pointer shadow-glow focus-visible:ring-2 focus-visible:ring-accent focus-visible:outline-none focus-visible:ring-offset-2 dark:focus-visible:ring-offset-bg-base"
                >
                  {isSaving ? (
                    <>
                      <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                      Saving...
                    </>
                  ) : (
                    'Commit Record'
                  )}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </section>
  );
}
