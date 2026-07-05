import React, { useState, useEffect } from 'react';
import { useAssets } from './hooks/useAssets';
import { Assignment, ConditionStatus, Usage } from '@constants/assets';
import { useAuth } from '@context/AuthContext';
import { createAsset, updateAsset, deleteAsset } from '@api/assetsApi';
import Loading from '@components/Loading';
import ErrorState from '@components/ErrorState';

export default function Assets() {
  const { user } = useAuth();
  const {
    assets,
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
    refetch
  } = useAssets();

  // Modal form state
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingAsset, setEditingAsset] = useState(null);
  
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
    setFormLocation('');
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
    setFormLocation(asset.location || '');
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
      location: formLocation.trim(),
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
          <h2 className="text-4xl font-black tracking-tight text-slate-900">
            All Assets
          </h2>
          <p className="text-slate-500 font-medium mt-1">Manage and track your vault inventory</p>
        </div>
        
        <div className="flex gap-4 items-center w-full sm:w-auto">
          {/* Sort Buttons */}
          <div className="flex gap-2 bg-white border-2 border-slate-100 p-1 rounded-xl">
            <button 
              onClick={() => handleSortChange(sortBy === 'createdAt' || sortBy === 'date' ? 'createdAt' : 'createdAt')}
              className={`px-4 py-2 rounded-lg text-xs font-black uppercase tracking-wider transition-all flex items-center gap-1 ${
                sortBy === 'createdAt' || sortBy === 'date' ? 'bg-[#0EA5E9] text-white' : 'text-slate-400 hover:text-slate-600'
              }`}
            >
              Date {sortBy === 'createdAt' && (direction === 'ASC' ? '↑' : '↓')}
            </button>
            <button 
              onClick={() => handleSortChange('name')}
              className={`px-4 py-2 rounded-lg text-xs font-black uppercase tracking-wider transition-all flex items-center gap-1 ${
                sortBy === 'name' ? 'bg-[#0EA5E9] text-white' : 'text-slate-400 hover:text-slate-600'
              }`}
            >
              Name {sortBy === 'name' && (direction === 'ASC' ? '↑' : '↓')}
            </button>
          </div>

          {/* Add Asset Button (Admins only) */}
          {user?.role === 'ADMIN' && (
            <button
              onClick={handleOpenAddModal}
              className="px-6 py-3 bg-[#0EA5E9] hover:bg-[#0EA5E9]/90 text-white rounded-xl font-black text-xs uppercase tracking-widest transition-all active:scale-[0.98] shadow-lg shadow-blue-100 ml-auto sm:ml-0"
            >
              Add Asset
            </button>
          )}
        </div>
      </div>

      {/* Filter and Search controls */}
      <div className="flex flex-col lg:flex-row gap-4 items-stretch lg:items-center justify-between bg-white border-2 border-slate-100 p-4 rounded-3xl shadow-sm">
        <div className="flex flex-col sm:flex-row gap-4 items-stretch sm:items-center flex-1">
          {/* Search Box */}
          <div className="relative flex-1 max-w-md">
            <input
              type="text"
              placeholder="Search assets by name..."
              value={searchTerm}
              onChange={e => setSearchTerm(e.target.value)}
              className="w-full h-11 pl-4 pr-10 bg-slate-50 border-2 border-slate-100 rounded-xl focus:border-[#0EA5E9] focus:bg-white focus:outline-none font-semibold text-sm transition-all"
            />
            {searchTerm && (
              <button 
                onClick={() => setSearchTerm('')}
                className="absolute right-3 top-3 text-slate-400 hover:text-slate-600 font-black text-xs"
              >
                ✕
              </button>
            )}
          </div>

          {/* Filters Selects */}
          <div className="flex flex-wrap gap-2">
            <select
              value={conditionFilter}
              onChange={e => setConditionFilter(e.target.value)}
              className="h-11 px-4 bg-slate-50 border-2 border-slate-100 rounded-xl focus:border-[#0EA5E9] focus:bg-white focus:outline-none text-xs font-black uppercase tracking-wider text-slate-500"
            >
              <option value="">All Conditions</option>
              {Object.values(ConditionStatus).map(c => (
                <option key={c} value={c}>{c}</option>
              ))}
            </select>

            <select
              value={usageFilter}
              onChange={e => setUsageFilter(e.target.value)}
              className="h-11 px-4 bg-slate-50 border-2 border-slate-100 rounded-xl focus:border-[#0EA5E9] focus:bg-white focus:outline-none text-xs font-black uppercase tracking-wider text-slate-500"
            >
              <option value="">All Usage Statuses</option>
              {Object.values(Usage).map(u => (
                <option key={u} value={u}>{u.replace('_', ' ')}</option>
              ))}
            </select>

            <select
              value={assignmentFilter}
              onChange={e => setAssignmentFilter(e.target.value)}
              className="h-11 px-4 bg-slate-50 border-2 border-slate-100 rounded-xl focus:border-[#0EA5E9] focus:bg-white focus:outline-none text-xs font-black uppercase tracking-wider text-slate-500"
            >
              <option value="">All Assignments</option>
              {Object.values(Assignment).map(a => (
                <option key={a} value={a}>{a}</option>
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
        <div className="bg-white border-2 border-slate-100 border-dashed rounded-[2.5rem] p-12 text-center">
          <p className="text-slate-400 font-bold text-sm uppercase tracking-widest">No assets found</p>
          <p className="text-slate-300 text-xs mt-1">Try adjusting your filters or search terms</p>
        </div>
      ) : (
        <div className="space-y-8">
          {/* Asset Grid */}
          <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
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
                <div 
                  className="bg-white border-2 border-slate-100 rounded-[2.5rem] p-8 flex flex-col gap-6 transition-all hover:-translate-y-1 hover:border-[#0EA5E9]/30 shadow-sm hover:shadow-md" 
                  key={id}
                >
                  {/* Card Header */}
                  <div className="w-full flex items-center justify-between">
                    <h2 className="text-[#0EA5E9] font-black font-mono text-sm tracking-tighter">
                      VO-{id.toString().padStart(4, '0')}
                    </h2>

                    <span className={`px-3 py-1 rounded-full text-[10px] font-black uppercase tracking-widest ${
                      usageStatus === Usage.IN_USE ? 'bg-blue-50 text-[#0EA5E9]' :
                      usageStatus === Usage.SERVICE ? 'bg-amber-50 text-amber-600' :
                      'bg-slate-100 text-slate-500'
                    }`}>
                      {usageStatus.replace('_', ' ')}
                    </span>
                  </div>

                  {/* Asset Info */}
                  <div className="space-y-1">
                    <h2 className="text-2xl font-black text-slate-900 leading-tight">
                      {name}
                    </h2>

                    <div>
                      <div className="flex items-center gap-2 font-bold text-slate-400 text-sm">
                        <p>{type}</p>
                        <span className="w-1 h-1 bg-slate-300 rounded-full"></span>
                        <p>{location}</p>
                      </div>
                      <h2 className="text-xs font-mono text-slate-300 mt-1 uppercase tracking-widest">
                        SN: {serialNumber || 'N/A'}
                      </h2>
                    </div>
                  </div>

                  {/* Assigned to Details */}
                  {assignment === Assignment.ASSIGNED ? (
                    <div className="bg-slate-50 p-4 rounded-2xl border border-slate-100">
                      <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-1">Assigned To</p>
                      <h2 className="font-bold text-slate-800 flex items-center gap-2">
                        <div className="w-2 h-2 bg-emerald-400 rounded-full"></div>
                        {assignedTo || 'Unspecified User'}
                      </h2>
                    </div>
                  ) : (
                    <div className="bg-slate-50 p-4 rounded-2xl border border-dashed border-slate-200">
                      <p className="text-xs font-bold text-slate-400 italic">Available for assignment</p>
                    </div>
                  )}

                  {/* Price and Acquired Date Footer */}
                  <div className="mt-auto pt-4 border-t border-slate-100 flex flex-col gap-4">
                    <div className="w-full flex items-end justify-between">
                      <div>
                        <h2 className="text-2xl font-black text-slate-900">
                          R {(purchasePrice || 0).toLocaleString()}
                        </h2>
                        <p className="text-[10px] font-bold text-slate-400 uppercase tracking-tight">
                          Acquired: {purchaseDate ? new Date(purchaseDate).toLocaleDateString() : 'N/A'}
                        </p>
                      </div>

                      {/* Condition Status Indicator */}
                      <div className={`px-3 py-1 rounded-lg border-2 font-black text-[10px] uppercase tracking-tighter ${
                        conditionStatus === ConditionStatus.EXCELLENT ? 'border-emerald-100 text-emerald-600 bg-emerald-50/10' :
                        conditionStatus === ConditionStatus.GOOD ? 'border-blue-100 text-blue-600 bg-blue-50/10' :
                        conditionStatus === ConditionStatus.FAIR ? 'border-yellow-100 text-yellow-600 bg-yellow-50/10' :
                        'border-red-100 text-red-600 bg-red-50/10'
                      }`}>
                        {conditionStatus}
                      </div>
                    </div>

                    {/* Admin Action Buttons */}
                    {user?.role === 'ADMIN' && (
                      <div className="flex gap-2 w-full">
                        <button
                          onClick={() => handleOpenEditModal(asset)}
                          className="flex-1 py-2 bg-slate-50 border-2 border-slate-100 hover:bg-slate-100 text-slate-700 rounded-xl font-black text-[10px] uppercase tracking-wider transition-all"
                        >
                          Edit
                        </button>
                        <button
                          onClick={() => handleDeleteAssetAsync(id)}
                          className="flex-1 py-2 bg-red-50 border-2 border-red-100 hover:bg-red-100 text-red-600 rounded-xl font-black text-[10px] uppercase tracking-wider transition-all"
                        >
                          Delete
                        </button>
                      </div>
                    )}
                  </div>
                </div>
              );
            })}
          </div>

          {/* Pagination Controls */}
          {!searchTerm.trim() && totalPages > 1 && (
            <div className="w-full flex flex-col sm:flex-row items-center justify-between border-t-2 border-slate-100 pt-6 gap-4">
              <p className="text-sm font-bold text-slate-400">
                Showing {filteredAssets.length} of {totalElements} Assets
              </p>
              <div className="flex gap-2">
                <button
                  disabled={page === 0}
                  onClick={() => setPage(page - 1)}
                  className="px-4 py-2 border-2 border-slate-100 rounded-xl text-xs font-black uppercase tracking-wider text-slate-500 disabled:opacity-50 disabled:cursor-not-allowed hover:bg-slate-50 transition-all cursor-pointer"
                >
                  Previous
                </button>
                <div className="flex items-center px-4 bg-slate-50 rounded-xl border border-slate-100">
                  <span className="text-xs font-black text-slate-700">Page {page + 1} of {totalPages}</span>
                </div>
                <button
                  disabled={page >= totalPages - 1}
                  onClick={() => setPage(page + 1)}
                  className="px-4 py-2 border-2 border-slate-100 rounded-xl text-xs font-black uppercase tracking-wider text-slate-500 disabled:opacity-50 disabled:cursor-not-allowed hover:bg-slate-50 transition-all cursor-pointer"
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
          <div className="bg-white border-2 border-slate-200 rounded-[2.5rem] p-8 shadow-2xl w-full max-w-2xl max-h-[90vh] overflow-y-auto flex flex-col relative animate-in zoom-in-95 duration-200">
            {/* Close Button */}
            <button 
              onClick={() => setIsModalOpen(false)} 
              className="absolute right-6 top-6 text-slate-400 hover:text-slate-600 font-black text-lg transition-colors cursor-pointer"
            >
              ✕
            </button>

            {/* Modal Title */}
            <div className="mb-6">
              <h3 className="text-2xl font-black text-slate-900">
                {editingAsset ? 'Edit Asset Parameters' : 'Register New Asset'}
              </h3>
              <p className="text-slate-400 text-xs font-bold uppercase mt-1 tracking-widest">
                {editingAsset ? `ID: VO-${editingAsset.id.toString().padStart(4, '0')}` : 'Establish vault record'}
              </p>
            </div>

            {/* Error Notification */}
            {formError && (
              <div className="mb-6 p-4 bg-red-50 border border-red-100 rounded-2xl text-xs font-semibold text-red-600 text-center">
                {formError}
              </div>
            )}

            {/* Modal Form */}
            <form onSubmit={handleSaveAssetAsync} className="space-y-6 flex-1">
              <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {/* Name */}
                <div className="space-y-2">
                  <label className="text-[10px] font-black uppercase tracking-widest text-slate-400 px-1">Asset Name *</label>
                  <input
                    type="text"
                    required
                    placeholder="e.g. Server Node A"
                    value={formName}
                    onChange={e => setFormName(e.target.value)}
                    className="w-full h-11 px-4 bg-slate-50 border-2 border-slate-100 rounded-xl focus:border-[#0EA5E9] focus:bg-white focus:outline-none font-semibold text-sm transition-all"
                  />
                </div>

                {/* Type */}
                <div className="space-y-2">
                  <label className="text-[10px] font-black uppercase tracking-widest text-slate-400 px-1">Asset Type *</label>
                  <input
                    type="text"
                    required
                    placeholder="e.g. Hardware, License"
                    value={formType}
                    onChange={e => setFormType(e.target.value)}
                    className="w-full h-11 px-4 bg-slate-50 border-2 border-slate-100 rounded-xl focus:border-[#0EA5E9] focus:bg-white focus:outline-none font-semibold text-sm transition-all"
                  />
                </div>

                {/* Location */}
                <div className="space-y-2">
                  <label className="text-[10px] font-black uppercase tracking-widest text-slate-400 px-1">Location *</label>
                  <input
                    type="text"
                    required
                    placeholder="e.g. Cape Town Vault, Rack 4B"
                    value={formLocation}
                    onChange={e => setFormLocation(e.target.value)}
                    className="w-full h-11 px-4 bg-slate-50 border-2 border-slate-100 rounded-xl focus:border-[#0EA5E9] focus:bg-white focus:outline-none font-semibold text-sm transition-all"
                  />
                </div>

                {/* Serial Number */}
                <div className="space-y-2">
                  <label className="text-[10px] font-black uppercase tracking-widest text-slate-400 px-1">Serial Number</label>
                  <input
                    type="text"
                    placeholder="e.g. SN-998273-X"
                    value={formSerialNumber}
                    onChange={e => setFormSerialNumber(e.target.value)}
                    className="w-full h-11 px-4 bg-slate-50 border-2 border-slate-100 rounded-xl focus:border-[#0EA5E9] focus:bg-white focus:outline-none font-semibold text-sm transition-all"
                  />
                </div>

                {/* Price */}
                <div className="space-y-2">
                  <label className="text-[10px] font-black uppercase tracking-widest text-slate-400 px-1">Purchase Price (R)</label>
                  <input
                    type="number"
                    step="0.01"
                    placeholder="e.g. 15999.99"
                    value={formPurchasePrice}
                    onChange={e => setFormPurchasePrice(e.target.value)}
                    className="w-full h-11 px-4 bg-slate-50 border-2 border-slate-100 rounded-xl focus:border-[#0EA5E9] focus:bg-white focus:outline-none font-semibold text-sm transition-all"
                  />
                </div>

                {/* Purchase Date */}
                <div className="space-y-2">
                  <label className="text-[10px] font-black uppercase tracking-widest text-slate-400 px-1">Acquired Date</label>
                  <input
                    type="date"
                    value={formPurchaseDate}
                    onChange={e => setFormPurchaseDate(e.target.value)}
                    className="w-full h-11 px-4 bg-slate-50 border-2 border-slate-100 rounded-xl focus:border-[#0EA5E9] focus:bg-white focus:outline-none font-semibold text-sm text-slate-700 transition-all"
                  />
                </div>

                {/* Condition Status */}
                <div className="space-y-2">
                  <label className="text-[10px] font-black uppercase tracking-widest text-slate-400 px-1">Condition Status *</label>
                  <select
                    value={formConditionStatus}
                    onChange={e => setFormConditionStatus(e.target.value)}
                    className="w-full h-11 px-4 bg-slate-50 border-2 border-slate-100 rounded-xl focus:border-[#0EA5E9] focus:bg-white focus:outline-none font-semibold text-sm text-slate-700 transition-all"
                  >
                    {Object.values(ConditionStatus).map(c => (
                      <option key={c} value={c}>{c}</option>
                    ))}
                  </select>
                </div>

                {/* Usage Status */}
                <div className="space-y-2">
                  <label className="text-[10px] font-black uppercase tracking-widest text-slate-400 px-1">Usage Status *</label>
                  <select
                    value={formUsageStatus}
                    onChange={e => setFormUsageStatus(e.target.value)}
                    className="w-full h-11 px-4 bg-slate-50 border-2 border-slate-100 rounded-xl focus:border-[#0EA5E9] focus:bg-white focus:outline-none font-semibold text-sm text-slate-700 transition-all"
                  >
                    {Object.values(Usage).map(u => (
                      <option key={u} value={u}>{u.replace('_', ' ')}</option>
                    ))}
                  </select>
                </div>

                {/* Assignment Status */}
                <div className="space-y-2">
                  <label className="text-[10px] font-black uppercase tracking-widest text-slate-400 px-1">Assignment status *</label>
                  <select
                    value={formAssignment}
                    onChange={e => {
                      setFormAssignment(e.target.value);
                      if (e.target.value === Assignment.UNASSIGNED) {
                        setFormAssignedTo('');
                      }
                    }}
                    className="w-full h-11 px-4 bg-slate-50 border-2 border-slate-100 rounded-xl focus:border-[#0EA5E9] focus:bg-white focus:outline-none font-semibold text-sm text-slate-700 transition-all"
                  >
                    {Object.values(Assignment).map(a => (
                      <option key={a} value={a}>{a}</option>
                    ))}
                  </select>
                </div>

                {/* Assigned To */}
                <div className="space-y-2">
                  <label className="text-[10px] font-black uppercase tracking-widest text-slate-400 px-1">Assigned To User</label>
                  <input
                    type="text"
                    disabled={formAssignment === Assignment.UNASSIGNED}
                    placeholder="e.g. John Doe"
                    value={formAssignedTo}
                    onChange={e => setFormAssignedTo(e.target.value)}
                    className="w-full h-11 px-4 bg-slate-50 border-2 border-slate-100 rounded-xl focus:border-[#0EA5E9] focus:bg-white focus:outline-none font-semibold text-sm transition-all disabled:opacity-50 disabled:cursor-not-allowed"
                  />
                </div>
              </div>

              {/* Action Buttons */}
              <div className="flex gap-4 pt-4 border-t border-slate-100">
                <button
                  type="button"
                  onClick={() => setIsModalOpen(false)}
                  disabled={isSaving}
                  className="flex-1 py-4 border-2 border-slate-100 hover:bg-slate-50 text-slate-500 rounded-2xl font-black text-sm uppercase tracking-widest transition-all"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={isSaving}
                  className="flex-1 py-4 bg-[#0EA5E9] hover:brightness-110 text-white rounded-2xl font-black text-sm uppercase tracking-widest transition-all flex items-center justify-center gap-2"
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
