import React, { useState } from 'react';
import { sidebarItems } from "./sidebarItems";
import { NavLink } from "react-router-dom";
import { useAuth } from "@context/AuthContext";
import { ROUTES } from '@constants/routes';
import { HiMenu, HiX, HiLogout } from 'react-icons/hi';
import ThemeToggle from '@components/ThemeToggle';
import { useFocusTrap } from '@hooks/useFocusTrap';

export default function Sidebar() {
  const { user, logout } = useAuth();
  const [isOpen, setIsOpen] = useState(false);
  const drawerRef = useFocusTrap(isOpen, () => setIsOpen(false));

  return (
    <>
      {/* Mobile/Tablet Top Bar */}
      <header className="lg:hidden fixed top-0 left-0 right-0 h-16 bg-sidebar-bg text-text-primary flex items-center justify-between px-6 z-40 border-b border-sidebar-border shadow-xs">
        <div className="flex items-center gap-3">
          <div className="logo-3d min-w-[32px] h-[32px] rounded-full flex items-center justify-center font-bold text-white text-xs tracking-wider">VO</div>
          <h2 className="text-text-primary font-black text-xl tracking-tighter">VaultOps</h2>
        </div>
        <div className="flex items-center gap-2">
          <ThemeToggle />
          <button 
            onClick={() => setIsOpen(true)}
            className="p-2 hover:bg-black/5 dark:hover:bg-white/10 rounded-xl transition-colors focus-visible:ring-2 focus-visible:ring-accent focus-visible:outline-none cursor-pointer text-text-primary"
            aria-label="Open menu"
          >
            <HiMenu className="text-2xl" />
          </button>
        </div>
      </header>

      {/* Backdrop (Mobile/Tablet only) */}
      {isOpen && (
        <div 
          className="lg:hidden fixed inset-0 bg-slate-900/40 z-40 backdrop-blur-xs transition-opacity duration-300"
          onClick={() => setIsOpen(false)}
        />
      )}

      {/* Sidebar Drawer */}
      <nav 
        ref={drawerRef}
        className={`
          z-50 
          fixed top-0 left-0 h-screen w-[280px] 
          bg-sidebar-bg border-r border-sidebar-border
          flex flex-col 
          transform transition-transform duration-300 ease-in-out
          ${isOpen ? 'translate-x-0' : '-translate-x-full'}
          lg:translate-x-0 lg:w-[260px] xl:w-[300px]
        `}
      >
        
        {/* Brand Header */}
        <div className='flex items-center justify-between border-b border-sidebar-border py-6 px-6 mb-6 lg:justify-start lg:px-8 lg:py-8'>
          <div className="flex items-center">
            <div className="logo-3d min-w-[40px] h-[40px] rounded-full flex items-center justify-center font-bold text-white text-sm tracking-wider">VO</div>
            <h2 className='ml-4 text-text-primary font-black text-2xl tracking-tighter'>VaultOps</h2>
          </div>
          {/* Close Button on Mobile/Tablet */}
          <button 
            onClick={() => setIsOpen(false)}
            className="lg:hidden p-2 hover:bg-black/5 dark:hover:bg-white/10 rounded-xl transition-colors focus-visible:ring-2 focus-visible:ring-accent focus-visible:outline-none text-text-secondary cursor-pointer"
            aria-label="Close menu"
          >
            <HiX className="text-2xl" />
          </button>
        </div>
 
        {/* Navigation Items */}
        <div className="flex flex-col w-full gap-2 px-4 overflow-y-auto">
          {sidebarItems
            .filter(item => !item.adminOnly || user?.role === 'ADMIN')
            .map(item => {
              const { path, title, icon: Icon } = item;
              return (
                <NavLink 
                  to={path}
                  key={title}
                  end={path === ROUTES.PORTAL}
                  onClick={() => setIsOpen(false)} // Close drawer on link click
                  className={({ isActive }) => 
                    `flex items-center gap-4 py-3.5 px-6 rounded-2xl transition-all duration-200
                     focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-accent focus-visible:ring-offset-2 focus-visible:ring-offset-sidebar-bg
                     ${isActive && path !== '#' 
                       ? 'bg-sidebar-active-bg text-sidebar-active-text font-bold shadow-xs' 
                       : 'text-sidebar-text hover:bg-black/5 dark:hover:bg-white/5 hover:text-text-primary'}`
                  }
                >
                  {Icon && <Icon className="text-xl" />}
                  <span className='text-[0.95rem] tracking-wide'>{title}</span>
                </NavLink>
              );
            })}
        </div>

        {/* User Card & Theme Toggle Section */}
        {user && (
          <div className='flex flex-col gap-4 w-full pb-8 px-4 mt-auto border-t border-sidebar-border pt-6'>
            <div className="flex items-center justify-between px-4">
              <span className="text-[10px] font-black uppercase tracking-widest text-text-secondary">Theme Mode</span>
              <ThemeToggle />
            </div>
            <div className='w-full p-4 bg-surface-elevated rounded-3xl flex items-center justify-between gap-3 border border-border-token shadow-xs'>
              <div className="flex items-center gap-3 overflow-hidden">
                <div className='relative shrink-0'>
                  <div className='w-[40px] h-[40px] rounded-full bg-accent text-white flex items-center justify-center font-black text-sm shadow-xs'>
                    {user.name ? user.name.split(' ').map(n => n[0]).join('').toUpperCase() : 'U'}
                  </div>
                  <div className='absolute -bottom-0.5 -right-0.5 w-3 h-3 bg-emerald-400 border-2 border-surface-elevated rounded-full'></div>
                </div>
                <div className='overflow-hidden text-text-primary'>
                  <h2 className='text-sm font-bold truncate max-w-[100px] lg:max-w-[80px] xl:max-w-[120px]'>{user.name}</h2>
                  <p className='text-[10px] text-text-secondary font-medium uppercase tracking-widest'>{user.role}</p>
                </div>
              </div>
              <button
                onClick={logout}
                className="p-2.5 text-text-secondary hover:text-red-500 hover:bg-red-500/10 dark:hover:bg-red-500/20 rounded-xl transition-all cursor-pointer focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-red-500 shrink-0"
                aria-label="Logout"
                title="Logout"
              >
                <HiLogout className="text-xl" />
              </button>
            </div>
          </div>
        )}
      </nav>
    </>
  );
}
