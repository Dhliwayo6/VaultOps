import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'
import Sidebar from './components/SideBar/Sidebar.jsx'
import Portal from './pages/Portal/Portal.jsx'
// import ItemView from './pages/ItemView/ItemView.jsx'
import ItemDetail from './pages/ItemDetail/ItemDetail.jsx'

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <App />
  </StrictMode>,
)
