import { useState, useEffect, useCallback } from 'react';
import { getDashboardStats, getMonthlyTrends, getCategoryConditionStats, getFinancialStats, getDashboardAlerts } from '@api/statsApi';
import { getAssets } from '@api/assetsApi';
import { useAuth } from '@context/AuthContext';

export function useDashboardStats(locationId = null) {
  const { user } = useAuth();
  const isAdmin = user?.role === 'ADMIN';

  const [stats, setStats] = useState([]);
  const [alerts, setAlerts] = useState([]);
  const [recentAssets, setRecentAssets] = useState([]);
  const [vaultCapacity, setVaultCapacity] = useState(0);
  const [serviceNotice, setServiceNotice] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  // New chart state variables
  const [allocationData, setAllocationData] = useState([]);
  const [categoryData, setCategoryData] = useState([]);
  const [trendData, setTrendData] = useState([]);
  const [financialData, setFinancialData] = useState(null);
  const [chartsLoading, setChartsLoading] = useState(true);
  const [chartsError, setChartsError] = useState(null);

  const fetchData = useCallback(async () => {
    setIsLoading(true);
    setChartsLoading(true);
    setError(null);
    setChartsError(null);
    try {
      // 1. Fetch base dashboard statistics, alerts, and recent assets
      const [statsData, assetsData, alertsData] = await Promise.all([
        getDashboardStats(locationId),
        getAssets({ page: 0, size: 5, sortBy: 'createdAt', direction: 'DESC', locationId }).catch(err => {
          return { content: [] };
        }),
        getDashboardAlerts(locationId).catch(err => {
          console.error("Failed to fetch alerts", err);
          return [];
        })
      ]);

      setAlerts(alertsData || []);

      // Format bento stats
      const statsArray = [
        { label: "Total Assets", value: statsData.totalAssets || 0 },
        { label: "In Use", value: statsData.inUseCount || 0 },
        { label: "In Storage", value: statsData.inStorageCount || 0 },
        { label: "In Service", value: statsData.inServiceCount || 0 },
        { label: "Damaged", value: statsData.damagedCount || 0 },
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

      // Calculate capacity dynamically
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

      // Format allocation doughnut chart data
      const allocation = [
        { name: 'In Use', value: statsData.inUseCount || 0 },
        { name: 'In Storage', value: statsData.inStorageCount || 0 },
        { name: 'Under Maintenance', value: statsData.inServiceCount || 0 }
      ];
      setAllocationData(allocation);

      // 2. Fetch trends, category-condition distributions, and financial metrics (if admin)
      const [trends, categories] = await Promise.all([
        getMonthlyTrends(locationId).catch(err => {
          console.error("Failed to fetch trends", err);
          return [];
        }),
        getCategoryConditionStats(locationId).catch(err => {
          console.error("Failed to fetch categories", err);
          return [];
        })
      ]);

      setTrendData(trends);

      // Group categories for stacked bar chart: { category, EXCELLENT, GOOD, FAIR, BAD, DAMAGED }
      const categoryGroups = {};
      categories.forEach(item => {
        const cat = item.category || 'Uncategorized';
        if (!categoryGroups[cat]) {
          categoryGroups[cat] = { category: cat, EXCELLENT: 0, GOOD: 0, FAIR: 0, BAD: 0, DAMAGED: 0 };
        }
        categoryGroups[cat][item.conditionStatus] = item.count;
      });
      setCategoryData(Object.values(categoryGroups));

      if (isAdmin) {
        try {
          const financial = await getFinancialStats(locationId);
          setFinancialData(financial);
        } catch (err) {
          console.error("Failed to fetch financial metrics", err);
          setFinancialData(null);
        }
      }

    } catch (err) {
      console.error(err);
      setError(err.message || 'Failed to load dashboard statistics.');
      setChartsError(err.message || 'Failed to load chart data.');
    } finally {
      setIsLoading(false);
      setChartsLoading(false);
    }
  }, [isAdmin, locationId]);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  return {
    stats,
    alerts,
    recentAssets,
    vaultCapacity,
    serviceNotice,
    isLoading,
    error,
    refetch: fetchData,
    // Chart data props
    allocationData,
    categoryData,
    trendData,
    financialData,
    chartsLoading,
    chartsError,
    isAdmin
  };
}
