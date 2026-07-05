import React, { useEffect, useState } from 'react';
import { getAllUsers, changeUserRole } from '@api/usersApi';

export default function UserManagement() {
  const [users, setUsers] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [errorMsg, setErrorMsg] = useState('');
  const [updatingId, setUpdatingId] = useState(null);

  const fetchUsers = async () => {
    setIsLoading(true);
    setErrorMsg('');
    try {
      const data = await getAllUsers();
      setUsers(data || []);
    } catch (err) {
      console.error(err);
      setErrorMsg(err.message || 'Failed to retrieve users.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleRoleChange = async (userId, currentRole) => {
    const targetRole = currentRole === 'ADMIN' ? 'USER' : 'ADMIN';
    setUpdatingId(userId);
    setErrorMsg('');

    try {
      const updatedUser = await changeUserRole(userId, targetRole);
      setUsers(prev => prev.map(u => u.id === userId ? updatedUser : u));
    } catch (err) {
      console.error(err);
      setErrorMsg(err.message || 'Failed to update user role.');
    } finally {
      setUpdatingId(null);
    }
  };

  useEffect(() => {
    fetchUsers();
  }, []);

  return (
    <section className="w-full min-h-full flex flex-col gap-8 animate-in fade-in duration-500">
      <div>
        <h2 className="text-4xl font-black tracking-tight text-slate-900">
          User Management
        </h2>
        <p className="text-slate-500 font-medium mt-1">Manage system users, roles, and authorization levels</p>
      </div>

      {errorMsg && (
        <div className="p-4 bg-red-50 border border-red-100 rounded-2xl text-sm font-semibold text-red-600">
          {errorMsg}
        </div>
      )}

      {isLoading ? (
        <div className="flex justify-center py-12">
          <div className="w-8 h-8 border-4 border-slate-200 border-t-[#0EA5E9] rounded-full animate-spin" />
        </div>
      ) : (
        <div className="bg-white border-2 border-slate-200 rounded-3xl overflow-hidden shadow-sm">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="border-b-2 border-slate-100 text-[10px] uppercase tracking-widest font-black text-slate-400">
                <th className="px-8 py-5">ID</th>
                <th className="px-8 py-5">Name</th>
                <th className="px-8 py-5">Email</th>
                <th className="px-8 py-5">Role</th>
                <th className="px-8 py-5">Status</th>
                <th className="px-8 py-5 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-100">
              {users.map(u => (
                <tr key={u.id} className="group hover:bg-slate-50 transition-colors">
                  <td className="px-8 py-6 font-mono text-xs text-[#0EA5E9] font-bold">
                    #{u.id}
                  </td>
                  <td className="px-8 py-6 font-bold text-slate-900">
                    {u.name}
                  </td>
                  <td className="px-8 py-6 font-semibold text-slate-600">
                    {u.email}
                  </td>
                  <td className="px-8 py-6">
                    <span className={`px-3 py-1 rounded-full text-[10px] font-black uppercase tracking-widest ${
                      u.role === 'ADMIN' ? 'bg-purple-50 text-purple-600' : 'bg-slate-100 text-slate-500'
                    }`}>
                      {u.role}
                    </span>
                  </td>
                  <td className="px-8 py-6">
                    <span className={`px-3 py-1 rounded-full text-[10px] font-black uppercase tracking-widest ${
                      u.status === 'ACTIVE' ? 'bg-emerald-50 text-emerald-600' : 'bg-amber-50 text-amber-600'
                    }`}>
                      {u.status}
                    </span>
                  </td>
                  <td className="px-8 py-6 text-right">
                    <button
                      onClick={() => handleRoleChange(u.id, u.role)}
                      disabled={updatingId !== null}
                      className={`px-4 py-2 rounded-xl text-xs font-black uppercase tracking-widest transition-all ${
                        u.role === 'ADMIN'
                          ? 'border border-amber-200 text-amber-600 hover:bg-amber-50'
                          : 'bg-[#0EA5E9] text-white hover:bg-[#0284c7]'
                      } disabled:opacity-50`}
                    >
                      {updatingId === u.id
                        ? 'Updating...'
                        : u.role === 'ADMIN'
                        ? 'Demote'
                        : 'Promote'}
                    </button>
                  </td>
                </tr>
              ))}
              {users.length === 0 && (
                <tr>
                  <td colSpan="6" className="text-center py-8 text-slate-400 font-bold">
                    No users found.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      )}
    </section>
  );
}
