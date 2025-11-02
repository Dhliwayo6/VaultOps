import "./conditionCard.css"

export default function ConditionCard({item}) {
    const { icon, color, title , itemCount } = item;

  return (
    <div className="condition-card momo-trust" style={{"borderLeft": `5px solid ${color}`}}>
        <div className="condition-card-head">
            {icon}

            <h2 style={{"color": color}}>{itemCount}</h2>
        </div>

        <div className="consdition-card-body">
            <h2>{title}</h2>
            <p>Items tracked</p>
        </div>
    </div>
  )
}