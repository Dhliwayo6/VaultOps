import Sidebar from '../components/Sidebar/Sidebar';
import Dashboard from '../components/Dashboard/Dashboard';
import Assets from '../components/Assets/Assets';
import { Outlet } from 'react-router-dom';

export default function Portal() {
  return (
    <article className="bg-[#F8FAFC] min-h-[100dvh] flex flex-col md:flex-row font-sans text-slate-900">
      <Sidebar activeTab={''} setActiveTab={function (tab: string): void {
              throw new Error('Function not implemented.');
          } } />
      
      <main className="
        flex-1 
        w-full 
        pb-24 md:pb-8 /* Space for mobile bottom nav */
        md:ml-[120px] 
        lg:ml-[250px] 
        xl:ml-[300px]
        transition-all duration-300
      ">
        <div className="max-w-[1600px] mx-auto p-4 md:p-8 lg:p-12">
          <Outlet />
        </div>
      </main>
    </article>
  )
}