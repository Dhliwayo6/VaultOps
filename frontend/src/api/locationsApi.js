import { apiFetch } from '@utils/api';
import { handleResponse } from './apiHelper';

export async function getLocations() {
  const response = await apiFetch('/api/locations');
  return handleResponse(response);
}

export async function getLocationById(id) {
  const response = await apiFetch(`/api/location/${id}`);
  return handleResponse(response);
}

export async function createLocation(locationData) {
  const response = await apiFetch('/api/location', {
    method: 'POST',
    body: JSON.stringify(locationData),
  });
  return handleResponse(response);
}

export async function updateLocation(id, locationData) {
  const response = await apiFetch(`/api/location/${id}`, {
    method: 'PUT',
    body: JSON.stringify(locationData),
  });
  return handleResponse(response);
}

export async function deleteLocation(id) {
  const response = await apiFetch(`/api/location/${id}`, {
    method: 'DELETE',
  });
  return handleResponse(response);
}
