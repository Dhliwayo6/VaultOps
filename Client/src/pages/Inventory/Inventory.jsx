import { useEffect, useState } from "react";
import InventoryTable from "../../components/InventoryTable/InventoryTable";
import Search from "../../components/Search/Search";
import "./inventory.css";
import Pagination from "../../components/Pagination/Pagination";
import { use } from "react";
import FilterItems from "../../components/FilterItems/FilterItems";
import { dummyItems } from "./InventoryTools/"


export default function Inventory() {
    const [items, setItems] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [itemsPerPage] = useState(10);

    const [filteredItems, setFilteredItems] = useState([]);
    const [currentFilter, setCurrentFilter] = useState("All Items");

    useEffect(() => {
        const dummyData = dummyItems;
        setItems(dummyData);
        setFilteredItems(dummyData);
    }, []);

    // Get current items
    const indexOfLastItem = currentPage * itemsPerPage;
    const indexOfFirstItem = indexOfLastItem - itemsPerPage;
    const currentItems = filteredItems.slice(indexOfFirstItem, indexOfLastItem);

    // Changes pange
    const paginate = (pageNum) => setCurrentPage(pageNum);

    const filterInventory = (type)=>{
        let filterItems;

        if (type === "All Items") filterItems = items;

        else filterItems = items.filter(filter => filter.status === type)
        

        setFilteredItems(filterItems);
        setCurrentFilter(type)
    }
  return (
    <section className="inventory">
        <Search pageName={"Inventory"} />

        <div className="inventory-container">
            <FilterItems filter={filterInventory} currentfilter={currentFilter} />

            <InventoryTable items={currentItems} />

            <Pagination 
                itemsPerPage={itemsPerPage}
                totalItems={filteredItems.length}
                paginate={paginate}
                currentPage={currentPage}
            />
        </div>
    </section>
  )
}