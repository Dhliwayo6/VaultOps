import { useState } from 'react';
import { DUMMY_ASSETS } from '@constants/assets';

export function useAssets() {
  const [sortBy, setSortBy] = useState('date');

  const sortedAssets = [...DUMMY_ASSETS].sort((a, b) => {
    if (sortBy === 'name') return a.name.localeCompare(b.name);
    return new Date(b.purchaseDate).getTime() - new Date(a.purchaseDate).getTime();
  });

  return {
    sortBy,
    setSortBy,
    assets: sortedAssets
  };
}
