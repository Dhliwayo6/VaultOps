import React from 'react';
import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import Dashboard from '../features/dashboard/Dashboard';
import { useDashboardStats } from '../features/dashboard/hooks/useDashboardStats';

vi.mock('../features/dashboard/hooks/useDashboardStats', () => ({
  useDashboardStats: vi.fn(),
}));

vi.mock('@features/assets/components/AddAssetButton', () => ({
  default: () => <button data-testid="add-asset-button">Add Asset</button>,
}));

describe('Dashboard Component Tests', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('should render Loading state when stats are fetching', () => {
    useDashboardStats.mockReturnValue({
      stats: [],
      recentAssets: [],
      vaultCapacity: 0,
      serviceNotice: '',
      isLoading: true,
      error: null,
      refetch: vi.fn(),
    });

    render(
      <MemoryRouter>
        <Dashboard />
      </MemoryRouter>
    );

    expect(screen.getByText('Fetching vault diagnostics...')).toBeInTheDocument();
  });

  test('should render Error state when fetching fails', () => {
    useDashboardStats.mockReturnValue({
      stats: [],
      recentAssets: [],
      vaultCapacity: 0,
      serviceNotice: '',
      isLoading: false,
      error: 'Network connection failed',
      refetch: vi.fn(),
    });

    render(
      <MemoryRouter>
        <Dashboard />
      </MemoryRouter>
    );

    expect(screen.getByText('Dashboard Error')).toBeInTheDocument();
    expect(screen.getByText('Network connection failed')).toBeInTheDocument();
  });

  test('should render stats and inventory when data is populated', () => {
    useDashboardStats.mockReturnValue({
      stats: [
        { label: 'Total Assets', value: '1,234', color: 'bg-red' },
        { label: 'In Use', value: '567', color: 'bg-green' },
      ],
      recentAssets: [
        { id: 'SN-001', name: 'Firewall Router', user: 'Alice', status: 'IN USE', lastUpdated: '2026/07/07' },
      ],
      vaultCapacity: 45,
      serviceNotice: '3 items require maintenance.',
      isLoading: false,
      error: null,
      refetch: vi.fn(),
    });

    render(
      <MemoryRouter>
        <Dashboard />
      </MemoryRouter>
    );

    expect(screen.getByText('Dashboard')).toBeInTheDocument();
    expect(screen.getByText('Total Assets')).toBeInTheDocument();
    expect(screen.getByText('1,234')).toBeInTheDocument();
    expect(screen.getByText('In Use')).toBeInTheDocument();
    expect(screen.getByText('567')).toBeInTheDocument();

    expect(screen.getAllByText('Firewall Router')[0]).toBeInTheDocument();
    expect(screen.getAllByText('Alice')[0]).toBeInTheDocument();
    expect(screen.getAllByText('IN USE')[0]).toBeInTheDocument();

    expect(screen.getByText('Vault Capacity')).toBeInTheDocument();
    expect(screen.getByText('45%')).toBeInTheDocument();
    expect(screen.getByText('3 items require maintenance.')).toBeInTheDocument();
  });
});
