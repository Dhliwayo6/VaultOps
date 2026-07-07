import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { isValidPassword } from '@utils/user';
import NavigateBackButton from '@components/NavigateBackButton';
import { ROUTES } from '@constants/routes';
import { login as apiLogin } from '@api/authApi';
import { useAuth } from '@context/AuthContext';
import { HiEye, HiEyeOff } from 'react-icons/hi';
import OrbitGraphic from '@components/OrbitGraphic';

export default function SignIn() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const [errorMsg, setErrorMsg] = useState("");
    const [showPassword, setShowPassword] = useState(false);

    const navigate = useNavigate();
    const { login } = useAuth();

    const HandleLoginAsync = async (e) => {
        e.preventDefault();
        
        if (!isValidPassword(password) || isLoading) {
            setErrorMsg("Invalid email or password format.");
            return;
        }

        setIsLoading(true);
        setErrorMsg("");

        try {
            const data = await apiLogin({ email, password });
            login(data.user, data.accessToken);
            navigate(ROUTES.PORTAL);
        } catch (error) {
            console.error("Failed to login", error);
            setErrorMsg(error.message || "An unexpected error occurred. Please try again.");
        } finally {
            setIsLoading(false);
        }
    }

    return (
        <article className='relative w-full min-h-screen lg:grid lg:grid-cols-12 bg-bg-base text-text-primary transition-colors duration-300'>
            {/* Go Back button absolute overlay */}
            <div className='absolute top-0 left-0 p-6 z-10'>
                <NavigateBackButton title='go back' />
            </div>

            {/* Left side: Form container */}
            <div className='lg:col-span-6 flex flex-col justify-center min-h-screen px-6 py-12 sm:px-12 md:px-20 max-w-[450px] lg:max-w-none mx-auto w-full'>
                <div className='w-full max-w-[400px] mx-auto'>
                    <div className='mb-10 text-center lg:text-left'>
                        <h1 className="text-4xl font-black tracking-tight text-text-primary font-display">Sign In</h1>
                        <p className="text-text-secondary font-medium mt-1">Accessing your secure vault</p>
                    </div>

                    <div className="bg-surface-elevated border border-border-token rounded-card p-8 shadow-elevation relative overflow-hidden">
                        {/* Subtle Top Loading Bar */}
                        {isLoading && (
                            <div className="absolute top-0 left-0 w-full h-1 bg-bg-base overflow-hidden">
                                <div className="h-full bg-accent animate-progress-strip"></div>
                            </div>
                        )}

                        <form onSubmit={HandleLoginAsync} className='flex flex-col gap-6'>
                            {errorMsg && (
                                <div role="alert" className="p-4 bg-red-500/10 border border-red-500/20 rounded-2xl text-xs font-semibold text-red-500 text-center">
                                    {errorMsg}
                                </div>
                            )}
                            <div className={`space-y-4 transition-opacity duration-300 ${isLoading ? 'opacity-50 pointer-events-none' : 'opacity-100'}`}>
                                {/* Email Input */}
                                <div className="space-y-2">
                                    <label htmlFor="email-input" className='text-[10px] font-black uppercase tracking-widest text-text-secondary px-1'>Email</label>
                                    <input 
                                        id="email-input"
                                        type="text" 
                                        placeholder="Enter your email"
                                        value={email}
                                        onChange={e => setEmail(e.target.value)}
                                        className='w-full h-12 px-4 bg-bg-base border border-border-token rounded-xl focus:border-accent focus:bg-surface-elevated focus:outline-none focus-visible:ring-2 focus-visible:ring-accent font-medium text-text-primary'
                                    />
                                 </div>

                                {/* Password Input */}
                                <div className="space-y-2">
                                    <label htmlFor="password-input" className='text-[10px] font-black uppercase tracking-widest text-text-secondary px-1'>Password</label>
                                    <div className="relative">
                                        <input 
                                            id="password-input"
                                            type={showPassword ? "text" : "password"} 
                                            placeholder="••••••••"
                                            value={password}
                                            onChange={e => setPassword(e.target.value)}
                                            className='w-full h-12 pl-4 pr-12 bg-bg-base border border-border-token rounded-xl focus:border-accent focus:bg-surface-elevated focus:outline-none focus-visible:ring-2 focus-visible:ring-accent font-medium text-text-primary'
                                        />
                                        <button
                                            type="button"
                                            onClick={() => setShowPassword(!showPassword)}
                                            className="absolute right-4 top-1/2 -translate-y-1/2 text-text-secondary hover:text-text-primary focus:outline-none focus-visible:ring-2 focus-visible:ring-accent rounded p-1 cursor-pointer flex items-center justify-center"
                                            aria-label={showPassword ? "Hide password" : "Show password"}
                                        >
                                            {showPassword ? <HiEyeOff className="text-xl" /> : <HiEye className="text-xl" />}
                                        </button>
                                    </div>
                                </div>
                            </div>

                            <button 
                                type="submit"
                                disabled={isLoading}
                                className={`w-full py-4 rounded-2xl font-black text-sm uppercase tracking-widest transition-all flex items-center justify-center gap-3 cursor-pointer focus-visible:ring-2 focus-visible:ring-accent focus-visible:outline-none focus-visible:ring-offset-2 dark:focus-visible:ring-offset-bg-base
                                    ${isLoading 
                                        ? 'bg-bg-base text-text-secondary opacity-50 cursor-not-allowed border border-border-token' 
                                        : 'bg-accent text-white hover:bg-accent-hover active:scale-[0.98] shadow-glow'
                                    }`}
                            >
                                {isLoading ? (
                                    <>
                                        <div className="w-4 h-4 border-2 border-text-secondary border-t-text-primary rounded-full animate-spin" />
                                        Authenticating...
                                    </>
                                ) : (
                                    "Login"
                                )}
                            </button>

                            {!isLoading && (
                                <div className='w-full flex items-center justify-center pt-2'>
                                    <p className='text-sm font-medium text-text-secondary'>
                                        New here? <Link className='text-accent font-black hover:underline focus-visible:ring-2 focus-visible:ring-accent focus-visible:outline-none rounded px-1' to={ROUTES.SIGN_UP}>SIGN UP</Link>
                                    </p>
                                </div>
                            )}
                        </form>
                    </div>
                </div>
            </div>

            {/* Right side: Rotating Orbit Graphic decorative panel */}
            <div className='hidden lg:col-span-6 lg:flex flex-col items-center justify-center relative overflow-hidden min-h-screen'>
                 {/* Premium subtle glow background gradient */}
                 <div className="absolute inset-0 bg-[radial-gradient(circle_800px_at_100%_200px,rgba(20,184,166,0.08),transparent)]" />
                 
                 <div className="relative z-10 text-center space-y-8 p-12 max-w-md">
                     <OrbitGraphic />
                     <div className="space-y-3">
                         <h2 className="text-3xl font-black font-display tracking-tight text-text-primary">
                             VaultOps Security Core
                         </h2>
                         <p className="text-text-secondary font-medium leading-relaxed">
                             Centralized asset management and cryptographic control layers for corporate ecosystems.
                         </p>
                     </div>
                 </div>
            </div>
        </article>
    )
}
