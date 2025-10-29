import "./dashboardCard.css"

export default function DashboardCard({title, items, color}) {
  return (
    <div className="dasboard-card" style={{background: `linear-gradient(315deg, rgba(255,255,255,0.35) 0%, rgba(255,255,255,0.1) 60%), ${color}`}}>
        <h2>{title}</h2>
        <h2>{items}</h2>
    </div>
  )
}