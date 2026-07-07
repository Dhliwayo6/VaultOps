import Sidebar from '@layout/Sidebar/Sidebar';
import { Outlet } from 'react-router-dom';

export default function Portal() {
  return (
    <article className="bg-bg-base min-h-[100dvh] flex flex-col lg:flex-row font-sans text-text-primary">
      <a href="#main-content" className="skip-link">Skip to main content</a>
      <Sidebar />
      
      <main 
        id="main-content"
        tabIndex="-1"
        className="
          flex-1 
          w-full 
          pt-16 lg:pt-0
          pb-8
          lg:ml-[260px] 
          xl:ml-[300px]
          transition-all duration-300
          outline-none
        "
      >
        <div className="max-w-[1600px] mx-auto p-4 md:p-8 lg:p-12">
          <Outlet />
        </div>
      </main>
    </article>
  )
}
