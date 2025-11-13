import "./sidebar.css";
import { MdDashboard } from "react-icons/md";
import { MdOutlineInventory2 } from "react-icons/md";

export default function Sidebar() {
  return (
    <nav className="dashboard-nav">
        <div>
            <a href="#">VoltOps</a>
        </div>
        

        <ul className="dash-links">
            <li>
                <a href="#">
                    <MdDashboard />
                    <p>Home</p>
                </a>
            </li>
            <li>
                <a href="#">
                    <MdOutlineInventory2 />
                    <p>Inventory</p>
                </a>
            </li>
            <li>
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
            </li>
        </ul>
    </nav>
  )
}