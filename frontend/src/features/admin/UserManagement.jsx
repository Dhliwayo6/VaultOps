import React, { useEffect, useState } from 'react';
import { getAllUsers, changeUserRole, changeUserStatus, deleteUser } from '@api/usersApi';
import { FaSpinner } from 'react-icons/fa';
import { useAuth } from '@context/AuthContext';

export default function UserManagement() {
  const [users, setUsers] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [errorMsg, setErrorMsg] = useState('');
  const [updatingId, setUpdatingId] = useState(null);
  const { user: currentUser } = useAuth();

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
    setUpdatingId(`role-${userId}`);
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

  const handleStatusToggle = async (userId, currentStatus) => {
    const targetStatus = currentStatus === 'SUSPENDED' ? 'ACTIVE' : 'SUSPENDED';
    setUpdatingId(`status-${userId}`);
    setErrorMsg('');

    try {
      const updatedUser = await changeUserStatus(userId, targetStatus);
      setUsers(prev => prev.map(u => u.id === userId ? updatedUser : u));
    } catch (err) {
      console.error(err);
      setErrorMsg(err.message || 'Failed to update user status.');
    } finally {
      setUpdatingId(null);
    }
  };

  const handleDeleteUser = async (userId, userName) => {
    if (!window.confirm(`Are you sure you want to delete user "${userName}"?`)) {
      return;
    }
    setUpdatingId(`delete-${userId}`);
    setErrorMsg('');

    try {
      await deleteUser(userId);
      setUsers(prev => prev.filter(u => u.id !== userId));
    } catch (err) {
      console.error(err);
      setErrorMsg(err.message || 'Failed to delete user.');
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
        <h2 className="text-3xl md:text-4xl font-black tracking-tight text-text-primary">
          User Management
        </h2>
        <p className="text-text-secondary font-medium mt-1">Manage system users, roles, and authorization levels</p>
      </div>

      {errorMsg && (
        <div role="alert" className="p-4 bg-red-500/10 border border-red-500/20 rounded-2xl text-sm font-semibold text-red-500 text-center">
          {errorMsg}
        </div>
      )}

      {isLoading ? (
        <div className="flex justify-center py-12">
          <FaSpinner className="w-8 h-8 text-accent animate-spin" />
        </div>
      ) : (
        <div className="bg-surface-elevated border border-border-token rounded-card overflow-x-auto shadow-elevation">
          <table className="w-full min-w-[800px] text-left border-collapse">
            <thead>
              <tr className="border-b border-border-token text-[10px] uppercase tracking-widest font-black text-text-secondary">
                <th className="px-8 py-5">ID</th>
                <th className="px-8 py-5">Name</th>
                <th className="px-8 py-5">Email</th>
                <th className="px-8 py-5">Role</th>
                <th className="px-8 py-5">Status</th>
                <th className="px-8 py-5 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-border-token">
              {users.map(u => (
                <tr key={u.id} className="group hover:bg-bg-base transition-colors">
                  <td className="px-8 py-6 font-mono text-xs text-accent font-bold">
                    #{u.id}
                  </td>
                  <td className="px-8 py-6 font-bold text-text-primary">
                    {u.name}
                  </td>
                  <td className="px-8 py-6 font-semibold text-text-secondary">
                    {u.email}
                  </td>
                  <td className="px-8 py-6">
                    <span className={`px-3 py-1 rounded-full text-[10px] font-black uppercase tracking-widest ${
                      u.role === 'ADMIN' 
                        ? 'bg-purple-500/10 text-purple-700 dark:text-purple-400' 
                        : 'bg-bg-base text-text-secondary border border-border-token'
                    }`}>
                      {u.role}
                    </span>
                  </td>
                  <td className="px-8 py-6">
                    <span className={`px-3 py-1 rounded-full text-[10px] font-black uppercase tracking-widest ${
                      u.status === 'ACTIVE' 
                        ? 'bg-emerald-500/10 text-emerald-700 dark:text-emerald-400' 
                        : 'bg-amber-500/10 text-amber-800 dark:text-amber-400'
                    }`}>
                      {u.status}
                    </span>
                  </td>
                  <td className="px-8 py-6 text-right">
                    <div className="flex items-center justify-end gap-2">
                      <button
                        onClick={() => handleRoleChange(u.id, u.role)}
                        disabled={updatingId !== null || currentUser?.email === u.email}
                        aria-label={`${u.role === 'ADMIN' ? 'Demote' : 'Promote'} user ${u.name}`}
                        className={`px-3 py-1.5 rounded-xl text-[10px] font-black uppercase tracking-widest transition-all cursor-pointer focus-visible:ring-2 focus-visible:ring-accent focus-visible:outline-none focus-visible:ring-offset-2 dark:focus-visible:ring-offset-bg-base ${
                          u.role === 'ADMIN'
                            ? 'border border-amber-500/35 text-amber-800 dark:text-amber-400 hover:bg-amber-500/10'
                            : 'bg-accent hover:bg-accent-hover text-white shadow-glow'
                        } disabled:opacity-50`}
                      >
                        {updatingId === `role-${u.id}`
                          ? 'Updating...'
                          : u.role === 'ADMIN'
                          ? 'Demote'
                          : 'Promote'}
                      </button>

                      {currentUser?.email !== u.email && (
                        <button
                          onClick={() => handleStatusToggle(u.id, u.status)}
                          disabled={updatingId !== null}
                          aria-label={`${u.status === 'SUSPENDED' ? 'Activate' : 'Suspend'} user ${u.name}`}
                          className={`px-3 py-1.5 rounded-xl text-[10px] font-black uppercase tracking-widest transition-all cursor-pointer focus-visible:ring-2 focus-visible:outline-none ${
                            u.status === 'SUSPENDED'
                              ? 'bg-emerald-500/10 text-emerald-700 dark:text-emerald-400 border border-emerald-500/20 hover:bg-emerald-500/20'
                              : 'bg-orange-500/10 text-orange-700 dark:text-orange-400 border border-orange-500/20 hover:bg-orange-500/20'
                          } disabled:opacity-50`}
                        >
                          {updatingId === `status-${u.id}`
                            ? 'Updating...'
                            : u.status === 'SUSPENDED'
                            ? 'Activate'
                            : 'Suspend'}
                        </button>
                      )}

                      {currentUser?.email !== u.email && (
                        <button
                          onClick={() => handleDeleteUser(u.id, u.name)}
                          disabled={updatingId !== null}
                          aria-label={`Delete user ${u.name}`}
                          className="px-3 py-1.5 rounded-xl text-[10px] font-black uppercase tracking-widest transition-all cursor-pointer border border-red-500/20 text-red-500 hover:bg-red-500/10 focus-visible:ring-2 focus-visible:ring-red-500 focus-visible:outline-none disabled:opacity-50"
                        >
                          {updatingId === `delete-${u.id}` ? 'Deleting...' : 'Delete'}
                        </button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
              {users.length === 0 && (
                <tr>
                  <td colSpan="6" className="text-center py-8 text-text-secondary font-bold">
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
