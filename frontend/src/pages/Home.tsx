import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import Hero from '../components/Hero/Hero';
import Features from '../components/Features/Features';
import Footer from '../components/Footer/Footer';

const Home: React.FC = () => {
  const navigate = useNavigate();



  return (
    <div className="min-h-screen bg-white font-sans text-slate-900">
      {/* Navigation */}
      <nav className="border-b-2 border-slate-50 sticky top-0 bg-white/80 backdrop-blur-md z-50">
        <div className="max-w-7xl mx-auto px-6 h-20 flex items-center justify-between">
          <Link to={"/"} className="flex items-center gap-2">
            <div className="w-8 h-8 bg-[#0EA5E9] rounded-lg flex items-center justify-center text-white font-black text-xl">V</div>
            <span className="text-2xl font-black tracking-tighter text-slate-900" >VaultOps</span>
          </Link>
          <div className="flex items-center gap-4">
            <button 
              onClick={() => navigate('/sign-in')}
              className="px-5 py-2.5 text-slate-600 font-bold hover:text-slate-900 transition-colors"
            >
              Log in
            </button>
          </div>
        </div>
      </nav>

      {/* Hero Section */}
      <Hero />

      {/* Features Grid */}
      <Features />

      {/* Footer */}
      <Footer />
    </div>
  );
};

export default Home;