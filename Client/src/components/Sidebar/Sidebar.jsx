import "./sidebar.css";
import { MdDashboard } from "react-icons/md";
import { MdOutlineInventory2 } from "react-icons/md";
import { Link } from "react-router-dom";

export default function Sidebar() {
  return (
    <nav className="dashboard-nav">
        <div>
            <Link to="/">VaultOps</Link>
        </div>
        

        <ul className="dash-links">
            <li>
                <Link to="/">
                    <MdDashboard />
                    <p>Home</p>
                </Link>
            </li>
            <li>
                <Link to="/items">
                    <MdOutlineInventory2 />
                    <p>Inventory</p>
                </Link>
            </li>
            {/* <li>
                <a href="#">
                    <MdDashboard />
                    <p>Dashboard</p>
                </a>
            </li>
            <li>
                <a href="#">
                    <MdDashboard />
                    <p>Dashboard</p>
                </a>
            </li> */}
        </ul>
    </nav>
  )
}