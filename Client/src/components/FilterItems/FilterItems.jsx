import "./filterItems.css";
import { filterButtons } from "./filterItemsTool";

export default function FilterItems({filter, currentfilter}) {
  return (
    <div className="inventory-filter-container">
        {
            filterButtons.map((x, index) => {
                const { type, event } = x;

                return <button 
                    key={index} 
                    onClick={()=> filter(type)}
                    className={`${type === currentfilter? "active-filter" : "in-active-filter"}`}
                >
                    {type}
                </button>
            })
        }
    </div>
  )
}