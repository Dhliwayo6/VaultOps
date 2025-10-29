import React from 'react'
import Sidebar from '../../components/SideBar/Sidebar'
import Dashboard from '../Dashboard/Dashboard'

export default function Portal() {
  return (
    <article className='portal'>
        <Sidebar />
        <Dashboard />
    </article>
  )
}