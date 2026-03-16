  
interface Asset {
  id: string;
  name: string;
  user: string;
  status: 'In Use' | 'In Storage' | 'In Service' | 'Damaged';
  lastUpdated: string;
}
export const stats = [
    { label: "Total Assets", value: "1,284", color: "bg-white text-slate-800" },
    { label: "In Use", value: "856", color: "bg-[#0EA5E9] text-white" }, // Primary accent
    { label: "In Storage", value: "342", color: "bg-white text-slate-800" },
    { label: "In Service", value: "76", color: "bg-white text-slate-800" },
    { label: "Damaged", value: "10", color: "bg-red-50 text-red-600" },
  ];

export const recentAssets: Asset[] = [
    { id: "SK-9920", name: "Dell Precision 5570", user: "Mandisi", status: "In Use", lastUpdated: "2 mins ago" },
    { id: "SK-8812", name: "Sony A7 IV Camera", user: "Sarah Smith", status: "In Use", lastUpdated: "1 hour ago" },
    { id: "SK-4421", name: "Logitech MX Master 3", user: "Alex Agu", status: "In Storage", lastUpdated: "Yesterday" },
  ];