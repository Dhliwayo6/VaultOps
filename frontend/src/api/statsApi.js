import { apiFetch } from '@utils/api';
import { handleResponse } from './apiHelper';

export async function getDashboardStats() {
  const response = await apiFetch('/api/stats/dashboard');
  return handleResponse(response);
}
