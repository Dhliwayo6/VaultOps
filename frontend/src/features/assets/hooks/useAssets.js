import { useState, useEffect, useCallback } from 'react';
import { getAssets, searchAssets } from '@api/assetsApi';

export function useAssets() {
  const [assets, setAssets] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  // Pagination & Sorting state
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(6); // Render 6 assets per page
  const [sortBy, setSortBy] = useState('createdAt');
  const [direction, setDirection] = useState('DESC');
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  // Search & Filtering state
  const [searchTerm, setSearchTerm] = useState('');
  const [conditionFilter, setConditionFilter] = useState('');
  const [usageFilter, setUsageFilter] = useState('');
  const [assignmentFilter, setAssignmentFilter] = useState('');

  const fetchAssetsData = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      let data;
      if (searchTerm.trim()) {
        // If searching, call search endpoint (returns plain array)
        data = await searchAssets(searchTerm.trim()).catch(err => {
          // Handle empty search results gracefully (NoResultsException)
          return [];
        });
        
        // Map list to page-like structure
        const list = Array.isArray(data) ? data : [];
        setAssets(list);
        setTotalPages(1);
        setTotalElements(list.length);
      } else {
        // Otherwise paginated getAssets
        data = await getAssets({ page, size, sortBy, direction }).catch(err => {
          // Handle empty list gracefully (NoResultsException)
          return { content: [], totalPages: 0, totalElements: 0 };
        });

        let content = [];
        if (data && data.content) {
          content = data.content;
        } else if (Array.isArray(data)) {
          content = data;
        }
        setAssets(content);
        setTotalPages(data.totalPages || 1);
        setTotalElements(data.totalElements || content.length);
      }
    } catch (err) {
      console.error(err);
      setError(err.message || 'Failed to fetch assets.');
    } finally {
      setIsLoading(false);
    }
  }, [page, size, sortBy, direction, searchTerm]);

  useEffect(() => {
    fetchAssetsData();
  }, [fetchAssetsData]);

  // Reset page when searching or changing sort
  useEffect(() => {
    setPage(0);
  }, [searchTerm, sortBy, direction]);

  // Client-side filtering on the retrieved asset list
  const filteredAssets = assets.filter(asset => {
    if (conditionFilter && asset.conditionStatus !== conditionFilter) return false;
    if (usageFilter && asset.usageStatus !== usageFilter) return false;
    if (assignmentFilter && asset.assignment !== assignmentFilter) return false;
    return true;
  });

  return {
    assets: filteredAssets,
    isLoading,
    error,
    page,
    setPage,
    size,
    setSize,
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
    refetch: fetchAssetsData
  };
}
