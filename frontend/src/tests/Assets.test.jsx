import React from 'react';
import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import Assets from '../features/assets/Assets';
import { useAssets } from '../features/assets/hooks/useAssets';
import { useAuth } from '@context/AuthContext';

vi.mock('../features/assets/hooks/useAssets', () => ({
  useAssets: vi.fn(),
}));

vi.mock('@context/AuthContext', () => ({
  useAuth: vi.fn(),
}));

describe('Assets Component Tests', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('should render Loading state when assets are fetching', () => {
    useAuth.mockReturnValue({ user: { role: 'USER' } });
    useAssets.mockReturnValue({
      assets: [],
      isLoading: true,
      error: null,
      page: 0,
      setPage: vi.fn(),
      sortBy: 'createdAt',
      setSortBy: vi.fn(),
      direction: 'DESC',
      setDirection: vi.fn(),
      totalPages: 0,
      totalElements: 0,
      searchTerm: '',
      setSearchTerm: vi.fn(),
      conditionFilter: '',
      setConditionFilter: vi.fn(),
      usageFilter: '',
      setUsageFilter: vi.fn(),
      assignmentFilter: '',
      setAssignmentFilter: vi.fn(),
      refetch: vi.fn(),
    });

    render(
      <MemoryRouter>
        <Assets />
      </MemoryRouter>
    );

    expect(screen.getByText('Acquiring secure inventory list...')).toBeInTheDocument();
  });

  test('should render Error state when fetching fails', () => {
    useAuth.mockReturnValue({ user: { role: 'USER' } });
    useAssets.mockReturnValue({
      assets: [],
      isLoading: false,
      error: 'Fetch failed',
      page: 0,
      setPage: vi.fn(),
      sortBy: 'createdAt',
      setSortBy: vi.fn(),
      direction: 'DESC',
      setDirection: vi.fn(),
      totalPages: 0,
      totalElements: 0,
      searchTerm: '',
      setSearchTerm: vi.fn(),
      conditionFilter: '',
      setConditionFilter: vi.fn(),
      usageFilter: '',
      setUsageFilter: vi.fn(),
      assignmentFilter: '',
      setAssignmentFilter: vi.fn(),
      refetch: vi.fn(),
    });

    render(
      <MemoryRouter>
        <Assets />
      </MemoryRouter>
    );

    expect(screen.getByText('Inventory Fetch Error')).toBeInTheDocument();
    expect(screen.getByText('Fetch failed')).toBeInTheDocument();
  });

  test('should render asset list when data is loaded successfully', () => {
    useAuth.mockReturnValue({ user: { role: 'ADMIN' } });
    useAssets.mockReturnValue({
      assets: [
        { id: 1, name: 'Office Laptop', type: 'Hardware', location: 'Cape Town', assignment: 'UNASSIGNED', conditionStatus: 'EXCELLENT', usageStatus: 'STORAGE', serialNumber: 'SN-001', purchasePrice: 12000, purchaseDate: '2026-01-01' },
      ],
      isLoading: false,
      error: null,
      page: 0,
      setPage: vi.fn(),
      sortBy: 'createdAt',
      setSortBy: vi.fn(),
      direction: 'DESC',
      setDirection: vi.fn(),
      totalPages: 1,
      totalElements: 1,
      searchTerm: '',
      setSearchTerm: vi.fn(),
      conditionFilter: '',
      setConditionFilter: vi.fn(),
      usageFilter: '',
      setUsageFilter: vi.fn(),
      assignmentFilter: '',
      setAssignmentFilter: vi.fn(),
      refetch: vi.fn(),
    });

    render(
      <MemoryRouter>
        <Assets />
      </MemoryRouter>
    );

    expect(screen.getByText('All Assets')).toBeInTheDocument();
    expect(screen.getByText('Office Laptop')).toBeInTheDocument();
    expect(screen.getByText(/SN-001/)).toBeInTheDocument();
    expect(screen.getByText('Cape Town')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /add asset/i })).toBeInTheDocument();
  });
});
