import React from 'react';
import { render, screen, fireEvent } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import Sidebar from '../layout/Sidebar/Sidebar';
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
});
