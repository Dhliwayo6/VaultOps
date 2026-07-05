import { apiFetch } from '@utils/api';
import { handleResponse } from './apiHelper';

export async function importAssets(file, dryRun = false) {
  const formData = new FormData();
  formData.append('file', file);

  const response = await apiFetch(`/api/import/assets?dryRun=${dryRun}`, {
    method: 'POST',
    body: formData,
  });
  return handleResponse(response);
}

export async function downloadExcelTemplate() {
  const response = await apiFetch('/api/import/template/excel');
  return handleResponse(response);
}

export async function downloadCsvTemplate() {
  const response = await apiFetch('/api/import/template/csv');
  return handleResponse(response);
}

export async function getImportLogs(limit = 50) {
  const response = await apiFetch(`/api/import/logs?limit=${limit}`);
  return handleResponse(response);
}

export async function getImportLogById(id) {
  const response = await apiFetch(`/api/import/logs/${id}`);
  return handleResponse(response);
}
