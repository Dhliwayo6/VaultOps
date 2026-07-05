export async function handleResponse(response) {
  if (!response.ok) {
    let message = 'API request failed';
    try {
      const data = await response.json();
      message = data.message || data.error || message;
    } catch (e) {
      // Keep default message if not JSON
    }
    throw new Error(message);
  }
  if (response.status === 204) return null;
  const contentType = response.headers.get('content-type');
  if (contentType && contentType.includes('application/json')) {
    return await response.json();
  }
  return await response.blob();
}
