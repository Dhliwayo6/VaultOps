import { apiFetch } from '@utils/api';
import { handleResponse } from './apiHelper';

export async function getAssets({ page, size, sortBy, direction } = {}) {
  let query = '';
  const params = [];
  if (page !== undefined && page !== null) params.push(`page=${page}`);
  if (size !== undefined && size !== null) params.push(`size=${size}`);
  if (sortBy) params.push(`sortBy=${sortBy}`);
  if (direction) params.push(`direction=${direction}`);
  
  if (params.length > 0) {
    query = `?${params.join('&')}`;
  }

  const response = await apiFetch(`/api/assets${query}`);
  return handleResponse(response);
}

export async function getTopFourAssetsInUse() {
  const response = await apiFetch('/api/assets/top-four/in-use');
  return handleResponse(response);
}

export async function getTopFourAssetsInRepairs() {
  const response = await apiFetch('/api/assets/top-four/in-repairs');
  return handleResponse(response);
}

export async function createAsset(assetData) {
  const response = await apiFetch('/api/asset', {
    method: 'POST',
    body: JSON.stringify(assetData),
  });
  return handleResponse(response);
}

export async function getAssetById(id) {
  const response = await apiFetch(`/api/asset/${id}`);
  return handleResponse(response);
}

export async function updateAsset(id, assetData) {
  const response = await apiFetch(`/api/asset/${id}`, {
    method: 'PUT',
    body: JSON.stringify(assetData),
  });
  return handleResponse(response);
}

export async function deleteAsset(id) {
  const response = await apiFetch(`/api/asset/${id}`, {
    method: 'DELETE',
  });
  return handleResponse(response);
}

export async function searchAssets(name) {
  const response = await apiFetch(`/api/asset/search?name=${encodeURIComponent(name)}`);
  return handleResponse(response);
}
