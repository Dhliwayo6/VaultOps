import { apiFetch } from '@utils/api';
import { handleResponse } from './apiHelper';

export async function getDashboardStats(locationId) {
  const query = locationId ? `?locationId=${locationId}` : '';
  const response = await apiFetch(`/api/stats/dashboard${query}`);
  return handleResponse(response);
}

export async function getMonthlyTrends(locationId) {
  const query = locationId ? `?locationId=${locationId}` : '';
  const response = await apiFetch(`/api/stats/trends${query}`);
  return handleResponse(response);
}

export async function getCategoryConditionStats(locationId) {
  const query = locationId ? `?locationId=${locationId}` : '';
  const response = await apiFetch(`/api/stats/categories${query}`);
  return handleResponse(response);
}

export async function getFinancialStats(locationId) {
  const query = locationId ? `?locationId=${locationId}` : '';
  const response = await apiFetch(`/api/stats/value${query}`);
  return handleResponse(response);
}

export async function getDashboardAlerts(locationId) {
  const query = locationId ? `?locationId=${locationId}` : '';
  const response = await apiFetch(`/api/stats/alerts${query}`);
  return handleResponse(response);
}
