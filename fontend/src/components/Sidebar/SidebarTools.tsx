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
    path: "dashboard",
    title: "Home",
    icon: AiFillHome,
  },
  {
    path: "personal-account",
    title: "Personal Accounts",
    icon: FaWallet,
  },
  {
    path: "#",
    title: "Joint Accounts",
    icon: GiWallet,
  },
  {
    path: "#",
    title: "Transectons",
    icon: FaBookOpen,
  },
];
