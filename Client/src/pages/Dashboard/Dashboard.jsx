import "./dashboard.css";
import { IoSearch } from "react-icons/io5";
import { conditions, dashboardCardData, itemsInUse } from "./dashboardTools";
import ConditionCard from "../../components/conditionCard/ConditionCard";
import DashboardCard from "../../components/DashboardCard/DashboardCard";

export default function Dashboard() {
  return (
    <section className="dashboard">
        <div className="dash-intro">
            <p>Welcome to your <span>Dashboard</span></p>

            <button className="open-search">
                <IoSearch />
            </button>
        </div>

        <div className="dash-condition-summary">
            <p className="dash-condition-summary-intro">Overall inventory condtition</p>

            <div className="dash-condition-summary-list">
                {
                    conditions().map((x, index) => {
                        return <ConditionCard item={x} key={index} />
                    })
                }
            </div>
        </div>

        <div className="dashboard-card-container">
            <div className="dashboard-items-in-use">
                <DashboardCard card={dashboardCardData[0]} items={itemsInUse} />
            </div>
            <div className="dashboard-items-in-use">
                <DashboardCard card={dashboardCardData[1]} items={itemsInUse} />
            </div>
        </div>
    </section>
  )
}