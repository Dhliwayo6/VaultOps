import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { isValidPassword } from '../tools/UserTools';
import NavigateBackButton from '../UI/NavigateBackButton';

export default function SignIn() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [isLoading, setIsLoading] = useState(false);

    const navigate = useNavigate();

    const HandleLoginAsync = async (e: React.FormEvent) => {
        e.preventDefault();
        
        if (!isValidPassword(password) || isLoading) return;

        setIsLoading(true); // Start Loader

        try {
            // Add Api call logic later
            

            // if (success) navigate('/dashboard');
            navigate("/portal")
        } catch (error) {
            console.error("Failed to login", error);
        } finally {
            setIsLoading(false);
        }
    }

    return (
        <article className='relative w-full min-h-screen flex flex-col items-center bg-slate-50'>
            <div className='absolute top-0 left-0 p-6'>
                <NavigateBackButton title='go back' />
            </div>

            <div className='w-full max-w-[450px] px-6 flex flex-col justify-center flex-1'>
                <div className='mb-10 text-center'>
                    <h1 className="text-4xl font-black tracking-tight text-slate-900">Sign In</h1>
                    <p className="text-slate-500 font-medium mt-1">Accessing your secure vault</p>
                </div>

                <div className="bg-white border-2 border-slate-200 rounded-3xl p-8 shadow-sm relative overflow-hidden">
                    {/* Subtle Top Loading Bar */}
                    {isLoading && (
                        <div className="absolute top-0 left-0 w-full h-1 bg-slate-100 overflow-hidden">
                            <div className="h-full bg-[#0EA5E9] animate-progress-strip"></div>
                        </div>
                    )}

                    <form onSubmit={HandleLoginAsync} className='flex flex-col gap-6'>
                        <div className={`space-y-4 transition-opacity duration-300 ${isLoading ? 'opacity-50 pointer-events-none' : 'opacity-100'}`}>
                            {/* Email Input */}
                            <div className="space-y-2">
                                <label className='text-[10px] font-black uppercase tracking-widest text-slate-400 px-1'>Email</label>
                                <input 
                                    type="text" 
                                    placeholder="Enter your email"
                                    value={email}
                                    onChange={e => setEmail(e.target.value)}
                                    className='w-full h-12 px-4 bg-slate-50 border-2 border-slate-100 rounded-xl focus:border-[#0EA5E9] focus:outline-none font-medium'
                                />
                            </div>

                            {/* Password Input */}
                            <div className="space-y-2">
                                <label className='text-[10px] font-black uppercase tracking-widest text-slate-400 px-1'>Password</label>
                                <input 
                                    type="password" 
                                    placeholder="••••••••"
                                    value={password}
                                    onChange={e => setPassword(e.target.value)}
                                    className='w-full h-12 px-4 bg-slate-50 border-2 border-slate-100 rounded-xl focus:border-[#0EA5E9] focus:outline-none font-medium'
                                />
                            </div>
                        </div>

                        <button 
                            type="submit"
                            disabled={isLoading}
                            className={`w-full py-4 rounded-2xl font-black text-sm uppercase tracking-widest transition-all flex items-center justify-center gap-3
                                ${isLoading 
                                    ? 'bg-slate-100 text-slate-400 cursor-not-allowed' 
                                    : 'bg-[#0EA5E9] text-white hover:brightness-110 active:scale-[0.98] shadow-lg shadow-blue-100'
                                }`}
                        >
                            {isLoading ? (
                                <>
                                    <div className="w-4 h-4 border-2 border-slate-300 border-t-slate-500 rounded-full animate-spin" />
                                    Authenticating...
                                </>
                            ) : (
                                "Login"
                            )}
                        </button>

                        {!isLoading && (
                            <div className='w-full flex items-center justify-center pt-2'>
                                <p className='text-sm font-medium text-slate-500'>
                                    New here? <Link className='text-[#0EA5E9] font-black' to={"/sign-up"}>SIGN UP</Link>
                                </p>
                            </div>
                        )}
                    </form>
                </div>
            </div>
        </article>
    )
}