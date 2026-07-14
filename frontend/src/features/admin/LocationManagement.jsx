import React, { useEffect, useState } from 'react';
import { getLocations, createLocation, updateLocation, deleteLocation } from '@api/locationsApi';
import { getAssets } from '@api/assetsApi';
import { FaSpinner, FaMapMarkerAlt, FaPlus, FaEdit, FaTrash, FaTimes } from 'react-icons/fa';

export default function LocationManagement() {
  const [locations, setLocations] = useState([]);
  const [assets, setAssets] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [errorMsg, setErrorMsg] = useState('');
  const [successMsg, setSuccessMsg] = useState('');
  const [actionLoading, setActionLoading] = useState(false);

  // Form state
  const [editId, setEditId] = useState(null);
  const [name, setName] = useState('');
  const [maxCapacity, setMaxCapacity] = useState(100);
  const [description, setDescription] = useState('');
  const [address, setAddress] = useState('');

  const fetchData = async () => {
    setIsLoading(true);
    setErrorMsg('');
    try {
      const [locsData, assetsData] = await Promise.all([
        getLocations(),
        getAssets() // calls non-paginated endpoint when no page/size passed
      ]);
      setLocations(locsData || []);
      setAssets(assetsData || []);
    } catch (err) {
      console.error(err);
      setErrorMsg(err.message || 'Failed to retrieve locations data.');
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const getOccupancy = (locId) => {
    return assets.filter(a => a.location && a.location.id === locId).length;
  };

  const handleEditClick = (loc) => {
    setEditId(loc.id);
    setName(loc.name);
    setMaxCapacity(loc.maxCapacity);
    setDescription(loc.description || '');
    setAddress(loc.address || '');
    setErrorMsg('');
    setSuccessMsg('');
  };

  const handleCancelEdit = () => {
    setEditId(null);
    setName('');
    setMaxCapacity(100);
    setDescription('');
    setAddress('');
    setErrorMsg('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!name.trim()) {
      setErrorMsg('Location name is required.');
      return;
    }
    if (maxCapacity < 1) {
      setErrorMsg('Capacity must be at least 1.');
      return;
    }

    setActionLoading(true);
    setErrorMsg('');
    setSuccessMsg('');

    try {
      const payload = {
        name: name.trim(),
        maxCapacity: parseInt(maxCapacity, 10),
        description: description.trim() || null,
        address: address.trim() || null
      };

      if (editId) {
        await updateLocation(editId, payload);
        setSuccessMsg('Location updated successfully.');
      } else {
        await createLocation(payload);
        setSuccessMsg('Location created successfully.');
      }

      handleCancelEdit();
      // Reload lists
      const [locsData, assetsData] = await Promise.all([
        getLocations(),
        getAssets()
      ]);
      setLocations(locsData || []);
      setAssets(assetsData || []);
    } catch (err) {
      console.error(err);
      setErrorMsg(err.message || 'An error occurred while saving the location.');
    } finally {
      setActionLoading(false);
    }
  };

  const handleDelete = async (locId, locName) => {
    if (locId === 1) {
      setErrorMsg('Cannot delete the default Unassigned location.');
      return;
    }
    const currentOccupancy = getOccupancy(locId);
    if (currentOccupancy > 0) {
      setErrorMsg(`Cannot delete "${locName}" as it currently has ${currentOccupancy} assets assigned.`);
      return;
    }
    if (!window.confirm(`Are you sure you want to delete the location "${locName}"?`)) {
      return;
    }

    setActionLoading(true);
    setErrorMsg('');
    setSuccessMsg('');

    try {
      await deleteLocation(locId);
      setSuccessMsg('Location deleted successfully.');
      setLocations(prev => prev.filter(l => l.id !== locId));
    } catch (err) {
      console.error(err);
      setErrorMsg(err.message || 'Failed to delete location.');
    } finally {
      setActionLoading(false);
    }
  };

  return (
    <section className="w-full min-h-full flex flex-col gap-8 animate-in fade-in duration-500">
      <div>
        <h2 className="text-3xl md:text-4xl font-black tracking-tight text-text-primary">
          Location Management
        </h2>
        <p className="text-text-secondary font-medium mt-1">Configure asset storage sites, capacities, and physical addresses</p>
      </div>

      {errorMsg && (
        <div role="alert" className="p-4 bg-red-500/10 border border-red-500/20 rounded-2xl text-sm font-semibold text-red-500 text-center">
          {errorMsg}
        </div>
      )}

      {successMsg && (
        <div role="alert" className="p-4 bg-emerald-500/10 border border-emerald-500/20 rounded-2xl text-sm font-semibold text-emerald-500 text-center">
          {successMsg}
        </div>
      )}

      {isLoading ? (
        <div className="flex justify-center py-12">
          <FaSpinner className="w-8 h-8 text-accent animate-spin" />
        </div>
      ) : (
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Table of Locations */}
          <div className="lg:col-span-2 bg-surface-elevated border border-border-token rounded-card overflow-hidden shadow-elevation flex flex-col">
            <div className="overflow-x-auto">
              <table className="w-full min-w-[600px] text-left border-collapse">
                <thead>
                  <tr className="border-b border-border-token text-[10px] uppercase tracking-widest font-black text-text-secondary bg-bg-base/50">
                    <th className="px-6 py-5">Location</th>
                    <th className="px-6 py-5">Occupancy</th>
                    <th className="px-6 py-5">Address</th>
                    <th className="px-6 py-5 text-right">Actions</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-border-token">
                  {locations.map(loc => {
                    const occupancy = getOccupancy(loc.id);
                    const percent = Math.min(100, Math.round((occupancy / loc.maxCapacity) * 100));
                    let progressColor = 'bg-accent';
                    if (percent >= 90) progressColor = 'bg-red-500';
                    else if (percent >= 75) progressColor = 'bg-amber-500';

                    return (
                      <tr key={loc.id} className="group hover:bg-bg-base transition-colors">
                        <td className="px-6 py-5">
                          <div className="flex flex-col">
                            <span className="font-bold text-text-primary flex items-center gap-2">
                              {loc.name}
                              {loc.id === 1 && (
                                <span className="px-2 py-0.5 rounded text-[8px] font-black uppercase tracking-widest bg-bg-base text-text-secondary border border-border-token">
                                  Default
                                </span>
                              )}
                            </span>
                            {loc.description && (
                              <span className="text-xs text-text-secondary mt-0.5 line-clamp-1">
                                {loc.description}
                              </span>
                            )}
                          </div>
                        </td>
                        <td className="px-6 py-5">
                          <div className="flex flex-col gap-1 w-full max-w-[120px]">
                            <div className="flex justify-between text-[10px] font-black uppercase text-text-secondary">
                              <span>{occupancy} / {loc.maxCapacity}</span>
                              <span>{percent}%</span>
                            </div>
                            <div className="w-full h-2 bg-bg-base rounded-full overflow-hidden border border-border-token">
                              <div 
                                className={`h-full ${progressColor} transition-all duration-500`}
                                style={{ width: `${percent}%` }}
                              />
                            </div>
                          </div>
                        </td>
                        <td className="px-6 py-5 text-sm text-text-secondary">
                          {loc.address || <span className="italic opacity-50">Not specified</span>}
                        </td>
                        <td className="px-6 py-5 text-right">
                          <div className="flex items-center justify-end gap-2">
                            <button
                              onClick={() => handleEditClick(loc)}
                              disabled={actionLoading}
                              title="Edit Location"
                              aria-label={`Edit ${loc.name}`}
                              className="p-2 rounded-xl text-text-secondary hover:text-accent hover:bg-bg-base border border-transparent hover:border-border-token transition-all cursor-pointer disabled:opacity-50"
                            >
                              <FaEdit />
                            </button>
                            <button
                              onClick={() => handleDelete(loc.id, loc.name)}
                              disabled={actionLoading || loc.id === 1}
                              title={loc.id === 1 ? 'Cannot delete default location' : 'Delete Location'}
                              aria-label={`Delete ${loc.name}`}
                              className="p-2 rounded-xl text-text-secondary hover:text-red-500 hover:bg-red-500/10 border border-transparent hover:border-red-500/20 transition-all cursor-pointer disabled:opacity-30"
                            >
                              <FaTrash />
                            </button>
                          </div>
                        </td>
                      </tr>
                    );
                  })}
                  {locations.length === 0 && (
                    <tr>
                      <td colSpan="4" className="text-center py-8 text-text-secondary font-bold">
                        No locations configured.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>

          {/* Create/Edit Form */}
          <div className="bg-surface-elevated border border-border-token rounded-card p-6 shadow-elevation flex flex-col gap-6 h-fit">
            <div className="flex justify-between items-center">
              <h3 className="text-lg font-black tracking-tight text-text-primary flex items-center gap-2">
                <FaMapMarkerAlt className="text-accent" />
                {editId ? `Edit Location #${editId}` : 'Create Location'}
              </h3>
              {editId && (
                <button 
                  onClick={handleCancelEdit}
                  className="p-1.5 rounded-lg hover:bg-bg-base text-text-secondary hover:text-text-primary transition-all cursor-pointer"
                >
                  <FaTimes />
                </button>
              )}
            </div>

            <form onSubmit={handleSubmit} className="flex flex-col gap-4">
              <div className="flex flex-col gap-1.5">
                <label className="text-[10px] font-black uppercase tracking-widest text-text-secondary">
                  Location Name *
                </label>
                <input
                  type="text"
                  required
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  placeholder="e.g. Headquarters, Warehouse A"
                  className="w-full px-4 py-3 bg-bg-base border border-border-token rounded-xl text-text-primary placeholder:text-text-secondary/50 font-bold focus:outline-none focus:border-accent transition-colors"
                />
              </div>

              <div className="flex flex-col gap-1.5">
                <label className="text-[10px] font-black uppercase tracking-widest text-text-secondary">
                  Max Capacity Limit *
                </label>
                <input
                  type="number"
                  required
                  min="1"
                  value={maxCapacity}
                  onChange={(e) => setMaxCapacity(parseInt(e.target.value, 10))}
                  placeholder="e.g. 100"
                  className="w-full px-4 py-3 bg-bg-base border border-border-token rounded-xl text-text-primary font-bold focus:outline-none focus:border-accent transition-colors"
                />
              </div>

              <div className="flex flex-col gap-1.5">
                <label className="text-[10px] font-black uppercase tracking-widest text-text-secondary">
                  Address
                </label>
                <input
                  type="text"
                  value={address}
                  onChange={(e) => setAddress(e.target.value)}
                  placeholder="e.g. 123 Main St, Tech City"
                  className="w-full px-4 py-3 bg-bg-base border border-border-token rounded-xl text-text-primary placeholder:text-text-secondary/50 font-medium focus:outline-none focus:border-accent transition-colors"
                />
              </div>

              <div className="flex flex-col gap-1.5">
                <label className="text-[10px] font-black uppercase tracking-widest text-text-secondary">
                  Description
                </label>
                <textarea
                  value={description}
                  onChange={(e) => setDescription(e.target.value)}
                  placeholder="Describe this storage site..."
                  rows="3"
                  className="w-full px-4 py-3 bg-bg-base border border-border-token rounded-xl text-text-primary placeholder:text-text-secondary/50 font-medium focus:outline-none focus:border-accent transition-colors resize-none"
                />
              </div>

              <button
                type="submit"
                disabled={actionLoading}
                className="w-full py-3.5 bg-accent hover:bg-accent-hover text-white rounded-xl text-xs font-black uppercase tracking-widest shadow-glow flex items-center justify-center gap-2 transition-all cursor-pointer disabled:opacity-50 mt-2"
              >
                {actionLoading ? (
                  <FaSpinner className="animate-spin w-4 h-4" />
                ) : editId ? (
                  'Save Changes'
                ) : (
                  <>
                    <FaPlus /> Create Location
                  </>
                )}
              </button>
            </form>
          </div>
        </div>
      )}
    </section>
  );
}
