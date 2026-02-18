import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'
import { createBrowserRouter, Router, RouterProvider } from 'react-router-dom'
import Portal from './pages/Portal'
import Dashboard from './components/Dashboard/Dashboard'
import Assets from './components/Assets/Assets'

function App() {
  const router = createBrowserRouter([
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
