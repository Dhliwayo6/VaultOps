import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '@context/AuthContext';
import { ROUTES } from '@constants/routes';

export default function ProtectedRoute() {
  const { isAuthenticated, isLoading } = useAuth();

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-slate-50">
        <div className="w-8 h-8 border-4 border-slate-200 border-t-[#0EA5E9] rounded-full animate-spin" />
      </div>
    );
  }

  return isAuthenticated ? <Outlet /> : <Navigate to={ROUTES.SIGN_IN} replace />;
}
