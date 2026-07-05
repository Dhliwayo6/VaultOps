import { stats, recentAssets } from '@constants/dashboard';

export function useDashboardStats() {
  return {
    stats,
    recentAssets,
    vaultCapacity: 82,
    serviceNotice: "3 items are marked for service this week. Check the maintenance log."
  };
}
