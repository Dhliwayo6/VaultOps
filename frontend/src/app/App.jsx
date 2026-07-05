import React from 'react';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import { AuthProvider } from '@context/AuthContext';
import { ROUTES } from '@constants/routes';
import Home from './pages/Home';
import Portal from './pages/Portal';
import SignIn from '@features/auth/SignIn';
import SignUp from '@features/auth/SignUp';
import OtpActivation from '@features/auth/OtpActivation';
import Dashboard from '@features/dashboard/Dashboard';
import Assets from '@features/assets/Assets';
import '@styles/App.css';

function App() {
  const router = createBrowserRouter([
    {
      path: ROUTES.HOME,
      element: <Home />,
    },
    {
      path: ROUTES.SIGN_IN,
      element: <SignIn />
    },
    {
      path: ROUTES.SIGN_UP,
      element: <SignUp />
    },
    {
      path: ROUTES.OTP,
      element: <OtpActivation />
    },
    {
      path: ROUTES.PORTAL,
      element: <Portal />,
      children: [
        {
          index: true,
          element: <Dashboard />
        },
        {
          path: "assets",
          element: <Assets />
        }
      ]
    }
  ]);

  return (
    <AuthProvider>
      <RouterProvider router={router} />
    </AuthProvider>
  );
}

export default App;
