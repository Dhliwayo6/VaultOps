import { apiFetch } from '@utils/api';
import { handleResponse } from './apiHelper';

function buildQueryString(filters = {}) {
  const params = [];
  if (filters.category) params.push(`category=${encodeURIComponent(filters.category)}`);
  if (filters.condition) params.push(`condition=${encodeURIComponent(filters.condition)}`);
  if (filters.usage) params.push(`usage=${encodeURIComponent(filters.usage)}`);
  if (filters.location) params.push(`location=${encodeURIComponent(filters.location)}`);
  if (filters.purchaseDateFrom) params.push(`purchaseDateFrom=${encodeURIComponent(filters.purchaseDateFrom)}`);
  if (filters.purchaseDateTo) params.push(`purchaseDateTo=${encodeURIComponent(filters.purchaseDateTo)}`);
  return params.length > 0 ? `?${params.join('&')}` : '';
}

export async function exportToExcel(filters) {
  const query = buildQueryString(filters);
  const response = await apiFetch(`/api/export/assets/excel${query}`);
  return handleResponse(response);
}

export async function exportToCsv(filters) {
  const query = buildQueryString(filters);
  const response = await apiFetch(`/api/export/assets/csv${query}`);
  return handleResponse(response);
}
