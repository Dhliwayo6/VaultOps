import { apiFetch } from '@utils/api';
import { handleResponse } from './apiHelper';

export async function getAuditLogs({ actionType, startDate, endDate, page = 0, size = 20 } = {}) {
  const params = new URLSearchParams();
  if (actionType) params.append('actionType', actionType);
  if (startDate) params.append('startDate', startDate);
  if (endDate) params.append('endDate', endDate);
  params.append('page', page.toString());
  params.append('size', size.toString());

  const response = await apiFetch(`/api/audit-log?${params.toString()}`);
  return handleResponse(response);
}
