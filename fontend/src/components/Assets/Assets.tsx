import { useState } from 'react';
import { Assignment, DUMMY_ASSETS, ConditionStatus, Usage } from "./AssetTools";

export default function Assets() {
  const [sortBy, setSortBy] = useState<'date' | 'name'>('date');

  // Logic for sorting
  const sortedAssets = [...DUMMY_ASSETS].sort((a, b) => {
    if (sortBy === 'name') return a.name.localeCompare(b.name);
    return new Date(b.purchaseDate).getTime() - new Date(a.purchaseDate).getTime();
  });

  return (
    <section className="w-full min-h-full flex flex-col gap-8 animate-in fade-in duration-500">
        {/* Header */}
        <div className="w-full flex justify-between items-end">
            <div>
                <h2 className="text-5xl font-black tracking-tighter text-slate-900">
                    All Assets
                </h2>
                <p className="text-slate-500 font-medium mt-1">Manage and track your vault inventory</p>
            </div>
            
            <div className="flex gap-2 bg-white border-2 border-slate-100 p-1 rounded-xl">
                <button 
                    onClick={() => setSortBy('date')}
                    className={`px-4 py-2 rounded-lg text-xs font-black uppercase tracking-wider transition-all ${sortBy === 'date' ? 'bg-[#0EA5E9] text-white' : 'text-slate-400 hover:text-slate-600'}`}
                >
                    Date
                </button>
                <button 
                    onClick={() => setSortBy('name')}
                    className={`px-4 py-2 rounded-lg text-xs font-black uppercase tracking-wider transition-all ${sortBy === 'name' ? 'bg-[#0EA5E9] text-white' : 'text-slate-400 hover:text-slate-600'}`}
                >
                    Name
                </button>
            </div>
        </div>

        {/* Filtering and Sorting */}
        <div className="w-full border-b-2 border-slate-100">
            {/* Filters */}
            <div className="flex gap-8 h-full">
                <button className="pb-4 border-b-4 border-[#0EA5E9] text-[#0EA5E9] font-black text-sm uppercase tracking-widest">
                    Condition    
                </button>
                <button className="pb-4 border-b-4 border-transparent text-slate-400 hover:text-slate-600 font-black text-sm uppercase tracking-widest transition-all">
                    Usage    
                </button>
                <button className="pb-4 border-b-4 border-transparent text-slate-400 hover:text-slate-600 font-black text-sm uppercase tracking-widest transition-all">
                    Assignment    
                </button>
            </div>
        </div>


        {/* Displaying assets */}
        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
            {
                sortedAssets.map(asset => {
                    const { 
                        id,
                        name,
                        type,
                        location,
                        assignment,
                        serialNumber,
                        purchasePrice,
                        purchaseDate,
                        conditionStatus,
                        usageStatus,
                        assignedTo,
                     } = asset;

                     return (
                      <div className="bg-white border-2 border-slate-100 rounded-[2.5rem] p-8 flex flex-col gap-6 transition-all hover:-translate-y-1 hover:border-[#0EA5E9]/30" key={id}>
                        {/* Card Header */}
                        <div className="w-full flex items-center justify-between">
                            <h2 className="text-[#0EA5E9] font-black font-mono text-sm tracking-tighter">
                                ASSET-00{id}
                            </h2>

                            <span className={`px-3 py-1 rounded-full text-[10px] font-black uppercase tracking-widest ${
                              usageStatus === Usage.IN_USE ? 'bg-blue-50 text-[#0EA5E9]' : 'bg-slate-100 text-slate-500'
                            }`}>
                                {usageStatus.replace('_', ' ')}
                            </span>
                        </div>

                        {/* Asset Basic info */}
                        <div className="space-y-1">
                            <h2 className="text-2xl font-black text-slate-900 leading-tight">
                                {name}
                            </h2>

                            <div>
                                <div className="flex items-center gap-2 font-bold text-slate-400 text-sm">
                                    <p>{type}</p>
                                    <span className="w-1 h-1 bg-slate-300 rounded-full"></span>
                                    <p>{location}</p>
                                </div>
                                <h2 className="text-xs font-mono text-slate-300 mt-1 uppercase tracking-widest">
                                    SN: {serialNumber}
                                </h2>
                            </div>
                        </div>

                        {/* Assigned to */}
                        {
                            assignment === Assignment.ASSIGNED ?
                            <div className="bg-slate-50 p-4 rounded-2xl border border-slate-100">
                                <p className="text-[10px] font-black text-slate-400 uppercase tracking-widest mb-1">Assigned To</p>
                                <h2 className="font-bold text-slate-800 flex items-center gap-2">
                                    <div className="w-2 h-2 bg-emerald-400 rounded-full"></div>
                                    {assignedTo}
                                </h2>
                            </div> : 
                            <div className="bg-slate-50 p-4 rounded-2xl border border-dashed border-slate-200">
                                <p className="text-xs font-bold text-slate-400 italic">Available for assignment</p>
                            </div>
                        }

                        {/* Price and Date */}
                        <div className="mt-auto pt-4 border-t border-slate-100">
                            <div className="w-full flex items-end justify-between">
                                <div>
                                    <h2 className="text-2xl font-black text-slate-900">
                                        R {purchasePrice.toLocaleString()}
                                    </h2>

                                    <p className="text-[10px] font-bold text-slate-400 uppercase tracking-tight">
                                        Acquired: {new Date(purchaseDate).toLocaleDateString()}
                                    </p>
                                </div>

                                <div className={`px-3 py-1 rounded-lg border-2 font-black text-[10px] uppercase tracking-tighter ${
                                  conditionStatus === ConditionStatus.EXCELLENT ? 'border-emerald-100 text-emerald-600' : 'border-slate-100 text-slate-500'
                                }`}>
                                    {conditionStatus}
                                </div>
                            </div>
                        </div>
                      </div>
                     )
                })
            }
        </div>
    </section>
  )
}