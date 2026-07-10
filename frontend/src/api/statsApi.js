import { apiFetch } from '@utils/api';
import { handleResponse } from './apiHelper';

export async function getDashboardStats() {
  const response = await apiFetch('/api/stats/dashboard');
  return handleResponse(response);
}

export async function getMonthlyTrends() {
  const response = await apiFetch('/api/stats/trends');
  return handleResponse(response);
}

export async function getCategoryConditionStats() {
  const response = await apiFetch('/api/stats/categories');
  return handleResponse(response);
}

export async function getFinancialStats() {
  const response = await apiFetch('/api/stats/value');
  return handleResponse(response);
}

export async function getDashboardAlerts() {
  const response = await apiFetch('/api/stats/alerts');
  return handleResponse(response);
}
