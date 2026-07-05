import { useState, useEffect, useCallback } from 'react';
import { getDashboardStats } from '@api/statsApi';
import { getAssets } from '@api/assetsApi';

export function useDashboardStats() {
  const [stats, setStats] = useState([]);
  const [recentAssets, setRecentAssets] = useState([]);
  const [vaultCapacity, setVaultCapacity] = useState(0);
  const [serviceNotice, setServiceNotice] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchData = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const [statsData, assetsData] = await Promise.all([
        getDashboardStats(),
        getAssets({ page: 0, size: 5, sortBy: 'createdAt', direction: 'DESC' }).catch(err => {
          // If no assets yet, return an empty page shape
          return { content: [] };
        })
      ]);

      // Format bento stats
      const statsArray = [
        { label: "Total Assets", value: (statsData.totalAssets || 0).toLocaleString(), color: "bg-white text-slate-800" },
        { label: "In Use", value: (statsData.inUseCount || 0).toLocaleString(), color: "bg-[#0EA5E9] text-white" },
        { label: "In Storage", value: (statsData.inStorageCount || 0).toLocaleString(), color: "bg-white text-slate-800" },
        { label: "In Service", value: (statsData.inServiceCount || 0).toLocaleString(), color: "bg-white text-slate-800" },
        { label: "Damaged", value: (statsData.damagedCount || 0).toLocaleString(), color: "bg-red-50 text-red-600" },
      ];
      setStats(statsArray);

      // Handle raw "No results found" object
      let rawAssets = [];
      if (assetsData && assetsData.content) {
        rawAssets = assetsData.content;
      } else if (Array.isArray(assetsData)) {
        rawAssets = assetsData;
      }

      // Map recent assets
      const mappedAssets = rawAssets.map(asset => ({
        id: asset.serialNumber || `VO-${asset.id}`,
        name: asset.name,
        user: asset.assignedTo || 'Available',
        status: (asset.usageStatus || '').replace('_', ' '),
        lastUpdated: new Date(asset.latestUpdatedDate || asset.createdAt).toLocaleDateString()
      }));
      setRecentAssets(mappedAssets);

      // Calculate capacity dynamically (e.g. storage count relative to a vault size of 500)
      const storageCount = statsData.inStorageCount || 0;
      const capacityPercentage = Math.min(Math.round((storageCount / 500) * 100), 100);
      setVaultCapacity(capacityPercentage || 0);

      // Set dynamic notice
      if (statsData.inServiceCount > 0) {
        const daysText = statsData.averageDaysInRepair > 0 
          ? `average repair turnaround is ${statsData.averageDaysInRepair.toFixed(1)} days.` 
          : 'check the maintenance logs.';
        setServiceNotice(`${statsData.inServiceCount} items are marked for service. The ${daysText}`);
      } else {
        setServiceNotice("All vault hardware operating nominal. No items scheduled for repairs.");
      }

    } catch (err) {
      console.error(err);
      setError(err.message || 'Failed to load dashboard statistics.');
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  return {
    stats,
    recentAssets,
    vaultCapacity,
    serviceNotice,
    isLoading,
    error,
    refetch: fetchData
  };
}
