import { sidebarItems } from "./SidebarTools";
import { Link } from "react-router-dom";

interface SidebarProps {
  activeTab: string;
  setActiveTab: (tab: string) => void;
}



export default function Sidebar({ activeTab, setActiveTab }: SidebarProps) {
  return (
    <nav className='z-50 w-full fixed bottom-0 flex items-center justify-between p-2 bg-[#0EA5E9] rounded-t-[2.5rem] md:w-[100px] lg:w-[260px] md:top-0 md:left-0 md:flex-col md:rounded-none md:justify-start md:items-stretch'>
      
      {/* Brand */}
      <div className='hidden md:flex items-center justify-center border-b-2 border-white/10 py-8 mb-6 lg:justify-start lg:px-8'>
        <div className="bg-white min-w-[40px] h-[40px] rounded-xl flex items-center justify-center font-black text-[#0EA5E9] text-xl">VO</div>
        <h2 className='hidden lg:block ml-4 text-white font-black text-2xl tracking-tighter'>VaultOps</h2>
      </div>

      {/* Nav */}
      <div className="flex md:flex-col w-full gap-2 px-2">
        {sidebarItems.map(items => {
          const { path, title } = items
          return <Link to={path}
            key={title}
            className={`flex-1 md:flex-initial flex flex-col items-center py-4 md:py-3 rounded-2xl transition-all duration-200 lg:flex-row lg:justify-start lg:gap-4 lg:px-6
              ${activeTab === title ? 'bg-white text-[#0EA5E9] font-bold' : 'text-white/80 hover:bg-white/10 hover:text-white'}`}
          >
            <span className='text-[10px] lg:text-[0.95rem] tracking-wide'>{title}</span>
          </Link>
        })}
      </div>

      {/* User */}
      <div className='hidden md:flex flex-1 items-end w-full pb-8 px-4'>
        <div className='w-full p-3 lg:p-4 bg-white/10 rounded-3xl flex items-center gap-3 border border-white/10'>
          <div className='relative'>
            <div className='w-[40px] h-[40px] rounded-full bg-white text-[#0EA5E9] flex items-center justify-center font-black text-sm'>AA</div>
            <div className='absolute -bottom-0.5 -right-0.5 w-3 h-3 bg-emerald-400 border-2 border-[#0EA5E9] rounded-full'></div>
          </div>
          <div className='hidden lg:block overflow-hidden text-white'>
            <h2 className='text-sm font-bold truncate'>Alexander Agu</h2>
            <p className='text-[10px] opacity-60 font-medium uppercase tracking-widest'>Admin</p>
          </div>
        </div>
      </div>
    </nav>
  );
}