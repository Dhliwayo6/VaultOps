import { AiFillHome } from "react-icons/ai";
import { FaWallet, FaBookOpen, FaUsers, FaUpload, FaMapMarkerAlt } from "react-icons/fa";
import { GiWallet } from "react-icons/gi";
import { ROUTES } from '@constants/routes';

export const sidebarItems = [
  {
    path: ROUTES.PORTAL,
    title: "Home",
    icon: AiFillHome,
  },
  {
    path: ROUTES.ASSETS,
    title: "Assets",
    icon: FaWallet,
  },
  {
    path: ROUTES.USERS,
    title: "Users",
    icon: FaUsers,
    adminOnly: true,
  },
  {
    path: ROUTES.IMPORT,
    title: "Import",
    icon: FaUpload,
    adminOnly: true,
  },
  {
    path: ROUTES.LOCATIONS,
    title: "Locations",
    icon: FaMapMarkerAlt,
    adminOnly: true,
  },
  {
    path: ROUTES.REPORTS,
    title: "Reports",
    icon: GiWallet,
  },
];
