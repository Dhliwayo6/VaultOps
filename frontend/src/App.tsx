import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'
import { createBrowserRouter, Router, RouterProvider } from 'react-router-dom'
import Portal from './pages/Portal'
import Dashboard from './components/Dashboard/Dashboard'
import Assets from './components/Assets/Assets'
import SignIn from './pages/SignIn'
import Home from './pages/Home'
import SignUp from './pages/SignUp'
import OtpActivation from './pages/OtpActivation'


function App() {
  const router = createBrowserRouter([
    {
      path: "",
      element: <Home />,
    },

    {
      path: "sign-in",
      element: <SignIn />
    },

    {
      path: "sign-up",
      element: <SignUp />
    },

    {
      path: "otp",
      element: <OtpActivation />
    },

    {
      path: "portal",
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
    <>
    <RouterProvider router={router} />
    </>
  )
}

export default App
