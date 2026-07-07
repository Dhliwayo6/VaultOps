import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import OtpActivation from '../features/auth/OtpActivation';
import { verifyOtp as apiVerifyOtp, resendOtp as apiResendOtp } from '@api/authApi';

vi.mock('@api/authApi', () => ({
  verifyOtp: vi.fn(),
  resendOtp: vi.fn(),
}));

const mockNavigate = vi.fn();
vi.mock('react-router-dom', async (importOriginal) => {
  const actual = await importOriginal();
  return {
    ...actual,
    useNavigate: () => mockNavigate,
    useParams: () => ({ email: 'alice@example.com' }),
  };
});

describe('OtpActivation Component Tests', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('should render 6 OTP inputs and submit button', () => {
    render(
      <MemoryRouter>
        <OtpActivation />
      </MemoryRouter>
    );

    expect(screen.getByRole('heading', { name: /verify access/i })).toBeInTheDocument();
    expect(screen.getByText('alice@example.com')).toBeInTheDocument();

    const inputs = screen.getAllByRole('textbox');
    expect(inputs).toHaveLength(6);

    expect(screen.getByRole('button', { name: /confirm identity/i })).toBeInTheDocument();
  });

  test('should enter OTP code and verify successfully', async () => {
    apiVerifyOtp.mockResolvedValue({ success: true });

    render(
      <MemoryRouter>
        <OtpActivation />
      </MemoryRouter>
    );

    const inputs = screen.getAllByRole('textbox');
    inputs.forEach((input, index) => {
      fireEvent.change(input, { target: { value: String(index + 1) } });
    });

    fireEvent.click(screen.getByRole('button', { name: /confirm identity/i }));

    await waitFor(() => {
      expect(apiVerifyOtp).toHaveBeenCalledWith({ email: 'alice@example.com', code: '123456' });
      expect(mockNavigate).toHaveBeenCalledWith('/sign-in');
    });
  });

  test('should trigger resend token call on resend button click', async () => {
    apiResendOtp.mockResolvedValue({ success: true });

    render(
      <MemoryRouter>
        <OtpActivation />
      </MemoryRouter>
    );

    const resendBtn = screen.getByRole('button', { name: /resend/i });
    fireEvent.click(resendBtn);

    await waitFor(() => {
      expect(apiResendOtp).toHaveBeenCalledWith('alice@example.com');
      expect(screen.getByRole('status')).toHaveTextContent('New Security Token Sent');
    });
  });

  test('should display API error message on verification failure', async () => {
    apiVerifyOtp.mockRejectedValue(new Error('Invalid token'));

    render(
      <MemoryRouter>
        <OtpActivation />
      </MemoryRouter>
    );

    const inputs = screen.getAllByRole('textbox');
    inputs.forEach((input, index) => {
      fireEvent.change(input, { target: { value: '9' } });
    });

    fireEvent.click(screen.getByRole('button', { name: /confirm identity/i }));

    await waitFor(() => {
      expect(screen.getByRole('alert')).toHaveTextContent('Invalid token');
    });
  });
});
