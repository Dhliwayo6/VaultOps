import "./dashboardCard.css";
import { GoArrowRight } from "react-icons/go";

export default function DashboardCard({ card, items }) {
    const { icon, headerName, link, darkBg, iconTheme, titleColor, hover } = card;

  return (
    <section className="dashboard-card momo-trust">
        <div className="dashboard-card-header">
            <div className="dashboard-card-header-name">
                <div className={`${iconTheme}`}>{icon}</div>
                <h2 className={`${titleColor}`}>{headerName}</h2>
            </div>

            <div className={`dashboard-card-header-number ${darkBg}`}>
                <p>{items.length}</p>
            </div>
        </div>

        <div className="dashboard-card-body">
            {
                items.map((item, index) => {
                    const { title, who } = item

                    return <div className={`dashboard-item-card ${hover}`} key={index}>
                        <h2 className={`${titleColor}`}>{title}</h2>
                        <p>{who}</p>
                    </div>
                })
            }
        </div>

        <div className="dashboard-card-footer">
            <a href={link} className={`${darkBg} ${titleColor}`}>
                <p>View More</p>
                <GoArrowRight />
            </a>
        </div>
    </section>
  )
}