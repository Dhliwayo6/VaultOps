import { AiFillHome } from "react-icons/ai";
import { FaWallet, FaBookOpen } from "react-icons/fa";
import { GiWallet } from "react-icons/gi";
import type { IconType } from "react-icons";

interface SidebarData {
  path: string;
  title: string;
  icon: IconType;
}

export const sidebarItems: SidebarData[] = [
  {
    path: "",
    title: "Home",
    icon: AiFillHome,
  },
  {
    path: "assets",
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
