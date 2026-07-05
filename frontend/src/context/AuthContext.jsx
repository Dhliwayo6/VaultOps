import React, { createContext, useContext, useReducer, useEffect } from 'react';
import { apiFetch, setAccessToken, registerLogoutCallback } from '@utils/api';

const AuthContext = createContext(null);

const authReducer = (state, action) => {
  switch (action.type) {
    case 'LOGIN':
      return { ...state, isAuthenticated: true, user: action.payload, isLoading: false };
    case 'LOGOUT':
      return { ...state, isAuthenticated: false, user: null, isLoading: false };
    case 'FINISH_LOADING':
      return { ...state, isLoading: false };
    default:
      return state;
  }
};

const initialState = {
  isAuthenticated: false,
  user: null,
  isLoading: true
};

export function AuthProvider({ children }) {
  const [state, dispatch] = useReducer(authReducer, initialState);

  const login = (userData, token) => {
    setAccessToken(token);
    dispatch({ type: 'LOGIN', payload: userData });
  };

  const logout = async () => {
    try {
      await apiFetch('/api/auth/logout', { method: 'POST' });
    } catch (e) {
      console.error('Logout request failed', e);
    } finally {
      setAccessToken('');
      dispatch({ type: 'LOGOUT' });
    }
  };

  // Session restoration on mount
  useEffect(() => {
    const restoreSession = async () => {
      try {
        const response = await apiFetch('/api/auth/refresh', { method: 'POST' });
        if (response.ok) {
          const data = await response.json();
          setAccessToken(data.accessToken);
          dispatch({ type: 'LOGIN', payload: data.user });
        } else {
          dispatch({ type: 'FINISH_LOADING' });
        }
      } catch (err) {
        dispatch({ type: 'FINISH_LOADING' });
      }
    };

    // Register callback for API client to logout on 401 failure
    registerLogoutCallback(() => {
      dispatch({ type: 'LOGOUT' });
    });

    restoreSession();
  }, []);

  return (
    <AuthContext.Provider value={{ ...state, login, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
