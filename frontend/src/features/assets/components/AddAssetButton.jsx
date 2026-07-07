import React from 'react';
import { useNavigate } from 'react-router-dom';
import { ROUTES } from '@constants/routes';

export default function AddAssetButton() {
  const navigate = useNavigate();

  return (
    <button 
      onClick={() => navigate(ROUTES.ASSETS + '?create=true')}
      className='px-5 py-4 bg-accent hover:bg-accent-hover font-black uppercase text-xs text-white rounded-2xl tracking-widest transition-all hover:brightness-110 hover:-translate-y-0.5 active:translate-y-0 cursor-pointer shadow-glow focus-visible:ring-2 focus-visible:ring-accent focus-visible:outline-none focus-visible:ring-offset-2 dark:focus-visible:ring-offset-bg-base'
    >
      Add Asset
    </button>
  );
}
