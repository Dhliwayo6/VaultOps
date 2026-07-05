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
import ProtectedRoute from '@components/ProtectedRoute';
import AdminRoute from '@components/AdminRoute';
import UserManagement from '@features/admin/UserManagement';
import ImportAssets from '@features/admin/ImportAssets';
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
      element: <ProtectedRoute />,
      children: [
        {
          path: "",
          element: <Portal />,
          children: [
            {
              index: true,
              element: <Dashboard />
            },
            {
              path: "assets",
              element: <Assets />
            },
            {
              element: <AdminRoute />,
              children: [
                {
                  path: "users",
                  element: <UserManagement />
                },
                {
                  path: "import",
                  element: <ImportAssets />
                }
              ]
            }
          ]
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
