import "./dashboard.css";
import { conditions, dashboardCardData, itemsInUse } from "./dashboardTools";
import ConditionCard from "../../components/conditionCard/ConditionCard";
import DashboardCard from "../../components/DashboardCard/DashboardCard";
import Search from "../../components/Search/Search";

export default function Dashboard() {
  return (
    <section className="dashboard">
        <Search pageName={"Dashboard"} />

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
            <div className="dashboard-items-in-repair">
                <DashboardCard card={dashboardCardData[1]} items={itemsInUse} />
            </div>
        </div>
    </section>
  )
}