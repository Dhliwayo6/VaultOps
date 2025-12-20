import React from "react";
import { useParams } from "react-router-dom";
import "./itemView.css";
import { dummyItems } from "../Inventory/InventoryTools";

const ItemDetail = () => {
  const { id } = useParams();

  const item = dummyItems.find((item) => item.itemId === "ITM-002");

  const statusColors = {
    "Available": "#16A34A",
    "Low Stock": "#F59E0B",
    "Out of Stock": "#DC2626",
  };

  if (!item) {
    return (
      <div className="item-detail-container">
        <div className="item-detail-wrap">
          <p className="muted">Item not found</p>

          <button className="btn" onClick={() => {}}>
            ← Back to Items
          </button>
        </div>
      </div>
    );
  }

  const color = statusColors[item.status];

  return (
    <div className="item-detail-container">
      <div className="item-detail-wrap">
             <button className="btn back-btn" onClick={() => {}}>
        ← Back to Items
      </button>

      <div className="card">
        <div className="header">
          <div>
            <h1 className="title">{item.name}</h1>
            <p className="muted text-lg">Item ID: {item.itemId}</p>
          </div>

          <span
            className="badge"
            style={{
              backgroundColor: `${color}15`,
              color: color,
              borderColor: `${color}40`,
            }}
          >
            {item.status}
          </span>
        </div>

        <div className="section">
          <h2 className="section-title">Description</h2>
          <p>{item.description}</p>
        </div>

        <div className="grid">
          <div>
            <h3 className="section-title">Inventory Details</h3>

            <div className="row">
              <span className="muted">Date Added:</span>
              <span>
                {new Date(item.dateAdded).toLocaleDateString("en-US", {
                  year: "numeric",
                  month: "short",
                  day: "numeric",
                })}
              </span>
            </div>

            <div className="row">
              <span className="muted">Current Quantity:</span>
              <span className="quantity" style={{ color }}>
                {item.quantity}
              </span>
            </div>

            <div className="row">
              <span className="muted">Minimum Required:</span>
              <span>{item.minQuantity}</span>
            </div>
          </div>

          <div>
            <h3 className="section-title">Storage Location</h3>

            <div className="row">
              <span className="muted">Building:</span>
              <span>{item.location.building}</span>
            </div>

            <div className="row">
              <span className="muted">Room:</span>
              <span>{item.location.room}</span>
            </div>

            <div className="row">
              <span className="muted">Shelf:</span>
              <span>{item.location.shelf}</span>
            </div>
          </div>
        </div>
      </div> 
      </div>

    </div>
  );
};

export default ItemDetail;
