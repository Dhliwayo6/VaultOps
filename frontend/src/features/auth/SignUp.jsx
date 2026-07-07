import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { isValidPassword } from '@utils/user';
import NavigateBackButton from '@components/NavigateBackButton';
import { ROUTES } from '@constants/routes';
import { register as apiRegister } from '@api/authApi';
import { HiEye, HiEyeOff } from 'react-icons/hi';
import OrbitGraphic from '@components/OrbitGraphic';

const passwordRules = [
    { label: "At least 8 characters", check: (p) => p.length >= 8 },
    { label: "Uppercase & lowercase characters", check: (p) => /[A-Z]/.test(p) && /[a-z]/.test(p) },
    { label: "At least 1 number", check: (p) => /\d/.test(p) },
    { label: "At least 1 special character", check: (p) => /[^A-Za-z0-9]/.test(p) },
    { label: "Passwords match", check: (p, cp) => p === cp && cp.length > 0 }
];

export default function SignUp() {
    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [phone, setPhone] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [showPassword, setShowPassword] = useState(false);
    const [showConfirmPassword, setShowConfirmPassword] = useState(false);
    const [isLoading, setIsLoading] = useState(false);
    const [failed, setFailed] = useState(false);
    const [errorMsg, setErrorMsg] = useState("");

    const navigate = useNavigate();

    const inputs = [
        { id: "signup-name", required: true, title: "Name", placeholder: "Enter your name", input: setName, value: name },
        { id: "signup-email", required: true, title: "Email", placeholder: "Enter your email", input: setEmail, value: email },
        { id: "signup-phone", required: false, title: "Phone Number", placeholder: "Enter phone number", input: setPhone, value: phone },
    ];

    const HandleRegisterUserAsync = async (e) => {
        e.preventDefault();
        
        if (name === "" || email === "" || password === "" || confirmPassword === "") {
            setFailed(true);
            setErrorMsg("Please fill in all required fields.");
            return;
        }

        if (!isValidPassword(password)) {
            setFailed(true);
            setErrorMsg("Password does not meet all security requirements.");
            return;
        }

        if (password !== confirmPassword) {
            setFailed(true);
            setErrorMsg("Passwords do not match.");
            return;
        }

        setIsLoading(true);
        setFailed(false);
        setErrorMsg("");

        try {
            await apiRegister({ name, email, phone, password });
            navigate(`/otp/${encodeURIComponent(email)}`);
        } catch (err) {
            console.error("Failed to create account", err);
            setErrorMsg(err.message || "An unexpected error occurred. Please try again.");
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <article className='relative w-full min-h-screen lg:grid lg:grid-cols-12 bg-bg-base text-text-primary transition-colors duration-300'>
            {/* Go Back button absolute overlay */}
            <div className='absolute top-0 left-0 p-6 z-10'>
                <NavigateBackButton title='go back' />
            </div>

            {/* Left side: Form container */}
            <div className='lg:col-span-6 flex flex-col justify-center min-h-screen px-6 py-12 sm:px-12 md:px-20 max-w-[500px] lg:max-w-none mx-auto w-full'>
                <div className='w-full max-w-[440px] mx-auto'>
                    {/* Header */}
                    <div className='mb-8 text-center lg:text-left'>
                        <h1 className="text-4xl font-black tracking-tight text-text-primary font-display">Sign Up</h1>
                        <p className="text-text-secondary font-medium mt-1">Create your secure vault identity</p>
                    </div>

                    {/* Form Card */}
                    <div className="bg-surface-elevated border border-border-token rounded-card p-8 shadow-elevation relative overflow-hidden">
                        {/* Top Loading Bar */}
                        {isLoading && (
                            <div className="absolute top-0 left-0 w-full h-1 bg-bg-base overflow-hidden">
                                <div className="h-full bg-accent animate-[loading_1.5s_infinite_linear]" 
                                     style={{ width: '30%', backgroundImage: 'linear-gradient(to right, transparent, var(--color-accent), transparent)' }} 
                                />
                            </div>
                        )}

                        <form onSubmit={HandleRegisterUserAsync} className='flex flex-col gap-5'>
                            {errorMsg && (
                                <div role="alert" className="p-4 bg-red-500/10 border border-red-500/20 rounded-2xl text-xs font-semibold text-red-500 text-center">
                                    {errorMsg}
                                </div>
                            )}
                            <div className={`space-y-4 transition-all duration-300 ${isLoading ? 'opacity-50 pointer-events-none grayscale' : 'opacity-100'}`}>
                                {inputs.map((data, index) => (
                                    <div key={index} className="space-y-1.5">
                                        <label htmlFor={data.id} className='flex gap-1 items-center px-1'>
                                            <span className='text-[10px] font-black uppercase tracking-widest text-text-secondary'>
                                                {data.title} {data.required && <span className='text-accent'>*</span>}
                                            </span>
                                        </label>
                                        <input 
                                            id={data.id}
                                            type="text" 
                                            placeholder={data.placeholder} 
                                            value={data.value} 
                                            onChange={e => data.input(e.target.value)}
                                            required={data.required}
                                            aria-required={data.required ? "true" : undefined}
                                            className={`w-full h-11 px-4 bg-bg-base border border-border-token rounded-xl focus:outline-none focus-visible:ring-2 focus-visible:ring-accent font-medium transition-all text-text-primary`}
                                        />
                                    </div>
                                ))}

                                {/* Password Input */}
                                <div className="space-y-1.5">
                                    <label htmlFor="signup-password" className='flex gap-1 items-center px-1'>
                                        <span className='text-[10px] font-black uppercase tracking-widest text-text-secondary'>
                                            Password <span className='text-accent'>*</span>
                                        </span>
                                    </label>
                                    <div className="relative">
                                        <input 
                                            id="signup-password"
                                            type={showPassword ? "text" : "password"} 
                                            placeholder="Create a strong password" 
                                            value={password} 
                                            onChange={e => setPassword(e.target.value)}
                                            required
                                            aria-required="true"
                                            className={`w-full h-11 pl-4 pr-12 bg-bg-base border border-border-token rounded-xl focus:outline-none focus-visible:ring-2 focus-visible:ring-accent font-medium transition-all text-text-primary`}
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

                                {/* Confirm Password Input */}
                                <div className="space-y-1.5">
                                    <label htmlFor="signup-confirm-password" className='flex gap-1 items-center px-1'>
                                        <span className='text-[10px] font-black uppercase tracking-widest text-text-secondary'>
                                            Confirm Password <span className='text-accent'>*</span>
                                        </span>
                                    </label>
                                    <div className="relative">
                                        <input 
                                            id="signup-confirm-password"
                                            type={showConfirmPassword ? "text" : "password"} 
                                            placeholder="Confirm your password" 
                                            value={confirmPassword} 
                                            onChange={e => setConfirmPassword(e.target.value)}
                                            required
                                            aria-required="true"
                                            className={`w-full h-11 pl-4 pr-12 bg-bg-base border border-border-token rounded-xl focus:outline-none focus-visible:ring-2 focus-visible:ring-accent font-medium transition-all text-text-primary`}
                                        />
                                        <button
                                            type="button"
                                            onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                                            className="absolute right-4 top-1/2 -translate-y-1/2 text-text-secondary hover:text-text-primary focus:outline-none focus-visible:ring-2 focus-visible:ring-accent rounded p-1 cursor-pointer flex items-center justify-center"
                                            aria-label={showConfirmPassword ? "Hide confirm password" : "Show confirm password"}
                                        >
                                            {showConfirmPassword ? <HiEyeOff className="text-xl" /> : <HiEye className="text-xl" />}
                                        </button>
                                    </div>
                                </div>

                                {/* Password Constraints styled as a "Notice" widget */}
                                <div className="mt-3 p-4 bg-bg-base rounded-2xl border border-border-token">
                                    <p className="text-[10px] font-black text-text-secondary uppercase mb-2 tracking-widest">Security Requirements</p>
                                    <ul className="grid grid-cols-1 gap-1.5">
                                        {passwordRules.map((rule, i) => {
                                            const isSatisfied = rule.check(password, confirmPassword);
                                            return (
                                                <li key={i} className="text-[11px] font-bold flex items-center gap-2 transition-colors duration-300">
                                                    <div 
                                                        className={`w-2 h-2 rounded-full transition-colors duration-300 ${
                                                            isSatisfied ? 'bg-emerald-500 shadow-emerald-500/40 shadow-xs' : 'bg-slate-400 dark:bg-slate-600'
                                                        }`} 
                                                    />
                                                    <span className={`transition-colors duration-300 ${isSatisfied ? 'text-text-primary' : 'text-text-secondary'}`}>
                                                        {rule.label}
                                                    </span>
                                                </li>
                                            );
                                        })}
                                    </ul>
                                </div>
                            </div>

                            <button 
                                type="submit"
                                disabled={isLoading}
                                className={`w-full py-4 mt-2 rounded-2xl font-black text-sm uppercase tracking-widest transition-all flex items-center justify-center gap-3 cursor-pointer focus-visible:ring-2 focus-visible:ring-accent focus-visible:outline-none focus-visible:ring-offset-2 dark:focus-visible:ring-offset-bg-base
                                    ${isLoading 
                                        ? 'bg-bg-base text-text-secondary opacity-50 cursor-not-allowed border border-border-token' 
                                        : 'bg-accent text-white hover:bg-accent-hover active:scale-[0.98] shadow-glow'
                                    }`}
                            >
                                {isLoading ? (
                                    <>
                                        <div className="w-4 h-4 border-2 border-text-secondary border-t-text-primary rounded-full animate-spin" />
                                        Creating Account...
                                    </>
                                ) : (
                                    "Create Account"
                                )}
                            </button>

                            {!isLoading && (
                                <p className='text-center text-sm font-medium text-text-secondary'>
                                    Already have an account? <Link className='text-accent font-black hover:underline focus-visible:ring-2 focus-visible:ring-accent focus-visible:outline-none rounded px-1' to={ROUTES.SIGN_IN}>SIGN IN</Link>
                                </p>
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
            
            <style>{`
                @keyframes loading {
                    0% { transform: translateX(-100%); }
                    100% { transform: translateX(400%); }
                }
            `}</style>
        </article>
    );
}
