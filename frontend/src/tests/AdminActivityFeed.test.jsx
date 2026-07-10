import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import AdminActivityFeed from '../features/dashboard/components/AdminActivityFeed';
import { getAuditLogs } from '@api/auditApi';

vi.mock('@api/auditApi', () => ({
  getAuditLogs: vi.fn(),
}));

describe('AdminActivityFeed Component Tests', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('should render audit logs and handle filters and pagination', async () => {
    getAuditLogs.mockResolvedValue({
      content: [
        {
          id: 1,
          timestamp: '2026-07-10T10:00:00',
          actingUser: 'm***i@example.com',
          actionType: 'CREATE_ASSET',
          description: 'Asset registered: Server Room A',
          resourceType: 'Asset',
          resourceId: '10'
        }
      ],
      totalPages: 2,
      totalElements: 2
    });

    render(<AdminActivityFeed />);

    expect(screen.getByLabelText('Admin Activity Feed')).toBeInTheDocument();
    
    await waitFor(() => {
      expect(screen.getByText('Asset registered: Server Room A')).toBeInTheDocument();
    });

    expect(screen.getByText('User: m***i@example.com')).toBeInTheDocument();

    const actionSelect = screen.getByLabelText('Action Type');
    expect(actionSelect).toBeInTheDocument();

    getAuditLogs.mockResolvedValue({
      content: [
        {
          id: 2,
          timestamp: '2026-07-10T11:00:00',
          actingUser: 'm***i@example.com',
          actionType: 'DELETE_ASSET',
          description: 'Asset deleted',
          resourceType: 'Asset',
          resourceId: '12'
        }
      ],
      totalPages: 2,
      totalElements: 2
    });

    const nextBtn = screen.getByText('Next');
    fireEvent.click(nextBtn);

    await waitFor(() => {
      expect(screen.getByText('Asset deleted')).toBeInTheDocument();
    });
  });
});
