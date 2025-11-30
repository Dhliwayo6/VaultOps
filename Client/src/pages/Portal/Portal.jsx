import "./portal.css"
import Sidebar from '../../components/SideBar/Sidebar'
import Dashboard from '../Dashboard/Dashboard'
import Inventory from "../Inventory/Inventory"

export default function Portal() {
  return (
    <article className='portal'>  
      <Sidebar />
      <Inventory />  
    </article>
  )
}