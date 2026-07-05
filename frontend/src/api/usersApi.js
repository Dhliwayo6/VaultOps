import { apiFetch } from '@utils/api';
import { handleResponse } from './apiHelper';

export async function getAllUsers() {
  const response = await apiFetch('/api/users');
  return handleResponse(response);
}

export async function changeUserRole(id, role) {
  const response = await apiFetch(`/api/users/${id}/role`, {
    method: 'PUT',
    body: JSON.stringify({ role }),
  });
  return handleResponse(response);
}
