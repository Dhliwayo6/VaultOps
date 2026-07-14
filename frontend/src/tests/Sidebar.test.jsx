import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import Sidebar from '../layout/Sidebar/Sidebar';
import Portal from '../app/pages/Portal';
import { useAuth } from '@context/AuthContext';

vi.mock('@context/AuthContext', () => ({
  useAuth: vi.fn(),
}));

vi.mock('@components/ThemeToggle', () => ({
  default: () => <div data-testid="theme-toggle">ThemeToggle</div>,
}));

describe('Sidebar Component Tests', () => {
  const mockLogout = vi.fn();

  test('should render only user items when role is USER', () => {
    useAuth.mockReturnValue({
      user: { name: 'Regular User', role: 'USER' },
      logout: mockLogout,
    });

    render(
      <MemoryRouter initialEntries={['/portal']}>
        <Sidebar />
      </MemoryRouter>
    );

    expect(screen.getByText('Home')).toBeInTheDocument();
    expect(screen.getByText('Assets')).toBeInTheDocument();
    expect(screen.getByText('Reports')).toBeInTheDocument();

    expect(screen.queryByText('Users')).not.toBeInTheDocument();
    expect(screen.queryByText('Import')).not.toBeInTheDocument();
  });

  test('should render user and admin items when role is ADMIN', () => {
    useAuth.mockReturnValue({
      user: { name: 'Admin User', role: 'ADMIN' },
      logout: mockLogout,
    });

    render(
      <MemoryRouter initialEntries={['/portal']}>
        <Sidebar />
      </MemoryRouter>
    );

    expect(screen.getByText('Home')).toBeInTheDocument();
    expect(screen.getByText('Assets')).toBeInTheDocument();
    expect(screen.getByText('Users')).toBeInTheDocument();
    expect(screen.getByText('Import')).toBeInTheDocument();
    expect(screen.getByText('Reports')).toBeInTheDocument();
  });

  test('should invoke logout when logout button is clicked', () => {
    useAuth.mockReturnValue({
      user: { name: 'Admin User', role: 'ADMIN' },
      logout: mockLogout,
    });

    render(
      <MemoryRouter initialEntries={['/portal']}>
        <Sidebar />
      </MemoryRouter>
    );

    const logoutBtn = screen.getByRole('button', { name: /logout/i });
    fireEvent.click(logoutBtn);
    expect(mockLogout).toHaveBeenCalled();
  });

  test('should apply active classes to the NavLink corresponding to active route', () => {
    useAuth.mockReturnValue({
      user: { name: 'Regular User', role: 'USER' },
      logout: mockLogout,
    });

    render(
      <MemoryRouter initialEntries={['/portal/assets']}>
        <Sidebar />
      </MemoryRouter>
    );

    // NavLinks have titles
    const assetsLink = screen.getByRole('link', { name: /assets/i });
    const homeLink = screen.getByRole('link', { name: /home/i });

    expect(assetsLink.className).toContain('bg-sidebar-active-bg');
    expect(homeLink.className).not.toContain('bg-sidebar-active-bg');
  });

  test('should render toggle button and trigger onToggleCollapse when clicked', () => {
    useAuth.mockReturnValue({
      user: { name: 'Admin User', role: 'ADMIN' },
      logout: mockLogout,
    });
    const mockToggle = vi.fn();

    render(
      <MemoryRouter initialEntries={['/portal']}>
        <Sidebar isCollapsed={false} onToggleCollapse={mockToggle} />
      </MemoryRouter>
    );

    const toggleBtn = screen.getByRole('button', { name: /collapse sidebar/i });
    expect(toggleBtn).toBeInTheDocument();
    expect(toggleBtn).toHaveAttribute('aria-expanded', 'true');

    fireEvent.click(toggleBtn);
    expect(mockToggle).toHaveBeenCalledTimes(1);
  });

  test('should display tooltips and correct aria attributes when collapsed', () => {
    useAuth.mockReturnValue({
      user: { name: 'Admin User', role: 'ADMIN' },
      logout: mockLogout,
    });

    render(
      <MemoryRouter initialEntries={['/portal']}>
        <Sidebar isCollapsed={true} onToggleCollapse={vi.fn()} />
      </MemoryRouter>
    );

    const toggleBtn = screen.getByRole('button', { name: /expand sidebar/i });
    expect(toggleBtn).toBeInTheDocument();
    expect(toggleBtn).toHaveAttribute('aria-expanded', 'false');

    // Check tooltips and aria-labels on nav items
    const homeLink = screen.getByRole('link', { name: /home/i });
    expect(homeLink).toHaveAttribute('title', 'Home');
    expect(homeLink).toHaveAttribute('aria-label', 'Home');
  });

  test('should apply active classes to NavLink when collapsed', () => {
    useAuth.mockReturnValue({
      user: { name: 'Regular User', role: 'USER' },
      logout: mockLogout,
    });

    render(
      <MemoryRouter initialEntries={['/portal/assets']}>
        <Sidebar isCollapsed={true} />
      </MemoryRouter>
    );

    const assetsLink = screen.getByRole('link', { name: /assets/i });
    expect(assetsLink.className).toContain('bg-sidebar-active-bg');
  });

  test('should toggle collapsed state and persist to localStorage', () => {
    localStorage.clear();
    useAuth.mockReturnValue({
      user: { name: 'Admin User', role: 'ADMIN' },
      logout: mockLogout,
    });

    render(
      <MemoryRouter initialEntries={['/portal']}>
        <Portal />
      </MemoryRouter>
    );

    const toggleBtn = screen.getByRole('button', { name: /collapse sidebar/i });
    expect(toggleBtn).toBeInTheDocument();
    expect(localStorage.getItem('vaultops_sidebar_collapsed')).toBeNull();

    fireEvent.click(toggleBtn);
    expect(localStorage.getItem('vaultops_sidebar_collapsed')).toBe('true');

    expect(screen.getByRole('button', { name: /expand sidebar/i })).toBeInTheDocument();

    fireEvent.click(screen.getByRole('button', { name: /expand sidebar/i }));
    expect(localStorage.getItem('vaultops_sidebar_collapsed')).toBe('false');
  });
});

