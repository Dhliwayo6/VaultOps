import "./portal.css"
import Sidebar from '../../components/SideBar/Sidebar'
import Dashboard from '../Dashboard/Dashboard'
import Inventory from "../Inventory/Inventory"
import { Outlet } from "react-router-dom"

export default function Portal() {
  return (
    <article className='portal'>  
      <Sidebar />
      <Outlet /> 
    </article>
  )
}