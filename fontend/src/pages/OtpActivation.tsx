import React, { useRef, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom';

export default function OtpActivation() {
    const navigate = useNavigate();
    const { email } = useParams();

    const [codeSent, setCodeSent] = useState(false);
    const [isLoading, setIsLoading] = useState(false);
    
    const length = 6;
    const [otp, setOtp] = useState<string[]>(Array(length).fill(""));
    const inputs = useRef<(HTMLInputElement | null)[]>([]);

    const handleChange = (value: string, index: number) => {
        const newOtp = [...otp];
        newOtp[index] = value.slice(-1);
        setOtp(newOtp);

        if (value && index < length - 1) {
            inputs.current[index + 1]?.focus();
        }
    };

    const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>, index: number) => {
        if (e.key === "Backspace" && !otp[index] && index > 0) {
            inputs.current[index - 1]?.focus();
        }
    };
    
    const handleAccountActivationAsync = async (e: React.FormEvent) => {
        e.preventDefault();
        const code = otp.join("");
        if (code.length !== 6 || isLoading) return;

        setIsLoading(true);
        try {
            // Simulate verification
            await new Promise(resolve => setTimeout(resolve, 2000));
            // navigate('/success');
        } catch (err) {
            console.log("Failed to activate account", err);
        } finally {
            setIsLoading(false);
        }
    };
    
    const HandleResendCodeAsync = async () => {
        if (isLoading) return;
        try {
            setCodeSent(true);
            setTimeout(() => setCodeSent(false), 4000); // Hide notice after 4s
        } catch {
            setCodeSent(false);
        }
    }

    return (
        <article className='w-full min-h-screen flex flex-col items-center bg-slate-50'>

            <div className='flex flex-1 w-full items-center justify-center px-6'>
                <div className='w-full max-w-[450px] flex flex-col items-center'>
                    
                    {/* Header */}
                    <div className='text-center mb-10'>
                        <h2 className='text-4xl font-black tracking-tight text-slate-900'>
                            Verify Access
                        </h2>
                        <p className='text-slate-500 font-medium mt-2'>
                            Sent to <span className="text-slate-900 font-bold">{email || "your email"}</span>
                        </p>
                    </div>

                    {/* Verification Card */}
                    <div className="bg-white border-2 border-slate-200 rounded-3xl p-8 shadow-sm w-full relative overflow-hidden">
                        {isLoading && (
                            <div className="absolute top-0 left-0 w-full h-1 bg-slate-100 overflow-hidden">
                                <div className="h-full bg-[#0EA5E9] animate-[loading_1.5s_infinite_linear]" 
                                     style={{ width: '30%', backgroundImage: 'linear-gradient(to right, transparent, #0EA5E9, transparent)' }} 
                                />
                            </div>
                        )}

                        <form onSubmit={handleAccountActivationAsync} className='flex flex-col items-center gap-8'>
                            <div className={`flex gap-2 sm:gap-3 transition-opacity duration-300 ${isLoading ? 'opacity-50 pointer-events-none' : ''}`}>
                                {otp.map((digit, index) => (
                                    <input
                                        key={index}
                                        ref={(el) => { inputs.current[index] = el; }}
                                        value={digit}
                                        maxLength={1}
                                        inputMode="numeric"
                                        className="w-10 h-14 sm:w-14 sm:h-16 border-2 border-slate-100 bg-slate-50 rounded-xl text-center text-2xl font-black text-slate-900 focus:border-[#0EA5E9] focus:bg-white focus:outline-none transition-all"
                                        onChange={(e) => handleChange(e.target.value, index)}
                                        onKeyDown={(e) => handleKeyDown(e, index)}
                                    />
                                ))}
                            </div>

                            <button 
                                type="submit"
                                disabled={isLoading || otp.some(d => !d)}
                                className={`w-full py-4 rounded-2xl font-black text-sm uppercase tracking-widest transition-all flex items-center justify-center gap-3
                                    ${isLoading || otp.some(d => !d)
                                        ? 'bg-slate-100 text-slate-400 cursor-not-allowed' 
                                        : 'bg-[#0EA5E9] text-white hover:brightness-110 active:scale-[0.98] shadow-lg shadow-blue-100'
                                    }`}
                            >
                                {isLoading ? (
                                    <>
                                        <div className="w-4 h-4 border-2 border-slate-300 border-t-slate-600 rounded-full animate-spin" />
                                        Verifying...
                                    </>
                                ) : (
                                    "Confirm Identity"
                                )}
                            </button>
                        </form>
                    </div>

                    {/* Footer Actions */}
                    <div className="mt-8 flex flex-col items-center gap-4">
                        <p className='text-sm font-medium text-slate-500'>
                            Didn't receive the code? 
                            <button 
                                onClick={HandleResendCodeAsync} 
                                className='ml-2 text-[#0EA5E9] font-black hover:underline disabled:opacity-50'
                                disabled={isLoading}
                            >
                                RESEND
                            </button>
                        </p>

                        {codeSent && (
                            <div className="px-4 py-2 bg-blue-50 border border-blue-100 rounded-full">
                                <p className='text-[#0EA5E9] text-[11px] font-black uppercase tracking-widest'>
                                    New Security Token Sent
                                </p>
                            </div>
                        )}
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
    )
}