import { useState } from 'react'
import './App.css'
import { createBrowserRouter, RouterProvider } from 'react-router-dom'
import Portal from "./pages/Portal/Portal"
import Dashboard from './pages/Dashboard/Dashboard';
import Inventory from './pages/Inventory/Inventory';
import ItemDetail from './pages/ItemDetail/ItemDetail';

function App() {
  const router = createBrowserRouter([
    {
      path: "/",
      element: <Portal />,
      children: [
        {
          index: true,
          element: <Dashboard />
        },
        {
          path: "/items",
          element: <Inventory />
        }
      ]
    },
    {
      path: "/:itemId",
      element: <ItemDetail />
    }
  ]);

  return (
    <RouterProvider router={router} />
  )
}

export default App
