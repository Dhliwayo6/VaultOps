import { apiFetch } from '@utils/api';
import { handleResponse } from './apiHelper';

export async function login({ email, password }) {
  const response = await apiFetch('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify({ email, password }),
  });
  return handleResponse(response);
}

export async function register({ name, email, phone, password }) {
  const response = await apiFetch('/api/auth/register', {
    method: 'POST',
    body: JSON.stringify({ name, email, phone, password }),
  });
  return handleResponse(response);
}

export async function verifyOtp({ email, code }) {
  const response = await apiFetch('/api/auth/verify-otp', {
    method: 'POST',
    body: JSON.stringify({ email, code }),
  });
  return handleResponse(response);
}

export async function resendOtp(email) {
  const response = await apiFetch('/api/auth/resend-otp', {
    method: 'POST',
    body: JSON.stringify({ email }),
  });
  return handleResponse(response);
}

export async function logout() {
  const response = await apiFetch('/api/auth/logout', {
    method: 'POST',
  });
  return handleResponse(response);
}

export async function refresh() {
  const response = await apiFetch('/api/auth/refresh', {
    method: 'POST',
  });
  return handleResponse(response);
}
