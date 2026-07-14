import React from 'react';
import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import Dashboard from '../features/dashboard/Dashboard';
import { useDashboardStats } from '../features/dashboard/hooks/useDashboardStats';
import { ThemeProvider } from '../context/ThemeContext';

vi.mock('../features/dashboard/hooks/useDashboardStats', () => ({
  useDashboardStats: vi.fn(),
}));

describe('Dashboard Component Tests', () => {
  beforeAll(() => {
    Object.defineProperty(window, 'matchMedia', {
      writable: true,
      value: vi.fn().mockImplementation(query => ({
        matches: false,
        media: query,
        onchange: null,
        addListener: vi.fn(),
        removeListener: vi.fn(),
        addEventListener: vi.fn(),
        removeEventListener: vi.fn(),
        dispatchEvent: vi.fn(),
      })),
    });
  });

  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('should render Loading state when stats are fetching', () => {
    useDashboardStats.mockReturnValue({
      stats: [],
      alerts: [],
      recentAssets: [],
      vaultCapacity: 0,
      serviceNotice: '',
      isLoading: true,
      error: null,
      refetch: vi.fn(),
      isAdmin: false,
    });

    render(
      <ThemeProvider>
        <MemoryRouter>
          <Dashboard />
        </MemoryRouter>
      </ThemeProvider>
    );

    expect(screen.getByText('Fetching vault diagnostics...')).toBeInTheDocument();
  });

  test('should render Error state when fetching fails', () => {
    useDashboardStats.mockReturnValue({
      stats: [],
      alerts: [],
      recentAssets: [],
      vaultCapacity: 0,
      serviceNotice: '',
      isLoading: false,
      error: 'Network connection failed',
      refetch: vi.fn(),
      isAdmin: false,
    });

    render(
      <ThemeProvider>
        <MemoryRouter>
          <Dashboard />
        </MemoryRouter>
      </ThemeProvider>
    );

    expect(screen.getByText('Dashboard Error')).toBeInTheDocument();
    expect(screen.getByText('Network connection failed')).toBeInTheDocument();
  });

  test('should render stats, nominal alert state, and USER quick actions', () => {
    useDashboardStats.mockReturnValue({
      stats: [
        { label: 'Total Assets', value: '1,234', color: 'bg-red' },
        { label: 'In Use', value: '567', color: 'bg-green' },
      ],
      alerts: [],
      recentAssets: [
        { id: 'SN-001', name: 'Firewall Router', user: 'Alice', status: 'IN USE', lastUpdated: '2026/07/07' },
      ],
      vaultCapacity: 45,
      serviceNotice: '3 items require maintenance.',
      isLoading: false,
      error: null,
      refetch: vi.fn(),
      isAdmin: false,
    });

    render(
      <ThemeProvider>
        <MemoryRouter>
          <Dashboard />
        </MemoryRouter>
      </ThemeProvider>
    );

    expect(screen.getByText('Dashboard')).toBeInTheDocument();
    expect(screen.getByText('Total Assets')).toBeInTheDocument();
    expect(screen.getByText('1,234')).toBeInTheDocument();

    expect(screen.getAllByText('Firewall Router')[0]).toBeInTheDocument();
    expect(screen.getAllByText('Alice')[0]).toBeInTheDocument();

    expect(screen.getByText('Vault Capacity')).toBeInTheDocument();
    expect(screen.getByText('45%')).toBeInTheDocument();
    
    // Check nominal alert state
    expect(screen.getByText('All systems nominal')).toBeInTheDocument();
    expect(screen.getByText('No issues require attention.')).toBeInTheDocument();

    // Check USER quick actions
    expect(screen.getByText('Request Maintenance')).toBeInTheDocument();
    expect(screen.getByText('Check-In / Out')).toBeInTheDocument();
    expect(screen.getByText('Report Damage')).toBeInTheDocument();
    
    // ADMIN quick actions should not be present
    expect(screen.queryByText('Bulk Import')).not.toBeInTheDocument();
    expect(screen.queryByText('Export Registry')).not.toBeInTheDocument();
  });

  test('should render active alerts and ADMIN quick actions', () => {
    useDashboardStats.mockReturnValue({
      stats: [
        { label: 'Total Assets', value: '1,234', color: 'bg-red' },
      ],
      alerts: [
        { id: 'damaged', type: 'danger', title: 'Damaged Assets Detected', message: '2 assets damaged.', link: '/assets?condition=DAMAGED', actionLabel: 'Resolve' }
      ],
      recentAssets: [],
      vaultCapacity: 80,
      serviceNotice: '',
      isLoading: false,
      error: null,
      refetch: vi.fn(),
      isAdmin: true,
    });

    render(
      <ThemeProvider>
        <MemoryRouter>
          <Dashboard />
        </MemoryRouter>
      </ThemeProvider>
    );

    // Verify active alert
    expect(screen.getByText('Damaged Assets Detected')).toBeInTheDocument();
    expect(screen.getByText('2 assets damaged.')).toBeInTheDocument();
    expect(screen.getByText('Resolve')).toBeInTheDocument();

    // Verify ADMIN quick actions are present
    expect(screen.getByText('Request Maintenance')).toBeInTheDocument();
    expect(screen.getByText('Check-In / Out')).toBeInTheDocument();
    expect(screen.getByText('Report Damage')).toBeInTheDocument();
    expect(screen.getByText('Bulk Import')).toBeInTheDocument();
    expect(screen.getByText('Export Registry')).toBeInTheDocument();
  });
});
