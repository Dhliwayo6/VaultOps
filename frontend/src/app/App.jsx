import React, { lazy, Suspense } from 'react';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import { AuthProvider } from '@context/AuthContext';
import { ROUTES } from '@constants/routes';
import Home from './pages/Home';
import ProtectedRoute from '@components/ProtectedRoute';
import AdminRoute from '@components/AdminRoute';
import Loading from '@components/Loading';
import '@styles/App.css';

// Lazy load non-critical pages & routes
const SignIn = lazy(() => import('@features/auth/SignIn'));
const SignUp = lazy(() => import('@features/auth/SignUp'));
const OtpActivation = lazy(() => import('@features/auth/OtpActivation'));
const Portal = lazy(() => import('./pages/Portal'));
const Dashboard = lazy(() => import('@features/dashboard/Dashboard'));
const Assets = lazy(() => import('@features/assets/Assets'));
const UserManagement = lazy(() => import('@features/admin/UserManagement'));
const ImportAssets = lazy(() => import('@features/admin/ImportAssets'));
const Reports = lazy(() => import('@features/reports/Reports'));
const LocationManagement = lazy(() => import('@features/admin/LocationManagement'));

const withSuspense = (Component) => (
  <Suspense fallback={<Loading message="Connecting to vault..." />}>
    <Component />
  </Suspense>
);

function App() {
  const router = createBrowserRouter([
    {
      path: ROUTES.HOME,
      element: <Home />,
    },
    {
      path: ROUTES.SIGN_IN,
      element: withSuspense(SignIn)
    },
    {
      path: ROUTES.SIGN_UP,
      element: withSuspense(SignUp)
    },
    {
      path: ROUTES.OTP,
      element: withSuspense(OtpActivation)
    },
    {
      path: ROUTES.PORTAL,
      element: <ProtectedRoute />,
      children: [
        {
          path: "",
          element: withSuspense(Portal),
          children: [
            {
              index: true,
              element: withSuspense(Dashboard)
            },
            {
              path: "assets",
              element: withSuspense(Assets)
            },
            {
              path: "reports",
              element: withSuspense(Reports)
            },
            {
              element: <AdminRoute />,
              children: [
                {
                  path: "users",
                  element: withSuspense(UserManagement)
                },
                {
                  path: "import",
                  element: withSuspense(ImportAssets)
                },
                {
                  path: "locations",
                  element: withSuspense(LocationManagement)
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
