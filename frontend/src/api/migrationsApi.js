import { apiFetch } from '@utils/api';
import { handleResponse } from './apiHelper';

export async function createMigration(migrationData) {
  const response = await apiFetch('/api/migration', {
    method: 'POST',
    body: JSON.stringify(migrationData),
  });
  return handleResponse(response);
}

export async function getMigrations() {
  const response = await apiFetch('/api/migration');
  return handleResponse(response);
}

export async function getMigrationById(id) {
  const response = await apiFetch(`/api/migration/${id}`);
  return handleResponse(response);
}

export async function updateMigration(id, migrationData) {
  const response = await apiFetch(`/api/migration/${id}`, {
    method: 'PUT',
    body: JSON.stringify(migrationData),
  });
  return handleResponse(response);
}

export async function deleteMigration(id) {
  const response = await apiFetch(`/api/migration/${id}`, {
    method: 'DELETE',
  });
  return handleResponse(response);
}
