import "./portal.css"
import Sidebar from '../../components/SideBar/Sidebar'
import Dashboard from '../Dashboard/Dashboard'

export default function Portal() {
  return (
    <article className='portal'>
        
        {/* <div className="nav">
          
        </div> */}
        <Sidebar />
        <Dashboard />
        {/* <div className="content">
        
        </div> */}
        
    </article>
  )
}