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
    }
  ]);

  return (
    <>
    <RouterProvider router={router} />
    </>
  )
}

export default App
