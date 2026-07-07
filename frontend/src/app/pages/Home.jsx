import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import Hero from '@layout/Hero';
import Features from '@layout/Features';
import Footer from '@layout/Footer';
import { ROUTES } from '@constants/routes';
import ThemeToggle from '@components/ThemeToggle';

const Home = () => {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen bg-bg-base font-sans text-text-primary transition-colors duration-300">
      <a href="#main-content" className="skip-link">Skip to main content</a>
      
      {/* Navigation */}
      <nav className="border-b border-border-token sticky top-0 bg-surface-elevated/80 backdrop-blur-md z-50">
        <div className="max-w-7xl mx-auto px-6 h-20 flex items-center justify-between">
          <Link 
            to={ROUTES.HOME} 
            className="flex items-center gap-2 rounded-xl focus-visible:ring-2 focus-visible:ring-accent focus-visible:outline-none focus-visible:ring-offset-2"
          >
            <div className="w-8 h-8 rounded-full logo-3d flex items-center justify-center text-white font-black text-xs tracking-wider">VO</div>
            <span className="text-2xl font-black tracking-tighter text-text-primary" >VaultOps</span>
          </Link>
          <div className="flex items-center gap-4">
            <ThemeToggle />
            <button 
              onClick={() => navigate(ROUTES.SIGN_IN)}
              className="px-5 py-2 border border-border-token hover:border-accent bg-surface-elevated/50 hover:bg-accent/10 text-text-secondary hover:text-text-primary font-bold rounded-full transition-all duration-300 shadow-xs hover:shadow-[0_0_15px_rgba(20,184,166,0.25)] cursor-pointer focus-visible:ring-2 focus-visible:ring-accent focus-visible:outline-none focus-visible:ring-offset-2"
            >
              Log in
            </button>
          </div>
        </div>
      </nav>

      <main id="main-content" tabIndex="-1" className="outline-none">
        {/* Hero Section */}
        <Hero />

        {/* Features Grid */}
        <Features />
      </main>

      {/* Footer */}
      <Footer />
    </div>
  );
};

export default Home;
