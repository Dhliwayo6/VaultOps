import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { isValidPassword } from '../tools/UserTools';
import NavigateBackButton from '../UI/NavigateBackButton';

interface SignUpInputs {
    required: boolean;
    title: string;
    placeholder: string;
    input: React.Dispatch<React.SetStateAction<string>>;
    value: string;
}

const passwordConstraints = [
    "At least 8 characters",
    "Uppercase & lowercase characters",
    "At least 1 number",
    "At least 1 special character"
];

export default function SignUp() {
    const [name, setName] = useState("");
    const [email, setEmail] = useState("");
    const [phone, setPhone] = useState("");
    const [password, setPassword] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const [failed, setFailed] = useState(false);

    const navigate = useNavigate();

    const inputs: SignUpInputs[] = [
        { required: true, title: "Name", placeholder: "Enter your name", input: setName, value: name },
        { required: true, title: "Email", placeholder: "Enter your email", input: setEmail, value: email },
        { required: false, title: "Phone Number", placeholder: "Enter phone number", input: setPhone, value: phone },
        { required: true, title: "Password", placeholder: "Create a strong password", input: setPassword, value: password },
    ];

    const HandleRegisterUserAsync = async (e: React.FormEvent) => {
        e.preventDefault();
        
        if (!isValidPassword(password) || name === "" || email === "" || isLoading) {
            setFailed(true);
            return;
        }

        setIsLoading(true);
        setFailed(false);

        try {
            // Simulate API Call
            await new Promise(resolve => setTimeout(resolve, 2000));
            // if (res) navigate(`/activate/${email}`)
            navigate("/otp")
        } catch {
            console.log("Failed to create account");
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <article className='relative w-full min-h-screen flex flex-col items-center bg-slate-50 py-10'>
            <div className='absolute top-0 left-0 p-6'>
                <NavigateBackButton title='go back' />
            </div>

            <div className='w-full max-w-[500px] px-6 flex flex-col justify-center flex-1'>
                {/* Header */}
                <div className='mb-8 text-center'>
                    <h1 className="text-4xl font-black tracking-tight text-slate-900">Sign Up</h1>
                    <p className="text-slate-500 font-medium mt-1">Create your secure vault identity</p>
                </div>

                {/* Form Card */}
                <div className="bg-white border-2 border-slate-200 rounded-3xl p-8 shadow-sm relative overflow-hidden">
                    {/* Top Loading Bar */}
                    {isLoading && (
                        <div className="absolute top-0 left-0 w-full h-1 bg-slate-100 overflow-hidden">
                            <div className="h-full bg-[#0EA5E9] animate-[loading_1.5s_infinite_linear]" 
                                 style={{ width: '30%', backgroundImage: 'linear-gradient(to right, transparent, #0EA5E9, transparent)' }} 
                            />
                        </div>
                    )}

                    <form onSubmit={HandleRegisterUserAsync} className='flex flex-col gap-5'>
                        <div className={`space-y-4 transition-all duration-300 ${isLoading ? 'opacity-50 pointer-events-none grayscale' : 'opacity-100'}`}>
                            {inputs.map((data, index) => (
                                <div key={index} className="space-y-1.5">
                                    <label className='flex gap-1 items-center px-1'>
                                        <span className='text-[10px] font-black uppercase tracking-widest text-slate-400'>
                                            {data.title} {data.required && <span className='text-[#0EA5E9]'>*</span>}
                                        </span>
                                    </label>
                                    <input 
                                        type={data.title === "Password" ? "password" : "text"} 
                                        placeholder={data.placeholder} 
                                        value={data.value} 
                                        onChange={e => data.input(e.target.value)}
                                        className={`w-full h-11 px-4 bg-slate-50 border-2 rounded-xl focus:outline-none font-medium transition-all
                                            ${failed && data.required && !data.value ? 'border-red-200 bg-red-50' : 'border-slate-100 focus:border-[#0EA5E9]'}`}
                                    />

                                    {/* Password Constraints styled as a "Notice" widget */}
                                    {data.title === "Password" && (
                                        <div className="mt-3 p-4 bg-slate-50 rounded-2xl border border-slate-100">
                                            <p className="text-[10px] font-black text-slate-400 uppercase mb-2 tracking-widest">Security Requirements</p>
                                            <ul className="grid grid-cols-1 gap-1">
                                                {passwordConstraints.map((text, i) => (
                                                    <li key={i} className="text-[11px] font-bold text-slate-500 flex items-center gap-2">
                                                        <div className="w-1 h-1 bg-slate-300 rounded-full" />
                                                        {text}
                                                    </li>
                                                ))}
                                            </ul>
                                        </div>
                                    )}
                                </div>
                            ))}
                        </div>

                        <button 
                            type="submit"
                            disabled={isLoading}
                            className={`w-full py-4 mt-2 rounded-2xl font-black text-sm uppercase tracking-widest transition-all flex items-center justify-center gap-3
                                ${isLoading 
                                    ? 'bg-slate-100 text-slate-400 cursor-not-allowed' 
                                    : 'bg-[#0EA5E9] text-white hover:brightness-110 active:scale-[0.98] shadow-lg shadow-blue-100'
                                }`}
                        >
                            {isLoading ? (
                                <>
                                    <div className="w-4 h-4 border-2 border-slate-300 border-t-slate-600 rounded-full animate-spin" />
                                    Creating Account...
                                </>
                            ) : (
                                "Create Account"
                            )}
                        </button>

                        {!isLoading && (
                            <p className='text-center text-sm font-medium text-slate-500'>
                                Already have an account? <Link className='text-[#0EA5E9] font-black' to={"/sign-in"}>SIGN IN</Link>
                            </p>
                        )}
                    </form>
                </div>

                {/* <p className="mt-8 text-center text-[10px] font-bold text-slate-400 uppercase tracking-widest opacity-60">
                    Vault Registry Protocol v2.0.4
                </p> */}
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