import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import SignIn from '../features/auth/SignIn';
import { login as apiLogin } from '@api/authApi';
import { useAuth } from '@context/AuthContext';
import { isValidPassword } from '@utils/user';

vi.mock('@api/authApi', () => ({
  login: vi.fn(),
}));

vi.mock('@context/AuthContext', () => ({
  useAuth: vi.fn(),
}));

vi.mock('@utils/user', () => ({
  isValidPassword: vi.fn(),
}));

const mockNavigate = vi.fn();
vi.mock('react-router-dom', async (importOriginal) => {
  const actual = await importOriginal();
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

describe('SignIn Component Tests', () => {
  const mockLogin = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
    useAuth.mockReturnValue({
      login: mockLogin,
    });
  });

  test('should render sign in form fields', () => {
    render(
      <MemoryRouter>
        <SignIn />
      </MemoryRouter>
    );

    expect(screen.getByRole('heading', { name: /sign in/i })).toBeInTheDocument();
    expect(screen.getByLabelText(/email/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/^password$/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /login/i })).toBeInTheDocument();
  });

  test('should show validation error on submission of invalid password format', async () => {
    isValidPassword.mockReturnValue(false);

    render(
      <MemoryRouter>
        <SignIn />
      </MemoryRouter>
    );

    fireEvent.change(screen.getByLabelText(/email/i), { target: { value: 'user@example.com' } });
    fireEvent.change(screen.getByLabelText(/^password$/i), { target: { value: 'short' } });
    fireEvent.click(screen.getByRole('button', { name: /login/i }));

    expect(screen.getByRole('alert')).toHaveTextContent('Invalid email or password format.');
    expect(apiLogin).not.toHaveBeenCalled();
  });

  test('should submit successfully with correct credentials and redirect', async () => {
    isValidPassword.mockReturnValue(true);
    apiLogin.mockResolvedValue({
      user: { id: 1, name: 'John Doe', role: 'USER' },
      accessToken: 'mock-jwt-token',
    });

    render(
      <MemoryRouter>
        <SignIn />
      </MemoryRouter>
    );

    fireEvent.change(screen.getByLabelText(/email/i), { target: { value: 'user@example.com' } });
    fireEvent.change(screen.getByLabelText(/^password$/i), { target: { value: 'Password123!' } });
    fireEvent.click(screen.getByRole('button', { name: /login/i }));

    await waitFor(() => {
      expect(apiLogin).toHaveBeenCalledWith({ email: 'user@example.com', password: 'Password123!' });
      expect(mockLogin).toHaveBeenCalledWith({ id: 1, name: 'John Doe', role: 'USER' }, 'mock-jwt-token');
      expect(mockNavigate).toHaveBeenCalledWith('/portal');
    });
  });

  test('should display API error message on login failure', async () => {
    isValidPassword.mockReturnValue(true);
    apiLogin.mockRejectedValue(new Error('Invalid credentials'));

    render(
      <MemoryRouter>
        <SignIn />
      </MemoryRouter>
    );

    fireEvent.change(screen.getByLabelText(/email/i), { target: { value: 'user@example.com' } });
    fireEvent.change(screen.getByLabelText(/^password$/i), { target: { value: 'Password123!' } });
    fireEvent.click(screen.getByRole('button', { name: /login/i }));

    await waitFor(() => {
      expect(screen.getByRole('alert')).toHaveTextContent('Invalid credentials');
    });
  });

  test('should toggle password visibility when clicking show/hide button', () => {
    render(
      <MemoryRouter>
        <SignIn />
      </MemoryRouter>
    );

    const passwordInput = screen.getByLabelText(/^password$/i);
    expect(passwordInput.type).toBe('password');

    const toggleButton = screen.getByRole('button', { name: /show password/i });
    fireEvent.click(toggleButton);

    expect(passwordInput.type).toBe('text');
    expect(screen.getByRole('button', { name: /hide password/i })).toBeInTheDocument();

    fireEvent.click(screen.getByRole('button', { name: /hide password/i }));
    expect(passwordInput.type).toBe('password');
  });
});
