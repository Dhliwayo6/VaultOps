import "./inventoryTable.css";

export default function InventoryTable({items}) {


  return (
    <div className="table-wrapper">
      <table className="table">
        <thead>
          <tr>
            <th>Item ID</th>
            <th>Item Name</th>
            <th>Location</th>
            <th>Date Added</th>
            <th>Status</th>
          </tr>
        </thead>

        <tbody>
          {items.map((item, index) => {
            const { itemId, itemName, location, dateAdded, status } = item;

            return (
            <tr key={index}>
              <td>{itemId}</td>
              <td>{itemName}</td>
              <td>{location}</td>
              <td>{dateAdded}</td>
              <td className={`status ${status.toLowerCase().replace(" ", "-")}`}>
                {status}
              </td>
            </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  )
}