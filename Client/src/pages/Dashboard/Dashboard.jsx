import DashboardCard from "../../components/DashboardCard/DashboardCard";
import "./dashboard.css";
import { IoSearch } from "react-icons/io5";
import { conditions } from "./dashboardTools";

export default function Dashboard() {
  return (
    <section className="dashboard">
        <div className="dash-intro">
            <p>Welcome to your <span>Dashboard</span></p>

            <button className="open-search">
                <IoSearch />
            </button>
        </div>

        <div className="dash-summary">
            <p className="dash-summary-intro">Overall inventory condtition</p>

            <div className="dash-summary-list">
                {
                    conditions().map((x, index) => {
                        const { title, items, color } = x;
                        return <DashboardCard
                            key={index} 
                            title={title}
                            items={items}
                            color={color}
                        />
                    })
                }  
            </div>
            
        </div>
    </section>
  )
}