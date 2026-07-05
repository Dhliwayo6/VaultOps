import { AiFillHome } from "react-icons/ai";
import { FaWallet, FaBookOpen } from "react-icons/fa";
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
    path: "#",
    title: "Reports",
    icon: GiWallet,
  },
  {
    path: "#",
    title: "Settings",
    icon: FaBookOpen,
  },
];
