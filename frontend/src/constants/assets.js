export const Assignment = {
  ASSIGNED: "ASSIGNED",
  UNASSIGNED: "UNASSIGNED"
};

export const ConditionStatus = {
  EXCELLENT: "EXCELLENT",
  GOOD: "GOOD",
  FAIR: "FAIR",
  BAD: "BAD",
  DAMAGED: "DAMAGED"
};

export const Usage = {
  IN_USE: "IN_USE",
  STORAGE: "STORAGE",
  SERVICE: "SERVICE"
};

export const DUMMY_ASSETS = [
  {
    id: 1,
    name: "MacBook Pro M3 14\"",
    type: "Laptop",
    location: "Main Office - Floor 2",
    assignment: Assignment.ASSIGNED,
    serialNumber: "SN-MBP-2024-X99",
    purchasePrice: 2499.00,
    purchaseDate: "2025-01-15",
    conditionStatus: ConditionStatus.EXCELLENT,
    usageStatus: Usage.IN_USE,
    assignedTo: "Alexander Agu",
    createdAt: "2025-01-15T08:30:00Z",
    latestUpdatedDate: "2026-02-10T14:20:00Z"
  },
  {
    id: 2,
    name: "Dell UltraSharp 27\"",
    type: "Peripheral",
    location: "Warehouse A - Shelf 4",
    assignment: Assignment.UNASSIGNED,
    serialNumber: "SN-DELL-U27-001",
    purchasePrice: 549.99,
    purchaseDate: "2024-11-20",
    conditionStatus: ConditionStatus.GOOD,
    usageStatus: Usage.STORAGE,
    assignedTo: null,
    createdAt: "2024-11-21T09:00:00Z",
    latestUpdatedDate: "2025-12-05T10:15:00Z"
  },
  {
    id: 3,
    name: "Cisco Catalyst 9300",
    type: "Networking",
    location: "Server Room 1",
    assignment: Assignment.ASSIGNED,
    serialNumber: "SN-CISCO-93-K9",
    purchasePrice: 4200.00,
    purchaseDate: "2023-06-10",
    conditionStatus: ConditionStatus.FAIR,
    usageStatus: Usage.SERVICE,
    assignedTo: "IT Infrastructure Team",
    createdAt: "2023-06-11T11:00:00Z",
    latestUpdatedDate: "2026-02-17T09:45:00Z"
  },
  {
    id: 4,
    name: "Logitech MX Master 3S",
    type: "Accessory",
    location: "Main Office - Floor 2",
    assignment: Assignment.ASSIGNED,
    serialNumber: "SN-LOGI-MX3S-55",
    purchasePrice: 99.00,
    purchaseDate: "2025-02-01",
    conditionStatus: ConditionStatus.EXCELLENT,
    usageStatus: Usage.IN_USE,
    assignedTo: "Oatlegile Diraditsile",
    createdAt: "2025-02-01T15:30:00Z",
    latestUpdatedDate: "2025-02-01T15:30:00Z"
  },
  {
    id: 5,
    name: "Ergonomic Office Chair",
    type: "Furniture",
    location: "Loading Dock",
    assignment: Assignment.UNASSIGNED,
    serialNumber: "SN-CHAIR-ERG-11",
    purchasePrice: 350.00,
    purchaseDate: "2022-05-12",
    conditionStatus: ConditionStatus.BAD,
    usageStatus: Usage.SERVICE,
    assignedTo: null,
    createdAt: "2022-05-13T10:00:00Z",
    latestUpdatedDate: "2026-01-20T11:00:00Z"
  }
];
