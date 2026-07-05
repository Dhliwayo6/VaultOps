import { apiFetch } from '@utils/api';
import { handleResponse } from './apiHelper';

export async function createMaintenance(maintenanceData) {
  const response = await apiFetch('/api/maintenance', {
    method: 'POST',
    body: JSON.stringify(maintenanceData),
  });
  return handleResponse(response);
}

export async function getMaintenances() {
  const response = await apiFetch('/api/maintenances');
  return handleResponse(response);
}

export async function getMaintenanceById(id) {
  const response = await apiFetch(`/api/maintenance/${id}`);
  return handleResponse(response);
}

export async function updateMaintenance(id, maintenanceData) {
  const response = await apiFetch(`/api/maintenance/${id}`, {
    method: 'PUT',
    body: JSON.stringify(maintenanceData),
  });
  return handleResponse(response);
}

export async function deleteMaintenance(id) {
  const response = await apiFetch(`/api/maintenance/${id}`, {
    method: 'DELETE',
  });
  return handleResponse(response);
}
