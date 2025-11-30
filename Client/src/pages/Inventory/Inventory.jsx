import { useEffect, useState } from "react";
import InventoryTable from "../../components/InventoryTable/InventoryTable";
import Search from "../../components/Search/Search";
import "./inventory.css";
import { filterButtons, storageItems } from "./InventoryTools";
import Pagination from "../../components/Pagination/Pagination";


export default function Inventory() {
    const [items, setItems] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [itemsPerPage] = useState(10);

    useEffect(() => {
        const dummyData = storageItems;
        setItems(dummyData);
    }, []);

    // Get current items
    const indexOfLastItem = currentPage * itemsPerPage;
    const indexOfFirstItem = indexOfLastItem - itemsPerPage;
    const currentItems = items.slice(indexOfFirstItem, indexOfLastItem);

    // Changes pange
    const paginate = (pageNum) => setCurrentPage(pageNum);
  return (
    <section className="inventory">
        <Search pageName={"Inventory"} />

        <div className="inventory-container">
            <div className="inventory-filter-container">
                {
                    filterButtons.map((filter, index) => {
                        const { type, event } = filter;

                        return <button key={index} onClick={()=> event()}>{type}</button>
                    })
                }
            </div>

            <InventoryTable items={currentItems} />

            <Pagination 
                itemsPerPage={itemsPerPage}
                totalItems={items.length}
                paginate={paginate}
                currentPage={currentPage}
            />
        </div>
    </section>
  )
}