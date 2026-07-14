import React, { useState } from 'react';
import { sidebarItems } from "./sidebarItems";
import { NavLink } from "react-router-dom";
import { useAuth } from "@context/AuthContext";
import { ROUTES } from '@constants/routes';
import { HiMenu, HiX, HiLogout } from 'react-icons/hi';
import { PanelLeftClose, PanelLeftOpen } from 'lucide-react';
import ThemeToggle from '@components/ThemeToggle';
import { useFocusTrap } from '@hooks/useFocusTrap';

export default function Sidebar({ isCollapsed = false, onToggleCollapse }) {
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
          lg:translate-x-0 
          ${isCollapsed ? 'lg:w-[80px]' : 'lg:w-[260px] xl:w-[300px]'}
          transition-all duration-300
        `}
      >
        
        {/* Brand Header */}
        <div className={`flex border-b border-sidebar-border py-6 px-6 mb-6 transition-all duration-300 ${isCollapsed ? 'lg:flex-col lg:items-center lg:gap-4 lg:px-4 lg:py-6' : 'items-center justify-between lg:px-8 lg:py-8'}`}>
          <div className="flex items-center overflow-hidden">
            <div className="logo-3d min-w-[40px] h-[40px] rounded-full flex items-center justify-center font-bold text-white text-sm tracking-wider shrink-0">VO</div>
            <h2 className={`ml-4 text-text-primary font-black text-2xl tracking-tighter transition-all duration-300 ${isCollapsed ? 'lg:opacity-0 lg:max-w-0 lg:hidden' : 'opacity-100 max-w-full'}`}>
              VaultOps
            </h2>
          </div>
          {/* Close Button on Mobile/Tablet */}
          <button 
            onClick={() => setIsOpen(false)}
            className="lg:hidden p-2 hover:bg-black/5 dark:hover:bg-white/10 rounded-xl transition-colors focus-visible:ring-2 focus-visible:ring-accent focus-visible:outline-none text-text-secondary cursor-pointer"
            aria-label="Close menu"
          >
            <HiX className="text-2xl" />
          </button>

          {/* Collapse Toggle Button (Tablet/Desktop only) */}
          <button
            onClick={onToggleCollapse}
            className="hidden lg:flex p-2 hover:bg-black/5 dark:hover:bg-white/10 rounded-xl transition-colors focus-visible:ring-2 focus-visible:ring-accent focus-visible:outline-none text-text-secondary cursor-pointer shrink-0"
            aria-label={isCollapsed ? "Expand sidebar" : "Collapse sidebar"}
            aria-expanded={!isCollapsed}
          >
            {isCollapsed ? <PanelLeftOpen className="text-xl" /> : <PanelLeftClose className="text-xl" />}
          </button>
        </div>
 
        {/* Navigation Items */}
        <div className={`flex flex-col w-full gap-2 transition-all duration-300 overflow-y-auto ${isCollapsed ? 'lg:px-2 px-4' : 'px-4'}`}>
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
                     ${isCollapsed ? 'lg:px-0 lg:justify-center lg:w-12 lg:h-12 lg:mx-auto' : ''}
                     ${isActive && path !== '#' 
                       ? 'bg-sidebar-active-bg text-sidebar-active-text font-bold shadow-xs' 
                       : 'text-sidebar-text hover:bg-black/5 dark:hover:bg-white/5 hover:text-text-primary'}`
                  }
                  title={isCollapsed ? title : undefined}
                  aria-label={title}
                >
                  {Icon && <Icon className="text-xl shrink-0" />}
                  <span className={`text-[0.95rem] tracking-wide transition-all duration-300 ${isCollapsed ? 'lg:opacity-0 lg:max-w-0 lg:hidden' : 'opacity-100'}`}>
                    {title}
                  </span>
                </NavLink>
              );
            })}
        </div>

        {/* User Card & Theme Toggle Section */}
        {user && (
          <div className='flex flex-col gap-4 w-full pb-8 px-4 mt-auto border-t border-sidebar-border pt-6'>
            <div className={`flex items-center justify-between px-4 transition-all duration-300 ${isCollapsed ? 'lg:px-0 lg:justify-center' : ''}`}>
              <span className={`text-[10px] font-black uppercase tracking-widest text-text-secondary transition-all duration-300 ${isCollapsed ? 'lg:opacity-0 lg:max-w-0 lg:hidden' : 'opacity-100'}`}>Theme Mode</span>
              <ThemeToggle />
            </div>
            <div className={`w-full transition-all duration-300 ${isCollapsed ? 'lg:p-0 lg:bg-transparent lg:border-none lg:shadow-none' : 'p-4 bg-surface-elevated border border-border-token rounded-3xl shadow-xs'}`}>
              <div className={`flex items-center justify-between gap-3 transition-all duration-300 ${isCollapsed ? 'lg:flex-col lg:gap-4 lg:justify-center' : ''}`}>
                <div className={`flex items-center gap-3 overflow-hidden transition-all duration-300 ${isCollapsed ? 'lg:flex-col lg:gap-2' : ''}`}>
                  <div className='relative shrink-0 mx-auto'>
                    <div className='w-[40px] h-[40px] rounded-full bg-accent text-white flex items-center justify-center font-black text-sm shadow-xs'>
                      {user.name ? user.name.split(' ').map(n => n[0]).join('').toUpperCase() : 'U'}
                    </div>
                    <div className='absolute -bottom-0.5 -right-0.5 w-3 h-3 bg-emerald-400 border-2 border-surface-elevated rounded-full'></div>
                  </div>
                  <div className={`overflow-hidden text-text-primary text-center transition-all duration-300 ${isCollapsed ? 'lg:opacity-0 lg:max-w-0 lg:hidden' : ''}`}>
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
          </div>
        )}
      </nav>
    </>
  );
}
