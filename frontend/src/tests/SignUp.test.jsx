import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import SignUp from '../features/auth/SignUp';
import { register as apiRegister } from '@api/authApi';
import { isValidPassword } from '@utils/user';

vi.mock('@api/authApi', () => ({
  register: vi.fn(),
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

describe('SignUp Component Tests', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('should render registration form fields', () => {
    render(
      <MemoryRouter>
        <SignUp />
      </MemoryRouter>
    );

    expect(screen.getByRole('heading', { name: /sign up/i })).toBeInTheDocument();
    expect(screen.getByLabelText(/name/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/email/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/phone/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/^password/i)).toBeInTheDocument();
    expect(screen.getByLabelText(/^confirm password/i)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /create account/i })).toBeInTheDocument();
  });

  test('should display validation error if password is weak', async () => {
    isValidPassword.mockReturnValue(false);

    render(
      <MemoryRouter>
        <SignUp />
      </MemoryRouter>
    );

    fireEvent.change(screen.getByLabelText(/name/i), { target: { value: 'Alice' } });
    fireEvent.change(screen.getByLabelText(/email/i), { target: { value: 'alice@example.com' } });
    fireEvent.change(screen.getByLabelText(/^password/i), { target: { value: 'weak' } });
    fireEvent.change(screen.getByLabelText(/^confirm password/i), { target: { value: 'weak' } });
    fireEvent.click(screen.getByRole('button', { name: /create account/i }));

    await waitFor(() => {
      expect(screen.getByRole('alert')).toHaveTextContent('Password does not meet all security requirements.');
    });
    expect(apiRegister).not.toHaveBeenCalled();
  });

  test('should display validation error if passwords do not match', async () => {
    isValidPassword.mockReturnValue(true);

    render(
      <MemoryRouter>
        <SignUp />
      </MemoryRouter>
    );

    fireEvent.change(screen.getByLabelText(/name/i), { target: { value: 'Alice' } });
    fireEvent.change(screen.getByLabelText(/email/i), { target: { value: 'alice@example.com' } });
    fireEvent.change(screen.getByLabelText(/^password/i), { target: { value: 'Password123!' } });
    fireEvent.change(screen.getByLabelText(/^confirm password/i), { target: { value: 'Different123!' } });
    fireEvent.click(screen.getByRole('button', { name: /create account/i }));

    await waitFor(() => {
      expect(screen.getByRole('alert')).toHaveTextContent('Passwords do not match.');
    });
    expect(apiRegister).not.toHaveBeenCalled();
  });

  test('should submit registration and navigate to OTP page', async () => {
    isValidPassword.mockReturnValue(true);
    apiRegister.mockResolvedValue({ success: true });

    render(
      <MemoryRouter>
        <SignUp />
      </MemoryRouter>
    );

    fireEvent.change(screen.getByLabelText(/name/i), { target: { value: 'Alice' } });
    fireEvent.change(screen.getByLabelText(/email/i), { target: { value: 'alice@example.com' } });
    fireEvent.change(screen.getByLabelText(/phone/i), { target: { value: '+27821234567' } });
    fireEvent.change(screen.getByLabelText(/^password/i), { target: { value: 'Password123!' } });
    fireEvent.change(screen.getByLabelText(/^confirm password/i), { target: { value: 'Password123!' } });
    fireEvent.click(screen.getByRole('button', { name: /create account/i }));

    await waitFor(() => {
      expect(apiRegister).toHaveBeenCalledWith({
        name: 'Alice',
        email: 'alice@example.com',
        phone: '+27821234567',
        password: 'Password123!',
      });
      expect(mockNavigate).toHaveBeenCalledWith(`/otp/alice%40example.com`);
    });
  });

  test('should display API error message on registration failure', async () => {
    isValidPassword.mockReturnValue(true);
    apiRegister.mockRejectedValue(new Error('Email already registered'));

    render(
      <MemoryRouter>
        <SignUp />
      </MemoryRouter>
    );

    fireEvent.change(screen.getByLabelText(/name/i), { target: { value: 'Alice' } });
    fireEvent.change(screen.getByLabelText(/email/i), { target: { value: 'alice@example.com' } });
    fireEvent.change(screen.getByLabelText(/^password/i), { target: { value: 'Password123!' } });
    fireEvent.change(screen.getByLabelText(/^confirm password/i), { target: { value: 'Password123!' } });
    fireEvent.click(screen.getByRole('button', { name: /create account/i }));

    await waitFor(() => {
      expect(screen.getByRole('alert')).toHaveTextContent('Email already registered');
    });
  });

  test('should toggle password and confirm password visibility when clicking show/hide buttons', () => {
    render(
      <MemoryRouter>
        <SignUp />
      </MemoryRouter>
    );

    const passwordInput = screen.getByLabelText(/^password/i);
    const confirmPasswordInput = screen.getByLabelText(/^confirm password/i);

    expect(passwordInput.type).toBe('password');
    expect(confirmPasswordInput.type).toBe('password');

    const togglePasswordBtn = screen.getByRole('button', { name: /show password/i });
    fireEvent.click(togglePasswordBtn);
    expect(passwordInput.type).toBe('text');

    const toggleConfirmPasswordBtn = screen.getByRole('button', { name: /show confirm password/i });
    fireEvent.click(toggleConfirmPasswordBtn);
    expect(confirmPasswordInput.type).toBe('text');

    fireEvent.click(screen.getByRole('button', { name: /hide password/i }));
    expect(passwordInput.type).toBe('password');

    fireEvent.click(screen.getByRole('button', { name: /hide confirm password/i }));
    expect(confirmPasswordInput.type).toBe('password');
  });
});
