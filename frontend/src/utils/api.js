const BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8081';

let accessToken = '';

export const getAccessToken = () => accessToken;
export const setAccessToken = (token) => {
  accessToken = token;
};

let logoutCallback = null;
export const registerLogoutCallback = (callback) => {
  logoutCallback = callback;
};

export async function apiFetch(endpoint, options = {}) {
  const url = `${BASE_URL}${endpoint}`;
  
  // Prepare headers
  const headers = {
    ...options.headers,
  };

  if (!(options.body instanceof FormData)) {
    if (!headers['Content-Type']) {
      headers['Content-Type'] = 'application/json';
    }
  }

  if (accessToken) {
    headers['Authorization'] = `Bearer ${accessToken}`;
  }

  const fetchOptions = {
    ...options,
    headers,
  };

  // Include credentials for all requests so secure cookies are transmitted
  fetchOptions.credentials = 'include';

  let response = await fetch(url, fetchOptions);

  if (response.status === 401 && !endpoint.includes('/api/auth/login') && !endpoint.includes('/api/auth/refresh')) {
    // Attempt token refresh
    try {
      const refreshResponse = await fetch(`${BASE_URL}/api/auth/refresh`, {
        method: 'POST',
        credentials: 'include',
      });

      if (refreshResponse.ok) {
        const data = await refreshResponse.json();
        setAccessToken(data.accessToken);
        
        // Retry the original request
        headers['Authorization'] = `Bearer ${data.accessToken}`;
        response = await fetch(url, fetchOptions);
      } else {
        // Refresh failed, log out
        setAccessToken('');
        if (logoutCallback) {
          logoutCallback();
        } else {
          window.location.href = '/sign-in';
        }
      }
    } catch (error) {
      setAccessToken('');
      if (logoutCallback) {
        logoutCallback();
      } else {
        window.location.href = '/sign-in';
      }
    }
  }

  return response;
}
