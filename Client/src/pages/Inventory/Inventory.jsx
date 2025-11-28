import InventoryTable from "../../components/InventoryTable/InventoryTable";
import "./inventory.css";
import { filterButtons } from "./InventoryTools";


export default function Inventory() {
  return (
    <section className="inventory">
        <h2 className="inventory-title">VaultOps Inventory</h2>

        <div className="inventory-container">
            <div className="inventory-filter-container">
                {
                    filterButtons.map((filter, index) => {
                        const { type, event } = filter;

                        return <button key={index} onClick={()=> event()}>{type}</button>
                    })
                }
            </div>

            <InventoryTable />
        </div>
    </section>
  )
}